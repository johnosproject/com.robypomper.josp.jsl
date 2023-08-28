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

import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.java.JavaDate;
import com.robypomper.josp.jcp.db.apis.EventDBService;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.db.apis.StatusHistoryDBService;
import com.robypomper.josp.jcp.db.apis.entities.Event;
import com.robypomper.josp.jcp.db.apis.entities.ObjectStatusHistory;
import com.robypomper.josp.jcp.db.apis.entities.Service;
import com.robypomper.josp.jcp.db.apis.entities.ServiceStatus;
import com.robypomper.josp.jcp.gws.broker.BrokerClientJSL;
import com.robypomper.josp.jcp.gws.broker.BrokerJSL;
import com.robypomper.josp.jcp.gws.exceptions.JSLServiceMissingPermissionException;
import com.robypomper.josp.jcp.gws.exceptions.JSLServiceNotInDBException;
import com.robypomper.josp.jcp.gws.exceptions.JSLServiceNotRegisteredException;
import com.robypomper.josp.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GWClientS2O extends GWClientTCPAbs implements BrokerClientJSL {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(GWClientS2O.class);
    private final BrokerJSL broker;
    private final ServiceDBService serviceDBService;
    private final EventDBService eventsDBService;
    private final StatusHistoryDBService statusesHistoryDBService;
    private final String srvId;
    private final String usrId;


    // Constructors

    public GWClientS2O(ServerClient client, BrokerJSL gwBroker, ServiceDBService serviceDBService,
                       EventDBService eventsDBService, StatusHistoryDBService statusesHistoryDBService) throws JSLServiceNotRegisteredException {
        super(client);
        this.broker = gwBroker;
        this.serviceDBService = serviceDBService;
        this.eventsDBService = eventsDBService;
        this.statusesHistoryDBService = statusesHistoryDBService;

        this.srvId = JOSPProtocol_Service.fullSrvIdToSrvId(getId());
        this.usrId = JOSPProtocol_Service.fullSrvIdToUsrId(getId());
        String instId = JOSPProtocol_Service.fullSrvIdToInstId(getId());


        Optional<ServiceStatus> srvStatusOpt = serviceDBService.findStatus(getId());
        ServiceStatus srvStatusDB;
        if (srvStatusOpt.isPresent())
            srvStatusDB = srvStatusOpt.get();
        else {
            srvStatusDB = new ServiceStatus();
            srvStatusDB.setFullId(getId());
            srvStatusDB.setSrvId(srvId);
            srvStatusDB.setUsrId(usrId);
            srvStatusDB.setInstId(instId);
            //srvStatus.setVersion(instId);
        }
        saveSrvDBStatus(srvStatusDB);

        getBroker().registerService(this);
        onRegisteredToBroker();
    }


    // Getters

    protected BrokerJSL getBroker() {
        return broker;
    }

    public String getName() {
        Optional<Service> optSrvStatus = serviceDBService.find(srvId);
        if (!optSrvStatus.isPresent())
            return "N/A";
        return optSrvStatus.get().getSrvName();
    }

    public String getSrvId() {
        return srvId;
    }

    public String getUsrId() {
        return usrId;
    }

    // Connection mngm

    private void onRegisteredToBroker() {
        synchronized (this) {
            ServiceStatus srvStatusDB;
            try {
                srvStatusDB = getSrvDBStatus(getId());
            } catch (JSLServiceNotInDBException e) {
                log.warn(String.format("Error set service '%s' online because service not stored in DB", getId()));
                try {
                    forceDisconnection();
                } catch (PeerDisconnectionException peerDisconnectionException) {
                    log.warn(String.format("Error on Service '%s' forced disconnection", getId()));
                }
                return;
            }

            srvStatusDB.setOnline(true);
            srvStatusDB.setLastConnectionAt(JavaDate.getNowDate());

            saveSrvDBStatus(srvStatusDB);
        }
    }

    @Override
    protected void onDisconnected() {
        getBroker().deregisterService(this);

        synchronized (this) {
            ServiceStatus srvDB;
            try {
                srvDB = getSrvDBStatus(getId());
            } catch (JSLServiceNotInDBException e) {
                log.warn(String.format("Error set service '%s' offline because service not stored in DB", getId()));
                return;
            }

            srvDB.setOnline(false);
            srvDB.setLastDisconnectionAt(JavaDate.getNowDate());

            saveSrvDBStatus(srvDB);
        }
    }


    // GWServer client's Messages methods

    public boolean processFromServiceMsg(String data) {
        String objId;
        try {
            objId = JOSPProtocol_ServiceToObject.getObjId(data);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(String.format("Error parsing data from service '%s', no object's id", getId()), e);
            return true;
        }

        String msgType = "UNKNOWN";
        try {
            if (JOSPProtocol_ServiceToObject.isObjectSetNameMsg(data)) {
                msgType = JOSPProtocol_ServiceToObject.OBJ_SETNAME_REQ_NAME;
                processObjectSetNameMsg(objId, data);

            } else if (JOSPProtocol_ServiceToObject.isObjectSetOwnerIdMsg(data)) {
                msgType = JOSPProtocol_ServiceToObject.OBJ_SETOWNERID_REQ_NAME;
                processObjectSetOwnerMsg(objId, data);

            } else if (JOSPProtocol_ServiceToObject.isObjectAddPermMsg(data)) {
                msgType = JOSPProtocol_ServiceToObject.OBJ_ADDPERM_REQ_NAME;
                processObjectAddPermMsg(objId, data);

            } else if (JOSPProtocol_ServiceToObject.isObjectUpdPermMsg(data)) {
                msgType = JOSPProtocol_ServiceToObject.OBJ_UPDPERM_REQ_NAME;
                processObjectUpdPermMsg(objId, data);

            } else if (JOSPProtocol_ServiceToObject.isObjectRemPermMsg(data)) {
                msgType = JOSPProtocol_ServiceToObject.OBJ_REMPERM_REQ_NAME;
                processObjectRemPermMsg(objId, data);

            } else if (JOSPProtocol_ServiceToObject.isObjectActionCmdMsg(data)) {
                msgType = JOSPProtocol_ServiceToObject.CMD_MSG_NAME;
                processObjectActionMsg(objId, data);

            } else if (JOSPProtocol_ServiceToObject.isHistoryEventsMsg(data)) {
                msgType = JOSPProtocol_ServiceToObject.HISTORY_EVENTS_REQ_NAME;
                processHistoryEventsMsg(objId, data);

            } else if (JOSPProtocol_ServiceToObject.isHistoryCompStatusMsg(data)) {
                msgType = JOSPProtocol_ServiceToObject.HISTORY_EVENTS_REQ_NAME;
                processHistoryCompMsg(objId, data);

            } else {
                log.warn(String.format("Error unrecognized data from service '%s'", getId()));
            }

        } catch (JSLServiceMissingPermissionException e) {
            log.warn(String.format("Error on forward data '%s' from service '%s' to '%s' object because missing permission (req: %s; actual: %s)", msgType, getId(), objId, e.getMinPermReq(), e.getCurrentPerm()), e);

        } catch (PeerStreamException | PeerNotConnectedException e) {
            log.warn(String.format("Error on sending data '%s' from service '%s' to '%s' object because object not connected or stream error", msgType, getId(), objId), e);

        } catch (Throwable e) {
            log.warn(String.format("Error on processing data '%s' from service '%s' to '%s' object", msgType, getId(), objId), e);

        }

        return true;
    }


    // To JOD messages

    private void processObjectSetNameMsg(String objId, String data) throws JSLServiceMissingPermissionException, PeerStreamException, PeerNotConnectedException {
        getBroker().send(this, objId, data, JOSPPerm.Type.CoOwner);
    }

    private void processObjectSetOwnerMsg(String objId, String data) throws JSLServiceMissingPermissionException, PeerStreamException, PeerNotConnectedException {
        getBroker().send(this, objId, data, JOSPPerm.Type.CoOwner);
    }

    private void processObjectAddPermMsg(String objId, String data) throws JSLServiceMissingPermissionException, PeerStreamException, PeerNotConnectedException {
        getBroker().send(this, objId, data, JOSPPerm.Type.CoOwner);
    }

    private void processObjectUpdPermMsg(String objId, String data) throws JSLServiceMissingPermissionException, PeerStreamException, PeerNotConnectedException {
        getBroker().send(this, objId, data, JOSPPerm.Type.CoOwner);
    }

    private void processObjectRemPermMsg(String objId, String data) throws JSLServiceMissingPermissionException, PeerStreamException, PeerNotConnectedException {
        getBroker().send(this, objId, data, JOSPPerm.Type.CoOwner);
    }

    private void processObjectActionMsg(String objId, String data) throws JSLServiceMissingPermissionException, PeerStreamException, PeerNotConnectedException {
        getBroker().send(this, objId, data, JOSPPerm.Type.Actions);
    }


    // To JCP messages

    private void processHistoryEventsMsg(String objId, String data) throws JOSPProtocol.ParsingException {
        if (!getBroker().checkServiceCloudPermissionOnObject(getId(), objId, JOSPPerm.Type.CoOwner)) {
            log.warn(String.format("Error Service '%s' have NOT enough permission to request events on Object '%s'", getId(), objId));
            return;
        }

        String reqId = JOSPProtocol_ServiceToObject.getHistoryEventsMsg_ReqId(data);
        HistoryLimits limits = JOSPProtocol_ServiceToObject.getHistoryEventsMsg_Limits(data);

        List<Event> objEvents = eventsDBService.find(objId, limits);
        List<JOSPEvent> eventsHistory = new ArrayList<>();
        for (Event e : objEvents)
            eventsHistory.add(Event.toJOSPEvent(e));

        String resMsg = JOSPProtocol_ObjectToService.createHistoryEventsMsg(objId, reqId, eventsHistory);

        try {
            send(resMsg);

        } catch (PeerStreamException | PeerNotConnectedException e) {
            log.warn(String.format("Error on sending '%s' response to Service '%s' related to Object '%s'", JOSPProtocol_ObjectToService.HISTORY_EVENTS_REQ_NAME, getId(), objId));
        }
    }

    private void processHistoryCompMsg(String objId, String data) throws JOSPProtocol.ParsingException {
        if (!getBroker().checkServiceCloudPermissionOnObject(getId(), objId, JOSPPerm.Type.Status)) {
            log.warn(String.format("Error Service '%s' have NOT enough permission to request component history on Object '%s'", getId(), objId));
            return;
        }

        String reqId = JOSPProtocol_ServiceToObject.getHistoryCompStatusMsg_ReqId(data);
        String compPath = JOSPProtocol_ServiceToObject.getHistoryCompStatusMsg_CompPath(data);
        HistoryLimits limits = JOSPProtocol_ServiceToObject.getHistoryCompStatusMsg_Limits(data);

        List<ObjectStatusHistory> objStatuses = statusesHistoryDBService.find(objId, compPath, limits);
        List<JOSPStatusHistory> statusesHistory = new ArrayList<>();
        for (ObjectStatusHistory s : objStatuses)
            statusesHistory.add(ObjectStatusHistory.toJOSPStatusHistory(s));

        String resMsg = JOSPProtocol_ObjectToService.createHistoryCompStatusMsg(objId, compPath, reqId, statusesHistory);

        try {
            send(resMsg);

        } catch (PeerStreamException | PeerNotConnectedException e) {
            log.warn(String.format("Error on sending '%s' response to Service '%s' related to Object '%s'", JOSPProtocol_ObjectToService.HISTORY_STATUS_REQ_NAME, getId(), objId));
        }
    }


    // DB methods

    private ServiceStatus getSrvDBStatus(String srvId) throws JSLServiceNotInDBException {
        Optional<ServiceStatus> optSrvStatus = serviceDBService.findStatus(srvId);
        if (optSrvStatus.isPresent())
            return optSrvStatus.get();

        throw new JSLServiceNotInDBException(srvId);
    }

    private void saveSrvDBStatus(ServiceStatus srvStatusDB) {
        serviceDBService.save(srvStatusDB);
    }

}
