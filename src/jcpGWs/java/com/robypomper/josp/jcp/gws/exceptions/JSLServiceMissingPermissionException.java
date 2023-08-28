/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.gws.exceptions;

import com.robypomper.josp.protocol.JOSPPerm;

public class JSLServiceMissingPermissionException extends Throwable {

    // Class constants

    private static final String MSG = "Service's '%s' have NOT enough permission to send message to Object '%s' (min permission required '%s', current permission '%s')";

    // Internal vars

    private final String srvId;
    private final String objId;
    private final JOSPPerm.Type reqPerm;
    private final JOSPPerm.Type currentPerm;


    // Constructors

    public JSLServiceMissingPermissionException(String srvId, String objId, JOSPPerm.Type reqPerm, JOSPPerm.Type currentPerm) {
        super(String.format(MSG, srvId, objId, reqPerm, currentPerm));
        this.srvId = srvId;
        this.objId = objId;
        this.reqPerm = reqPerm;
        this.currentPerm = currentPerm;
    }


    // Getters

    public String getSrvId() {
        return srvId;
    }

    public String getObjId() {
        return objId;
    }

    public JOSPPerm.Type getMinPermReq() {
        return reqPerm;
    }

    public JOSPPerm.Type getCurrentPerm() {
        return currentPerm;
    }

}
