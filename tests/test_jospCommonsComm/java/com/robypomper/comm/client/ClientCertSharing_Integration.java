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

import com.robypomper.comm.behaviours.ByeMsgConfigs;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.ServerException;
import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.peer.PeerConnectionListener_Latch;
import com.robypomper.comm.server.ServerCertSharing;
import com.robypomper.comm.server.ServerClientsListener_Latch;
import com.robypomper.comm.server.ServerState;
import com.robypomper.comm.trustmanagers.DynAddTrustManager;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import org.junit.jupiter.api.*;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * - client connect and disconnect (one request client)
 * - trust manager with added server's cert
 * - only server's cert shared
 */
public class ClientCertSharing_Integration {

    // Class constants

    protected static final String CLIENT_ID = "clientLocalId";
    protected static final String CLIENT_CERT_ID = "clientCertId";
    protected static final String CLIENT_KS_PASS = "654321";
    protected static final String SERVER_ID = "serverLocalId";
    protected static final String SERVER_CERT_ID = "serverCertId";
    protected static final String SERVER_KS_PASS = "123456";
    protected static final int PORT = 10000;


    // Internal vars

    protected ServerCertSharing server;
    protected ClientCertSharing client;
    protected static KeyStore serverKeyStore;
    protected static Certificate serverCertificate;
    protected DynAddTrustManager serverTrustManager;
    protected SSLContext sslServerCtx;
    protected static KeyStore clientKeyStore;
    protected static Certificate clientCertificate;
    protected DynAddTrustManager clientTrustManager;
    protected SSLContext sslClientCtx;
    protected ServerClientsListener_Latch listenerServerClient;
    protected PeerConnectionListener_Latch listenerClientConnection;


    // setup/tear down

    @BeforeAll
    public static void setUpAll() {
        try {
            serverKeyStore = JavaJKS.generateKeyStore(SERVER_CERT_ID, SERVER_KS_PASS, SERVER_CERT_ID);
            serverCertificate = JavaJKS.extractCertificate(serverKeyStore, SERVER_CERT_ID);

            clientKeyStore = JavaJKS.generateKeyStore(CLIENT_CERT_ID, CLIENT_KS_PASS, CLIENT_CERT_ID);
            clientCertificate = JavaJKS.extractCertificate(clientKeyStore, CLIENT_CERT_ID);

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @BeforeEach
    public void setUp() {
        try {
            serverTrustManager = new DynAddTrustManager();
            sslServerCtx = JavaSSL.generateSSLContext(serverKeyStore, SERVER_KS_PASS, serverTrustManager);

            clientTrustManager = new DynAddTrustManager();
            sslClientCtx = JavaSSL.generateSSLContext(clientKeyStore, CLIENT_KS_PASS, clientTrustManager);

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

        listenerServerClient = new ServerClientsListener_Latch();
        listenerClientConnection = new PeerConnectionListener_Latch();
    }

    @AfterEach
    public void tearDown() throws ServerException {
        if (server != null && server.getState() != ServerState.STOPPED) {
            server.shutdown();
        }
    }

    @Test
    public void INTEGRATION_CertSharing_ClientAndServer() throws ServerStartupException, PeerConnectionException, CertificateEncodingException, InterruptedException {
        server = new ServerCertSharing(SERVER_ID + "-CertSharing", null, PORT, null, serverTrustManager, serverCertificate);
        server.addListener(listenerServerClient);
        server.startup();
        client = new ClientCertSharing(CLIENT_ID + "-CertSharing", server.getLocalId(), server.getServerPeerInfo().getAddr(), PORT, null, clientTrustManager, clientCertificate);
        client.addListener(listenerClientConnection);

        Assertions.assertEquals(0, serverTrustManager.getAcceptedIssuers().length);
        Assertions.assertEquals(0, clientTrustManager.getAcceptedIssuers().length);

        client.shareCertificate();

        Assertions.assertTrue(client.waitForDone(100));

        Assertions.assertTrue(listenerClientConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(serverCertificate.getEncoded().length, client.getConnectionInfo().getStats().getBytesRx());
        Assertions.assertEquals(clientCertificate.getEncoded().length + ByeMsgConfigs.BYE_MSG.length(), client.getConnectionInfo().getStats().getBytesTx());

        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(1, serverTrustManager.getAcceptedIssuers().length);
        Assertions.assertEquals(1, clientTrustManager.getAcceptedIssuers().length);
    }

    @Test
    public void INTEGRATION_CertSharing_OnlyServer() throws ServerStartupException, PeerConnectionException, CertificateEncodingException, InterruptedException {
        server = new ServerCertSharing(SERVER_ID + "-CertSharing", null, PORT, null, serverTrustManager, serverCertificate);
        server.addListener(listenerServerClient);
        server.startup();
        client = new ClientCertSharing(CLIENT_ID + "-CertSharing", server.getLocalId(), server.getServerPeerInfo().getAddr(), PORT, null, clientTrustManager, null);
        client.addListener(listenerClientConnection);

        Assertions.assertEquals(0, serverTrustManager.getAcceptedIssuers().length);
        Assertions.assertEquals(0, clientTrustManager.getAcceptedIssuers().length);

        client.shareCertificate();

        Assertions.assertTrue(client.waitForDone(1000));

        Assertions.assertTrue(listenerClientConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(serverCertificate.getEncoded().length, client.getConnectionInfo().getStats().getBytesRx());
        Assertions.assertEquals(ByeMsgConfigs.BYE_MSG.length(), client.getConnectionInfo().getStats().getBytesTx());

        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(0, serverTrustManager.getAcceptedIssuers().length);
        Assertions.assertEquals(1, clientTrustManager.getAcceptedIssuers().length);
    }

}
