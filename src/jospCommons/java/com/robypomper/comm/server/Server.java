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

package com.robypomper.comm.server;

import com.robypomper.comm.behaviours.ByeMsgConfigs;
import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.exception.ServerShutdownException;
import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.peer.PeerInfoLocal;

import java.util.List;

public interface Server {

    // Getters

    ServerState getState();

    PeerInfoLocal getServerPeerInfo();

    String getLocalId();    //getLocalInfo().getId()

    String getProtocolName();

    List<ServerClient> getClients();


    // Server startup methods

    void startup() throws ServerStartupException;

    void shutdown() throws ServerShutdownException;


    // Behaviours configs

    DataEncodingConfigs getDataEncodingConfigs();

    ByeMsgConfigs getByeConfigs();

    HeartBeatConfigs getHeartBeatConfigs();


    // Listeners

    void addListener(ServerStateListener listener);

    void removeListener(ServerStateListener listener);

    void addListener(ServerClientsListener listener);

    void removeListener(ServerClientsListener listener);

    void addListener(ServerDataListener listener);

    void removeListener(ServerDataListener listener);

}
