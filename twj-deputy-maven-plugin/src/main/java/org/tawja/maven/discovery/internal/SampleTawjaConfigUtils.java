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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tawja.maven.discovery.model.InputMavenConfig;
import org.tawja.maven.discovery.model.InputMavenProject;

/**
 *
 * @author jbennani
 */
public class SampleTawjaConfigUtils {

    private static Log logger = LogFactory.getLog(SampleTawjaConfigUtils.class);

    private SampleTawjaConfigUtils() {

    }

    /**
     * @return the logger
     */
    public static Log getLogger() {
        return logger;
    }

    public static InputMavenConfig loadTawjaInputConfig() {
        File inpuitConfigFile = new File("./src/test/resources/discovery/tawja-config.xml");
        DiscoveryManager mgr = new DiscoveryManager(true);
        InputMavenConfig inputConfig = mgr.getInputConfigParserManager().deserialize(inpuitConfigFile);
        return inputConfig;
    }

    public static InputMavenConfig createSampleInputConfig() {
        InputMavenConfig config = new InputMavenConfig();
        config.setBaseDirectory("./");
        InputMavenProject mavenProject = new InputMavenProject();
        // First Project
        mavenProject = new InputMavenProject();
        mavenProject.setGroupId("org.tawja");
        mavenProject.setArtifactId("root");
        mavenProject.setRelativePath("Tawja/twj-root/");
        config.addProject(mavenProject);
        // Second Project
        mavenProject = new InputMavenProject();
        mavenProject.setGroupId("org.tawja");
        mavenProject.setArtifactId("parent");
        mavenProject.setRelativePath("Tawja/twj-main/");
        config.addProject(mavenProject);
        // Third Project
        mavenProject = new InputMavenProject();
        mavenProject.setGroupId("org.tawja.design");
        mavenProject.setArtifactId("twj-design-logos");
        mavenProject.setRelativePath("Tawja/twj-main/");
        config.addProject(mavenProject);

        return config;
    }

}
