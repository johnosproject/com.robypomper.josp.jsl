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

public class Markers {

    //@formatter:off

    // Component's types

    protected static final Marker MAIN_COMP     = MarkerManager.getMarker("MAIN_COMP");     // all main-components logs
    protected static final Marker EXT_COMP      = MarkerManager.getMarker("EXT_COMP");      // all external components logs
    protected static final Marker SUB_COMP      = MarkerManager.getMarker("SUB_COMP");      // all sub-components logs
    protected static final Marker IMPL_COMP     = MarkerManager.getMarker("IMPL_COMP");     // all components implementations logs


    // Log types

    public static final Marker METHODS      = MarkerManager.getMarker("METHODS");       // info about methods
    public static final Marker SPACER       = MarkerManager.getMarker("SPACER");        // log spacer and bars

    //@formatter: on
}
