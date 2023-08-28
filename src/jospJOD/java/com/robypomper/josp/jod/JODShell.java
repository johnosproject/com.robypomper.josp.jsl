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

package com.robypomper.josp.jod;

import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import com.robypomper.josp.jod.info.JODInfo;
import com.robypomper.josp.jod.shell.*;
import com.robypomper.josp.states.JODState;
import com.robypomper.josp.states.StateException;
import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * Runnable class for JOD Shell.
 * <p>
 * For more details on accepted cmdLine params check the method {@link #createArgsParser()}.
 */
public class JODShell {

    // Internal vars

    private JOD jod;
    private Shell shell;
    private boolean fatalThrown = false;


    // CmdLine Args

    private static final String ARG_CONFIGS_FILE = "configs";
    private static final String ARG_CONFIGS_FILE_SHORT = "c";
    private static final String ARG_CONFIGS_FILE_DESCR = "specify JOD config file path (default: jod.yml)";
    private static final String ARGS_DEF_CONFIGS_FILE = "configs/jod.yml";


    // Exit codes

    private static final int EXIT_OK = 0;
    private static final int EXIT_ERROR_CONFIG = -1;
    private static final int EXIT_ERROR_START = -2;
    private static final int EXIT_ERROR_STOP = -3;
    private static final int EXIT_ERROR_SHELL = -4;


    // Main method

    /**
     * Initialize a Shell object and execute it.
     * <p>
     * Each operation is wrapped in a <code>try...catch</code> block that manage
     * and print application exceptions.
     *
     * @param args cmdLine args, for more info check the method
     *             {@link #createArgsParser()}.
     */
    public static void main(String[] args) {
        JODShell shell = new JODShell();

        // Get cmdLine args
        Options options = createArgsParser();
        CommandLine parsedArgs = parseArgs(options, args);
        String configsFile = parsedArgs.getOptionValue(ARG_CONFIGS_FILE, ARGS_DEF_CONFIGS_FILE);

        // Initialize JOD
        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        System.out.println("INF: Load JOD Obj.");
        try {
            JOD.Settings settings = FactoryJOD.loadSettings(configsFile);
            shell.createJOD(settings);
        } catch (JODShell.Exception | JOD.FactoryException e) {
            shell.fatal(e, EXIT_ERROR_CONFIG);
            return;
        }

        // Start JOD
        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        System.out.println("INF: Start JOD Obj.");
        try {
            shell.startJOD();
        } catch (JODShell.Exception | StateException e) {
            shell.fatal(e, EXIT_ERROR_START);
            return;
        }

        // Run interactive shell
        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        System.out.println("INF: Run JOD Shell.");
        try {
            shell.startShell(shell.jod.getObjectInfo().getObjName());
        } catch (IOException e) {
            shell.fatal(e, EXIT_ERROR_SHELL);
            return;
        }

        // Stop JOD
        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        if (shell.jod.getState() != JODState.STOP) {
            System.out.println("INF: Stop JOD Obj.");
            try {
                shell.stopJOD();
            } catch (JODShell.Exception | StateException e) {
                shell.fatal(e, EXIT_ERROR_STOP);
                return;
            }
        } else {
            System.out.println("INF: JOD Obj already stopped.");
        }

        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        System.out.println("INF: EXIT");
        System.exit(EXIT_OK);
    }

    /**
     * Manage fatal errors, also recursively.
     *
     * @param e        exception that thrown error.
     * @param exitCode exit code assigned to the error.
     */
    private void fatal(Throwable e, int exitCode) {
        boolean firstFatal = !fatalThrown;
        fatalThrown = true;

        String msg;
        switch (exitCode) {
            case EXIT_ERROR_CONFIG:
                msg = String.format("Can't load JOD Object because configuration error: '%s'", e.getMessage());
                break;
            case EXIT_ERROR_START:
                msg = String.format("Can't start JOD Object because startup error: '%s'", e.getMessage());
                break;
            case EXIT_ERROR_STOP:
                msg = String.format("Error on stopping JOD Object because: '%s'", e.getMessage());
                break;
            case EXIT_ERROR_SHELL:
                msg = String.format("Error on JOD Shell execution: '%s'", e.getMessage());
                break;
            default:
                msg = "Unknown error on JOD Shell";
                break;
        }


        System.out.println("FAT: " + msg);
        System.err.println();
        System.err.println("######################################");
        e.printStackTrace();
        System.err.println("######################################");
        System.err.println();

        if (firstFatal)
            System.exit(exitCode);
    }


    // JOD mngm

    /**
     * Create new instance of the JOD object.
     *
     * @param settings settings used by the JOD object returned.
     */
    public void createJOD(JOD.Settings settings) throws JODShell.Exception, JOD.FactoryException {
        if (jod != null)
            throw new JODShell.Exception("Can't initialize JOD object twice.");

        jod = FactoryJOD.createJOD(settings);
    }

    /**
     * Start internal JOD object and all his sub-systems.
     */
    public void startJOD() throws JODShell.Exception, StateException {
        if (jod == null)
            throw new JODShell.Exception("Can't start JOD object because was not initialized.");

        jod.startup();
    }

    /**
     * Stop internal JOD object and all his sub-systems.
     */
    public void stopJOD() throws JODShell.Exception, StateException {
        if (jod == null)
            throw new JODShell.Exception("Can't stop JOD object because was not initialized.");

        if (jod.getState() == JODState.STOP || jod.getState() == JODState.RESTARTING)
            throw new JODShell.Exception("Can't stop JOD object because was is already stopped or it's shouting down.");

        jod.shutdown();
    }


    // Shell mngm

    /**
     * Start interactive shell.
     */
    public void startShell(String objName) throws IOException {
        shell = ShellFactory.createConsoleShell(JODInfo.APP_NAME + "-" + objName, JODInfo.APP_NAME_FULL,
                this,
                new CmdsShell(shell),
                new CmdsJOD(jod),
                new CmdsJCPClient(jod.getJCPClient()),
                new CmdsJODObjectInfo(jod.getObjectInfo()),
                new CmdsJODExecutorMngr(jod.getObjectStructure(), jod.getExecutor(), jod.getHistory()),
                new CmdsJODStructure(jod.getObjectStructure()),
                new CmdsJODPermissions(jod.getObjectInfo(), jod.getPermission()),
                new CmdsJODCommunication(jod.getCommunication())
        );
        shell.commandLoop();
    }


    // CmdLine args parser

    /**
     * Shell application accept following cmdLine args:
     * <ul>
     *     <li>
     *         {@value #ARG_CONFIGS_FILE} ({@value #ARG_CONFIGS_FILE_SHORT}):
     *         {@value #ARG_CONFIGS_FILE_DESCR}
     *     </li>
     * </ul>
     *
     * @return {@link Options} object to use for args parsing.
     */
    private static Options createArgsParser() {
        Options options = new Options();

        Option configsFile = new Option(ARG_CONFIGS_FILE_SHORT, ARG_CONFIGS_FILE, true, ARG_CONFIGS_FILE_DESCR);
        configsFile.setRequired(false);
        configsFile.setType(String.class);
        options.addOption(configsFile);

        return options;
    }

    /**
     * Parse <code>args</code> with <code>options</code>.
     *
     * @param options object to use for args parsing.
     * @param args    args to be parsed.
     * @return {@link CommandLine} object to use to get cmdLine args.
     */
    private static CommandLine parseArgs(Options options, String[] args) {
        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            new HelpFormatter().printHelp(JODInfo.APP_NAME, options);
            System.exit(1);
            return null;
        }
        return cmd;
    }


    // Exceptions

    /**
     * Exception thrown during JODShell methods execution.
     */
    public static class Exception extends Throwable {
        public Exception(String msg) {
            super(msg);
        }

        public Exception(String msg, Exception e) {
            super(msg, e);
        }
    }
}
