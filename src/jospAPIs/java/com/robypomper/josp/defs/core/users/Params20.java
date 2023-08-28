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

package com.robypomper.josp.defs.core.users;


import com.robypomper.josp.consts.JOSPConstants;

/**
 * JOSP Core - Users 2.0
 */
public class Params20 {

    // User

    public static class User {
        public String usrId;
        public String username;
        public boolean authenticated;
        public boolean admin;
        public boolean maker;
        public boolean developer;

        public static User ANONYMOUS;

        static {
            ANONYMOUS = new User();
            ANONYMOUS.usrId = JOSPConstants.ANONYMOUS_ID;
            ANONYMOUS.username = JOSPConstants.ANONYMOUS_USERNAME;
            ANONYMOUS.authenticated = false;
            ANONYMOUS.admin = false;
            ANONYMOUS.maker = false;
            ANONYMOUS.developer = false;
        }
    }

}