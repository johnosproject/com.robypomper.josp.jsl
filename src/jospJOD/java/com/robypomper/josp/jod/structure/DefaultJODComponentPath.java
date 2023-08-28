/*******************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jod.structure;

import java.util.ArrayList;
import java.util.List;


/**
 * Default implementation of {@link JODComponentPath} interface.
 * <p>
 * This implementation provide also component's search method. With static methods
 * {@link #searchComponent(JODContainer, JODComponentPath)} and
 * {@link #searchComponents(JODContainer, JODComponentPath)} you'll get the
 * component(s) referenced by given {@link JODComponentPath} using the
 * {@link JODContainer} as path's root.
 */
public class DefaultJODComponentPath implements JODComponentPath {

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
    public DefaultJODComponentPath(String pathStr) {
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
    public DefaultJODComponentPath(JODComponentPath path, String child) {
        // ToDo: check behaviour with not Unique path (focus on ...>* paths)
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
    public JODComponentPath add(String child) {
        return new DefaultJODComponentPath(this, child);
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
    public static JODComponent searchComponent(JODContainer container, JODComponentPath path) {
        return searchComponentByUniquePath(container, path.getPathContainers(), path.getComponentName());
    }

    /**
     * Search and return <code>path</code> referenced components using the component
     * <code>container</code> as path's root.
     * <p>
     * Differently from {@link #searchComponent(JODContainer, JODComponentPath)}
     * this method handle not {@link #isUnique()} paths and return a list fo
     *
     * @param container the container to use as root of given <code>path</code>.
     * @param path      the path of the components required.
     * @return a list of components that match the <code>path</code> or an empty
     * list.
     */
    public static List<JODComponent> searchComponents(JODContainer container, JODComponentPath path) {
        throw new RuntimeException("AbsJODComponentPath::searchComponents() not yet implemented");
    }

    /**
     * Recursive method for {@link #searchComponent(JODContainer, JODComponentPath)}
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
    private static JODComponent searchComponentByUniquePath(JODComponent currentComp, List<String> compsPath, String compName) {
        if (currentComp == null) return null;

        if (!(currentComp instanceof JODContainer)) return null;

        JODContainer currContainer = (JODContainer) currentComp;
        if (compsPath.isEmpty())
            return currContainer.getComponentByName(compName);

        JODComponent nextComp = currContainer.getComponentByName(compsPath.get(0));
        List<String> nextCompsPath = new ArrayList<>(compsPath);
        nextCompsPath.remove(0);
        return searchComponentByUniquePath(nextComp, nextCompsPath, compName);
    }

}
