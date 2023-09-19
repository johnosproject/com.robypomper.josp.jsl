/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
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

package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.java.JavaDate;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.consts.JOSPConstants;
import com.robypomper.josp.jsl.admin.JSLAdmin;
import com.robypomper.josp.types.RESTItemList;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CmdsJSLAdmin {

    private final JSLAdmin admin;

    public CmdsJSLAdmin(JSLAdmin admin) {
        this.admin = admin;
    }


    // JCP APIs Status

    @Command(name = "Admin-JCP-APIs-Objects-Stats",
            abbrev = "admJCPAPIsObjsStats",
            description = "Print JCP APIs objects stats",
            header = "JCP APIs OBJECTS STATS")
    public String adminJCPAPIsObjectsStats() {
        com.robypomper.josp.defs.admin.apis.status.Params20.Objects objsList;
        try {
            objsList = admin.getJCPAPIsObjects();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Total  . . . . . %s\n", objsList.count);
        s += String.format("  Active . . . . . %s\n", objsList.activeCount);
        s += String.format("  Inactive . . . . %s\n", objsList.inactiveCount);
        s += String.format("  Online . . . . . %s\n", objsList.onlineCount);
        s += String.format("  Offline  . . . . %s\n", objsList.offlineCount);
        s += String.format("  Owners . . . . . %s\n", objsList.ownersCount);
        return s;
    }

    @Command(name = "Admin-JCP-APIs-Objects-List",
            abbrev = "admJCPAPIsObjsList",
            description = "Print JCP APIs objects list",
            header = "JCP APIs OBJECTS LIST")
    public String adminJCPAPIsObjectsList() {
        com.robypomper.josp.defs.admin.apis.status.Params20.Objects objsList;
        try {
            objsList = admin.getJCPAPIsObjects();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder("(ID) Name");
        for (RESTItemList item : objsList.objectsList)
            s.append(String.format("  - (%-20s) %s\n", item.id, item.name));
        return s.toString();
    }

    @Command(name = "Admin-JCP-APIs-Objects-Details",
            abbrev = "admJCPAPIsObjs",
            description = "Print JCP APIs object details",
            header = "JCP APIs OBJECT DETAILS")
    public String adminJCPAPIsObjectDetails(String objId) {
        com.robypomper.josp.defs.admin.apis.status.Params20.Object obj;
        try {
            obj = admin.getJCPAPIsObject(objId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Id . . . . . . %s\n", obj.id);
        s += String.format("  Name . . . . . %s\n", obj.name);
        s += String.format("  Status . . . . %s / %s\n", obj.online ? "online" : "offline", obj.active ? "active" : "deactivated");
        s += String.format("  Owner  . . . . %s\n", obj.owner);
        s += String.format("  Version  . . . %s\n", obj.version);
        s += String.format("  Created at . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(obj.createdAt));
        s += String.format("  Updated at . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(obj.updatedAt));
        return s;
    }

    @Command(name = "Admin-JCP-APIs-Services-Stats",
            abbrev = "admJCPAPIsSrvsStats",
            description = "Print JCP APIs service stats",
            header = "JCP APIs SERVICES STATS")
    public String adminJCPAPIsServicesStats() {
        com.robypomper.josp.defs.admin.apis.status.Params20.Services srvsList;
        try {
            srvsList = admin.getJCPAPIsServices();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Total  . . . . . . . %s\n", srvsList.count);
        s += String.format("  Online . . . . . . . %s\n", srvsList.onlineCount);
        s += String.format("  Offline  . . . . . . %s\n", srvsList.offlineCount);
        s += String.format("  Total instances  . . %s\n", srvsList.instancesCount);
        s += String.format("  Online instances . . %s\n", srvsList.instancesOnlineCount);
        s += String.format("  Offline instances  . %s\n", srvsList.instancesOfflineCount);
        return s;
    }

    @Command(name = "Admin-JCP-APIs-Services-List",
            abbrev = "admJCPAPIsSrvsList",
            description = "Print JCP APIs service list",
            header = "JCP APIs SERVICES LIST")
    public String adminJCPAPIsServicesList() {
        com.robypomper.josp.defs.admin.apis.status.Params20.Services srvsList;
        try {
            srvsList = admin.getJCPAPIsServices();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder("(ID) Name");
        for (RESTItemList item : srvsList.servicesList)
            s.append(String.format("  - (%-20s) %s\n", item.id, item.name));
        return s.toString();
    }

    @Command(name = "Admin-JCP-APIs-Services-Details",
            abbrev = "admJCPAPIsSrvs",
            description = "Print JCP APIs service details",
            header = "JCP APIs SERVICE DETAILS")
    public String adminJCPAPIsServiceDetails(String srvId) {
        com.robypomper.josp.defs.admin.apis.status.Params20.Service srv;
        try {
            srv = admin.getJCPAPIsService(srvId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Id . . . . . . %s\n", srv.id);
        s += String.format("  Name . . . . . %s\n", srv.name);
        s += String.format("  Created at . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(srv.createdAt));
        s += String.format("  Updated at . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(srv.updatedAt));
        return s;
    }

    @Command(name = "Admin-JCP-APIs-Users-Stats",
            abbrev = "admJCPAPIsUsrsStats",
            description = "Print JCP APIs users stats",
            header = "JCP APIs USERS STATS")
    public String adminJCPAPIsUsersStats() {
        com.robypomper.josp.defs.admin.apis.status.Params20.Users usrsList;
        try {
            usrsList = admin.getJCPAPIsUsers();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Total  . . . . . %s\n", usrsList.count);
        return s;
    }

    @Command(name = "Admin-JCP-APIs-Users-List",
            abbrev = "admJCPAPIsUsrsList",
            description = "Print JCP APIs users list",
            header = "JCP APIs USERS LIST")
    public String adminJCPAPIsUsersList() {
        com.robypomper.josp.defs.admin.apis.status.Params20.Users usrsList;
        try {
            usrsList = admin.getJCPAPIsUsers();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder("(ID) Name");
        for (RESTItemList item : usrsList.usersList)
            s.append(String.format("  - (%-20s) %s\n", item.id, item.name));
        return s.toString();
    }

    @Command(name = "Admin-JCP-APIs-Users-Details",
            abbrev = "admJCPAPIsUsrs",
            description = "Print JCP APIs user details",
            header = "JCP APIs USER DETAILS")
    public String adminJCPAPIsUserDetails(String usrId) {
        com.robypomper.josp.defs.admin.apis.status.Params20.User usr;
        try {
            usr = admin.getJCPAPIsUser(usrId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Id . . . . . . %s\n", usr.id);
        s += String.format("  Name . . . . . %s\n", usr.name);
        s += String.format("  First Name . . %s\n", usr.first_name);
        s += String.format("  Second Name  . %s\n", usr.second_name);
        s += String.format("  Email  . . . . %s\n", usr.email);
        s += String.format("  Created at . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(usr.createdAt));
        s += String.format("  Updated at . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(usr.updatedAt));
        return s;
    }

    @Command(name = "Admin-JCP-APIs-Gateways-Stats",
            abbrev = "admJCPAPIsGWsStats",
            description = "Print JCP APIs gateways stats",
            header = "JCP APIs GATEWAYS STATS")
    public String adminJCPAPIsGatewaysStats() {
        com.robypomper.josp.defs.admin.apis.status.Params20.Gateways gwsList;
        try {
            gwsList = admin.getJCPAPIsGateways();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Count  . . . . . %s\n", gwsList.count);
        s += String.format("  Removed  . . . . %s\n", gwsList.removed);
        s += String.format("  Total  . . . . . %s\n", gwsList.total);
        return s;
    }

    @Command(name = "Admin-JCP-APIs-Gateways-List",
            abbrev = "admJCPAPIsGWsList",
            description = "Print JCP APIs gateways list",
            header = "JCP APIs GATEWAYS LIST")
    public String adminJCPAPIsGatewaysList() {
        com.robypomper.josp.defs.admin.apis.status.Params20.Gateways gwsList;
        try {
            gwsList = admin.getJCPAPIsGateways();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder("(ID) Name");
        for (RESTItemList item : gwsList.gatewaysList)
            s.append(String.format("  - (%-20s) %s\n", item.id, item.name));
        return s.toString();
    }

    @Command(name = "Admin-JCP-APIs-Gateways-Details",
            abbrev = "admJCPAPIsGWs",
            description = "Print JCP APIs gateway details",
            header = "JCP APIs GATEWAY DETAILS")
    public String adminJCPAPIsGatewayDetails(String gwId) {
        com.robypomper.josp.defs.admin.apis.status.Params20.Gateway gw;
        try {
            gw = admin.getJCPAPIsGateway(gwId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Id . . . . . . %s\n", gw.id);
        s += String.format("  Name . . . . . %s\n", gw.name);
        s += String.format("  Type . . . . . %s\n", gw.type);
        s += String.format("  Version  . . . %s\n", gw.version);
        s += String.format("  GW Url . . . . %s\n", gw.gwUrl);
        s += String.format("  Status . . . . %s\n", gw.connected ? "Connected" : "Disconnected");
        s += String.format("  ReConn Att.  . %s\n", gw.reconnectionAttempts);
        s += String.format("  Conn Clients . %s\n", gw.currentClients);
        s += String.format("  Name . . . . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(gw.lastClientConnected));
        s += String.format("  Name . . . . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(gw.lastClientDisconnected));
        s += String.format("  Created at . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(gw.createdAt));
        s += String.format("  Updated at . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(gw.updatedAt));
        return s;
    }


    // JCP APIs Executable

    @Command(name = "Admin-JCP-APIs-Executable",
            abbrev = "admJCPAPIsExec",
            description = "Print JCP APIs executable info",
            header = "JCP APIs EXECUTABLE INFO")
    public String adminJCPAPIsExec() {
        Date online;
        //com.robypomper.josp.defs.admin.apis.executable.Params20.Process process;
        com.robypomper.josp.defs.admin.apis.executable.Params20.OS os;
        com.robypomper.josp.defs.admin.apis.executable.Params20.CPU cpu;
        //com.robypomper.josp.defs.admin.apis.executable.Params20.Memory memory;
        com.robypomper.josp.defs.admin.apis.executable.Params20.Disks disks;
        List<com.robypomper.josp.defs.admin.apis.executable.Params20.Disk> disksList = new ArrayList<>();
        com.robypomper.josp.defs.admin.apis.executable.Params20.Networks networks;
        List<com.robypomper.josp.defs.admin.apis.executable.Params20.Network> networksList = new ArrayList<>();
        try {
            online = admin.getJCPAPIsExecOnline();
            //process = admin.getJCPAPIsExecProcess();
            os = admin.getJCPAPIsExecOS();
            cpu = admin.getJCPAPIsExecCPU();
            //memory = admin.getJCPAPIsExecMemory();
            disks = admin.getJCPAPIsExecDisks();
            for (RESTItemList item : disks.disksList)
                disksList.add(admin.getJCPAPIsExecDisk(item.id));
            networks = admin.getJCPAPIsExecNetworks();
            for (RESTItemList item : networks.networksList)
                networksList.add(admin.getJCPAPIsExecNetwork(Integer.parseInt(item.id)));

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder();
        s.append(String.format("  Online . . . . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(online)));
        //s += String.format("  Process  . . . . %s\n", process....);
        s.append(String.format("  OS . . . . . . . %s (v. %s arch %s)\n", os.name, os.version, os.arch));
        s.append(String.format("  CPU load . . . . %.2f (#%d)\n", cpu.loadAvg, cpu.count));
        //s += String.format("  Memory . . . . . %s\n", memory....);
        s.append("  Disks:\n");
        s.append("    Name Free/Usable/Total\n");
        for (com.robypomper.josp.defs.admin.apis.executable.Params20.Disk disk : disksList)
            s.append(String.format("    - %.10s   %dMB / %dMB  / %dMB\n", disk.name, disk.spaceFree, disk.spaceUsable, disk.spaceMax));
        s.append("  Networks:\n");
        s.append(String.format("    Main . . . . . %s (%s)\n", networks.addrLocalhost.getHostName(), networks.addrLocalhost.getHostAddress()));
        s.append(String.format("    Loopback . . . %s (%s)\n", networks.addrLoopback.getHostName(), networks.addrLoopback.getHostAddress()));
        for (com.robypomper.josp.defs.admin.apis.executable.Params20.Network net : networksList) {
            s.append(String.format("    - %s (%s)\n", net.name, net.nameDisplay));
            s.append("      Address:\n");
            for (InetAddress addr : net.address)
                s.append(String.format("        %s (%s)\n", addr.getHostName(), addr.getHostAddress()));
            s.append("      Interfaces Address:\n");
            for (InterfaceAddress addr : net.addressIntf)
                s.append(String.format("        %s\n", addr.toString()));
        }
        return s.toString();
    }

    @Command(name = "Admin-JCP-APIs-Executable-Java",
            abbrev = "admJCPAPIsExecJava",
            description = "Print JCP APIs Java's executable info",
            header = "JCP APIs JAVA EXECUTABLE INFO")
    public String adminJCPAPIsExecJava() {
        com.robypomper.josp.defs.admin.apis.executable.Params20.JavaVM vm;
        //com.robypomper.josp.defs.admin.apis.executable.Params20.JavaRuntime runtime;
        com.robypomper.josp.defs.admin.apis.executable.Params20.JavaTimes times;
        com.robypomper.josp.defs.admin.apis.executable.Params20.JavaClasses classes;
        com.robypomper.josp.defs.admin.apis.executable.Params20.JavaMemory memory;
        com.robypomper.josp.defs.admin.apis.executable.Params20.JavaThreads threads;
        try {
            vm = admin.getJCPAPIsExecJavaVM();
            //runtime = admin.getJCPAPIsExecJavaRuntime();
            times = admin.getJCPAPIsExecJavaTimes();
            classes = admin.getJCPAPIsExecJavaClasses();
            memory = admin.getJCPAPIsExecJavaMemory();
            threads = admin.getJCPAPIsExecJavaThreads();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  VM . . . . . . . %s (v. %s)\n", vm.vmName, vm.vmVersion);
        s += String.format("  VM Spec  . . . . v. %s (vendor %s)\n", vm.specVersion, vm.specVendor);
        //s += String.format("  Runtime  . . . . %s\n", process....);
        s += String.format("  Started  . . . . %s (%s)\n", JavaDate.DEF_DATE_FORMATTER.format(times.timeStart), humanReadableFormat(times.timeRunning * 1000));
        s += String.format("  Classes  . . . . %d / %d / %d (Loaded/Loaded Total/Unloaded)\n", classes.classesLoaded, classes.classesLoadedTotal, classes.classesUnloaded);
        s += String.format("  Memory . . . . . %.2f / %.2f / %.2f (Init/Used/Committed)\n", memory.memoryInit, memory.memoryUsed, memory.memoryCommitted);
        s += String.format("  Memory Heap  . . %.2f / %.2f / %.2f / %.2f (Used/Free/Committed/Max)\n", memory.memoryHeapUsed, memory.memoryHeapFree, memory.memoryHeapCommitted, memory.memoryHeapMax);
        s += String.format("  Threads  . . . . %d (%d) / %d / %d Running (Daemon)/Peak/Total)\n", threads.threadsCount, threads.threadsCountDaemon, threads.threadsCountPeak, threads.threadsCountStarted);
        return s;
    }

    @Command(name = "Admin-JCP-APIs-Executable-Java-Threads",
            abbrev = "admJCPAPIsExecJavaThs",
            description = "Print JCP APIs Java's executable threads list",
            header = "JCP APIs JAVA EXECUTABLE THREADS LIST")
    public String adminJCPAPIsExecThreadsList() {
        com.robypomper.josp.defs.admin.apis.executable.Params20.JavaThreads threads;
        List<com.robypomper.josp.defs.admin.apis.executable.Params20.JavaThread> threadsList = new ArrayList<>();
        try {
            threads = admin.getJCPAPIsExecJavaThreads();
            for (RESTItemList item : threads.threadsList)
                threadsList.add(admin.getJCPAPIsExecJavaThread(Long.parseLong(item.id)));

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }
        StringBuilder s = new StringBuilder("(ID) Name State");
        for (com.robypomper.josp.defs.admin.apis.executable.Params20.JavaThread thread : threadsList)
            s.append(String.format("  - (%6s) %20s %s\n", thread.id, thread.name, thread.state));
        return s.toString();
    }


    // JCP APIs Build Info

    @Command(name = "Admin-JCP-APIs-Build-Info",
            abbrev = "admJCPAPIsBuild",
            description = "Print JCP APIs build info",
            header = "JCP APIs BUILD INFO")
    public String adminJCPAPIsBuildInfo() {
        com.robypomper.josp.defs.admin.apis.buildinfo.Params20.BuildInfo buildInfo;
        try {
            buildInfo = admin.getJCPAPIsBuildInfo();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP APIs status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP APIs status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Version  . . . . %s (build %s)\n", buildInfo.version, buildInfo.versionBuild);
        s += String.format("  Sources  . . . . ::%s:%s\n", buildInfo.project, buildInfo.sourceSet);
        s += String.format("  Git  . . . . . . %s (branch %s)\n", buildInfo.gitCommitShort, buildInfo.gitBranch);
        s += "  Build Process:";
        s += String.format("    Time . . . . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(buildInfo.buildTime));
        s += String.format("    Java . . . . . v. %s (path %s)\n", buildInfo.javaVersion, buildInfo.javaHome);
        s += String.format("    Gradle . . . . v. %s\n", buildInfo.gradleVersion);
        s += String.format("    User . . . . . %s\n", buildInfo.user);
        s += String.format("    OS . . . . . . %s (v. %s arch %s)\n", buildInfo.osName, buildInfo.osVersion, buildInfo.osArch);
        return s;
    }


    // JCP Gateways Status

    @Command(name = "Admin-JCP-Gateways-Server-List",
            abbrev = "admJCPGatewaysServerList",
            description = "Print JCP Gateways server list",
            header = "JCP GATEWAYS SERVER LIST")
    public String adminJCPGatewaysServerList() {
        com.robypomper.josp.defs.admin.gateways.status.Params20.GatewaysServers gwServersList;
        try {
            gwServersList = admin.getJCPGatewaysServers();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder("(ID) Name");
        for (RESTItemList item : gwServersList.serverList)
            s.append(String.format("  - (%-20s) %s\n", item.id, item.name));
        return s.toString();
    }

    @Command(name = "Admin-JCP-Gateways-GWs-List",
            abbrev = "admJCPGatewaysGWsList",
            description = "Print JCP Gateways GWs list",
            header = "JCP GATEWAYS GWs LIST")
    public String adminJCPGatewaysGWsList(String gwServerId) {
        com.robypomper.josp.defs.admin.gateways.status.Params20.GWs gwsList;
        try {
            gwsList = admin.getJCPGatewaysGWs(gwServerId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder("(ID) Name");
        for (RESTItemList item : gwsList.gwList)
            s.append(String.format("  - (%-20s) %s\n", item.id, item.name));
        return s.toString();
    }

    @Command(name = "Admin-JCP-Gateways-GWs-Details",
            abbrev = "admJCPGatewaysGWs",
            description = "Print JCP Gateways GWs details",
            header = "JCP GATEWAYS GWs DETAILS")
    public String adminJCPGatewaysGWDetails(String gwServerId, String gwId) {
        com.robypomper.josp.defs.admin.gateways.status.Params20.GW gw;
        try {
            gw = admin.getJCPGatewaysGW(gwServerId, gwId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Id . . . . . . %s\n", gw.id);
        s += String.format("  Status . . . . %s\n", gw.status);
        s += String.format("  Clients  . . . %d (max %d)\n", gw.clientsCount, gw.maxClientsCount);
        s += String.format("  Type . . . . . %s\n", gw.type);
        s += String.format("  Private addr . %s\n", gw.internalAddress);
        s += String.format("  Public addr  . %s\n", gw.publicAddress);
        s += String.format("  API port . . . %s\n", gw.apisPort);
        s += String.format("  GW port  . . . %s\n", gw.gwPort);
        return s;
    }

    @Command(name = "Admin-JCP-Gateways-GWs-Clients-List",
            abbrev = "admJCPGatewaysGWsClisList",
            description = "Print JCP Gateways GWs clients list",
            header = "JCP GATEWAYS GWs CLIENTS LIST")
    public String adminJCPGatewaysGWsClientList(String gwServerId, String gwId) {
        com.robypomper.josp.defs.admin.gateways.status.Params20.GW gwsClientsList;
        try {
            gwsClientsList = admin.getJCPGatewaysGW(gwServerId, gwId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder("(ID) Name");
        for (RESTItemList item : gwsClientsList.clientsList)
            s.append(String.format("  - (%-20s) %s\n", item.id, item.name));
        return s.toString();
    }

    @Command(name = "Admin-JCP-Gateways-GWs-Clients-Details",
            abbrev = "admJCPGatewaysGWsClis",
            description = "Print JCP Gateways GWs client details",
            header = "JCP GATEWAYS GWs CLIENT DETAILS")
    public String adminJCPGatewaysGWDetails(String gwServerId, String gwId, String gwClientId) {
        com.robypomper.josp.defs.admin.gateways.status.Params20.GWClient gwCli;
        try {
            gwCli = admin.getJCPGatewaysGWsClient(gwServerId, gwId, gwClientId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Id . . . . . . %s\n", gwCli.id);
        s += String.format("  Status . . . . %s\n", gwCli.isConnected ? "Connected" : "Disconnected");
        s += String.format("  Rx . . . . . . %.2f MB (%s)\n", (double)gwCli.bytesRx / JOSPConstants.BYTE_TRANSFORM, JavaDate.DEF_DATE_FORMATTER.format(gwCli.lastDataRx));
        s += String.format("  Tx . . . . . . %.2f MB (%s)\n", (double)gwCli.bytesTx / JOSPConstants.BYTE_TRANSFORM, JavaDate.DEF_DATE_FORMATTER.format(gwCli.lastDataTx));
        s += String.format("  Local  . . . . %s\n", gwCli.local);
        s += String.format("  Remote . . . . %s\n", gwCli.remote);
        s += String.format("  Last Conn  . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(gwCli.lastConnection));
        s += String.format("  Last Disconn . %s\n", JavaDate.DEF_DATE_FORMATTER.format(gwCli.lastDisconnection));
        s += String.format("  Last HB  . . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(gwCli.lastHeartBeat));
        s += String.format("  Last HB Fail . %s\n", JavaDate.DEF_DATE_FORMATTER.format(gwCli.lastHeartBeatFailed));
        s += String.format("  Last Conn  . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(gwCli.lastConnection));
        return s;
    }

    @Command(name = "Admin-JCP-Gateways-Broker-Objects-List",
            abbrev = "admJCPGatewaysBrkrObjsList",
            description = "Print JCP Gateways broker objects list",
            header = "JCP GATEWAYS BROKER OBJECTS LIST")
    public String adminJCPGatewaysBrokerObjectsList(String gwServerId) {
        com.robypomper.josp.defs.admin.gateways.status.Params20.Broker brokerList;
        try {
            brokerList = admin.getJCPGatewaysBroker(gwServerId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder("(ID) Name");
        for (RESTItemList item : brokerList.objsList)
            s.append(String.format("  - (%-20s) %s\n", item.id, item.name));
        return s.toString();
    }

    @Command(name = "Admin-JCP-Gateways-Broker-Objects-Details",
            abbrev = "admJCPGatewaysBrkrObjs",
            description = "Print JCP Gateways broker object details",
            header = "JCP GATEWAYS BROKER OBJECT DETAILS")
    public String adminJCPGatewaysBrokerObjectDetails(String gwServerId, String objId) {
        com.robypomper.josp.defs.admin.gateways.status.Params20.BrokerObject obj;
        try {
            obj = admin.getJCPGatewaysBrokerObject(gwServerId, objId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Id . . . . . . %s\n", obj.id);
        s += String.format("  Name . . . . . %s\n", obj.name);
        s += String.format("  Owner  . . . . %s\n", obj.owner);
        return s;
    }

    @Command(name = "Admin-JCP-Gateways-Broker-Services-List",
            abbrev = "admJCPGatewaysBrkrSrvsList",
            description = "Print JCP Gateways broker services list",
            header = "JCP GATEWAYS BROKER SERVICES LIST")
    public String adminJCPGatewaysBrokerServicesList(String gwServerId) {
        com.robypomper.josp.defs.admin.gateways.status.Params20.Broker brokerList;
        try {
            brokerList = admin.getJCPGatewaysBroker(gwServerId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder("(ID) Name");
        for (RESTItemList item : brokerList.srvsList)
            s.append(String.format("  - (%-20s) %s\n", item.id, item.name));
        return s.toString();
    }

    @Command(name = "Admin-JCP-Gateways-Broker-Services-Details",
            abbrev = "admJCPGatewaysBrkrSrvs",
            description = "Print JCP Gateways broker service details",
            header = "JCP GATEWAYS BROKER SERVICE DETAILS")
    public String adminJCPGatewaysBrokerServiceDetails(String gwServerId, String srvId) {
        com.robypomper.josp.defs.admin.gateways.status.Params20.BrokerService srv;
        try {
            srv = admin.getJCPGatewaysBrokerService(gwServerId, srvId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Id . . . . . . %s\n", srv.id);
        s += String.format("  Name . . . . . %s\n", srv.name);
        s += String.format("  User . . . . . %s\n", srv.user);
        return s;
    }

    @Command(name = "Admin-JCP-Gateways-Broker-ObjectsDB-List",
            abbrev = "admJCPGatewaysBrkrObjsDBList",
            description = "Print JCP Gateways broker objects DB list",
            header = "JCP GATEWAYS BROKER OBJECTS DB LIST")
    public String adminJCPGatewaysBrokerObjectsDBList(String gwServerId) {
        com.robypomper.josp.defs.admin.gateways.status.Params20.Broker brokerList;
        try {
            brokerList = admin.getJCPGatewaysBroker(gwServerId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder("(ID) Name");
        for (RESTItemList item : brokerList.objsDBList)
            s.append(String.format("  - (%-20s) %s\n", item.id, item.name));
        return s.toString();
    }

    @Command(name = "Admin-JCP-Gateways-Broker-ObjectsDB-Details",
            abbrev = "admJCPGatewaysBrkrObjsDB",
            description = "Print JCP Gateways broker object DB details",
            header = "JCP GATEWAYS BROKER OBJECT DB DETAILS")
    public String adminJCPGatewaysBrokerObjectDBDetails(String gwServerId, String objId) {
        com.robypomper.josp.defs.admin.gateways.status.Params20.BrokerObjectDB obj;
        try {
            obj = admin.getJCPGatewaysBrokerObjectDB(gwServerId, objId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Id . . . . . . %s\n", obj.id);
        s += String.format("  Name . . . . . %s\n", obj.name);
        s += String.format("  Owner  . . . . %s\n", obj.owner);
        return s;
    }


    // JCP Gateways Executable

    @Command(name = "Admin-JCP-Gateways-List",
            abbrev = "admJCPGatewaysList",
            description = "Print JCP Gateways list",
            header = "JCP GATEWAYS LIST")
    public String adminJCPGatewaysList() {
        com.robypomper.josp.defs.admin.gateways.status.Params20.GatewaysServers gwsServersList;
        try {
            gwsServersList = admin.getJCPGatewaysServers();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder("(ID) Name");
        for (RESTItemList item : gwsServersList.serverList)
            s.append(String.format("  - (%-20s) %s\n", item.id, item.name));
        return s.toString();
    }

    @Command(name = "Admin-JCP-Gateways-Executable",
            abbrev = "admJCPGatewaysExec",
            description = "Print JCP Gateways executable info",
            header = "JCP GATEWAYS EXECUTABLE INFO")
    public String adminJCPGatewaysExec(String gwServerId) {
        Date online;
        //com.robypomper.josp.defs.admin.gateways.executable.Params20.Process process;
        com.robypomper.josp.defs.admin.gateways.executable.Params20.OS os;
        com.robypomper.josp.defs.admin.gateways.executable.Params20.CPU cpu;
        //com.robypomper.josp.defs.admin.gateways.executable.Params20.Memory memory;
        com.robypomper.josp.defs.admin.gateways.executable.Params20.Disks disks;
        List<com.robypomper.josp.defs.admin.gateways.executable.Params20.Disk> disksList = new ArrayList<>();
        com.robypomper.josp.defs.admin.gateways.executable.Params20.Networks networks;
        List<com.robypomper.josp.defs.admin.gateways.executable.Params20.Network> networksList = new ArrayList<>();
        try {
            online = admin.getJCPGatewaysExecOnline(gwServerId);
            //process = admin.getJCPGatewaysExecProcess(gwServerId);
            os = admin.getJCPGatewaysExecOS(gwServerId);
            cpu = admin.getJCPGatewaysExecCPU(gwServerId);
            //memory = admin.getJCPGatewaysExecMemory(gwServerId);
            disks = admin.getJCPGatewaysExecDisks(gwServerId);
            for (RESTItemList item : disks.disksList)
                disksList.add(admin.getJCPGatewaysExecDisk(gwServerId, item.id));
            networks = admin.getJCPGatewaysExecNetworks(gwServerId);
            for (RESTItemList item : networks.networksList)
                networksList.add(admin.getJCPGatewaysExecNetwork(gwServerId, Integer.parseInt(item.id)));

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder();
        s.append(String.format("  Online . . . . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(online)));
        //s += String.format("  Process  . . . . %s\n", process....);
        s.append(String.format("  OS . . . . . . . %s (v. %s arch %s)\n", os.name, os.version, os.arch));
        s.append(String.format("  CPU load . . . . %.2f (#%d)\n", cpu.loadAvg, cpu.count));
        //s += String.format("  Memory . . . . . %s\n", memory....);
        s.append("  Disks:\n");
        s.append("    Name Free/Usable/Total\n");
        for (com.robypomper.josp.defs.admin.gateways.executable.Params20.Disk disk : disksList)
            s.append(String.format("    - %.10s   %dMB / %dMB  / %dMB\n", disk.name, disk.spaceFree, disk.spaceUsable, disk.spaceMax));
        s.append("  Networks:\n");
        s.append(String.format("    Main . . . . . %s (%s)\n", networks.addrLocalhost.getHostName(), networks.addrLocalhost.getHostAddress()));
        s.append(String.format("    Loopback . . . %s (%s)\n", networks.addrLoopback.getHostName(), networks.addrLoopback.getHostAddress()));
        for (com.robypomper.josp.defs.admin.gateways.executable.Params20.Network net : networksList) {
            s.append(String.format("    - %s (%s)\n", net.name, net.nameDisplay));
            s.append("      Address:\n");
            for (InetAddress addr : net.address)
                s.append(String.format("        %s (%s)\n", addr.getHostName(), addr.getHostAddress()));
            s.append("      Interfaces Address:\n");
            for (InterfaceAddress addr : net.addressIntf)
                s.append(String.format("        %s\n", addr.toString()));
        }
        return s.toString();
    }

    @Command(name = "Admin-JCP-Gateways-Executable-Java",
            abbrev = "admJCPGatewaysExecJava",
            description = "Print JCP Gateways Java's executable info",
            header = "JCP GATEWAYS JAVA EXECUTABLE INFO")
    public String adminJCPGatewaysExecJava(String gwServerId) {
        com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaVM vm;
        //com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaRuntime runtime;
        com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaTimes times;
        com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaClasses classes;
        com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaMemory memory;
        com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaThreads threads;
        try {
            vm = admin.getJCPGatewaysExecJavaVM(gwServerId);
            //runtime = admin.getJCPGatewaysExecJavaRuntime(gwServerId);
            times = admin.getJCPGatewaysExecJavaTimes(gwServerId);
            classes = admin.getJCPGatewaysExecJavaClasses(gwServerId);
            memory = admin.getJCPGatewaysExecJavaMemory(gwServerId);
            threads = admin.getJCPGatewaysExecJavaThreads(gwServerId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  VM . . . . . . . %s (v. %s)\n", vm.vmName, vm.vmVersion);
        s += String.format("  VM Spec  . . . . v. %s (vendor %s)\n", vm.specVersion, vm.specVendor);
        //s += String.format("  Runtime  . . . . %s\n", process....);
        s += String.format("  Started  . . . . %s (%s)\n", JavaDate.DEF_DATE_FORMATTER.format(times.timeStart), humanReadableFormat(times.timeRunning * 1000));
        s += String.format("  Classes  . . . . %d / %d / %d (Loaded/Loaded Total/Unloaded)\n", classes.classesLoaded, classes.classesLoadedTotal, classes.classesUnloaded);
        s += String.format("  Memory . . . . . %.2f / %.2f / %.2f (Init/Used/Committed)\n", memory.memoryInit, memory.memoryUsed, memory.memoryCommitted);
        s += String.format("  Memory Heap  . . %.2f / %.2f / %.2f / %.2f (Used/Free/Committed/Max)\n", memory.memoryHeapUsed, memory.memoryHeapFree, memory.memoryHeapCommitted, memory.memoryHeapMax);
        s += String.format("  Threads  . . . . %d (%d) / %d / %d Running (Daemon)/Peak/Total)\n", threads.threadsCount, threads.threadsCountDaemon, threads.threadsCountPeak, threads.threadsCountStarted);
        return s;
    }

    @Command(name = "Admin-JCP-Gateways-Executable-Java-Threads",
            abbrev = "admJCPGatewaysExecJavaThs",
            description = "Print JCP Gateways Java's executable threads list",
            header = "JCP GATEWAYS JAVA EXECUTABLE THREADS LIST")
    public String adminJCPGatewaysExecThreadsList(String gwServerId) {
        com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaThreads threads;
        List<com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaThread> threadsList = new ArrayList<>();
        try {
            threads = admin.getJCPGatewaysExecJavaThreads(gwServerId);
            for (RESTItemList item : threads.threadsList)
                threadsList.add(admin.getJCPGatewaysExecJavaThread(gwServerId, Long.parseLong(item.id)));

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }
        StringBuilder s = new StringBuilder("(ID) Name State");
        for (com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaThread thread : threadsList)
            s.append(String.format("  - (%6s) %20s %s\n", thread.id, thread.name, thread.state));
        return s.toString();
    }


    // JCP Gateways Build Info

    @Command(name = "Admin-JCP-Gateways-Build-Info",
            abbrev = "admJCPGatewaysBuild",
            description = "Print JCP Gateways build info",
            header = "JCP GATEWAYS BUILD INFO")
    public String adminJCPGatewaysBuildInfo(String gwServerId) {
        com.robypomper.josp.defs.admin.gateways.buildinfo.Params20.BuildInfo buildInfo;
        try {
            buildInfo = admin.getJCPGatewaysBuildInfo(gwServerId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Gateways status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Gateways status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Version  . . . . %s (build %s)\n", buildInfo.version, buildInfo.versionBuild);
        s += String.format("  Sources  . . . . ::%s:%s\n", buildInfo.project, buildInfo.sourceSet);
        s += String.format("  Git  . . . . . . %s (branch %s)\n", buildInfo.gitCommitShort, buildInfo.gitBranch);
        s += "  Build Process:";
        s += String.format("    Time . . . . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(buildInfo.buildTime));
        s += String.format("    Java . . . . . v. %s (path %s)\n", buildInfo.javaVersion, buildInfo.javaHome);
        s += String.format("    Gradle . . . . v. %s\n", buildInfo.gradleVersion);
        s += String.format("    User . . . . . %s\n", buildInfo.user);
        s += String.format("    OS . . . . . . %s (v. %s arch %s)\n", buildInfo.osName, buildInfo.osVersion, buildInfo.osArch);
        return s;
    }


    // JCP JSL Web Bridge Status

    @Command(name = "Admin-JCP-JSLWebBridge-Sessions-List",
            abbrev = "admJCPJSLWebBridgeSesssList",
            description = "Print JCP JSL Web Bridge sessions list",
            header = "JCP JSL WEB BRIDGE SESSIONS LIST")
    public String adminJCPJSLWebBridgeSessionsList() {
        com.robypomper.josp.defs.admin.jslwebbridge.status.Params20.Sessions sesssList;
        try {
            sesssList = admin.getJCPJSLWebBridgeSessions();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP JSL Web Bridge status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP JSL Web Bridge status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder("(ID) Name");
        for (RESTItemList item : sesssList.sessionsList)
            s.append(String.format("  - (%-20s) %s\n", item.id, item.name));
        return s.toString();
    }

    @Command(name = "Admin-JCP-JSLWebBridge-Objects-Details",
            abbrev = "admJCPJSLWebBridgeSesss",
            description = "Print JCP JSL Web Bridge session details",
            header = "JCP JSL WEB BRIDGE SESSION DETAILS")
    public String adminJCPJSLWebBridgeSessionDetails(String sessionId) {
        com.robypomper.josp.defs.admin.jslwebbridge.status.Params20.Session sess;
        try {
            sess = admin.getJCPJSLWebBridgeSession(sessionId);

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP JSL Web Bridge status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP JSL Web Bridge status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Id . . . . . . %s\n", sess.id);
        s += String.format("  Name . . . . . %s\n", sess.name);
        s += String.format("  Created at . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(sess.createdAt));
        s += String.format("  Updated at . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(sess.lastAccessedAt));
        s += String.format("  Updated at . . %s\n", humanReadableFormat(sess.maxInactiveInterval / 1000));
        return s;
    }


    // JCP JSL Web Bridge Executable

    @Command(name = "Admin-JCP-JSLWebBridge-Executable",
            abbrev = "admJCPJSLWebBridgeExec",
            description = "Print JCP JSL Web Bridge executable info",
            header = "JCP JSL WEB BRIDGE EXECUTABLE INFO")
    public String adminJCPJSLWebBridgeExec() {
        Date online;
        //com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Process process;
        com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.OS os;
        com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.CPU cpu;
        //com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Memory memory;
        com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Disks disks;
        List<com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Disk> disksList = new ArrayList<>();
        com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Networks networks;
        List<com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Network> networksList = new ArrayList<>();
        try {
            online = admin.getJCPJSLWebBridgeExecOnline();
            //process = admin.getJCPJSLWebBridgeExecProcess();
            os = admin.getJCPJSLWebBridgeExecOS();
            cpu = admin.getJCPJSLWebBridgeExecCPU();
            //memory = admin.getJCPJSLWebBridgeExecMemory();
            disks = admin.getJCPJSLWebBridgeExecDisks();
            for (RESTItemList item : disks.disksList)
                disksList.add(admin.getJCPJSLWebBridgeExecDisk(item.id));
            networks = admin.getJCPJSLWebBridgeExecNetworks();
            for (RESTItemList item : networks.networksList)
                networksList.add(admin.getJCPJSLWebBridgeExecNetwork(Integer.parseInt(item.id)));

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP JSL Web Bridge status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP JSL Web Bridge status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder();
        s.append(String.format("  Online . . . . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(online)));
        //s += String.format("  Process  . . . . %s\n", process....);
        s.append(String.format("  OS . . . . . . . %s (v. %s arch %s)\n", os.name, os.version, os.arch));
        s.append(String.format("  CPU load . . . . %.2f (#%d)\n", cpu.loadAvg, cpu.count));
        //s += String.format("  Memory . . . . . %s\n", memory....);
        s.append("  Disks:\n");
        s.append("    Name Free/Usable/Total\n");
        for (com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Disk disk : disksList)
            s.append(String.format("    - %.10s   %dMB / %dMB  / %dMB\n", disk.name, disk.spaceFree, disk.spaceUsable, disk.spaceMax));
        s.append("  Networks:\n");
        s.append(String.format("    Main . . . . . %s (%s)\n", networks.addrLocalhost.getHostName(), networks.addrLocalhost.getHostAddress()));
        s.append(String.format("    Loopback . . . %s (%s)\n", networks.addrLoopback.getHostName(), networks.addrLoopback.getHostAddress()));
        for (com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Network net : networksList) {
            s.append(String.format("    - %s (%s)\n", net.name, net.nameDisplay));
            s.append("      Address:\n");
            for (InetAddress addr : net.address)
                s.append(String.format("        %s (%s)\n", addr.getHostName(), addr.getHostAddress()));
            s.append("      Interfaces Address:\n");
            for (InterfaceAddress addr : net.addressIntf)
                s.append(String.format("        %s\n", addr.toString()));
        }
        return s.toString();
    }

    @Command(name = "Admin-JCP-JSLWebBridge-Executable-Java",
            abbrev = "admJCPJSLWebBridgeExecJava",
            description = "Print JCP JSL Web Bridge Java's executable info",
            header = "JCP JSL WEB BRIDGE JAVA EXECUTABLE INFO")
    public String adminJCPJSLWebBridgeExecJava() {
        com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaVM vm;
        //com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaRuntime runtime;
        com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaTimes times;
        com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaClasses classes;
        com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaMemory memory;
        com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaThreads threads;
        try {
            vm = admin.getJCPJSLWebBridgeExecJavaVM();
            //runtime = admin.getJCPJSLWebBridgeExecJavaRuntime();
            times = admin.getJCPJSLWebBridgeExecJavaTimes();
            classes = admin.getJCPJSLWebBridgeExecJavaClasses();
            memory = admin.getJCPJSLWebBridgeExecJavaMemory();
            threads = admin.getJCPJSLWebBridgeExecJavaThreads();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP JSL Web Bridge status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP JSL Web Bridge status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  VM . . . . . . . %s (v. %s)\n", vm.vmName, vm.vmVersion);
        s += String.format("  VM Spec  . . . . v. %s (vendor %s)\n", vm.specVersion, vm.specVendor);
        //s += String.format("  Runtime  . . . . %s\n", process....);
        s += String.format("  Started  . . . . %s (%s)\n", JavaDate.DEF_DATE_FORMATTER.format(times.timeStart), humanReadableFormat(times.timeRunning * 1000));
        s += String.format("  Classes  . . . . %d / %d / %d (Loaded/Loaded Total/Unloaded)\n", classes.classesLoaded, classes.classesLoadedTotal, classes.classesUnloaded);
        s += String.format("  Memory . . . . . %.2f / %.2f / %.2f (Init/Used/Committed)\n", memory.memoryInit, memory.memoryUsed, memory.memoryCommitted);
        s += String.format("  Memory Heap  . . %.2f / %.2f / %.2f / %.2f (Used/Free/Committed/Max)\n", memory.memoryHeapUsed, memory.memoryHeapFree, memory.memoryHeapCommitted, memory.memoryHeapMax);
        s += String.format("  Threads  . . . . %d (%d) / %d / %d Running (Daemon)/Peak/Total)\n", threads.threadsCount, threads.threadsCountDaemon, threads.threadsCountPeak, threads.threadsCountStarted);
        return s;
    }

    @Command(name = "Admin-JCP-JSLWebBridge-Executable-Java-Threads",
            abbrev = "admJCPJSLWebBridgeExecJavaThs",
            description = "Print JCP JSL Web Bridge Java's executable threads list",
            header = "JCP JSL WEB BRIDGE JAVA EXECUTABLE THREADS LIST")
    public String adminJCPJSLWebBridgeExecThreadsList() {
        com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaThreads threads;
        List<com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaThread> threadsList = new ArrayList<>();
        try {
            threads = admin.getJCPJSLWebBridgeExecJavaThreads();
            for (RESTItemList item : threads.threadsList)
                threadsList.add(admin.getJCPJSLWebBridgeExecJavaThread(Long.parseLong(item.id)));

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP JSL Web Bridge status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP JSL Web Bridge status' because '%s'", e.getMessage());
        }
        StringBuilder s = new StringBuilder("(ID) Name State");
        for (com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaThread thread : threadsList)
            s.append(String.format("  - (%6s) %20s %s\n", thread.id, thread.name, thread.state));
        return s.toString();
    }


    // JCP JSL Web Bridge Build Info

    @Command(name = "Admin-JCP-JSLWebBridge-Build-Info",
            abbrev = "admJCPJSLWebBridgeBuild",
            description = "Print JCP JSL Web Bridge build info",
            header = "JCP JSL WEB BRIDGE BUILD INFO")
    public String adminJCPJSLWebBridgeBuildInfo() {
        com.robypomper.josp.defs.admin.jslwebbridge.buildinfo.Params20.BuildInfo buildInfo;
        try {
            buildInfo = admin.getJCPJSLWebBridgeBuildInfo();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP JSL Web Bridge status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP JSL Web Bridge status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Version  . . . . %s (build %s)\n", buildInfo.version, buildInfo.versionBuild);
        s += String.format("  Sources  . . . . ::%s:%s\n", buildInfo.project, buildInfo.sourceSet);
        s += String.format("  Git  . . . . . . %s (branch %s)\n", buildInfo.gitCommitShort, buildInfo.gitBranch);
        s += "  Build Process:";
        s += String.format("    Time . . . . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(buildInfo.buildTime));
        s += String.format("    Java . . . . . v. %s (path %s)\n", buildInfo.javaVersion, buildInfo.javaHome);
        s += String.format("    Gradle . . . . v. %s\n", buildInfo.gradleVersion);
        s += String.format("    User . . . . . %s\n", buildInfo.user);
        s += String.format("    OS . . . . . . %s (v. %s arch %s)\n", buildInfo.osName, buildInfo.osVersion, buildInfo.osArch);
        return s;
    }


    // JCP Front End Status

    // N/A


    // JCP Front End Executable

    @Command(name = "Admin-JCP-FrontEnd-Executable",
            abbrev = "admJCPFrontEndExec",
            description = "Print JCP Front End executable info",
            header = "JCP FRONT END EXECUTABLE INFO")
    public String adminJCPFrontEndExec() {
        Date online;
        //com.robypomper.josp.defs.admin.frontend.executable.Params20.Process process;
        com.robypomper.josp.defs.admin.frontend.executable.Params20.OS os;
        com.robypomper.josp.defs.admin.frontend.executable.Params20.CPU cpu;
        //com.robypomper.josp.defs.admin.frontend.executable.Params20.Memory memory;
        com.robypomper.josp.defs.admin.frontend.executable.Params20.Disks disks;
        List<com.robypomper.josp.defs.admin.frontend.executable.Params20.Disk> disksList = new ArrayList<>();
        com.robypomper.josp.defs.admin.frontend.executable.Params20.Networks networks;
        List<com.robypomper.josp.defs.admin.frontend.executable.Params20.Network> networksList = new ArrayList<>();
        try {
            online = admin.getJCPFrontEndExecOnline();
            //process = admin.getJCPFrontEndExecProcess();
            os = admin.getJCPFrontEndExecOS();
            cpu = admin.getJCPFrontEndExecCPU();
            //memory = admin.getJCPFrontEndExecMemory();
            disks = admin.getJCPFrontEndExecDisks();
            for (RESTItemList item : disks.disksList)
                disksList.add(admin.getJCPFrontEndExecDisk(item.id));
            networks = admin.getJCPFrontEndExecNetworks();
            for (RESTItemList item : networks.networksList)
                networksList.add(admin.getJCPFrontEndExecNetwork(Integer.parseInt(item.id)));

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Front End status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Front End status' because '%s'", e.getMessage());
        }

        StringBuilder s = new StringBuilder();
        s.append(String.format("  Online . . . . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(online)));
        //s += String.format("  Process  . . . . %s\n", process....);
        s.append(String.format("  OS . . . . . . . %s (v. %s arch %s)\n", os.name, os.version, os.arch));
        s.append(String.format("  CPU load . . . . %.2f (#%d)\n", cpu.loadAvg, cpu.count));
        //s += String.format("  Memory . . . . . %s\n", memory....);
        s.append("  Disks:\n");
        s.append("    Name Free/Usable/Total\n");
        for (com.robypomper.josp.defs.admin.frontend.executable.Params20.Disk disk : disksList)
            s.append(String.format("    - %.10s   %dMB / %dMB  / %dMB\n", disk.name, disk.spaceFree, disk.spaceUsable, disk.spaceMax));
        s.append("  Networks:\n");
        s.append(String.format("    Main . . . . . %s (%s)\n", networks.addrLocalhost.getHostName(), networks.addrLocalhost.getHostAddress()));
        s.append(String.format("    Loopback . . . %s (%s)\n", networks.addrLoopback.getHostName(), networks.addrLoopback.getHostAddress()));
        for (com.robypomper.josp.defs.admin.frontend.executable.Params20.Network net : networksList) {
            s.append(String.format("    - %s (%s)\n", net.name, net.nameDisplay));
            s.append("      Address:\n");
            for (InetAddress addr : net.address)
                s.append(String.format("        %s (%s)\n", addr.getHostName(), addr.getHostAddress()));
            s.append("      Interfaces Address:\n");
            for (InterfaceAddress addr : net.addressIntf)
                s.append(String.format("        %s\n", addr.toString()));
        }
        return s.toString();
    }

    @Command(name = "Admin-JCP-FrontEnd-Executable-Java",
            abbrev = "admJCPFrontEndExecJava",
            description = "Print JCP Front End Java's executable info",
            header = "JCP FRONT END JAVA EXECUTABLE INFO")
    public String adminJCPFrontEndExecJava() {
        com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaVM vm;
        //com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaRuntime runtime;
        com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaTimes times;
        com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaClasses classes;
        com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaMemory memory;
        com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaThreads threads;
        try {
            vm = admin.getJCPFrontEndExecJavaVM();
            //runtime = admin.getJCPFrontEndExecJavaRuntime();
            times = admin.getJCPFrontEndExecJavaTimes();
            classes = admin.getJCPFrontEndExecJavaClasses();
            memory = admin.getJCPFrontEndExecJavaMemory();
            threads = admin.getJCPFrontEndExecJavaThreads();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Front End status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Front End status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  VM . . . . . . . %s (v. %s)\n", vm.vmName, vm.vmVersion);
        s += String.format("  VM Spec  . . . . v. %s (vendor %s)\n", vm.specVersion, vm.specVendor);
        //s += String.format("  Runtime  . . . . %s\n", process....);
        s += String.format("  Started  . . . . %s (%s)\n", JavaDate.DEF_DATE_FORMATTER.format(times.timeStart), humanReadableFormat(times.timeRunning * 1000));
        s += String.format("  Classes  . . . . %d / %d / %d (Loaded/Loaded Total/Unloaded)\n", classes.classesLoaded, classes.classesLoadedTotal, classes.classesUnloaded);
        s += String.format("  Memory . . . . . %.2f / %.2f / %.2f (Init/Used/Committed)\n", memory.memoryInit, memory.memoryUsed, memory.memoryCommitted);
        s += String.format("  Memory Heap  . . %.2f / %.2f / %.2f / %.2f (Used/Free/Committed/Max)\n", memory.memoryHeapUsed, memory.memoryHeapFree, memory.memoryHeapCommitted, memory.memoryHeapMax);
        s += String.format("  Threads  . . . . %d (%d) / %d / %d Running (Daemon)/Peak/Total)\n", threads.threadsCount, threads.threadsCountDaemon, threads.threadsCountPeak, threads.threadsCountStarted);
        return s;
    }

    @Command(name = "Admin-JCP-FrontEnd-Executable-Java-Threads",
            abbrev = "admJCPFrontEndExecJavaThs",
            description = "Print JCP Front End Java's executable threads list",
            header = "JCP FRONT END JAVA EXECUTABLE THREADS LIST")
    public String adminJCPFrontEndExecThreadsList() {
        com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaThreads threads;
        List<com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaThread> threadsList = new ArrayList<>();
        try {
            threads = admin.getJCPFrontEndExecJavaThreads();
            for (RESTItemList item : threads.threadsList)
                threadsList.add(admin.getJCPFrontEndExecJavaThread(Long.parseLong(item.id)));

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Front End status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Front End status' because '%s'", e.getMessage());
        }
        StringBuilder s = new StringBuilder("(ID) Name State");
        for (com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaThread thread : threadsList)
            s.append(String.format("  - (%6s) %20s %s\n", thread.id, thread.name, thread.state));
        return s.toString();
    }


    // JCP Front End Info

    @Command(name = "Admin-JCP-FrontEnd-Build-Info",
            abbrev = "admJCPFrontEndBuild",
            description = "Print JCP Front End build info",
            header = "JCP FRONT END BUILD INFO")
    public String adminJCPFrontEndBuildInfo() {
        com.robypomper.josp.defs.admin.frontend.buildinfo.Params20.BuildInfo buildInfo;
        try {
            buildInfo = admin.getJCPFrontEndBuildInfo();

        } catch (JSLAdmin.UserNotAuthException | JSLAdmin.UserNotAdminException e) {
            return String.format("Current user can't access to 'JCP Front End status' because '%s'", e.getMessage());

        } catch (JCPClient2.ResponseException | JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException e) {
            return String.format("Client error on access to 'JCP Front End status' because '%s'", e.getMessage());
        }

        String s = "";
        s += String.format("  Version  . . . . %s (build %s)\n", buildInfo.version, buildInfo.versionBuild);
        s += String.format("  Sources  . . . . ::%s:%s\n", buildInfo.project, buildInfo.sourceSet);
        s += String.format("  Git  . . . . . . %s (branch %s)\n", buildInfo.gitCommitShort, buildInfo.gitBranch);
        s += "  Build Process:";
        s += String.format("    Time . . . . . %s\n", JavaDate.DEF_DATE_FORMATTER.format(buildInfo.buildTime));
        s += String.format("    Java . . . . . v. %s (path %s)\n", buildInfo.javaVersion, buildInfo.javaHome);
        s += String.format("    Gradle . . . . v. %s\n", buildInfo.gradleVersion);
        s += String.format("    User . . . . . %s\n", buildInfo.user);
        s += String.format("    OS . . . . . . %s (v. %s arch %s)\n", buildInfo.osName, buildInfo.osVersion, buildInfo.osArch);
        return s;
    }


    // Utils

    private static String humanReadableFormat(long millis) {
        Duration duration = Duration.ofMillis(millis);
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

}
