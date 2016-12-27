/*
 * Copyright 2015 Tawja.
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
package org.tawja.maven.plugins.discovery;

import org.apache.maven.project.MavenProject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tawja.maven.discovery.MavenManager;

/**
 *
 * @author jbennani
 */
public class JungTest {

    private Logger logger = LoggerFactory.getLogger(JungTest.class);

    public JungTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void LoadPomTest() throws Exception {
        logger.info("Program started.");

        MavenManager mgr = new MavenManager();
        MavenProject project = mgr.loadMavenPom("../../src/test/resources/discovery/test-pom-01.xml");

        logger.info("Project Loaded : " + project.getGroupId() + ":" + project.getArtifactId());

        logger.info("Program completed.");
    }
}
