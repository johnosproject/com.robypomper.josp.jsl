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

package com.robypomper.josp.jcp.defs.base.internal.status.buildinfo;


/**
 * JCP All - Status / Build Info 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to query JCP service build info";


    // API Methods

    //@formatter:off
    // Build Info methods
    public static final String MTHD_BUILDINFO        = "";
    //@formatter:on


    // API Paths

    //@formatter:off
    // Build Info methods
    public static final String FULL_PATH_BUILDINFO      = API_PATH + "/" + API_VER + "/" + MTHD_BUILDINFO;
    //@formatter:on


    // API Descriptions

    //@formatter:off
    // Build Info methods
    public static final String DESCR_PATH_BUILDINFO     = "Return the BuildInfo structure for current JCP Service";
    //@formatter:on

}
