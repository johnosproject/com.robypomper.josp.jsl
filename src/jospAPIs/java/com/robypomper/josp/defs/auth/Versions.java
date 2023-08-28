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

package com.robypomper.josp.defs.auth;

import com.robypomper.josp.consts.APIVersionsAbs;


/**
 * JOSP Auth
 * <p>
 * API's versions definition for 1st level API.
 */
public class Versions extends APIVersionsAbs {

    // Class Constants

    // API Group
    public static final String API_NAME = "JOSP Auth";
    public static final String API_SERVICE = "JCP APIs";
    // API SubGroups
    public static final String API_GROUP = "Main";
    public static final String API_GROUP_FULL = "";
    public static final String API_GROUP_DESCR = "";
    // Urls
    public static final String API_PATH_BASE = "/auth";//"/apis/josp/auth";
    public static final String AUTH_PATH_BASE = "/auth";
    // Versions
    public static final String VER_JCP_APIs_2_0 = "2.0";

}