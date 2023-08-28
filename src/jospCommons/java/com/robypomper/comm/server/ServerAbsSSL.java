/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.comm.server;

import com.robypomper.comm.behaviours.ByeMsgConfigs;
import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.ServerShutdownException;
import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.cert.Certificate;

public abstract class ServerAbsSSL extends ServerAbs {

    // Class constants

    public static final AbsCustomTrustManager TRUST_MANAGER = null;
    public static final Certificate LOCAL_PUBLIC_CERTIFICATE = null;
    public static final boolean REQUIRE_AUTH = false;
    public static final boolean ENABLE_CERT_SHARING_SERVER = false;
    public static final int TIMEOUT_CERT_SHARING_SERVER = 0;


    // Internal vars

    private final SSLContext sslCtx;
    private final boolean requireAuth;
    private final boolean enableServerCertSharing;
    private ServerCertSharing serverCertSharing = null;
    private final InetAddress bindAddr;
    private final AbsCustomTrustManager trustManager;
    private final Certificate localPublicCertificate;


    // Constructors

    protected ServerAbsSSL(String localId, int bindPort, String protoName,
                           SSLContext sslCtx) {
        this(localId, null, bindPort, protoName,
                sslCtx);
    }

    protected ServerAbsSSL(String localId, int bindPort, String protoName,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing) {
        this(localId, null, bindPort, protoName,
                sslCtx, trustManager, localPublicCertificate, requireAuth, enableCertSharing,
                DataEncodingConfigs.CHARSET, DataEncodingConfigs.DELIMITER,
                HeartBeatConfigs.TIMEOUT_MS, HeartBeatConfigs.TIMEOUT_HB_MS, HeartBeatConfigs.ENABLE_HB_RES,
                ByeMsgConfigs.ENABLE, ByeMsgConfigs.BYE_MSG);
    }

    protected ServerAbsSSL(String localId, int bindPort, String protoName,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing,
                           Charset charset, byte[] delimiter,
                           int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                           Boolean enableByeMsg, byte[] byeMsg) {
        super(localId, null, bindPort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
        this.sslCtx = sslCtx;
        this.requireAuth = requireAuth;
        this.enableServerCertSharing = enableCertSharing;
        this.bindAddr = null;
        this.trustManager = trustManager;
        this.localPublicCertificate = localPublicCertificate;
    }

    protected ServerAbsSSL(String localId, int bindPort, String protoName,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing,
                           Charset charset, String delimiter,
                           int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                           Boolean enableByeMsg, String byeMsg) {
        super(localId, null, bindPort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
        this.sslCtx = sslCtx;
        this.requireAuth = requireAuth;
        this.enableServerCertSharing = enableCertSharing;
        this.bindAddr = null;
        this.trustManager = trustManager;
        this.localPublicCertificate = localPublicCertificate;
    }

    protected ServerAbsSSL(String localId, InetAddress bindAddr, int bindPort, String protoName,
                           SSLContext sslCtx) {
        this(localId, bindAddr, bindPort, protoName,
                sslCtx, TRUST_MANAGER, LOCAL_PUBLIC_CERTIFICATE, REQUIRE_AUTH, ENABLE_CERT_SHARING_SERVER);
    }

    protected ServerAbsSSL(String localId, InetAddress bindAddr, int bindPort, String protoName,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing) {
        this(localId, bindAddr, bindPort, protoName,
                sslCtx, trustManager, localPublicCertificate, requireAuth, enableCertSharing,
                DataEncodingConfigs.CHARSET, DataEncodingConfigs.DELIMITER,
                HeartBeatConfigs.TIMEOUT_MS, HeartBeatConfigs.TIMEOUT_HB_MS, HeartBeatConfigs.ENABLE_HB_RES,
                ByeMsgConfigs.ENABLE, ByeMsgConfigs.BYE_MSG);
    }

    protected ServerAbsSSL(String localId, InetAddress bindAddr, int bindPort, String protoName,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing,
                           Charset charset, byte[] delimiter,
                           int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                           Boolean enableByeMsg, byte[] byeMsg) {
        super(localId, bindAddr, bindPort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
        this.sslCtx = sslCtx;
        this.requireAuth = requireAuth;
        this.enableServerCertSharing = enableCertSharing;
        this.bindAddr = bindAddr;
        this.trustManager = trustManager;
        this.localPublicCertificate = localPublicCertificate;
    }

    protected ServerAbsSSL(String localId, InetAddress bindAddr, int bindPort, String protoName,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing,
                           Charset charset, String delimiter,
                           int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                           Boolean enableByeMsg, String byeMsg) {
        super(localId, bindAddr, bindPort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
        this.sslCtx = sslCtx;
        this.requireAuth = requireAuth;
        this.enableServerCertSharing = enableCertSharing;
        this.bindAddr = bindAddr;
        this.trustManager = trustManager;
        this.localPublicCertificate = localPublicCertificate;
    }


    // Getters

    public SSLContext getSSLContext() {
        return sslCtx;
    }

    public boolean isAuthRequired() {
        return requireAuth;
    }


    // Server startup methods

    public void startup() throws ServerStartupException {
        super.startup();
        if (enableServerCertSharing) {
            serverCertSharing = ServerCertSharing.generate(this, bindAddr, super.getServerPeerInfo().getPort(), trustManager, localPublicCertificate);
            serverCertSharing.startup();
        }
    }

    protected ServerClient generateClient(Socket socket) throws PeerConnectionException {
        if (!isAuthRequired())
            try {
                ((SSLSocket) socket).startHandshake();

            } catch (IOException e) {
                throw new PeerConnectionException(null, socket, e, "ServerClient discharged because error on requested handshake.");
            }

        if (isAuthRequired())
            try {
                ((SSLSocket) socket).getSession().getPeerPrincipal();
            } catch (SSLPeerUnverifiedException e) {
                throw new PeerConnectionException(null, socket, e, "ServerClient discharged because client not authenticated.");
            }

        return super.generateClient(socket);
    }

    public void shutdown() throws ServerShutdownException {
        super.shutdown();

        if (enableServerCertSharing) {
            serverCertSharing.shutdown();
            serverCertSharing = null;
        }
    }


    // Connection methods

    @Override
    protected ServerSocket generateBindedServerSocket(InetAddress bindAddr, int bindPort) throws ServerStartupException {
        try {
            SSLServerSocket sslSS = (SSLServerSocket) sslCtx.getServerSocketFactory().createServerSocket(bindPort, 50, bindAddr);
            sslSS.setEnabledProtocols(new String[]{"TLSv1.2"});
            sslSS.setNeedClientAuth(requireAuth);
            return sslSS;

        } catch (IOException e) {
            throw new ServerStartupException(this, bindAddr, bindPort, e);
        }
    }

}
