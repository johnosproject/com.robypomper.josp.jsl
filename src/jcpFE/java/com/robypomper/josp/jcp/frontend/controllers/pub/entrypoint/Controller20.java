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

package com.robypomper.josp.jcp.frontend.controllers.pub.entrypoint;

import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.defs.frontend.pub.entrypoint.Paths20;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * JCP Front End API - Entry Point 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImpl {

    // Internal vars

    private final String jslWBUrlInternal;
    private final String jslWBUrlPublic;
    private final String clientId;
    private final String clientSecret;


    // Constructor

    @Autowired
    public Controller20(ClientParams params) {
        this.jslWBUrlInternal = (params.sslPrivate ? "https://" : "http:") + (params.jslWBHostPrivate) + ":" + params.jslWBPort;
        this.jslWBUrlPublic = (params.sslPublic ? "https://" : "http:") + (params.jslWBHostPublic) + ":" + params.jslWBPort;
        this.clientId = params.clientId;
        this.clientSecret = params.clientSecret;
    }


    // Methods

    @GetMapping(path = Paths20.FULL_PATH_ENTRYPOINT, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_ENTRYPOINT)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP FE Backend entry point url", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<String> getEntrypoint() {
        return ResponseEntity.ok(jslWBUrlPublic);
    }

    @GetMapping(path = Paths20.FULL_PATH_INIT_JSL_SESSION, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_INIT_JSL_SESSION)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP FE Backend entry point", response = Boolean.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    public ResponseEntity<Boolean> initJSLWebBridgeSession(@RequestParam("session_id") String sessionId) {
        try {
            URL url = new URL(jslWBUrlInternal + com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.init.Paths20.FULL_PATH_INIT_JSL + "?client_id=" + clientId + "&client_secret=" + clientSecret);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);
            conn.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((reader.readLine()) != null) ;
            reader.close();

            return ResponseEntity.ok(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(false);
    }

}
