/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.robypomper.josp.jsl.objs.remote;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.history.DefaultHistoryObjEvents;
import com.robypomper.josp.jsl.objs.history.HistoryObjEvents;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DefaultObjInfo extends ObjBase implements ObjInfo {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(DefaultObjInfo.class);
    private String name = null;
    private String ownerId = null;
    private String jodVersion = null;
    private String model = null;
    private String brand = null;
    private String longDescr = null;
    private final List<RemoteObjectInfoListener> listenersInfo = new ArrayList<>();
    private HistoryObjEvents eventsHistory;


    // Constructor

    public DefaultObjInfo(JSLRemoteObject remoteObject, JSLServiceInfo serviceInfo) {
        super(remoteObject, serviceInfo);
    }


    // Getters / Setters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return getRemote().getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name != null ? name : "N/A";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String newName) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectSetNameMsg(getServiceInfo().getFullId(), getId(), newName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOwnerId() {
        return ownerId != null ? ownerId : "N/A";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOwnerId(String newOwnerId) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectSetOwnerIdMsg(getServiceInfo().getFullId(), getId(), newOwnerId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJODVersion() {
        return jodVersion != null ? jodVersion : "N/A";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModel() {
        return model != null ? model : "N/A";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBrand() {
        return brand != null ? brand : "N/A";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLongDescr() {
        return longDescr != null ? longDescr : "N/A";
    }


    // Processing

    public boolean processObjectInfoMsg(String msg, JOSPPerm.Connection connType) throws Throwable {
        try {
            String newName = JOSPProtocol_ObjectToService.getObjectInfoMsg_Name(msg);
            if (name == null || !name.equals(newName)) {
                String oldName = name;
                name = newName;
                emitInfo_NameChanged(newName, oldName);
            }
            String newOwnerId = JOSPProtocol_ObjectToService.getObjectInfoMsg_OwnerId(msg);
            if (ownerId == null || !ownerId.equals(newOwnerId)) {
                String oldOwnerId = ownerId;
                ownerId = newOwnerId;
                emitInfo_OwnerIdChanged(newOwnerId, oldOwnerId);
            }
            String newJODVersion = JOSPProtocol_ObjectToService.getObjectInfoMsg_JODVersion(msg);
            if (jodVersion == null || !jodVersion.equals(newJODVersion)) {
                String oldJODVersion = jodVersion;
                jodVersion = newJODVersion;
                emitInfo_JODVersionChanged(jodVersion, oldJODVersion);
            }
            String newModel = JOSPProtocol_ObjectToService.getObjectInfoMsg_Model(msg);
            if (model == null || !model.equals(newModel)) {
                String oldModel = model;
                model = newModel;
                emitInfo_ModelChanged(model, oldModel);
            }
            String newBrand = JOSPProtocol_ObjectToService.getObjectInfoMsg_Brand(msg);
            if (brand == null || !brand.equals(newBrand)) {
                String oldBrand = brand;
                brand = newBrand;
                emitInfo_BrandChanged(brand, oldBrand);
            }
            String newLongDescr = JOSPProtocol_ObjectToService.getObjectInfoMsg_LongDescr(msg);
            if (longDescr == null || !longDescr.equals(newLongDescr)) {
                String oldLongDescr = longDescr;
                longDescr = newLongDescr;
                emitInfo_LongDescrChanged(longDescr, oldLongDescr);
            }

            if (connType == JOSPPerm.Connection.LocalAndCloud && !getRemote().getComm().isCloudConnected()) {
                boolean isCloudConnected = JOSPProtocol_ObjectToService.getObjectInfoMsg_IsCloudConnected(msg);
                ((DefaultObjComm) getRemote().getComm()).setCloudConnected(isCloudConnected);
            }

        } catch (JOSPProtocol.ParsingException e) {
            throw new Throwable(String.format("Error on processing ObjectInfo message for '%s' object because %s", getRemote().getId(), e.getMessage()), e);
        }

        return true;
    }


    // Listeners

    @Override
    public void addListener(RemoteObjectInfoListener listener) {
        if (listenersInfo.contains(listener))
            return;

        listenersInfo.add(listener);
    }

    @Override
    public void removeListener(RemoteObjectInfoListener listener) {
        if (!listenersInfo.contains(listener))
            return;

        listenersInfo.remove(listener);
    }

    private void emitInfo_NameChanged(String newName, String oldName) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onNameChanged(getRemote(), newName, oldName);
    }

    private void emitInfo_OwnerIdChanged(String newOwnerId, String oldOwnerId) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onOwnerIdChanged(getRemote(), newOwnerId, oldOwnerId);
    }

    private void emitInfo_JODVersionChanged(String jodVersion, String oldJODVersion) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onJODVersionChanged(getRemote(), jodVersion, oldJODVersion);
    }

    private void emitInfo_ModelChanged(String model, String oldModel) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onModelChanged(getRemote(), model, oldModel);
    }

    private void emitInfo_BrandChanged(String brand, String oldBrand) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onBrandChanged(getRemote(), brand, oldBrand);
    }

    private void emitInfo_LongDescrChanged(String longDescr, String oldLongDescr) {
        for (RemoteObjectInfoListener l : listenersInfo)
            l.onLongDescrChanged(getRemote(), longDescr, oldLongDescr);
    }


    // Events History

    @Override
    public List<JOSPEvent> getEventsHistory(HistoryLimits limits, int timeoutSeconds) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (eventsHistory == null) eventsHistory = new DefaultHistoryObjEvents(getRemote(), getServiceInfo());
        return eventsHistory.getEventsHistory(limits, timeoutSeconds);
    }


    @Override
    public void getEventsHistory(HistoryLimits limits, HistoryObjEvents.EventsListener listener) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (eventsHistory == null) eventsHistory = new DefaultHistoryObjEvents(getRemote(), getServiceInfo());
        eventsHistory.getEventsHistory(limits, listener);
    }

    public boolean processHistoryEventsMsg(String msg) {
        return ((DefaultHistoryObjEvents) eventsHistory).processHistoryEventsMsg(msg);
    }

}
