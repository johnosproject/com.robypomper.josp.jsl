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

import com.robypomper.josp.clients.JCPClient2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class JCPClientsMngr {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JCPClientsMngr.class);
    @Autowired
    private ClientParams params;
    private JCPAPIsClient apisClient;
    private final Map<String,JCPGWsClient> gwsClientMngr = new HashMap<>();
    private JCPJSLWebBridgeClient jslWBClient;
    private JCPFEClient feClient;


    // Single clients getters

    public boolean isJCPAPIsClientInit() {
        return apisClient!=null;
    }

    public JCPAPIsClient getJCPAPIsClient() {
        if (apisClient==null) {
            apisClient = new JCPAPIsClient(params, true);
            apisClient.addConnectionListener(new ConnectionLogger());
            if (apisClient.isConnected())
                log.info(String.format("Created and connected '%s' JCP client with '%s' address.", apisClient.getApiName(), apisClient.getAPIsUrl()));
            else
                log.warn(String.format("Created but not connected '%s' JCP client with '%s' address.", apisClient.getApiName(), apisClient.getAPIsUrl()));
        }

        return apisClient;
    }

    public boolean isJCPJSLWebBridgeClientInit() {
        return jslWBClient!=null;
    }

    public JCPJSLWebBridgeClient getJCPJSLWebBridgeClient() {
        if (jslWBClient==null) {
            jslWBClient = new JCPJSLWebBridgeClient(params, true);
            jslWBClient.addConnectionListener(new ConnectionLogger());
            if (jslWBClient.isConnected())
                log.info(String.format("Created and connected '%s' JCP client with '%s' address.", jslWBClient.getApiName(), jslWBClient.getAPIsUrl()));
            else
                log.warn(String.format("Created but not connected '%s' JCP client with '%s' address.", jslWBClient.getApiName(), jslWBClient.getAPIsUrl()));
        }
        return jslWBClient;
    }

    public boolean isJCPFEClientInit() {
        return feClient!=null;
    }

    public JCPFEClient getJCPFEClient() {
        if (feClient==null) {
            feClient = new JCPFEClient(params, true);
            feClient.addConnectionListener(new ConnectionLogger());
            if (feClient.isConnected())
                log.info(String.format("Created and connected '%s' JCP client with '%s' address.", feClient.getApiName(), feClient.getAPIsUrl()));
            else
                log.warn(String.format("Created but not connected '%s' JCP client with '%s' address.", feClient.getApiName(), feClient.getAPIsUrl()));
        }

        return feClient;
    }


    // External managed clients getters

    public JCPGWsClient createGWsClientByGW(String gwId, String apiHost, int apiPort) {
        String apiAddrs = String.format("%s:%d", apiHost, apiPort);

        if (getGWsClientByGW(gwId)!=null)
            log.warn(String.format("Override JCPGWsClient for '%s' JCP GWs with '%s' address.", gwId, apiAddrs));

        JCPGWsClient gwClient = new JCPGWsClient(params,true,apiAddrs,gwId);
        gwClient.addConnectionListener(new ConnectionLogger());
        gwsClientMngr.put(gwId,gwClient);
        if (gwClient.isConnected())
            log.info(String.format("Created and connected '%s' JCP client with '%s' address.", gwClient.getApiName(), gwClient.getAPIsUrl()));
        else
            log.warn(String.format("Created but not connected '%s' JCP client with '%s' address.", gwClient.getApiName(), gwClient.getAPIsUrl()));
        return gwClient;
    }

    public JCPGWsClient getGWsClientByGW(String gwId) {
        return gwsClientMngr.get(gwId);
    }

    public JCPGWsClient removeGWsClientByGW(String gwId) {
        JCPGWsClient removedClient = gwsClientMngr.remove(gwId);
        if (removedClient==null)
            log.warn(String.format("Try to remove not existing JCPGWsClient for '%s' JCP GWs.", gwId));

        log.info(String.format("Created JCPGWsClient for '%s' JCP GWs.", gwId));
        return removedClient;
    }

    public JCPGWsClient getGWsClientByGWServer(String gwServerId) {
        //return gwsClientMngr.get(gwServerId);
        for (String gwKey : gwsClientMngr.keySet())
            if (gwKey.startsWith(gwServerId))
                return gwsClientMngr.get(gwKey);

        return null;
    }

    public Map<String,JCPGWsClient> getGWsClientsAll() {
        return gwsClientMngr;
    }

    public Collection<String> getGWsServerAll() {
        //return gwsClientMngr.keySet();
        List<String> serverIds = new ArrayList<>();

        for (String gwKey : gwsClientMngr.keySet()) {
            String gwsSerial = gwKey.substring(0,gwKey.length()-4);
            if (!serverIds.contains(gwsSerial))
                serverIds.add(gwsSerial);
        }

        return serverIds;
    }


    // Clients listener

    private class ConnectionLogger implements JCPClient2.ConnectionListener {

        private boolean isConnectionFailedPrinted = false;
        private boolean isAuthenticationFailedPrinted = false;

        @Override
        public void onConnected(JCPClient2 jcpClient) {
            log.info(String.format("Connected %s %s with '%s' address.", jcpClient.getClass().getSimpleName(), jcpClient.getApiName(), jcpClient.getAPIsUrl()));

            isConnectionFailedPrinted = false;
            isAuthenticationFailedPrinted = false;
        }

        @Override
        public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
            if (!isConnectionFailedPrinted) {
                log.warn(String.format("Error on connecting %s %s with '%s' address (%s).", jcpClient.getClass().getSimpleName(), jcpClient.getApiName(), jcpClient.getAPIsUrl(), t));
                isConnectionFailedPrinted = true;
            } else
                log.trace(String.format("Error on connecting %s %s with '%s' address (%s).", jcpClient.getClass().getSimpleName(), jcpClient.getApiName(), jcpClient.getAPIsUrl(), t));

            isAuthenticationFailedPrinted = false;
        }

        @Override
        public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
            if (!isAuthenticationFailedPrinted) {
                log.warn(String.format("Error on authenticating %s %s with '%s' address (%s).", jcpClient.getClass().getSimpleName(), jcpClient.getApiName(), jcpClient.getAPIsUrl(), t));
                isAuthenticationFailedPrinted = true;
            } else
                log.trace(String.format("Error on authenticating %s %s with '%s' address (%s).", jcpClient.getClass().getSimpleName(), jcpClient.getApiName(), jcpClient.getAPIsUrl(), t));

            isConnectionFailedPrinted = false;
        }

        @Override
        public void onDisconnected(JCPClient2 jcpClient) {
            log.warn(String.format("Disconnected %s %s with '%s' address.", jcpClient.getClass().getSimpleName(), jcpClient.getApiName(), jcpClient.getAPIsUrl()));

            isConnectionFailedPrinted = false;
            isAuthenticationFailedPrinted = false;
        }
    };

}
