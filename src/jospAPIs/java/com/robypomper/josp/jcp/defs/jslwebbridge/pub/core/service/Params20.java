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

package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.service;


/**
 * JCP JSL Web Bridge - Service 2.0
 */
public class Params20 {

    public static class JOSPSrvHtml {

        public String name;
        public String state;
        public String stateJCP;
        public boolean isJCPConnected;
        public String stateCloud;
        public boolean isCloudConnected;
        public String stateLocal;
        public boolean isLocalRunning;
        public String srvId;
        public String usrId;
        public String instId;
        public String jslVersion;
        public String[] supportedJCPAPIsVersions;
        public String[] supportedJOSPProtocolVersions;
        public String[] supportedJODVersions;
        public String sessionId;

    }

}
