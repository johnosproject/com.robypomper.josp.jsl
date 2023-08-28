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

package com.robypomper.josp.callers.apis.core.objects;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPIObj;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.defs.core.objects.Params20;
import com.robypomper.josp.defs.core.objects.Paths20;

import java.util.Collections;
import java.util.List;


/**
 * JOSP Core - Objects 2.0
 */
@SuppressWarnings("unused")
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


    // Generator methods

    /**
     * Generate and return a valid Object's Cloud ID.
     *
     * @param objIdHw the object's Hardware ID.
     * @param ownerId the owner's User ID.
     * @return the object's Cloud ID.
     */
    public String getIdGenerated(String objIdHw, String ownerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        Params20.GenerateObjId params = new Params20.GenerateObjId();
        params.objIdHw = objIdHw;
        params.ownerId = ownerId;

        return jcpClient.execReq(Verb.POST, Paths20.FULL_PATH_ID_GENERATE, String.class, params, isSecure());
    }

    public String getIdRegenerated(String objIdHw, String ownerId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        Params20.GenerateObjId params = new Params20.GenerateObjId();
        params.objIdHw = objIdHw;
        params.ownerId = ownerId;

        return jcpClient.execReq(Verb.POST, Paths20.FULL_PATH_ID_REGENERATE, String.class, params, isSecure());
    }


    // History methods

    /**
     * Upload a single status to the cloud.
     *
     * @param statusHistory the status to upload.
     */
    public void postHistory(Params20.HistoryStatus statusHistory) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        postHistory(Collections.singletonList(statusHistory));
    }

    /**
     * Upload a list of statuses to the cloud.
     *
     * @param statusesHistory the statuses to upload.
     */
    public void postHistory(List<Params20.HistoryStatus> statusesHistory) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        jcpClient.execReq(Verb.POST, Paths20.FULL_PATH_HISTORY, statusesHistory, isSecure());
    }

}
