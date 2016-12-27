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

import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.tawja.maven.discovery.model.DiscoveredMavenProject;
import org.tawja.maven.discovery.model.DiscoveredMavenProjectRelation;

/**
 *
 * @author jbennani
 */
class RadialRings implements VisualizationServer.Paintable {

    Collection<Double> depths;
    
    RadialTreeLayout<DiscoveredMavenProject, DiscoveredMavenProjectRelation> radialLayout;
    Forest<DiscoveredMavenProject, DiscoveredMavenProjectRelation> graph;
    VisualizationViewer<DiscoveredMavenProject, DiscoveredMavenProjectRelation> vv;

    public RadialRings(RadialTreeLayout<DiscoveredMavenProject, DiscoveredMavenProjectRelation> radialLayout, Forest<DiscoveredMavenProject, DiscoveredMavenProjectRelation> graph, VisualizationViewer<DiscoveredMavenProject, DiscoveredMavenProjectRelation> vv) {
        this.radialLayout = radialLayout;
        this.graph = graph;
        this.vv = vv;
        
        depths = getDepths();
    }

    private Collection<Double> getDepths() {
        Set<Double> depths = new HashSet<Double>();
        Map<DiscoveredMavenProject, PolarPoint> polarLocations = radialLayout.getPolarLocations();
        for (DiscoveredMavenProject v : graph.getVertices()) {
            PolarPoint pp = polarLocations.get(v);
            depths.add(pp.getRadius());
        }
        return depths;
    }

    public void paint(Graphics g) {
        g.setColor(Color.gray);
        Graphics2D g2d = (Graphics2D) g;
        Point2D center = radialLayout.getCenter();

        Ellipse2D ellipse = new Ellipse2D.Double();
        for (double d : depths) {
            ellipse.setFrameFromDiagonal(center.getX() - d, center.getY() - d,
                    center.getX() + d, center.getY() + d);
            Shape shape
                    = vv.getRenderContext().getMultiLayerTransformer().transform(ellipse);
            g2d.draw(shape);
        }
    }

    public boolean useTransform() {
        return true;
    }
}
