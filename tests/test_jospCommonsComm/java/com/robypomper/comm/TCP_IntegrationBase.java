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

package com.robypomper.comm;

import com.robypomper.comm.client.Client;
import com.robypomper.comm.client.ClientAbs_Impl;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.ServerException;
import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.peer.PeerConnectionListener_Latch;
import com.robypomper.comm.peer.PeerDataListener_Latch;
import com.robypomper.comm.server.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TCP_IntegrationBase {

    // Class constants

    protected static final String CLIENT_LOCAL_ID = "clientLocalId";
    protected static final String CLIENT_SERVER_REMOTE_ID = "serverRemoteId(CL)";
    protected static final String SERVER_LOCAL_ID = "serverLocalId";
    protected static final String PROTO_NAME = "ex_tcp";
    protected static final int PORT = 10000;
    protected static final String STR_DATA = "Data Tx/Rx";
    protected static final String STR_DELIMITER = "#END#";
    protected static final int HB_TIMEOUT = 20;
    protected static final int HB_HB_TIMEOUT = 10;
    protected static final int RECONNECT_DELAY = 50;


    // Internal vars

    protected Server server;
    protected Client client;
    protected ServerStateListener_Latch listenerServerState;
    protected ServerClientsListener_Latch listenerServerClient;
    protected ServerDataListener_Latch listenerServerData;
    protected PeerConnectionListener_Latch listenerClientConnection;
    protected PeerDataListener_Latch listenerClientData;


    // setup/tear down

    @BeforeEach
    public void setUp() {
        listenerServerState = new ServerStateListener_Latch();
        listenerServerClient = new ServerClientsListener_Latch();
        listenerServerData = new ServerDataListener_Latch();
        listenerClientConnection = new PeerConnectionListener_Latch();
        listenerClientData = new PeerDataListener_Latch();
    }

    @AfterEach
    public void tearDown() throws ServerException, InterruptedException, PeerDisconnectionException {
        if (client != null && client.getState() != ConnectionState.DISCONNECTED) {
            if (client.getState() != ConnectionState.DISCONNECTING) {
                System.out.println("TEAR_DOWN: Disconnect client");
                listenerClientConnection.onDisconnect = new CountDownLatch(1);
                client.disconnect();
            }
            System.out.println("TEAR_DOWN: Wait client disconnected");
            listenerClientConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS);
        }
        if (server != null && server.getState() != ServerState.STOPPED) {
            if (server.getState() != ServerState.SHUTDOWN) {
                System.out.println("TEAR_DOWN: Stop server");
                listenerServerState.onShutdown = new CountDownLatch(1);
                server.shutdown();
            }
            System.out.println("TEAR_DOWN: Wait server stopped");
            listenerServerState.onShutdown.await(100, TimeUnit.MILLISECONDS);
        }
    }

    protected void initServer() {
        server = new ServerAbs_Impl(SERVER_LOCAL_ID, PORT, PROTO_NAME);
        server.addListener(listenerServerState);
        server.addListener(listenerServerClient);
        server.addListener(listenerServerData);
    }

    protected void startServer() {
        try {
            initServer();
            server.startup();
            if (!listenerServerState.onStartup.await(100, TimeUnit.MILLISECONDS))
                throw new RuntimeException("Server not started, can't continue test");

        } catch (ServerStartupException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void initAndStartServer() {
        initServer();
        startServer();
    }

    protected ServerClient waitAndGetClientConnectedOnServer() {
        try {
            if (listenerServerClient.onConnect.await(100, TimeUnit.MILLISECONDS)) {
                if (server.getClients().size() > 0)
                    return server.getClients().get(0);
                else
                    throw new RuntimeException("Client connected but immediately disconnected (server side), can't continue test");
            }

        } catch (InterruptedException ignore) {
        }

        throw new RuntimeException("Client not connected (server side), can't continue test");
    }

    protected void shutdownServer() {
        try {
            server.shutdown();
            if (!listenerServerState.onShutdown.await(100, TimeUnit.MILLISECONDS))
                throw new RuntimeException("Server not stopped, can't continue test");

        } catch (InterruptedException | ServerException e) {
            throw new RuntimeException(e);
        }
    }

    protected void initClient() {
        client = new ClientAbs_Impl(CLIENT_LOCAL_ID, CLIENT_SERVER_REMOTE_ID, server.getServerPeerInfo().getAddr(), PORT, PROTO_NAME);
        client.addListener(listenerClientConnection);
        client.addListener(listenerClientData);
    }

    protected void initClientOnLocalhost() {
        try {
            client = new ClientAbs_Impl(CLIENT_LOCAL_ID, CLIENT_SERVER_REMOTE_ID, InetAddress.getLocalHost(), PORT, PROTO_NAME);
            client.addListener(listenerClientConnection);
            client.addListener(listenerClientData);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    protected void connectClient() {
        try {
            client.connect();
            if (!listenerClientConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
                throw new RuntimeException("Client not connected, can't continue test");

        } catch (InterruptedException | PeerConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    protected void disconnectClient() {
        try {
            client.disconnect();
            if (!listenerClientConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS))
                throw new RuntimeException("Client not disconnected, can't continue test");

        } catch (InterruptedException | PeerDisconnectionException e) {
            throw new RuntimeException(e);
        }
    }

}
