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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.robypomper.josp.consts.JOSPConstants;
import com.robypomper.josp.types.RESTItemList;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;


/**
 * JCP All - Status / Executable 2.0
 */
public class Params20 {

    // Index

    public static class Index {

        public final String urlOnline = Paths20.FULL_PATH_EXEC_ONLINE;
        public final String urlProcess = Paths20.FULL_PATH_EXEC_PROCESS;
        public final String urlJava = Paths20.FULL_PATH_EXEC_JAVA;
        public final String urlOS = Paths20.FULL_PATH_EXEC_OS;
        public final String urlCPU = Paths20.FULL_PATH_EXEC_CPU;
        public final String urlMemory = Paths20.FULL_PATH_EXEC_MEMORY;
        public final String urlDisks = Paths20.FULL_PATH_EXEC_DISKS;
        public final String urlNetworks = Paths20.FULL_PATH_EXEC_NETWORKS;

    }


    // Process

    public static class Process {

        public final String test = "test-ok";

        public Process() {

        }

    }


    // Java

    public static class JavaIndex {

        public final String urlVM = Paths20.FULL_PATH_EXEC_JAVA_VM;
        public final String urlRuntime = Paths20.FULL_PATH_EXEC_JAVA_RUNTIME;
        public final String urlTimes = Paths20.FULL_PATH_EXEC_JAVA_TIMES;
        public final String urlClasses = Paths20.FULL_PATH_EXEC_JAVA_CLASSES;
        public final String urlMemory = Paths20.FULL_PATH_EXEC_JAVA_MEMORY;
        public final String urlThreads = Paths20.FULL_PATH_EXEC_JAVA_THREADS;

    }

    public static class JavaVM {

        public final String vmName;             // "OpenJDK 64-Bit Server VM"
        public final String vmVersion;          // "25.262-b10"                             "11.0.4+11"
        public final String specName;           // "Java Virtual Machine Specification"
        public final String specVendor;         // "Oracle Corporation"
        public final String specVersion;        // "1.8"                                    "11"
        public final String specMngmVersion;    // "1.2"                                    "2.0"

        public JavaVM() {
            vmName = ManagementFactory.getRuntimeMXBean().getVmName();
            vmVersion = ManagementFactory.getRuntimeMXBean().getVmVersion();
            specName = ManagementFactory.getRuntimeMXBean().getSpecName();
            specVendor = ManagementFactory.getRuntimeMXBean().getSpecVendor();
            specVersion = ManagementFactory.getRuntimeMXBean().getSpecVersion();
            specMngmVersion = ManagementFactory.getRuntimeMXBean().getManagementSpecVersion();
        }

    }

    public static class JavaRuntime {

        public final Map<String, String> runtimeSystemProps;
        public final List<String> runtimeInputArgs;
        public final String runtimePathClass;
        public final String runtimePathBootClass;
        public final String runtimePathLibrary;

        public JavaRuntime() {
            runtimeSystemProps = ManagementFactory.getRuntimeMXBean().getSystemProperties();
            runtimeInputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
            runtimePathClass = ManagementFactory.getRuntimeMXBean().getClassPath();
            if (ManagementFactory.getRuntimeMXBean().isBootClassPathSupported())
                runtimePathBootClass = ManagementFactory.getRuntimeMXBean().getBootClassPath();
            else
                runtimePathBootClass = "NotSupported by RuntimeMXBean";
            runtimePathLibrary = ManagementFactory.getRuntimeMXBean().getLibraryPath();
        }

    }

    public static class JavaTimes {

        public final Date timeStart;
        public final long timeRunning;

        public JavaTimes() {
            timeStart = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
            timeRunning = ManagementFactory.getRuntimeMXBean().getUptime();
        }

    }

    public static class JavaClasses {

        public final long classesLoaded;
        public final long classesLoadedTotal;
        public final long classesUnloaded;

        public JavaClasses() {
            classesLoaded = ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();
            classesLoadedTotal = ManagementFactory.getClassLoadingMXBean().getTotalLoadedClassCount();
            classesUnloaded = ManagementFactory.getClassLoadingMXBean().getUnloadedClassCount();
        }

    }

    public static class JavaMemory {

