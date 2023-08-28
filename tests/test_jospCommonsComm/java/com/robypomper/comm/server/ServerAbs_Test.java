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

import com.robypomper.comm.behaviours.ByeMsgImpl;
import com.robypomper.comm.behaviours.HeartBeatImpl;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.comm.exception.ServerException;
import com.robypomper.comm.exception.ServerStartupException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tested methods:
 * - startup()
 * - shutdown()
 * - clients connections
 * - behaviours configs: dispatch on all clients's peer
 * - client's events: forward data events
 * <p>
 * Indirect tested methods:
 * - constructors: on all methods
 * - doStartup(): on all startup methods
 * - startInfiniteLoop: on all startup methods
 * - client's events: forward connection events on all client connections methods
 * <p>
 * Abstract methods call:
 * - generateServerSocket(): called by doStartup() (no tests methods)
 * <p>
 * Not tested methods:
 * - getters
 * - extractServerClientId()
 */
public class ServerAbs_Test {

    // Class constants

    private static final String LOCAL_ID = "serverLocalId";
    private static final String PROTO_NAME = "ex_tcp";
    private static final int PORT = 10000;


    // Internal vars

    protected ServerAbs server;
    protected Socket client;
    protected ServerStateListener_Latch listenerState;
    protected ServerClientsListener_Latch listenerClient;
    protected ServerDataListener_Latch listenerData;


    // setup/tear down

    @BeforeEach
    public void setUp() throws IOException {
        //server = new ServerSocket(PORT);
        //client = new Socket();
        //client = new Socket(server.getInetAddress(), PORT);
        //client = new Socket(server.getInetAddress(), PORT, InetAddress.getByName(null), PORT + 1);

        listenerState = new ServerStateListener_Latch();
        listenerClient = new ServerClientsListener_Latch();
        listenerData = new ServerDataListener_Latch();
    }

    @AfterEach
    public void tearDown() throws ServerException, InterruptedException, IOException {
        if (server.getState() != ServerState.STOPPED) {
            listenerState.onShutdown = new CountDownLatch(1);
            server.shutdown();
            listenerState.onShutdown.await(100, TimeUnit.MILLISECONDS);
        }
        if (client != null)
            client.close();
    }


    // startup

    @Test
    public void METHOD_startup() throws ServerStartupException, InterruptedException, IOException {
        server = new ServerAbs_Impl(LOCAL_ID, PORT, PROTO_NAME);
        server.addListener(listenerState);

        server.startup();
        Assertions.assertTrue(listenerState.onStartup.await(100, TimeUnit.MILLISECONDS));
        client = new Socket(server.getServerPeerInfo().getAddr(), PORT);
    }

    @Test
    public void METHOD_startup_EXCEPTION() throws ServerStartupException, InterruptedException {
        server = new ServerAbs_Impl(LOCAL_ID, PORT, PROTO_NAME);
        server.addListener(listenerState);

        server.startup();
        Assertions.assertTrue(listenerState.onStartup.await(100, TimeUnit.MILLISECONDS));

        Server server2 = new ServerAbs_Impl(LOCAL_ID, PORT, PROTO_NAME);
        server2.addListener(listenerState);
        ServerStartupException exception = Assertions.assertThrows(ServerStartupException.class, server2::startup);

        String expected = String.format("Can't startup '%s' server because can't bind server's socket to '%s:%d'", server, "null", PORT);
        Assertions.assertEquals(expected, exception.getMessage());
        Assertions.assertEquals(ServerState.STOPPED, server2.getState());
    }


    // shutdown

