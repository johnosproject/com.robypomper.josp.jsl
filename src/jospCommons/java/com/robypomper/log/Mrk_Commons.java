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

public class Mrk_Commons extends Markers {

    //@formatter:off

    // Commons-Various

    public static final Marker STATE         = MarkerManager.getMarker("STATE");

    // Commons-Components

    protected static final Marker COMM          = MarkerManager.getMarker("COMM");                                                  // all communication component logs
    // Comm - Server
    public static final Marker COMM_SRV         = MarkerManager.getMarker("COMM_SRV").setParents(COMM,EXT_COMP);                    // all server component logs
    public static final Marker COMM_SRV_IMPL    = MarkerManager.getMarker("COMM_SRV_IMPL").setParents(COMM,EXT_COMP);               // all server implementations logs
    // Comm - Client
    public static final Marker COMM_CL          = MarkerManager.getMarker("COMM_CL").setParents(COMM,EXT_COMP);                     // all client component logs
    public static final Marker COMM_CL_IMPL     = MarkerManager.getMarker("COMM_CL_IMPL").setParents(COMM,EXT_COMP);                // all client implementations logs
    // SSL Utils, Server and Client
    public static final Marker COMM_SSL_UTILS   = MarkerManager.getMarker("COMM_SSL_UTILS").setParents(COMM,EXT_COMP);              // all ssl utils logs
    public static final Marker COMM_SSL_CERTSRV = MarkerManager.getMarker("COMM_SSL_CERTSRV").setParents(COMM_SRV_IMPL,EXT_COMP);   // all ssl certificate server logs
    public static final Marker COMM_SSL_SRV     = MarkerManager.getMarker("COMM_SSL_SRV").setParents(COMM,EXT_COMP);                // all ssl server component logs
    public static final Marker COMM_SSL_CERTCL  = MarkerManager.getMarker("COMM_SSL_CERTCL").setParents(COMM_CL_IMPL,EXT_COMP);     // all ssl certificate client logs
    public static final Marker COMM_SSL_CL      = MarkerManager.getMarker("COMM_SSL_CL").setParents(COMM,EXT_COMP);                 // all ssl client component logs
    // JCPClient
    public static final Marker COMM_JCPCL       = MarkerManager.getMarker("COMM_JCPCL").setParents(COMM);                           // all JOSP Client Platform's clients logs


    // Commons-Components: Discovery

    protected static final Marker DISC          = MarkerManager.getMarker("DISC").setParents(EXT_COMP);                 // all communication component logs
    public static final Marker DISC_IMPL        = MarkerManager.getMarker("DISC_IMPL").setParents(DISC,IMPL_COMP);      // all discovery system implementations logs
    // Disc - Publisher
    public static final Marker DISC_PUB         = MarkerManager.getMarker("DISC_PUB").setParents(DISC);                 // all publisher logs
    public static final Marker DISC_PUB_IMPL    = MarkerManager.getMarker("DISC_PUB_IMPL").setParents(DISC,IMPL_COMP);  // all publisher system implementations logs
    // Disc - Discoverer
    public static final Marker DISC_DISC        = MarkerManager.getMarker("DISC_DISC").setParents(DISC);                // all discoverer logs
    public static final Marker DISC_DISC_IMPL   = MarkerManager.getMarker("DISC_DISC_IMPL").setParents(DISC,IMPL_COMP); // all discoverer system implementations logs

    //@formatter: on
}
