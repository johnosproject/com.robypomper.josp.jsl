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

package com.robypomper.josp.jcp.defs.apis.internal.status;


/**
 * JCP APIs - Status 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Return JCP APIs's status";


    // API Params

    //@formatter:off
    public static final String PARAM_OBJ        = "obj_id";
    public static final String PARAM_SRV        = "srv_id";
    public static final String PARAM_USR        = "usr_id";
    public static final String PARAM_GW         = "gw_id";

    public static final String PARAM_URL_OBJ        = "{" + PARAM_OBJ + "}";
    public static final String PARAM_URL_SRV        = "{" + PARAM_SRV + "}";
    public static final String PARAM_URL_USR        = "{" + PARAM_USR + "}";
    public static final String PARAM_URL_GW         = "{" + PARAM_GW + "}";
    //@formatter:on


    // API Methods

    //@formatter:off
    // Index
    public static final String MTHD_STATUS             = "";
    // Objects
    public static final String MTHD_STATUS_OBJS        = "objects";
    public static final String MTHD_STATUS_OBJ         = "objects/" + PARAM_URL_OBJ;
    // Services
    public static final String MTHD_STATUS_SRVS        = "services";
    public static final String MTHD_STATUS_SRV         = "services/" + PARAM_URL_SRV;
    // Users
    public static final String MTHD_STATUS_USRS        = "users";
    public static final String MTHD_STATUS_USR         = "users/" + PARAM_URL_USR;
    // Gateways
    public static final String MTHD_STATUS_GWS         = "gateways";
    public static final String MTHD_STATUS_GW          = "gateways/" + PARAM_URL_GW;
    //@formatter:on


    // API Paths

    //@formatter:off
    // Index
    public static final String FULL_PATH_STATUS                = API_PATH + "/" + API_VER + "/" + MTHD_STATUS;
    // Objects
    public static final String FULL_PATH_STATUS_OBJS           = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_OBJS;
    public static final String FULL_PATH_STATUS_OBJ            = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_OBJ;
    // Services
    public static final String FULL_PATH_STATUS_SRVS           = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_SRVS;
    public static final String FULL_PATH_STATUS_SRV            = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_SRV;
    // Users
    public static final String FULL_PATH_STATUS_USRS           = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_USRS;
    public static final String FULL_PATH_STATUS_USR            = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_USR;
    // Gateways
    public static final String FULL_PATH_STATUS_GWS            = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_GWS;
    public static final String FULL_PATH_STATUS_GW             = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_GW;
    //@formatter:on


    // API Paths composers

    //@formatter:off
    // Objects
    public static String FULL_PATH_STATUS_OBJ      (String objId){ return FULL_PATH_STATUS_OBJ        .replace(PARAM_URL_OBJ,objId); }
    // Service
    public static String FULL_PATH_STATUS_SRV      (String srvId){ return FULL_PATH_STATUS_SRV        .replace(PARAM_URL_SRV,srvId); }
    // Users
    public static String FULL_PATH_STATUS_USR      (String usrId){ return FULL_PATH_STATUS_USR        .replace(PARAM_URL_USR,usrId); }
    // Gateways
    public static String FULL_PATH_STATUS_GW       (String gwId){ return FULL_PATH_STATUS_GW          .replace(PARAM_URL_GW,gwId); }
    //@formatter:off


    // API Descriptions

    //@formatter:off
    // Index
    public static final String DESCR_PATH_STATUS       = "Index of current API Group";
    // Objects
    public static final String DESCR_PATH_STATUS_OBJS  = "Return objects list";
    public static final String DESCR_PATH_STATUS_OBJ   = "Return request object stats";
    // Services
    public static final String DESCR_PATH_STATUS_SRVS  = "Return services list";
    public static final String DESCR_PATH_STATUS_SRV   = "Return request service stats";
    // Users
    public static final String DESCR_PATH_STATUS_USRS  = "Return users list";
    public static final String DESCR_PATH_STATUS_USR   = "Return request user stats";
    // Gateways
    public static final String DESCR_PATH_STATUS_GWS   = "Return gateways list";
    public static final String DESCR_PATH_STATUS_GW    = "Return request gateways stats";
    //@formatter:on

}
