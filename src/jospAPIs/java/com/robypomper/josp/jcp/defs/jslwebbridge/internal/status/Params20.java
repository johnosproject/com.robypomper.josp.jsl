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

package com.robypomper.josp.jcp.defs.jslwebbridge.internal.status;

import com.robypomper.josp.types.RESTItemList;

import java.util.Date;
import java.util.List;


/**
 * JCP JSL Web Bridge - Status 2.0
 */
public class Params20 {

    // Index

    public static class Index {

        public final String urlSessions = Paths20.FULL_PATH_STATUS_SESSIONS;

    }


    // Sessions

    public static class Sessions {

        public List<RESTItemList> sessionsList;

    }

    public static class Session {

        public String id;
        public String name;
        public Date createdAt;
        public Date lastAccessedAt;
        public int maxInactiveInterval; // seconds

    }

}
