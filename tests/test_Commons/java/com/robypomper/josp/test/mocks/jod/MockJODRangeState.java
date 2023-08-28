/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
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

package com.robypomper.josp.test.mocks.jod;

import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.history.JODHistory;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.jod.structure.pillars.JODBooleanState;
import com.robypomper.josp.jod.structure.pillars.JODRangeState;

public class MockJODRangeState extends JODRangeState {

    public static final JODStructure DEF_STATE_STRUCTURE = new MockJODStructure();
    public static final JODExecutorMngr DEF_STATE_EXECUTORS_MANAGER = new MockJODExecutorManager();
    public static final JODHistory DEF_STATE_HISTORY = new MockJODHistory();
    public static final String DEF_STATE_NAME = "TEST RangeState 001";
    public static final String DEF_STATE_DESCR = "Mockup for JODRangeState class.";
    public static final String DEF_STATE_LISTENER = "testListener://range state listener";
    public static final String DEF_STATE_PULLER = "testPuller://range state puller";
    public static final double DEF_STATE_MIN = 0;
    public static final double DEF_STATE_MAX = 100;
    public static final double DEF_STATE_STEP = 5;

    public MockJODRangeState() throws JODStructure.ComponentInitException {
        this(DEF_STATE_NAME);
    }

    public static MockJODRangeState create() {
        try {
            return new MockJODRangeState();
        } catch (JODStructure.ComponentInitException e) {
            assert false;
            return null;
        }
    }

    public MockJODRangeState(String stateName) throws JODStructure.ComponentInitException {
        this(DEF_STATE_STRUCTURE, DEF_STATE_EXECUTORS_MANAGER, DEF_STATE_HISTORY, stateName, DEF_STATE_DESCR, DEF_STATE_LISTENER, DEF_STATE_PULLER, DEF_STATE_MIN, DEF_STATE_MAX, DEF_STATE_STEP);
    }

    public static MockJODRangeState create(String name) {
        try {
            return new MockJODRangeState(name);
        } catch (JODStructure.ComponentInitException e) {
            assert false;
            return null;
        }
    }

    private MockJODRangeState(JODStructure structure, JODExecutorMngr execMngr, JODHistory history, String stateName, String stateDescr, String stateListener, String statePuller, Double stateMin, Double stateMax, Double stateStep) throws JODStructure.ComponentInitException {
        super(structure, execMngr, history, stateName, stateDescr, stateListener, statePuller, stateMin, stateMax, stateStep);
    }

    public static MockJODRangeState create(JODStructure structure, JODExecutorMngr execMngr, JODHistory history, String stateName, String stateDescr, String stateListener, String statePuller, Double stateMin, Double stateMax, Double stateStep) {
        try {
            return new MockJODRangeState(structure, execMngr, history, stateName, stateDescr, stateListener, statePuller, stateMin, stateMax, stateStep);
        } catch (JODStructure.ComponentInitException e) {
            assert false;
            return null;
        }
    }

}
