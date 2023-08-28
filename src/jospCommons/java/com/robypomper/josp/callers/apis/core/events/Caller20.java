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

package com.robypomper.josp.callers.apis.core.events;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPIObj;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.defs.core.events.Params20;
import com.robypomper.josp.defs.core.events.Paths20;

import java.util.Collections;
import java.util.List;


/**
 * JOSP Core - Events 2.0
 */
public class Caller20 extends AbsAPIObj {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public Caller20(JCPAPIsClientObj jcpClient) {
        super(jcpClient);
    }


    // Object methods

    /**
     * Upload a single event to the cloud.
     *
     * @param event the event to upload.
     */
    public void uploadEvent(Params20.Event event) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        uploadEvents(Collections.singletonList(event));
    }


    // Service methods

    /**
     * Upload a list of events to the cloud.
     *
     * @param events the events to upload.
     */
    public void uploadEvents(List<Params20.Event> events) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        jcpClient.execReq(Verb.POST, Paths20.FULL_PATH_OBJECT, events, isSecure());
    }

}
