/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2024 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.robypomper.josp.jsl.user;

import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.discovery.Discover;
import com.robypomper.josp.callers.apis.core.users.Caller20;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.defs.core.users.Params20;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.states.StateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class JSLUserMngr_002 implements JSLUserMngr, JCPClient2.LoginListener {

    // Class constants

    public static final String ANONYMOUS_ID = JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString();
    public static final String ANONYMOUS_USERNAME = JOSPPerm.WildCards.USR_ANONYMOUS_NAME.toString();


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLUserMngr_002.class);
    private final JSLSettings_002 locSettings;
    private final JCPAPIsClientSrv jcpClient;
    private final Caller20 apiUsrsCaller;
    private Params20.User user;
    private JSLCommunication comm = null;
    // Listeners
    private final List<JSLUserMngr.UserListener> userListeners = new ArrayList<>();


    // Constructor

    public JSLUserMngr_002(JSLSettings_002 settings, JCPAPIsClientSrv jcpClient) {
        this.locSettings = settings;
        this.jcpClient = jcpClient;
        apiUsrsCaller = new Caller20(jcpClient);

        if (jcpClient.isAuthCodeFlowEnabled()) {
            log.trace("Perform JSLUserMngr login");
            if (jcpClient.isConnected())
                onLogin(jcpClient);
            else
                onLocalLogin();
        } else {
            onLogout(jcpClient);
            log.trace("Set JSLUserMngr with anonymous user");
        }

        log.debug("Setting login manager to JCPClient");
        this.jcpClient.addLoginListener(this);
        log.debug("Login manager set to JCPClient");

        log.info(String.format("Initialized JSLUserMngr instance for '%s' user with '%s' id", getUsername(), getUserId()));
    }


    // User's info

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserAuthenticated() {
        return jcpClient.isAuthCodeFlowEnabled() && !user.usrId.equals(ANONYMOUS_ID)
                && user.authenticated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAdmin() {
        return isUserAuthenticated() && user.admin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMaker() {
        return isUserAuthenticated() && user.maker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDeveloper() {
        return isUserAuthenticated() && user.developer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserId() {
        return user != null ? user.usrId : ANONYMOUS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return user != null ? user.username : ANONYMOUS_USERNAME;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setCommunication(JSLCommunication communication) {
        this.comm = communication;
    }


    // User events

    @Override
    public void addUserListener(JSLUserMngr.UserListener listener) {
        userListeners.add(listener);
    }

    @Override
    public void removeUserListener(JSLUserMngr.UserListener listener) {
        userListeners.add(listener);
    }

    private void _emitLoggedIn_PreRestart() {
        List<JSLUserMngr.UserListener> tmpList = new ArrayList<>(userListeners);
        for (JSLUserMngr.UserListener l : tmpList)
            l.onLoginPreRestart(this);
    }

    private void _emitLoggedOut_PreRestart() {
        List<JSLUserMngr.UserListener> tmpList = new ArrayList<>(userListeners);
        for (JSLUserMngr.UserListener l : tmpList)
            l.onLogoutPreRestart(this);
    }

    private void _emitLoggedIn() {
        List<JSLUserMngr.UserListener> tmpList = new ArrayList<>(userListeners);
        for (JSLUserMngr.UserListener l : tmpList)
            l.onLogin(this);
    }

    private void _emitLoggedOut() {
        List<JSLUserMngr.UserListener> tmpList = new ArrayList<>(userListeners);
        for (JSLUserMngr.UserListener l : tmpList)
            l.onLogout(this);
    }


    // LoginManager impl

    @Override
    public void onLogin(JCPClient2 jcpClient2) {
        // Cache user's info
        log.debug("Caching user's info from JCP");
        try {
            this.user = apiUsrsCaller.getCurrent();
            locSettings.setUsrId(getUserId());
            locSettings.setUsrName(getUsername());

            // Set JCP Client user id header
            jcpClient.setUserId(getUserId());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(String.format("Error on getting user id and name from JCP because %s", e.getMessage()), e);
            log.trace("Set anonymous user");
            user = Params20.User.ANONYMOUS;
        }

        log.info(String.format("Logged in user '%s' with id '%s'", getUsername(), getUserId()));
        _emitLoggedIn_PreRestart();

        if (comm == null)
            return;

        if (comm.getCloudConnection().getState().isConnected()) {
            try {
                comm.getCloudConnection().disconnect();
            } catch (PeerDisconnectionException e) {
                log.warn(String.format("Error on shutdown cloud communication on updating user id because %s", e.getMessage()), e);
            }
            try {
                comm.getCloudConnection().connect();
            } catch (PeerConnectionException e) {
                log.warn(String.format("Error on starting cloud communication on updating user id because %s", e.getMessage()), e);
            }
        }
        if (comm.getLocalConnections().isRunning()) {
            try {
                comm.getLocalConnections().stop();
                comm.getLocalConnections().start();

            } catch (StateException | Discover.DiscoveryException e) {
                log.warn(String.format("Error on restart local communication on updating user id because %s", e.getMessage()), e);
            }
        }

        _emitLoggedIn();
    }

    @Override
    public void onLogout(JCPClient2 jcpClient2) {
        String loggedUsrId = getUserId();
        String loggedUsername = getUsername();

        user = Params20.User.ANONYMOUS;
        locSettings.setUsrId(null);
        locSettings.setUsrName(null);

        // Set JCP Client user id header
        jcpClient.setUserId(null);

        log.info(String.format("Logged out user '%s' with id '%s'", loggedUsername, loggedUsrId));
        _emitLoggedOut_PreRestart();

        if (comm == null)
            return;

        if (comm.getCloudConnection().getState().isConnected()) {
            try {
                comm.getCloudConnection().disconnect();
            } catch (PeerDisconnectionException e) {
                log.warn(String.format("Error on shutdown cloud communication on updating user id because %s", e.getMessage()), e);
            }
            try {
                comm.getCloudConnection().connect();
            } catch (PeerConnectionException e) {
                log.warn(String.format("Error on starting cloud communication on updating user id because %s", e.getMessage()), e);
            }
        }
        if (comm.getLocalConnections().isRunning()) {
            try {
                comm.getLocalConnections().stop();
                comm.getLocalConnections().start();

            } catch (StateException | Discover.DiscoveryException e) {
                log.warn(String.format("Error on restart local communication on updating user id because %s", e.getMessage()), e);
            }
        }

        _emitLoggedOut();
    }

    /**
     * Method to handle the local user login.
     * <p>
     * This method is called by {@link JSLUserMngr_002} constructor when user is
     * already logged but the JCP client is not connected.
     * <p>
     * It read user's id and username from local settings.
     */
    private void onLocalLogin() {
        // Cache user's info
        log.debug("Set user's info from settings");
        user = new Params20.User();
        user.usrId = locSettings.getUsrId();
        user.username = locSettings.getUsrName();

        // Set JCP Client user id header
        jcpClient.setUserId(user.usrId);

        log.info(String.format("Logged in user '%s' with id '%s'", user.username, user.usrId));

        _emitLoggedIn();
    }

}
