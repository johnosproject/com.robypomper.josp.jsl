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
import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.discovery.Discover;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.comm.JSLLocalClientsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.states.StateException;

public class CmdsJSLCommunication {

    // Internal vars

    private final JSLCommunication comm;


    // Constructor

    public CmdsJSLCommunication(JSLCommunication comm) {
        this.comm = comm;
    }


    // Local communication control

    @Command(description = "Print local communication status.")
    public String commLocalStatus() {
        return String.format("Local communication discovery system is %s", comm.getLocalConnections().getState());
    }

    @Command(description = "Start the local communication discovery system.")
    public String commLocalStart() {
        if (comm.getLocalConnections().isRunning())
            return "Local communication discovery system is already started, do noting";

        try {
            comm.getLocalConnections().start();
        } catch (StateException | Discover.DiscoveryException e) {
            return String.format("Error on starting local communication discovery system because %s.", e.getMessage());
        }

        if (comm.getLocalConnections().isRunning())
            return "Local communication discovery system started successfully.";

        return "Error on starting local communication discovery system.";
    }

    @Command(description = "Stop the local communication discovery system.")
    public String commLocalStop() {
        if (!comm.getLocalConnections().isRunning())
            return "Local communication discovery system is already stopped, do noting";

        try {
            comm.getLocalConnections().stop();
        } catch (StateException | Discover.DiscoveryException e) {
            return String.format("Error on stopping local communication discovery system because %s.", e.getMessage());
        }

        if (!comm.getLocalConnections().isRunning())
            return "Local communication discovery system stopped successfully.";

        return "Error on stopping local communication discovery system.";
    }

    @Command(description = "Print all local connections.")
    public String commPrintAllLocalConnections() {
        StringBuilder s = new StringBuilder("LOCAL CONNECTIONS LIST\n");
        for (JSLLocalClient client : comm.getLocalConnections().getLocalClients()) {
            String fullAddr = String.format("%s:%d", client.getConnectionInfo().getRemoteInfo().getAddr().getHostAddress(), client.getConnectionInfo().getRemoteInfo().getPort());
            s.append(String.format("- %-30s (obj: %s; status: %s; local: %s)\n", fullAddr, client.getRemoteId(), client.getState().isConnected() ? "connected" : "NOT conn.", client.getConnectionInfo().getRemoteInfo().getAddr().getHostAddress()));
        }

        return s.toString();
    }


    // Cloud communication mngm

    @Command(description = "Print cloud communication status.")
    public String commCloudStatus() {
        return String.format("Cloud communication client system is %s", comm.getCloudConnection().getState());
    }

    @Command(description = "Connect the cloud communication client.")
    public String commCloudConnect() {
        try {
            comm.getCloudConnection().connect();
        } catch (PeerConnectionException e) {
            return String.format("Error on connecting cloud communication client because %s.", e.getMessage());
        }

        if (comm.getCloudConnection().getState().isConnected())
            return "Cloud communication client connected successfully.";
        return "Error on connecting cloud communication client.";
    }

    @Command(description = "Connect the cloud communication client.")
    public String commCloudDisconnect() {
        try {
            comm.getCloudConnection().disconnect();
        } catch (PeerDisconnectionException e) {
            return String.format("Error on discconnecting cloud communication client because %s.", e.getMessage());
        }

        if (!comm.getCloudConnection().getState().isConnected())
            return "Cloud communication client disconnected successfully.";
        return "Error on disconnecting cloud communication client.";
    }


    // Communication's listeners

    @Command(description = "Add logger listener to objects manager's events.")
    public String objsCommAddListeners() {
        comm.getCloudConnection().addListener(new PeerConnectionListener() {
            @Override
            public void onConnecting(Peer peer) {
                System.out.println(CmdsJSLObjsMngr.PRE + " Cloud CONNECTING " + CmdsJSLObjsMngr.POST);
            }

            @Override
            public void onWaiting(Peer peer) {
                System.out.println(CmdsJSLObjsMngr.PRE + " Cloud WAITING " + CmdsJSLObjsMngr.POST);
            }

            @Override
            public void onConnect(Peer peer) {
                System.out.println(CmdsJSLObjsMngr.PRE + " Cloud CONNECTED " + CmdsJSLObjsMngr.POST);
            }

            @Override
            public void onDisconnecting(Peer peer) {
                System.out.println(CmdsJSLObjsMngr.PRE + " Cloud DISCONNECTEDING " + CmdsJSLObjsMngr.POST);
            }

            @Override
            public void onDisconnect(Peer peer) {
                System.out.println(CmdsJSLObjsMngr.PRE + " Cloud DISCONNECTED " + CmdsJSLObjsMngr.POST);
            }

            @Override
            public void onFail(Peer peer, String failMsg, Throwable exception) {
                System.out.println(CmdsJSLObjsMngr.PRE + " Cloud ERROR (" + failMsg + " [" + exception.getClass().getSimpleName() + "] " + exception.getMessage() + ") " + CmdsJSLObjsMngr.POST);
            }
        });
        comm.getLocalConnections().addListener(new JSLLocalClientsMngr.CommLocalStateListener() {

            @Override
            public void onStarted() {
                System.out.println(CmdsJSLObjsMngr.PRE + " Local STARTED " + CmdsJSLObjsMngr.POST);
            }

            @Override
            public void onStopped() {
                System.out.println(CmdsJSLObjsMngr.PRE + " Local STOPPED" + CmdsJSLObjsMngr.POST);
            }

        });
        comm.getLocalConnections().addListener(new JSLLocalClientsMngr.LocalClientListener() {

            @Override
            public void onLocalConnected(JSLRemoteObject jslObj, JSLLocalClient jslLocCli) {
                System.out.println(CmdsJSLObjsMngr.PRE + " Local CONNECTED (" + jslObj.getName() + ")" + CmdsJSLObjsMngr.POST);
            }

            @Override
            public void onLocalConnectionError(JSLLocalClient jslLocCli, Throwable throwable) {
                System.out.println(CmdsJSLObjsMngr.PRE + " Local CONNECTION ERROR ([" + throwable.getClass().getSimpleName() + "] " + throwable.getMessage() + ")" + CmdsJSLObjsMngr.POST);
            }

            @Override
            public void onLocalDisconnected(JSLRemoteObject jslObj, JSLLocalClient jslLocCli) {
                System.out.println(CmdsJSLObjsMngr.PRE + " Local DISCONNECTED (" + jslObj.getName() + ") " + CmdsJSLObjsMngr.POST);
            }

        });

        return "ok";
    }

}
