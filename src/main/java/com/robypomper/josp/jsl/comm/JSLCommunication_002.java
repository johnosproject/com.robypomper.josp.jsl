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

package com.robypomper.josp.jsl.comm;

import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.java.JavaThreads;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLObjsMngr_002;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of the {@link JSLCommunication} interface.
 */
public class JSLCommunication_002 implements JSLCommunication {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLCommunication_002.class);
    // JSL
    private final JSLSettings_002 locSettings;
    private final JSLObjsMngr_002 jslObjsMngr;
    // Comms
    private final JCPAPIsClientSrv jcpClient;
    private final JSLGwS2OClient gwClient;
    private final JSLLocalClientsMngr localClients;


    // Constructor

    /**
     * @param settings    the JSL settings.
     * @param srvInfo     the service's info.
     * @param jcpClient   the jcp service client.
     * @param jslObjsMngr the {@link JSLObjsMngr} instance used to update component
     *                    status.
     */
    public JSLCommunication_002(JSL_002 jsl, JSLSettings_002 settings, JSLServiceInfo srvInfo, JCPAPIsClientSrv jcpClient, JSLObjsMngr_002 jslObjsMngr, String instanceId) throws LocalCommunicationException {
        this.locSettings = settings;
        this.jslObjsMngr = jslObjsMngr;
        this.jcpClient = jcpClient;
        this.jcpClient.addConnectionListener(jcpConnectionListener);

        this.localClients = new JSLLocalClientsMngr(jsl, this, jslObjsMngr, locSettings, srvInfo);
        this.gwClient = new JSLGwS2OClient(this, srvInfo, jcpClient, instanceId);
        this.gwClient.addListener(gwClientListener);

        log.info(String.format("Initialized JODCommunication instance for '%s' ('%s') service", srvInfo.getSrvName(), srvInfo.getSrvId()));
    }


    // To Object Msg

    // see JSLRemoteObject


    // From Object Msg

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean processFromObjectMsg(String msg, JOSPPerm.Connection connType) {
        String objId;
        try {
            objId = JOSPProtocol_ObjectToService.getObjId(msg);
            log.trace(String.format("Received '%s' message from %s (%s)", msg.substring(0, msg.indexOf('\n')), objId, connType == JOSPPerm.Connection.OnlyLocal ? "local connection" : "cloud connection"));

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(String.format("Error on parsing '%s' message because %s", msg.substring(0, msg.indexOf('\n')), e.getMessage()), e);
            return false;
        }

        try {
            JSLRemoteObject obj = jslObjsMngr.getById(objId);
            int count = 0;
            while (obj == null && count < 5) {
                count++;
                JavaThreads.softSleep(100);
                obj = jslObjsMngr.getById(objId);
            }

            if (obj == null && connType == JOSPPerm.Connection.LocalAndCloud && JOSPProtocol_ObjectToService.isObjectInfoMsg(msg)) {
                jslObjsMngr.addCloudObject(objId);
                obj = jslObjsMngr.getById(objId);
            }

            if (obj == null)
                throw new Throwable(String.format("Object '%s' not found", objId));//throw new ObjectNotFound(objId)

            if (!obj.processFromObjectMsg(msg, connType))
                throw new Throwable(String.format("Unknown error on processing '%s' message", msg.substring(0, msg.indexOf('\n'))));

            log.trace(String.format("Message '%s' received from '%s' processed successfully", msg.substring(0, msg.indexOf('\n')), objId));
            return true;

        } catch (Throwable t) {
            log.warn(String.format("Error on processing '%s' message from %s because %s", msg.substring(0, msg.indexOf('\n')), objId, t.getMessage()), t);
            return false;
        }
    }


    // Connections access

    /**
     * {@inheritDoc}
     */
    @Override
    public JCPAPIsClientSrv getCloudAPIs() {
        return jcpClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLGwS2OClient getCloudConnection() {
        return gwClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLLocalClientsMngr getLocalConnections() {
        return localClients;
    }


    // Clients and server listeners

    @SuppressWarnings("FieldCanBeLocal")
    private final JCPClient2.ConnectionListener jcpConnectionListener = new JCPClient2.ConnectionListener() {

        private boolean connFailedPrinted = false;

        @Override
        public void onConnected(JCPClient2 jcpClient) {
            log.info("JCP APIs client connected");
            connFailedPrinted = false;
        }

        @Override
        public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
            if (t instanceof JCPClient2.JCPNotReachableException) {
                if (connFailedPrinted) {
                    log.debug(String.format("JCP APIs at '%s' still unreachable", jcpClient.getAPIsUrl()));
                } else {
                    log.warn(String.format("Can't connect to JCP APIs at '%s', retry later", jcpClient.getAPIsUrl()));
                    connFailedPrinted = true;
                }
            } else
                log.warn(String.format("Error on JCP APIs connection attempt at '%s'", jcpClient.getAPIsUrl()), t);
        }

        @Override
        public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
            log.warn(String.format("Error on authenticating to JCP APIs because %s", t.getMessage()), t);
        }

        @Override
        public void onDisconnected(JCPClient2 jcpClient) {
            log.info("JCP APIs Client disconnected");
        }

    };

    @SuppressWarnings("FieldCanBeLocal")
    private final PeerConnectionListener gwClientListener = new PeerConnectionListener() {

        @Override
        public void onConnecting(Peer peer) {
        }

        @Override
        public void onWaiting(Peer peer) {
        }

        @Override
        public void onConnect(Peer peer) {
            log.info("JCP GWs client connected");
        }

        @Override
        public void onDisconnecting(Peer peer) {
        }

        @Override
        public void onDisconnect(Peer peer) {
            log.info("JCP GWs Client disconnected");
        }

        @Override
        public void onFail(Peer peer, String failMsg, Throwable t) {
            // Connection problem
            if (t instanceof PeerConnectionException) {
                log.warn(String.format("Error on JCP GWs client connection: %s (%s)", failMsg, t), t);
                return;
            }

            log.warn(String.format("Error on JCP GWs client: %s (%s)", failMsg, t), t);
        }

    };

}
