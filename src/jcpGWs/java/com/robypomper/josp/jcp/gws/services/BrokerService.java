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

import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.gws.broker.Broker;
import com.robypomper.josp.jcp.gws.broker.BrokerJOD;
import com.robypomper.josp.jcp.gws.broker.BrokerJSL;
import com.robypomper.josp.jcp.gws.broker.BrokerObjDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class BrokerService {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(BrokerService.class);
    private final Broker broker;


    // Constructors

    @Autowired
    protected BrokerService(PermissionsDBService permissionsDBService) {
        broker = new Broker(permissionsDBService);
    }


    @PreDestroy
    public void destroy() {
        //broker.dismiss();
        //log.trace("JCP GW O2S service destroyed");
    }


    // Getters

    public BrokerJOD getBrokerJOD() {
        return broker;
    }

    public BrokerJSL getBrokerJSL() {
        return broker;
    }

    public BrokerObjDB getBrokerObjDB() {
        return broker;
    }

}
