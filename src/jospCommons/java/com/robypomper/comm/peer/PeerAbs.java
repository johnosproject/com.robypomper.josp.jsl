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

package com.robypomper.comm.peer;

import com.robypomper.comm.behaviours.*;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.configs.DataEncodingConfigsDefault;
import com.robypomper.comm.connection.ConnectionInfo;
import com.robypomper.comm.connection.ConnectionInfoDefault;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerException;
import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaByteArrays;
import com.robypomper.java.JavaListeners;
import com.robypomper.java.JavaThreads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// ConnectionInfo
// Disconnect
// SendData
// InfiniteLoop
// BehavioursConfigs (DataEncoding, ByeMsg, HB)
// Listeners (Connection, Data)
public abstract class PeerAbs implements Peer {

    // Class constants

    public static final String TH_INF_LOOP_NAME = "PEER_INF_LOOP";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(PeerAbs.class);
    // connection
    private final ConnectionInfoDefault connectionInfo;
    private DisconnectionReason disconnectionReason;
    // behaviours
    private final DataEncodingConfigsDefault dataEncoding;
    private final HeartBeatImpl heartBeat;
    private final ByeMsgImpl byeMsg;
    // Only when connected
    private Thread processingThread = null;
    private DataOutputStream toRemote = null;
    private DataInputStream fromRemote = null;
    // Peer's Listeners
    private final List<PeerConnectionListener> listenersConnection = new ArrayList<>();
    private final List<PeerDataListener> listenersData = new ArrayList<>();


    // Constructors

    protected PeerAbs(String localId, String remoteId, String protoName) {
        this(localId, remoteId, protoName, null);
    }

    protected PeerAbs(String localId, String remoteId, String protoName, PeerConnectionListener listenerConnection) {
        this(localId, remoteId, protoName, listenerConnection,
                DataEncodingConfigs.CHARSET, DataEncodingConfigs.DELIMITER,
                HeartBeatConfigs.TIMEOUT_MS, HeartBeatConfigs.TIMEOUT_HB_MS, HeartBeatConfigs.ENABLE_HB_RES,
                ByeMsgConfigs.ENABLE, ByeMsgConfigs.BYE_MSG);
    }

