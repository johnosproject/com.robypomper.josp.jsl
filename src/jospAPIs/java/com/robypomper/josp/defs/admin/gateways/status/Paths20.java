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

package com.robypomper.josp.defs.admin.gateways.status;


/**
 * JOSP Admin - Gateways / Status 2.0
 */
public class Paths20 extends com.robypomper.josp.jcp.defs.gateways.internal.status.Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to query JCP Gateways status";


    // API Params

    //@formatter:off
    public static final String PARAM_GW_SERVER         = "gw_server_id";

    public static final String PARAM_URL_GW_SERVER     = "{" + PARAM_GW_SERVER + "}";
    //@formatter:on


    // API Methods

    //@formatter:off
    // List methods
    public static final String MTHD_LIST        = "all";
    //@formatter:on


    // API Paths

    //@formatter:off
    // List methods
    public static final String FULL_PATH_JCP_GWS_STATUS_LIST            = API_PATH + "/" + API_VER + "/" + MTHD_LIST;
    // Index methods
    public static final String FULL_PATH_JCP_GWS_STATUS                 = API_PATH + "/" + API_VER + "/" + PARAM_URL_GW_SERVER + "/" + MTHD_STATUS;
    // Status methods
    public static final String FULL_PATH_JCP_GWS_STATUS_GWS             = API_PATH + "/" + API_VER + "/" + PARAM_URL_GW_SERVER + "/" + MTHD_STATUS_GWS;
    public static final String FULL_PATH_JCP_GWS_STATUS_GW              = API_PATH + "/" + API_VER + "/" + PARAM_URL_GW_SERVER + "/" + MTHD_STATUS_GW;
    public static final String FULL_PATH_JCP_GWS_STATUS_GW_CLIENT       = API_PATH + "/" + API_VER + "/" + PARAM_URL_GW_SERVER + "/" + MTHD_STATUS_GW_CLIENT;
    // Broker status methods
    public static final String FULL_PATH_JCP_GWS_STATUS_BROKER          = API_PATH + "/" + API_VER + "/" + PARAM_URL_GW_SERVER + "/" + MTHD_STATUS_BROKER;
    public static final String FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ      = API_PATH + "/" + API_VER + "/" + PARAM_URL_GW_SERVER + "/" + MTHD_STATUS_BROKER_OBJ;
    public static final String FULL_PATH_JCP_GWS_STATUS_BROKER_SRV      = API_PATH + "/" + API_VER + "/" + PARAM_URL_GW_SERVER + "/" + MTHD_STATUS_BROKER_SRV;
    public static final String FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ_DB   = API_PATH + "/" + API_VER + "/" + PARAM_URL_GW_SERVER + "/" + MTHD_STATUS_BROKER_OBJ_DB;


    // API Paths composers

    //@formatter:off
    // Index methods
    public static String FULL_PATH_JCP_GWS_STATUS                   (String gwServerId){ return FULL_PATH_JCP_GWS_STATUS                                                .replace(PARAM_URL_GW_SERVER,gwServerId); }
    // GWs status methods
    public static String FULL_PATH_JCP_GWS_STATUS_GWS               (String gwServerId){ return FULL_PATH_JCP_GWS_STATUS_GWS                                            .replace(PARAM_URL_GW_SERVER,gwServerId); }
    public static String FULL_PATH_JCP_GWS_STATUS_GW                (String gwServerId, String gwId){ return FULL_PATH_JCP_GWS_STATUS_GW                                .replace(PARAM_URL_GW_SERVER,gwServerId).replace(PARAM_URL_GW,gwId); }
    public static String FULL_PATH_JCP_GWS_STATUS_GW_CLIENT         (String gwServerId, String gwId, String gwClientId){ return FULL_PATH_JCP_GWS_STATUS_GW_CLIENT      .replace(PARAM_URL_GW_SERVER,gwServerId).replace(PARAM_URL_GW,gwId) + "?" + PARAM_GW_CLIENT + "=" + encode(gwClientId); }
    // Broker status methods
    public static String FULL_PATH_JCP_GWS_STATUS_BROKER            (String gwServerId){ return FULL_PATH_JCP_GWS_STATUS_BROKER                             .replace(PARAM_URL_GW_SERVER,gwServerId); }
    public static String FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ        (String gwServerId, String objId){ return FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ           .replace(PARAM_URL_GW_SERVER,gwServerId).replace(PARAM_URL_OBJ,objId); }
    public static String FULL_PATH_JCP_GWS_STATUS_BROKER_SRV        (String gwServerId, String srvId){ return FULL_PATH_JCP_GWS_STATUS_BROKER_SRV           .replace(PARAM_URL_GW_SERVER,gwServerId) + "?" + PARAM_SRV + "=" + encode(srvId); }
    public static String FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ_DB     (String gwServerId, String objId){ return FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ_DB        .replace(PARAM_URL_GW_SERVER,gwServerId).replace(PARAM_URL_OBJ,objId); }
    //@formatter:on
    //@formatter:on


    // API Descriptions

    //@formatter:off
    // List methods
    public static final String DESCR_PATH_JCP_GWS_STATUS_LIST           = "Return JCP Gateways servers list";
    // Index methods
    public static final String DESCR_PATH_JCP_GWS_STATUS                = DESCR_PATH_STATUS;
    // Status methods
    public static final String DESCR_PATH_JCP_GWS_STATUS_GWS            = DESCR_PATH_STATUS_GWS;
    public static final String DESCR_PATH_JCP_GWS_STATUS_GW             = DESCR_PATH_STATUS_GW;
    public static final String DESCR_PATH_JCP_GWS_STATUS_GW_CLIENT      = DESCR_PATH_STATUS_GW_CLIENT;
    // Broker status methods
    public static final String DESCR_PATH_JCP_GWS_STATUS_BROKER            = DESCR_PATH_STATUS_BROKER;
    public static final String DESCR_PATH_JCP_GWS_STATUS_BROKER_OBJ        = DESCR_PATH_STATUS_BROKER_OBJ;
    public static final String DESCR_PATH_JCP_GWS_STATUS_BROKER_SRV        = DESCR_PATH_STATUS_BROKER_SRV;
    public static final String DESCR_PATH_JCP_GWS_STATUS_BROKER_OBJ_DB     = DESCR_PATH_STATUS_BROKER_OBJ_DB;
    //@formatter:on

}
