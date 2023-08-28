/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
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

package com.robypomper.java;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Utils class to execute shell command directly from Java code.
 */
public class JavaExecProcess {

    // Class constants

    private static final String SHELL_BIN_SH = "/bin/sh";
    private static final String SHELL_BIN_BASH = "/bin/bash";
    private static final String SHELL_BIN_WIN_CMD = "cmd";
    private static final String SHELL_BIN_WIN_PS = "powershell";

    private static final String SHELL_ARG_SH = "-c";
    private static final String SHELL_ARG_BASH = "-c";
    private static final String SHELL_ARG_WIN_CMD = "/c";
    private static final String SHELL_ARG_WIN_PS = "-c";

    /**
     * Default shell's bin path.
     */
    private static final String DEF_SHELL_BIN = autoDetectShellBin();
    private static final String DEF_SHELL_ARG = autoDetectShellArg();

    /**
     * Default timeout in ms for cmd execution.
     */
    public static final int DEF_TIMEOUT = 60 * 1000;


    // Exec process


    /**
     * Execute given <code>cmd</code> as a command for {@link #getShellBin()} shell.
     * <p>
     * The <code>cmd</code> string can contain any available command on running
     * machine and his all params. It also support piped command (<code>|</code>)
     * for example <code>echo "hello world" | sed 's/world/john/g'</code>.
     * Redirects (<code>cmd &gt;&gt; out.txt</code>, <code>cmd &gt; out.txt</code>,
     * <code>cmd &amp;&gt; out.txt</code>, <code>cmd &gt; out.txt 2&gt;err.txt</code>)
     * are not supported.
     * <p>
     * Before exit from this method, it checks if the command was terminated.
     * If it's not, then this method throw a {@link ExecStillAliveException}.
     * The command timeout is defined in ms by {@link #DEF_TIMEOUT} constant.
     *
     * @param cmd the command to execute.
     * @return a String containing the command output.
     * @throws IOException             if an I/O error occurs
     * @throws ExecStillAliveException if the command don't terminate before
     *                                 timeout.
     */
    public static String execCmd(String cmd) throws IOException, ExecStillAliveException {
        return execCmd(cmd, DEF_TIMEOUT);
    }

    /**
     * Execute given <code>cmd</code> as a command for {@link #getShellBin()} shell.
     * <p>
     * The <code>cmd</code> string can contain any available command on running
     * machine and his all params. It also support piped command (<code>|</code>)
     * for example <code>echo "hello world" | sed 's/world/john/g'</code>.
     * Redirects (<code>cmd &gt;&gt; out.txt</code>, <code>cmd &gt; out.txt</code>,
     * <code>cmd &amp;&gt; out.txt</code>, <code>cmd &gt; out.txt 2&gt;err.txt</code>)
     * are not supported.
     * <p>
     * Before exit from this method, it checks if the command was terminated.
     * If it's not, then this method throw a {@link ExecStillAliveException}.
     * The command timeout can be set via the <code>timeout</code> param.
     *
     * @param cmd       the command to execute.
     * @param timeoutMs the command's timeout in ms.
     * @return a String containing the command output.
     * @throws IOException             if an I/O error occurs
     * @throws ExecStillAliveException if the command don't terminate before
     *                                 timeout.
     */
    public static String execCmd(String cmd, int timeoutMs) throws IOException, ExecStillAliveException {
        Process process = createAndRunProcess(cmd);
        waitTerminate(process, cmd, timeoutMs);

        String output = readProcessOutput(process);
        if (output.isEmpty())
            output = readProcessError(process);
        return output;
    }


    /**
     * Execute given <code>cmd</code> as a command for {@link #getShellBin()} shell.
     * <p>
     * The <code>cmd</code> string can contain any available command on running
     * machine and his all params. It also support piped command (<code>|</code>)
     * for example <code>echo "hello world" | sed 's/world/john/g'</code>.
     * Redirects (<code>cmd &gt;&gt; out.txt</code>, <code>cmd &gt; out.txt</code>,
     * <code>cmd &amp;&gt; out.txt</code>, <code>cmd &gt; out.txt 2&gt;err.txt</code>)
     * are not supported.
     * <p>
     * Differently from {@link #execCmd} methods, this one exits without waiting
     * for command termination.
     *
     * @param cmd the command to execute.
     * @throws IOException if an I/O error occurs
     */
    public static void execDaemon(String cmd) throws IOException {
        Process process = createAndRunProcess(cmd);
    }

    /**
     * Execute given <code>cmd</code> as a commands concatenation by
     * <code>&amp;&amp;</code>.
     * <p>
     * The <code>cmd</code> concatenated are executed by {@link #execCmd}
     * method. So before executing each cmd, this method wait for previous cmd
     * termination.
     * <p>
     * Each cmd must be terminate before {@link #DEF_TIMEOUT}.
     *
     * @param cmd the commands concatenation to execute.
     * @return a String containing all commands output.
     * @throws ExecConcatException on a single command error or timeout.
     */
    public static String execCmdConcat(String cmd) throws ExecConcatException {
        return execCmdConcat(cmd, DEF_TIMEOUT);
    }

