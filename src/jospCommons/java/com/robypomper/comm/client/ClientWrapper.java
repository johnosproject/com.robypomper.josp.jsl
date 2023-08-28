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
import com.robypomper.comm.behaviours.ByeMsgConfigsWrapper;
import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.behaviours.HeartBeatConfigsWrapper;
import com.robypomper.comm.configs.AutoReConnectConfigs;
import com.robypomper.comm.configs.AutoReConnectConfigsWrapper;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.configs.DataEncodingConfigsWrapper;
import com.robypomper.comm.connection.ConnectionInfo;
import com.robypomper.comm.connection.ConnectionInfoDefault;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.comm.peer.DisconnectionReason;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.comm.peer.PeerDataListener;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ClientWrapper implements Client {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(ClientWrapper.class);
    // wrapper
    private Client wrapper;
    private final String localId;
    private final String remoteId;
    private final ConnectionInfoDefault connectionInfo;
    private DisconnectionReason disconnectionReason;
    // behaviours
    private final DataEncodingConfigsWrapper dataEncoding;
    private final HeartBeatConfigsWrapper heartBeat;
    private final ByeMsgConfigsWrapper byeMsg;
    private final AutoReConnectConfigsWrapper autoReConnect;
    // Peer's Listeners
    private final List<PeerConnectionListener> listenersConnection = new ArrayList<>();
    private final List<PeerDataListener> listenersData = new ArrayList<>();


    // Constructors

    public ClientWrapper(String localId, String remoteId, String protoName) {
        this(localId, remoteId, protoName, null);
    }

    public ClientWrapper(String localId, String remoteId, String protoName, PeerConnectionListener listenerConnection) {
        this(localId, remoteId, protoName, listenerConnection,
                DataEncodingConfigs.CHARSET, DataEncodingConfigs.DELIMITER,
                HeartBeatConfigs.TIMEOUT_MS, HeartBeatConfigs.TIMEOUT_HB_MS, HeartBeatConfigs.ENABLE_HB_RES,
                ByeMsgConfigs.ENABLE, ByeMsgConfigs.BYE_MSG);
    }

    public ClientWrapper(String localId, String remoteId, String protoName,
                         Charset charset, byte[] delimiter,
                         int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                         Boolean enableByeMsg, byte[] byeMsg) {
        this(localId, remoteId, protoName, null,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
    }

    public ClientWrapper(String localId, String remoteId, String protoName, PeerConnectionListener listenerConnection,
                         Charset charset, byte[] delimiter,
                         int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                         Boolean enableByeMsg, byte[] byeMsg) {
        this.localId = localId;
        this.remoteId = remoteId;

        if (charset == null) charset = DataEncodingConfigs.CHARSET;
        if (hbTimeoutMs < 0) hbTimeoutMs = HeartBeatConfigs.TIMEOUT_MS;
        if (hbTimeoutHBMs < 0) hbTimeoutHBMs = HeartBeatConfigs.TIMEOUT_HB_MS;
        if (enableHBRes == null) enableHBRes = HeartBeatConfigs.ENABLE_HB_RES;
        if (enableByeMsg == null) enableByeMsg = ByeMsgConfigs.ENABLE;

        if (delimiter != null) dataEncoding = new DataEncodingConfigsWrapper(this, charset, delimiter);
        else dataEncoding = new DataEncodingConfigsWrapper(this, charset, DataEncodingConfigs.DELIMITER);
        this.heartBeat = new HeartBeatConfigsWrapper(this, hbTimeoutMs, hbTimeoutHBMs, enableHBRes);
        if (byeMsg != null) this.byeMsg = new ByeMsgConfigsWrapper(this, dataEncoding, enableByeMsg, byeMsg);
        else this.byeMsg = new ByeMsgConfigsWrapper(this, dataEncoding, enableByeMsg, ByeMsgConfigs.BYE_MSG);
        this.autoReConnect = new AutoReConnectConfigsWrapper(this /*, boolean enable*/);

        this.connectionInfo = new ConnectionInfoDefault(this, localId, remoteId, protoName);
        this.disconnectionReason = DisconnectionReason.NOT_DISCONNECTED;

        if (listenerConnection != null)
            listenersConnection.add(listenerConnection);

        //addListener(new PeerAbs.PeerConnectionLogger("ClientWrapper"));
    }

    public ClientWrapper(String localId, String remoteId, String protoName,
                         Charset charset, String delimiter,
                         int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                         Boolean enableByeMsg, String byeMsg) {
        this(localId, remoteId, protoName, null,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
    }

    public ClientWrapper(String localId, String remoteId, String protoName, PeerConnectionListener listenerConnection,
                         Charset charset, String delimiter,
                         int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                         Boolean enableByeMsg, String byeMsg) {
        this.localId = localId;
        this.remoteId = remoteId;

        if (charset == null) charset = DataEncodingConfigs.CHARSET;
        if (delimiter == null) delimiter = DataEncodingConfigs.DELIMITER;
        if (hbTimeoutMs < 0) hbTimeoutMs = HeartBeatConfigs.TIMEOUT_MS;
        if (hbTimeoutHBMs < 0) hbTimeoutHBMs = HeartBeatConfigs.TIMEOUT_HB_MS;
        if (enableHBRes == null) enableHBRes = HeartBeatConfigs.ENABLE_HB_RES;
        if (enableByeMsg == null) enableByeMsg = ByeMsgConfigs.ENABLE;
        if (byeMsg == null) byeMsg = ByeMsgConfigs.BYE_MSG;

        this.dataEncoding = new DataEncodingConfigsWrapper(this, charset, delimiter);
        this.heartBeat = new HeartBeatConfigsWrapper(this, hbTimeoutMs, hbTimeoutHBMs, enableHBRes);
        this.byeMsg = new ByeMsgConfigsWrapper(this, dataEncoding, enableByeMsg, byeMsg);
        this.autoReConnect = new AutoReConnectConfigsWrapper(this /*, boolean enable*/);

        this.connectionInfo = new ConnectionInfoDefault(this, localId, remoteId, protoName);
        this.disconnectionReason = DisconnectionReason.NOT_DISCONNECTED;

        if (listenerConnection != null)
            listenersConnection.add(listenerConnection);

        //addListener(new PeerAbs.PeerConnectionLogger("ClientWrapper"));
    }


    // toString()

    @Override
    public String toString() {
        return getConnectionInfo().toString();
    }


    // Wrapper methods

    public Client getWrapper() {
        return wrapper;
    }

    protected void setWrapper(Client newWrapper, @SuppressWarnings("SameParameterValue") boolean inheritFromWrapper) {
        if (wrapper != null) {
            if (wrapper.getState().isConnected()
                    || wrapper.getState().isDisconnecting()) {
                try {
                    wrapper.disconnect();
                } catch (PeerDisconnectionException ignore) { /* ignored because focus on update wrapper */}
            }

            disconnectionReason = wrapper.getDisconnectionReason();

            for (PeerConnectionListener l : listenersConnection)
                wrapper.removeListener(l);
            for (PeerDataListener l : listenersData)
                wrapper.removeListener(l);
        }

        wrapper = newWrapper;

        if (wrapper != null) {
            if (inheritFromWrapper) {
                wrapper.getDataEncodingConfigs().setCharset(dataEncoding.getCharset());
                wrapper.getDataEncodingConfigs().setDelimiter(dataEncoding.getDelimiter());
                wrapper.getDataEncodingConfigs().setDelimiter(dataEncoding.getDelimiterString());

                wrapper.getHeartBeatConfigs().setTimeout(heartBeat.getTimeout());
                wrapper.getHeartBeatConfigs().setHBTimeout(heartBeat.getHBTimeout());
                wrapper.getHeartBeatConfigs().enableHBResponse(heartBeat.isHBResponseEnabled());

                wrapper.getByeConfigs().enable(byeMsg.isEnable());
                wrapper.getByeConfigs().setByeMsg(byeMsg.getByeMsg());
                wrapper.getByeConfigs().setByeMsg(byeMsg.getByeMsgString());

                wrapper.getAutoReConnectConfigs().enable(autoReConnect.isEnable());
                wrapper.getAutoReConnectConfigs().setDelay(autoReConnect.getDelay());
            }

            for (PeerConnectionListener l : listenersConnection)
                wrapper.addListener(l);
            for (PeerDataListener l : listenersData)
                wrapper.addListener(l);
        }
    }

    protected void resetWrapper() {
        setWrapper(null, false);
    }


    // Getters

    @Override
    public ConnectionState getState() {
        return wrapper != null ? wrapper.getState() : connectionInfo.getState();
    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        return wrapper != null ? wrapper.getConnectionInfo() : connectionInfo;
    }

    @Override
    public DisconnectionReason getDisconnectionReason() {
        return disconnectionReason;
    }

    @Override
    public String getLocalId() {
        return wrapper != null ? wrapper.getLocalId() : localId;
    }

    @Override
    public String getRemoteId() {
        return wrapper != null ? wrapper.getRemoteId() : remoteId;
    }

    @Override
    public Socket getSocket() {
        return wrapper != null ? wrapper.getSocket() : null;
    }


    // Client connection methods

    @Override
    public void connect() throws PeerConnectionException {
        if (wrapper == null)
            throw new PeerConnectionException(this, String.format("Error on Peer '%s' connection because wrapped client not set", this));

        if (getState().isConnected())
            return;

        if (getState().isConnecting())
            return;

        doConnect();
    }

    // emitOnConnecting
    // on wrapper
    //   emitOnConnecting_Waiting
    //   emitOnDisconnect
    protected void doConnect() throws PeerConnectionException {
        if (wrapper == null) {
            JavaAssertions.makeAssertion_Failed("Can't call clientWrapper::doConnect() with null wrapper");
            return;
        }

        if (getState().isConnected()) {
            JavaAssertions.makeAssertion_Failed("Can't call clientWrapper::doConnect() when already connected");
            return;
        }

        if (!getState().isConnecting())
            emitOnConnecting();

        wrapper.connect();
    }

    @Override
    public void disconnect() throws PeerDisconnectionException {
        if (wrapper == null)
            throw new PeerDisconnectionException(this, String.format("Error on Peer '%s' disconnection because wrapped client not set", this));

        if (getState().isDisconnected())
            return;

        if (getState().isDisconnecting())
            return;

        doDisconnect();
    }

    // emitOnDisconnecting
    // emitOnDisconnect   via wrapper.disconnect() -> closeSocket ...> PeerInfLoop ..> wrapper's listeners
    protected void doDisconnect() throws PeerDisconnectionException {
        if (wrapper == null) {
            JavaAssertions.makeAssertion_Failed("Can't call clientWrapper::doDisconnect() with null wrapper");
            return;
        }

        if (getState().isDisconnected()) {
            JavaAssertions.makeAssertion_Failed("Can't call clientWrapper::doConnect() when already disconnected");
            return;
        }

        if (!getState().isDisconnecting())
            emitOnDisconnecting();

        wrapper.disconnect();
    }


    // Messages methods

    @Override
    public void sendData(byte[] data) throws PeerNotConnectedException, PeerStreamException {
        if (wrapper == null)
            throw new PeerNotConnectedException(this, String.format("Error on Peer '%s' sendData because wrapped client not set", this));

        wrapper.sendData(data);
    }

    @Override
    public void sendData(String data) throws PeerNotConnectedException, PeerStreamException {
        if (wrapper == null)
            throw new PeerNotConnectedException(this, String.format("Error on Peer '%s' sendData because wrapped client not set", this));

        wrapper.sendData(data);
    }


    // Behaviours configs

    @Override
    public DataEncodingConfigs getDataEncodingConfigs() {
        return dataEncoding;
    }

    @Override
    public ByeMsgConfigs getByeConfigs() {
        return byeMsg;
    }

    @Override
    public HeartBeatConfigs getHeartBeatConfigs() {
        return heartBeat;
    }

    @Override
    public AutoReConnectConfigs getAutoReConnectConfigs() {
        return autoReConnect;
    }


    // Listeners

    @Override
    public void addListener(PeerConnectionListener listener) {
        listenersConnection.add(listener);
        if (wrapper != null)
            wrapper.addListener(listener);
    }

    @Override
    public void removeListener(PeerConnectionListener listener) {
        listenersConnection.remove(listener);
        if (wrapper != null)
            wrapper.removeListener(listener);
    }

    protected void emitOnConnecting() {
        log.info(String.format("ClientWrapper '%s' connecting...", getLocalId()));

        connectionInfo.updateOnConnecting();
        JavaListeners.emitter(this, listenersConnection, "onConnecting", new JavaListeners.ListenerMapper<PeerConnectionListener>() {
            @Override
            public void map(PeerConnectionListener l) {
                l.onConnecting(ClientWrapper.this);
            }
        });
    }

    protected void emitOnConnecting_Waiting() {
        log.info(String.format("ClientWrapper '%s' waiting to connect...", getLocalId()));

        connectionInfo.updateOnConnecting_Waiting();
        JavaListeners.emitter(this, listenersConnection, "onWaiting", new JavaListeners.ListenerMapper<PeerConnectionListener>() {
            @Override
            public void map(PeerConnectionListener l) {
                l.onWaiting(ClientWrapper.this);
            }
        });
    }

    protected void emitOnConnect() {            // ToDO: Not throw because onConnect was emit only by wrapped client
        log.info(String.format("ClientWrapper '%s' connected", getLocalId()));

        connectionInfo.updateOnConnected();
        JavaListeners.emitter(this, listenersConnection, "onConnect", new JavaListeners.ListenerMapper<PeerConnectionListener>() {
            @Override
            public void map(PeerConnectionListener l) {
                l.onConnect(ClientWrapper.this);
            }
        });
    }

    protected void emitOnDisconnecting() {
        log.info(String.format("ClientWrapper '%s' disconnecting...", getLocalId()));

        connectionInfo.updateOnDisconnecting();
        JavaListeners.emitter(this, listenersConnection, "onDisconnecting", new JavaListeners.ListenerMapper<PeerConnectionListener>() {
            @Override
            public void map(PeerConnectionListener l) {
                l.onDisconnecting(ClientWrapper.this);
            }
        });
    }

    protected void emitOnDisconnect() {         // ToDO: Not throw because onDisconnect was emit only by wrapped client
        log.info(String.format("ClientWrapper '%s' disconnected", getLocalId()));

        connectionInfo.updateOnDisconnected();
        JavaListeners.emitter(this, listenersConnection, "onDisconnect", new JavaListeners.ListenerMapper<PeerConnectionListener>() {
            @Override
            public void map(PeerConnectionListener l) {
                l.onDisconnect(ClientWrapper.this);
            }
        });
    }

    protected void emitOnFail(String failMsg) {
        emitOnFail(failMsg, null);
    }

    protected void emitOnFail(String failMsg, Throwable exception) {
        log.warn(String.format("ClientWrapper '%s' failed: '%s'", getLocalId(), failMsg));

        Throwable finalException = exception != null ? exception : new Throwable(failMsg);
        JavaListeners.emitter(this, listenersConnection, "onFail", new JavaListeners.ListenerMapper<PeerConnectionListener>() {
            @Override
            public void map(PeerConnectionListener l) {
                l.onFail(ClientWrapper.this, failMsg, finalException);
            }
        });
    }

    @Override
    public void addListener(PeerDataListener listener) {
        listenersData.add(listener);
        if (wrapper != null)
            wrapper.addListener(listener);
    }

    @Override
    public void removeListener(PeerDataListener listener) {
        listenersData.remove(listener);
        if (wrapper != null)
            wrapper.removeListener(listener);
    }

}
