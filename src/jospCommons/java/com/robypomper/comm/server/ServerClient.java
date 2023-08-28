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

import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.peer.PeerAbs;

import java.net.Socket;
import java.nio.charset.Charset;

public class ServerClient extends PeerAbs {

    // Internal vars

    private final ServerAbs server;
    private final Socket socket;


    // Constructors

    public ServerClient(ServerAbs server, String localId, String remoteId, String protoName, Socket socket,
                        Charset charset, byte[] delimiter,
                        int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                        Boolean enableByeMsg, byte[] byeMsg) {
        super(localId, remoteId, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);

        this.server = server;
        this.socket = socket;

        if (getSocket() != null && getSocket().isConnected() && !getSocket().isClosed())
            startupConnection();
    }


    // Getters

    public Server getServer() {
        return server;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    protected void closeSocket() throws PeerDisconnectionException {
        server.closeSocket(this, socket);
    }

    @Override
    protected boolean processData(byte[] data) {
        return server.processData(this, data);
    }

    @Override
    protected boolean processData(String data) {
        return server.processData(this, data);
    }

}
