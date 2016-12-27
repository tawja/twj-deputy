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
import java.util.List;
import org.apache.maven.model.Model;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.model.Parent;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.tawja.maven.discovery.model.DiscoveredMavenProject;
import org.tawja.maven.discovery.model.DiscoveredMavenProjectRelation;
import org.tawja.maven.discovery.model.DiscoveredMavenProjectRelationType;
import org.tawja.maven.discovery.model.DiscoveredMavenRemoteRepository;
import org.tawja.maven.discovery.model.DiscoveredConfig;
import org.tawja.maven.discovery.model.InputMavenConfig;
import org.tawja.maven.discovery.model.InputMavenProject;

/**
 *
 * @author jbennani
 */
public class DiscoveryManager {

    /**
     * @return the logger
     */
    public Log getLogger() {
        return logger;
    }

    /**
     * @return the inputConfigParserManager
     */
    public InputConfigParserManager getInputConfigParserManager() {
        return inputConfigParserManager;
    }

    /**
     * @return the discoveredConfigParserManager
     */
    public DiscoveredConfigParserManager getDiscoveredConfigParserManager() {
        return discoveredConfigParserManager;
    }

    /**
     * @return the mavenManager
     */
    public MavenManager getMavenManager() {
        return mavenManager;
    }

    private Log logger = LogFactory.getLog(DiscoveryManager.class);

    private InputConfigParserManager inputConfigParserManager;
    private DiscoveredConfigParserManager discoveredConfigParserManager;
    private MavenManager mavenManager;

    private RepositorySystem mavenSystem;
    private Settings mavenEffectiveSettings;
    private RepositorySystemSession mavenSession;
    private List<RemoteRepository> mavenRepositories;
    private ModelResolver mavenModelResolver;

    public DiscoveryManager() {
        this(false);
    }

    public DiscoveryManager(Boolean useXmlConfig) {
        inputConfigParserManager = new InputConfigParserManager(useXmlConfig);
        discoveredConfigParserManager = new DiscoveredConfigParserManager(useXmlConfig);
        mavenManager = new MavenManager();
    }

    public DiscoveredConfig discover(InputMavenConfig inputMavenConfig) {
        DiscoveredConfig config = new DiscoveredConfig();
        // Load global variable that define main source directory
        // Priority : Config, Activated Profile, Default Profile (activated by default)
        String baseSourceDir = System.getProperty("baseSourceDir");
        for (Profile profile : mavenManager.getMavenEffectiveSettings().getProfiles()) {
            if (profile.getActivation().isActiveByDefault()) {
                Object value = profile.getProperties().get("baseSourceDir");
                if (value != null) {
                    baseSourceDir = value.toString();
                    break;
                }
            }
        }
        for (String profileName : mavenManager.getMavenEffectiveSettings().getActiveProfiles()) {
            Profile profile = mavenManager.getMavenEffectiveSettings().getProfilesAsMap().get(profileName);
            if (profile != null) {
                Object value = profile.getProperties().get("baseSourceDir");
                if (value != null) {
                    baseSourceDir = value.toString();
                    break;
                }
            }
        }

        String baseDirPath = inputMavenConfig.getBaseDirectory();
        String baseDirectory = new File(".").getAbsolutePath();
        if (baseDirPath != null && !baseDirPath.isEmpty()) {
            baseDirectory = baseDirPath;
        } else if (baseSourceDir != null && !baseSourceDir.isEmpty()) {
            baseDirectory = baseSourceDir;
        }
        config.setBaseDirectory(FileUtils.getCleanFullPath(baseDirectory));

        String localRepoDirPath = FileUtils.getCleanFullPath(mavenManager.getMavenSession().getLocalRepository().getBasedir().getAbsolutePath());
        config.setLocalRepoPath(localRepoDirPath);

        for (RemoteRepository mavenRepository : mavenManager.getMavenRepositories()) {
            DiscoveredMavenRemoteRepository repo = new DiscoveredMavenRemoteRepository();
            repo.setId(mavenRepository.getId());
            repo.setType(mavenRepository.getContentType());
            repo.setUrl(mavenRepository.getUrl());
            config.putMavenRemoteRepository(repo);
        }

        // Scan first Root level projects
        for (InputMavenProject inputMavenProject : inputMavenConfig.getProjects()) {
            DiscoveredMavenProject mavenProject = config.getMavenProject(inputMavenProject.getGroupId(), inputMavenProject.getArtifactId());
            if (mavenProject == null) {
                mavenProject = new DiscoveredMavenProject();
                Model model = fillDiscoveredMavenProject(mavenProject, inputMavenProject.getGroupId(), inputMavenProject.getArtifactId(), baseDirPath + File.separator + inputMavenProject.getRelativePath());
                mavenProject.setIsRootProject(Boolean.TRUE);
                mavenProject.setModel(model);
                config.putMavenProject(mavenProject);
            }
        }

        // Scan Childs
        // ...
        for (InputMavenProject inputMavenProject : inputMavenConfig.getProjects()) {
            DiscoveredMavenProject mavenProject = config.getMavenProject(inputMavenProject.getGroupId(), inputMavenProject.getArtifactId());
            // Project : Child Modules
            fillChildsDiscoveredMavenProject(config, mavenProject);
        }

        // Scan Parents
        for (InputMavenProject inputMavenProject : inputMavenConfig.getProjects()) {
            DiscoveredMavenProject mavenProject = config.getMavenProject(inputMavenProject.getGroupId(), inputMavenProject.getArtifactId());
            // Project : Parent
            fillParentDiscoveredMavenProject(config, mavenProject);
        }

        return config;
    }

