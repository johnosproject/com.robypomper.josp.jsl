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

import com.robypomper.josp.types.RESTItemList;
import com.robypomper.josp.types.josp.gw.GWType;

import java.util.Date;
import java.util.List;


/**
 * JCP Gateways - Status 2.0
 */
public class Params20 {

    // Index

    public static class Index {

        public final String urlGateways = Paths20.FULL_PATH_STATUS_GWS;
        public final String urlBroker = Paths20.FULL_PATH_STATUS_BROKER;

    }

    // GWs

    public static class GWs {

        public String id;
        public List<RESTItemList> gwList;

    }

    public static class GW {

        public String id;
        public GWType type;
        public String status;
        public String internalAddress;
        public String publicAddress;
        public int gwPort;
        public int apisPort;
        public int clientsCount;
        public int maxClientsCount;
        public List<RESTItemList> clientsList;

    }

    public static class GWClient {

        public String id;
        public boolean isConnected;
        public String local;
        public String remote;
        public long bytesRx;
        public long bytesTx;
        public Date lastDataRx;
        public Date lastDataTx;
        public Date lastConnection;
        public Date lastDisconnection;
        public Date lastHeartBeat;
        public Date lastHeartBeatFailed;

    }


    // Broker

    public static class Broker {

        public List<RESTItemList> objsList;
        public List<RESTItemList> srvsList;
        public List<RESTItemList> objsDBList;

    }

    public static class BrokerObject extends BrokerObjectDB {

    }

    public static class BrokerService {

        public String id;
        public String name;
        public String user;

    }

    public static class BrokerObjectDB {

        public String id;
        public String name;
        public String owner;

    }
}
