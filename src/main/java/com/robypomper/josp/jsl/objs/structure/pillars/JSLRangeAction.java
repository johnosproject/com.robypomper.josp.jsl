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

package com.robypomper.josp.jsl.objs.structure.pillars;

import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.objs.structure.JSLActionParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSLRangeAction extends JSLRangeState implements JSLAction {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLRangeAction.class);


    // Constructor

    /**
     * Default constructor that initialize the action component with his
     * properties.
     *
     * @param remoteObject the {@link JSLRemoteObject} representing JOD object.
     * @param name         the name of the component.
     * @param descr        the description of the component.
     */
    public JSLRangeAction(JSLRemoteObject remoteObject, String name, String descr, String type, double min, double max, double step, double state) {
        super(remoteObject, name, descr, type, min, max, step, state);
    }


    // Action's methods

    public void execSetValue(double val) throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        if (val < getMin() || val > getMax())
            return;

        JSLAction.execAction(new JSLRangeAction.JOSPRange(val, this), this, log);
    }

    public void execSetMin() throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        if (getState() == getMin())
            return;

        JSLAction.execAction(new JSLRangeAction.JOSPRange(getMin(), this), this, log);
    }

    public void execSetMax() throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        if (getState() == getMax())
            return;

        JSLAction.execAction(new JSLRangeAction.JOSPRange(getMax(), this), this, log);
    }

    public void execIncrease() throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        if (getState() >= getMax())
            return;

        double val = getState() + getStep();
        if (val > getMax())
            val = getMax();

        JSLAction.execAction(new JSLRangeAction.JOSPRange(val, this), this, log);
    }

    public void execDecrease() throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        if (getState() <= getMin())
            return;

        double val = getState() - getStep();
        if (val < getMin())
            val = getMin();

        JSLAction.execAction(new JSLRangeAction.JOSPRange(val, this), this, log);
    }


    // Boolean ActionParams implementation

    public static class JOSPRange implements JSLActionParams {

        private final double newState;
        private final double oldState;

        public JOSPRange(double newState, JSLRangeState component) {
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
            String newVal = String.format(KEY_VALUE_FORMAT, "new", JavaFormatter.doubleToStr(newState));
            String oldVal = String.format(KEY_VALUE_FORMAT, "old", JavaFormatter.doubleToStr(oldState));
            return newVal + ITEMS_SEP + oldVal;
        }

    }

}
