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

package com.robypomper.josp.jsl.objs;

import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.remote.*;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.objs.structure.JSLActionParams;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Default implementation of {@link JSLRemoteObject} interface.
 */
public class DefaultJSLRemoteObject implements JSLRemoteObject {

    // Internal vars

    private static final Logger log = LogManager.getLogger();

    private final DefaultObjInfo objInfo;
    private final DefaultObjStruct objStruct;
    private final DefaultObjPerms objPerms;
    private final DefaultObjComm objComm;

    private final String objId;


    // Constructor

    /**
     * Default constructor that set reference to current {@link JSLServiceInfo},
     * represented object's id and the first client connected to represented JOD
     * object.
     *
     * @param srvInfo       current service info.
     * @param objId         represented object's id.
     * @param communication instance of the {@link JSLCommunication}.
     */
    public DefaultJSLRemoteObject(JSLServiceInfo srvInfo, String objId, JSLCommunication communication) {
        this(srvInfo,objId,null,communication);
    }

    /**
     * Default constructor that set reference to current {@link JSLServiceInfo},
     * represented object's id and the first client connected to represented JOD
     * object.
     *
     * @param srvInfo       current service info.
     * @param objId         represented object's id.
     * @param localClient   the client connected with JOD object.
     * @param communication instance of the {@link JSLCommunication}.
     */
    public DefaultJSLRemoteObject(JSLServiceInfo srvInfo, String objId, JSLLocalClient localClient, JSLCommunication communication) {
        objInfo = new DefaultObjInfo(this, srvInfo);
        objStruct = new DefaultObjStruct(this, srvInfo);
        objPerms = new DefaultObjPerms(this, srvInfo);
        objComm = new DefaultObjComm(this, srvInfo, communication);

        this.objId = objId;

        if (localClient!=null) {
            objComm.addLocalClient(localClient);
            log.info(Mrk_JSL.JSL_OBJS_SUB, String.format("Initialized JSLRemoteObject '%s' on '%s' service (via direct connection: '%s')", objId, srvInfo.getSrvId(), localClient.getConnectionInfo()));
        } else
            log.info(Mrk_JSL.JSL_OBJS_SUB, String.format("Initialized JSLRemoteObject '%s' on '%s' service (via cloud connection)", objId, srvInfo.getSrvId()));
    }


    // Remote Object's basic info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return objId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getInfo().getName();
    }


    // Remote Object's sections getters

    @Override
    public ObjInfo getInfo() {
        return objInfo;
    }

    @Override
    public ObjStruct getStruct() {
        return objStruct;
    }

    @Override
    public ObjPerms getPerms() {
        return objPerms;
    }

    @Override
    public ObjComm getComm() {
        return objComm;
    }


    // To / From Object Msg

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendObjectCmdMsg(JSLAction component, JSLActionParams command) throws ObjectNotConnected, MissingPermission {
        objStruct.sendObjectCmdMsg(component, command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean processFromObjectMsg(String msg, JOSPPerm.Connection connType) throws Throwable {
        if (JOSPProtocol_ObjectToService.isObjectInfoMsg(msg))
            return ((DefaultObjInfo) getInfo()).processObjectInfoMsg(msg, connType);

        else if (JOSPProtocol_ObjectToService.isObjectStructMsg(msg))
            return ((DefaultObjStruct) getStruct()).processObjectStructMsg(msg);
        else if (JOSPProtocol_ObjectToService.isObjectStateUpdMsg(msg))
            return ((DefaultObjStruct) getStruct()).processObjectUpdMsg(msg);
        else if (JOSPProtocol_ObjectToService.isHistoryCompStatusMsg(msg))
            return ((DefaultObjStruct) getStruct()).processHistoryCompStatusMsg(msg);
        else if (JOSPProtocol_ObjectToService.isHistoryEventsMsg(msg))
            return ((DefaultObjInfo) getInfo()).processHistoryEventsMsg(msg);

        else if (JOSPProtocol_ObjectToService.isObjectPermsMsg(msg))
            return ((DefaultObjPerms) getPerms()).processObjectPermsMsg(msg);
        else if (JOSPProtocol_ObjectToService.isServicePermsMsg(msg))
            return ((DefaultObjPerms) getPerms()).processServicePermMsg(msg);

        else if (JOSPProtocol_ObjectToService.isObjectDisconnectMsg(msg))
            return ((DefaultObjComm) getComm()).processObjectDisconnectMsg(msg, connType);

        throw new Throwable("Unknown message type");
    }


}
