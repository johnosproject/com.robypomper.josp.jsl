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

import com.robypomper.comm.behaviours.ByeMsgDefault;
import com.robypomper.comm.behaviours.HeartBeatDefault;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerException;
import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.java.JavaThreads;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Tested methods:
 * - disconnect()
 * - sendData()
 * - heartBeatListener: simulate HB timeout
 * - byeMsgListener: simulate byeMsg Rx
 * <p>
 * Indirect tested methods:
 * - constructors: on all methods
 * - doDisconnect(): on all disconnect methods
 * - startInfiniteLoop(): on all processData methods
 * - PeerInfiniteLoop: on all processData methods
 * - listenForData(): on all processData methods
 * - PeerConnectionListener: on disconnect, sendData and processData methods
 * - PeerDataListener: on sendData and processData methods
 * <p>
 * Abstract methods call:
 * - closeSocket(): called by doDisconnect() (no tests methods)
 * - processData(): called by PeerInfiniteLoop
 * <p>
 * Not tested methods:
 * - getters
 * - behaviours configs
 */
public class PeerAbs_Test {

    // Class constants

    private static final String LOCAL_ID = "localId";
    private static final String REMOTE_ID = "remoteId";
    private static final String PROTO_NAME = "ex_tcp";
    private static final int PORT = 10000;
    private static final byte[] DATA = "data example".getBytes();


    // Internal vars

    protected Peer peer;
    protected ServerSocket server;
    protected PeerConnectionListener_Latch listenerConnection;
    protected PeerDataListener_Latch listenerData;


    // setup/tear down

    @BeforeEach
    public void setUp() throws IOException {
        server = new ServerSocket(PORT);
        //socket = new Socket();
        //socket = new Socket(server.getInetAddress(), PORT);
        //socket = new Socket(server.getInetAddress(), PORT, InetAddress.getByName(null), PORT + 1);

        listenerConnection = new PeerConnectionListener_Latch();
        listenerData = new PeerDataListener_Latch();
    }

    @AfterEach
    public void tearDown() throws PeerDisconnectionException, IOException {
        peer.disconnect();
        server.close();
    }


    // disconnect

