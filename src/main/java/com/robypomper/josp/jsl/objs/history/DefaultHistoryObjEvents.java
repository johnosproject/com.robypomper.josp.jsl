/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2024 Roberto Pompermaier
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

public class DefaultHistoryObjEvents extends HistoryBase implements HistoryObjEvents {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(DefaultHistoryObjEvents.class);
    private final JSLRemoteObject obj;
    private final Map<Integer, EventsListener> listeners = new HashMap<>();
    private int reqCount = 0;


    // Constructor

    public DefaultHistoryObjEvents(JSLRemoteObject obj, JSLServiceInfo srvInfo) {
        super(obj, srvInfo);
        this.obj = obj;
    }


    // Getters

    @Override
    public JSLRemoteObject getObject() {
        return obj;
    }

    @Override
    public List<JOSPEvent> getEventsHistory(HistoryLimits limits, long timeout) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        final List<JOSPEvent> result = new ArrayList<>();
        final CountDownLatch countdown = new CountDownLatch(1);
        // register internal listener
        int reqId = registerListener(new EventsListener() {
            @Override
            public void receivedEvents(List<JOSPEvent> history) {
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
        if (countdown.getCount() != 0)
            return null;

        return result;
    }

    @Override
    public void getEventsHistory(HistoryLimits limits, EventsListener listener) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        // register listener
        int reqId = registerListener(listener);
        // send
        send(reqId, limits);
    }

    private int registerListener(EventsListener listener) {
        listeners.put(reqCount, listener);
        return reqCount++;
    }

    private void send(int reqId, HistoryLimits limits) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        try {
            sendToObjectCloudly(JOSPPerm.Type.CoOwner, JOSPProtocol_ServiceToObject.createEventsReqMsg(getServiceInfo().getFullId(), getRemote().getId(), Integer.toString(reqId), limits));

        } catch (JSLRemoteObject.MissingPermission | PeerNotConnectedException | PeerStreamException ignore) {
            sendToObjectLocally(JOSPPerm.Type.CoOwner, JOSPProtocol_ServiceToObject.createEventsReqMsg(getServiceInfo().getFullId(), getRemote().getId(), Integer.toString(reqId), limits));
        }
    }


    // Processing

    public boolean processHistoryEventsMsg(String msg) {
        String reqId;
        List<JOSPEvent> eventsHistory;
        try {
            reqId = JOSPProtocol_ObjectToService.getEventsResMsg_ReqId(msg);
            eventsHistory = JOSPProtocol_ObjectToService.getEventsResMsg_HistoryMessage(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(String.format("Error on processing message %s because %s", JOSPProtocol_ServiceToObject.EVENTS_MSG_REQ_NAME, e.getMessage()), e);
            return false;
        }

        EventsListener l = listeners.get(Integer.parseInt(reqId));
        if (l == null) {
            log.warn(String.format("Error on processing message %s because no listener expecting '%s' request", JOSPProtocol_ServiceToObject.EVENTS_MSG_REQ_NAME, reqId));
            return false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                l.receivedEvents(eventsHistory);
            }
        }).start();

        return true;
    }

}
