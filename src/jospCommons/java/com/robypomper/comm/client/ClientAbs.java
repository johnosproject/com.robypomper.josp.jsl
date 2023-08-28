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
import com.robypomper.comm.configs.AutoReConnectConfigsDefault;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerUnknownHostException;
import com.robypomper.comm.peer.DisconnectionReason;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerAbs;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaTimers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Timer;

public abstract class ClientAbs extends PeerAbs implements Client {

    // Class constants

    public static final String TH_RE_CONNECT_NAME = "RE_CONNECT";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(ClientAbs.class);
    // communication
    private Socket socket = null;
    // remote's endpoint
    private final InetAddress remoteAddr;
    private final int remotePort;
    // behaviours
    private final AutoReConnectConfigsDefault autoReConnect;
    private Timer reConnectTimer = null;


    // Constructors

    public ClientAbs(String localId, String remoteId, String remoteAddr, int remotePort, String protoName) throws PeerUnknownHostException {
        this(localId, remoteId, str2Inet_onClientConstructor(localId, remoteAddr), remotePort, protoName);
    }

    public ClientAbs(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName) {
        this(localId, remoteId, remoteAddr, remotePort, protoName,
                DataEncodingConfigs.CHARSET, DataEncodingConfigs.DELIMITER,
                HeartBeatConfigs.TIMEOUT_MS, HeartBeatConfigs.TIMEOUT_HB_MS, HeartBeatConfigs.ENABLE_HB_RES,
                ByeMsgConfigs.ENABLE, ByeMsgConfigs.BYE_MSG,
                AutoReConnectConfigs.ENABLE, AutoReConnectConfigs.DELAY);
    }

