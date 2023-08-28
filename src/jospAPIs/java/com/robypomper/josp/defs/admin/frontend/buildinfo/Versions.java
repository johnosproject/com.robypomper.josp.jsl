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

package com.robypomper.josp.defs.admin.frontend.buildinfo;


/**
 * JOSP Admin - Front End / Build Info
 * <p>
 * API's versions definition for API Group.
 */
public class Versions extends com.robypomper.josp.defs.admin.frontend.Versions {

    // Class Constants

    // Name
    public static final String API_GROUP = "Build Info";
    public static final String API_GROUP_FULL = com.robypomper.josp.defs.admin.frontend.Versions.API_GROUP_FULL + " / " + API_GROUP;
    public static final String API_GROUP_DESCR = "";
    // Urls
    public static final String API_PATH_BASE = com.robypomper.josp.defs.admin.frontend.Versions.API_PATH_BASE + "/buildinfo";
    public static final String AUTH_PATH_BASE = com.robypomper.josp.defs.admin.frontend.Versions.AUTH_PATH_BASE;

}