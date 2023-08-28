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

package com.robypomper.discovery;

import com.robypomper.java.JavaListeners;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryBase<T, L extends DiscoveryBaseStateListener<T>> {

    enum DiscoveryType {
        Discover,
        Publisher
    }

    // Class constants

    protected static final int PROCESS_WAITING_TIMEOUT_MS = 5 * 1000;
    protected static final int PROCESS_WAITING_PUB_DEPUB_MS = 5 * 1000;

    // Internal vars

    private final DiscoveryType discoveryType;
    private DiscoveryState state = DiscoveryState.STOPPED;
    private final String srvType;
    // listeners
    private final List<L> listenersState = new ArrayList<>();
    private final List<DiscoveryServicesListener> listenersServicesDiscovery = new ArrayList<>();             // DiscoveryServicesListener
    // registered services
    private final List<DiscoveryService> discoveredServices = new ArrayList<>();
    // interfaces
    private final List<String> interfaces = new ArrayList<>();
    // logs
    protected boolean disableLogs;


    // Constructors

    public DiscoveryBase(String srvType) {
        this.srvType = srvType;
        if (this instanceof Discover)
            discoveryType = DiscoveryType.Discover;
        else
            discoveryType = DiscoveryType.Publisher;
    }


    // Getters

    public DiscoveryState getState() {
        return state;
    }

    public String getServiceType() {
        return srvType;
    }

    public List<DiscoveryService> getServicesDiscovered() {
        return discoveredServices;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    // Listeners

    public void addListener(L listener) {
        listenersState.add(listener);
    }

    public void removeListener(L listener) {
        listenersState.remove(listener);
    }

    protected void emitOnStarting(T emitter, Logger log) {
        state = DiscoveryState.STARTUP;
    }

    protected void emitOnStart(T emitter, Logger log) {
        if (!disableLogs)
            log.info(String.format("%s '%s' services started", discoveryType, getServiceType()));

        state = DiscoveryState.STARTED;

        JavaListeners.emitter(this, listenersState, "onStart", new JavaListeners.ListenerMapper<L>() {
            @Override
            public void map(L l) {
                l.onStart(emitter);
            }
        });
    }

    protected void emitOnStopping(T emitter, Logger log) {
        state = DiscoveryState.SHUTDOWN;
    }

    protected void emitOnStop(T emitter, Logger log) {
        if (!disableLogs)
            log.info(String.format("%s '%s' services stopped", discoveryType, getServiceType()));

        state = DiscoveryState.STOPPED;

        JavaListeners.emitter(this, listenersState, "onStop", new JavaListeners.ListenerMapper<L>() {
            @Override
            public void map(L l) {
                l.onStop(emitter);
            }
        });
    }

    protected void emitOnFail(T emitter, Logger log, String failMsg) {
        emitOnFail(emitter, log, failMsg, null);
    }

    protected void emitOnFail(T emitter, Logger log, String failMsg, Throwable exception) {
        if (!disableLogs)
            log.warn(String.format("%s '%s' services failed: '%s' [%s] %s", discoveryType, getServiceType(), failMsg, exception, exception.getMessage()));

        JavaListeners.emitter(this, listenersState, "onFail", new JavaListeners.ListenerMapper<L>() {
            @Override
            public void map(L l) {
                l.onFail(emitter, failMsg, exception);
            }
        });
    }

    public void addListener(DiscoveryServicesListener listener) {
        listenersServicesDiscovery.add(listener);
    }

    public void removeListener(DiscoveryServicesListener listener) {
        listenersServicesDiscovery.remove(listener);
    }

    protected void emitOnServiceDiscovered(Logger log, DiscoveryService discSrv) {
        if (!disableLogs)
            if (discoveryType == DiscoveryType.Discover)
                log.info(String.format("Discovered service '%s'", discSrv));
            else
                log.info(String.format("Self discovered service '%s'", discSrv));

        JavaListeners.emitter(this, listenersServicesDiscovery, "onServiceDiscovered", new JavaListeners.ListenerMapper<DiscoveryServicesListener>() {
            @Override
            public void map(DiscoveryServicesListener l) {
                l.onServiceDiscovered(discSrv);
            }
        });
    }

    protected void emitOnServiceLost(Logger log, DiscoveryService lostSrv) {
        if (!disableLogs)
            if (discoveryType == DiscoveryType.Discover)
                log.info(String.format("Lost service '%s'", lostSrv));
            else
                log.info(String.format("Self lost service '%s'", lostSrv));

        JavaListeners.emitter(this, listenersServicesDiscovery, "onServiceLost", new JavaListeners.ListenerMapper<DiscoveryServicesListener>() {
            @Override
            public void map(DiscoveryServicesListener l) {
                l.onServiceLost(lostSrv);
            }
        });
    }


    // Registered services

    protected void registerService(Logger log, DiscoveryService discSrv) {
        synchronized (discoveredServices) {
            if (discSrv.alreadyIn(discoveredServices))
                return;

            discoveredServices.add(discSrv);
        }
        emitOnServiceDiscovered(log, discSrv);
    }

    protected void deregisterService(Logger log, DiscoveryService lostSrv) {
        synchronized (discoveredServices) {
            if (!lostSrv.alreadyIn(discoveredServices))
                return;

            discoveredServices.remove(lostSrv.extractFrom(discoveredServices));
        }
        emitOnServiceLost(log, lostSrv);
    }

    protected void deregisterAllServices(Logger log) {
        List<DiscoveryService> toRemove = new ArrayList<>(discoveredServices);
        for (DiscoveryService srv : toRemove)
            deregisterService(log, srv);
    }


    // Registered interfaces

    protected void registerInterface(String addIntf) {
        if (interfaces.contains(addIntf))
            return;

        interfaces.add(addIntf);
    }

    protected void deregisterInterface(String remIntf) {
        if (!interfaces.contains(remIntf))
            return;

        interfaces.remove(remIntf);
    }

}
