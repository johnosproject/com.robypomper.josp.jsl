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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.KeycloakApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.robypomper.java.*;
import com.robypomper.josp.jcp.defs.base.internal.status.executable.Paths20;
import com.robypomper.josp.states.JCPClient2State;
import com.robypomper.josp.states.StateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutionException;


@SuppressWarnings({"UnnecessaryReturnStatement", "unused"})
public class DefaultJCPClient2 implements JCPClient2 {

    // Class constants

    public static final String TH_CONNECTION_NAME = "CONN_%s";
    public static final String TH_CONNECTION_CHECK_NAME = "CONN_CK_%s";
    public static final String HEAD_COOKIE = "Cookie";
    public static final String HEAD_SET_COOKIE = "Set-Cookie";
    public static final String SESSION_KEY = "JSESSIONID";
    public static final String DEFAULT_ACCEPT = "application/json";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JavaEnum.SynchronizableState<JCPClient2State> state = new JavaEnum.SynchronizableState<>(JCPClient2State.DISCONNECTED, log);
    // Configs
    private final String clientId;
    private final String apiName;
    private final String baseUrlAuth;
    private final String baseUrlAPIs;
    private final boolean securedAPIs;
    private final String authRealm;
    private final int connectionTimerDelaySeconds;
    // Listeners
    private final List<ConnectionListener> connectionListeners = new ArrayList<>();
    private final List<LoginListener> loginListeners = new ArrayList<>();
    // Connection timers
    private Timer connectionTimer = null;
    private Timer connectionCheckTimer = null;
    // OAuth
    private final OAuth20Service service;
    private OAuth2AccessToken accessToken = null;
    private boolean cliCred_isConnected = false;
    private String cliCred_refreshToken = null;
    private boolean authCode_isConnected = false;
    private String authCode_refreshToken = null;
    private String authCode_loginCode = null;
    // Headers and session
    private final Map<String, String> defaultHeaders = new HashMap<>();
    private String sessionId = null;
    // Other
    private Date lastConnection;
    private Date lastDisconnection;


    // Constructor

    public DefaultJCPClient2(String clientId, String clientSecret,
                             String apisBaseUrl, boolean apisSecured,
                             String authBaseUrl, String authScopes, String authCallBack, String authRealm,
                             String apiName) {
        this(clientId, clientSecret, apisBaseUrl, apisSecured, authBaseUrl, authScopes, authCallBack, authRealm, null, 30, apiName);
    }

    public DefaultJCPClient2(String clientId, String clientSecret,
                             String apisBaseUrl, boolean apisSecured,
                             String authBaseUrl, String authScopes, String authCallBack, String authRealm,
                             int connectionRetrySeconds,
                             String apiName) {
        this(clientId, clientSecret, apisBaseUrl, apisSecured, authBaseUrl, authScopes, authCallBack, authRealm, null, connectionRetrySeconds, apiName);
    }

    public DefaultJCPClient2(String clientId, String clientSecret,
                             String apisBaseUrl, boolean apisSecured,
                             String authBaseUrl, String authScopes, String authCallBack, String authRealm, String authCodeRefreshToken,
                             String apiName) {
        this(clientId, clientSecret, apisBaseUrl, apisSecured, authBaseUrl, authScopes, authCallBack, authRealm, authCodeRefreshToken, 30, apiName);
    }

    public DefaultJCPClient2(String clientId, String clientSecret,
                             String apisBaseUrl, boolean apisSecured,
                             String authBaseUrl, String authScopes, String authCallBack, String authRealm, String authCodeRefreshToken,
                             int connectionRetrySeconds,
                             String apiName) {
        this.clientId = clientId;
        this.service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .defaultScope(authScopes)
                .callback(authCallBack)
                .build(KeycloakApi.instance("https://" + authBaseUrl, authRealm));
        this.apiName = apiName;
        this.baseUrlAuth = authBaseUrl;
        this.baseUrlAPIs = apisBaseUrl;
        this.securedAPIs = apisSecured;
        if (authCodeRefreshToken != null && !authCodeRefreshToken.isEmpty())
            this.authCode_refreshToken = authCodeRefreshToken;
        this.authRealm = authRealm;
        this.connectionTimerDelaySeconds = connectionRetrySeconds;
        this.state.setStateName(apiName);

        this.defaultHeaders.put("Accept",DEFAULT_ACCEPT);
    }


