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

import edu.uci.ics.jung.visualization.VisualizationImageServer;
import java.awt.Color;
import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.batik.svggen.SVGGraphics2D;
import org.junit.Assert;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;
import org.eclipse.aether.version.Version;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tawja.maven.discovery.MavenManager;
import org.tawja.maven.discovery.internal.AwtUtils;
import org.tawja.maven.discovery.internal.DiscoveryManager;
import org.tawja.maven.discovery.internal.SampleTawjaConfigUtils;
import static org.tawja.maven.discovery.internal.SampleTawjaConfigUtils.createSampleInputConfig;
import org.tawja.maven.discovery.internal.SvgUtils;
import org.tawja.maven.discovery.model.DiscoveredMavenProject;
import org.tawja.maven.discovery.model.DiscoveredMavenProjectRelation;
import org.tawja.maven.discovery.model.DiscoveredConfig;
import org.tawja.maven.discovery.model.InputMavenConfig;
import org.tawja.maven.discovery.util.Booter;
import org.tawja.maven.discovery.util.ConsoleDependencyGraphDumper;
import org.tawja.maven.discovery.visualize.JungProjectGraphLayoutEnum;
import org.tawja.maven.discovery.visualize.JungProjectGraphViewer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author jbennani
 */
public class SimpleTest {

    private Logger logger = LoggerFactory.getLogger(SimpleTest.class);
    private DiscoveryManager mgr;

    public SimpleTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        mgr = new DiscoveryManager(true);
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
    public void LoadConfigTest() throws Exception {
        logger.info("Program started.");

        MavenManager mgr = new MavenManager();
        MavenProject project = mgr.loadMavenPom("../../src/test/resources/discovery/plugin-config.xml");

        logger.info("Project Loaded : " + project.getGroupId() + ":" + project.getArtifactId());

        logger.info("Program completed.");
    }

    @Test
    public void testXStreamBasicTest() throws Exception {
        logger.info("TEST : XStreamBasicTest");

        // Manager
        DiscoveryManager mgr = new DiscoveryManager(true);

        // MODEL
        InputMavenConfig config = SampleTawjaConfigUtils.createSampleInputConfig();

        // SAVE
        String xml = mgr.getInputConfigParserManager().serialize(config);
        logger.info("XML 1 : ");
        logger.info("\r\n" + xml);

        // LOAD
        InputMavenConfig newConfig = mgr.getInputConfigParserManager().deserialize(xml);
        String xml1 = mgr.getInputConfigParserManager().serialize(newConfig);
        logger.info("XML 2 : ");
        logger.info("\r\n" + xml1);

        Assert.assertEquals("Initial XML is different from the reloaded one.", xml, xml1);
    }

    @Test
    public void XStreamBasicJsonTest() throws Exception {
        logger.info("TEST : XStreamBasicTest");

        // Manager
        DiscoveryManager mgr = new DiscoveryManager();

        // MODEL
        InputMavenConfig config = createSampleInputConfig();

        // SAVE
        String json = mgr.getInputConfigParserManager().serialize(config);
        logger.info("JSON 1 : ");
        logger.info("\r\n" + json);

        // LOAD
        InputMavenConfig newConfig = mgr.getInputConfigParserManager().deserialize(json);
        String json1 = mgr.getInputConfigParserManager().serialize(newConfig);
        logger.info("JSON 2 : ");
        logger.info("\r\n" + json1);

        Assert.assertEquals("Initial JSON is different from the reloaded one.", json, json1);
    }

    @Test
    public void DiscoveryMavenProjects01() throws Exception {
        logger.info("TEST : DiscoveryMavenProjects01");

        DiscoveryManager mgr = new DiscoveryManager();
        InputMavenConfig config = createSampleInputConfig();

        // SAVE
        String json = mgr.getInputConfigParserManager().serialize(config);
        logger.info("JSON 1 : ");
        logger.info("\r\n" + json);

        //------------------------------------------------------------
        RepositorySystem system = Booter.newRepositorySystem();
        Settings effectiveSettings = Booter.getEffectiveSettings();
        RepositorySystemSession session = Booter.newRepositorySystemSession(system, effectiveSettings.getLocalRepository());

        //RepositorySystemSession session = Booter.newRepositorySystemSession(system, "target/local-repo");
        Artifact artifact = new DefaultArtifact("org.tawja:root:[0,)");

        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact(artifact);

        // Local repo added as remote one
        rangeRequest.setRepositories(Booter.newRepositories(system, session, effectiveSettings, false));
        //rangeRequest.set
        //session.getLocalRepositoryManager().

        VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);

        List<Version> versions = rangeResult.getVersions();
        logger.info("VERSIONS : " + versions);
        //------------------------------------------------------------

