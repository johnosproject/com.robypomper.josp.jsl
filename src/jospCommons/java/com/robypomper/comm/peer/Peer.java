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

package com.robypomper.comm.peer;

import com.robypomper.comm.behaviours.ByeMsgConfigs;
import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.connection.ConnectionInfo;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;

import java.net.Socket;

public interface Peer {

    // Getters

    ConnectionState getState();

    ConnectionInfo getConnectionInfo();

    DisconnectionReason getDisconnectionReason();

    String getLocalId();    //peer.getConnectionInfo().getLocalInfo().getId()

    String getRemoteId();   //peer.getConnectionInfo().getRemoteInfo().getId()

    Socket getSocket();


    // Connection methods

    void disconnect() throws PeerDisconnectionException;


    // Messages methods

    void sendData(byte[] data) throws PeerNotConnectedException, PeerStreamException;

    void sendData(String data) throws PeerNotConnectedException, PeerStreamException;


    // Behaviours configs

    DataEncodingConfigs getDataEncodingConfigs();

    ByeMsgConfigs getByeConfigs();

    HeartBeatConfigs getHeartBeatConfigs();


    // Listeners

    void addListener(PeerConnectionListener listener);

    void removeListener(PeerConnectionListener listener);

    void addListener(PeerDataListener listener);

    void removeListener(PeerDataListener listener);

}
