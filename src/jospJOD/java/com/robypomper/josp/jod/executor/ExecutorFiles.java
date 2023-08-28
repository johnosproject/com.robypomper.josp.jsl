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


import com.robypomper.java.JavaFiles;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_Commons;
import com.robypomper.log.Mrk_JOD;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * JOD Puller for Files.
 * <p>
 * Each time this executor must execute an action command, it write given value on specified file.
 */
public class ExecutorFiles extends AbsJODExecutor implements JODBooleanAction.JOSPBoolean.Executor, JODRangeAction.JOSPRange.Executor {

    // Class constants

    private static final String PROP_FILE_PATH = "path";
    private static final String PROP_DEF_FORMAT = "format";


    // Internal vars

    /**
     * Path for local file to monitoring.
     * <p>
     * In configs it can be defined using the component's substitution vars.
     */
    private final String filePath;
    /**
     * Specify in witch format the component state's value must be written on file.
     * <p>
     * It can be set as Substitution ACTION_xy value. Default Substitutions.ACTION_VAL.
     */
    private final String actionStrFormat;


    // Constructor

    /**
     * Default ExecutorFile constructor.
     *
     * @param name       name of the executor.
     * @param proto      proto of the executor.
     * @param configsStr configs string, can be an empty string.
     */
    public ExecutorFiles(String name, String proto, String configsStr, JODComponent component) throws MissingPropertyException {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorFiles for component '%s' init with config string '%s://%s'", getName(), proto, configsStr));

        Map<String, String> configs = splitConfigsStrings(configsStr);
        actionStrFormat = parseConfigString(configs, PROP_DEF_FORMAT, Substitutions.ACTION_VAL);
        filePath = parseConfigString(configs, PROP_FILE_PATH);

        try {
            JavaFiles.createParentIfNotExist(filePath);

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' file '%s' not exist and can't create watcher file", getName(), new File(filePath).getAbsolutePath()), e);
        }
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

        String actionStr = new Substitutions(actionStrFormat)
                //.substituteObject(jod.getObjectInfo())
                //.substituteObjectConfigs(jod.getObjectInfo())
                .substituteComponent(getComponent())
                .substituteState((JODState)getComponent())
                .substituteAction(commandAction)
                .toString();
        log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Write ExecutorFiles state '%s' on '%s' file", actionStr, filePath));

        try {
            JavaFiles.writeString(filePath, actionStr);

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorFiles error on writing file '%s' for component '%s'", filePath, getName()));
            return false;
        }

        ((JODState) getComponent()).forceCheckState();

        return true;
    }


    @Override
    public boolean exec(JOSPProtocol.ActionCmd commandAction, JODRangeAction.JOSPRange cmdAction) {
        System.out.printf("\n\nReceived action command from %s::%s (srv::usr) for %s::%s (obj::component)%n", commandAction.getServiceId(), commandAction.getUserId(), commandAction.getObjectId(), commandAction.getComponentPath());
        System.out.printf("\tnewState %f%n", cmdAction.newState);
        System.out.printf("\toldState %f%n", cmdAction.oldState);


        String actionStr = new Substitutions(actionStrFormat)
                .substituteAction(commandAction)
                .toString();
        log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Write ExecutorFiles state '%s' on '%s' file", actionStr, filePath));

        if (actionStr != null)
            try {
                JavaFiles.writeString(filePath, actionStr);

            } catch (IOException e) {
                log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorFiles error on writing file '%s' for component '%s'", filePath, getName()));
                return false;
            }

        ((JODState) getComponent()).forceCheckState();

        return true;
    }

}
