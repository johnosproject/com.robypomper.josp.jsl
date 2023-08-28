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

package com.robypomper.josp.states;

/**
 * GW Client state representations.
 */
public enum GWClientState {

    /**
     * GW Client is connected to the server and it's fully operative.
     */
    CONNECTED,

    /**
     * GW Client is connecting to the server. This state is reached only on
     * first connection attempt. Other connection retry have others
     * CONNECTION_XYZ states depending on what was wrong on first connection
     * attempt.
     */
    CONNECTING,

    /**
     * GW Client is trying connect to the server but the JCP APIs are not
     * reachable.
     * When this state is set, an internal listener is registered to
     * the JCP APIs connection event.
     */
    CONNECTING_WAITING_JCP_APIS,

    /**
     * GW Client is trying connect to the server but the JCP GWs are not
     * reachable or other errors occurred.
     * When this state is set, an internal timer is setup to retry connect
     * to the GW server.
     */
    CONNECTING_WAITING_JCP_GWS,

    /**
     * GW Client is disconnected to the server.
     */
    DISCONNECTED,

    /**
     * GW Client is disconnecting to the server.
     */
    DISCONNECTING;


    /**
     * Join all CONNECTING_ states.
     *
     * @return true if current state is a CONNECTING_ state.
     */
    public boolean isCONNECTING() {
        return this == CONNECTING
                || this == CONNECTING_WAITING_JCP_APIS
                || this == CONNECTING_WAITING_JCP_GWS
                ;
    }

}
