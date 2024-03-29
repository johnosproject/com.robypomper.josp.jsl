/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.robypomper.josp.jsl.comm;

import com.robypomper.comm.connection.ConnectionInfo;
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.comm.trustmanagers.DynAddTrustManager;
import com.robypomper.discovery.Discover;
import com.robypomper.discovery.DiscoveryService;
import com.robypomper.discovery.DiscoveryServicesListener;
import com.robypomper.discovery.DiscoverySystemFactory;
import com.robypomper.java.*;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.objs.JSLObjsMngr_002;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.DefaultObjComm;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.states.JSLLocalState;
import com.robypomper.josp.states.StateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.*;
import java.util.concurrent.CountDownLatch;


/**
 * Class for the JSL Local Clients manager, the manager for the Discovery System
 * and local clients to JOSP Objects.
 * <p>
 * When this manager is started, it will start the Discovery System to discover
 * JOSP Object's services. When a service is discovered, it will try to connect
 * to it using a local client. If the connection is established, it will wait
 * for the object's id (only if required). Then it will pass the connection to
 * the {@link com.robypomper.josp.jsl.objs.JSLObjsMngr}.
 * <p>
 * This manager handles also the clients disconnections. On a client disconnection,
 * it will try to re-connect the remote object using a backup client (if
 * available). If and only if the remote object is definitely disconnected (locally),
 * then it will emit the {@link LocalClientListener#onLocalDisconnected(JSLRemoteObject, JSLLocalClient)}
 * event.
 * <p>
 * When this manager is stopped, it will stop the Discovery System and disconnect
 * all local clients. That makes also all (locally connected) JOSP Objects to be
 * disconnected.
 * <p>
 * The connection process can be split in 4 phases:
 * <ul>
 *   <ol>Discovered JOD Object's service: when a new JOSP Object has been discovered</ol>
 *   <ol>Connection established: when the client negotiated and established the connection</ol>
 *   <ol>Connection Ready: when get the JOSP Object's ID</ol>
 *   <ol>Remote Object Ready: when the connection has been associated to a {@link JSLRemoteObject}</ol>
 * </ul>
 * If the connection fails at any phase, the client is disconnected and the
 * connection is discharged.<br/>
 * Discovered JOD Object's services and available clients can be retrieved using
 * the {@link #getDiscoveredServices()} and {@link #getLocalClients()} methods.
 * <p>
 * TODO: move backup connections from DefaultObjComm to here
 */
