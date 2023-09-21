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

package com.robypomper.josp.jsl.objs.structure.pillars;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.objs.structure.JSLActionParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSLBooleanAction extends JSLBooleanState implements JSLAction {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLBooleanAction.class);


    // Constructor

    /**
     * Default constructor that initialize the action component with his
     * properties.
     *
     * @param remoteObject the {@link JSLRemoteObject} representing JOD object.
     * @param name         the name of the component.
     * @param descr        the description of the component.
     */
    public JSLBooleanAction(JSLRemoteObject remoteObject, String name, String descr, String type, boolean state) {
        super(remoteObject, name, descr, type, state);
    }


    // Action's methods

    public void execSetTrue() throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        if (getState())
            return;

        JSLAction.execAction(new JSLBooleanAction.JOSPBoolean(true, this), this, log);
    }

    public void execSetFalse() throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        if (!getState())
            return;

        JSLAction.execAction(new JSLBooleanAction.JOSPBoolean(false, this), this, log);
    }

    public void execSwitch() throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        JSLAction.execAction(new JSLBooleanAction.JOSPBoolean(!getState(), this), this, log);
    }


    // Boolean ActionParams implementation

    public static class JOSPBoolean implements JSLActionParams {

        private final boolean newState;
        private final boolean oldState;

        public JOSPBoolean(boolean newState, JSLBooleanState component) {
            this.newState = newState;
            this.oldState = component.getState();
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            // No '\n', no ';'
            String newVal = String.format(KEY_VALUE_FORMAT, "new", Boolean.toString(newState));
            String oldVal = String.format(KEY_VALUE_FORMAT, "old", Boolean.toString(oldState));
            return newVal + ITEMS_SEP + oldVal;
        }

    }

}
