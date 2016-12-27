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
package org.tawja.maven.discovery.visualize;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.shortestpath.MinimumSpanningForest2;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.util.Animator;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.util.Collection;
import javax.swing.JComponent;
import org.tawja.maven.discovery.internal.AwtUtils;
import org.tawja.maven.discovery.model.DiscoveredMavenProject;
import org.tawja.maven.discovery.model.DiscoveredMavenProjectRelation;
import static org.tawja.maven.discovery.visualize.ProjectGraphSimplePanel.g_array;

/**
 *
 * @author jbennani
 */
/**
 * Adapted example from Jung tutorial
 */
public class JungProjectGraphViewer extends SparseMultigraph<DiscoveredMavenProject, DiscoveredMavenProjectRelation> {

    private Layout<DiscoveredMavenProject, DiscoveredMavenProjectRelation> layout;

    private Integer width;
    private Integer height;
    private Float marginPercentage;
    
    VisualizationServer.Paintable rings;

    /**
     * Creates a new instance of SimpleGraphView
     */
    public JungProjectGraphViewer() {
        /*
            addVertex("Vertex-1");
            addVertex("Vertex-2");
            addVertex("Vertex-3");
            addEdge("Edge-A", "Vertex-1", "Vertex-3");
            addEdge("Edge-B", "Vertex-2", "Vertex-3", EdgeType.DIRECTED);
            addEdge("Edge-C", "Vertex-2", "Vertex-1", EdgeType.DIRECTED);
            addEdge("Edge-D", "Vertex-1", "Vertex-2", EdgeType.DIRECTED);
         */
    }

    public JungProjectGraphViewer(Collection<DiscoveredMavenProject> projects, Collection<DiscoveredMavenProjectRelation> relations, Integer width, Integer height, Float marginPercentage, JungProjectGraphLayoutEnum layoutEnum) {
        initialize(projects, relations, width, height, marginPercentage, layoutEnum);
    }

    public void initialize(Collection<DiscoveredMavenProject> projects, Collection<DiscoveredMavenProjectRelation> relations, Integer width, Integer height, Float marginPercentage, JungProjectGraphLayoutEnum layoutEnum) {
        this.width = width;
        this.height = height;
        this.marginPercentage = marginPercentage;

        layout = createLayout(layoutEnum);

        for (DiscoveredMavenProject project : projects) {
            addVertex(project);
        }
        for (DiscoveredMavenProjectRelation relation : relations) {
            addEdge(relation, relation.getLinkOrigin(), relation.getLinkTarget(), EdgeType.DIRECTED);
        }
    }

    public VisualizationImageServer<DiscoveredMavenProject, DiscoveredMavenProjectRelation> getServer() {
        VisualizationImageServer<DiscoveredMavenProject, DiscoveredMavenProjectRelation> vv = new VisualizationImageServer<>(getLayout(), new Dimension(getWidth(), getHeight()));
        initializeServer(vv);
        return vv;
    }

    public VisualizationViewer<DiscoveredMavenProject, DiscoveredMavenProjectRelation> getViewer() {
        VisualizationViewer<DiscoveredMavenProject, DiscoveredMavenProjectRelation> vv = new VisualizationViewer<>(getLayout(), new Dimension(getWidth(), getHeight()));
        initializeServer(vv);
        initializeViewer(vv);
        return vv;
    }

    protected Layout<DiscoveredMavenProject, DiscoveredMavenProjectRelation> createLayout(JungProjectGraphLayoutEnum layoutEnum) {
        //Layout<DiscoveredMavenProject, DiscoveredMavenProjectRelation> layout = new CircleLayout<>(this);
        //Layout<DiscoveredMavenProject, DiscoveredMavenProjectRelation> layout = new FRLayout<>(this);
        Layout<DiscoveredMavenProject, DiscoveredMavenProjectRelation> layout = new FRLayout<>(this);
        
        layout.setSize(new Dimension(Math.round(getWidth() - getWidth() * getMarginPercentage()), Math.round(getHeight() - getHeight() * getMarginPercentage())));
        
        return layout;
    }

