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

package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects;


/**
 * JCP JSL Web Bridge - Objects 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to query objects info";


    // API Params

    //@formatter:off
    public static final String PARAM_OBJ        = "obj_id";

    public static final String PARAM_URL_OBJ        = "{" + PARAM_OBJ + "}";
    //@formatter:on


    // API Methods

    //@formatter:off
    //
    private static final String MTHD_LIST       = "";
    private static final String MTHD_DETAILS    = PARAM_URL_OBJ;
    private static final String MTHD_OWNER      = PARAM_URL_OBJ + "/owner/";
    private static final String MTHD_NAME       = PARAM_URL_OBJ + "/name/";
    private static final String MTHD_EVENTS     = PARAM_URL_OBJ + "/events/";
    //@formatter:on


    // API Paths

    //@formatter:off
    //
    public static final String FULL_PATH_LIST       = API_PATH + "/" + API_VER + "/" + MTHD_LIST;
    public static final String FULL_PATH_DETAILS    = API_PATH + "/" + API_VER + "/" + MTHD_DETAILS;
    public static final String FULL_PATH_OWNER      = API_PATH + "/" + API_VER + "/" + MTHD_OWNER;
    public static final String FULL_PATH_NAME       = API_PATH + "/" + API_VER + "/" + MTHD_NAME;
    public static final String FULL_PATH_EVENTS     = API_PATH + "/" + API_VER + "/" + MTHD_EVENTS;
    //@formatter:on

    // API Paths composers

    //@formatter:off
    //
    public static String FULL_PATH_DETAILS  (String objId){ return FULL_PATH_DETAILS    .replace(PARAM_URL_OBJ,objId); }
    public static String FULL_PATH_OWNER    (String objId){ return FULL_PATH_OWNER      .replace(PARAM_URL_OBJ,objId); }
    public static String FULL_PATH_NAME     (String objId){ return FULL_PATH_NAME       .replace(PARAM_URL_OBJ,objId); }
    public static String FULL_PATH_EVENTS   (String objId){ return FULL_PATH_EVENTS       .replace(PARAM_URL_OBJ,objId); }
    //@formatter:on


    // API Descriptions

    //@formatter:off
    //
    public static final String DESCR_PATH_LIST      = "Return the list of available objects";
    public static final String DESCR_PATH_DETAILS   = "Return object's details";
    public static final String DESCR_PATH_OWNER     = "Set object's owner";
    public static final String DESCR_PATH_NAME      = "Set object's name";
    public static final String DESCR_PATH_EVENTS    = "Return object's events list";
    //@formatter:on

//@formatter:on
}
