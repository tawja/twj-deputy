/*
 * Copyright 2016 Tawja.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tawja.maven.discovery.internal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.ArtifactResolver;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;
import org.eclipse.aether.version.Version;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.tawja.maven.discovery.util.Booter;
import org.tawja.maven.discovery.util.ManualRepositorySystemFactory;

/**
 *
 * @author jbennani
 */
public class MavenManager {

    /**
     * @return the logger
     */
    public Log getLogger() {
        return logger;
    }

    /**
     * @return the mavenSystem
     */
    public RepositorySystem getMavenSystem() {
        return mavenSystem;
    }

    /**
     * @return the mavenEffectiveSettings
     */
    public Settings getMavenEffectiveSettings() {
        return mavenEffectiveSettings;
    }

    /**
     * @return the mavenSession
     */
    public RepositorySystemSession getMavenSession() {
        return mavenSession;
    }

    /**
     * @return the mavenRepositories
     */
    public List<RemoteRepository> getMavenRepositories() {
        return mavenRepositories;
    }

    /**
     * @return the mavenModelResolver
     */
    public ModelResolver getMavenModelResolver() {
        return mavenModelResolver;
    }

    private final Log logger;

    private RepositorySystem mavenSystem;
    private Settings mavenEffectiveSettings;
    private RepositorySystemSession mavenSession;
    private List<RemoteRepository> mavenRepositories;
    private ModelResolver mavenModelResolver;

    public MavenManager() {
        this.logger = LogFactory.getLog(MavenManager.class);
        iniMavenIfRequired();
    }

    private void iniMavenIfRequired() {
        if (getMavenSystem() == null) {
            mavenSystem = Booter.newRepositorySystem();
            mavenEffectiveSettings = Booter.getEffectiveSettings();

            //Local repository will be used here
            mavenSession = Booter.newRepositorySystemSession(getMavenSystem(), getMavenEffectiveSettings().getLocalRepository());
            //mavenSession = Booter.newRepositorySystemSession(mavenSystem, "target/local-repo");

            // Local repo not added as remote one
            mavenRepositories = Booter.newRepositories(getMavenSystem(), getMavenSession(), getMavenEffectiveSettings(), false);

            // Model Resolver
            try {
                ServiceLocator serviceLocator = ManualRepositorySystemFactory.getServiceLocator();
                Constructor<?> constr = Class.forName("org.apache.maven.repository.internal.DefaultModelResolver").getConstructors()[0];
                constr.setAccessible(true);
                mavenModelResolver = (ModelResolver) constr.newInstance(getMavenSession(), null, null,
                        serviceLocator.getService(ArtifactResolver.class),
                        serviceLocator.getService(VersionRangeResolver.class),
                        serviceLocator.getService(RemoteRepositoryManager.class), getMavenRepositories());
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException | InvocationTargetException ex) {
                getLogger().error("Unable to build the Maven Model Resolver", ex);
            }
        }
    }

    private void resetMaven() {
        mavenSystem = null;
        mavenEffectiveSettings = null;
        mavenSession = null;
        mavenRepositories = null;
        mavenModelResolver = null;
    }

    public List<Version> getVersions(String groupId, String artifactId) {
        iniMavenIfRequired();

        Artifact artifact = new DefaultArtifact(groupId + ":" + artifactId + ":[0,)");

        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact(artifact);
        rangeRequest.setRepositories(getMavenRepositories());
        List<Version> versions = null;
        try {
            VersionRangeResult rangeResult = getMavenSystem().resolveVersionRange(getMavenSession(), rangeRequest);
            versions = rangeResult.getVersions();
        } catch (VersionRangeResolutionException ex) {
            getLogger().error("Unable to resolve version range", ex);
        }

        return versions;
    }

