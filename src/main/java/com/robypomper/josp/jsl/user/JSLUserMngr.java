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


import com.robypomper.josp.jsl.comm.JSLCommunication;

/**
 * Interface for JSL User management system.
 * <p>
 * This system allow to manage current user: get user info and access to his
 * preferences.
 * <p>
 * Before a service can access to user info and preference, it must require the
 * permission to the user. This class allow service to require this permissions.
 *
 * <b>NB!</b>: user must be logged in/out via
 * {@link com.robypomper.josp.clients.JCPAPIsClientSrv}
 */
public interface JSLUserMngr {

    // User's info

    /**
     * Check to {@link com.robypomper.josp.clients.JCPAPIsClientSrv} if
     * current service authenticated user or not.
     *
     * @return <code>true</code> if current service authenticated user with
     * user's login.
     */
    boolean isUserAuthenticated();

    boolean isAdmin();

    boolean isMaker();

    boolean isDeveloper();

    /**
     * The logged user ID.
     *
     * @return logged user ID, if user was not logged then it return
     * <code>null</code>.
     */
    String getUserId();

    /**
     * The logged user ID.
     *
     * @return logged user ID, if user was not logged then it return
     * <code>null</code>.
     */
    String getUsername();


    void setCommunication(JSLCommunication comm);


    // User's settings

    // ToDo: Implements user's specific settings (local/cloud)
    // String getUserSetting(String key);


    // User/Service settings

    // ToDo: Implements user's related to current service settings (local/cloud)
    // String getUserSrvSetting(String key);


    // User events

    void addUserListener(JSLUserMngr.UserListener listener);

    void removeUserListener(JSLUserMngr.UserListener listener);

    interface UserListener {

        void onLoginPreRestart(JSLUserMngr jslUserMngr);

        void onLogoutPreRestart(JSLUserMngr jslUserMngr);

        void onLogin(JSLUserMngr jslUserMngr);

        void onLogout(JSLUserMngr jslUserMngr);

    }

}
