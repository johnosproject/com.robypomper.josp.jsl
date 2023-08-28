/*******************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
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

package com.robypomper.josp.jod.comm;

import com.robypomper.comm.client.Client;
import com.robypomper.comm.client.ClientAbsSSL;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.josp.callers.apis.core.gateways.Caller20;
import com.robypomper.josp.clients.AbsGWsClient;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.defs.core.gateways.Params20;
import com.robypomper.josp.jod.events.Events;
import com.robypomper.josp.jod.objinfo.JODObjectInfo_002;
import com.robypomper.josp.protocol.JOSPPerm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.security.cert.Certificate;


/**
 * Client implementation for Gateway Object2Service connection.
 * <p>
 * This class provide a SSLClient to connect to the O2S Gw.
 */
public class JODGwO2SClient extends AbsGWsClient {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    // JOD
    private final JODCommunication_002 jodComm;
    private final JODObjectInfo_002 objInfo;
    // Configs
    private final Caller20 apiGWsCaller;


    // Constructor

    /**
     * Default constructor for JOSP GW O2S client.
     * <p>
     * Generate the SSL context, request the GW's access info and  to use for O2S Gw connection.
     * It use the object's id as certificate id and load the O2S Gw certificate
     * to the {@link javax.net.ssl.TrustManager} used for the SSL context.
     *
     * @param jodComm instance of the {@link JODCommunication}
     *                that initialized this client. It will used to
     *                process data received from the O2S Gw.
     * @param objInfo the objInfo representing the service.
     */
    public JODGwO2SClient(JODCommunication_002 jodComm, JODObjectInfo_002 objInfo, JCPAPIsClientObj jcpClient, String instanceId) {
        super(objInfo.getObjId(), "JODGWsO2S-Internal", jcpClient);
        this.jodComm = jodComm;
        this.objInfo = objInfo;
        this.apiGWsCaller = new Caller20(jcpClient, instanceId);

        addListener(new PeerConnectionListener() {

            @Override
            public void onConnecting(Peer peer) {
            }

            @Override
            public void onWaiting(Peer peer) {
            }

            @Override
            public void onConnect(Peer peer) {
                Events.registerCloudConnect("Comm Cloud Connected", JODGwO2SClient.this);
                jodComm.syncObject();
            }

            @Override
            public void onDisconnecting(Peer peer) {
            }

            @Override
            public void onDisconnect(Peer peer) {
                Events.registerCloudDisconnect("Comm Cloud Disconnected", JODGwO2SClient.this);
            }

            @Override
            public void onFail(Peer peer, String failMsg, Throwable exception) {
                Events.registerCloudConnect("Comm Cloud Fail", JODGwO2SClient.this, exception);
            }

        });
    }


    // Getters

    @Override
    public String getLocalId() {
        return getWrapper() != null ? getWrapper().getLocalId() : objInfo.getObjId();
    }


    // Client connection methods - O2S/S2O Sub classing

    @Override
    protected Params20.O2SAccessInfo getAccessInfo(Certificate localCertificate) throws Throwable {
        return apiGWsCaller.getO2SAccessInfo(localCertificate);
    }

    @Override
    protected Client initGWsClient(Params20.AccessInfo accessInfo, SSLContext sslCtx) throws Throwable {
        assert accessInfo instanceof Params20.O2SAccessInfo : String.format("AccessInfo for JODGWsO2SClient must be of type 'O2SAccessInfo', but found '%s'", accessInfo.getClass().getSimpleName());

        InetAddress gwAddress = InetAddress.getByName(accessInfo.gwAddress);

        return new ClientAbsSSL(getLocalId(), getRemoteId(), gwAddress, accessInfo.gwPort, getConnectionInfo().getProtocolName(),
                sslCtx,
                getDataEncodingConfigs().getCharset(), getDataEncodingConfigs().getDelimiter(),
                getHeartBeatConfigs().getTimeout(), getHeartBeatConfigs().getHBTimeout(), getHeartBeatConfigs().isHBResponseEnabled(),
                getByeConfigs().isEnable(), getByeConfigs().getByeMsg(),
                getAutoReConnectConfigs().isEnable(), getAutoReConnectConfigs().getDelay()
        ) {

            @Override
            protected boolean processData(byte[] data) {
                return false;
            }

            @Override
            protected boolean processData(String data) {
                return jodComm.processFromServiceMsg(data, JOSPPerm.Connection.LocalAndCloud);
            }

        };
    }

}
