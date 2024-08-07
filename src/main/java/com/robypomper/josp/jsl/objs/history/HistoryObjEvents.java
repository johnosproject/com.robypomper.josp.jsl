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

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;

import java.util.List;

public interface HistoryObjEvents {

    JSLRemoteObject getObject();

    List<JOSPEvent> getEventsHistory(HistoryLimits limits, long timeout) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

    void getEventsHistory(HistoryLimits limits, EventsListener listener) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

    interface EventsListener {

        void receivedEvents(List<JOSPEvent> history);

    }

}
