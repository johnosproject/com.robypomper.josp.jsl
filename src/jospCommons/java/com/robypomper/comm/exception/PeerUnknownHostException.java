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

public class PeerUnknownHostException extends PeerException {

    // Class constants

    private static final String MSG = "Error on Peer '%s' because can't resolve remoteAddr '%s'";


    // Internal vars

    private final String hostname;


    // Constructors

    public PeerUnknownHostException(String peerLocalId, String hostname) {
        this(peerLocalId, hostname, String.format(MSG, peerLocalId, hostname));
    }

    public PeerUnknownHostException(String peerLocalId, String hostname, Throwable cause) {
        this(peerLocalId, hostname, cause, String.format(MSG, peerLocalId, hostname));
    }

    public PeerUnknownHostException(String peerLocalId, String hostname, String message) {
        this(peerLocalId, hostname, null, message);
    }

    public PeerUnknownHostException(String peerLocalId, String hostname, Throwable cause, String message) {
        this((Peer) null, hostname, cause, message);
    }

    public PeerUnknownHostException(Peer peer, String hostname) {
        this(peer, hostname, String.format(MSG, peer, hostname));
    }

    public PeerUnknownHostException(Peer peer, String hostname, Throwable cause) {
        this(peer, hostname, cause, String.format(MSG, peer, hostname));
    }

    public PeerUnknownHostException(Peer peer, String hostname, String message) {
        this(peer, hostname, null, message);
    }

    public PeerUnknownHostException(Peer peer, String hostname, Throwable cause, String message) {
        super(peer, cause, message);
        this.hostname = hostname;
    }


    // Getters

    public String getHostname() {
        return hostname;
    }

}
