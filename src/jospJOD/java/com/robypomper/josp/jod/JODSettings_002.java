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

package com.robypomper.josp.jod;

import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.settings.DefaultSettings;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JODSettings_002 extends DefaultSettings implements JOD.Settings {

    //@formatter:off
    public static final String JCP_CONNECT              = "jcp.connect";
    public static final String JCP_CONNECT_DEF          = "true";
    public static final String JCP_REFRESH_TIME         = "jcp.client.refresh";
    public static final String JCP_REFRESH_TIME_DEF     = "30";
    public static final String JCP_SSL                  = "jcp.client.ssl";
    public static final String JCP_SSL_DEF              = "true";
    public static final String JCP_URL_APIS             = "jcp.url.apis";
    public static final String JCP_URL_DEF_APIS         = "api.johnosproject.org";
    public static final String JCP_URL_AUTH             = "jcp.url.auth";
    public static final String JCP_URL_DEF_AUTH         = "auth.johnosproject.org";
    public static final String JCP_CLIENT_ID            = "jcp.client.id";
    public static final String JCP_CLIENT_ID_DEF        = "";
    public static final String JCP_CLIENT_SECRET        = "jcp.client.secret";
    public static final String JCP_CLIENT_SECRET_DEF    = "";

    public static final String JODOBJ_NAME              = "jod.obj.name";
    public static final String JODOBJ_NAME_DEF          = "";
    public static final String JODOBJ_IDCLOUD           = "jod.obj.id_cloud";
    public static final String JODOBJ_IDCLOUD_DEF       = "";
    public static final String JODOBJ_IDHW              = "jod.obj.id_hw";
    public static final String JODOBJ_IDHW_DEF          = "";

    public static final String JODPULLER_IMPLS          = "jod.executor_mngr.pullers";
    public static final String JODPULLER_IMPLS_DEF      = "";
    public static final String JODLISTENER_IMPLS        = "jod.executor_mngr.listeners";
    public static final String JODLISTENER_IMPLS_DEF    = "";
    public static final String JODEXECUTOR_IMPLS        = "jod.executor_mngr.executors";
    public static final String JODEXECUTOR_IMPLS_DEF    = "";

    public static final String JODSTRUCT_PATH           = "jod.structure.path";
    public static final String JODSTRUCT_PATH_DEF       = "configs/struct.jod";

    public static final String JODPERM_PATH             = "jod.permissions.path";
    public static final String JODPERM_PATH_DEF         = "configs/perms.jod";
    public static final String JODPERM_REFRESH          = "jod.permissions.refresh";
    public static final String JODPERM_REFRESH_DEF      = "30";
    public static final String JODPERM_GENSTARTEGY      = "jod.permissions.generation_strategy";
    public static final String JODPERM_GENSTARTEGY_DEF  = "standard";
    public static final String JODPERM_OWNER            = "jod.permissions.owner";
    public static final String JODPERM_OWNER_DEF        = JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString();

    public static final String JODCOMM_LOCAL_ENABLED    = "jod.comm.local.enabled";
    public static final String JODCOMM_LOCAL_ENABLED_DEF = "true";
    public static final String JODCOMM_LOCAL_DISCOVERY  = "jod.comm.local.discovery";
    public static final String JODCOMM_LOCAL_DISCOVERY_DEF = "Auto";
    public static final String JODCOMM_LOCAL_PORT       = "jod.comm.local.port";
    public static final String JODCOMM_LOCAL_PORT_DEF   = "0";

    public static final String JODCOMM_CLOUD_ENABLED = "jod.comm.cloud.enabled";
    public static final String JODCOMM_CLOUD_ENABLED_DEF = "true";
    //@formatter:on


    // Constructor

    public static JOD.Settings instance(File file) throws IOException {
        return new JODSettings_002(file);
    }

    public static JOD.Settings instance(Map<String, Object> properties) {
        return new JODSettings_002(properties);
    }

    public JODSettings_002(File file) throws IOException {
        super(file);
    }

    public JODSettings_002(Map<String, Object> properties) {
        super(properties);
    }


    // JCP Client

    //@Override
    public boolean getJCPConnect() {
        return getBoolean(JCP_CONNECT, JCP_CONNECT_DEF);
    }

    //@Override
    public int getJCPRefreshTime() {
        return getInt(JCP_REFRESH_TIME, JCP_REFRESH_TIME_DEF);
    }

    //@Override
    public boolean getJCPUseSSL() {
        return getBoolean(JCP_SSL, JCP_SSL_DEF);
    }

    //@Override
    public String getJCPUrlAPIs() {
        return getString(JCP_URL_APIS, JCP_URL_DEF_APIS);
    }

    //@Override
    public String getJCPUrlAuth() {
        return getString(JCP_URL_AUTH, JCP_URL_DEF_AUTH);
    }

    //@Override
    public String getJCPId() {
        return getString(JCP_CLIENT_ID, JCP_CLIENT_ID_DEF);
    }

    //@Override
    public String getJCPSecret() {
        return getString(JCP_CLIENT_SECRET, JCP_CLIENT_SECRET_DEF);
    }


    // Object info

    //@Override
    public String getObjName() {
        return getString(JODOBJ_NAME, JODOBJ_NAME_DEF);
    }

    //@Override
    public void setObjName(String objName) {
        store(JODOBJ_NAME, objName, true);
    }

    //@Override
    public String getObjIdCloud() {
        return getString(JODOBJ_IDCLOUD, JODOBJ_IDCLOUD_DEF);
    }

    //@Override
    public void setObjIdCloud(String objId) {
        store(JODOBJ_IDCLOUD, objId, true);
    }

    //@Override
    public String getObjIdHw() {
        return getString(JODOBJ_IDHW, JODOBJ_IDHW_DEF);
    }

    //@Override
    public void setObjIdHw(String objIdHw) {
        store(JODOBJ_IDHW, objIdHw, true);
    }


    // Executor Manager

    //@Override
    public String getJODPullerImpls() {
        return getString(JODPULLER_IMPLS, JODPULLER_IMPLS_DEF);
    }

    //@Override
    public String getJODListenerImpls() {
        return getString(JODLISTENER_IMPLS, JODLISTENER_IMPLS_DEF);
    }

    //@Override
    public String getJODExecutorImpls() {
        return getString(JODEXECUTOR_IMPLS, JODEXECUTOR_IMPLS_DEF);
    }


    // Structure

    //@Override
    public File getStructurePath() {
        return getFile(JODSTRUCT_PATH, JODSTRUCT_PATH_DEF);
    }


    // Permissions

    //@Override
    public File getPermissionsPath() {
        return getFile(JODPERM_PATH, JODPERM_PATH_DEF);
    }

    //@Override
    public int getPermissionsRefreshTime() {
        return getInt(JODPERM_REFRESH, JODPERM_REFRESH_DEF);
    }

    //@Override
    public JOSPPerm.GenerateStrategy getPermissionsGenerationStrategy() {
        String val = getString(JODPERM_GENSTARTEGY, JODPERM_GENSTARTEGY_DEF);
        return JOSPPerm.GenerateStrategy.valueOf(val.toUpperCase());
    }

    /**
     * Object startup
     * - init jod
     * - read from local configs                             when: owner set
     * on error                                            when: owner not set
     * - set default anonymous owner on local configs
     * - read from local configs
     * - start jod
     * <p>
     * <p>
     * Owner set from service 2 object
     * - store on local configs
     * - set owner on cloud
     * <p>
     * Owner set from service 2 object via cloud
     * - jcp: store on cloud object's temporary properties
     * - jcp: set owner on object
     * - store on local configs
     * - set owner on cloud
     */
    //@Override
    public String getOwnerId() {
        return getString(JODPERM_OWNER, JODPERM_OWNER_DEF);
    }

    //@Override
    public void setOwnerId(String ownerId) {
        store(JODPERM_OWNER, ownerId, true);
    }


    // Communication local

    //@Override
    public boolean getLocalEnabled() {
        return getBoolean(JODCOMM_LOCAL_ENABLED, JODCOMM_LOCAL_ENABLED_DEF);
    }

    //@Override
    public String getLocalDiscovery() {
        return getString(JODCOMM_LOCAL_DISCOVERY, JODCOMM_LOCAL_DISCOVERY_DEF);
    }

    //@Override
    public int getLocalServerPort() {
        return getInt(JODCOMM_LOCAL_PORT, JODCOMM_LOCAL_PORT_DEF);
    }

    //@Override
    public boolean getCloudEnabled() {
        return getBoolean(JODCOMM_CLOUD_ENABLED, JODCOMM_CLOUD_ENABLED_DEF);
    }

}
