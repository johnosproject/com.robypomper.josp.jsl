/*******************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
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

package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.states.StateException;

public class CmdsJCPClient {

    private final JCPClient2 jcpClient;

    public CmdsJCPClient(JCPClient2 jcpClient) {
        this.jcpClient = jcpClient;
    }


    @Command(description = "Print JCP Client status.")
    public String jcpClientStatus() {
        return jcpClient.isConnected() ? "JCP Client is connect." : "JCP Client is NOT connect.";
    }

    @Command(description = "Connect JCP Client.")
    public String jcpClientConnect() {
        if (jcpClient.isConnected())
            return "JCP Client already connected.";

        try {
            jcpClient.connect();

        } catch (StateException e) {
            return String.format("Error on JCP Client connection: %s.", e.getMessage());

        } catch (JCPClient2.AuthenticationException e) {
            return String.format("Error on JCP Client authentication: %s.", e.getMessage());
        }

        return "JCP Client connected successfully.";
    }

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

}
