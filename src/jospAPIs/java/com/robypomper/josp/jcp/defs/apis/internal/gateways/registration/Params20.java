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

package com.robypomper.josp.jcp.defs.apis.internal.gateways.registration;

import com.robypomper.josp.types.josp.gw.GWType;

import java.util.Date;


/**
 * JCP APIs - Gateways / Registration 2.0
 */
public class Params20 {

    // Registration

    public static class JCPGWsStartup {

        public GWType type;
        public String gwAddr;
        public int gwPort;
        public String gwAPIsAddr;
        public int gwAPIsPort;
        public int clientsMax;
        public String version;

    }

    public static class JCPGWsStatus {

        public int clients;
        public int clientsMax;
        public Date lastClientConnectedAt;
        public Date lastClientDisconnectedAt;

    }

}
