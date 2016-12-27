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
package org.tawja.maven.discovery.model;

/**
 *
 * @author jbennani
 */
public class DiscoveredMavenProjectRelation extends Object {

    private DiscoveredMavenProject linkOrigin;
    private DiscoveredMavenProject linkTarget;
    private DiscoveredMavenProjectRelationType linkType;

    public DiscoveredMavenProjectRelation() {
    }

    @Override
    public String toString() {
        return getId();
    }

    /**
     * @return the id
     */
    public String getId() {
        return generateId(linkOrigin.getId(), linkTarget.getId(), linkType.toString());
    }

    /**
     * @param originId
     * @param targetId
     * @param type
     * @return the id
     */
    public static String generateId(String originId, String targetId, String type) {
        return originId + "#" + targetId + "#" + type;
    }

    /**
     * @return the linkOrigin
     */
    public DiscoveredMavenProject getLinkOrigin() {
        return linkOrigin;
    }

    /**
     * @param linkOrigin the linkOrigin to set
     */
    public void setLinkOrigin(DiscoveredMavenProject linkOrigin) {
        this.linkOrigin = linkOrigin;
    }

    /**
     * @return the linkTarget
     */
    public DiscoveredMavenProject getLinkTarget() {
        return linkTarget;
    }

    /**
     * @param linkTarget the linkTarget to set
     */
    public void setLinkTarget(DiscoveredMavenProject linkTarget) {
        this.linkTarget = linkTarget;
    }

    /**
     * @return the linkType
     */
    public DiscoveredMavenProjectRelationType getLinkType() {
        return linkType;
    }

    /**
     * @param linkType the linkType to set
     */
    public void setLinkType(DiscoveredMavenProjectRelationType linkType) {
        this.linkType = linkType;
    }
}
