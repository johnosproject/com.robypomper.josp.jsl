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

package com.robypomper.josp.callers.apis.admin.gateways.executable;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPISrv;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.defs.admin.gateways.executable.Params20;
import com.robypomper.josp.defs.admin.gateways.executable.Paths20;

import java.util.Date;


/**
 * JOSP Admin - Gateways / Executable 2.0
 */
public class Caller20 extends AbsAPISrv {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public Caller20(JCPAPIsClientSrv jcpClient) {
        super(jcpClient);
    }


    // Index methods

    public Params20.GatewaysServers getList() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_LIST, Params20.GatewaysServers.class, isSecure());
    }


    // Index methods

    public Params20.Index getIndex(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC(gwServerId), Params20.Index.class, isSecure());
    }


    // Online methods

    public Date getOnlineReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_ONLINE(gwServerId), Date.class, isSecure());
    }


    // Process methods

    public Params20.Process getProcessReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_PROCESS(gwServerId), Params20.Process.class, isSecure());
    }


    // Java methods

    public Params20.JavaIndex getJavaIndex(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA(gwServerId), Params20.JavaIndex.class, isSecure());
    }

    public Params20.JavaVM getJavaVMReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA_VM(gwServerId), Params20.JavaVM.class, isSecure());
    }

    public Params20.JavaRuntime getJavaRuntimeReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA_RUNTIME(gwServerId), Params20.JavaRuntime.class, isSecure());
    }

    public Params20.JavaTimes getJavaTimesReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA_TIMES(gwServerId), Params20.JavaTimes.class, isSecure());
    }

    public Params20.JavaClasses getJavaClassesReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA_CLASSES(gwServerId), Params20.JavaClasses.class, isSecure());
    }

    public Params20.JavaMemory getJavaMemoryReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA_MEMORY(gwServerId), Params20.JavaMemory.class, isSecure());
    }

    public Params20.JavaThreads getJavaThreadsReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA_THREADS(gwServerId), Params20.JavaThreads.class, isSecure());
    }

    public Params20.JavaThread getJavaThreadReq(String gwServerId, long threadId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_JAVA_THREAD(gwServerId, threadId), Params20.JavaThread.class, isSecure());
    }


    // OS methods

    public Params20.OS getOSReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_OS(gwServerId), Params20.OS.class, isSecure());
    }


    // CPU methods

    public Params20.CPU getCPUReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_CPU(gwServerId), Params20.CPU.class, isSecure());
    }


    // Memory methods

    public Params20.Memory getMemoryReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_MEMORY(gwServerId), Params20.Memory.class, isSecure());
    }


    // Disks methods

    public Params20.Disks getDisksReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_DISKS(gwServerId), Params20.Disks.class, isSecure());
    }

    public Params20.Disk getDiskReq(String gwServerId, String diskId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_DISK(gwServerId, diskId), Params20.Disk.class, isSecure());
    }


    // Networks methods

    public Params20.Networks getNetworksReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_NETWORKS(gwServerId), Params20.Networks.class, isSecure());
    }

    public Params20.Network getNetworkReq(String gwServerId, int networkId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_EXEC_NETWORK(gwServerId, networkId), Params20.Network.class, isSecure());
    }

}
