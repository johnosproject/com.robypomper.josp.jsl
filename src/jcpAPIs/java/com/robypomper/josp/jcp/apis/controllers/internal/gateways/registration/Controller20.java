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

package com.robypomper.josp.jcp.apis.controllers.internal.gateways.registration;

import com.robypomper.josp.jcp.apis.mngs.GWsManager;
import com.robypomper.josp.jcp.apis.mngs.exceptions.GWNotFoundException;
import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.consts.JCPConstants;
import com.robypomper.josp.jcp.defs.apis.internal.gateways.registration.Params20;
import com.robypomper.josp.jcp.defs.apis.internal.gateways.registration.Paths20;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;


/**
 * JCP APIs - Gateways / Registration 2.0
 */
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImpl {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private GWsManager gwManager;


    // Registration methods

    /**
     * Allow JCP GW to register their startup and become available for objects
     * and services connections.
     *
     * @return true on GW registration success.
     */
    @PostMapping(path = Paths20.FULL_PATH_STARTUP, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STARTUP,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "True on GW registration success.", response = Boolean.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 404, message = "User with specified id not found"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
            @ApiResponse(code = 503, message = "Authorization server not available"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Boolean> postRegisterGatewayReq(@RequestHeader(JCPConstants.API_HEADER_GW_ID) String gwId,
                                                          @RequestBody Params20.JCPGWsStartup gwStartup) {
        checkGwId(log, gwId);

        gwManager.register(gwId, gwStartup);

        return ResponseEntity.ok(true);
    }

    /**
     * Allow JCP GW to update their status when clients connects or disconnects.
     *
     * @return true on GW status successfully updated.
     */
    @PostMapping(path = Paths20.FULL_PATH_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "True on GW status successfully updated.", response = Boolean.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 404, message = "User with specified id not found"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
            @ApiResponse(code = 503, message = "Authorization server not available"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Boolean> postUpdateGatewayReq(@RequestHeader(JCPConstants.API_HEADER_GW_ID) String gwId,
                                                        @RequestBody Params20.JCPGWsStatus gwStatus) {
        checkGwId(log, gwId);

        try {
            gwManager.update(gwId, gwStatus);
            return ResponseEntity.ok(true);

        } catch (GWNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Can't find JCP GW with id '%s'", gwId), e);

        }
    }

    /**
     * Allow JCP GW to register their startup and become available for objects
     * and services connections.
     *
     * @return true on GW registration success.
     */
    @PostMapping(path = Paths20.FULL_PATH_SHUTDOWN, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_SHUTDOWN,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "True on GW registration success.", response = Boolean.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 404, message = "User with specified id not found"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
            @ApiResponse(code = 503, message = "Authorization server not available"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Boolean> postDeregisterGatewayReq(@RequestHeader(JCPConstants.API_HEADER_GW_ID) String gwId) {
        checkGwId(log, gwId);

        try {
            gwManager.deregister(gwId);
            return ResponseEntity.ok(true);

        } catch (GWNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Can't find JCP GW with id '%s'", gwId), e);

        }
    }

}
