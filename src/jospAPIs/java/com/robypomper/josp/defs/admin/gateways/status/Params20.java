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

package com.robypomper.josp.defs.admin.gateways.status;


import com.robypomper.josp.types.RESTItemList;

import java.util.List;


/**
 * JOSP Admin - Gateways / Status 2.0
 */
public class Params20 extends com.robypomper.josp.jcp.defs.gateways.internal.status.Params20 {

    // List

    public static class GatewaysServers {

        public List<RESTItemList> serverList;

    }


    // Index

    public static class Index {

        public final String urlGateways;
        public final String urlBroker;

        public Index(String gwServerId) {
            this.urlGateways = Paths20.FULL_PATH_JCP_GWS_STATUS_GWS(gwServerId);
            this.urlBroker = Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER(gwServerId);
        }

    }

}