    protected void initializeServer(BasicVisualizationServer<DiscoveredMavenProject, DiscoveredMavenProjectRelation> vv) {
        //Layout<DiscoveredMavenProject, DiscoveredMavenProjectRelation> layout = new CircleLayout<>(this);
        //layout.setSize(new Dimension(Math.round(width-width*marginPercentage), Math.round(height-height*marginPercentage)));

        //BasicVisualizationServer<DiscoveredMavenProject, DiscoveredMavenProjectRelation> vv = new BasicVisualizationServer<>(layout, new Dimension(width, height));
        //VisualizationImageServer<DiscoveredMavenProject, DiscoveredMavenProjectRelation> vv = new VisualizationImageServer<>(layout, new Dimension(width, height));
        //VisualizationViewer<DiscoveredMavenProject, DiscoveredMavenProjectRelation> vv = new VisualizationViewer<>(layout, new Dimension(width, height));
        // Setup up a new vertex to paint transformer...
        Function<DiscoveredMavenProject, Paint> vertexPaint = new ProjectPickableVertexPaintTransformer(vv.getPickedVertexState());

        // Set up a new stroke Transformer for the edges
        float dash[] = {10.0f};
        final Stroke edgeStroke = new BasicStroke(1.0f,
                BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash,
                0.0f);
        Function<DiscoveredMavenProjectRelation, Stroke> edgeStrokeTransformer = new Function<DiscoveredMavenProjectRelation, Stroke>() {
            @Override
            public Stroke apply(DiscoveredMavenProjectRelation relation) {
                return edgeStroke;
            }
        };
        Function<DiscoveredMavenProject, String> vertexLabelTransformer = new Function<DiscoveredMavenProject, String>() {
            @Override
            public String apply(DiscoveredMavenProject o) {
                //return o.toString();
                //return "<html><div bgcolor=\"#FFFFFF\"><center>" + o.getGroupId() + "<br/><b>" + o.getArtifactId() + "</b></center></div></html>";
                //return "<html><center>" + o.getGroupId() + "<br/><b>" + o.getArtifactId() + "</b><br/><br/><br/><b>" + o.getModuleName() + "</b></center></html>";
                //return "<html><center>" + o.getGroupId() + "<br/><br/><b>" + o.getArtifactId() + "</b></center></html>";
                return "<html><center>" + o.getGroupId() + "<br/><b>" + o.getArtifactId() + "</b><br/><br/>" + o.getName() + "<br/><br/></center></html>";
            }
        };
        Function<DiscoveredMavenProjectRelation, String> edgeLabelTransformer = new Function<DiscoveredMavenProjectRelation, String>() {
            @Override
            public String apply(DiscoveredMavenProjectRelation o) {
                //return o.toString();
                //return "<html>" + o.getLinkOrigin().toString() + "<br/>" + o.getLinkTarget().toString() + "<br/>" + o.getLinkType().toString() + "</html>";
                return "<html><center>" + o.getLinkType().toString() + "</center></html>";
            }
        };

        final Color vertexLabelColor = Color.BLACK;
        DefaultVertexLabelRenderer vertexLabelRenderer
                = new DefaultVertexLabelRenderer(vertexLabelColor) {
            @Override
            public <V> Component getVertexLabelRendererComponent(
                    JComponent vv, Object value, Font font,
                    boolean isSelected, V vertex) {
                super.getVertexLabelRendererComponent(
                        vv, value, font, isSelected, vertex);
                setForeground(vertexLabelColor);
                return this;
            }
        };

        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
        vv.getRenderContext().setVertexLabelRenderer(vertexLabelRenderer);
        vv.getRenderContext().setVertexLabelTransformer(vertexLabelTransformer);
        vv.getRenderContext().setEdgeLabelTransformer(edgeLabelTransformer);

        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        //vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.AUTO);

        // Center
        MutableTransformer view = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
        MutableTransformer layout01 = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
        Point2D ctr = vv.getCenter();
        Point2D pnt = view.inverseTransform(ctr);
        double scale = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
        double deltaX = (ctr.getX() - Math.round(getWidth() - getWidth() * getMarginPercentage()) / 2) * 1 / scale;
        double deltaY = (ctr.getY() - Math.round(getHeight() - getHeight() * getMarginPercentage()) / 2) * 1 / scale;
        Point2D delta = new Point2D.Double(deltaX, deltaY);
        layout01.translate(deltaX, deltaY);

        //return vv;
    }

    protected void initializeViewer(VisualizationViewer<DiscoveredMavenProject, DiscoveredMavenProjectRelation> vv) {
    }

    /**
     * @return the layout
     */
    public Layout<DiscoveredMavenProject, DiscoveredMavenProjectRelation> getLayout() {
        return layout;
    }

    /**
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @return the marginPercentage
     */
    public Float getMarginPercentage() {
        return marginPercentage;
    }
}
