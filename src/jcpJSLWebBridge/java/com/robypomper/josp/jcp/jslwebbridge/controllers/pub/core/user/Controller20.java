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

package com.robypomper.josp.jcp.jslwebbridge.controllers.pub.core.user;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.user.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.user.Paths20;
import com.robypomper.josp.jcp.info.JCPJSLWBVersions;
import com.robypomper.josp.jcp.jslwebbridge.controllers.ControllerImplJSL;
import com.robypomper.josp.jcp.jslwebbridge.services.JSLWebBridgeService;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.states.StateException;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * JCP JSL Web Bridge - User 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImplJSL {

    public static final String SESS_ATTR_LOGIN_REDIRECT = "redirect_url_login";
    public static final String SESS_ATTR_LOGOUT_REDIRECT = "redirect_url_logout";

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private JSLWebBridgeService webBridgeService;
    private final String URL_REDIRECT_HOME = "/";


    // Constructors

    public Controller20() {
        super(Paths20.API_NAME, Paths20.API_VER, JCPJSLWBVersions.API_NAME, Paths20.DOCS_NAME, Paths20.DOCS_DESCR);
    }


    // Methods User Info

    @GetMapping(path = Paths20.FULL_PATH_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_DETAILS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = Params20.JOSPUserHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Params20.JOSPUserHtml> jsonUserDetails(@ApiIgnore HttpSession session) {

        JSL jsl = getJSL(session.getId(), "get user");
        JSLUserMngr jslUserMngr = jsl.getUserMngr();
        Params20.JOSPUserHtml jospUsr = new Params20.JOSPUserHtml();
        jospUsr.id = jslUserMngr.getUserId();
        jospUsr.name = jslUserMngr.getUsername();
        jospUsr.isAuthenticated = jslUserMngr.isUserAuthenticated();
        jospUsr.isAdmin = jslUserMngr.isAdmin();
        jospUsr.isMaker = jslUserMngr.isMaker();
        jospUsr.isDeveloper = jslUserMngr.isDeveloper();
        return ResponseEntity.ok(jospUsr);
    }


    // Methods - Login

    @GetMapping(path = Paths20.FULL_PATH_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_LOGIN)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<String> htmlLoginUser(@ApiIgnore HttpSession session,
                                                @ApiIgnore HttpServletResponse response,
                                                @RequestParam(name = "redirect_uri", required = false) String redirectUrl,
                                                @RequestParam(name = "auto_redirect", required = false) boolean autoRedirect) {
        JSL jsl = getJSL(session.getId(), "get login url");

        if (redirectUrl != null)
            session.setAttribute(SESS_ATTR_LOGIN_REDIRECT, redirectUrl);

        String redirect = jsl.getJCPClient().getAuthLoginUrl();

        if (autoRedirect) {
            try {
                response.sendRedirect(redirect);

            } catch (IOException ignore) {
            }
            return ResponseEntity.ok(String.format("Redirect failed, please go to <a href=\"%s\">%s</a>", redirect, redirect));
        }

        return ResponseEntity.ok(redirect);
    }

    @GetMapping(path = Paths20.FULL_PATH_LOGIN_CALLBACK, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_LOGIN_CALLBACK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<String> htmlLoginUserCallback(@ApiIgnore HttpSession session,
                                                        @ApiIgnore HttpServletResponse response,
                                                        @RequestParam(name = "session_state") String sessionState,
                                                        @RequestParam(name = "code") String code) {
        //https://localhost:8080/login/code/?session_state=087edff3-848c-4b59-9592-e44c7410e6b0&code=8ab0ceb4-e3cf-48e2-99df-b59fe7be129d.087edff3-848c-4b59-9592-e44c7410e6b0.79e472b0-e562-4535-a516-db7d7696a447
        JSL jsl = getJSL(session.getId(), "exec user login callback");

        String redirectURL = (String) session.getAttribute(SESS_ATTR_LOGIN_REDIRECT);
        session.removeAttribute(SESS_ATTR_LOGIN_REDIRECT);

        try {
            jsl.getJCPClient().setLoginCodeAndReconnect(code);

        } catch (StateException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't connect JCP APIs service because %s", e.getMessage()), e);

        } catch (JCPClient2.AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't access to JCP APIs service because authentication error %s", e.getMessage()), e);
        }

        try {
            if (redirectURL != null)
                response.sendRedirect(redirectURL);
            else
                response.sendRedirect(URL_REDIRECT_HOME);

        } catch (IOException ignore) {
        }

        return ResponseEntity.ok(String.format("User login successfully but redirect failed, please go to <a href=\"%s\">%s</a>", URL_REDIRECT_HOME, URL_REDIRECT_HOME));
    }

    @GetMapping(path = Paths20.FULL_PATH_LOGIN_EXT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_LOGIN_EXT)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<String> htmlLoginUserExt(@ApiIgnore HttpSession session,
                                                   @ApiIgnore HttpServletResponse response,
                                                   @RequestParam(name = "code_redirect_uri", required = false) String codeRedirectUrl,
                                                   @RequestParam(name = "redirect_uri", required = false) String redirectUrl,
                                                   @RequestParam(name = "auto_redirect", required = false) boolean autoRedirect) {
        JSL jsl = getJSL(session.getId(), "get login url");

        if (redirectUrl != null)
            session.setAttribute(SESS_ATTR_LOGIN_REDIRECT, redirectUrl);

        String redirect = jsl.getJCPClient().getAuthLoginUrl();
        // https://auth-stage.johnosproject.org:/auth/realms/jcp/protocol/openid-connect/auth?response_type=code&client_id=jcp-fe&redirect_uri=https%3A%2F%2Fjslwb-stage.johnosproject.org%3A443%2Fapis%2Fpub%2Fjslwebbridge%2Fcore%2Fuser%2F2.0%2Flogin%2Fcode%2F&scope=openid%20offline_access

        String authUrl = redirect;
        String authUrl_url = authUrl.split("\\?")[0];
        String authUrl_params = authUrl.split("\\?")[1];
        try {
            authUrl_params = authUrl_params.replaceFirst("\\bredirect_uri=.*?(&|$)", "redirect_uri=" + URLEncoder.encode(codeRedirectUrl, "UTF-8") + "$1");
        } catch (UnsupportedEncodingException e) {
            authUrl_params = authUrl_params.replaceFirst("\\bredirect_uri=.*?(&|$)", "redirect_uri=" + codeRedirectUrl + "$1");
        }
        redirect = authUrl_url + "?" + authUrl_params;

        if (autoRedirect) {
            try {
                response.sendRedirect(redirect);

            } catch (IOException ignore) {
            }
            return ResponseEntity.ok(String.format("Redirect failed, please go to <a href=\"%s\">%s</a>", redirect, redirect));
        }

        return ResponseEntity.ok(redirect);
    }


    // Methods - Logout

    @GetMapping(path = Paths20.FULL_PATH_LOGOUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_LOGOUT)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<String> htmlLogoutUser(@ApiIgnore HttpSession session,
                                                 @ApiIgnore HttpServletResponse response,
                                                 @RequestParam(name = "redirect_uri", required = false) String redirectUrl,
                                                 @RequestParam(name = "auto_redirect", required = false) boolean autoRedirect) {

        JSL jsl = getJSL(session.getId(), "get logout url");

        String redirect = jsl.getJCPClient().getAuthLogoutUrl(redirectUrl);
        jsl.getJCPClient().userLogout();

        if (autoRedirect) {
            try {
                response.sendRedirect(redirect);

            } catch (IOException ignore) {
            }
            return ResponseEntity.ok(String.format("User logout successfully but redirect failed, please go to <a href=\"%s\">%s</a>", redirect, redirect));
        }

        return ResponseEntity.ok(redirect);
    }


    // Methods - Registration

    @GetMapping(path = Paths20.FULL_PATH_REGISTRATION, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_REGISTRATION)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = String.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<String> htmlRegistrationUser(@ApiIgnore HttpSession session,
                                                       @ApiIgnore HttpServletResponse response,
                                                       @RequestParam(name = "redirect_uri", required = false) String redirectUrl,
                                                       @RequestParam(name = "auto_redirect", required = false) boolean autoRedirect) {

        JSL jsl = getJSL(session.getId(), "get registration url");

        if (redirectUrl != null)
            session.setAttribute(SESS_ATTR_LOGIN_REDIRECT, redirectUrl);

        String redirect = jsl.getJCPClient().getAuthRegistrationUrl();
        jsl.getJCPClient().userLogout();

        if (autoRedirect) {
            try {
                response.sendRedirect(redirect);

            } catch (IOException ignore) {
            }
            return ResponseEntity.ok(String.format("Redirect failed, please go to <a href=\"%s\">%s</a>", redirect, redirect));
        }

        return ResponseEntity.ok(redirect);
    }


    // Utils

    private String getCurrentBaseUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder();
        String scheme = request.getScheme();
        int port = request.getServerPort();
        if (port < 0)
            port = 80;          // Work around java.net.URL bug

        if (request.getHeader("X-Forwarded-For") != null) {
            scheme = "https";
            port = 443;
        }

        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());
        if ((scheme.equals("http") && (port != 80))
                || (scheme.equals("https") && (port != 443))) {
            url.append(':');
            url.append(port);
        }

        return url.toString();
    }

}
