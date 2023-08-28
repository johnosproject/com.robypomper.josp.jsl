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

package com.robypomper.josp.jsl.admin;


import com.robypomper.josp.clients.JCPClient2;

import java.util.Date;

/**
 * Interface for JSL Services info system.
 * <p>
 * This system collect all service's info and provide them to other JSL's systems.
 * <p>
 * JSLServiceInfo implementations can access to the JCP API and JSL settings file
 * to load and store values of Service's info.
 */
public interface JSLAdmin {

    // JCP APIs status

    com.robypomper.josp.defs.admin.apis.status.Params20.Objects getJCPAPIsObjects() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.status.Params20.Object getJCPAPIsObject(String objId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.status.Params20.Services getJCPAPIsServices() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.status.Params20.Service getJCPAPIsService(String srvId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.status.Params20.Users getJCPAPIsUsers() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.status.Params20.User getJCPAPIsUser(String usrId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.status.Params20.Gateways getJCPAPIsGateways() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.status.Params20.Gateway getJCPAPIsGateway(String gwId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP APIs Executable

    Date getJCPAPIsExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.Process getJCPAPIsExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.JavaVM getJCPAPIsExecJavaVM() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.JavaRuntime getJCPAPIsExecJavaRuntime() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.JavaTimes getJCPAPIsExecJavaTimes() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.JavaClasses getJCPAPIsExecJavaClasses() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.JavaMemory getJCPAPIsExecJavaMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.JavaThreads getJCPAPIsExecJavaThreads() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.JavaThread getJCPAPIsExecJavaThread(long threadId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.OS getJCPAPIsExecOS() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.CPU getJCPAPIsExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.Memory getJCPAPIsExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.Disks getJCPAPIsExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.Disk getJCPAPIsExecDisk(String diskId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.Networks getJCPAPIsExecNetworks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.apis.executable.Params20.Network getJCPAPIsExecNetwork(int networkId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP APIs Build Info

    com.robypomper.josp.defs.admin.apis.buildinfo.Params20.BuildInfo getJCPAPIsBuildInfo() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP Gateways status

    com.robypomper.josp.defs.admin.gateways.status.Params20.GatewaysServers getJCPGatewaysServers() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.status.Params20.GWs getJCPGatewaysGWs(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.status.Params20.GW getJCPGatewaysGW(String gwServerId, String gwId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.status.Params20.GWClient getJCPGatewaysGWsClient(String gwServerId, String gwId, String gwClientId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.status.Params20.Broker getJCPGatewaysBroker(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.status.Params20.BrokerObject getJCPGatewaysBrokerObject(String gwServerId, String objId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.status.Params20.BrokerService getJCPGatewaysBrokerService(String gwServerId, String srvId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.status.Params20.BrokerObjectDB getJCPGatewaysBrokerObjectDB(String gwServerId, String objId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP Gateways Executable

    Date getJCPGatewaysExecOnline(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.Process getJCPGatewaysExecProcess(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaVM getJCPGatewaysExecJavaVM(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaRuntime getJCPGatewaysExecJavaRuntime(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaTimes getJCPGatewaysExecJavaTimes(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaClasses getJCPGatewaysExecJavaClasses(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaMemory getJCPGatewaysExecJavaMemory(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaThreads getJCPGatewaysExecJavaThreads(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.JavaThread getJCPGatewaysExecJavaThread(String gwServerId, long threadId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.OS getJCPGatewaysExecOS(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.CPU getJCPGatewaysExecCPU(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.Memory getJCPGatewaysExecMemory(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.Disks getJCPGatewaysExecDisks(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.Disk getJCPGatewaysExecDisk(String gwServerId, String diskId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.Networks getJCPGatewaysExecNetworks(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.gateways.executable.Params20.Network getJCPGatewaysExecNetwork(String gwServerId, int networkId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP Gateways Build Info

    com.robypomper.josp.defs.admin.gateways.buildinfo.Params20.BuildInfo getJCPGatewaysBuildInfo(String gwServerId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP JSL Web Bridge status

    com.robypomper.josp.defs.admin.jslwebbridge.status.Params20.Sessions getJCPJSLWebBridgeSessions() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.status.Params20.Session getJCPJSLWebBridgeSession(String sessionId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP JSL Web Bridge Executable

    Date getJCPJSLWebBridgeExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Process getJCPJSLWebBridgeExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaVM getJCPJSLWebBridgeExecJavaVM() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaRuntime getJCPJSLWebBridgeExecJavaRuntime() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaTimes getJCPJSLWebBridgeExecJavaTimes() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaClasses getJCPJSLWebBridgeExecJavaClasses() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaMemory getJCPJSLWebBridgeExecJavaMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaThreads getJCPJSLWebBridgeExecJavaThreads() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.JavaThread getJCPJSLWebBridgeExecJavaThread(long threadId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.OS getJCPJSLWebBridgeExecOS() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.CPU getJCPJSLWebBridgeExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Memory getJCPJSLWebBridgeExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Disks getJCPJSLWebBridgeExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Disk getJCPJSLWebBridgeExecDisk(String diskId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Networks getJCPJSLWebBridgeExecNetworks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.jslwebbridge.executable.Params20.Network getJCPJSLWebBridgeExecNetwork(int networkId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP JSL Web Bridge Build Info

    com.robypomper.josp.defs.admin.jslwebbridge.buildinfo.Params20.BuildInfo getJCPJSLWebBridgeBuildInfo() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP Front End status

    //com.robypomper.josp.defs.admin.frontend.status.Params20.... getJCPFrontEnd...() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP Front End Executable

    Date getJCPFrontEndExecOnline() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.Process getJCPFrontEndExecProcess() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaVM getJCPFrontEndExecJavaVM() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaRuntime getJCPFrontEndExecJavaRuntime() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaTimes getJCPFrontEndExecJavaTimes() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaClasses getJCPFrontEndExecJavaClasses() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaMemory getJCPFrontEndExecJavaMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaThreads getJCPFrontEndExecJavaThreads() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.JavaThread getJCPFrontEndExecJavaThread(long threadId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.OS getJCPFrontEndExecOS() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.CPU getJCPFrontEndExecCPU() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.Memory getJCPFrontEndExecMemory() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.Disks getJCPFrontEndExecDisks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.Disk getJCPFrontEndExecDisk(String diskId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.Networks getJCPFrontEndExecNetworks() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;

    com.robypomper.josp.defs.admin.frontend.executable.Params20.Network getJCPFrontEndExecNetwork(int networkId) throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // JCP Front End Build Info

    com.robypomper.josp.defs.admin.frontend.buildinfo.Params20.BuildInfo getJCPFrontEndBuildInfo() throws UserNotAuthException, UserNotAdminException, JCPClient2.ConnectionException, JCPClient2.ResponseException, JCPClient2.RequestException, JCPClient2.AuthenticationException;


    // Exceptions

    class UserNotAuthException extends Throwable {

        // Class constants

        private static final String MSG = "User not authenticated can't access to '%s' resource";

        // Internal vars

        private final String resource;


        // Constructors

        public UserNotAuthException(String resource) {
            super(String.format(MSG, resource));
            this.resource = resource;
        }


        // Getters

        public String getResource() {
            return resource;
        }

    }

    class UserNotAdminException extends Throwable {

        // Class constants

        private static final String MSG = "User '%s' (%s) not authorized to access '%s' resource";

        // Internal vars

        private final String usrId;
        private final String usrName;
        private final String resource;


        // Constructors

        public UserNotAdminException(String usrId, String usrName, String resource) {
            super(String.format(MSG, usrId, usrName, resource));
            this.usrId = usrId;
            this.usrName = usrName;
            this.resource = resource;
        }


        // Getters

        public String getUsrId() {
            return usrId;
        }

        public String getUsrName() {
            return usrName;
        }

        public String getResource() {
            return resource;
        }

    }

}
