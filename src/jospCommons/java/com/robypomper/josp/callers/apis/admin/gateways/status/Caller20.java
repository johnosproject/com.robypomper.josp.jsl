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

package com.robypomper.josp.callers.apis.admin.gateways.status;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPISrv;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.defs.admin.gateways.status.Params20;
import com.robypomper.josp.defs.admin.gateways.status.Paths20;


/**
 * JOSP Admin - Gateways / Status 2.0
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


    // List methods

    public Params20.GatewaysServers getGWsListReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_LIST, Params20.GatewaysServers.class, isSecure());
    }


    // Index methods

    public Params20.Index getIndex(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS(gwServerId), Params20.Index.class, isSecure());
    }


    // GWs status methods

    public Params20.GWs getGWsReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_GWS(gwServerId), Params20.GWs.class, isSecure());
    }

    public Params20.GW getGWReq(String gwServerId, String gwId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_GW(gwServerId, gwId), Params20.GW.class, isSecure());
    }

    public Params20.GWClient getGWsClientReq(String gwServerId, String gwId, String gwClientId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_GW_CLIENT(gwServerId, gwId, gwClientId), Params20.GWClient.class, isSecure());
    }


    // Broker status methods

    public Params20.Broker getBrokerReq(String gwServerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER(gwServerId), Params20.Broker.class, isSecure());
    }

    public Params20.BrokerObject getBrokerObjectReq(String gwServerId, String objId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ(gwServerId, objId), Params20.BrokerObject.class, isSecure());
    }

    public Params20.BrokerService getBrokerServiceReq(String gwServerId, String srvId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_SRV(gwServerId, srvId), Params20.BrokerService.class, isSecure());
    }

    public Params20.BrokerObjectDB getBrokerObjectDBReq(String gwServerId, String objId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ_DB(gwServerId, objId), Params20.BrokerObjectDB.class, isSecure());
    }

}
