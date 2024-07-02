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
import com.robypomper.josp.jsl.objs.structure.AbsJSLState;
import com.robypomper.josp.jsl.objs.structure.JSLStateUpdate;
import com.robypomper.josp.protocol.JOSPProtocol;

import java.util.ArrayList;
import java.util.List;


public class JSLRangeState extends AbsJSLState {

    // Internal vars

    private double state;
    private final double min;
    private final double max;
    private final double step;
    private final List<RangeStateListener> listeners = new ArrayList<>();


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
    public JSLRangeState(JSLRemoteObject remoteObject, String name, String descr, String type, double min, double max, double step, double state) {
        super(remoteObject, name, descr, type);
        this.min = min;
        this.max = max;
        this.step = step;
        this.state = state;
    }


    // Status's properties

    public double getState() {
        return state;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }

    @Override
    public boolean updateStatus(JOSPProtocol.StatusUpd statusUpd) {
        if (statusUpd.getUpdate() instanceof JOSPRange) {
            JOSPRange stateUpdate = (JOSPRange) statusUpd.getUpdate();
            double oldState = state;
            state = stateUpdate.newState;

            if (oldState != state) {
                for (RangeStateListener l : listeners)
                    l.onStateChanged(this, state, oldState);
                if (state <= min)
                    for (RangeStateListener l : listeners)
                        l.onMinReached(this, state, min);
                if (state >= max)
                    for (RangeStateListener l : listeners)
                        l.onMaxReached(this, state, min);
            }

            return true;
        }
        return false;
    }


    // Listeners

    public void addListener(RangeStateListener listener) {
        if (listeners.contains(listener))
            return;

        listeners.add(listener);
    }

    public void removeListener(RangeStateListener listener) {
        if (!listeners.contains(listener))
            return;

        listeners.remove(listener);
    }


    // Boolean StateUpdate implementation

    public static class JOSPRange implements JSLStateUpdate {

        public final double newState;
        public final double oldState;

        public JOSPRange(String updData) {
            String[] lines = updData.split(ITEMS_SEP);

            Double newVal = JavaFormatter.strToDouble(lines[0].substring(lines[0].indexOf(KEY_VALUE_SEP) + 1));
            newState = newVal != null ? newVal : 0;
            Double oldVal = JavaFormatter.strToDouble(lines[1].substring(lines[1].indexOf(KEY_VALUE_SEP) + 1));
            oldState = oldVal != null ? oldVal : 0;
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            throw new RuntimeException("JSL JOSPRange::encode() method must be NOT called");
        }

    }


    // Range listener implementation

    public interface RangeStateListener {

        void onStateChanged(JSLRangeState component, double newState, double oldState);

        void onMinReached(JSLRangeState component, double state, double min);

        void onMaxReached(JSLRangeState component, double state, double max);

    }

}
