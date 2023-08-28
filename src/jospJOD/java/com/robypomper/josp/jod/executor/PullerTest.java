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
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.pillars.JODBooleanState;
import com.robypomper.josp.jod.structure.pillars.JODRangeState;
import com.robypomper.log.Mrk_JOD;


/**
 * JOD Puller test.
 * <p>
 * Each time the {@link #pull()} method is called, it print a log message and call
 * the {@link JODState} sub class's <code>setUpdate(...)</code> method.
 */
public class PullerTest extends AbsJODPuller {

    // Constructor

    /**
     * Default PullerTest constructor.
     *
     * @param name       name of the puller.
     * @param proto      proto of the puller.
     * @param configsStr configs string, can be an empty string.
     */
    public PullerTest(String name, String proto, String configsStr, JODComponent component) {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerTest for component '%s' init with config string '%s://%s'.", getName(), proto, configsStr));
    }


    // Mngm

    /**
     * Pull method: print a log message and call the {@link JODState} sub
     * class's <code>setUpdate(...)</code> method.
     */
    @Override
    public void pull() {
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerTest '%s' of proto '%s' pulling", getName(), getProto()));

        String state = "";
        if (getComponent() instanceof JODBooleanState)
            state = "true";
        else if (getComponent() instanceof JODRangeState)
            state = "5";

        if (!convertAndSetStatus(state))
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerShell for component '%s' can't update his component because not supported (%s)", getName(), getComponent().getClass().getSimpleName()));
        //// For each JODState supported
        //if (getComponent() instanceof JODBooleanState)
        //    ((JODBooleanState) getComponent()).setUpdate(true);
        //else if (getComponent() instanceof JODRangeState)
        //    ((JODRangeState) getComponent()).setUpdate(5);

    }

}
