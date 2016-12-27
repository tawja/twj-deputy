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
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author jbennani
 */
public class SvgUtils {

    private static Log logger = LogFactory.getLog(SvgUtils.class);

    private SvgUtils() {

    }

    /**
     * @return the logger
     */
    public static Log getLogger() {
        return logger;
    }

    public static void transcodeSvgToPngFile(String inputSvgFilePath) throws Exception {
        // Create a JPEG transcoder
        PNGTranscoder t = new PNGTranscoder();

        // Set the transcoding hints.
        /*
         t.addTranscodingHint(PNGTranscoder.KEY_QUALITY, new Float(.8));
         */
        // Create the transcoder input.
        File inFile = new File(inputSvgFilePath);
        String svgURI = inFile.toURL().toString();
        TranscoderInput input = new TranscoderInput(svgURI);

        // Create the transcoder output.
        OutputStream ostream = new FileOutputStream(FilenameUtils.removeExtension(inFile.getName()) + ".png");
        TranscoderOutput output = new TranscoderOutput(ostream);

        // Save the image.
        t.transcode(input, output);

        // Flush and close the stream.
        ostream.flush();
        ostream.close();
    }


}
