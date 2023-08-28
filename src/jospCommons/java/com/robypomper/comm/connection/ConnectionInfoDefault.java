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

package com.robypomper.comm.connection;

import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.behaviours.HeartBeatListener;
import com.robypomper.comm.exception.PeerInfoSocketNotConnectedException;
import com.robypomper.comm.peer.*;
import com.robypomper.java.JavaAssertions;

public class ConnectionInfoDefault implements ConnectionInfo {

    // Internal vars

    private final Peer peer;
    private ConnectionState state = ConnectionState.DISCONNECTED;
    private final ConnectionStatsDefault stats;
    private final PeerInfoLocalDefault local;
    private final PeerInfoRemoteDefault remote;
    private final String protoName;


    // Constructors

    public ConnectionInfoDefault(Peer peer, String localId, String remoteId, String protoName) {
        this.peer = peer;
        boolean connected = peer.getSocket() != null && peer.getSocket().isConnected() && !peer.getSocket().isClosed();

        this.stats = new ConnectionStatsDefault();
        this.local = new PeerInfoLocalDefault(localId, protoName);
        this.remote = new PeerInfoRemoteDefault(remoteId, protoName);
        if (connected) {
            updateOnConnected();
        }

        this.protoName = protoName;

        peer.addListener(dataListener);
        peer.getHeartBeatConfigs().addListener(hbListener);
    }


    // toString()

    @Override
    public String toString() {
        String connState;
        //@formatter:off
        switch (state) {
            case CONNECTING:        connState = "-| "; break;
            case WAITING_SERVER:    connState = "..."; break;
            case CONNECTED:         connState = "---"; break;
            case DISCONNECTING:     connState = " |-"; break;
            case DISCONNECTED:      connState = " | "; break;
            default:                connState = "   ";
        }
        //@formatter:on
        return String.format("%s <%s> %s", local, connState, remote);
    }


    // Getters

    @Override
    public ConnectionState getState() {
        return state;
    }

    @Override
    public ConnectionStats getStats() {
        return stats;
    }

    @Override
    public PeerInfoLocal getLocalInfo() {
        return local;
    }

    @Override
    public PeerInfoRemote getRemoteInfo() {
        return remote;
    }

    @Override
    public String getProtocolName() {
        return protoName;
    }


    // Update on peer's events - Connection

    public void updateOnConnecting() {
        state = ConnectionState.CONNECTING;
    }

    public void updateOnConnecting_Waiting() {
        state = ConnectionState.WAITING_SERVER;
    }

    public void updateOnConnected() {
        try {
            local.updateOnConnected(peer.getSocket());
            remote.updateOnConnected(peer.getSocket());

        } catch (PeerInfoSocketNotConnectedException e) {
            JavaAssertions.makeAssertion_Failed(e, String.format("Method ConnectionInfoDefault.updateOnConnected() called when internal socket is NOT connected. [%s] %s", e.getClass().getSimpleName(), e.getMessage()));
        }
        state = ConnectionState.CONNECTED;
        stats.updateOnConnected();
        //System.out.print(String.format("#### updateOnConnected (%s) %s\n#### ", this.hashCode(), this));
        //System.out.println(JavaThreads.currentStackTraceToString().replace("\n","\n#### ") + "\n");
    }

    public void updateOnDisconnecting() {
        state = ConnectionState.DISCONNECTING;
    }

    public void updateOnDisconnected() {
        state = ConnectionState.DISCONNECTED;
        stats.updateOnDisconnected();
        local.updateOnDisconnected();
        remote.updateOnDisconnected();
        //System.out.println(String.format("#### updateOnDisconnected (%s) %s", this.hashCode(), this));
        //System.out.println(JavaThreads.currentStackTraceToString().replace("\n","\n#### "));
    }


    // Update on peer's events - Heartbeat

    public void updateOnHeartBeatSuccess() {
        stats.updateOnHeartBeatSuccess();
    }

    public void updateOnHeartBeatFail() {
        stats.updateOnHeartBeatFail();
    }


    // Update on peer's events - Data Rx/Tx

    public void updateOnDataRx(byte[] data) {
        stats.updateOnDataRx(data);
    }

    public void updateOnDataTx(byte[] data) {
        stats.updateOnDataTx(data);
    }


    // Updates listeners

    private PeerDataListener dataListener = new PeerDataListener() {

        @Override
        public void onDataRx(Peer peer, byte[] data) {
            updateOnDataRx(data);
        }

        @Override
        public void onDataTx(Peer peer, byte[] data) {
            updateOnDataTx(data);
        }

    };

    @SuppressWarnings("FieldCanBeLocal")
    private final HeartBeatListener hbListener = new HeartBeatListener() {

        @Override
        public void onSend(Peer peer, HeartBeatConfigs hb) {
        }

        @Override
        public void onSuccess(Peer peer, HeartBeatConfigs hb) {
            updateOnHeartBeatSuccess();
        }

        @Override
        public void onFail(Peer peer, HeartBeatConfigs hb) {
            updateOnHeartBeatFail();
        }

    };

}
