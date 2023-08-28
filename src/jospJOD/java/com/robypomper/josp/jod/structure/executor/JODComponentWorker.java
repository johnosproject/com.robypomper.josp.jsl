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
 * Base class for messaging classes between structure and executor mngr JOD systems.
 * <p>
 * His sub classes are initialized from the JOD structure during
 * {@link JODComponent} creation and given to JOD executor manager for worker
 * (Puller, Listener or Executor) creation.
 */
public abstract class JODComponentWorker {

    // Internal vars

    private final JODComponent component;
    private final String name;
    private final String proto;
    private final String configsStr;


    // Constructor

    /**
     * Full args constructor.
     *
     * @param component  the {@link JODComponent} that created this JODComponentWorker.
     * @param name       the JODWorker's name.
     * @param proto      the JODWorker's protocol.
     * @param configsStr the JODWorker's configs string.
     */
    protected JODComponentWorker(JODComponent component, String name, String proto, String configsStr) {
        this.component = component;
        this.name = name;
        this.proto = proto;
        this.configsStr = configsStr;
    }


    // Getters

    /**
     * {@link JODComponent} that generated current JODComponentWorker.
     *
     * @return the owner {@link JODComponent}.
     */
    public JODComponent getComponent() {
        return component;
    }

    /**
     * JODWorker's name.
     *
     * @return the JODComponentWorker's name.
     */
    public String getName() {
        return name;
    }

    /**
     * JODWorker's protocol.
     *
     * @return the JODComponentWorker's protocol.
     */
    public String getProto() {
        return proto;
    }

    /**
     * JODWorker's configs string.
     *
     * @return the JODComponentWorker's configs string.
     */
    public String getConfigsStr() {
        return configsStr;
    }

}
