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

package com.robypomper.josp.jsl;

import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.discovery.Discover;
import com.robypomper.java.JavaEnum;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.jsl.admin.JSLAdmin;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.states.JSLState;
import com.robypomper.josp.states.StateException;
import com.robypomper.log.Mrk_JOD;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;


/**
 * Default {@link JSL} implementation, initialization excluded.
 * <p>
 * This class fully manage a JSL library (connect,disconnect,state...) for all
 * JSL systems. But JSL systems initialization is delegate to his sub-classes.
 * That allow to initialize multiple JSL objects using different systems
 * implementations. Helping provide new JSL versions and flavours.
 * <p>
 * The JSL and {@link AbsJSL} hierarchy is design to allow sub-classes to initialize
 * JSL systems (user mngr, objs, comm...) and delegate JSL systems orchestration
 * to AbsJSL class. AbsJSL class manage JSL system using only their interfaces,
 * that make system implementation completely interoperable (at JSL level). So
 * AbsJSL sub-classes (like {@link JSL_002} can switch to different systems
 * implementations/versions keeping full compatibility with all others JSL
 * systems.
 * <p>
 * All AbsJSL sub-classes must implement the <code>instance(...)</code> method
 * and return a self instance. <code>instance(...)</code> method can be
 * implemented using {@link JSL.Settings} param or his sub-class.
 * Returned class from {@link FactoryJSL#getJSLClass(String)} must implement a
 * <code>instance(...)</code> method with a param corresponding to class returned
 * by <code>FactoryJOD#getJODSettingsClass</code>. Both method are called using
 * same String param corresponding to JSL version.
 */
@SuppressWarnings({"JavadocReference"})
public abstract class AbsJSL implements JSL {

    // Private systems references

    private final JSL.Settings settings;
    private final JCPAPIsClientSrv jcpClient;
    private final JSLServiceInfo srvInfo;
    private final JSLUserMngr user;
    private final JSLAdmin admin;
    private final JSLObjsMngr objs;
    private final JSLCommunication comm;


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JavaEnum.Synchronizable<JSLState> state = new JavaEnum.Synchronizable<>(JSLState.STOP);


    // Constructor

    /**
     * Default constructor, set all private systems references.
     *
     * @param settings  object containing current JOD configs.
     * @param jcpClient instance of JCP client for services.
     * @param srvInfo   {@link JSLServiceInfo} reference.
     * @param user      {@link JSLUserMngr} reference.
     * @param objs      {@link JSLObjsMngr} reference.
     * @param comm      {@link JSLCommunication} reference.
     */
    public AbsJSL(Settings settings, JCPAPIsClientSrv jcpClient, JSLServiceInfo srvInfo, JSLUserMngr user, JSLAdmin admin, JSLObjsMngr objs, JSLCommunication comm) {
        this.settings = settings;
        this.jcpClient = jcpClient;
        this.srvInfo = srvInfo;
        this.user = user;
        this.admin = admin;
        this.objs = objs;
        this.comm = comm;

        log.info(Mrk_JSL.JSL_MAIN, String.format("Initialized AbsJSL/%s instance for '%s' ('%s') service", this.getClass().getSimpleName(), srvInfo.getSrvName(), srvInfo.getFullId()));
    }


    // JSL mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLState getState() {
        return state.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startup() throws StateException {
        log.info(Mrk_JSL.JSL_MAIN, String.format("Start JSL instance for '%s' service", srvInfo.getSrvId()));
        log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL state is %s", getState()));

        if (state.enumEquals(JSLState.RUN))
            return; // Already done

        else if (state.enumEquals(JSLState.STARTING))
            return; // Already in progress

        else if (state.enumEquals(JSLState.RESTARTING))
            throw new StateException("Can't startup JSL library instance because is restarting, try again later");

        else if (state.enumEquals(JSLState.STOP))
            startupInstance();

        else if (state.enumEquals(JSLState.SHOUTING)) {
            throw new StateException("Can't startup JSL library instance because is shuting down, try again later");
        }

        log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL state is %s", getState()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() throws StateException {
        log.info(Mrk_JSL.JSL_MAIN, String.format("Shuting down JSL instance for '%s' service", srvInfo.getSrvId()));
        log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL state is %s", getState()));

        if (state.enumEquals(JSLState.RUN))
            shutdownInstance();

        else if (state.enumEquals(JSLState.STARTING))
            throw new StateException("Can't shout down JSL library instance because is starting, try again later");

        else if (state.enumEquals(JSLState.RESTARTING))
            throw new StateException("Can't shout down JSL library instance because is restarting, try again later");

        else if (state.enumEquals(JSLState.STOP))
            return; // Already done

        else if (state.enumEquals(JSLState.SHOUTING))
            return; // Already in progress

        log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL state is %s", getState()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean restart() throws StateException {
        log.info(Mrk_JSL.JSL_MAIN, String.format("Shuting down JSL instance for '%s' service", srvInfo.getSrvId()));
        log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL state is %s", getState()));

        if (state.enumEquals(JSLState.RUN))
            restartInstance();

        else if (state.enumEquals(JSLState.STARTING))
            throw new StateException("Can't restart JSL library instance because is starting, try again later");

        else if (state.enumEquals(JSLState.RESTARTING))
            return true; // Already in progress

        else if (state.enumEquals(JSLState.STOP))
            restartInstance();

        else if (state.enumEquals(JSLState.SHOUTING))
            throw new StateException("Can't restart JSL library instance because is shuting down, try again later");

        log.trace(Mrk_JSL.JSL_MAIN, String.format("JSL state is %s", getState()));
        return state.enumEquals(JSLState.STARTING);
    }

