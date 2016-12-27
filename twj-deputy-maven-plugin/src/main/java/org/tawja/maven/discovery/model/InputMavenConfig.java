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
import java.util.List;

/**
 *
 * @author jbennani
 */
public class InputMavenConfig {

    private String baseDirectory;
    private List<InputMavenProject> projects;

    public InputMavenConfig() {
        this.projects = new ArrayList<InputMavenProject>();
    }

    /**
     * @return the mavenConfig
     */
    public List<InputMavenProject> getProjects() {
        return projects;
    }

    /**
     * @param mavenProjects the mavenConfig to set
     */
    public void setProjects(List<InputMavenProject> mavenProjects) {
        this.projects = mavenProjects;
    }

    /**
     * @param mavenProject the mavenConfig to add to the mavenProjects list
     */
    public void addProject(InputMavenProject projects) {
        this.projects.add(projects);
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
}