@SuppressWarnings("UnnecessaryReturnStatement")
public class JSLLocalClientsMngr {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLLocalClientsMngr.class);
    /**
     * State of current JSLLocalClientsMngr.
     * <p>
     * This state is checked internally from the start() and stop() methods.
     * And it is updated from the startDiscovering() and stopDiscovering()
     * methods. Moreover, those two methods are synchronized on this object.
     */
    private final JavaEnum.Synchronizable<JSLLocalState> state = new JavaEnum.Synchronizable<>(JSLLocalState.STOP);
    private final boolean onlyLocalhostEnabled;
    private final boolean onlySSLEnabled;
    private final boolean onlyNoSSLEnabled;
    private final boolean sslSharingEnabled;
    // JSL
    /**
     * Reference to JSL Communication, used to process messages from objects.
     */
    private final JSLCommunication_002 jslComm;
    /**
     * Reference to JSL Objects Manager, used to add new connections.
     */
    private final JSLObjsMngr_002 jslObjsMngr;
    // Configs
    /**
     * Reference to JSL Service Info, used to get the service and full service ids.
     */
    private final JSLServiceInfo srvInfo;
    /**
     * Instance of the discovery system used to discover JOD Object's services.
     */
    private final Discover discover;
    /**
     * Contains discovered (but not lost) services.
     * <p>
     * A discovered JOSP Object service is added on service discovery [Phase 1] (processDiscovered()).
     * A discovered JOSP Object service is removed on service lost (processOnLost()).
     * <p>
     * It is reset on manager stop.
     */
    private final List<DiscoveryService> availableDiscoveryServices = new ArrayList<>();
    /**
     * List of currently waiting latches used to wait for SSL connections to be
     * established.
     * <p>
     * A latch is added and removed from the processDiscovered() method during the connection creation.
     * Those latches are updated (countdown) on connection established or failed
     * (onConnectionConnected() or onConnectionFail()).
     * <p>
     * It is reset on manager stop.
     */
    private final Map<JSLLocalClient, CountDownLatch> discoveryServicesLatches = new HashMap<>();
    /**
     * Contains all connecting and connected clients.
     * <p>
     * A connecting client is temporary added on connection creation (processDiscovered()) during the discovered JOD Object's service [Phase 1].
     * A connecting client is removed on connection fail (processOnFail()).
     * A connected client is removed on disconnection (processOnDisconnected()).
     * <p>
     * A connecting client is temporary added because if it fails the connection,
     * it is removed immediately. At the same time, if the connection fails (not
     * catchable from processDiscovered()) then it is removed from the
     * processOnFail() method.
     * <p>
     * It is reset on manager stop.
     */
    private final Map<JSLLocalClient, DiscoveryService> connectionsDiscoveryServices = new HashMap<>();
    /**
     * Contains all connections (ready and not ready).
     * <p>
     * An almost ready connection is temporary added when the connection is established [Phase 2] (processOnConnected()), with `false` value.
     * When the associated object become ready [Phase 4] (always on processOnConnected() method), it is updated to `true` value.
     * A ready connection is removed on disconnection (processOnDisconnected()).
     * <p>
     * An almost ready connection is temporary added because if it fails the JOSP
     * protocol initialization (i.e. id sharing). When the JOSP protocol is
     * terminated then it is updated to ready connection.
     * <p>
     * It is reset on manager stop.
     */
    private final Map<JSLLocalClient, Boolean> availableConnections = new HashMap<>();
    /**
     * Contains all object's ids for each ready connection.
     * <p>
     * An object's id is added when the connection becomes ready [Phase 3] (processOnConnected()).
     * An object's id is removed on disconnection (processOnDisconnected()).
     * <p>
     * It is reset on manager stop.
     */
    private final Map<JSLLocalClient, String> connectionsObjectIDs = new HashMap<>();
    /**
     * Contains all remote objects for each ready connection.
     * <p>
     * A remote object is added when the object becomes ready [Phase 4] (processOnConnected()).
     * A remote object is removed on disconnection (processOnDisconnected()).
     * <p>
     * It is reset on manager stop.
     */
    private final Map<JSLLocalClient, JSLRemoteObject> connectionsRemoteObjects = new HashMap<>();
    /**
     * Listeners for CommLocalStateListener events.
     */
    private final List<CommLocalStateListener> statusListeners = new ArrayList<>();
    /**
     * Listeners for LocalClientListener events.
     */
    private final List<LocalClientListener> connectionsListeners = new ArrayList<>();
    /**
     * SSL context for the local client. `null` if no SSL is enabled.
     */
    private final SSLContext sslCtx;
    /**
     * Local client certificate. `null` if no SSL is enabled.
     */
    private final Certificate clientCertificate;
    /**
     * Local client certificate's id. `null` if no SSL is enabled.
     */
    private final String clientCertificateId;
    /**
     * TrustManager used by the SSL context for the local client.
     * It is always a {@link DynAddTrustManager}.
     */
    private final AbsCustomTrustManager trustManager = new DynAddTrustManager();


    // Constructor

    /**
     * Create a new JSLLocalClientsMngr and initialize his discovery system.
     *
     * @param jslComm     the JSL Communication instance.
     * @param jslObjsMngr the JSL Objects Manager instance.
     * @param settings    the JSL Settings instance.
     * @param srvInfo     the JSL Service Info instance.
     * @throws JSLCommunication.LocalCommunicationException if errors occurs on creating the discovery system.
     */
    public JSLLocalClientsMngr(JSLCommunication_002 jslComm, JSLObjsMngr_002 jslObjsMngr, JSLSettings_002 settings, JSLServiceInfo srvInfo)
            throws JSLCommunication.LocalCommunicationException {
        this.jslComm = jslComm;
        this.jslObjsMngr = jslObjsMngr;
        // Settings
        this.onlyLocalhostEnabled = settings.getLocalOnlyLocalhost();
        this.onlySSLEnabled = settings.getLocalOnlySSLEnabled();
        this.onlyNoSSLEnabled = settings.getLocalOnlyNoSSLEnabled();
        this.sslSharingEnabled = settings.getLocalSSLSharingEnabled();
        String discoverySystem = settings.getJSLDiscovery();
        String ksPath = settings.getLocalKeyStorePath();
        String ksPass = settings.getLocalKeyStorePass();
        String ksAlias = settings.getLocalKeyStoreAlias();

        if (onlySSLEnabled && onlyNoSSLEnabled)
            throw new JSLCommunication.LocalCommunicationException("Only one of 'jsl.comm.local.onlySSL' or 'jsl.comm.local.onlyNoSSL' properties can be enabled");
        this.srvInfo = srvInfo;

        // Init local service client and discovery
        try {
            log.debug(String.format("Creating discovery '%s' service for local object's servers", discoverySystem));
            discover = DiscoverySystemFactory.createDiscover(discoverySystem, JOSPProtocol.DISCOVERY_TYPE);
            log.info(String.format("Discovery system '%s' service initialized for Local Communication", discoverySystem));

        } catch (Discover.DiscoveryException e) {
            throw new JSLCommunication.LocalCommunicationException(String.format("Error on creating discovery '%s' service for local object's servers", discoverySystem), e);
        }

        if (onlyNoSSLEnabled) {
            log.info("Initialized Local Communication for PLAIN connections");
            sslCtx = null;
            clientCertificate = null;
            clientCertificateId = null;
        } else {

            boolean mustLoad = new File(ksPath).exists();
            // Load/generate certificate
            try {
                String tmpCertificateId = srvInfo.getFullId();
                if (ksAlias == null || ksAlias.isEmpty())
                    ksAlias = tmpCertificateId + "-LocalCert";
                KeyStore ks;
                if (mustLoad)
                    ks = JavaJKS.loadKeyStore(ksPath, ksPass);
                else
                    ks = JavaJKS.generateAndLoadNewKeyStoreFile(tmpCertificateId, ksPath, ksPass, ksAlias);
                clientCertificate = JavaJKS.extractKeyStoreCertificate(ks, ksAlias);
                clientCertificateId = JavaJKS.getCertificateId(clientCertificate);
                sslCtx = JavaSSL.generateSSLContext(ks, ksPass, trustManager);

            } catch (JavaJKS.LoadingException |
                     JavaJKS.GenerationException |
                     JavaSSL.GenerationException e) {
                if (new File(ksPath).exists())
                    throw new JSLCommunication.LocalCommunicationException(String.format("Error on loading the local communication certificate at '%s'", ksPath), e);
                else
                    throw new JSLCommunication.LocalCommunicationException(String.format("Error on generating the local communication certificate at '%s'", ksPath), e);
            }

            String sslSupport = onlySSLEnabled ? "ENCRYPTED"
                    : "ENCRYPTED and PLAIN";
            String sslShare = sslSharingEnabled ? "ENABLED" : "DISABLED";
            log.info("Initialized Local Communication for " + sslSupport + " connections using '" + clientCertificateId + "' as certificate's id and with SSL Share " + sslShare);
        }
    }


    // Local Clients states manager

    /**
     * @return the state of current JSLLocalClientMngr.
     */
    public JSLLocalState getState() {
        return state.get();
    }

    /**
     * @return true if current JSLLocalClientMngr is running.
     */
    public boolean isRunning() {
        return discover.getState().isRunning();
    }

    /**
     * Start JSLLocalClientsMngr.
     *
     * @throws StateException              JSLLocalClientsMngr can't be stopped when it's in SHOUTING state.
     * @throws Discover.DiscoveryException exception thrown if errors occurs on Discovery system startup.
     */
    public void start() throws StateException, Discover.DiscoveryException {
        if (state.get().isRUN())
            return; // Already done

        else if (state.enumEquals(JSLLocalState.STARTING))
            return; // Already in progress

        else if (state.enumEquals(JSLLocalState.STOP))
            startDiscovering();

        else if (state.enumEquals(JSLLocalState.SHOUTING)) {
            throw new StateException("Can't startup Local discovery because is shutting down, try again later");
        }
    }

    /**
     * Stop JSLLocalClientsMngr.
     *
     * @throws StateException              JSLLocalClientsMngr can't be stopped when it's in STARTING state.
     * @throws Discover.DiscoveryException exception thrown if errors occurs on Discovery system shutdown.
     */
    public void stop() throws StateException, Discover.DiscoveryException {
        if (state.get().isRUN())
            stopDiscovering();

        else if (state.enumEquals(JSLLocalState.STARTING))
            throw new StateException("Can't shut down Local discovery because is starting up, try again later");

        else if (state.enumEquals(JSLLocalState.STOP))
            return; // Already done

        else if (state.enumEquals(JSLLocalState.SHOUTING))
            return; // Already in progress

        log.info("Disconnecting local communication service's clients");
        Set<JSLLocalClient> tmpList = new HashSet<>(getLocalClients());
        for (JSLLocalClient client : tmpList)
            if (client.getState().isConnected())
                try {
                    client.disconnect();

                } catch (PeerDisconnectionException e) {
                    log.warn(String.format("Error on disconnecting from '%s' object on server '%s:%d' from '%s' service because %s", client.getRemoteId(), client.getConnectionInfo().getLocalInfo().getAddr().getHostAddress(), client.getConnectionInfo().getLocalInfo().getPort(), srvInfo.getSrvId(), e.getMessage()), e);
                }
        log.debug("Local communication service's clients disconnected");

        // todo clean the code
//        availableDiscoveryServices.clear();
//        discoveryServicesLatches.clear();
//        connectionsDiscoveryServices.clear();
//        availableConnections.clear();
//        connectionsObjectIDs.clear();
//        connectionsRemoteObjects.clear();
    }

    private void startDiscovering() throws Discover.DiscoveryException {
        assert state.enumEquals(JSLLocalState.STOP) : "Method startDiscovering() can be called only from STOP state";

        log.info(String.format("Start local service's discovery '%s'", srvInfo.getSrvId()));

        synchronized (state) {
            log.debug("Local Discovery state = STARTING");
            state.set(JSLLocalState.STARTING);

            discover.addListener(discoverListener);
            log.debug("Starting local service's discovery");
            discover.start();
            log.debug("Local service's discovery started");

            log.debug("Local Discovery state = RUN");
            state.set(JSLLocalState.RUN_WAITING);

            emit_LocalStarted();
        }
    }

    private void stopDiscovering() {
        assert state.get().isRUN() : "Method startDiscovering() can be called only from RUN_ state";
        log.info(String.format("Stop local communication service's discovery '%s' and disconnect local clients", srvInfo.getSrvId()));

        synchronized (state) {
            log.debug("Local Discovery state = SHOUTING");
            state.set(JSLLocalState.SHOUTING);

            log.debug("Stopping local service's discovery");
            discover.stop();
            log.debug("Local service's discovery stopped");
            discover.removeListener(discoverListener);

            log.debug("Local Discovery state = STOP");
            state.set(JSLLocalState.STOP);

            emit_LocalStopped();
        }
    }


    // Process received messages

    /**
     * Process received message from object.
     *
     * @param msg      the message to process.
     * @param connType the connection type of the message.
     * @return true if message was processed successfully, false otherwise.
     */
    public boolean processFromObjectMsg(JSLLocalClient client, String msg, JOSPPerm.Connection connType) {
        log.warn(String.format("Received message from object %s: %s", connectionsObjectIDs.get(client), msg));
        return jslComm.processFromObjectMsg(msg, connType);
    }


    // Discovered services and servers' connections

    /**
     * @return discovered (but not lost) services.
     */
    public List<DiscoveryService> getDiscoveredServices() {
        return new ArrayList<>(availableDiscoveryServices);
    }

    /**
     * @return all connections (ready and not ready).
     */
    public List<JSLLocalClient> getLocalClients() {
        return new ArrayList<>(availableConnections.keySet());
    }


    // Certificates mngm

    /**
     * Check if the local (client) certificate is full.
     *
     * @return true if the certificate used by current client contains the JOSP
     * Service's full ID.
     */
    public boolean isLocalCertificateFull() {
        return clientCertificateId != null && JOSPProtocol.isFullSrvId(clientCertificateId);
    }

    /**
     * Check if the remote (server) certificate is full.
     *
     * @param objId the full JSOP Object id to check.
     * @return true if the given string is a valid full JOSP Object's ID.
     */
    public boolean isRemoteCertificateFull(String objId) {
        return JOSPProtocol.isFullObjId(objId);
    }

    /**
     * @return the TrustManager used by the SSL context for the local client.
     */
    public AbsCustomTrustManager getTrustManager() {
        return trustManager;
    }


    // Local service discovery listener

    private final DiscoveryServicesListener discoverListener = new DiscoveryServicesListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceDiscovered(DiscoveryService discSrv) {
            Thread.currentThread().setName("JSLDiscovery");
            onDiscovered(discSrv);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceLost(DiscoveryService lostSrv) {
            onLost(lostSrv);
        }

    };

    private void onDiscovered(DiscoveryService discSrv) {
        registerDiscoveryLUID(discSrv);
        processDiscovered(discSrv);
    }

    private void onLost(DiscoveryService lostSrv) {
        processOnLost(lostSrv);
        deregisterDiscoveryLUID(lostSrv);
    }


    // Internal local communication client listener

    private final PeerConnectionListener localClientListener = new PeerConnectionListener() {

        @Override
        public void onConnecting(Peer peer) {
        }

        @Override
        public void onWaiting(Peer peer) {
        }

        @Override
        public void onConnect(Peer peer) {
            onConnectionConnected((JSLLocalClient) peer);
        }

        @Override
        public void onDisconnecting(Peer peer) {}

        @Override
        public void onDisconnect(Peer peer) {
            onConnectionDisconnected((JSLLocalClient) peer);
        }

        @Override
        public void onFail(Peer peer, String failMsg, Throwable e) {
            onConnectionFail((JSLLocalClient) peer, failMsg, e);
        }

    };

    private void onConnectionConnected(JSLLocalClient client) {
        // unlock processDiscovered method
        discoveryServicesLatches.get(client).countDown();

        processOnConnected(client);
    }

    private void onConnectionDisconnected(JSLLocalClient client) {
        processOnDisconnected(client);
    }

    private void onConnectionFail(JSLLocalClient client, String failMsg, Throwable e) {
        // unlock processDiscovered method
        if (discoveryServicesLatches.containsKey(client))
            discoveryServicesLatches.get(client).countDown();

        processOnFail(client, failMsg, e);
    }


    // Internal events processors

    private void processDiscovered(DiscoveryService discSrv) {
        /* !! 1. Discovered JOD Object's service !! */
        log.debug(String.format("%s Phase1 Discovered JOD Object's service '%s' at '%s:%d' on '%s' interface by '%s' service", discoveryLUID(discSrv), discSrv.name, discSrv.address, discSrv.port, discSrv.intf, srvInfo.getSrvId()));
        log.info(String.format("%s Discovered Remote Object service %s at '%s:%d'", discoveryLUID(discSrv), discSrv.name, discSrv.address, discSrv.port));

        if (availableDiscoveryServices.contains(discSrv)) {
            log.info(String.format("%s Discovered JOD Object's service '%s' already know, skipped", discoveryLUID(discSrv), discSrv.name));
            return;
        }
        availableDiscoveryServices.add(discSrv);

        // Check if discovered object is at localhost (if check enabled)
        if (onlyLocalhostEnabled && !discSrv.address.isLoopbackAddress()) {
            log.warn(String.format("%s Discovered JOD Object's service '%s' do not use local address, skipped", discoveryLUID(discSrv), discSrv.name));
            return;
        }

        // Create discovered object connection
        boolean sslEnabled = !onlyNoSSLEnabled;
        boolean noSSLEnabled = !onlySSLEnabled;

        if (sslEnabled) {
            log.debug(String.format("%s Connecting to discovered JOD Object's service '%s' using ENCRYPTED connection", discoveryLUID(discSrv), discSrv.name));

            JSLLocalClient localClient = new JSLLocalClientSSLShare(this, srvInfo.getFullId(),
                        discSrv.address, discSrv.port,  // ToDo: Give also discSrv.intf, so client can bind right interface
                        discSrv.name,                   // ToDo: this should be the remoteObjId
                        localClientListener,
                        sslSharingEnabled,
                        sslCtx, clientCertificate, trustManager);
            connectionsDiscoveryServices.put(localClient, discSrv);
            discoveryServicesLatches.put(localClient, new CountDownLatch(1));
            boolean errorOnConnect = false;
            try {
                localClient.connect();
            } catch (PeerConnectionException e) {
                errorOnConnect = true;
                if (e.getMessage().contains("SSL handshake failed"))
                    log.debug(String.format("%s Discovered JOD Object's '%s' service do not support ENCRYPTED connection", discoveryLUID(discSrv), discSrv.name));
                else
                    log.warn(String.format("%s %s using ENCRYPTED connection, %s", discoveryLUID(discSrv), e.getMessage(), discSrv.name));
            }

            // Wait for connection to be established (or not)
            if (!errorOnConnect)
                try {
                    discoveryServicesLatches.get(localClient).await();
                } catch (InterruptedException e) {
                    log.debug(String.format("%s Error connecting to discovered JOD Object's service '%s' using ENCRYPTED connection, %s", discoveryLUID(discSrv), discSrv.name, e), e);
                }
            discoveryServicesLatches.remove(localClient);

            // Check connection established nor set to null
            if (localClient.getState().isConnected()) {
                log.debug(String.format("%s Discovered JOD Object's service '%s' connected using ENCRYPTED connection",
                        discoveryLUID(discSrv), discSrv.name));
                return;     // once the connection is established, the flow continues with onConnected
            }

            // Reset local client
            connectionsDiscoveryServices.remove(localClient);
        }

        if (noSSLEnabled) {
            log.debug(String.format("%s Connecting to discovered JOD Object's service '%s' using PLAIN connection",
                    discoveryLUID(discSrv), discSrv.name));
            // Create and connect new local client with NoSSL
            JSLLocalClient localClient = new JSLLocalClientNoSSL(this, srvInfo.getFullId(),
                    discSrv.address, discSrv.port, discSrv.name,
                    localClientListener);
            connectionsDiscoveryServices.put(localClient, discSrv);
            discoveryServicesLatches.put(localClient, new CountDownLatch(1));
            boolean errorOnConnect = false;
            try {
                localClient.connect();
            } catch (PeerConnectionException e) {
                errorOnConnect = true;
                log.debug(String.format("%s %s using ENCRYPTED connection, %s", discoveryLUID(discSrv), e.getMessage(), discSrv.name));
            }

            // Wait for connection to be established (or not)
            if (!errorOnConnect)
                try {
                    discoveryServicesLatches.get(localClient).await();
                } catch (InterruptedException e) {
                    log.debug(String.format("%s Error connecting to discovered JOD Object's service '%s' using ENCRYPTED connection, %s", discoveryLUID(discSrv), discSrv.name, e), e);
                }
            discoveryServicesLatches.remove(localClient);

            // Check connection established nor set to null
            if (localClient.getState().isConnected()) {
                log.debug(String.format("%s Discovered JOD Object's service '%s' connected using PLAIN connection",
                        discoveryLUID(discSrv), discSrv.name));
                return;     // once the connection is established, the flow continues with onConnected
            }

            // Reset local client
            connectionsDiscoveryServices.remove(localClient);
        }

        // Server is not supported from current clients
        log.warn(String.format("%s Discovered JOD Object's service '%s' at '%s:%d' can't connected because object's server not supported", discoveryLUID(discSrv), discSrv.name, discSrv.address, discSrv.port));
    }

    private void processOnLost(DiscoveryService lostSrv) {
        // remove lost service from local discovered
        availableDiscoveryServices.remove(lostSrv);
        log.debug(String.format("%s Lost JOD Object '%s' at '%s:%d' lost", discoveryLUID(lostSrv), lostSrv.name, lostSrv.address, lostSrv.port));
    }

    private void processOnConnected(JSLLocalClient client) {
        DiscoveryService discSrv = connectionsDiscoveryServices.get(client);    // added previously in processDiscovered
        assert discSrv != null : "A DiscoveryService must be associated to current client connection, normally via processDiscovered() method";

        if (client instanceof JSLLocalClientSSLShare) {
            boolean isRemoteCertificateIdFull = JOSPProtocol.isFullObjId(client.getRemoteId());
            ((JSLLocalClientSSLShare)client).setIsServerCertificateFull(isRemoteCertificateIdFull);
        }

        /* !! 2. Connection established !! */
        log.debug(String.format("%s Phase2 Connection established to JOD Object's service '%s'", discoveryLUID(discSrv), discSrv.name));
        availableConnections.put(client, false);
        registerLUID(client);
        log.info(String.format("%s > %s New connection to server '%s' for discovery JOD Object's service '%s'", discoveryLUID(discSrv), LUID(client), client.getConnectionInfo().getRemoteInfo(), discSrv.name));

        // Get object's id from server
        String remObjId;
        try {
            remObjId = getOrWaitObjectId(client);
            log.debug(String.format("%s Get object's id `%s` from server '%s:%d'.", discoveryLUID(discSrv), remObjId, client.getSocket().getInetAddress(), client.getSocket().getPort()));
        } catch (IOException e) {
            log.warn(String.format("%s Error on getting object's id from discovered JOD Object's service '%s' (%s), discharge connection.", LUID(client), discSrv.name, e));
            availableConnections.remove(client);
            deregisterLUID(client);
            return;
        }

        /* !! 3. Connection ready !! */
        log.debug(String.format("%s Phase3 Connection ready to JOD Object's '%s'", LUID(client), remObjId));
        connectionsObjectIDs.put(client, remObjId);

        // pass the connection to the ObjsMngr -> it will use/close it depending on object's connection status
        JSLRemoteObject remObj = jslObjsMngr.addNewConnection(client, remObjId);
        if (remObj == null) {
            log.warn(String.format("%s Error on generating Remote Object JOD Object (%s), discharge connection.", LUID(client), remObjId));
            connectionsObjectIDs.remove(client);
            availableConnections.remove(client);
            deregisterLUID(client);
            try {
                client.disconnect();
            } catch (PeerDisconnectionException ignore) {}
            emit_LocalConnectionError(client, String.format("Can't create localClient object for server %s", client.getRemoteId()));
            return;
        }

        // If local client is NoSSL or local SSL certificate is partial
        //   Send service's fullId message to server
        if (client instanceof JSLLocalClientNoSSL
                || !isLocalCertificateFull()) {
            String msg = srvInfo.getFullId() + "\n";
            log.info(String.format("%s Sending message '%s' to JOD Object (%s) via local communication", LUID(client), msg.substring(0, msg.indexOf('\n')), remObjId));

            // Send service's fullId message to server
            try {
                client.sendData(msg);

            } catch (PeerNotConnectedException | PeerStreamException e) {
                log.warn(String.format("%s Error on sending ID message to JOD Object (%s), discharge connection.", LUID(client), remObjId));
                connectionsObjectIDs.remove(client);
                availableConnections.remove(client);
                deregisterLUID(client);
                try {
                    client.disconnect();
                } catch (PeerDisconnectionException ignore) {}
                emit_LocalConnectionError(client, String.format("Can't create localClient object for server %s", client.getRemoteId()));
                return;
            }
        }

        /* !! 4. Remote object ready !! */
        connectionsRemoteObjects.put(client, remObj);
        availableConnections.put(client, true);
        emit_LocalConnected(remObj, client);
        log.debug(String.format("%s Phase4 Remote Object's connection '%s' ready", LUID(client), remObjId));
        log.info(String.format("%s Registered JOD Object %s's with connection '%s@%s:%d'", LUID(client), remObjId, client.getSecurityLevel(), client.getSocket().getInetAddress(), client.getSocket().getPort()));
    }

    private void processOnDisconnected(JSLLocalClient client) {
        log.info(String.format("%s Connection '%s:%d' closed with reason '%s'", LUID(client), client.getConnectionInfo().getRemoteInfo().getAddr(), client.getConnectionInfo().getRemoteInfo().getPort(), client.getDisconnectionReason()));

        // Remove from manager
        connectionsDiscoveryServices.remove(client);
        availableConnections.remove(client);
        connectionsObjectIDs.remove(client);
        JSLRemoteObject remObj = connectionsRemoteObjects.remove(client);

        // No remote object associated with closed connection, skip
        if (remObj == null) {
            log.warn(String.format("%s No JOD Object associated with closed connection", LUID(client)));
            return;
        }

        // TODO fix missing backup clients
        // If remote object is NOT locally connected, look for a backup client and connect it
        if (!remObj.getComm().isLocalConnected()) {
            List<JSLLocalClient> objClients = ((DefaultObjComm) remObj.getComm()).getLocalClients();
            for (JSLLocalClient obCli : objClients) {
                try {
                    obCli.connect();
                    break;
                } catch (PeerConnectionException e) {
                    log.warn(String.format("%s Remote Object %s (%s) re-connection '%s:%d' attempt failed because [%s] %s",
                            LUID(client),
                            remObj.getName(), remObj.getId(),
                            obCli.getConnectionInfo().getRemoteInfo().getAddr(),
                            obCli.getConnectionInfo().getRemoteInfo().getPort(),
                            e.getClass().getSimpleName(), e));
                }
            }
        }

        // If remote object has been re-connected locally
        if (remObj.getComm().isLocalConnected()) {
            // Log "connection switch"
            ConnectionInfo newConnection = ((DefaultObjComm) remObj.getComm()).getConnectedLocalClient().getConnectionInfo();
            log.info(String.format("%s Remote Object %s (%s) switched connection to '%s:%d'",
                    LUID(client),
                    remObj.getName(), remObj.getId(),
                    newConnection.getRemoteInfo().getAddr(),
                    newConnection.getRemoteInfo().getPort()));

        } else {
            // Log "Object disconnected
            log.info(String.format("%s Remote Object '%s' disconnected locally", LUID(client), remObj.getName()));
            emit_LocalDisconnected(remObj, client);
        }
    }

    private void processOnFail(JSLLocalClient client, String failMsg, Throwable e) {
        log.debug(String.format("%s %s", LUID(client), failMsg));
        log.debug(String.format("%s Error on '%s' connection: [%s] '%s'",
                LUID(client), client, e.getClass().getSimpleName(), e));

        connectionsDiscoveryServices.remove(client);
        connectionsRemoteObjects.remove(client);
        String rObjID = connectionsObjectIDs.remove(client);

        // TODO: analyze the error and print adeguate logging message
        log.warn(String.format("%s Error on '%s' connection: [%s]", LUID(client), client, rObjID));

        emit_LocalConnectionError(client, e);
    }

    /**
     * Get the full service id.
     * <p>
     * First it try to get it from the client's connection's (as the remote id)
     * but if it's not a valid full/partial service id, then it waits for the
     * service id as first line received from the client.
     * <p>
     * The service id is contained into the remote id if it uses an SSL certificate
     * conform with the JOSP Security levels.
     *
     * @param serverConnection the connection representing the object connected
     * @return the object id
     * @throws IOException if some error occurs with network communication or
     *                     sockets configuration.
     */
    private String getOrWaitObjectId(JSLLocalClient serverConnection) throws IOException {
        String objId;
        try { // SocketException
            objId = serverConnection.getRemoteId();
            // test objId
            if (!isRemoteCertificateFull(objId)) {
                // Wait for service's presentation message from the client
                Socket socket = serverConnection.getSocket();
                int tmpSoTimeout = socket.getSoTimeout();

                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    objId = "";
                    while (objId.isEmpty())
                        objId = in.readLine();
                } catch (IOException e) {
                    throw new IOException("Error on reading object's id message from server", e);
                }

                // test objId
                if (!isRemoteCertificateFull(objId))
                    throw new IOException(String.format("Error on parsing object's id message from server (Invalid id '%s')", objId));

                try {
                    socket.setSoTimeout(tmpSoTimeout);
                } catch (IOException e) {
                    throw new IOException("Error on reading object's id message from server", e);
                }
            }
        } catch (SocketException e) {
            throw new IOException("Error on setting socket timeout", e);
        }

        return objId;
    }


    // Listeners manager

    /**
     * Add given listener to current JSLLocalClientsMngr's events.
     *
     * @param listener the listener to add.
     */
    public void addListener(CommLocalStateListener listener) {
        if (statusListeners.contains(listener))
            return;

        statusListeners.add(listener);
    }

    /**
     * Remove given listener from current JSLLocalClientsMngr's events.
     *
     * @param listener the listener to remove.
     */
    public void removeListener(CommLocalStateListener listener) {
        if (!statusListeners.contains(listener))
            return;

        statusListeners.remove(listener);
    }

    private void emit_LocalStarted() {
        for (CommLocalStateListener l : statusListeners)
            l.onStarted();
    }

    private void emit_LocalStopped() {
        for (CommLocalStateListener l : statusListeners)
            l.onStopped();
    }

    /**
     * JSLLocalClientsMngr events interface.
     */
    public interface CommLocalStateListener {

        void onStarted();

        void onStopped();

    }


    // Listeners clients connections

    /**
     * Add given listener to all managed clients's events.
     *
     * @param listener the listener to add.
     */
    public void addListener(LocalClientListener listener) {
        if (connectionsListeners.contains(listener))
            return;

        connectionsListeners.add(listener);
    }

    /**
     * Remove given listener from all managed clients's events.
     *
     * @param listener the listener to remove.
     */
    public void removeListener(LocalClientListener listener) {
        if (!connectionsListeners.contains(listener))
            return;

        connectionsListeners.remove(listener);
    }

    private void emit_LocalConnected(JSLRemoteObject jslObj, JSLLocalClient jslLocCli) {
        for (LocalClientListener l : connectionsListeners)
            l.onLocalConnected(jslObj, jslLocCli);
    }

    private void emit_LocalConnectionError(JSLLocalClient jslLocCli, String msg) {
        emit_LocalConnectionError(jslLocCli, new Throwable(msg));
    }

    private void emit_LocalConnectionError(JSLLocalClient jslLocCli, Throwable exception) {
        for (LocalClientListener l : connectionsListeners)
            l.onLocalConnectionError(jslLocCli, exception);
    }

    private void emit_LocalDisconnected(JSLRemoteObject jslObj, JSLLocalClient jslLocCli) {
        for (LocalClientListener l : connectionsListeners)
            l.onLocalDisconnected(jslObj, jslLocCli);
    }

    /**
     * Local clients events interface.
     */
    public interface LocalClientListener {

        void onLocalConnected(JSLRemoteObject jslObj, JSLLocalClient jslLocCli);

        void onLocalConnectionError(JSLLocalClient jslLocCli, Throwable throwable);

        void onLocalDisconnected(JSLRemoteObject jslObj, JSLLocalClient jslLocCli);

    }


    // LUID: Connection Local Unique ID

    private final Map<JSLLocalClient, Integer> luids = new HashMap<>();
    private final Map<DiscoveryService, Integer> discLuids = new HashMap<>();
    private int lastLUID = -1;
    private int discLastLUID = -1;

    private void registerLUID(JSLLocalClient client) {
        synchronized (luids) {
            luids.put(client, ++lastLUID);
        }
    }

    private void deregisterLUID(JSLLocalClient client) {
        synchronized (luids) {
            luids.remove(client);
        }
    }

    private String LUID(JSLLocalClient client) {
        if (!luids.containsKey(client))
            return "[C#: ----]";
        int luid = luids.get(client);
        return String.format("[C#: %04x]", luid);
    }

    private void registerDiscoveryLUID(DiscoveryService client) {
        synchronized (discLuids) {
            discLuids.put(client, ++discLastLUID);
        }
    }

    private void deregisterDiscoveryLUID(DiscoveryService client) {
        synchronized (discLuids) {
            discLuids.remove(client);
        }
    }

    private String discoveryLUID(DiscoveryService client) {
        if (!discLuids.containsKey(client))
            return "[D#: ----]";
        int luid = discLuids.get(client);
        return String.format("[D#: %04x]", luid);
    }


}
