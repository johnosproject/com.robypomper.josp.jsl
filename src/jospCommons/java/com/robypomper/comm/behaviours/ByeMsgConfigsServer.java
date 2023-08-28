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

import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.server.Server;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.comm.server.ServerClientsListener;

import java.util.Arrays;

public class ByeMsgConfigsServer extends ByeMsgConfigsDefault {

    // Internal vars

    // server
    private final Server server;


    // Constructors

    public ByeMsgConfigsServer(Server server, DataEncodingConfigs dataEncoding) {
        this(server, dataEncoding, ENABLE, BYE_MSG);
    }

    public ByeMsgConfigsServer(Server server, DataEncodingConfigs dataEncoding, byte[] byeMsg) {
        this(server, dataEncoding, ENABLE, byeMsg);
    }

    public ByeMsgConfigsServer(Server server, DataEncodingConfigs dataEncoding, String byeMsg) {
        this(server, dataEncoding, ENABLE, byeMsg);
    }

    public ByeMsgConfigsServer(Server server, DataEncodingConfigs dataEncoding, boolean enable, byte[] byeMsg) {
        super(dataEncoding, enable, byeMsg);

        this.server = server;
        server.addListener(serverClientsListener);
    }

    public ByeMsgConfigsServer(Server server, DataEncodingConfigs dataEncoding, boolean enable, String byeMsg) {
        super(dataEncoding, enable, byeMsg);

        this.server = server;
        server.addListener(serverClientsListener);
    }

    private final ServerClientsListener serverClientsListener = new ServerClientsListener() {

        @Override
        public void onConnect(Server server, ServerClient client) {
            client.getByeConfigs().enable(isEnable());
            client.getByeConfigs().setByeMsg(getByeMsg());
            client.getByeConfigs().setByeMsg(getByeMsgString());

            if (isEnable())
                client.getByeConfigs().addListener(clientByeMsgListener);
        }

        @Override
        public void onDisconnect(Server server, ServerClient client) {
            client.getByeConfigs().removeListener(clientByeMsgListener);
        }

        @Override
        public void onFail(Server server, ServerClient client, String failMsg, Throwable exception) {
        }

    };

    private final ByeMsgListener clientByeMsgListener = new ByeMsgListener() {

        @Override
        public void onBye(Peer peer, ByeMsgImpl byeMsg) {
            emitOnBye(peer, byeMsg);
        }

    };


    // Getter/setters

    @Override
    public void enable(boolean enable) {
        if (server != null)
            for (ServerClient client : server.getClients()) {
                if (client.getByeConfigs().isEnable() == isEnable())
                    client.getByeConfigs().enable(enable);
                if (isEnable())
                    client.getByeConfigs().addListener(clientByeMsgListener);
                else
                    client.getByeConfigs().removeListener(clientByeMsgListener);
            }

        super.enable(enable);
    }

    @Override
    public void setByeMsg(byte[] byeMsg) {
        if (server != null)
            for (ServerClient client : server.getClients()) {
                if (Arrays.equals(client.getByeConfigs().getByeMsg(), getByeMsg()))
                    client.getByeConfigs().setByeMsg(byeMsg);
            }

        super.setByeMsg(byeMsg);
    }

    @Override
    public void setByeMsg(String byeMsg) {
        if (server != null)
            for (ServerClient client : server.getClients()) {
                if (client.getByeConfigs().getByeMsgString().equals(getByeMsgString()))
                    client.getByeConfigs().setByeMsg(byeMsg);
            }

        super.setByeMsg(byeMsg);
    }

}
