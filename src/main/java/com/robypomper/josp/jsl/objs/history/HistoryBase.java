/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
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

package com.robypomper.josp.jsl.objs.history;

import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.DefaultObjComm;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HistoryBase {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(HistoryBase.class);
    private final JSLRemoteObject remoteObject;
    private final JSLServiceInfo srvInfo;


    // Constructor

    protected HistoryBase(JSLRemoteObject remoteObject, JSLServiceInfo srvInfo) {
        this.remoteObject = remoteObject;
        this.srvInfo = srvInfo;
    }


    // Getters

    protected JSLRemoteObject getRemote() {
        return remoteObject;
    }

    protected JSLServiceInfo getServiceInfo() {
        return srvInfo;
    }


    // Send message to object

    protected void sendToObjectLocally(JOSPPerm.Type minReqPerm, String msg) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (!getRemote().getComm().isLocalConnected())
            throw new JSLRemoteObject.ObjectNotConnected(getRemote());

        // Send via local communication
        if (getRemote().getComm().isLocalConnected()) {
            JOSPPerm.Type permType = getRemote().getPerms().getPermTypes().get(JOSPPerm.Connection.OnlyLocal);
            if (permType.compareTo(minReqPerm) < 0 && !getRemote().getInfo().getOwnerId().equals(JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString()))
                throw new JSLRemoteObject.MissingPermission(getRemote(), JOSPPerm.Connection.OnlyLocal, permType, minReqPerm, msg);

            try {
                getRemote().getComm().getActiveLocalClient().sendData(msg);

            } catch (PeerNotConnectedException | PeerStreamException e) {
                log.warn(String.format("Error on sending message '%s' to object (via local) because %s", msg.substring(0, msg.indexOf('\n')), e.getMessage()), e);
            }
        }
    }

    protected void sendToObjectCloudly(JOSPPerm.Type minReqPerm, String msg) throws JSLRemoteObject.MissingPermission, PeerNotConnectedException, PeerStreamException {
        // Send via cloud communication
        JOSPPerm.Type permType = getRemote().getPerms().getPermTypes().get(JOSPPerm.Connection.LocalAndCloud);
        if (permType.compareTo(minReqPerm) < 0 && !getRemote().getInfo().getOwnerId().equals(JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString()))
            throw new JSLRemoteObject.MissingPermission(getRemote(), JOSPPerm.Connection.LocalAndCloud, permType, minReqPerm, msg);

        ((DefaultObjComm) getRemote().getComm()).getCloudConnection().sendData(msg);
    }

}
