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

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.josp.protocol.JOSPProtocol_ServiceToObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultObjPerms extends ObjBase implements ObjPerms {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(DefaultObjPerms.class);
    private List<JOSPPerm> perms = new ArrayList<>();
    private final Map<JOSPPerm.Connection, JOSPPerm.Type> permTypes = new HashMap<>();
    private final List<RemoteObjectPermsListener> listenersInfo = new ArrayList<>();


    // Constructor

    public DefaultObjPerms(JSLRemoteObject remoteObject, JSLServiceInfo serviceInfo) {
        super(remoteObject, serviceInfo);
        permTypes.put(JOSPPerm.Connection.OnlyLocal, JOSPPerm.Type.None);
        permTypes.put(JOSPPerm.Connection.LocalAndCloud, JOSPPerm.Type.None);
    }


    // Getters / Setters

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JOSPPerm> getPerms() {
        return perms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<JOSPPerm.Connection, JOSPPerm.Type> getPermTypes() {
        return permTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JOSPPerm.Type getServicePerm(JOSPPerm.Connection connType) {
        return permTypes.get(connType);
    }

    @Override
    public void addPerm(String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectAddPermMsg(getServiceInfo().getFullId(), getRemote().getId(), srvId, usrId, permType, connType));
    }

    @Override
    public void updPerm(String permId, String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectUpdPermMsg(getServiceInfo().getFullId(), getRemote().getId(), permId, srvId, usrId, permType, connType));
    }

    @Override
    public void remPerm(String permId) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectRemPermMsg(getServiceInfo().getFullId(), getRemote().getId(), permId));
    }


    // Processing

    public boolean processObjectPermsMsg(String msg) {
        List<JOSPPerm> oldPerms = perms;
        try {
            perms = JOSPProtocol_ObjectToService.getObjectPermsMsg_Perms(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(String.format("%s Error on processing ObjectPerms message '%s...' for '%s' object because %s",
                    getLogRO(), msg.substring(0, Math.min(10, msg.length())), getRemote().getId(), e.getMessage()), e);
            return false;
        }

        emitInfo_PermissionsChanged(perms, oldPerms);
        return true;
    }

    public boolean processServicePermMsg(String msg) {
        JOSPPerm.Connection connType;
        JOSPPerm.Type permType;
        try {
            connType = JOSPProtocol_ObjectToService.getServicePermsMsg_ConnType(msg);
            permType = JOSPProtocol_ObjectToService.getServicePermsMsg_PermType(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(String.format("%s Error on processing ServicePerms message '%s...' for '%s' object because %s",
                    getLogRO(), msg.substring(0, Math.min(10, msg.length())), getRemote().getId(), e.getMessage()), e);
            return false;
        }

        JOSPPerm.Type oldPermType = permTypes.get(connType);
        permTypes.put(connType, permType);
        emitInfo_ServicePermChanged(connType, permTypes.get(connType), oldPermType);
        return true;

    }


    // Listeners

    @Override
    public void addListener(RemoteObjectPermsListener listener) {
        if (listenersInfo.contains(listener))
            return;

        listenersInfo.add(listener);
    }

    @Override
    public void removeListener(RemoteObjectPermsListener listener) {
        if (!listenersInfo.contains(listener))
            return;

        listenersInfo.remove(listener);
    }

    private void emitInfo_PermissionsChanged(List<JOSPPerm> perms, List<JOSPPerm> oldPerms) {
        List<RemoteObjectPermsListener> tmpList = new ArrayList<>(listenersInfo);
        for (RemoteObjectPermsListener l : tmpList)
            l.onPermissionsChanged(getRemote(), perms, oldPerms);
    }

    private void emitInfo_ServicePermChanged(JOSPPerm.Connection connType, JOSPPerm.Type type, JOSPPerm.Type oldPermType) {
        List<RemoteObjectPermsListener> tmpList = new ArrayList<>(listenersInfo);
        for (RemoteObjectPermsListener l : tmpList)
            l.onServicePermChanged(getRemote(), connType, type, oldPermType);
    }

}
