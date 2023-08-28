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

package com.robypomper.comm.exception;

import com.robypomper.comm.peer.PeerInfo;

import java.net.ServerSocket;
import java.net.Socket;

public class PeerInfoSocketNotConnectedException extends PeerInfoException {

    // Class constants

    private static final String MSG = "Error set connected state to PeerInfo '%s' because socket not connected";
    private static final String MSG_SERVER = "Error set connected state to PeerInfo '%s' because server socket not bound";


    // Internal vars

    private final Socket socket;
    private final ServerSocket serverSocket;


    // Constructors

    public PeerInfoSocketNotConnectedException(PeerInfo peer, Socket socket) {
        super(peer, String.format(MSG, peer));
        this.socket = socket;
        this.serverSocket = null;
    }

    public PeerInfoSocketNotConnectedException(PeerInfo peer, Socket socket, Throwable cause) {
        super(peer, cause, String.format(MSG, peer));
        this.socket = socket;
        this.serverSocket = null;
    }

    public PeerInfoSocketNotConnectedException(PeerInfo peer, ServerSocket socket) {
        super(peer, String.format(MSG_SERVER, peer));
        this.socket = null;
        this.serverSocket = socket;
    }

    public PeerInfoSocketNotConnectedException(PeerInfo peer, ServerSocket socket, Throwable cause) {
        super(peer, cause, String.format(MSG_SERVER, peer));
        this.socket = null;
        this.serverSocket = socket;
    }


    // Getters

    public Socket getSocket() {
        return socket;
    }

}
