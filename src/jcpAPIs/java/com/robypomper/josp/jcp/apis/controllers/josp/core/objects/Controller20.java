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

package com.robypomper.josp.jcp.apis.controllers.josp.core.objects;

import com.robypomper.java.JavaRandomStrings;
import com.robypomper.josp.consts.JOSPConstants;
import com.robypomper.josp.defs.core.objects.Params20;
import com.robypomper.josp.defs.core.objects.Paths20;
import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.StatusHistoryDBService;
import com.robypomper.josp.jcp.db.apis.entities.Object;
import com.robypomper.josp.jcp.db.apis.entities.ObjectId;
import com.robypomper.josp.jcp.db.apis.entities.ObjectStatusHistory;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;


/**
 * JOSP Core - Objects 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImpl {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private ObjectDBService objectDBService;
    @Autowired
    private StatusHistoryDBService statusHistoryDBService;

    // Generator methods

    /**
     * Generate and return a valid Object's ID.
     * <p>
     * Each object id is registered to given hardware id and object's owner (user
     * id). So when is request a object id from already registered pair (object hw/
     * owner id), same object id will returned.
     *
     * @param objIdParams object containing the object's hardware id and his owner id.
     * @return valid object's object id for given params.
     */
    @PostMapping(path = Paths20.FULL_PATH_ID_GENERATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_ID_GENERATE,
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
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<String> generateObjectId(
            @RequestBody Params20.GenerateObjId objIdParams) {
        return regenerateObjectId(null, objIdParams);
    }

    /**
     * Generate and return a valid Object's ID.
     * <p>
     * Each object id is registered to given hardware id and object's owner (user
     * id). So when is request a object id from already registered pair (object hw/
     * owner id), same object id will returned.
     *
     * @param objIdParams object containing the object's hardware id and his owner id.
     * @return valid object's object id for given params.
     */
    @PostMapping(path = Paths20.FULL_PATH_ID_REGENERATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_ID_REGENERATE,
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
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<String> regenerateObjectId(
            @RequestHeader(JOSPConstants.API_HEADER_OBJ_ID) String oldObjId,
            @RequestBody Params20.GenerateObjId objIdParams) {

        ObjectId newObjId = toObjectId(objIdParams, oldObjId);
        objectDBService.save(newObjId);

        if (oldObjId != null) {
            Optional<Object> optObj = objectDBService.find(oldObjId);
            if (optObj.isPresent()) {
                Object obj = optObj.get();
                obj.setActive(false);
                objectDBService.save(obj);
            }
        }

        return ResponseEntity.ok(newObjId.getObjId());
    }


    // History methods

    @PostMapping(path = Paths20.FULL_PATH_HISTORY, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_HISTORY,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok"/*, message = "Object's statuses", response = Event.class, responseContainer = "List"*/),
            @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_OBJ_ID),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<Boolean> postObjectHistory(@RequestHeader(JOSPConstants.API_HEADER_OBJ_ID) String objId,
                                                     @RequestBody List<Params20.HistoryStatus> statusHistories) {
        checkObjId(log, objId);

        for (Params20.HistoryStatus hs : statusHistories) {
            ObjectStatusHistory hsDB = new ObjectStatusHistory();
            hsDB.setShId(hs.id);
            hsDB.setObjId(objId);
            hsDB.setCompPath(hs.compPath);
            hsDB.setCompType(hs.compType);
            hsDB.setUpdatedAt(hs.updatedAt);
            hsDB.setPayload(hs.payload);
            statusHistoryDBService.add(hsDB);
        }

        return ResponseEntity.ok(true);
    }


    // ObjectId register

    private ObjectId toObjectId(Params20.GenerateObjId objIdParams, String oldObjId) {
        ObjectId objId = new ObjectId();
        objId.setObjId(String.format("%s-%s-%s", objIdParams.objIdHw, JavaRandomStrings.randomAlfaString(5), JavaRandomStrings.randomAlfaString(5)));
        objId.setObjIdHw(objIdParams.objIdHw);
        objId.setUsrId(objIdParams.ownerId);
        if (oldObjId != null)
            objId.setOldObjId(oldObjId);
        else
            objId.setOldObjId("");
        return objId;
    }

}
