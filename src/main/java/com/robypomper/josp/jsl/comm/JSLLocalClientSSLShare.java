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
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPSecurityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.security.cert.Certificate;


/**
 * Client implementation for JOD local server.
 * <p>
 * This class provide a Cert Sharing Client (a client that allow to share
 * client and server certificates).
 */
@SuppressWarnings("un1used")
public class JSLLocalClientSSLShare extends ClientAbsSSL implements JSLLocalClient {


    // Internal vars

    private static final int CERT_SHARING_TIMEOUT_MS = 30 * 1000;
    private final JSLLocalClientsMngr clientsMngr;
    private final boolean serverIsSSLShare;
    private JOSPSecurityLevel security;
    private JSLRemoteObject remoteObject = null;


    // Constructors

    public JSLLocalClientSSLShare(JSLLocalClientsMngr clientsMngr, String srvFullId,
                                  InetAddress remoteAddress, int port, String remoteObjId,
                                  PeerConnectionListener localClientListener,
                                  boolean useSSLSharing,
                                  SSLContext sslCtx, Certificate localPublicCertificate, AbsCustomTrustManager trustManager) {
        super(srvFullId, remoteObjId, remoteAddress, port, JOSPProtocol.JOSP_PROTO_NAME, sslCtx, trustManager, localPublicCertificate,
                useSSLSharing, CERT_SHARING_TIMEOUT_MS);

        this.clientsMngr = clientsMngr;
        this.serverIsSSLShare = useSSLSharing && isServerCertSharingEnabled(remoteAddress, port);
        this.security = JOSPSecurityLevel.calculate(true, serverIsSSLShare, false);   // temporary value, after connection it can be updated with useCertificatedId=true

        addListener(localClientListener);
    }

    protected void setIsServerCertificateFull(boolean isServerCertificateFull) {
        this.security = JOSPSecurityLevel.calculate(true, serverIsSSLShare, isServerCertificateFull);
    }

    // Message methods

    @Override
    protected boolean processData(byte[] data) {
        return false;
    }

    @Override
    protected boolean processData(String data) {
        return clientsMngr.processFromObjectMsg(this, data, JOSPPerm.Connection.OnlyLocal);
    }


    // JSL Local Client methods

    /**
     * When created, add corresponding JSLRemoteObject to current local client.
     *
     * @param remoteObject the JSLRemoteObject instance that use current local client
     *                     to communicate with object.
     */
    @Override
    public void setRemoteObject(JSLRemoteObject remoteObject) {
        if (this.remoteObject != null)
            throw new IllegalArgumentException("Can't set JSLRemoteObject twice for JSLLocalClient.");
        this.remoteObject = remoteObject;
    }


    // Others

    @Override
    public JOSPSecurityLevel getSecurityLevel() {
        return security;
    }

}
