/*
 * Copyright 2013 original author or authors.
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

import java.io.IOException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Common properties for all maven goals related to ParallelExec. Additionnaly
 * manage all inputs needed for the execution (scan source files).
 *
 * @author Adam DUBIEL
 * @author Jaafar BENNANI-SMIRES
 */
@Mojo(name = "discovery", defaultPhase = LifecyclePhase.COMPILE)
public class DiscoveryMojo extends AbstractMojo {

    /**
     * Disable the execution
     */
    @Parameter(property = DiscoveryConstants.PROP_PREFIX + ".disabled", defaultValue = "false")
    protected boolean disabled;

    /**
     * Defines which of the included files in the source directories to exclude
     * (non by default).
     */
    @Parameter
    protected String[] messages;

    /**
     * Inject building OS name.
     */
    @Parameter(defaultValue = "${os.name}")
    protected String osName;

    /**
     * Access to Maven Project object
     */
    @Parameter(property = "project", readonly = true, required = true)
    protected MavenProject mavenProject;

    /**
     * Access to Maven Session object
     */
    @Parameter(property = "session", readonly = true, required = true)
    protected MavenSession mavenSession;

    protected String basedir() {
        try {
            return mavenProject.getBasedir().getCanonicalPath();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not extract basedir of project.", exception);
        }
    }

    /**
     * Main execution method. Childs should only override executeInternal()
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     * @throws org.apache.maven.plugin.MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Log log = getLog();

        log.info("Resolver Information :");
        log.info("     - MavenProject = '" + mavenProject.getGroupId() + ":" + mavenProject.getArtifactId() + "'");
        log.info("     - BaseDir = '" + basedir() + "'");
        log.info("     - Disabled = '" + disabled + "'");
        log.info("     - Messages = '" + messages + "'");
    }
}
