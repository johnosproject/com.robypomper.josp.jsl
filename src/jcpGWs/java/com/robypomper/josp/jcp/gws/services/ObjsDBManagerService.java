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

import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.gws.db.ObjsDBManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class ObjsDBManagerService {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(ObjsDBManagerService.class);
    private final ObjsDBManager objsMngr;


    // Constructors

    @Autowired
    protected ObjsDBManagerService(@Value("${jcp.gws.region:Central}") final String region,
                                   BrokerService gwBroker, ObjectDBService objectDBService, PermissionsDBService permissionsDBService) {
        objsMngr = new ObjsDBManager(region, gwBroker.getBrokerObjDB(), objectDBService, permissionsDBService);
    }

    @PreDestroy
    public void destroy() {
        objsMngr.destroy();
        log.trace("Objects DB Manager service destroyed");
    }

}
