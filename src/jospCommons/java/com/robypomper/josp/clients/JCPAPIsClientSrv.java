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


@SuppressWarnings("unused")
public abstract class JCPAPIsClientSrv extends DefaultJCPClient2 implements JCPClient2.ConnectionListener {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    public static final String JCP_NAME = "JCP APIs";
    public boolean connFailedPrinted;


    // Constructor

    public JCPAPIsClientSrv(boolean useSSL, String client, String secret, String urlAPIs, String urlAuth, String callBack, String authCodeRefreshToken) {
        super(client, secret, urlAPIs, useSSL, urlAuth, "openid offline_access", callBack, "jcp", authCodeRefreshToken, 30, JCP_NAME);
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

    public void setServiceId(String srvId) {
        if (srvId != null && !srvId.isEmpty())
            addDefaultHeader(JOSPConstants.API_HEADER_SRV_ID, srvId);
        else
            removeDefaultHeader(JOSPConstants.API_HEADER_SRV_ID);
    }

    public void setUserId(String usrId) {
        if (usrId != null && !usrId.isEmpty())
            addDefaultHeader(JOSPConstants.API_HEADER_USR_ID, usrId);
        else
            removeDefaultHeader(JOSPConstants.API_HEADER_USR_ID);
    }

    public void setLoginCodeAndReconnect(String loginCode) throws StateException, AuthenticationException {
        disconnect();
        setLoginCode(loginCode);
        connect();
    }


    // Self-Connection observer

    @Override
    public void onConnected(JCPClient2 jcpClient) {
        log.info(String.format("%s connected", JCP_NAME));
        storeTokens();
    }

    @Override
    public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
        if (connFailedPrinted) {
            log.debug(String.format("Error on %s connection attempt because %s", JCP_NAME, t.getMessage()));
        } else {
            log.warn(String.format("Error on %s connection attempt because %s", JCP_NAME, t.getMessage()));
            connFailedPrinted = true;
        }
    }

    @Override
    public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
        log.warn(String.format("Error on %s connection authentication because %s", getApiName(), t.getMessage()));
    }

    @Override
    public void onDisconnected(JCPClient2 jcpClient) {
        log.info(String.format("%s disconnected", JCP_NAME));
        connFailedPrinted = false;
    }

    protected abstract void storeTokens();

}
