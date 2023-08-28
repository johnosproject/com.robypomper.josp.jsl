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

package com.robypomper.josp.jcp.base.controllers.internal.status.executable;

import com.robypomper.java.JavaDate;
import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.defs.base.internal.status.executable.Params20;
import com.robypomper.josp.jcp.defs.base.internal.status.executable.Paths20;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.net.SocketException;
import java.util.Date;


/**
 * JCP All - Status / Executable 2.0
 */
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImpl {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);


    // Index methods

    @GetMapping(path = Paths20.FULL_PATH_EXEC, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's executable index", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Index> getIndex() {
        return ResponseEntity.ok(new Params20.Index());
    }


    // Online methods

    @GetMapping(path = Paths20.FULL_PATH_EXEC_ONLINE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_ONLINE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's local date", response = String.class)
    })
    public ResponseEntity<Date> getOnlineReq() {
        return ResponseEntity.ok(JavaDate.getNowDate());
    }


    // Process methods

    @GetMapping(path = Paths20.FULL_PATH_EXEC_PROCESS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_PROCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's process info and stats", response = Params20.Process.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Process> getProcessReq() {
        return ResponseEntity.ok(new Params20.Process());
    }


    // Java methods

    @GetMapping(path = Paths20.FULL_PATH_EXEC_JAVA, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_JAVA,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's executable java index", response = Params20.JavaIndex.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaIndex> getJavaIndex() {
        return ResponseEntity.ok(new Params20.JavaIndex());
    }

    @GetMapping(path = Paths20.FULL_PATH_EXEC_JAVA_VM, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_JAVA_VM,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaVM.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaVM> getJavaVMReq() {
        return ResponseEntity.ok(new Params20.JavaVM());
    }

    @GetMapping(path = Paths20.FULL_PATH_EXEC_JAVA_RUNTIME, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_JAVA_RUNTIME,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaRuntime.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaRuntime> getJavaRuntimeReq() {
        return ResponseEntity.ok(new Params20.JavaRuntime());
    }

    @GetMapping(path = Paths20.FULL_PATH_EXEC_JAVA_TIMES, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_JAVA_TIMES,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaTimes.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaTimes> getJavaTimesReq() {
        return ResponseEntity.ok(new Params20.JavaTimes());
    }

    @GetMapping(path = Paths20.FULL_PATH_EXEC_JAVA_CLASSES, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_JAVA_CLASSES,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaClasses.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaClasses> getJavaClassesReq() {
        return ResponseEntity.ok(new Params20.JavaClasses());
    }

    @GetMapping(path = Paths20.FULL_PATH_EXEC_JAVA_MEMORY, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_JAVA_MEMORY,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaMemory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaMemory> getJavaMemoryReq() {
        return ResponseEntity.ok(new Params20.JavaMemory());
    }

    @GetMapping(path = Paths20.FULL_PATH_EXEC_JAVA_THREADS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_JAVA_THREADS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaThreads.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaThreads> getJavaThreadsReq() {
        return ResponseEntity.ok(new Params20.JavaThreads());
    }

    @GetMapping(path = Paths20.FULL_PATH_EXEC_JAVA_THREAD, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_JAVA_THREAD,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's java vm and process info and stats", response = Params20.JavaThread.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.JavaThread> getJavaThreadReq(@PathVariable(Paths20.PARAM_THREAD) long threadId) {
        return ResponseEntity.ok(new Params20.JavaThread(threadId));
    }


    // OS methods

    @GetMapping(path = Paths20.FULL_PATH_EXEC_OS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_OS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's operating system info and stats", response = Params20.OS.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.OS> getOsReq() {
        return ResponseEntity.ok(new Params20.OS());
    }


    // CPU methods

    @GetMapping(path = Paths20.FULL_PATH_EXEC_CPU, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_CPU,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's CPU info and stats", response = Params20.CPU.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.CPU> getCPUReq() {
        return ResponseEntity.ok(new Params20.CPU());
    }


    // Memory methods

    @GetMapping(path = Paths20.FULL_PATH_EXEC_MEMORY, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_MEMORY,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's memory info and stats", response = Params20.Memory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Memory> getMemoryReq() {
        return ResponseEntity.ok(new Params20.Memory());
    }


    // Disks methods

    @GetMapping(path = Paths20.FULL_PATH_EXEC_DISKS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_DISKS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's disks info and stats", response = Params20.Disks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Disks> getDisksReq() {
        return ResponseEntity.ok(new Params20.Disks());
    }

    @GetMapping(path = Paths20.FULL_PATH_EXEC_DISK, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_DISK,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's disks info and stats", response = Params20.Disk.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Disk> getDiskReq(@PathVariable(Paths20.PARAM_DISK) String diskId) {
        return ResponseEntity.ok(new Params20.Disk(diskId));
    }


    // Networks methods

    @GetMapping(path = Paths20.FULL_PATH_EXEC_NETWORKS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_NETWORKS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's network info and stats", response = Params20.Networks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Networks> getNetworksReq() {
        return ResponseEntity.ok(new Params20.Networks());
    }

    @GetMapping(path = Paths20.FULL_PATH_EXEC_NETWORK, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_EXEC_NETWORK,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP Service's network's interfaces info and stats", response = Params20.Network.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<Params20.Network> getNetworkIntfsReq(@PathVariable(Paths20.PARAM_NTWK) int networkId) {
        try {
            return ResponseEntity.ok(new Params20.Network(networkId));
        } catch (SocketException e) {
            throw genericException(Paths20.FULL_PATH_EXEC_NETWORK, e);
        }
    }

}
