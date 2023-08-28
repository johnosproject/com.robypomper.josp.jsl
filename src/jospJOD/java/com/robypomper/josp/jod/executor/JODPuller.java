/*******************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
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

package com.robypomper.josp.jod.executor;


/**
 * Puller interface used by
 * {@link JODExecutorMngr} JOD system.
 */
public interface JODPuller extends JODWorker {

    // Polling method

    /**
     * Polling method, executed periodically each {@link AbsJODPuller#getPollingTime()}
     * milliseconds.
     * <p>
     * Implementation of this function must perform a pulling request and
     * when detect value updates, then send the update to the corresponding JOD
     * Component via the <code>AbsJODWorker#sendUpdate(com.robypomper.josp.jod.structure.JODStateUpdate)</code>
     * method (this method is provided by the {@link AbsJODWorker} class because
     * need to be implemented, the {@link JODPuller} interface can't host methods
     * implementations. AbsJODWorker is the basic class for each JOD Puller
     * implementation).
     * <p>
     * This method is call only by the internal JOD Puller timer, so no extra
     * check are required like the {@link AbsJODListenerLoop} class.
     */
    void pull();

    // Mngm

    /**
     * Start puller timer.
     */
    void startTimer();


    /**
     * Stop puller timer.
     */
    void stopTimer();

}
