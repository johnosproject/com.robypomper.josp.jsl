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

package com.robypomper.comm.connection;

import com.robypomper.comm.peer.Peer_Mock;
import com.robypomper.comm.peer.Peer_MockEvents;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * Tested methods:
 * - constructors: on all methods
 * - toString()
 * - getState()
 * - getters: pre-connection/post-connection
 * - connectionListener
 * - dataListener
 * - hbListener
 * <p>
 * Indirect tested methods:
 * - updateOnConnecting(): on all toString and state methods
 * - updateOnConnecting_Waiting(): on all toString and state methods
 * - updateOnConnected(): on all toString, state and getters methods
 * - updateOnDisconnecting(): on all toString and state methods
 * - updateOnDisconnected(): on all toString and state methods
 * <p>
 * Not tested methods:
 * - getProtocolName()
 * - updateOnHeartBeatSuccess
 * - updateOnHeartBeatFail
 * - updateOnDataRx
 * - updateOnDataTx
 */
public class ConnectionInfoDefault_Test {

    // Class constants

    private static final String LOCAL_ID = "localId";
    private static final String REMOTE_ID = "remoteId";
    private static final String PROTO_NAME = "ex_tcp";
    private static final int PORT = 10000;
    private static final byte[] DATA = "data example".getBytes();


    // Internal vars

    protected ServerSocket server;
    protected Socket socket;


    // setup/tear down

    @BeforeEach
    public void setUp() throws IOException {
        server = new ServerSocket(PORT);
        //socket = new Socket();
        //socket = new Socket(server.getInetAddress(),PORT);
        //socket = new Socket(server.getInetAddress(), PORT, InetAddress.getByName(null), PORT + 1);
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (socket != null) socket.close();
        server.close();
    }


    // constructors

    @Test
    public void METHOD_constructors_ON_socketNotConnected() {
        Socket socket = new Socket();
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);

