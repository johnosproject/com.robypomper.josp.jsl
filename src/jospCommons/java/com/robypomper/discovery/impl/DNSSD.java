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

import java.io.IOException;

/**
 * Utils class for DNS-SD discovery system.
 */
public class DNSSD {

    // Class constants

    public static final String IMPL_NAME = "DNSSD";
    public static final String TH_DISCOVER_NAME = "DISCOVER_" + IMPL_NAME;
    public static final String TH_RESOLVER_NAME = "RESOLVER_" + IMPL_NAME;


    // isAvailable static check

    public static boolean isAvailable() {
        String[] cmdArray = new String[]{
                "dns-sd",
                "-V",
        };

        try {
            Process p = Runtime.getRuntime().exec(cmdArray);
            p.waitFor();
            return !p.isAlive();

        } catch (IOException | IllegalThreadStateException | InterruptedException e) {
            return false;
        }
    }

}
