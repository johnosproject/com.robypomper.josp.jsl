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

package com.robypomper.josp.jod.comm;

import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.josp.protocol.JOSPProtocol_Service;

import java.net.InetAddress;


/**
 * Default implementation of {@link JODLocalClientInfo} interface.
 */
public class DefaultJODLocalClientInfo implements JODLocalClientInfo {

    // Local vars

    private final ServerClient client;
    private final String fullSrvId;
    private final String srvId;
    private final String usrId;
    private final String instId;


    // Constructor

    /**
     * Default constructor that split the client's id in JSL's ids (service, user
     * and instance).
     *
     * @param client the communication level's client's info.
     */
    public DefaultJODLocalClientInfo(ServerClient client) {
        this.client = client;

        fullSrvId = client.getRemoteId();
        this.srvId = JOSPProtocol_Service.fullSrvIdToSrvId(fullSrvId);
        this.usrId = JOSPProtocol_Service.fullSrvIdToUsrId(fullSrvId);
        this.instId = JOSPProtocol_Service.fullSrvIdToInstId(fullSrvId);
    }


    // Service info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullSrvId() {
        return fullSrvId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSrvId() {
        return srvId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsrId() {
        return usrId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInstanceId() {
        return instId;
    }


    // Connection info

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerClient getClient() {
        return client;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientId() {
        return client.getLocalId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getClientAddress() {
        return client.getConnectionInfo().getRemoteInfo().getAddr();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getClientPort() {
        return client.getConnectionInfo().getRemoteInfo().getPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientFullAddress() {
        return String.format("%s:%s", getClientAddress(), getClientPort());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalFullAddress() {
        return String.format("%s:%s", client.getConnectionInfo().getLocalInfo().getAddr(), client.getConnectionInfo().getLocalInfo().getPort());
    }


    // Connection mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return client.getState().isConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectLocal() throws JODCommunication.LocalCommunicationException {
        try {
            client.disconnect();

        } catch (PeerDisconnectionException e) {
            throw new JODCommunication.LocalCommunicationException(String.format("Error on JODLocalServer on disconnecting client '%s'", getClientId()), e);
        }
    }

}
