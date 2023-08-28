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

import java.util.List;


/**
 * Interface for Discover implementations.
 * <p>
 * Each DiscoverAbs instance is looking for a specific type of services. The
 * Discover's type can be queried with the method {@link #getServiceType()}
 */
public interface Discover {

    // Getters

    /**
     * @return current Discover service state.
     */
    DiscoveryState getState();

    /**
     * Return the service type looked from current discover object.
     */
    String getServiceType();

    /**
     * Return all services discovered.
     */
    List<DiscoveryService> getServicesDiscovered();

    /**
     * Return all available interfaces.
     */
    List<String> getInterfaces();


    // Discovery mngm

    /**
     * Start the discovery system.
     * <p>
     * After calling this method, the discovery system start to emit
     * {@link DiscoveryServicesListener} events on service discovered/lost.
     */
    void start() throws DiscoveryException;

    /**
     * Start the discovery system.
     * <p>
     * After calling this method, the discovery system start to emit
     * {@link DiscoveryServicesListener} events on service discovered/lost.
     */
    void stop();


    // Listener mngm

    /**
     * Add given listener to discover state listener list.
     *
     * @param listener the listener to add.
     */
    void addListener(DiscoverStateListener listener);

    /**
     * Remove given listener to discover state listener list.
     *
     * @param listener the listener to remove.
     */
    void removeListener(DiscoverStateListener listener);

    /**
     * Add given listener to discovery system listener list.
     * <p>
     * This listeners observe all services (de)publications.
     *
     * @param listener the listener to add.
     */
    void addListener(DiscoveryServicesListener listener);

    /**
     * Remove given listener to discovery system listener list.
     * <p>
     * This listeners observe all services (de)publications.
     *
     * @param listener the listener to remove.
     */
    void removeListener(DiscoveryServicesListener listener);


    // Exceptions

    /**
     * Exceptions thrown on discovery errors.
     */
    class DiscoveryException extends Throwable {
        public DiscoveryException(String msg) {
            super(msg);
        }

        public DiscoveryException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
