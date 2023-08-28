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

import com.robypomper.comm.behaviours.ByeMsgConfigs;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.peer.DisconnectionReason;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.java.JavaThreads;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Tested interactions:
 * - connect/disconnect
 * - get IDs
 * - bye msg: TX on server shutdown and RX on client disconnection
 * - heartbeat
 * <p>
 * Not tested interactions (because already tested by ClientTCP_Integration):
 * - data encoding
 */
public class Server_Integration extends TCP_IntegrationBase {

    // connect

    @Test
    public void METHOD_connect() throws InterruptedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();
        ServerClient serverClient = waitAndGetClientConnectedOnServer();

        System.out.println("Check server's clients count = 1");
        Assertions.assertTrue(listenerServerClient.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(1, server.getClients().size());

        System.out.println("Disconnect client");
        disconnectClient();

        System.out.println("Check server's clients count = 0");
        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(0, server.getClients().size());

        System.out.println("Check server's client status = DISCONNECTED by REMOTE_REQUEST");
        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, serverClient.getState());
        Assertions.assertEquals(DisconnectionReason.REMOTE_REQUEST, serverClient.getDisconnectionReason());
    }

    @Test
    public void METHOD_connect_onServerShutdown() throws InterruptedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();
        ServerClient serverClient = waitAndGetClientConnectedOnServer();

        System.out.println("Check server's clients count = 1");
        Assertions.assertTrue(listenerServerClient.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(1, server.getClients().size());

        System.out.println("Stop server");
        shutdownServer();

        System.out.println("Check server's clients count = 0");
        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(0, server.getClients().size());

        System.out.println("Check server's client status = DISCONNECTED by LOCAL_REQUEST");
        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, serverClient.getState());
        Assertions.assertEquals(DisconnectionReason.LOCAL_REQUEST, serverClient.getDisconnectionReason());
    }

    @Test
    public void METHOD_connect_onClientBrokeConnection() throws InterruptedException, IOException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();
        ServerClient serverClient = waitAndGetClientConnectedOnServer();

        System.out.println("Check server's clients count = 1");
        Assertions.assertTrue(listenerServerClient.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(1, server.getClients().size());

        System.out.println("Break client connection");
        client.getSocket().close();

        System.out.println("Check server's clients count = 0");
        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(0, server.getClients().size());

        System.out.println("Check server's client status = DISCONNECTED by CONNECTION_LOST");
        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, serverClient.getState());
        Assertions.assertEquals(DisconnectionReason.CONNECTION_LOST, serverClient.getDisconnectionReason());
    }


    // ids

    @Test
    public void METHOD_getIDs() throws InterruptedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();
        ServerClient serverClient = waitAndGetClientConnectedOnServer();

        System.out.println("Check server's client local id after connection");
        String generatedServerLocalClientId = String.format("%s:%d@%s", serverClient.getSocket().getInetAddress().getHostAddress(), serverClient.getSocket().getPort(), SERVER_LOCAL_ID);
        Assertions.assertEquals(generatedServerLocalClientId, serverClient.getLocalId());

        System.out.println("Check server's client remote id after connection");
        String generatedServerRemoteClientId = String.format("%s://%s:%d", PROTO_NAME, serverClient.getSocket().getInetAddress().getHostAddress(), serverClient.getSocket().getPort());
        Assertions.assertEquals(generatedServerRemoteClientId, serverClient.getRemoteId());

        System.out.println("Disconnect client");
        disconnectClient();

        System.out.println("Check server's client local id after disconnection");
        Assertions.assertEquals(generatedServerLocalClientId, serverClient.getLocalId());

        System.out.println("Check server's client remote id after disconnection");
        Assertions.assertEquals(generatedServerRemoteClientId, serverClient.getRemoteId());

        System.out.println("Check server's client status = DISCONNECTED by REMOTE_REQUEST");
        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, serverClient.getState());
        Assertions.assertEquals(DisconnectionReason.REMOTE_REQUEST, serverClient.getDisconnectionReason());
    }


    // byeMsg

    @Test
    public void INTEGRATION_byeMsg_onServerShutdown() throws InterruptedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();
        ServerClient serverClient = waitAndGetClientConnectedOnServer();


        System.out.println("Check server's client has tx 0 byte of data");
        Assertions.assertEquals(0, serverClient.getConnectionInfo().getStats().getBytesTx());

        System.out.println("Stop server and send BYE_MSG to client");
        shutdownServer();

        System.out.println("Check server's client has tx BYE_MSG.length byte of data");
        Assertions.assertEquals(ByeMsgConfigs.BYE_MSG.length(), serverClient.getConnectionInfo().getStats().getBytesTx());

        System.out.println("Check server's client status = DISCONNECTED by LOCAL_REQUEST");
        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, serverClient.getState());
        Assertions.assertEquals(DisconnectionReason.LOCAL_REQUEST, serverClient.getDisconnectionReason());
    }

    @Test
    public void INTEGRATION_byeMsg_onClientDisconnect() throws InterruptedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();
        ServerClient serverClient = waitAndGetClientConnectedOnServer();

        System.out.println("Check server's client has rx 0 byte of data");
        Assertions.assertEquals(0, serverClient.getConnectionInfo().getStats().getBytesRx());

        System.out.println("Disconnect client and send BYE_MSG to server");
        disconnectClient();

        System.out.println("Check server's client has rx BYE_MSG.length byte of data");
        Assertions.assertEquals(ByeMsgConfigs.BYE_MSG.length(), serverClient.getConnectionInfo().getStats().getBytesRx());

        System.out.println("Check server's client status = DISCONNECTED by REMOTE_REQUEST");
        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, serverClient.getState());
        Assertions.assertEquals(DisconnectionReason.REMOTE_REQUEST, serverClient.getDisconnectionReason());
    }


    // heartbeat

    @Test
    public void INTEGRATION_heartbeat() {
        System.out.println("Start server");
        initAndStartServer();
        server.getHeartBeatConfigs().setTimeout(HB_TIMEOUT);
        server.getHeartBeatConfigs().setHBTimeout(HB_HB_TIMEOUT);

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();
        ServerClient serverClient = waitAndGetClientConnectedOnServer();

        System.out.println("Check server's client has rx 0 HB messages");
        Assertions.assertEquals(0, serverClient.getConnectionInfo().getStats().getHeartBeatReceived());
        Assertions.assertNull(serverClient.getConnectionInfo().getStats().getLastHeartBeat());

        System.out.println("Wait for client HB request success");
        JavaThreads.softSleep(HB_TIMEOUT + HB_HB_TIMEOUT);

        System.out.println("Check server's client has rx some HB messages");
        Assertions.assertTrue(0 < serverClient.getConnectionInfo().getStats().getHeartBeatReceived());
        Assertions.assertNotNull(serverClient.getConnectionInfo().getStats().getLastHeartBeat());

        System.out.println("Check server's client status = CONNECTED");
        Assertions.assertEquals(ConnectionState.CONNECTED, serverClient.getState());
    }

    @Test
    public void INTEGRATION_heartbeat_FAIL_onHBDisabledClient() throws InterruptedException {
        System.out.println("Start server");
        initAndStartServer();
        server.getHeartBeatConfigs().setTimeout(HB_TIMEOUT);
        server.getHeartBeatConfigs().setHBTimeout(HB_HB_TIMEOUT);

        System.out.println("Create client");
        initClient();
        client.getHeartBeatConfigs().enableHBResponse(false);

        System.out.println("Connect client");
        connectClient();
        ServerClient serverClient = waitAndGetClientConnectedOnServer();

        System.out.println("Check server's client has rx 0 HB messages");
        Assertions.assertEquals(0, serverClient.getConnectionInfo().getStats().getHeartBeatReceived());
        Assertions.assertNull(serverClient.getConnectionInfo().getStats().getLastHeartBeat());

        System.out.println("Wait for client HB request failure");
        JavaThreads.softSleep(HB_TIMEOUT + HB_HB_TIMEOUT);

        System.out.println("Check server's client has rx 0 HB messages");
        Assertions.assertEquals(0, serverClient.getConnectionInfo().getStats().getHeartBeatReceived());
        Assertions.assertNull(serverClient.getConnectionInfo().getStats().getLastHeartBeat());

        System.out.println("Check server's client status = DISCONNECTED by HEARTBEAT_TIMEOUT");
        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, serverClient.getState());
        Assertions.assertEquals(DisconnectionReason.HEARTBEAT_TIMEOUT, serverClient.getDisconnectionReason());
    }

}
