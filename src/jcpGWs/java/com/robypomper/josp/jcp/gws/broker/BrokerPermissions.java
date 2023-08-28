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

package com.robypomper.josp.jcp.gws.broker;

import com.robypomper.java.JavaStructures.Pair;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.db.apis.entities.Permission;
import com.robypomper.josp.protocol.JOSPPerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrokerPermissions {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Broker.class);
    private final Broker broker;
    private final PermissionsDBService permissionsDBService;

    public BrokerPermissions(Broker broker, PermissionsDBService permissionsDBService) {
        this.broker = broker;
        this.permissionsDBService = permissionsDBService;
    }


    // Permissions methods

    public boolean checkServicePermissionOnObject(String srvId, String objId, JOSPPerm.Connection minPermConnectionReq, JOSPPerm.Type minReqPerm) {
        return getServiceAllowedObjects(srvId, minPermConnectionReq, minReqPerm).containsKey(objId);
    }

    public Pair<JOSPPerm.Type, JOSPPerm.Connection> getServicePermissionsOnObject(String srvId, String objId, JOSPPerm.Connection minPermConnectionReq, JOSPPerm.Type minReqPerm) {
        return getServiceAllowedObjects(srvId, minPermConnectionReq, minReqPerm).get(objId);
    }

    public boolean _checkObjectPermissionOnService(String objId, String srvId, JOSPPerm.Connection minPermConnectionReq, JOSPPerm.Type minReqPerm) {
        return getObjectAllowedServices(objId, minPermConnectionReq, minReqPerm).containsKey(srvId);
    }

    public Pair<JOSPPerm.Type, JOSPPerm.Connection> _getObjectPermissionOnService(String objId, String srvId, JOSPPerm.Connection minPermConnectionReq, JOSPPerm.Type minReqPerm) {
        return getObjectAllowedServices(objId, minPermConnectionReq, minReqPerm).get(srvId);
    }


    // Allowed methods

    public Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> getServiceAllowedObjects(String srvId, JOSPPerm.Connection minPermConnectionReq) {
        return getServiceAllowedObjects(srvId, minPermConnectionReq, JOSPPerm.Type.None);
    }

    // Query DB (srv ext)
    public Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> getServiceAllowedObjects(String srvId, JOSPPerm.Connection minPermConnectionReq, JOSPPerm.Type minPermTypeReq) {
        Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> allowedObjects = new HashMap<>();

        List<Permission> srvExtendedPermissions = permissionsDBService.findBySrvExtended(srvId);

        // each srv's permission (included #All, #Owner)
        //     get permission's object
        //     if service allowed for access to obj
        //         add object (keep major permission)
        for (Permission srvExtPerm : srvExtendedPermissions) {
            BrokerClientJOD obj = getRegisteredObj(srvExtPerm.getObjId());
            if (obj==null)
                continue;
            if (permissionCheck(srvExtPerm, obj.getId(), srvId, minPermConnectionReq, minPermTypeReq))
                addGreaterServicePerm(allowedObjects, obj.getId(), srvExtPerm);
        }

        return allowedObjects;
    }

    public Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> getObjectAllowedServices(String objId, JOSPPerm.Connection minPermConnectionReq) {
        return getObjectAllowedServices(objId, minPermConnectionReq, JOSPPerm.Type.None);
    }

    // Query DB (obj)
    // Scan broker srv
    public Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> getObjectAllowedServices(String objId, JOSPPerm.Connection minPermConnectionReq, JOSPPerm.Type minPermTypeReq) {
        Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> allowedServices = new HashMap<>();

        List<Permission> objPermissions = permissionsDBService.findByObj(objId);

        // each obj's permission
        //     for each registered services
        //         if service allowed for access to obj
        //             add service (keep major permission)
        for (Permission objPerm : objPermissions)
            for (BrokerClientJSL srv : broker.getAllServices())
                if (permissionCheck(objPerm, objId, srv.getId(), minPermConnectionReq, minPermTypeReq))
                    addGreaterServicePerm(allowedServices, srv.getId(), objPerm);

        return allowedServices;
    }


    // Broker client's methods

    private BrokerClientJOD getRegisteredObj(String objId) {
        BrokerClientJOD obj = broker.getObject(objId);
        if (obj == null)
            obj = broker.getObjectDB(objId);
        if (obj == null)
            log.warn(String.format("Error on get Object '%s' because not registered on broker", objId));

        return obj;
    }

    private BrokerClientJSL getRegisteredSrv(String srvId) {
        BrokerClientJSL srv = broker.getService(srvId);
        if (srv == null)
            log.warn(String.format("Error on get Service '%s' because not registered on broker", srvId));

        return srv;
    }


    // Permission utils

    private boolean permissionCheck(Permission objPerm, String objId, String srvId, JOSPPerm.Connection minPermConnectionReq, JOSPPerm.Type minPermTypeReq) {
        BrokerClientJOD obj = getRegisteredObj(objId);
        if (obj==null)
            return false;
        BrokerClientJSL srv = getRegisteredSrv(srvId);

        String permSrvId = objPerm.getSrvId();
        String permUsrId = objPerm.getUsrId();
        JOSPPerm.Connection permConn = objPerm.getConnection();
        JOSPPerm.Type permType = objPerm.getType();

        if (!permSrvId.equals(JOSPPerm.WildCards.SRV_ALL.toString()))
            if (!permSrvId.equals(srv.getSrvId()))
                return false;

        if (!permUsrId.equals(JOSPPerm.WildCards.USR_ALL.toString()))
            if (!(permUsrId.equals(JOSPPerm.WildCards.USR_OWNER.toString())
                    && obj.getOwner().equals(srv.getUsrId()))
                    && !obj.getOwner().equals(JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString()))
                if (!permUsrId.equals(srv.getUsrId()))
                    return false;

        if (permConn.lowerThan(minPermConnectionReq))
            return false;

        if (permType.lowerThan(minPermTypeReq))
            return false;

        return true;
    }

    private static void addGreaterServicePerm(Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> allowedServices, String srvId, Permission objPerm) {
        if (allowedServices.containsKey(srvId)) {
            Pair<JOSPPerm.Type, JOSPPerm.Connection> existing = allowedServices.get(srvId);
            if (greaterThan_preferConnection(existing, objPerm))
                return;
        }

        allowedServices.put(srvId, new Pair<>(objPerm.getType(), objPerm.getConnection()));
    }

    @SuppressWarnings("unused")
    private static boolean greaterThan_preferConnection(Pair<JOSPPerm.Type, JOSPPerm.Connection> existing, Permission objPerm) {
        // existing.connection > perm.connection
        if (existing.getSecond().greaterThan(objPerm.getConnection()))
            return true;

        // existing.connection < perm.connection
        if (existing.getSecond().lowerThan(objPerm.getConnection()))
            return false;

        // existing.connection = perm.connection
        // existing.type > perm.type
        if (existing.getFirst().greaterThan(objPerm.getType()))
            return true;

        // existing.connection = perm.connection
        // existing.type <= perm.type
        return false;
    }

    @SuppressWarnings("unused")
    private static boolean greaterThan_preferType(Pair<JOSPPerm.Type, JOSPPerm.Connection> existing, Permission objPerm) {
        // existing.type > perm.type
        if (existing.getFirst().greaterThan(objPerm.getType()))
            return true;

        // existing.type < perm.type
        if (existing.getFirst().lowerThan(objPerm.getType()))
            return false;

        // existing.type = perm.type
        // existing.connection > perm.connection
        if (existing.getSecond().greaterThan(objPerm.getConnection()))
            return true;

        // existing.type = perm.type
        // existing.connection <= perm.connection
        return false;
    }

}
