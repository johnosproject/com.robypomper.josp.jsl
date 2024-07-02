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

package com.robypomper.josp.jsl.objs.remote;

import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLGwS2OClient;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.comm.JSLLocalClientsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DefaultObjComm extends ObjBase implements ObjComm {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(DefaultObjComm.class);
    private final JSLCommunication communication;
    private boolean isCloudConnected = true;
    private final List<RemoteObjectConnListener> listenersConn = new ArrayList<>();


    // Constructor

    public DefaultObjComm(JSLRemoteObject remoteObject, JSLServiceInfo serviceInfo, JSLCommunication communication) {
        super(remoteObject, serviceInfo);
        this.communication = communication;
        communication.getLocalConnections().addListener(localClientsListener);
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLCommunication getCommunication() {
        return communication;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return isCloudConnected() || isLocalConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCloudConnected() {
        return communication.getCloudConnection().getState().isConnected() && isCloudConnected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLocalConnected() {
        JSLLocalClient client = getActiveLocalClient();
        return client != null && client.getState().isConnected();
    }


    /**
     * {@inheritDoc}
     */
    public void setCloudConnected(boolean connected) {
        if (isCloudConnected==connected) return;

        isCloudConnected = connected;

        if (isCloudConnected)
            emitConn_CloudConnected();
        else
            emitConn_CloudDisconnected();
    }

    /**
     * @return the active local connection to the object.
     * if the object is disconnected or works only via cloud, then the
     * returned value will be null.
     */
    @Override
    public JSLLocalClient getActiveLocalClient() {
        return communication.getLocalConnections().getActiveLocalClientByObject(getRemote());
    }

    /**
     * @return a list containing all available local connections to the object.
     * if the object is disconnected or works only via cloud, then the
     * returned array will be empty.
     */
    @Override
    public List<JSLLocalClient> getLocalBackupClients() {
        return communication.getLocalConnections().getLocalBackupClientsByObject(getRemote());
    }

    public JSLGwS2OClient getCloudConnection() {
        return getCommunication().getCloudConnection();
    }


    // Processing

    public boolean processObjectDisconnectMsg(String msg, JOSPPerm.Connection connType) throws Throwable {
        if (connType == JOSPPerm.Connection.LocalAndCloud && isCloudConnected) {
            isCloudConnected = false;
            emitConn_CloudDisconnected();
        }
        return true;
    }


    // JSLLocalClientsMngr listener

    private final JSLLocalClientsMngr.LocalClientListener localClientsListener = new JSLLocalClientsMngr.LocalClientListener() {
        @Override
        public void onLocalConnected(JSLRemoteObject jslObj, JSLLocalClient jslLocCli) {
            if (jslObj == getRemote())
                emitConn_LocalConnected(jslLocCli);
        }

        @Override
        public void onLocalConnectionError(JSLLocalClient jslLocCli, Throwable throwable) {}

        @Override
        public void onLocalDisconnected(JSLRemoteObject jslObj, JSLLocalClient jslLocCli) {
            if (jslObj == getRemote())
                emitConn_LocalDisconnected(jslLocCli);
        }
    };


    // Listeners

    @Override
    public void addListener(RemoteObjectConnListener listener) {
        if (listenersConn.contains(listener))
            return;

        listenersConn.add(listener);
    }

    @Override
    public void removeListener(RemoteObjectConnListener listener) {
        if (!listenersConn.contains(listener))
            return;

        listenersConn.remove(listener);
    }

    private void emitConn_LocalConnected(JSLLocalClient localClient) {
        for (RemoteObjectConnListener l : listenersConn)
            l.onLocalConnected(getRemote(), localClient);
    }

    private void emitConn_LocalDisconnected(JSLLocalClient localClient) {
        for (RemoteObjectConnListener l : listenersConn)
            l.onLocalDisconnected(getRemote(), localClient);

    }

    private void emitConn_CloudConnected() {
        for (RemoteObjectConnListener l : listenersConn)
            l.onCloudConnected(getRemote());
    }

    private void emitConn_CloudDisconnected() {
        for (RemoteObjectConnListener l : listenersConn)
            l.onCloudDisconnected(getRemote());
    }

}