    protected PeerAbs(String localId, String remoteId, String protoName,
                      Charset charset, byte[] delimiter,
                      int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                      Boolean enableByeMsg, byte[] byeMsg) {
        this(localId, remoteId, protoName, null,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
    }

    protected PeerAbs(String localId, String remoteId, String protoName, PeerConnectionListener listenerConnection,
                      Charset charset, byte[] delimiter,
                      int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                      Boolean enableByeMsg, byte[] byeMsg) {
        if (charset == null) charset = DataEncodingConfigs.CHARSET;
        if (hbTimeoutMs < 0) hbTimeoutMs = HeartBeatConfigs.TIMEOUT_MS;
        if (hbTimeoutHBMs < 0) hbTimeoutHBMs = HeartBeatConfigs.TIMEOUT_HB_MS;
        if (enableHBRes == null) enableHBRes = HeartBeatConfigs.ENABLE_HB_RES;
        if (enableByeMsg == null) enableByeMsg = ByeMsgConfigs.ENABLE;

        if (delimiter != null) dataEncoding = new DataEncodingConfigsDefault(charset, delimiter);
        else dataEncoding = new DataEncodingConfigsDefault(charset, DataEncodingConfigs.DELIMITER);
        heartBeat = new HeartBeatDefault(this, hbTimeoutMs, hbTimeoutHBMs, enableHBRes);
        heartBeat.addListener(heartBeatListener);
        if (byeMsg != null) this.byeMsg = new ByeMsgDefault(this, dataEncoding, enableByeMsg, byeMsg);
        else this.byeMsg = new ByeMsgDefault(this, dataEncoding, enableByeMsg, ByeMsgConfigs.BYE_MSG);

        connectionInfo = new ConnectionInfoDefault(this, localId, remoteId, protoName);
        disconnectionReason = DisconnectionReason.NOT_DISCONNECTED;

        if (listenerConnection != null)
            addListener(listenerConnection);

        //addListener(new PeerConnectionLogger("PeerAbs"));
    }

    protected PeerAbs(String localId, String remoteId, String protoName,
                      Charset charset, String delimiter,
                      int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                      Boolean enableByeMsg, String byeMsg) {
        this(localId, remoteId, protoName, null,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
    }

    protected PeerAbs(String localId, String remoteId, String protoName, PeerConnectionListener listenerConnection,
                      Charset charset, String delimiter,
                      int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                      Boolean enableByeMsg, String byeMsg) {
        if (charset == null) charset = DataEncodingConfigs.CHARSET;
        if (delimiter == null) delimiter = DataEncodingConfigs.DELIMITER;
        if (hbTimeoutMs < 0) hbTimeoutMs = HeartBeatConfigs.TIMEOUT_MS;
        if (hbTimeoutHBMs < 0) hbTimeoutHBMs = HeartBeatConfigs.TIMEOUT_HB_MS;
        if (enableHBRes == null) enableHBRes = HeartBeatConfigs.ENABLE_HB_RES;
        if (enableByeMsg == null) enableByeMsg = ByeMsgConfigs.ENABLE;
        if (byeMsg == null) byeMsg = ByeMsgConfigs.BYE_MSG;
        dataEncoding = new DataEncodingConfigsDefault(charset, delimiter);
        heartBeat = new HeartBeatDefault(this, hbTimeoutMs, hbTimeoutHBMs, enableHBRes);
        heartBeat.addListener(heartBeatListener);
        this.byeMsg = new ByeMsgDefault(this, dataEncoding, enableByeMsg, byeMsg);

        disconnectionReason = DisconnectionReason.NOT_DISCONNECTED;
        connectionInfo = new ConnectionInfoDefault(this, localId, remoteId, protoName);

        if (listenerConnection != null)
            addListener(listenerConnection);

        //addListener(new PeerConnectionLogger("PeerAbs"));
    }


    // toString()

    @Override
    public String toString() {
        return connectionInfo.toString();
    }


    // Getters

    @Override
    public ConnectionState getState() {
        return connectionInfo.getState();
    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    @Override
    public DisconnectionReason getDisconnectionReason() {
        return disconnectionReason;
    }

    protected void setDisconnectionReason(DisconnectionReason reason) {
        disconnectionReason = reason;
    }

    @Override
    public String getLocalId() {
        return connectionInfo.getLocalInfo().getId();
    }

    @Override
    public String getRemoteId() {
        return connectionInfo.getRemoteInfo().getId();
    }


    // Connection methods

    @Override
    public void disconnect() throws PeerDisconnectionException {
        if (getState().isDisconnected())
            return;

        if (getState().isDisconnecting())
            return;

        doDisconnect(true, false);
    }

    protected void doDisconnect(boolean localRequest, boolean remoteRequest) throws PeerDisconnectionException {
        if (getState().isDisconnected()) {
            JavaAssertions.makeAssertion_Failed("Can't call PeerAbs::doDisconnect() with null wrapper");
            return;
        }

        if (!getState().isDisconnecting())
            emitOnDisconnecting();

        if (localRequest && byeMsg.isEnable()) {
            try {
                byeMsg.sendByeMsg();
            } catch (PeerException e) {
                JavaAssertions.makeWarning_Failed(e, String.format("Error disconnecting '%s' peer because can't send bye msg.", this));
            }
        }

        if (getSocket() != null) {
            closeSocket();
        }

        JavaAssertions.makeWarning(processingThread != null, String.format("Instance 'Peer'(%s) var 'processingThread''s of his processing thread can NOT be null on peer disconnection.", this));
        if (processingThread != null)
            processingThread.interrupt();
    }

    protected abstract void closeSocket() throws PeerDisconnectionException;

    protected void startupConnection() {
        try {
            toRemote = new DataOutputStream(getSocket().getOutputStream());
            fromRemote = new DataInputStream(getSocket().getInputStream());

        } catch (IOException e) {
            emitOnFail("Error open socket streams", e);
            return;
        }

        processingThread = JavaThreads.initAndStart(new PeerInfiniteLoop(), TH_INF_LOOP_NAME, getLocalId());
    }

    private class PeerInfiniteLoop implements Runnable {

        @Override
        public void run() {
            emitOnConnect();

            Exception ex = null;
            boolean receivedByeMsg = false;
            boolean errorOnDataRead = false;
            boolean errorOnRemotePeerConnetion = false;

            byte[] dataRead;
            byte[] dataBuffered = new byte[0];

            while (true) {
                // Read
                try {
                    byte[][] dataRead_dataBuffered = listenForData(dataBuffered);
                    dataRead = dataRead_dataBuffered[0];
                    dataBuffered = dataRead_dataBuffered[1];
                    emitOnDataRx(dataRead);

                } catch (IOException e) {
                    // depending on exception throw, the connection was closed
                    // by local/remote peer or because connection lost/error
                    ex = e;

                    // Local causes
                    if (e instanceof SocketException && (e.getMessage().equals("Socket closed")         // Socket
                            || e.getMessage().equals("Socket is closed")))                              // SSLSocket
                        break;                          // Local disconnection via disconnect() method (this case is executed also when called socket.close()....
                    if (e.getMessage().equals("Stream closed.")) {
                        log.trace("(WARNING: local disconnection request WRONG WAY, use Peer.disconnect() method instead direct call to internalSocket.close())");
                        break;                          // Local disconnection via socket.close() method (NB!: wrong way)
                    }

                    // Remote causes
                    if (e.getMessage().equals("Can't read data because reached end of stream")) {
                        errorOnDataRead = true;
                        emitOnFail("Error reading data", e);
                        break;                          // Remote disconnection without ByeMsg (NB!: wrong/error way)
                    }

                    if (e instanceof SocketTimeoutException && e.getMessage().equals("Read timed out")) {
                        if (!heartBeat.isWaiting()) {
                            try {
                                heartBeat.sendHeartBeatReq();
                            } catch (PeerException e2) {
                                errorOnDataRead = true;
                                emitOnFail("Error can't send heartbeat", e2);
                                break;
                            }
                            continue;
                        }
                        errorOnDataRead = true;
                        emitOnFail("Error heartbeat timeout", e);
                        break;
                    }

                    // Other causes
                    if (e instanceof SSLException && e.getMessage().contains("the trustAnchors parameter must be non-empty")) {
                        errorOnRemotePeerConnetion = true;
                        break;
                    }
                    if (e instanceof SSLException && e.getMessage().contains("Received fatal alert: internal_error")) {
                        errorOnRemotePeerConnetion = true;      // Remote disconnection because local peer do NOT have remote certificate
                        break;
                    }

                    JavaAssertions.makeWarning_Failed(e, String.format("Exception on Peer '%s''s processing data thread not managed.", this));
                    errorOnDataRead = true;
                    emitOnFail("Error reading data", e);
                    break;
                }

                // Process HeartBeat
                try {
                    if (heartBeat.processHeartBeatMsg(dataRead))
                        continue;                                           // Processed HB request or response, continue infinite loop

                } catch (PeerException e) {
                    emitOnFail("Error send HB response", e);
                    continue;
                }

                // Process ByeMsg
                if (byeMsg.processByeMsg(dataRead)) {
                    receivedByeMsg = true;
                    break;
                }

                // Process
                try {
                    if (!processData(dataRead)) {
                        String dataReadStr = new String(dataRead, getDataEncodingConfigs().getCharset());
                        if (!processData(dataReadStr))
                            emitOnFail(String.format("Received unknown data '%s'", dataReadStr));
                    }
                } catch (Throwable e) {
                    String dataReadStr = new String(dataRead, getDataEncodingConfigs().getCharset());
                    emitOnFail(String.format("Error occurred processing data '%s'", dataReadStr), e);
                }
            }

            // Determinate why terminated:
            String info;
            if (receivedByeMsg) {
                setDisconnectionReason(DisconnectionReason.REMOTE_REQUEST);
                info = String.format("Peer '%s' disconnect to '%s' because remote peer request", getLocalId(), getRemoteId());
            } else if (errorOnRemotePeerConnetion) {
                setDisconnectionReason(DisconnectionReason.REMOTE_ERROR);
                info = String.format("Peer '%s' disconnect to '%s' because remote peer error", getLocalId(), getRemoteId());
            } else if (errorOnDataRead) {
                setDisconnectionReason(DisconnectionReason.CONNECTION_LOST);
                info = String.format("Peer '%s' disconnect to '%s' because lost connection", getLocalId(), getRemoteId());
            } else {
                setDisconnectionReason(DisconnectionReason.LOCAL_REQUEST);
                info = String.format("Peer '%s' disconnect to '%s' because local peer request", getLocalId(), getRemoteId());
                if (heartBeat.isTimeoutExpired()) {
                    setDisconnectionReason(DisconnectionReason.HEARTBEAT_TIMEOUT);
                    info += " caused by HeartBeat timeout";
                }
            }

            if (ex == null)
                log.trace(info);
            else
                log.trace(info + String.format(" [%s] %s", ex.getClass().getSimpleName(), ex.getMessage()));

            toRemote = null;
            fromRemote = null;
            processingThread = null;
            emitOnDisconnect();
        }

    }

    private byte[][] listenForData(byte[] dataBuffered) throws IOException {
        byte[] dataRead = new byte[0];

        while (dataRead.length == 0) {
            int bytesRead = 0;

            // Get buffered data
            if (dataBuffered.length > 0) {
                dataRead = dataBuffered;
                bytesRead = dataBuffered.length;
                dataBuffered = new byte[0];
            }

            // Read data
            int bytesReadTmp = 0;
            while (bytesReadTmp != -1 && !(JavaByteArrays.contains(dataRead, getDataEncodingConfigs().getDelimiter()))) {
                int available = fromRemote.available();
                byte[] dataRead_More = new byte[available > 0 ? (Math.min(available, 1024)) : 1];
                bytesReadTmp = fromRemote.read(dataRead_More);
                bytesRead += bytesReadTmp;
                dataRead_More = JavaByteArrays.trim(dataRead_More);
                dataRead = JavaByteArrays.append(dataRead, dataRead_More);
            }

            // Check if disconnected by remote peer
            if (bytesReadTmp == -1) {
                if (bytesRead > 0)
                    throw new IOException("Incomplete data received but remote peer disconnected, data will discharged");
                throw new IOException("Can't read data because reached end of stream");
            }

            if (JavaByteArrays.contains(dataRead, getDataEncodingConfigs().getDelimiter())) {
                dataBuffered = JavaByteArrays.after(dataRead, getDataEncodingConfigs().getDelimiter());
                dataRead = JavaByteArrays.before(dataRead, getDataEncodingConfigs().getDelimiter());
            }
        }

        byte[][] ret = new byte[2][];
        ret[0] = dataRead;
        ret[1] = dataBuffered;
        return ret;
    }


    // Messages methods

    @Override
    public void sendData(byte[] data) throws PeerNotConnectedException, PeerStreamException {
        if (getState().isDisconnected())
            throw new PeerNotConnectedException(this);

        try {
            toRemote.write(data);
            toRemote.write(getDataEncodingConfigs().getDelimiter());


        } catch (NullPointerException e) {
            if (Arrays.equals(getByeConfigs().getByeMsg(), data))
                return;
            throw e;

        } catch (IOException e) {
            throw new PeerStreamException(this, toRemote, e);
        }

        emitOnDataTx(data);
    }

    @Override
    public void sendData(String data) throws PeerNotConnectedException, PeerStreamException {
        sendData(data.getBytes(getDataEncodingConfigs().getCharset()));
    }

    protected abstract boolean processData(byte[] data);

    protected abstract boolean processData(String data);


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


    // Listeners

    @Override
    public void addListener(PeerConnectionListener listener) {
        synchronized (listenersConnection) {
            listenersConnection.add(listener);
        }
    }

    @Override
    public void removeListener(PeerConnectionListener listener) {
        synchronized (listenersConnection) {
            listenersConnection.remove(listener);
        }
    }

    protected void emitOnConnecting() {
        log.info(String.format("Peer '%s' connecting...", getLocalId()));

        connectionInfo.updateOnConnecting();
        JavaListeners.emitter(this, listenersConnection, "onConnecting", new JavaListeners.ListenerMapper<PeerConnectionListener>() {
            @Override
            public void map(PeerConnectionListener l) {
                l.onConnecting(PeerAbs.this);
            }
        });
    }

    protected void emitOnConnecting_Waiting() {
        log.info(String.format("Peer '%s' waiting to connect...", getLocalId()));

        connectionInfo.updateOnConnecting_Waiting();
        JavaListeners.emitter(this, listenersConnection, "onWaiting", new JavaListeners.ListenerMapper<PeerConnectionListener>() {
            @Override
            public void map(PeerConnectionListener l) {
                l.onWaiting(PeerAbs.this);
            }
        });
    }

    protected void emitOnConnect() {
        log.info(String.format("Peer '%s' connected", getLocalId()));

        connectionInfo.updateOnConnected();
        JavaListeners.emitter(this, listenersConnection, "onConnect", new JavaListeners.ListenerMapper<PeerConnectionListener>() {
            @Override
            public void map(PeerConnectionListener l) {
                l.onConnect(PeerAbs.this);
            }
        });
    }

    protected void emitOnDisconnecting() {
        log.info(String.format("Peer '%s' disconnecting...", getLocalId()));

        connectionInfo.updateOnDisconnecting();
        JavaListeners.emitter(this, listenersConnection, "onDisconnecting", new JavaListeners.ListenerMapper<PeerConnectionListener>() {
            @Override
            public void map(PeerConnectionListener l) {
                l.onDisconnecting(PeerAbs.this);
            }
        });
    }

    protected void emitOnDisconnect() {
        connectionInfo.updateOnDisconnected();

        if (getDisconnectionReason() == DisconnectionReason.NOT_DISCONNECTED)
            return;

        log.info(String.format("Peer '%s' disconnected (reason: %s)", getLocalId(), getDisconnectionReason()));

        JavaListeners.emitter(this, listenersConnection, "onDisconnect", new JavaListeners.ListenerMapper<PeerConnectionListener>() {
            @Override
            public void map(PeerConnectionListener l) {
                l.onDisconnect(PeerAbs.this);
            }
        });
    }

    protected void emitOnFail(String failMsg) {
        emitOnFail(failMsg, null);
    }

    protected void emitOnFail(String failMsg, Throwable exception) {
        log.warn(String.format("Peer '%s' failed '%s'", getLocalId(), failMsg));

        Throwable finalException = exception != null ? exception : new Throwable(failMsg);
        JavaListeners.emitter(this, listenersConnection, "onFail", new OnFailListenerMapper(PeerAbs.this, failMsg, finalException));
    }

    private static class OnFailListenerMapper implements JavaListeners.ListenerMapper<PeerConnectionListener> {

        private final PeerAbs peerAbs;
        private final String failMsg;
        private final Throwable finalException;

        public OnFailListenerMapper(PeerAbs peerAbs, String failMsg, Throwable finalException) {
            this.peerAbs = peerAbs;
            this.failMsg = failMsg;
            this.finalException = finalException;
        }

        @Override
        public void map(PeerConnectionListener l) {
            l.onFail(peerAbs, failMsg, finalException);
        }

    }

    @Override
    public void addListener(PeerDataListener listener) {
        listenersData.add(listener);
    }

    @Override
    public void removeListener(PeerDataListener listener) {
        listenersData.remove(listener);
    }

    protected void emitOnDataRx(byte[] data) {
        log.debug(String.format("Peer '%s' rx data '%s'", getLocalId(), new String(data, getDataEncodingConfigs().getCharset()).replace("\n", "\\n")));

        JavaListeners.emitter(this, listenersData, "onDataRx", new JavaListeners.ListenerMapper<PeerDataListener>() {
            @Override
            public void map(PeerDataListener l) {
                l.onDataRx(PeerAbs.this, data);
            }
        });
    }

    protected void emitOnDataTx(byte[] data) {
        log.debug(String.format("Peer '%s' tx data '%s'", getLocalId(), new String(data, getDataEncodingConfigs().getCharset()).replace("\n", "\\n")));

        JavaListeners.emitter(this, listenersData, "onDataTx", new JavaListeners.ListenerMapper<PeerDataListener>() {
            @Override
            public void map(PeerDataListener l) {
                l.onDataTx(PeerAbs.this, data);
            }
        });
    }


    // Updates listeners

    HeartBeatListener heartBeatListener = new HeartBeatListener() {

        @Override
        public void onSend(Peer peer, HeartBeatConfigs hb) {
        }

        @Override
        public void onSuccess(Peer peer, HeartBeatConfigs hb) {
        }

        @Override
        public void onFail(Peer peer, HeartBeatConfigs hb) {
            try {
                emitOnFail("HB reached timeout");
                doDisconnect(false, false);
            } catch (PeerException ignore) { /* ignored because focus on local disconnection */ }
        }

    };


    // Disconnection reason

    public static class PeerConnectionLogger implements PeerConnectionListener {

        String classHolder;

        public PeerConnectionLogger(String classHolderName) {
            this.classHolder = classHolderName;
        }

        @Override
        public void onConnecting(Peer peer) {
            System.out.println();
            System.out.printf("%d::onConnecting(%s) <= %s%n", this.hashCode(), peer, classHolder);
            System.out.println();
        }

        @Override
        public void onWaiting(Peer peer) {
            System.out.println();
            System.out.printf("%d::onWaiting(%s) <= %s%n", this.hashCode(), peer, classHolder);
            System.out.println();
        }

        @Override
        public void onConnect(Peer peer) {
            System.out.println();
            System.out.printf("%d::onConnect(%s) <= %s%n", this.hashCode(), peer, classHolder);
            System.out.println();
        }

        @Override
        public void onDisconnecting(Peer peer) {
            System.out.println();
            System.out.printf("%d::onDisconnecting(%s) <= %s%n", this.hashCode(), peer, classHolder);
            System.out.println();
        }

        @Override
        public void onDisconnect(Peer peer) {
            System.out.println();
            System.out.printf("%d::onDisconnect([%d] %s) %S <= disconnect reason: %s%n", this.hashCode(), peer.hashCode(), peer, peer.getDisconnectionReason(), classHolder);
            System.out.println();
        }

        @Override
        public void onFail(Peer peer, String failMsg, Throwable exception) {
            System.out.println();
            System.out.printf("%d::onFail(%s) <= %s%n", this.hashCode(), peer, classHolder);
            System.out.println();
        }

    }

}
