/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.callers.gateways.status;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPIJCP;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.JCPGWsClient;
import com.robypomper.josp.jcp.defs.gateways.internal.status.Params20;
import com.robypomper.josp.jcp.defs.gateways.internal.status.Paths20;


/**
 * JCP Gateways - Status 2.0
 */
public class Caller20 extends AbsAPIJCP {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public Caller20(JCPGWsClient jcpClient) {
        super(jcpClient);
    }


    // Index methods

    public Params20.Index getIndex() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_STATUS, Params20.Index.class, isSecure());
    }


    // GWs status methods

    public Params20.GWs getGWsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_STATUS_GWS, Params20.GWs.class, isSecure());
    }

    public Params20.GW getGWReq(String gwId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_STATUS_GW(gwId), Params20.GW.class, isSecure());
    }

    public Params20.GWClient getGWsClientReq(String gwId, String gwClientId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_STATUS_GW_CLIENT(gwId, gwClientId), Params20.GWClient.class, isSecure());
    }


    // Broker status methods

    public Params20.Broker getBrokerReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_STATUS_BROKER, Params20.Broker.class, isSecure());
    }

    public Params20.BrokerObject getBrokerObjectReq(String objId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_STATUS_BROKER_OBJ(objId), Params20.BrokerObject.class, isSecure());
    }

    public Params20.BrokerService getBrokerServiceReq(String srvId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_STATUS_BROKER_SRV(srvId), Params20.BrokerService.class, isSecure());
    }

    public Params20.BrokerObjectDB getBrokerObjectDBReq(String objId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_STATUS_BROKER_OBJ_DB(objId), Params20.BrokerObjectDB.class, isSecure());
    }

}
