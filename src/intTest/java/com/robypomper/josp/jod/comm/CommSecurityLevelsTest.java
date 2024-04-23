/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
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

import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.discovery.Discover;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaThreads;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.events.JODEvents_002;
import com.robypomper.josp.jod.objinfo.JODObjectInfo_002;
import com.robypomper.josp.jod.permissions.JODPermissions_002;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLCommunication_002;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLObjsMngr_002;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.DefaultObjComm;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPSecurityLevel;
import com.robypomper.josp.states.StateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.net.SocketException;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for check the communication between JOD and JSL.
 * <p>
 * This class initialize the JODCommunication and JSLCommunication classes to
 * perform his tests.
 */
@ExtendWith(MockitoExtension.class)
public class CommSecurityLevelsTest {


    // Class constants

    protected static final String UNIQUE_ID = "123456789";
    protected static final String JOD_KS_PATH = "./build/tmp/jod_ks.jks";
    protected static final String JSL_KS_PATH = "./build/tmp/jsl_ks.jks";
    protected static final long INTF_NoSSL_WAIT_MS = 200L;
    protected static final long INTF_NoSSL_DISCONNECTION_WAIT_MS = 100L;
    protected static final long INTF_SSL_WAIT_MS = 500L;
    protected static final long INTF_SSL_DISCONNECTION_WAIT_MS = 100L;


    // Internal vars

    protected static int port = 1234;
    JODSettings_002 jodSettings;
    private JODCommunication jodComm;
    JSLSettings_002 jslSettings;
    private JSLCommunication jslComm;


    @BeforeEach
    public void setup() {
        System.out.println("\nSETUP TEST (BeforeEach)");

        File jodKSFile = new File(JOD_KS_PATH);
        if (jodKSFile.exists()) {
            System.out.printf("Warning, JOD keystore file already exists (%s), remove it%n", JOD_KS_PATH);
            if (!jodKSFile.delete())
                throw new RuntimeException(String.format("Can't remove existing JOD keystore '%s', abort test", JOD_KS_PATH));
        }
        File jslKSFile = new File(JSL_KS_PATH);
        if (jslKSFile.exists()) {
            System.out.printf("Warning, JSL keystore file already exists (%s), remove it%n", JSL_KS_PATH);
            if (!jslKSFile.delete())
                throw new RuntimeException(String.format("Can't remove existing JSL keystore '%s', abort test", JSL_KS_PATH));
        }
    }


    @AfterEach
    public void tearDown() {
        System.out.println("\nCLEAN TEST");
        if (jodComm != null && jodComm.isLocalRunning())
            try {
                jodComm.stopLocal();
            } catch (Throwable ignore) {}

        if (jslComm != null && jslComm.getLocalConnections().isRunning())
            try {
                jslComm.getLocalConnections().stop();
            } catch (Throwable ignore) {}

        File jodKSFile = new File(JOD_KS_PATH);
        if (jodKSFile.exists())
            if (!jodKSFile.delete())
                System.out.printf("Warning, JOD keystore file not deleted (%s)%n", JOD_KS_PATH);
        File jslKSFile = new File(JSL_KS_PATH);
        if (jslKSFile.exists())
            if (!jslKSFile.delete())
                System.out.printf("Warning, JSL keystore file not deleted (%s)%n", JSL_KS_PATH);
    }


    // Publish/Discovery and connect/disconnect

