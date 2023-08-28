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

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.JOSPStateUpdateParams;

import java.util.HashMap;
import java.util.Map;


/**
 * Default implementation of {@link JSLState} interface.
 */
public abstract class AbsJSLState extends AbsJSLComponent
        implements JSLState {

    // Constructor

    /**
     * Default constructor that initialize the status component with his
     * properties.
     *
     * @param remoteObject the {@link JSLRemoteObject} representing JOD object.
     * @param name         the name of the component.
     * @param descr        the description of the component.
     * @param type         the type of the component.
     */
    public AbsJSLState(JSLRemoteObject remoteObject, String name, String descr, String type) {
        super(remoteObject, name, descr, type);
    }


    // Status classes

    private static final Map<String, Class<? extends JOSPStateUpdateParams>> stateClasses = new HashMap<>();

    public static void loadAllStateClasses() {
        registerStateClass(JSLBooleanState.JOSPBoolean.class.getSimpleName(), JSLBooleanState.JOSPBoolean.class);
        registerStateClass(JSLRangeState.JOSPRange.class.getSimpleName(), JSLRangeState.JOSPRange.class);
    }

    public static void registerStateClass(String typeName, Class<? extends JOSPStateUpdateParams> cl) {
        stateClasses.put(typeName, cl);
    }

    public static Map<String, Class<? extends JOSPStateUpdateParams>> getStateClasses() {
        return stateClasses;
    }

}
