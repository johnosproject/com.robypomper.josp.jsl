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

import com.robypomper.discovery.impl.*;


/**
 * Provide methods to create {@link Publisher} and {@link Discover} instances.
 * <p>
 * Available implementations list:
 * <ul>
 *     <li>{@value Avahi#IMPL_NAME}: use the Avahi cmd line program</li>
 *     <li>{@value JmDNS#IMPL_NAME}: use the a JmDNS instances for each network interface</li>
 *     <li>{@value JmmDNS#IMPL_NAME}: use the JmmDNS instance for all network interfaces</li>
 * </ul>
 */
public class DiscoverySystemFactory {

    // Class constants

    public static final String IMPL_NAME_AUTO = "Auto";


    /**
     * Create and return a {@link Publisher} instance of class depending on
     * given <code>implementation</code> param.
     *
     * @param implementation the string identify the implementation required.
     * @param srvType        string containing the service type ("_http._tcp").
     * @param srvName        string containing the service name.
     * @param srvPort        string containing the service port.
     * @return instance of {@link Publisher}.
     */
    public static Publisher createPublisher(String implementation, String srvType, String srvName, int srvPort) throws Publisher.PublishException {
        return createPublisher(implementation, srvType, srvName, srvPort, "");
    }

    /**
     * Create and return a {@link Publisher} instance of class depending on
     * given <code>implementation</code> param.
     *
     * @param implementation the string identify the implementation required.
     * @param srvType        string containing the service type ("_http._tcp").
     * @param srvName        string containing the service name.
     * @param srvPort        string containing the service port.
     * @param extraText      string containing extra text related to service to publish.
     * @return instance of {@link Publisher}.
     */
    public static Publisher createPublisher(String implementation, String srvType, String srvName, int srvPort, String extraText) throws Publisher.PublishException {
        if (IMPL_NAME_AUTO.equalsIgnoreCase(implementation))
            implementation = detectAutoImplementation();

        if (Avahi.IMPL_NAME.equalsIgnoreCase(implementation))
            return new PublisherAvahi(srvType, srvName, srvPort, extraText);
        if (JmDNS.IMPL_NAME.equalsIgnoreCase(implementation))
            return new PublisherJmDNS(srvType, srvName, srvPort, extraText);
        if (JmmDNS.IMPL_NAME.equalsIgnoreCase(implementation))
            return new PublisherJmmDNS(srvType, srvName, srvPort, extraText);
        if (DNSSD.IMPL_NAME.equalsIgnoreCase(implementation))
            return new PublisherDNSSD(srvType, srvName, srvPort, extraText);

        throw new Publisher.PublishException(String.format("ERR: can't find '%s' PublisherAbs implementation", implementation));
    }

    /**
     * Create and return a {@link Discover} instance of class depending on
     * given <code>implementation</code> param.
     *
     * @param implementation the string identify the implementation required.
     * @param srvType        string containing the service type ("_http._tcp") to listen.
     * @return instance of {@link Discover}.
     */
    public static Discover createDiscover(String implementation, String srvType) throws Discover.DiscoveryException {
        if (IMPL_NAME_AUTO.equalsIgnoreCase(implementation))
            implementation = detectAutoImplementation();

        if (Avahi.IMPL_NAME.equalsIgnoreCase(implementation))
            return new DiscoverAvahi(srvType);
        if (JmDNS.IMPL_NAME.equalsIgnoreCase(implementation))
            return new DiscoverJmDNS(srvType);
        if (JmmDNS.IMPL_NAME.equalsIgnoreCase(implementation))
            return new DiscoverJmmDNS(srvType);
        if (DNSSD.IMPL_NAME.equalsIgnoreCase(implementation))
            return new DiscoverDNSSD(srvType);

        throw new Discover.DiscoveryException(String.format("ERR: can't find '%s' DiscoverAbs implementation", implementation));
    }

    private static String detectAutoImplementation() {
        if (Avahi.isAvailable())
            return Avahi.IMPL_NAME;
        if (DNSSD.isAvailable())
            return DNSSD.IMPL_NAME;
        return JmDNS.IMPL_NAME;
    }

}
