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
 * Implementations of the discovery system based on Avahi.
 */
public class Avahi {

    // Class constants

    public static final String IMPL_NAME = "Avahi";
    public static final String TH_DISCOVER_NAME = "DISCOVER_" + IMPL_NAME;


    // isAvailable static check

    public static boolean isAvailable() {
        String[] cmdArray = new String[]{
                "avahi-publish",
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
