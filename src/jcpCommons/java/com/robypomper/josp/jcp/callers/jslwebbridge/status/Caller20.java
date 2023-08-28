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

package com.robypomper.josp.jcp.callers.jslwebbridge.status;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPIJCP;
import com.robypomper.josp.clients.JCPAPIsClientJCP;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.defs.jslwebbridge.internal.status.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.internal.status.Paths20;


/**
 * JCP JSL Web Bridge - Status 2.0
 */
public class Caller20 extends AbsAPIJCP {

    public Caller20(JCPAPIsClientJCP jcpClient) {
        super(jcpClient);
    }

    public JCPAPIsClientJCP getClient() {
        return (JCPAPIsClientJCP) jcpClient;
    }


    // Index methods

    public Params20.Index getIndex() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_STATUS, Params20.Index.class, isSecure());
    }


    // Sessions methods

    public Params20.Sessions getSessionsReq() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_STATUS_SESSIONS, Params20.Sessions.class, isSecure());
    }

    public Params20.Session getSessionReq(String sessionId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_STATUS_SESSION(sessionId), Params20.Session.class, isSecure());
    }

}
