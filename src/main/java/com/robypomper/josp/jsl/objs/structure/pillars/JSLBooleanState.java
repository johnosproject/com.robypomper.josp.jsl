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

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.AbsJSLState;
import com.robypomper.josp.jsl.objs.structure.JSLStateUpdate;
import com.robypomper.josp.protocol.JOSPProtocol;

import java.util.ArrayList;
import java.util.List;


public class JSLBooleanState extends AbsJSLState {

    // Internal vars

    private boolean state;
    private List<BooleanStateListener> listeners = new ArrayList<>();


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
    public JSLBooleanState(JSLRemoteObject remoteObject, String name, String descr, String type, boolean state) {
        super(remoteObject, name, descr, type);
        this.state = state;
    }


    // Status's properties

    public boolean getState() {
        return state;
    }

    @Override
    public boolean updateStatus(JOSPProtocol.StatusUpd statusUpd) {
        if (statusUpd.getUpdate() instanceof JOSPBoolean) {
            JOSPBoolean stateUpdate = (JOSPBoolean) statusUpd.getUpdate();
            boolean oldState = state;
            state = stateUpdate.newState;

            if (oldState != state)
                for (BooleanStateListener l : listeners)
                    l.onStateChanged(this, state, oldState);

            return true;
        }
        return false;
    }


    // Listeners

    public void addListener(BooleanStateListener listener) {
        if (listeners.contains(listener))
            return;

        listeners.add(listener);
    }

    public void removeListener(BooleanStateListener listener) {
        if (!listeners.contains(listener))
            return;

        listeners.remove(listener);
    }


    // Boolean StateUpdate implementation

    public static class JOSPBoolean implements JSLStateUpdate {

        public final boolean newState;
        public final boolean oldState;

        public JOSPBoolean(String updData) {
            String[] lines = updData.split(ITEMS_SEP);

            newState = Boolean.parseBoolean(lines[0].substring(lines[0].indexOf(KEY_VALUE_SEP) + 1));
            oldState = Boolean.parseBoolean(lines[1].substring(lines[1].indexOf(KEY_VALUE_SEP) + 1));
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            throw new RuntimeException("JSL JOSPBoolean::encode() method must be NOT called");
        }

    }


    // Boolean listener implementation

    public interface BooleanStateListener {

        void onStateChanged(JSLBooleanState component, boolean newState, boolean oldState);

    }

}