    /**
     * Execute given <code>cmd</code> as a commands concatenation by
     * <code>&amp;&amp;</code>.
     * <p>
     * The <code>cmd</code> concatenated are executed by {@link #execCmd}
     * method. So before executing each cmd, this method wait for previous cmd
     * termination.
     * <p>
     * Each cmd must be terminate before <code>timeoutMs</code>.
     *
     * @param cmd       the commands concatenation to execute.
     * @param timeoutMs the single command's timeout in ms.
     * @return a String containing all commands output.
     * @throws ExecConcatException on a single command error or timeout.
     */
    public static String execCmdConcat(String cmd, int timeoutMs) throws ExecConcatException {
        StringBuilder all = new StringBuilder();

        for (String singleCmd : cmd.split("&&")) {
            all.append("CMD:").append(singleCmd).append("\n");
            try {
                all.append(execCmd(singleCmd, timeoutMs));
            } catch (IOException | ExecStillAliveException e) {
                throw new ExecConcatException(cmd, singleCmd, all.toString(), e);
            }
        }

        return all.toString();
    }


    // Execution utils

    private static Process createAndRunProcess(String cmd) throws IOException {
        ProcessBuilder pBuild = new ProcessBuilder(getShellBin(), getShellArg(), cmd);
        return pBuild.start();
    }

    private static void waitTerminate(Process process, String cmd, int timeoutMs) throws ExecStillAliveException {
        JavaThreads.softSleep(100);
        try {
            process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignore) {
        }
        if (process.isAlive())
            throw new ExecStillAliveException(cmd, timeoutMs);
    }

    private static String readProcessOutput(Process process) throws IOException {
        return readProcessStream(process, process.getInputStream());
    }

    private static String readProcessError(Process process) throws IOException {
        return readProcessStream(process, process.getErrorStream());
    }

    private static String readProcessStream(Process process, InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line).append("\n");

        return sb.toString();
    }


    // Configs

    private static String shellBin = DEF_SHELL_BIN;
    private static String shellArg = DEF_SHELL_ARG;

    /**
     * This method allow to change default shell binary.
     *
     * @param bin the full path of shell binary file.
     * @throws FileNotFoundException if given path refer to a not existing file.
     */
    public static void setShellBin(String bin) throws FileNotFoundException {
        if (new File(bin).exists())
            shellBin = bin;
        else
            throw new FileNotFoundException(String.format("Shell's binaries doesn't exist. File not found %s", bin));
    }

    /**
     * @return current shell bin path.
     */
    public static String getShellBin() {
        return shellBin;
    }

    /**
     * This method allow to change default shell argument.
     *
     * @param arg the first argument for the shell to run a command (pe: '-c' or '\\c').
     */
    public static void setShellArg(String arg) {
        shellArg = arg;
    }

    /**
     * @return current shell argument.
     */
    public static String getShellArg() {
        return shellArg;
    }


    // Shell detection methods

    private static String autoDetectShellBin() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows"))
            return SHELL_BIN_WIN_PS;
        else
            return SHELL_BIN_BASH;
    }

    private static String autoDetectShellArg() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows"))
            return SHELL_ARG_WIN_PS;
        else
            return SHELL_ARG_BASH;
    }


    // Conversion methods used by exec process

    private static String[] splitCmd(String cmd) {
        String[] cmdParts = cmd.trim().split(" ");
        List<String> cmdSplitted = new ArrayList<>();
        for (int i = 0; i < cmdParts.length; i++) {
            String part = cmdParts[i];

            if (part.startsWith("'")) {
                if (part.endsWith("'")) {
                    cmdSplitted.add(part.substring(1, part.length() - 1));
                    continue;
                }

                StringBuilder partComplete = new StringBuilder(part);
                for (int k = i + 1; k < cmdParts.length; k++) {
                    partComplete.append(" ").append(cmdParts[k]);
                    if (cmdParts[k].endsWith("'")) {
                        cmdSplitted.add(partComplete.substring(1, partComplete.length() - 1));
                        i = k + 1;
                        break;
                    }
                }
                continue;
            }

            cmdSplitted.add(part);
        }
        return cmdSplitted.toArray(new String[0]);
    }


    // Exceptions

    /**
     * Exceptions thrown on execution timeout.
     */
    public static class ExecStillAliveException extends Throwable {

        private static final String MSG = "Timeout of %d milliseconds reached on cmd '%s'";

        public ExecStillAliveException(String cmd, int timeout) {
            super(String.format(MSG, timeout, cmd));
        }

    }

    /**
     * Exceptions thrown on concatenated commands error.
     */
    public static class ExecConcatException extends Throwable {

        private static final String MSG = "Error executing '%s' from concat command '%s'";

        private final String provisionalOutput;

        public ExecConcatException(String cmd, String singleCmd, String provisionalOutput, Throwable e) {
            super(String.format(MSG, singleCmd, cmd), e);
            this.provisionalOutput = provisionalOutput;
        }

        public String getProvisionalOutput() {
            return provisionalOutput;
        }
    }

}
