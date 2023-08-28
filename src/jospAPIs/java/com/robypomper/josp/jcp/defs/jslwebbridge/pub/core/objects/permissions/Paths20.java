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

package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.permissions;


/**
 * JCP JSL Web Bridge - Objects / Permissions 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to get/upd/rem/dup objects permissions";


    // API Params

    //@formatter:off
    public static final String PARAM_OBJ        = "obj_id";
    public static final String PARAM_PERM       = "comp_path";

    public static final String PARAM_URL_OBJ        = "{" + PARAM_OBJ + "}";
    public static final String PARAM_URL_PERM       = "{" + PARAM_PERM + "}";
    //@formatter:on


    // API Methods

    //@formatter:off
    //
    private static final String MTHD_LIST    = "" + PARAM_URL_OBJ + "/";
    private static final String MTHD_ADD      = "" + PARAM_URL_OBJ + "/add/";
    private static final String MTHD_UPD      = "" + PARAM_URL_OBJ + "/upd/" + PARAM_URL_PERM + "/";
    private static final String MTHD_DEL      = "" + PARAM_URL_OBJ + "/del/" + PARAM_URL_PERM + "/";
    private static final String MTHD_DUP      = "" + PARAM_URL_OBJ + "/dup/" + PARAM_URL_PERM + "/";
    //@formatter:on


    // API Paths

    //@formatter:off
    //
    public static final String FULL_PATH_LIST       = API_PATH + "/" + API_VER + "/" + MTHD_LIST;
    public static final String FULL_PATH_ADD        = API_PATH + "/" + API_VER + "/" + MTHD_ADD;
    public static final String FULL_PATH_UPD        = API_PATH + "/" + API_VER + "/" + MTHD_UPD;
    public static final String FULL_PATH_DEL        = API_PATH + "/" + API_VER + "/" + MTHD_DEL;
    public static final String FULL_PATH_DUP        = API_PATH + "/" + API_VER + "/" + MTHD_DUP;
    //@formatter:on


    // API Paths composers

    //@formatter:off
    //
    public static String FULL_PATH_LIST (String objId)                  { return FULL_PATH_LIST .replace(PARAM_URL_OBJ,objId); }
    public static String FULL_PATH_ADD  (String objId)                  { return FULL_PATH_ADD  .replace(PARAM_URL_OBJ,objId); }
    public static String FULL_PATH_UPD  (String objId, String permId)   { return FULL_PATH_UPD  .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_PERM,permId); }
    public static String FULL_PATH_DEL  (String objId, String permId)   { return FULL_PATH_DEL  .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_PERM,permId); }
    public static String FULL_PATH_DUP  (String objId, String permId)   { return FULL_PATH_DUP  .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_PERM,permId); }
    //@formatter:on


    // API Descriptions

    //@formatter:off
    //
    public static final String DESCR_PATH_LIST  = "Return the object's permissions list";
    public static final String DESCR_PATH_ADD   = "Send add permission request to object";
    public static final String DESCR_PATH_UPD   = "Send update permission request to object";
    public static final String DESCR_PATH_DEL   = "Send remove permission request to object";
    public static final String DESCR_PATH_DUP   = "Send duplicate permission request to object";
    //@formatter:on

}
