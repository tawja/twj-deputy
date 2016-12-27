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

import org.apache.maven.model.Model;

/**
 *
 * @author jbennani
 */
public class DiscoveredMavenProject extends Object {



    /**
     *
     * @param groupId
     * @param artifactId
     * @return the id
     */
    public static String generateId(String groupId, String artifactId) {
        return groupId + ":" + artifactId;
    }

    private Model model;

    private Boolean isRootProject;
    private Boolean isFfullyResolved;
    private Boolean isSourceProject;
    private String moduleName;

    private String groupId;
    private String artifactId;
    private String name;
    private String version;
    private String localSourcePath;
    private String localRepoPomPath;
    private String description;
    private String scmConnectionString;
    private String scmDevConnectionString;
    private String scmTag;
    //private String scmDevTag;

    public DiscoveredMavenProject() {
        isRootProject = Boolean.FALSE;
        isFfullyResolved = Boolean.FALSE;
        isSourceProject = Boolean.FALSE;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the moduleName
     */
    public String getModuleName() {
        return moduleName;
    }
    /**
     * @param moduleName the moduleName to set
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @return the isFfullyResolved
     */
    public Boolean getIsFullyResolved() {
        return isFfullyResolved;
    }

    /**
     * @param isFfullyResolved the isFfullyResolved to set
     */
    public void setIsFullyResolved(Boolean isFfullyResolved) {
        this.isFfullyResolved = isFfullyResolved;
    }

    @Override
    public String toString() {
        return getId();
    }

    /**
     * @return the model
     */
    public Model getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * @return the id
     */
    public String getId() {
        return generateId(groupId, artifactId);
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the artifactId
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * @param artifactId the artifactId to set
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the scmConnectionString
     */
    public String getScmConnectionString() {
        return scmConnectionString;
    }

    /**
     * @param scmConnectionString the scmConnectionString to set
     */
    public void setScmConnectionString(String scmConnectionString) {
        this.scmConnectionString = scmConnectionString;
    }

    /**
     * @return the scmDevConnectionString
     */
    public String getScmDevConnectionString() {
        return scmDevConnectionString;
    }

    /**
     * @param scmDevConnectionString the scmDevConnectionString to set
     */
    public void setScmDevConnectionString(String scmDevConnectionString) {
        this.scmDevConnectionString = scmDevConnectionString;
    }

    /**
     * @return the scmTag
     */
    public String getScmTag() {
        return scmTag;
    }

    /**
     * @param scmTag the scmTag to set
     */
    public void setScmTag(String scmTag) {
        this.scmTag = scmTag;
    }

    /**
     * @return the localSourcePath
     */
    public String getLocalSourcePath() {
        return localSourcePath;
    }

    /**
     * @param localSourcePath the localSourcePath to set
     */
    public void setLocalSourcePath(String localSourcePath) {
        this.localSourcePath = localSourcePath;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the localRepoPomPath
     */
    public String getLocalRepoPomPath() {
        return localRepoPomPath;
    }

    /**
     * @param localRepoPomPath the localRepoPomPath to set
     */
    public void setLocalRepoPomPath(String localRepoPomPath) {
        this.localRepoPomPath = localRepoPomPath;
    }

    /**
     * @return the isRootProject
     */
    public Boolean getIsRootProject() {
        return isRootProject;
    }

    /**
     * @param isRootProject the isRootProject to set
     */
    public void setIsRootProject(Boolean isRootProject) {
        this.isRootProject = isRootProject;
    }

    /**
     * @return the isSourceProject
     */
    public Boolean getIsSourceProject() {
        return isSourceProject;
    }

    /**
     * @param isSourceProject the isSourceProject to set
     */
    public void setIsSourceProject(Boolean isSourceProject) {
        this.isSourceProject = isSourceProject;
    }

}
