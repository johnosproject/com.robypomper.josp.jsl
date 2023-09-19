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

import com.robypomper.comm.exception.ServerShutdownException;
import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.josp.jod.comm.JODLocalServer;
import com.robypomper.josp.test.mocks.jod.MockJODCommunication;
import com.robypomper.josp.test.mocks.jod.MockJODObjectInfo;
import com.robypomper.josp.test.mocks.jod.MockJODPermissions;
import com.robypomper.log.Mrk_Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.net.InetAddress;
import java.util.Objects;

public class JSLLocalClientTest {

    // Class constants

    protected static final String TEST_FILES_PREFIX = "tmp/tests/";
    protected static final String KS_FILE = String.format("%s.p12", JSLLocalClientTest.class.getSimpleName());
    protected static final String PUB_CERT_PATH = String.format("%s.crt", JSLLocalClientTest.class.getSimpleName());
    protected static final String JSL_PUB_CERT_PATH = TEST_FILES_PREFIX + PUB_CERT_PATH;
    protected static final String LOCALHOST = InetAddress.getLoopbackAddress().getHostAddress();


    // Internal vars

    protected static Logger log = LogManager.getLogger();
    protected static JODLocalServer jodServer;
    protected static int port = 1234;


    // Test config

    @BeforeEach
    public void setUp() throws ServerStartupException {
        log.debug(Mrk_Test.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Mrk_Test.TEST_METHODS, "setUp");

        // Create test dir
        File testDirFiles = new File(TEST_FILES_PREFIX);
        //noinspection ResultOfMethodCallIgnored
        testDirFiles.mkdirs();

        port++;

        // Start server
        jodServer = JODLocalServer.instantiate(new MockJODCommunication(), new MockJODObjectInfo(), new MockJODPermissions(), port);
        jodServer.startup();

        log.debug(Mrk_Test.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() throws ServerShutdownException {
        // Stop JOD servers
        if (jodServer.getState().isRunning())
            jodServer.shutdown();

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


    // Connect and disconnect

//    @Test
//    public void testLocalConnectAndDisconnect() throws StateException, Client.AAAException, IOException {
//        System.out.println("\nJSL LOCAL CLIENT CONNECT");
//        JSLLocalClient client = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId", LOCALHOST, port,
//                JSL_PUB_CERT_PATH);
//        client.connect();
//        Assertions.assertTrue(client.isConnected());
//        Assertions.assertEquals(jodServer.getServerId(), client.tryObjId());
//
//        System.out.println("\nJSL LOCAL CLIENT DISCONNECT");
//        client.disconnect();
//        Assertions.assertFalse(client.isConnected());
//    }
//
//    @Test
//    public void testLocalConnectAndServerStop() throws StateException, Client.AAAException, IOException {
//        System.out.println("\nJSL LOCAL CLIENT CONNECT");
//        JSLLocalClient client = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId", LOCALHOST, port,
//                JSL_PUB_CERT_PATH);
//        client.connect();
//        Assertions.assertTrue(client.isConnected());
//
//        Assertions.assertEquals(jodServer.getServerId(), client.tryObjId());
//
//        System.out.println("\nJOD LOCAL SERVER STOP");
//        jodServer.stop();
//        Assertions.assertFalse(client.isConnected());
//    }

}
