/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
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

package com.robypomper.josp.jsl.srvinfo;

import com.robypomper.josp.callers.apis.core.services.Caller20;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class JSLServiceInfo_002 implements JSLServiceInfo {

    // Class constants

    String FULL_ID_FORMATTER = "%s/%s/%s";
    private static final String SRVNAME_OFFLINE = "NoSrvName-Offline";
    private static final String SRVID_OFFLINE = "ZZZZZ-ZZZZZ-ZZZZZ";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLServiceInfo_002.class);
    private final JSLSettings_002 locSettings;
    private final Caller20 apiSrvsCaller;
    private JSLUserMngr userMngr;
    private JSLObjsMngr objs;
    private JSLCommunication comm;
    private String instanceId;


    // Constructor

    /**
     * Create new service info.
     * <p>
     * This constructor create an instance of {@link Caller20} and request
     * common/mandatory info for caching them.
     *
     * @param settings   the JSL settings.
     * @param jcpClient  the JCP client.
     * @param instanceId the service instance id.
     */
    public JSLServiceInfo_002(JSLSettings_002 settings, JCPAPIsClientSrv jcpClient, String instanceId) {
        this.locSettings = settings;
        this.instanceId = instanceId;
        this.apiSrvsCaller = new Caller20(jcpClient);

        log.debug("Setting service's id to JCPClient");
        jcpClient.setServiceId(getSrvId());
        log.debug("Service's id set to JCPClient");

        log.info(String.format("Initialized JSLServiceInfo instance for '%s' service with '%s' id", getSrvName(), getSrvId()));
    }


    // Service's systems

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSystems(JSLUserMngr userMngr, JSLObjsMngr objs) {
        this.userMngr = userMngr;
        this.objs = objs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCommunication(JSLCommunication comm) {
        this.comm = comm;
    }


    // Srv's info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSrvId() {
        return locSettings.getSrvId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSrvName() {
        return locSettings.getSrvName();
    }


    // Users's info

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserLogged() {
        if (userMngr == null)
            throw new SystemNotSetException("UserMngr");

        return userMngr.isUserAuthenticated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserId() {
        if (userMngr == null)
            throw new SystemNotSetException("UserMngr");

        return userMngr.getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        if (userMngr == null)
            throw new SystemNotSetException("UserMngr");

        return userMngr.getUsername();
    }


    // Instance and fullId

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInstanceId() {
        return instanceId;
    }


    /**
     * The full service id is composed by service and user ids.
     *
     * @return an id composed by service and user id.
     */
    public String getFullId() {
        return String.format(FULL_ID_FORMATTER, getSrvId(), getUserId(), getInstanceId());
    }


    // Objects's info

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConnectedObjectsCount() {
        if (objs == null)
            throw new SystemNotSetException("ObjsMngr");

        return objs.getAllConnectedObjects().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getKnownObjectsCount() {
        if (objs == null)
            throw new SystemNotSetException("ObjsMngr");

        return objs.getAllObjects().size();
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAutoRefresh() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAutoRefresh() {

    }

}
