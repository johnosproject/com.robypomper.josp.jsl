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

package com.robypomper.josp.jcp.apis.controllers.josp.core.services;

import com.robypomper.josp.defs.core.services.Paths20;
import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.external.resources.auth.AuthDefault;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;


/**
 * JOSP Core - Services 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImpl {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private AuthDefault authDefault;
    @Autowired
    private ServiceDBService serviceService;
    @Autowired
    private HttpSession httpSession;


    //// Register methods
    //
    ///**
    // * Return current {@link Service} instance.
    // * <p>
    // * This method afterwards check if the service is registered in the JCP db. If
    // * not, then it query the auth's server and register current service in the JCP
    // * db.
    // *
    // * @return current service representation.
    // */
    //@PostMapping(path = Paths20.FULL_PATH_REGISTER, produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation(value = Paths20.DESCR_PATH_REGISTER,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_SRV_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Service's id and name", response = Params20.SrvName.class),
    //        @ApiResponse(code = 401, message = "Client not authenticated"),
    //        @ApiResponse(code = 404, message = "Service with specified id not found"),
    //        @ApiResponse(code = 500, message = "Authorization not setup"),
    //        @ApiResponse(code = 503, message = "Authorization server not available"),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    //public ResponseEntity<Params20.SrvName> get() {
    //    try {
    //        String srvId = SecurityUser.getUserClientId();
    //        Service service = getOrRegisterService(srvId);
    //        Params20.SrvName srvName = new Params20.SrvName();
    //        srvName.srvId = service.getSrvId();
    //        srvName.srvName = service.getSrvName();
    //        return ResponseEntity.ok(srvName);
    //
    //    } catch (SecurityUser.UserNotAuthenticated e) {
    //        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Client not authenticated.", e);
    //    } catch (SecurityUser.AuthNotFoundException e) {
    //        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authorization not setup.", e);
    //    } catch (JCPClient2.ConnectionException e) {
    //        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't connect to remote service.", e);
    //    } catch (JCPClient2.RequestException e) {
    //        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error on executing remote request.", e);
    //    }
    //}


    //// Utils
    //
    ///**
    // * Looks for <code>userId</code> on JCP db, if can't find it then create a
    // * new user querying the auth server, store it on JCP db and finally return
    // * current user representation.
    // *
    // * @param srvId the <code>usrId</code> to search in the JCP db or to register.
    // * @return the {@link Service} object stored on the JCP db.
    // */
    //private Service getOrRegisterService(String srvId) throws JCPClient2.ConnectionException, JCPClient2.RequestException {
    //    Optional<Service> optService = serviceService.find(srvId);
    //
    //    if (optService.isPresent())
    //        return optService.get();
    //
    //    Service newService = authDefault.queryService(srvId);
    //    newService = serviceService.save(newService);
    //    return newService;
    //}

}
