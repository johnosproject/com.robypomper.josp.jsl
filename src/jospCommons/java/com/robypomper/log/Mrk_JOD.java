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

public class Mrk_JOD extends Markers {

    //@formatter:off

    // John Object Daemon

    protected static final Marker JOD               = MarkerManager.getMarker("JOD");
    public static final Marker JOD_MAIN             = MarkerManager.getMarker("JOD_MAIN").setParents(JOD,MAIN_COMP);
    // JOD - Events
    public static final Marker JOD_EVENTS           = MarkerManager.getMarker("JOD_EVENTS").setParents(JOD,MAIN_COMP);
    // JOD - Info
    public static final Marker JOD_INFO             = MarkerManager.getMarker("JOD_INFO").setParents(JOD,MAIN_COMP);
    // JOD - Executor
    public static final Marker JOD_EXEC             = MarkerManager.getMarker("JOD_EXEC").setParents(JOD,MAIN_COMP);
    public static final Marker JOD_EXEC_SUB         = MarkerManager.getMarker("JOD_EXEC_SUB").setParents(JOD,SUB_COMP);
    public static final Marker JOD_EXEC_IMPL        = MarkerManager.getMarker("JOD_EXEC_IMPL").setParents(JOD,IMPL_COMP);
    // JOD - Structure
    public static final Marker JOD_STRU             = MarkerManager.getMarker("JOD_STRU").setParents(JOD,MAIN_COMP);
    public static final Marker JOD_STRU_SUB         = MarkerManager.getMarker("JOD_STRU_SUB").setParents(JOD,SUB_COMP);
    // JOD - Permission
    public static final Marker JOD_PERM             = MarkerManager.getMarker("JOD_PERM").setParents(JOD,MAIN_COMP);
    // JOD - Comm
    public static final Marker JOD_COMM             = MarkerManager.getMarker("JOD_COMM").setParents(JOD,MAIN_COMP);
    public static final Marker JOD_COMM_SUB         = MarkerManager.getMarker("JOD_COMM_SUB").setParents(JOD,SUB_COMP);
    public static final Marker JOD_COMM_JCPCL       = MarkerManager.getMarker("JOD_COMM_JCPCL").setParents(JOD, Mrk_Commons.COMM_JCPCL);
    // JOD - History
    public static final Marker JOD_HISTORY          = MarkerManager.getMarker("JOD_HISTORY").setParents(JOD,MAIN_COMP);
    // JOD - Shell
    public static final Marker JOD_SHELL            = MarkerManager.getMarker("JOD_SHELL").setParents(JOD,MAIN_COMP);
    public static final Marker JOD_SHELL_SUB        = MarkerManager.getMarker("JOD_SHELL_SUB").setParents(JOD,SUB_COMP);

    //@formatter: on
}
