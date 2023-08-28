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

package com.robypomper.josp.jcp.jslwebbridge.controllers.pub.admin.gateways.status;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.gateways.status.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.gateways.status.Paths20;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.controllers.ControllerLinkJSL;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.admin.JSLAdmin;
import com.robypomper.josp.types.RESTItemList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


/**
 * JCP JSL Web Bridge - Admin / Gateways / Status 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerLinkJSL {


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private ClientParams params;


    // Constructors

    public Controller20() {
        super(Paths20.API_NAME, Paths20.API_VER, JCPJSLWBVersions.API_NAME, Paths20.DOCS_NAME, Paths20.DOCS_DESCR);
    }


    // JCP Gateways Status methods

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_LIST)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.GatewaysServers.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.GatewaysServers> getJCPGatewaysStatusGatewaysServersReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        Params20.GatewaysServers result;
        try {
            result = jsl.getAdmin().getJCPGatewaysServers();

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }

        List<RESTItemList> jslwbServerList = new ArrayList<>();
        for (RESTItemList item : result.serverList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            newItem.url = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS(item.id);
            jslwbServerList.add(newItem);
        }
        result.serverList = jslwbServerList;

        return ResponseEntity.ok(result);
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Index> getJCPAPIsStatusReq(
            @PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId) {
        return ResponseEntity.ok(new Params20.Index(gwServerId));
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GWS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GWS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.GWs.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.GWs> getJCPGatewaysStatusGatewaysReq(
            @ApiIgnore HttpSession session,
            @PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId) {
        JSL jsl = getJSL(session.getId());
        Params20.GWs result;
        try {
            result = jsl.getAdmin().getJCPGatewaysGWs(gwServerId);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }

        List<RESTItemList> jslwbGatewaysList = new ArrayList<>();
        for (RESTItemList item : result.gwList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            newItem.url = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW(gwServerId, item.id);
            jslwbGatewaysList.add(newItem);
        }
        result.gwList = jslwbGatewaysList;

        return ResponseEntity.ok(result);
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.GW.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.GW> getJCPGatewaysStatusGatewayReq(
            @ApiIgnore HttpSession session,
            @PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
            @PathVariable(Paths20.PARAM_GW) String gwId) {
        JSL jsl = getJSL(session.getId());
        Params20.GW result;
        try {
            result = jsl.getAdmin().getJCPGatewaysGW(gwServerId, gwId);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }

        List<RESTItemList> clientsList = new ArrayList<>();
        for (RESTItemList item : result.clientsList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            newItem.url = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW_CLIENT(gwServerId, gwId, item.id);
            clientsList.add(newItem);
        }
        result.clientsList = clientsList;

        return ResponseEntity.ok(result);
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW_CLIENT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_GW_CLIENT)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.GWClient.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.GWClient> getJCPGatewaysStatusGatewayClientReq(
            @ApiIgnore HttpSession session,
            @PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
            @PathVariable(Paths20.PARAM_GW) String gwId,
            @RequestParam(Paths20.PARAM_GW_CLIENT) String gwClientId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGatewaysGWsClient(gwServerId, gwId, gwClientId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Broker.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Broker> getJCPGatewaysStatusBrokerReq(
            @ApiIgnore HttpSession session,
            @PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId) {
        JSL jsl = getJSL(session.getId());
        Params20.Broker result;
        try {
            result = jsl.getAdmin().getJCPGatewaysBroker(gwServerId);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }


        List<RESTItemList> objsList = new ArrayList<>();
        for (RESTItemList item : result.objsList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            newItem.url = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ(gwServerId, item.id);
            objsList.add(newItem);
        }
        result.objsList = objsList;

        List<RESTItemList> srvsList = new ArrayList<>();
        for (RESTItemList item : result.srvsList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            newItem.url = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_SRV(gwServerId, item.id);
            srvsList.add(newItem);
        }
        result.srvsList = srvsList;

        List<RESTItemList> objsDBList = new ArrayList<>();
        for (RESTItemList item : result.objsDBList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            newItem.url = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ_DB(gwServerId, item.id);
            objsDBList.add(newItem);
        }
        result.objsDBList = objsDBList;

        return ResponseEntity.ok(result);
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.BrokerObject.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.BrokerObject> getJCPGatewaysStatusBrokerObjectReq(
            @ApiIgnore HttpSession session,
            @PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
            @PathVariable(Paths20.PARAM_OBJ) String objId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGatewaysBrokerObject(gwServerId, objId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_SRV, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_SRV)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.BrokerService.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.BrokerService> getJCPGatewaysStatusBrokerServiceReq(
            @ApiIgnore HttpSession session,
            @PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
            @RequestParam(Paths20.PARAM_SRV) String srvId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGatewaysBrokerService(gwServerId, srvId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ_DB, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_GATEWAYS_STATUS_BROKER_OBJ_DB)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.BrokerObjectDB.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.BrokerObjectDB> getJCPGatewaysStatusBrokerObjectDBReq(
            @ApiIgnore HttpSession session,
            @PathVariable(Paths20.PARAM_GW_SERVER) String gwServerId,
            @PathVariable(Paths20.PARAM_OBJ) String objId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPGatewaysBrokerObjectDB(gwServerId, objId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

}
