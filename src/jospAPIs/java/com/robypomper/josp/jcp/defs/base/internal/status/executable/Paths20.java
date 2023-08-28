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

package com.robypomper.josp.jcp.defs.base.internal.status.executable;


/**
 * JCP All - Status / Executable 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "Methods to query JCP service executable status";


    // API Params

    //@formatter:off
    public static final String PARAM_THREAD     = "thread_id";
    public static final String PARAM_DISK       = "disk_id";
    public static final String PARAM_NTWK       = "ntwk_id";

    public static final String PARAM_URL_THREAD     = "{" + PARAM_THREAD + "}";
    public static final String PARAM_URL_DISK       = "{" + PARAM_DISK + "}";
    public static final String PARAM_URL_NTWK       = "{" + PARAM_NTWK + "}";
    //@formatter:on


    // API Methods

    //@formatter:off
    // Executable status methods
    // Index
    public static final String MTHD_EXEC                     = "";
    // Online
    public static final String MTHD_EXEC_ONLINE              = "online/";
    // Process
    public static final String MTHD_EXEC_PROCESS             = "process/";
    // Java
    public static final String MTHD_EXEC_JAVA                = "java/";
    public static final String MTHD_EXEC_JAVA_VM             = "java/vm/";
    public static final String MTHD_EXEC_JAVA_RUNTIME        = "java/runtime/";
    public static final String MTHD_EXEC_JAVA_TIMES          = "java/times/";
    public static final String MTHD_EXEC_JAVA_CLASSES        = "java/classes/";
    public static final String MTHD_EXEC_JAVA_MEMORY         = "java/memory/";
    public static final String MTHD_EXEC_JAVA_THREADS        = "java/threads/";
    public static final String MTHD_EXEC_JAVA_THREAD         = "java/threads/" + PARAM_URL_THREAD + "/";
    // OS
    public static final String MTHD_EXEC_OS                  = "os/";
    // CPU
    public static final String MTHD_EXEC_CPU                 = "cpu/";
    // Memory
    public static final String MTHD_EXEC_MEMORY              = "memory/";
    // Disks
    public static final String MTHD_EXEC_DISKS               = "disks/";
    public static final String MTHD_EXEC_DISK                = "disks/" + PARAM_URL_DISK + "/";
    // Networks
    public static final String MTHD_EXEC_NETWORKS            = "networks/";
    public static final String MTHD_EXEC_NETWORK             = "networks/" + PARAM_URL_NTWK + "/";
    //@formatter:on


    // API Paths

    //@formatter:off
    // Executable status methods
    // Index
    public static final String FULL_PATH_EXEC                     = API_PATH + "/" + API_VER + "/" + MTHD_EXEC;
    // Online
    public static final String FULL_PATH_EXEC_ONLINE              = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_ONLINE;
    // Process
    public static final String FULL_PATH_EXEC_PROCESS             = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_PROCESS;
    // Java
    public static final String FULL_PATH_EXEC_JAVA                = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA;
    public static final String FULL_PATH_EXEC_JAVA_VM             = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_VM;
    public static final String FULL_PATH_EXEC_JAVA_RUNTIME        = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_RUNTIME;
    public static final String FULL_PATH_EXEC_JAVA_TIMES          = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_TIMES;
    public static final String FULL_PATH_EXEC_JAVA_CLASSES        = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_CLASSES;
    public static final String FULL_PATH_EXEC_JAVA_MEMORY         = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_MEMORY;
    public static final String FULL_PATH_EXEC_JAVA_THREADS        = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_THREADS;
    public static final String FULL_PATH_EXEC_JAVA_THREAD         = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_JAVA_THREAD;
    // OS
    public static final String FULL_PATH_EXEC_OS                  = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_OS;
    // CPU
    public static final String FULL_PATH_EXEC_CPU                 = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_CPU;
    // Memory
    public static final String FULL_PATH_EXEC_MEMORY              = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_MEMORY;
    // Disks
    public static final String FULL_PATH_EXEC_DISKS               = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_DISKS;
    public static final String FULL_PATH_EXEC_DISK                = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_DISK;
    // Networks
    public static final String FULL_PATH_EXEC_NETWORKS            = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_NETWORKS;
    public static final String FULL_PATH_EXEC_NETWORK             = API_PATH + "/" + API_VER + "/" + MTHD_EXEC_NETWORK;
    //@formatter:on


    // API Paths composers

    //@formatter:off
    // Java
    public static String FULL_PATH_EXEC_JAVA_THREAD         (long threadId){ return FULL_PATH_EXEC_JAVA_THREAD      .replace(PARAM_URL_THREAD,Long.toString(threadId)); }
    // Disks
    public static String FULL_PATH_EXEC_DISK                (String diskId){ return FULL_PATH_EXEC_DISK             .replace(PARAM_URL_DISK,diskId); }
    // Networks
    public static String FULL_PATH_EXEC_NETWORK             (int networkId){ return FULL_PATH_EXEC_NETWORK          .replace(PARAM_URL_NTWK,Integer.toString(networkId)); }
    //@formatter:off
    

    // API Descriptions

    //@formatter:off
    // Executable status methods
    public static final String DESCR_PATH_EXEC                  = "Index of current API Group";
    public static final String DESCR_PATH_EXEC_ONLINE           = "Return current JCP Service executable's ONLINE status, current local date/time";
    public static final String DESCR_PATH_EXEC_PROCESS          = "Return current JCP Service executable's PROCESS status";
    public static final String DESCR_PATH_EXEC_JAVA             = "Index of Java API SubGroup";
    public static final String DESCR_PATH_EXEC_JAVA_VM          = "Return current JCP Service executable's JAVA_VM status";
    public static final String DESCR_PATH_EXEC_JAVA_RUNTIME     = "Return current JCP Service executable's JAVA_RUNTIME status";
    public static final String DESCR_PATH_EXEC_JAVA_TIMES       = "Return current JCP Service executable's JAVA_TIMES status";
    public static final String DESCR_PATH_EXEC_JAVA_CLASSES     = "Return current JCP Service executable's JAVA_CLASSES status";
    public static final String DESCR_PATH_EXEC_JAVA_MEMORY      = "Return current JCP Service executable's JAVA_MEMORY status";
    public static final String DESCR_PATH_EXEC_JAVA_THREADS     = "Return current JCP Service executable's JAVA_THREADS list";
    public static final String DESCR_PATH_EXEC_JAVA_THREAD      = "Return requests JCP Service executable's JAVA_THREAD status";
    public static final String DESCR_PATH_EXEC_OS               = "Return current JCP Service executable's OS status";
    public static final String DESCR_PATH_EXEC_CPU              = "Return current JCP Service executable's CPU status";
    public static final String DESCR_PATH_EXEC_MEMORY           = "Return current JCP Service executable's MEMORY status";
    public static final String DESCR_PATH_EXEC_DISKS            = "Return current JCP Service executable's DISKS list";
    public static final String DESCR_PATH_EXEC_DISK             = "Return requests JCP Service executable's DISK status";
    public static final String DESCR_PATH_EXEC_NETWORKS         = "Return current JCP Service executable's NETWORKS list";
    public static final String DESCR_PATH_EXEC_NETWORK          = "Return requests JCP Service executable's NETWORK status";
    //@formatter:on

}
