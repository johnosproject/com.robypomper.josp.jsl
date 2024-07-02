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

package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.states.StateException;

import java.util.Scanner;

public class CmdsJCPClient {

    private final JCPAPIsClientSrv jcpClient;

    public CmdsJCPClient(JCPAPIsClientSrv jcpClient) {
        this.jcpClient = jcpClient;
    }

    // Client connection

    /**
     * Checks and print JCP Client status.
     * <p>
     * The returned string include also if the user is logged or not.
     *
     * @return a pretty printed string containing the JCP client status.
     */
    @Command(description = "Print JCP Client status.")
    public String jcpClientStatus() {
        String s = jcpClient.isConnected() ? "JCP Client is connect" : "JCP Client is NOT connect";
        s += " ";
        s += jcpClient.isAuthCodeFlowEnabled() ? "(user logged in)." : "(user not logged).";
        return s;
    }

    /**
     * Connect the JCP Client to the JCP Cloud.
     * <p>
     * The method called start the authentication process, if it terminate
     * successfully then the client result connected, otherwise it result as
     * disconnected.
     * <p>
     * It always authenticate the service with Client Credential flow. To the
     * otherside, it authenticate also the user with Authentication Code flow
     * only if the refresh token or authentication token are set.
     *
     * @return a string indicating if the client connected successfully or not.
     */
    @Command(description = "Connect JCP Client.")
    public String jcpClientConnect() {
        if (jcpClient.isConnected())
            return "JCP Client already connected.";

        try {
            jcpClient.connect();

        } catch (StateException | JCPClient2.AuthenticationException e) {
            return String.format("Error on JCP Client connection: %s.", e.getMessage());
        }
        return "JCP Client connected successfully.";
    }

    /**
     * Disconnect the JCP Client to the JCP Cloud.
     * <p>
     * The method called reset all client's connection reference.
     * <p>
     * It always disconnect the service authenticated with Client Credential
     * flow. Meanwhile, the Authentication Code flow is disconnected only if it
     * was previously connected.
     *
     * @return a string indicating if the client connected successfully or not.
     */
    @Command(description = "Disconnect JCP Client.")
    public String jcpClientDisconnect() {
        if (!jcpClient.isConnected())
            return "JCP Client already disconnected.";

        try {
            jcpClient.disconnect();

        } catch (StateException e) {
            return String.format("Error on JCP Client disconnection: %s.", e.getMessage());
        }

        return "JCP Client disconnected successfully.";
    }


    // User login

    /**
     * With this method users can login to JCP cloud thought current service.
     * <p>
     * It print the logging url, that must be visited by the user. Then after
     * the user perform the authentication, in the url bar he can find the
     * authorization code (the authentication server redirect to an url
     * containing the code as param). Finally the user must copy that code in the
     * service console.
     *
     * <b>NB:</b> this method trigger the <code>JCPAPIsClientSrv.LoginManager#onLogin()</code>
     * event, handled by <code>JSLUserMngr_002#onLogin()</code> method.
     *
     * @return a string indicating if the user was logged in successfully or not.
     */
    @Command(description = "Login user to JCP Client.")
    public String jcpUserLogin() {
        if (jcpClient.isAuthCodeFlowEnabled())
            return "User already logged in.";

        final Scanner in = new Scanner(System.in);

        String url = jcpClient.getAuthLoginUrl();
        System.out.println("Please open following url and login to JCP Cloud");
        System.out.println(url);
        System.out.println("then paste the redirected url 'code' param");
        System.out.print("<<");
        String code = in.nextLine();


        try {
            jcpClient.setLoginCodeAndReconnect(code);

        } catch (StateException | JCPClient2.AuthenticationException e) {
            return String.format("Can't proceed with user login because %s", e.getMessage());
        }

        return "User logged in successfully";
    }

    /**
     * With this method users can logout to JCP cloud thought current service.
     *
     * <b>NB:</b> this method trigger the <code>JCPAPIsClientSrv.LoginManager#onLogout()</code>
     * event, handled by <code>JSLUserMngr_002#onLogout()</code> method.
     *
     * @return a string indicating if the user was logged out successfully or not.
     */
    @Command(description = "Logout user to JCP Client.")
    public String jcpUserLogout() {
        if (!jcpClient.isAuthCodeFlowEnabled())
            return "User already logged out.";

        try {
            jcpClient.userLogout();
        } catch (JCPClient2.AuthenticationException |
                 JCPClient2.ResponseException | JCPClient2.RequestException |
                 JCPClient2.ConnectionException e) {
            return String.format("Can't proceed with user logout because %s", e.getMessage());
        }
        return "User logged out successfully";
    }

}
