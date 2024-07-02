/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2024 Roberto Pompermaier
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;


/**
 * Base component representation.
 * <p>
 * This interface collect all components commons properties such as name,
 * description, etc...
 */
public interface JSLComponent {

    // Commons properties

    /**
     * @return the name of the component.
     */
    String getName();

    /**
     * @return the description of the component.
     */
    String getDescr();

    /**
     * @return the path of the component (it will be unique path).
     */
    JSLComponentPath getPath();

    /**
     * @return the remote object of the component.
     */
    JSLRemoteObject getRemoteObject();

    /**
     * @return the parent of the component.
     */
    @JsonIgnore
    JSLContainer getParent();

    /**
     * @return the component type.
     */
    String getType();

}
