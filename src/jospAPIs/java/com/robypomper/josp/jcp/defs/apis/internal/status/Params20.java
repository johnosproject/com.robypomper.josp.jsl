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

import com.robypomper.josp.types.RESTItemList;
import com.robypomper.josp.types.josp.gw.GWType;

import java.util.Date;
import java.util.List;


/**
 * JCP APIs - Status 2.0
 */
public class Params20 {

    // Index

    public static class Index {

        public final String urlObjects = Paths20.FULL_PATH_STATUS_OBJS;
        public final String urlServices = Paths20.FULL_PATH_STATUS_SRVS;
        public final String urlUsers = Paths20.FULL_PATH_STATUS_USRS;
        public final String urlGateways = Paths20.FULL_PATH_STATUS_GWS;

    }


    // Objects

    public static class Objects {

        public long count;
        public long onlineCount;
        public long offlineCount;
        public long activeCount;
        public long inactiveCount;
        public long ownersCount;
        public List<RESTItemList> objectsList;

    }

    public static class Object {

        public String id;
        public String name;
        public String owner;
        public boolean online;
        public boolean active;
        public String version;
        public Date createdAt;
        public Date updatedAt;

    }

    // Services

    public static class Services {

        public long count;
        public long onlineCount;
        public long offlineCount;
        public long instancesCount;
        public long instancesOnlineCount;
        public long instancesOfflineCount;
        public List<RESTItemList> servicesList;

    }

    public static class Service {

        public String id;
        public String name;
        public Date createdAt;
        public Date updatedAt;

    }


    // Users

    public static class Users {

        public long count;
        public List<RESTItemList> usersList;

    }

    public static class User {

        public String id;
        public String name;
        public String first_name;
        public String second_name;
        public String email;
        public Date createdAt;
        public Date updatedAt;

    }


    // Gateways

    public static class Gateways {

        public long count;
        public long removed;
        public long total;
        public List<RESTItemList> gatewaysList;

    }

    public static class Gateway {

        public String id;
        public String name;
        public String gwUrl;
        public String apiUrl;
        public GWType type;
        public String version;
        public boolean connected;
        public int reconnectionAttempts;
        public Date createdAt;
        public Date updatedAt;
        public int currentClients;
        public int maxClients;
        public Date lastClientConnected;
        public Date lastClientDisconnected;

    }

}
