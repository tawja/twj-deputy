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
public enum DiscoveredMavenProjectRelationType {
// Link is between the 
    INFO, // Target gives information about the current origin node
    DEPENDENCY, // Target is a DEPENDENCY of current origin node
    PARENT, // Target is a PARENT of current origin node
    MODULE // Target is a sub MODULE of current origin node
}
