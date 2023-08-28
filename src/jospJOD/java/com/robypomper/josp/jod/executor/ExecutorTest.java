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
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_JOD;


/**
 * JOD Executor test.
 * <p>
 * This executor action print a log message and return <code>true</code>.
 * <p>
 * The action's method chain (exec(), subExec(), don't use any params because
 * was not yet implemented.
 */
public class ExecutorTest extends AbsJODExecutor implements JODBooleanAction.JOSPBoolean.Executor, JODRangeAction.JOSPRange.Executor {


    // Constructor

    /**
     * Default ExecutorTest constructor.
     *
     * @param name       name of the executor.
     * @param proto      proto of the executor.
     * @param configsStr configs string, can be an empty string.
     */
    public ExecutorTest(String name, String proto, String configsStr, JODComponent component) {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorTest for component '%s' init with config string '%s://%s'", getName(), proto, configsStr));
    }


    // Mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exec(JOSPProtocol.ActionCmd commandAction, JODBooleanAction.JOSPBoolean cmdAction) {
        System.out.printf("\n\nReceived action command from %s::%s (srv::usr) for %s::%s (obj::component)%n", commandAction.getServiceId(), commandAction.getUserId(), commandAction.getObjectId(), commandAction.getComponentPath());
        System.out.printf("\tnewState %b%n", cmdAction.newState);
        System.out.printf("\toldState %b%n", cmdAction.oldState);
        return true;
    }

    @Override
    public boolean exec(JOSPProtocol.ActionCmd commandAction, JODRangeAction.JOSPRange cmdAction) {
        System.out.printf("\n\nReceived action command from %s::%s (srv::usr) for %s::%s (obj::component)%n", commandAction.getServiceId(), commandAction.getUserId(), commandAction.getObjectId(), commandAction.getComponentPath());
        System.out.printf("\tnewState %f%n", cmdAction.newState);
        System.out.printf("\toldState %f%n", cmdAction.oldState);
        return true;
    }

}
