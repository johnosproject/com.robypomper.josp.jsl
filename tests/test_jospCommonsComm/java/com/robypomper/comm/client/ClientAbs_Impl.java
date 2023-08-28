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
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerUnknownHostException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public class ClientAbs_Impl extends ClientAbs {

    public ClientAbs_Impl(String localId, String remoteId, String remoteAddr, int remotePort, String protoName) throws PeerUnknownHostException {
        super(localId, remoteId, remoteAddr, remotePort, protoName);
    }

    public ClientAbs_Impl(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName) {
        super(localId, remoteId, remoteAddr, remotePort, protoName);
    }

    public ClientAbs_Impl(String localId, String remoteId, String remoteAddr, int remotePort, String protoName, Charset charset, byte[] delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, byte[] byeMsg, Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        super(localId, remoteId, remoteAddr, remotePort, protoName, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg, enableReConnect, reConnectDelayMs);
    }

    public ClientAbs_Impl(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName, Charset charset, byte[] delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, byte[] byeMsg, Boolean enableReConnect, int reConnectDelayMs) {
        super(localId, remoteId, remoteAddr, remotePort, protoName, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg, enableReConnect, reConnectDelayMs);
    }

    public ClientAbs_Impl(String localId, String remoteId, String remoteAddr, int remotePort, String protoName, Charset charset, String delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, String byeMsg, Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        super(localId, remoteId, remoteAddr, remotePort, protoName, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg, enableReConnect, reConnectDelayMs);
    }

    public ClientAbs_Impl(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName, Charset charset, String delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, String byeMsg, Boolean enableReConnect, int reConnectDelayMs) {
        super(localId, remoteId, remoteAddr, remotePort, protoName, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg, enableReConnect, reConnectDelayMs);
    }

    @Override
    public HeartBeatImpl getHeartBeatConfigs() {
        return (HeartBeatImpl) super.getHeartBeatConfigs();
    }

    @Override
    protected Socket generateConnectedSocket(InetAddress remoteAddr, int remotePort) throws PeerConnectionException {
        Socket s = new Socket();
        try {
            s.connect(new InetSocketAddress(remoteAddr, remotePort));
            return s;

        } catch (IOException e) {
            throw new PeerConnectionException(this, getSocket(), remoteAddr, remotePort, e);
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

    @Override
    protected boolean processData(byte[] data) {
        return true;
    }

    @Override
    protected boolean processData(String data) {
        return false;
    }

}
