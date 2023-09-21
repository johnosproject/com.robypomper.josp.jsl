/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.robypomper.josp.jsl.comm;

import com.robypomper.comm.client.Client;
import com.robypomper.comm.client.ClientAbsSSL;
import com.robypomper.josp.callers.apis.core.gateways.Caller20;
import com.robypomper.josp.clients.AbsGWsClient;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.defs.core.gateways.Params20;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.security.cert.Certificate;


/**
 * Client implementation for Gateway Service2Object connection.
 * <p>
 * This class provide a SSLClient to connect to the S2O Gw.
 */
public class JSLGwS2OClient extends AbsGWsClient {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLGwS2OClient.class);
    // JSL
    private final JSLCommunication_002 jslComm;
    private final JSLServiceInfo srvInfo;
    // Configs
    private final Caller20 apiGWsCaller;


    // Constructor

    /**
     * Generate the SSL context to use for O2S Gw connection.
     * It use the object's id as certificate id and load the S2O Gw certificate
     * to the {@link javax.net.ssl.TrustManager} used for the SSL context.
     *
     * @param jslComm instance of the {@link JSLCommunication}
     *                that initialized this client. It will used to
     *                process data received from the O2S Gw.
     * @param srvInfo the srvInfo representing the service.
     */
    public JSLGwS2OClient(JSLCommunication_002 jslComm, JSLServiceInfo srvInfo, JCPAPIsClientSrv jcpClient, String instanceId) {
        super(srvInfo.getFullId(), "JSLGWsS2O-Internal", jcpClient);
        this.jslComm = jslComm;
        this.srvInfo = srvInfo;
        this.apiGWsCaller = new Caller20(jcpClient, instanceId);
    }


    // Getters

    @Override
    public String getLocalId() {
        return getWrapper() != null ? getWrapper().getLocalId() : srvInfo.getFullId();
    }


    // Client connection methods - O2S/S2O Sub classing

    @Override
    protected Params20.S2OAccessInfo getAccessInfo(Certificate localCertificate) throws Throwable {
        return apiGWsCaller.getS2OAccessInfo(localCertificate);
    }

    @Override
    protected Client initGWsClient(Params20.AccessInfo accessInfo, SSLContext sslCtx) throws Throwable {
        assert accessInfo instanceof Params20.S2OAccessInfo : String.format("AccessInfo for JODGWsO2SClient must be of type 'S2OAccessInfo', but found '%s'", accessInfo.getClass().getSimpleName());

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
                return jslComm.processFromObjectMsg(data, JOSPPerm.Connection.LocalAndCloud);
            }

        };
    }

}
