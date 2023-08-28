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

package com.robypomper.build.gradle;

import com.robypomper.build.commons.ProcessUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskAction;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Add tasks to run java application as a AsyncTask.
 */
public class GradleAsyncTasksUtils {

    // StartAsync

    static public class StartAsync extends DefaultTask {

        public String command;                             // cmd to execute
        public String ready;                               // log line printed on successfully startup
        public File workingDir;                            // working dir, or null for project's dir
        public File logFile = null;                        // file to write logs
        public boolean addLogFileStartTime = false;        // if true, add startup timestamp to log file
        public File pidFile = null;                        // file to write process pid
        public boolean suppressConsoleOutputs = false;     // to deprecate?
        public boolean allowMultipleInstance = false;      // if true, multiple instance can be executed simultaneously (it break the StopAsync task)
        public boolean throwOnError = true;                // if true, each error throw an exception, otherwise the task exits

        @TaskAction
        void spawnProcess() {
            // Check "runnablity"
            if (ProcessUtils.isProcessRunning(pidFile) && !allowMultipleInstance)
                if (throwOnError) {
                    throw new GradleException("Task is already running and no multiple instance allowed");
                } else {
                    log("WAR: Task is already running and no multiple instance allowed");
                    return;
                }

            ProcessBuilder builder = new ProcessBuilder(command.split(" "));
            builder.environment().put("JAVA_HOME", System.getProperty("java.home"));

            // Setup process's WorkingDir
            if (workingDir==null)
                workingDir = this.getProject().getProjectDir();
            else if (!workingDir.exists())
                workingDir.mkdirs();
            if (workingDir != null)
                builder.directory(workingDir);

            // Setup process's outputs
            builder.redirectErrorStream(true);
            if (suppressConsoleOutputs) {
                builder.redirectOutput(ProcessUtils.NULL_FILE);
                log("DEB: Process's logs hidden");
            }
            if (logFile!=null) {
                if (addLogFileStartTime)
                    logFile = new File(logFile.getAbsolutePath() + "_" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()));
                if (!logFile.exists())
                    logFile.getParentFile().mkdirs();
                builder.redirectOutput(logFile);
                log("DEB: Process's logs on file");
            }

            // Run process
            Process process = null;
            try {
                process = builder.start();
            } catch (IOException e) {
                if (throwOnError)
                    throw new GradleException("Task cannot be executed because IOError: " + e.getMessage(),e);
                else {
                    log(String.format("WAR: Task cannot be executed because IOError on executing task '%s': ", command) + e.getMessage());
                    return;
                }
            }
            log("DEB: Process started");

            // Get pid
            long processID = ProcessUtils.getProcessID(process);

            // Print process info
            log("+ -------------------------");
            log("| Process executed         " + command);
            log("+ -------------------------");
            log("| Process id:              " + processID);
            log("| Logs on file:            " + logFile.getAbsolutePath());
            if (pidFile!=null)
                log("| PID on file:             " + pidFile.getAbsolutePath());
            log("| Process's working dir:   " + builder.directory().getAbsolutePath());
            log("+ -------------------------");

            // PID file
            if (pidFile!=null) {
                if (!pidFile.exists())
                    pidFile.getParentFile().mkdirs();
                try {
                    FileOutputStream fos = new FileOutputStream(pidFile);
                    DataOutputStream dos = new DataOutputStream(fos);
                    dos.write(Long.toString(processID).getBytes());
                    dos.close();

                } catch (IOException e) {
                    if (throwOnError)
                        throw new GradleException("Task pid cannot write to pid file because IOException: " + e.getMessage(),e);
                    else {
                        log(String.format("WAR: Task '%s' pid cannot write to pid file because IOException: ", command) + e.getMessage());
                        return;
                    }
                }
                log(String.format("DEB: Process's PID stored on '%s' file", pidFile));
            }

            // Checks 'command is ready'
            if (suppressConsoleOutputs) {
                log("DEB: Task started");
                return;
            }

            InputStream stdout;
            if (logFile!=null) {
                try {
                    stdout = new FileInputStream(logFile);
                } catch (FileNotFoundException retry) {
                    logFile.getParentFile().mkdirs();
                    try {
                        stdout = new FileInputStream(logFile);
                    } catch (FileNotFoundException e) {
                        if (throwOnError)
                            throw new GradleException("Task output cannot be redirect to log file because FileNotFoundException: " + e.getMessage(),e);
                        else {
                            log(String.format("WAR: Task '%s' output cannot be redirect to log file because FileNotFoundException: ", command) + e.getMessage());
                            return;
                        }
                    }
                }

            } else
                stdout = process.getInputStream();


            try {
                ProcessUtils.waitForReadyStr(stdout, ready);

            } catch (GradleException e) {
                if (throwOnError)
                    throw new GradleException("Task output cannot be parsed to checks 'command is ready' because IOException: " + e.getMessage(),e);
                else {
                    log(String.format("WAR: Task '%s' output cannot be parsed to checks 'command is ready' because IOException: ", command) + e.getMessage());
                    return;
                }
            }

            log("DEB: Task started");
        }

        public boolean isRunning() {
            return ProcessUtils.isProcessRunning(pidFile);
        }

    }


    // StopAsync

    static public class StopAsync extends DefaultTask {

        public File pidFile = null;                        // file to read process pid
        public boolean throwOnError = true;                // if true, each error throw an exception, otherwise the task exits

        @TaskAction
        void killProcess() {

            long pid;
            try {
                pid = ProcessUtils.getProcessID(pidFile);

            } catch (GradleException e) {
                if (throwOnError)
                    throw new GradleException("Task pid can't be read because: " + e.getMessage(),e);
                else {
                    log("WAR: Task pid can't be read because: : " + e.getMessage());
                    return;
                }
            }

            // Build and run KILL process
            ProcessBuilder builder = new ProcessBuilder(String.format("kill %s", pid).split(" "));
            try {
                builder.start();
            } catch (IOException e) {
                if (throwOnError)
                    throw new GradleException("Task pid can't be read because Can't execute 'ps' command: " + e.getMessage(),e);
                else {
                    log("WAR: Task pid can't be read because Can't execute 'ps' command: : " + e.getMessage());
                    return;
                }
            }

            // PID file
            pidFile.delete();
        }

    }


    // Utils methods

    static public void waitAfter(Task task, int seconds, Task secondTask) {
        task.doLast(it -> {
            if (seconds>0)
                log(String.format("DEB: Wait %d seconds before executing next task...", seconds));
            try {
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException ignore) {
                log("WAR: Task's waitAfter interrupted, continue");}
        });
        secondTask.mustRunAfter(task);
    }

    static public void log(String msg) {
        System.out.println(msg);
    }

}
