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

import com.robypomper.josp.jod.structure.*;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.util.Collections;
import java.util.List;

public class MockJODState extends MockJODComponent implements JODState {

    public static final String DEF_STATE_NAME = "TEST State 001";
    public static final String DEF_STATE_DESCR = "Mockup for JODState class.";
    public static final String DEF_STATE_TYPE = "protocol";
    public static final JODComponentPath DEF_STATE_PATH = null; // new MockJODComponentPath();
    public static final JODContainer DEF_STATE_PARENT = null; // new MockJODContainer(String.format("Parent of '%s'", MockJODState.DEF_STATE_NAME));
    public static final List<JOSPStatusHistory> DEF_STATE_HISTORY_STATUS = Collections.emptyList();
    public static final String DEF_STATE_WORKER = "state worker";
    public static final String DEF_STATE_STATE = "state state";

    private final String stateWorker;
    private final String stateState;

    public MockJODState() {
        this(DEF_STATE_NAME);
    }

    public MockJODState(String stateName) {
        this(stateName, DEF_STATE_DESCR, DEF_STATE_TYPE, DEF_STATE_PATH, DEF_STATE_PARENT, DEF_STATE_HISTORY_STATUS, DEF_STATE_WORKER, DEF_STATE_STATE);
    }

    public MockJODState(String compName, String compDescr, String compType, JODComponentPath compPath, JODContainer compParent, List<JOSPStatusHistory> compHistoryList, String stateWorker, String stateState) {
        super(compName, compDescr, compType, compPath, compParent, compHistoryList);
        this.stateWorker = stateWorker;
        this.stateState = stateState;
    }

    @Override
    public String getWorker() {
        return stateWorker;
    }

    @Override
    public String getState() {
        return stateState;
    }

    @Override
    public void forceCheckState() {

    }

    @Override
    public void propagateState(JODStateUpdate statusUpd) {

    }
}
