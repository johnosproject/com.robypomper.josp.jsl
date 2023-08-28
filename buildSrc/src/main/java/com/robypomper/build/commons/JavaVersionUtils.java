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

package com.robypomper.build.commons;

import org.gradle.api.JavaVersion;

public class JavaVersionUtils {

    public final static JavaVersion CURRENT_JAVA_VERSION = JavaVersion.current();

    public static boolean currentGreaterEqualsThan9() {
        String currVer = System.getProperty("java.version");
        return currVer.startsWith("9") || currVer.startsWith("1.9")
                || currVer.startsWith("10")
                || currVer.startsWith("11")
                || currVer.startsWith("12")
                || currVer.startsWith("13")
                || currVer.startsWith("14");
    }

}
