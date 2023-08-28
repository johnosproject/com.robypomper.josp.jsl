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
import com.robypomper.comm.behaviours.HeartBeatConfigs_Mock;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.connection.ConnectionInfo;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.comm.exception.PeerDisconnectionException;

import java.net.Socket;

public class Peer_Mock implements Peer {

    private final Socket socket;

    public Peer_Mock(Socket socket) {
        this.socket = socket;
    }

    @Override
    public ConnectionState getState() {
        return null;
    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        return null;
    }

    @Override
    public DisconnectionReason getDisconnectionReason() {
        return null;
    }

    @Override
    public String getLocalId() {
        return null;
    }

    @Override
    public String getRemoteId() {
        return null;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void disconnect() throws PeerDisconnectionException {
    }

    @Override
    public void sendData(byte[] data) {
    }

    @Override
    public void sendData(String data) {
    }

    @Override
    public DataEncodingConfigs getDataEncodingConfigs() {
        return null;
    }

    @Override
    public ByeMsgConfigs getByeConfigs() {
        return null;
    }

    @Override
    public HeartBeatConfigs getHeartBeatConfigs() {
        return new HeartBeatConfigs_Mock();
    }

    @Override
    public void addListener(PeerConnectionListener listener) {
    }

    @Override
    public void removeListener(PeerConnectionListener listener) {
    }

    @Override
    public void addListener(PeerDataListener listener) {
    }

    @Override
    public void removeListener(PeerDataListener listener) {
    }

}
