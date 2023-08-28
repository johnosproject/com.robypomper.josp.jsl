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
public interface JSLContainer extends JSLComponent {

    // Sub components

    /**
     * @return the list of all component's names contained in current container.
     */
    Collection<JSLComponent> getComponents();

    /**
     * Search the <code>name</code> component in the current container components
     * list.
     *
     * @param name the name of searched component.
     * @return the contained component or null if no component correspond to
     * given <code>name</code>.
     */
    JSLComponent getComponentByName(String name);

}
