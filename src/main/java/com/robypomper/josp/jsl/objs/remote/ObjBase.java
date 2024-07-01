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

package com.robypomper.josp.jsl.objs.remote;

import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol_ServiceToObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all object's interfaces ({@link ObjInfo}, {@link ObjStruct},
 * {@link ObjComm}...).
 * <p>
 * This class provide a default constructor and protected method callable only
 * from object's interfaces.
 */
public class ObjBase {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(ObjBase.class);

    private final JSLRemoteObject remoteObject;
    private final JSLServiceInfo serviceInfo;


    // Constructor

    public ObjBase(JSLRemoteObject remoteObject, JSLServiceInfo serviceInfo) {
        this.remoteObject = remoteObject;
        this.serviceInfo = serviceInfo;
    }


    // Getters

    protected JSLRemoteObject getRemote() {
        return remoteObject;
    }

    protected JSLServiceInfo getServiceInfo() {
        return serviceInfo;
    }


    // Send message to object

    protected void sendToObject(String msg) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (!getRemote().getComm().isConnected())
            throw new JSLRemoteObject.ObjectNotConnected(getRemote());

        JOSPPerm.Type minReqPerm = JOSPPerm.Type.None;
        if (JOSPProtocol_ServiceToObject.isObjectSetNameMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectSetOwnerIdMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectAddPermMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectUpdPermMsg(msg)
                || JOSPProtocol_ServiceToObject.isObjectRemPermMsg(msg))
            minReqPerm = JOSPPerm.Type.CoOwner;

        if (JOSPProtocol_ServiceToObject.isObjectActionCmdMsg(msg))
            minReqPerm = JOSPPerm.Type.Actions;

        // Send via local communication
        if (getRemote().getComm().isLocalConnected()) {
            JOSPPerm.Type permType = getRemote().getPerms().getPermTypes().get(JOSPPerm.Connection.OnlyLocal);
            if (permType.compareTo(minReqPerm) < 0 && !getRemote().getInfo().getOwnerId().equals(JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString()))
                throw new JSLRemoteObject.MissingPermission(getRemote(), JOSPPerm.Connection.OnlyLocal, permType, minReqPerm, msg);

            try {
                getRemote().getComm().getActiveLocalClient().sendData(msg);
                return;

            } catch (PeerNotConnectedException | PeerStreamException e) {
                log.warn(String.format("Error on sending message '%s' to object (via local) because %s", msg.substring(0, msg.indexOf('\n')), e.getMessage()), e);
            }
        }

        // Send via cloud communication
        if (getRemote().getComm().isCloudConnected()) {
            JOSPPerm.Type permType = getRemote().getPerms().getPermTypes().get(JOSPPerm.Connection.LocalAndCloud);
            if (permType.compareTo(minReqPerm) < 0 && !getRemote().getInfo().getOwnerId().equals(JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString()))
                throw new JSLRemoteObject.MissingPermission(getRemote(), JOSPPerm.Connection.LocalAndCloud, permType, minReqPerm, msg);

            try {
                ((DefaultObjComm) getRemote().getComm()).getCloudConnection().sendData(msg);

            } catch (PeerNotConnectedException | PeerStreamException e) {
                log.warn(String.format("Error on sending message '%s' to object (via cloud) because %s", msg.substring(0, msg.indexOf('\n')), e.getMessage()), e);
            }
        }
    }

}
