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

package com.robypomper.comm.client;

import com.robypomper.comm.behaviours.ByeMsgConfigs;
import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.configs.AutoReConnectConfigs;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerUnknownHostException;
import com.robypomper.comm.server.ServerAbsSSL;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.cert.Certificate;

public abstract class ClientAbsSSL extends ClientAbs {

    // Internal vars

    // ssl
    private final SSLContext sslCtx;
    // cert sharing
    private final boolean enableCertSharing;
    private final AbsCustomTrustManager trustManager;
    private final Certificate localPublicCertificate;
    private final int certSharingTimeoutMs;


    // Constructors

    public ClientAbsSSL(String localId, String remoteId, String remoteAddr, int remotePort, String protoName,
                        SSLContext sslCtx) throws PeerUnknownHostException {
        this(localId, remoteId, str2Inet_onClientConstructor(localId, remoteAddr), remotePort, protoName,
                sslCtx);
    }

    public ClientAbsSSL(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName,
                        SSLContext sslCtx) {
        this(localId, remoteId, remoteAddr, remotePort, protoName,
                sslCtx, ServerAbsSSL.TRUST_MANAGER, ServerAbsSSL.LOCAL_PUBLIC_CERTIFICATE, ServerAbsSSL.ENABLE_CERT_SHARING_SERVER, ServerAbsSSL.TIMEOUT_CERT_SHARING_SERVER);
    }

    public ClientAbsSSL(String localId, String remoteId, String remoteAddr, int remotePort, String protoName,
                        SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean enableCertSharing, int certSharingTimeoutMs) throws PeerUnknownHostException {
        this(localId, remoteId, str2Inet_onClientConstructor(localId, remoteAddr), remotePort, protoName,
                sslCtx, trustManager, localPublicCertificate, enableCertSharing, certSharingTimeoutMs);
    }

    public ClientAbsSSL(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName,
                        SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean enableCertSharing, int certSharingTimeoutMs) {
        this(localId, remoteId, remoteAddr, remotePort, protoName,
                sslCtx, trustManager, localPublicCertificate, enableCertSharing, certSharingTimeoutMs,
                DataEncodingConfigs.CHARSET, DataEncodingConfigs.DELIMITER,
                HeartBeatConfigs.TIMEOUT_MS, HeartBeatConfigs.TIMEOUT_HB_MS, HeartBeatConfigs.ENABLE_HB_RES,
                ByeMsgConfigs.ENABLE, ByeMsgConfigs.BYE_MSG,
                AutoReConnectConfigs.ENABLE, AutoReConnectConfigs.DELAY);
    }

