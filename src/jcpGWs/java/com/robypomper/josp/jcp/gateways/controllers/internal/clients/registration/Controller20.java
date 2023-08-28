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

package com.robypomper.josp.jcp.gateways.controllers.internal.clients.registration;

import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.java.JavaJKS;
import com.robypomper.josp.consts.JOSPConstants;
import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.defs.gateways.internal.clients.registration.Params20;
import com.robypomper.josp.jcp.defs.gateways.internal.clients.registration.Paths20;
import com.robypomper.josp.jcp.gws.gw.GWAbs;
import com.robypomper.josp.jcp.gws.services.GWServiceO2S;
import com.robypomper.josp.jcp.gws.services.GWServiceS2O;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;


/**
 * JCP Gateways - Clients / Registration 2.0
 */
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
//@Profile("jcp-gateways")
public class Controller20 extends ControllerImpl {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private GWServiceO2S gwO2SService;
    @Autowired
    private GWServiceS2O gwS2OService;


    // Methods

    @PostMapping(path = Paths20.FULL_PATH_GW_O2S_ACCESS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_GW_O2S_ACCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Params20.O2SAccessInfo.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_OBJ_ID),
            @ApiResponse(code = 500, message = "Error adding client certificate")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.O2SAccessInfo> postO2SAccess(@RequestHeader(JOSPConstants.API_HEADER_OBJ_ID) String objId,
                                                                @RequestBody Params20.O2SAccessRequest accessRequest) {

        checkObjId(log, objId);

        Certificate clientCertificate = generateCertificate(Paths20.FULL_PATH_GW_O2S_ACCESS, accessRequest.clientCertificate);
        String certId = String.format("%s/%s", objId, accessRequest.instanceId);
        registerCertificate(Paths20.FULL_PATH_GW_O2S_ACCESS, gwO2SService.get(), certId, clientCertificate);
        log.trace(String.format("Registered certificate for Object '%s'", objId));

        return ResponseEntity.ok((Params20.O2SAccessInfo) getAccessInfo(gwO2SService.get()));
    }


    @PostMapping(path = Paths20.FULL_PATH_GW_S2O_ACCESS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_GW_S2O_ACCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Params20.S2OAccessInfo.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_SRV_ID),
            @ApiResponse(code = 500, message = "Error adding client certificate")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.S2OAccessInfo> postS2OAccess(@RequestHeader(JOSPConstants.API_HEADER_SRV_ID) String srvId,
                                                                @RequestBody Params20.S2OAccessRequest accessRequest) {

        checkSrvId(log, srvId);

        Certificate clientCertificate = generateCertificate(Paths20.FULL_PATH_GW_S2O_ACCESS, accessRequest.clientCertificate);
        String certId = String.format("%s/%s", srvId, accessRequest.instanceId);
        registerCertificate(Paths20.FULL_PATH_GW_S2O_ACCESS, gwS2OService.get(), certId, clientCertificate);
        log.trace(String.format("Registered certificate for Service '%s'", srvId));

        return ResponseEntity.ok((Params20.S2OAccessInfo) getAccessInfo(gwS2OService.get()));
    }


    // Utils

    private static Certificate generateCertificate(String requestName, byte[] certificateBytes) {
        try {
            return JavaJKS.loadCertificateFromBytes(certificateBytes);

        } catch (JavaJKS.LoadingException e) {
            throw genericException(requestName, e);
        }
    }

    private static void registerCertificate(String requestName, GWAbs gwService, String certId, Certificate certificate) {
        try {
            gwService.addClientCertificate(certId, certificate);

        } catch (AbsCustomTrustManager.UpdateException e) {
            throw genericException(requestName, e);
        }
    }

    private static Params20.AccessInfo getAccessInfo(GWAbs gwService) {
        try {
            return gwService.getAccessInfo();

        } catch (CertificateEncodingException e) {
            log.trace(String.format("Error retrieve gw server '%s' certificate", gwService.getId()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error getting GW server '%s' certificate.", gwService.getId()));
        }
    }

}
