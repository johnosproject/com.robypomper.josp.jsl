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

import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.java.JavaThreads;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * Tested interactions:
 * - connect/disconnect: on SSL, SSL Auth and SSL with Cert Sharing
 * - get IDs: on SSL, SSL Auth and SSL with Cert Sharing
 * <p>
 * Not tested interactions (because already tested by ServerTCP_Integration):
 * - connect/disconnect
 * - get IDs
 * - bye msg: TX on server shutdown and RX on client disconnection
 * - heartbeat
 * <p>
 * Not tested interactions (because already tested by ClientTCP_Integration):
 * - data encoding
 */
public class ServerSSL_Integration extends SSL_IntegrationBase {

    // connect

    @Test
    public void METHOD_connect_SSL() throws AbsCustomTrustManager.UpdateException, InterruptedException {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);     // Not required for SSL
        clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSL

        initAndStartServer(false);
        initClient();

        connectClient();

        Assertions.assertTrue(listenerServerClient.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(1, server.getClients().size());

        disconnectClient();

        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(0, server.getClients().size());
    }

    @Test
    public void METHOD_connect_SSL_FAIL() throws InterruptedException {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);     // Not required for SSL
        //clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSL

        initAndStartServer(false);
        initClient();

        try {
            client.connect();
        } catch (PeerConnectionException ignore) {
        }

        Assertions.assertFalse(listenerServerClient.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertFalse(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());

        Assertions.assertEquals(0, server.getClients().size());
    }

    @Test
    public void METHOD_connect_SSLAuth() throws AbsCustomTrustManager.UpdateException, InterruptedException {
        serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);       // Required for SSLAuth
        clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSLAuth

        initAndStartServer(true);
        initClient();

        connectClient();

        Assertions.assertTrue(listenerServerClient.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(1, server.getClients().size());

        disconnectClient();

        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(0, server.getClients().size());
    }

    @Test
    public void METHOD_connect_SSLAuth_FAIL_noSrvCertOnCli() throws AbsCustomTrustManager.UpdateException, InterruptedException {
        serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);       // Required for SSLAuth
        //clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSLAuth

        initAndStartServer(true);
        initClient();

        try {
            client.connect();
        } catch (PeerConnectionException ignore) {
        }

        Assertions.assertFalse(listenerServerClient.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertFalse(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());

        Assertions.assertEquals(0, server.getClients().size());
    }

    @Test
    public void METHOD_connect_SSLAuth_FAIL_noCliCertOnSrv() throws AbsCustomTrustManager.UpdateException, InterruptedException {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);       // Required for SSLAuth
        clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSLAuth

        initAndStartServer(true);
        initClient();

        try {
            client.connect();
        } catch (PeerConnectionException ignore) {
        }

        Assertions.assertFalse(listenerServerClient.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertFalse(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());

        Assertions.assertEquals(0, server.getClients().size());
    }

    @Test
    public void METHOD_connect_SSLAuth_FAIL_noSrvCertOnCli_noCliCertOnSrv() throws AbsCustomTrustManager.UpdateException, InterruptedException {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);       // Required for SSLAuth
        //clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSLAuth

        initAndStartServer(true);
        initClient();

        try {
            client.connect();
        } catch (PeerConnectionException ignore) {
        }

        Assertions.assertFalse(listenerServerClient.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertFalse(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());

        Assertions.assertEquals(0, server.getClients().size());
    }

    @Test
    public void METHOD_connect_SSLAuthCertSharing() throws InterruptedException {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);     // Not required for SSL with CertSharing
        //clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);     // Not required for SSL with CertSharing

        initAndStartServerWithCertSharing();
        initClientWithCertSharing();

        connectClient();

        Assertions.assertTrue(listenerServerClient.onConnect.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertEquals(1, server.getClients().size());

        disconnectClient();

        Assertions.assertTrue(listenerServerClient.onDisconnect.await(100, TimeUnit.MILLISECONDS));
        JavaThreads.softSleep(10);
        Assertions.assertEquals(0, server.getClients().size());
    }


    // ids

    @Test
    public void METHOD_getIDs_SSL() throws AbsCustomTrustManager.UpdateException {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);     // Not required for SSL
        clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSL

        initAndStartServer(false);
        initClient();

        connectClient();

        ServerClient serverClient = waitAndGetClientConnectedOnServer();
        String generatedServerLocalClientId = String.format("%s:%d@%s", serverClient.getSocket().getInetAddress().getHostAddress(), serverClient.getSocket().getPort(), SERVER_LOCAL_ID);
        Assertions.assertEquals(generatedServerLocalClientId, serverClient.getLocalId());
        String generatedServerRemoteClientId = String.format("%s://%s:%d", PROTO_NAME, serverClient.getSocket().getInetAddress().getHostAddress(), serverClient.getSocket().getPort());
        Assertions.assertEquals(generatedServerRemoteClientId, serverClient.getRemoteId());

        disconnectClient();

        Assertions.assertEquals(generatedServerLocalClientId, serverClient.getLocalId());
        Assertions.assertEquals(generatedServerRemoteClientId, serverClient.getRemoteId());
    }

    @Test
    public void METHOD_getIDs_SSLAuth() throws AbsCustomTrustManager.UpdateException {
        serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);       // Required for SSLAuth
        clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSLAuth

        initAndStartServer(true);
        initClient();

        connectClient();

        ServerClient serverClient = waitAndGetClientConnectedOnServer();
        String generatedServerLocalClientId = String.format("%s:%d@%s", serverClient.getSocket().getInetAddress().getHostAddress(), serverClient.getSocket().getPort(), SERVER_LOCAL_ID);
        Assertions.assertEquals(generatedServerLocalClientId, serverClient.getLocalId());
        Assertions.assertEquals(CLIENT_CERT_ID, serverClient.getRemoteId());

        disconnectClient();

        Assertions.assertEquals(generatedServerLocalClientId, serverClient.getLocalId());
        Assertions.assertEquals(CLIENT_CERT_ID, serverClient.getRemoteId());
    }

    @Test
    public void METHOD_getIDs_SSLAuthCertSharing() {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);     // Not required for SSL with CertSharing
        //clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);     // Not required for SSL with CertSharing

        initAndStartServerWithCertSharing();
        initClientWithCertSharing();

        connectClient();

        ServerClient serverClient = waitAndGetClientConnectedOnServer();
        String generatedServerLocalClientId = String.format("%s:%d@%s", serverClient.getSocket().getInetAddress().getHostAddress(), serverClient.getSocket().getPort(), SERVER_LOCAL_ID);
        Assertions.assertEquals(generatedServerLocalClientId, serverClient.getLocalId());
        Assertions.assertEquals(CLIENT_CERT_ID, serverClient.getRemoteId());

        disconnectClient();

        Assertions.assertEquals(generatedServerLocalClientId, serverClient.getLocalId());
        Assertions.assertEquals(CLIENT_CERT_ID, serverClient.getRemoteId());
    }

}
