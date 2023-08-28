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

package com.robypomper.discovery.impl;

import com.robypomper.java.JavaThreads;
import com.robypomper.java.JavaTimers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.*;
import java.util.*;


/**
 * Implementations of the discovery system based on JmDNS (single cast).
 * <p>
 * This implementation try to publish service on all available interfaces, like
 * the {@link JmmDNS} implementation. Differently from {@link JmmDNS},
 * this class monitor all {@link NetworkInterface} detected by Java and add them
 * to the interface list. Then create a JmDNS object for each interface detected.
 * <p>
 * Ths JmDNS sub-system is implemented as static method, so active interfaces,
 * published services and registered listeners are shared among all instance
 * of this class.
 */
public class JmDNS {

    // Class constants

    public static final String IMPL_NAME = "JmDNS";
    public static final String TH_INTERFACE_LOOP = IMPL_NAME + "_INTERFACE_LOOP";
    public static final int INTERFACE_LOOP_INTERVAL_MS = 5 * 1000;


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JmDNS.class);
    private static List<Object> callers = new ArrayList<>();
    // interfaces
    private static Timer intfTimer = null;
    private static final List<NetworkInterface> interfaces = new ArrayList<>();
    private static final Map<String, javax.jmdns.JmDNS> jmDNSs = new HashMap<>();
    // published services
    private static final List<Service> pubServices = new ArrayList<>();
    private static final Map<String, ServiceInfo> pubServicesByInterface = new HashMap<>();
    // discovering listeners
    private static final Map<ServiceListener, String> discListeners = new HashMap<>();


    // JmDNS sub-system mngm

    /**
     * @return <code>true</code> if the JmDNS sub-system is running.
     */
    public static boolean isStarted() {
        return intfTimer != null;
    }

    /**
     * Start the JmDNS sub-system.
     * <p>
     * It start a timer to monitor the network interfaces.
     * Each time a new interface is found, a new JmDNS instance is created.
     */
    public static void startJmDNSSubSystem(Object caller) {
        callers.add(caller);

        if (isStarted())
            return;

        intfTimer = JavaTimers.initAndStart(new InterfaceWatchdogTimer(), true, TH_INTERFACE_LOOP, 0, INTERFACE_LOOP_INTERVAL_MS);
    }

    /**
     * Stop the JmDNS sub-system.
     * <p>
     * It terminate the timer to monitor the network interfaces and close all
     * JmDNS instances created.
     */
    public static void stopJmDNSSubSystem(Object caller) {
        callers.remove(caller);
        if (callers.size() > 0)
            return;

        if (!isStarted())
            return;

        // Stop interface monitor timer
        JavaTimers.stopTimer(intfTimer);
        intfTimer = null;

        synchronized (jmDNSs) {
            // Close all JmDNS interfaces
            for (javax.jmdns.JmDNS jmDNS : jmDNSs.values())
                try {
                    jmDNS.close();

                } catch (IOException e) {
                    log.warn("Can't close JmDNS instance.", e);
                }

            List<NetworkInterface> intfsList = new ArrayList<>(interfaces);
            for (NetworkInterface intf : intfsList)
                JavaThreads.initAndStart(new RemoveInterfaceRunnable(intf), TH_INTERFACE_LOOP + "_REM");
        }
        jmDNSs.clear();
        interfaces.clear();
    }

    private static class InterfaceWatchdogTimer implements Runnable {

        @Override
        public void run() {
            List<NetworkInterface> newInterfaces;
            try {
                newInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

            } catch (SocketException e) {
                log.warn("Can't list localhost interfaces, retry at next attempt", e);
                return;
            }

            List<NetworkInterface> oldInterfaces = new ArrayList<>(interfaces);

            for (NetworkInterface intf : newInterfaces)
                if (!interfaces.contains(intf))
                    JavaThreads.initAndStart(new AddInterfaceRunnable(intf), TH_INTERFACE_LOOP + "_ADD");

            // check for removed interfaces
            for (NetworkInterface intf : oldInterfaces)
                if (!newInterfaces.contains(intf))
                    JavaThreads.initAndStart(new RemoveInterfaceRunnable(intf), TH_INTERFACE_LOOP + "_REM");

        }

    };


    // Interfaces methods

    /**
     * Create a JmDNS instance for given interface, then register listener and
     * public services (already registered or publicized) the created JmDNS
     * instance.
     */
    private static class AddInterfaceRunnable implements Runnable {

        private final NetworkInterface intf;

        private AddInterfaceRunnable(NetworkInterface intf) {
            this.intf = intf;
        }

        @Override
        public void run() {
            interfaces.add(intf);

            // Get interface address
            int count = 0;
            for (InterfaceAddress iAddr : intf.getInterfaceAddresses()) {
                InetAddress addr = iAddr.getAddress();
                String intfUniqueId = toUniqueId(intf, addr);

                try {
                    if (!intf.supportsMulticast())
                        throw new RuntimeException(String.format("Interface '%s' don't support multicast.", intf.getDisplayName()));

                    javax.jmdns.JmDNS jmDNS = javax.jmdns.JmDNS.create(addr);
                    synchronized (jmDNSs) {
                        jmDNSs.put(intfUniqueId, jmDNS);
                    }

                    synchronized (discListeners) {
                        for (Map.Entry<ServiceListener, String> s : discListeners.entrySet())
                            jmDNS.addServiceListener(s.getValue(), s.getKey());
                    }

                    synchronized (pubServices) {
                        for (Service s : pubServices)
                            try {
                                register(intfUniqueId, jmDNS, s);

                            } catch (IOException e) {
                                log.warn(String.format("Can't register '%s' service on '%s/%s'", s.getName(), intf.getDisplayName(), addr.getHostAddress()));
                            }
                    }
                } catch (RuntimeException | IOException e) {
                    log.warn(String.format("Can't initialize interface '%s' on address '%s'", intf.getDisplayName(), addr.getHostAddress()));
                }

                count++;
            }

            if (intf.getInterfaceAddresses().size() > 0 && count == 0)
                log.warn(String.format("Initialize interface '%s' with no address", intf.getDisplayName()));
        }

    }

    /**
     * Remove the JmDNS instance corresponding to the given interface, then
     * de-register all listener and de-public all services.
     */
    private static class RemoveInterfaceRunnable implements Runnable {

        private final NetworkInterface intf;

        private RemoveInterfaceRunnable(NetworkInterface intf) {
            this.intf = intf;
        }

        @Override
        public void run() {
            interfaces.remove(intf);

            for (InterfaceAddress iAddr : intf.getInterfaceAddresses()) {
                InetAddress addr = iAddr.getAddress();
                String intfUniqueId = toUniqueId(intf, addr);

                javax.jmdns.JmDNS jmDNS;
                synchronized (jmDNSs) {
                    jmDNS = jmDNSs.remove(intfUniqueId);
                }

                synchronized (pubServices) {
                    for (Service srv : pubServices)
                        deregister(intfUniqueId, jmDNS, srv);
                }

                synchronized (discListeners) {
                    for (Map.Entry<ServiceListener, String> s : discListeners.entrySet())
                        jmDNS.removeServiceListener(s.getValue(), s.getKey());
                }
            }
        }

    }


    // Publish services

    /**
     * Publish given service to all JmDNS instances.
     *
     * @param srv the service to publish.
     */
    public static void addPubService(Service srv) throws JmDNSException {
        //if (!isStarted())
        //    throw new JmDNSException("JmDNS sub system must be started before add services");

        synchronized (pubServices) {
            if (pubServices.contains(srv))
                return;

            pubServices.add(srv);
        }

        synchronized (jmDNSs) {
            // Publish service on all interfaces
            for (Map.Entry<String, javax.jmdns.JmDNS> entry : jmDNSs.entrySet()) {
                String intfUniqueId = entry.getKey();
                javax.jmdns.JmDNS jmDNS = entry.getValue();

                try {
                    register(intfUniqueId, jmDNS, srv);

                } catch (IOException e) {
                    log.warn(String.format("Can't register '%s' service on '%s'", srv.getName(), intfUniqueId));
                }
            }
        }
    }

    /**
     * De-publish given service from all JmDNS instances.
     *
     * @param srv the service to de-publish, this reference must be the same
     *            used in the {@link #addPubService(Service)}.
     */
    public static void removePubService(Service srv) throws JmDNSException {
        //if (!isStarted())
        //    throw new JmDNSException("JmDNS sub system must be started before remove services");

        synchronized (pubServices) {
            if (!pubServices.contains(srv))
                return;

            pubServices.remove(srv);
        }

        synchronized (jmDNSs) {
            for (Map.Entry<String, javax.jmdns.JmDNS> e : jmDNSs.entrySet()) {
                String intfUniqueId = e.getKey();
                javax.jmdns.JmDNS jmDNS = e.getValue();

                deregister(intfUniqueId, jmDNS, srv);
            }
        }
    }

    private static void register(String intfUniqueId, javax.jmdns.JmDNS jmDNS, Service srv) throws IOException {
        ServiceInfo si = srv.generateServiceInfo();
        jmDNS.registerServiceType(si.getType());
        jmDNS.registerService(si);
        pubServicesByInterface.put(intfUniqueId + srv.getName(), si);
    }

    private static void deregister(String intfUniqueId, javax.jmdns.JmDNS jmDNS, Service srv) {
        ServiceInfo si = pubServicesByInterface.get(intfUniqueId + srv.getName());
        jmDNS.unregisterService(si);
    }


    // Discover services

    /**
     * Register given listener to all JmDNS instances.
     *
     * @param listener the listener to register.
     */
    public static void addDiscoveryListener(String type, ServiceListener listener) throws JmDNSException {
        //if (!isStarted())
        //    throw new JmDNSException("JmDNS sub system must be started before add listeners");

        synchronized (discListeners) {
            if (discListeners.containsKey(listener))
                removeDiscoveryListener(discListeners.get(listener), listener);

            discListeners.put(listener, type);
        }

        synchronized (jmDNSs) {
            for (javax.jmdns.JmDNS jmDNS : jmDNSs.values())
                jmDNS.addServiceListener(type, listener);
        }
    }

    /**
     * De-register given listener from all JmDNS instances.
     *
     * @param listener the listener to de-register.
     */
    public static void removeDiscoveryListener(String type, ServiceListener listener) throws JmDNSException {
        //if (!isStarted())
        //    throw new JmDNSException("JmDNS sub system must be started before remove listeners");

        synchronized (discListeners) {
            if (!discListeners.containsKey(listener))
                return;

            discListeners.remove(listener);
        }

        synchronized (jmDNSs) {
            for (javax.jmdns.JmDNS jmDNS : jmDNSs.values())
                jmDNS.removeServiceListener(type, listener);
        }
    }


    /**
     * Return an unique id related to interface, address and protocol.
     * <p>
     * If it's a loop back interface, keep only first address for each IP protocol version.<br>
     * Otherwise it keep only the address string and the IP protocol version used.
     *
     * @param intf    interface to associate at returned unique id.
     * @param address address (and protocol) to associate at returned unique id.
     * @return a string containing the interface unique id.
     */
    public static String toUniqueId(NetworkInterface intf, InetAddress address) {
        String intfName = intf.getDisplayName();
        String proto = address instanceof Inet4Address ? "IPv4" : "IPv6";
        String hostaddress = null;

        try {
            if (intf.isLoopback())
                hostaddress = "localhost/" + proto;

        } catch (SocketException ignore) {
        }

        if (hostaddress == null)
            hostaddress = address.getHostAddress();

        // un unico indirizzo per interface
        return intfName + ":(" + proto + ")" + hostaddress;
    }

    /**
     * Return an unique id related to discovered service.
     * <p>
     * The service id is composed by:
     * <ul>
     *     <li>Discovered service's name</li>
     *     <li>Discovering interface's unique id</li>
     * </ul>
     *
     * @param event        the JmDNS event that discovered/resolved the service.
     * @param intfUniqueId the unique id associated to the discovering interface..
     * @return a string containing the service unique id.
     */
    public static String toUniqueId(ServiceEvent event, String intfUniqueId) {
        if (event.getInfo().getHostAddresses().length == 0)
            return String.format("%s/(intf)%s", event.getName(), intfUniqueId);

        StringBuilder address = new StringBuilder();
        for (String s : event.getInfo().getHostAddresses())
            address.append(s).append(",");
        return String.format("%s/(intf)%s/(srv)%s", event.getName(), intfUniqueId, address.substring(0, address.length() - 1));
    }


    /**
     * Transitional class: from group of fields to {@link ServiceInfo} instance.
     * <p>
     * JmDNS require dedicated instance of ServiceInfo for each pair service-JmDNS
     * instance. This class allow to create {@link ServiceInfo} objects starting
     * from the same values.
     */
    public static class Service {

        // Internal vars

        String type;
        String name;
        int port;
        String extraText;

        // Constructor

        /**
         * Default constructor.
         *
         * @param type      the service type to publish.
         * @param name      the service name to publish.
         * @param port      the service port to publish.
         * @param extraText the extra text related to the service to publish.
         */
        public Service(String type, String name, int port, String extraText) {
            this.type = type;
            this.name = name;
            this.port = port;
            this.extraText = extraText;
        }

        public ServiceInfo generateServiceInfo() {
            return ServiceInfo.create(type + ".local.", name, port, extraText);
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public int getPort() {
            return port;
        }

    }


    // Exceptions

    public static class JmDNSException extends Throwable {
        public JmDNSException(String msg) {
            super(msg);
        }

        public JmDNSException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
