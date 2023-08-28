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
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Tested methods:
 * - getters
 * <p>
 * Indirect tested methods:
 * - constructors: on all methods
 * - PeerInfoLocalDefault(): on all methods
 */
public class PeerInfoLocalDefault_Test {

    // Class constants

    private static final String LOCAL_ID = "localId";
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


    // getters (fields are set after first connection)

    @Test
    public void METHOD_getters_ON_notConnected() {
        PeerInfoLocalDefault peerInfo = new PeerInfoLocalDefault(LOCAL_ID, PROTO_NAME);

        Assertions.assertNull(peerInfo.getIntf());
    }

    @Test
    public void METHOD_getters_ON_connected() throws IOException, PeerInfoSocketNotConnectedException {
        PeerInfoLocalDefault peerInfo = new PeerInfoLocalDefault(LOCAL_ID, PROTO_NAME);
        socket = new Socket(server.getInetAddress(), PORT);
        peerInfo.updateOnConnected(socket);

        Assertions.assertEquals(NetworkInterface.getByInetAddress(peerInfo.getAddr()), peerInfo.getIntf());
    }

    @Test
    public void METHOD_getters_ON_connected_disconnected() throws IOException, PeerInfoSocketNotConnectedException {
        PeerInfoLocalDefault peerInfo = new PeerInfoLocalDefault(LOCAL_ID, PROTO_NAME);
        socket = new Socket(server.getInetAddress(), PORT);
        peerInfo.updateOnConnected(socket);

        Assertions.assertEquals(NetworkInterface.getByInetAddress(peerInfo.getAddr()), peerInfo.getIntf());

        socket.close();
        peerInfo.updateOnDisconnected();

        Assertions.assertEquals(NetworkInterface.getByInetAddress(peerInfo.getAddr()), peerInfo.getIntf());
    }

}