    @Test
    public void METHOD_disconnect_ON_connected() throws PeerException, InterruptedException, IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        PeerAbs_Impl peer = new PeerAbs_Impl(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket, listenerConnection);
        this.peer = peer;
        if (!listenerConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Peer not connected, can't continue test");

        peer.disconnect();
        Assertions.assertEquals(ConnectionState.DISCONNECTING, peer.getState());

        listenerConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS);
        Assertions.assertEquals(ConnectionState.DISCONNECTED, peer.getState());
    }

    @Test
    public void METHOD_disconnectByRemote_ON_connected() throws PeerException, InterruptedException, IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        PeerAbs_Impl peer = new PeerAbs_Impl(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket, listenerConnection);
        this.peer = peer;
        if (!listenerConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Peer not connected, can't continue test");

        Socket serverSocket = server.accept();
        DataOutputStream out = new DataOutputStream(serverSocket.getOutputStream());
        out.write(peer.getByeConfigs().getByeMsg());
        out.write(peer.getDataEncodingConfigs().getDelimiter());

        listenerConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS);
        Assertions.assertEquals(ConnectionState.DISCONNECTED, peer.getState());
    }

    // Out of scope because the disconnect()'s exception can be thrown only by closeSocket() abstract method
    //public void METHOD_disconnect_ON_connected_EXCEPTION_PeerException() {
    //    See METHOD_disconnect_ON_connected()
    //}


    // sendData

    @Test
    public void METHOD_sendData() throws PeerException, InterruptedException, IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        PeerAbs_Impl peer = new PeerAbs_Impl(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket, listenerConnection);
        this.peer = peer;
        peer.addListener(listenerData);
        if (!listenerConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Peer not connected, can't continue test");
        peer.sendData(DATA);

        Assertions.assertTrue(listenerData.onDataTx.await(100, TimeUnit.MILLISECONDS));

        Socket remoteSocket = server.accept();
        byte[] readData = new byte[DATA.length];
        int readBytes = new DataInputStream(remoteSocket.getInputStream()).read(readData);

        Assertions.assertEquals(DATA.length, readBytes);
        Assertions.assertArrayEquals(DATA, readData);
    }

    @Test
    public void METHOD_sendData_EXCEPTION_PeerNotConnectedException() {
        Socket socket = new Socket();
        PeerAbs_Impl peer = new PeerAbs_Impl(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket);
        this.peer = peer;

        // call sendData() method when peer is not connected throw an PeerNotConnectedException exception
        PeerNotConnectedException exception = Assertions.assertThrows(PeerNotConnectedException.class, () -> peer.sendData(DATA));

        String expected = String.format("Error on Peer '%s' because socket not connected", peer);
        Assertions.assertEquals(expected, exception.getMessage());
        Assertions.assertEquals(ConnectionState.DISCONNECTED, peer.getState());
    }

    @Test
    public void METHOD_sendData_EXCEPTION_PeerStreamException() throws InterruptedException, IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        PeerAbs_Impl peer = new PeerAbs_Impl(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket, listenerConnection);
        this.peer = peer;
        if (!listenerConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Peer not connected, can't continue test");

        socket.close();

        // call sendData() method when socket is not connected (but peer is) throw an PeerStreamException exception
        PeerException exception = Assertions.assertThrows(PeerException.class, () -> peer.sendData(DATA));
        if (exception instanceof PeerNotConnectedException) {
            System.out.println("Test not fully executed:");
            System.out.println("\tclosing socket caused peer disconnection before test can send data.");
            System.out.println("\tyou can re-execute this test until this message will not displayed");
            return;
        }

        String expected = String.format("Error on Peer '%s''s output stream", peer);
        if (exception.getMessage().contains("<--->") && expected.contains("< | >"))
            expected = expected.replace("< | >", "<--->");
        if (exception.getMessage().contains("< | >") && expected.contains("<--->"))
            expected = expected.replace("<--->", "< | >");
        Assertions.assertTrue(exception instanceof PeerStreamException);
        Assertions.assertEquals(expected, exception.getMessage());
        Assertions.assertTrue(listenerConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, peer.getState());
    }


    // heartBeatListener

    @Test
    public void EVENT_heartbeat_ON_request() throws InterruptedException, IOException, PeerException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        PeerAbs_Impl peer = new PeerAbs_Impl(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket, listenerConnection);
        this.peer = peer;
        peer.addListener(listenerData);
        if (!listenerConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Peer not connected, can't continue test");

        peer.getHeartBeatConfigs().setHBTimeout(50);
        peer.getHeartBeatConfigs().sendHeartBeatReq();  // set HB configs to waiting

        Assertions.assertTrue(listenerData.onDataTx.await(100, TimeUnit.MILLISECONDS));

        Socket remoteSocket = server.accept();
        byte[] readData = new byte[HeartBeatDefault.MSG_HEARTBEAT_REQ.getBytes().length];
        int readBytes = new DataInputStream(remoteSocket.getInputStream()).read(readData);

        Assertions.assertEquals(HeartBeatDefault.MSG_HEARTBEAT_REQ.getBytes().length, readBytes);
        Assertions.assertArrayEquals(HeartBeatDefault.MSG_HEARTBEAT_REQ.getBytes(), readData);
    }

    @Test
    public void EVENT_heartbeat_ON_response() throws InterruptedException, IOException, PeerException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        PeerAbs_Impl peer = new PeerAbs_Impl(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket, listenerConnection);
        this.peer = peer;
        peer.addListener(listenerData);
        if (!listenerConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Peer not connected, can't continue test");

        peer.getHeartBeatConfigs().setHBTimeout(50);
        peer.getHeartBeatConfigs().sendHeartBeatReq();  // set HB configs to waiting state
        Socket remoteSocket = server.accept();
        DataOutputStream out = new DataOutputStream(remoteSocket.getOutputStream());
        out.write(HeartBeatDefault.MSG_HEARTBEAT_RES.getBytes());
        out.write(peer.getDataEncodingConfigs().getDelimiter());
        out.flush();

        if (!listenerData.onDataRx.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Peer not received data, can't continue test");

        int count = 5;
        while (peer.getConnectionInfo().getStats().getHeartBeatReceived() == 0 && count-- > 0)
            JavaThreads.softSleep(5);
        Assertions.assertEquals(1, peer.getConnectionInfo().getStats().getHeartBeatReceived());
    }

    @Test
    public void EVENT_heartbeat_ON_responseTimeout() throws InterruptedException, PeerException, IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        PeerAbs_Impl peer = new PeerAbs_Impl(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket, listenerConnection);
        this.peer = peer;
        peer.addListener(listenerData);
        if (!listenerConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Peer not connected, can't continue test");

        peer.getHeartBeatConfigs().setHBTimeout(50);
        peer.getHeartBeatConfigs().sendHeartBeatReq();  // set HB configs to waiting state

        listenerConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS);
        Assertions.assertEquals(ConnectionState.DISCONNECTED, peer.getState());
    }


    // byeMsglistener

    @Test
    public void EVENT_remoteDisconnection_ON_byeMsg() throws InterruptedException, IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        PeerAbs_Impl peer = new PeerAbs_Impl(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket, listenerConnection);
        this.peer = peer;
        peer.addListener(listenerData);
        if (!listenerConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Peer not connected, can't continue test");

        Socket remoteSocket = server.accept();
        DataOutputStream out = new DataOutputStream(remoteSocket.getOutputStream());
        out.write(ByeMsgDefault.BYE_MSG.getBytes());
        out.write(peer.getDataEncodingConfigs().getDelimiter());
        out.flush();

        Assertions.assertTrue(listenerConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ByeMsgDefault.BYE_MSG.getBytes().length, peer.getConnectionInfo().getStats().getBytesRx());
    }

    @Test
    public void EVENT_remoteDisconnection_ON_noByeMsg() throws InterruptedException, IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        PeerAbs_Impl peer = new PeerAbs_Impl(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket, listenerConnection);
        this.peer = peer;
        peer.addListener(listenerData);
        if (!listenerConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Peer not connected, can't continue test");

        Socket remoteSocket = server.accept();
        remoteSocket.close();

        Assertions.assertTrue(listenerConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertTrue(listenerConnection.onFail.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(0, peer.getConnectionInfo().getStats().getBytesRx());
    }


    // processData

    @Test
    public void METHOD_processData() throws InterruptedException, IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        PeerAbs_Impl peer = new PeerAbs_Impl(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket, listenerConnection);
        this.peer = peer;
        peer.addListener(listenerData);
        if (!listenerConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Peer not connected, can't continue test");

        Socket remoteSocket = server.accept();
        DataOutputStream out = new DataOutputStream(remoteSocket.getOutputStream());
        out.write(DATA);
        out.write(peer.getDataEncodingConfigs().getDelimiter());
        out.flush();

        Assertions.assertTrue(listenerData.onDataRx.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(DATA.length, peer.getConnectionInfo().getStats().getBytesRx());
        int count = 5;
        while (peer.getLastProcessedData() == null && count-- > 0)
            JavaThreads.softSleep(5);
        Assertions.assertArrayEquals(DATA, peer.getLastProcessedData());
    }

    @Test
    public void METHOD_processData_fail() throws InterruptedException, IOException {
        Socket socket = new Socket(server.getInetAddress(), PORT);
        PeerAbs_Impl peer = new PeerAbs_Impl(LOCAL_ID, REMOTE_ID, PROTO_NAME, socket, listenerConnection);
        this.peer = peer;
        peer.addListener(listenerData);
        if (!listenerConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Peer not connected, can't continue test");

        Socket remoteSocket = server.accept();
        DataOutputStream out = new DataOutputStream(remoteSocket.getOutputStream());
        out.write(PeerAbs_Impl.AVOID_PROCESSING);
        out.write(peer.getDataEncodingConfigs().getDelimiter());
        out.flush();

        Assertions.assertTrue(listenerConnection.onFail.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(PeerAbs_Impl.AVOID_PROCESSING.length, peer.getConnectionInfo().getStats().getBytesRx());
    }

}
