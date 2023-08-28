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

package com.robypomper.josp.callers.apis.core.permissions;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPIObj;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.defs.core.permissions.Paths20;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;

import java.util.List;


/**
 * JOSP Core - Permissions 2.0
 */
@SuppressWarnings("unused")
public class Caller20 extends AbsAPIObj {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public Caller20(JCPAPIsClientObj jcpClient) {
        super(jcpClient);
    }


    // Generator methods

    public List<JOSPPerm> getPermissionsGeneratedSTANDARD() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return getPermissionsGenerated(JOSPPerm.GenerateStrategy.STANDARD);
    }

    public List<JOSPPerm> getPermissionsGeneratedPUBLIC() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return getPermissionsGenerated(JOSPPerm.GenerateStrategy.PUBLIC);
    }

    /**
     * Request to the JCP a generic object's permission list.
     * <p>
     * The list generated can be different depending on generation strategy request.
     *
     * @return a valid permission list.
     */
    public List<JOSPPerm> getPermissionsGenerated(JOSPPerm.GenerateStrategy strategy) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        String permsStr = jcpClient.execReq(Verb.GET, Paths20.FULL_PATH_GENERATE(strategy.toString()), String.class, isSecure());

        try {
            return JOSPPerm.listFromString(permsStr);

        } catch (JOSPProtocol.ParsingException e) {
            throw new JCPClient2.RequestException(String.format("Can't parse JOSPPerm list from returned string '%s'", permsStr));
        }
    }

}
