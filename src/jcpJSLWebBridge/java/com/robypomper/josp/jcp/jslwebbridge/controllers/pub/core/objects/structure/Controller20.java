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

package com.robypomper.josp.jcp.jslwebbridge.controllers.pub.core.objects.structure;

import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.structure.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.structure.Paths20;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.controllers.ControllerImplJSL;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
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
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


/**
 * JCP JSL Web Bridge - Objects / Structure 2.0
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


    // Methods - Obj's Structure

    @GetMapping(path = Paths20.FULL_PATH_STRUCT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_STRUCT)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Params20.JOSPStructHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Params20.JOSPStructHtml> jsonObjectStructure(@ApiIgnore HttpSession session,
                                                                       @PathVariable(Paths20.PARAM_OBJ) String objId) {
        JSLRemoteObject obj = getJSLObj(session.getId(), objId, "get object structure");

        // ToDo add MissingPermission exception to getStructure() method
        JSLRoot root = obj.getStruct().getStructure();
        Params20.JOSPStructHtml jospStruct = new Params20.JOSPStructHtml();
        setComponentHtml(jospStruct, root);
        setContainerHtml(jospStruct, root);
        setRootHtml(jospStruct, root);
        return ResponseEntity.ok(jospStruct);
    }


    // Obj's compos

    @GetMapping(path = Paths20.FULL_PATH_COMP, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_COMP)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Params20.JOSPComponentHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Params20.JOSPComponentHtml> jsonObjectComponent(@ApiIgnore HttpSession session,
                                                                          @PathVariable(Paths20.PARAM_OBJ) String objId,
                                                                          @PathVariable(Paths20.PARAM_COMP) String compPath) {
        JSLRemoteObject obj = getJSLObj(session.getId(), objId, "get object component");

        JSLComponent comp = compPath.equals("-") ? obj.getStruct().getStructure() : getJSLObjComp(session.getId(), objId, compPath, JSLComponent.class, "get object component");
        return ResponseEntity.ok(generateJOSPComponentHtml(comp));
    }


    // Shared structure support methods

    private static Params20.JOSPComponentHtml generateJOSPComponentHtml(JSLComponent comp) {
        if (comp instanceof JSLContainer) {
            Params20.JOSPContainerHtml jospContainer = new Params20.JOSPContainerHtml();
            setComponentHtml(jospContainer, comp);
            setContainerHtml(jospContainer, (JSLContainer) comp);
            return jospContainer;
        }

        // Actions
        if (comp instanceof JSLBooleanAction) {
            Params20.JOSPBooleanActionHtml jospBoolAction = new Params20.JOSPBooleanActionHtml();
            setComponentHtml(jospBoolAction, comp);
            setBoolStateHtml(jospBoolAction, (JSLBooleanState) comp);
            setBoolActionHtml(jospBoolAction, (JSLBooleanAction) comp);
            return jospBoolAction;
        }
        if (comp instanceof JSLRangeAction) {
            Params20.JOSPRangeActionHtml jospRangeAction = new Params20.JOSPRangeActionHtml();
            setComponentHtml(jospRangeAction, comp);
            setRangeStateHtml(jospRangeAction, (JSLRangeState) comp);
            setRangeActionHtml(jospRangeAction, (JSLRangeAction) comp);
            return jospRangeAction;
        }

        // States
        if (comp instanceof JSLBooleanState) {
            Params20.JOSPBooleanStateHtml jospBoolState = new Params20.JOSPBooleanStateHtml();
            setComponentHtml(jospBoolState, comp);
            setBoolStateHtml(jospBoolState, (JSLBooleanState) comp);
            return jospBoolState;
        }
        if (comp instanceof JSLRangeState) {
            Params20.JOSPRangeStateHtml jospRangeState = new Params20.JOSPRangeStateHtml();
            setComponentHtml(jospRangeState, comp);
            setRangeStateHtml(jospRangeState, (JSLRangeState) comp);
            return jospRangeState;
        }

        Params20.JOSPComponentHtml jospContainer = new Params20.JOSPComponentHtml();
        setComponentHtml(jospContainer, comp);
        return jospContainer;
    }

    private static void setComponentHtml(Params20.JOSPComponentHtml compHtml, JSLComponent comp) {
        String objId = comp.getRemoteObject().getId();
        String componentPath = comp.getPath().getString();
        compHtml.name = comp.getName();
        compHtml.description = comp.getDescr();
        compHtml.objId = comp.getRemoteObject().getId();
        compHtml.parentPath = comp.getParent() != null ? comp.getParent().getPath().getString() : "";
        compHtml.componentPath = comp.getPath().getString();
        compHtml.type = comp.getType();
        compHtml.pathSelf = Paths20.FULL_PATH_COMP(objId, comp instanceof JSLRoot ? "-" : componentPath);
        compHtml.pathHistory = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.states.Paths20.FULL_PATH_STATUS_HISTORY(objId, comp instanceof JSLRoot ? "-" : componentPath);
    }

    private static void setBoolStateHtml(Params20.JOSPBooleanStateHtml compHtml, JSLBooleanState comp) {
        String objId = comp.getRemoteObject().getId();
        String componentPath = comp.getPath().getString();
        compHtml.state = comp.getState();
        compHtml.pathState = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.states.Paths20.FULL_PATH_BOOL(objId, componentPath);
    }

    private static void setRangeStateHtml(Params20.JOSPRangeStateHtml compHtml, JSLRangeState comp) {
        String objId = comp.getRemoteObject().getId();
        String componentPath = comp.getPath().getString();
        compHtml.state = comp.getState();
        compHtml.pathState = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.states.Paths20.FULL_PATH_RANGE(objId, componentPath);
        compHtml.max = comp.getMax();
        compHtml.min = comp.getMin();
        compHtml.step = comp.getStep();
    }

    private static void setBoolActionHtml(Params20.JOSPBooleanActionHtml compHtml, JSLBooleanAction comp) {
        String objId = comp.getRemoteObject().getId();
        String componentPath = comp.getPath().getString();
        compHtml.pathSwitch = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20.FULL_PATH_BOOL_SWITCH(objId, componentPath);
        compHtml.pathTrue = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20.FULL_PATH_BOOL_TRUE(objId, componentPath);
        compHtml.pathFalse = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20.FULL_PATH_BOOL_FALSE(objId, componentPath);
    }

    private static void setRangeActionHtml(Params20.JOSPRangeActionHtml compHtml, JSLRangeAction comp) {
        String objId = comp.getRemoteObject().getId();
        String componentPath = comp.getPath().getString();
        compHtml.state = comp.getState();
        compHtml.pathState = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.states.Paths20.FULL_PATH_RANGE(objId, componentPath);
        compHtml.max = comp.getMax();
        compHtml.min = comp.getMin();
        compHtml.step = comp.getStep();
        compHtml.pathSetValue = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20.FULL_PATH_RANGE_SET(objId, componentPath);
        compHtml.pathInc = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20.FULL_PATH_RANGE_INC(objId, componentPath);
        compHtml.pathDec = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20.FULL_PATH_RANGE_DEC(objId, componentPath);
        compHtml.pathMax = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20.FULL_PATH_RANGE_MAX(objId, componentPath);
        compHtml.pathMin = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20.FULL_PATH_RANGE_MIN(objId, componentPath);
        compHtml.pathSet1_2 = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20.FULL_PATH_RANGE_1_2(objId, componentPath);
        compHtml.pathSet1_3 = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20.FULL_PATH_RANGE_1_3(objId, componentPath);
        compHtml.pathSet2_3 = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20.FULL_PATH_RANGE_2_3(objId, componentPath);
    }

    private static void setContainerHtml(Params20.JOSPContainerHtml compHtml, JSLContainer comp) {
        List<Params20.JOSPComponentHtml> subCompsHtml = new ArrayList<>();
        for (JSLComponent sc : comp.getComponents())
            subCompsHtml.add(generateJOSPComponentHtml(sc));
        compHtml.subComps = subCompsHtml;
    }

    private static void setRootHtml(Params20.JOSPStructHtml compHtml, JSLRoot comp) {
        compHtml.brand = comp.getDescr_long();
        compHtml.model = comp.getBrand();
        compHtml.descrLong = comp.getModel();
    }

}
