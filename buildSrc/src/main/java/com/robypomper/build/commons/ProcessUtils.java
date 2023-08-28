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

package com.robypomper.build.commons;

import org.gradle.api.GradleException;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;


public class ProcessUtils {

    // Enums

    public enum OSs {
        LINUX,
        MAC,
        WIN,
        SOLARIS,
        UNKNOW
    }


    // Constants

    public final static OSs CURRENT_OS = getCurrentOS();
    public final static File NULL_FILE = new File((CURRENT_OS == OSs.WIN ? "NUL" : "/dev/null"));


    // OS

    private static OSs getCurrentOS() {
        String os = System.getProperty("os.name");
        if ((os.contains("nix") || os.contains("nux") || os.contains("aix")))
            return OSs.LINUX;
        if (os.contains("mac"))
            return OSs.MAC;
        if (os.contains("win"))
            return OSs.WIN;
        if (os.contains("sunos"))
            return OSs.SOLARIS;
        return OSs.UNKNOW;
    }


    // Process

    /**
     * true if there is PID file and correspondent process is in OS's process list
     * false if the PID file null
     * false if there is NO PID file
     * false if the pid in PID file not valid or not in OS's process list
     *
     * @return
     */
    public static boolean isProcessRunning(File pidFile) {
        // Check pid file
        if (pidFile == null || !pidFile.exists())
            return false;

        // Read pid from pid file
        long pid = getProcessID(pidFile);

        // Exec command "ps -o pid= -p {pid}"
        Process process;
        BufferedReader reader;
        try {
            process = new ProcessBuilder("ps -o pid= -p %d".split(" ")).start();
            InputStream stdout = process.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stdout));

        } catch (IOException e) {
            throw new GradleException("Can't execute 'ps' command because IOException: " + e.getMessage(), e);
        }

        // Look for pid in "ps" command output
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(Long.toString(pid)))
                    return true;
            }
        } catch (IOException e) {
            throw new GradleException("Can't read 'ps' command's output because IOException: " + e.getMessage(), e);
        }

        return false;
    }

    public static long getProcessID(Process p) {
        long result = -1;

        if (JavaVersionUtils.currentGreaterEqualsThan9()) {
            try {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                result = f.getLong(p);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                assert false;
            }

        } else {

            try {
                System.out.println(p.getClass().getName());
                if (p.getClass().getName().equals("java.lang.Win32Process") ||
                        p.getClass().getName().equals("java.lang.ProcessImpl")) {
                    //for windows
                    try {
                        Field f = p.getClass().getDeclaredField("handle");
                        System.out.println(f);
                        f.setAccessible(true);
                        long handl = f.getLong(p);
                        //Kernel32 kernel = Kernel32.INSTANCE;
                        //WinNT.HANDLE hand = new WinNT.HANDLE();
                        //hand.setPointer(Pointer.createConstant(handl));
                        //result = kernel.GetProcessId(hand);
                        //f.setAccessible(false);
                        throw new GradleException("Can't get process id because Windows version not implemented");

                    } catch (NoSuchFieldException e) {
                        throw new GradleException("Can't get process id because error occurred: " + e.getMessage(), e);
                    }

                } else if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
                    //for unix based operating systems
                    Field f = p.getClass().getDeclaredField("pid");
                    f.setAccessible(true);
                    result = f.getLong(p);
                    f.setAccessible(false);

                } else {
                    // for unknown
                    result = -1;
                }

            } catch (Exception e) {
                throw new GradleException("Can't get process id because error occurred: " + e.getMessage(), e);
            }
        }

        return result;
    }

    /*getProcessID*/
    public static long getProcessID(File pid) {
        if (pid == null)
            throw new GradleException("Can't stop Async task because no pidFile set");
        if (!pid.exists())
            throw new GradleException("Can't stop Async task because pidFile not exist");

        List<String> pidLines;
        try {
            pidLines = Files.readAllLines(pid.toPath());

        } catch (IOException e) {
            throw new GradleException(String.format("Can't stop Async task because error on read pidFile '%s': ", pid) + e.getClass().getSimpleName() + ":" + e.getMessage(), e);
        }
        if (pidLines.isEmpty())
            throw new GradleException(String.format("Can't stop Async task because pidFile '%s' is empty", pid.getAbsolutePath()));

        String pidStr = pidLines.get(0);
        try {
            return Long.parseLong(pidStr);

        } catch (NumberFormatException e) {
            throw new GradleException(String.format("Can't stop Async task because pidFile '%s' does not contain pid number (found '%s')", pid.getAbsolutePath(), pidStr), e);
        }
    }

    public static boolean waitForReadyStr(InputStream stdout, String ready) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains(ready)) {
                    return true;
                }
            }

        } catch (IOException e) {
            throw new GradleException("Can't read command's output because IOException: " + e.getMessage(), e);
        }
        return false;
    }

}
