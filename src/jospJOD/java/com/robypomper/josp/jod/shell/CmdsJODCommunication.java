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
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.comm.JODGwO2SClient;
import com.robypomper.josp.jod.comm.JODLocalClientInfo;

public class CmdsJODCommunication {

    // Internal vars

    private final JODCommunication comm;


    // Constructor

    public CmdsJODCommunication(JODCommunication comm) {
        this.comm = comm;
    }


    // Local communication control

    @Command(description = "Print local communication status.")
    public String commLocalStatus() {
        return String.format("Local communication discovery system is %s", comm.isLocalRunning());
    }

    @Command(description = "Start the local communication server.")
    public String commLocalStart() {
        if (comm.isLocalRunning())
            return "Local communication server is already started, do noting";

        try {
            comm.startLocal();
        } catch (JODCommunication.LocalCommunicationException e) {
            return String.format("Error on starting local communication server because %s.", e.getMessage());
        }

        if (comm.isLocalRunning())
            return "Local communication server started successfully.";

        return "Error on starting local communication server.";
    }

    @Command(description = "Stop the local communication server.")
    public String commLocalStop() {
        if (!comm.isLocalRunning())
            return "Local communication server is already stopped, do noting";

        try {
            comm.stopLocal();
        } catch (JODCommunication.LocalCommunicationException e) {
            return String.format("Error on stopping local communication server because %s.", e.getMessage());
        }

        if (!comm.isLocalRunning())
            return "Local communication server stopped successfully.";

        return "Error on stopping local communication server.";
    }

    @Command(description = "Print all local connections.")
    public String commPrintAllLocalConnections() {
        StringBuilder s = new StringBuilder("LOCAL CONNECTIONS LIST\n");
        for (JODLocalClientInfo conn : comm.getAllLocalClientsInfo()) {
            String fullAddr = String.format("%s:%d", conn.getClientAddress(), conn.getClientPort());
            s.append(String.format("- %-30s (srv: %s; usr: %s; status: %s; local: %s)\n", fullAddr, conn.getSrvId(), conn.getUsrId(), conn.isConnected() ? "connected" : "NOT conn.", conn.getLocalFullAddress()));
        }

        return s.toString();
    }


    // Cloud communication mngm

    @Command(description = "Print cloud communication status.")
    public String commCloudStatus() {
        JODGwO2SClient cloud = comm.getCloudConnection();

        return String.format("Cloud communication client system is %s ([%d] %s)", cloud.getState(), cloud.hashCode(), cloud);
    }

    @Command(description = "Connect the cloud communication client.")
    public String commCloudConnect() {
        JODGwO2SClient cloud = comm.getCloudConnection();

        if (cloud.getState().isConnected())
            return "Cloud communication client is already connected, do noting";

        if (cloud.getState().isConnecting())
            return "Cloud communication client is already connecting, do noting";

        try {
            cloud.connect();

        } catch (PeerConnectionException e) {
            return String.format("ERROR on Cloud communication client disconnection because [%s] %s\nCloud communication state %s (%s)", e.getClass().getSimpleName(), e.getMessage(), cloud.getState(), cloud);
        }

        if (cloud.getState().isConnected())
            return "Cloud communication client connected successfully.";

        return String.format("Error on connecting cloud communication client, %s (%s).", cloud.getState(), cloud);
    }

    @Command(description = "Connect the cloud communication client.")
    public String commCloudDisconnect() {
        JODGwO2SClient cloud = comm.getCloudConnection();

        if (!cloud.getState().isConnected())
            return "Cloud communication client is already disconnected, do noting";

        try {
            cloud.disconnect();
        } catch (PeerDisconnectionException e) {
            return String.format("ERROR on Cloud communication client disconnection because [%s] %s\nCloud communication state %s (%s)", e.getClass().getSimpleName(), e.getMessage(), cloud.getState(), cloud);
        }

        if (!cloud.getState().isConnected())
            return "Cloud communication client disconnected successfully.";

        return "Error on disconnecting cloud communication client.";
    }

}
