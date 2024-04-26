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

import java.util.ArrayList;
import java.util.List;


/**
 * Default implementation of {@link JSLComponentPath} interface.
 * <p>
 * This implementation provide also component's search method. With static methods
 * {@link #searchComponent(JSLContainer, JSLComponentPath)} and
 * {@link #searchComponents(JSLContainer, JSLComponentPath)} you'll get the
 * component(s) referenced by given {@link JSLComponentPath} using the
 * {@link JSLContainer} as path's root.
 */
public class DefaultJSLComponentPath implements JSLComponentPath {

    // Internal vars

    private final String pathStr;
    private final List<String> containers;
    private final String compName;


    // Constructor

    /**
     * Default constructor that initialize new path from initialization string.
     *
     * @param pathStr the initialization string.
     */
    public DefaultJSLComponentPath(String pathStr) {
        this.pathStr = pathStr;
        containers = new ArrayList<>();
        for (String s : pathStr.split(StructureDefinitions.PATH_SEP))
            if (!s.trim().isEmpty())
                containers.add(s.trim());
            else
                containers.add(StructureDefinitions.PATH_ALL);
        compName = containers.remove(containers.size() - 1);
    }

    /**
     * Create child constructor.
     * <p>
     * This constructor create a path that refer to <code>child</code> component
     * of container referenced by <code>path</code>.
     *
     * @param path  the container's path.
     * @param child the children name.
     */
    public DefaultJSLComponentPath(JSLComponentPath path, String child) {
        // ToDo: Check DefaultJSLComponentPath constructor behaviour with not Unique path (focus on ...>* paths)
        this((path.getString().compareTo(StructureDefinitions.PATH_STR_ROOT) == 0 ? "" : path.getString() + StructureDefinitions.PATH_SEP) + child);
    }


    // Component

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUnique() {
        return (!pathStr.contains(StructureDefinitions.PATH_ALL)) && (!pathStr.startsWith(StructureDefinitions.PATH_SEP));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getComponentName() {
        return compName;
    }


    // Super containers

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getPathContainers() {
        return containers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevel() {
        if (!isUnique())
            return -1;
        return containers.size() + 1;
    }


    // Mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLComponentPath add(String child) {
        return new DefaultJSLComponentPath(this, child);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString() {
        return pathStr;
    }


    // Search

    /**
     * Search and return <code>path</code> referenced component using the component
     * <code>container</code> as path's root.
     *
     * @param container the container to use as root of given <code>path</code>.
     * @param path      the path of the component required. It must be {@link #isUnique()}
     *                  path.
     * @return the component reference by <code>path</code> or null.
     */
    public static JSLComponent searchComponent(JSLContainer container, JSLComponentPath path) {
        return searchComponentByUniquePath(container, path.getPathContainers(), path.getComponentName());
    }

    /**
     * Search and return <code>path</code> referenced components using the component
     * <code>container</code> as path's root.
     * <p>
     * Differently from {@link #searchComponent(JSLContainer, JSLComponentPath)}
     * this method handle not {@link #isUnique()} paths and return a list fo
     *
     * @param container the container to use as root of given <code>path</code>.
     * @param path      the path of the components required.
     * @return a list of components that match the <code>path</code> or an empty
     * list.
     */
    public static List<JSLComponent> searchComponents(JSLContainer container, JSLComponentPath path) {
        throw new RuntimeException("AbsJODComponentPath::searchComponents() not yet implemented");
    }

    /**
     * Recursive method for {@link #searchComponent(JSLContainer, JSLComponentPath)}
     * method.
     *
     * <b>Here is implemented the logic of JODComponentPath</b> ({@link #isUnique()}=true)
     * search function.
     *
     * @param currentComp latest component that matched the <code>compsPath</code>
     *                    sequence.
     * @param compsPath   the list of super containers of required component, for
     *                    each interaction (this is a recursive method) this list
     *                    lost his first element. Recursion terminate when this
     *                    list is empty.
     * @param compName    required component's name.
     * @return the component reference by <code>path</code> or null.
     */
    private static JSLComponent searchComponentByUniquePath(JSLComponent currentComp, List<String> compsPath, String compName) {
        if (currentComp == null) return null;

        if (!(currentComp instanceof JSLContainer)) return null;

        JSLContainer currContainer = (JSLContainer) currentComp;
        if (compsPath.isEmpty())
            return currContainer.getComponentByName(compName);

        JSLComponent nextComp = currContainer.getComponentByName(compsPath.get(0));
        List<String> nextCompsPath = new ArrayList<>(compsPath);
        nextCompsPath.remove(0);
        return searchComponentByUniquePath(nextComp, nextCompsPath, compName);
    }

}
