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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Base class for {@link Publisher} implementations.
 * <p>
 * This class manage type, name and port of the service to publish for all
 * {@link Publisher} subclasses.
 */
public abstract class PublisherAbs extends DiscoveryBase<Publisher, PublisherStateListener> implements Publisher {

    // Class constants

    public static final int WAIT_MAX_COUNT = 100;
    public static final int WAIT_LOOP_TIME = 100; //ms


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(PublisherAbs.class);
    // service info
    private final String srvName;
    private final int srvPort;
    private final String srvText;
    // publishing services
    private CountDownLatch onPublished;
    private CountDownLatch onDepublished;
    // discover published services
    protected Discover discover;


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType   the service type to publish.
     * @param srvName   the service name to publish.
     * @param srvPort   the service port to publish.
     * @param extraText string containing extra text related to service to publish.
     */
    protected PublisherAbs(String srvType, String srvName, int srvPort, String extraText) {
        super(srvType);
        this.srvName = srvName;
        this.srvPort = srvPort;
        this.srvText = extraText;
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceName() {
        return srvName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServicePort() {
        return srvPort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceExtraText() {
        return srvText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Discover getInternalDiscover() {
        return discover;
    }


    // Publication mngm

    /**
     * {@inheritDoc}
     * <p>
     * Basic implementation that use only the <code>boolean isPublished</code> field set on first service (self) resolving
     * and on first service (self) lost.
     */
    @Override
    public boolean isPublishedFully() {
        // published on ALL jmdns
        return isPublishedPartially() && getServicesDiscovered().size() == getInterfaces().size();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Basic implementation that use only the <code>boolean isPublished</code> field set on first service (self) resolving
     * and on first service (self) lost.
     */
    @Override
    public boolean isPublishedPartially() {
        // published at last ONE self-publication (discovered by internal discovery)
        return discover != null && getServicesDiscovered().size() > 0;
    }


    // Internal discovery mngm (SubClass Support Methods)

    /**
     * Start internal auto discovery service used to detect if current service
     * is published or hided successfully.
     *
     * @param implementation the string identify the implementation required.
     */
    protected void startAutoDiscovery(String implementation) throws Discover.DiscoveryException {
        onPublished = new CountDownLatch(1);
        onDepublished = new CountDownLatch(1);
        discover = DiscoverySystemFactory.createDiscover(implementation, getServiceType());
        discover.addListener(selfDiscoveryListener);
        //noinspection rawtypes
        ((DiscoveryBase) discover).disableLogs = true;
        discover.start();
    }

    /**
     * Stop internal auto discovery system.
     */
    protected void stopAutoDiscovery() {
        if (discover != null) {
            discover.stop();
            discover.removeListener(selfDiscoveryListener);
        }
        discover = null;
    }

    private final DiscoveryServicesListener selfDiscoveryListener = new DiscoveryServicesListener() {

        @Override
        public void onServiceDiscovered(DiscoveryService discSrv) {
            registerInterface(discSrv.intf);

            if (!discSrv.name.equalsIgnoreCase(getServiceName()))
                return;

            onPublished.countDown();
            registerService(log, discSrv);
        }

        @Override
        public void onServiceLost(DiscoveryService lostService) {
            if (!lostService.name.equalsIgnoreCase(getServiceName()))
                return;

            onDepublished.countDown();
            deregisterService(log, lostService);
        }

    };


    // Wait methods (SubClass Support Methods)

    /**
     * Method to wait for current service publication via internal auto discovery
     * system.
     * <p>
     * This method wait at least for {@value #WAIT_LOOP_TIME} x {@value #WAIT_MAX_COUNT}
     * milliseconds, then throw an exception.
     */
    protected void waitServicePublication() {
        try {
            onPublished.await(PROCESS_WAITING_PUB_DEPUB_MS, TimeUnit.MILLISECONDS);

        } catch (InterruptedException ignore) {
        }
    }

    /**
     * Method to wait for current service de-publication via internal auto discovery
     * system.
     * <p>
     * This method wait at least for {@value #WAIT_LOOP_TIME} x {@value #WAIT_MAX_COUNT}
     * milliseconds, then throw an exception.
     */
    protected void waitServiceDepublication() {
        try {
            onDepublished.await(PROCESS_WAITING_PUB_DEPUB_MS, TimeUnit.MILLISECONDS);

        } catch (InterruptedException ignore) {
        }
    }


    // DNS-SD names de/encoding (Static Support Methods)

    public static String getServiceNameEncoded_DNSSD(String decoded) {
        return decoded
                .replaceAll(" ", "\\\\032");
    }

    public static String getServiceNameDecoded_DNSSD(String encoded) {
        return encoded
                .replaceAll("\\\\032", " ");
    }

    public static String getServiceNameEncoded_Avahi(String decoded) {
        return decoded
                .replaceAll(" ", "\\\\032")
                .replaceAll("&", "\\\\038")
                .replaceAll("\\(", "\\\\040")
                .replaceAll("\\)", "\\\\041");
    }

    public static String getServiceNameDecoded_Avahi(String encoded) {
        return encoded
                .replaceAll("\\\\032", " ")
                .replaceAll("\\\\038", "&")
                .replaceAll("\\\\040", "(")
                .replaceAll("\\\\041", ")");
    }

}
