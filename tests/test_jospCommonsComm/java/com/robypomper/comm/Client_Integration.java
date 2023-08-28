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
import com.robypomper.comm.behaviours.HeartBeatListener_Latch;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.comm.peer.DisconnectionReason;
import com.robypomper.comm.server.ServerAbs_Impl;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.java.JavaThreads;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tested interactions:
 * - connect/disconnect
 * - get IDs
 * - bye msg: TX on client disconnection and RX on server shutdown
 * - data encoding
 * - heartbeat
 * - re-connect: on missing server and on heartbeat timeout
 */
public class Client_Integration extends TCP_IntegrationBase {

    // connect

    @Test
    public void METHOD_connect() {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();

        System.out.println("Check client status = CONNECTED");
        Assertions.assertEquals(ConnectionState.CONNECTED, client.getState());

        System.out.println("Disconnect client");
        disconnectClient();

        System.out.println("Check client status = DISCONNECTED by LOCAL_REQUEST");
        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());
        Assertions.assertEquals(DisconnectionReason.LOCAL_REQUEST, client.getDisconnectionReason());
    }

    @Test
    public void METHOD_connect_onServerShutdown() throws InterruptedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();

        System.out.println("Check client status = CONNECTED");
        Assertions.assertEquals(ConnectionState.CONNECTED, client.getState());

        System.out.println("Stop server");
        JavaThreads.initAndStart(new Runnable() {
            @Override
            public void run() {
                shutdownServer();
            }
        }, "ShutdownServer");

        System.out.println("Check client status = DISCONNECTED by REMOTE_REQUEST");
        Assertions.assertTrue(listenerClientConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());
        Assertions.assertEquals(DisconnectionReason.REMOTE_REQUEST, client.getDisconnectionReason());
    }

    @Test
    public void METHOD_connect_onServerBrokeConnection() throws IOException, InterruptedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();
        ServerClient serverClient = waitAndGetClientConnectedOnServer();

        System.out.println("Check client status = CONNECTED");
        Assertions.assertEquals(ConnectionState.CONNECTED, client.getState());

        System.out.println("Break server's client connection");
        serverClient.getSocket().close();

        System.out.println("Check client status = DISCONNECTED by CONNECTION_LOST");
        Assertions.assertTrue(listenerClientConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());
        Assertions.assertEquals(DisconnectionReason.CONNECTION_LOST, client.getDisconnectionReason());
    }


    // ids

    @Test
    public void METHOD_getIDs() {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Check client local id before connection");
        Assertions.assertEquals(CLIENT_LOCAL_ID, client.getLocalId());

        System.out.println("Check client remote id before connection");
        Assertions.assertEquals(CLIENT_SERVER_REMOTE_ID, client.getRemoteId());

        System.out.println("Connect client");
        connectClient();

        System.out.println("Check client local id after connection");
        Assertions.assertEquals(CLIENT_LOCAL_ID, client.getLocalId());

        System.out.println("Check client remote id after connection");
        String remoteId = String.format("%s://%s:%d", client.getConnectionInfo().getProtocolName(), client.getConnectionInfo().getRemoteInfo().getAddr().getHostAddress(), client.getConnectionInfo().getRemoteInfo().getPort());
        Assertions.assertEquals(remoteId, client.getRemoteId());

        System.out.println("Disconnect client");
        disconnectClient();

        System.out.println("Check client local id after disconnection");
        Assertions.assertEquals(CLIENT_LOCAL_ID, client.getLocalId());

        System.out.println("Check client remote id after disconnection");
        Assertions.assertEquals(remoteId, client.getRemoteId());
    }


    // byeMsg

    @Test
    public void INTEGRATION_byeMsg_onClientDisconnect() {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();

        System.out.println("Check client has tx 0 byte of data");
        Assertions.assertEquals(0, client.getConnectionInfo().getStats().getBytesTx());

        System.out.println("Disconnect client and send BYE_MSG to server");
        disconnectClient();

        System.out.println("Check client has tx BYE_MSG.length byte of data");
        Assertions.assertEquals(ByeMsgConfigs.BYE_MSG.length(), client.getConnectionInfo().getStats().getBytesTx());
    }

    @Test
    public void INTEGRATION_byeMsg_onServerShutdown() throws InterruptedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();

        System.out.println("Check client has rx 0 byte of data");
        Assertions.assertEquals(0, client.getConnectionInfo().getStats().getBytesRx());

        System.out.println("Stop server and send BYE_MSG to client");
        JavaThreads.initAndStart(new Runnable() {
            @Override
            public void run() {
                shutdownServer();
            }
        }, "ShutdownServer");

        System.out.println("Check client has rx BYE_MSG.length byte of data");
        Assertions.assertTrue(listenerClientConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ByeMsgConfigs.BYE_MSG.length(), client.getConnectionInfo().getStats().getBytesRx());
    }


    // data encoding

    @Test
    public void INTEGRATION_dataEncodingCharset_beforeClientConnect() throws InterruptedException, PeerStreamException, PeerNotConnectedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Set DataEncoding Charset before client connection");
        server.getDataEncodingConfigs().setCharset(StandardCharsets.UTF_16LE);       // Default: StandardCharsets.UTF_8
        client.getDataEncodingConfigs().setCharset(StandardCharsets.UTF_16LE);       // Default: StandardCharsets.UTF_8

        System.out.println("Connect client");
        connectClient();

        System.out.println("Send string data to server");
        client.sendData(STR_DATA);
        listenerServerData.onDataRx.await(100, TimeUnit.MILLISECONDS);
        JavaThreads.softSleep(10);  // sleep required to process data

        System.out.println("Check server rx string data");
        Assertions.assertEquals(STR_DATA, ((ServerAbs_Impl) server).lastDataRx);

        System.out.println("Disconnect client");
        disconnectClient();
    }

    @Test
    public void INTEGRATION_dataEncodingCharset_afterClientConnect() throws InterruptedException, PeerStreamException, PeerNotConnectedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();

        System.out.println("Set DataEncoding Charset after client connection");
        server.getDataEncodingConfigs().setCharset(StandardCharsets.UTF_16LE);       // Default: StandardCharsets.UTF_8
        client.getDataEncodingConfigs().setCharset(StandardCharsets.UTF_16LE);       // Default: StandardCharsets.UTF_8

        System.out.println("Send string data to server");
        client.sendData(STR_DATA);
        listenerServerData.onDataRx.await(100, TimeUnit.MILLISECONDS);
        JavaThreads.softSleep(10);  // sleep required to process data

        System.out.println("Check server rx string data");
        Assertions.assertEquals(STR_DATA, ((ServerAbs_Impl) server).lastDataRx);

        System.out.println("Disconnect client");
        disconnectClient();
    }

    @Test
    public void INTEGRATION_dataEncodingCharset_FAIL_beforeClientConnect() throws InterruptedException, PeerStreamException, PeerNotConnectedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Set DataEncoding Charset only to server");
        server.getDataEncodingConfigs().setCharset(StandardCharsets.UTF_16LE);       // Default: StandardCharsets.UTF_8
        //client.getDataEncodingConfigs().setCharset(StandardCharsets.UTF_16LE);       // Default: StandardCharsets.UTF_8

        System.out.println("Connect client");
        connectClient();

        System.out.println("Send string data to server");
        client.sendData(STR_DATA);
        listenerServerData.onDataRx.await(100, TimeUnit.MILLISECONDS);
        JavaThreads.softSleep(10);  // sleep required to process data

        System.out.println("Check server rx NO data");
        Assertions.assertNull(((ServerAbs_Impl) server).lastDataRx);

        System.out.println("Check emitted onFail");
        //...todo

        System.out.println("Disconnect client");
        disconnectClient();
    }

    @Test
    public void INTEGRATION_dataEncodingCharset_FAIL_afterClientConnect() throws InterruptedException, PeerStreamException, PeerNotConnectedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();

        System.out.println("Send string data to server (1st attempt success)");
        client.sendData(STR_DATA);
        listenerServerData.onDataRx.await(100, TimeUnit.MILLISECONDS);
        JavaThreads.softSleep(10);  // sleep required to process data
        Assertions.assertEquals(STR_DATA, ((ServerAbs_Impl) server).lastDataRx);

        System.out.println("Set DataEncoding Charset only to server");
        server.getDataEncodingConfigs().setCharset(StandardCharsets.UTF_16LE);       // Default: StandardCharsets.UTF_8
        //client.getDataEncodingConfigs().setCharset(StandardCharsets.UTF_16LE);       // Default: StandardCharsets.UTF_8
        ((ServerAbs_Impl) server).lastDataRx = null;

        System.out.println("Send string data to server");
        client.sendData(STR_DATA);
        listenerServerData.onDataRx.await(100, TimeUnit.MILLISECONDS);
        JavaThreads.softSleep(10);  // sleep required to process data

        System.out.println("Check server rx NO data");
        Assertions.assertNull(((ServerAbs_Impl) server).lastDataRx);

        System.out.println("Check emitted onFail");
        //...todo

        System.out.println("Disconnect client");
        disconnectClient();
    }

    @Test
    public void INTEGRATION_dataEncodingDelimiter_beforeClientConnect() throws InterruptedException, PeerStreamException, PeerNotConnectedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Set DataEncoding delimiter before client connection");
        server.getDataEncodingConfigs().setDelimiter(STR_DELIMITER);
        client.getDataEncodingConfigs().setDelimiter(STR_DELIMITER);

        System.out.println("Connect client");
        connectClient();

        System.out.println("Send string data to server");
        client.sendData(STR_DATA);
        listenerServerData.onDataRx.await(100, TimeUnit.MILLISECONDS);
        JavaThreads.softSleep(10);  // sleep required to process data

        System.out.println("Check server rx string data");
        Assertions.assertEquals(STR_DATA, ((ServerAbs_Impl) server).lastDataRx);

        System.out.println("Disconnect client");
        disconnectClient();
    }

    @Test
    public void INTEGRATION_dataEncodingDelimiter_afterClientConnect() throws InterruptedException, PeerStreamException, PeerNotConnectedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();

        System.out.println("Set DataEncoding delimiter after client connection");
        server.getDataEncodingConfigs().setDelimiter(STR_DELIMITER);
        client.getDataEncodingConfigs().setDelimiter(STR_DELIMITER);

        System.out.println("Send string data to server");
        client.sendData(STR_DATA);
        listenerServerData.onDataRx.await(100, TimeUnit.MILLISECONDS);
        JavaThreads.softSleep(10);  // sleep required to process data

        System.out.println("Check server rx string data");
        Assertions.assertEquals(STR_DATA, ((ServerAbs_Impl) server).lastDataRx);

        System.out.println("Disconnect client");
        disconnectClient();
    }

    @Test
    public void INTEGRATION_dataEncodingDelimiter_FAIL_beforeClientConnect() throws InterruptedException, PeerStreamException, PeerNotConnectedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Set DataEncoding delimiter only to server");
        server.getDataEncodingConfigs().setDelimiter(STR_DELIMITER);
        //client.getDataEncodingConfigs().setDelimiter(STR_DELIMITER);

        System.out.println("Connect client");
        connectClient();

        System.out.println("Send string data to server");
        client.sendData(STR_DATA);
        listenerServerData.onDataRx.await(100, TimeUnit.MILLISECONDS);
        JavaThreads.softSleep(10);  // sleep required to process data

        System.out.println("Check server rx NO data");
        Assertions.assertNull(((ServerAbs_Impl) server).lastDataRx);

        System.out.println("Check emitted onFail");
        //...todo

        System.out.println("Disconnect client");
        disconnectClient();
    }

    @Test
    public void INTEGRATION_dataEncodingDelimiter_FAIL_afterClientConnect() throws InterruptedException, PeerStreamException, PeerNotConnectedException {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();

        System.out.println("Connect client");
        connectClient();
        client.sendData(STR_DATA);

        System.out.println("Send string data to server (1st attempt success)");
        listenerServerData.onDataRx.await(100, TimeUnit.MILLISECONDS);
        JavaThreads.softSleep(10);  // sleep required to process data
        Assertions.assertEquals(STR_DATA, ((ServerAbs_Impl) server).lastDataRx);

        System.out.println("Set DataEncoding delimiter only to server");
        server.getDataEncodingConfigs().setDelimiter(STR_DELIMITER);
        //client.getDataEncodingConfigs().setDelimiter(STR_DELIMITER);
        ((ServerAbs_Impl) server).lastDataRx = null;

        System.out.println("Send string data to server");
        client.sendData(STR_DATA);
        listenerServerData.onDataRx.await(100, TimeUnit.MILLISECONDS);
        JavaThreads.softSleep(10);  // sleep required to process data

        System.out.println("Check server rx NO data");
        Assertions.assertNull(((ServerAbs_Impl) server).lastDataRx);

        System.out.println("Check emitted onFail");
        //...todo

        System.out.println("Disconnect client");
        disconnectClient();
    }


    // heartbeat

    @Test
    public void INTEGRATION_heartbeat() {
        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Create client");
        initClient();
        client.getHeartBeatConfigs().setTimeout(HB_TIMEOUT);
        client.getHeartBeatConfigs().setHBTimeout(HB_HB_TIMEOUT);

        System.out.println("Connect client");
        connectClient();

        System.out.println("Check client has rx 0 HB messages");
        Assertions.assertEquals(0, client.getConnectionInfo().getStats().getHeartBeatReceived());
        Assertions.assertNull(client.getConnectionInfo().getStats().getLastHeartBeat());

        System.out.println("Wait for client HB request success");
        JavaThreads.softSleep(HB_TIMEOUT + HB_HB_TIMEOUT);  // +10

        System.out.println("Check client has rx some HB messages");
        Assertions.assertTrue(0 < client.getConnectionInfo().getStats().getHeartBeatReceived());
        Assertions.assertNotNull(client.getConnectionInfo().getStats().getLastHeartBeat());

        System.out.println("Check client status = CONNECTED");
        Assertions.assertEquals(ConnectionState.CONNECTED, client.getState());
    }

    @Test
    public void INTEGRATION_heartbeat_FAIL_onHBDisabledServer() throws InterruptedException {
        System.out.println("Start server");
        initAndStartServer();
        server.getHeartBeatConfigs().enableHBResponse(false);

        System.out.println("Create client");
        initClient();
        client.getHeartBeatConfigs().setTimeout(HB_TIMEOUT);
        client.getHeartBeatConfigs().setHBTimeout(HB_HB_TIMEOUT);

        System.out.println("Connect client");
        connectClient();

        System.out.println("Check client has rx 0 HB messages");
        Assertions.assertEquals(0, client.getConnectionInfo().getStats().getHeartBeatReceived());
        Assertions.assertNull(client.getConnectionInfo().getStats().getLastHeartBeat());

        System.out.println("Wait for client HB request failure");
        JavaThreads.softSleep(HB_TIMEOUT + HB_HB_TIMEOUT);

        System.out.println("Check client has rx 0 HB messages");
        Assertions.assertEquals(0, client.getConnectionInfo().getStats().getHeartBeatReceived());
        Assertions.assertNull(client.getConnectionInfo().getStats().getLastHeartBeat());

        System.out.println("Check server's client status = DISCONNECTED");
        Assertions.assertTrue(listenerClientConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());
        Assertions.assertEquals(DisconnectionReason.HEARTBEAT_TIMEOUT, client.getDisconnectionReason());
    }


    // re-Connect

    @Test
    public void INTEGRATION_reConnect_ON_notExistingServer() throws InterruptedException {
        System.out.println("Create client");
        initClientOnLocalhost();
        client.getAutoReConnectConfigs().enable(true);
        client.getAutoReConnectConfigs().setDelay(RECONNECT_DELAY);

        System.out.println("Connect client throw PeerConnectionException");
        Assertions.assertThrows(PeerConnectionException.class, client::connect);

        System.out.println("Check client status = WAITING_SERVER");
        Assertions.assertEquals(ConnectionState.WAITING_SERVER, client.getState());

        System.out.println("Start server");
        initAndStartServer();

        System.out.println("Check client status = CONNECTED");
        Assertions.assertTrue(listenerClientConnection.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.CONNECTED, client.getState());
    }

    @Test
    public void INTEGRATION_reConnect_ON_HeartbeatTimeout() throws InterruptedException {
        System.out.println("Start server");
        initAndStartServer();
        server.getHeartBeatConfigs().enableHBResponse(false);

        System.out.println("Create client");
        initClient();
        client.getAutoReConnectConfigs().enable(true);
        client.getHeartBeatConfigs().setTimeout(HB_TIMEOUT);
        client.getHeartBeatConfigs().setHBTimeout(HB_HB_TIMEOUT);
        HeartBeatListener_Latch listenerHeartbeat = new HeartBeatListener_Latch();
        client.getHeartBeatConfigs().addListener(listenerHeartbeat);

        System.out.println("Connect client");
        connectClient();

        System.out.println("Check client status = CONNECTED");
        Assertions.assertEquals(ConnectionState.CONNECTED, client.getState());
        listenerClientConnection.onConnect = new CountDownLatch(1);

        System.out.println("Wait for HB failure and client disconnection");
        Assertions.assertTrue(listenerHeartbeat.onFail.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertTrue(listenerClientConnection.onFail.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertTrue(listenerClientConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS));

        System.out.println("Check client status = DISCONNECTED by HEARTBEAT_TIMEOUT");
        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        //Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());     // Client is already connecting
        Assertions.assertEquals(DisconnectionReason.HEARTBEAT_TIMEOUT, client.getDisconnectionReason());
        listenerClientConnection.onDisconnect = new CountDownLatch(1);
        listenerServerClient.onDisconnect = new CountDownLatch(1);

        System.out.println("Check client status = CONNECTED");
        Assertions.assertTrue(listenerClientConnection.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.CONNECTED, client.getState());
    }

}
