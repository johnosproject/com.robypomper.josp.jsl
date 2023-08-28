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

package com.robypomper.josp.jcp.gws.gw;

import com.robypomper.comm.server.Server;
import com.robypomper.comm.server.ServerAbsSSL;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.comm.server.ServerClientsListener;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.josp.protocol.JOSPProtocol;

import javax.net.ssl.SSLContext;
import java.security.cert.Certificate;

public class GWServer extends ServerAbsSSL {

    // Internal vars

    private final GWAbs gwService;
    private final AbsCustomTrustManager trustManager;
    private final Certificate publicCertificate;


    // Constructors

    public GWServer(GWAbs gwService, SSLContext sslCtx, String idServer, int port, AbsCustomTrustManager trustManager, Certificate publicCertificate) {
        super(idServer, port, JOSPProtocol.JOSP_PROTO_NAME, sslCtx, trustManager, publicCertificate, true, false);
        this.gwService = gwService;
        this.trustManager = trustManager;
        this.publicCertificate = publicCertificate;
        addListener(serverClientListener);
    }


    // Getters

    public AbsCustomTrustManager getTrustManager() {
        return trustManager;
    }

    public Certificate getPublicCertificate() {
        return publicCertificate;
    }


    // Clients listener

    @SuppressWarnings("FieldCanBeLocal")
    private final ServerClientsListener serverClientListener = new ServerClientsListener() {

        @Override
        public void onConnect(Server server, ServerClient client) {
            gwService.onClientConnection(client);
        }

        @Override
        public void onDisconnect(Server server, ServerClient client) {
            gwService.onClientDisconnection(client);
        }

        @Override
        public void onFail(Server server, ServerClient client, String failMsg, Throwable exception) {
            System.out.println(failMsg + " - " + exception);
        }

    };

    // Messages methods

    @Override
    public boolean processData(ServerClient client, byte[] data) {
        return false;
    }

    @Override
    public boolean processData(ServerClient client, String data) {
        return gwService.processData(client, data);
    }

}