    // Getter state

    /**
     * {@inheritDoc}
     */
    @Override
    public JCPClient2State getState() {
        return state.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return cliCred_isConnected || authCode_isConnected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnecting() {
        return connectionTimer != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisconnecting() {
        throw new JavaNotImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSessionSet() {
        return sessionId != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastConnection() {
        return lastConnection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastDisconnection() {
        return lastDisconnection;
    }


    // Getter configs

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientId() {
        return clientId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getApiName() {
        return apiName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAPIsUrl() {
        return prepareUrl(false, "/", securedAPIs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAPIsHostname() {
        return urlToHostname(getAPIsUrl());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthUrl() {
        return prepareUrl(true, "/", securedAPIs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthHostname() {
        return urlToHostname(getAuthUrl());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthLoginUrl() {
        return service.getAuthorizationUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthLoginUrl(String redirectUrl) {
        //return service.getAuthorizationUrl() + ...;
        throw new JavaNotImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthLogoutUrl() {
        return prepareUrl(true, getLogoutPath(null), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthLogoutUrl(String redirectUrl) {
        return prepareUrl(true, getLogoutPath(redirectUrl), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthRegistrationUrl() {
        return prepareUrl(true, getRegistrationPath(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthCodeRefreshToken() {
        return authCode_refreshToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHttps() {
        return securedAPIs;
    }


    // Getter state authentication

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserAuthenticated() {
        return isAuthCodeFlowEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserAnonymous() {
        return !isAuthCodeFlowEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClientCredentialFlowEnabled() {
        return !isAuthCodeFlowEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthCodeFlowEnabled() {
        return authCode_refreshToken != null || authCode_loginCode != null;
    }


    // Getters utils

    private String urlToHostname(String url) {
        try {
            String host = new URL(url).getHost();
            InetAddress addr = InetAddress.getByName(host);
            return addr.getHostAddress();

        } catch (UnknownHostException e) {
            return "Unknown";

        } catch (MalformedURLException ignore) {
            assert false;
            return "";
        }
    }

    private String getLogoutPath(String redirectUrl) {
        //https://localhost:8998/auth/realms/jcp/protocol/openid-connect/logout?redirect_uri=https://...
        String url = "/auth/realms/" + authRealm + "/protocol/openid-connect/logout";
        if (redirectUrl != null)
            url += "?redirect_uri=" + redirectUrl;
        return url;
    }

    private String getRegistrationPath() {
        //http://localhost:8998/auth/realms/jcp/protocol/openid-connect/registrations?client_id=<client_id>&response_type=code&scope=openid email&redirect_uri=http://<domain.com>/<redirect-path>&kc_locale=<two-digit-lang-code>
        String url = "/auth/realms/" + authRealm + "/protocol/openid-connect/registrations";
        url += "?client_id=" + clientId;
        url += "&response_type=code";
        //url += "&&scope=openid email";
        url += "&redirect_uri=" + service.getCallback();
        return url;
    }


    // Connection

    @Override
    public void connect() throws StateException, AuthenticationException {
        doConnect();
    }

    @Override
    public void disconnect() throws StateException {
        doDisconnect();
    }


    // JCP Client states manager

    private void doConnect() throws StateException, AuthenticationException {
        if (state.get().isCONNECTED())
            return; // Already done

        else if (state.get().isCONNECTING())
            return; // Already in progress

        else if (state.enumEquals(JCPClient2State.DISCONNECTED))
            initConnection();

        else if (state.enumEquals(JCPClient2State.DISCONNECTING)) {
            if (stopDisconnecting())
                initConnection();
            else
                throw new StateException(String.format("Can't connect %s Client because is disconnecting, try again later", getApiName()));
        }
    }

    private void doDisconnect() throws StateException {
        if (state.get().isCONNECTED())
            closeConnection();

        else if (state.get().isCONNECTING()) {
            if (stopConnecting()) {
                if (state.get().isCONNECTED())
                    closeConnection();
            } else
                throw new StateException(String.format("Can't disconnect %s Client because is connecting, try again later", getApiName()));

        } else if (state.enumEquals(JCPClient2State.DISCONNECTED))
            return; // Already done

        else if (state.enumEquals(JCPClient2State.DISCONNECTING))
            return; // Already in progress
    }

    private void initConnection() throws AuthenticationException {
        assert state.enumEquals(JCPClient2State.DISCONNECTED)
                || state.get().isCONNECTING() :
                "Method initConnection() can be called only from DISCONNECTED or CONNECTING_ state";

        synchronized (state) {
            if (!state.get().isCONNECTING())
                state.set(JCPClient2State.CONNECTING);

            try {
                checkServerReachability(false, Paths20.FULL_PATH_EXEC_ONLINE);

            } catch (JCPNotReachableException e) {
                if (state.enumNotEquals(JCPClient2State.CONNECTING_WAITING_JCP)) {
                    log.warn(String.format("JCP Client '%s' can't connect, start JCP Client connection timer", getApiName()));
                    state.set(JCPClient2State.CONNECTING_WAITING_JCP);
                    startConnectionTimer();
                }
                emitConnectionFailed(e);
                return;
            }

            try {
                checkServerReachability(true, "/auth/realms/jcp/.well-known/openid-configuration");

            } catch (JCPNotReachableException e) {
                if (state.enumNotEquals(JCPClient2State.CONNECTING_WAITING_AUTH)) {
                    log.warn(String.format("JCP Client '%s' can't connect, start JCP Client connection timer", getApiName()));
                    state.set(JCPClient2State.CONNECTING_WAITING_AUTH);
                    startConnectionTimer();
                }
                emitConnectionFailed(e);
                return;
            }

            if (isClientCredentialFlowEnabled()) {
                try {
                    initAccessTokenCliCredFlow();
                } catch (AuthenticationException e) {
                    initConnectionException(e);
                    throw new AuthenticationException(String.format("Client '%s' can't authenticate to %s (Exception on get access token via Client Credential Flow: %s)", clientId, apiName, e), e);
                } catch (ConnectionException ignore) {}

            } else if (isAuthCodeFlowEnabled()) {
                try {
                    try {
                        initAccessTokenAuthCodeFlow();
                    } catch (ConnectionException ignore) {
                    }

                } catch (AuthenticationException e) {
                    initConnectionException(e);
                    throw new AuthenticationException(String.format("Client '%s' can't authenticate to %s (Exception on get access token via Auth Code Flow: %s)", clientId, apiName, e), e);
                }
            }

            stopConnectionTimer();
            startConnectionCheckTimer();
            lastConnection = JavaDate.getNowDate();
            emitConnected();
        }

    }

    private boolean refreshConnection() {
        if (isClientCredentialFlowEnabled())
            return refreshAccessTokenCliCredFlow();

        if (isAuthCodeFlowEnabled())
            return refreshAccessTokenAuthCodeFlow();

        return false;
    }

    private void initConnectionException(AuthenticationException e) {
        state.set(JCPClient2State.DISCONNECTED);
        authCode_refreshToken = null;
        authCode_loginCode = null;
        emitAuthenticationFailed(e);
        //try {
        //    initConnection();
        //} catch (AuthenticationException ignore) {
        //    /* No authentication in initConnection() because reset all auth fields */
        //}
    }

    private boolean stopConnecting() {
        assert state.get().isCONNECTING() :
                "Method stopConnecting() can be called only from CONNECTING_ state";

        // If connecting (1st attempt)
        if (state.enumEquals(JCPClient2State.CONNECTING)) {
            JavaThreads.softSleep(1000);
            return !state.get().isCONNECTING();
        }

        synchronized (state) {
            // Clean up connection waiting stuff
            if (state.enumEquals(JCPClient2State.CONNECTING_WAITING_JCP)) {
                log.warn(String.format("JCP Client '%s' disconnect, stop JCP Client connection timer", getApiName()));
                stopConnectionTimer();
            }
            if (state.enumEquals(JCPClient2State.CONNECTING_WAITING_AUTH)) {
                log.warn(String.format("JCP Client '%s' disconnect, stop JCP Client connection timer", getApiName()));
                stopConnectionTimer();
            }

            state.set(JCPClient2State.DISCONNECTED);
            return true;
        }
    }

    private void closeConnection() {
        assert state.get().isCONNECTED() :
                "Method closeConnection() can be called only from CONNECTED_ state";

        synchronized (state) {
            state.set(JCPClient2State.DISCONNECTING);

            cliCred_isConnected = false;
            authCode_isConnected = false;
            accessToken = null;

            stopConnectionCheckTimer();

            state.set(JCPClient2State.DISCONNECTED);
            lastDisconnection = JavaDate.getNowDate();
            emitDisconnected();
        }
    }

    private boolean stopDisconnecting() {
        assert state.enumEquals(JCPClient2State.DISCONNECTING) :
                "Method stopDisconnecting() can be called only from DISCONNECTING state";

        // If disconnecting (1st attempt)
        JavaThreads.softSleep(1000);
        return state.enumNotEquals(JCPClient2State.DISCONNECTING);
    }

    private boolean checkConnection() {
        assert state.get().isCONNECTED() :
                "Method checkConnection() can be called only from CONNECTED_ state";

        try {
            checkServerReachability(false, Paths20.FULL_PATH_EXEC_ONLINE);
            checkServerReachability(true, "/auth/realms/jcp/.well-known/openid-configuration");
            return true;

        } catch (JCPNotReachableException ignore) {
        }

        cliCred_isConnected = false;
        authCode_isConnected = false;
        accessToken = null;

        try {
            doDisconnect();
            doConnect();
        } catch (StateException | AuthenticationException e) {
            log.warn(String.format("JCP Client '%s' can't reconnect because %s", getApiName(), e.getMessage()), e);
        }

        return false;
    }

    private void checkServerReachability(boolean toAuth, String path) throws JCPNotReachableException {
        try {
            String urlString = prepareUrl(toAuth, path, securedAPIs);
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int code;
            try {
                code = con.getResponseCode();
            } catch (SSLHandshakeException ei) {
                try {
                    JavaSSLIgnoreChecks.disableSSLChecksAndHostVerifierOnLocalHost();
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    code = con.getResponseCode();

                } catch (SSLHandshakeException e1) {
                    throw new JCPNotReachableException(String.format("Error connecting to %s because SSL handshaking failed", apiName));
                } catch (IOException e1) {
                    if (e1.getMessage().startsWith("HTTPS hostname wrong:  should be ")) {
                        JavaSSLIgnoreChecks.disableSSLChecksAndHostVerifierOnAllHost();
                        con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");
                        code = con.getResponseCode();
                    } else
                        throw e1;
                }
            }
            if (code != 200) {
                String errMsg = String.format("Error connecting to %s because '%s%s' (%s) returned '%d' code", apiName, toAuth ? baseUrlAuth : baseUrlAPIs, path, toAuth ? "Auth's url" : "APIs's url", code);
                throw new JCPNotReachableException(errMsg);
            }

        } catch (IOException e) {
            String errMsg = String.format("Error connecting to %s because '%s%s' (%s) not reachable [%s:%s]", apiName, toAuth ? baseUrlAuth : baseUrlAPIs, path, toAuth ? "Auth's url" : "APIs's url", e.getClass().getSimpleName(), e.getMessage());
            throw new JCPNotReachableException(errMsg);
        }
    }

    private void initAccessTokenCliCredFlow() throws ConnectionException, AuthenticationException {
        try {
            try {
                accessToken = service.getAccessTokenClientCredentialsGrant();
                log.debug(String.format("JCP Client '%s' authenticated via CliCred flow.", getApiName()));

            } catch (SSLHandshakeException e) {

                try {
                    JavaSSLIgnoreChecks.disableSSLChecksAndHostVerifierOnLocalHost();
                    accessToken = service.getAccessTokenClientCredentialsGrant();
                    log.debug(String.format("JCP Client '%s' authenticated via CliCred flow (SSL localhost checks disabled).", getApiName()));

                } catch (SSLHandshakeException e1) {
                    throw new ConnectionException(String.format("Error connecting to %s because SSL handshaking failed (%s)", getApiName(), e.getMessage()), e1);
                }
            }

        } catch (OAuth2AccessTokenErrorResponse e) {
            throw new AuthenticationException(String.format("Error connecting to %s because authentication error for client ([%s] %s)", getApiName(), e.getClass().getSimpleName(), e.getMessage()), e);

        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new ConnectionException(String.format("Error connecting to %s because can't get the access token for Client Credentials flow ([%s] %s)", getApiName(), e.getClass().getSimpleName(), e.getMessage()), e);
        }


        cliCred_refreshToken = accessToken.getRefreshToken();
        cliCred_isConnected = true;

        authCode_isConnected = false;
        authCode_refreshToken = null;
        authCode_loginCode = null;
        state.set(JCPClient2State.CONNECTED_ANONYMOUS);
    }

    private void initAccessTokenAuthCodeFlow() throws ConnectionException, AuthenticationException {
        if (authCode_loginCode == null && authCode_refreshToken == null)
            throw new AuthenticationException(String.format("Error connecting to %s because Login Code nor Refresh Token were set for Auth Code flow", getApiName()));

        try {
            try {
                accessToken = authCode_loginCode != null
                        ? service.getAccessToken(authCode_loginCode)
                        : service.refreshAccessToken(authCode_refreshToken);

            } catch (SSLHandshakeException e) {

                try {
                    JavaSSLIgnoreChecks.disableSSLChecksAndHostVerifierOnLocalHost();
                    accessToken = authCode_loginCode != null
                            ? service.getAccessToken(authCode_loginCode)
                            : service.refreshAccessToken(authCode_refreshToken);

                } catch (SSLHandshakeException e1) {
                    throw new ConnectionException(String.format("Error connecting to %s because SSL handshaking failed (%s)", getApiName(), e.getMessage()), e1);
                }
            }

        } catch (OAuth2AccessTokenErrorResponse e) {
            String exMessage = e.getMessage();
            if (exMessage.contains("Incorrect redirect_uri"))
                exMessage = String.format("Incorrect redirect_uri (%s)", service.getCallback());
            String method = String.format("get access token with auth code flow (%s)", authCode_loginCode!=null ? "loginCode" : "refreshToken");
            throw new AuthenticationException(String.format("Error connecting to %s (%s) because %s", getApiName(), method, exMessage), e);

        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new ConnectionException(String.format("Error connecting to %s because can't get the access token for Auth Code flow because %s", getApiName(), e.getMessage()), e);
        }

        authCode_refreshToken = accessToken.getRefreshToken();
        authCode_isConnected = true;
        authCode_loginCode = null;
        emitLoggedIn();

        cliCred_isConnected = false;
        cliCred_refreshToken = null;
        state.set(JCPClient2State.CONNECTED_LOGGED);
    }

    private boolean refreshAccessTokenCliCredFlow() {
        if (cliCred_refreshToken==null)
            return false;

        try {
            accessToken = service.refreshAccessToken(cliCred_refreshToken);
            log.debug(String.format("JCP Client '%s' refreshed access token via CliCred flow.", getApiName()));

        } catch (OAuth2AccessTokenErrorResponse | IOException | InterruptedException | ExecutionException ignore) {
            return false;
        }

        cliCred_refreshToken = accessToken.getRefreshToken();
        return true;
    }

    private boolean refreshAccessTokenAuthCodeFlow() {
        if (authCode_refreshToken==null)
            return false;

        try {
            accessToken = service.refreshAccessToken(authCode_refreshToken);
            log.debug(String.format("JCP Client '%s' refreshed access token via AuthCode flow.", getApiName()));

        } catch (OAuth2AccessTokenErrorResponse | IOException | InterruptedException | ExecutionException ignore) {
            return false;
        }

        authCode_refreshToken = accessToken.getRefreshToken();
        return true;
    }



    // JCP re-connection timer

    private void startConnectionTimer() {
        assert state.enumEquals(JCPClient2State.CONNECTING_WAITING_JCP)
                || state.enumEquals(JCPClient2State.CONNECTING_WAITING_AUTH) :
                "Method startConnectionTimer() can be called only from CONNECTING_WAITING_JCP or CONNECTING_WAITING_AUTH state; current state " + state.get();

        connectionTimer = JavaTimers.initAndStart(new ReConnectionTimer(),true,String.format(TH_CONNECTION_NAME, apiName.toUpperCase()),Integer.toString(this.hashCode()),connectionTimerDelaySeconds * 1000,connectionTimerDelaySeconds * 1000);
    }

    private void stopConnectionTimer() {
        if (connectionTimer == null) return;

        JavaTimers.stopTimer(connectionTimer);
        connectionTimer = null;
    }

    private class ReConnectionTimer implements Runnable {

        @Override
        public void run() {
            try {
                initConnection();
            } catch (AuthenticationException ignore) {
            }
        }

    }


    // JCP connection check timer

    private void startConnectionCheckTimer() {
        if (!isConnected())
            return;

        connectionCheckTimer = JavaTimers.initAndStart(new CheckConnectionTimer(),true,String.format(TH_CONNECTION_CHECK_NAME, apiName.toUpperCase()),Integer.toString(this.hashCode()),connectionTimerDelaySeconds * 1000,connectionTimerDelaySeconds * 1000);
    }

    private void stopConnectionCheckTimer() {
        if (connectionCheckTimer == null) return;

        JavaTimers.stopTimer(connectionCheckTimer);
        connectionCheckTimer = null;
    }

    private class CheckConnectionTimer implements Runnable {

        @Override
        public void run() {
            if (!checkConnection()) {
                log.trace(String.format("Stopped CheckConnectionTimer for JCP Client '%s'", getApiName()));
                stopConnectionCheckTimer();
            }
        }

    }


    // Login

    @Override
    public void setLoginCode(String loginCode) {
        authCode_loginCode = loginCode;
    }

    @Override
    public void userLogout() {
        if (!isUserAuthenticated())
            return;

        try {
            execReq(true, Verb.GET, getLogoutPath(null), true);
        } catch (ConnectionException | AuthenticationException | RequestException | ResponseException e) {
            e.printStackTrace();
        }

        cleanSession();

        try {
            disconnect();
        } catch (StateException ignore) {
        }
        authCode_refreshToken = null;
        emitLoggedOut();

        try {
            connect();
        } catch (StateException ignore) {
            assert false : "StateException should NOT be throw calling connect() after disconnect()";
        } catch (AuthenticationException ignore) {
            assert false : "AuthenticationException should NOT be throw after clean user and session data";
        }
    }


    // Connection listeners

    @Override
    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    @Override
    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    private void emitConnected() {
        List<JCPClient2.ConnectionListener> tmpList = new ArrayList<>(connectionListeners);
        for (ConnectionListener l : tmpList)
            l.onConnected(this);
    }

    private void emitConnectionFailed(Throwable t) {
        List<ConnectionListener> tmpList = new ArrayList<>(connectionListeners);
        for (ConnectionListener l : tmpList)
            l.onConnectionFailed(this, t);
    }

    private void emitAuthenticationFailed(Throwable t) {
        List<ConnectionListener> tmpList = new ArrayList<>(connectionListeners);
        for (ConnectionListener l : tmpList)
            l.onAuthenticationFailed(this, t);
    }

    private void emitDisconnected() {
        List<ConnectionListener> tmpList = new ArrayList<>(connectionListeners);
        for (ConnectionListener l : tmpList)
            l.onDisconnected(this);
    }


    // Login listeners

    @Override
    public void addLoginListener(LoginListener listener) {
        loginListeners.add(listener);
    }

    @Override
    public void removeLoginListener(LoginListener listener) {
        loginListeners.remove(listener);
    }

    private void emitLoggedIn() {
        List<LoginListener> tmpList = new ArrayList<>(loginListeners);
        for (LoginListener l : tmpList)
            l.onLogin(this);
    }

    private void emitLoggedOut() {
        List<LoginListener> tmpList = new ArrayList<>(loginListeners);
        for (LoginListener l : tmpList)
            l.onLogout(this);
    }


    // Headers and sessions

    @Override
    public void addDefaultHeader(String headerName, String headerValue) {
        defaultHeaders.put(headerName, headerValue);
    }

    @Override
    public void removeDefaultHeader(String headerName) {
        defaultHeaders.remove(headerName);
    }

    private void injectDefaultHeaders(OAuthRequest request) {
        for (Map.Entry<String, String> h : defaultHeaders.entrySet())
            request.addHeader(h.getKey(), h.getValue());
    }

    private void injectSession(OAuthRequest request) {
        if (sessionId != null)
            request.addHeader(HEAD_COOKIE, sessionId);
    }

    private void storeSession(Response response) {
        String setCookie = response.getHeader(HEAD_SET_COOKIE);
        if (setCookie == null)
            return;

        if (setCookie.contains(SESSION_KEY))
            sessionId = setCookie;
    }

    private void cleanSession() {
        sessionId = null;
    }


    // Exec requests

    @Override
    public void execReq(Verb reqType, String path) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(false, reqType, path, null, new HashMap<>(), false);
    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(toAuth, reqType, path, null, new HashMap<>(), false);
    }

    @Override
    public void execReq(Verb reqType, String path, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(false, reqType, path, null, new HashMap<>(), secure);
    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(toAuth, reqType, path, null, new HashMap<>(), secure);
    }

    @Override
    public void execReq(Verb reqType, String path, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(false, reqType, path, null, params, secure);
    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(toAuth, reqType, path, null, params, secure);
    }

    @Override
    public void execReq(Verb reqType, String path, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(false, reqType, path, null, objParam, secure);
    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        execReq(toAuth, reqType, path, null, objParam, secure);
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        return execReq(false, reqType, path, reqObject, new HashMap<>(), secure);
    }

    @Override
    public <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        return execReq(toAuth, reqType, path, reqObject, new HashMap<>(), secure);
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        return execReq(false, reqType, path, reqObject, (Object) params, secure);
    }

    @Override
    public <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, Map<String, String> params, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        return execReq(toAuth, reqType, path, reqObject, (Object) params, secure);
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        return execReq(false, reqType, path, reqObject, objParam, secure);
    }

    @Override
    public <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, Object objParam, boolean secure) throws ConnectionException, AuthenticationException, RequestException, ResponseException {
        String fullUrl = prepareUrl(toAuth, path, secure);
        if (!isConnected())
            throw new ConnectionException(String.format("Error on exec request '[%s] %s' because not connected to %s", reqType, fullUrl, apiName));

        if (reqType == Verb.GET) {
            if (objParam instanceof Map)
                //noinspection unchecked
                path = prepareGetPath(path, (Map<String, String>) objParam);
            else
                throw new RequestException(String.format("Error on exec request '[%s] %s' because GET request must give a Map<String,String> as parameter (get %s)", reqType, fullUrl, objParam.getClass().getSimpleName()));
        }

        OAuthRequest request;
        Response response;
        if (reqType == Verb.GET || objParam == null)
            request = prepareRequest(toAuth, reqType, path, secure);
        else
            request = prepareRequest(reqType, path, objParam, secure);

        injectDefaultHeaders(request);
        injectSession(request);

        String responseBody;
        try {
            response = service.execute(request);

            if (response.getCode() == 401) {
                log.warn(String.format("JCP Client '%s' unauthorized, refresh it.", getApiName()));

                if (!refreshConnection()) {
                    log.warn(String.format("JCP Client '%s' refresh failed, re-connect.", getApiName()));
                    try {
                        disconnect();

                    } catch (StateException ignore) {}

                    try {
                        try {
                            connect();

                        } catch (AuthenticationException e) {
                            if (!isAuthCodeFlowEnabled())
                                throw e;

                            authCode_refreshToken = null;
                            authCode_loginCode = null;

                            connect();
                        }
                    } catch (StateException ignore) {}

//
//                    // Get new access token with new authentication process
//                    if (isClientCredentialFlowEnabled())
//                        initAccessTokenCliCredFlow();
//
//                    else if (isAuthCodeFlowEnabled()) {
//                        emitLoggedOut();
//                        log.debug(String.format("JCP Client '%s' de-authenticated, re-authenticate via AuthCode flow.", getApiName()));
//                        try {
//                            initAccessTokenAuthCodeFlow();
//
//                        } catch (AuthenticationException e) {
//
//                            // AuthCode logout but ClientCredential connected
//                            log.debug(String.format("JCP Client '%s' can't re-authenticate via AuthCode flow because '%s'", getApiName(), e));
//                            initAccessTokenCliCredFlow();
//                        }
//                    }
                }

                service.signRequest(accessToken, request);
                response = service.execute(request);
            }

            storeSession(response);
            if (response.getCode() != 200)
                throwErrorCodes(request, response);

            if (reqObject != null && reqObject.isInstance(response))
                return reqObject.cast(response);
            responseBody = response.getBody();

        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RequestException(String.format("Error on exec [%s] request @ '%s' because %s", reqType, request.getUrl(), e.getMessage()), e);
        }

        if (reqObject == null)
            return null;
        String body = trimBody(responseBody);
        if (reqObject.equals(String.class))
            //noinspection unchecked
            return (T) body;

        return parseJSON(body, reqObject, fullUrl);
    }

    private String prepareGetPath(String path, Map<String, String> params) {
        if (params.size() == 0)
            return path;

        StringBuilder fullUrl = new StringBuilder(path + "?");
        for (Map.Entry<String, String> pair : params.entrySet())
            fullUrl.append(pair.getKey()).append("=").append(pair.getValue()).append("&");

        return fullUrl.toString();
    }

    private String prepareUrl(boolean toAuth, String path, boolean secure) {
        String fullUrl = !secure && !toAuth ? "http://" : "https://";
        fullUrl += toAuth ? baseUrlAuth : baseUrlAPIs;
        fullUrl += path;
        return fullUrl;
    }

    private OAuthRequest prepareRequest(boolean toAuth, Verb reqType, String path, boolean secure) {
        String fullUrl = prepareUrl(toAuth, path, secure);
        OAuthRequest req = new OAuthRequest(reqType, fullUrl);
        service.signRequest(accessToken, req);
        return req;
    }

    private OAuthRequest prepareRequest(Verb reqType, String path, Object param, boolean secure) throws RequestException {
        String fullUrl = secure ? "https://" : "http://";
        fullUrl += baseUrlAPIs + path;
        OAuthRequest req = new OAuthRequest(reqType, fullUrl);
        req.addHeader("Content-Type", "application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        try {
            req.setPayload(mapper.writeValueAsString(param));

        } catch (JsonProcessingException e) {
            throw new RequestException(String.format("Error on prepare request '[%s] %s' because can't serialize param to json (%s)", reqType, path, e.getMessage()), e);
        }

        service.signRequest(accessToken, req);
        return req;
    }

    private void throwErrorCodes(OAuthRequest request, Response response) throws ResponseException {
        int code = response.getCode();
        String fullUrl = request.getUrl();

        switch (code) {
            case 200:
                break;
            case 400:
                throw new BadRequest_400(fullUrl);
            case 401:
                if (response.getCode() == 401 && response.getHeader("WWW-Authenticate").contains("invalid_token"))
                    throw new Unauthorized_401(fullUrl, response, request.getHeaders().get("Authorization").substring("Bearer ".length()));
                else
                    throw new Unauthorized_401(fullUrl, response);
            case 403:
                throw new NotAuthorized_403(fullUrl);
            case 404:
                throw new NotFound_404(fullUrl);
            case 409:
                throw new Conflict_409(fullUrl);
            default:
                try {
                    throw new Error_Code(fullUrl, code, response.getBody());
                } catch (IOException e) {
                    throw new Error_Code(fullUrl, code, "");
                }
        }
    }

    private String trimBody(String body) {
        if (body.startsWith("\""))
            body = body.substring(1);
        if (body.endsWith("\""))
            body = body.substring(0, body.length() - 1);
        return body.trim();
    }

    private <T> T parseJSON(String body, Class<T> reqObject, String fullUrl) throws ResponseParsingException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(body, reqObject);

        } catch (IOException e) {
            throw new ResponseParsingException(fullUrl, e);
        }
    }


    // Static copy method

    public static void copyCredentials(DefaultJCPClient2 src, DefaultJCPClient2 dest) {
        // Configs
        //dest.clientId = src.clientId;         // FINAL
        //dest.apiName = src.apiName;           // FINAL
        //dest.baseUrlAuth = src.baseUrlAuth;   // FINAL
        //dest.baseUrlAPIs = src.baseUrlAPIs;   // FINAL
        //dest.securedAPIs = src.securedAPIs;   // FINAL
        //dest.authRealm = src.authRealm;       // FINAL
        //dest.connectionTimerDelaySeconds = src.connectionTimerDelaySeconds;           // FINAL and NOT related with credentials
        // Listeners
        //dest.connectionListeners = src.connectionListeners;   // FINAL and NOT related with credentials
        //dest.loginListeners = src.loginListeners;             // FINAL and NOT related with credentials
        // Connection timers
        //dest.connectionTimer = src.connectionTimer;           // NOT related with credentials
        //dest.connectionCheckTimer = src.connectionCheckTimer; // NOT related with credentials
        // OAuth
        //dest.service = src.service;           // FINAL
        dest.accessToken = src.accessToken;
        dest.cliCred_isConnected = src.cliCred_isConnected;
        dest.cliCred_refreshToken = src.cliCred_refreshToken;
        dest.authCode_isConnected = src.authCode_isConnected;
        dest.authCode_refreshToken = src.authCode_refreshToken;
        dest.authCode_loginCode = src.authCode_loginCode;
        // Headers and sessions
        dest.defaultHeaders.putAll(src.defaultHeaders);
        dest.sessionId = src.sessionId;
    }

}
