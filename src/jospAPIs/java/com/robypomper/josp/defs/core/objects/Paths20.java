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

package com.robypomper.josp.defs.core.objects;


/**
 * JOSP Core - Objects 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Generate object's id and register status history updates";


    // API Methods

    //@formatter:off
    // Generator methods
    private static final String MTHD_ID_GENERATE        = "id/generate";
    private static final String MTHD_ID_REGENERATE      = "id/regenerate";
    // History methods
    private static final String MTHD_HISTORY            = "history";
    //@formatter:on


    // API Paths

    //@formatter:off
    // Generator methods
    public static final String FULL_PATH_ID_GENERATE         = API_PATH + "/" + API_VER + "/" + MTHD_ID_GENERATE;
    public static final String FULL_PATH_ID_REGENERATE       = API_PATH + "/" + API_VER + "/" + MTHD_ID_REGENERATE;
    // History methods
    public static final String FULL_PATH_HISTORY            = API_PATH + "/" + API_VER + "/" + MTHD_HISTORY;
    //@formatter:on


    // API Descriptions

    //@formatter:off
    // Generator methods
    public static final String DESCR_PATH_ID_GENERATE       = "Generate and return an obj's id for a new object";
    public static final String DESCR_PATH_ID_REGENERATE     = "Generate and return an obj's id for a existing object";
    // History methods
    public static final String DESCR_PATH_HISTORY       = "Store given statuses as caller object's statuses history";
    //@formatter:on

}
