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

package com.robypomper.josp.jcp.apis.controllers.internal.status;

import com.robypomper.josp.jcp.apis.mngs.GWsManager;
import com.robypomper.josp.jcp.apis.mngs.exceptions.GWNotFoundException;
import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.db.apis.UserDBService;
import com.robypomper.josp.jcp.db.apis.entities.GW;
import com.robypomper.josp.jcp.db.apis.entities.Object;
import com.robypomper.josp.jcp.db.apis.entities.Service;
import com.robypomper.josp.jcp.db.apis.entities.User;
import com.robypomper.josp.jcp.defs.apis.internal.status.Params20;
import com.robypomper.josp.jcp.defs.apis.internal.status.Paths20;
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
import java.util.ArrayList;
import java.util.Optional;


/**
 * JCP APIs - Status 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImpl {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private ObjectDBService objDB;
    @Autowired
    private ServiceDBService srvDB;
    @Autowired
    private UserDBService usrDB;
    @Autowired
    private GWsManager gwsManager;


    // Index methods

    @GetMapping(path = Paths20.FULL_PATH_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.FULL_PATH_STATUS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's status index", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<Params20.Index> getIndex() {
        return ResponseEntity.ok(new Params20.Index());
    }


    // Objects methods

    @GetMapping(path = Paths20.FULL_PATH_STATUS_OBJS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_OBJS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's objects list", response = Params20.Objects.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Objects> getObjectsReq() {
        Params20.Objects objects = new Params20.Objects();

        objects.count = objDB.count();
        objects.onlineCount = objDB.countOnline();
        objects.offlineCount = objDB.countOffline();
        objects.activeCount = objDB.countActive();
        objects.inactiveCount = objDB.countInactive();
        objects.ownersCount = objDB.countOwners();
        objects.objectsList = new ArrayList<>();
        for (Object obj : objDB.findAll()) {
            RESTItemList objItem = new RESTItemList();
            objItem.id = obj.getObjId();
            objItem.name = obj.getName();
            objItem.url = Paths20.FULL_PATH_STATUS_OBJ(obj.getObjId());
            objects.objectsList.add(objItem);
        }

        return ResponseEntity.ok(objects);
    }

    @GetMapping(path = Paths20.FULL_PATH_STATUS_OBJ, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_OBJ,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's object info", response = Params20.Object.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Object> getObjectReq(
            @PathVariable(Paths20.PARAM_OBJ) String objId) {
        Optional<Object> optObj = objDB.find(objId);
        if (!optObj.isPresent())
            throw resourceNotFound("Object", objId);

        Object obj = optObj.get();
        Params20.Object objRes = new Params20.Object();
        objRes.id = obj.getObjId();
        objRes.name = obj.getName();
        objRes.owner = obj.getOwner().getOwnerId();
        objRes.online = obj.getStatus().isOnline();
        objRes.active = obj.getActive();
        objRes.version = obj.getVersion();
        objRes.createdAt = obj.getCreatedAt();
        objRes.updatedAt = obj.getUpdatedAt();

        return ResponseEntity.ok(objRes);
    }


    // Services methods

    @GetMapping(path = Paths20.FULL_PATH_STATUS_SRVS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_SRVS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's services list", response = Params20.Services.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Services> getServicesReq() {
        Params20.Services services = new Params20.Services();

        services.count = srvDB.count();
        services.onlineCount = srvDB.countOnline();
        services.offlineCount = srvDB.countOffline();
        services.instancesCount = srvDB.countInstances();
        services.instancesOnlineCount = srvDB.countInstancesOnline();
        services.instancesOfflineCount = srvDB.countInstancesOffline();
        services.servicesList = new ArrayList<>();
        for (Service srv : srvDB.findAll()) {
            RESTItemList srvItem = new RESTItemList();
            srvItem.id = srv.getSrvId();
            srvItem.name = srv.getSrvName();
            srvItem.url = Paths20.FULL_PATH_STATUS_SRV(srv.getSrvId());
            services.servicesList.add(srvItem);
        }

        return ResponseEntity.ok(services);
    }

    @GetMapping(path = Paths20.FULL_PATH_STATUS_SRV, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_SRV,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's service info", response = Params20.Service.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Service> getServiceReq(@PathVariable(Paths20.PARAM_SRV) String srvId) {
        Optional<Service> optSrv = srvDB.find(srvId);
        if (!optSrv.isPresent())
            throw resourceNotFound("Service", srvId);

        Service srv = optSrv.get();
        Params20.Service srvRes = new Params20.Service();
        srvRes.id = srv.getSrvId();
        srvRes.name = srv.getSrvName();
        srvRes.createdAt = srv.getCreatedAt();
        srvRes.updatedAt = srv.getUpdatedAt();

        return ResponseEntity.ok(srvRes);
    }


    // Users methods

    @GetMapping(path = Paths20.FULL_PATH_STATUS_USRS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_USRS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's users list", response = Params20.Users.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Users> getUsersReq() {
        Params20.Users users = new Params20.Users();

        users.count = usrDB.count();
        users.usersList = new ArrayList<>();
        for (User usr : usrDB.findAll()) {
            RESTItemList usrItem = new RESTItemList();
            usrItem.id = usr.getUsrId();
            usrItem.name = usr.getUsername();
            usrItem.url = Paths20.FULL_PATH_STATUS_USR(usr.getUsrId());
            users.usersList.add(usrItem);
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping(path = Paths20.FULL_PATH_STATUS_USR, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_USR,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's user info", response = Params20.User.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.User> getUserReq(@PathVariable(Paths20.PARAM_USR) String usrId) {
        Optional<User> optUsr = usrDB.get(usrId);
        if (!optUsr.isPresent())
            throw resourceNotFound("User", usrId);

        User usr = optUsr.get();
        Params20.User usrRes = new Params20.User();
        usrRes.id = usr.getUsrId();
        usrRes.name = usr.getUsername();
        usrRes.first_name = usr.getProfile().getName();
        usrRes.second_name = usr.getProfile().getSurname();
        usrRes.email = usr.getProfile().getEmail();
        usrRes.createdAt = usr.getCreatedAt();
        usrRes.updatedAt = usr.getUpdatedAt();

        return ResponseEntity.ok(usrRes);
    }


    // Gateways methods

    @GetMapping(path = Paths20.FULL_PATH_STATUS_GWS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_GWS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's Gateways list", response = Params20.Gateways.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Gateways> getGatewaysReq() {
        Params20.Gateways gws = new Params20.Gateways();

        gws.count = gwsManager.getGWsCount();
        gws.removed = gwsManager.getGWsRemovedCount();
        gws.total = gwsManager.getGWsTotalCount();
        gws.gatewaysList = new ArrayList<>();
        for (GW gw : gwsManager.getAllGWs()) {
            RESTItemList usrItem = new RESTItemList();
            usrItem.id = gw.getGwId();
            usrItem.name = gw.getGwId();
            usrItem.url = Paths20.FULL_PATH_STATUS_GW(gw.getGwId());
            gws.gatewaysList.add(usrItem);
        }

        return ResponseEntity.ok(gws);
    }

    @GetMapping(path = Paths20.FULL_PATH_STATUS_GW, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STATUS_GW,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's gateway info", response = Params20.Gateway.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<Params20.Gateway> getGatewayReq(@PathVariable(Paths20.PARAM_GW) String gwId) {
        GW gw = null;
        try {
            gw = gwsManager.getById(gwId);
        } catch (GWNotFoundException e) {
            throw resourceNotFound("Gateway", gwId);
        }

        Params20.Gateway gwRes = new Params20.Gateway();
        gwRes.id = gw.getGwId();
        gwRes.gwUrl = gw.getGwAddr() + ":" + gw.getGwPort();
        gwRes.apiUrl = gw.getGwAPIsAddr() + ":" + gw.getGwAPIsPort();
        gwRes.type = gw.getType();
        gwRes.version = gw.getVersion();
        gwRes.connected = gw.getStatus().isOnline();
        gwRes.reconnectionAttempts = gw.getStatus().getReconnectionAttempts();
        gwRes.createdAt = gw.getCreatedAt();
        gwRes.updatedAt = gw.getUpdatedAt();
        gwRes.currentClients = gw.getStatus().getClients();
        gwRes.maxClients = gw.getClientsMax();
        gwRes.lastClientConnected = gw.getStatus().getLastClientConnectedAt();
        gwRes.lastClientDisconnected = gw.getStatus().getLastClientDisconnectedAt();

        return ResponseEntity.ok(gwRes);
    }

}
