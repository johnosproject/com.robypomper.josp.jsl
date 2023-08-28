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

package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions;


/**
 * JCP JSL Web Bridge - Objects / Actions 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to send objects actions";


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
    private static final String MTHD_BOOL_SWITCH    = "bool/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP + "/switch/";
    private static final String MTHD_BOOL_TRUE      = "bool/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP + "/true/";
    private static final String MTHD_BOOL_FALSE     = "bool/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP + "/false/";
    private static final String MTHD_RANGE_SET      = "range/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP + "/";
    private static final String MTHD_RANGE_SETg     = "range/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP + "/{val}";
    private static final String MTHD_RANGE_INC      = "range/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP + "/inc/";
    private static final String MTHD_RANGE_DEC      = "range/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP + "/dec/";
    private static final String MTHD_RANGE_MAX      = "range/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP + "/max/";
    private static final String MTHD_RANGE_MIN      = "range/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP + "/min/";
    private static final String MTHD_RANGE_1_2      = "range/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP + "/1_2/";
    private static final String MTHD_RANGE_1_3      = "range/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP + "/1_3/";
    private static final String MTHD_RANGE_2_3      = "range/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP + "/2_3/";
    //@formatter:on


    // API Paths

    //@formatter:off
    //
    public static final String FULL_PATH_BOOL_SWITCH    = API_PATH + "/" + API_VER + "/" + MTHD_BOOL_SWITCH;
    public static final String FULL_PATH_BOOL_TRUE      = API_PATH + "/" + API_VER + "/" + MTHD_BOOL_TRUE;
    public static final String FULL_PATH_BOOL_FALSE     = API_PATH + "/" + API_VER + "/" + MTHD_BOOL_FALSE;
    public static final String FULL_PATH_RANGE_SET      = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_SET;
    public static final String FULL_PATH_RANGE_SETg     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_SETg;
    public static final String FULL_PATH_RANGE_INC     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_INC;
    public static final String FULL_PATH_RANGE_DEC     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_DEC;
    public static final String FULL_PATH_RANGE_MAX     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_MAX;
    public static final String FULL_PATH_RANGE_MIN     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_MIN;
    public static final String FULL_PATH_RANGE_1_2     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_1_2;
    public static final String FULL_PATH_RANGE_1_3     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_1_3;
    public static final String FULL_PATH_RANGE_2_3     = API_PATH + "/" + API_VER + "/" + MTHD_RANGE_2_3;
    //@formatter:on


    // API Paths composers

    //@formatter:off
    //
    public static String FULL_PATH_BOOL_SWITCH  (String objId, String compPath){ return FULL_PATH_BOOL_SWITCH   .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    public static String FULL_PATH_BOOL_TRUE    (String objId, String compPath){ return FULL_PATH_BOOL_TRUE     .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    public static String FULL_PATH_BOOL_FALSE   (String objId, String compPath){ return FULL_PATH_BOOL_FALSE    .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    public static String FULL_PATH_RANGE_SET    (String objId, String compPath){ return FULL_PATH_RANGE_SET     .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    public static String FULL_PATH_RANGE_SETg   (String objId, String compPath){ return FULL_PATH_RANGE_SETg    .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    public static String FULL_PATH_RANGE_INC    (String objId, String compPath){ return FULL_PATH_RANGE_INC     .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    public static String FULL_PATH_RANGE_DEC    (String objId, String compPath){ return FULL_PATH_RANGE_DEC     .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    public static String FULL_PATH_RANGE_MAX    (String objId, String compPath){ return FULL_PATH_RANGE_MAX     .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    public static String FULL_PATH_RANGE_MIN    (String objId, String compPath){ return FULL_PATH_RANGE_MIN     .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    public static String FULL_PATH_RANGE_1_2    (String objId, String compPath){ return FULL_PATH_RANGE_1_2     .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    public static String FULL_PATH_RANGE_1_3    (String objId, String compPath){ return FULL_PATH_RANGE_1_3     .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    public static String FULL_PATH_RANGE_2_3    (String objId, String compPath){ return FULL_PATH_RANGE_2_3     .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    //@formatter:on


    // API Descriptions

    //@formatter:off
    //
    public static final String DESCR_PATH_BOOL_SWITCH   = "Send action to boolean component, invert the component state";
    public static final String DESCR_PATH_BOOL_TRUE     = "Send action to boolean component, set the component state to true";
    public static final String DESCR_PATH_BOOL_FALSE    = "Send action to boolean component, set the component state to false";
    public static final String DESCR_PATH_RANGE_SET     = "Send action to range component, set the component state to given value";
    public static final String DESCR_PATH_RANGE_SETg    = "Send action to range component, set the component state to given value";
    public static final String DESCR_PATH_RANGE_INC     = "Send action to range component, increment the component state of 1 step";
    public static final String DESCR_PATH_RANGE_DEC     = "Send action to range component, decrease the component state of 1 stop";
    public static final String DESCR_PATH_RANGE_MAX     = "Send action to range component, set the component state to max";
    public static final String DESCR_PATH_RANGE_MIN     = "Send action to range component, set the component state to min";
    public static final String DESCR_PATH_RANGE_1_2     = "Send action to range component, set the component state to 1/2";
    public static final String DESCR_PATH_RANGE_1_3     = "Send action to range component, set the component state to 1/3";
    public static final String DESCR_PATH_RANGE_2_3     = "Send action to range component, set the component state to 2/3";
    //@formatter:on

}