    /**
     * After init and startup the JODCommunication and JSLCommunication classes,
     * check the local connections JOSP Security level on both sides.
     * <p>
     * Checks on connection establishment:
     * - JODCommunication: client's full id is srvId/usrId/instId
     * - JODCommunication: client's security level is NoSSL
     * - JSLCommunication: MISSING server's full id is TestObject
     * - JSLCommunication: server's security level is NoSSL
     * <p>
     * This test verify the correct connection between JOD and JSL with NoSSL
     * security level.
     * <p>
     * JOD Settings & Certificates:
     * - No certificate
     * - JODCOMM_LOCAL_SSL_ENABLED = false
     * <p>
     * JSL Settings & Certificates:
     * - No certificate
     * - JSLCOMM_LOCAL_ONLY_NO_SSL = true
     */
    @Test
    public void testLocal_NoSSL(
            @Mock JODObjectInfo_002 objInfo, @Mock JCPAPIsClientObj jcpClientObj, @Mock JODPermissions_002 jodPermissions, @Mock JODEvents_002 jodEvents,
            @Mock JSLServiceInfo srvInfo, @Mock JCPAPIsClientSrv jcpClientSrv, @Mock JSLObjsMngr_002 jslObjsMngr, @Mock DefaultObjComm jslObjComm, @Mock JSLRemoteObject jslRemoteObject
    ) throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, Discover.DiscoveryException, StateException {


        when(objInfo.getObjName()).thenReturn("TestObject");
        when(objInfo.getObjId()).thenReturn("11111-22222-33333");
        when(srvInfo.getSrvId()).thenReturn("srvId");
        when(srvInfo.getSrvName()).thenReturn("TestService");
        when(srvInfo.getFullId()).thenReturn("srvId/usrId/instId");
        when(jslObjsMngr.createNewRemoteObject(any(), any())).thenReturn(jslRemoteObject);
        when(jslRemoteObject.getName()).thenReturn("TestObject");
        when(jslRemoteObject.getComm()).thenReturn(jslObjComm);     // Used during jsl disconnection
        when(jslObjComm.isLocalConnected()).thenReturn(false);   // Used during jsl disconnection


        System.out.println("\nSETUP TEST");
        Map<String, Object> jodSettingsMap = getDefaultJODSettings(port);
        jodSettingsMap.put(JODSettings_002.JODCOMM_LOCAL_SSL_ENABLED, "false");
        jodSettings = new JODSettings_002(jodSettingsMap);
        Map<String, Object> jslSettingsMap = getDefaultJSLSettings();
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_ONLY_NO_SSL, "true");
        jslSettings = new JSLSettings_002(jslSettingsMap);

        System.out.println("\nJOD LOCAL COMM START");
        jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        jslComm = new JSLCommunication_002(null, jslSettings, srvInfo, jcpClientSrv, jslObjsMngr, UNIQUE_ID + "srv");
        jslComm.getLocalConnections().start();

