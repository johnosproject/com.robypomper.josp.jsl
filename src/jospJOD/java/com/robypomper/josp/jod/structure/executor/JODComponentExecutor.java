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

package com.robypomper.josp.jod.structure.executor;

import com.robypomper.josp.jod.structure.JODComponent;


/**
 * Class for messaging data to the {@link com.robypomper.josp.jod.executor.JODExecutor}
 * creation.
 * <p>
 * His implementation is provided by {@link JODComponentWorker} super class. The
 * aim of this class is just for type checks.
 */
public class JODComponentExecutor extends JODComponentWorker {

    /**
     * {@inheritDoc}
     * <p>
     * This constructor is Public only because of JOD Shell testing and creation
     * JODComponentExecutor creation.
     */
    public JODComponentExecutor(JODComponent component, String name, String proto, String configsStr) {
        super(component, name, proto, configsStr);
    }

}
