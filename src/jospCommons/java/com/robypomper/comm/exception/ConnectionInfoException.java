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

import com.robypomper.comm.connection.ConnectionInfo;

public class ConnectionInfoException extends Throwable {

    // Internal vars

    private final ConnectionInfo connection;


    // Constructors

    public ConnectionInfoException(ConnectionInfo connection) {
        this.connection = connection;
    }

    public ConnectionInfoException(ConnectionInfo connection, String message) {
        super(message);
        this.connection = connection;
    }

    public ConnectionInfoException(ConnectionInfo connection, Throwable cause) {
        super(cause);
        this.connection = connection;
    }

    public ConnectionInfoException(ConnectionInfo connection, Throwable cause, String message) {
        super(message, cause);
        this.connection = connection;
    }


    // Getters

    public ConnectionInfo getConnectionInfo() {
        return connection;
    }

}
