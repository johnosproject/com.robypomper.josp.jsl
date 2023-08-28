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

package com.robypomper.josp.jcp.clients;

import com.robypomper.josp.clients.JCPAPIsClientJCP;
import com.robypomper.josp.clients.JCPClient2;


/**
 * Cloud JCP GWs implementation of {@link JCPClient2} interface.
 */
public class JCPGWsClient extends JCPAPIsClientJCP {

    // Internal vars

    public static final String JCP_NAME = "JCP GWs (%s)";


    // Constructor

    protected JCPGWsClient(ClientParams params, boolean usePrivate, String urlGWs, String instanceName) {
        super(usePrivate ? params.sslPrivate : params.sslPublic,
                params.clientId,
                params.clientSecret,
                urlGWs,
                params.authHostPublic + (params.authPort.isEmpty() ? "" : ":" + params.authPort),
                String.format(JCP_NAME, instanceName),
                params.clientCallBack);
    }

}
