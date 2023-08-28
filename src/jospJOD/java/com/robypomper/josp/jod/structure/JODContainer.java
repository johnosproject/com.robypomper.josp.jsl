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

import java.util.Collection;


/**
 * Base container representation.
 * <p>
 * A container is a special component that can contain other components. This
 * component is used to group and organize components across the object's structure.
 * <p>
 * This interface define methods to access to the sub components: getting the
 * list of component's names or to get once by his name.
 */
public interface JODContainer extends JODComponent {

    // Sub components

    /**
     * @return the list of all component's names contained in current container.
     */
    Collection<JODComponent> getComponents();

    /**
     * Search the <code>name</code> component in the current container components
     * list.
     *
     * @param name the name of searched component.
     * @return the contained component or null if no component correspond to
     * given <code>name</code>.
     */
    JODComponent getComponentByName(String name);

}