    public ClientAbsSSL(String localId, String remoteId, String remoteAddr, int remotePort, String protoName,
                        SSLContext sslCtx,
                        Charset charset, byte[] delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, byte[] byeMsg,
                        Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        this(localId, remoteId, str2Inet_onClientConstructor(localId, remoteAddr), remotePort, protoName,
                sslCtx,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
    }

    public ClientAbsSSL(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName,
                        SSLContext sslCtx,
                        Charset charset, byte[] delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, byte[] byeMsg,
                        Boolean enableReConnect, int reConnectDelayMs) {
        this(localId, remoteId, remoteAddr, remotePort, protoName,
                sslCtx, ServerAbsSSL.TRUST_MANAGER, ServerAbsSSL.LOCAL_PUBLIC_CERTIFICATE, ServerAbsSSL.ENABLE_CERT_SHARING_SERVER, ServerAbsSSL.TIMEOUT_CERT_SHARING_SERVER,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
    }

    public ClientAbsSSL(String localId, String remoteId, String remoteAddr, int remotePort, String protoName,
                        SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean enableCertSharing, int certSharingTimeoutMs,
                        Charset charset, byte[] delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, byte[] byeMsg,
                        Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        this(localId, remoteId, str2Inet_onClientConstructor(localId, remoteAddr), remotePort, protoName,
                sslCtx, trustManager, localPublicCertificate, enableCertSharing, certSharingTimeoutMs,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
    }

    public ClientAbsSSL(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName,
                        SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean enableCertSharing, int certSharingTimeoutMs,
                        Charset charset, byte[] delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, byte[] byeMsg,
                        Boolean enableReConnect, int reConnectDelayMs) {
        super(localId, remoteId, remoteAddr, remotePort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
        this.sslCtx = sslCtx;

        if (enableCertSharing && trustManager == null)
            throw new IllegalArgumentException("When enableCertSharing is true, trustManager can't be null.");
        this.enableCertSharing = enableCertSharing;
        this.trustManager = trustManager;
        this.localPublicCertificate = localPublicCertificate;
        this.certSharingTimeoutMs = certSharingTimeoutMs;
    }

    public ClientAbsSSL(String localId, String remoteId, String remoteAddr, int remotePort, String protoName,
                        SSLContext sslCtx,
                        Charset charset, String delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, String byeMsg,
                        Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        this(localId, remoteId, str2Inet_onClientConstructor(localId, remoteAddr), remotePort, protoName,
                sslCtx,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
    }

    public ClientAbsSSL(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName,
                        SSLContext sslCtx,
                        Charset charset, String delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, String byeMsg,
                        Boolean enableReConnect, int reConnectDelayMs) {
        this(localId, remoteId, remoteAddr, remotePort, protoName,
                sslCtx, ServerAbsSSL.TRUST_MANAGER, ServerAbsSSL.LOCAL_PUBLIC_CERTIFICATE, ServerAbsSSL.ENABLE_CERT_SHARING_SERVER, ServerAbsSSL.TIMEOUT_CERT_SHARING_SERVER,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
    }

    public ClientAbsSSL(String localId, String remoteId, String remoteAddr, int remotePort, String protoName,
                        SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean enableCertSharing, int certSharingTimeoutMs,
                        Charset charset, String delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, String byeMsg,
                        Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        this(localId, remoteId, str2Inet_onClientConstructor(localId, remoteAddr), remotePort, protoName,
                sslCtx, trustManager, localPublicCertificate, enableCertSharing, certSharingTimeoutMs,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
    }

    public ClientAbsSSL(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName,
                        SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean enableCertSharing, int certSharingTimeoutMs,
                        Charset charset, String delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, String byeMsg,
                        Boolean enableReConnect, int reConnectDelayMs) {
        super(localId, remoteId, remoteAddr, remotePort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
        this.sslCtx = sslCtx;

        if (enableCertSharing && trustManager == null)
            throw new IllegalArgumentException("When enableCertSharing is true, trustManager can't be null.");
        this.enableCertSharing = enableCertSharing;
        this.trustManager = trustManager;
        this.localPublicCertificate = localPublicCertificate;
        this.certSharingTimeoutMs = certSharingTimeoutMs;
    }


    // Getters

    public SSLContext getSSLContext() {
        return sslCtx;
    }


    // Connection methods

    @Override
    protected Socket generateConnectedSocket(InetAddress remoteAddr, int remotePort) throws PeerConnectionException {
        SSLSocket s;
        try {
            s = (SSLSocket) sslCtx.getSocketFactory().createSocket(remoteAddr, remotePort);

        } catch (IOException e) {
            throw new PeerConnectionException(this, getSocket(), remoteAddr, remotePort, e);
        }

        try {
            s.startHandshake();
            return s;

        } catch (IOException e) {
            if (!enableCertSharing)
                throw new PeerConnectionException(this, getSocket(), remoteAddr, remotePort, e, String.format("Error on Peer '%s' because SSL handshake failed with '%s:%d'", this, remoteAddr.getHostAddress(), remotePort));
        }

        try {
            if (!shareCertificate(remoteAddr, remotePort))
                throw new PeerConnectionException(this, getSocket(), remoteAddr, remotePort, String.format("Error on Peer '%s' because CertSharing reached timeout with '%s:%d'", this, remoteAddr.getHostAddress(), remotePort));

        } catch (PeerConnectionException e) {
            throw new PeerConnectionException(this, getSocket(), remoteAddr, remotePort, e, String.format("Error on Peer '%s' because CertSharing can't connect with '%s:%d' (CertShare's endpoint)", this, remoteAddr.getHostAddress(), remotePort));
        }

        try {
            s = (SSLSocket) sslCtx.getSocketFactory().createSocket(remoteAddr, remotePort);

        } catch (IOException e) {
            throw new PeerConnectionException(this, getSocket(), remoteAddr, remotePort, e);
        }

        try {
            s.startHandshake();
            return s;

        } catch (IOException e) {
            throw new PeerConnectionException(this, getSocket(), remoteAddr, remotePort, e, String.format("Error on Peer '%s' because CertSharing and SSL handshake failed with '%s:%d'", this, remoteAddr.getHostAddress(), remotePort));
        }
    }

    @Override
    protected void closeSocket() throws PeerDisconnectionException {
        try {
            getSocket().close();

        } catch (IOException e) {
            throw new PeerDisconnectionException(this, getSocket(), getConnectionInfo().getRemoteInfo().getAddr(), getConnectionInfo().getRemoteInfo().getPort(), e);
        }
    }

    protected boolean shareCertificate(InetAddress remoteAddr, int remotePort) throws PeerConnectionException {
        ClientCertSharing clientCertSharing = ClientCertSharing.generate(this, remoteAddr, remotePort, trustManager, localPublicCertificate);
        clientCertSharing.shareCertificate();
        return clientCertSharing.waitForDone(certSharingTimeoutMs);
    }

}
