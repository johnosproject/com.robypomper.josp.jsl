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
import com.robypomper.josp.jcp.db.apis.EventDBService;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.db.apis.StatusHistoryDBService;
import com.robypomper.josp.jcp.gws.broker.BrokerJSL;
import com.robypomper.josp.jcp.gws.exceptions.JSLServiceNotRegisteredException;
import com.robypomper.josp.types.josp.gw.GWType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class GWS2O extends GWAbs {

    // Class constants

    private static final String ID = "S2O-%s@%s";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(GWS2O.class);
    private final Map<String, GWClientS2O> jslGWClients = new HashMap<>();
    private final BrokerJSL gwBroker;
    private final ServiceDBService serviceDBService;
    private final EventDBService eventsDBService;
    private final StatusHistoryDBService statusesHistoryDBService;


    // Constructors

    public GWS2O(final String region, final String addrInternal, final String addrPublic, final int gwPort, final int apiPort,
                 final int maxClients,
                 JCPClientsMngr clientsMngr,
                 BrokerJSL gwBroker, ServiceDBService serviceDBService, EventDBService eventsDBService, StatusHistoryDBService statusesHistoryDBService) throws ServerStartupException, JavaJKS.GenerationException, JavaSSL.GenerationException {
        super(GWType.Srv2Obj, getSerial() + "_S2O", addrInternal, addrPublic, gwPort, apiPort, maxClients, clientsMngr, log);
        this.gwBroker = gwBroker;
        this.serviceDBService = serviceDBService;
        this.eventsDBService = eventsDBService;
        this.statusesHistoryDBService = statusesHistoryDBService;
    }

    public void destroy() {
        super.destroy();
    }


    // GWServer's Client events

    @Override
    protected void onClientConnection(ServerClient client) {
        InetAddress remAddr = client.getConnectionInfo().getRemoteInfo().getAddr();
        int remPort = client.getConnectionInfo().getRemoteInfo().getPort();
        log.info(String.format("JOD Service '%s' connected to JCP GW '%s' (remote peer: '%s:%d')", client.getRemoteId(), getId(), remAddr.getHostAddress(), remPort));

        if (jslGWClients.get(client.getRemoteId()) != null) {
            disconnectBecauseError(client, "already connected");
            return;
        }

        GWClientS2O gwService;
        try {
            gwService = new GWClientS2O(client, gwBroker, serviceDBService, eventsDBService, statusesHistoryDBService);

        } catch (JSLServiceNotRegisteredException serviceNotRegistered) {
            disconnectBecauseError(client, "not registered");
            return;
        }
        jslGWClients.put(client.getRemoteId(), gwService);

        increaseClient();
    }

    @Override
    protected void onClientDisconnection(ServerClient client) {
        InetAddress remAddr = client.getConnectionInfo().getRemoteInfo().getAddr();
        int remPort = client.getConnectionInfo().getRemoteInfo().getPort();
        log.info(String.format("JOD Service '%s' disconnected from JCP GW '%s' (remote peer: '%s:%d')", client.getRemoteId(), getId(), remAddr.getHostAddress(), remPort));

        if (jslGWClients.get(client.getRemoteId()) == null)
            return;

        jslGWClients.remove(client.getRemoteId());

        decreaseClient();
    }


    // GWServer's Messages methods

    @Override
    protected boolean processData(ServerClient client, String data) {
        GWClientS2O gwService = jslGWClients.get(client.getRemoteId());
        return gwService.processFromServiceMsg(data);
    }

}
