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

package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;

public class CmdsJSLServiceInfo {

    private final JSLServiceInfo srvInfo;

    public CmdsJSLServiceInfo(JSLServiceInfo srvInfo) {
        this.srvInfo = srvInfo;
    }


    @Command(description = "Print Service Info > Srv.")
    public String infoSrv() {
        String s = "";
        s += "SERVICE'S INFO \n";
        s += String.format("  SrvId . . . . . %s\n", srvInfo.getSrvId());
        s += String.format("  SrvName . . . . %s\n", srvInfo.getSrvName());
        s += "\n";
        s += String.format("  is user auth  . %s\n", srvInfo.isUserLogged());
        if (srvInfo.isUserLogged()) {
            s += String.format("  UsrId . . . . . %s\n", srvInfo.getUserId());
            s += String.format("  UsrName . . . . %s\n", srvInfo.getUsername());
        }

        return s;
    }

}
