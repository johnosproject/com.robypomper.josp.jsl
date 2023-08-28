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


import com.robypomper.java.JavaExecProcess;
import com.robypomper.java.JavaFiles;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_Commons;
import com.robypomper.log.Mrk_JOD;

import java.io.IOException;
import java.util.Map;


/**
 * JOD Puller for shell commands.
 * <p>
 * Each time this executor must execute an action command, it executes
 * corresponding shell command.
 * <p>
 * This class can execute shell command on different operating system, because
 * it uses the {@link JavaExecProcess} class to run shell commands.
 */
public class ExecutorShell extends AbsJODExecutor implements JODBooleanAction.JOSPBoolean.Executor, JODRangeAction.JOSPRange.Executor {

    // Class constants

    private static final String PROP_CMD = "cmd";
    private static final String PROP_REDIRECT = "redirect";
    private static final int CMD_EXECUTION_TIMEOUT_MS = 5000;


    // Internal vars

    private final String cmd;
    private final String redirect;


    // Constructor

    /**
     * Default ExecutorShell constructor.
     *
     * @param name       name of the executor.
     * @param proto      proto of the executor.
     * @param configsStr configs string, can be an empty string.
     */
    public ExecutorShell(String name, String proto, String configsStr, JODComponent component) throws MissingPropertyException {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorShell for component '%s' init with config string '%s://%s'", getName(), proto, configsStr));

        Map<String, String> configs = splitConfigsStrings(configsStr);
        cmd = parseConfigString(configs, PROP_CMD);
        redirect = parseConfigString(configs, PROP_REDIRECT, "");
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

        String cmdUpd = new Substitutions(cmd)
                //.substituteObject(jod.getObjectInfo())
                //.substituteObjectConfigs(jod.getObjectInfo())
                .substituteComponent(getComponent())
                .substituteState((JODState)getComponent())
                .substituteAction(commandAction)
                .toString();
        String redirectUpd = redirect.isEmpty() ? null :
                new Substitutions(redirect)
                        //.substituteObject(jod.getObjectInfo())
                        //.substituteObjectConfigs(jod.getObjectInfo())
                        .substituteComponent(getComponent())
                        .substituteState((JODState)getComponent())
                        .substituteAction(commandAction)
                        .toString();

        log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Exec ExecutorShell cmd '%s'", cmdUpd));

        // Split, redirect*redirectUpd!=null, CmdPartitioning
        try {
            String output = JavaExecProcess.execCmdConcat(cmdUpd, CMD_EXECUTION_TIMEOUT_MS);
            if (redirectUpd != null) {
                StringBuilder writableOutput = new StringBuilder();
                for (String s : output.split("\n"))
                    if (!s.startsWith("CMD"))
                        writableOutput.append(s).append("\n");
                JavaFiles.writeString(redirectUpd, writableOutput.toString());
            }

        } catch (JavaExecProcess.ExecConcatException e) {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("ExecutorShell error on executing partial cmd '%s' for component '%s' because %s", cmdUpd, getName(), e.getMessage()), e);
            return false;

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("ExecutorShell error on writing output to '%s' file of partial cmd '%s' for component '%s' because %s", redirectUpd, cmdUpd, getName(), e.getMessage()), e);
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

        String cmdUpd = new Substitutions(cmd)
                .substituteAction(commandAction)
                .toString();
        String redirectUpd = redirect.isEmpty() ? null :
                new Substitutions(redirect)
                        .substituteAction(commandAction)
                        .toString();

        log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Exec ExecutorShell cmd '%s'", cmdUpd));

        // Split, redirect*redirectUpd!=null, CmdPartitioning
        try {
            String output = JavaExecProcess.execCmdConcat(cmdUpd, CMD_EXECUTION_TIMEOUT_MS);
            if (redirectUpd != null)
                JavaFiles.writeString(redirectUpd, output);

        } catch (JavaExecProcess.ExecConcatException e) {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("ExecutorShell error on executing partial cmd '%s' for component '%s' because %s", cmdUpd, getName(), e.getMessage()), e);
            return false;

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("ExecutorShell error on writing output to '%s' file of partial cmd '%s' for component '%s' because %s", redirectUpd, cmdUpd, getName(), e.getMessage()), e);
            return false;
        }

        ((JODState) getComponent()).forceCheckState();

        return true;
    }

}
