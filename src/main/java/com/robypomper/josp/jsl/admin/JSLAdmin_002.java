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

package com.robypomper.josp.jsl.admin;

import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 *
 */
public class JSLAdmin_002 implements JSLAdmin {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLAdmin_002.class);
    private final JSLSettings_002 locSettings;
    private final JCPAPIsClientSrv jcpClient;
    private final JSLUserMngr userMngr;


    // Constructor

    public JSLAdmin_002(JSLSettings_002 settings, JCPAPIsClientSrv jcpClient, JSLUserMngr userMngr) {
        this.locSettings = settings;
        this.jcpClient = jcpClient;
        this.userMngr = userMngr;

        log.info("Initialized JSLAdmin instance");
    }


    // User's checks

    public void checkUserIsAdmin(String resource) throws UserNotAuthException, UserNotAdminException {
        if (!userMngr.isUserAuthenticated())
            throw new UserNotAuthException(resource);

        if (!userMngr.isAdmin())
            throw new UserNotAdminException(userMngr.getUserId(), userMngr.getUsername(), resource);
    }


    // JCP APIs status

    public com.robypomper.josp.defs.admin.apis.status.Params20.Objects getJCPAPIsObjects() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs objects");
        return new com.robypomper.josp.callers.apis.admin.apis.status.Caller20(jcpClient).getObjectsReq();
    }

    public com.robypomper.josp.defs.admin.apis.status.Params20.Object getJCPAPIsObject(String objId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs object");
        return new com.robypomper.josp.callers.apis.admin.apis.status.Caller20(jcpClient).getObjectReq(objId);
    }

    public com.robypomper.josp.defs.admin.apis.status.Params20.Services getJCPAPIsServices() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs services");
        return new com.robypomper.josp.callers.apis.admin.apis.status.Caller20(jcpClient).getServicesReq();
    }

    public com.robypomper.josp.defs.admin.apis.status.Params20.Service getJCPAPIsService(String srvId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs service");
        return new com.robypomper.josp.callers.apis.admin.apis.status.Caller20(jcpClient).getServiceReq(srvId);
    }

    public com.robypomper.josp.defs.admin.apis.status.Params20.Users getJCPAPIsUsers() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs users");
        return new com.robypomper.josp.callers.apis.admin.apis.status.Caller20(jcpClient).getUsersReq();
    }

    public com.robypomper.josp.defs.admin.apis.status.Params20.User getJCPAPIsUser(String usrId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs user");
        return new com.robypomper.josp.callers.apis.admin.apis.status.Caller20(jcpClient).getUserReq(usrId);
    }

    public com.robypomper.josp.defs.admin.apis.status.Params20.Gateways getJCPAPIsGateways() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs gateways");
        return new com.robypomper.josp.callers.apis.admin.apis.status.Caller20(jcpClient).getGatewaysReq();
    }

    public com.robypomper.josp.defs.admin.apis.status.Params20.Gateway getJCPAPIsGateway(String gwId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs gateway");
        return new com.robypomper.josp.callers.apis.admin.apis.status.Caller20(jcpClient).getGatewayReq(gwId);
    }


    // JCP APIs Executable

    public Date getJCPAPIsExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs online executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getOnlineReq();
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.Process getJCPAPIsExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs process executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getProcessReq();
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.JavaVM getJCPAPIsExecJavaVM() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Java VM executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getJavaVMReq();
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.JavaRuntime getJCPAPIsExecJavaRuntime() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Java runtime executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getJavaRuntimeReq();
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.JavaTimes getJCPAPIsExecJavaTimes() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Java times executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getJavaTimesReq();
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.JavaClasses getJCPAPIsExecJavaClasses() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Java classes executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getJavaClassesReq();
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.JavaMemory getJCPAPIsExecJavaMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Java memory executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getJavaMemoryReq();
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.JavaThreads getJCPAPIsExecJavaThreads() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Java threads executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getJavaThreadsReq();
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.JavaThread getJCPAPIsExecJavaThread(long threadId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs Java thread executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getJavaThreadReq(threadId);
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.OS getJCPAPIsExecOS() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs os executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getOSReq();
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.CPU getJCPAPIsExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs cpu executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getCPUReq();
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.Memory getJCPAPIsExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs memory executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getMemoryReq();
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.Disks getJCPAPIsExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs disks executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getDisksReq();
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.Disk getJCPAPIsExecDisk(String diskId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs disk executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getDiskReq(diskId);
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.Networks getJCPAPIsExecNetworks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs networks executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getNetworksReq();
    }

    public com.robypomper.josp.defs.admin.apis.executable.Params20.Network getJCPAPIsExecNetwork(int networkId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs network executable status");
        return new com.robypomper.josp.callers.apis.admin.apis.executable.Caller20(jcpClient).getNetworkReq(networkId);
    }


    // JCP APIs Build Info

    public com.robypomper.josp.defs.admin.apis.buildinfo.Params20.BuildInfo getJCPAPIsBuildInfo() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP APIs build info");
        return new com.robypomper.josp.callers.apis.admin.apis.buildinfo.Caller20(jcpClient).getBuildInfoReq();
    }


    // JCP Gateways list

    public com.robypomper.josp.defs.admin.gateways.status.Params20.GatewaysServers getJCPGatewaysServers() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways gateways list");
        return new com.robypomper.josp.callers.apis.admin.gateways.status.Caller20(jcpClient).getGWsListReq();      // status
        //return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getList();      // executable
        //return new com.robypomper.josp.callers.apis.admin.gateways.buildinfo.Caller20(jcpClient).getList();       // build info
    }


    // JCP Gateways status

    public com.robypomper.josp.defs.admin.gateways.status.Params20.GWs getJCPGatewaysGWs(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways gateways");
        return new com.robypomper.josp.callers.apis.admin.gateways.status.Caller20(jcpClient).getGWsReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.status.Params20.GW getJCPGatewaysGW(String gwServerId, String gwId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways gateway");
        return new com.robypomper.josp.callers.apis.admin.gateways.status.Caller20(jcpClient).getGWReq(gwServerId, gwId);
    }

    public com.robypomper.josp.defs.admin.gateways.status.Params20.GWClient getJCPGatewaysGWsClient(String gwServerId, String gwId, String gwClientId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways gateway client");
        return new com.robypomper.josp.callers.apis.admin.gateways.status.Caller20(jcpClient).getGWsClientReq(gwServerId, gwId, gwClientId);
    }

    public com.robypomper.josp.defs.admin.gateways.status.Params20.Broker getJCPGatewaysBroker(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways broker");
        return new com.robypomper.josp.callers.apis.admin.gateways.status.Caller20(jcpClient).getBrokerReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.status.Params20.BrokerObject getJCPGatewaysBrokerObject(String gwServerId,String objId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways broker's object");
        return new com.robypomper.josp.callers.apis.admin.gateways.status.Caller20(jcpClient).getBrokerObjectReq(gwServerId,objId);
    }

    public com.robypomper.josp.defs.admin.gateways.status.Params20.BrokerService getJCPGatewaysBrokerService(String gwServerId,String srvId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways broker's service");
        return new com.robypomper.josp.callers.apis.admin.gateways.status.Caller20(jcpClient).getBrokerServiceReq(gwServerId,srvId);
    }

    public com.robypomper.josp.defs.admin.gateways.status.Params20.BrokerObjectDB getJCPGatewaysBrokerObjectDB(String gwServerId,String objId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways broker's object db");
        return new com.robypomper.josp.callers.apis.admin.gateways.status.Caller20(jcpClient).getBrokerObjectDBReq(gwServerId,objId);
    }


    // JCP Gateways Executable

    public Date getJCPGatewaysExecOnline(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways online executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getOnlineReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.Process getJCPGatewaysExecProcess(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways process executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getProcessReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaVM getJCPGatewaysExecJavaVM(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways Java VM executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getJavaVMReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaRuntime getJCPGatewaysExecJavaRuntime(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways Java runtime executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getJavaRuntimeReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaTimes getJCPGatewaysExecJavaTimes(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways Java times executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getJavaTimesReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaClasses getJCPGatewaysExecJavaClasses(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways Java classes executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getJavaClassesReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaMemory getJCPGatewaysExecJavaMemory(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways Java memory executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getJavaMemoryReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaThreads getJCPGatewaysExecJavaThreads(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways Java threads executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getJavaThreadsReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaThread getJCPGatewaysExecJavaThread(String gwServerId, long threadId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways Java thread executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getJavaThreadReq(gwServerId, threadId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.OS getJCPGatewaysExecOS(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways os executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getOSReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.CPU getJCPGatewaysExecCPU(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways cpu executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getCPUReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.Memory getJCPGatewaysExecMemory(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways memory executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getMemoryReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.Disks getJCPGatewaysExecDisks(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways disks executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getDisksReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.Disk getJCPGatewaysExecDisk(String gwServerId, String diskId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways disk executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getDiskReq(gwServerId, diskId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.Networks getJCPGatewaysExecNetworks(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways networks executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getNetworksReq(gwServerId);
    }

    public com.robypomper.josp.defs.admin.gateways.executable.Params20.Network getJCPGatewaysExecNetwork(String gwServerId, int networkId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways network executable status");
        return new com.robypomper.josp.callers.apis.admin.gateways.executable.Caller20(jcpClient).getNetworkReq(gwServerId, networkId);
    }


    // JCP Gateways Build Info

    public com.robypomper.josp.defs.admin.gateways.buildinfo.Params20.BuildInfo getJCPGatewaysBuildInfo(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Gateways build info");
        return new com.robypomper.josp.callers.apis.admin.gateways.buildinfo.Caller20(jcpClient).getBuildInfoReq(gwServerId);
    }


    // JCP JSL Web Bridge status

    public com.robypomper.josp.defs.admin.jslwebbridge.status.Params20.Sessions getJCPJSLWebBridgeSessions() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge sessions");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.status.Caller20(jcpClient).getJCPJSLWBStatusSessions();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.status.Params20.Session getJCPJSLWebBridgeSession(String sessionId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge session");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.status.Caller20(jcpClient).getJCPJSLWBStatusSession(sessionId);
    }


    // JCP JSL Web Bridge Executable

    public Date getJCPJSLWebBridgeExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge online executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getOnlineReq();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Process getJCPJSLWebBridgeExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge process executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getProcessReq();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaVM getJCPJSLWebBridgeExecJavaVM() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Java VM executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getJavaVMReq();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaRuntime getJCPJSLWebBridgeExecJavaRuntime() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Java runtime executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getJavaRuntimeReq();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaTimes getJCPJSLWebBridgeExecJavaTimes() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Java times executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getJavaTimesReq();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaClasses getJCPJSLWebBridgeExecJavaClasses() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Java classes executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getJavaClassesReq();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaMemory getJCPJSLWebBridgeExecJavaMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Java memory executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getJavaMemoryReq();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaThreads getJCPJSLWebBridgeExecJavaThreads() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Java threads executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getJavaThreadsReq();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaThread getJCPJSLWebBridgeExecJavaThread(long threadId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge Java thread executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getJavaThreadReq(threadId);
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.OS getJCPJSLWebBridgeExecOS() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge os executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getOSReq();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.CPU getJCPJSLWebBridgeExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge cpu executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getCPUReq();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Memory getJCPJSLWebBridgeExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge memory executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getMemoryReq();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Disks getJCPJSLWebBridgeExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge disks executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getDisksReq();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Disk getJCPJSLWebBridgeExecDisk(String diskId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge disk executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getDiskReq(diskId);
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Networks getJCPJSLWebBridgeExecNetworks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge networks executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getNetworksReq();
    }

    public com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Network getJCPJSLWebBridgeExecNetwork(int networkId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge network executable status");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.executable.Caller20(jcpClient).getNetworkReq(networkId);
    }


    // JCP JSL Web Bridge Build Info

    public com.robypomper.josp.defs.admin.jslwebbridge.buildinfo.Params20.BuildInfo getJCPJSLWebBridgeBuildInfo() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP JSL Web Bridge build info");
        return new com.robypomper.josp.callers.apis.admin.jslwebbridge.buildinfo.Caller20(jcpClient).getBuildInfoReq();
    }


    // JCP Front End status

    //public com.robypomper.josp.defs.admin.frontend.status.Params20.... getJCPFrontEnd...() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
    //    checkUserIsAdmin("JCP Front End ... status");
    //    return new com.robypomper.josp.callers.apis.admin.frontend.status.Caller20(jcpClient).getObjectsReq();
    //}


    // JCP Front End Executable

    public Date getJCPFrontEndExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End online executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getOnlineReq();
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.Process getJCPFrontEndExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End process executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getProcessReq();
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaVM getJCPFrontEndExecJavaVM() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Java VM executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getJavaVMReq();
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaRuntime getJCPFrontEndExecJavaRuntime() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Java runtime executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getJavaRuntimeReq();
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaTimes getJCPFrontEndExecJavaTimes() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Java times executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getJavaTimesReq();
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaClasses getJCPFrontEndExecJavaClasses() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Java classes executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getJavaClassesReq();
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaMemory getJCPFrontEndExecJavaMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Java memory executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getJavaMemoryReq();
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaThreads getJCPFrontEndExecJavaThreads() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Java threads executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getJavaThreadsReq();
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaThread getJCPFrontEndExecJavaThread(long threadId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End Java thread executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getJavaThreadReq(threadId);
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.OS getJCPFrontEndExecOS() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End os executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getOSReq();
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.CPU getJCPFrontEndExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End cpu executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getCPUReq();
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.Memory getJCPFrontEndExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End memory executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getMemoryReq();
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.Disks getJCPFrontEndExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End disks executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getDisksReq();
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.Disk getJCPFrontEndExecDisk(String diskId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End disk executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getDiskReq(diskId);
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.Networks getJCPFrontEndExecNetworks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End networks executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getNetworksReq();
    }

    public com.robypomper.josp.defs.admin.frontend.executable.Params20.Network getJCPFrontEndExecNetwork(int networkId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End network executable status");
        return new com.robypomper.josp.callers.apis.admin.frontend.executable.Caller20(jcpClient).getNetworkReq(networkId);
    }


    // JCP Front End Build Info

    public com.robypomper.josp.defs.admin.frontend.buildinfo.Params20.BuildInfo getJCPFrontEndBuildInfo() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException {
        checkUserIsAdmin("JCP Front End build info");
        return new com.robypomper.josp.callers.apis.admin.frontend.buildinfo.Caller20(jcpClient).getBuildInfoReq();
    }

}
