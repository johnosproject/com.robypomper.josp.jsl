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

package com.robypomper.josp.jcp.jslwebbridge.controllers.pub.admin.apis.executable;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.clients.ClientParams;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.apis.executable.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.admin.apis.executable.Paths20;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.controllers.ControllerLinkJSL;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.admin.JSLAdmin;
import com.robypomper.josp.types.RESTItemList;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * JCP JSL Web Bridge - Admin / APIs / Executable 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerLinkJSL {


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private ClientParams params;


    // Constructors

    public Controller20() {
        super(Paths20.API_NAME, Paths20.API_VER, JCPJSLWBVersions.API_NAME, Paths20.DOCS_NAME, Paths20.DOCS_DESCR);
    }


    // JCP APIs Executable methods

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Index.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Index> getJCPAPIsExecReq() {
        return ResponseEntity.ok(new Params20.Index());
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_ONLINE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_ONLINE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Date.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Date> getJCPAPIsExecOnlineReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecOnline());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_PROCESS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_PROCESS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Process.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Process> getJCPAPIsExecProcessReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecProcess());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.JavaIndex.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.JavaIndex> getJCPAPIsExecJavaReq() {
        return ResponseEntity.ok(new Params20.JavaIndex());
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_VM, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_VM)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.JavaVM.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.JavaVM> getJCPAPIsExecJavaVMReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecJavaVM());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_RUNTIME, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.JavaRuntime.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.JavaRuntime> getJCPAPIsExecJavaRuntimeReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecJavaRuntime());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_TIMES)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_TIMES)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.JavaTimes.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.JavaTimes> getJCPAPIsExecJavaTimesReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecJavaTimes());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_CLASSES)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_CLASSES)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.JavaClasses.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.JavaClasses> getJCPAPIsExecJavaClassesReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecJavaClasses());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_MEMORY)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_MEMORY)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.JavaMemory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.JavaMemory> getJCPAPIsExecJavaMemoryReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecJavaMemory());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_THREADS)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_THREADS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.JavaThreads.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.JavaThreads> getJCPAPIsExecJavaThreadsReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        Params20.JavaThreads result;
        try {
            result = jsl.getAdmin().getJCPAPIsExecJavaThreads();

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }

        List<RESTItemList> threadsList = new ArrayList<>();
        for (RESTItemList item : result.threadsList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            newItem.url = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_THREAD(Long.parseLong(item.id));
            threadsList.add(newItem);
        }
        result.threadsList = threadsList;

        return ResponseEntity.ok(result);
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_THREAD)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_JAVA_THREAD)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.JavaThread.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.JavaThread> getJCPAPIsExecJavaThreadReq(
            @ApiIgnore HttpSession session,
            @PathVariable(Paths20.PARAM_THREAD) String threadId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecJavaThread(Long.parseLong(threadId)));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_OS)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_OS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.OS.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.OS> getJCPAPIsExecOSReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecOS());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_CPU)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_CPU)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.CPU.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.CPU> getJCPAPIsExecCPUReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecCPU());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_MEMORY)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_MEMORY)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Memory.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Memory> getJCPAPIsExecMemoryReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecMemory());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_DISKS)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_DISKS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Disks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Disks> getJCPAPIsExecDisksReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        Params20.Disks result;
        try {
            result = jsl.getAdmin().getJCPAPIsExecDisks();

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }

        List<RESTItemList> diskList = new ArrayList<>();
        for (RESTItemList item : result.disksList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            try {
                newItem.url = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_DISK(URLEncoder.encode(item.id, StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            diskList.add(newItem);
        }
        result.disksList = diskList;

        return ResponseEntity.ok(result);
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_DISK)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_DISK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Disk.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Disk> getJCPAPIsExecDiskReq(
            @ApiIgnore HttpSession session,
            @PathVariable(Paths20.PARAM_THREAD) String diskId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecDisk(diskId));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_NETWORKS)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_NETWORKS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Networks.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Networks> getJCPAPIsExecNetworksReq(@ApiIgnore HttpSession session) {
        JSL jsl = getJSL(session.getId());
        Params20.Networks result;
        try {
            result = jsl.getAdmin().getJCPAPIsExecNetworks();

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }

        List<RESTItemList> networkList = new ArrayList<>();
        for (RESTItemList item : result.networksList) {
            RESTItemList newItem = new RESTItemList();
            newItem.id = item.id;
            newItem.name = item.name;
            newItem.url = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_NETWORK(Integer.parseInt(item.id));
            networkList.add(newItem);
        }
        result.networksList = networkList;

        return ResponseEntity.ok(result);
    }

    @GetMapping(path = Paths20.FULL_PATH_JSLWB_ADMIN_APIS_EXEC_NETWORK)
    @ApiOperation(value = Paths20.DESCR_PATH_JSLWB_ADMIN_APIS_EXEC_NETWORK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP ", response = Params20.Network.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
            @ApiResponse(code = 503, message = "Error accessing the resource"),
    })
    public ResponseEntity<Params20.Network> getJCPAPIsExecNetworkReq(
            @ApiIgnore HttpSession session,
            @PathVariable(Paths20.PARAM_NTWK) String networkId) {
        JSL jsl = getJSL(session.getId());
        try {
            return ResponseEntity.ok(jsl.getAdmin().getJCPAPIsExecNetwork(Integer.parseInt(networkId)));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw jcpServiceNotAvailable(jsl.getJCPClient(), e);

        } catch (JSLAdmin.UserNotAdminException | JSLAdmin.UserNotAuthException e) {
            throw userNotAuthorizedException(jsl.getJCPClient(), e);
        }
    }

}
