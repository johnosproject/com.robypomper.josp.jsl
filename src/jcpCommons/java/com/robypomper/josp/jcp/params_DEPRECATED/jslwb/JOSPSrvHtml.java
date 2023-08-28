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

package com.robypomper.josp.jcp.params_DEPRECATED.jslwb;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.robypomper.comm.connection.ConnectionState;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.states.JCPClient2State;
import com.robypomper.josp.states.JSLLocalState;
import com.robypomper.josp.states.JSLState;

import javax.servlet.http.HttpSession;

@JsonAutoDetect
public class JOSPSrvHtml {

    public final String name;
    public final JSLState state;
    public final JCPClient2State stateJCP;
    public final boolean isJCPConnected;
    public final ConnectionState stateCloud;
    public final boolean isCloudConnected;
    public final JSLLocalState stateLocal;
    public final boolean isLocalRunning;
    public final String srvId;
    public final String usrId;
    public final String instId;
    public final String jslVersion;
    public final String[] supportedJCPAPIsVersions;
    public final String[] supportedJOSPProtocolVersions;
    public final String[] supportedJODVersions;
    public final String sessionId;

    public JOSPSrvHtml(HttpSession session, JSL jsl) {
        this.name = jsl.getServiceInfo().getSrvName();
        this.state = jsl.getState();
        this.stateJCP = jsl.getJCPClient().getState();
        this.isJCPConnected = jsl.getJCPClient().isConnected();
        this.stateCloud = jsl.getCommunication().getCloudConnection().getState();
        this.isCloudConnected = jsl.getCommunication().getCloudConnection().getState().isConnected();
        this.stateLocal = jsl.getCommunication().getLocalConnections().getState();
        this.isLocalRunning = jsl.getCommunication().getLocalConnections().isRunning();
        this.srvId = jsl.getServiceInfo().getSrvId();
        this.usrId = jsl.getServiceInfo().getUserId();
        this.instId = jsl.getServiceInfo().getInstanceId();
        this.jslVersion = jsl.version();
        this.supportedJCPAPIsVersions = jsl.versionsJCPAPIs();
        this.supportedJOSPProtocolVersions = jsl.versionsJOSPProtocol();
        this.supportedJODVersions = jsl.versionsJOSPObject();
        this.sessionId = session.getId();
    }

}
