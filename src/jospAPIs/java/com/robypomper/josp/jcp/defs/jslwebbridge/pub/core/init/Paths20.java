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

package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.init;


/**
 * JCP JSL Web Bridge - Init 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Initializers for JSL instances in JCP JSL WebBridge";


    // API Methods

    //@formatter:off
    //
    private static final String MTHD_INIT_STATUS    = "status";
    private static final String MTHD_INIT_JSL       = "jsl";
    private static final String MTHD_INIT_SSE       = "sse";
    //@formatter:on


    // API Paths

    //@formatter:off
    //
    public static final String FULL_PATH_INIT_STATUS = API_PATH + "/" + API_VER + "/" + MTHD_INIT_STATUS;
    public static final String FULL_PATH_INIT_JSL   = API_PATH + "/" + API_VER + "/" + MTHD_INIT_JSL;
    public static final String FULL_PATH_INIT_SSE   = API_PATH + "/" + API_VER + "/" + MTHD_INIT_SSE;
    //@formatter:on


    // API Descriptions

    //@formatter:off
    //
    public static final String DESCR_PATH_INIT_STATUS   = "Return the session id and JSL instance status";
    public static final String DESCR_PATH_INIT_JSL      = "Initialize new JSL Instance for current session";
    public static final String DESCR_PATH_INIT_SSE      = "Create and return new SSE, if given client params then it also initialize new JSL Instance for current session";
    //@formatter:on

}
