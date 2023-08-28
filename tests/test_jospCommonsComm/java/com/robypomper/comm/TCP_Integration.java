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

import com.robypomper.comm.client.Client;
import com.robypomper.comm.client.ClientAbsSSL;
import com.robypomper.comm.client.ClientAbsTCP;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.*;
import com.robypomper.comm.peer.PeerConnectionListener_Latch;
import com.robypomper.comm.peer.PeerDataListener_Latch;
import com.robypomper.comm.server.Server;
import com.robypomper.comm.server.ServerAbsSSL;
import com.robypomper.comm.server.ServerAbsTCP;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.comm.trustmanagers.DynAddTrustManager;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.concurrent.TimeUnit;

@Deprecated
public class TCP_Integration {

    // Class constants

    private static final InetAddress ADDRESS = InetAddress.getLoopbackAddress();
    private static final int CERT_SHARING_TIMEOUT_MS = 5 * 1000;
    private static final String SERVER_KS_PASS = "123456";
    private static final String CLIENT_KS_PASS = "654321";


    // Internal vars

    protected Server server;


    // setup/tear down

    @BeforeEach
    public void setUp() throws IOException {
    }

    @AfterEach
    public void tearDown() throws ServerException {
        server.shutdown();
    }


    @Test
    public void TCP() throws InterruptedException, ServerException, PeerConnectionException, PeerNotConnectedException, PeerStreamException, PeerDisconnectionException {
        server = new ServerTCP_Echo("server", ADDRESS, 10000, "TCP-Echo");
        server.startup();

        ClientAbsTCP client = new ClientTCPPrinter("client", "server(CL)", ADDRESS, 10000, "TCP-Echo");
        PeerConnectionListener_Latch clientConnectionListener = new PeerConnectionListener_Latch();
        PeerDataListener_Latch clientDataListener = new PeerDataListener_Latch();
        client.addListener(clientConnectionListener);
        client.addListener(clientDataListener);

        client.connect();
        if (!clientConnectionListener.onConnect.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not connected after 100 ms, exit");
            return;
        }

        client.sendData("Ciao da client");
        if (!clientDataListener.onDataTx.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not send data after 100 ms, exit");
            return;
        }
        if (!clientDataListener.onDataRx.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not received data after 100 ms, exit");
            return;
        }

        client.disconnect();
        if (!clientConnectionListener.onDisconnect.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not connected after 100 ms, exit");
            return;
        }

        server.shutdown();
    }

    @Test
    public void SSL() throws InterruptedException, JavaSSL.GenerationException, JavaJKS.GenerationException, AbsCustomTrustManager.UpdateException, ServerException, PeerConnectionException, PeerNotConnectedException, PeerStreamException, PeerDisconnectionException {
        String serverId = "id-server-test";
        KeyStore serverKeyStore = JavaJKS.generateKeyStore(serverId, SERVER_KS_PASS, serverId);
        DynAddTrustManager serverTrustManager = new DynAddTrustManager();
        Certificate serverCertificate = JavaJKS.extractCertificate(serverKeyStore, serverId);
        SSLContext sslServerCtx = JavaSSL.generateSSLContext(serverKeyStore, SERVER_KS_PASS, serverTrustManager);

        String clientId = "id-client-test";
        KeyStore clientKeyStore = JavaJKS.generateKeyStore(clientId, CLIENT_KS_PASS, clientId);
        Certificate clientCertificate = JavaJKS.extractCertificate(clientKeyStore, clientId);
        DynAddTrustManager clientTrustManager = new DynAddTrustManager();
        SSLContext sslClientCtx = JavaSSL.generateSSLContext(clientKeyStore, CLIENT_KS_PASS, clientTrustManager);


        //serverTrustManager.addCertificate(clientId, clientCertificate);
        clientTrustManager.addCertificate(serverId, serverCertificate);


        server = new ServerSSL_Echo(serverId, ADDRESS, 10000, "TCP-Echo", sslServerCtx, null, null, false, false);
        server.startup();

        Client client = new ClientSSLPrinter(clientId, "server(CL)", ADDRESS, 10000, "TCP-Echo", sslClientCtx, clientTrustManager, clientCertificate, false, CERT_SHARING_TIMEOUT_MS);
        PeerConnectionListener_Latch clientConnectionListener = new PeerConnectionListener_Latch();
        PeerDataListener_Latch clientDataListener = new PeerDataListener_Latch();
        client.addListener(clientConnectionListener);
        client.addListener(clientDataListener);

        client.connect();
        if (!clientConnectionListener.onConnect.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not connected after 100 ms, exit");
            return;
        }

        client.sendData("Ciao da client");
        if (!clientDataListener.onDataTx.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not send data after 100 ms, exit");
            return;
        }
        if (!clientDataListener.onDataRx.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not received data after 100 ms, exit");
            return;
        }

        client.disconnect();
        if (!clientConnectionListener.onDisconnect.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not connected after 100 ms, exit");
            return;
        }

        server.shutdown();
    }