    public void fillChildsDiscoveredMavenProject(DiscoveredConfig config, DiscoveredMavenProject mavenProject) {
        // Project : Childs
        if (mavenProject.getModel() != null) {
            List<String> modules = mavenProject.getModel().getModules();
            for (String moduleName : modules) {
                String moduleLocalPath = mavenProject.getLocalSourcePath() + File.separator + moduleName;
                // Retreive POM : Priority : 1- Local source dir, 2- Local Repo, 3- Remote repos
                File targetPomFile = new File(moduleLocalPath + File.separator + "pom.xml");
                DiscoveredMavenProject childMavenProject = new DiscoveredMavenProject();
                childMavenProject.setGroupId(mavenProject.getGroupId());
                childMavenProject.setArtifactId(mavenProject.getArtifactId() + "/" + moduleName);
                childMavenProject.setModuleName(moduleName);
                if (targetPomFile.exists()) {
                    //childMavenProject = new DiscoveredMavenProject();
                    Model model = fillDiscoveredMavenProject(childMavenProject, null, null, moduleLocalPath);
                    childMavenProject.setModel(model);
                    childMavenProject.setIsSourceProject(Boolean.TRUE);
                    
                    fillChildsDiscoveredMavenProject(config, childMavenProject);
                }
                config.putMavenProject(childMavenProject);

                // Relation : Parent
                DiscoveredMavenProjectRelation childRelation = null; //config.getMavenProjectRelation(childMavenProject.getId(), childMavenProject.getId(), DiscoveredMavenProjectRelationType.MODULE.toString());
                if (childRelation == null) {
                    childRelation = new DiscoveredMavenProjectRelation();
                    childRelation.setLinkOrigin(mavenProject);
                    childRelation.setLinkTarget(childMavenProject);
                    childRelation.setLinkType(DiscoveredMavenProjectRelationType.MODULE);
                    config.putMavenProjectRelations(childRelation);
                }
            }
        }
    }

    public void fillParentDiscoveredMavenProject(DiscoveredConfig config, DiscoveredMavenProject mavenProject) {
        // Project : Parent
        if (mavenProject.getModel() != null) {
            Parent parent = mavenProject.getModel().getParent();
            if (parent != null) {
                DiscoveredMavenProject mavenParentProject = config.getMavenProject(parent.getGroupId(), parent.getArtifactId());
                if (mavenParentProject == null) {
                    mavenParentProject = new DiscoveredMavenProject();
                    Model parentModel = fillDiscoveredMavenProject(mavenParentProject, parent.getGroupId(), parent.getArtifactId(), null);
                    mavenParentProject.setModel(parentModel);
                    fillParentDiscoveredMavenProject(config, mavenParentProject);

                    config.putMavenProject(mavenParentProject);

                }

                // Relation : Parent
                DiscoveredMavenProjectRelation parentRelation = config.getMavenProjectRelation(mavenProject.getId(), mavenParentProject.getId(), DiscoveredMavenProjectRelationType.PARENT.toString());
                if (parentRelation == null) {
                    parentRelation = new DiscoveredMavenProjectRelation();
                    parentRelation.setLinkOrigin(mavenProject);
                    parentRelation.setLinkTarget(mavenParentProject);
                    parentRelation.setLinkType(DiscoveredMavenProjectRelationType.PARENT);
                    config.putMavenProjectRelations(parentRelation);
                }
            }
        }
    }

    public Model fillDiscoveredMavenProject(DiscoveredMavenProject mavenProject, String groupId, String artifactId, String localSourcePath) {
        //DiscoveredMavenProject mavenProject = new DiscoveredMavenProject();

        if (groupId != null) {
            mavenProject.setGroupId(groupId);
        }

        if (artifactId != null) {
            mavenProject.setArtifactId(artifactId);
        }

        if (groupId != null && artifactId != null) {
            mavenProject.setVersion(mavenManager.getHighestVersion(mavenProject.getGroupId(), mavenProject.getArtifactId()));
        }

        if (localSourcePath != null) {
            mavenProject.setLocalSourcePath(FileUtils.getCleanFullPath(localSourcePath));
        }

        // Retreive POM : Priority : 1- Local source dir, 2- Local Repo, 3- Remote repos
        File targetPomFile = new File(mavenProject.getLocalSourcePath() + File.separator + "pom.xml");
        if (!targetPomFile.exists() && groupId != null && artifactId != null) {
            targetPomFile = mavenManager.resolveArtifact(mavenProject.getGroupId(), mavenProject.getArtifactId(), mavenProject.getVersion(), "pom", null);
        } else {
            mavenProject.setIsSourceProject(Boolean.TRUE);
        }

        if (targetPomFile != null) {
            mavenProject.setIsFullyResolved(Boolean.TRUE);
            String pomPath = FileUtils.getCleanFullPath(targetPomFile.getAbsolutePath());
            mavenProject.setLocalRepoPomPath(pomPath);

            Model model = mavenManager.getEffectiveModel(targetPomFile);

            if (model != null) {
                mavenProject.setGroupId(model.getGroupId());
                mavenProject.setArtifactId(model.getArtifactId());
                mavenProject.setName(model.getName());
                mavenProject.setVersion(model.getVersion());

                mavenProject.setDescription(model.getDescription());
                mavenProject.setScmConnectionString(model.getScm().getConnection());
                mavenProject.setScmDevConnectionString(model.getScm().getDeveloperConnection());
                mavenProject.setScmTag(model.getScm().getTag());
            }

            return model;
        } else {
            return null;
        }
    }
}
