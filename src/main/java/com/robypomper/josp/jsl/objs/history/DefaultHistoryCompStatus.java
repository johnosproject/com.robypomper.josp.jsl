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

package com.robypomper.josp.jsl.objs.history;

import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.exception.PeerStreamException;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DefaultHistoryCompStatus extends HistoryBase implements HistoryCompStatus {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(DefaultHistoryCompStatus.class);
    private final JSLComponent comp;
    private Map<Integer, StatusHistoryListener> listeners = new HashMap<>();
    private int reqCount = 0;


    // Constructor

    public DefaultHistoryCompStatus(JSLComponent comp, JSLServiceInfo srvInfo) {
        super(comp.getRemoteObject(), srvInfo);
        this.comp = comp;
    }


    // Getters

    protected JSLComponent getComponent() {
        return comp;
    }

    protected JSLRemoteObject getRemote() {
        return comp.getRemoteObject();
    }

    @Override
    public List<JOSPStatusHistory> getStatusHistory(HistoryLimits limits, long timeout) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        final List<JOSPStatusHistory> result = new ArrayList<>();
        final CountDownLatch countdown = new CountDownLatch(1);
        // register internal listener
        int reqId = registerListener(new StatusHistoryListener() {
            @Override
            public void receivedStatusHistory(List<JOSPStatusHistory> history) {
                result.addAll(history);
                countdown.countDown();
            }
        });

        // send
        send(reqId, limits);

        // wait internal listener
        try {
            countdown.await(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) {
        }

        return result;
    }

    @Override
    public void getStatusHistory(HistoryLimits limits, StatusHistoryListener listener) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        // register listener
        int reqId = registerListener(listener);
        // send
        send(reqId, limits);
    }

    private int registerListener(StatusHistoryListener listener) {
        listeners.put(reqCount, listener);
        return reqCount++;
    }

    private void send(int reqId, HistoryLimits limits) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        try {
            sendToObjectCloudly(JOSPProtocol_ServiceToObject.HISTORY_STATUS_REQ_MIN_PERM, JOSPProtocol_ServiceToObject.createHistoryCompStatusMsg(getServiceInfo().getFullId(), getRemote().getId(), getComponent().getPath().getString(), Integer.toString(reqId), limits));

        } catch (JSLRemoteObject.MissingPermission | PeerNotConnectedException | PeerStreamException ignore) {
            sendToObjectLocally(JOSPProtocol_ServiceToObject.HISTORY_STATUS_REQ_MIN_PERM, JOSPProtocol_ServiceToObject.createHistoryCompStatusMsg(getServiceInfo().getFullId(), getRemote().getId(), getComponent().getPath().getString(), Integer.toString(reqId), limits));
        }
    }


    // Processing

    public boolean processHistoryCompStatusMsg(String msg) {
        // Received StatusHistory message
        System.out.println(String.format("Received StatusHistory Message for %s component on %s object", getComponent().getName(), getRemote().getName()));

        String objId;
        String fullSrvId;
        String compPathStr;
        String reqId;
        List<JOSPStatusHistory> statusesHistory;
        try {
            objId = JOSPProtocol_ObjectToService.getHistoryCompStatusMsg_ObjId(msg);
            compPathStr = JOSPProtocol_ObjectToService.getHistoryCompStatusMsg_CompPath(msg);
            reqId = JOSPProtocol_ObjectToService.getHistoryCompStatusMsg_ReqId(msg);
            statusesHistory = JOSPProtocol_ObjectToService.getHistoryCompStatusMsg_HistoryStatus(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(String.format("Error on processing message %s because %s", JOSPProtocol_ServiceToObject.HISTORY_STATUS_REQ_NAME, e.getMessage()), e);
            return false;
        }

        StatusHistoryListener l = listeners.get(Integer.parseInt(reqId));
        if (l == null) {
            log.warn(String.format("Error on processing message %s because no listener expecting '%s' request", JOSPProtocol_ServiceToObject.HISTORY_STATUS_REQ_NAME, reqId));
            return false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                l.receivedStatusHistory(statusesHistory);
            }
        }).start();

        return true;
    }

}
