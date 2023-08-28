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

package com.robypomper.josp.jcp.gws.db;

import com.robypomper.java.JavaStructures;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.db.apis.entities.Object;
import com.robypomper.josp.jcp.gws.broker.BrokerObjDB;
import com.robypomper.josp.jcp.gws.gw.GWAbs;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjsDBManager {

    // Class constants

    private static final String ID = "ODB-%s@%s";


    // Internal vars

    private final String id;
    private final Map<String, ObjDB> objsDB = new HashMap<>();
    private final BrokerObjDB broker;
    private final ObjectDBService objectDBService;
    private final PermissionsDBService permissionsDBService;


    // Constructors

    public ObjsDBManager(String region, BrokerObjDB gwBroker, ObjectDBService objectDBService, PermissionsDBService permissionsDBService) {
        this.id = String.format(ID, GWAbs.getSerial(), region);
        this.broker = gwBroker;
        this.objectDBService = objectDBService;
        this.permissionsDBService = permissionsDBService;

        loadObjsFromDB();
    }

    public void destroy() {
        List<ObjDB> objsDBTmp = new ArrayList<>(objsDB.values());
        for (ObjDB objDB : objsDBTmp) {
            broker.deregisterObject(objDB);
            objsDB.remove(objDB.getId());
        }
    }


    // Getters

    public String getId() {
        return id;
    }

    private BrokerObjDB getBroker() {
        return broker;
    }


    // DB objects loader

    private void loadObjsFromDB() {
        for (Object obj : objectDBService.findAll()) {
            ObjDB objDB = new ObjDB(obj, permissionsDBService);
            objDB.resume();
            objsDB.put(obj.getObjId(), objDB);
            broker.registerObject(objDB);
            getBroker().send(objDB, objDB.getMsgOBJ_INFO(), JOSPPerm.Type.Status);
            getBroker().send(objDB, objDB.getMsgOBJ_STRUCT(), JOSPPerm.Type.Status);
            getBroker().send(objDB, objDB.getMsgOBJ_PERM(), JOSPPerm.Type.CoOwner);
            Map<String, JavaStructures.Pair<JOSPPerm.Type, JOSPPerm.Connection>> x = getBroker().getObjectCloudAllowedServices(obj.getObjId());
            for (Map.Entry<String, JavaStructures.Pair<JOSPPerm.Type, JOSPPerm.Connection>> srvPerm : x.entrySet())
                getBroker().send(objDB, srvPerm.getKey(), objDB.getMsgSERVICE_PERM(srvPerm.getValue().getFirst(), srvPerm.getValue().getSecond()), JOSPPerm.Type.None);
        }
    }

}
