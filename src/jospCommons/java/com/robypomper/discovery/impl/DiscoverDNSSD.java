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
import java.util.Arrays;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

/**
 * DNSSD discover.
 */
public class DiscoverDNSSD extends DiscoverAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(DiscoverDNSSD.class);
    private Thread browseThread = null;
    private Process browseProcess = null;
    private boolean isShuttingDown = false;


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType the service type to looking for.
     */
    public DiscoverDNSSD(String srvType) {
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

        JavaAssertions.makeAssertion(browseProcess == null, "Can't call DiscoverDNSSD::start() with browseProcess!=null.");
        try {
            String cmd = String.format("dns-sd -B %s", getServiceType());
            browseProcess = Runtime.getRuntime().exec(cmd);

        } catch (IOException e) {
            throw new DiscoveryException("Can't start DNSSD Discover", e);
        }

        isShuttingDown = false;
        JavaAssertions.makeAssertion(browseThread == null, "Can't call DiscoverDNSSD::start() with browseThread!=null.");
        browseThread = JavaThreads.initAndStart(new DNSSDBrowseRunnable(), DNSSD.TH_DISCOVER_NAME, getServiceType());

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

        JavaAssertions.makeAssertion(browseProcess != null, "Can't call DiscoverDNSSD::stop() with browseProcess==null.");
        if (browseProcess != null)
            try {
                browseProcess.destroy();
                browseProcess.waitFor(PROCESS_WAITING_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            } catch (InterruptedException e) {
                JavaAssertions.makeAssertion_Failed(e, "Exception on DNSSDDiscover '%s' browser thread termination interrupted");
            }
        browseProcess = null;

        isShuttingDown = true;
        JavaAssertions.makeAssertion(browseThread != null, "Can't call DiscoverDNSSD::stop() with browseThread==null.");
        if (browseThread != null)
            if (!JavaThreads.stopAndJoin(browseThread, 1000))
                JavaAssertions.makeAssertion_Failed("Error on DNSSDDiscover '%s' browser thread termination reached timeout");
        browseThread = null;

        deregisterAllServices();

        emitOnStop(this, log);
    }


    // Browsing

    /**
     * Runnable implementation for read and parsing <code>dns-sd</code>
     * command output.
     */
    private class DNSSDBrowseRunnable implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(browseProcess.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.trim().split("\\s+");

                    boolean founded = fields[1].equalsIgnoreCase("Add");
                    boolean lost = fields[1].equalsIgnoreCase("Rmv");
                    if (!founded && !lost)
                        continue;

                    ListIterator<String> iterator = Arrays.asList(fields).listIterator(6);
                    StringBuilder nameBuilder = new StringBuilder();
                    while (iterator.hasNext())
                        nameBuilder.append(iterator.next()).append(' ');
                    String name = nameBuilder.toString().trim();

                    if (founded)
                        onFound(name, fields);

                    if (lost)
                        onLost(name, fields);

                }

            } catch (IOException e) {
                // exception throw by reader.readLine()
                if (!isShuttingDown)
                    emitOnFail(DiscoverDNSSD.this, log, "Error reading DNS-SD process output", e);
            }

        }

    }

    private void onFound(String srvName, String[] fields) {
        ////Browsing for _discTest._tcp
        ////DATE: ---Thu 23 Jul 2020---
        ////16:59:23.941  ...STARTING...
        ////Timestamp     A/R    Flags  if Domain               Service Type         Instance Name
        //17:01:29.571  Add        2   4 local.               _discTest._tcp.      Pinco Pallo Example
        //17:01:29.808  Add        2   1 local.               _discTest._tcp.      Pinco Pallo Example

        if (!disableLogs)
            log.trace(String.format("Found service '%s'", srvName));

        JavaThreads.initAndStart(new DNSSDResolve_L_Runnable(srvName, fields[3]), DNSSD.TH_RESOLVER_NAME, srvName);
        //resolveViaZ(name, fields[3]);
        //resolveViaL(srvName, fields[3]);
    }

    private class DNSSDResolve_L_Runnable implements Runnable {

        private final String srvName;
        private final String srvIntf;

        private DNSSDResolve_L_Runnable(String srvName, String srvIntf) {
            this.srvName = srvName;
            this.srvIntf = srvIntf;
        }

        @Override
        public void run() {
            String[] cmdArray = new String[]{"dns-sd", "-L", srvName, getServiceType()};
            Process resolveProcess;
            try {
                resolveProcess = Runtime.getRuntime().exec(cmdArray);

            } catch (IOException e) {
                emitOnFail(DiscoverDNSSD.this, log, String.format("Error resolving service '%s' executing resolve process", srvName), e);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(resolveProcess.getInputStream()));
            String line;
            IOException ex = null;
            try {
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("Lookup ") && line.contains(PublisherAbs.getServiceNameEncoded_DNSSD(srvName))) {

                        if (!onLookUp_L(srvName, line, srvIntf))
                            continue;

                        resolveProcess.destroy();
                        return;
                    }
                }
            } catch (IOException e) {
                ex = e;
            }

            emitOnFail(DiscoverDNSSD.this, log, String.format("Error resolving service '%s' reading resolve process output", srvName), ex);
        }

    }

    private class DNSSDResolve_Z_Runnable implements Runnable {

        private final String srvName;
        private final String srvIntf;

        private DNSSDResolve_Z_Runnable(String srvName, String srvIntf) {
            this.srvName = srvName;
            this.srvIntf = srvIntf;
        }

        @Override
        public void run() {
            String cmd = String.format("dns-sd -Z %s.", getServiceType());
            Process resolveProcess;
            try {
                resolveProcess = Runtime.getRuntime().exec(cmd);

            } catch (IOException e) {
                emitOnFail(DiscoverDNSSD.this, log, String.format("Error resolving service '%s' executing resolve process", srvName), e);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(resolveProcess.getInputStream()));
            String line;
            IOException ex = null;
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(getServiceType())) {

                        String line2 = reader.readLine();
                        String line3 = reader.readLine();
                        if (!onLookUp_Z(srvName, line, line2, line3, srvIntf))
                            continue;

                        resolveProcess.destroy();
                        return;
                    }
                }
            } catch (IOException e) {
                ex = e;
            }

            emitOnFail(DiscoverDNSSD.this, log, String.format("Error resolving service '%s' reading resolve process output", srvName), ex);
        }

    }

    private boolean onLookUp_L(String srvName, String line, String srvIntf) {
        //Lookup Pinco Pallo Example (DNSSD - PubDisc&Pub)._discTest._tcp.local
        //DATE: ---Mon 27 Jul 2020---
        //15:40:09.200  ...STARTING...
        //15:40:09.201  Pinco\032Pallo\032Example\032(DNSSD\032-\032PubDisc&Pub)._discTest._tcp.local. can be reached at MacBook-Pro-di-Roberto.local.:5555 (interface 1) Flags: 1
        //19:09:32.256  Pinco\032Pallo\032Example\032B._discTest._tcp.local. can be reached at MacBook-Pro-di-Roberto.local.:5555 (interface 4)

        String[] fields_l1 = line.trim().split("\\s+");

        String intf_Extracted = fields_l1[8].substring(0, fields_l1[8].length() - 1);
        if (!intf_Extracted.equalsIgnoreCase(srvIntf))
            return false;

        String proto = "N/A";
        String name = PublisherAbs.getServiceNameDecoded_DNSSD(fields_l1[1].substring(0, fields_l1[1].indexOf(getServiceType()) - 1));
        String type = getServiceType();
        String extra = "";//fields_l3[2];
        String inetAddrStr = fields_l1[6].substring(0, fields_l1[6].indexOf(":"));
        InetAddress inetAddr;
        try {
            inetAddr = InetAddress.getByName(inetAddrStr);
        } catch (UnknownHostException e) {
            emitOnFail(this, log, String.format("Error resolving hostname '%s' for service '%s'", inetAddrStr, srvName), e);
            return false;
        }
        int port = Integer.parseInt(fields_l1[6].substring(fields_l1[6].indexOf(":") + 1));

        DiscoveryService discSrv = new DiscoveryService(name, type, intf_Extracted, proto, inetAddr, port, extra);
        registerService(discSrv);

        return true;
    }

    private boolean onLookUp_Z(String srvName, String line1, String line2, String line3, String srvIntf) {//_josp2._tcp                                     PTR     Star_apple_78-6499._josp2._tcp
        //Star_apple_78-6499._josp2._tcp                  SRV     0 0 1234 fe80-0-0-0-c41-51f0-dc6-78ee-en0.local. ; Replace with unicast FQDN of target host
        //Star_apple_78-6499._josp2._tcp                  TXT     "6499"
        //
        //_josp2._tcp                                     PTR     My\032Test._josp2._tcp
        //My\032Test._josp2._tcp                          SRV     0 0 515 MacBook-Pro-di-Roberto.local. ; Replace with unicast FQDN of target host
        //My\032Test._josp2._tcp                          TXT     "pdl=application/postscript"

        String[] fields_l1 = line1.split("\\s+");
        String[] fields_l2 = line2.split("\\s+");
        String[] fields_l3 = line3.split("\\s+");

        String intf_Extracted = fields_l1[8].substring(0, fields_l1[8].length() - 1);
        if (!intf_Extracted.equalsIgnoreCase(srvIntf))
            return false;

        String proto = "N/A";
        //String name = PublisherAbs.getServiceNameDecoded(fields_l1[2].substring(0, fields_l1[2].indexOf(getServiceType()) - 1));
        String name = fields_l1[2].substring(0, fields_l1[2].indexOf(getServiceType()) - 1).replaceAll("\\\\032", " ");
        String type = fields_l1[0];
        String extra = fields_l3[2];
        String inetAddrStr = fields_l2[5];
        InetAddress inetAddr;
        try {
            inetAddr = InetAddress.getByName(inetAddrStr);
        } catch (UnknownHostException e) {
            emitOnFail(this, log, String.format("Error resolving hostname '%s' for service '%s'", inetAddrStr, srvName), e);
            return false;
        }
        int port = Integer.parseInt(fields_l2[4]);

        DiscoveryService discSrv = new DiscoveryService(name, type, srvIntf, proto, inetAddr, port, extra);
        registerService(discSrv);

        return true;
    }

    private void onLost(String srvName, String[] fields) {
        ////Browsing for _discTest._tcp
        ////DATE: ---Thu 23 Jul 2020---
        ////16:59:23.941  ...STARTING...
        ////Timestamp     A/R    Flags  if Domain               Service Type         Instance Name
        //17:01:41.087  Rmv        0   4 local.               _discTest._tcp.      Pinco Pallo Example
        //17:01:45.335  Rmv        0   1 local.               _discTest._tcp.      Pinco Pallo Example

        DiscoveryService lostSrv = new DiscoveryService(srvName, fields[5], fields[3], null, null, null, null);
        deregisterService(lostSrv);
    }

}
