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
 * Executor interface used by
 * {@link JODExecutorMngr} JOD system.
 */
public interface JODExecutor extends JODWorker {

    // Execution method

    /**
     * Method called from {@link com.robypomper.josp.jod.structure.JODComponent}
     * to exec current executor action.
     *
     * @return true if the action was execute successfully, false otherwise.
     */
    @Deprecated
    boolean exec();

    // Mngm

    /**
     * Enable action execution.
     */
    void enable();

    /**
     * Disable action execution.
     */
    void disable();

}
