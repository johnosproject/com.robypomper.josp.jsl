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

import com.robypomper.comm.exception.PeerInfoSocketNotConnectedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Tested methods:
 * - toString()
 * - isConnected()
 * - updateOnConnected(): only exception
 * - getters: local/remote
 * <p>
 * Indirect tested methods:
 * - constructors: on all methods
 * - updateOnConnected(): on all ON_connected and ON_connected_disconnected methods
 * - updateOnDisconnected(): on all ON_connected_disconnected
 * <p>
 * Not tested methods:
 * - getId()
 * - isLocal()/isRemote()
 */
public class PeerInfoDefault_Test {

    // Class constants

    private static final String LOCAL_ID = "localId";
    private static final boolean IS_LOCAL = true;
    private static final boolean IS_REMOTE = false;
    private static final String PROTO_NAME = "proto-test";
    private static final int PORT = 10000;


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


    // toString

    @Test
    public void METHOD_toString_ON_notConnected() {
        PeerInfoDefault peerInfo = new PeerInfoDefault(LOCAL_ID, PROTO_NAME, IS_LOCAL);

        Assertions.assertEquals("localId (NotConnected)", peerInfo.toString());
    }

    @Test
    public void METHOD_toString_ON_connected() throws IOException, PeerInfoSocketNotConnectedException {
        PeerInfoDefault peerInfo = new PeerInfoDefault(LOCAL_ID, PROTO_NAME, IS_LOCAL);
        socket = new Socket(server.getInetAddress(), PORT);
        peerInfo.updateOnConnected(socket);

        String expected = String.format("%s (%s:%d)", LOCAL_ID, socket.getLocalAddress().getHostAddress(), socket.getLocalPort());
        Assertions.assertEquals(expected, peerInfo.toString());
    }

    @Test
    public void METHOD_toString_ON_connectedLoopBack() throws IOException, PeerInfoSocketNotConnectedException {
        PeerInfoDefault peerInfo = new PeerInfoDefault(LOCAL_ID, PROTO_NAME, IS_LOCAL);
        socket = new Socket(server.getInetAddress(), PORT, InetAddress.getByName(null), PORT + 1);
        peerInfo.updateOnConnected(socket);

        Assertions.assertEquals("localId (127.0.0.1:10001)", peerInfo.toString());
    }


    // isConnected

    @Test
    public void METHOD_isConnected_ON_notConnected() {
        PeerInfoDefault peerInfo = new PeerInfoDefault(LOCAL_ID, PROTO_NAME, IS_LOCAL);

        Assertions.assertFalse(peerInfo.isConnected());
    }

    @Test
    public void METHOD_isConnected_ON_connected() throws IOException, PeerInfoSocketNotConnectedException {
        PeerInfoDefault peerInfo = new PeerInfoDefault(LOCAL_ID, PROTO_NAME, IS_LOCAL);
        socket = new Socket(server.getInetAddress(), PORT);
        peerInfo.updateOnConnected(socket);

        Assertions.assertTrue(peerInfo.isConnected());
    }

    @Test
    public void METHOD_isConnected_ON_connected_disconnected() throws IOException, PeerInfoSocketNotConnectedException {
        PeerInfoDefault peerInfo = new PeerInfoDefault(LOCAL_ID, PROTO_NAME, IS_LOCAL);
        socket = new Socket(server.getInetAddress(), PORT);
        peerInfo.updateOnConnected(socket);

        Assertions.assertTrue(peerInfo.isConnected());

        socket.close();
        peerInfo.updateOnDisconnected();

        Assertions.assertFalse(peerInfo.isConnected());
    }

    @Test
    public void METHOD_updateOnConnected_EXCEPTION_PeerInfoSocketNotConnectedException() {
        PeerInfoDefault peerInfo = new PeerInfoDefault(LOCAL_ID, PROTO_NAME, IS_LOCAL);
        socket = new Socket();

        PeerInfoSocketNotConnectedException exception = Assertions.assertThrows(PeerInfoSocketNotConnectedException.class, () -> peerInfo.updateOnConnected(socket));

        String expected = String.format("Error set connected state to PeerInfo '%s' because socket not connected", peerInfo);
        Assertions.assertEquals(expected, exception.getMessage());
    }


    // getters (fields are set after first connection)

