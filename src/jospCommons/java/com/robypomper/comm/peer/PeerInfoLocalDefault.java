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

import com.robypomper.comm.exception.PeerInfoSocketNotConnectedException;
import com.robypomper.java.JavaAssertions;

import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class PeerInfoLocalDefault extends PeerInfoDefault implements PeerInfoLocal {

    // Internal vars

    private NetworkInterface localIntf = null;


    // Constructors

    public PeerInfoLocalDefault(String localId, String protoName) {
        super(localId, protoName, true);
    }


    // Getters

    @Override
    public NetworkInterface getIntf() {
        return localIntf;
    }


    // Updaters

    public void updateOnConnected(Socket socket) throws PeerInfoSocketNotConnectedException {
        super.updateOnConnected(socket);
        try {
            localIntf = NetworkInterface.getByInetAddress(getAddr());
        } catch (SocketException e) {
            JavaAssertions.makeAssertion_Failed(e, "Method NetworkInterface.getByInetAddress(InetAddress) is called from PeerInfoDefault.updateOnConnected(Socket) method, so it should NOT throw any SocketException");
        }
    }

    public void updateOnConnected(ServerSocket socket) throws PeerInfoSocketNotConnectedException {
        super.updateOnConnected(socket);
        try {
            localIntf = NetworkInterface.getByInetAddress(getAddr());
        } catch (SocketException e) {
            JavaAssertions.makeAssertion_Failed(e, "Method NetworkInterface.getByInetAddress(InetAddress) is called from PeerInfoDefault.updateOnConnected(ServerSocket) method, so it should NOT throw any SocketException");
        }
    }

}
