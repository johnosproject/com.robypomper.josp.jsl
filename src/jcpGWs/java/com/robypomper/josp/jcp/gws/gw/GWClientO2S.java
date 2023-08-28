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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.java.JavaDate;
import com.robypomper.java.JavaStructures.Pair;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.jcp.db.apis.entities.Object;
import com.robypomper.josp.jcp.db.apis.entities.*;
import com.robypomper.josp.jcp.gws.broker.BrokerClientJOD;
import com.robypomper.josp.jcp.gws.broker.BrokerJOD;
import com.robypomper.josp.jcp.gws.db.ObjDB;
import com.robypomper.josp.jcp.gws.exceptions.JODObjectIdNotEqualException;
import com.robypomper.josp.jcp.gws.exceptions.JODObjectNotInDBException;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GWClientO2S extends GWClientTCPAbs implements BrokerClientJOD {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(GWClientO2S.class);
    private final BrokerJOD broker;
    private final ObjectDBService objectDBService;
    private final PermissionsDBService permissionsDBService;
    private boolean isRegistered = false;
    private ObjDB objDB;


    // Constructors

    public GWClientO2S(ServerClient client, BrokerJOD gwBroker, ObjectDBService objectDBService, PermissionsDBService permissionsDBService) {
        super(client);
        this.broker = gwBroker;
        this.objectDBService = objectDBService;
        this.permissionsDBService = permissionsDBService;
    }


    // Getters

    protected BrokerJOD getBroker() {
        return broker;
    }

    public String getName() {
        return objDB.getName();
    }

    public String getOwner() {
        return objDB.getOwner();
    }

    public ObjDB getObjDB() {
        return objDB;
    }


    // Connection mngm

    private void registerToBroker() {
        synchronized (this) {
            objDB.getStatus().setOnline(true);
            objDB.getStatus().setLastConnectionAt(JavaDate.getNowDate());

            saveObjDBStatus(objDB);
        }

        getBroker().registerObject(this);
        isRegistered = true;
    }

    @Override
    protected void onDisconnected() {
        getBroker().deregisterObject(this);
        isRegistered = false;

        synchronized (this) {
            objDB.getStatus().setOnline(false);
            objDB.getStatus().setLastDisconnectionAt(JavaDate.getNowDate());

            saveObjDBStatus(objDB);
        }
    }


    // GWServer client's Messages methods

    public boolean processFromObjectMsg(String data) {
        String msgType = "UNKNOWN";

        try {
            if (JOSPProtocol_ObjectToService.isObjectInfoMsg(data)) {
                msgType = JOSPProtocol_ObjectToService.OBJ_INF_REQ_NAME;
                processObjectInfoMsg(data);
            } else if (JOSPProtocol_ObjectToService.isObjectStructMsg(data)) {
                msgType = JOSPProtocol_ObjectToService.OBJ_STRUCT_REQ_NAME;
                processObjectStructMsg(data);
            } else if (JOSPProtocol_ObjectToService.isObjectPermsMsg(data)) {
                msgType = JOSPProtocol_ObjectToService.OBJ_PERMS_REQ_NAME;
                processObjectPermsMsg(data);
            } else if (JOSPProtocol_ObjectToService.isObjectStateUpdMsg(data)) {
                msgType = JOSPProtocol_ObjectToService.UPD_MSG_NAME;
                processUpdateMsg(data);
            } else {
                log.warn(String.format("Error unrecognized data from object '%s'", getId()));
            }

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(String.format("Error on parsing data '%s' from object '%s'", msgType, getId()), e);

        } catch (JODObjectIdNotEqualException e) {
            log.warn(String.format("Error on processing data '%s' from object '%s' because data contain wrong object id '%s'", msgType, getId(), e.getMsgObjId()), e);

        } catch (JODObjectNotInDBException e) {
            log.warn(String.format("Error on processing data '%s' from object '%s' because object not stored in DB", msgType, getId()), e);

        } catch (Throwable e) {
            log.warn(String.format("Error on processing data '%s' from object '%s'", msgType, getId()), e);
        }
        return true;
    }

    private void processObjectInfoMsg(String data) throws JOSPProtocol.ParsingException, JODObjectIdNotEqualException {
        String objId = JOSPProtocol_ObjectToService.getObjId(data);
        if (!objId.equals(getId()))
            throw new JODObjectIdNotEqualException(objId, getId());
        String name = JOSPProtocol_ObjectToService.getObjectInfoMsg_Name(data);
        String ownerId = JOSPProtocol_ObjectToService.getObjectInfoMsg_OwnerId(data);
        String jodVers = JOSPProtocol_ObjectToService.getObjectInfoMsg_JODVersion(data);
        String model = JOSPProtocol_ObjectToService.getObjectInfoMsg_Model(data);
        String brand = JOSPProtocol_ObjectToService.getObjectInfoMsg_Brand(data);
        String longDescr = JOSPProtocol_ObjectToService.getObjectInfoMsg_LongDescr(data);

        updateInfo(objId, name, ownerId, jodVers, model, brand, longDescr);

        if (!isRegistered)
            registerToBroker();

        getBroker().send(this, data, JOSPPerm.Type.Status);
    }

    private void processObjectStructMsg(String data) throws JOSPProtocol.ParsingException, JODObjectIdNotEqualException, JODObjectNotInDBException {
        String objId = JOSPProtocol_ObjectToService.getObjId(data);
        if (!objId.equals(getId()))
            throw new JODObjectIdNotEqualException(objId, getId());
        String struct = JOSPProtocol_ObjectToService.getObjectStructMsg_Struct(data);

        updateStructure(struct);

        getBroker().send(this, data, JOSPPerm.Type.Status);
    }

    private void processObjectPermsMsg(String data) throws JOSPProtocol.ParsingException, JODObjectIdNotEqualException {
        String objId = JOSPProtocol_ObjectToService.getObjId(data);
        if (!objId.equals(getId()))
            throw new JODObjectIdNotEqualException(objId, getId());
        List<JOSPPerm> perms = JOSPProtocol_ObjectToService.getObjectPermsMsg_Perms(data);

        Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> oldAllowedServices = getBroker().getObjectCloudAllowedServices(getId());

        updatePerms(objId, perms);

        Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> newAllowedServices = getBroker().getObjectCloudAllowedServices(getId());

        Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> addedServices = new HashMap<>();
        Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> updatedServices = new HashMap<>();
        Map<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> removedServices = new HashMap<>();
        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> service : newAllowedServices.entrySet())
            if (!oldAllowedServices.containsKey(service.getKey()))
                addedServices.put(service.getKey(),service.getValue());
        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> service : newAllowedServices.entrySet())
            if (oldAllowedServices.containsKey(service.getKey())
                    && !oldAllowedServices.get(service.getKey()).equals(service.getValue()))
                updatedServices.put(service.getKey(),service.getValue());
        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> service : oldAllowedServices.entrySet())
            if (!newAllowedServices.containsKey(service.getKey()))
                removedServices.put(service.getKey(),service.getValue());

        // System.out.println("");
        // System.out.println("Updated permissions on " + objId + " object:");
        // System.out.println(" ADD: " + String.join(", ", addedServices.keySet()));
        // System.out.println(" UPD: " + String.join(", ", updatedServices.keySet()));
        // System.out.println(" REM: " + String.join(", ", removedServices.keySet()));
        // System.out.println("");

        // added (send presentations)
        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> newService : addedServices.entrySet()) {
            getBroker().send(this, newService.getKey(), getMsgOBJ_INFO(), JOSPPerm.Type.Status);
            getBroker().send(this, newService.getKey(), getMsgOBJ_STRUCT(), JOSPPerm.Type.Status);
        }

        getBroker().send(this, data, JOSPPerm.Type.CoOwner);

        // added
        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> newService : addedServices.entrySet())
            getBroker().send(this, newService.getKey(), getMsgSERVICE_PERM(newService.getValue().getFirst(), newService.getValue().getSecond()), JOSPPerm.Type.None);

        // to update
        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> newService : updatedServices.entrySet())
            getBroker().send(this, newService.getKey(), getMsgSERVICE_PERM(newService.getValue().getFirst(), newService.getValue().getSecond()), JOSPPerm.Type.None);

        // to remove
        for (Map.Entry<String, Pair<JOSPPerm.Type, JOSPPerm.Connection>> oldService : removedServices.entrySet())
            getBroker().send(this, oldService.getKey(), getMsgSERVICE_PERM(JOSPPerm.Type.None, oldService.getValue().getSecond()), JOSPPerm.Type.None);
    }

    private void processUpdateMsg(String data) throws JOSPProtocol.ParsingException, JODObjectIdNotEqualException, JODObjectNotInDBException {
        String objId = JOSPProtocol_ObjectToService.getObjId(data);
        if (!objId.equals(getId()))
            throw new JODObjectIdNotEqualException(objId, getId());
        JOSPProtocol.StatusUpd upd = JOSPProtocol.extractStatusUpdFromMsg(data);

        updateStructureStatus(objId, upd);
        getBroker().send(this, data, JOSPPerm.Type.Status);
    }


    // Update methods

    private void updateInfo(String objId, String name, String ownerId, String jodVers, String model, String brand, String longDescr) {
        synchronized (this) {
            Object objDB = getOrCreateObjDB(objId);

            objDB.getOwner().setObjId(objId);
            objDB.getOwner().setOwnerId(ownerId);

            objDB.setObjId(objId);
            objDB.setName(name);
            objDB.setActive(true);
            objDB.setVersion(jodVers);

            objDB.getInfo().setObjId(objId);
            objDB.getInfo().setModel(model);
            objDB.getInfo().setBrand(brand);
            objDB.getInfo().setLongDescr(longDescr);

            objDB.getStatus().setObjId(objId);
            objDB.getStatus().setOnline(true);
            objDB.getStatus().setLastConnectionAt(JavaDate.getNowDate());

            saveObjDB(objDB);
        }
    }

    private void updateStructure(String struct) {
        synchronized (this) {
            if (objDB.getStatus().getStructure() != null
                    && objDB.getStatus().getStructure().equals(struct))
                return;

            objDB.getStatus().setStructure(struct);
            objDB.getStatus().setLastStructUpdateAt(JavaDate.getNowDate());

            saveObjDBStatus(objDB);
        }
    }

    private void updatePerms(String objId, List<JOSPPerm> perms) {
        List<Permission> oldPermissions = permissionsDBService.findByObj(objId);
        List<Permission> newPermissions = PermissionsDBService.jospPermsToDBPerms(perms);

        synchronized (permissionsDBService) {       // this should be sync on all access to permissionSBServices also from GWBroker class
            permissionsDBService.removeAll(oldPermissions);
            permissionsDBService.addAll(newPermissions);
        }
    }

    private void updateStructureStatus(String objId, JOSPProtocol.StatusUpd state) {
        String errUpdatingMsg = String.format("Error updating object '%s' status on DB because ", objId);
        ObjectMapper mapper = new ObjectMapper();

        synchronized (this) {
            String stateStr = updateStructureStatus_extractStateValue(state);
            if (stateStr == null) {
                log.warn(errUpdatingMsg + "unknown state type.");
                return;
            }

            String struct = objDB.getStatus().getStructure();
            if (struct == null) {
                log.warn(errUpdatingMsg + "object's structure not set.");
                return;
            }

            Map<String, java.lang.Object> structMap;
            try {
                TypeReference<HashMap<String, java.lang.Object>> typeRef = new TypeReference<HashMap<String, java.lang.Object>>() {
                };
                structMap = mapper.readValue(struct, typeRef);

            } catch (JsonProcessingException e) {
                log.warn(errUpdatingMsg + "can't parse structure stored on DB.", e);
                return;
            }

            ArrayList<Map<String, java.lang.Object>> allComps;
            java.lang.Object components = structMap.get("components");
            if (!(components instanceof ArrayList)) {
                log.warn(errUpdatingMsg + "can't parse structure stored on DB ('components' field not found or is not ArrayList).");
                return;
            }
            allComps = (ArrayList<Map<String, java.lang.Object>>) components;

            Map<String, java.lang.Object> updatableComp = updateStructureStatus_extractComponent(state, allComps);
            if (updatableComp == null) {
                log.warn(errUpdatingMsg + "updating state not found in structure stored on DB.");
                return;
            }

            updatableComp.put("state", stateStr);

            String structUpdated;
            try {
                structUpdated = mapper.writeValueAsString(structMap);
            } catch (JsonProcessingException e) {
                log.warn(errUpdatingMsg + "can't serialize updated structure.", e);
                return;
            }

            objDB.getStatus().setStructure(structUpdated);
            objDB.getStatus().setLastStatusUpdAt(JavaDate.getNowDate());

            saveObjDBStatus(objDB);
        }
    }

    private String updateStructureStatus_extractStateValue(JOSPProtocol.StatusUpd state) {
        JOSPProtocol.JOSPStateUpdateStr updStr = (JOSPProtocol.JOSPStateUpdateStr) state.getUpdate();

        if (JSLBooleanState.JOSPBoolean.class.getSimpleName().compareToIgnoreCase(updStr.getType()) == 0)
            return Boolean.toString(new JSLBooleanState.JOSPBoolean(updStr.encode()).newState);

        if (JSLRangeState.JOSPRange.class.getSimpleName().compareToIgnoreCase(updStr.getType()) == 0)
            return Double.toString(new JSLRangeState.JOSPRange(updStr.encode()).newState);

        return null;
    }

    private Map<String, java.lang.Object> updateStructureStatus_extractComponent(JOSPProtocol.StatusUpd state, ArrayList<Map<String, java.lang.Object>> allComps) {
        for (String compName : state.getComponentPath().split(">"))
            for (Map<String, java.lang.Object> subComp : allComps)
                if (subComp.get("name").equals(compName)) {
                    allComps = (ArrayList<Map<String, java.lang.Object>>) subComp.get("components");
                    if (allComps == null)
                        return subComp;
                    break;
                }
        return null;
    }


    // DB methods

    private Object getOrCreateObjDB(String objId) {
        Optional<Object> optObj = objectDBService.find(objId);
        if (optObj.isPresent())
            return optObj.get();

        Object objDB = new Object();
        objDB.setOwner(new ObjectOwner());
        objDB.setInfo(new ObjectInfo());
        objDB.setStatus(new ObjectStatus());
        return objDB;
    }

    private void saveObjDB(Object objDB) {
        Object obj = objectDBService.save(objDB);
        if (this.objDB == null)
            this.objDB = new ObjDB(obj, permissionsDBService);
        else
            this.objDB.setObjDB(obj);
    }

    private void saveObjDBStatus(ObjDB objDB) {
        objectDBService.save(objDB.getStatus());
    }


    // Object presentation messages

    public String getMsgOBJ_INFO() {
        return objDB.getMsgOBJ_INFO();
    }

    public String getMsgOBJ_STRUCT() {
        return objDB.getMsgOBJ_STRUCT();
    }

    public String getMsgOBJ_PERM() {
        return objDB.getMsgOBJ_PERM();
    }

    public String getMsgSERVICE_PERM(JOSPPerm.Type type, JOSPPerm.Connection conn) {
        return objDB.getMsgSERVICE_PERM(type, conn);
    }

    public String getMsgOBJ_DISCONNECTED() {
        return objDB.getMsgOBJ_DISCONNECTED();
    }
}
