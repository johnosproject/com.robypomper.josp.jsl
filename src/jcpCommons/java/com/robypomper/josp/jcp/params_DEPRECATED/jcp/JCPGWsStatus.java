/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.params_DEPRECATED.jcp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;


/**
 * Messaging class to transmit JOSP GW O2S access info to requiring JOD objects.
 */
public class JCPGWsStatus {

    // Params

    public int clients;

    public final int clientsMax;

    public Date lastClientConnectedAt;

    public Date lastClientDisconnectedAt;


    // Constructor

    @JsonCreator
    public JCPGWsStatus(@JsonProperty("clients") int clients,
                        @JsonProperty("clientsMax") int clientsMax,
                        @JsonProperty("lastClientConnectedAt") Date lastClientConnectedAt,
                        @JsonProperty("version") Date lastClientDisconnectedAt) {
        this.clients = clients;
        this.clientsMax = clientsMax;
        this.lastClientConnectedAt = lastClientConnectedAt;
        this.lastClientDisconnectedAt = lastClientDisconnectedAt;
    }

}
