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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
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
    public static final String KS_DEF_PATH = "./local_ks.jks";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLLocalClient.class);
    private final JSLCommunication_002 communication;
    private final JSLLocalClientsMngr clientsMngr;
    private JSLRemoteObject remoteObject = null;


    // Constructor
    private static final AbsCustomTrustManager trustManager = new DynAddTrustManager();
    private static Certificate localCertificate = null;
    private static SSLContext sslCtx = null;

    public static JSLLocalClient instantiate(JSLCommunication_002 communication, JSLLocalClientsMngr clientsMngr, String srvFullId,
                                             InetAddress remoteAddress, int port, String remoteObjId,
                                             String ksPath, String ksPass, String ksAlias, String defKsPath)
    throws JavaJKS.GenerationException, JavaSSL.GenerationException, JavaJKS.LoadingException{
        if (localCertificate == null || sslCtx == null) {
            if (ksPath == null || ksPath.isEmpty()) {
                ksPath = defKsPath != null && !defKsPath.isEmpty() ? defKsPath : KS_DEF_PATH;   // name - the system-dependent filename
                ksPass = KS_PASS;
            }

            if (ksAlias == null || ksAlias.isEmpty()) {
                ksAlias = srvFullId.substring(0, srvFullId.indexOf('/')) + "-LocalCert";
                //ksAlias = srvFullId + "-LocalCert";
            }

            KeyStore clientKeyStore;
            if (new File(ksPath).exists()) {
                log.debug(String.format("Load keystore from '%s' path.", ksPath));
                clientKeyStore = JavaJKS.loadKeyStore(ksPath, ksPass);
            } else {
                // certificateId = srvFullId, ksPass = KS_PASS, certAlias = srvId + "-LocalCert"
                log.debug(String.format("Generate keystore and store to '%s' path.", ksPath));
                clientKeyStore = JavaJKS.generateKeyStore(srvFullId, KS_PASS, ksAlias);
                JavaJKS.storeKeyStore(clientKeyStore, ksPath, KS_PASS);
            }

            log.debug(String.format("Extract local certificate with '%s' alias.", ksAlias));
            localCertificate = JavaJKS.extractCertificate(clientKeyStore, ksAlias);
            if (localCertificate == null)
                throw new JavaJKS.GenerationException(String.format("Certificate alias '%s' not found in '%s' keystore.", ksAlias, ksPath));
            sslCtx = JavaSSL.generateSSLContext(clientKeyStore, KS_PASS, trustManager);
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
//        log.info(String.format("Data '%s...' send to object '%s' from '%s' service", new String(data).substring(0, new String(data).indexOf("\n")), getRemoteId(), getLocalId()));
//        super.sendData(data);
//    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void sendData(String data) throws PeerStreamException, PeerNotConnectedException {
//        log.info(String.format("Data '%s...' send to object '%s' from '%s' service", data.substring(0, data.indexOf("\n")), getRemoteId(), getLocalId()));
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
