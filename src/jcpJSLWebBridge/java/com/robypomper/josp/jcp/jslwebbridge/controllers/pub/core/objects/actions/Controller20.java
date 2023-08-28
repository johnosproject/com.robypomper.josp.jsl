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

package com.robypomper.josp.jcp.jslwebbridge.controllers.pub.core.objects.actions;

import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.controllers.ControllerImplJSL;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;


/**
 * JCP JSL Web Bridge - Objects / Actions 2.0
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


    // Methods - Boolean

    @GetMapping(path = Paths20.FULL_PATH_BOOL_SWITCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_BOOL_SWITCH)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonBoolSwitch(@ApiIgnore HttpSession session,
                                                  @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                  @PathVariable(Paths20.PARAM_COMP) String compPath) {
        JSLBooleanAction comp = getJSLObjComp(session.getId(), objId, compPath, JSLBooleanAction.class, "switch boolean component");

        try {
            comp.execSwitch();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_BOOL_TRUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_BOOL_TRUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonBoolTrue(@ApiIgnore HttpSession session,
                                                @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                @PathVariable(Paths20.PARAM_COMP) String compPath) {
        JSLBooleanAction comp = getJSLObjComp(session.getId(), objId, compPath, JSLBooleanAction.class, "set true boolean component");

        try {
            comp.execSetTrue();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_BOOL_FALSE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_BOOL_FALSE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonBoolFalse(@ApiIgnore HttpSession session,
                                                 @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                 @PathVariable(Paths20.PARAM_COMP) String compPath) {
        JSLBooleanAction comp = getJSLObjComp(session.getId(), objId, compPath, JSLBooleanAction.class, "set false boolean component");

        try {
            comp.execSetFalse();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }


    // Methods - Range

    @PostMapping(path = Paths20.FULL_PATH_RANGE_SET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_RANGE_SET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeSet_POST(@ApiIgnore HttpSession session,
                                                     @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                     @PathVariable(Paths20.PARAM_COMP) String compPath,
                                                     @RequestParam("val") String val) {
        return jsonRangeSet(session, objId, compPath, val);
    }

    @GetMapping(path = Paths20.FULL_PATH_RANGE_SETg, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_RANGE_SETg)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeSet(@ApiIgnore HttpSession session,
                                                @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                @PathVariable(Paths20.PARAM_COMP) String compPath,
                                                @PathVariable("val") String val) {
        Double dVal = JavaFormatter.strToDouble(val);
        if (dVal == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request param 'val' can't be cast to double (%s), action '%s' on '%s' object not executed.", val, compPath, objId));

        JSLRangeAction comp = getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class, "set value on range component");

        try {
            comp.execSetValue(dVal);
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_RANGE_INC, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_RANGE_INC)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeInc(@ApiIgnore HttpSession session,
                                                @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                @PathVariable(Paths20.PARAM_COMP) String compPath) {
        JSLRangeAction comp = getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class, "increase range component");

        try {
            comp.execIncrease();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_RANGE_DEC, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_RANGE_DEC)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeDec(@ApiIgnore HttpSession session,
                                                @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                @PathVariable(Paths20.PARAM_COMP) String compPath) {
        JSLRangeAction comp = getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class, "decrease range component");

        try {
            comp.execDecrease();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_RANGE_MAX, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_RANGE_MAX)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeMax(@ApiIgnore HttpSession session,
                                                @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                @PathVariable(Paths20.PARAM_COMP) String compPath) {
        JSLRangeAction comp = getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class, "set max on range component");

        try {
            comp.execSetMax();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_RANGE_MIN, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_RANGE_MIN)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeMin(@ApiIgnore HttpSession session,
                                                @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                @PathVariable(Paths20.PARAM_COMP) String compPath) {
        JSLRangeAction comp = getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class, "set min on range component");

        try {
            comp.execSetMin();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_RANGE_1_2, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_RANGE_1_2)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRange1_2(@ApiIgnore HttpSession session,
                                                @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                @PathVariable(Paths20.PARAM_COMP) String compPath) {
        JSLRangeAction comp = getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class, "set 1/2 on range component");

        try {
            double half = comp.getMin() + ((comp.getMax() - comp.getMin()) / 2);
            comp.execSetValue(half);
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_RANGE_1_3, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_RANGE_1_3)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRange1_3(@ApiIgnore HttpSession session,
                                                @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                @PathVariable(Paths20.PARAM_COMP) String compPath) {
        JSLRangeAction comp = getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class, "set 1/3 on range component");

        try {
            double fist_third = comp.getMin() + ((comp.getMax() - comp.getMin()) / 3);
            comp.execSetValue(fist_third);
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_RANGE_2_3, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_RANGE_2_3)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Boolean.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRange2_3(@ApiIgnore HttpSession session,
                                                @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                @PathVariable(Paths20.PARAM_COMP) String compPath) {
        JSLRangeAction comp = getJSLObjComp(session.getId(), objId, compPath, JSLRangeAction.class, "set 2/3 on range component");

        try {
            double second_third = comp.getMin() + ((comp.getMax() - comp.getMin()) / 3) + ((comp.getMax() - comp.getMin()) / 3);
            comp.execSetValue(second_third);
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw missingPermissionsExceptionOnSendAction(objId, compPath, e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw objNotConnectedExceptionOnSendAction(objId, compPath, e);
        }
    }

}
