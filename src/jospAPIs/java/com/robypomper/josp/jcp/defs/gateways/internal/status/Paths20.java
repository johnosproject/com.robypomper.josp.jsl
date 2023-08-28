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

package com.robypomper.josp.jcp.defs.gateways.internal.status;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * JCP Gateways - Status 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Return JCP GWs's instance status";


    // API Params

    //@formatter:off
    public static final String PARAM_GW         = "gw_id";
    public static final String PARAM_GW_CLIENT  = "gw_client_id";
    public static final String PARAM_OBJ        = "obj_id";
    public static final String PARAM_SRV        = "srv_id";

    public static final String PARAM_URL_GW         = "{" + PARAM_GW + "}";
    public static final String PARAM_URL_GW_CLIENT  = "{" + PARAM_GW_CLIENT + "}";
    public static final String PARAM_URL_OBJ        = "{" + PARAM_OBJ + "}";
    public static final String PARAM_URL_SRV        = "{" + PARAM_SRV + "}";
    //@formatter:on


    // API Methods

    //@formatter:off
    // Index methods
    public static final String MTHD_STATUS             = "";
    // GWs status methods
    public static final String MTHD_STATUS_GWS         = "gws";
    public static final String MTHD_STATUS_GW          = "gws/" + PARAM_URL_GW;
    public static final String MTHD_STATUS_GW_CLIENT   = "gws/" + PARAM_URL_GW + "/clients";// + "?" + PARAM_GW_CLIENT + "=" + PARAM_URL_GW_CLIENT;
    // Broker status methods
    public static final String MTHD_STATUS_BROKER          = "broker";
    public static final String MTHD_STATUS_BROKER_OBJ      = "broker/obj/" + PARAM_URL_OBJ;
    public static final String MTHD_STATUS_BROKER_SRV      = "broker/srv/";// + "?" + PARAM_SRV + "=" + PARAM_URL_SRV;
    public static final String MTHD_STATUS_BROKER_OBJ_DB   = "broker/objdb/" + PARAM_URL_OBJ;
    //@formatter:on


    // API Paths

    //@formatter:off
    // Index methods
    public static final String FULL_PATH_STATUS                = API_PATH + "/" + API_VER + "/" + MTHD_STATUS;
    // GWs status methods
    public static final String FULL_PATH_STATUS_GWS            = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_GWS;
    public static final String FULL_PATH_STATUS_GW             = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_GW;
    public static final String FULL_PATH_STATUS_GW_CLIENT      = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_GW_CLIENT;
    // Broker status methods
    public static final String FULL_PATH_STATUS_BROKER         = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_BROKER;
    public static final String FULL_PATH_STATUS_BROKER_OBJ     = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_BROKER_OBJ;
    public static final String FULL_PATH_STATUS_BROKER_SRV     = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_BROKER_SRV;
    public static final String FULL_PATH_STATUS_BROKER_OBJ_DB  = API_PATH + "/" + API_VER + "/" + MTHD_STATUS_BROKER_OBJ_DB;
    //@formatter:on


    // API Paths composers
    protected static String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    //@formatter:off
    // GWs status methods
    public static String FULL_PATH_STATUS_GW                (String gwId){ return FULL_PATH_STATUS_GW                               .replace(PARAM_URL_GW,gwId); }
    public static String FULL_PATH_STATUS_GW_CLIENT         (String gwId, String gwClientId){ return FULL_PATH_STATUS_GW_CLIENT     .replace(PARAM_URL_GW,gwId) + "?" + PARAM_GW_CLIENT + "=" + encode(gwClientId); }
    // Broker status methods
    public static String FULL_PATH_STATUS_BROKER_OBJ        (String objId){ return FULL_PATH_STATUS_BROKER_OBJ          .replace(PARAM_URL_OBJ,objId); }
    public static String FULL_PATH_STATUS_BROKER_SRV        (String srvId){ return FULL_PATH_STATUS_BROKER_SRV          + "?" + PARAM_SRV + "=" + encode(srvId); }
    public static String FULL_PATH_STATUS_BROKER_OBJ_DB     (String objId){ return FULL_PATH_STATUS_BROKER_OBJ_DB       .replace(PARAM_URL_OBJ,objId); }
    //@formatter:on


    // API Descriptions

    //@formatter:off
    // Index methods
    public static final String DESCR_PATH_STATUS               = "Index of current API Group";
    // GWs status methods
    public static final String DESCR_PATH_STATUS_GWS           = "Return GW instances list hosted on current JCP Gateways";
    public static final String DESCR_PATH_STATUS_GW            = "Return GW's instance status";
    public static final String DESCR_PATH_STATUS_GW_CLIENT     = "Return GW's client status";
    // Broker status methods
    public static final String DESCR_PATH_STATUS_BROKER            = "Return objects and services list registered on current JCP Gateways (Broker)";
    public static final String DESCR_PATH_STATUS_BROKER_OBJ        = "Return required objects status";
    public static final String DESCR_PATH_STATUS_BROKER_SRV        = "Return required service status";
    public static final String DESCR_PATH_STATUS_BROKER_OBJ_DB     = "Return required objects status (from DB)";

    //@formatter:on

}
