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

import com.robypomper.java.JavaJSONArrayToFile;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class StatusHistoryArray extends JavaJSONArrayToFile<JOSPStatusHistory, Long> {

    public StatusHistoryArray(File jsonFile) throws IOException {
        super(jsonFile, JOSPStatusHistory.class);
    }

    @Override
    protected int compareItemIds(Long id1, Long id2) {
        return id1.compareTo(id2);
    }

    @Override
    protected Long getItemId(JOSPStatusHistory value) {
        return value.getId();
    }

    @Override
    protected Date getItemDate(JOSPStatusHistory value) {
        return value.getUpdatedAt();
    }

}
