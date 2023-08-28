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

package com.robypomper.josp.jcp.gws.broker;

import com.robypomper.java.JavaStructures;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.List;
import java.util.Map;

public interface BrokerObjDB {

    // Getters

    List<BrokerClientObjDB> getAllObjectsDB();

    BrokerClientObjDB getObjectDB(String objId);


    // Registration

    void registerObject(BrokerClientObjDB gwObject);

    void deregisterObject(BrokerClientObjDB gwObject);


    // Communication

    void send(BrokerClient gwClientJOD, String data, JOSPPerm.Type minPerm);

    void send(BrokerClient gwClientJOD, String srvId, String data, JOSPPerm.Type minPerm);


    // Permission

    Map<String, JavaStructures.Pair<JOSPPerm.Type, JOSPPerm.Connection>> getObjectCloudAllowedServices(String objId);

}
