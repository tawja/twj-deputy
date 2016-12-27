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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author jbennani
 */
public class FileUtils {

    private static Log logger = LogFactory.getLog(FileUtils.class);

    private FileUtils() {

    }

    /**
     * @return the logger
     */
    public static Log getLogger() {
        return logger;
    }

    public static String readFile(String path, String encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static String getCleanFullPath(String filePath) {
        filePath = new File(filePath).getAbsolutePath();
        try {
            filePath = new File(filePath).getCanonicalPath();
        } catch (Exception ex) {
            getLogger().error("Unable to find canonical path : " + filePath, ex);
        }
        return filePath;
    }
}
