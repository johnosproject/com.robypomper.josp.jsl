/*******************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
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

package com.robypomper.josp.jod.objinfo;

import com.robypomper.java.JavaRandomStrings;

import java.util.Random;

/**
 * Support class for Object Info generation.
 */
public class LocalObjectInfo {

    // Class constants

    private static final int LENGTH = 5;        // 26^5 = 11.881.376 combinations


    // Generator methods

    /**
     * Generate random (and hopefully unique) object's Hardware ID.
     *
     * @return a 5-chars random string.
     */
    public static String generateObjIdHw() {
        return JavaRandomStrings.randomAlfaString(LENGTH);
    }

    /**
     * Generate readable, random (and hopefully unique) object's name.
     *
     * @return a {fruit}_{2Digit} random string.
     */
    public static String generateObjName() {
        return String.format("%s_%02d", JavaRandomStrings.randomFruitsString(), new Random().nextInt(100));
    }

}