    public ClientAbs(String localId, String remoteId, String remoteAddr, int remotePort, String protoName,
                     Charset charset, byte[] delimiter,
                     int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                     Boolean enableByeMsg, byte[] byeMsg,
                     Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        this(localId, remoteId, str2Inet_onClientConstructor(localId, remoteAddr), remotePort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
    }

    public ClientAbs(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName,
                     Charset charset, byte[] delimiter,
                     int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                     Boolean enableByeMsg, byte[] byeMsg,
                     Boolean enableReConnect, int reConnectDelayMs) {
        super(localId, remoteId, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
        this.remoteAddr = remoteAddr;
        this.remotePort = remotePort;
        if (enableReConnect == null) enableReConnect = AutoReConnectConfigs.ENABLE;
        if (reConnectDelayMs < 0) reConnectDelayMs = AutoReConnectConfigs.DELAY;
        this.autoReConnect = new AutoReConnectConfigsDefault(enableReConnect, reConnectDelayMs);

        //addListener(new PeerConnectionLogger("ClientAbs"));
        addListener(autoReConnectListener);
    }

    public ClientAbs(String localId, String remoteId, String remoteAddr, int remotePort, String protoName,
                     Charset charset, String delimiter,
                     int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                     Boolean enableByeMsg, String byeMsg,
                     Boolean enableReConnect, int reConnectDelayMs) throws PeerUnknownHostException {
        this(localId, remoteId, str2Inet_onClientConstructor(localId, remoteAddr), remotePort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg,
                enableReConnect, reConnectDelayMs);
    }

    public ClientAbs(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName,
                     Charset charset, String delimiter,
                     int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                     Boolean enableByeMsg, String byeMsg,
                     Boolean enableReConnect, int reConnectDelayMs) {
        super(localId, remoteId, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
        this.remoteAddr = remoteAddr;
        this.remotePort = remotePort;
        if (enableReConnect == null) enableReConnect = AutoReConnectConfigs.ENABLE;
        if (reConnectDelayMs < 0) reConnectDelayMs = AutoReConnectConfigs.DELAY;
        this.autoReConnect = new AutoReConnectConfigsDefault(enableReConnect, reConnectDelayMs);

        //addListener(new PeerConnectionLogger("ClientAbs"));
        addListener(autoReConnectListener);
    }


    // Getters

    @Override
    public Socket getSocket() {
        return socket;
    }


    // Connection methods

    @Override
    public void connect() throws PeerConnectionException {
        if (getState().isConnected())
            return;

        if (getState().isConnecting())
            return;

        doConnect(false);
    }

    private void doConnect(boolean preventReConnecting) throws PeerConnectionException {
        if (getState().isConnected()) {
            JavaAssertions.makeAssertion_Failed("Can't call ClientAbs::doConnect() when already connected");
            return;
        }

        if (!getState().isConnecting())
            emitOnConnecting();

        try {
            socket = generateConnectedSocket(remoteAddr, remotePort);

        } catch (Throwable e) {

            if (!preventReConnecting) {
                if (getAutoReConnectConfigs().isEnable()
                        && e instanceof PeerConnectionException && ((PeerConnectionException) e).isRemotePeerNotAvailable())
                    scheduleReConnecting();
                else
                    emitOnDisconnect();
            }
            throw e;
        }

        startupConnection();
        stopReConnecting();
    }

    protected abstract Socket generateConnectedSocket(InetAddress remoteAddr, int remotePort) throws PeerConnectionException;

    @Override
    protected void doDisconnect(boolean localRequest, boolean remoteRequest) throws PeerDisconnectionException {
        if (getState().isDisconnected()) {
            JavaAssertions.makeAssertion_Failed("Can't call ClientAbs::doDisconnect() when already disconnected");
            return;
        }

        ConnectionState preState = getState();
        if (!getState().isDisconnecting())
            emitOnDisconnecting();

        if (preState == ConnectionState.WAITING_SERVER) {
            stopReConnecting();     // or tryStopReConnecting()
            setDisconnectionReason(DisconnectionReason.LOCAL_REQUEST);
            emitOnDisconnect();
            return;
        }

        super.doDisconnect(localRequest, remoteRequest);
        socket = null;
    }


    // Behaviours configs

    @Override
    public AutoReConnectConfigs getAutoReConnectConfigs() {
        return autoReConnect;
    }


    // Re-Connecting

    protected boolean isReConnecting() {
        return reConnectTimer != null;
    }

    protected void scheduleReConnecting() {
        reConnectTimer = JavaTimers.initAndStart(new ClientReConnectTimer(), true, TH_RE_CONNECT_NAME, getLocalId(), getAutoReConnectConfigs().getDelay(), getAutoReConnectConfigs().getDelay());
        emitOnConnecting_Waiting();
    }

    protected void stopReConnecting() {
        if (reConnectTimer == null)
            return;

        JavaTimers.stopTimer(reConnectTimer);
        reConnectTimer = null;
    }

    private class ClientReConnectTimer implements Runnable {

        @Override
        public void run() {
            try {
                log.trace(String.format("Peer '%s' re-connecting for scheduled attempt", getLocalId()));
                doConnect(true);

            } catch (PeerConnectionException e) {
                Throwable cause = e;
                while (cause.getCause()!=null)
                    cause = cause.getCause();
                emitOnFail("Error re-connecting Client for scheduled attempt: %s", e);
            }
        }

    }

    @SuppressWarnings("FieldCanBeLocal")
    private final PeerConnectionListener autoReConnectListener = new PeerConnectionListener() {

        @Override
        public void onConnecting(Peer peer) {
        }

        @Override
        public void onWaiting(Peer peer) {
        }

        @Override
        public void onConnect(Peer peer) {
        }

        @Override
        public void onDisconnecting(Peer peer) {
        }

        @Override
        public void onDisconnect(Peer peer) {
            if (!getAutoReConnectConfigs().isEnable())
                return;

            if (getDisconnectionReason() == DisconnectionReason.LOCAL_REQUEST)
                return;

            try {
                log.trace(String.format("Peer '%s' re-connecting after NOT required disconnection (reason: %s)", getLocalId(), getDisconnectionReason()));
                doConnect(true);

            } catch (PeerConnectionException e) {
                Throwable cause = e;
                while (cause.getCause()!=null)
                    cause = cause.getCause();
                emitOnFail("Error re-connecting Client after NOT required disconnection, schedule re-connection: %s", e);
                scheduleReConnecting();
            }
        }

        @Override
        public void onFail(Peer peer, String failMsg, Throwable exception) {
        }

    };

    // Others

    protected static InetAddress str2Inet_onClientConstructor(String localId, String remoteAddr) throws PeerUnknownHostException {
        try {
            return InetAddress.getByName(remoteAddr);

        } catch (UnknownHostException e) {
            throw new PeerUnknownHostException(localId, remoteAddr, e);
        }
    }

}
