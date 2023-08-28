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

package com.robypomper.josp.jod.comm;

import com.robypomper.comm.server.Server;
import com.robypomper.comm.server.ServerAbsSSL;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.comm.server.ServerClientsListener;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.comm.trustmanagers.DynAddTrustManager;
import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;
import com.robypomper.java.JavaThreads;
import com.robypomper.josp.jod.events.Events;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;


/**
 * Server that listen and process local client (JSL) connections.
 * <p>
 * This class provide a Cert Sharing Server (a server that allow to share
 * client and server certificates).
 */
public class JODLocalServer extends ServerAbsSSL {

    // Class constants

    public static final String KS_PASS = "123456";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODObjectInfo objInfo;
    private final JODCommunication communication;
    private final JODPermissions permissions;
    private final List<JODLocalClientInfo> localClients = new ArrayList<>();


    // Constructor

    public static JODLocalServer instantiate(JODCommunication communication, JODObjectInfo objInfo,
                                             JODPermissions permissions, int port) {

        String localId = objInfo.getObjId();

        AbsCustomTrustManager trustManager = new DynAddTrustManager();
        Certificate localCertificate = null;
        SSLContext sslCtx = null;
        try {
            KeyStore clientKeyStore = JavaJKS.generateKeyStore(localId, KS_PASS, localId + "-LocalCert");
            localCertificate = JavaJKS.extractCertificate(clientKeyStore, localId + "-LocalCert");
            sslCtx = JavaSSL.generateSSLContext(clientKeyStore, KS_PASS, trustManager);

        } catch (JavaJKS.GenerationException | JavaSSL.GenerationException e) {
            assert false : String.format("JKS and SSL generation are standard and should not throw exception [%s] %s", e.getClass().getSimpleName(), e.getMessage());
        }

        return new JODLocalServer(communication, objInfo, permissions, port, sslCtx, trustManager, localCertificate);
    }

    private JODLocalServer(JODCommunication communication, JODObjectInfo objInfo,
                           JODPermissions permissions, int port,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate) {
        super(objInfo.getObjId(), port, JOSPProtocol.JOSP_PROTO_NAME, sslCtx, trustManager, localPublicCertificate, JOSPProtocol.CLIENT_AUTH_REQUIRED, JOSPProtocol.CERT_SHARING_ENABLE);

        this.objInfo = objInfo;
        this.communication = communication;
        this.permissions = permissions;

        addListener(new ServerClientsListener() {

            @Override
            public void onConnect(Server server, ServerClient client) {
                onClientConnection(client);
            }

            @Override
            public void onDisconnect(Server server, ServerClient client) {
                onClientDisconnection(client);
            }

            @Override
            public void onFail(Server server, ServerClient client, String failMsg, Throwable exception) {
                log.warn(String.format("Error on JODLocalServer's client '%s' connection (%s)%n", client, failMsg));
            }

        });
    }


    // Message methods

    @Override
    public boolean processData(ServerClient client, byte[] data) {
        return false;
    }

    @Override
    public boolean processData(ServerClient client, String data) {
        return communication.processFromServiceMsg(data, JOSPPerm.Connection.OnlyLocal);
    }


    // Connections mngm

    /**
     * Return all server's {@link JODLocalClientInfo}.
     *
     * @return an unmodifiable array containing client's info.
     */
    public List<JODLocalClientInfo> getLocalClientsInfo() {
        return new ArrayList<>(localClients);
    }

    /**
     * Return the {@link JODLocalClientInfo} of the given <code>serviceId</code>.
     *
     * @param serviceId the required service's id.
     * @return the {@link JODLocalClientInfo} or <code>null</code> if given id
     * not found.
     */
    public JODLocalClientInfo getLocalConnectionByServiceId(String serviceId) {
        for (JODLocalClientInfo conn : localClients)
            if (conn.getClientId().equals(serviceId))
                return conn;

        return null;
    }

    /**
     * Process the new cient connection.
     * <p>
     * Generate a new {@link JODLocalClientInfo} from the given {@link ServerClient}
     * and check if another client from the same instance is already known. If it
     * is then check if the already known client is still connected, then discard
     * the new client; else it relplace the old client with the new one.
     *
     * @param client the new client's info.
     */
    private void onClientConnection(ServerClient client) {
        JavaThreads.initAndStart(new OnClientConnectionRunnable(client), "LOCAL_JSL_CONNECTED", client.toString());
    }

    private class OnClientConnectionRunnable implements Runnable {

        private final ServerClient client;

        private OnClientConnectionRunnable(ServerClient client) {
            this.client = client;
        }

