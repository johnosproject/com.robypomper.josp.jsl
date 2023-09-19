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

package com.robypomper.josp.jsl.comm;

import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.comm.JODLocalClientInfo;
import com.robypomper.josp.jod.events.JODEvents;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLObjsMngr_002;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.test.mocks.jod.MockJCPClient_Object;
import com.robypomper.josp.test.mocks.jod.MockJODEvents;
import com.robypomper.josp.test.mocks.jod.MockJODObjectInfo;
import com.robypomper.josp.test.mocks.jod.MockJODPermissions;
import com.robypomper.josp.test.mocks.jsl.MockJCPClient_Service;
import com.robypomper.josp.test.mocks.jsl.MockJSLServiceInfo;
import com.robypomper.josp.test.mocks.jsl.MockJSLUserMngr_002;
import com.robypomper.log.Mrk_Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JOSPCommunicationIntegration {

    // Class constants

    protected static final String TEST_FILES_PREFIX = "tmp/tests/";
    protected static final String UNIQUE_ID = "123456789";


    // Internal vars

    protected static Logger log = LogManager.getLogger();
    protected static int port = 1234;
    JODSettings_002 jodSettings;
    JCPAPIsClientObj jcpClientObj;
    JODObjectInfo objInfo;
    JODPermissions jodPermissions;
    JODEvents jodEvents;
    JSLSettings_002 jslSettings;
    JCPAPIsClientSrv jcpClientSrv;
    JSLServiceInfo srvInfo;
    JSLUserMngr jslUserMngr;
    JSLObjsMngr jslObjsMngr;

    // Test config

    @BeforeEach
    public void setUp() throws JCPClient2.AuthenticationException {
        log.debug(Mrk_Test.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Mrk_Test.TEST_METHODS, "setUp");

        // Create test dir
        File testDirFiles = new File(TEST_FILES_PREFIX);
        //noinspection ResultOfMethodCallIgnored
        testDirFiles.mkdirs();

        port += 2;

        // Init JOD Comm params
        jodSettings = new JODSettings_002(getDefaultJODSettings(port));
        jcpClientObj = new MockJCPClient_Object();
        objInfo = new MockJODObjectInfo("objId");
        jodPermissions = new MockJODPermissions();
        jodEvents = new MockJODEvents();

        // Init JSL Comm params
        jslSettings = new JSLSettings_002(getDefaultJSLSettings());
        jcpClientSrv = new MockJCPClient_Service();
        srvInfo = new MockJSLServiceInfo("srvId/usrId/instId");
        jslUserMngr = new MockJSLUserMngr_002();
        //jslObjsMngr = new MockJSLObjsMngr_002();
        jslObjsMngr = new JSLObjsMngr_002(jslSettings, srvInfo, jslUserMngr);

        log.debug(Mrk_Test.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() {
        // Empty test dir
        File testDirFiles = new File(TEST_FILES_PREFIX);
        for (String s : Objects.requireNonNull(testDirFiles.list())) {
            File currentFile = new File(testDirFiles.getPath(), s);
            //noinspection ResultOfMethodCallIgnored
            currentFile.delete();
        }
        //noinspection ResultOfMethodCallIgnored
        testDirFiles.delete();
    }


    // Publish/Discovery and connect/disconnect

//    @Test
//    public void testLocalPublishAndDiscoveryFirstJOD() throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, SocketException, AbsGWsClient.GWsClientException, Discover.DiscoveryException, StateException {
//        System.out.println("\nJOD LOCAL COMM START");
//        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID);
//        jodComm.startLocal();
//
//        System.out.println("\nJSL LOCAL COMM START");
//        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr, UNIQUE_ID + "srv");
//        jslComm.getLocalConnections().start();
//
//
//        int ntwkIntfs = getNetworkInterfacesCount();
//        try {
//            Thread.sleep(ntwkIntfs * 1000);
//        } catch (InterruptedException ignore) {}
//
//        Assertions.assertEquals(1, getJODLocConnCount(jodComm));
//        Assertions.assertEquals(1, getJODLocConnConnectedCount(jodComm));
//        Assertions.assertEquals(1, getJSLLocConnCount(jslComm));
//        Assertions.assertEquals(1, getJSLLocConnConnectedCount(jslComm));
//
//        System.out.println("\nJOD and JSL LOCAL COM STOP");
//        jodComm.stopLocal();
//        jslComm.getLocalConnections().stop();
//    }
//
//    @Test
//    public void testLocalPublishAndDiscoveryFirstJSL() throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, SocketException, Discover.DiscoveryException, StateException, AbsGWsClient.GWsClientException {
//        System.out.println("\nJSL LOCAL COMM START");
//        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr, UNIQUE_ID + "srv");
//        jslComm.getLocalConnections().start();
//
//        System.out.println("\nJOD LOCAL COMM START");
//        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID);
//        jodComm.startLocal();
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ignore) {}
//
//        int ntwkIntfs = getNetworkInterfacesCount();
//        try {
//            Thread.sleep(ntwkIntfs * 1000);
//        } catch (InterruptedException ignore) {}
//
//        Assertions.assertEquals(1, getJODLocConnCount(jodComm));
//        Assertions.assertEquals(1, getJODLocConnConnectedCount(jodComm));
//        Assertions.assertEquals(1, getJSLLocConnCount(jslComm));
//        Assertions.assertEquals(1, getJSLLocConnConnectedCount(jslComm));
//
//        System.out.println("\nJOD and JSL LOCAL COM STOP");
//        jodComm.stopLocal();
//        jslComm.getLocalConnections().stop();
//    }
//
//    @Test
//    public void testLocalPublishAndDiscoveryStopJOD() throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, SocketException, AbsGWsClient.GWsClientException, Discover.DiscoveryException, StateException {
//        System.out.println("\nJOD LOCAL COMM START");
//        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID);
//        jodComm.startLocal();
//
//        System.out.println("\nJSL LOCAL COMM START");
//        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr, UNIQUE_ID + "srv");
//        jslComm.getLocalConnections().start();
//
//        int ntwkIntfs = getNetworkInterfacesCount();
//        try {
//            Thread.sleep(ntwkIntfs * 1000);
//        } catch (InterruptedException ignore) {}
//
////        for (JODLocalConnection c: jodComm.getAllLocalConnection())
////            System.out.println("#" + c.getClientId() + "\t" + c.getPeerAddress() + "\t" + c.getPeerPort() + "\t" + c.isConnected());
////        for (JSLLocalClient c: jslComm.getAllLocalConnection())
////            System.out.println("@" + c.getClientId() + "\t" + c.getObjId() + "\t" + c.getServerAddr() + "\t" + c.getServerPort() + "\t" + c.isConnected());
//
//        System.out.println("\nJOD LOCAL COM STOP");
//        jodComm.stopLocal();
//
//
//        Assertions.assertEquals(1, getJODLocConnCount(jodComm));
//        Assertions.assertEquals(0, getJODLocConnConnectedCount(jodComm));
//        Assertions.assertEquals(0, getJSLLocConnCount(jslComm));
//        Assertions.assertEquals(0, getJSLLocConnConnectedCount(jslComm));
//
//        System.out.println("\nJSL LOCAL COM STOP");
//        jslComm.getLocalConnections().stop();
//    }
//
//    @Test
//    public void testLocalPublishAndDiscoveryStopJSL() throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, SocketException, AbsGWsClient.GWsClientException, Discover.DiscoveryException, StateException {
//        System.out.println("\nJOD LOCAL COMM START");
//        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID);
//        jodComm.startLocal();
//
//        System.out.println("\nJSL LOCAL COMM START");
//        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr, UNIQUE_ID + "srv");
//        jslComm.getLocalConnections().start();
//
//        int ntwkIntfs = getNetworkInterfacesCount();
//        try {
//            Thread.sleep(ntwkIntfs * 1000);
//        } catch (InterruptedException ignore) {}
//
//
//        System.out.println("\nJSL LOCAL COM STOP");
//        jslComm.getLocalConnections().stop();
//
////        for (JODLocalConnection c: jodComm.getAllLocalConnection())
////            System.out.println("#" + c.getClientId() + "\t" + c.getPeerAddress() + "\t" + c.getPeerPort() + "\t" + c.isConnected());
////        for (JSLLocalClient c: jslComm.getAllLocalConnection())
////            System.out.println("@" + c.getClientId() + "\t" + c.getObjId() + "\t" + c.getServerAddr() + "\t" + c.getServerPort() + "\t" + c.isConnected());
////
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException ignore) {}
//        Assertions.assertEquals(1, getJODLocConnCount(jodComm));
//        Assertions.assertEquals(0, getJODLocConnConnectedCount(jodComm));
//        Assertions.assertEquals(0, getJSLLocConnCount(jslComm));
//        Assertions.assertEquals(0, getJSLLocConnConnectedCount(jslComm));
//
//        System.out.println("\nJOD LOCAL COM STOP");
//        jodComm.stopLocal();
//    }


    // Multiple server

//    @Test()
//    public void testLocalPublishAndDiscoveryTwoJOD() throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, Discover.DiscoveryException, StateException, AbsGWsClient.GWsClientException {
//        System.out.println("\nJOD LOCAL COMM START (1st)");
//        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID);
//        jodComm.startLocal();
//
//        System.out.println("\nJOD LOCAL COMM START (2nd)");
//        port += 2;
//        JODSettings_002 jodSettings2 = new JODSettings_002(getDefaultJODSettings(port));
//        JODObjectInfo objInfo2 = new MockJODObjectInfo("objId_2");
//        JODCommunication jodComm2 = new JODCommunication_002(jodSettings2, objInfo2, jcpClientObj, jodPermissions, jodEvents, UNIQUE_ID + "bis");
//        jodComm2.startLocal();
//
//        System.out.println("\nJSL LOCAL COMM START");
//        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr, UNIQUE_ID + "srv");
//        jslComm.getLocalConnections().start();
//
//        int ntwkIntfs = 1;//getNetworkInterfacesCount();
//        try {
//            Thread.sleep(ntwkIntfs * 1000);
//            if (ntwkIntfs * 2 > getJSLLocConnCount(jslComm)) {
//                int extraSecs = (ntwkIntfs * 2 - getJSLLocConnCount(jslComm));
//                System.out.printf("\nEXTRA TIME (%s)%n", extraSecs);
//                Thread.sleep(extraSecs * 1000);
//            }
//        } catch (InterruptedException ignore) {}
//
////        for (JSLLocalClient c : jslComm.getAllLocalServers())
////            System.out.println(c.getClientId() + "\t" + c.getObjId() + "\t" + c.getServerAddr() + "\t" + c.getServerPort() + "\t" + c.isConnected());
//
////        Assertions.assertEquals(ntwkIntfs, getJODLocConnCount(jodComm));
////        Assertions.assertEquals(ntwkIntfs, getJODLocConnConnectedCount(jodComm));
////        Assertions.assertEquals(ntwkIntfs, getJODLocConnCount(jodComm2));
////        Assertions.assertEquals(ntwkIntfs, getJODLocConnConnectedCount(jodComm2));
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException ignore) {}
//        Assertions.assertEquals(2, getJSLLocConnCount(jslComm));
//        Assertions.assertEquals(2, getJSLLocConnConnectedCount(jslComm));
//
//        System.out.println("\nJOD and JSL LOCAL COM STOP");
//        jodComm.stopLocal();
//        jodComm2.stopLocal();
//        jslComm.getLocalConnections().stop();
//    }


    private int getJODLocConnCount(JODCommunication jodComm) {
        return jodComm.getAllLocalClientsInfo().size();
    }

    private int getJODLocConnConnectedCount(JODCommunication jodComm) {
        int count = 0;
        for (JODLocalClientInfo conn : jodComm.getAllLocalClientsInfo())
            if (conn.isConnected())
                count++;
        return count;
    }

    private int getJSLLocConnCount(JSLCommunication jslComm) {
        return jslComm.getLocalConnections().getLocalClients().size();
    }

    private int getJSLLocConnConnectedCount(JSLCommunication jslComm) {
        int count = 0;
        for (JSLLocalClient conn : jslComm.getLocalConnections().getLocalClients())
            if (conn.getState().isConnected())
                count++;
        return count;
    }

    private int getNetworkInterfacesCount() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        int count = 0;
        while (interfaces.hasMoreElements()) {
            interfaces.nextElement();
            count++;
        }
        return count;
    }


    private Map<String, Object> getDefaultJODSettings(int port) {
        Map<String, Object> properties = new HashMap<>();

        // Comm's paths
        properties.put(JODSettings_002.JODCOMM_LOCAL_ENABLED, "true");
        properties.put(JODSettings_002.JODCOMM_LOCAL_PORT, Integer.toString(port));
        properties.put(JODSettings_002.JODCOMM_CLOUD_ENABLED, "false");
        return properties;
    }

    private Map<String, Object> getDefaultJSLSettings() {
        Map<String, Object> properties = new HashMap<>();

        // Comm's paths
        properties.put(JSLSettings_002.JSLCOMM_LOCAL_ENABLED, "true");
        properties.put(JSLSettings_002.JSLCOMM_CLOUD_ENABLED, "false");
        return properties;
    }

}
