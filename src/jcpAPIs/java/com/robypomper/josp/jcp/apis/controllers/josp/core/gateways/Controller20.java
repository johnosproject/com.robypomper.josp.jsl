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

package com.robypomper.josp.jcp.apis.controllers.josp.core.gateways;

import com.robypomper.josp.consts.JOSPConstants;
import com.robypomper.josp.defs.core.gateways.Params20;
import com.robypomper.josp.defs.core.gateways.Paths20;
import com.robypomper.josp.jcp.apis.mngs.GWsManager;
import com.robypomper.josp.jcp.apis.mngs.exceptions.GWNotAvailableException;
import com.robypomper.josp.jcp.apis.mngs.exceptions.GWNotReachableException;
import com.robypomper.josp.jcp.apis.mngs.exceptions.GWResponseException;
import com.robypomper.josp.jcp.base.controllers.ControllerLink;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import io.swagger.annotations.*;
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
 * JOSP Core - Gateways 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerLink {

    // Internal vars

    @Autowired
    private GWsManager gwManager;


    // Access methods

    @PostMapping(path = Paths20.FULL_PATH_GWS_O2S_ACCESS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_GWS_O2S_ACCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Params20.O2SAccessInfo.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_OBJ_ID),
            @ApiResponse(code = 500, message = "Error adding client certificate"),
            @ApiResponse(code = 503, message = "Internal error, no gateways available certificate")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<Params20.O2SAccessInfo> postO2SAccess(@RequestHeader(JOSPConstants.API_HEADER_OBJ_ID) String objId,
                                                                @RequestBody Params20.O2SAccessRequest accessRequest) {
        try {
            return ResponseEntity.ok(gwManager.getAccessInfo(objId, accessRequest));

        } catch (GWNotAvailableException | GWNotReachableException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, String.format("Can't get AccessInfo for '%s' client (%s)", objId, e), e);

        } catch (GWResponseException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Forward AccessInfo request for '%s' client failed", objId), e);
        }
    }


    @PostMapping(path = Paths20.FULL_PATH_GWS_S2O_ACCESS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_GWS_S2O_ACCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Params20.S2OAccessInfo.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_SRV_ID),
            @ApiResponse(code = 500, message = "Error adding client certificate"),
            @ApiResponse(code = 503, message = "Internal error, no gateways available certificate")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<Params20.S2OAccessInfo> postS2OAccess(@RequestHeader(JOSPConstants.API_HEADER_SRV_ID) String srvId,
                                                                @RequestBody Params20.S2OAccessRequest accessRequest) {
        try {
            return ResponseEntity.ok(gwManager.getAccessInfo(srvId, accessRequest));

        } catch (GWNotAvailableException | GWNotReachableException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, String.format("Can't get AccessInfo for '%s' client (%s)", srvId, e), e);

        } catch (GWResponseException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Forward AccessInfo request for '%s' client failed", srvId), e);
        }

    }

}
