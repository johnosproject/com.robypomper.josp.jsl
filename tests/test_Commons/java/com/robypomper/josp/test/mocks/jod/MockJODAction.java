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
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.util.Collections;
import java.util.List;

public class MockJODAction extends MockJODState implements JODAction {

    public static final String DEF_ACTION_NAME = "TEST State 001";
    public static final String DEF_ACTION_DESCR = "Mockup for JODState class.";
    public static final String DEF_ACTION_TYPE = "protocol";
    public static final JODComponentPath DEF_ACTION_PATH = null; // new MockJODComponentPath();
    public static final JODContainer DEF_ACTION_PARENT = null; // new MockJODContainer(String.format("Parent of '%s'", MockJODState.DEF_ACTION_NAME));
    public static final List<JOSPStatusHistory> DEF_ACTION_HISTORY_STATUS = Collections.emptyList();
    public static final String DEF_ACTION_WORKER = "action's worker";
    public static final String DEF_ACTION_STATE = "action's state";
    public static final String DEF_ACTION_EXECUTOR = "action's executor";

    private final String actionExecutor;

    public MockJODAction() {
        this(DEF_ACTION_NAME);
    }

    public MockJODAction(String actionName) {
        this(actionName, DEF_ACTION_DESCR, DEF_ACTION_TYPE, DEF_ACTION_PATH, DEF_ACTION_PARENT, DEF_ACTION_HISTORY_STATUS, DEF_ACTION_WORKER, DEF_ACTION_STATE, DEF_ACTION_EXECUTOR);
    }

    public MockJODAction(String compName, String compDescr, String compType, JODComponentPath compPath, JODContainer compParent, List<JOSPStatusHistory> compHistoryList, String stateWorker, String stateState, String actionExecutor) {
        super(compName, compDescr, compType, compPath, compParent, compHistoryList,stateWorker, stateState);
        this.actionExecutor = actionExecutor;
    }

    @Override
    public String getExecutor() {
        return actionExecutor;
    }

    @Override
    public boolean execAction(JOSPProtocol.ActionCmd commandAction) {
        return false;
    }
}
