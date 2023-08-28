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

package com.robypomper.josp.jcp.apis.controllers.josp.core.events;

import com.robypomper.java.JavaString;
import com.robypomper.josp.consts.JOSPConstants;
import com.robypomper.josp.defs.core.events.Paths20;
import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.db.apis.EventDBService;
import com.robypomper.josp.jcp.db.apis.entities.Event;
import com.robypomper.josp.protocol.JOSPEvent;
import com.robypomper.josp.protocol.JOSPPerm;
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


/**
 * JOSP Core - Events 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImpl {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private EventDBService eventService;


    // Objects methods

    @PostMapping(path = Paths20.FULL_PATH_OBJECT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_OBJECT,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "True", response = Boolean.class),
            @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_OBJ_ID),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<Boolean> postObjectEvents(@RequestHeader(JOSPConstants.API_HEADER_OBJ_ID) String objId,
                                                    @RequestBody List<JOSPEvent> events) {
        checkObjId(log, objId);

        //eventService.add(Event.newObjEvent(client.getClientId(), EventType.ConnectToCloud));
        for (JOSPEvent e : events)
            eventService.add(Event.fromJOSPEvent(e));

        return ResponseEntity.ok(true);
    }
    //
    //@GetMapping(path = Paths20.FULL_PATH_OBJECT, produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation(value = Paths20.DESCR_PATH_OBJECTg,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_OBJ_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Object's events", response = Event.class, responseContainer = "List"),
    //        @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_OBJ_ID),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    //public ResponseEntity<List<Event>> getObjectEvents(@RequestHeader(JOSPConstants.API_HEADER_OBJ_ID) String objId) {
    //    checkObjId(log,objId);
    //
    //    return getObjectEventsById(objId);
    //}
    //
    //@GetMapping(path = Paths20.FULL_PATH_GET_OBJECT, produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation(value = Paths20.DESCR_PATH_GET_OBJECT,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_OBJ_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Object's events", response = Event.class, responseContainer = "List"),
    //        @ApiResponse(code = 400, message = "Missing mandatory param 'objId' or it's invalid full service id."),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    //public ResponseEntity<List<Event>> getObjectEventsById(@PathVariable("objId") String objId) {
    //        if (objId == null || objId.isEmpty())
    //            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory param '%s'.", JOSPConstants.API_HEADER_OBJ_ID));
    //
    //        if (!isObjectId(objId))
    //            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Given objId '%s' is not a valid object id.", objId));
    //
    //        return ResponseEntity.ok(eventService.findBySrcId(objId));
    //    }


    //// Methods Objects Last
    //
    //@GetMapping(path = Paths20.FULL_PATH_OBJECT_LAST, produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation(value = Paths20.DESCR_PATH_OBJECT_LAST,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_OBJ_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Last object's event", response = Event.class),
    //        @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_OBJ_ID),
    //        @ApiResponse(code = 404, message = "No events found for specified object"),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    //public ResponseEntity<Event> getObjectLastEvent(@RequestHeader(JOSPConstants.API_HEADER_OBJ_ID) String objId) {
    //    checkObjId(log,objId);
    //
    //    return getObjectLastEventById(objId);
    //}
    //
    //@GetMapping(path = Paths20.FULL_PATH_GET_OBJECT_LAST, produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation(value = Paths20.DESCR_PATH_GET_OBJECT_LAST,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_OBJ_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Last object's event", response = Event.class),
    //        @ApiResponse(code = 400, message = "Missing mandatory param 'objId' or it's invalid object id."),
    //        @ApiResponse(code = 404, message = "No events found for specified object"),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    //public ResponseEntity<Event> getObjectLastEventById(@PathVariable("objId") String objId) {
    //    if (objId == null || objId.isEmpty())
    //        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory param '%s'.", JOSPConstants.API_HEADER_OBJ_ID));
    //
    //    if (!isObjectId(objId))
    //        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Given objId '%s' is not a valid object id.", objId));
    //
    //    List<Event> events = eventService.findBySrcId(objId);
    //    if (events.size() < 1)
    //        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No events found for specified object.");
    //
    //    return ResponseEntity.ok(events.get(0));
    //}


    //// Methods Objects Type
    //
    //@GetMapping(path = Paths20.FULL_PATH_OBJECT_BY_TYPE, produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation(value = Paths20.DESCR_PATH_OBJECT_BY_TYPE,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_OBJ_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Object's events of specified type", response = Event.class, responseContainer = "List"),
    //        @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_OBJ_ID),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    //public ResponseEntity<List<Event>> getObjectTypeEvent(
    //        @RequestHeader(JOSPConstants.API_HEADER_OBJ_ID) String objId,
    //        @PathVariable("type") EventType type) {
    //    checkObjId(log,objId);
    //
    //    return getObjectTypeEventById(objId, type);
    //}
    //
    //@GetMapping(path = Paths20.FULL_PATH_GET_OBJECT_BY_TYPE, produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation(value = Paths20.DESCR_PATH_GET_OBJECT_BY_TYPE,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_OBJ_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Object's events of specified type", response = Event.class, responseContainer = "List"),
    //        @ApiResponse(code = 400, message = "Missing mandatory param 'objId' or it's invalid object id."),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    //public ResponseEntity<List<Event>> getObjectTypeEventById(
    //        @PathVariable("objId") String objId,
    //        @PathVariable("type") EventType type) {
    //        if (objId == null || objId.isEmpty())
    //            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory param '%s'.", JOSPConstants.API_HEADER_OBJ_ID));
    //
    //        if (!isObjectId(objId))
    //            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Given objId '%s' is not a valid object id.", objId));
    //
    //        return ResponseEntity.ok(eventService.findBySrcIdAndEvnType(objId, type));
    //    }


    // Service methods

    @PostMapping(path = Paths20.FULL_PATH_SERVICE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_SERVICE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "True", response = Boolean.class),
            @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_OBJ_ID),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<Boolean> postSERVICEEvents(@RequestHeader(JOSPConstants.API_HEADER_SRV_ID) String srvId,
                                                     @RequestBody List<JOSPEvent> events) {
        checkSrvId(log, srvId);

        //eventService.add(Event.newObjEvent(client.getClientId(), EventType.ConnectToCloud));
        for (JOSPEvent e : events)
            eventService.add(Event.fromJOSPEvent(e));

        return ResponseEntity.ok(true);
    }

    //@GetMapping(path = Paths20.FULL_PATH_SERVICE, produces = MediaType.APPLICATION_JSON_VALUE)              //  /service    -> FULL_PATH_GET_SERVICE
    //@ApiOperation(value = Paths20.DESCR_PATH_SERVICEg,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_SRV_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Service's events", response = Event.class, responseContainer = "List"),
    //        @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_SRV_ID),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    //public ResponseEntity<List<Event>> getServiceEvents(@RequestHeader(JOSPConstants.API_HEADER_SRV_ID) String srvId) {
    //    checkSrvId(log,srvId);
    //
    //    return getServiceEventsById(srvId);
    //}
    //
    //@GetMapping(path = Paths20.FULL_PATH_GET_SERVICE, produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation(value = Paths20.DESCR_PATH_GET_SERVICE,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_SRV_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Service's events", response = Event.class, responseContainer = "List"),
    //        @ApiResponse(code = 400, message = "Missing mandatory param 'fullSrvId' or it's invalid full service id."),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    //public ResponseEntity<List<Event>> getServiceEventsById(@PathVariable("fullSrvId") String srvId) {
    //    if (srvId == null || srvId.isEmpty())
    //        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory param '%s'.", JOSPConstants.API_HEADER_SRV_ID));
    //
    //    String srvIdReceived = srvId;
    //    srvId = srvId.replace('@', '/');
    //    if (!isServiceId(srvId))
    //        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Given srvId '%s' is not a valid service id (@ replaced with /).", srvIdReceived));
    //
    //    return ResponseEntity.ok(eventService.findBySrcId(srvId));
    //}


    //// Methods Service Last
    //
    //@GetMapping(path = Paths20.FULL_PATH_SERVICE_LAST, produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation(value = Paths20.DESCR_PATH_SERVICE_LAST,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_SRV_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Last service's event", response = Event.class),
    //        @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_SRV_ID),
    //        @ApiResponse(code = 404, message = "No events found for specified service"),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    //public ResponseEntity<Event> getServiceLastEvents(@RequestHeader(JOSPConstants.API_HEADER_SRV_ID) String srvId) {
    //    checkSrvId(log,srvId);
    //
    //    return getServiceLastEventsById(srvId);
    //}
    //
    //@GetMapping(path = Paths20.FULL_PATH_GET_SERVICE_LAST, produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation(value = Paths20.DESCR_PATH_GET_SERVICE_LAST,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_SRV_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Last service's event", response = Event.class),
    //        @ApiResponse(code = 400, message = "Missing mandatory param 'fullSrvId' or it's invalid full service id."),
    //        @ApiResponse(code = 404, message = "No events found for specified service"),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    //public ResponseEntity<Event> getServiceLastEventsById(@PathVariable("fullSrvId") String srvId) {
    //    if (srvId == null || srvId.isEmpty())
    //        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory param '%s'.", JOSPConstants.API_HEADER_SRV_ID));
    //
    //    String srvIdReceived = srvId;
    //    srvId = srvId.replace('@', '/');
    //    if (!isServiceId(srvId))
    //        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Given srvId '%s' is not a valid service id (@ replaced with /).", srvIdReceived));
    //
    //    List<Event> events = eventService.findBySrcId(srvId);
    //    if (events.size() < 1)
    //        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No events found for specified service.");
    //
    //    return ResponseEntity.ok(events.get(0));
    //}


    //// Methods Service Type
    //
    //@GetMapping(path = Paths20.FULL_PATH_SERVICE_BY_TYPE, produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation(value = Paths20.DESCR_PATH_SERVICE_BY_TYPE,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_SRV_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Service's events of specified event type", response = Event.class, responseContainer = "List"),
    //        @ApiResponse(code = 400, message = "Missing mandatory header " + JOSPConstants.API_HEADER_SRV_ID),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    //public ResponseEntity<List<Event>> getServiceTypeEvents(
    //        @RequestHeader(JOSPConstants.API_HEADER_SRV_ID) String srvId,
    //        @PathVariable("type") EventType type) {
    //    checkSrvId(log,srvId);
    //
    //    return getServiceTypeEventsById(srvId, type);
    //}
    //
    //@GetMapping(path = Paths20.FULL_PATH_GET_SERVICE_BY_TYPE, produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation(value = Paths20.DESCR_PATH_GET_SERVICE_BY_TYPE,
    //        authorizations = @Authorization(
    //                value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
    //                scopes = @AuthorizationScope(
    //                        scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
    //                        description = SwaggerConfigurer.ROLE_SRV_DESC
    //                )
    //        )
    //)
    //@ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Service's events of specified event type", response = Event.class, responseContainer = "List"),
    //        @ApiResponse(code = 400, message = "Missing mandatory param 'fullSrvId' or it's invalid full service id."),
    //})
    //@RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    //public ResponseEntity<List<Event>> getServiceTypeEventsById(
    //        @PathVariable("fullSrvId") String srvId,
    //        @PathVariable("type") EventType type) {
    //    if (srvId == null || srvId.isEmpty())
    //        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory param '%s'.", JOSPConstants.API_HEADER_SRV_ID));
    //
    //    String srvIdReceived = srvId;
    //    srvId = srvId.replace('@', '/');
    //    if (!isServiceId(srvId))
    //        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Given srvId '%s' is not a valid service id (@ replaced with /).", srvIdReceived));
    //
    //    return ResponseEntity.ok(eventService.findBySrcIdAndEvnType(srvId, type));
    //}


    // SrcId type checker

    private boolean isObjectId(String srcId) {
        // IUWUH-EHCEF-JZZCF
        return srcId.length() == JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString().length()
                && JavaString.occurrenceCount(srcId, "-") == 2;
    }

    private boolean isServiceId(String srcId) {
        // jcp-fe/00000-00000-00000/5464 = length29
        // jcp-fe/67de275c-1861-4e86-8e03-2ef643c8a592/9606 = length48
        return (srcId.length() == 29 || srcId.length() == 48)
                && JavaString.occurrenceCount(srcId, "/") == 2;
    }


}
