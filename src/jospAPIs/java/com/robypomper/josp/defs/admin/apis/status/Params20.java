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

package com.robypomper.josp.defs.admin.apis.status;


/**
 * JOSP Admin - APIs / Status 2.0
 */
public class Params20 extends com.robypomper.josp.jcp.defs.apis.internal.status.Params20 {

    // Index

    public static class Index {

        public final String urlObjects = Paths20.FULL_PATH_JCP_APIS_STATUS_OBJS;
        public final String urlServices = Paths20.FULL_PATH_JCP_APIS_STATUS_SRVS;
        public final String urlUsers = Paths20.FULL_PATH_JCP_APIS_STATUS_USRS;
        public final String urlGateways = Paths20.FULL_PATH_JCP_APIS_STATUS_GWS;

    }

}
