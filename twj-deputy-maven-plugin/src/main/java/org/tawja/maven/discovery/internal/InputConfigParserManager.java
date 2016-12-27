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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.tawja.maven.discovery.model.InputMavenConfig;
import org.tawja.maven.discovery.model.InputMavenProject;

/**
 *
 * @author jbennani
 */
public class InputConfigParserManager extends AbstractParserManager<InputMavenConfig> {

    /**
     * @return the logger
     */
    @Override
    public Log getLogger() {
        return logger;
    }

    private final Log logger = LogFactory.getLog(InputConfigParserManager.class);

    public InputConfigParserManager() {
        super();
    }

    public InputConfigParserManager(Boolean useXmlConfig) {
        super(useXmlConfig);
    }

    @Override
    protected void internalInitParser() {
        // MODEL
        // Input
        getXstream().alias("config", InputMavenConfig.class);
        getXstream().alias("project", InputMavenProject.class);
    }
}
