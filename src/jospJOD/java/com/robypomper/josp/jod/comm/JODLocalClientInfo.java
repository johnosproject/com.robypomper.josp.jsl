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

import com.robypomper.comm.server.ServerClient;

import java.net.InetAddress;


/**
 * Interface that represent and provide local client (JSL) info.
 */
public interface JODLocalClientInfo {

    // Service info

    /**
     * The full service id composed by service, user and instance ids.
     *
     * @return the represented client's service full id.
     */
    String getFullSrvId();

    /**
     * The service id.
     *
     * @return the represented client's service id.
     */
    String getSrvId();

    /**
     * The user id of the user logged in the client's service.
     *
     * @return the represented client's user id.
     */
    String getUsrId();

    /**
     * Th unique ID per instance.
     * <p>
     * This id must be unique across all other srv/usr instances. That means
     * if two differents clients from same service and user are connected, they
     * must have different instance id. To the other side if the two clients
     * are from same instance, also the instance id will be the same.
     *
     * @return the represented client's instance id.
     */
    String getInstanceId();


    // Connection info

    ServerClient getClient();

    /**
     * The client id is generated and managed by the communication level below.
     * <p>
     * The client id is composed by the service, user and instance id.
     *
     * @return the represented client's id.
     */
    String getClientId();

    /**
     * @return the represented client's address.
     */
    InetAddress getClientAddress();

    /**
     * @return the represented client's port.
     */
    int getClientPort();

    /**
     * A string containing the local client (JSL) address and port that the
     * represented client is connected from.
     *
     * @return the string containing the local client address and port.
     */
    String getClientFullAddress();

    /**
     * A string containing the local server (JOD) address and port that the
     * represented client is connected with.
     *
     * @return the string containing the local server address and port.
     */
    String getLocalFullAddress();


    // Connection mngm

    /**
     * @return <code>true</code> it current local connection is connected.
     */
    boolean isConnected();

    /**
     * Close connection and disconnect corresponding JSL service.
     */
    void disconnectLocal() throws JODCommunication.LocalCommunicationException;

}
