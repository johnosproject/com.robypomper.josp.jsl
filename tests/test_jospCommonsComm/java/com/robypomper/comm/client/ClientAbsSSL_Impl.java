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

import com.robypomper.comm.behaviours.HeartBeatImpl;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerUnknownHostException;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.security.cert.Certificate;

public class ClientAbsSSL_Impl extends ClientAbsSSL {

    public ClientAbsSSL_Impl(String localId, String remoteId, String remoteAddr, int remotePort, String protoName, SSLContext sslCtx) throws PeerUnknownHostException {
        super(localId, remoteId, remoteAddr, remotePort, protoName, sslCtx);
    }

    public ClientAbsSSL_Impl(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName, SSLContext sslCtx) {
        super(localId, remoteId, remoteAddr, remotePort, protoName, sslCtx);
    }

    public ClientAbsSSL_Impl(String localId, String remoteId, String remoteAddr, int remotePort, String protoName, SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean enableCertSharing, int certSharingTimeoutMs) throws PeerUnknownHostException {
        super(localId, remoteId, remoteAddr, remotePort, protoName, sslCtx, trustManager, localPublicCertificate, enableCertSharing, certSharingTimeoutMs);
    }

    public ClientAbsSSL_Impl(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName, SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean enableCertSharing, int certSharingTimeoutMs) {
        super(localId, remoteId, remoteAddr, remotePort, protoName, sslCtx, trustManager, localPublicCertificate, enableCertSharing, certSharingTimeoutMs);
    }

    public ClientAbsSSL_Impl(String localId, String remoteId, String remoteAddr, int remotePort, String protoName, SSLContext sslCtx, Charset charset, byte[] delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, byte[] byeMsg, Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        super(localId, remoteId, remoteAddr, remotePort, protoName, sslCtx, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg, enableReConnect, reConnectDelayMs);
    }

    public ClientAbsSSL_Impl(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName, SSLContext sslCtx, Charset charset, byte[] delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, byte[] byeMsg, Boolean enableReConnect, int reConnectDelayMs) {
        super(localId, remoteId, remoteAddr, remotePort, protoName, sslCtx, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg, enableReConnect, reConnectDelayMs);
    }

    public ClientAbsSSL_Impl(String localId, String remoteId, String remoteAddr, int remotePort, String protoName, SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean enableCertSharing, int certSharingTimeoutMs, Charset charset, byte[] delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, byte[] byeMsg, Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        super(localId, remoteId, remoteAddr, remotePort, protoName, sslCtx, trustManager, localPublicCertificate, enableCertSharing, certSharingTimeoutMs, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg, enableReConnect, reConnectDelayMs);
    }

    public ClientAbsSSL_Impl(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName, SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean enableCertSharing, int certSharingTimeoutMs, Charset charset, byte[] delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, byte[] byeMsg, Boolean enableReConnect, int reConnectDelayMs) {
        super(localId, remoteId, remoteAddr, remotePort, protoName, sslCtx, trustManager, localPublicCertificate, enableCertSharing, certSharingTimeoutMs, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg, enableReConnect, reConnectDelayMs);
    }

    public ClientAbsSSL_Impl(String localId, String remoteId, String remoteAddr, int remotePort, String protoName, SSLContext sslCtx, Charset charset, String delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, String byeMsg, Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        super(localId, remoteId, remoteAddr, remotePort, protoName, sslCtx, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg, enableReConnect, reConnectDelayMs);
    }

    public ClientAbsSSL_Impl(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName, SSLContext sslCtx, Charset charset, String delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, String byeMsg, Boolean enableReConnect, int reConnectDelayMs) {
        super(localId, remoteId, remoteAddr, remotePort, protoName, sslCtx, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg, enableReConnect, reConnectDelayMs);
    }

    public ClientAbsSSL_Impl(String localId, String remoteId, String remoteAddr, int remotePort, String protoName, SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean enableCertSharing, int certSharingTimeoutMs, Charset charset, String delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, String byeMsg, Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        super(localId, remoteId, remoteAddr, remotePort, protoName, sslCtx, trustManager, localPublicCertificate, enableCertSharing, certSharingTimeoutMs, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg, enableReConnect, reConnectDelayMs);
    }

    public ClientAbsSSL_Impl(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName, SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean enableCertSharing, int certSharingTimeoutMs, Charset charset, String delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, String byeMsg, Boolean enableReConnect, int reConnectDelayMs) {
        super(localId, remoteId, remoteAddr, remotePort, protoName, sslCtx, trustManager, localPublicCertificate, enableCertSharing, certSharingTimeoutMs, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg, enableReConnect, reConnectDelayMs);
    }

    @Override
    public HeartBeatImpl getHeartBeatConfigs() {
        return (HeartBeatImpl) super.getHeartBeatConfigs();
    }

    @Override
    protected void closeSocket() throws PeerDisconnectionException {
        try {
            getSocket().close();

        } catch (IOException e) {
            throw new PeerDisconnectionException(this, getSocket(), getConnectionInfo().getRemoteInfo().getAddr(), getConnectionInfo().getRemoteInfo().getPort(), e);
        }
    }

    @Override
    protected boolean processData(byte[] data) {
        return true;
    }

    @Override
    protected boolean processData(String data) {
        return false;
    }

}
