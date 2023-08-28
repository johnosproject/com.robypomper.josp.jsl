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

package com.robypomper.josp.jod.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * State component representation.
 * <p>
 * State component must propagate monitored status changes to connected services.
 */
@SuppressWarnings("JavadocReference")
public interface JODState extends JODComponent {

    // Status's properties

    /**
     * The string representing the worker that monitoring current component.
     *
     * @return the string representing current state's worker.
     */
    @JsonIgnore
    String getWorker();

    String getState();


    // Status upd flow (struct)

    /**
     * Called from action side when an action was executed successfully.
     */
    void forceCheckState();

    /**
     * Called by current state's worker (puller or listener), this method forward
     * the <code>statusUpd</code> to the JOD Communication system.
     *
     * @param statusUpd the status to propagate.
     */
    void propagateState(JODStateUpdate statusUpd) throws JODStructure.CommunicationSetException;

}
