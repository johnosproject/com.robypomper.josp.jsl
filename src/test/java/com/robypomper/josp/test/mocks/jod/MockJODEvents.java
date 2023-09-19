/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
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

package com.robypomper.josp.test.mocks.jod;

import com.robypomper.java.JavaJSONArrayToFile;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.jod.events.JODEvents;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;
import com.robypomper.josp.types.josp.EventType;

import java.util.List;

public class MockJODEvents implements JODEvents {

    @Override
    public void setJCPClient(JCPAPIsClientObj jcpClient) {
    }

    @Override
    public void register(EventType type, String phase, String payload) {
    }

    @Override
    public void register(EventType type, String phase, String payload, Throwable error) {
    }

    @Override
    public List<JOSPEvent> getHistoryEvents(HistoryLimits limits) {
        return null;
    }

    @Override
    public List<JOSPEvent> filterHistoryEvents(HistoryLimits limits, JavaJSONArrayToFile.Filter<JOSPEvent> filter) {
        return null;
    }

    @Override
    public void startCloudSync() {
    }

    @Override
    public void stopCloudSync() {
    }

    @Override
    public void storeCache() {
    }

}
