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

package com.robypomper.josp.jsl;

import com.robypomper.settings.DefaultSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;

public class JSLSettings_002 extends DefaultSettings implements JSL.Settings {

    //@formatter:off
    public static final String JCP_CONNECT              = "jcp.connect";
    public static final String JCP_CONNECT_DEF          = "true";
    /** Used as `connectionTimerDelaySeconds` value into DefaultJCPClient2. */
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
    public static final String JCP_CLIENT_CALLBACK      = "jcp.client.callback";
    public static final String JCP_CLIENT_CALLBACK_DEF  = "https://localhost:8080";
    public static final String JCP_CLIENT_TOKEN_AUTH    = "jcp.client.token.authCode";
    public static final String JCP_CLIENT_TOKEN_AUTH_DEF= null;

    public static final String JSLSRV_NAME              = "jsl.srv.name";
    public static final String JSLSRV_NAME_DEF          = "";
    public static final String JSLSRV_ID                = "jsl.srv.id";
    public static final String JSLSRV_ID_DEF            = "";
    public static final String JSLSRV_INSTANCE          = "jsl.srv.instance";
    public static final String JSLSRV_INSTANCE_DEF      = "";
    /**
     * Path to use as main dir for all relative paths, like the `jsl.comm.local.ks.path`.
     * By default, it's an empty string that means to use the working directory as base path.
     */
    public static final String JSLSRV_BASE_DIR          = "jsl.srv.baseDir";
    public static final String JSLSRV_BASE_DIR_DEF      = "";

    public static final String JSLUSR_NAME              = "jsl.usr.name";
    public static final String JSLUSR_NAME_DEF          = "";
    public static final String JSLUSR_ID                = "jsl.usr.id";
    public static final String JSLUSR_ID_DEF            = "";

    public static final String JSLCOMM_LOCAL_ENABLED    = "jsl.comm.local.enabled";
    public static final String JSLCOMM_LOCAL_ENABLED_DEF = "true";
    public static final String JSLCOMM_LOCAL_ONLY_LOCALHOST = "jsl.comm.local.onlyLocalhost";
    public static final String JSLCOMM_LOCAL_ONLY_LOCALHOST_DEF = "false";
    public static final String JSLCOMM_LOCAL_DISCOVERY  = "jsl.comm.local.discovery";
    public static final String JSLCOMM_LOCAL_DISCOVERY_DEF = "Auto";
    /**
     * If 'true' the local client will use only SSL.
     * <p>
     * Default `false`.
     */
    public static final String JSLCOMM_LOCAL_ONLY_SSL    = "jsl.comm.local.onlySSL";
    public static final String JSLCOMM_LOCAL_ONLY_SSL_DEF = "false";
    /**
     * If 'true' the local client will use only NoSSL.
     * <p>
     * Default `false`.
     */
    public static final String JSLCOMM_LOCAL_ONLY_NO_SSL    = "jsl.comm.local.onlyNoSSL";
    public static final String JSLCOMM_LOCAL_ONLY_NO_SSL_DEF = "false";
    /**
     * If 'true' the local client will try to exchange his certificate with the
     * JOD Remote Object's one, always if the object's is configured to share
     * his certificate.
     * <p>
     * Enable the {@link com.robypomper.josp.protocol.JOSPSecurityLevel#SSLShareInstance}
     * and {@link com.robypomper.josp.protocol.JOSPSecurityLevel#SSLShareComp} security levels.
     * <p>
     * Default `true`.
     */
    public static final String JSLCOMM_LOCAL_SSL_SHARING_ENABLED    = "jsl.comm.local.sslSharingEnabled";
    public static final String JSLCOMM_LOCAL_SSL_SHARING_ENABLED_DEF = "true";

    /**
     * Path for the service's local keystore. It can be absolute or relative to `jsl.srv.baseDir`.
     * By default, it's an empty string, that means it will generate his own certificate at first
     * object's connection and save it into `jsl.comm.local.ks.defPath`.
     */
    public static final String JSLCOMM_LOCAL_KS_PATH    = "jsl.comm.local.ks.path";
    public static final String JSLCOMM_LOCAL_KS_PATH_DEF = "./local_ks.jks";

    /**
     * Password for the service's local keystore. 
     * By default, it's an empty string that means no password.
     */
    public static final String JSLCOMM_LOCAL_KS_PASS    = "jsl.comm.local.ks.pass";
    public static final String JSLCOMM_LOCAL_KS_PASS_DEF = "123456";

    /**
     * Alias of the certificate stored into the service's local keystore. 
     * By default, it's an empty string that means `$SRV_ID-LocalCert`.
     */
    public static final String JSLCOMM_LOCAL_KS_ALIAS    = "jsl.comm.local.ks.alias";
    public static final String JSLCOMM_LOCAL_KS_ALIAS_DEF = "";

