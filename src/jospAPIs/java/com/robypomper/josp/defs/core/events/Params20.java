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

package com.robypomper.josp.defs.core.events;

import com.robypomper.josp.types.josp.AgentType;
import com.robypomper.josp.types.josp.EventType;

import java.util.Date;


/**
 * JOSP Core - Events 2.0
 */
public class Params20 {

    // Events

    public static class Event {

        public long id;
        public EventType type;
        public String srcId;
        public AgentType srcType;
        public Date emittedAt;
        public String phase;
        public String payload;
        public String errorPayload;

    }

}
