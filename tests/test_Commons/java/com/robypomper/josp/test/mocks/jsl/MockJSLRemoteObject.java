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

package com.robypomper.josp.test.mocks.jsl;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.remote.ObjInfo;
import com.robypomper.josp.jsl.objs.remote.ObjPerms;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.objs.structure.JSLActionParams;
import com.robypomper.josp.protocol.JOSPPerm;

public class MockJSLRemoteObject implements JSLRemoteObject {

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ObjInfo getInfo() {
        return null;
    }

    @Override
    public ObjStruct getStruct() {
        return null;
    }

    @Override
    public ObjPerms getPerms() {
        return null;
    }

    @Override
    public ObjComm getComm() {
        return null;
    }

    @Override
    public void sendObjectCmdMsg(JSLAction component, JSLActionParams command) {
    }

    @Override
    public boolean processFromObjectMsg(String msg, JOSPPerm.Connection connType) {
        return false;
    }

}