    public static final String JSLCOMM_CLOUD_ENABLED    = "jsl.comm.cloud.enabled";
    public static final String JSLCOMM_CLOUD_ENABLED_DEF = "true";
    //@formatter:on


    // Constructor

    public static JSL.Settings instance(File file) throws IOException {
        return new JSLSettings_002(file);
    }

    public static JSL.Settings instance(Map<String, Object> properties) {
        return new JSLSettings_002(properties);
    }

    public JSLSettings_002(File file) throws IOException {
        super(file);
    }

    public JSLSettings_002(Map<String, Object> properties) {
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

    public String getJCPCallback() {
        return getString(JCP_CLIENT_CALLBACK, JCP_CLIENT_CALLBACK_DEF);
    }

    public String getJCPAuthCodeRefreshToken() {
        return getString(JCP_CLIENT_TOKEN_AUTH, JCP_CLIENT_TOKEN_AUTH_DEF);
    }

    public void setJCPAuthCodeRefreshToken(String refreshToken) {
        store(JCP_CLIENT_TOKEN_AUTH, refreshToken, true);
    }


    // Service info

    public String getSrvId() {
        return getString(JSLSRV_ID, getString(JCP_CLIENT_ID, JSLSRV_ID_DEF));
    }

    public void setSrvId(String srvId) {
        store(JSLSRV_ID, srvId, true);
    }

    public String getSrvInstance() {
        String fromFile = getString(JSLSRV_INSTANCE, null);
        if (fromFile != null)
            return fromFile;

        String newInstance = Integer.toString(new Random().nextInt(JSL_002.MAX_INSTANCE_ID));
        setSrvInstance(newInstance);
        return newInstance;
    }

    public void setSrvInstance(String srvInstance) {
        store(JSLSRV_INSTANCE, srvInstance, true);
    }

    public String getSrvName() {
        return getString(JSLSRV_NAME, JSLSRV_NAME_DEF);
    }

    public void setSrvName(String srvName) {
        store(JSLSRV_NAME, srvName, true);
    }

    public String getSrvBaseDir() {
        return getString(JSLSRV_BASE_DIR, JSLSRV_BASE_DIR_DEF);
    }

    public void setSrvBaseDir(String srvBaseDir) {
        store(JSLSRV_BASE_DIR, srvBaseDir, true);
    }


    // User info

    public String getUsrId() {
        return getString(JSLUSR_ID, JSLUSR_ID_DEF);
    }

    public void setUsrId(String userId) {
        store(JSLUSR_ID, userId, true);
    }

    public String getUsrName() {
        return getString(JSLUSR_NAME, JSLUSR_NAME_DEF);
    }

    public void setUsrName(String username) {
        store(JSLUSR_NAME, username, true);
    }


    // JSL Comm

    //@Override
    public boolean getLocalEnabled() {
        return getBoolean(JSLCOMM_LOCAL_ENABLED, JSLCOMM_LOCAL_ENABLED_DEF);
    }

    public boolean getLocalOnlySSLEnabled() {
        return getBoolean(JSLCOMM_LOCAL_ONLY_SSL, JSLCOMM_LOCAL_ONLY_SSL_DEF);
    }

    public boolean getLocalOnlyNoSSLEnabled() {
        return getBoolean(JSLCOMM_LOCAL_ONLY_NO_SSL, JSLCOMM_LOCAL_ONLY_NO_SSL_DEF);
    }

    public boolean getLocalSSLSharingEnabled() {
        return getBoolean(JSLCOMM_LOCAL_SSL_SHARING_ENABLED, JSLCOMM_LOCAL_SSL_SHARING_ENABLED_DEF);
    }

    public boolean getLocalOnlyLocalhost() {
        return getBoolean(JSLCOMM_LOCAL_ONLY_LOCALHOST, JSLCOMM_LOCAL_ONLY_LOCALHOST_DEF);
    }

    public String getLocalKeyStorePath() {
        String path = getString(JSLCOMM_LOCAL_KS_PATH, JSLCOMM_LOCAL_KS_PATH_DEF);
        if (!Paths.get(path).isAbsolute())
            path = Paths.get(getSrvBaseDir(), path).toString();
        return path;
    }

    public String getLocalKeyStorePass() {
        return getString(JSLCOMM_LOCAL_KS_PASS, JSLCOMM_LOCAL_KS_PASS_DEF);
    }

    public String getLocalKeyStoreAlias() {
        return getString(JSLCOMM_LOCAL_KS_ALIAS, JSLCOMM_LOCAL_KS_ALIAS_DEF);
    }

    public String getJSLDiscovery() {
        return getString(JSLCOMM_LOCAL_DISCOVERY, JSLCOMM_LOCAL_DISCOVERY_DEF);
    }

    //@Override
    public boolean getCloudEnabled() {
        return getBoolean(JSLCOMM_CLOUD_ENABLED, JSLCOMM_CLOUD_ENABLED_DEF);
    }

}
