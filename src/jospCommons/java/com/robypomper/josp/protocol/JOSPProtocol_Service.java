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

package com.robypomper.josp.protocol;

public class JOSPProtocol_Service {

    // Full Srv Id

    public static final String FULL_SRV_ID_FORMAT = "%s/%s/%s";

    public static String fullSrvIdToSrvId(String fullSrvId) {
        return fullSrvId.split("/")[0];
    }

    public static String fullSrvIdToUsrId(String fullSrvId) {
        return fullSrvId.split("/")[1];
    }

    public static String fullSrvIdToInstId(String fullSrvId) {
        return fullSrvId.split("/")[2];
    }

}
