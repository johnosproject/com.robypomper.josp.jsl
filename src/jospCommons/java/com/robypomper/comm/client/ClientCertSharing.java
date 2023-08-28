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

import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerException;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.comm.server.ServerCertSharing;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaByteArrays;
import com.robypomper.java.JavaJKS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientCertSharing extends ClientAbsTCP {


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(ClientCertSharing.class);
    private final String clientId;
    private final Certificate localPublicCertificate;
    private final AbsCustomTrustManager certTrustManager;
    private byte[] serverCertBuffer = new byte[0];
    private final CountDownLatch certificateTx = new CountDownLatch(1);
    private final CountDownLatch certificateRx = new CountDownLatch(1);


    // Constructors

    public ClientCertSharing(String localId, String remoteId, InetAddress remoteAddr, int remotePort,
                             String clientId, AbsCustomTrustManager certTrustManager, Certificate localPublicCertFile) {
        super(localId, remoteId, remoteAddr, remotePort, ServerCertSharing.PROTO_NAME);
        addListener(listenerConnection);
        this.clientId = clientId;
        this.certTrustManager = certTrustManager;
        this.localPublicCertificate = localPublicCertFile;
    }

    public static ClientCertSharing generate(Client client, InetAddress remoteAddr, int remotePort, AbsCustomTrustManager trustManager, Certificate localPublicCertificate) {
        return new ClientCertSharing(client.getLocalId() + "-CertSharing", client.getRemoteId() + "-CertSharing", remoteAddr, remotePort + 1, client.getLocalId(), trustManager, localPublicCertificate);
    }


    // Sharing certificate

    public void shareCertificate() throws PeerConnectionException {
        if (getState() != ConnectionState.CONNECTED)
            super.connect();

        // onConnected event, the client send his certificate to the server (if localPublicCertificate!=null)
        // also the server, onConnected event, send his certificate to the client
    }

    public boolean waitForDone(int ms) {
        if (localPublicCertificate == null) {
            try {
                //noinspection ResultOfMethodCallIgnored
                certificateRx.await(ms, TimeUnit.MILLISECONDS);

            } catch (InterruptedException ignore) {
            }
            return certificateRx.getCount() == 0;
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            certificateRx.await(ms, TimeUnit.MILLISECONDS);
            //noinspection ResultOfMethodCallIgnored
            certificateTx.await(ms, TimeUnit.MILLISECONDS);

        } catch (InterruptedException ignore) {
        }
        return certificateRx.getCount() == 0 && certificateTx.getCount() == 0;
    }


    // Messages methods

    @Override
    protected boolean processData(byte[] data) {
        bufferServerCertificate(data);
        return true;
    }

    @Override
    protected boolean processData(String data) {
        return false;
    }

    private void bufferServerCertificate(byte[] readData) {
        serverCertBuffer = JavaByteArrays.append(serverCertBuffer, readData);
        storeServerCertificate(serverCertBuffer);
    }

    private void storeServerCertificate(byte[] serverCert) {
        if (serverCert.length == 0)
            return;

        try {
            log.trace(String.format("Client store server '%s''s certificate '%s'", getRemoteId(), String.format("CL@%s", clientId)));
            certTrustManager.addCertificateByte(String.format("CL@%s", clientId), serverCert);

        } catch (AbsCustomTrustManager.UpdateException | JavaJKS.LoadingException e) {
            log.trace(String.format("ERROR on Client store server '%s''s certificate [%s] %s", getRemoteId(), e.getClass().getSimpleName(), e.getMessage()));
            return;
        }
        certificateRx.countDown();

        try {
            if (localPublicCertificate == null || certificateTx.getCount() == 0)
                disconnect();

        } catch (PeerDisconnectionException e) {
            JavaAssertions.makeAssertion_Failed(e, String.format("Method disconnect() is called by a procedure triggered on data received, that means the client state must be connected. [%s] %s", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    private void sendClientCertificateToServer() {
        if (localPublicCertificate == null)
            return;

        try {
            log.trace(String.format("Client send his certificate to server '%s'", getRemoteId()));
            sendData(localPublicCertificate.getEncoded());
            certificateTx.countDown();

        } catch (PeerException e) {
            JavaAssertions.makeAssertion_Failed(e, "Method sendClientCertificate() is called only by onConnect() event, so it should NOT throw any PeerException");
        } catch (CertificateEncodingException e) {
            JavaAssertions.makeAssertion_Failed(e, "Method Certificate.getEncoded() is used to convert a valid certificate to byte[], that should not throw exception");
        }

        try {
            if (certificateRx.getCount() == 0)
                disconnect();

        } catch (PeerDisconnectionException e) {
            JavaAssertions.makeAssertion_Failed(e, "Method disconnect() is called only by onConnect() event, so it should NOT throw any PeerException");
        }
    }


    // Peer listener

    PeerConnectionListener listenerConnection = new PeerConnectionListener() {

        @Override
        public void onConnecting(Peer peer) {
        }

        @Override
        public void onWaiting(Peer peer) {
        }

        @Override
        public void onConnect(Peer peer) {
            sendClientCertificateToServer();
        }

        @Override
        public void onDisconnecting(Peer peer) {
        }

        @Override
        public void onDisconnect(Peer peer) {
        }

        @Override
        public void onFail(Peer peer, String failMsg, Throwable exception) {
        }

    };

}