    @Test
    public void SSL_Auth() throws InterruptedException, JavaSSL.GenerationException, JavaJKS.GenerationException, AbsCustomTrustManager.UpdateException, ServerException, PeerConnectionException, PeerNotConnectedException, PeerStreamException, PeerDisconnectionException {
        String serverId = "id-server-test";
        KeyStore serverKeyStore = JavaJKS.generateKeyStore(serverId, SERVER_KS_PASS, serverId);
        DynAddTrustManager serverTrustManager = new DynAddTrustManager();
        Certificate serverCertificate = JavaJKS.extractCertificate(serverKeyStore, serverId);
        SSLContext sslServerCtx = JavaSSL.generateSSLContext(serverKeyStore, SERVER_KS_PASS, serverTrustManager);

        String clientId = "id-client-test";
        KeyStore clientKeyStore = JavaJKS.generateKeyStore(clientId, CLIENT_KS_PASS, clientId);
        Certificate clientCertificate = JavaJKS.extractCertificate(clientKeyStore, clientId);
        DynAddTrustManager clientTrustManager = new DynAddTrustManager();
        SSLContext sslClientCtx = JavaSSL.generateSSLContext(clientKeyStore, CLIENT_KS_PASS, clientTrustManager);


        serverTrustManager.addCertificate(clientId, clientCertificate);
        clientTrustManager.addCertificate(serverId, serverCertificate);


        server = new ServerSSL_Echo(serverId, ADDRESS, 10000, "TCP-Echo", sslServerCtx, null, null, true, false);
        server.startup();

        Client client = new ClientSSLPrinter(clientId, "server(CL)", ADDRESS, 10000, "TCP-Echo", sslClientCtx, clientTrustManager, clientCertificate, false, CERT_SHARING_TIMEOUT_MS);
        PeerConnectionListener_Latch clientConnectionListener = new PeerConnectionListener_Latch();
        PeerDataListener_Latch clientDataListener = new PeerDataListener_Latch();
        client.addListener(clientConnectionListener);
        client.addListener(clientDataListener);

        client.connect();
        if (!clientConnectionListener.onConnect.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not connected after 100 ms, exit");
            return;
        }

        client.sendData("Ciao da client");
        if (!clientDataListener.onDataTx.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not send data after 100 ms, exit");
            return;
        }
        if (!clientDataListener.onDataRx.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not received data after 100 ms, exit");
            return;
        }

        client.disconnect();
        if (!clientConnectionListener.onDisconnect.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not connected after 100 ms, exit");
            return;
        }

        server.shutdown();
    }

    @Test
    public void SSL_Auth_EXCEPTION() throws JavaSSL.GenerationException, JavaJKS.GenerationException, AbsCustomTrustManager.UpdateException, ServerException {
        String serverId = "id-server-test";
        KeyStore serverKeyStore = JavaJKS.generateKeyStore(serverId, SERVER_KS_PASS, serverId);
        DynAddTrustManager serverTrustManager = new DynAddTrustManager();
        Certificate serverCertificate = JavaJKS.extractCertificate(serverKeyStore, serverId);
        SSLContext sslServerCtx = JavaSSL.generateSSLContext(serverKeyStore, SERVER_KS_PASS, serverTrustManager);

        String clientId = "id-client-test";
        KeyStore clientKeyStore = JavaJKS.generateKeyStore(clientId, CLIENT_KS_PASS, clientId);
        Certificate clientCertificate = JavaJKS.extractCertificate(clientKeyStore, clientId);
        DynAddTrustManager clientTrustManager = new DynAddTrustManager();
        SSLContext sslClientCtx = JavaSSL.generateSSLContext(clientKeyStore, CLIENT_KS_PASS, clientTrustManager);


        //serverTrustManager.addCertificate(clientId, clientCertificate);
        clientTrustManager.addCertificate(serverId, serverCertificate);


        server = new ServerSSL_Echo(serverId, ADDRESS, 10000, "TCP-Echo", sslServerCtx, null, null, true, false);
        server.startup();

        Client client = new ClientSSLPrinter(clientId, "server(CL)", ADDRESS, 10000, "TCP-Echo", sslClientCtx, clientTrustManager, clientCertificate, false, CERT_SHARING_TIMEOUT_MS);
        PeerConnectionListener_Latch clientConnectionListener = new PeerConnectionListener_Latch();
        PeerDataListener_Latch clientDataListener = new PeerDataListener_Latch();
        client.addListener(clientConnectionListener);
        client.addListener(clientDataListener);


        PeerConnectionException exception = Assertions.assertThrows(PeerConnectionException.class, client::connect);

        String expected = String.format("Error on Peer '%s' because SSL handshake failed with '%s:%d'", client.toString().replace("< | >", "<-| >"), ADDRESS.getHostAddress(), 10000);
        Assertions.assertEquals(expected, exception.getMessage());
        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());

