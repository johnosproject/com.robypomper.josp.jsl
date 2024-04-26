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


/**
 * Definition class containing all definition for JSL Structure context.
 *
 * <ul>
 *     <li>
 *         <b>Component's type</b> are used to parse the object's structure file.
 *     </li>
 *     <li>
 *         <b>Path's elements</b> sub-strings representing path's elements like
 *         components separators and wildcards.
 *     </li>
 *     <li>
 *         <b>Root properties</b> default root component properties.
 *     </li>
 *     <li>
 *         <b>Parsing properties</b> strings that identifies the JSON/XML/...
 *         component's properties.
 *     </li>
 *     <li>
 *         <b>Default component paths</b> strings that can be used to initialize
 *         default components like Root component.
 *     </li>
 * </ul>
 */
public interface StructureDefinitions {

    // Component types

    String TYPE_CONTAINER = "Container";
    String TYPE_BOOL_STATE = "BooleanState";
    String TYPE_RANGE_STATE = "RangeState";
    String TYPE_BOOL_ACTION = "BooleanAction";
    String TYPE_RANGE_ACTION = "RangeAction";


    // Path's elements

    String PATH_SEP = ">";
    String PATH_ALL = "*";


    // Root properties

    String ROOT_NAME = "root";
    String ROOT_DESCR = "Main object's container.";
    String ROOT_PATH = "";


    // Parsing properties

    String PROP_COMPONENT_TYPE = "type";
    String PROP_COMPONENT_NAME = "name";
    String PROP_COMPONENT_DESCR = "descr";
    String PROP_COMPONENT_STATE = "state";
    String PROP_COMPONENT_LISTNER = "listener";
    String PROP_COMPONENT_PULLER = "puller";
    String PROP_COMPONENT_EXECUTOR = "executor";
    String PROP_COMPONENT_RANGE_MIN = "min";
    String PROP_COMPONENT_RANGE_MAX = "max";
    String PROP_COMPONENT_RANGE_STEP = "step";


    // Default component paths

    String PATH_STR_ROOT = ROOT_PATH;
    String PATH_STR_ALL = "*";

}
