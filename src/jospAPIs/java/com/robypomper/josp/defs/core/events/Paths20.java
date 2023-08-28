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

package com.robypomper.josp.defs.core.events;


/**
 * JOSP Core - Events 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Register and get events";


    // API Methods

    //@formatter:off
    // Objects methods
    private static final String MTHD_OBJECT                 = "object";
    //private static final String MTHD_GET_OBJECT             = "object/{objId}";
    //private static final String MTHD_OBJECT_LAST            = "object/last";
    //private static final String MTHD_GET_OBJECT_LAST        = "object/{objId}/last";
    //private static final String MTHD_OBJECT_BY_TYPE         = "object/type/{type}";
    //private static final String MTHD_GET_OBJECT_BY_TYPE     = "object/{objId}/type/{type}";
    // Service methods
    private static final String MTHD_SERVICE                = "service";
    //private static final String MTHD_GET_SERVICE            = "service/{fullSrvId}";
    //private static final String MTHD_SERVICE_LAST           = "service/last";
    //private static final String MTHD_GET_SERVICE_LAST       = "service/{fullSrvId}/last";
    //private static final String MTHD_SERVICE_BY_TYPE        = "service/type/{type}";
    //private static final String MTHD_GET_SERVICE_BY_TYPE    = "service/{fullSrvId}/type/{type}";
    //@formatter:on


    // API Paths

    //@formatter:off
    // Objects methods
    public static final String FULL_PATH_OBJECT                 = API_PATH + "/" + API_VER + "/" + MTHD_OBJECT;
    //public static final String FULL_PATH_GET_OBJECT             = API_PATH + "/" + API_VER + "/" + MTHD_GET_OBJECT;
    //public static final String FULL_PATH_OBJECT_LAST            = API_PATH + "/" + API_VER + "/" + MTHD_OBJECT_LAST;
    //public static final String FULL_PATH_GET_OBJECT_LAST        = API_PATH + "/" + API_VER + "/" + MTHD_GET_OBJECT_LAST;
    //public static final String FULL_PATH_OBJECT_BY_TYPE         = API_PATH + "/" + API_VER + "/" + MTHD_OBJECT_BY_TYPE;
    //public static final String FULL_PATH_GET_OBJECT_BY_TYPE     = API_PATH + "/" + API_VER + "/" + MTHD_GET_OBJECT_BY_TYPE;
    // Service methods
    public static final String FULL_PATH_SERVICE                = API_PATH + "/" + API_VER + "/" + MTHD_SERVICE;
    //public static final String FULL_PATH_GET_SERVICE            = API_PATH + "/" + API_VER + "/" + MTHD_GET_SERVICE;
    //public static final String FULL_PATH_SERVICE_LAST           = API_PATH + "/" + API_VER + "/" + MTHD_SERVICE_LAST;
    //public static final String FULL_PATH_GET_SERVICE_LAST       = API_PATH + "/" + API_VER + "/" + MTHD_GET_SERVICE_LAST;
    //public static final String FULL_PATH_SERVICE_BY_TYPE        = API_PATH + "/" + API_VER + "/" + MTHD_SERVICE_BY_TYPE;
    //public static final String FULL_PATH_GET_SERVICE_BY_TYPE    = API_PATH + "/" + API_VER + "/" + MTHD_GET_SERVICE_BY_TYPE;
    //@formatter:on


    // API Descriptions

    //@formatter:off
    // Objects methods
    public static final String DESCR_PATH_OBJECT                = "Store given events as caller object's events";
    //public static final String DESCR_PATH_OBJECTg               = "Return latest events from current object";
    //public static final String DESCR_PATH_GET_OBJECT            = "Return latest events from specified object";
    //public static final String DESCR_PATH_OBJECT_LAST           = "Return last event from current object";
    //public static final String DESCR_PATH_GET_OBJECT_LAST       = "Return last event from specified object";
    //public static final String DESCR_PATH_OBJECT_BY_TYPE        = "Return latest events of specified type from current object";
    //public static final String DESCR_PATH_GET_OBJECT_BY_TYPE    = "Return latest events of specified type from specified object";
    // Service methods
    public static final String DESCR_PATH_SERVICE               = "Store given events as caller service's events";
    //public static final String DESCR_PATH_SERVICEg              = "Return latest events from current service";
    //public static final String DESCR_PATH_GET_SERVICE           = "Return latest events from specified services";
    //public static final String DESCR_PATH_SERVICE_LAST          = "Return last event from current service";
    //public static final String DESCR_PATH_GET_SERVICE_LAST      = "Return last event from specified service";
    //public static final String DESCR_PATH_SERVICE_BY_TYPE       = "Return latest events from current service and specified event type";
    //public static final String DESCR_PATH_GET_SERVICE_BY_TYPE   = "Return latest events from specified services and specified event type";
    //@formatter:on

}
