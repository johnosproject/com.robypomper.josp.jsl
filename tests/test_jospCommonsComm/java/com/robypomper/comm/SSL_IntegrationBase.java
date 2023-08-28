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

import com.robypomper.comm.client.ClientAbsSSL_Impl;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.server.ServerAbsSSL_Impl;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.comm.trustmanagers.DynAddTrustManager;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.concurrent.TimeUnit;

public class SSL_IntegrationBase extends TCP_IntegrationBase {

    // Class constants

    protected static final String CLIENT_CERT_ID = "clientCertId";
    protected static final String CLIENT_KS_PASS = "654321";
    protected static final String SERVER_CERT_ID = "serverCertId";
    protected static final String SERVER_KS_PASS = "123456";


    // Internal vars

    protected static KeyStore serverKeyStore;
    protected static Certificate serverCertificate;
    protected DynAddTrustManager serverTrustManager;
    protected SSLContext sslServerCtx;
    protected static KeyStore clientKeyStore;
    protected static Certificate clientCertificate;
    protected DynAddTrustManager clientTrustManager;
    protected SSLContext sslClientCtx;


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
        super.setUp();
        try {
            serverTrustManager = new DynAddTrustManager();
            sslServerCtx = JavaSSL.generateSSLContext(serverKeyStore, SERVER_KS_PASS, serverTrustManager);

            clientTrustManager = new DynAddTrustManager();
            sslClientCtx = JavaSSL.generateSSLContext(clientKeyStore, CLIENT_KS_PASS, clientTrustManager);

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    protected void initAndStartServer(boolean requireAuth) {
        try {
            server = new ServerAbsSSL_Impl(SERVER_LOCAL_ID, PORT, PROTO_NAME, sslServerCtx, null, null, requireAuth, false);
            server.addListener(listenerServerState);
            server.addListener(listenerServerClient);
            server.addListener(listenerServerData);
            server.startup();
            if (!listenerServerState.onStartup.await(100, TimeUnit.MILLISECONDS))
                throw new RuntimeException("Server not started, can't continue test");

        } catch (ServerStartupException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void initAndStartServerWithCertSharing() {
        try {
            server = new ServerAbsSSL_Impl(SERVER_LOCAL_ID, PORT, PROTO_NAME, sslServerCtx, serverTrustManager, serverCertificate, true, true);
            server.addListener(listenerServerState);
            server.addListener(listenerServerClient);
            server.addListener(listenerServerData);
            server.startup();
            if (!listenerServerState.onStartup.await(100, TimeUnit.MILLISECONDS))
                throw new RuntimeException("Server not started, can't continue test");

        } catch (ServerStartupException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected ServerClient waitAndGetClientConnectedOnServer() {
        try {
            if (listenerServerClient.onConnect.await(100, TimeUnit.MILLISECONDS)) {
                if (server.getClients().size() > 0)
                    return server.getClients().get(0);
                else
                    throw new RuntimeException("Client connected but immediately disconnected (server side), can't continue test");
            }

        } catch (InterruptedException ignore) {
        }

        throw new RuntimeException("Client not connected (server side), can't continue test");
    }

    protected void initClient() {
        client = new ClientAbsSSL_Impl(CLIENT_LOCAL_ID, CLIENT_SERVER_REMOTE_ID, server.getServerPeerInfo().getAddr(), PORT, PROTO_NAME, sslClientCtx);
        client.addListener(listenerClientConnection);
        client.addListener(listenerClientData);
    }

    protected void initClientWithCertSharing() {
        client = new ClientAbsSSL_Impl(CLIENT_LOCAL_ID, CLIENT_SERVER_REMOTE_ID, server.getServerPeerInfo().getAddr(), PORT, PROTO_NAME, sslClientCtx, clientTrustManager, clientCertificate, true, 5000);
        client.addListener(listenerClientConnection);
        client.addListener(listenerClientData);
    }

    protected void connectClient() {
        try {
            client.connect();
            if (!listenerClientConnection.onConnect.await(100, TimeUnit.MILLISECONDS))
                throw new RuntimeException("Client not connected, can't continue test");

        } catch (InterruptedException | PeerConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    protected void disconnectClient() {
        try {
            client.disconnect();
            if (!listenerClientConnection.onDisconnect.await(100, TimeUnit.MILLISECONDS))
                throw new RuntimeException("Client not disconnected, can't continue test");

        } catch (InterruptedException | PeerDisconnectionException e) {
            throw new RuntimeException(e);
        }
    }

}