    @Test
    public void METHOD_gettersLocal_ON_notConnected() {
        PeerInfoDefault peerInfo = new PeerInfoDefault(LOCAL_ID, PROTO_NAME, IS_LOCAL);

        Assertions.assertNull(peerInfo.getHostname());
        Assertions.assertNull(peerInfo.getAddr());
        Assertions.assertNull(peerInfo.getPort());
    }

    @Test
    public void METHOD_gettersLocal_ON_connected() throws IOException, PeerInfoSocketNotConnectedException {
        PeerInfoDefault peerInfo = new PeerInfoDefault(LOCAL_ID, PROTO_NAME, IS_LOCAL);
        socket = new Socket(server.getInetAddress(), PORT);
        peerInfo.updateOnConnected(socket);

        Assertions.assertEquals(InetAddress.getLocalHost().getHostName().toUpperCase(), peerInfo.getHostname().toUpperCase());
        Assertions.assertEquals(socket.getLocalAddress(), peerInfo.getAddr());
        Assertions.assertEquals(socket.getLocalPort(), (int) peerInfo.getPort());
    }

    @Test
    public void METHOD_gettersLocal_ON_connected_disconnected() throws IOException, PeerInfoSocketNotConnectedException {
        PeerInfoDefault peerInfo = new PeerInfoDefault(LOCAL_ID, PROTO_NAME, IS_LOCAL);
        socket = new Socket(server.getInetAddress(), PORT);
        peerInfo.updateOnConnected(socket);

        Assertions.assertEquals(InetAddress.getLocalHost().getHostName().toUpperCase(), peerInfo.getHostname().toUpperCase());
        Assertions.assertEquals(socket.getLocalAddress(), peerInfo.getAddr());
        Assertions.assertEquals(socket.getLocalPort(), (int) peerInfo.getPort());

        InetAddress rememberLocalAddress = socket.getLocalAddress();
        int rememberLocalPort = socket.getLocalPort();
        socket.close();
        peerInfo.updateOnDisconnected();

        Assertions.assertEquals(InetAddress.getLocalHost().getHostName().toUpperCase(), peerInfo.getHostname().toUpperCase());
        Assertions.assertEquals(rememberLocalAddress, peerInfo.getAddr());
        Assertions.assertEquals(rememberLocalPort, (int) peerInfo.getPort());
    }

    @Test
    public void METHOD_gettersRemote_ON_notConnected() {
        PeerInfoDefault peerInfo = new PeerInfoDefault(LOCAL_ID, PROTO_NAME, IS_REMOTE);

        Assertions.assertNull(peerInfo.getHostname());
        Assertions.assertNull(peerInfo.getAddr());
        Assertions.assertNull(peerInfo.getPort());
    }

    @Test
    public void METHOD_gettersRemote_ON_connected() throws IOException, PeerInfoSocketNotConnectedException {
        PeerInfoDefault peerInfo = new PeerInfoDefault(LOCAL_ID, PROTO_NAME, IS_REMOTE);
        socket = new Socket(server.getInetAddress(), PORT);
        peerInfo.updateOnConnected(socket);

        Assertions.assertEquals(InetAddress.getLocalHost().getHostName(), peerInfo.getHostname());
        Assertions.assertEquals(socket.getInetAddress(), peerInfo.getAddr());
        Assertions.assertEquals(socket.getPort(), (int) peerInfo.getPort());
    }

    @Test
    public void METHOD_gettersRemote_ON_connected_disconnected() throws IOException, PeerInfoSocketNotConnectedException {
        PeerInfoDefault peerInfo = new PeerInfoDefault(LOCAL_ID, PROTO_NAME, IS_REMOTE);
        socket = new Socket(server.getInetAddress(), PORT);
        peerInfo.updateOnConnected(socket);

        Assertions.assertEquals(InetAddress.getLocalHost().getHostName(), peerInfo.getHostname());
        Assertions.assertEquals(socket.getInetAddress(), peerInfo.getAddr());
        Assertions.assertEquals(socket.getPort(), (int) peerInfo.getPort());

        InetAddress rememberRemoteAddress = socket.getInetAddress();
        int rememberRemotePort = socket.getPort();
        socket.close();
        peerInfo.updateOnDisconnected();

        Assertions.assertEquals(InetAddress.getLocalHost().getHostName(), peerInfo.getHostname());
        Assertions.assertEquals(rememberRemoteAddress, peerInfo.getAddr());
        Assertions.assertEquals(rememberRemotePort, (int) peerInfo.getPort());
    }

}
