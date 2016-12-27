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

/**
 *
 * @author jbennani
 */
import java.awt.Paint;

import com.google.common.base.Function;

import edu.uci.ics.jung.visualization.picking.PickedInfo;
import org.tawja.maven.discovery.internal.AwtUtils;
import org.tawja.maven.discovery.model.DiscoveredMavenProject;

/**
 * Paints each vertex according to the <code>Paint</code> parameters given in
 * the constructor, so that picked and non-picked vertices can be made to look
 * different.
 * @param <DiscoveredMavenProject>
 * @param <Paint>
 */
public class ProjectPickableVertexPaintTransformer<V extends DiscoveredMavenProject> implements Function<V, Paint> {

    protected PickedInfo<DiscoveredMavenProject> pi;

    /**
     *
     * @param pi specifies which vertices report as "picked"
     */
    public ProjectPickableVertexPaintTransformer(PickedInfo<DiscoveredMavenProject> pi) {
        if (pi == null) {
            throw new IllegalArgumentException("PickedInfo instance must be non-null");
        }
        this.pi = pi;
    }

    @Override
    public Paint apply(V v) {
        DiscoveredMavenProject project = (DiscoveredMavenProject) v;
        if (pi.isPicked(project)) {
            if (project.getIsFullyResolved()) {
                if (project.getIsRootProject()) {
                    if (project.getIsSourceProject()) {
                        //return Color.GREEN
                        return AwtUtils.hex2Rgb("#009900");
                    } else {
                        //return Color.BLUE;
                        return AwtUtils.hex2Rgb("#0066cc");
                    }
                } else {
                    //return Color.ORANGE;
                    return AwtUtils.hex2Rgb("#cc6600");
                }
            } else {
                //return Color.RED;
                return AwtUtils.hex2Rgb("#cc0000");
            }
        } else {
            if (project.getIsFullyResolved()) {
                if (project.getIsRootProject()) {
                    if (project.getIsSourceProject()) {
                        //return Color.GREEN
                        return AwtUtils.hex2Rgb("#66ff66");
                    } else {
                        //return Color.BLUE;
                        return AwtUtils.hex2Rgb("#66ccff");
                    }
                } else {
                    //return Color.ORANGE;
                    return AwtUtils.hex2Rgb("#ffcc33");
                }
            } else {
                //return Color.RED;
                return AwtUtils.hex2Rgb("#ff6666");
            }
        }
    }

}
