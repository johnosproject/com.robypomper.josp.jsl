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

package com.robypomper.josp.clients;

import com.robypomper.josp.consts.JOSPConstants;
import com.robypomper.josp.states.StateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Cloud JCP APIs implementation of {@link JCPClient2} interface.
 * <p>
 * This class initialize a JCPClient that can be used by JCP instance to access
 * to him self. As Spring component it can be declared in any other component
 * as variable (and @Autowired annotation) or directly as constructor param.
 * <p>
 * The client configurations are read from Spring Boot <code>application.yml</code>
 * file:
 * <ul>
 *     <li>
 *         <b>jcp.client.id</b>: client's id .
 *     </li>
 *     <li>
 *         <b>jcp.client.secret</b>: client's secret.
 *     </li>
 *     <li>
 *         <b>jcp.url.apis</b>: jcp apis server url.
 *     </li>
 *     <li>
 *         <b>jcp.url.auth</b>: auth server url.
 *     </li>
 * </ul>
 * <p>
 * As workaround of development localhost hostname usage, this class disable the
 * SSL checks and the connect to configured the server.
 */
@SuppressWarnings("unused")
public class JCPAPIsClientJCP extends DefaultJCPClient2 implements JCPClient2.ConnectionListener {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    public boolean connFailedPrinted;


    // Constructor

    public JCPAPIsClientJCP(boolean useSSL, String client, String secret, String urlAPIs, String urlAuth, String apiName, String callBack) {
        super(client, secret, urlAPIs, useSSL, urlAuth, "openid offline_access", callBack, "jcp", 30, apiName);
        addConnectionListener(this);

        connFailedPrinted = true;
        try {
            connect();

        } catch (StateException | AuthenticationException e) {
            log.warn(String.format("Error connecting to JCP %s because %s", getApiName(), e.getMessage()), e);
        }
        connFailedPrinted = false;
    }


    // Headers default values setters

    public void setObjectId(String objId) {
        if (objId != null && !objId.isEmpty())
            addDefaultHeader(JOSPConstants.API_HEADER_OBJ_ID, objId);
        else
            removeDefaultHeader(JOSPConstants.API_HEADER_OBJ_ID);
    }

    public void setServiceId(String srvId) {
        if (srvId != null && !srvId.isEmpty())
            addDefaultHeader(JOSPConstants.API_HEADER_SRV_ID, srvId);
        else
            removeDefaultHeader(JOSPConstants.API_HEADER_SRV_ID);
    }

    public void setUserId(String usrId) {
        if (usrId != null && !usrId.isEmpty())
            addDefaultHeader(JOSPConstants.API_HEADER_SRV_ID, usrId);
        else
            removeDefaultHeader(JOSPConstants.API_HEADER_SRV_ID);
    }


    // Self-Connection observer

    @Override
    public void onConnected(JCPClient2 jcpClient) {
        log.info(String.format("%s connected", getApiName()));
        connFailedPrinted = false;
    }

    @Override
    public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
        if (connFailedPrinted) {
            log.debug(String.format("Error on %s connection attempt because %s with state %s", getApiName(), t.getMessage(), jcpClient.getState()));
        } else {
            log.warn(String.format("Error on %s connection attempt because %s with state %s", getApiName(), t.getMessage(), jcpClient.getState()));
            connFailedPrinted = true;
        }
    }

    @Override
    public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
        log.warn(String.format("Error on %s connection authentication because %s with state %s", getApiName(), t.getMessage(), jcpClient.getState()));
    }

    @Override
    public void onDisconnected(JCPClient2 jcpClient) {
        log.info(String.format("%s disconnected with state %s", getApiName(), jcpClient.getState()));
    }

}
