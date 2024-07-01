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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.history.DefaultHistoryCompStatus;
import com.robypomper.josp.jsl.objs.history.HistoryCompStatus;
import com.robypomper.josp.jsl.objs.structure.*;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultObjStruct extends ObjBase implements ObjStruct {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(DefaultObjStruct.class);
    private JSLRoot root = null;
    private final List<RemoteObjectStructListener> listenersInfo = new ArrayList<>();
    private Map<JSLComponent, HistoryCompStatus> compsStatusHistory = new HashMap<>();


    // Constructor

    public DefaultObjStruct(JSLRemoteObject remoteObject, JSLServiceInfo serviceInfo) {
        super(remoteObject, serviceInfo);
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInit() {
        return root != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLRoot getStructure() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    public JSLComponent getComponent(String compPath) {
        return getComponent(new DefaultJSLComponentPath(compPath));
    }

    /**
     * {@inheritDoc}
     */
    public JSLComponent getComponent(JSLComponentPath compPath) {
        return DefaultJSLComponentPath.searchComponent(root, compPath);
    }


    // Processing

    public boolean processObjectStructMsg(String msg) {
        String msgSubStr = msg.substring(0, Math.min(10, msg.length()));
        ObjectMapper mapper = new ObjectMapper();
        String structStr;

        try {
            structStr = JOSPProtocol_ObjectToService.getObjectStructMsg_Struct(msg);
            InjectableValues.Std injectVars = new InjectableValues.Std();
            injectVars.addValue(JSLRemoteObject.class, getRemote());
            mapper.setInjectableValues(injectVars);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(String.format("%s Error on processing ObjectStructure message '%s...' for '%s' object because %s",
                    getLogRO(), msgSubStr, getRemote().getId(), e.getMessage()), e);
            return false;
        }

        try {
            root = mapper.readValue(structStr, JSLRoot_Jackson.class);

        } catch (JsonProcessingException e) {
            log.warn(String.format("%s Error on processing ObjectStructure message '%s...' for '%s' object because %s",
                    getLogRO(), msgSubStr, getRemote().getId(), e.getMessage()), e);
            return false;
        }

        emitInfo_StructureChanged(root);
        return true;
    }

    public boolean processObjectUpdMsg(String msg) {
        String msgSubStr = msg.substring(0, Math.min(10, msg.length()));

        // parse received data
        JOSPProtocol.StatusUpd upd;
        try {
            upd = JOSPProtocol.fromMsgToUpd(msg, AbsJSLState.getStateClasses());
        } catch (JOSPProtocol.ParsingException e) {
            log.warn(String.format("%s Error on parsing update '%s...' because %s",
                    getLogRO(), msgSubStr, e.getMessage()), e);
            return false;
        }
        // search destination object/components
        JSLComponentPath compPath = new DefaultJSLComponentPath(upd.getComponentPath());
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(getStructure(), compPath);

        // forward update msg
        log.trace(String.format("%s Processing update on '%s' component for '%s' object",
                getLogRO(), compPath.getString(), getRemote().getId()));
        if (comp == null) {
            log.warn(String.format("%s Error on processing update on '%s' component for '%s' object because component not found",
                    getLogRO(), compPath.getString(), getRemote().getId()));
            return false;
        }
        if (!(comp instanceof JSLState)) {
            log.warn(String.format("%s Error on processing update on '%s' component for '%s' object because component not a status component",
                    getLogRO(), compPath.getString(), getRemote().getId()));
            return false;
        }
        JSLState stateComp = (JSLState) comp;

        // set object/component's update
        if (stateComp.updateStatus(upd)) {
            String state = "";
            if (stateComp instanceof JSLBooleanState)
                state = Boolean.toString(((JSLBooleanState)stateComp).getState());
            else if (stateComp instanceof JSLRangeState)
                state = Double.toString(((JSLRangeState)stateComp).getState());
            log.info(String.format("%s Updated status of '%s' component with value '%s' for '%s' object",
                    getLogRO(), compPath.getString(), state, getRemote().getId()));

        } else {
            log.warn(String.format("%s Error on processing update on '%s' component for '%s' object",
                    getLogRO(), compPath.getString(), getRemote().getId()));
            return false;
        }

        log.trace(String.format("%s Update '%s...' processed for '%s' object",
                getLogRO(), msgSubStr, getRemote().getId()));
        return true;
    }

    public boolean processHistoryCompStatusMsg(String msg) {
        String msgSubStr = msg.substring(0, Math.min(10, msg.length()));

        String compPath;
        try {
            compPath = JOSPProtocol_ObjectToService.getHistoryResMsg_CompPath(msg);
        } catch (JOSPProtocol.ParsingException e) {
            log.warn(String.format("%s Error on parsing update '%s...' because %s",
                    getLogRO(), msgSubStr, e.getMessage()), e);
            return false;
        }
        JSLComponent component = getComponent(compPath);
        DefaultHistoryCompStatus compStatus = (DefaultHistoryCompStatus) getComponentHistory(component);
        return compStatus.processHistoryCompStatusMsg(msg);
    }

    // Listeners

    @Override
    public void addListener(RemoteObjectStructListener listener) {
        if (listenersInfo.contains(listener))
            return;

        listenersInfo.add(listener);
    }

    @Override
    public void removeListener(RemoteObjectStructListener listener) {
        if (!listenersInfo.contains(listener))
            return;

        listenersInfo.remove(listener);
    }

    private void emitInfo_StructureChanged(JSLRoot root) {
        for (RemoteObjectStructListener l : listenersInfo)
            l.onStructureChanged(getRemote(), root);
    }


    // Senders

    public void sendObjectCmdMsg(JSLAction component, JSLActionParams command) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        sendToObject(JOSPProtocol_ServiceToObject.createObjectActionCmdMsg(getServiceInfo().getFullId(), getRemote().getId(), component.getPath().getString(), command));
    }


    // Components History

    @Override
    public List<JOSPHistory> getComponentHistory(JSLComponent component, HistoryLimits limits, int timeoutSeconds) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        return getComponentHistory(component).getStatusHistory(limits, timeoutSeconds);
    }

    @Override
    public void getComponentHistory(JSLComponent component, HistoryLimits limits, HistoryCompStatus.StatusHistoryListener listener) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        getComponentHistory(component).getStatusHistory(limits, listener);
    }

    private HistoryCompStatus getComponentHistory(JSLComponent component) {
        if (compsStatusHistory.get(component) == null)
            compsStatusHistory.put(component, new DefaultHistoryCompStatus(component, getServiceInfo()));
        return compsStatusHistory.get(component);
    }

}
