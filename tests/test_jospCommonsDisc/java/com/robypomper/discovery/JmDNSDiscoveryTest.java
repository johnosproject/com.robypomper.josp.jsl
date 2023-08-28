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

import com.robypomper.discovery.impl.JmDNS;
import org.junit.jupiter.api.Test;

public class JmDNSDiscoveryTest extends DiscoveryTestGeneric {

    public JmDNSDiscoveryTest() {
        super(JmDNS.IMPL_NAME, 10 * 1000);
    }


    // Tests

    @Test
    public void METHOD_Discover_startAndStop() throws Discover.DiscoveryException, InterruptedException {
        super.METHOD_Discover_startAndStop();
    }

    @Test
    public void METHOD_Publisher_startAndStop() throws Publisher.PublishException, InterruptedException {
        super.METHOD_Publisher_startAndStop();
    }

    @Test
    public void INTEGRATION_PublishAndDiscover() throws Publisher.PublishException, Discover.DiscoveryException, InterruptedException {
        super.INTEGRATION_PublishAndDiscover();
    }

    @Test
    public void INTEGRATION_DiscoverAndPublish() throws Publisher.PublishException, Discover.DiscoveryException, InterruptedException {
        super.INTEGRATION_DiscoverAndPublish();
    }

}