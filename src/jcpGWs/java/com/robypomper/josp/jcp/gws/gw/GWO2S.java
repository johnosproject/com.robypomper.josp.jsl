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

import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import com.robypomper.josp.jcp.clients.JCPClientsMngr;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.gws.broker.BrokerJOD;
import com.robypomper.josp.types.josp.gw.GWType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class GWO2S extends GWAbs {

    // Class constants

    private static final String ID = "O2S-%s@%s";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(GWO2S.class);
    private final Map<String, GWClientO2S> jodGWClients = new HashMap<>();
    private final BrokerJOD gwBroker;
    private final ObjectDBService objectDBService;
    private final PermissionsDBService permissionsDBService;


    // Constructors

    public GWO2S(String region, String addrInternal, String addrPublic, int gwPort, int apiPort,
                 int maxClients,
                 JCPClientsMngr clientsMngr,
                 BrokerJOD gwBroker, ObjectDBService objectDBService, PermissionsDBService permissionsDBService) throws ServerStartupException, JavaJKS.GenerationException, JavaSSL.GenerationException {
        super(GWType.Obj2Srv, getSerial() + "_O2S", addrInternal, addrPublic, gwPort, apiPort, maxClients, clientsMngr, log);
        this.gwBroker = gwBroker;
        this.objectDBService = objectDBService;
        this.permissionsDBService = permissionsDBService;

    }

    public void destroy() {
        super.destroy();
    }


    // GWServer's Client events

    @Override
    protected void onClientConnection(ServerClient client) {
        InetAddress remAddr = client.getConnectionInfo().getRemoteInfo().getAddr();
        int remPort = client.getConnectionInfo().getRemoteInfo().getPort();
        log.info(String.format("JOD Object '%s' connected to JCP GW '%s' (remote peer: '%s:%d')", client.getRemoteId(), getId(), remAddr.getHostAddress(), remPort));

        if (jodGWClients.get(client.getRemoteId()) != null) {
            disconnectBecauseError(client, "already connected");
            return;
        }

        GWClientO2S gwObject = new GWClientO2S(client, gwBroker, objectDBService, permissionsDBService);
        jodGWClients.put(client.getRemoteId(), gwObject);

        increaseClient();
    }

    @Override
    protected void onClientDisconnection(ServerClient client) {
        InetAddress remAddr = client.getConnectionInfo().getRemoteInfo().getAddr();
        int remPort = client.getConnectionInfo().getRemoteInfo().getPort();
        log.info(String.format("JOD Object '%s' disconnected from JCP GW '%s' (remote peer: '%s:%d')", client.getRemoteId(), getId(), remAddr.getHostAddress(), remPort));

        if (jodGWClients.get(client.getRemoteId()) == null)
            return;

        jodGWClients.remove(client.getRemoteId());

        decreaseClient();
    }


    // GWServer's Messages methods

    @Override
    protected boolean processData(ServerClient client, String data) {
        GWClientO2S gwObject = jodGWClients.get(client.getRemoteId());
        return gwObject.processFromObjectMsg(data);
    }

}
