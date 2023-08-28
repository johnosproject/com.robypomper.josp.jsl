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

import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Default Executor class used by {@link JODExecutor} implementations.
 */
public abstract class AbsJODExecutor extends AbsJODWorker implements JODExecutor {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();
    private boolean enabled = false;


    // Constructor

    /**
     * {@inheritDoc}
     */
    public AbsJODExecutor(String name, String proto, JODComponent component) {
        super(name, proto, component);
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }


    // JODExecutor's impl

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exec() {
        log.debug(Mrk_JOD.JOD_EXEC_SUB, String.format("Executing '%s' executor", getName()));
        if (!enabled) {
            log.warn(Mrk_JOD.JOD_EXEC_SUB, String.format("Error on exec '%s' executor because disabled", getName()));
            return false;
        }

        // Do something...
        log.trace(Mrk_JOD.JOD_EXEC_SUB, "Do something...");


        log.debug(Mrk_JOD.JOD_EXEC_SUB, String.format("Executor '%s' executed", getName()));
        return true;
    }


    // Mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void enable() {
        log.info(Mrk_JOD.JOD_EXEC_SUB, String.format("Enable '%s' executor", getName()));
        if (isEnabled()) return;

        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Enabling executor");
        enabled = true;

        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Executor enabled");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disable() {
        log.info(Mrk_JOD.JOD_EXEC_SUB, String.format("Disable '%s' executor", getName()));
        if (!isEnabled()) return;

        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Disabling executor");
        enabled = false;
        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Executor disabled");
    }

}
