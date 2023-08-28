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

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PeerStreamException extends PeerException {

    // Class constants

    private static final String MSG_IN = "Error on Peer '%s''s input stream";
    private static final String MSG_OUT = "Error on Peer '%s''s output stream";


    // Internal vars

    private final DataInputStream in;
    private final DataOutputStream out;


    // Constructors

    public PeerStreamException(Peer peer, DataInputStream in) {
        this(peer, in, String.format(MSG_IN, peer));
    }

    public PeerStreamException(Peer peer, DataInputStream in, Throwable cause) {
        this(peer, in, cause, String.format(MSG_IN, peer));
    }

    public PeerStreamException(Peer peer, DataInputStream in, String message) {
        this(peer, in, null, message);
    }

    public PeerStreamException(Peer peer, DataInputStream in, Throwable cause, String message) {
        super(peer, message);
        this.in = in;
        this.out = null;
    }

    public PeerStreamException(Peer peer, DataOutputStream out) {
        this(peer, out, String.format(MSG_OUT, peer));
    }

    public PeerStreamException(Peer peer, DataOutputStream out, Throwable cause) {
        this(peer, out, cause, String.format(MSG_OUT, peer));
    }

    public PeerStreamException(Peer peer, DataOutputStream out, String message) {
        this(peer, out, null, message);
    }

    public PeerStreamException(Peer peer, DataOutputStream out, Throwable cause, String message) {
        super(peer, cause, message);
        this.in = null;
        this.out = out;
    }


    // Getters

    public boolean isInputStreamError() {
        return in != null;
    }

    public DataInputStream getInputStream() {
        return in;
    }

    public boolean isOutputStreamError() {
        return out != null;
    }

    public DataOutputStream getOutputStream() {
        return out;
    }

}
