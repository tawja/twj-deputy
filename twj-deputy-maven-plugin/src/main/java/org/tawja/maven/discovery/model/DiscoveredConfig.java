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
package org.tawja.maven.discovery.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jbennani
 */
public class DiscoveredConfig {

    private String baseDirectory;
    private String localRepoPath;
    private Map<String, DiscoveredMavenRemoteRepository> mavenRemoteRepositories;

    private final Map<String, DiscoveredMavenProject> mavenProjects;
    private final Map<String, DiscoveredMavenProjectRelation> mavenProjectRelations;

    public DiscoveredConfig() {
        this.mavenRemoteRepositories = new HashMap();
        this.mavenProjects = new HashMap();
        this.mavenProjectRelations = new HashMap();
    }

    /**
     * @return the mavenProjects
     */
    public Collection<DiscoveredMavenProject> getMavenProjects() {
        return mavenProjects.values();
    }

    /**
     * @param groupId
     * @param artifactId
     * @return the mavenProject
     */
    public DiscoveredMavenProject getMavenProject(String groupId, String artifactId) {
        return mavenProjects.get(DiscoveredMavenProject.generateId(groupId, artifactId));
    }

    /**
     * @param originId
     * @param targetId
     * @param type
     * @return the mavenProjectRelation
     */
    public DiscoveredMavenProjectRelation getMavenProjectRelation(String originId, String targetId, String type) {
        return mavenProjectRelations.get(DiscoveredMavenProjectRelation.generateId(originId, targetId, type));
    }

    /**
     * @param mavenProjectList the mavenProjects to set
     */
    public void setMavenProjects(Collection<DiscoveredMavenProject> mavenProjectList) {
        mavenProjects.clear();
        for (DiscoveredMavenProject mavenProject : mavenProjectList) {
            mavenProjects.put(mavenProject.getId(), mavenProject);
        }
    }

    /**
     * @param mavenProject the mavenConfig to add to the mavenProjects list
     */
    public void putMavenProject(DiscoveredMavenProject mavenProject) {
        if (!mavenProjects.containsKey(mavenProject.getId())) {
            mavenProjects.put(mavenProject.getId(), mavenProject);
        }
    }

    /**
     * @param mavenProjectRelation the mavenConfig to add to the mavenProjects
     * list
     */
    public void putMavenProjectRelations(DiscoveredMavenProjectRelation mavenProjectRelation) {
        if (!mavenProjectRelations.containsKey(mavenProjectRelation.getId())) {
            mavenProjectRelations.put(mavenProjectRelation.getId(), mavenProjectRelation);
        }
    }

    /**
     * @return the mavenProjectRelations
     */
    public Collection<DiscoveredMavenProjectRelation> getMavenProjectRelations() {
        return mavenProjectRelations.values();
    }

    /**
     * @param mavenProjectRelationList the mavenProjectRelations to set
     */
    public void setMavenProjectRelations(Collection<DiscoveredMavenProjectRelation> mavenProjectRelationList) {
        mavenProjectRelations.clear();
        for (DiscoveredMavenProjectRelation mavenProjectRelation : mavenProjectRelationList) {
            mavenProjectRelations.put(mavenProjectRelation.getId(), mavenProjectRelation);
        }
    }

    /**
     * @return the baseDirectory
     */
    public String getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * @param baseDirectory the baseDirectory to set
     */
    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * @return the localRepoPath
     */
    public String getLocalRepoPath() {
        return localRepoPath;
    }

    /**
     * @param localRepoPath the localRepoPath to set
     */
    public void setLocalRepoPath(String localRepoPath) {
        this.localRepoPath = localRepoPath;
    }

    /**
     * @return the mavenRemoteRepositories
     */
    public Collection<DiscoveredMavenRemoteRepository> getMavenRemoteRepositories() {
        return mavenRemoteRepositories.values();
    }

    /**
     * @param mavenRemoteRepositoryList the mavenRemoteRepositoryList to set
     */
    public void setMavenRemoteRepositories(Collection<DiscoveredMavenRemoteRepository> mavenRemoteRepositoryList) {
        mavenRemoteRepositories.clear();
        for (DiscoveredMavenRemoteRepository mavenRemoteRepository : mavenRemoteRepositoryList) {
            mavenRemoteRepositories.put(mavenRemoteRepository.getId(), mavenRemoteRepository);
        }
    }

    /**
     * @param mavenRemoteRepository the mavenRemoteRepository to be added to
     * mavenRemoteRepositories
     */
    public void putMavenRemoteRepository(DiscoveredMavenRemoteRepository mavenRemoteRepository) {
        this.mavenRemoteRepositories.put(mavenRemoteRepository.getId(), mavenRemoteRepository);
    }
}