        @Override
        public void run() {
            if (!client.getState().isConnecting())
                JavaThreads.softSleep(100);

            JavaThreads.softSleep(1000);

            if (!client.getState().isConnected())
                return;

            JODLocalClientInfo newConn = new DefaultJODLocalClientInfo(client);

            JODLocalClientInfo oldConn;
            synchronized (localClients) {
                oldConn = getLocalConnectionByServiceId(newConn.getClientId());
                if (oldConn == null)
                    localClients.add(newConn);
                else if (!oldConn.isConnected()) {
                    localClients.remove(oldConn);
                    localClients.add(newConn);
                }

                if (oldConn == null) {                          // new JSL service
                    try {
                        sendObjectPresentation(newConn);
                    } catch (JODCommunication.ServiceNotConnected e) {
                        log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("JOD Local Server error on sending object's presentation to new service's client '%s', discharge client", client), e);
                        localClients.remove(newConn);
                        return;
                    }
                    Events.registerLocalConn("Local JSL connected", newConn, client);
                    log.info(Mrk_JOD.JOD_COMM, String.format("JOD Local Server added JSL '%s' service with connection '%s'", newConn.getClientId(), newConn.getClientFullAddress()));

                } else if (!oldConn.isConnected()) {            // Update JSL service
                    try {
                        sendObjectPresentation(newConn);
                    } catch (JODCommunication.ServiceNotConnected e) {
                        log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("JOD Local Server error on sending object's presentation to new service's client '%s', discharge client", client), e);
                        localClients.remove(newConn);
                        return;
                    }
                    Events.registerLocalConn("Local JSL updated connection", newConn, client);
                    log.info(Mrk_JOD.JOD_COMM, String.format("JOD Local Server updated JSL '%s' connection from '%s' to '%s'", newConn.getClientId(), newConn.getClientFullAddress(), oldConn.getClientFullAddress()));

                } else {                                        //Discharge connection
                    try {
                        newConn.disconnectLocal();

                    } catch (JODCommunication.LocalCommunicationException ignore) { /* client discharged, ignoring disconnection error */ }

                    Events.registerLocalConn("Local JSL discharged connection because JSL service already connected", newConn, client);
                    log.info(Mrk_JOD.JOD_COMM, String.format("JOD Local Server discharged JSL '%s' connection '%s' because already connected on '%s'", newConn.getClientId(), newConn.getClientFullAddress(), oldConn.getClientFullAddress()));
                }
            }
        }

    }

    private void sendObjectPresentation(JODLocalClientInfo locConn) throws JODCommunication.ServiceNotConnected {
        try {
            communication.sendToSingleLocalService(locConn, JOSPProtocol_ObjectToService.createObjectInfoMsg(objInfo.getObjId(), objInfo.getObjName(), objInfo.getJODVersion(), objInfo.getOwnerId(), objInfo.getModel(), objInfo.getBrand(), objInfo.getLongDescr(), communication.getCloudConnection().getState().isConnected()), JOSPPerm.Type.Status);
            communication.sendToSingleLocalService(locConn, JOSPProtocol_ObjectToService.createObjectStructMsg(objInfo.getObjId(), objInfo.getStructForJSL()), JOSPPerm.Type.Status);
            communication.sendToSingleLocalService(locConn, JOSPProtocol_ObjectToService.createObjectPermsMsg(objInfo.getObjId(), permissions.getPermsForJSL()), JOSPPerm.Type.CoOwner);

            JOSPPerm.Type permType = permissions.getServicePermission(locConn.getSrvId(), locConn.getUsrId(), JOSPPerm.Connection.OnlyLocal);
            communication.sendToSingleLocalService(locConn, JOSPProtocol_ObjectToService.createServicePermMsg(objInfo.getObjId(), permType, JOSPPerm.Connection.OnlyLocal), permType);

        } catch (JODStructure.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM_SUB, String.format("JOD Local Server error on serialize object's structure to local service because %s", e.getMessage()), e);
        }
    }

    /**
     * Process the client disconnection.
     *
     * @param client the disconnected client's info.
     */
    private void onClientDisconnection(ServerClient client) {
        JODLocalClientInfo locConn;
        synchronized (localClients) {
            locConn = getLocalConnectionByServiceId(client.getLocalId());
            if (locConn == null)
                return;
            localClients.remove(locConn);
        }

        Events.registerLocalDisc("Local JSL disconnected", locConn, client);
        log.info(Mrk_JOD.JOD_COMM, String.format("JOD Local Server remove JSL '%s' connection '%s' because disconnected", locConn.getClientId(), locConn.getClientFullAddress()));
    }

}
