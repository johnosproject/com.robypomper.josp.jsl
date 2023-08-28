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

public class Mrk_JSL extends Markers {

    //@formatter:off

    // John Service Library

    protected static final Marker JSL               = MarkerManager.getMarker("JOD");
    public static final Marker JSL_MAIN             = MarkerManager.getMarker("JSL_MAIN").setParents(JSL,MAIN_COMP);
    // JSL - Info
    public static final Marker JSL_INFO             = MarkerManager.getMarker("JSL_INFO").setParents(JSL,MAIN_COMP);
    // JSL - Objects
    public static final Marker JSL_OBJS             = MarkerManager.getMarker("JSL_OBJS").setParents(JSL,MAIN_COMP);
    public static final Marker JSL_OBJS_SUB         = MarkerManager.getMarker("JSL_OBJS_SUB").setParents(JSL,SUB_COMP);
    // JSL - User
    public static final Marker JSL_USR              = MarkerManager.getMarker("JSL_USR").setParents(JSL,MAIN_COMP);
    // JSL - Comm
    public static final Marker JSL_COMM             = MarkerManager.getMarker("JSL_COMM").setParents(JSL,MAIN_COMP);
    public static final Marker JSL_COMM_SUB         = MarkerManager.getMarker("JSL_COMM_SUB").setParents(JSL,SUB_COMP);
    public static final Marker JSL_COMM_JCPCL       = MarkerManager.getMarker("JSL_COMM_JCPCL").setParents(JSL, Mrk_Commons.COMM_JCPCL);
    // JOD - Shell
    public static final Marker JSL_SHELL            = MarkerManager.getMarker("JSL_SHELL").setParents(JSL,MAIN_COMP);
    public static final Marker JSL_SHELL_SUB        = MarkerManager.getMarker("JSL_SHELL_SUB").setParents(JSL,SUB_COMP);

    //@formatter: on
}
