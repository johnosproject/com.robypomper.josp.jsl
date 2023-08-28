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

package com.robypomper.josp.callers.apis.admin.apis.status;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPISrv;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.defs.admin.apis.status.Params20;
import com.robypomper.josp.defs.admin.apis.status.Paths20;


/**
 * JOSP Admin - APIs / Status 2.0
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

    public Params20.Index getIndex() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_APIS_STATUS, Params20.Index.class, isSecure());
    }


    // Objects methods

    public Params20.Objects getObjectsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_APIS_STATUS_OBJS, Params20.Objects.class, isSecure());
    }

    public Params20.Object getObjectReq(String objId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_APIS_STATUS_OBJ(objId), Params20.Object.class, isSecure());
    }


    // Service methods

    public Params20.Services getServicesReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_APIS_STATUS_SRVS, Params20.Services.class, isSecure());
    }

    public Params20.Service getServiceReq(String srvId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_APIS_STATUS_SRV(srvId), Params20.Service.class, isSecure());
    }


    // Users methods

    public Params20.Users getUsersReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_APIS_STATUS_USRS, Params20.Users.class, isSecure());
    }

    public Params20.User getUserReq(String usrId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_APIS_STATUS_USR(usrId), Params20.User.class, isSecure());
    }


    // Gateways methods

    public Params20.Gateways getGatewaysReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_APIS_STATUS_GWS, Params20.Gateways.class, isSecure());
    }

    public Params20.Gateway getGatewayReq(String gwId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_JCP_APIS_STATUS_GW(gwId), Params20.Gateway.class, isSecure());
    }

}