    public String getHighestVersion(String groupId, String artifactId) {
        Artifact artifact = new DefaultArtifact(groupId, artifactId, "pom", "[0,)");

        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact(artifact);
        rangeRequest.setRepositories(getMavenRepositories());
        Version version = null;
        try {
            VersionRangeResult rangeResult = getMavenSystem().resolveVersionRange(getMavenSession(), rangeRequest);
            version = rangeResult.getHighestVersion();
        } catch (VersionRangeResolutionException ex) {
            getLogger().error("Unable to resolve version range", ex);
        }

        if (version != null) {
            return version.toString();
        } else {
            return null;
        }
    }

    public Artifact getArtifact(String groupId, String artifactId, String version) {
        Artifact artifact = new DefaultArtifact(groupId + ":" + artifactId + ":[0,)");

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(getMavenRepositories());

        try {
            ArtifactResult artifactResult = getMavenSystem().resolveArtifact(getMavenSession(), artifactRequest);
            artifact = artifactResult.getArtifact();
        } catch (ArtifactResolutionException ex) {
            getLogger().error("Unable to resolve artifact", ex);
        }

        return artifact;
    }

    public MavenProject loadMavenPom(String path) {
        File pomfile = new File(path);
        Model model = null;
        FileReader reader = null;
        MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        try {
            reader = new FileReader(pomfile);
            model = mavenreader.read(reader);
            model.setPomFile(pomfile);
        } catch (IOException | XmlPullParserException ex) {

        }
        MavenProject project = new MavenProject(model);

        //return project.getGroupId() + ":" + project.getArtifactId();
        return project;
    }

    public static List<Dependency> getArtifactsDependencies(MavenProject project, String dependencyType, String scope) throws Exception {
        DefaultArtifact pomArtifact = new DefaultArtifact(project.getId());

        RepositorySystemSession repoSession = null; // TODO
        RepositorySystem repoSystem = null; // TODO

        List<RemoteRepository> remoteRepos = project.getRemoteProjectRepositories();
        List<Dependency> ret = new ArrayList<>();

        Dependency dependency = new Dependency(pomArtifact, scope);

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(dependency);
        collectRequest.setRepositories(remoteRepos);

        DependencyNode node = repoSystem.collectDependencies(repoSession, collectRequest).getRoot();
        DependencyRequest projectDependencyRequest = new DependencyRequest(node, null);

        repoSystem.resolveDependencies(repoSession, projectDependencyRequest);

        PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
        node.accept(nlg);

        ret.addAll(nlg.getDependencies(true));

        return ret;
    }

    public static String relativeLocalRepoPomPatch(String groupId, String artifactId, String version) {
        String groupPath = groupId.replace('.', File.separatorChar);
        String targetRelativePath = groupPath + File.separator + artifactId + File.separator + version + File.separator + artifactId + "- + " + version + ".pom";
        return targetRelativePath;
    }

    public Model getEffectiveModel(File pomXmlFile) {
        ModelBuilder modelBuilder = new DefaultModelBuilderFactory().newInstance();
        ModelBuildingRequest req = new DefaultModelBuildingRequest();

        Model model = null;

        try {
            req.setPomFile(pomXmlFile);
            req.setProcessPlugins(false);
            req.setModelResolver(getMavenModelResolver());
            req.setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL);

            model = modelBuilder.build(req).getEffectiveModel();
        } catch (Exception ex) {
            getLogger().error("Unable to build the Maven Model", ex);
        }

        return model;
    }

    public File resolveArtifact(String groupId, String artifactId, String version, String type, String classifier) {
        File result = null;

        Artifact artifact = new DefaultArtifact(groupId, artifactId, classifier, type, version);
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(getMavenRepositories());

        try {
            ArtifactResult artifactResult = getMavenSystem().resolveArtifact(getMavenSession(), artifactRequest);
            artifact = artifactResult.getArtifact();
            if (artifact != null) {
                result = artifact.getFile();
            }
        } catch (ArtifactResolutionException e) {
            // TODO add error handling -> maybe throw an exception that indicates the error or return an Optional
        }

        return result;
    }
}