        System.out.println("\nWAIT FOR CONNECTIONS");
        JavaThreads.softSleep(INTF_NoSSL_WAIT_MS);
        System.out.println("Wait terminated at " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));

        System.out.println("\nCHECK FOR CONNECTIONS");
        //assertEquals(1, jodComm.getAllLocalClientsInfo().size());
        JODLocalClientInfo jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        assertEquals(JOSPSecurityLevel.NoSSL, jodRemoteClient.getSecurityLevel());

        System.out.println("\nCHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(1, jslComm.getLocalConnections().getLocalClients().size());
        JSLLocalClient jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        //...
        assertEquals(JOSPSecurityLevel.NoSSL, jslClient.getSecurityLevel());

        System.out.println("\nJOD LOCAL COMM STOP");
        try {
            jodComm.stopLocal();
        } catch (Throwable ignore) {}

        JavaThreads.softSleep(INTF_NoSSL_DISCONNECTION_WAIT_MS);

        System.out.println("\nJSL LOCAL COMM STOP");
        jslComm.getLocalConnections().stop();
    }

    @Test
    public void testLocal_SSLShareComp(
            @Mock JODObjectInfo_002 objInfo, @Mock JCPAPIsClientObj jcpClientObj, @Mock JODPermissions_002 jodPermissions, @Mock JODEvents_002 jodEvents,
            @Mock JSLServiceInfo srvInfo, @Mock JCPAPIsClientSrv jcpClientSrv, @Mock JSLObjsMngr_002 jslObjsMngr, @Mock DefaultObjComm jslObjComm, @Mock JSLRemoteObject jslRemoteObject
    ) throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, Discover.DiscoveryException, StateException, JavaJKS.GenerationException {


        when(objInfo.getObjName()).thenReturn("TestObject");
        when(objInfo.getObjId()).thenReturn("11111-22222-33333");
        when(srvInfo.getSrvId()).thenReturn("srvId");
        when(srvInfo.getSrvName()).thenReturn("TestService");
        when(srvInfo.getFullId()).thenReturn("srvId/usrId/instId");
        when(jslObjsMngr.createNewRemoteObject(any(), any())).thenReturn(jslRemoteObject);
        when(jslRemoteObject.getName()).thenReturn("TestObject");
        when(jslRemoteObject.getComm()).thenReturn(jslObjComm);     // Used during jsl disconnection
        when(jslObjComm.isLocalConnected()).thenReturn(false);   // Used during jsl disconnection


        System.out.println("\nSETUP TEST");
        Map<String, Object> jodSettingsMap = getDefaultJODSettings(port);
        jodSettingsMap.put(JODSettings_002.JODCOMM_LOCAL_SSL_ENABLED, "true");
        jodSettingsMap.put(JODSettings_002.JODCOMM_LOCAL_SSL_SHARING_ENABLED, "true");
        // generate partial certificate for JOD
        JavaJKS.generateNewKeyStoreFile("MyObjectCertificate", JOD_KS_PATH, JODSettings_002.JODCOMM_LOCAL_KS_PASS_DEF, "11111-22222-33333-LocalCert");
        jodSettings = new JODSettings_002(jodSettingsMap);
        Map<String, Object> jslSettingsMap = getDefaultJSLSettings();
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_ONLY_SSL, "true");
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_SSL_SHARING_ENABLED, "true");
        // generate partial certificate for JSL
        JavaJKS.generateNewKeyStoreFile("MyServiceCertificate", JSL_KS_PATH, JSLSettings_002.JSLCOMM_LOCAL_KS_PASS_DEF, "srvId/usrId/instId-LocalCert");
        jslSettings = new JSLSettings_002(jslSettingsMap);

        System.out.println("\nJOD LOCAL COMM START");
        jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        jslComm = new JSLCommunication_002(null, jslSettings, srvInfo, jcpClientSrv, jslObjsMngr, UNIQUE_ID + "srv");
        jslComm.getLocalConnections().start();

        System.out.println("\nWAIT FOR CONNECTIONS");
        JavaThreads.softSleep(INTF_SSL_WAIT_MS);
        System.out.println("Wait terminated at " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));

        System.out.println("\nCHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(1, jodComm.getAllLocalClientsInfo().size());
        JODLocalClientInfo jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        assertEquals(JOSPSecurityLevel.SSLShareComp, jodRemoteClient.getSecurityLevel());

        System.out.println("\nCHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(1, jslComm.getLocalConnections().getLocalClients().size());
        JSLLocalClient jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        //...
        assertEquals(JOSPSecurityLevel.SSLShareComp, jslClient.getSecurityLevel());

        System.out.println("\nJOD LOCAL COMM STOP");
        try {
            jodComm.stopLocal();
        } catch (Throwable ignore) {}

        JavaThreads.softSleep(INTF_SSL_DISCONNECTION_WAIT_MS);

        System.out.println("\nJSL LOCAL COMM STOP");
        jslComm.getLocalConnections().stop();
    }

    @Test
    public void testLocal_SSLComp(
            @Mock JODObjectInfo_002 objInfo, @Mock JCPAPIsClientObj jcpClientObj, @Mock JODPermissions_002 jodPermissions, @Mock JODEvents_002 jodEvents,
            @Mock JSLServiceInfo srvInfo, @Mock JCPAPIsClientSrv jcpClientSrv, @Mock JSLObjsMngr_002 jslObjsMngr, @Mock DefaultObjComm jslObjComm, @Mock JSLRemoteObject jslRemoteObject
    ) throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, Discover.DiscoveryException, StateException, JavaJKS.GenerationException, JavaJKS.LoadingException, AbsCustomTrustManager.UpdateException {


        when(objInfo.getObjName()).thenReturn("TestObject");
        when(objInfo.getObjId()).thenReturn("11111-22222-33333");
        when(srvInfo.getSrvId()).thenReturn("srvId");
        when(srvInfo.getSrvName()).thenReturn("TestService");
        when(srvInfo.getFullId()).thenReturn("srvId/usrId/instId");
        when(jslObjsMngr.createNewRemoteObject(any(), any())).thenReturn(jslRemoteObject);
        when(jslRemoteObject.getName()).thenReturn("TestObject");
        when(jslRemoteObject.getComm()).thenReturn(jslObjComm);     // Used during jsl disconnection
        when(jslObjComm.isLocalConnected()).thenReturn(false);   // Used during jsl disconnection


        System.out.println("\nSETUP TEST");
        Map<String, Object> jodSettingsMap = getDefaultJODSettings(port);
        jodSettingsMap.put(JODSettings_002.JODCOMM_LOCAL_SSL_ENABLED, "true");
        jodSettingsMap.put(JODSettings_002.JODCOMM_LOCAL_SSL_SHARING_ENABLED, "false");
        jodSettingsMap.put(JODSettings_002.JODCOMM_LOCAL_KS_ALIAS, "Custom-11111-22222-33333-LocalCert");
        // generate partial certificate for JOD
        JavaJKS.generateNewKeyStoreFile("MyObjectCertificate", JOD_KS_PATH, JODSettings_002.JODCOMM_LOCAL_KS_PASS_DEF, "Custom-11111-22222-33333-LocalCert");
        jodSettings = new JODSettings_002(jodSettingsMap);
        Map<String, Object> jslSettingsMap = getDefaultJSLSettings();
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_ONLY_SSL, "true");
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_SSL_SHARING_ENABLED, "false");
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_KS_ALIAS, "Custom-srvId/usrId/instId-LocalCert");
        // generate partial certificate for JSL
        JavaJKS.generateNewKeyStoreFile("MyServiceCertificate", JSL_KS_PATH, JSLSettings_002.JSLCOMM_LOCAL_KS_PASS_DEF, "Custom-srvId/usrId/instId-LocalCert");
        jslSettings = new JSLSettings_002(jslSettingsMap);

        System.out.println("\nJOD LOCAL COMM START");
        jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        jslComm = new JSLCommunication_002(null, jslSettings, srvInfo, jcpClientSrv, jslObjsMngr, UNIQUE_ID + "srv");
        jslComm.getLocalConnections().start();
        // Register JSL certificate in JOD and vice versa
        Certificate jodCertificate = JavaJKS.loadCertificateFromKeyStoreFile(jodSettings.getLocalKeyStorePath(), jodSettings.getLocalKeyStorePass(), "Custom-11111-22222-33333-LocalCert");
        Certificate jslCertificate = JavaJKS.loadCertificateFromKeyStoreFile(jslSettings.getLocalKeyStorePath(), jslSettings.getLocalKeyStorePass(), "Custom-srvid/usrid/instid-LocalCert");
        assert jodCertificate != null;
        assert jslCertificate != null;
        jodComm.getLocalServer().getTrustManager().addCertificate("srvId-LocalCert", jslCertificate);
        jslComm.getLocalConnections().getTrustManager().addCertificate("11111-LocalCert", jodCertificate);
        // Start JSL communication
        jslComm.getLocalConnections().start();

        System.out.println("\nWAIT FOR CONNECTIONS");
        JavaThreads.softSleep(INTF_SSL_WAIT_MS);
        System.out.println("Wait terminated at " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));

        System.out.println("\nCHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(1, jodComm.getAllLocalClientsInfo().size());
        JODLocalClientInfo jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        assertEquals(JOSPSecurityLevel.SSLComp, jodRemoteClient.getSecurityLevel());

        System.out.println("\nCHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(1, jslComm.getLocalConnections().getLocalClients().size());
        JSLLocalClient jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        //...
        assertEquals(JOSPSecurityLevel.SSLComp, jslClient.getSecurityLevel());

        System.out.println("\nJOD LOCAL COMM STOP");
        try {
            jodComm.stopLocal();
        } catch (Throwable ignore) {}

        JavaThreads.softSleep(INTF_SSL_DISCONNECTION_WAIT_MS);

        System.out.println("\nJSL LOCAL COMM STOP");
        jslComm.getLocalConnections().stop();
    }

    @Test
    public void testLocal_SSLShareInstance(
            @Mock JODObjectInfo_002 objInfo, @Mock JCPAPIsClientObj jcpClientObj, @Mock JODPermissions_002 jodPermissions, @Mock JODEvents_002 jodEvents,
            @Mock JSLServiceInfo srvInfo, @Mock JCPAPIsClientSrv jcpClientSrv, @Mock JSLObjsMngr_002 jslObjsMngr, @Mock DefaultObjComm jslObjComm, @Mock JSLRemoteObject jslRemoteObject
    ) throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, Discover.DiscoveryException, StateException {


        when(objInfo.getObjName()).thenReturn("TestObject");
        when(objInfo.getObjId()).thenReturn("11111-22222-33333");
        when(srvInfo.getSrvId()).thenReturn("srvId");
        when(srvInfo.getSrvName()).thenReturn("TestService");
        when(srvInfo.getFullId()).thenReturn("srvId/usrId/instId");
        when(jslObjsMngr.createNewRemoteObject(any(), any())).thenReturn(jslRemoteObject);
        when(jslRemoteObject.getName()).thenReturn("TestObject");
        when(jslRemoteObject.getComm()).thenReturn(jslObjComm);     // Used during jsl disconnection
        when(jslObjComm.isLocalConnected()).thenReturn(false);   // Used during jsl disconnection


        System.out.println("\nSETUP TEST");
        Map<String, Object> jodSettingsMap = getDefaultJODSettings(port);
        jodSettingsMap.put(JODSettings_002.JODCOMM_LOCAL_SSL_ENABLED, "true");
        jodSettingsMap.put(JODSettings_002.JODCOMM_LOCAL_SSL_SHARING_ENABLED, "true");
        // let auto-generate full certificate for JOD
        jodSettings = new JODSettings_002(jodSettingsMap);
        Map<String, Object> jslSettingsMap = getDefaultJSLSettings();
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_ONLY_SSL, "true");
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_SSL_SHARING_ENABLED, "true");
        // let auto-generate full certificate for JSL
        jslSettings = new JSLSettings_002(jslSettingsMap);

        System.out.println("\nJOD LOCAL COMM START");
        if (new File(jodSettings.getLocalKeyStorePath()).exists())
            throw new RuntimeException("existing JOD keystore file " + jodSettings.getLocalKeyStorePath());
        jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        if (new File(jslSettings.getLocalKeyStorePath()).exists())
            throw new RuntimeException("existing JSL keystore file " + jslSettings.getLocalKeyStorePath());
        jslComm = new JSLCommunication_002(null, jslSettings, srvInfo, jcpClientSrv, jslObjsMngr, UNIQUE_ID + "srv");
        jslComm.getLocalConnections().start();

        System.out.println("\nWAIT FOR CONNECTIONS");
        JavaThreads.softSleep(INTF_SSL_WAIT_MS);
        System.out.println("Wait terminated at " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));

        System.out.println("\nCHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(1, jodComm.getAllLocalClientsInfo().size());
        JODLocalClientInfo jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        assertEquals(JOSPSecurityLevel.SSLShareInstance, jodRemoteClient.getSecurityLevel());

        System.out.println("\nCHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(1, jslComm.getLocalConnections().getLocalClients().size());
        JSLLocalClient jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        //...
        assertEquals(JOSPSecurityLevel.SSLShareInstance, jslClient.getSecurityLevel());

        System.out.println("\nJOD LOCAL COMM STOP");
        try {
            jodComm.stopLocal();
        } catch (Throwable ignore) {}

        JavaThreads.softSleep(INTF_SSL_DISCONNECTION_WAIT_MS);

        System.out.println("\nJSL LOCAL COMM STOP");
        jslComm.getLocalConnections().stop();
    }

    @Test
    public void testLocal_SSLInstance(
            @Mock JODObjectInfo_002 objInfo, @Mock JCPAPIsClientObj jcpClientObj, @Mock JODPermissions_002 jodPermissions, @Mock JODEvents_002 jodEvents,
            @Mock JSLServiceInfo srvInfo, @Mock JCPAPIsClientSrv jcpClientSrv, @Mock JSLObjsMngr_002 jslObjsMngr, @Mock DefaultObjComm jslObjComm, @Mock JSLRemoteObject jslRemoteObject
    ) throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, Discover.DiscoveryException, StateException, AbsCustomTrustManager.UpdateException, JavaJKS.LoadingException {


        when(objInfo.getObjName()).thenReturn("TestObject");
        when(objInfo.getObjId()).thenReturn("11111-22222-33333");
        when(srvInfo.getSrvId()).thenReturn("srvId");
        when(srvInfo.getSrvName()).thenReturn("TestService");
        when(srvInfo.getFullId()).thenReturn("srvId/usrId/instId");
        when(jslObjsMngr.createNewRemoteObject(any(), any())).thenReturn(jslRemoteObject);
        when(jslRemoteObject.getName()).thenReturn("TestObject");
        when(jslRemoteObject.getComm()).thenReturn(jslObjComm);     // Used during jsl disconnection
        when(jslObjComm.isLocalConnected()).thenReturn(false);   // Used during jsl disconnection


        System.out.println("\nSETUP TEST");
        Map<String, Object> jodSettingsMap = getDefaultJODSettings(port);
        jodSettingsMap.put(JODSettings_002.JODCOMM_LOCAL_SSL_ENABLED, "true");
        jodSettingsMap.put(JODSettings_002.JODCOMM_LOCAL_SSL_SHARING_ENABLED, "false");
        // let auto-generate full certificate for JOD
        jodSettings = new JODSettings_002(jodSettingsMap);
        Map<String, Object> jslSettingsMap = getDefaultJSLSettings();
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_ONLY_SSL, "true");
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_SSL_SHARING_ENABLED, "false");
        // let auto-generate full certificate for JSL
        jslSettings = new JSLSettings_002(jslSettingsMap);

        System.out.println("\nJOD LOCAL COMM START");
        jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        jslComm = new JSLCommunication_002(null, jslSettings, srvInfo, jcpClientSrv, jslObjsMngr, UNIQUE_ID + "srv");
        // Register JSL certificate in JOD and vice versa
        Certificate jodCertificate = JavaJKS.loadCertificateFromKeyStoreFile(jodSettings.getLocalKeyStorePath(), jodSettings.getLocalKeyStorePass(), "11111-22222-33333-LocalCert");
        Certificate jslCertificate = JavaJKS.loadCertificateFromKeyStoreFile(jslSettings.getLocalKeyStorePath(), jslSettings.getLocalKeyStorePass(), "srvid/usrid/instid-LocalCert");
        assert jodCertificate != null;
        assert jslCertificate != null;
        jodComm.getLocalServer().getTrustManager().addCertificate("srvId-LocalCert", jslCertificate);
        jslComm.getLocalConnections().getTrustManager().addCertificate("11111-LocalCert", jodCertificate);
        // Start JSL communication
        jslComm.getLocalConnections().start();

        System.out.println("\nWAIT FOR CONNECTIONS");
        JavaThreads.softSleep(INTF_SSL_WAIT_MS);
        System.out.println("Wait terminated at " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));

        System.out.println("\nCHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(1, jodComm.getAllLocalClientsInfo().size());
        JODLocalClientInfo jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        assertEquals(JOSPSecurityLevel.SSLInstance, jodRemoteClient.getSecurityLevel());

        System.out.println("\nCHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(1, jslComm.getLocalConnections().getLocalClients().size());
        JSLLocalClient jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        //...
        assertEquals(JOSPSecurityLevel.SSLInstance, jslClient.getSecurityLevel());

        System.out.println("\nJOD LOCAL COMM STOP");
        try {
            jodComm.stopLocal();
        } catch (Throwable ignore) {}

        JavaThreads.softSleep(INTF_SSL_DISCONNECTION_WAIT_MS);

        System.out.println("\nJSL LOCAL COMM STOP");
        jslComm.getLocalConnections().stop();
    }

    private Map<String, Object> getDefaultJODSettings(int port) {
        Map<String, Object> properties = new HashMap<>();

        // Comm's paths
        properties.put(JODSettings_002.JODCOMM_LOCAL_ENABLED, "true");
        properties.put(JODSettings_002.JODCOMM_LOCAL_PORT, Integer.toString(port));
        properties.put(JODSettings_002.JODCOMM_LOCAL_KS_PATH, JOD_KS_PATH);
        properties.put(JODSettings_002.JODCOMM_CLOUD_ENABLED, "false");
        return properties;
    }

    private Map<String, Object> getDefaultJSLSettings() {
        Map<String, Object> properties = new HashMap<>();

        // Comm's paths
        properties.put(JSLSettings_002.JSLCOMM_LOCAL_ENABLED, "true");
        properties.put(JSLSettings_002.JSLCOMM_LOCAL_ONLY_LOCALHOST, "true");
        properties.put(JSLSettings_002.JSLCOMM_LOCAL_KS_PATH, JSL_KS_PATH);
        properties.put(JSLSettings_002.JSLCOMM_CLOUD_ENABLED, "false");
        return properties;
    }


}
