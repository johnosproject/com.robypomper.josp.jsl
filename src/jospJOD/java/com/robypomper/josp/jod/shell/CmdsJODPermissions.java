/*******************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
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

package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.protocol.JOSPPerm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

// pl
// pa user1 service2 LocalAndCloud Actions
// pd user1 service1
public class CmdsJODPermissions {

    private final JODObjectInfo objInfo;
    private final JODPermissions permission;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public CmdsJODPermissions(JODObjectInfo objInfo, JODPermissions permission) {
        this.objInfo = objInfo;
        this.permission = permission;
    }


    @Command(description = "Print all object's permissions.")
    public String permissionsList() {
        String s = "OBJECT'S PERMISSIONS LIST\n";
        s += JOSPPerm.logPermissions(permission.getPermissions());
        return s;
    }

    @Command(description = "Add given params as object's permissions.")
    public String permissionAdd(String srvId, String usrId, String typeStr, String connectionStr) {
        JOSPPerm.Connection connection;
        JOSPPerm.Type type;
        try {
            connection = connectionFromStr(connectionStr);
            type = typeFromStr(typeStr);
            srvId = srvIdFromStr(srvId);
            usrId = usrIdFromStr(usrId);

        } catch (Throwable e) {
            return "Error invalid params: " + e.getMessage();
        }

        if (permission.addPermissions(srvId, usrId, type, connection))
            return "Permission added successfully.";
        return "Error adding permission.";
    }

    @Command(description = "Update given object's permission.")
    public String permissionUpdate(String permId, String srvId, String usrId, String typeStr, String connectionStr) {
        JOSPPerm.Connection connection;
        JOSPPerm.Type type;
        try {
            connection = connectionFromStr(connectionStr);
            type = typeFromStr(typeStr);
            srvId = srvIdFromStr(srvId);
            usrId = usrIdFromStr(usrId);

        } catch (Throwable e) {
            return "Error invalid params: " + e.getMessage();
        }

        if (permission.updPermissions(permId, srvId, usrId, type, connection))
            return "Permission added successfully.";
        return "Error adding permission.";
    }

    @Command(description = "Delete object's permissions with given id.")
    public String permissionRemove(String permId) {
        if (permission.remPermissions(permId))
            return "Permission deleted successfully.";
        return "Error deleting permission.";
    }

    @Command(description = "Set object's owner.")
    public String permissionOwnerSet(String usrId) {
        objInfo.setOwnerId(usrId);
        return "Owner set successfully.";
    }

    @Command(description = "Reset object's owner to unset.")
    public String permissionOwnerReset() {
        objInfo.resetOwnerId();
        return "Owner reset successfully.";
    }


    // Parsing args utils

    private JOSPPerm.Connection connectionFromStr(String connectionStr) throws Throwable {
        if (connectionStr.compareToIgnoreCase("LocalAndCloud") == 0
                || connectionStr.compareToIgnoreCase("Cloud") == 0)
            return JOSPPerm.Connection.LocalAndCloud;

        if (connectionStr.compareToIgnoreCase("OnlyLocal") == 0
                || connectionStr.compareToIgnoreCase("Local") == 0)
            return JOSPPerm.Connection.OnlyLocal;

        throw new Throwable("Can't parse '%s' string as permission's connection type");
    }

    private JOSPPerm.Type typeFromStr(String typeStr) throws Throwable {
        if (typeStr.compareToIgnoreCase("CoOwner") == 0
                || typeStr.compareToIgnoreCase("Owner") == 0)
            return JOSPPerm.Type.CoOwner;

        if (typeStr.compareToIgnoreCase("Action") == 0
                || typeStr.compareToIgnoreCase("Actions") == 0)
            return JOSPPerm.Type.Actions;

        if (typeStr.compareToIgnoreCase("State") == 0
                || typeStr.compareToIgnoreCase("States") == 0
                || typeStr.compareToIgnoreCase("Status") == 0
                || typeStr.compareToIgnoreCase("Statuses") == 0)
            return JOSPPerm.Type.Status;

        if (typeStr.compareToIgnoreCase("None") == 0
                || typeStr.compareToIgnoreCase("No") == 0)
            return JOSPPerm.Type.None;

        throw new Throwable("Can't parse '%s' string as permission's type");
    }

    private String srvIdFromStr(String srvId) throws Throwable {
        if (srvId.compareToIgnoreCase("#All") == 0
                || srvId.compareToIgnoreCase("All") == 0)
            return "#All";

        if (srvId.length() == 17)
            return srvId;

        throw new Throwable("String '%s' is not a valid service's id.");
    }

    private String usrIdFromStr(String usrId) throws Throwable {
        if (usrId.compareToIgnoreCase("#All") == 0
                || usrId.compareToIgnoreCase("All") == 0)
            return "#All";

        if (usrId.compareToIgnoreCase("#Owner") == 0
                || usrId.compareToIgnoreCase("Owner") == 0)
            return "#Owner";

        if (usrId.length() == 17)
            return usrId;

        throw new Throwable("String '%s' is not a valid user's id.");
    }

}
