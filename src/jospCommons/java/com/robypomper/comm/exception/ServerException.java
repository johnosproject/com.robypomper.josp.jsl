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

public class ServerException extends Throwable {

    // Internal vars

    private final Server server;


    // Constructors

    public ServerException(Server server) {
        this.server = server;
    }

    public ServerException(Server server, String message) {
        super(message);
        this.server = server;
    }

    public ServerException(Server server, Throwable cause) {
        super(cause);
        this.server = server;
    }

    public ServerException(Server server, Throwable cause, String message) {
        super(message, cause);
        this.server = server;
    }


    // Getters

    public Server getServer() {
        return server;
    }

}
