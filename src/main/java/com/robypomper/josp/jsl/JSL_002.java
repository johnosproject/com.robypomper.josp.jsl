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

import com.robypomper.java.JavaVersionUtils;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.defs.core.Versions;
import com.robypomper.josp.jsl.admin.JSLAdmin;
import com.robypomper.josp.jsl.admin.JSLAdmin_002;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLCommunication_002;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLObjsMngr_002;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo_002;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.jsl.user.JSLUserMngr_002;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.states.StateException;
import com.robypomper.josp.jsl.BuildInfoJospJSL;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;


public class JSL_002 extends AbsJSL {

    // Class constants

    public static final String VERSION = BuildInfoJospJSL.current.buildVersion;
    private static final int MAX_INSTANCE_ID = 10000;


    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Constructor

    public JSL_002(JSLSettings_002 settings, JCPAPIsClientSrv jcpClient, JSLServiceInfo srvInfo, JSLUserMngr usr, JSLAdmin admin, JSLObjsMngr objs, JSLCommunication comm) {
        super(settings, jcpClient, srvInfo, usr, admin, objs, comm);
    }

    public static JSL instance(JSLSettings_002 settings) throws JSLCommunication.LocalCommunicationException, JCPClient2.AuthenticationException {
        log.info("\n\n" + JavaVersionUtils.buildJavaVersionStr("John Service Library", VERSION));

        String instanceId = Integer.toString(new Random().nextInt(MAX_INSTANCE_ID));
        log.info(Mrk_JSL.JSL_MAIN, String.format("Init JSL instance id '%s'", instanceId));

        JCPAPIsClientSrv jcpClient = new JCPAPIsClientSrv(
                settings.getJCPUseSSL(),
                settings.getJCPId(),
                settings.getJCPSecret(),
                settings.getJCPUrlAPIs(),
                settings.getJCPUrlAuth(),
                settings.getJCPCallback(),
                settings.getJCPAuthCodeRefreshToken()) {

            @Override
            protected void storeTokens() {
                // Store refresh tokens
                if (isClientCredentialFlowEnabled())
                    settings.setJCPAuthCodeRefreshToken(null);
                if (isAuthCodeFlowEnabled())
                    settings.setJCPAuthCodeRefreshToken(getAuthCodeRefreshToken());
            }

        };

        if (settings.getJCPConnect())
            try {
                try {
                    jcpClient.connect();

                } catch (JCPClient2.AuthenticationException e) {
                    log.warn(Mrk_JSL.JSL_MAIN, String.format("Error on user authentication to the JCP %s, retry", e.getMessage()), e);
                    jcpClient.connect();
                }

            } catch (StateException e) {
                assert false : "Exception StateException can't be thrown because connect() was call after client creation.";
            }

        JSLServiceInfo_002 srvInfo = new JSLServiceInfo_002(settings, jcpClient, instanceId);

        JSLUserMngr_002 usr = new JSLUserMngr_002(settings, jcpClient);

        JSLAdmin_002 admin = new JSLAdmin_002(settings, jcpClient, usr);

        JSLObjsMngr_002 objs = new JSLObjsMngr_002(settings, srvInfo, usr);

        srvInfo.setSystems(usr, objs);

        JSLCommunication comm = new JSLCommunication_002(null, settings, srvInfo, jcpClient, objs, instanceId);

        srvInfo.setCommunication(comm);
        usr.setCommunication(comm);
        objs.setCommunication(comm);

        return new JSL_002(settings, jcpClient, srvInfo, usr, admin, objs, comm);
    }

    @Override
    public String version() {
        return VERSION;
    }

    @Override
    public String[] versionsJOSPObject() {
        return new String[]{"2.0.0", "2.0.1"};
    }

    @Override
    public String[] versionsJOSPProtocol() {
        return new String[]{JOSPProtocol.JOSP_PROTO_VERSION_2_0};
    }

    @Override
    public String[] versionsJCPAPIs() {
        return new String[]{Versions.VER_JCP_APIs_2_0};
    }

}
