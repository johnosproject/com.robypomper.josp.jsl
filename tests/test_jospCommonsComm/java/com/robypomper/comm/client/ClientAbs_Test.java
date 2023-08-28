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

import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener_Latch;
import com.robypomper.comm.peer.PeerDataListener_Latch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tested methods:
 * - connect()
 * - scheduleReConnecting()
 * <p>
 * Indirect tested methods:
 * - constructors: on all methods
 * - doConnect(): on all connect methods
 * - ClientReConnect: on all scheduleReConnecting methods
 * - disconnect(): on disconnect scheduleReConnecting methods
 * - doDisconnect(): on all disconnect methods
 * - behaviours configs
 * - str2Inet_onClientConstructor
 */
public class ClientAbs_Test {

    // Class constants

    private static final String LOCAL_ID = "clientLocalId";
    private static final String REMOTE_ID = "remoteId";
    private static final String PROTO_NAME = "ex_tcp";
    private static final int PORT = 10000;
    protected static final int RECONNECT_DELAY = 50;


    // Internal vars

    protected Peer peer;
    protected ServerSocket server;
    protected PeerConnectionListener_Latch listenerConnection;
    protected PeerDataListener_Latch listenerData;


    // setup/tear down

    @BeforeEach
    public void setUp() throws IOException {
        System.out.println("SET_UP: Start server");
        server = new ServerSocket(PORT);
        //socket = new Socket();
        //socket = new Socket(server.getInetAddress(), PORT);
        //socket = new Socket(server.getInetAddress(), PORT, InetAddress.getByName(null), PORT + 1);

        listenerConnection = new PeerConnectionListener_Latch();
        listenerData = new PeerDataListener_Latch();
    }

    @AfterEach
    public void tearDown() throws PeerDisconnectionException, IOException {
        System.out.println("TEAR_DOWN: Disconnect client");
        peer.disconnect();

        System.out.println("TEAR_DOWN: Stop server");
        server.close();
    }


    // connect

    @Test
    public void METHOD_connect() throws PeerConnectionException, InterruptedException {
        System.out.println("Create client");
        ClientAbs_Impl peer = new ClientAbs_Impl(LOCAL_ID, REMOTE_ID, server.getInetAddress(), PORT, PROTO_NAME);
        peer.addListener(listenerConnection);
        this.peer = peer;

        System.out.println("Connect client, ignore exception");
        peer.connect();

        System.out.println("Check client status = CONNECTING");
        Assertions.assertEquals(ConnectionState.CONNECTING, peer.getState());

        System.out.println("Check client status = CONNECTED");
        Assertions.assertTrue(listenerConnection.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.CONNECTED, peer.getState());
    }

    @Test
    public void METHOD_connect_EXCEPTION_PeerConnectionException() {
        System.out.println("Create client");
        ClientAbs_Impl peer = new ClientAbs_Impl(LOCAL_ID, REMOTE_ID, server.getInetAddress(), PORT + 1, PROTO_NAME);
        peer.addListener(listenerConnection);
        this.peer = peer;

        System.out.println("Connect client throw PeerConnectionException");
        PeerConnectionException exception = Assertions.assertThrows(PeerConnectionException.class, peer::connect);

        System.out.println("Check PeerConnectionException");
        String expected = String.format("Error on Peer '%s' because can't connect socket to '%s:%d'", peer.toString().replace("< | >", "<-| >"), server.getInetAddress().getHostAddress(), PORT + 1);
        Assertions.assertEquals(expected, exception.getMessage());

        System.out.println("Check client status = DISCONNECTED");
        Assertions.assertEquals(ConnectionState.DISCONNECTED, peer.getState());
    }


    // scheduleReConnecting

    @Test
    public void METHOD_scheduleReConnecting_ON_notExistingServer() {
        System.out.println("Create client");
        ClientAbs_Impl peer = new ClientAbs_Impl(LOCAL_ID, REMOTE_ID, server.getInetAddress(), PORT + 1, PROTO_NAME);
        peer.addListener(listenerConnection);
        peer.getAutoReConnectConfigs().enable(true);
        this.peer = peer;

        System.out.println("Connect client, ignore exception");
        try {
            peer.connect();

        } catch (PeerConnectionException ignore) {
        }

        System.out.println("Check client status = WAITING_SERVER");
        Assertions.assertEquals(ConnectionState.WAITING_SERVER, peer.getState());
    }

    @Test
    public void METHOD_scheduleReConnecting_ON_notExistingServer_existingServer() throws IOException, InterruptedException {
        System.out.println("Stop server (default)");
        server.close();

        System.out.println("Create client");
        ClientAbs_Impl peer = new ClientAbs_Impl(LOCAL_ID, REMOTE_ID, server.getInetAddress(), PORT + 1, PROTO_NAME);
        peer.addListener(listenerConnection);
        peer.getAutoReConnectConfigs().enable(true);
        this.peer = peer;
        peer.getAutoReConnectConfigs().setDelay(RECONNECT_DELAY);

        System.out.println("Connect client, ignore exception");
        try {
            peer.connect();
        } catch (PeerConnectionException ignore) {
        }

        System.out.println("Check client status = WAITING_SERVER");
        Assertions.assertEquals(ConnectionState.WAITING_SERVER, peer.getState());

        System.out.println("Start server");
        server = new ServerSocket(PORT + 1);

        System.out.println("Check client status = CONNECTED");
        Assertions.assertTrue(listenerConnection.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.CONNECTED, peer.getState());
    }

    @Test
    public void METHOD_scheduleReConnecting_ON_notExistingServer_disconnect() throws PeerDisconnectionException, InterruptedException {
        System.out.println("Create client");
        ClientAbs_Impl peer = new ClientAbs_Impl(LOCAL_ID, REMOTE_ID, server.getInetAddress(), PORT + 1, PROTO_NAME);
        peer.addListener(listenerConnection);
        peer.getAutoReConnectConfigs().enable(true);
        this.peer = peer;

        System.out.println("Connect client, ignore exception");
        try {
            peer.connect();
        } catch (PeerConnectionException ignore) {
        }

        System.out.println("Check client status = WAITING_SERVER");
        Assertions.assertEquals(ConnectionState.WAITING_SERVER, peer.getState());

        System.out.println("Disconnect client");
        listenerConnection.onDisconnect = new CountDownLatch(1);
        peer.disconnect();
        Assertions.assertTrue(listenerConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, peer.getState());
    }

}
