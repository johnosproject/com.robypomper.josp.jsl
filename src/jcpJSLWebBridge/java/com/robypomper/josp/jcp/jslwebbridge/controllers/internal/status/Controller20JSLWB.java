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

package com.robypomper.josp.jcp.jslwebbridge.controllers.internal.status;

import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.defs.jslwebbridge.internal.status.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.internal.status.Paths20;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.types.RESTItemList;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;


/**
 * JCP JSL Web Bridge - Status 2.0
 */
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME + "2")
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20JSLWB extends ControllerImpl {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20JSLWB.class);
    @Autowired
    private JSLWebBridgeService jslWB;


    // Sessions methods

    @GetMapping(path = Paths20.FULL_PATH_STATUS_SESSIONS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_SESSIONS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web bridge's web sessions list", response = Params20.Sessions.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Sessions> getSessionsReq() {
        Params20.Sessions sessions = new Params20.Sessions();

        sessions.sessionsList = new ArrayList<>();
        for (HttpSession session : jslWB.getAllSessions()) {
            RESTItemList sessionItem = new RESTItemList();
            sessionItem.id = session.getId();
            sessionItem.name = session.getId().substring(0, 5) + "...";
            sessionItem.url = Paths20.FULL_PATH_STATUS_SESSION(session.getId());
            sessions.sessionsList.add(sessionItem);
        }

        return ResponseEntity.ok(sessions);
    }

    @GetMapping(path = Paths20.FULL_PATH_STATUS_SESSION, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_SESSION,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP JSL Web bridge's web session info", response = Params20.Session.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Session> getSessionReq(@PathVariable(Paths20.PARAM_SESSION) String sessionId) {
        HttpSession session = jslWB.getSession(sessionId);
        if (session == null)
            throw resourceNotFound("Web Session", sessionId);

        Params20.Session sessionRes = new Params20.Session();
        sessionRes.id = session.getId();
        sessionRes.name = session.getId().substring(0, 5) + "...";
        sessionRes.createdAt = new Date(session.getCreationTime());
        sessionRes.lastAccessedAt = new Date(session.getLastAccessedTime());
        sessionRes.maxInactiveInterval = session.getMaxInactiveInterval();

        return ResponseEntity.ok(sessionRes);
    }

}
