/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.clients;

import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.states.JCPClient2State;
import com.robypomper.josp.states.StateException;

import java.util.Date;
import java.util.Map;


/**
 * This interface expose all method for JCP Clients.
 * <p>
 * It's used as interface by any JCP client: by JOD and JSL clients to JCP APIs,
 * but also by JCP services to connect to other JCP services.
 * <p>
 * JCPClient2 implementation must implement support for:
 * <ul>
 *    <li><b>Connection</b></li>
 *    <li><b>Login</b></li>
 *    <li><b>Headers</b></li>
 *    <li><b>Sessions</b></li>
 *    <li><b>Request execution</b></li>
 * </ul>
 */
@SuppressWarnings("unused")
public interface JCPClient2 {

    // Getter state

    /**
     * @return the JCP Client's status.
     */
    JCPClient2State getState();

    /**
     * @return true only if the JCP Client is connected to the designed
     * JCP service.
     */
    boolean isConnected();

    /**
     * @return true only if the JCP Client is connecting (or waiting for
     * re-connecting) to the designed JCP service.
     */
    boolean isConnecting();

    /**
     * @return true only if the JCP Client is NOT connected and is not try to
     * reconnect to the designed JCP service.
     */
    boolean isDisconnecting();

    /**
     * @return true only if the JCP Client had negotiated a session with
     * the designed JCP service.
     */
    boolean isSessionSet();

    /**
     * @return the Date when last connection to the designed JCP Service was
     * opened successfully.
     */
    Date getLastConnection();

    /**
     * @return the Date when last connection to the designed JCP Service was
     * closed.
     */
    Date getLastDisconnection();


    // Getter configs

    String getClientId();           /* client id used to authenticate??? */

    String getApiName();            /* Displayable APIs name */

    String getAPIsUrl();            /* Full url 'protocol://domain:port/' */

    String getAPIsHostname();       /* replace getIPAPIs() */

    String getAuthUrl();            /* Full url 'protocol://domain:port/' */

    String getAuthHostname();       /* replace getIPAuth() */

    String getAuthLoginUrl();                       /* replace getLoginUrl() */

    String getAuthLoginUrl(String redirectUrl);

    String getAuthLogoutUrl();                      /* replace getLogoutUrl() */

    String getAuthLogoutUrl(String redirectUrl);    /* replace getLogoutUrl(String) */

    String getAuthRegistrationUrl();

    String getAuthCodeRefreshToken();

    boolean isHttps();


    // Getter state authentication

    boolean isUserAuthenticated();  /* replace isLoggedIn() */

    boolean isUserAnonymous();

    boolean isClientCredentialFlowEnabled();

    boolean isAuthCodeFlowEnabled();


    // Connection

    void connect() throws StateException, AuthenticationException;

    void disconnect() throws StateException;


    // Login

    void setLoginCode(String loginCode);

    void userLogout();


    // Connection listeners

    void addConnectionListener(ConnectionListener listener);

    void removeConnectionListener(ConnectionListener listener);


    // Connection listeners interfaces

    interface ConnectionListener {

        void onConnected(JCPClient2 jcpClient);

        void onConnectionFailed(JCPClient2 jcpClient, Throwable t);

        void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t);

