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

package com.robypomper.josp.jcp.defs.jslwebbridge.internal.status;


/**
 * JCP JSL Web Bridge - Status 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Return JCP JSL WebBridge's instance status";


    // API Params

    //@formatter:off
    public static final String PARAM_SESSION        = "session_id";

    public static final String PARAM_URL_SESSION    = "{" + PARAM_SESSION + "}";
    //@formatter:on


    // API Methods

    //@formatter:off
    // Index methods
    public static final String MTHD_STATUS      = "";
    // Sessions methods
    public static final String MTHD_STATUS_SESSIONS     = "sessions";
    public static final String MTHD_STATUS_SESSION      = "sessions/" + PARAM_URL_SESSION;
    //@formatter:on


    // API Paths

    //@formatter:off
    // Index methods
    public static final String FULL_PATH_STATUS     = API_PATH + "/" + API_VER + "/" + MTHD_STATUS;
    // Sessions methods
    public static final String FULL_PATH_STATUS_SESSIONS        = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_SESSIONS;
    public static final String FULL_PATH_STATUS_SESSION         = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_SESSION;
    //@formatter:on


    // API Paths composers

    //@formatter:off
    // Sessions methods
    public static String FULL_PATH_STATUS_SESSION       (String sessionId){ return FULL_PATH_STATUS_SESSION     .replace(PARAM_URL_SESSION,sessionId); }
    //@formatter:on


    // API Descriptions

    //@formatter:off
    // Index methods
    public static final String DESCR_PATH_STATUS        = "Index of current API Group";
    // Sessions methods
    public static final String DESCR_PATH_STATUS_SESSIONS       = "Return current JCP JSL Web Bridge web sessions list";
    public static final String DESCR_PATH_STATUS_SESSION        = "Return required web session status";
    //@formatter:on

}
