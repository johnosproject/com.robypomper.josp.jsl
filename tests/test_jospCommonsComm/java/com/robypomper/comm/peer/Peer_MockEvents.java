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

import com.robypomper.comm.behaviours.HeartBeatConfigs_MockEvents;
import com.robypomper.comm.connection.ConnectionInfoDefault;
import com.robypomper.java.JavaListeners;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Peer_MockEvents extends Peer_Mock {

    private final ConnectionInfoDefault connectionInfo;
    private final HeartBeatConfigs_MockEvents heartBeat;
    private final List<PeerConnectionListener> listenersConnection = new ArrayList<>();
    private final List<PeerDataListener> listenersData = new ArrayList<>();

    public Peer_MockEvents(String localId, String remoteId, String proto, Socket socket) {
        super(socket);
        heartBeat = new HeartBeatConfigs_MockEvents();
        connectionInfo = new ConnectionInfoDefault(this, localId, remoteId, proto);
    }

    @Override
    public ConnectionInfoDefault getConnectionInfo() {
        return connectionInfo;
    }

    @Override
    public HeartBeatConfigs_MockEvents getHeartBeatConfigs() {
        return heartBeat;
    }

    @Override
    public void addListener(PeerConnectionListener listener) {
        listenersConnection.add(listener);
    }

    @Override
    public void removeListener(PeerConnectionListener listener) {
        listenersConnection.remove(listener);
    }

    public void emitOnConnect() {
        connectionInfo.updateOnConnected();
        JavaListeners.emitter(this, listenersConnection, "onConnect", new JavaListeners.ListenerMapper<PeerConnectionListener>() {
            @Override
            public void map(PeerConnectionListener l) {
                l.onConnect(Peer_MockEvents.this);
            }
        });
    }

    public void emitOnDisconnect() {
        connectionInfo.updateOnDisconnected();
        JavaListeners.emitter(this, listenersConnection, "onDisconnect", new JavaListeners.ListenerMapper<PeerConnectionListener>() {
            @Override
            public void map(PeerConnectionListener l) {
                l.onDisconnect(Peer_MockEvents.this);
            }
        });
    }

    @Override
    public void addListener(PeerDataListener listener) {
        listenersData.add(listener);
    }

    @Override
    public void removeListener(PeerDataListener listener) {
        listenersData.remove(listener);
    }

    public void emitOnDataRx(byte[] data) {
        JavaListeners.emitter(this, listenersData, "onDataRx", new JavaListeners.ListenerMapper<PeerDataListener>() {
            @Override
            public void map(PeerDataListener l) {
                l.onDataRx(Peer_MockEvents.this, data);
            }
        });
    }

    public void emitOnDataTx(byte[] data) {
        JavaListeners.emitter(this, listenersData, "onDataTx", new JavaListeners.ListenerMapper<PeerDataListener>() {
            @Override
            public void map(PeerDataListener l) {
                l.onDataTx(Peer_MockEvents.this, data);
            }
        });
    }
}
