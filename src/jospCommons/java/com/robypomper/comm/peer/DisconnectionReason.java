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

public enum DisconnectionReason {

    // Values

    NOT_DISCONNECTED,

    LOCAL_REQUEST,
    REMOTE_REQUEST,

    CONNECTION_LOST,
    HEARTBEAT_TIMEOUT,
    REMOTE_ERROR;


    // Utils

    public boolean isRequested() {
        return this == LOCAL_REQUEST
                || this == REMOTE_REQUEST;
    }

    public boolean isError() {
        return this == CONNECTION_LOST
                || this == HEARTBEAT_TIMEOUT
                || this == REMOTE_ERROR;
    }

}