    @Override
    public void printInstanceInfo() {
        log.info(Mrk_JSL.JSL_MAIN, "JSL Srv");
        log.info(Mrk_JSL.JSL_MAIN, " -- IDs");
        log.info(Mrk_JSL.JSL_MAIN, "        JSL Srv");
        log.info(Mrk_JSL.JSL_MAIN, String.format("            ID                = %s", srvInfo.getSrvId()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("            name              = %s", srvInfo.getSrvName()));
        log.info(Mrk_JSL.JSL_MAIN, "        User");
        log.info(Mrk_JSL.JSL_MAIN, String.format("            ID                = %s", srvInfo.getUserId()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("            name              = %s", srvInfo.getUsername()));
        log.info(Mrk_JSL.JSL_MAIN, " -- Ver.s");
        log.info(Mrk_JSL.JSL_MAIN, String.format("    JSL Srv state             = %s", getState()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("    JSL Srv version           = %s", version()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("    JOSP JOD supported        = %s", Arrays.asList(versionsJOSPObject())));
        log.info(Mrk_JSL.JSL_MAIN, String.format("    JOSP protocol supported   = %s", Arrays.asList(versionsJOSPProtocol())));
        log.info(Mrk_JSL.JSL_MAIN, String.format("    JCP APIs supported        = %s", Arrays.asList(versionsJCPAPIs())));
        log.info(Mrk_JSL.JSL_MAIN, " -- Comm.s");
        log.info(Mrk_JSL.JSL_MAIN, "        JCP APIs");
        log.info(Mrk_JOD.JOD_MAIN, String.format("            State             = %s", comm.getCloudAPIs().getState()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("            Url               = %s", comm.getCloudAPIs().getAPIsUrl()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("            HostName          = %s", comm.getCloudAPIs().getAPIsHostname()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("            IsConnected       = %s", comm.getCloudAPIs().isConnected()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("            IsAuth            = %s", comm.getCloudAPIs().isUserAuthenticated()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("            LastConn          = %s", comm.getCloudAPIs().getLastConnection()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("            LastDiscon        = %s", comm.getCloudAPIs().getLastDisconnection()));
        log.info(Mrk_JOD.JOD_MAIN, "        Cloud Comm.");
        log.info(Mrk_JOD.JOD_MAIN, String.format("            State (Client)    = %s", comm.getCloudConnection().getState()));
        InetAddress cloudAddr = comm.getCloudConnection().getConnectionInfo().getRemoteInfo().getAddr();
        Integer cloudPort = comm.getCloudConnection().getConnectionInfo().getRemoteInfo().getPort();
        log.info(Mrk_JOD.JOD_MAIN, String.format("            HostName          = %s", (cloudAddr != null ? cloudAddr.getHostName() : "N/A")));
        log.info(Mrk_JOD.JOD_MAIN, String.format("            IPAddr            = %s", (cloudAddr != null ? cloudAddr.getHostAddress() : "N/A")));
        log.info(Mrk_JOD.JOD_MAIN, String.format("            Port            = %s", (cloudPort != null ? cloudPort : "N/A")));
        log.info(Mrk_JOD.JOD_MAIN, String.format("            IsConnected       = %s", comm.getCloudConnection().getState().isConnected()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("            LastConn          = %s", comm.getCloudConnection().getConnectionInfo().getStats().getLastConnection()));
        log.info(Mrk_JOD.JOD_MAIN, String.format("            LastDiscon        = %s", comm.getCloudConnection().getConnectionInfo().getStats().getLastDisconnection()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("            LastDisconReason  = %s", comm.getCloudConnection().getDisconnectionReason()));
        log.info(Mrk_JOD.JOD_MAIN, "        Local Comm.");
        log.info(Mrk_JSL.JSL_MAIN, String.format("            State (ClientMngr)= %s", comm.getLocalConnections().getState()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("            IsRunning         = %s", comm.getLocalConnections().isRunning()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("            ClientsCount      = %d", comm.getLocalConnections().getLocalClients().size()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("            ClientsConn       = %d", comm.getLocalConnections().getConnectedCount()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("            ClientsDisconn    = %d", comm.getLocalConnections().getDisconnectedCount()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("            LastStart         = %s", comm.getLocalConnections().getLastStartup()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("            LastStop          = %s", comm.getLocalConnections().getLastShutdown()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("            LastConn          = %s", comm.getLocalConnections().getLastObjConnection()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("            LastDisconn       = %s", comm.getLocalConnections().getLastObjDisconnection()));
        List<JSLRemoteObject> objsList = objs.getAllObjects();
        StringBuilder objNames = new StringBuilder();
        for (JSLRemoteObject ro : objsList)
            objNames.append(ro.getName()).append(", ");
        log.info(Mrk_JSL.JSL_MAIN, " -- Objs Mngr");
        log.info(Mrk_JSL.JSL_MAIN, String.format("        Count                 = %s", objsList.size()));
        log.info(Mrk_JSL.JSL_MAIN, String.format("        List                  = %s", objNames));
    }

    private void startupInstance() {
        assert state.enumEquals(JSLState.STOP)
                || state.enumEquals(JSLState.RESTARTING) :
                "Method startupInstance() can be called only from STOP or RESTARTING state";

        synchronized (state) {
            if (state.enumNotEquals(JSLState.RESTARTING))
                state.set(JSLState.STARTING);

            try {
                boolean startLocal = ((JSLSettings_002) settings).getLocalEnabled();
                log.info(Mrk_JSL.JSL_MAIN, String.format("JSLCommunication local communication %s", startLocal ? "enabled" : "disabled"));
                if (startLocal) comm.getLocalConnections().start();

            } catch (StateException | Discover.DiscoveryException e) {
                log.warn(Mrk_JSL.JSL_MAIN, String.format("Error on starting local communication of '%s' service because %s", srvInfo.getSrvId(), e.getMessage()), e);
            }

            try {
                boolean startCloud = ((JSLSettings_002) settings).getCloudEnabled();
                log.info(Mrk_JSL.JSL_MAIN, String.format("JCP GWs client %s", startCloud ? "enabled" : "disabled"));
                if (startCloud)
                    comm.getCloudConnection().connect();

            } catch (PeerConnectionException e) {
                log.warn(Mrk_JSL.JSL_MAIN, "JCP GWs client not connected, retry later", e);
            }

            if (state.enumNotEquals(JSLState.RESTARTING))
                state.set(JSLState.RUN);
        }

        log.info(Mrk_JSL.JSL_MAIN, String.format("JSL Service '%s' started", srvInfo.getSrvId()));

        printInstanceInfo();
    }

    private void shutdownInstance() {
        assert state.enumEquals(JSLState.RUN)
                || state.enumEquals(JSLState.RESTARTING) :
                "Method shutdownInstance() can be called only from RUN or RESTARTING state";

        synchronized (state) {
            if (state.enumNotEquals(JSLState.RESTARTING))
                state.set(JSLState.SHOUTING);

            log.trace(Mrk_JSL.JSL_MAIN, "JSLCommunication stop discovery and disconnect from JCP");
            try {
                comm.getLocalConnections().stop();

            } catch (StateException | Discover.DiscoveryException e) {
                log.warn(Mrk_JSL.JSL_MAIN, String.format("Error on stopping local communication service '%s''s objects discovery because %s", srvInfo.getSrvId(), e.getMessage()), e);
            }

            try {
                comm.getCloudConnection().disconnect();

            } catch (PeerDisconnectionException e) {
                log.warn(Mrk_JSL.JSL_MAIN, String.format("Error on disconnecting cloud communication of '%s' service because %s", srvInfo.getSrvId(), e.getMessage()), e);
            }

            if (state.enumNotEquals(JSLState.RESTARTING))
                state.set(JSLState.STOP);
        }

        log.info(Mrk_JSL.JSL_MAIN, String.format("JSL Service '%s' stopped", srvInfo.getSrvId()));
    }

    private void restartInstance() {
        assert state.enumEquals(JSLState.RUN)
                || state.enumEquals(JSLState.STOP) :
                "Method shutdownInstance() can be called only from RUN or STOP state";

        synchronized (state) {
            state.set(JSLState.RESTARTING);

            log.trace(Mrk_JSL.JSL_MAIN, "JSL shout down for restarting");
            if (state.enumEquals(JSLState.RUN))
                shutdownInstance();

            log.trace(Mrk_JSL.JSL_MAIN, "JSL startup for restarting");
            startupInstance();

            state.set(JSLState.RUN);
        }

        log.info(Mrk_JSL.JSL_MAIN, String.format("JSL Service '%s' restarted", srvInfo.getSrvId()));
    }


    // JSL Systems

    /**
     * {@inheritDoc}
     */
    @Override
    public JCPAPIsClientSrv getJCPClient() {
        return jcpClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLServiceInfo getServiceInfo() {
        return srvInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLUserMngr getUserMngr() {
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLAdmin getAdmin() {
        return admin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLObjsMngr getObjsMngr() {
        return objs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLCommunication getCommunication() {
        return comm;
    }

}
