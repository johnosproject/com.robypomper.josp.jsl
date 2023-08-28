/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.apis.mngs;

import com.robypomper.java.JavaTimers;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.apis.mngs.exceptions.GWNotAvailableException;
import com.robypomper.josp.jcp.apis.mngs.exceptions.GWNotFoundException;
import com.robypomper.josp.jcp.apis.mngs.exceptions.GWNotReachableException;
import com.robypomper.josp.jcp.apis.mngs.exceptions.GWResponseException;
import com.robypomper.josp.jcp.clients.JCPClientsMngr;
import com.robypomper.josp.jcp.clients.JCPGWsClient;
import com.robypomper.josp.jcp.db.apis.GWDBService;
import com.robypomper.josp.jcp.db.apis.entities.GW;
import com.robypomper.josp.jcp.db.apis.entities.GWStatus;
import com.robypomper.josp.states.StateException;
import com.robypomper.josp.types.josp.gw.GWType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * GWsManager allow to register JCP GWs and make them available for JOD/JSL
 * clients.
 * <p>
 * The GWsManager keep the GWs list update and auto-remove unavailable GWs. The
 * checks are performed during GW registration and when a client (JOD/JSL)
 * require GW's access info.<br>
 * If a GW fail availability check, then the GWsManager start a timer to
 * periodically check if tested GW return available. If it fails after
 * {@link #availabilityAttempts} then thw GW is removed.
 * <p>
 * When register a GW the GWsManager crete also his {@link JCPGWsClient} client
 * on {@link JCPClientsMngr}.
 * <p>
 * GWsManager is used by:<br>
 * **APIGWsController (called by JOD/JSL)**: allow JOD and JSL to register
 * their certificate to the GW and return Access Info with GW address and
 * port.<br>
 * **JCPAPIGWsRegistrationController (called by JCP GWs)**: register a gw on
 * JCP APIs.<br>
 * **JCPAPIsStatusController (called by JSL):** require GWs and GWsManager
 * status.<br>
 */
@Component
public class GWsManager implements ApplicationListener<ContextRefreshedEvent> {

    // Class constants

    public static final String TH_AVAILABILITY_CHECK_NAME = "GW_AVAIL_CK_%s";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(GWsManager.class);
    private final GWDBService gwService;
    private final JCPClientsMngr clientsMngr;
    private final boolean loadOnStartup;    // must be false on jcpAll, because HTTPS wrong hostname exception
    private final int availabilityTimersDelayMS;
    private final int availabilityAttempts;
    private int removed = 0;
    private int total = 0;
    private final Map<String, Timer> availabilityTimers = new HashMap<>();

    @Autowired
    public GWsManager(@Value("${jcp.apis.gws.cache.load_on_startup:true}") final boolean loadOnStartup,
                      @Value("${jcp.apis.gws.availability.delay:30000}") final int availabilityTimersDelayMS,
                      @Value("${jcp.apis.gws.availability.attempts:10}") final int availabilityAttempts,
                      GWDBService gwService, JCPClientsMngr clientsMngr) {
        this.gwService = gwService;
        this.clientsMngr = clientsMngr;
        this.loadOnStartup = loadOnStartup;
        this.availabilityTimersDelayMS = availabilityTimersDelayMS;
        this.availabilityAttempts = availabilityAttempts;

        if (!this.loadOnStartup)
            cleanCachedGWsFromDB();
    }


    // Spring events listener

    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (this.loadOnStartup)
            loadCachedGWsFromDB();
    }


    // GW management

    public void register(String gwId, com.robypomper.josp.jcp.defs.apis.internal.gateways.registration.Params20.JCPGWsStartup gwStartup) {
        GW gw;
        try {
            gw = getById(gwId);

        } catch (GWNotFoundException e) {
            gw = createGW(gwId, gwStartup.gwAddr, gwStartup.gwAPIsPort);
        }

        gw.setType(gwStartup.type);
        gw.setGwAddr(gwStartup.gwAddr);
        gw.setGwPort(gwStartup.gwPort);
        gw.setGwAPIsAddr(gwStartup.gwAddr);
        gw.setGwAPIsPort(gwStartup.gwAPIsPort);
        gw.setClientsMax(gwStartup.clientsMax);
        gw.setVersion(gwStartup.version);

        if (!checkGWAvailability(gw))
            startGWAvailabilityTimer(gw);

        save(gw);
    }

    public void update(String gwId, com.robypomper.josp.jcp.defs.apis.internal.gateways.registration.Params20.JCPGWsStatus gwStatus) throws GWNotFoundException {
        GW gw = getById(gwId);

        gw.getStatus().setOnline(true);
        gw.getStatus().setClients(gwStatus.clients);
        gw.getStatus().setLastClientConnectedAt(gwStatus.lastClientConnectedAt);
        gw.getStatus().setLastClientDisconnectedAt(gwStatus.lastClientDisconnectedAt);
        gw.setClientsMax(gwStatus.clientsMax);

        save(gw);
    }

    public void deregister(String gwId) throws GWNotFoundException {
        GW gw = getById(gwId);

        removeGW(gw);
    }

    private GW createGW(String gwId, String gwAPIsAddr, int gwAPIsPort) {
        GW gw = new GW();
        gw.setGwId(gwId);

        GWStatus gwStatus = new GWStatus();
        gwStatus.setGwId(gwId);
        gw.setStatus(gwStatus);

        clientsMngr.createGWsClientByGW(gwId, gwAPIsAddr, gwAPIsPort);
        log.info(String.format("Registered new JCP GW '%s'", gwId));

        total++;

        return gw;
    }

    private void removeGW(GW gw) {
        delete(gw);
        JCPGWsClient gwClient = clientsMngr.removeGWsClientByGW(gw.getGwId());

        try {
            gwClient.disconnect();

        } catch (StateException ignore) {
        }

        log.info(String.format("Removed JCP GW '%s'", gw.getGwId()));

        removed++;
    }


    // JCP GWs Access Info

    public com.robypomper.josp.jcp.defs.gateways.internal.clients.registration.Params20.O2SAccessInfo getAccessInfo(String objId, com.robypomper.josp.jcp.defs.gateways.internal.clients.registration.Params20.O2SAccessRequest accessRequest) throws GWNotAvailableException, GWNotReachableException, GWResponseException {
        GW gw = getAvailable(GWType.Obj2Srv);
        com.robypomper.josp.jcp.callers.gateways.clients.registration.Caller20 apiGWsGWs = new com.robypomper.josp.jcp.callers.gateways.clients.registration.Caller20(clientsMngr.getGWsClientByGW(gw.getGwId()));
        try {
            return apiGWsGWs.postO2SAccess(objId, accessRequest);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException e) {
            throw new GWNotReachableException(gw.getGwId(), e);

        } catch (JCPClient2.RequestException e) {
            throw new GWResponseException(gw.getGwId(), e);
        }
    }

    public com.robypomper.josp.jcp.defs.gateways.internal.clients.registration.Params20.S2OAccessInfo getAccessInfo(String srvId, com.robypomper.josp.jcp.defs.gateways.internal.clients.registration.Params20.S2OAccessRequest accessRequest) throws GWNotAvailableException, GWNotReachableException, GWResponseException {
        GW gw = getAvailable(GWType.Srv2Obj);
        com.robypomper.josp.jcp.callers.gateways.clients.registration.Caller20 apiGWsGWs = new com.robypomper.josp.jcp.callers.gateways.clients.registration.Caller20(clientsMngr.getGWsClientByGW(gw.getGwId()));
        try {
            return apiGWsGWs.postS2OAccess(srvId, accessRequest);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException e) {
            throw new GWNotReachableException(gw.getGwId(), e);

        } catch (JCPClient2.RequestException e) {
            throw new GWResponseException(gw.getGwId(), e);
        }
    }


    // DB methods

    private void save(GW gw) {
        gwService.save(gw);
    }

    private void delete(GW gw) {
        gwService.delete(gw);
    }

    private void loadCachedGWsFromDB() {
        for (GW gw : gwService.getAll()) {
            clientsMngr.createGWsClientByGW(gw.getGwId(), gw.getGwAPIsAddr(), gw.getGwAPIsPort());
            log.info(String.format("Registered JCP GW '%s' from DB", gw.getGwId()));
            if (!checkGWAvailability(gw))
                startGWAvailabilityTimer(gw);
            total++;
        }
    }

    private void cleanCachedGWsFromDB() {
        gwService.deleteAll();
    }


    // Getters

    public int getGWsCount() {
        return gwService.getAll().size();
    }

    public int getGWsRemovedCount() {
        return removed;
    }

    public int getGWsTotalCount() {
        return total;
    }

    public List<GW> getAllGWs() {
        return gwService.getAll();
    }

    public GW getById(String gwId) throws GWNotFoundException {
        Optional<GW> op = gwService.findById(gwId);
        if (!op.isPresent())
            throw new GWNotFoundException(gwId);

        return op.get();
    }

    private GW getAvailable(GWType gwType) throws GWNotAvailableException {
        List<GW> gws = gwService.getAll(gwType);
        if (gws.isEmpty())
            throw new GWNotAvailableException(gwType);

        for (GW gw : gws)
            if (checkGWAvailability(gw))
                return gw;

        throw new GWNotAvailableException(gwType);
    }


    // Availability checks

    private boolean checkGWAvailability(GW gw) {
        com.robypomper.josp.jcp.callers.base.status.executable.Caller20 cl = new com.robypomper.josp.jcp.callers.base.status.executable.Caller20(clientsMngr.getGWsClientByGW(gw.getGwId()));

        // Test JCP GWs status APIs
        try {
            cl.getOnlineReq();
            if (!gw.getStatus().isOnline())
                log.info(String.format("JCP APIs check JCP GWs '%s' connectivity success", gw.getGwId()));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(String.format("JCP APIs check JCP GWs '%s' connectivity failed", gw.getGwId()));
            updateGWAvailability(gw, false);
            return false;
        }

        // Test JCP GW JOSP server
        //if (!JavaNetworks.checkSocketReachability(gw.getGwAddr(), gw.getGwPort(), AVAILABILITY_SOCKET_TIMEOUT_MS)) {
        //    log.warn(String.format("JCP APIs check JCP GWs '%s' connectivity failed", gw.getGwId()) + "\n" + ControllerError.concatenateCauses(e));
        //    updateGWAvailability(gw, false);
        //    return false;
        //}

        updateGWAvailability(gw, true);
        return true;
    }

    private void updateGWAvailability(GW gw, boolean online) {

        log.info(String.format("Update JCP GW '%s' availability", gw.getGwId()));
        log.info(String.format("'%s'", gw.getStatus()!=null ? gw.getStatus() : "NULL"));
        if (gw.getStatus().isOnline() == online)
            return;

        gw.getStatus().setOnline(online);
        save(gw);
    }


    // Availability timer

    private void startGWAvailabilityTimer(GW gw) {
        if (availabilityTimers.containsKey(gw.getGwId()))
            return;

        Timer timer = JavaTimers.initAndStart(new CheckAvailability(gw), String.format(TH_AVAILABILITY_CHECK_NAME, gw.getGwId()), availabilityTimersDelayMS, availabilityTimersDelayMS);

        availabilityTimers.put(gw.getGwId(), timer);
    }

    private void stopGWAvailabilityTimer(GW gw) {
        Timer removedJSLTimer = availabilityTimers.remove(gw.getGwId());
        JavaTimers.stopTimer(removedJSLTimer);
    }

    private class CheckAvailability implements Runnable {

        private final GW gw;

        public CheckAvailability(GW gw) {
            this.gw = gw;
        }

        @Override
        public void run() {
            if (checkGWAvailability(gw)) {
                gw.getStatus().setReconnectionAttempts(0);
                save(gw);
                stopGWAvailabilityTimer(gw);
                return;
            }

            int attempts = gw.getStatus().getReconnectionAttempts();

            if (attempts > availabilityAttempts) {
                stopGWAvailabilityTimer(gw);
                removeGW(gw);
                log.warn(String.format("JCP APIs removed JCP GWs '%s' of type %s with '%s:%d' address because not reachable after %d attempts", gw.getGwId(), gw.getType(), gw.getGwAddr(), gw.getGwPort(), attempts));
                return;
            }

            gw.getStatus().setReconnectionAttempts(++attempts);
            save(gw);
        }

    }

}
