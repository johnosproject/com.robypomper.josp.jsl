/*******************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jod.events;


import com.robypomper.java.JavaJSONArrayToFile;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;
import com.robypomper.josp.types.josp.EventType;

import java.io.IOException;
import java.util.List;

/**
 * Interface for Object's events.
 * <p>
 * Events are a collection of structured data that describe what happen on an
 * object. Each object's system register an event when something occurs (see
 * {@link EventType} class).
 * <p>
 * Events are only request by services. No events updates are send to services.
 * <p>
 * This system provide the event registration methid used to all other systems
 * to register events. It also listen the JCPClient connection to sync the
 * events to the Cloud (via APIs) each time a new event is registered. When the
 * Cloud connection is down, it preserve the events on local file.<br>
 * The same file used to provide object's events when requested by a service
 * locally. In fact, when a service require the object's events, first it send
 * the request to the cloud. Only if the cloud is not available, then the service
 * send the events request directly to the object.
 */
public interface JODEvents {

    // Object's systems

    void setJCPClient(JCPAPIsClientObj jcpClient);


    // Register new event

    void register(EventType type, String phase, String payload);

    void register(EventType type, String phase, String payload, Throwable error);


    // Status History

    List<JOSPEvent> getHistoryEvents(HistoryLimits limits);

    List<JOSPEvent> filterHistoryEvents(HistoryLimits limits, JavaJSONArrayToFile.Filter<JOSPEvent> filter);


    // Mngm methods

    /**
     * Start syncing events to the cloud.
     * <p>
     * When started, Events system uploads all buffered events to the cloud,
     * then each time a new event is registered it's also immediately sync to
     * the cloud.
     * <p>
     * Until it's stopped.
     * <p>
     * If the cloud is not available, then Events system register to the
     * {@link com.robypomper.josp.clients.JCPClient2} connection
     * listener. When the connection become available, it uploads all buffered
     * events to the cloud.
     */
    void startCloudSync();

    /**
     * Stop syncing events to the cloud.
     * <p>
     * When stopped, the Events system stop to sync registered events to the
     * cloud. It store latest sync event id for next {@link #startCloudSync()}
     * call.
     */
    void stopCloudSync();

    /**
     * Store all events on file and empty the buffer.
     */
    void storeCache() throws IOException;

}
