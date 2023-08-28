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

package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.structure;


/**
 * JCP JSL Web Bridge - Objects / Structure 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to query objects structure and components";


    // API Params

    //@formatter:off
    public static final String PARAM_OBJ        = "obj_id";
    public static final String PARAM_COMP        = "comp_path";

    public static final String PARAM_URL_OBJ        = "{" + PARAM_OBJ + "}";
    public static final String PARAM_URL_COMP        = "{" + PARAM_COMP + "}";
    //@formatter:on


    // API Methods

    //@formatter:off
    //
    private static final String MTHD_STRUCT    = PARAM_URL_OBJ;
    private static final String MTHD_COMP      = PARAM_URL_OBJ + "/" + PARAM_URL_COMP;
    //@formatter:on


    // API Paths

    //@formatter:off
    //
    public static final String FULL_PATH_STRUCT     = API_PATH + "/" + API_VER + "/" + MTHD_STRUCT;
    public static final String FULL_PATH_COMP       = API_PATH + "/" + API_VER + "/" + MTHD_COMP;
    //@formatter:on

    // API Paths composers

    //@formatter:off
    //
    public static String FULL_PATH_STRUCT   (String objId)                  { return FULL_PATH_STRUCT   .replace(PARAM_URL_OBJ,objId); }
    public static String FULL_PATH_COMP     (String objId, String compPath) { return FULL_PATH_COMP     .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    //@formatter:on


    // API Descriptions

    //@formatter:off
    //
    public static final String DESCR_PATH_STRUCT    = "Return the object's full structure";
    public static final String DESCR_PATH_COMP      = "Return the object's component";
    //@formatter:on

}
