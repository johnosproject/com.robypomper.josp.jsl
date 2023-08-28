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

package com.robypomper.josp.jcp.apis.controllers.josp.admin.gateways.status;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.defs.admin.gateways.status.Params20;
import com.robypomper.josp.defs.admin.gateways.status.Paths20;
import com.robypomper.josp.jcp.base.controllers.ControllerLink;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.callers.gateways.status.Caller20;
import com.robypomper.josp.jcp.clients.JCPClientsMngr;
import com.robypomper.josp.jcp.clients.JCPGWsClient;
import com.robypomper.josp.types.RESTItemList;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;


/**
 * JOSP Admin - Gateways / Status 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerLink {

    // Internal vars

    @Autowired
    private JCPClientsMngr clientsMngr;


    // List methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_LIST,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Gateways's server list", response = Params20.GatewaysServers.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.GatewaysServers> getList() {
        Params20.GatewaysServers gwServers = new Params20.GatewaysServers();
        gwServers.serverList = new ArrayList<>();
        for (String serverId : clientsMngr.getGWsServerAll()) {
            RESTItemList gwServer = new RESTItemList();
            gwServer.id = serverId;
            gwServer.name = serverId;
            gwServer.url = Paths20.FULL_PATH_JCP_GWS_STATUS(serverId);
            gwServers.serverList.add(gwServer);
        }
        return ResponseEntity.ok(gwServers);
    }


    // Index methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Gateways's status index", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Index> getIndex(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId) {
        return ResponseEntity.ok(new Params20.Index(gwServerId));
    }


    // GWs status methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_GWS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_GWS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GWs list", response = Params20.GWs.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.GWs> getGWsReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        Params20.GWs result;
        try {
            result = caller.getGWsReq();

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }

        List<RESTItemList> gwsList = new ArrayList<>();
        for (RESTItemList item : result.gwList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            newItem.url = Paths20.FULL_PATH_JCP_GWS_STATUS_GW(gwServerId, item.id);
            gwsList.add(newItem);
        }
        result.gwList = gwsList;

        return ResponseEntity.ok(result);
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_GW, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_GW,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GWs list", response = Params20.GW.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.GW> getGWReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
                                                @PathVariable(Paths20.PARAM_GW) String gwId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        Params20.GW result;
        try {
            result = caller.getGWReq(gwId);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }

        List<RESTItemList> clientsList = new ArrayList<>();
        for (RESTItemList item : result.clientsList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            newItem.url = Paths20.FULL_PATH_JCP_GWS_STATUS_GW_CLIENT(gwServerId, gwId, item.id);
            clientsList.add(newItem);
        }
        result.clientsList = clientsList;

        return ResponseEntity.ok(result);
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_GW_CLIENT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_GW_CLIENT,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP GWs list", response = Params20.GWClient.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.GWClient> getGWClientReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
                                                            @PathVariable(Paths20.PARAM_GW) String gwId,
                                                            @RequestParam(Paths20.PARAM_GW_CLIENT) String gwClientId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getGWsClientReq(gwId, gwClientId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }


    // Broker status methods

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_BROKER,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP Broker's objects and services list", response = Params20.Broker.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Broker> getBrokerReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        Params20.Broker result;
        try {
            result = caller.getBrokerReq();

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }

        List<RESTItemList> objsList = new ArrayList<>();
        for (RESTItemList item : result.objsList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            newItem.url = Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ(gwServerId, item.id);
            objsList.add(newItem);
        }
        result.objsList = objsList;

        List<RESTItemList> srvsList = new ArrayList<>();
        for (RESTItemList item : result.srvsList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            newItem.url = Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_SRV(gwServerId, item.id);
            srvsList.add(newItem);
        }
        result.srvsList = srvsList;

        List<RESTItemList> objsDBList = new ArrayList<>();
        for (RESTItemList item : result.objsDBList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            newItem.url = Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ_DB(gwServerId, item.id);
            objsDBList.add(newItem);
        }
        result.objsDBList = objsDBList;

        return ResponseEntity.ok(result);
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_BROKER_OBJ,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP Broker's object info", response = Params20.BrokerObject.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.BrokerObject> getBrokerObjectReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
                                                                    @PathVariable(Paths20.PARAM_OBJ) String objId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getBrokerObjectReq(objId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_SRV)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_BROKER_SRV,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP Broker's object info", response = Params20.BrokerService.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.BrokerService> getBrokerServiceReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
                                                                      @RequestParam(Paths20.PARAM_SRV) String srvId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getBrokerServiceReq(srvId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JCP_GWS_STATUS_BROKER_OBJ_DB)
    @ApiOperation(value = Paths20.DESCR_PATH_JCP_GWS_STATUS_BROKER_OBJ_DB,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JOSP Broker's object (DB) info", response = Params20.BrokerObjectDB.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.BrokerObjectDB> getBrokerObjectDBReq(@PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
                                                                        @PathVariable(Paths20.PARAM_OBJ) String objId) {
        JCPGWsClient client = clientsMngr.getGWsClientByGWServer(gwServerId);
        Caller20 caller = new Caller20(client);
        try {
            return ResponseEntity.ok(caller.getBrokerObjectDBReq(objId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            throw jcpServiceNotAvailable(client, e);
        }
    }

}
