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

package com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.apis.executable;


/**
 * JCP JSL Web Bridge - Admin / APIs / Executable 2.0
 */
public class Params20 extends com.robypomper.josp.defs.admin.apis.executable.Params20 {

    // JCP APIs Executable methods

    public static class Index {

        public final String urlOnline = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_ONLINE;
        public final String urlProcess = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_PROCESS;
        public final String urlJava = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA;
        public final String urlOS = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_OS;
        public final String urlCPU = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_CPU;
        public final String urlMemory = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_MEMORY;
        public final String urlDisks = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_DISKS;
        public final String urlNetworks = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_NETWORKS;

    }

    public static class JavaIndex {

        public final String urlVM = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_VM;
        public final String urlRuntime = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_RUNTIME;
        public final String urlTimes = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_TIMES;
        public final String urlClasses = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_CLASSES;
        public final String urlMemory = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_MEMORY;
        public final String urlThreads = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_THREADS;

    }

}