        Assertions.assertEquals(ConnectionState.DISCONNECTED, connectionInfo.getState());
    }

    @Test
    public void METHOD_constructors_ON_socketConnected() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);

        Assertions.assertEquals(ConnectionState.CONNECTED, connectionInfo.getState());
    }


    // toString

    @Test
    public void METHOD_toString_ON_notConnected() {
        Socket socket = new Socket();
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);

        String expected = String.format("%s < | > %s", connectionInfo.getLocalInfo(), connectionInfo.getRemoteInfo());
        Assertions.assertEquals(expected, connectionInfo.toString());
    }

    @Test
    public void METHOD_toString_ON_connecting() {
        Socket socket = new Socket();
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);
        connectionInfo.updateOnConnecting();

        String expected = String.format("%s <-| > %s", connectionInfo.getLocalInfo(), connectionInfo.getRemoteInfo());
        Assertions.assertEquals(expected, connectionInfo.toString());
    }

    @Test
    public void METHOD_toString_ON_connectingWaiting() {
        Socket socket = new Socket();
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);
        connectionInfo.updateOnConnecting_Waiting();

        String expected = String.format("%s <...> %s", connectionInfo.getLocalInfo(), connectionInfo.getRemoteInfo());
        Assertions.assertEquals(expected, connectionInfo.toString());
    }

    @Test
    public void METHOD_toString_ON_connected() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);
        //connectionInfo.updateOnConnected();       // already called in ConnectioninfoDefault constructor

        String expected = String.format("%s <---> %s", connectionInfo.getLocalInfo(), connectionInfo.getRemoteInfo());
        Assertions.assertEquals(expected, connectionInfo.toString());
    }

    @Test
    public void METHOD_toString_ON_disconnecting() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);
        connectionInfo.updateOnDisconnecting();

        String expected = String.format("%s < |-> %s", connectionInfo.getLocalInfo(), connectionInfo.getRemoteInfo());
        Assertions.assertEquals(expected, connectionInfo.toString());
    }

    @Test
    public void METHOD_toString_ON_disconnected() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);
        socket.close();
        connectionInfo.updateOnDisconnected();

        String expected = String.format("%s < | > %s", connectionInfo.getLocalInfo(), connectionInfo.getRemoteInfo());
        Assertions.assertEquals(expected, connectionInfo.toString());
    }


    // state

    @Test
    public void METHOD_state_ON_notConnected() {
        Socket socket = new Socket();
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);

        Assertions.assertEquals(ConnectionState.DISCONNECTED, connectionInfo.getState());
    }

    @Test
    public void METHOD_state_ON_connecting() {
        Socket socket = new Socket();
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);
        connectionInfo.updateOnConnecting();

        Assertions.assertEquals(ConnectionState.CONNECTING, connectionInfo.getState());
    }

    @Test
    public void METHOD_state_ON_connectingWaiting() {
        Socket socket = new Socket();
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);
        connectionInfo.updateOnConnecting_Waiting();

        Assertions.assertEquals(ConnectionState.WAITING_SERVER, connectionInfo.getState());
    }

    @Test
    public void METHOD_state_ON_connected() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);
        //connectionInfo.updateOnConnected();       // already called in ConnectioninfoDefault constructor

        Assertions.assertEquals(ConnectionState.CONNECTED, connectionInfo.getState());
    }

    @Test
    public void METHOD_state_ON_disconnecting() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);
        connectionInfo.updateOnDisconnecting();

        Assertions.assertEquals(ConnectionState.DISCONNECTING, connectionInfo.getState());
    }

    @Test
    public void METHOD_state_ON_disconnected() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);
        socket.close();
        connectionInfo.updateOnDisconnected();

        Assertions.assertEquals(ConnectionState.DISCONNECTED, connectionInfo.getState());
    }


    // getters

    @Test
    public void METHOD_getter_ON_notConnected() {
        Socket socket = new Socket();
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);

        Assertions.assertNull(connectionInfo.getStats().getLastConnection());
        Assertions.assertNull(connectionInfo.getStats().getLastDisconnection());
        Assertions.assertFalse(connectionInfo.getLocalInfo().isConnected());
        Assertions.assertFalse(connectionInfo.getRemoteInfo().isConnected());
    }

    @Test
    public void METHOD_getter_ON_notConnected_connect() throws IOException {
        Socket socket = new Socket();
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);
        socket.connect(new InetSocketAddress(server.getInetAddress(), PORT));
        connectionInfo.updateOnConnected();

        Assertions.assertTrue(ConnectionStatsDefault_Test.checkDate(connectionInfo.getStats().getLastConnection(), new Date(), 100));
        Assertions.assertNull(connectionInfo.getStats().getLastDisconnection());
        Assertions.assertTrue(connectionInfo.getLocalInfo().isConnected());
        Assertions.assertTrue(connectionInfo.getRemoteInfo().isConnected());
    }

    @Test
    public void METHOD_getter_ON_connected() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);

        Assertions.assertTrue(ConnectionStatsDefault_Test.checkDate(connectionInfo.getStats().getLastConnection(), new Date(), 100));
        Assertions.assertNull(connectionInfo.getStats().getLastDisconnection());
        Assertions.assertTrue(connectionInfo.getLocalInfo().isConnected());
        Assertions.assertTrue(connectionInfo.getRemoteInfo().isConnected());
    }

    @Test
    public void METHOD_getter_ON_connected_disconnect() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        ConnectionInfoDefault connectionInfo = new ConnectionInfoDefault(new Peer_Mock(socket), LOCAL_ID, REMOTE_ID, PROTO_NAME);
        socket.close();
        connectionInfo.updateOnDisconnected();

        Assertions.assertNotNull(connectionInfo.getStats().getLastConnection());
        Assertions.assertTrue(ConnectionStatsDefault_Test.checkDate(connectionInfo.getStats().getLastDisconnection(), new Date(), 100));
        Assertions.assertFalse(connectionInfo.getLocalInfo().isConnected());
        Assertions.assertFalse(connectionInfo.getRemoteInfo().isConnected());
    }


    // connectionListener

    @Test
    public void METHOD_connectionListener_ON_onConnected() throws IOException {
        Socket socket = new Socket();
        Peer_MockEvents peerEvents = new Peer_MockEvents(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket);
        ConnectionInfoDefault connectionInfo = peerEvents.getConnectionInfo();
        socket.connect(new InetSocketAddress(server.getInetAddress(), PORT));
        peerEvents.emitOnConnect();

        Assertions.assertEquals(ConnectionState.CONNECTED, connectionInfo.getState());
        Assertions.assertTrue(ConnectionStatsDefault_Test.checkDate(connectionInfo.getStats().getLastConnection(), new Date(), 100));
    }

    @Test
    public void METHOD_connectionListener_ON_onConnected_EXCEPTION_AssertionError() {
        Socket socket = new Socket();
        Peer_MockEvents peerEvents = new Peer_MockEvents(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket);

        // call emitOnConnected() method when socket is not connected throw an AssertionError exception
        AssertionError exception = Assertions.assertThrows(AssertionError.class, peerEvents::emitOnConnect);

        String expected = "Method ConnectionInfoDefault.updateOnConnected() called when internal socket is NOT connected. [PeerInfoSocketNotConnectedException] Error set connected state to PeerInfo 'localId (NotConnected)' because socket not connected";
        Assertions.assertEquals(expected, exception.getMessage());
    }

    @Test
    public void METHOD_connectionListener_ON_onDisconnected() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        Peer_MockEvents peerEvents = new Peer_MockEvents(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket);
        ConnectionInfoDefault connectionInfo = peerEvents.getConnectionInfo();
        socket.close();
        peerEvents.emitOnDisconnect();

        Assertions.assertEquals(ConnectionState.DISCONNECTED, connectionInfo.getState());
        Assertions.assertTrue(ConnectionStatsDefault_Test.checkDate(connectionInfo.getStats().getLastDisconnection(), new Date(), 100));
    }


    // dataListener

    @Test
    public void METHOD_dataListener_ON_onDataRx() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        Peer_MockEvents peerEvents = new Peer_MockEvents(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket);
        ConnectionInfoDefault connectionInfo = peerEvents.getConnectionInfo();
        peerEvents.emitOnDataRx(DATA);

        Assertions.assertTrue(ConnectionStatsDefault_Test.checkDate(connectionInfo.getStats().getLastDataRx(), new Date(), 100));
        Assertions.assertEquals(DATA.length, connectionInfo.getStats().getBytesRx());
    }

    @Test
    public void METHOD_dataListener_ON_onDataRxMultiple() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        Peer_MockEvents peerEvents = new Peer_MockEvents(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket);
        ConnectionInfoDefault connectionInfo = peerEvents.getConnectionInfo();
        peerEvents.emitOnDataRx(DATA);
        peerEvents.emitOnDataRx(DATA);
        peerEvents.emitOnDataRx(DATA);

        Assertions.assertTrue(ConnectionStatsDefault_Test.checkDate(connectionInfo.getStats().getLastDataRx(), new Date(), 100));
        Assertions.assertEquals(DATA.length * 3, connectionInfo.getStats().getBytesRx());
    }

    @Test
    public void METHOD_dataListener_ON_onDataTx() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        Peer_MockEvents peerEvents = new Peer_MockEvents(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket);
        ConnectionInfoDefault connectionInfo = peerEvents.getConnectionInfo();
        peerEvents.emitOnDataTx(DATA);

        Assertions.assertTrue(ConnectionStatsDefault_Test.checkDate(connectionInfo.getStats().getLastDataTx(), new Date(), 100));
        Assertions.assertEquals(DATA.length, connectionInfo.getStats().getBytesTx());
    }

    @Test
    public void METHOD_dataListener_ON_onDataTxMultiple() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        Peer_MockEvents peerEvents = new Peer_MockEvents(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket);
        ConnectionInfoDefault connectionInfo = peerEvents.getConnectionInfo();
        peerEvents.emitOnDataTx(DATA);
        peerEvents.emitOnDataTx(DATA);
        peerEvents.emitOnDataTx(DATA);

        Assertions.assertTrue(ConnectionStatsDefault_Test.checkDate(connectionInfo.getStats().getLastDataTx(), new Date(), 100));
        Assertions.assertEquals(DATA.length * 3, connectionInfo.getStats().getBytesTx());
    }


    // hbListener

    @Test
    public void METHOD_hbListener_ON_onSuccess() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        Peer_MockEvents peerEvents = new Peer_MockEvents(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket);
        ConnectionInfoDefault connectionInfo = peerEvents.getConnectionInfo();
        peerEvents.getHeartBeatConfigs().emitOnSuccess(peerEvents, peerEvents.getHeartBeatConfigs());

        Assertions.assertTrue(ConnectionStatsDefault_Test.checkDate(connectionInfo.getStats().getLastHeartBeat(), new Date(), 100));
        Assertions.assertEquals(1, connectionInfo.getStats().getHeartBeatReceived());
    }

    @Test
    public void METHOD_hbListener_ON_onFail() throws IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        Peer_MockEvents peerEvents = new Peer_MockEvents(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket);
        ConnectionInfoDefault connectionInfo = peerEvents.getConnectionInfo();
        peerEvents.getHeartBeatConfigs().emitOnFail(peerEvents, peerEvents.getHeartBeatConfigs());

        Assertions.assertTrue(ConnectionStatsDefault_Test.checkDate(connectionInfo.getStats().getLastHeartBeatFailed(), new Date(), 100));
        Assertions.assertEquals(0, connectionInfo.getStats().getHeartBeatReceived());
    }

}
