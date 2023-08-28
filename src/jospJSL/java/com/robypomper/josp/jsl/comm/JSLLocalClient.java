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

import com.robypomper.comm.client.ClientAbsSSL;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.comm.trustmanagers.DynAddTrustManager;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.cert.Certificate;


/**
 * Client implementation for JOD local server.
 * <p>
 * This class provide a Cert Sharing Client (a client that allow to share
 * client and server certificates).
 */
@SuppressWarnings("un1used")
public class JSLLocalClient extends ClientAbsSSL {

    // Class constants

    public static final String KS_PASS = "123456";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLCommunication_002 communication;
    private final JSLLocalClientsMngr clientsMngr;
    private JSLRemoteObject remoteObject = null;


    // Constructor

    public static JSLLocalClient instantiate(JSLCommunication_002 communication, JSLLocalClientsMngr clientsMngr, String srvFullId,
                                             InetAddress remoteAddress, int port, String remoteObjId) {

        String localId = srvFullId;

        AbsCustomTrustManager trustManager = new DynAddTrustManager();
        Certificate localCertificate = null;
        SSLContext sslCtx = null;
        try {
            KeyStore clientKeyStore = JavaJKS.generateKeyStore(localId, KS_PASS, localId + "-LocalCert");
            localCertificate = JavaJKS.extractCertificate(clientKeyStore, localId + "-LocalCert");
            sslCtx = JavaSSL.generateSSLContext(clientKeyStore, KS_PASS, trustManager);

        } catch (JavaJKS.GenerationException | JavaSSL.GenerationException e) {
            assert false : String.format("JKS and SSL generation are standard and should not throw exception [%s] %s", e.getClass().getSimpleName(), e.getMessage());
        }

        return new JSLLocalClient(communication, clientsMngr, srvFullId, remoteAddress, port, remoteObjId, sslCtx, trustManager, localCertificate);
    }

    private JSLLocalClient(JSLCommunication_002 communication, JSLLocalClientsMngr clientsMngr, String srvFullId,
                           InetAddress remoteAddress, int port, String remoteObjId,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate) {
        super(srvFullId, remoteObjId, remoteAddress, port, JOSPProtocol.JOSP_PROTO_NAME, sslCtx, trustManager, localPublicCertificate, JOSPProtocol.CERT_SHARING_ENABLE, JOSPProtocol.CERT_SHARING_TIMEOUT);

        this.communication = communication;
        this.clientsMngr = clientsMngr;

        addListener(new PeerConnectionListener() {

            @Override
            public void onConnecting(Peer peer) {
            }

            @Override
            public void onWaiting(Peer peer) {
            }

            @Override
            public void onConnect(Peer peer) {
                clientsMngr.onClientConnected(JSLLocalClient.this);
            }

            @Override
            public void onDisconnecting(Peer peer) {
            }

            @Override
            public void onDisconnect(Peer peer) {
                clientsMngr.onClientDisconnected(JSLLocalClient.this);
            }

            @Override
            public void onFail(Peer peer, String failMsg, Throwable exception) {
                clientsMngr.onClientConnectionError(JSLLocalClient.this, exception);
            }

        });
    }


    // Message methods

    @Override
    protected boolean processData(byte[] data) {
        return false;
    }

    @Override
    protected boolean processData(String data) {
        return communication.processFromObjectMsg(data, JOSPPerm.Connection.OnlyLocal);
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void sendData(byte[] data) throws PeerStreamException, PeerNotConnectedException {
//        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Data '%s...' send to object '%s' from '%s' service", new String(data).substring(0, new String(data).indexOf("\n")), getRemoteId(), getLocalId()));
//        super.sendData(data);
//    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void sendData(String data) throws PeerStreamException, PeerNotConnectedException {
//        log.info(Mrk_JSL.JSL_COMM_SUB, String.format("Data '%s...' send to object '%s' from '%s' service", data.substring(0, data.indexOf("\n")), getRemoteId(), getLocalId()));
//        super.sendData(data);
//    }


    // JSL Local Client methods

    /**
     * When created, add corresponding JSLRemoteObject to current local client.
     *
     * @param remoteObject the JSLRemoteObject instance that use current local client
     *                     to communicate with object.
     */
    public void setRemoteObject(JSLRemoteObject remoteObject) {
        if (this.remoteObject != null)
            throw new IllegalArgumentException("Can't set JSLRemoteObject twice for JSLLocalClient.");
        this.remoteObject = remoteObject;
    }

//    /**
//     * Version of method {@link #getObjId()} that do NOT throws exceptions.
//     *
//     * @return the represented server's object id.
//     */
//    public String tryObjId() {
//        try {
//            return getServerId();
//        } catch (ServerNotConnectedException e) {
//            return null;
//        }
//    }

//    /**
//     * The object id.
//     *
//     * @return the represented server's object id.
//     */
//    public String getObjId() throws ServerNotConnectedException {
//        return getServerId();
//    }

}
