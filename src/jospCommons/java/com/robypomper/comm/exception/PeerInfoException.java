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

public class PeerInfoException extends Throwable {

    // Internal vars

    private final PeerInfo peer;


    // Constructors

    public PeerInfoException(PeerInfo peer) {
        this.peer = peer;
    }

    public PeerInfoException(PeerInfo peer, String message) {
        super(message);
        this.peer = peer;
    }

    public PeerInfoException(PeerInfo peer, Throwable cause) {
        super(cause);
        this.peer = peer;
    }

    public PeerInfoException(PeerInfo peer, Throwable cause, String message) {
        super(message, cause);
        this.peer = peer;
    }


    // Getters

    public PeerInfo getPeer() {
        return peer;
    }

}
