package org.tawja.maven.discovery.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilder;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.tawja.maven.discovery.internal.DiscoveryManager;

/**
 * A helper to boot the repository system and a repository system session.
 */
public class Booter {

    private static Log logger = LogFactory.getLog(DiscoveryManager.class);

    public static final String userHome = System.getProperty("user.home");
    public static final File userMavenConfigurationHome = new File(userHome, ".m2");
    public static final String envM2Home = System.getenv("M2_HOME");
    public static final File DEFAULT_USER_SETTINGS_FILE = new File(userMavenConfigurationHome, "settings.xml");
    public static final File DEFAULT_GLOBAL_SETTINGS_FILE
            = new File(System.getProperty("maven.home", envM2Home != null ? envM2Home : ""), "conf/settings.xml");

    public static Settings getEffectiveSettings() {
        SettingsBuildingRequest settingsBuildingRequest = new DefaultSettingsBuildingRequest();
        settingsBuildingRequest.setSystemProperties(System.getProperties());
        settingsBuildingRequest.setUserSettingsFile(DEFAULT_USER_SETTINGS_FILE);
        settingsBuildingRequest.setGlobalSettingsFile(DEFAULT_GLOBAL_SETTINGS_FILE);

        SettingsBuildingResult settingsBuildingResult;
        DefaultSettingsBuilderFactory mvnSettingBuilderFactory = new DefaultSettingsBuilderFactory();
        DefaultSettingsBuilder settingsBuilder = mvnSettingBuilderFactory.newInstance();

        Settings effectiveSettings = null;

        try {
            settingsBuildingResult = settingsBuilder.build(settingsBuildingRequest);

            effectiveSettings = settingsBuildingResult.getEffectiveSettings();
            /**
             * Map<String, Profile> profilesMap =
             * effectiveSettings.getProfilesAsMap();
             * Collection<RemoteRepository> remotes = new ArrayList<>(20); for
             * (String profileName : effectiveSettings.getActiveProfiles()) {
             * Profile profile = profilesMap.get(profileName); List<Repository>
             * repositories = profile.getRepositories(); for (Repository repo :
             * repositories) { RemoteRepository remoteRepo = new
             * RemoteRepository.Builder(repo.getId(), "default",
             * repo.getUrl()).build();
             *
             * remotes.add(remoteRepo); } }
             *
             */
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return effectiveSettings;
    }

    public static List<RemoteRepository> getRemoteRepositores(Settings effectiveSettings) {
        Map<String, Profile> profilesMap
                = effectiveSettings.getProfilesAsMap();
        List<RemoteRepository> remotes = new ArrayList<>(20);
        for (String profileName : effectiveSettings.getActiveProfiles()) {
            Profile profile = profilesMap.get(profileName);
            if (profile != null) {
                List<Repository> repositories = profile.getRepositories();
                for (Repository repo
                        : repositories) {
                    RemoteRepository remoteRepo = new RemoteRepository.Builder(repo.getId(), "default",
                            repo.getUrl()).build();

                    remotes.add(remoteRepo);
                }
            }
        }
        return remotes;
    }

    public static RepositorySystem newRepositorySystem() {
        ManualRepositorySystemFactory.initialize();
        return ManualRepositorySystemFactory.getRepositorySystem();
        // return org.eclipse.aether.examples.guice.GuiceRepositorySystemFactory.newRepositorySystem();
        // return org.eclipse.aether.examples.sisu.SisuRepositorySystemFactory.newRepositorySystem();
        // return org.eclipse.aether.examples.plexus.PlexusRepositorySystemFactory.newRepositorySystem();
    }

    public static DefaultRepositorySystemSession newRepositorySystemSession(RepositorySystem system, String localRepoPath) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        if (localRepoPath == null) {
            localRepoPath = "target/local-repo";
        }
        logger.info("LOCAL REPO : " + localRepoPath);
        LocalRepository localRepo = new LocalRepository(localRepoPath);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        session.setTransferListener(new ConsoleTransferListener());
        session.setRepositoryListener(new ConsoleRepositoryListener());

        // uncomment to generate dirty trees
        // session.setDependencyGraphTransformer( null );
        return session;
    }

    public static LocalRepository getLocalRepository() {
        LocalRepository localRepo = new LocalRepository(getEffectiveSettings().getLocalRepository());
        return localRepo;
    }

    public static List<RemoteRepository> newRepositories(RepositorySystem system, RepositorySystemSession session, Settings effectiveSettings, Boolean localRepoAsRemote) {

        List<RemoteRepository> remoteRepos = getRemoteRepositores(effectiveSettings);
        if (remoteRepos == null || remoteRepos.size() == 0) {
            remoteRepos = new ArrayList<RemoteRepository>(Arrays.asList(newCentralRepository()));
        }
        //remoteRepos = new ArrayList<RemoteRepository>();
        if (localRepoAsRemote) {
            LocalRepository localRepo = getLocalRepository();
            try {
            RemoteRepository local = new RemoteRepository.Builder("local", "default", localRepo.getBasedir().toURI().toURL().toString()).build();
            remoteRepos.add(local);
            } catch (Exception ex) {
                logger.error("Unable to get URL from localRepo path", ex);
            }
        }
        for (RemoteRepository remoteRepo : remoteRepos) {
            logger.info("REMOTE REPO : " + remoteRepo.toString());
        }
        
        return remoteRepos;
    }

    private static RemoteRepository newCentralRepository() {
        return new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2/").build();
    }

}
