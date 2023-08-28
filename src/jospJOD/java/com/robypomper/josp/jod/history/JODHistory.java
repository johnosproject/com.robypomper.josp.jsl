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

package com.robypomper.josp.jod.history;

import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.io.IOException;
import java.util.List;

public interface JODHistory {


    // Register new status

    void register(JODComponent comp, JODStateUpdate update);


    // Get statuses

    List<JOSPStatusHistory> getHistoryStatus(JODComponent comp, HistoryLimits limits);


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
