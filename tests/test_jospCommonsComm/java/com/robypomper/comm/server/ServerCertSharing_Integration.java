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

import com.robypomper.comm.client.Client;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.ServerException;
import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.peer.PeerConnectionListener_Latch;
import com.robypomper.comm.peer.PeerDataListener_Latch;
import com.robypomper.comm.trustmanagers.DynAddTrustManager;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * - trust manager with added client's cert
 */
public class ServerCertSharing_Integration {

    // Class constants

    protected static final String CLIENT_LOCAL_ID = "clientLocalId";
    protected static final String CLIENT_SERVER_REMOTE_ID = "serverRemoteId(CL)";
    protected static final String CLIENT_KS_PASS = "654321";
    protected static final String CLIENT_CERT_ID = "clientCertId";
    protected static final String SERVER_LOCAL_ID = "serverLocalId-CertSharingTest";
    protected static final String SERVER_CERT_ID = "serverCertId";
    protected static final String SERVER_KS_PASS = "123456";
    protected static final String PROTO_NAME = "ex_tcp";
    protected static final int PORT = 10000;


    // Internal vars

    protected Server server;
    protected Client client;
    protected ServerStateListener_Latch listenerServerState;
    protected ServerClientsListener_Latch listenerServerClient;
    protected ServerDataListener_Latch listenerServerData;
    protected PeerConnectionListener_Latch listenerClientConnection;
    protected PeerDataListener_Latch listenerClientData;
    protected KeyStore serverKeyStore;
    protected DynAddTrustManager serverTrustManager;
    protected Certificate serverCertificate;
    protected SSLContext sslServerCtx;
    protected KeyStore clientKeyStore;
    protected Certificate clientCertificate;
    protected DynAddTrustManager clientTrustManager;
    protected SSLContext sslClientCtx;


    // setup/tear down

    @BeforeEach
    public void setUp() {
        listenerServerState = new ServerStateListener_Latch();
        listenerServerClient = new ServerClientsListener_Latch();
        listenerServerData = new ServerDataListener_Latch();
        listenerClientConnection = new PeerConnectionListener_Latch();
        listenerClientData = new PeerDataListener_Latch();
        try {
            serverKeyStore = JavaJKS.generateKeyStore(SERVER_CERT_ID, SERVER_KS_PASS, SERVER_CERT_ID);
            serverTrustManager = new DynAddTrustManager();
            serverCertificate = JavaJKS.extractCertificate(serverKeyStore, SERVER_CERT_ID);
            sslServerCtx = JavaSSL.generateSSLContext(serverKeyStore, SERVER_KS_PASS, serverTrustManager);

            clientKeyStore = JavaJKS.generateKeyStore(CLIENT_CERT_ID, CLIENT_KS_PASS, CLIENT_CERT_ID);
            clientCertificate = JavaJKS.extractCertificate(clientKeyStore, CLIENT_CERT_ID);
            clientTrustManager = new DynAddTrustManager();
            sslClientCtx = JavaSSL.generateSSLContext(clientKeyStore, CLIENT_KS_PASS, clientTrustManager);

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @AfterEach
    public void tearDown() throws ServerException, InterruptedException, PeerDisconnectionException {
        if (client != null && client.getState() != ConnectionState.DISCONNECTED) {
            listenerClientConnection.onDisconnect = new CountDownLatch(1);
            client.disconnect();
            listenerClientConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS);
        }
        if (server != null && server.getState() != ServerState.STOPPED) {
            listenerServerState.onShutdown = new CountDownLatch(1);
            server.shutdown();
            listenerServerState.onShutdown.await(100, TimeUnit.MILLISECONDS);
        }
    }


    @Test
    public void INTEGRATION_certSharing() throws ServerStartupException {
        ServerCertSharing serverCS = new ServerCertSharing(SERVER_LOCAL_ID, null, PORT, null, serverTrustManager, serverCertificate);
        server = serverCS;

        serverCS.startup();


//        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);
//        clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);
//
//        server = new ServerAbsSSL_Impl(SERVER_LOCAL_ID, InetAddress.getLocalHost(), PORT, PROTO_NAME, sslServerCtx, null, null, false, false);
//        server.addListener(listenerServerState);
//        server.addListener(listenerServerClient);
//        server.addListener(listenerServerData);
//        server.startup();
//        if (!listenerServerState.onStartup.await(100, TimeUnit.MILLISECONDS))
//            throw new RuntimeException("Server not started, can't continue test");
//
//        client = new ClientAbsSSL_Impl(CLIENT_LOCAL_ID, CLIENT_SERVER_REMOTE_ID, server.getServerPeerInfo().getAddr(), PORT, PROTO_NAME, listenerClientConnection, sslClientCtx);
//        client.addListener(listenerClientData);
//
//        Assertions.assertEquals(CLIENT_LOCAL_ID, client.getLocalId());
//        Assertions.assertEquals(CLIENT_SERVER_REMOTE_ID, client.getRemoteId());
//
//        client.connect();
//        if (!listenerClientConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
//            throw new RuntimeException("Client not connected, can't continue test");
    }

}
