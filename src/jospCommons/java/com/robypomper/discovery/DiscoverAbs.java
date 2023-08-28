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


/**
 * Base class for {@link Discover} implementations.
 * <p>
 * This class manage discover service type and the
 * {@link DiscoveryServicesListener} for all
 * {@link Discover} subclasses.
 */
public abstract class DiscoverAbs extends DiscoveryBase<Discover, DiscoverStateListener> implements Discover {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(DiscoverAbs.class);


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType the service type to looking for.
     */
    protected DiscoverAbs(String srvType) {
        super(srvType);
    }


    // Service registration methods

    protected void registerService(DiscoveryService discSrv) {
        registerInterface(discSrv.intf);
        super.registerService(log, discSrv);
    }

    protected void deregisterService(DiscoveryService lostSrv) {
        super.deregisterService(log, lostSrv);
    }

    protected void deregisterAllServices() {
        super.deregisterAllServices(log);
    }

}
