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

import com.robypomper.discovery.DiscoverAbs;
import com.robypomper.discovery.DiscoveryService;
import com.robypomper.discovery.PublisherAbs;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaThreads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Avahi discover.
 */
public class DiscoverAvahi extends DiscoverAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(DiscoverAvahi.class);
    private Thread browseThread = null;
    private Process browseProcess = null;
    private boolean isShuttingDown = false;


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType the service type to looking for.
     */
    public DiscoverAvahi(String srvType) {
        super(srvType);
    }


    // Discovery mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws DiscoveryException {
        if (getState().isRunning())
            return;

        if (getState().isStartup())
            return;

        emitOnStarting(this, log);

        JavaAssertions.makeAssertion(browseProcess == null, "Can't call DiscoverAvahi::start() with browseProcess!=null.");
        try {
            String cmd = String.format("avahi-browse -pr %s.", getServiceType());
            browseProcess = Runtime.getRuntime().exec(cmd);

        } catch (IOException e) {
            throw new DiscoveryException("Can't start Avahi Discover", e);
        }

        isShuttingDown = false;
        JavaAssertions.makeAssertion(browseThread == null, "Can't call DiscoverAvahi::start() with browseThread!=null.");
        browseThread = JavaThreads.initAndStart(new AvahiBrowseRunnable(), Avahi.TH_DISCOVER_NAME, getServiceType());

        emitOnStart(this, log);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (getState().isStopped())
            return;

        if (getState().isShutdown())
            return;

        emitOnStopping(this, log);

        JavaAssertions.makeAssertion(browseProcess != null, "Can't call DiscoverAvahi::stop() with browseProcess==null.");
        if (browseProcess != null)
            try {
                browseProcess.destroy();
                browseProcess.waitFor(PROCESS_WAITING_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            } catch (InterruptedException e) {
                JavaAssertions.makeAssertion_Failed(e, "Exception on AvahiDiscover '%s' browser thread termination interrupted");
            }
        browseProcess = null;

        isShuttingDown = true;
        JavaAssertions.makeAssertion(browseThread != null, "Can't call DiscoverAvahi::stop() with browseThread==null.");
        if (browseThread != null)
            if (!JavaThreads.stopAndJoin(browseThread, 1000))
                JavaAssertions.makeAssertion_Failed("Error on AvahiDiscover '%s' browser thread termination reached timeout");
        browseThread = null;

        deregisterAllServices();

        emitOnStop(this, log);
    }


    // Browsing

    /**
     * Runnable implementation for read and parsing <code>avahi-browse</code>
     * command output.
     */
    private class AvahiBrowseRunnable implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(browseProcess.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    boolean founded = line.startsWith("+;");
                    boolean resolved = line.startsWith("=;");
                    boolean lost = line.startsWith("-;");

                    if (!founded && !resolved && !lost)
                        continue;

                    if (founded)
                        if (!disableLogs)
                            log.trace(String.format("Found service '%s'", line));

                    if (resolved)
                        onResolved(line);

                    if (lost)
                        onLost(line);
                }

            } catch (IOException e) {
                // exception throw by reader.readLine()
                if (!isShuttingDown)
                    emitOnFail(DiscoverAvahi.this, log, "Error reading DNS-SD process output", e);
            }

        }

    }

    private void onResolved(String line) {
        //=;docker0;IPv4;JOD_Gw_Test_\040\123PROTOCOL_NAME\125\041-1625630496;_josp._tcp;local;roby-hp.local;172.17.0.1;42175;"robypomper"
        //=;lo;IPv4;SrvName;_http._tcp;local;localhost;127.0.0.1;1234;"robypomper"

        String[] fields = line.split(";");
        String intf = fields[1];
        String proto = fields[2];
        String name = PublisherAbs.getServiceNameDecoded_Avahi(fields[3]);
        String type = fields[4];
        String extra = fields.length == 10 ? fields[9] : null;
        String inetAddrStr = fields[7];
        InetAddress inetAddr;
        try {
            inetAddr = InetAddress.getByName(inetAddrStr);
        } catch (UnknownHostException e) {
            emitOnFail(this, log, String.format("Error resolving hostname '%s' for service '%s'", inetAddrStr, name), e);
            return;
        }
        int port = Integer.parseInt(fields[8]);

        DiscoveryService discSrv = new DiscoveryService(name, type, intf, proto, inetAddr, port, extra);
        registerService(discSrv);
    }

    private void onLost(String line) {
        //-;wlo1;IPv6;JOD_Gw_Test_\040\123PROTOCOL_NAME\125\041-1769875233;_josp._tcp;local
        //-;lo;IPv4;SrvName;_http._tcp;local

        String[] fields = line.split(";");
        String intf = fields[1];
        String proto = fields[2];
        String name = PublisherAbs.getServiceNameDecoded_Avahi(fields[3]);
        String type = fields[4];

        DiscoveryService lostSrv = new DiscoveryService(name, type, intf, proto, null, null, null);

        deregisterService(lostSrv);
    }

}