    @Test
    public void METHOD_shutdown() throws ServerException, InterruptedException {
        server = new ServerAbs_Impl(LOCAL_ID, PORT, PROTO_NAME);
        server.addListener(listenerState);

        server.startup();
        if (!listenerState.onStartup.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Server not started, can't continue test");

        server.shutdown();
        Assertions.assertTrue(listenerState.onShutdown.await(100, TimeUnit.MILLISECONDS));
    }


    // clients connections

    @Test
    public void METHOD_clientConnections_clientConnect_serverShutdown() throws ServerException, InterruptedException, IOException {
        server = new ServerAbs_Impl(LOCAL_ID, PORT, PROTO_NAME);
        server.addListener(listenerState);
        server.addListener(listenerClient);

        server.startup();
        if (!listenerState.onStartup.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Server not started, can't continue test");

        client = new Socket(server.getServerPeerInfo().getAddr(), PORT);
        Assertions.assertTrue(listenerClient.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(1, server.getClients().size());

        server.shutdown();
        listenerState.onShutdown.await(100, TimeUnit.MILLISECONDS);
        Assertions.assertTrue(listenerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(0, server.getClients().size());
    }

    @Test
    public void METHOD_clientConnections_ON_clientConnect_clientDisconnect() throws ServerException, InterruptedException, IOException {
        server = new ServerAbs_Impl(LOCAL_ID, PORT, PROTO_NAME);
        server.addListener(listenerState);
        server.addListener(listenerClient);

        server.startup();
        if (!listenerState.onStartup.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Server not started, can't continue test");

        client = new Socket(server.getServerPeerInfo().getAddr(), PORT);
        Assertions.assertTrue(listenerClient.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(1, server.getClients().size());

        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        out.write(server.getByeConfigs().getByeMsg());
        out.write(server.getDataEncodingConfigs().getDelimiter());
        client.close();

        Assertions.assertTrue(listenerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(0, server.getClients().size());
    }


    // behaviours configs

    @Test
    public void METHOD_behavioursConfigs_ON_notConnectedServerClient() throws ServerException, InterruptedException, IOException {
        server = new ServerAbs_Impl(LOCAL_ID, PORT, PROTO_NAME);
        server.addListener(listenerState);
        server.addListener(listenerClient);
        server.getDataEncodingConfigs().setCharset(StandardCharsets.US_ASCII);
        server.getDataEncodingConfigs().setDelimiter("#-#-#");
        server.getHeartBeatConfigs().setTimeout(1234);
        server.getHeartBeatConfigs().setHBTimeout(1235);
        server.getByeConfigs().setByeMsg("byeByeWorld");

        server.startup();
        if (!listenerState.onStartup.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Server not started, can't continue test");

        client = new Socket(server.getServerPeerInfo().getAddr(), PORT);
        if (!listenerClient.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Client not connected, can't continue test");

        ServerClient serverClient = server.getClients().get(0);
        Assertions.assertEquals(StandardCharsets.US_ASCII, serverClient.getDataEncodingConfigs().getCharset());
        Assertions.assertEquals("#-#-#", serverClient.getDataEncodingConfigs().getDelimiterString());
        Assertions.assertEquals(1234, serverClient.getHeartBeatConfigs().getTimeout());
        Assertions.assertEquals(1235, serverClient.getHeartBeatConfigs().getHBTimeout());
        Assertions.assertEquals("byeByeWorld", serverClient.getByeConfigs().getByeMsgString());
    }

    @Test
    public void METHOD_behavioursConfigs_ON_connectedServerClient() throws ServerException, InterruptedException, IOException {
        server = new ServerAbs_Impl(LOCAL_ID, PORT, PROTO_NAME);
        server.addListener(listenerState);
        server.addListener(listenerClient);

        server.startup();
        if (!listenerState.onStartup.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Server not started, can't continue test");

        client = new Socket(server.getServerPeerInfo().getAddr(), PORT);
        if (!listenerClient.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Client not connected, can't continue test");

        ServerClient serverClient = server.getClients().get(0);
        Assertions.assertEquals(DataEncodingConfigs.CHARSET, serverClient.getDataEncodingConfigs().getCharset());
        Assertions.assertEquals(DataEncodingConfigs.DELIMITER, serverClient.getDataEncodingConfigs().getDelimiterString());
        Assertions.assertEquals(HeartBeatImpl.TIMEOUT_MS, serverClient.getHeartBeatConfigs().getTimeout());
        Assertions.assertEquals(HeartBeatImpl.TIMEOUT_HB_MS, serverClient.getHeartBeatConfigs().getHBTimeout());
        Assertions.assertEquals(ByeMsgImpl.BYE_MSG, serverClient.getByeConfigs().getByeMsgString());

        server.getDataEncodingConfigs().setCharset(StandardCharsets.US_ASCII);
        server.getDataEncodingConfigs().setDelimiter("#-#-#");
        server.getHeartBeatConfigs().setTimeout(1234);
        server.getHeartBeatConfigs().setHBTimeout(1235);
        server.getByeConfigs().setByeMsg("byeByeWorld");

        Assertions.assertEquals(StandardCharsets.US_ASCII, serverClient.getDataEncodingConfigs().getCharset());
        Assertions.assertEquals("#-#-#", serverClient.getDataEncodingConfigs().getDelimiterString());
        Assertions.assertEquals(1234, serverClient.getHeartBeatConfigs().getTimeout());
        Assertions.assertEquals(1235, serverClient.getHeartBeatConfigs().getHBTimeout());
        Assertions.assertEquals("byeByeWorld", serverClient.getByeConfigs().getByeMsgString());
    }


    // client's events

    @Test
    public void METHOD_clientEvents_ON_dataRx() throws ServerException, InterruptedException, IOException {
        server = new ServerAbs_Impl(LOCAL_ID, PORT, PROTO_NAME);
        server.addListener(listenerState);
        server.addListener(listenerClient);
        server.addListener(listenerData);

        server.startup();
        if (!listenerState.onStartup.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Server not started, can't continue test");

        client = new Socket(server.getServerPeerInfo().getAddr(), PORT);
        if (!listenerClient.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Client not connected, can't continue test");

        byte[] msgFromClient = "Data from client".getBytes(server.getDataEncodingConfigs().getCharset());
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        out.write(msgFromClient);
        out.write(server.getDataEncodingConfigs().getDelimiter());
        Assertions.assertTrue(listenerData.onDataRx.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(msgFromClient.length, server.getClients().get(0).getConnectionInfo().getStats().getBytesRx());
    }

    @Test
    public void METHOD_clientEvents_ON_dataTx() throws ServerException, InterruptedException, IOException, PeerStreamException, PeerNotConnectedException {
        server = new ServerAbs_Impl(LOCAL_ID, PORT, PROTO_NAME);
        server.addListener(listenerState);
        server.addListener(listenerClient);
        server.addListener(listenerData);

        server.startup();
        if (!listenerState.onStartup.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Server not started, can't continue test");

        client = new Socket(server.getServerPeerInfo().getAddr(), PORT);
        if (!listenerClient.onConnect.await(100, TimeUnit.MILLISECONDS))
            throw new RuntimeException("Client not connected, can't continue test");

        byte[] msgFromClient = "Data from client".getBytes(server.getDataEncodingConfigs().getCharset());
        server.getClients().get(0).sendData(msgFromClient);
        Assertions.assertTrue(listenerData.onDataTx.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(msgFromClient.length, server.getClients().get(0).getConnectionInfo().getStats().getBytesTx());
    }

}
