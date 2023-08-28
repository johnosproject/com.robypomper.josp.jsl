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

import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaStructures;
import com.robypomper.java.JavaStructures.Pair;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.gws.exceptions.JSLServiceMissingPermissionException;
import com.robypomper.josp.protocol.JOSPPerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@SuppressWarnings("RedundantIfStatement")
public class Broker implements BrokerJOD, BrokerJSL, BrokerObjDB {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Broker.class);
    private final Map<String, BrokerClientJOD> registeredObjs = new HashMap<>();
    private final Map<String, BrokerClientJSL> registeredSrvs = new HashMap<>();
    private final Map<String, BrokerClientObjDB> registeredObjsDB = new HashMap<>();
    private final BrokerPermissions permissions;


    // Constructors

    public Broker(PermissionsDBService permissionsDBService) {
        this.permissions = new BrokerPermissions(this, permissionsDBService);
    }

    // Getters

    public List<BrokerClientJOD> getAllObjects() {
        return new ArrayList<>(registeredObjs.values());
    }

    public BrokerClientJOD getObject(String objId) {
        return registeredObjs.get(objId);
    }

    public List<BrokerClientJSL> getAllServices() {
        return new ArrayList<>(registeredSrvs.values());
    }

    public BrokerClientJSL getService(String srvId) {
        return registeredSrvs.get(srvId);
    }

    public List<BrokerClientObjDB> getAllObjectsDB() {
        return new ArrayList<>(registeredObjsDB.values());
    }

    public BrokerClientObjDB getObjectDB(String objId) {
        return registeredObjsDB.get(objId);
    }


    // Register methods

    @Override
    public void registerObject(BrokerClientObjDB gwObject) {
        synchronized (registeredObjsDB) {
            if (registeredObjsDB.containsKey(gwObject.getId())) {
                log.warn(String.format("Error registering Object (DB) '%s' to GW Broker, object already registered", gwObject.getId()));
                return;
            }

            registeredObjsDB.put(gwObject.getId(), gwObject);
        }
    }

    @Override
    public void deregisterObject(BrokerClientObjDB gwObject) {
        synchronized (registeredObjsDB) {
            if (!registeredObjsDB.containsKey(gwObject.getId())) {
                log.warn(String.format("Error registering Object (DB) '%s' to GW Broker, object NOT registered", gwObject.getId()));
                return;
            }

            registeredObjsDB.remove(gwObject.getId());
        }
    }

    @Override
    public void registerObject(BrokerClientJOD gwObject) {
        synchronized (registeredObjs) {
            if (registeredObjs.containsKey(gwObject.getId())) {
                log.warn(String.format("Error registering Object '%s' to GW Broker, object already registered", gwObject.getId()));
                return;
            }

            registeredObjs.put(gwObject.getId(), gwObject);
        }

        if (registeredObjsDB.containsKey(gwObject.getId()))
            registeredObjsDB.get(gwObject.getId()).pause();
    }

    @Override
    public void deregisterObject(BrokerClientJOD gwObject) {
        send(gwObject, gwObject.getMsgOBJ_DISCONNECTED(), JOSPPerm.Type.Status);

        synchronized (registeredObjs) {
            if (!registeredObjs.containsKey(gwObject.getId())) {
                log.warn(String.format("Error deregistering Object '%s' to GW Broker, object NOT registered", gwObject.getId()));
                return;
            }

            registeredObjs.remove(gwObject.getId());
        }

        if (!registeredObjsDB.containsKey(gwObject.getId())) {
            if (gwObject.getObjDB() != null)
                registeredObjsDB.put(gwObject.getId(), gwObject.getObjDB());
            else {
                JavaAssertions.makeAssertion_Failed("Error deregistering Object '%s' because objDB not available");
                return;
            }
        }

        registeredObjsDB.get(gwObject.getId()).resume();
    }

    @Override
    public void registerService(BrokerClientJSL gwService) {
        synchronized (registeredSrvs) {
            if (registeredSrvs.containsKey(gwService.getId())) {
                log.warn(String.format("Error registering Service '%s' to GW Broker, service already registered", gwService.getId()));
                return;
            }

            registeredSrvs.put(gwService.getId(), gwService);
        }

        Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> allowedObjects;

        allowedObjects = permissions.getServiceAllowedObjects(gwService.getId(), JOSPPerm.Connection.LocalAndCloud, JOSPPerm.Type.Status);
        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> objAllowed : allowedObjects.entrySet()) {
            BrokerClientJOD obj = registeredObjs.get(objAllowed.getKey());
            if (obj == null)
                obj = registeredObjsDB.get(objAllowed.getKey());
            Pair<JOSPPerm.Type, JOSPPerm.Connection> objPerm = objAllowed.getValue();

            send(obj, gwService.getId(), obj.getMsgOBJ_INFO(), JOSPPerm.Type.Status);
            send(obj, gwService.getId(), obj.getMsgOBJ_STRUCT(), JOSPPerm.Type.Status);
            send(obj, gwService.getId(), obj.getMsgOBJ_PERM(), JOSPPerm.Type.CoOwner);
            send(obj, gwService.getId(), obj.getMsgSERVICE_PERM(objPerm.getFirst(), objPerm.getSecond()), JOSPPerm.Type.None);
            if (obj instanceof BrokerClientObjDB)
                send(obj, gwService.getId(), obj.getMsgOBJ_DISCONNECTED(), JOSPPerm.Type.Status);
        }

        allowedObjects = permissions.getServiceAllowedObjects(gwService.getId(), JOSPPerm.Connection.LocalAndCloud, JOSPPerm.Type.CoOwner);
        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> objAllowed : allowedObjects.entrySet()) {
            BrokerClientJOD obj = registeredObjs.get(objAllowed.getKey());
            if (obj == null)
                obj = registeredObjsDB.get(objAllowed.getKey());

            send(obj, gwService.getId(), obj.getMsgOBJ_PERM(), JOSPPerm.Type.CoOwner);
        }

        log.info(String.format("Registered service '%s' to broker %d/%d/%d (online/total/service-available)", gwService.getId(), registeredObjs.size(), registeredObjsDB.size(), allowedObjects.size()));
        log.trace(String.format("          available objects      [%s]", String.join(", ", registeredObjs.keySet())));
        log.trace(String.format("          available objects (DB) [%s]", String.join(", ", registeredObjsDB.keySet())));
        log.trace(String.format("          allowed objects        [%s]", String.join(", ", allowedObjects.keySet())));
    }

    @Override
    public void deregisterService(BrokerClientJSL gwService) {
        synchronized (registeredSrvs) {
            if (!registeredSrvs.containsKey(gwService.getId())) {
                log.warn(String.format("Error registering Service '%s' to GW Broker, service NOT registered", gwService.getId()));
                return;
            }

            registeredSrvs.remove(gwService.getId());
        }
    }


    // Dispatch Message methods

    @Override
    public void send(BrokerClient gwClientJOD, String data, JOSPPerm.Type minPerm) {
        // from JOD to JSL
        // send to all service with at least 'minPerm' on gwClientJOD
        // send errors (PeerStreamException, PeerNotConnectedException) are only logged

        Set<String> srvIds = permissions.getObjectAllowedServices(gwClientJOD.getId(), JOSPPerm.Connection.LocalAndCloud, minPerm).keySet();
        //System.out.println("\n\t\tSend message " + data.substring(9,data.indexOf(' ',9)) + " from " + gwClientJOD.getId() + " to:");
        //System.out.println(" srvs: " + String.join(", ", srvIds) + "\n");
        for (String srvId : srvIds) {
            BrokerClientJSL srv = registeredSrvs.get(srvId);
            if (srv == null) {
                log.warn(String.format("Error forward data from Object '%s' to Service '%s', service not registered on broker", gwClientJOD.getId(), srvId));
                continue;
            }

            try {
                srv.send(data);

            } catch (PeerStreamException | PeerNotConnectedException e) {
                log.warn(String.format("Error forward data from Object '%s' to Service '%s'", gwClientJOD.getId(), srvId), e);
            }
        }
    }

    @Override
    public void send(BrokerClient gwClientJOD, String srvId, String data, JOSPPerm.Type minPerm) {
        // from JOD to JSL
        // send to 'gwClientJSL' service only if it has at least 'minPerm' on gwClientJOD
        // send errors (JSLServiceMissingPermissionException, PeerStreamException, PeerNotConnectedException) are only logged
        //System.out.println("\n\t\tSend message " + data.substring(9,data.indexOf(' ',9)) + " from " + gwClientJOD.getId() + " to " + srvId + "\n");

        if (minPerm!=JOSPPerm.Type.None && !permissions.checkServicePermissionOnObject(srvId, gwClientJOD.getId(), JOSPPerm.Connection.LocalAndCloud, minPerm)) {
            log.warn(String.format("Error send data from Object '%s' to Service '%s', service missing permission to object ", gwClientJOD.getId(), srvId));
            return;
        }

        BrokerClientJSL srv = registeredSrvs.get(srvId);
        if (srv == null) {
            log.warn(String.format("Error send data from Object '%s' to Service '%s', service not registered on broker", gwClientJOD.getId(), srvId));
            return;
        }

        try {
            srv.send(data);

        } catch (PeerStreamException | PeerNotConnectedException e) {
            log.warn(String.format("Error send data from Object '%s' to Service '%s'", gwClientJOD.getId(), srvId), e);
        }
    }

    @Override
    public void send(BrokerClientJSL gwClientJSL, String objId, String data, JOSPPerm.Type minPerm) throws JSLServiceMissingPermissionException, PeerStreamException, PeerNotConnectedException {
        // from JSL to JOD
        // send to 'objId' object only if 'gwClientJSL' has at least 'minPerm' on destination object
        // send errors (PeerStreamException, PeerNotConnectedException) are throw to caller methods
        //System.out.println("\n\t\tSend message " + data.substring(9,data.indexOf(' ',9)) + " from " + gwClientJSL.getId() + " to " + objId + "\n");

        BrokerClientJOD obj = registeredObjs.get(objId);
        if (obj == null) {
            if (registeredObjsDB.get(objId) == null)
                log.warn(String.format("Error forward data from Service '%s' to Object '%s', object not registered on broker", gwClientJSL.getId(), objId));
            else
                log.warn(String.format("Error forward data from Service '%s' to Object '%s', object not connected to broker", gwClientJSL.getId(), objId));
            return;
        }

        if (!permissions.checkServicePermissionOnObject(gwClientJSL.getId(), obj.getId(), JOSPPerm.Connection.LocalAndCloud, minPerm))
            throw new JSLServiceMissingPermissionException(gwClientJSL.getSrvId(), objId, minPerm, null);

        obj.send(data);
    }


    // Public permissions

    @Override
    public Map<String, JavaStructures.Pair<JOSPPerm.Type, JOSPPerm.Connection>> getObjectCloudAllowedServices(String objId) {
        return permissions.getObjectAllowedServices(objId, JOSPPerm.Connection.LocalAndCloud);
    }

    @Override
    public boolean checkServiceCloudPermissionOnObject(String srvId, String objId, JOSPPerm.Type minPerm) {
        return permissions.checkServicePermissionOnObject(srvId, objId, JOSPPerm.Connection.LocalAndCloud, minPerm);
    }

}
