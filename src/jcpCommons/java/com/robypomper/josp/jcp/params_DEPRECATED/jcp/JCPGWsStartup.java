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
import com.robypomper.josp.types.josp.gw.GWType;


/**
 * Messaging class to transmit JOSP GW O2S access info to requiring JOD objects.
 */
public class JCPGWsStartup {

    // Params

    public final GWType type;

    public final String gwAddr;

    public final int gwPort;

    public final String gwAPIsAddr;

    public final int gwAPIsPort;

    public final int clientsMax;

    public final String version;


    // Constructor

    @JsonCreator
    public JCPGWsStartup(@JsonProperty("type") GWType type,
                         @JsonProperty("gwAddr") String gwAddr,
                         @JsonProperty("gwPort") int gwPort,
                         @JsonProperty("gwAPIsAddr") String gwAPIsAddr,
                         @JsonProperty("gwAPIsPort") int gwAPIsPort,
                         @JsonProperty("clientsMax") int clientsMax,
                         @JsonProperty("version") String version) {
        this.type = type;
        this.gwAddr = gwAddr;
        this.gwPort = gwPort;
        this.gwAPIsAddr = gwAPIsAddr;
        this.gwAPIsPort = gwAPIsPort;
        this.clientsMax = clientsMax;
        this.version = version;
    }

}