        server.shutdown();
    }

    @Test
    public void SSL_Auth_ChertSharing() throws InterruptedException, JavaSSL.GenerationException, JavaJKS.GenerationException, AbsCustomTrustManager.UpdateException, ServerException, PeerConnectionException, PeerNotConnectedException, PeerStreamException, PeerDisconnectionException {
        String serverId = "id-server-test";
        KeyStore serverKeyStore = JavaJKS.generateKeyStore(serverId, SERVER_KS_PASS, serverId);
        DynAddTrustManager serverTrustManager = new DynAddTrustManager();
        Certificate serverCertificate = JavaJKS.extractCertificate(serverKeyStore, serverId);
        SSLContext sslServerCtx = JavaSSL.generateSSLContext(serverKeyStore, SERVER_KS_PASS, serverTrustManager);

        String clientId = "id-client-test";
        KeyStore clientKeyStore = JavaJKS.generateKeyStore(clientId, CLIENT_KS_PASS, clientId);
        Certificate clientCertificate = JavaJKS.extractCertificate(clientKeyStore, clientId);
        DynAddTrustManager clientTrustManager = new DynAddTrustManager();
        SSLContext sslClientCtx = JavaSSL.generateSSLContext(clientKeyStore, CLIENT_KS_PASS, clientTrustManager);


        //serverTrustManager.addCertificate(clientId, clientCertificate);
        clientTrustManager.addCertificate(serverId, serverCertificate);


        server = new ServerSSL_Echo(serverId, ADDRESS, 10000, "TCP-Echo", sslServerCtx, serverTrustManager, serverCertificate, true, true);
        server.startup();

        Client client = new ClientSSLPrinter(clientId, "server(CL)", ADDRESS, 10000, "TCP-Echo", sslClientCtx, clientTrustManager, clientCertificate, true, CERT_SHARING_TIMEOUT_MS);
        PeerConnectionListener_Latch clientConnectionListener = new PeerConnectionListener_Latch();
        PeerDataListener_Latch clientDataListener = new PeerDataListener_Latch();
        client.addListener(clientConnectionListener);
        client.addListener(clientDataListener);


        client.connect();
        if (!clientConnectionListener.onConnect.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not connected after 100 ms, exit");
            return;
        }

        client.sendData("Ciao da client");
        if (!clientDataListener.onDataTx.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not send data after 100 ms, exit");
            return;
        }
        if (!clientDataListener.onDataRx.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not received data after 100 ms, exit");
            return;
        }

        client.disconnect();
        if (!clientConnectionListener.onDisconnect.await(100, TimeUnit.MILLISECONDS)) {
            System.out.println("Error client not connected after 100 ms, exit");
            return;
        }

        server.shutdown();
    }


    public static class ClientTCPPrinter extends ClientAbsTCP {

        public ClientTCPPrinter(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName) {
            super(localId, remoteId, remoteAddr, remotePort, protoName);
        }

        @Override
        protected boolean processData(byte[] data) {
            return false;
        }

        @Override
        protected boolean processData(String data) {
            System.out.println("CLI: " + data);
            return true;
        }

    }

    public static class ServerTCP_Echo extends ServerAbsTCP {

        public ServerTCP_Echo(String localId, InetAddress bindAddr, int bindPort, String protoName) {
            super(localId, bindAddr, bindPort, protoName);
        }

        @Override
        public boolean processData(ServerClient client, byte[] data) {
            try {
                client.sendData(data);

            } catch (PeerException e) {
                System.err.println(String.format("Error on server processing data '%s'", new String(data, getDataEncodingConfigs().getCharset())));
            }
            return true;
        }

        @Override
        public boolean processData(ServerClient client, String data) {
            return false;
        }

    }

    public static class ClientSSLPrinter extends ClientAbsSSL {

        public ClientSSLPrinter(String localId, String remoteId, InetAddress remoteAddr, int remotePort, String protoName,
                                SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate clientCertificate, boolean enableChertSharing, int certSharingTimeoutMs) {
            super(localId, remoteId, remoteAddr, remotePort, protoName,
                    sslCtx, trustManager, clientCertificate, enableChertSharing, certSharingTimeoutMs);
        }

        @Override
        protected boolean processData(byte[] data) {
            return false;
        }

        @Override
        protected boolean processData(String data) {
            System.out.println("CLI: " + data);
            return true;
        }

    }

    public static class ServerSSL_Echo extends ServerAbsSSL {

        public ServerSSL_Echo(String localId, InetAddress bindAddr, int bindPort, String protoName,
                              SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing) {
            super(localId, bindAddr, bindPort, protoName, sslCtx, trustManager, localPublicCertificate, requireAuth, enableCertSharing);
        }

        @Override
        public boolean processData(ServerClient client, byte[] data) {
            try {
                client.sendData(data);

            } catch (PeerException e) {
                System.err.println(String.format("Error on server processing data '%s'", new String(data, getDataEncodingConfigs().getCharset())));
            }
            return true;
        }

        @Override
        public boolean processData(ServerClient client, String data) {
            return false;
        }

    }

}
