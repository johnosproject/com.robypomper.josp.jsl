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
 * JSL Local communication state representations.
 */
public enum JSLLocalState {

    /**
     * JSL library instance is started and waiting for new services.
     */
    RUN_WAITING,

    /**
     * JSL library instance is started and connected to some service.
     */
    RUN_CONNECTED,

    /**
     * JSL library instance is starting, when finish the status become
     * or {@link #STOP} if error occurs.
     */
    STARTING,

    /**
     * JSL library instance is stopped.
     */
    STOP,

    /**
     * JSL library instance is disconnecting, when finish the status
     * become {@link #STOP}.
     */
    SHOUTING;


    /**
     * Join all RUN_ states.
     *
     * @return true if current state is a RUN_ state.
     */
    public boolean isRUN() {
        return this == RUN_WAITING
                || this == RUN_CONNECTED
                //|| this == CONNECTING_WAITING_JCP_GWS
                ;
    }


}
