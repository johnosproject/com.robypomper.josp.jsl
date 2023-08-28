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
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.java.JavaVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLException;
import java.net.SocketException;

/**
 * Tested interactions:
 * - connect/disconnect: on SSL, SSL Auth and SSL with Cert Sharing
 * - get IDs: on SSL, SSL Auth and SSL with Cert Sharing
 * <p>
 * Not tested interactions (because already tested by ClientTCP_Integration):
 * - connect/disconnect
 * - get IDs
 * - bye msg: TX on client disconnection and RX on server shutdown
 * - data encoding
 * - heartbeat
 * - re-connect: on missing server and on heartbeat timeout
 */
public class ClientSSL_Integration extends SSL_IntegrationBase {

    // connect

    @Test
    public void METHOD_connect_SSL() throws AbsCustomTrustManager.UpdateException {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);     // Not required for SSL
        clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSL

        initAndStartServer(false);
        initClient();
        connectClient();

        Assertions.assertEquals(ConnectionState.CONNECTED, client.getState());

        disconnectClient();

        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());
    }

    @Test
    public void METHOD_connect_SSL_EXCEPTION_PeerConnectionException() {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);     // Not required for SSL
        //clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSL

        initAndStartServer(false);
        initClient();

        PeerConnectionException exception = Assertions.assertThrows(PeerConnectionException.class, client::connect);

        String expected = String.format("Error on Peer '%s' because SSL handshake failed with '%s:%d'", client.toString().replace("< | >", "<-| >"), server.getServerPeerInfo().getAddr().getHostAddress(), 10000);
        Assertions.assertEquals(expected, exception.getMessage());

        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());
    }

    @Test
    public void METHOD_connect_SSLAuth() throws AbsCustomTrustManager.UpdateException {
        serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);       // Required for SSLAuth
        clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSLAuth

        initAndStartServer(true);
        initClient();
        connectClient();

        Assertions.assertEquals(ConnectionState.CONNECTED, client.getState());

        disconnectClient();

        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());
    }

    @Test
    public void METHOD_connect_SSLAuth_EXCEPTION_PeerConnectionException_noSrvCertOnCli() throws AbsCustomTrustManager.UpdateException {
        serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);       // Required for SSLAuth
        //clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSLAuth

        initAndStartServer(true);
        initClient();

        PeerConnectionException exception = Assertions.assertThrows(PeerConnectionException.class, client::connect);

        String expected = String.format("Error on Peer '%s' because SSL handshake failed with '%s:%d'", client.toString().replace("< | >", "<-| >"), server.getServerPeerInfo().getAddr().getHostAddress(), 10000);
        Assertions.assertEquals(expected, exception.getMessage());

        Assertions.assertTrue(exception.getCause() instanceof SSLException);
        if (JavaVersion.JAVA_CURRENT.greaterEqual(JavaVersion.JAVA_11))
            Assertions.assertEquals("Unexpected error: java.security.InvalidAlgorithmParameterException: the trustAnchors parameter must be non-empty", exception.getCause().getMessage());
        else
            Assertions.assertEquals("java.lang.RuntimeException: Unexpected error: java.security.InvalidAlgorithmParameterException: the trustAnchors parameter must be non-empty", exception.getCause().getMessage());

        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());
    }

    @Test
    public void METHOD_connect_SSLAuth_EXCEPTION_PeerConnectionException_noCliCertOnSrv() throws AbsCustomTrustManager.UpdateException {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);       // Required for SSLAuth
        clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSLAuth

        initAndStartServer(true);
        initClient();

        PeerConnectionException exception = Assertions.assertThrows(PeerConnectionException.class, client::connect);

        String expected = String.format("Error on Peer '%s' because SSL handshake failed with '%s:%d'", client.toString().replace("< | >", "<-| >"), server.getServerPeerInfo().getAddr().getHostAddress(), 10000);
        Assertions.assertEquals(expected, exception.getMessage());

        Assertions.assertTrue(exception.getCause() instanceof SSLException || exception.getCause() instanceof SocketException);
        if (exception.getCause() instanceof SSLException) {
            if (JavaVersion.JAVA_CURRENT.greaterEqual(JavaVersion.JAVA_11))
                Assertions.assertEquals("readHandshakeRecord", exception.getCause().getMessage());
            else
                Assertions.assertEquals("Received fatal alert: internal_error", exception.getCause().getMessage());
        }
        if (exception.getCause() instanceof SocketException) {
            Assertions.assertEquals("Broken pipe (Write failed)", exception.getCause().getMessage());
        }

        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());
    }

    @Test
    public void METHOD_connect_SSLAuth_EXCEPTION_PeerConnectionException_noSrvCertOnCli_noCliCertOnSrv() {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);       // Required for SSLAuth
        //clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSLAuth

        initAndStartServer(true);
        initClient();

        PeerConnectionException exception = Assertions.assertThrows(PeerConnectionException.class, client::connect);

        String expected = String.format("Error on Peer '%s' because SSL handshake failed with '%s:%d'", client.toString().replace("< | >", "<-| >"), server.getServerPeerInfo().getAddr().getHostAddress(), 10000);
        Assertions.assertEquals(expected, exception.getMessage());

        Assertions.assertTrue(exception.getCause() instanceof SSLException);
        if (JavaVersion.JAVA_CURRENT.greaterEqual(JavaVersion.JAVA_11))
            Assertions.assertEquals("Unexpected error: java.security.InvalidAlgorithmParameterException: the trustAnchors parameter must be non-empty", exception.getCause().getMessage());
        else
            Assertions.assertEquals("java.lang.RuntimeException: Unexpected error: java.security.InvalidAlgorithmParameterException: the trustAnchors parameter must be non-empty", exception.getCause().getMessage());

        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());
    }

    @Test
    public void METHOD_connect_SSLAuthCertSharing() {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);     // Not required for SSL with CertSharing
        //clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);     // Not required for SSL with CertSharing

        initAndStartServerWithCertSharing();
        initClientWithCertSharing();

        connectClient();

        Assertions.assertEquals(ConnectionState.CONNECTED, client.getState());

        disconnectClient();

        Assertions.assertEquals(ConnectionState.DISCONNECTED, client.getState());
    }


    // ids

    @Test
    public void METHOD_getIDs_SSL() throws AbsCustomTrustManager.UpdateException {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);     // Not required for SSL
        clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSL

        initAndStartServer(false);
        initClient();

        Assertions.assertEquals(CLIENT_LOCAL_ID, client.getLocalId());
        Assertions.assertEquals(CLIENT_SERVER_REMOTE_ID, client.getRemoteId());

        connectClient();

        Assertions.assertEquals(CLIENT_LOCAL_ID, client.getLocalId());
        Assertions.assertEquals(SERVER_CERT_ID, client.getRemoteId());

        disconnectClient();

        Assertions.assertEquals(CLIENT_LOCAL_ID, client.getLocalId());
        Assertions.assertEquals(SERVER_CERT_ID, client.getRemoteId());
    }

    @Test
    public void METHOD_getIDs_SSLAuth() throws AbsCustomTrustManager.UpdateException {
        serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);       // Required for SSLAuth
        clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);       // Required for SSLAuth

        initAndStartServer(true);
        initClient();

        Assertions.assertEquals(CLIENT_LOCAL_ID, client.getLocalId());
        Assertions.assertEquals(CLIENT_SERVER_REMOTE_ID, client.getRemoteId());

        connectClient();

        Assertions.assertEquals(CLIENT_LOCAL_ID, client.getLocalId());
        Assertions.assertEquals(SERVER_CERT_ID, client.getRemoteId());

        disconnectClient();

        Assertions.assertEquals(CLIENT_LOCAL_ID, client.getLocalId());
        Assertions.assertEquals(SERVER_CERT_ID, client.getRemoteId());
    }

    @Test
    public void METHOD_getIDs_SSLAuthCertSharing() {
        //serverTrustManager.addCertificate(CLIENT_CERT_ID, clientCertificate);     // Not required for SSL with CertSharing
        //clientTrustManager.addCertificate(SERVER_CERT_ID, serverCertificate);     // Not required for SSL with CertSharing

        initAndStartServerWithCertSharing();
        initClientWithCertSharing();

        Assertions.assertEquals(CLIENT_LOCAL_ID, client.getLocalId());
        Assertions.assertEquals(CLIENT_SERVER_REMOTE_ID, client.getRemoteId());

        connectClient();

        Assertions.assertEquals(CLIENT_LOCAL_ID, client.getLocalId());
        Assertions.assertEquals(SERVER_CERT_ID, client.getRemoteId());

        disconnectClient();

        Assertions.assertEquals(CLIENT_LOCAL_ID, client.getLocalId());
        Assertions.assertEquals(SERVER_CERT_ID, client.getRemoteId());
    }

}
