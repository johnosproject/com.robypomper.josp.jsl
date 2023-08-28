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

import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.List;

public class MockJODPermissions implements JODPermissions {

    @Override
    public void setCommunication(JODCommunication comm) {
    }

    @Override
    public void syncObjPermissions() {
    }

    @Override
    public List<JOSPPerm> getPermissions() {
        return null;
    }

    @Override
    public boolean checkPermission(String srvId, String usrId, JOSPPerm.Type minReqPerm, JOSPPerm.Connection connType) {
        return false;
    }

    @Override
    public JOSPPerm.Type getServicePermission(String srvId, String usrId, JOSPPerm.Connection connType) {
        return null;
    }

    @Override
    public String getPermsForJSL() {
        return null;
    }

    @Override
    public boolean addPermissions(String srvId, String usrId, JOSPPerm.Type type, JOSPPerm.Connection connection) {
        return false;
    }

    @Override
    public boolean updPermissions(String permId, String srvId, String usrId, JOSPPerm.Type type, JOSPPerm.Connection connection) {
        return false;
    }

    @Override
    public boolean remPermissions(String permId) {
        return false;
    }

    @Override
    public void startAutoRefresh() {
    }

    @Override
    public void stopAutoRefresh() {
    }

    @Override
    public void updateObjIdAndSave() {

    }

}
