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

package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.states;


/**
 * JCP JSL Web Bridge - Objects / States 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to get objects states and their histories";


    // API Params

    //@formatter:off
    public static final String PARAM_OBJ        = "obj_id";
    public static final String PARAM_COMP       = "comp_path";

    public static final String PARAM_URL_OBJ        = "{" + PARAM_OBJ + "}";
    public static final String PARAM_URL_COMP       = "{" + PARAM_COMP + "}";
    //@formatter:on


    // API Methods

    //@formatter:off
    // State methods
    private static final String MTHD_BOOL   = "bool/" + PARAM_URL_OBJ +"/" + PARAM_URL_COMP +"/";
    private static final String MTHD_RANGE  = "range/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP +"/";
    // History methods
    private static final String MTHD_STATUS_HISTORY  = "history/" + PARAM_URL_OBJ + "/" + PARAM_URL_COMP +"/";
    //@formatter:on


    // API Paths

    //@formatter:off
    // State methods
    public static final String FULL_PATH_BOOL       = API_PATH + "/" + API_VER + "/" + MTHD_BOOL;
    public static final String FULL_PATH_RANGE      = API_PATH + "/" + API_VER + "/" + MTHD_RANGE;
    // History methods
    public static final String FULL_PATH_STATUS_HISTORY  = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_HISTORY;
    //@formatter:on


    // API Paths composers

    //@formatter:off
    // State methods
    public static String FULL_PATH_BOOL     (String objId, String compPath){ return FULL_PATH_BOOL  .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    public static String FULL_PATH_RANGE    (String objId, String compPath){ return FULL_PATH_RANGE .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    // History methods
    public static String FULL_PATH_STATUS_HISTORY       (String objId, String compPath){ return FULL_PATH_STATUS_HISTORY .replace(PARAM_URL_OBJ,objId).replace(PARAM_URL_COMP,compPath); }
    //@formatter:on


    // API Descriptions

    //@formatter:off
    // State methods
    public static final String DESCR_PATH_BOOL      = "Return boolean component state";
    public static final String DESCR_PATH_RANGE     = "Return range component state";
    // History methods
    public static final String DESCR_PATH_STATUS_HISTORY = "Return component state history";
    //@formatter:on

}
