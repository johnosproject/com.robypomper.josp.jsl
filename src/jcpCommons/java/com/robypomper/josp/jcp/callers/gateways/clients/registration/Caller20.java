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

package com.robypomper.josp.jcp.callers.gateways.clients.registration;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPIJCP;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.JCPGWsClient;
import com.robypomper.josp.jcp.defs.gateways.internal.clients.registration.Params20;
import com.robypomper.josp.jcp.defs.gateways.internal.clients.registration.Paths20;


/**
 * JCP Gateways - Clients / Registration 2.0
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


    // Access Info methods

    /**
     * Request to the JCP object's access info for Gateway O2S connection.
     * <p>
     * Object send his public certificate and instance id to the GW O2S and
     * the JCP respond with the GW O2S's address, port and public certificate.
     *
     * @return the GW O2S access info.
     */
    public Params20.O2SAccessInfo postO2SAccess(String objId, Params20.O2SAccessRequest accessRequestParams) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        getClient().setObjectId(objId);
        Params20.O2SAccessInfo response = jcpClient.execReq(Verb.POST, Paths20.FULL_PATH_GW_O2S_ACCESS, Params20.O2SAccessInfo.class, accessRequestParams, isSecure());
        getClient().setObjectId(null);
        return response;
    }

    /**
     * Request to the JCP service's access info for Gateway S2O connection.
     * <p>
     * Service send his public certificate and instance id to the GW S2O and
     * the JCP respond with the GW S2O's address, port and public certificate.
     *
     * @return the GW S2O access info.
     */
    public Params20.S2OAccessInfo postS2OAccess(String srvId, Params20.S2OAccessRequest accessRequestParams) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        getClient().setServiceId(srvId);
        Params20.S2OAccessInfo response = jcpClient.execReq(Verb.POST, Paths20.FULL_PATH_GW_S2O_ACCESS, Params20.S2OAccessInfo.class, accessRequestParams, isSecure());
        getClient().setServiceId(null);
        return response;
    }

}
