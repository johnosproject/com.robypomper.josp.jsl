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

package com.robypomper.log;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Mrk_Test extends Markers {

    //@formatter:off

    // Tests

    public static final Marker TEST             = MarkerManager.getMarker("TEST");          // all test logs
    public static final Marker TEST_METHODS     = MarkerManager.getMarker("TEST_METHODS").setParents(TEST,METHODS);
    public static final Marker TEST_SPACER      = MarkerManager.getMarker("TEST_SPACER").setParents(TEST,SPACER);

    //@formatter: on
}
