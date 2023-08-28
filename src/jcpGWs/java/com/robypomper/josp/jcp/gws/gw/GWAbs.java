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

package com.robypomper.josp.jcp.gws.gw;

import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.comm.exception.ServerException;
import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.server.Server;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.comm.server.ServerStateListener;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.comm.trustmanagers.DynAddTrustManager;
import com.robypomper.java.*;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.callers.apis.gateways.registration.Caller20;
import com.robypomper.josp.jcp.clients.JCPClientsMngr;
import com.robypomper.josp.jcp.defs.apis.internal.gateways.registration.Params20;
import com.robypomper.josp.jcp.info.JCPGWsVersions;
import com.robypomper.josp.types.josp.gw.GWType;
import org.slf4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class GWAbs implements ApplicationListener<ContextRefreshedEvent> {

    // Class constants

    private static final String KS_PASS = "123456";
    public static final String TH_REGISTER_NAME = "GW_REG_%s";


    // Internal vars

    private final Logger log;
    protected static final String gwSerial = JavaRandomStrings.randomAlfaString(5);
    private final GWType gwType;
    private final GWServer server;
    private final Caller20 jcpAPIsCaller;
    private final String addrInternal;
    private final String addrPublic;
    private final int apisPort;
    private final int maxClients;
    private final Params20.JCPGWsStatus gwStatus;
    private CountDownLatch deregisterCountDown = new CountDownLatch(1);
    private boolean springStarted = false;
    private Timer registerTimer;
    private final int registerTimerDelayMS;


    // Constructor

    protected GWAbs(GWType gwType, String idServer, String addrInternal, String addrPublic, int gwPort, int apiPort, int maxClients,
                    JCPClientsMngr clientsMngr, Logger log) throws ServerStartupException, JavaJKS.GenerationException, JavaSSL.GenerationException {
        this.log = log;
        this.gwType = gwType;
        this.addrInternal = addrInternal;
        this.addrPublic = addrPublic;
        this.apisPort = apiPort;
        this.maxClients = maxClients;
        this.registerTimerDelayMS = 60 * 1000;

        KeyStore ks = JavaJKS.generateKeyStore(idServer, KS_PASS, idServer);
        Certificate publicCertificate = JavaJKS.extractCertificate(ks, idServer);
        DynAddTrustManager trustManager = new DynAddTrustManager();
        SSLContext sslCtx = JavaSSL.generateSSLContext(ks, KS_PASS, trustManager);

        this.gwStatus = new Params20.JCPGWsStatus();
        this.gwStatus.clients = 0;
        this.gwStatus.clientsMax = maxClients;
        this.gwStatus.lastClientConnectedAt = null;
        this.gwStatus.lastClientDisconnectedAt = null;

        this.jcpAPIsCaller = new Caller20(clientsMngr.getJCPAPIsClient());
        clientsMngr.getJCPAPIsClient().addConnectionListener(jcpAPIsListener_GWRegister);

        this.server = new GWServer(this, sslCtx, idServer, gwPort, trustManager, publicCertificate);
        this.server.addListener(serverStateListener_GWRegister);
        this.server.startup();
    }

    public void destroy() {
        try {
            getServer().shutdown();
            deregisterCountDown.await(1000, TimeUnit.MILLISECONDS);

        } catch (ServerException e) {
            log.warn(String.format("Error shutdown JCP GW '%s' server", getId()), e);

        } catch (InterruptedException e) {
            log.warn(String.format("Error waiting shutdown JCP GW '%s' server", getId()), e);
        }

        if (deregisterCountDown.getCount() > 0) {
            log.info(String.format("Forced JCP GW '%s' de-registration", getId()));
            deregister();
        }
    }

    // Getters

    public static String getSerial() {
        return gwSerial;
    }

    public String getId() {
        return getServer().getLocalId();
    }

    public GWType getType() {
        return gwType;
    }

    public GWServer getServer() {
        return server;
    }

    public String getInternalAddress() {
        return addrInternal;
    }

    public String getPublicAddress() {
        return addrPublic;
    }

    public int getGWPort() {
        return server.getServerPeerInfo().getPort();
    }

    public int getAPIsPort() {
        return apisPort;
    }

    public int getMaxClient() {
        return maxClients;
    }

    private Params20.JCPGWsStatus getGWStatus() {
        return gwStatus;
    }

    private String getVersion() {
        return JCPGWsVersions.VER_JCPGWs_S2O_2_0;
    }


    // GWServer's Clients events

    protected abstract void onClientConnection(ServerClient client);

    protected abstract void onClientDisconnection(ServerClient client);

    protected void disconnectBecauseError(ServerClient client, String errorCause) {
        try {
            client.disconnect();
        } catch (PeerDisconnectionException ignore) {
        }
        String what = getType() == GWType.Obj2Srv ? "JOD Object" : "JSL Service";
        log.warn(String.format("%s '%s' %s to JCP GW '%s', disconnected", what, client.getRemoteId(), errorCause, getId()));
    }


    // GWServer's Messages methods

    protected abstract boolean processData(ServerClient client, String data);


    // JCP APIs GWs registration

    private void tryRegisterAndUpdate() {
        if (!server.getState().isRunning())
            return;
        if (!jcpAPIsCaller.getClient().isConnected())
            return;
        if (!springStarted)
            return;

        if (registerTimer==null)
            registerTimer = JavaTimers.initAndStart(new RegisterTimer(), true, String.format(TH_REGISTER_NAME, getId()), this.toString(), 0, registerTimerDelayMS);
    }

    private void deregister() {
        if (!server.getState().isStopped())
            JavaAssertions.makeAssertion(server.getState().isStopped(), "Can't call GWServiceAbs.deregister() method when internal server is not stopped.");

        JavaAssertions.makeWarning(registerTimer != null, "GW's registerTimer can't be null on GW's deregistration.");
        if (registerTimer != null)
            JavaTimers.stopTimer(registerTimer);
        registerTimer = null;

        if (!jcpAPIsCaller.getClient().isConnected()) {
            log.warn("Can't de-register JCP GW '%s' because JCP APIs not available");
            return;
        }

        try {
            jcpAPIsCaller.postShutdown(getId());
            log.info(String.format("JCP GW '%s' de-registered to JCP APIs successfully.", getId()));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(String.format("Error de-register JCP GW '%s' to JCP APIs.", getId()), e);
        }
    }

    private class RegisterTimer implements Runnable {

        boolean isPrinted = false;

        @Override
        public void run() {
            Params20.JCPGWsStartup gwStartup = new Params20.JCPGWsStartup();
            gwStartup.type = getType();
            gwStartup.gwAddr = getPublicAddress();
            gwStartup.gwPort = getGWPort();
            gwStartup.gwAPIsAddr = getInternalAddress();
            gwStartup.gwAPIsPort = getAPIsPort();
            gwStartup.clientsMax = getMaxClient();
            gwStartup.version = getVersion();
            try {
                jcpAPIsCaller.postStartup(gwStartup, getId());
                if (!isPrinted) {
                    log.info(String.format("JCP GW '%s' registered to JCP APIs successfully.", getId()));
                    isPrinted = true;
                } else
                    log.trace(String.format("JCP GW '%s' registered to JCP APIs successfully.", getId()));

            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
                log.warn(String.format("Error register JCP GW '%s' to JCP APIs'.", getId()), e);
                isPrinted = false;
            }

            update();
        }

    }

    private void update() {
        if (!jcpAPIsCaller.getClient().isConnected()) {
            log.warn(String.format("Can't update JCP GW '%s' because JCP APIs not available", getId()));
            return;
        }

        try {
            jcpAPIsCaller.postStatus(getGWStatus(), getId());
            log.trace(String.format("JCP GW '%s' updated to JCP APIs successfully.", getId()));

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(String.format("Can't update JCP GW '%s' status to JCP APIs.", getId()), e);
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final ServerStateListener serverStateListener_GWRegister = new ServerStateListener() {

        @Override
        public void onStart(Server server) {
            tryRegisterAndUpdate();
            deregisterCountDown = new CountDownLatch(1);
        }

        @Override
        public void onStop(Server server) {
            deregister();
            deregisterCountDown.countDown();
        }

        @Override
        public void onFail(Server server, String failMsg, Throwable exception) {
            log.warn(String.format("JCP GW '%s' failed: %s (%s)", this, failMsg, exception));
        }

    };

    @SuppressWarnings("FieldCanBeLocal")
    private final JCPClient2.ConnectionListener jcpAPIsListener_GWRegister = new JCPClient2.ConnectionListener() {

        @Override
        public void onConnected(JCPClient2 jcpClient) {
            tryRegisterAndUpdate();
        }

        @Override
        public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
        }

        @Override
        public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
        }

        @Override
        public void onDisconnected(JCPClient2 jcpClient) {
        }

    };

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        springStarted = true;
        tryRegisterAndUpdate();
    }


    // GW Status helpers

    protected void increaseClient() {
        gwStatus.clients++;
        gwStatus.lastClientConnectedAt = JavaDate.getNowDate();
        update();
    }

    protected void decreaseClient() {
        gwStatus.clients--;
        gwStatus.lastClientDisconnectedAt = JavaDate.getNowDate();
        update();
    }


    // APIGWsGWsController

    public void addClientCertificate(String certId, Certificate clientCert) throws AbsCustomTrustManager.UpdateException {
        getServer().getTrustManager().addCertificate(certId, clientCert);
    }

    public com.robypomper.josp.jcp.defs.gateways.internal.clients.registration.Params20.AccessInfo getAccessInfo() throws CertificateEncodingException {
        byte[] certBytes = getServer().getPublicCertificate().getEncoded();
        if (getType() == GWType.Obj2Srv) {
            com.robypomper.josp.jcp.defs.gateways.internal.clients.registration.Params20.O2SAccessInfo ai = new com.robypomper.josp.jcp.defs.gateways.internal.clients.registration.Params20.O2SAccessInfo();
            ai.gwAddress = getPublicAddress();
            ai.gwPort = getGWPort();
            ai.gwCertificate = certBytes;
            return ai;
        } else {
            com.robypomper.josp.jcp.defs.gateways.internal.clients.registration.Params20.S2OAccessInfo ai = new com.robypomper.josp.jcp.defs.gateways.internal.clients.registration.Params20.S2OAccessInfo();
            ai.gwAddress = getPublicAddress();
            ai.gwPort = getGWPort();
            ai.gwCertificate = certBytes;
            return ai;
        }
    }

}
