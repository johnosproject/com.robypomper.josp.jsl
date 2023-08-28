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

package com.robypomper.josp.jcp.gws.services;

import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import com.robypomper.josp.jcp.clients.JCPClientsMngr;
import com.robypomper.josp.jcp.db.apis.EventDBService;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.db.apis.StatusHistoryDBService;
import com.robypomper.josp.jcp.gws.gw.GWS2O;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class GWServiceS2O implements ApplicationListener<ContextRefreshedEvent> {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(GWServiceS2O.class);
    private final GWS2O gw;


    // Constructors

    @Autowired
    protected GWServiceS2O(@Value("${jcp.gws.region:Central}") final String region,
                           @Value("${jcp.gws.s2o.ip.internal}") final String addrInternal,
                           @Value("${jcp.gws.s2o.ip.public}") final String addrPublic,
                           @Value("${jcp.gws.s2o.port}") final int gwPort,
                           @Value("${server.port}") final int apiPort,
                           @Value("${jcp.gws.s2o.maxClients}") final int maxClients,
                           JCPClientsMngr clientsMngr,
                           BrokerService gwBroker, ServiceDBService serviceDBService, EventDBService eventsDBService, StatusHistoryDBService statusesHistoryDBService) throws ServerStartupException, JavaJKS.GenerationException, JavaSSL.GenerationException {
        gw = new GWS2O(region, addrInternal, addrPublic, gwPort, apiPort, maxClients, clientsMngr, gwBroker.getBrokerJSL(), serviceDBService, eventsDBService, statusesHistoryDBService);
    }

    @PreDestroy
    public void destroy() {
        if (gw!=null)
            gw.destroy();
        log.trace("JCP GW S2O service destroyed");
    }


    // Getters

    public GWS2O get() {
        return gw;
    }


    // Spring events listener

    public void onApplicationEvent(final ContextRefreshedEvent event) {
        gw.onApplicationEvent(event);
    }

}
