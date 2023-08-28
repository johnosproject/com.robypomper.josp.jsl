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

package com.robypomper.josp.jcp.jslwebbridge.controllers.pub.core.objects.permissions;

import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.permissions.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.permissions.Paths20;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.controllers.ControllerImplJSL;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
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
 * JCP JSL Web Bridge - Objects / Permissions 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImplJSL {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private JSLWebBridgeService webBridgeService;


    // Constructors

    public Controller20() {
        super(Paths20.API_NAME, Paths20.API_VER, JCPJSLWBVersions.API_NAME, Paths20.DOCS_NAME, Paths20.DOCS_DESCR);
    }


    // Methods - Obj's Perms List

    @GetMapping(path = Paths20.FULL_PATH_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_LIST)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Params20.JOSPPermHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<List<Params20.JOSPPermHtml>> jsonObjectPermissions(@ApiIgnore HttpSession session,
                                                                             @PathVariable(Paths20.PARAM_OBJ) String objId) {
        JSLRemoteObject obj = getJSLObj(session.getId(), objId, "reuire permissions list");

        // Convert permission list
        List<Params20.JOSPPermHtml> permsHtml = new ArrayList<>();
        // ToDo add MissingPermission exception on getPerms() method
        for (JOSPPerm p : obj.getPerms().getPerms()) {
            Params20.JOSPPermHtml jospPerm = new Params20.JOSPPermHtml();
            jospPerm.id = p.getId();
            jospPerm.objId = p.getObjId();
            jospPerm.srvId = p.getSrvId();
            jospPerm.usrId = p.getUsrId();
            jospPerm.type = p.getPermType().toString();
            jospPerm.connection = p.getConnType().toString();
            jospPerm.lastUpdate = p.getUpdatedAt();
            jospPerm.pathUpd = Paths20.FULL_PATH_UPD(p.getObjId(), p.getId());
            jospPerm.pathDel = Paths20.FULL_PATH_DEL(p.getObjId(), p.getId());
            jospPerm.pathDup = Paths20.FULL_PATH_DUP(p.getObjId(), p.getId());
            permsHtml.add(jospPerm);
        }

        return ResponseEntity.ok(permsHtml);
    }


    // Methods - Obj's perm add

    @PostMapping(path = Paths20.FULL_PATH_ADD, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_ADD)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectPermissionAdd(@ApiIgnore HttpSession session,
                                                           @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                           @RequestParam("srv_id") String srvId,
                                                           @RequestParam("usr_id") String usrId,
                                                           @RequestParam("type") JOSPPerm.Type type,
                                                           @RequestParam("conn") JOSPPerm.Connection connection) {
        JSLRemoteObject obj = getJSLObj(session.getId(), objId, "add permission");

        try {
            obj.getPerms().addPerm(srvId, usrId, type, connection);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsException(objId, "add permission", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "add permission", e);
        }

        return ResponseEntity.ok(true);
    }


    // Methods - Obj's perm upd

    @PostMapping(path = Paths20.FULL_PATH_UPD, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_UPD)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectPermissionUpd(@ApiIgnore HttpSession session,
                                                           @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                           @PathVariable(Paths20.PARAM_PERM) String permId,
                                                           @RequestParam(value = "srv_id", required = false) String srvId,
                                                           @RequestParam(value = "usr_id", required = false) String usrId,
                                                           @RequestParam(value = "type", required = false) JOSPPerm.Type type,
                                                           @RequestParam(value = "conn", required = false) JOSPPerm.Connection connection) {
        JSLRemoteObject obj = getJSLObj(session.getId(), objId, "update permission");
        JOSPPerm perm = getJSLObjPerm(session.getId(), objId, permId, "update permission");

        if (srvId == null)
            srvId = perm.getSrvId();
        if (usrId == null)
            usrId = perm.getUsrId();
        if (type == null)
            type = perm.getPermType();
        if (connection == null)
            connection = perm.getConnType();

        try {
            obj.getPerms().updPerm(permId, srvId, usrId, type, connection);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsException(objId, "update permission", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "update permission", e);
        }

        return ResponseEntity.ok(true);
    }

    // Methods - Obj's perm remove

    @GetMapping(path = Paths20.FULL_PATH_DEL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_DEL)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectPermissionDel(@ApiIgnore HttpSession session,
                                                           @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                           @PathVariable(Paths20.PARAM_PERM) String permId) {
        JSLRemoteObject obj = getJSLObj(session.getId(), objId, "remove permission");
        JOSPPerm perm = getJSLObjPerm(session.getId(), objId, permId, "remove permission");

        try {
            obj.getPerms().remPerm(perm.getId());

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsException(objId, "remove permission", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "remove permission", e);
        }

        return ResponseEntity.ok(true);
    }


    // Methods - Obj's perm duplicate

    @GetMapping(path = Paths20.FULL_PATH_DUP, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_DUP)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonObjectPermissionDup(@ApiIgnore HttpSession session,
                                                           @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                           @PathVariable(Paths20.PARAM_PERM) String permId) {
        JSLRemoteObject obj = getJSLObj(session.getId(), objId, "duplicate permission");
        JOSPPerm perm = getJSLObjPerm(session.getId(), objId, permId, "duplicate permission");

        try {
            obj.getPerms().addPerm(perm.getSrvId(), perm.getUsrId(), perm.getPermType(), perm.getConnType());

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsException(objId, "duplicate permission", e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedException(objId, "duplicate permission", e);
        }

        return ResponseEntity.ok(true);
    }

}
