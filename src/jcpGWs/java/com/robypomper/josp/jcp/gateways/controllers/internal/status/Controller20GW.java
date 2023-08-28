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

package com.robypomper.josp.jcp.gateways.controllers.internal.status;

import com.robypomper.comm.server.ServerClient;
import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.defs.gateways.internal.status.Params20;
import com.robypomper.josp.jcp.defs.gateways.internal.status.Paths20;
import com.robypomper.josp.jcp.gws.broker.BrokerClientJOD;
import com.robypomper.josp.jcp.gws.broker.BrokerClientJSL;
import com.robypomper.josp.jcp.gws.broker.BrokerClientObjDB;
import com.robypomper.josp.jcp.gws.gw.GWAbs;
import com.robypomper.josp.jcp.gws.services.BrokerService;
import com.robypomper.josp.jcp.gws.services.GWServiceO2S;
import com.robypomper.josp.jcp.gws.services.GWServiceS2O;
import com.robypomper.josp.types.RESTItemList;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;


/**
 * JCP Gateways - Status 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME + "2")
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20GW extends ControllerImpl {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20GW.class);
    @Autowired
    private GWServiceO2S gwO2SService;
    @Autowired
    private GWServiceS2O gwS2OService;
    @Autowired
    private BrokerService brokerService;


    // GWs status methods

    @GetMapping(path = Paths20.FULL_PATH_STATUS_GWS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_GWS,
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
    public ResponseEntity<Params20.GWs> getGWsReq() {
        Params20.GWs gws = new Params20.GWs();
        gws.id = GWAbs.getSerial();
        gws.gwList = new ArrayList<>();

        RESTItemList o2sItem = new RESTItemList();
        o2sItem.id = gwO2SService.get().getId();
        o2sItem.name = gwO2SService.get().getId();
        o2sItem.url = Paths20.FULL_PATH_STATUS_GW(gwO2SService.get().getId());
        gws.gwList.add(o2sItem);

        RESTItemList s2oItem = new RESTItemList();
        s2oItem.id = gwS2OService.get().getId();
        s2oItem.name = gwS2OService.get().getId();
        s2oItem.url = Paths20.FULL_PATH_STATUS_GW(gwS2OService.get().getId());
        gws.gwList.add(s2oItem);

        return ResponseEntity.ok(gws);
    }

    @GetMapping(path = Paths20.FULL_PATH_STATUS_GW, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_GW,
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
    public ResponseEntity<Params20.GW> getGWReq(@PathVariable(Paths20.PARAM_GW) String gwId) {
        if (gwO2SService.get().getId().equals(gwId))
            return ResponseEntity.ok(generateGW(gwO2SService.get()));

        else if (gwS2OService.get().getId().equals(gwId))
            return ResponseEntity.ok(generateGW(gwS2OService.get()));

        else
            throw resourceNotFound("GW", gwId);
    }

    @GetMapping(path = Paths20.FULL_PATH_STATUS_GW_CLIENT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_GW_CLIENT,
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
    public ResponseEntity<Params20.GWClient> getGWClientReq(@PathVariable(Paths20.PARAM_GW) String gwId, @RequestParam(Paths20.PARAM_GW_CLIENT) String gwClientId) {
        if (gwO2SService.get().getId().equals(gwId))
            return ResponseEntity.ok(generateGWClient(gwO2SService.get(), gwClientId));

        else if (gwS2OService.get().getId().equals(gwId))
            return ResponseEntity.ok(generateGWClient(gwS2OService.get(), gwClientId));

        else
            throw resourceNotFound("GW", gwId);
    }

    private Params20.GW generateGW(GWAbs gw) {
        Params20.GW gwRes = new Params20.GW();
        gwRes.id = gw.getId();
        gwRes.type = gw.getType();
        gwRes.status = gw.getServer().getState().toString();
        gwRes.internalAddress = gw.getInternalAddress();
        gwRes.publicAddress = gw.getPublicAddress();
        gwRes.gwPort = gw.getGWPort();
        gwRes.apisPort = gw.getAPIsPort();
        gwRes.clientsCount = gw.getServer().getClients().size();
        gwRes.maxClientsCount = gw.getMaxClient();

        gwRes.clientsList = new ArrayList<>();
        for (ServerClient c : gw.getServer().getClients()) {
            RESTItemList cliItem = new RESTItemList();
            cliItem.id = c.getRemoteId();
            cliItem.name = c.getConnectionInfo().getLocalInfo().toString();
            cliItem.url = Paths20.FULL_PATH_STATUS_GW_CLIENT(gw.getId(), c.getRemoteId());
            gwRes.clientsList.add(cliItem);
        }

        return gwRes;
    }

    private Params20.GWClient generateGWClient(GWAbs gw, String gwClientId) {
        for (ServerClient c : gw.getServer().getClients())
            if (c.getRemoteId().equals(gwClientId)) {
                Params20.GWClient gwCliRes = new Params20.GWClient();
                gwCliRes.id = c.getRemoteId();
                gwCliRes.isConnected = c.getState().isConnected();
                gwCliRes.local = c.getConnectionInfo().getLocalInfo().toString();
                gwCliRes.remote = c.getConnectionInfo().getRemoteInfo().toString();
                gwCliRes.bytesRx = c.getConnectionInfo().getStats().getBytesRx();
                gwCliRes.bytesTx = c.getConnectionInfo().getStats().getBytesTx();
                gwCliRes.lastDataRx = c.getConnectionInfo().getStats().getLastDataRx();
                gwCliRes.lastDataTx = c.getConnectionInfo().getStats().getLastDataTx();
                gwCliRes.lastConnection = c.getConnectionInfo().getStats().getLastConnection();
                gwCliRes.lastDisconnection = c.getConnectionInfo().getStats().getLastDisconnection();
                gwCliRes.lastHeartBeat = c.getConnectionInfo().getStats().getLastHeartBeat();
                gwCliRes.lastHeartBeatFailed = c.getConnectionInfo().getStats().getLastHeartBeatFailed();
                return gwCliRes;
            }

        throw resourceNotFound("GW's client", gwClientId);
    }


    // Broker status methods

    @GetMapping(path = Paths20.FULL_PATH_STATUS_BROKER, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_BROKER,
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
    public ResponseEntity<Params20.Broker> getBrokerReq() {
        Params20.Broker broker = new Params20.Broker();

        broker.objsList = new ArrayList<>();
        for (BrokerClientJOD obj : brokerService.getBrokerJOD().getAllObjects()) {
            RESTItemList objItem = new RESTItemList();
            objItem.id = obj.getId();
            objItem.name = obj.getObjDB().getName();
            objItem.url = Paths20.FULL_PATH_STATUS_BROKER_OBJ(obj.getId());
            broker.objsList.add(objItem);
        }

        broker.srvsList = new ArrayList<>();
        for (BrokerClientJSL srv : brokerService.getBrokerJSL().getAllServices()) {
            RESTItemList srvItem = new RESTItemList();
            srvItem.id = srv.getId();
            srvItem.name = srv.getName();
            srvItem.url = Paths20.FULL_PATH_STATUS_BROKER_SRV(srv.getId());
            broker.srvsList.add(srvItem);
        }

        broker.objsDBList = new ArrayList<>();
        for (BrokerClientObjDB objDB : brokerService.getBrokerObjDB().getAllObjectsDB()) {
            RESTItemList objDBItem = new RESTItemList();
            objDBItem.id = objDB.getId();
            objDBItem.name = objDB.getName();
            objDBItem.url = Paths20.FULL_PATH_STATUS_BROKER_OBJ(objDB.getId());
            broker.objsDBList.add(objDBItem);
        }

        return ResponseEntity.ok(broker);
    }

    @GetMapping(path = Paths20.FULL_PATH_STATUS_BROKER_OBJ, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_BROKER_OBJ,
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
    public ResponseEntity<Params20.BrokerObject> getBrokerObjectReq(@PathVariable(Paths20.PARAM_OBJ) String objId) {
        BrokerClientJOD obj = brokerService.getBrokerJOD().getObject(objId);
        if (obj == null)
            throw resourceNotFound("Broker's object", objId);

        Params20.BrokerObject objRes = new Params20.BrokerObject();
        objRes.id = obj.getId();
        objRes.name = obj.getName();
        objRes.owner = obj.getOwner();
        return ResponseEntity.ok(objRes);
    }

    @GetMapping(path = Paths20.FULL_PATH_STATUS_BROKER_SRV, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_BROKER_SRV,
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
    public ResponseEntity<Params20.BrokerService> getBrokerServiceReq(@RequestParam(Paths20.PARAM_SRV) String srvId) {
        BrokerClientJSL srv = brokerService.getBrokerJSL().getService(srvId);
        if (srv == null)
            throw resourceNotFound("Broker's service", srvId);

        Params20.BrokerService srvRes = new Params20.BrokerService();
        srvRes.id = srv.getId();
        srvRes.name = srv.getName();
        srvRes.user = srv.getUsrId();
        return ResponseEntity.ok(srvRes);
    }

    @GetMapping(path = Paths20.FULL_PATH_STATUS_BROKER_OBJ_DB)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_BROKER_OBJ_DB,
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
    public ResponseEntity<Params20.BrokerObjectDB> getBrokerObjectDBReq(@PathVariable(Paths20.PARAM_OBJ) String objId) {
        BrokerClientJOD obj = brokerService.getBrokerObjDB().getObjectDB(objId);
        if (obj == null)
            throw resourceNotFound("Broker's object (DB)", objId);

        Params20.BrokerObjectDB objRes = new Params20.BrokerObjectDB();
        objRes.id = obj.getId();
        objRes.name = obj.getName();
        objRes.owner = obj.getOwner();
        return ResponseEntity.ok(objRes);
    }

}
