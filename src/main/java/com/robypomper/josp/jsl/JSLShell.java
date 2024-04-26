/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2024 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.robypomper.josp.jsl;

import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import com.robypomper.josp.jsl.info.JSLInfo;
import com.robypomper.josp.jsl.shell.*;
import com.robypomper.josp.states.JSLState;
import com.robypomper.josp.states.StateException;
import org.apache.commons.cli.*;

import java.io.IOException;


/**
 * Runnable class for JSL Shell.
 * <p>
 * For more details on accepted cmdLine params check the method {@link #createArgsParser()}.
 */
public class JSLShell {

    // Internal vars

    private JSL jsl;
    private Shell shell;
    private boolean fatalThrown = false;


    // CmdLine Args

    private static final String ARG_CONFIGS_FILE = "configs";
    private static final String ARG_CONFIGS_FILE_SHORT = "c";
    private static final String ARG_CONFIGS_FILE_DESCR = "specify JSL config file path (default: jsl.yml)";
    private static final String ARGS_DEF_CONFIGS_FILE = "jsl.yml";
    private static final String ARG_JSL_VERSION = "jsl-version";
    private static final String ARG_JSL_VERSION_SHORT = "v";
    private static final String ARG_JSL_VERSION_DESCR = "specify which JSL version to use (default: jsl.version property from JSL config file)";


    // Exit codes

    private static final int EXIT_OK = 0;
    private static final int EXIT_ERROR_CONFIG = -1;
    private static final int EXIT_ERROR_STARTUP = -2;
    private static final int EXIT_ERROR_SHUTDOWN = -3;
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
        JSLShell shell = new JSLShell();

        // Get cmdLine args
        Options options = createArgsParser();
        CommandLine parsedArgs = parseArgs(options, args);
        String configsFile = parsedArgs.getOptionValue(ARG_CONFIGS_FILE, ARGS_DEF_CONFIGS_FILE);
        String jslVer = parsedArgs.getOptionValue(ARG_JSL_VERSION, "");

        // Initialize JSL
        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        System.out.println("INF: Init JSL Lib.");
        try {
            JSL.Settings settings = FactoryJSL.loadSettings(configsFile, jslVer);
            shell.createJSL(settings, jslVer);
        } catch (Exception | JSL.FactoryException e) {
            shell.fatal(e, EXIT_ERROR_CONFIG);
            return;
        }

        // Startup JSL
        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        System.out.println("INF: Startup JSL Lib.");
        try {
            shell.startupJSL();
        } catch (Exception | StateException e) {
            shell.fatal(e, EXIT_ERROR_STARTUP);
            return;
        }

        // Run interactive shell
        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        System.out.println("INF: Run JSL Shell.");
        try {
            shell.startShell(shell.jsl.getServiceInfo().getSrvName());
        } catch (IOException e) {
            shell.fatal(e, EXIT_ERROR_SHELL);
            return;
        }

        // Shutdown JSL
        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        if (shell.jsl.getState() != JSLState.STOP) {
            System.out.println("INF: Shutdown JSL Lib.");
            try {
                shell.shutdownJSL();
            } catch (Exception | StateException e) {
                shell.fatal(e, EXIT_ERROR_SHUTDOWN);
                return;
            }
        } else {
            System.out.println("INF: JSL Lib already disconnected.");
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
                msg = String.format("Can't load JSL Object because configuration error: '%s'", e.getMessage());
                break;
            case EXIT_ERROR_STARTUP:
                msg = String.format("Can't startup JSL Object because startup error: '%s'", e.getMessage());
                break;
            case EXIT_ERROR_SHUTDOWN:
                msg = String.format("Error on shutdown JSL Object because: '%s'", e.getMessage());
                break;
            case EXIT_ERROR_SHELL:
                msg = String.format("Error on JSL Shell execution: '%s'", e.getMessage());
                break;
            default:
                msg = "Unknown error on JSL Shell";
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


    // JSL mngm

    /**
     * Create new instance of the JSL library.
     *
     * @param settings settings used by the JSL object returned.
     * @param jslVer   used to force JSL Object version to create, if empty latest
     *                 version is used.
     */
    public void createJSL(JSL.Settings settings, String jslVer) throws JSLShell.Exception, JSL.FactoryException {
        if (jsl != null)
            throw new JSLShell.Exception("Can't initialize JSL object twice.");

        jsl = FactoryJSL.createJSL(settings, jslVer);
    }

    /**
     * Connect internal JSL library and start all his sub-systems.
     */
    public void startupJSL() throws JSLShell.Exception, StateException {
        if (jsl == null)
            throw new JSLShell.Exception("Can't connect JSL object because was not initialized.");

        jsl.startup();
    }

    /**
     * Disconnect internal JSL library and stops all his sub-systems.
     */
    public void shutdownJSL() throws JSLShell.Exception, StateException {
        if (jsl == null)
            throw new JSLShell.Exception("Can't disconnect JSL object because was not initialized.");

        jsl.shutdown();
    }


    // Shell mngm

    /**
     * Start interactive shell.
     */
    public void startShell(String objName) throws IOException {
        shell = ShellFactory.createConsoleShell(JSLInfo.APP_NAME + "-" + objName, JSLInfo.APP_NAME_FULL,
                this,
                new CmdsShell(shell),
                new CmdsJSL(jsl),
                new CmdsJCPClient(jsl.getJCPClient()),
                new CmdsJSLServiceInfo(jsl.getServiceInfo()),
                new CmdsJSLUserMngr(jsl.getUserMngr()),
                new CmdsJSLAdmin(jsl.getAdmin()),
                new CmdsJSLCommunication(jsl.getCommunication()),
                new CmdsJSLObjsMngr(jsl.getObjsMngr())
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
     *     <li>
     *         {@value #ARG_JSL_VERSION} ({@value #ARG_JSL_VERSION_SHORT}):
     *         {@value #ARG_JSL_VERSION_DESCR}
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

        Option jslVer = new Option(ARG_JSL_VERSION_SHORT, ARG_JSL_VERSION, true, ARG_JSL_VERSION_DESCR);
        jslVer.setRequired(false);
        jslVer.setType(String.class);
        options.addOption(jslVer);

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
            new HelpFormatter().printHelp(JSLInfo.APP_NAME, options);
            System.exit(1);
            return null;
        }
        return cmd;
    }


    // Exceptions

    /**
     * Exception thrown during JSLShell methods execution.
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
