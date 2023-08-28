/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.robypomper.josp.jsl.objs.structure;

import java.util.List;


/**
 * Component path representation.
 * <p>
 * ComponentPath instances help to identify a component(s) of an object. Each
 * path is initialized from a path string with following format:
 * <code>
 * [&gt;][SuperCompL1[&gt;SuperCompL2[...]]]|[&gt;] compName|*
 * </code>
 * <p>
 * A ComponentPath can identify a unique component or a set of components using
 * wildcards in the initialization path string.
 * <p>
 * Here a list of valid Component paths:
 * <code>
 * compName                 Single  1st level, component with compName
 * cont1&gt;compName           Single  2nd level (with name cont1), component with compName
 * cont1&gt;cont2&gt;compName     Single  3rd level (with name cont1 and cont2), component with compName
 * &gt;compName                Set     All levels, all components with compName
 * &gt;contXY&gt;compName         Set     All levels (with name contXY), all components with compName
 * &gt;contXY&gt;contZK&gt;compName  Set     All levels (with name contXY and contZK), components with compName
 * *                        Set     1st level, all components
 * cont1&gt;*                  Set     2nd level (with name cont1), all components
 * cont1&gt;cont2&gt;*            Set     3nd level (with name cont1 + cont2), all components
 * &gt;*                       Set     All levels, all components
 * &gt;contXY&gt;*                Set     All level (with name contXY), all components
 * &gt;contXY&gt;contZK&gt;*         Set     All level (with name contXY + contZK), all components
 * </code>
 */
public interface JSLComponentPath {

    // Component

    /**
     * Verify current path if it refer to a single component.
     * <p>
     * Checks are performed looking for wildcards in the initialization path string.
     *
     * @return <code>true</code> if the path string refers to a single component,
     * <code>false</code> otherwise.
     */
    boolean isUnique();

    /**
     * The name of the component referenced from current path.
     * <p>
     * This method return the component name also on not unique paths
     * (see {@link #isUnique()}). Returned value can be the common name of
     * components referenced or the wildcard {@value StructureDefinitions#PATH_ALL}.
     *
     * @return referenced component name or the wildcard {@value StructureDefinitions#PATH_ALL}.
     */
    String getComponentName();


    // Super containers

    /**
     * The super container list of the component referenced from current path.
     * <p>
     * If the component reference is contained in the Root component, then this
     * method return an empty string.
     * <p>
     * To the other side, this method return the component super container list
     * also on not unique paths (see {@link #isUnique()}). Returned value can
     * start with {@value StructureDefinitions#PATH_ALL}.
     *
     * @return ordered list of the super containers or null if current path refer
     * to multiple components.
     */
    List<String> getPathContainers();

    /**
     * The container levels of the component referenced from current path.
     * <p>
     * Components contained in the root of the object receive the level <code>1</code>.
     * Then, each super container in the component path, increase the
     * component level by <code>1</code>.
     * <p>
     * If current path refer to multiple components (when {@link #isUnique()}
     * return <code>false</code>), then this method return a negative value.
     *
     * @return referenced component level or negative value if current path refer
     * to multiple components.
     */
    int getLevel();


    // Mngm

    /**
     * Current path's initialization string.
     *
     * @return the path's initialization string.
     */
    String getString();

    /**
     * Create and return new path composed by current path plus given
     * <code>children</code> component name.
     *
     * @param child string containing the children component's name
     * @return new path referring to parent's <code>child</code> component.
     */
    JSLComponentPath add(String child);

}
