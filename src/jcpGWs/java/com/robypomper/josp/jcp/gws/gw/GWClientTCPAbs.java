/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.gws.gw;

import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.comm.server.ServerClient;

public abstract class GWClientTCPAbs {

    // Internal vars

    private final ServerClient client;


    // Constructors

    protected GWClientTCPAbs(ServerClient client) {
        this.client = client;
        client.addListener(connectionListener);
    }


    // Getters

    public String getId() {
        return client.getRemoteId();
    }


    // Connection mngm

    protected void forceDisconnection() throws PeerDisconnectionException {
        client.disconnect();
    }


    // Messages methods

    public void send(String data) throws PeerStreamException, PeerNotConnectedException {
        client.sendData(data);
    }


    // Connection mngm

    protected abstract void onDisconnected();

    @SuppressWarnings("FieldCanBeLocal")
    private final PeerConnectionListener connectionListener = new PeerConnectionListener() {

        @Override
        public void onConnecting(Peer peer) {
        }

        @Override
        public void onWaiting(Peer peer) {
        }

        @Override
        public void onConnect(Peer peer) {
        }

        @Override
        public void onDisconnecting(Peer peer) {
        }

        @Override
        public void onDisconnect(Peer peer) {
            onDisconnected();
        }

        @Override
        public void onFail(Peer peer, String failMsg, Throwable exception) {
        }

    };

}
