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

package com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.apis.status;


/**
 * JCP JSL Web Bridge - Admin / APIs / Status 2.0
 */
public class Paths20 extends com.robypomper.josp.defs.admin.apis.status.Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to manage JCP as Admin";


    // API Paths

    //@formatter:off
    // JCP APIs Status methods
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS            = API_PATH + "/" + API_VER + "/" + MTHD_STATUS;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_OBJS       = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_OBJS;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_OBJ        = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_OBJ;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_SRVS       = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_SRVS;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_SRV        = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_SRV;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_USRS       = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_USRS;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_USR        = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_USR;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_GWS        = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_GWS;
    public static final String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_GW         = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_GW;
    //@formatter:on


    // API Paths composers

    //@formatter:off
    // JCP APIs Status methods
    public static String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_OBJ      (String objId){ return FULL_PATH_JSLWB_ADMIN_APIS_STATUS_OBJ        .replace(PARAM_URL_OBJ,objId); }
    public static String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_SRV      (String srvId){ return FULL_PATH_JSLWB_ADMIN_APIS_STATUS_SRV        .replace(PARAM_URL_SRV,srvId); }
    public static String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_USR      (String usrId){ return FULL_PATH_JSLWB_ADMIN_APIS_STATUS_USR        .replace(PARAM_URL_USR,usrId); }
    public static String FULL_PATH_JSLWB_ADMIN_APIS_STATUS_GW       (String gwId){ return FULL_PATH_JSLWB_ADMIN_APIS_STATUS_GW          .replace(PARAM_URL_GW,gwId); }
    //@formatter:off


    // API Descriptions

    //@formatter:off
    // JCP APIs Status methods
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS            = DESCR_PATH_JCP_APIS_STATUS;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_OBJS       = DESCR_PATH_JCP_APIS_STATUS_OBJS;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_OBJ        = DESCR_PATH_JCP_APIS_STATUS_OBJ;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_SRVS       = DESCR_PATH_JCP_APIS_STATUS_SRVS;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_SRV        = DESCR_PATH_JCP_APIS_STATUS_SRV;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_USRS       = DESCR_PATH_JCP_APIS_STATUS_USRS;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_USR        = DESCR_PATH_JCP_APIS_STATUS_USR;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_GWS        = DESCR_PATH_JCP_APIS_STATUS_GWS;
    public static final String DESCR_PATH_JSLWB_ADMIN_APIS_STATUS_GW         = DESCR_PATH_JCP_APIS_STATUS_GW;
    //@formatter:on

}
