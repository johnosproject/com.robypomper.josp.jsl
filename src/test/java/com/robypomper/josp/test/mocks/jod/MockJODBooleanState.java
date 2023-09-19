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
import com.robypomper.josp.jod.structure.*;
import com.robypomper.josp.jod.structure.pillars.JODBooleanState;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.util.Collections;
import java.util.List;

public class MockJODBooleanState extends JODBooleanState {

    public static final JODStructure DEF_STATE_STRUCTURE = new MockJODStructure();
    public static final JODExecutorMngr DEF_STATE_EXECUTORS_MANAGER = new MockJODExecutorManager();
    public static final JODHistory DEF_STATE_HISTORY = new MockJODHistory();
    public static final String DEF_STATE_NAME = "TEST BooleanState 001";
    public static final String DEF_STATE_DESCR = "Mockup for JODBooleanState class.";
    public static final String DEF_STATE_LISTENER = "testListener://boolean state listener";
    public static final String DEF_STATE_PULLER = "testPuller://boolean state puller";

    public MockJODBooleanState() throws JODStructure.ComponentInitException {
        this(DEF_STATE_NAME);
    }

    public static MockJODBooleanState create() {
        try {
            return new MockJODBooleanState();
        } catch (JODStructure.ComponentInitException e) {
            assert false;
            return null;
        }
    }

    public MockJODBooleanState(String stateName) throws JODStructure.ComponentInitException {
        this(DEF_STATE_STRUCTURE, DEF_STATE_EXECUTORS_MANAGER, DEF_STATE_HISTORY, stateName, DEF_STATE_DESCR, DEF_STATE_LISTENER, DEF_STATE_PULLER);
    }

    public static MockJODBooleanState create(String stateName) {
        try {
            return new MockJODBooleanState(stateName);
        } catch (JODStructure.ComponentInitException e) {
            assert false;
            return null;
        }
    }

    private MockJODBooleanState(JODStructure structure, JODExecutorMngr execMngr, JODHistory history, String stateName, String stateDescr, String stateListener, String statePuller) throws JODStructure.ComponentInitException {
        super(structure, execMngr, history, stateName, stateDescr, stateListener, statePuller);
    }

    public static MockJODBooleanState create(JODStructure structure, JODExecutorMngr execMngr, JODHistory history, String stateName, String stateDescr, String stateListener, String statePuller) {
        try {
            return new MockJODBooleanState(structure, execMngr, history, stateName, stateDescr, stateListener, statePuller);
        } catch (JODStructure.ComponentInitException e) {
            assert false;
            return null;
        }
    }

}
