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

package com.robypomper.josp.jcp.apis.controllers.josp.core.permissions;

import com.robypomper.java.JavaDate;
import com.robypomper.josp.consts.JOSPConstants;
import com.robypomper.josp.defs.core.permissions.Paths20;
import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.db.apis.PermissionsDBService;
import com.robypomper.josp.protocol.JOSPPerm;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;


/**
 * JOSP Core - Permissions 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImpl {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private PermissionsDBService permissionsDBService;


    // Generator methods

    /**
     * Generate and return a valid Object's permission set depending on required
     * <code>strategy</code>.
     * <p>
     * Strategies:
     * <ul>
     *     <li>
     *         STANDARD:<br>
     *             #Owner, #All, LocalAndCloud, CoOwner
     *     </li>
     *     <li>
     *         PUBLIC:<br>
     *             #Owner, #All, LocalAndCloud, CoOwner<br>
     *             #All, #All, OnlyLocal, Action
     *     </li>
     * </ul>
     *
     * @param objId    the object id to register.
     * @param strategy the strategy to use for permission generation.
     * @return a set of object's permissions.
     */
    @GetMapping(path = Paths20.FULL_PATH_GENERATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_GENERATE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_OBJ_ID),
            @ApiResponse(code = 501, message = "Requested '" + JOSPConstants.API_HEADER_OBJ_ID + "' strategy not implemented")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<String> generatePermissions(
            @RequestHeader(JOSPConstants.API_HEADER_OBJ_ID) String objId,
            @PathVariable(Paths20.PARAM_STRATEGY) JOSPPerm.GenerateStrategy strategy) {

        checkObjId(log, objId);

        List<JOSPPerm> objPerms;
        if (strategy == JOSPPerm.GenerateStrategy.STANDARD)
            objPerms = populateStandard(objId);

        else if (strategy == JOSPPerm.GenerateStrategy.PUBLIC)
            objPerms = populatePublic(objId);

        else
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, String.format("Can't generate obj's permission because unknown strategy '%s'.", strategy));

        return ResponseEntity.ok(JOSPPerm.toString(objPerms));
    }

    private List<JOSPPerm> populateStandard(String objId) {
        List<JOSPPerm> objPerms = new ArrayList<>();
        objPerms.add(new JOSPPerm(objId, JOSPPerm.WildCards.SRV_ALL.toString(), JOSPPerm.WildCards.USR_OWNER.toString(), JOSPPerm.Type.CoOwner, JOSPPerm.Connection.LocalAndCloud, JavaDate.getNowDate()));
        return objPerms;
    }

    private List<JOSPPerm> populatePublic(String objId) {
        List<JOSPPerm> objPerms = new ArrayList<>();
        objPerms.add(new JOSPPerm(objId, JOSPPerm.WildCards.SRV_ALL.toString(), JOSPPerm.WildCards.USR_OWNER.toString(), JOSPPerm.Type.CoOwner, JOSPPerm.Connection.LocalAndCloud, JavaDate.getNowDate()));
        objPerms.add(new JOSPPerm(objId, JOSPPerm.WildCards.SRV_ALL.toString(), JOSPPerm.WildCards.USR_ALL.toString(), JOSPPerm.Type.Actions, JOSPPerm.Connection.OnlyLocal, JavaDate.getNowDate()));
        return objPerms;
    }

}
