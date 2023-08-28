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
import com.robypomper.josp.jod.executor.JODExecutor;
import com.robypomper.josp.protocol.JOSPProtocol;


/**
 * Action component representation.
 * <p>
 * Action component receive commands from the JOD Communication system and elaborate
 * them using associated {@link com.robypomper.josp.jod.executor.JODExecutor}.
 */
public interface JODAction extends JODState {

    // Status's properties

    /**
     * The string representing the executor that execute current component actions.
     *
     * @return the string representing current action's executor.
     */
    @JsonIgnore
    String getExecutor();


    // Status upd flow (struct)

    /**
     * Called by JOD Communication system, this method execute the action via
     * the {@link JODExecutor#exec()} method.
     *
     * @param commandAction the action's to execute.
     */
    boolean execAction(JOSPProtocol.ActionCmd commandAction);

}
