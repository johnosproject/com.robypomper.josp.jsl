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

package com.robypomper.josp.jcp.jslwebbridge.controllers.pub.core.init;

import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.init.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.init.Paths20;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.controllers.ControllerImplJSL;
import com.robypomper.josp.jcp.jslwebbridge.exceptions.JSLAlreadyInitForSessionException;
import com.robypomper.josp.jcp.jslwebbridge.exceptions.JSLErrorOnInitException;
import com.robypomper.josp.jcp.jslwebbridge.exceptions.JSLNotInitForSessionException;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jsl.JSL;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * JCP JSL Web Bridge - Init 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImplJSL {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private JSLWebBridgeService webBridgeService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Constructors

    public Controller20() {
        super(Paths20.API_NAME, Paths20.API_VER, JCPJSLWBVersions.API_NAME, Paths20.DOCS_NAME, Paths20.DOCS_DESCR);
    }


    // Methods - JSL Instance Status

    @GetMapping(path = Paths20.FULL_PATH_INIT_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_INIT_STATUS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Params20.JSLStatus.class),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Params20.JSLStatus> statusJSL(@ApiIgnore HttpSession session) {
        boolean isJSLInit = false;
        try {
            webBridgeService.getJSL(session.getId());
            isJSLInit = true;

        } catch (JSLNotInitForSessionException ignore) {
        }

        Params20.JSLStatus jslStatus = new Params20.JSLStatus();
        jslStatus.sessionId = session.getId();
        jslStatus.isJSLInit = isJSLInit;
        return ResponseEntity.ok(jslStatus);
    }


    // Methods - Init JSL Instance

    @GetMapping(path = Paths20.FULL_PATH_INIT_JSL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_INIT_JSL)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<String> initJSL(@ApiIgnore HttpSession session,
                                          @RequestParam("client_id") String clientId,
                                          @RequestParam("client_secret") String clientSecret,
                                          @RequestParam(name = "client_callback", required = false) String clientCallback) {
        JSL jsl;
        try {
            jsl = webBridgeService.initJSL(session.getId(), clientId, clientSecret, clientCallback);

        } catch (JSLAlreadyInitForSessionException ignore) {
            try {
                jsl = webBridgeService.getJSL(session.getId());

            } catch (JSLNotInitForSessionException ignore2) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't initialize/get JSL instance for '%s' session", session.getId()));
            }

        } catch (JSLErrorOnInitException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't initialize JSL instance for '%s' session (%s)", session.getId(), e));
        }

        return ResponseEntity.ok(jsl.getServiceInfo().getFullId());
    }


    // Methods - SSE Updater

    @GetMapping(path = Paths20.FULL_PATH_INIT_SSE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_INIT_SSE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = SseEmitter.class),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public SseEmitter initSSE(@ApiIgnore HttpSession session,
                              @ApiIgnore HttpServletResponse response,
                              @RequestParam(name = "client_id", required = false) String clientId,
                              @RequestParam(name = "client_secret", required = false) String clientSecret,
                              @RequestParam(name = "client_callback", required = false) String clientCallback) {
        if (clientId != null)
            initJSL(session, clientId, clientSecret,clientCallback);

        SseEmitter emitter;
        try {
            emitter = webBridgeService.getJSLEmitter(session.getId());

        } catch (JSLNotInitForSessionException e) {
            throw jslNotInitForSessionException(session.getId(), "initialize JSL Emitter");
        }

        response.addHeader("X-Accel-Buffering", "no");
        return emitter;
    }

}
