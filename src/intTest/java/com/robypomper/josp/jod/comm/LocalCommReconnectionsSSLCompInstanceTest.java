/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2024 Roberto Pompermaier
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

import com.robypomper.comm.peer.DisconnectionReason;
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
import com.robypomper.josp.states.StateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.robypomper.josp.jod.comm.CommSecurityLevelsTest.JOD_KS_PATH;
import static com.robypomper.josp.jod.comm.CommSecurityLevelsTest.JSL_KS_PATH;
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
public class LocalCommReconnectionsSSLCompInstanceTest {


    // Class constants

    protected static final String UNIQUE_ID = "123456789";
    protected static final long WAIT_PER_INTF = 150L;


    // Internal vars

    protected static int port = 1234;
    JODSettings_002 jodSettings;
    private JODCommunication jodComm;
    JSLSettings_002 jslSettings;
    private JSLCommunication jslComm;
    private static int ntwrkIntfs = -1;


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
    }


    // Publish/Discovery and connect/disconnect

    /**
     * After init and startup the JODCommunication and JSLCommunication classes,
     * check if the local connections are correctly established. Then it stops
     * the local connections and check if the disconnection is correctly performed.
     * <p>
     * Checks on connection establishment:
     * - JODCommunication: 1 local connection, 1 connected
     * - JODCommunication: client's full id is srvId/usrId/instId
     * - JODCommunication: client's disconnection reason is NOT_DISCONNECTED
     * - JSLCommunication: 1 local connection, 1 connected
     * - JSLCommunication: MISSING server's full id is TestObject
     * - JSLCommunication: client's disconnection reason is NOT_DISCONNECTED
     * <p>
     * Checks on disconnection:
     * - JODCommunication: client's disconnection reason is LOCAL_REQUEST
     * - JSLCommunication: client's disconnection reason is REMOTE_REQUEST
     * <p>
     * This test starts and stops the JODCommunication first.
     */
    @Test
    public void testLocalPublishAndDiscoveryFirstJOD(
            @Mock JODObjectInfo_002 objInfo, @Mock JCPAPIsClientObj jcpClientObj, @Mock JODPermissions_002 jodPermissions, @Mock JODEvents_002 jodEvents,
            @Mock JSLServiceInfo srvInfo, @Mock JCPAPIsClientSrv jcpClientSrv, @Mock JSLObjsMngr_002 jslObjsMngr, @Mock DefaultObjComm jslObjComm, @Mock JSLRemoteObject jslRemoteObject
    ) throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, SocketException, Discover.DiscoveryException, StateException, JavaJKS.GenerationException {


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
        new File(JOD_KS_PATH).delete();
        JavaJKS.generateNewKeyStoreFile("MyObjectCertificate", JOD_KS_PATH, JODSettings_002.JODCOMM_LOCAL_KS_PASS_DEF, "11111-22222-33333-LocalCert");
        jodSettings = new JODSettings_002(jodSettingsMap);
        Map<String, Object> jslSettingsMap = getDefaultJSLSettings();
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_ONLY_SSL, "true");
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_ONLY_NO_SSL, "false");
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_SSL_SHARING_ENABLED, "true");
        // generate partial certificate for JSL
        new File(JSL_KS_PATH).delete();
        JavaJKS.generateNewKeyStoreFile("MyServiceCertificate", JSL_KS_PATH, JSLSettings_002.JSLCOMM_LOCAL_KS_PASS_DEF, "srvId/usrId/instId-LocalCert");
        jslSettings = new JSLSettings_002(jslSettingsMap);

        System.out.println("\nJOD LOCAL COMM START");
        jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        jslComm = new JSLCommunication_002(null, jslSettings, srvInfo, jcpClientSrv, jslObjsMngr, UNIQUE_ID + "srv");
        jslComm.getLocalConnections().start();

        System.out.println("\nWAIT FOR CONNECTIONS");
        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("\nCHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(1, getJODLocConnCount(jodComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJODLocConnConnectedCount(jodComm));
        JODLocalClientInfo jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jodRemoteClient.getClient().getDisconnectionReason());

        System.out.println("\nCHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(1, getJSLLocConnCount(jslComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJSLLocConnConnectedCount(jslComm));
        JSLLocalClient jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        //...
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jslClient.getDisconnectionReason());




        System.out.println("\nJSL LOCAL COMM STOP 1st");
        jslComm.getLocalConnections().stop();

        System.out.println("WAIT FOR DISCONNECTIONS");
        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("CHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(0, getJODLocConnCount(jodComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(0, getJODLocConnConnectedCount(jodComm));
        //jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        //assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        //assertEquals(DisconnectionReason.NOT_DISCONNECTED, jodRemoteClient.getClient().getDisconnectionReason());

        System.out.println("CHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(0, getJSLLocConnCount(jslComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(0, getJSLLocConnConnectedCount(jslComm));
        //jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        ////...
        //assertEquals(DisconnectionReason.NOT_DISCONNECTED, jslClient.getDisconnectionReason());



        System.out.println("\nJSL LOCAL COMM START 2nd");
        jslComm.getLocalConnections().start();

        System.out.println("WAIT FOR CONNECTIONS");
        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("CHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(1, getJODLocConnCount(jodComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJODLocConnConnectedCount(jodComm));
        jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jodRemoteClient.getClient().getDisconnectionReason());

        System.out.println("CHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(1, getJSLLocConnCount(jslComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJSLLocConnConnectedCount(jslComm));
        jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        //...
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jslClient.getDisconnectionReason());






        System.out.println("\nJOD LOCAL COMM STOP");
        try {
            jodComm.stopLocal();
        } catch (Throwable ignore) {}

        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("\nJSL LOCAL COMM STOP");
        jslComm.getLocalConnections().stop();

        assertEquals(DisconnectionReason.LOCAL_REQUEST, jodRemoteClient.getClient().getDisconnectionReason());
        assertEquals(DisconnectionReason.REMOTE_REQUEST, jslClient.getDisconnectionReason());
    }

    /**
     * After init and startup the JSLCommunication and JODCommunication classes,
     * check if the local connections are correctly established. Then it stops
     * the local connections and check if the disconnection is correctly performed.
     * <p>
     * Checks on connection establishment:
     * - JODCommunication: 1 local connection, 1 connected
     * - JODCommunication: client's full id is srvId/usrId/instId
     * - JODCommunication: client's disconnection reason is NOT_DISCONNECTED
     * - JSLCommunication: 1 local connection, 1 connected
     * - JSLCommunication: MISSING server's full id is TestObject
     * - JSLCommunication: client's disconnection reason is NOT_DISCONNECTED
     * <p>
     * Checks on disconnection:
     * - JODCommunication: client's disconnection reason is REMOTE_REQUEST
     * - JSLCommunication: client's disconnection reason is LOCAL_REQUEST
     * <p>
     * This test starts and stops the JSLCommunication first.
     */
    @Test
    public void testLocalPublishAndDiscoveryFirstJSL(
            @Mock JODObjectInfo_002 objInfo, @Mock JCPAPIsClientObj jcpClientObj, @Mock JODPermissions_002 jodPermissions, @Mock JODEvents_002 jodEvents,
            @Mock JSLServiceInfo srvInfo, @Mock JCPAPIsClientSrv jcpClientSrv, @Mock JSLObjsMngr_002 jslObjsMngr, @Mock DefaultObjComm jslObjComm, @Mock JSLRemoteObject jslRemoteObject
    ) throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, SocketException, Discover.DiscoveryException, StateException, JavaJKS.GenerationException {


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
        new File(JOD_KS_PATH).delete();
        JavaJKS.generateNewKeyStoreFile("MyObjectCertificate", JOD_KS_PATH, JODSettings_002.JODCOMM_LOCAL_KS_PASS_DEF, "11111-22222-33333-LocalCert");
        jodSettings = new JODSettings_002(jodSettingsMap);
        Map<String, Object> jslSettingsMap = getDefaultJSLSettings();
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_ONLY_SSL, "true");
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_ONLY_NO_SSL, "false");
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_SSL_SHARING_ENABLED, "true");
        // generate partial certificate for JSL
        new File(JSL_KS_PATH).delete();
        JavaJKS.generateNewKeyStoreFile("MyServiceCertificate", JSL_KS_PATH, JSLSettings_002.JSLCOMM_LOCAL_KS_PASS_DEF, "srvId/usrId/instId-LocalCert");
        jslSettings = new JSLSettings_002(jslSettingsMap);

        System.out.println("\nJSL LOCAL COMM START");
        jslComm = new JSLCommunication_002(null, jslSettings, srvInfo, jcpClientSrv, jslObjsMngr, UNIQUE_ID + "srv");
        jslComm.getLocalConnections().start();

        System.out.println("\nJOD LOCAL COMM START");
        jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nWAIT FOR CONNECTIONS");
        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("\nCHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(1, getJODLocConnCount(jodComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJODLocConnConnectedCount(jodComm));
        JODLocalClientInfo jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jodRemoteClient.getClient().getDisconnectionReason());

        System.out.println("\nCHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(1, getJSLLocConnCount(jslComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJSLLocConnConnectedCount(jslComm));
        JSLLocalClient jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        //...
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jslClient.getDisconnectionReason());




        System.out.println("\nJOD LOCAL COMM STOP 1st");
        jodComm.stopLocal();

        System.out.println("WAIT FOR DISCONNECTIONS");
        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("CHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(0, getJODLocConnCount(jodComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(0, getJODLocConnConnectedCount(jodComm));
        //jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        //assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        //assertEquals(DisconnectionReason.NOT_DISCONNECTED, jodRemoteClient.getClient().getDisconnectionReason());

        System.out.println("CHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(0, getJSLLocConnCount(jslComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(0, getJSLLocConnConnectedCount(jslComm));
        //jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        ////...
        //assertEquals(DisconnectionReason.NOT_DISCONNECTED, jslClient.getDisconnectionReason());



        System.out.println("\nJOD LOCAL COMM START 2nd");
        jodComm.startLocal();

        System.out.println("WAIT FOR CONNECTIONS");
        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("CHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(1, getJODLocConnCount(jodComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJODLocConnConnectedCount(jodComm));
        jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jodRemoteClient.getClient().getDisconnectionReason());

        System.out.println("CHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(1, getJSLLocConnCount(jslComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJSLLocConnConnectedCount(jslComm));
        jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        //...
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jslClient.getDisconnectionReason());






        System.out.println("\nJSL LOCAL COMM STOP");
        jslComm.getLocalConnections().stop();

        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("\nJOD LOCAL COMM STOP");
        try {
            jodComm.stopLocal();
        } catch (Throwable ignore) {}

        assertEquals(DisconnectionReason.REMOTE_REQUEST, jodRemoteClient.getClient().getDisconnectionReason());
        assertEquals(DisconnectionReason.LOCAL_REQUEST, jslClient.getDisconnectionReason());
    }


    @Test
    public void testLocalReconnections(
            @Mock JODObjectInfo_002 objInfo, @Mock JCPAPIsClientObj jcpClientObj, @Mock JODPermissions_002 jodPermissions, @Mock JODEvents_002 jodEvents,
            @Mock JSLServiceInfo srvInfo, @Mock JCPAPIsClientSrv jcpClientSrv, @Mock JSLObjsMngr_002 jslObjsMngr, @Mock DefaultObjComm jslObjComm, @Mock JSLRemoteObject jslRemoteObject
    ) throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, SocketException, Discover.DiscoveryException, StateException, JavaJKS.GenerationException {


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
        new File(JOD_KS_PATH).delete();
        JavaJKS.generateNewKeyStoreFile("MyObjectCertificate", JOD_KS_PATH, JODSettings_002.JODCOMM_LOCAL_KS_PASS_DEF, "11111-22222-33333-LocalCert");
        jodSettings = new JODSettings_002(jodSettingsMap);
        Map<String, Object> jslSettingsMap = getDefaultJSLSettings();
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_ONLY_SSL, "true");
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_ONLY_NO_SSL, "false");
        jslSettingsMap.put(JSLSettings_002.JSLCOMM_LOCAL_SSL_SHARING_ENABLED, "true");
        // generate partial certificate for JSL
        new File(JSL_KS_PATH).delete();
        JavaJKS.generateNewKeyStoreFile("MyServiceCertificate", JSL_KS_PATH, JSLSettings_002.JSLCOMM_LOCAL_KS_PASS_DEF, "srvId/usrId/instId-LocalCert");
        jslSettings = new JSLSettings_002(jslSettingsMap);

        System.out.println("\nJOD LOCAL COMM START");
        jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        jslComm = new JSLCommunication_002(null, jslSettings, srvInfo, jcpClientSrv, jslObjsMngr, UNIQUE_ID + "srv");
        jslComm.getLocalConnections().start();

        System.out.println("\nWAIT FOR CONNECTIONS");
        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("\nCHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(1, getJODLocConnCount(jodComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJODLocConnConnectedCount(jodComm));
        JODLocalClientInfo jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jodRemoteClient.getClient().getDisconnectionReason());

        System.out.println("\nCHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(1, getJSLLocConnCount(jslComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJSLLocConnConnectedCount(jslComm));
        JSLLocalClient jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        //...
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jslClient.getDisconnectionReason());




        System.out.println("\nJSL LOCAL COMM STOP 1st");
        jslComm.getLocalConnections().stop();

        System.out.println("WAIT FOR DISCONNECTIONS");
        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("CHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(0, getJODLocConnCount(jodComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(0, getJODLocConnConnectedCount(jodComm));
        //jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        //assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        //assertEquals(DisconnectionReason.NOT_DISCONNECTED, jodRemoteClient.getClient().getDisconnectionReason());

        System.out.println("CHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(0, getJSLLocConnCount(jslComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(0, getJSLLocConnConnectedCount(jslComm));
        //jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        ////...
        //assertEquals(DisconnectionReason.NOT_DISCONNECTED, jslClient.getDisconnectionReason());



        System.out.println("\nJSL LOCAL COMM START 2nd");
        jslComm.getLocalConnections().start();

        System.out.println("WAIT FOR CONNECTIONS");
        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("CHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(1, getJODLocConnCount(jodComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJODLocConnConnectedCount(jodComm));
        jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jodRemoteClient.getClient().getDisconnectionReason());

        System.out.println("CHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(1, getJSLLocConnCount(jslComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJSLLocConnConnectedCount(jslComm));
        jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        //...
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jslClient.getDisconnectionReason());




        System.out.println("\nJOD LOCAL COMM STOP 1st");
        jodComm.stopLocal();

        System.out.println("WAIT FOR DISCONNECTIONS");
        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("CHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(0, getJODLocConnCount(jodComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(0, getJODLocConnConnectedCount(jodComm));
        //jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        //assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        //assertEquals(DisconnectionReason.NOT_DISCONNECTED, jodRemoteClient.getClient().getDisconnectionReason());

        System.out.println("CHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(0, getJSLLocConnCount(jslComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(0, getJSLLocConnConnectedCount(jslComm));
        //jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        ////...
        //assertEquals(DisconnectionReason.NOT_DISCONNECTED, jslClient.getDisconnectionReason());



        System.out.println("\nJOD LOCAL COMM START 2nd");
        jodComm.startLocal();

        System.out.println("WAIT FOR CONNECTIONS");
        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("CHECK FOR CONNECTIONS (JOD Side)");
        assertEquals(1, getJODLocConnCount(jodComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJODLocConnConnectedCount(jodComm));
        jodRemoteClient = jodComm.getAllLocalClientsInfo().get(0);
        assertEquals("srvId/usrId/instId", jodRemoteClient.getFullSrvId());
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jodRemoteClient.getClient().getDisconnectionReason());

        System.out.println("CHECK FOR CONNECTIONS (JSL Side)");
        assertEquals(1, getJSLLocConnCount(jslComm));           // increase it if JSLSettings::JSLCOMM_LOCAL_ONLY_LOCALHOST=false AND multiple interfaces on local machine
        assertEquals(1, getJSLLocConnConnectedCount(jslComm));
        jslClient = jslComm.getLocalConnections().getLocalClients().get(0);
        //...
        assertEquals(DisconnectionReason.NOT_DISCONNECTED, jslClient.getDisconnectionReason());





        System.out.println("\nJOD LOCAL COMM STOP");
        try {
            jodComm.stopLocal();
        } catch (Throwable ignore) {}

        JavaThreads.softSleep(getNetworkInterfacesCount() * WAIT_PER_INTF);

        System.out.println("\nJSL LOCAL COMM STOP");
        jslComm.getLocalConnections().stop();

        assertEquals(DisconnectionReason.LOCAL_REQUEST, jodRemoteClient.getClient().getDisconnectionReason());
        assertEquals(DisconnectionReason.REMOTE_REQUEST, jslClient.getDisconnectionReason());
    }


    // Utils

    public static int getJODLocConnCount(JODCommunication jodComm) {
        return jodComm.getAllLocalClientsInfo().size();
    }

    public static int getJODLocConnConnectedCount(JODCommunication jodComm) {
        int count = 0;
        for (JODLocalClientInfo conn : jodComm.getAllLocalClientsInfo())
            if (conn.isConnected())
                count++;
        return count;
    }

    public static int getJSLLocConnCount(JSLCommunication jslComm) {
        return jslComm.getLocalConnections().getLocalClients().size();
    }

    public static int getJSLLocConnConnectedCount(JSLCommunication jslComm) {
        int count = 0;
        for (JSLLocalClient conn : jslComm.getLocalConnections().getLocalClients())
            if (conn.getState().isConnected())
                count++;
        return count;
    }

    public static int getNetworkInterfacesCount() throws SocketException {
        if (ntwrkIntfs >= 0)
            return ntwrkIntfs;

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        int count = 0;
        while (interfaces.hasMoreElements()) {
            interfaces.nextElement();
            count++;
        }
        ntwrkIntfs = count;
        return ntwrkIntfs;
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
