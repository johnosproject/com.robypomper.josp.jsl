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

package com.robypomper.josp.jcp.callers.apis.gateways.registration;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPIJCP;
import com.robypomper.josp.clients.JCPAPIsClientJCP;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.consts.JCPConstants;
import com.robypomper.josp.jcp.defs.apis.internal.gateways.registration.Params20;
import com.robypomper.josp.jcp.defs.apis.internal.gateways.registration.Paths20;


/**
 * JCP APIs - Gateways / Registration 2.0
 */
public class Caller20 extends AbsAPIJCP {

    public Caller20(JCPAPIsClientJCP jcpClient) {
        super(jcpClient);
    }

    public JCPAPIsClientJCP getClient() {
        return (JCPAPIsClientJCP) jcpClient;
    }


    // Registration methods

    public boolean postStartup(Params20.JCPGWsStartup gwStartup, String gwId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        synchronized(jcpClient) {
            jcpClient.addDefaultHeader(JCPConstants.API_HEADER_GW_ID, gwId);
            return jcpClient.execReq(Verb.POST, Paths20.FULL_PATH_STARTUP, Boolean.class, gwStartup, isSecure());
        }
    }

    public boolean postStatus(Params20.JCPGWsStatus gwStatus, String gwId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        synchronized(jcpClient) {
            jcpClient.addDefaultHeader(JCPConstants.API_HEADER_GW_ID, gwId);
            return jcpClient.execReq(Verb.POST, Paths20.FULL_PATH_STATUS, Boolean.class, gwStatus, isSecure());
        }
    }

    public boolean postShutdown(String gwId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        synchronized(jcpClient) {
            jcpClient.addDefaultHeader(JCPConstants.API_HEADER_GW_ID, gwId);
            return jcpClient.execReq(Verb.POST, Paths20.FULL_PATH_SHUTDOWN, Boolean.class, isSecure());
        }
    }

}
