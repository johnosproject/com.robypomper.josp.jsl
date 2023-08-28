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

import com.robypomper.comm.server.Server;

import java.net.InetAddress;
import java.net.ServerSocket;

public class ServerStartupException extends ServerException {

    // Class constants

    private static final String MSG = "Can't startup '%s' server because can't bind server's socket to '%s:%d'";


    // Internal vars

    private final ServerSocket serverSocket;
    private final InetAddress bindAddr;
    private final int bindPort;


    // Constructors

    public ServerStartupException(Server server, InetAddress bindAddr, int bindPort) {
        this(server, bindAddr, bindPort, null);
    }

    public ServerStartupException(Server server, InetAddress bindAddr, int bindPort, Throwable cause) {
        this(server, null, bindAddr, bindPort, cause);
    }

    public ServerStartupException(Server server, ServerSocket serverSocket, InetAddress bindAddr, int bindPort) {
        this(server, serverSocket, bindAddr, bindPort, null);
    }

    public ServerStartupException(Server server, ServerSocket serverSocket, InetAddress bindAddr, int bindPort, Throwable cause) {
        super(server, cause, String.format(MSG, server.getLocalId(), bindAddr, bindPort));
        this.serverSocket = serverSocket;
        this.bindAddr = bindAddr;
        this.bindPort = bindPort;
    }


    // Getters

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public InetAddress getServerAddr() {
        return bindAddr;
    }

    public int getServerPort() {
        return bindPort;
    }

}
