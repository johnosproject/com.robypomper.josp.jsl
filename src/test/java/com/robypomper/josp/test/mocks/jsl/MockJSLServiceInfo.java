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

import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.user.JSLUserMngr;

public class MockJSLServiceInfo implements JSLServiceInfo {

    private final String fullId;

    public MockJSLServiceInfo(String fullId) {
        this.fullId = fullId;
    }

    @Override
    public void setSystems(JSLUserMngr user, JSLObjsMngr objs) {
    }

    @Override
    public void setCommunication(JSLCommunication comm) {
    }

    @Override
    public String getSrvId() {
        return null;
    }

    @Override
    public String getSrvName() {
        return null;
    }

    @Override
    public boolean isUserLogged() {
        return false;
    }

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getInstanceId() {
        return null;
    }

    @Override
    public String getFullId() {
        return fullId;
    }

    @Override
    public int getConnectedObjectsCount() {
        return 0;
    }

    @Override
    public int getKnownObjectsCount() {
        return 0;
    }

    @Override
    public void startAutoRefresh() {
    }

    @Override
    public void stopAutoRefresh() {
    }
}
