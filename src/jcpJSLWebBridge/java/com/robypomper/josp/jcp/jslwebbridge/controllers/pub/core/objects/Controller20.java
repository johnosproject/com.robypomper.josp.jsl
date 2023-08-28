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

package com.robypomper.josp.jcp.jslwebbridge.controllers.pub.core.objects;

import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.Paths20;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.controllers.ControllerImplJSL;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;
import com.robypomper.josp.protocol.JOSPPerm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


/**
 * JCP JSL Web Bridge - Objects 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImplJSL {

    // Internal var

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private JSLWebBridgeService webBridgeService;


    // Constructors

    public Controller20() {
        super(Paths20.API_NAME, Paths20.API_VER, JCPJSLWBVersions.API_NAME, Paths20.DOCS_NAME, Paths20.DOCS_DESCR);
    }


    // Methods - Objs List

    @GetMapping(path = Paths20.FULL_PATH_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_LIST)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Params20.JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<List<Params20.JOSPObjHtml>> jsonObjectsList(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId(), "list objects");

        List<Params20.JOSPObjHtml> objHtml = new ArrayList<>();
        for (JSLRemoteObject obj : jsl.getObjsMngr().getAllObjects()) {
            Params20.JOSPObjHtml jospObj = new Params20.JOSPObjHtml();
            jospObj.id = obj.getId();
            jospObj.name = obj.getName();
            jospObj.model = obj.getInfo().getModel();
            jospObj.owner = obj.getInfo().getOwnerId();
            jospObj.isConnected = obj.getComm().isConnected();
            jospObj.isCloudConnected = obj.getComm().isCloudConnected();
            jospObj.isLocalConnected = obj.getComm().isLocalConnected();
            jospObj.jodVersion = obj.getInfo().getJODVersion();
            jospObj.pathSingle = Paths20.FULL_PATH_DETAILS(obj.getId());
            jospObj.pathStruct = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.structure.Paths20.FULL_PATH_STRUCT(obj.getId());
            jospObj.pathEvents = Paths20.FULL_PATH_EVENTS(obj.getId());
            jospObj.pathPerms = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.permissions.Paths20.FULL_PATH_LIST(obj.getId());
            jospObj.pathPermsAdd = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.permissions.Paths20.FULL_PATH_ADD(obj.getId());
            jospObj.pathSetOwner = Paths20.FULL_PATH_OWNER(obj.getId());
            jospObj.pathSetName = Paths20.FULL_PATH_NAME(obj.getId());
            jospObj.permission = obj.getPerms().getServicePerm(JOSPPerm.Connection.LocalAndCloud).toString();
            objHtml.add(jospObj);
        }

        return ResponseEntity.ok(objHtml);
    }


    // Methods - Objs Details

    @GetMapping(path = Paths20.FULL_PATH_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_DETAILS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Params20.JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Params20.JOSPObjHtml> jsonObjectDetails(@ApiIgnore HttpSession session,
                                                                  @PathVariable(Paths20.PARAM_OBJ) String objId) {
        JSLRemoteObject obj = getJSLObj(session.getId(), objId, "get object details");
        Params20.JOSPObjHtml jospObj = new Params20.JOSPObjHtml();
        jospObj.id = obj.getId();
        jospObj.name = obj.getName();
        jospObj.model = obj.getInfo().getModel();
        jospObj.owner = obj.getInfo().getOwnerId();
        jospObj.isConnected = obj.getComm().isConnected();
        jospObj.isCloudConnected = obj.getComm().isCloudConnected();
        jospObj.isLocalConnected = obj.getComm().isLocalConnected();
        jospObj.jodVersion = obj.getInfo().getJODVersion();
        jospObj.pathSingle = Paths20.FULL_PATH_DETAILS(obj.getId());
        jospObj.pathStruct = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.structure.Paths20.FULL_PATH_STRUCT(obj.getId());
        jospObj.pathEvents = Paths20.FULL_PATH_EVENTS(obj.getId());
        jospObj.pathPerms = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.permissions.Paths20.FULL_PATH_LIST(obj.getId());
        jospObj.pathPermsAdd = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.permissions.Paths20.FULL_PATH_ADD(obj.getId());
        jospObj.pathSetOwner = Paths20.FULL_PATH_OWNER(obj.getId());
        jospObj.pathSetName = Paths20.FULL_PATH_NAME(obj.getId());
        jospObj.permission = obj.getPerms().getServicePerm(JOSPPerm.Connection.LocalAndCloud).toString();
        return ResponseEntity.ok(jospObj);
    }


    // Set owner and name

    @PostMapping(path = Paths20.FULL_PATH_OWNER, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_OWNER)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectOwner(@ApiIgnore HttpSession session,
                                                   @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                   @RequestParam("new_owner") String newOwner) {
        JSLRemoteObject obj = getJSLObj(session.getId(), objId, "set object owner");

        try {
            obj.getInfo().setOwnerId(newOwner);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsException(objId, "set owner id", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "set owner id", e);
        }

        return ResponseEntity.ok(true);
    }

    @PostMapping(path = Paths20.FULL_PATH_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_NAME)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectName(@ApiIgnore HttpSession session,
                                                  @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                  @RequestParam("new_name") String newName) {
        JSLRemoteObject obj = getJSLObj(session.getId(), objId, "set object name");

        try {
            obj.getInfo().setName(newName);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsException(objId, "set name", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "set name", e);
        }

        return ResponseEntity.ok(true);
    }


    // Events

    @GetMapping(path = Paths20.FULL_PATH_EVENTS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EVENTS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPEvent.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<List<JOSPEvent>> jsonObjectEvents(@ApiIgnore HttpSession session,
                                                            @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                            HistoryLimits limits) {
        JSLRemoteObject obj = getJSLObj(session.getId(), objId, "list object events");

        try {
            return ResponseEntity.ok(obj.getInfo().getEventsHistory(limits, 20));

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsException(objId, "get events", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "get events", e);
        }
    }

}
