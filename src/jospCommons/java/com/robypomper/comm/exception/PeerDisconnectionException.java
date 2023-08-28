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

import com.robypomper.comm.peer.Peer;

import java.net.InetAddress;
import java.net.Socket;

public class PeerDisconnectionException extends PeerException {

    // Class constants

    private static final String MSG = "Error on Peer '%s' because can't disconnect socket from '%s:%d'";


    // Internal vars

    private final Socket socket;
    private final InetAddress remoteAddr;
    private final int remotePort;


    // Constructors

    public PeerDisconnectionException(Peer peer) {
        this(peer, String.format(MSG, peer, "N/A", 0));
    }

    public PeerDisconnectionException(Peer peer, Throwable cause) {
        this(peer, cause, String.format(MSG, peer, "N/A", 0));
    }

    public PeerDisconnectionException(Peer peer, String message) {
        this(peer, (Throwable) null, message);
    }

    public PeerDisconnectionException(Peer peer, Throwable cause, String message) {
        this(peer, null, null, 0, cause, message);
    }

    public PeerDisconnectionException(Peer peer, Socket socket) {
        this(peer, socket, String.format(MSG, peer, "N/A", 0));
    }

    public PeerDisconnectionException(Peer peer, Socket socket, Throwable cause) {
        this(peer, socket, cause, String.format(MSG, peer, "N/A", 0));
    }

    public PeerDisconnectionException(Peer peer, Socket socket, String message) {
        this(peer, socket, null, message);
    }

    public PeerDisconnectionException(Peer peer, Socket socket, Throwable cause, String message) {
        this(peer, socket, null, 0, cause, message);
    }

    public PeerDisconnectionException(Peer peer, Socket socket, InetAddress remoteAddr, int remotePort) {
        this(peer, socket, remoteAddr, remotePort, String.format(MSG, peer, remoteAddr.getHostAddress(), remotePort));
    }

    public PeerDisconnectionException(Peer peer, Socket socket, InetAddress remoteAddr, int remotePort, Throwable cause) {
        this(peer, socket, remoteAddr, remotePort, cause, String.format(MSG, peer, remoteAddr.getHostAddress(), remotePort));
    }

    public PeerDisconnectionException(Peer peer, Socket socket, InetAddress remoteAddr, int remotePort, String message) {
        this(peer, socket, remoteAddr, remotePort, null, message);
    }

    public PeerDisconnectionException(Peer peer, Socket socket, InetAddress remoteAddr, int remotePort, Throwable cause, String message) {
        super(peer, cause, message);
        this.socket = socket;
        this.remoteAddr = remoteAddr;
        this.remotePort = remotePort;
    }


    // Getters

    public Socket getSocket() {
        return socket;
    }

    public InetAddress getRemoteAddr() {
        return remoteAddr;
    }

    public int getRemotePort() {
        return remotePort;
    }

}