        void onDisconnected(JCPClient2 jcpClient);

    }


    // Login listeners

    void addLoginListener(LoginListener listener);

    void removeLoginListener(LoginListener listener);


    // Login listeners interface

    interface LoginListener {

        void onLogin(JCPClient2 jcpClient);

        void onLogout(JCPClient2 jcpClient);

    }


    // Headers and sessions

    void addDefaultHeader(String headerName, String headerValue);

    void removeDefaultHeader(String headerName);


    // Exec requests

    void execReq(Verb reqType, String path) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(boolean toAuth, Verb reqType, String path) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(Verb reqType, String path, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(boolean toAuth, Verb reqType, String path, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(Verb reqType, String path, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(boolean toAuth, Verb reqType, String path, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(Verb reqType, String path, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    void execReq(boolean toAuth, Verb reqType, String path, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    <T> T execReq(Verb reqType, String path, Class<T> reqObject, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    <T> T execReq(Verb reqType, String path, Class<T> reqObject, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    <T> T execReq(Verb reqType, String path, Class<T> reqObject, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;

    <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException;


    // Connection exceptions

    class ConnectionException extends Throwable {

        public ConnectionException(String msg) {
            super(msg);
        }

        public ConnectionException(String msg, Throwable e) {
            super(msg, e);
        }

    }

    class JCPNotReachableException extends Throwable {

        public JCPNotReachableException(String msg) {
            super(msg);
        }

    }


    // Authentication exceptions

    class AuthenticationException extends Throwable {

        public AuthenticationException(String msg) {
            super(msg);
        }

        public AuthenticationException(String msg, Throwable e) {
            super(msg, e);
        }

    }


    // Request exceptions

    class RequestException extends Throwable {

        public RequestException(String msg) {
            super(msg);
        }

        public RequestException(String msg, Exception e) {
            super(msg, e);
        }

    }

    class ResponseException extends Throwable {
        protected final String fullUrl;

        public ResponseException(String msg, String fullUrl) {
            this(msg, fullUrl, null);
        }

        public ResponseException(String msg, String fullUrl, Exception e) {
            super(msg, e);
            this.fullUrl = fullUrl;
        }
    }

    class ResponseParsingException extends ResponseException {

        private static final String MSG = "Error on '%s' url response parsing ('%s').";

        public ResponseParsingException(String fullUrl, Exception e) {
            super(String.format(MSG, fullUrl, e.getMessage()), fullUrl, e);
        }

    }

    class BadRequest_400 extends ResponseException {

        private static final String MSG = "Server received Bad request for '%s' resource.";

        public BadRequest_400(String fullUrl) {
            super(String.format(MSG, fullUrl), fullUrl);
        }

    }

    class Unauthorized_401 extends ResponseException {

        private static final String MSG_1 = "Server received Unauthorized for '%s' resource, error: '%s'.";
        private static final String MSG_2 = "Server received Unauthorized for '%s' resource, error: '%s'\n\tinvalid_token='%s'";
        private static final String HEADER = "WWW-Authenticate";

        public Unauthorized_401(String fullUrl, Response response) {
            super(String.format(MSG_1, fullUrl, (response.getHeader(HEADER)!=null ? response.getHeader(HEADER) : "N/A")), fullUrl);
        }

        public Unauthorized_401(String fullUrl, Response response, String invalidToken) {
            super(String.format(MSG_2, fullUrl, (response.getHeader(HEADER)!=null ? response.getHeader(HEADER) : "N/A"), invalidToken), fullUrl);
        }

    }

    class NotAuthorized_403 extends ResponseException {

        private static final String MSG = "Client NOT authorized to access to '%s' resource.";

        public NotAuthorized_403(String fullUrl) {
            super(String.format(MSG, fullUrl), fullUrl);
        }

    }

    class NotFound_404 extends ResponseException {

        private static final String MSG = "Resource '%s' NOT found.";

        public NotFound_404(String fullUrl) {
            super(String.format(MSG, fullUrl), fullUrl);
        }

    }

    class Conflict_409 extends ResponseException {

        private static final String MSG = "Conflict on elaborating request on '%s' resource.";

        public Conflict_409(String fullUrl) {
            super(String.format(MSG, fullUrl), fullUrl);
        }

    }

    class Error_Code extends ResponseException {

        private static final String MSG = "Response error '%s' code on '%s' resource.";

        public Error_Code(String fullUrl, int code) {
            super(String.format(MSG, code, fullUrl), fullUrl);
        }

        public Error_Code(String fullUrl, int code, String body) {
            super(String.format(MSG + " Response body: '%s'", code, fullUrl, body), fullUrl);
        }

        public Error_Code(String fullUrl, int code, Exception cause) {
            super(String.format(MSG, code, fullUrl), fullUrl, cause);
        }

    }

}
