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

import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.discovery.Discover;
import com.robypomper.discovery.DiscoveryService;
import com.robypomper.discovery.DiscoveryServicesListener;
import com.robypomper.discovery.DiscoverySystemFactory;
import com.robypomper.java.JavaDate;
import com.robypomper.java.JavaEnum;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.objs.JSLObjsMngr_002;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.DefaultObjComm;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.states.JSLLocalState;
import com.robypomper.josp.states.StateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * JSL Local Clients manager.
 * <p>
 * This class manage the discovery system and all local clients to JOD instances.
 */
@SuppressWarnings("UnnecessaryReturnStatement")
public class JSLLocalClientsMngr {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLLocalClientsMngr.class);
    private final JavaEnum.Synchronizable<JSLLocalState> state = new JavaEnum.Synchronizable<>(JSLLocalState.STOP);
    // JSL
    private final JSL_002 jsl;
    private final JSLCommunication_002 jslComm;
    private final JSLObjsMngr_002 jslObjsMngr;
    private final JSLSettings_002 locSettings;
    // Configs
    private final JSLServiceInfo srvInfo;
    private final Discover discover;
    private final Map<DiscoveryService, Boolean> localDiscovered = new HashMap<>();          // Contains only connecting and connected clients;  The value indicate if client is connected;  Added on conn-creation, set true on conn-opened, removed on conn-close or conn-error;
    private final Map<JSLLocalClient, Boolean> localClients = new HashMap<>();               // Contains all (session) discovered services;      The value indicate if service id active;    Added on srv-disc (and set to true), set false on srv-lost, never removed;
    // Listeners
    private final List<CommLocalStateListener> statusListeners = new ArrayList<>();
    private final List<LocalClientListener> connectionsListeners = new ArrayList<>();
    // Connections stats
    private int countConnected = 0;
    private int countConnectionErrors = 0;
    private int countDisconnected = 0;
    private Date lastStart;
    private Date lastStop;
    private Date lastConnection;
    private Date lastDisconnection;


    // Constructor

    public JSLLocalClientsMngr(JSL_002 jsl, JSLCommunication_002 jslComm, JSLObjsMngr_002 jslObjsMngr, JSLSettings_002 settings, JSLServiceInfo srvInfo) throws JSLCommunication.LocalCommunicationException {
        this.jsl = jsl;
        this.jslComm = jslComm;

        this.jslObjsMngr = jslObjsMngr;
        this.locSettings = settings;
        this.srvInfo = srvInfo;

        // Init local service client and discovery
        String discoveryImpl = locSettings.getJSLDiscovery();
        try {
            log.debug(String.format("Creating discovery '%s' service for local object's servers", discoveryImpl));
            discover = DiscoverySystemFactory.createDiscover(discoveryImpl, JOSPProtocol.DISCOVERY_TYPE);
            log.debug(String.format("Discovery '%s' service created for local object's servers", discoveryImpl));

        } catch (Discover.DiscoveryException e) {
            log.warn(String.format("Error on creating discovery '%s' service for local object's servers because %s", discoveryImpl, e.getMessage()), e);
            throw new JSLCommunication.LocalCommunicationException(String.format("Error on creating discovery '%s' service for local object's servers", discoveryImpl), e);
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
            throw new StateException("Can't startup Local discovery because is shuting down, try again later");
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
    }

    private void startDiscovering() throws Discover.DiscoveryException {
        assert state.enumEquals(JSLLocalState.STOP) :
                "Method startDiscovering() can be called only from STOP state";

        log.info(String.format("Start local service's discovery '%s'", srvInfo.getSrvId()));

        synchronized (state) {
            log.debug("Local Discovery state = STARTING");
            state.set(JSLLocalState.STARTING);

            discover.addListener(discoverListener);
            log.debug("Starting local service's discovery");
            discover.start();
            log.debug("Local service's discovery started");
            lastStart = JavaDate.getNowDate();
            emit_LocalStarted();

            log.debug("Local Discovery state = RUN");
            state.set(JSLLocalState.RUN_WAITING);
        }
    }

    private void stopDiscovering() throws Discover.DiscoveryException {
        assert state.get().isRUN() :
                "Method startDiscovering() can be called only from RUN_ state";

        log.info(String.format("Stop local communication service's discovery '%s' and disconnect local clients", srvInfo.getSrvId()));

        synchronized (state) {
            log.debug("Local Discovery state = SHOUTING");
            state.set(JSLLocalState.SHOUTING);

            log.debug("Stopping local service's discovery");
            discover.stop();
            log.debug("Local service's discovery stopped");
            discover.removeListener(discoverListener);
            lastStop = JavaDate.getNowDate();
            emit_LocalStopped();

            log.debug("Disconnecting local communication service's clients");
            Set<JSLLocalClient> tmpList = new HashSet<>(localClients.keySet());
            for (JSLLocalClient locConn : tmpList)
                if (locConn.getState().isConnected())
                    try {
                        locConn.disconnect();

                    } catch (PeerDisconnectionException e) {
                        log.warn(String.format("Error on disconnecting to '%s' object on server '%s:%d' from '%s' service because %s", locConn.getRemoteId(), locConn.getConnectionInfo().getLocalInfo().getAddr().getHostAddress(), locConn.getConnectionInfo().getLocalInfo().getPort(), srvInfo.getSrvId(), e.getMessage()), e);
                    }
            log.debug("Local communication service's clients disconnected");

            log.debug("Local Discovery state = STOP");
            state.set(JSLLocalState.STOP);
        }
    }


    // Getter config stats

    /**
     * For the number of actually open connections see {@link #getLocalClientsConnected()}.
     *
     * @return the number of opened connections since this component was initialized.
     */
    public int getConnectedCount() {
        return countConnected;
    }

    /**
     * @return the number of error on opening connections since this component was initialized.
     */
    public int getConnectionErrorsCount() {
        return countConnectionErrors;
    }

    /**
     * @return the number of closed connections since this component was initialized.
     */
    public int getDisconnectedCount() {
        return countDisconnected;
    }

    /**
     * @return the Date when last startup was completed successfully, null
     * current JSLLocalClientsMngr was never started.
     */
    public Date getLastStartup() {
        return lastStart;
    }

    /**
     * @return the Date when last shutdown was completed successfully, null
     * current JSLLocalClientsMngr was never stopped.
     */
    public Date getLastShutdown() {
        return lastStop;
    }

    /**
     * @return the Date when last client's connection was opened successfully, null
     * if no client was never connected.
     */
    public Date getLastObjConnection() {
        return lastConnection;
    }

    /**
     * @return the Date when last client's connection was opened successfully, null
     * if no client was never disconnected.
     */
    public Date getLastObjDisconnection() {
        return lastDisconnection;
    }


    // Client access

    /**
     * @return the list all services discovered since this component was initialized (active and lost).
     */
    public List<DiscoveryService> getLocalDiscovered() {
        return new ArrayList<>(localDiscovered.keySet());
    }

    /**
     * @return the list all services discovered actually active.
     */
    public List<DiscoveryService> getLocalDiscoveredActive() {
        List<DiscoveryService> ret = new ArrayList<>();
        for (Map.Entry<DiscoveryService, Boolean> discSrv : localDiscovered.entrySet())
            if (discSrv.getValue())
                ret.add(discSrv.getKey());
        return ret;
    }

    /**
     * @return the list all services discovered actually lost.
     */
    public List<DiscoveryService> getLocalDiscoveredLost() {
        List<DiscoveryService> ret = new ArrayList<>();
        for (Map.Entry<DiscoveryService, Boolean> discSrv : localDiscovered.entrySet())
            if (!discSrv.getValue())
                ret.add(discSrv.getKey());
        return ret;
    }

    /**
     * @return the list clients (connecting or connected).
     */
    public List<JSLLocalClient> getLocalClients() {
        return new ArrayList<>(localClients.keySet());
    }

    /**
     * @return the list of connecting clients.
     */
    public List<JSLLocalClient> getLocalClientsConnecting() {
        List<JSLLocalClient> ret = new ArrayList<>();
        for (Map.Entry<JSLLocalClient, Boolean> discSrv : localClients.entrySet())
            if (!discSrv.getValue())
                ret.add(discSrv.getKey());
        return ret;
    }

    /**
     * @return the list of connected clients (actually opened connections).
     */
    public List<JSLLocalClient> getLocalClientsConnected() {
        List<JSLLocalClient> ret = new ArrayList<>();
        for (Map.Entry<JSLLocalClient, Boolean> discSrv : localClients.entrySet())
            if (discSrv.getValue())
                ret.add(discSrv.getKey());
        return ret;
    }


    // Local communication discovery listener

    private final DiscoveryServicesListener discoverListener = new DiscoveryServicesListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceDiscovered(DiscoveryService discSrv) {
            Thread.currentThread().setName("JSLDiscovery");
            synchronized (localClients) {
                log.info(String.format("Discover object's service '%s' at '%s:%d' on '%s' interface by '%s' service", discSrv.name, discSrv.address, discSrv.port, discSrv.intf, srvInfo.getSrvId()));
                localDiscovered.put(discSrv, true);

                // Check if discovered service is at localhost (if check enabled)
                if (locSettings.getLocalOnlyLocalhost() && !discSrv.address.isLoopbackAddress()) {
                    log.warn(String.format("Object's service '%s' at '%s:%d' use not Localhost address then discarded", discSrv.name, discSrv.address, discSrv.port));
                    return;
                }

                // Create discovered service connection
                JSLLocalClient locConn;
                try {
                    locConn = createAndConnectClient(discSrv);
                    localClients.put(locConn, false);

                } catch (PeerConnectionException e) {
                    log.warn(String.format("Error connecting on discovered service '%s' at '%s:%d'", discSrv.name, discSrv.address, discSrv.port), e);
                } catch (JavaJKS.GenerationException e) {
                    log.warn(String.format("Error generating local certificates for connection to discovered service '%s' at '%s:%d'", discSrv.name, discSrv.address, discSrv.port), e);
                } catch (JavaSSL.GenerationException e) {
                    log.warn(String.format("Error generating SSL context for connection to discovered service '%s' at '%s:%d'", discSrv.name, discSrv.address, discSrv.port), e);
                } catch (JavaJKS.LoadingException e) {
                    log.warn(String.format("Error loading local certificates for connection to discovered service '%s' at '%s:%d'", discSrv.name, discSrv.address, discSrv.port), e);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceLost(DiscoveryService lostSrv) {
            localDiscovered.remove(lostSrv);
        }

    };

    private JSLLocalClient createAndConnectClient(DiscoveryService discSrv) throws PeerConnectionException, JavaJKS.GenerationException, JavaSSL.GenerationException, JavaJKS.LoadingException {
        JSLLocalClient locConn = JSLLocalClient.instantiate(jslComm, this, srvInfo.getFullId(),
                discSrv.address, discSrv.port, discSrv.name,
                locSettings.getLocalKeyStorePath(), locSettings.getLocalKeyStorePass(),
                locSettings.getLocalKeyStoreAlias(), locSettings.getLocalKeyStoreDefaultPath()
        );      // ToDo: Give also discSrv.intf, so client can bind right interface
        locConn.connect();
        return locConn;
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


    // Manager listeners interfaces

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
        lastConnection = JavaDate.getNowDate();
        countConnected++;
        for (LocalClientListener l : connectionsListeners)
            l.onLocalConnected(jslObj, jslLocCli);
    }

    private void emit_LocalConnectionError(JSLLocalClient jslLocCli, String msg) {
        emit_LocalConnectionError(jslLocCli, new Throwable(msg));
    }

    private void emit_LocalConnectionError(JSLLocalClient jslLocCli, Throwable exception) {
        countConnectionErrors++;
        for (LocalClientListener l : connectionsListeners)
            l.onLocalConnectionError(jslLocCli, exception);
    }

    private void emit_LocalDisconnected(JSLRemoteObject jslObj, JSLLocalClient jslLocCli) {
        lastDisconnection = JavaDate.getNowDate();
        countDisconnected++;

        for (LocalClientListener l : connectionsListeners)
            l.onLocalDisconnected(jslObj, jslLocCli);
    }


    // Clients connections listeners interfaces

    /**
     * Local clients events interface.
     */
    public interface LocalClientListener {

        void onLocalConnected(JSLRemoteObject jslObj, JSLLocalClient jslLocCli);

        void onLocalConnectionError(JSLLocalClient jslLocCli, Throwable throwable);

        void onLocalDisconnected(JSLRemoteObject jslObj, JSLLocalClient jslLocCli);

    }


    // Messages methods ClientLocalEvents listener

    public void onClientConnected(JSLLocalClient client) {
        localClients.put(client, true);

        JSLRemoteObject rObj = jslObjsMngr.addNewConnection(client);
        if (rObj != null)
            emit_LocalConnected(rObj, client);
        else
            emit_LocalConnectionError(client, String.format("Can't create client object for server %s", client.getRemoteId()));
    }

    public void onClientConnectionError(JSLLocalClient client, Throwable e) {
        log.debug(String.format("Error on '%s' connection: [%s] '%s'",
                client, e.getClass().getSimpleName(), e));

        JSLRemoteObject rObj = jslObjsMngr.getByConnection(client);
        // TODO: analyze the error and print adeguate logging message
        log.warn(String.format("Error on '%s' connection: [%s]", client, rObj));

        emit_LocalConnectionError(client, e);
    }

    public void onClientDisconnected(JSLLocalClient client) {
        log.debug(String.format("Connection '%s:%d' closed with reason '%s'", client.getConnectionInfo().getRemoteInfo().getAddr(), client.getConnectionInfo().getRemoteInfo().getPort(), client.getDisconnectionReason()));

        JSLRemoteObject rObj = jslObjsMngr.getByConnection(client);
        localClients.remove(client);
        if (rObj == null)
            return;

        ((DefaultObjComm) rObj.getComm()).removeLocalClient(client);
        if (rObj.getComm().isLocalConnected()) {
            log.info(String.format("Connection '%s:%d' closed but object '%s' still connected",
                    client.getConnectionInfo().getRemoteInfo().getAddr(),
                    client.getConnectionInfo().getRemoteInfo().getPort(),
                    rObj.getName()));
        } else {
            log.info(String.format("Connection '%s:%d' closed for object '%s', check backups connections",
                    client.getConnectionInfo().getRemoteInfo().getAddr(),
                    client.getConnectionInfo().getRemoteInfo().getPort(),
                    rObj.getName()));
            List<JSLLocalClient> objClients = ((DefaultObjComm) rObj.getComm()).getLocalClients();
            for (JSLLocalClient obCli : objClients) {
                try {
                    obCli.connect();
                    break;
                } catch (PeerConnectionException e) {
                    log.debug(String.format("Object re-connection '%s:%d' attempt for object '%s' failed because [%s] %s",
                            obCli.getConnectionInfo().getRemoteInfo().getAddr(),
                            obCli.getConnectionInfo().getRemoteInfo().getPort(),
                            rObj.getName(), e.getClass().getSimpleName(), e));
                }
            }
            if (rObj.getComm().isLocalConnected())
                log.info(String.format("Object '%s' switched connection to '%s:%d'",
                        rObj.getName(),
                        ((DefaultObjComm) rObj.getComm()).getConnectedLocalClient().getConnectionInfo().getRemoteInfo().getAddr(),
                        ((DefaultObjComm) rObj.getComm()).getConnectedLocalClient().getConnectionInfo().getRemoteInfo().getPort()));
            else
                log.info(String.format("Object '%s' disconnected locally because no more connections available", rObj.getName()));
        }


        emit_LocalDisconnected(rObj, client);
    }

}
