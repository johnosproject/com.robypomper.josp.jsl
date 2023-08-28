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

import com.robypomper.discovery.impl.Avahi;
import com.robypomper.discovery.impl.DNSSD;
import com.robypomper.discovery.impl.JmDNS;
import com.robypomper.discovery.impl.JmmDNS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class DiscoveryTestGeneric {

    // Class constants

    //Registering Service `Pinco.Pallo._tcp.Example` port _discTest._tcp. TXT . 5555
    final static String DISC_SRV_NAME = "Pinco Pallo Example (%s - %s)";
    final static int DISC_SRV_PORT = 5555;
    final static String DISC_SRV_TYPE = "_discTest._tcp";


    // Internal vars

    protected static Logger log = LogManager.getLogger();

    String implName;
    int timeoutMs;

    Publisher pub;
    Publisher pub2;
    Discover disc;
    DiscoverStateListener_Latch discoverStateListener;
    PublisherStateListener_Latch publisherStateListener;
    DiscoveryServicesListener_Latch discoveryServiceListener;

    public DiscoveryTestGeneric(String implName) {
        this(implName, 1000);
    }

    public DiscoveryTestGeneric(String implName, int timeoutMs) {
        this.implName = implName;
        this.timeoutMs = timeoutMs;
    }


    // Test configurations

    @BeforeEach
    void setUp() {
        discoverStateListener = new DiscoverStateListener_Latch();
        publisherStateListener = new PublisherStateListener_Latch();
        discoveryServiceListener = new DiscoveryServicesListener_Latch();
    }

    @AfterEach
    void tearDown() {
        if (pub != null && pub.isPublishedPartially()) {
            System.out.println("TEAR_DOWN: Hide publisher 1");
            pub.hide(true);
        }
        pub = null;

        if (pub2 != null && pub2.isPublishedPartially()) {
            System.out.println("TEAR_DOWN: Hide publisher 2");
            pub2.hide(true);
        }
        pub2 = null;

        if (disc != null && disc.getState().isRunning()) {
            System.out.println("TEAR_DOWN: Stop discover");
            disc.stop();
        }
        disc = null;
    }


    // Generic implementation tests

    public void METHOD_Discover_startAndStop() throws Discover.DiscoveryException, InterruptedException {
        if (!checkDiscoverySystemSupportOnCurrentOs(implName)) {
            System.out.println("Start And Stop Discover TEST (" + implName + ") - NOT SUPPORTED on current OS (" + currentOs() + ")");
            return;
        }

        System.out.println("Create discover (" + implName + ")");
        disc = DiscoverySystemFactory.createDiscover(implName, DISC_SRV_TYPE);
        disc.addListener(discoverStateListener);

        System.out.println("Start discover (" + implName + ")");
        disc.start();
        Assertions.assertTrue(disc.getState().isRunning());
        Assertions.assertTrue(discoverStateListener.onStart.await(timeoutMs, TimeUnit.MILLISECONDS));

        System.out.println("Stop discover (" + implName + ")");
        disc.stop();
        Assertions.assertFalse(disc.getState().isRunning());
        Assertions.assertTrue(discoverStateListener.onStop.await(timeoutMs, TimeUnit.MILLISECONDS));
    }

    public void METHOD_Publisher_startAndStop() throws Publisher.PublishException, InterruptedException {
        if (!checkDiscoverySystemSupportOnCurrentOs(implName)) {
            System.out.println("Start And Stop Publisher TEST (" + implName + ") - NOT SUPPORTED on current OS (" + currentOs() + ")");
            return;
        }

        System.out.println("Create publisher (" + implName + ")");
        pub = DiscoverySystemFactory.createPublisher(implName, DISC_SRV_TYPE, String.format(DISC_SRV_NAME, implName, "Start&Stop"), DISC_SRV_PORT);
        pub.addListener(publisherStateListener);
        pub.addListener(discoveryServiceListener);

        System.out.println("Start publisher (" + implName + ")");
        pub.publish(false);
        Assertions.assertTrue(pub.getState().isRunning());
        Assertions.assertTrue(publisherStateListener.onStart.await(timeoutMs, TimeUnit.MILLISECONDS));
        Assertions.assertTrue(discoveryServiceListener.onServiceDiscovered.await(timeoutMs, TimeUnit.MILLISECONDS));

        System.out.println("Stop publisher (" + implName + ")");
        pub.hide(false);
        Assertions.assertFalse(pub.getState().isRunning());
        Assertions.assertTrue(publisherStateListener.onStop.await(timeoutMs, TimeUnit.MILLISECONDS));
        Assertions.assertTrue(discoveryServiceListener.onServiceLost.await(timeoutMs, TimeUnit.MILLISECONDS));
    }

    public void INTEGRATION_PublishAndDiscover() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        if (!checkDiscoverySystemSupportOnCurrentOs(implName)) {
            System.out.println("Publish And Discovery TEST (" + implName + ") - NOT SUPPORTED on current OS (" + currentOs() + ")");
            return;
        }

        String srvName = String.format(DISC_SRV_NAME, implName, "Pub&Disc");

        System.out.println("Create and start publisher (" + implName + ")");
        pub = DiscoverySystemFactory.createPublisher(implName, DISC_SRV_TYPE, srvName, DISC_SRV_PORT);
        pub.publish(false);

        System.out.println("Create and start discover (" + implName + ")");
        disc = DiscoverySystemFactory.createDiscover(implName, DISC_SRV_TYPE);
        disc.addListener(discoveryServiceListener);
        disc.start();

        System.out.println("Wait for discover, discovering service");
        Assertions.assertTrue(discoveryServiceListener.onServiceDiscovered.await(timeoutMs, TimeUnit.MILLISECONDS));

        System.out.println("Stop publisher (" + implName + ")");
        pub.hide(false);

        System.out.println("Wait for discover, losing service");
        Assertions.assertTrue(discoveryServiceListener.onServiceLost.await(timeoutMs, TimeUnit.MILLISECONDS));

        System.out.println("Stop discover (" + implName + ")");
        disc.stop();
    }

    public void INTEGRATION_DiscoverAndPublish() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        if (!checkDiscoverySystemSupportOnCurrentOs(implName)) {
            System.out.println("Publish And Discovery TEST (" + implName + ") - NOT SUPPORTED on current OS (" + currentOs() + ")");
            return;
        }

        String srvName = String.format(DISC_SRV_NAME, implName, "Disc&Pub");

        System.out.println("Create and start discover (" + implName + ")");
        disc = DiscoverySystemFactory.createDiscover(implName, DISC_SRV_TYPE);
        disc.addListener(discoveryServiceListener);
        disc.start();

        System.out.println("Create and start publisher (" + implName + ")");
        pub = DiscoverySystemFactory.createPublisher(implName, DISC_SRV_TYPE, srvName, DISC_SRV_PORT);
        pub.publish(false);

        System.out.println("Wait for discover, discovering service");
        Assertions.assertTrue(discoveryServiceListener.onServiceDiscovered.await(timeoutMs, TimeUnit.MILLISECONDS));

        System.out.println("Stop discover (" + implName + ")");
        disc.stop();

        System.out.println("Wait for discover, losing service");
        Assertions.assertTrue(discoveryServiceListener.onServiceLost.await(timeoutMs, TimeUnit.MILLISECONDS));

        System.out.println("Stop publisher (" + implName + ")");
        pub.hide(false);
    }


    // OS Support checks methods

    protected static boolean checkDiscoverySystemSupportOnCurrentOs(String implementation) {
        if (JmDNS.IMPL_NAME.equalsIgnoreCase(implementation)) {
            return true;

        } else if (JmmDNS.IMPL_NAME.equalsIgnoreCase(implementation)) {
            return true;

        } else if (Avahi.IMPL_NAME.equalsIgnoreCase(implementation)) {
            return isLinux();

        } else if (DNSSD.IMPL_NAME.equalsIgnoreCase(implementation)) {
            return isMac();

        }

        return false;
    }

    private static String currentOs() {
        return System.getProperty("os.name");
    }

    private static boolean isLinux() {
        String os = currentOs().toUpperCase(Locale.ENGLISH);
        return (os.contains("NIX") || os.contains("NUX") || os.contains("AIX"));
    }

    private static boolean isMac() {
        String os = currentOs().toUpperCase(Locale.ENGLISH);
        return (os.toUpperCase(Locale.ENGLISH)).contains("MAC");
    }

    private static boolean isWin() {
        String os = currentOs().toUpperCase(Locale.ENGLISH);
        return (os.contains("WIN"));
    }

    private static boolean isSolaris() {
        String os = currentOs().toUpperCase(Locale.ENGLISH);
        return (os.contains("SUNOS"));
    }

}