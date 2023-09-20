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

import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.java.JavaThreads;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLGwS2OClient;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DefaultObjComm extends ObjBase implements ObjComm {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLCommunication communication;
    private boolean isCloudConnected = true;
    private final List<JSLLocalClient> localConnections = new ArrayList<>();
    private final List<RemoteObjectConnListener> listenersConn = new ArrayList<>();


    // Constructor

    public DefaultObjComm(JSLRemoteObject remoteObject, JSLServiceInfo serviceInfo, JSLCommunication communication) {
        super(remoteObject, serviceInfo);
        this.communication = communication;
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
        return getConnectedLocalClient() != null;
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
     * Add given client as communication channel between this JSL object representation
     * and corresponding JOD object.
     * <p>
     * This method checks if it's the first client set for current object
     * representation then send object's presentation requests (objectInfo and
     * objectStructure).
     * <p>
     * If there is already a connected client, then this method disconnect given
     * one. Ther must be at least one connected client, others are used as
     * backups.
     *
     * @param newClient the client connected with corresponding JOD object.
     */
    public void addLocalClient(JSLLocalClient newClient) {
        log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("Add new client '%s' to object '%s'", newClient.getRemoteId(), getRemote().getName()));

        boolean wasConnected = isLocalConnected();
        JSLLocalClient oldClient = null;
        for (JSLLocalClient cl : localConnections) {
            if (
                // Check remote address and port
                    cl.getConnectionInfo().getRemoteInfo().getAddr() == newClient.getConnectionInfo().getRemoteInfo().getAddr()
                            && cl.getConnectionInfo().getRemoteInfo().getPort().intValue() == newClient.getConnectionInfo().getRemoteInfo().getPort().intValue()
                            // Check local address
                            && cl.getConnectionInfo().getLocalInfo().getAddr() == newClient.getConnectionInfo().getLocalInfo().getAddr()
            ) {
                oldClient = cl;
                break;
            }
        }

        try {
            newClient.setRemoteObject(getRemote());
        } catch (IllegalArgumentException ignore) {
        }

        // If object already connected
        //   Disconnect new client
        //   If client NOT already know
        //     Add new client to object's clients
        if (wasConnected) {
            JavaThreads.softSleep(100);         // Force switch thread, to allow starting client's thread
            try {
                newClient.disconnect();
            } catch (PeerDisconnectionException ignore) {
            }

            if (oldClient == null) {
                localConnections.add(newClient);
                log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("New client '%s' added to object '%s' as backup client", newClient, getRemote().getName()));
            } else
                log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("New client '%s' NOT added to object '%s' because client already know (%s)", newClient, getRemote().getName(), oldClient));

        }

        // If object NOT connected
        //   Add new client to object's clients
        //   If client already know
        //     Remove old Client from object's clients
        if (!wasConnected) {
            localConnections.add(newClient);
            if (oldClient != null) {
                localConnections.remove(oldClient);
                log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("New client '%s' added to object '%s' and replace old client (%s)", newClient, getRemote().getName(), oldClient));
            } else
                log.debug(Mrk_JSL.JSL_OBJS_SUB, String.format("New client '%s' added to object '%s'", newClient, getRemote().getName()));
        }

        if (!wasConnected && isLocalConnected())
            emitConn_LocalConnected(newClient);
    }

    /**
     * {@inheritDoc}
     */
    public void removeLocalClient(JSLLocalClient localClient) {
        localConnections.remove(localClient);
        emitConn_LocalDisconnected(localClient);
    }

    /**
     * @return an array containing all available local connections to the object.
     * if the object is disconnected or works only via cloud, then the
     * returned array will be empty.
     */
    public List<JSLLocalClient> getLocalClients() {
        return localConnections;
    }

    /**
     * @return the client connected (if any) to the corresponding JOD object.
     */
    public JSLLocalClient getConnectedLocalClient() {
        for (JSLLocalClient client : localConnections) {
            if (client.getState().isConnected())
                return client;
        }
        return null;
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
