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

public class CmdsJODObjectInfo {

    private final JODObjectInfo objInfo;

    public CmdsJODObjectInfo(JODObjectInfo objInfo) {
        this.objInfo = objInfo;
    }


    @Command(description = "Print Object Info > Obj.")
    public String infoObj() {
        String s = "";
        s += "OBJECT'S INFO \n";
        s += String.format("  ObjId . . . . . %s\n", objInfo.getObjId());
        s += String.format("  ObjName . . . . %s\n", objInfo.getObjName());
        s += String.format("  Model . . . . . %s\n", objInfo.getModel());
        s += String.format("  Brand . . . . . %s\n", objInfo.getBrand());
        s += String.format("  Descr . . . . . %s\n", objInfo.getLongDescr());

        return s;
    }

    @Command(description = "Print Object Info > User")
    public String infoUser() {
        String s = "";
        s += "USER'S INFO \n";
        s += String.format("  OwnerId . . . . %s\n", objInfo.getOwnerId());

        return s;
    }

    @Command(description = "Print all Object Info")
    public String info() {
        String s = "";
        s += infoObj();
        s += infoUser();

        return s;
    }


    @Command(description = "Set object's name")
    public String infoSetObjectName(String newName) {
        String oldName = objInfo.getObjName();
        objInfo.setObjName(newName);
        return String.format("Object's name changed from '%s' to '%s' successfully", oldName, newName);
    }
}
