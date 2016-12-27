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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import com.google.common.base.Function;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author jbennani
 */
public class JungTest1 {

    private Logger logger = LoggerFactory.getLogger(JungTest1.class);

    public JungTest1() {
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

    @Test
    public void testBatik01() throws Exception {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = null;
            docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            Element svgelem = document.createElement("svg");
            document.appendChild(svgelem);

            // Create an instance of the SVG Generator
            SVGGraphics2D graphic2d = new SVGGraphics2D(document);

            VisualizationImageServer<String, String> server = new SimpleGraphView2().getServer();
            server.printAll(graphic2d);

            // svgweb (IE fallback) needs size somehow defined
            Element el = graphic2d.getRoot();
            el.setAttributeNS(null, "viewBox", "0 0 350 350");
            el.setAttributeNS(null, "style", "width:100%;height:100%;");

            // Finally, stream out SVG to the standard output using
            // UTF-8 encoding.
            boolean useCSS = true; // we want to use CSS style attributes
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            Writer out = new OutputStreamWriter(bout, "UTF-8");
            graphic2d.stream(el, out, useCSS, false);

            //SvgComponent svgComponent = new SvgComponent();
            //svgComponent.setWidth(350, UNITS_PIXELS);
            //svgComponent.setHeight(350, UNITS_PIXELS);
            //svgComponent.setSvg(new String(bout.toByteArray()));
            //addComponent(svgComponent);
            String oFileName = "testBatik01.svg";
            try (OutputStream outputStream = new FileOutputStream(oFileName)) {
                bout.writeTo(outputStream);
            }
            //DeviceCMYKColorSpace
            transcodeToPng(oFileName);

        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    public void transcodeToPng(String filePath) throws Exception {
        // Create a JPEG transcoder
        PNGTranscoder t = new PNGTranscoder();

        // Set the transcoding hints.
        /*
         t.addTranscodingHint(PNGTranscoder.KEY_QUALITY, new Float(.8));
         */
        // Create the transcoder input.
        File inFile = new File(filePath);
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

    /**
     * Adapted example from Jung tutorial
     */
    class SimpleGraphView2 extends SparseMultigraph<String, String> {

        /**
         * Creates a new instance of SimpleGraphView
         */
        public SimpleGraphView2() {
            addVertex("Vertex-1");
            addVertex("Vertex-2");
            addVertex("Vertex-3");
            addEdge("Edge-A", "Vertex-1", "Vertex-3");
            addEdge("Edge-B", "Vertex-2", "Vertex-3", EdgeType.DIRECTED);
            addEdge("Edge-C", "Vertex-2", "Vertex-1", EdgeType.DIRECTED);
            addEdge("Edge-D", "Vertex-1", "Vertex-2", EdgeType.DIRECTED);

        }

        /**
         * @param args the command line arguments
         * @return
         */
        public VisualizationImageServer<String, String> getServer() {
            // Layout<V, E>, VisualizationComponent<V,E>
            Layout<String, String> layout = new CircleLayout<String, String>(
                    this);
            layout.setSize(new Dimension(300, 300));
            VisualizationImageServer<String, String> vv = new VisualizationImageServer<String, String>(
                    layout, new Dimension(350, 350));
            // Setup up a new vertex to paint transformer...
            Function<String, Paint> vertexPaint = new Function<String, Paint>() {
                public Paint apply(String i) {
                    return Color.GREEN;
                }
            };
            // Set up a new stroke Transformer for the edges
            float dash[] = {10.0f};
            final Stroke edgeStroke = new BasicStroke(1.0f,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash,
                    0.0f);
            Function<String, Stroke> edgeStrokeTransformer = new Function<String, Stroke>() {
                public Stroke apply(String s) {
                    return edgeStroke;
                }
            };
            Function<Object, String> labelTransformer = new Function<Object, String>() {
                public String apply(Object o) {
                    return o.toString();
                }
            };
            vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
            vv.getRenderContext().setEdgeStrokeTransformer(
                    edgeStrokeTransformer);
            vv.getRenderContext().setVertexLabelTransformer(
                    labelTransformer);
            vv.getRenderContext().setEdgeLabelTransformer(
                    labelTransformer);
            vv.getRenderer().getVertexLabelRenderer()
                    .setPosition(Position.CNTR);

            return vv;
        }
    }
}
