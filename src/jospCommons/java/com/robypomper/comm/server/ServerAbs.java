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
import com.robypomper.comm.behaviours.ByeMsgConfigsServer;
import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.behaviours.HeartBeatConfigsServer;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.configs.DataEncodingConfigsServer;
import com.robypomper.comm.exception.*;
import com.robypomper.comm.peer.*;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaListeners;
import com.robypomper.java.JavaThreads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class ServerAbs implements Server {

    // Class constants

    public static final String TH_INF_LOOP_NAME = "SRV_INF_LOOP";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(ServerAbs.class);
    private ServerState state = ServerState.STOPPED;
    private ServerSocket serverSocket;
    private final PeerInfoLocalDefault localInfo;
    private final DataEncodingConfigsServer dataEncoding;
    private final HeartBeatConfigsServer heartBeat;
    private final ByeMsgConfigsServer byeMsg;
    private final String protoName;
    // Bind's endpoint
    private final InetAddress bindAddr;
    private final int bindPort;
    // ServerClient's
    private final List<ServerClient> serverClients = new ArrayList<>();
    //
    private Thread infLoopTh;
    // Peer's Listeners
    private final List<ServerStateListener> listenersStatus = new ArrayList<>();
    private final List<ServerClientsListener> listenersClient = new ArrayList<>();
    private final List<ServerDataListener> listenersData = new ArrayList<>();


    // Constructors

    protected ServerAbs(String localId, int bindPort, String protoName) {
        this(localId, null, bindPort, protoName);
    }

    protected ServerAbs(String localId, InetAddress bindAddr, int bindPort, String protoName) {
        this(localId, bindAddr, bindPort, protoName,
                DataEncodingConfigs.CHARSET, DataEncodingConfigs.DELIMITER,
                HeartBeatConfigs.TIMEOUT_MS, HeartBeatConfigs.TIMEOUT_HB_MS, HeartBeatConfigs.ENABLE_HB_RES,
                ByeMsgConfigs.ENABLE, ByeMsgConfigs.BYE_MSG);
    }

    protected ServerAbs(String localId, InetAddress bindAddr, int bindPort, String protoName,
                        Charset charset, byte[] delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, byte[] byeMsg) {
        localInfo = new PeerInfoLocalDefault(localId, protoName);             // to updateOnConnected and updateOnDisconnected

        if (charset == null) charset = DataEncodingConfigs.CHARSET;
        if (hbTimeoutMs < 0) hbTimeoutMs = HeartBeatConfigs.TIMEOUT_MS;
        if (hbTimeoutHBMs < 0) hbTimeoutHBMs = HeartBeatConfigs.TIMEOUT_HB_MS;
        if (enableHBRes == null) enableHBRes = HeartBeatConfigs.ENABLE_HB_RES;
        if (enableByeMsg == null) enableByeMsg = ByeMsgConfigs.ENABLE;
        if (delimiter != null) dataEncoding = new DataEncodingConfigsServer(this, charset, delimiter);
        else dataEncoding = new DataEncodingConfigsServer(this, charset, DataEncodingConfigs.DELIMITER);
        heartBeat = new HeartBeatConfigsServer(this, hbTimeoutMs, hbTimeoutHBMs, enableHBRes);
        if (byeMsg != null) this.byeMsg = new ByeMsgConfigsServer(this, dataEncoding, enableByeMsg, byeMsg);
        else this.byeMsg = new ByeMsgConfigsServer(this, dataEncoding, enableByeMsg, ByeMsgConfigs.BYE_MSG);

        this.protoName = protoName;

        this.bindAddr = bindAddr;
        this.bindPort = bindPort;
    }

    protected ServerAbs(String localId, InetAddress bindAddr, int bindPort, String protoName,
                        Charset charset, String delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, String byeMsg) {
        localInfo = new PeerInfoLocalDefault(localId, protoName);             // to updateOnConnected and updateOnDisconnected

        if (charset == null) charset = DataEncodingConfigs.CHARSET;
        if (delimiter == null) delimiter = DataEncodingConfigs.DELIMITER;
        if (hbTimeoutMs < 0) hbTimeoutMs = HeartBeatConfigs.TIMEOUT_MS;
        if (hbTimeoutHBMs < 0) hbTimeoutHBMs = HeartBeatConfigs.TIMEOUT_HB_MS;
        if (enableHBRes == null) enableHBRes = HeartBeatConfigs.ENABLE_HB_RES;
        if (enableByeMsg == null) enableByeMsg = ByeMsgConfigs.ENABLE;
        if (byeMsg == null) byeMsg = ByeMsgConfigs.BYE_MSG;
        dataEncoding = new DataEncodingConfigsServer(this, charset, delimiter);
        heartBeat = new HeartBeatConfigsServer(this, hbTimeoutMs, hbTimeoutHBMs, enableHBRes);
        this.byeMsg = new ByeMsgConfigsServer(this, dataEncoding, enableByeMsg, byeMsg);

        this.protoName = protoName;

        this.bindAddr = bindAddr;
        this.bindPort = bindPort;
    }


    // Getters

    @Override
    public ServerState getState() {
        return state;
    }

    @Override
    public PeerInfoLocal getServerPeerInfo() {
        return localInfo;
    }

    @Override
    public String getLocalId() {
        return localInfo.getId();
    }

    @Override
    public String getProtocolName() {
        return protoName;
    }

    @Override
    public List<ServerClient> getClients() {
        return new ArrayList<>(serverClients);
    }


    // toString()

    @Override
    public String toString() {
        return String.format("%s", getLocalId());
    }


    // Server startup methods

    @Override
    public void startup() throws ServerStartupException {
        if (getState().isRunning())
            return;

        if (getState().isStartup())
            return;

        doStartup();
        startInfiniteLoop();
        try {
            localInfo.updateOnConnected(serverSocket);

        } catch (PeerInfoSocketNotConnectedException e) {
            JavaAssertions.makeAssertion_Failed(e, "Method PeerInfoDefault.updateOnConnected(ServerSocket) is called from ServerAbs.startup() method, so it should NOT throw any PeerInfoSocketNotConnectedException");
        }
    }

    public void doStartup() throws ServerStartupException {
        state = ServerState.STARTUP;
        try {
            serverSocket = generateBindedServerSocket(bindAddr, bindPort);
        } catch (Throwable t) {
            state = ServerState.STOPPED;
            throw t;
        }
    }

    protected abstract ServerSocket generateBindedServerSocket(InetAddress bindAddr, int bindPort) throws ServerStartupException;

    protected void startInfiniteLoop() {
        infLoopTh = JavaThreads.initAndStart(new ServerInfiniteLoop(), TH_INF_LOOP_NAME, getLocalId());
    }

    private class ServerInfiniteLoop implements Runnable {

        @Override
        public void run() {
            state = ServerState.STARTED;
            emitOnStart();

            while (true) {
                Socket socket;
                try {
                    socket = serverSocket.accept();

                } catch (IOException e) {
                    if (e instanceof SocketException && (e.getMessage().equals("Socket closed") || e.getMessage().equals("Socket is closed")))
                        break;

                    JavaAssertions.makeWarning_Failed(e, String.format("Exception on Server '%s''s listener thread not managed.", ServerAbs.this));
                    emitOnFail("Error waiting on server's client connection, server shutdown", e);
                    break;
                }

                ServerClient client;
                try {
                    client = generateClient(socket);

                } catch (PeerConnectionException e) {
                    emitOnFail("Error generating server's client", e);
                    continue;
                }

                client.addListener(serverClientConnectionListener);
                client.addListener(serverClientDataListener);
                serverClients.add(client);
            }

            infLoopTh = null;
            state = ServerState.STOPPED;
            serverSocket = null;
            emitOnStop();
        }

    }

    protected ServerClient generateClient(Socket socket) throws PeerConnectionException {
        String remoteId =  String.format("%s://%s:%d", protoName, socket.getInetAddress().getHostAddress(), socket.getPort());
        String localId = String.format("%s:%d@%s", socket.getInetAddress().getHostAddress(), socket.getPort(), getLocalId());
        return new ServerClient(ServerAbs.this, localId, remoteId, getProtocolName(), socket,
                getDataEncodingConfigs().getCharset(), getDataEncodingConfigs().getDelimiter(),
                getHeartBeatConfigs().getTimeout(), getHeartBeatConfigs().getHBTimeout(), getHeartBeatConfigs().isHBResponseEnabled(),
                getByeConfigs().isEnable(), getByeConfigs().getByeMsg());
    }

    @Override
    public void shutdown() throws ServerShutdownException {
        if (getState().isStopped())
            return;

        if (getState().isShutdown())
            return;

        state = ServerState.SHUTDOWN;

        try {
            serverSocket.close();

        } catch (IOException e) {
            throw new ServerShutdownException(this, e, "Error on closing server socket");
        }

        JavaAssertions.makeWarning(infLoopTh != null, String.format("Instance 'ServerAbs'(%s) var 'infLoopTh''s of his infinite loop thread can NOT be null on server shutdown.", this));
        if (infLoopTh!=null)
            infLoopTh.interrupt();

        for (ServerClient c : getClients())
            try {
                c.disconnect();

            } catch (PeerDisconnectionException ignore) { /* ignore because focused on server shutdown */ }
    }

    public void closeSocket(ServerClient client, Socket socket) throws PeerDisconnectionException {
        try {
            socket.close();

        } catch (IOException e) {
            throw new PeerDisconnectionException(client, socket, client.getConnectionInfo().getRemoteInfo().getAddr(), client.getConnectionInfo().getRemoteInfo().getPort(), e);
        }
    }


    // Message methods

    public abstract boolean processData(ServerClient client, byte[] data);

    public abstract boolean processData(ServerClient client, String data);


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
    public void addListener(ServerStateListener listener) {
        listenersStatus.add(listener);
    }

    @Override
    public void removeListener(ServerStateListener listener) {
        listenersStatus.remove(listener);
    }

    protected void emitOnStart() {
        log.info(String.format("Server '%s' started", this));

        JavaListeners.emitter(this, listenersStatus, "onStart", new JavaListeners.ListenerMapper<ServerStateListener>() {
            @Override
            public void map(ServerStateListener l) {
                l.onStart(ServerAbs.this);
            }
        });
    }

    protected void emitOnStop() {
        log.info(String.format("Server '%s' stopped", this));

        JavaListeners.emitter(this, listenersStatus, "onStop", new JavaListeners.ListenerMapper<ServerStateListener>() {
            @Override
            public void map(ServerStateListener l) {
                l.onStop(ServerAbs.this);
            }
        });
    }

    protected void emitOnFail(String failMsg) {
        emitOnFail(failMsg, null);
    }

    protected void emitOnFail(String failMsg, Throwable exception) {
        log.warn(String.format("Server '%s' failed: '%s'", this, failMsg));

        Throwable finalException = exception != null ? exception : new Throwable(failMsg);
        JavaListeners.emitter(this, listenersStatus, "onFail", new JavaListeners.ListenerMapper<ServerStateListener>() {
            @Override
            public void map(ServerStateListener l) {
                l.onFail(ServerAbs.this, failMsg, finalException);
            }
        });
    }

    @Override
    public void addListener(ServerClientsListener listener) {
        listenersClient.add(listener);
    }

    @Override
    public void removeListener(ServerClientsListener listener) {
        listenersClient.remove(listener);
    }

    protected void emitOnConnect(ServerClient client) {
        // Log already printed by PeerAbs::emitOnConnect
        //log.info(String.format("Server '%s' opened client '%s' connection", this, client.getLocalId()));

        JavaListeners.emitter(this, listenersClient, "onConnect", new JavaListeners.ListenerMapper<ServerClientsListener>() {
            @Override
            public void map(ServerClientsListener l) {
                l.onConnect(ServerAbs.this, client);
            }
        });
    }

    protected void emitOnDisconnect(ServerClient client) {
        // Log already printed by PeerAbs::emitOnDisconnect
        //log.info(String.format("Server '%s' disconnected client '%s' connection", this, client.getLocalId()));

        JavaListeners.emitter(this, listenersClient, "onDisconnect", new JavaListeners.ListenerMapper<ServerClientsListener>() {
            @Override
            public void map(ServerClientsListener l) {
                l.onDisconnect(ServerAbs.this, client);
            }
        });
    }

    protected void emitOnFail(ServerClient client, String failMsg, Throwable exception) {
        // Log already printed by PeerAbs::emitOnFail
        //log.warn(String.format("Server '%s' failed client '%s' connection", this, client.getLocalId()));

        JavaListeners.emitter(this, listenersClient, "onFail", new JavaListeners.ListenerMapper<ServerClientsListener>() {
            @Override
            public void map(ServerClientsListener l) {
                l.onFail(ServerAbs.this, client, failMsg, exception);
            }
        });
    }

    @Override
    public void addListener(ServerDataListener listener) {
        listenersData.add(listener);
    }

    @Override
    public void removeListener(ServerDataListener listener) {
        listenersData.remove(listener);
    }

    protected void emitOnDataRx(ServerClient client, byte[] data) {
        // Log already printed by PeerAbs::emitOnDataRx
        //log.debug(String.format("Server '%s' rx from client '%s' data '%s'", this, client.getLocalId(), new String(data,getDataEncodingConfigs().getCharset())));

        JavaListeners.emitter(this, listenersData, "onDataRx", new JavaListeners.ListenerMapper<ServerDataListener>() {
            @Override
            public void map(ServerDataListener l) {
                l.onDataRx(ServerAbs.this, client, data);
            }
        });
    }

    protected void emitOnDataTx(ServerClient client, byte[] data) {
        // Log already printed by PeerAbs::emitOnDataTx
        //log.debug(String.format("Server '%s' tx from client '%s' data '%s'", this, client.getLocalId(), new String(data,getDataEncodingConfigs().getCharset())));

        JavaListeners.emitter(this, listenersData, "onDataTx", new JavaListeners.ListenerMapper<ServerDataListener>() {
            @Override
            public void map(ServerDataListener l) {
                l.onDataTx(ServerAbs.this, client, data);
            }
        });
    }


    // Peer listeners

    private final PeerConnectionListener serverClientConnectionListener = new PeerConnectionListener() {

        @Override
        public void onConnecting(Peer peer) {
        }

        @Override
        public void onWaiting(Peer peer) {
        }

        @Override
        public void onConnect(Peer peer) {
            emitOnConnect((ServerClient) peer);
        }

        @Override
        public void onDisconnecting(Peer peer) {
        }

        @Override
        public void onDisconnect(Peer peer) {
            if (!(peer instanceof ServerClient)) {
                JavaAssertions.makeAssertion_Failed(String.format("Can't call ServerAbs::onDisconnect() with peer param's class '%s', param's class must be 'ServerClient", peer.getClass().getSimpleName()));
                return;
            }
            serverClients.remove(peer);

            peer.removeListener(serverClientConnectionListener);
            peer.removeListener(serverClientDataListener);

            emitOnDisconnect((ServerClient) peer);
        }

        @Override
        public void onFail(Peer peer, String failMsg, Throwable exception) {
            emitOnFail((ServerClient) peer, failMsg, exception);
        }

    };

    private final PeerDataListener serverClientDataListener = new PeerDataListener() {

        @Override
        public void onDataRx(Peer peer, byte[] data) {
            if (!(peer instanceof ServerClient)) {
                JavaAssertions.makeAssertion_Failed(String.format("Can't call ServerAbs::onDataRx() with peer param's class '%s', param's class must be 'ServerClient", peer.getClass().getSimpleName()));
                return;
            }
            emitOnDataRx((ServerClient) peer, data);
        }

        @Override
        public void onDataTx(Peer peer, byte[] data) {
            if (!(peer instanceof ServerClient)) {
                JavaAssertions.makeAssertion_Failed(String.format("Can't call ServerAbs::onDataTx() with peer param's class '%s', param's class must be 'ServerClient", peer.getClass().getSimpleName()));
                return;
            }
            emitOnDataTx((ServerClient) peer, data);
        }

    };

}