        //Assert.assertEquals("Initial JSON is different from the reloaded one.", json, json1);
    }

    @Test
    public void DiscoveryMavenProjects02() throws Exception {
        logger.info("TEST : DiscoveryMavenProjects01");

        DiscoveryManager mgr = new DiscoveryManager(true);
        InputMavenConfig inputConfig = createSampleInputConfig();

        // SAVE
        String json = mgr.getInputConfigParserManager().serialize(inputConfig);
        logger.info("JSON 1 : ");
        logger.info("\r\n" + json);

        //------------------------------------------------------------
        DiscoveredConfig config = mgr.discover(inputConfig);
        //------------------------------------------------------------

        // Print Results
        String json1 = mgr.getDiscoveredConfigParserManager().serialize(config);
        logger.info("JSON 2 : ");
        logger.info("\r\n" + json1);

        //Assert.assertEquals("Initial JSON is different from the reloaded one.", json, json1);
    }

    @Test
    public void DiscoveryMavenProjectsToSVG() throws Exception {
        logger.info("TEST : DiscoveryMavenProjectsToSVG");

        DiscoveryManager mgr = new DiscoveryManager(true);
        InputMavenConfig inputConfig = createSampleInputConfig();

        // SAVE
        String json = mgr.getInputConfigParserManager().serialize(inputConfig);
        logger.info("JSON 1 : ");
        logger.info("\r\n" + json);

        //------------------------------------------------------------
        DiscoveredConfig config = mgr.discover(inputConfig);
        //------------------------------------------------------------
        //------------------------------------------------------------
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = null;
            docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            Element svgelem = document.createElement("svg");
            document.appendChild(svgelem);

            Integer width = 1000;
            Integer height = 500;
            Float marginPercentage = 0.2f;

            // Create an instance of the SVG Generator
            SVGGraphics2D graphic2d = new SVGGraphics2D(document);
            graphic2d.setSVGCanvasSize(new Dimension(width, height));
            //graphic2d.setClip(0, 0, 1000, 1000);

            JungProjectGraphViewer vv = new JungProjectGraphViewer(config.getMavenProjects(), config.getMavenProjectRelations(), width, height, marginPercentage, JungProjectGraphLayoutEnum.FRLayout);
            VisualizationImageServer<DiscoveredMavenProject, DiscoveredMavenProjectRelation> server = vv.getServer();
            
            server.setBackground(new Color(0, 0, 0, 0));
            server.printAll(graphic2d);

            // svgweb (IE fallback) needs size somehow defined
            Element el = graphic2d.getRoot();
            el.setAttributeNS(null, "viewBox", "0 0 " + width + " " + height + "");
            el.setAttributeNS(null, "style", "width:100%;height:100%;");
            //graphic2d.setBackground(Color.white);

            // Ask the chart to render into the SVG Graphics2D implementation
            //Size size = config.getSize();
            //int zoomLevel = size.getZoomLevel() > 0 ? size.getZoomLevel() : 1;
            //int viewboxWidth = size.getWidth() / zoomLevel;
            //int viewboxHeight = size.getHeight() / zoomLevel;
            //int chartWidth = viewboxWidth - (60 / zoomLevel);
            //int chartHeight = viewboxHeight - (28 / zoomLevel);
//
            //chart.draw(svgGenerator, new Rectangle2D.Double(10, 10, chartWidth,
            //    chartHeight), null);
            //Element root = svgGenerator.getRoot();
            //root.setAttributeNS(null, "viewBox", "0 0 " + viewboxWidth + " "
            //    + viewboxHeight);
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
            String oFileName = "testBatik02.svg";
            try (OutputStream outputStream = new FileOutputStream(oFileName)) {
                bout.writeTo(outputStream);
            }
            //DeviceCMYKColorSpace
            SvgUtils.transcodeSvgToPngFile(oFileName);

        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
        //------------------------------------------------------------

        // Print Results
        String json1 = mgr.getDiscoveredConfigParserManager().serialize(config);
        logger.info("JSON 2 : ");
        logger.info("\r\n" + json1);

        //Assert.assertEquals("Initial JSON is different from the reloaded one.", json, json1);
    }

    @Test
    public void DiscoveryMavenProjectsToSVGFromFile() throws Exception {
        logger.info("TEST : DiscoveryMavenProjectsToSVGFromFile");

        DiscoveryManager mgr = new DiscoveryManager(true);

        // LOAD
        File inpuitConfigFile = new File("../../src/test/resources/discovery/tawja-config.xml");
        InputMavenConfig inputConfig = mgr.getInputConfigParserManager().deserialize(inpuitConfigFile);
        String json = mgr.getInputConfigParserManager().serialize(inputConfig);
        logger.info("XML input config : ");
        logger.info("\r\n" + json);

        //------------------------------------------------------------
        DiscoveredConfig config = mgr.discover(inputConfig);
        //------------------------------------------------------------
        //------------------------------------------------------------
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = null;
            docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            Element svgelem = document.createElement("svg");
            document.appendChild(svgelem);

            Integer width = 1000;
            Integer height = 500;
            Float marginPercentage = 0.2f;

            // Create an instance of the SVG Generator
            SVGGraphics2D graphic2d = new SVGGraphics2D(document);
            graphic2d.setSVGCanvasSize(new Dimension(width, height));
            //graphic2d.setClip(0, 0, 1000, 1000);

            JungProjectGraphViewer vv = new JungProjectGraphViewer(config.getMavenProjects(), config.getMavenProjectRelations(), width, height, marginPercentage, JungProjectGraphLayoutEnum.FRLayout);
            VisualizationImageServer<DiscoveredMavenProject, DiscoveredMavenProjectRelation> server = vv.getServer();

            //server.setBackground(new Color(0, 0, 0, 0));
            server.setBackground(AwtUtils.hex2AlphaRgb("#FFFFFFFF"));
            server.printAll(graphic2d);

            // svgweb (IE fallback) needs size somehow defined
            Element el = graphic2d.getRoot();
            el.setAttributeNS(null, "viewBox", "0 0 " + width + " " + height + "");
            el.setAttributeNS(null, "style", "width:100%;height:100%;");
            //graphic2d.setBackground(Color.white);

            // Ask the chart to render into the SVG Graphics2D implementation
            //Size size = config.getSize();
            //int zoomLevel = size.getZoomLevel() > 0 ? size.getZoomLevel() : 1;
            //int viewboxWidth = size.getWidth() / zoomLevel;
            //int viewboxHeight = size.getHeight() / zoomLevel;
            //int chartWidth = viewboxWidth - (60 / zoomLevel);
            //int chartHeight = viewboxHeight - (28 / zoomLevel);
//
            //chart.draw(svgGenerator, new Rectangle2D.Double(10, 10, chartWidth,
            //    chartHeight), null);
            //Element root = svgGenerator.getRoot();
            //root.setAttributeNS(null, "viewBox", "0 0 " + viewboxWidth + " "
            //    + viewboxHeight);
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
            String oFileName = "testBatik03.svg";
            try (OutputStream outputStream = new FileOutputStream(oFileName)) {
                bout.writeTo(outputStream);
            }
            //DeviceCMYKColorSpace
            SvgUtils.transcodeSvgToPngFile(oFileName);

        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
        //------------------------------------------------------------

        // Print Results
        String json1 = mgr.getDiscoveredConfigParserManager().serialize(config);
        logger.info("JSON 2 : ");
        logger.info("\r\n" + json1);

        //Assert.assertEquals("Initial JSON is different from the reloaded one.", json, json1);
    }

    @Test
    public void GetDependencyHierarchy() throws Exception {
        System.out.println("------------------------------------------------------------");
        //System.out.println(GetDependencyHierarchy.class.getSimpleName());

        DiscoveryManager mgr = new DiscoveryManager(true);
        //RepositorySystem system = org.eclipse.aether.examples.util.Booter.newRepositorySystem();
        RepositorySystem system = mgr.getMavenManager().getMavenSystem();

        //DefaultRepositorySystemSession session = org.eclipse.aether.examples.util.Booter.newRepositorySystemSession( system );
        DefaultRepositorySystemSession session = (DefaultRepositorySystemSession) mgr.getMavenManager().getMavenSession();

        session.setConfigProperty(ConflictResolver.CONFIG_PROP_VERBOSE, true);
        session.setConfigProperty(DependencyManagerUtils.CONFIG_PROP_VERBOSE, true);

        Artifact artifact = new DefaultArtifact("org.tawja", "root", "pom", "1.0-SNAPSHOT");
        //Artifact artifact = new DefaultArtifact("org.apache.maven:maven-aether-provider:3.1.0");

        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact(artifact);
        //descriptorRequest.setRepositories( org.eclipse.aether.examples.util.Booter.newRepositories( system, session ) );
        descriptorRequest.setRepositories(mgr.getMavenManager().getMavenRepositories());
        ArtifactDescriptorResult descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRootArtifact(descriptorResult.getArtifact());
        collectRequest.setDependencies(descriptorResult.getDependencies());
        collectRequest.setManagedDependencies(descriptorResult.getManagedDependencies());
        collectRequest.setRepositories(descriptorRequest.getRepositories());

        CollectResult collectResult = system.collectDependencies(session, collectRequest);

        collectResult.getRoot().accept(new ConsoleDependencyGraphDumper());
    }

}
