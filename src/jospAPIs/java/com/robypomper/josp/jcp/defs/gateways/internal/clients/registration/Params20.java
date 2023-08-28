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

package com.robypomper.josp.jcp.defs.gateways.internal.clients.registration;


/**
 * JCP Gateways - Clients / Registration 2.0
 */
public class Params20 {

    public static class AccessRequest {
        public String instanceId;
        public byte[] clientCertificate;
    }

    public static class O2SAccessRequest extends AccessRequest {
    }

    public static class S2OAccessRequest extends AccessRequest {
    }

    public static class AccessInfo {
        public String gwAddress;
        public int gwPort;
        public byte[] gwCertificate;
    }

    public static class O2SAccessInfo extends AccessInfo {
    }

    public static class S2OAccessInfo extends AccessInfo {
    }

}
