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

package com.robypomper.comm.behaviours;

import com.robypomper.comm.server.Server;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.comm.server.ServerClientsListener;

public class HeartBeatConfigsServer extends HeartBeatConfigsDefault {

    // Internal vars

    // server
    private final Server server;


    // Constructors

    public HeartBeatConfigsServer(Server server) {
        this(server, TIMEOUT_MS);
    }

    public HeartBeatConfigsServer(Server server, int timeoutMs) {
        this(server, timeoutMs, TIMEOUT_HB_MS);
    }

    public HeartBeatConfigsServer(Server server, int timeoutMs, int timeoutHBMs) {
        this(server, timeoutMs, timeoutHBMs, HeartBeatConfigs.ENABLE_HB_RES);
    }

    public HeartBeatConfigsServer(Server server, int timeoutMs, int timeoutHBMs, boolean enableHBRes) {
        super(timeoutMs, timeoutHBMs, enableHBRes);

        this.server = server;
        server.addListener(serverClientsListener);
    }

    private final ServerClientsListener serverClientsListener = new ServerClientsListener() {

        @Override
        public void onConnect(Server server, ServerClient client) {
            client.getHeartBeatConfigs().setTimeout(getTimeout());
            client.getHeartBeatConfigs().setHBTimeout(getHBTimeout());
        }

        @Override
        public void onDisconnect(Server server, ServerClient client) {
        }

        @Override
        public void onFail(Server server, ServerClient client, String failMsg, Throwable exception) {
        }

    };


    // Getter/setters

    @Override
    public void setTimeout(int ms) {
        if (server != null)
            for (ServerClient client : server.getClients())
                if (client.getHeartBeatConfigs().getTimeout() == getTimeout())
                    client.getHeartBeatConfigs().setTimeout(ms);

        super.setTimeout(ms);
    }

    @Override
    public void setHBTimeout(int ms) {
        if (server != null)
            for (ServerClient client : server.getClients())
                if (client.getHeartBeatConfigs().getHBTimeout() == getHBTimeout())
                    client.getHeartBeatConfigs().setHBTimeout(ms);

        super.setHBTimeout(ms);
    }

    @Override
    public void enableHBResponse(boolean enabled) {
        if (server != null)
            for (ServerClient client : server.getClients())
                if (client.getHeartBeatConfigs().isHBResponseEnabled() == isHBResponseEnabled())
                    client.getHeartBeatConfigs().enableHBResponse(enabled);

        super.enableHBResponse(enabled);
    }

}
