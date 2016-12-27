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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import java.io.File;
import java.io.PrintWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 *
 * @author jbennani
 */
public abstract class AbstractParserManager<T extends Object> {

    /**
     * @return the logger
     */
    public Log getLogger() {
        return logger;
    }

    /**
     * @return the xstream
     */
    public XStream getXstream() {
        return xstream;
    }

    /**
     * @return the useXmlConfig
     */
    public Boolean getUseXmlConfig() {
        return useXmlConfig;
    }
    
    private final Log logger = LogFactory.getLog(AbstractParserManager.class);
    
    private XStream xstream;
    private final Boolean useXmlConfig;
    
    public AbstractParserManager() {
        this(false);
    }
    
    public AbstractParserManager(Boolean useXmlConfig) {
        this.useXmlConfig = useXmlConfig;
        initParserIfRequired();
    }
    
    private Class<T> clazz;
    
    public T buildInderlyingBean() {
        try {
            return clazz.newInstance();
        } catch (Exception ex) {
            logger.error("Unable to create new underlying bean instance", ex);
        }
        return null;
    }
    
    protected abstract void internalInitParser();
    
    public void initParserIfRequired() {
        if (getXstream() == null) {
            if (getUseXmlConfig()) {
                xstream = new XStream();
                //XStream xstream = new XStream(new DomDriver()); // does not require XPP3 library
                //XStream xstream = new XStream(new StaxDriver()); // does not require XPP3 library starting with Java 6
            } else {
                xstream = new XStream(new JettisonMappedXmlDriver());
            }
            internalInitParser();
        }
    }
    
    private void resetParser() {
        xstream = null;
    }
    
    public String serialize(T parsedBean) {
        String outputString = null;
        try {
            outputString = getXstream().toXML(parsedBean);
        } catch (Exception ex) {
            getLogger().error("Unable to Serialise ", ex);
        }
        
        return outputString;
    }
    
    public void serializeInputMavenConfig(File outputFile, T parsedBean) {
        try {
            String ouputString = serialize(parsedBean);
            try (PrintWriter out = new PrintWriter(outputFile)) {
                out.println(ouputString);
            }
        } catch (Exception ex) {
            getLogger().error("Unable to Serialise input maven config : '" + outputFile.getAbsolutePath() + "'", ex);
        }
    }
    
    public T deserialize(String inputString) {
        try {
            T inputConfig = (T) getXstream().fromXML(inputString);
            return inputConfig;
        } catch (Exception ex) {
            getLogger().error("Unable to Deserialise", ex);
        }
        
        return buildInderlyingBean();
    }
    
    public T deserialize(File inputFile) {
        try {
            String inputString = FileUtils.readFile(inputFile.getAbsolutePath(), "UTF-8");
            T parsedBean = deserialize(inputString);
            return parsedBean;
        } catch (Exception ex) {
            getLogger().error("Unable to Deserialise : '" + inputFile.getAbsolutePath() + "'", ex);
        }
        
        return buildInderlyingBean();
    }
}
