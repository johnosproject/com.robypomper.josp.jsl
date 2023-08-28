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

package com.robypomper.josp.defs.auth.keycloak;


/**
 * Auth API Paths class definitions.
 * <p>
 * APIs group is used for the access to the auth server APIs, like that one to
 * retrieve user profile.
 * <p>
 * The Auth Service path is used by JOSP components to send auth requests.
 */
public class Paths20 {

    // Class constants

    public static final String REALM="jcp";


    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Forward requests to Keycloak's API as JCP Auth service";


    // API Methods

    //@formatter:off
    // Keycloak API
    private static final String MTHD_USER       = "admin/realms/" + REALM + "/users";
    // Auth service
    public static final String MTHD_AUTH        = "realms/" + REALM + "/protocol/openid-connect/auth";
    public static final String MTHD_TOKEN       = "realms/" + REALM + "/protocol/openid-connect/token";
    //@formatter:on


    // API Paths

    //@formatter:off
    // Keycloak API
    public static final String FULL_PATH_USER       = API_PATH + "/" + MTHD_USER;       //API_PATH + "/" + API_VER + "/" + MTHD_USER;
    // Auth service
    public static final String FULL_PATH_AUTH       = API_PATH + "/" + MTHD_AUTH;       //API_PATH + "/" + API_VER + "/" + MTHD_AUTH;
    public static final String FULL_PATH_TOKEN      = API_PATH + "/" + MTHD_TOKEN;      //API_PATH + "/" + API_VER + "/" + MTHD_TOKEN;
    //@formatter:on


    // API Descriptions

    //@formatter:off
    // Keycloak API
    public static final String DESCR_PATH_USER                = "...";
    // Auth service
    public static final String DESCR_PATH_AUTH                = "...";
    public static final String DESCR_PATH_TOKEN               = "...";
    //@formatter:on

}