        public final double memoryInit;
        public final double memoryUsed;
        public final double memoryCommitted;
        public final double memoryMax;
        public final double memoryHeapInit;
        public final double memoryHeapUsed;
        public final double memoryHeapFree;
        public final double memoryHeapCommitted;
        public final double memoryHeapMax;

        public JavaMemory() {

            memoryInit = (double) (ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getInit() / JOSPConstants.BYTE_TRANSFORM);
            memoryUsed = (double) (ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed() / JOSPConstants.BYTE_TRANSFORM);
            memoryCommitted = (double) (ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getCommitted() / JOSPConstants.BYTE_TRANSFORM);
            memoryMax = (double) (ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax() / JOSPConstants.BYTE_TRANSFORM);
            memoryHeapInit = (double) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getInit() / JOSPConstants.BYTE_TRANSFORM);
            memoryHeapUsed = (double) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / JOSPConstants.BYTE_TRANSFORM);
            memoryHeapFree = (double) Runtime.getRuntime().freeMemory() / JOSPConstants.BYTE_TRANSFORM;
            memoryHeapCommitted = (double) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted() / JOSPConstants.BYTE_TRANSFORM);
            //(double)Runtime.getRuntime().totalMemory() / byteTransform
            memoryHeapMax = (double) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / JOSPConstants.BYTE_TRANSFORM);
            //Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? -1 : (double)Runtime.getRuntime().maxMemory() / JOSPConstants.BYTE_TRANSFORM
        }

    }

    public static class JavaThreads {

        public final int threadsCount;
        public final int threadsCountDaemon;
        public final int threadsCountPeak;
        public final long threadsCountStarted;
        public List<RESTItemList> threadsList;

        public JavaThreads() {
            threadsList = new ArrayList<>();
            for (long id : ManagementFactory.getThreadMXBean().getAllThreadIds()) {
                RESTItemList th = new RESTItemList();
                th.id = Long.toString(id);
                th.name = ManagementFactory.getThreadMXBean().getThreadInfo(id).getThreadName();
                th.url = Paths20.FULL_PATH_EXEC_JAVA_THREAD(id);
                threadsList.add(th);
            }
            threadsCount = ManagementFactory.getThreadMXBean().getThreadCount();
            threadsCountDaemon = ManagementFactory.getThreadMXBean().getDaemonThreadCount();
            threadsCountPeak = ManagementFactory.getThreadMXBean().getPeakThreadCount();
            threadsCountStarted = ManagementFactory.getThreadMXBean().getTotalStartedThreadCount();
        }

    }

    public static class JavaThread {

        public final long id;
        public final String name;
        public final String state;
        public final long timeCpu;
        public final long timeUser;
        public final long timeWaited;
        public final long timeWaitedCount;
        public final long timeBlocked;
        public final long timeBlockedCount;

        public JavaThread(long thId) {
            this.id = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getThreadId();
            this.name = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getThreadName();
            this.state = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getThreadState().toString();
            this.timeCpu = ManagementFactory.getThreadMXBean().getThreadCpuTime(thId);
            this.timeUser = ManagementFactory.getThreadMXBean().getThreadUserTime(thId);
            this.timeWaited = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getWaitedCount();
            this.timeWaitedCount = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getWaitedTime();
            this.timeBlocked = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getBlockedCount();
            this.timeBlockedCount = ManagementFactory.getThreadMXBean().getThreadInfo(thId).getBlockedTime();
        }

        @JsonCreator
        public JavaThread(@JsonProperty long id, @JsonProperty String name, @JsonProperty String state,
                          @JsonProperty long timeCpu, @JsonProperty long timeUser, @JsonProperty long timeWaited,
                          @JsonProperty long timeWaitedCount, @JsonProperty long timeBlocked, @JsonProperty long timeBlockedCount) {
            this.id = id;
            this.name = name;
            this.state = state;
            this.timeCpu = timeCpu;
            this.timeUser = timeUser;
            this.timeWaited = timeWaited;
            this.timeWaitedCount = timeWaitedCount;
            this.timeBlocked = timeBlocked;
            this.timeBlockedCount = timeBlockedCount;
        }

    }


    // OS

    public static class OS {

        public final String name;           // "Mac OS X"  "Linux"
        public final String version;        // "10.16"
        public final String arch;           // "x86_64"    "amd64"

        public OS() {
            this.name = System.getProperty("os.name");
            //this.name = ManagementFactory.getOperatingSystemMXBean().getName();
            this.version = System.getProperty("os.version");
            //this.version = ManagementFactory.getOperatingSystemMXBean().getVersion();
            this.arch = System.getProperty("os.arch");
            //this.arch = ManagementFactory.getOperatingSystemMXBean().getArch();
        }

    }

    // CPU

    public static class CPU {

        public final int count;             // 8        4
        public final double loadAvg;        // 3.05...  0.75

        public CPU() {
            //this.count = Runtime.getRuntime().availableProcessors();
            this.count = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
            this.loadAvg = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        }

    }


    // Memory

    public static class Memory {

        public final String test = "test-ok";

        public Memory() {

        }

    }


    // Disks

    public static class Disks {

        public List<RESTItemList> disksList;

        public Disks() {
            disksList = new ArrayList<>();
            for (Path root : FileSystems.getDefault().getRootDirectories()) {
                RESTItemList disk = new RESTItemList();
                disk.id = root.toString();
                disk.name = root.toString();
                try {
                    disk.url = Paths20.FULL_PATH_EXEC_DISK(URLEncoder.encode(root.toString(), StandardCharsets.UTF_8.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                disksList.add(disk);
            }
        }

    }

    public static class Disk {

        public final String name;
        public final long spaceFree;
        public final long spaceUsable;
        public final long spaceMax;

        public Disk(String rootId) {
            File root = new File(rootId);
            name = root.getAbsolutePath();
            spaceFree = root.getFreeSpace() / JOSPConstants.BYTE_TRANSFORM;
            spaceUsable = root.getUsableSpace() / JOSPConstants.BYTE_TRANSFORM;
            spaceMax = root.getTotalSpace() / JOSPConstants.BYTE_TRANSFORM;
        }

    }


    // Networks

    public static class Networks {

        public final InetAddress addrLoopback;
        public final InetAddress addrLocalhost;
        public List<RESTItemList> networksList;

        public Networks() {
            addrLoopback = InetAddress.getLoopbackAddress();
            InetAddress addrLocalhostTmp = null;
            try {
                addrLocalhostTmp = InetAddress.getLocalHost();

            } catch (UnknownHostException ignore) {
            }
            addrLocalhost = addrLocalhostTmp;

            List<NetworkInterface> intfs;
            try {
                intfs = Collections.list(NetworkInterface.getNetworkInterfaces());

            } catch (SocketException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            networksList = new ArrayList<>();
            for (NetworkInterface netIntf : intfs) {
                RESTItemList intf = new RESTItemList();
                intf.id = Integer.toString(netIntf.getIndex());
                intf.name = netIntf.getDisplayName();
                intf.url = Paths20.FULL_PATH_EXEC_NETWORK(netIntf.getIndex());
                networksList.add(intf);
            }
        }

    }

    public static class Network {

        public final int index;
        public final String name;
        public final String nameDisplay;
        public final boolean isUp;
        public final boolean isLoopback;
        public final boolean isPointToPoint;
        public final boolean isVirtual;
        public final int MTU;
        public final List<InetAddress> address;
        public final List<InterfaceAddress> addressIntf;
        public final List<Integer> subIntfIndexes;

        public Network(int intfIndex) throws SocketException {
            NetworkInterface netIntf = NetworkInterface.getByIndex(intfIndex);
            index = netIntf.getIndex();
            name = netIntf.getName();
            nameDisplay = netIntf.getDisplayName();
            isUp = netIntf.isUp();
            isLoopback = netIntf.isLoopback();
            isPointToPoint = netIntf.isPointToPoint();
            isVirtual = netIntf.isVirtual();
            netIntf.getHardwareAddress();
            address = Collections.list(netIntf.getInetAddresses());
            addressIntf = netIntf.getInterfaceAddresses();
            MTU = netIntf.getMTU();
            List<NetworkInterface> subItfs = Collections.list(netIntf.getSubInterfaces());
            subIntfIndexes = new ArrayList<>();
            for (NetworkInterface subItf : subItfs) {
                subIntfIndexes.add(subItf.getIndex());
            }
        }
    }

}
