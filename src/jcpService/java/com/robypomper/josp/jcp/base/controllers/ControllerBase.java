/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.base.controllers;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.consts.JOSPConstants;
import com.robypomper.josp.jcp.consts.JCPConstants;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ControllerBase {

    // Params checks

    public static void checkObjId(Logger log, String objId) throws ResponseStatusException {
        checkHeaderParam(log, JOSPConstants.API_HEADER_OBJ_ID, objId);
    }

    public static void checkSrvId(Logger log, String srvId) throws ResponseStatusException {
        checkHeaderParam(log, JOSPConstants.API_HEADER_OBJ_ID, srvId);
    }

    public static void checkGwId(Logger log, String gwId) throws ResponseStatusException {
        checkHeaderParam(log, JCPConstants.API_HEADER_GW_ID, gwId);
    }

    public static void checkHeaderParam(Logger log, String headerName, String value) throws ResponseStatusException {
        if (value == null || value.isEmpty()) {
            log.trace(String.format("Error executing request because header '%s' not set", headerName));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", headerName));
        }
    }

    // Exceptions methods

    protected static ResponseStatusException genericException(String requestName, Throwable cause) {
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Exception executing request '%s' caused by '%s'", requestName, cause.toString()), cause);
    }

    protected static ResponseStatusException resourceNotFound(String resourceType, String resourceId) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Error resource %s with id '%s' not found", resourceType, resourceId));
    }

    protected static ResponseStatusException jcpServiceNotAvailable(JCPClient2 client, Throwable cause) {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, String.format("Service %s unavailable (%s)", client.getApiName(), cause.toString()), cause);
    }

}
