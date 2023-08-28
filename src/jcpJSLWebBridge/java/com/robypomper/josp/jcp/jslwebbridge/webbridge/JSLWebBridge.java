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

package com.robypomper.josp.jcp.jslwebbridge.webbridge;

import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.comm.peer.PeerInfoRemote;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaTimers;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.jslwebbridge.exceptions.JSLAlreadyInitForSessionException;
import com.robypomper.josp.jcp.jslwebbridge.exceptions.JSLErrorOnInitException;
import com.robypomper.josp.jcp.jslwebbridge.exceptions.JSLNotInitForSessionException;
import com.robypomper.josp.jsl.FactoryJSL;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.remote.ObjInfo;
import com.robypomper.josp.jsl.objs.remote.ObjPerms;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.states.StateException;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class JSLWebBridge {

    // Class constants

    public static final String HEART_BEAT = "HB";
    public static final boolean LOC_COMM_ENABLED = false;
    public static final String TH_SCHEDULE_JSL_REMOVE = "SCHEDULE_JSL_REMOVE";
    public static final String TH_EMITTERS_HEARTBEAT = "EMITTERS_HEARTBEAT";


    public static final String LOG_CREATED_JSL = "JSL Instance '%s' created for session '%s'";
    public static final String LOG_REMOVED_JSL = "JSL Instance '%s' removed for session '%s'";
    public static final String LOG_ERR_REMOVED_JSL = "Error on JSL Instance '%s' removing for session '%s' (%s)";
    public static final String LOG_SCHEDULE_JSL_REMOVE = "JSL Instance scheduled for remove for session '%s'";
    public static final String LOG_SCHEDULE_JSL_REMOVE_ABORT = "Aborted JSL Instance removing for session '%s'";
    public static final String LOG_CREATED_EMITTER = "JSL Instance '%s' created emitter for session '%s' at address '%s'";
    public static final String LOG_REMOVED_EMITTER = "JSL Instance '%s' removed emitter for session '%s' at address '%s'";
    public static final String LOG_SEND_EVENT = "JSL Instance '%s' for session '%s' at address '%s' send '%s' event";
    public static final String LOG_SEND_EVENT_DISCONNECTED = "JSL Instance '%s' for session '%s' at address '%s' emitter disconnected";
    public static final String LOG_ERR_SEND_EVENT = "Error on JSL Instance '%s' for session '%s' at address '%s' sending '%s' event (%s)";
    public static final String LOG_JSL_EVENT = "JSL Instance '%s' event '%s'";
    public static final String LOG_JSL_ERROR = "JSL Instance '%s' error '%s'";
    public static final String ASSERTION_NO_JSL = "Can't call JSLWebBridge.%s() method when no JSL Instance was created for session '%s'";
    public static final String ASSERTION_NO_EMITTERS = "Can't call JSLWebBridge.%s() method when no emitters list was created for session '%s'";
    public static final String ASSERTION_NO_EMITTER_CLIENT = "Can't call JSLWebBridge.%s() method when no emitters was created for session '%s' at address '%s'";
    public static final String ASSERTION_EXIST_EMITTER_CLIENT = "Can't call JSLWebBridge.%s() method when emitter was already created for session '%s' at address '%s'";

    //@formatter:off
    public static final String EVENT_JCP_APIS_CONN          = "{\"what\": \"JCP_APIS_CONN\",        \"url\": \"%s\"}";
    public static final String EVENT_JCP_APIS_DISCONN       = "{\"what\": \"JCP_APIS_DISCONN\",     \"url\": \"%s\"}";
    public static final String EVENT_JCP_APIS_FAIL_GEN      = "{\"what\": \"JCP_APIS_FAIL_GEN\",    \"url\": \"%s\",    \"error\": \"%s\"}";
    public static final String EVENT_JCP_APIS_FAIL_AUTH     = "{\"what\": \"JCP_APIS_FAIL_AUT\",    \"url\": \"%s\",    \"error\": \"%s\"}";
    public static final String EVENT_JCP_APIS_LOGIN         = "{\"what\": \"JCP_APIS_LOGIN\",       \"url\": \"%s\",    \"userid\": \"%s\", \"username\": \"%s\"}";
    public static final String EVENT_JCP_APIS_LOGOUT        = "{\"what\": \"JCP_APIS_LOGOUT\",      \"url\": \"%s\"}";

    public static final String EVENT_JCP_GWS_CONNECTING     = "{\"what\": \"JCP_GWS_CONNECTING\",       \"proto\": \"%s\",  \"host\": \"%s\",   \"port\": \"%s\"}";
    public static final String EVENT_JCP_GWS_WAITING        = "{\"what\": \"JCP_GWS_WAITING\",          \"proto\": \"%s\",  \"host\": \"%s\",   \"port\": \"%s\"}";
    public static final String EVENT_JCP_GWS_CONNECTED      = "{\"what\": \"JCP_GWS_CONNECTED\",        \"proto\": \"%s\",  \"host\": \"%s\",   \"port\": \"%s\"}";
    public static final String EVENT_JCP_GWS_DISCONNECTING  = "{\"what\": \"JCP_GWS_DISCONNECTING\",    \"proto\": \"%s\",  \"host\": \"%s\",   \"port\": \"%s\"}";
    public static final String EVENT_JCP_GWS_DISCONNECTED   = "{\"what\": \"JCP_GWS_DISCONNECTED\",     \"proto\": \"%s\",  \"host\": \"%s\",   \"port\": \"%s\"}";
    public static final String EVENT_JCP_GWS_FAIL           = "{\"what\": \"JCP_GWS_FAIL\",             \"proto\": \"%s\",  \"host\": \"%s\",   \"port\": \"%s\",   \"error\": \"%s\"}";

    public static final String EVENT_OBJ_ADD            = "{\"what\": \"OBJ_ADD\",          \"objId\": \"%s\"}";
    public static final String EVENT_OBJ_REM            = "{\"what\": \"OBJ_REM\",          \"objId\": \"%s\"}";
    public static final String EVENT_OBJ_CONN           = "{\"what\": \"OBJ_CONN\",         \"objId\": \"%s\"}";
    public static final String EVENT_OBJ_DISCONN        = "{\"what\": \"OBJ_DISCONN\",      \"objId\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_STRUCT     = "{\"what\": \"OBJ_UPD_STRUCT\",   \"objId\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_PERMS      = "{\"what\": \"OBJ_UPD_PERMS\",    \"objId\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_PERM_SRV   = "{\"what\": \"OBJ_UPD_PERM_SRV\", \"objId\": \"%s\",  \"new\": \"%s\",        \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_COMP       = "{\"what\": \"OBJ_UPD_COMP\",     \"objId\": \"%s\",  \"compPath\": \"%s\",   \"new\": \"%s\",    \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_INFO_NAME          = "{\"what\": \"OBJ_UPD_INFO_NAME\",        \"objId\": \"%s\",  \"new\": \"%s\", \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_INFO_OWNER         = "{\"what\": \"OBJ_UPD_INFO_OWNER\",       \"objId\": \"%s\",  \"new\": \"%s\", \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_INFO_JOD_VERSION   = "{\"what\": \"OBJ_UPD_INFO_JOD_VERSION\", \"objId\": \"%s\",  \"new\": \"%s\", \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_INFO_MODEL         = "{\"what\": \"OBJ_UPD_INFO_MODEL\",       \"objId\": \"%s\",  \"new\": \"%s\", \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_INFO_BRAND         = "{\"what\": \"OBJ_UPD_INFO_BRAND\",       \"objId\": \"%s\",  \"new\": \"%s\", \"old\": \"%s\"}";
    public static final String EVENT_OBJ_UPD_INFO_LONG_DESCR    = "{\"what\": \"OBJ_UPD_INFO_LONG_DESCR\",  \"objId\": \"%s\",  \"new\": \"%s\", \"old\": \"%s\"}";
    //@formatter:on


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLWebBridge.class);
    private final JSLParams jslParams;
    // jsl instances
    private final Map<String, JSL> jslInstances = new HashMap<>();
    private final Map<String, Timer> jslRemoveTimers = new HashMap<>();
    private final int jslRemoveScheduledDelaySeconds;
    // sse emitters
    private final Map<String, Map<String, SseEmitter>> jslEmitters = new HashMap<>();
    private final Map<SseEmitter, Integer> emittersCounters = new HashMap<>();
    private final Map<SseEmitter, String> emittersIds = new HashMap<>();
    // jsl listeners 1st level
    private final Map<JSL, JSLObjsMngr.ObjsMngrListener> objsMngrListeners = new HashMap<>();
    private final Map<JSL, JCPClient2.ConnectionListener> cloudAPIsListeners = new HashMap<>();
    private final Map<JSL, JCPClient2.LoginListener> cloudAPIsLoginListeners = new HashMap<>();
    private final Map<JSL, PeerConnectionListener> cloudConnectionListeners = new HashMap<>();
    // objs listeners 2nd level
    private final Map<JSL, Map<JSLRemoteObject, ObjInfo.RemoteObjectInfoListener>> objInfoListeners = new HashMap<>();
    private final Map<JSL, Map<JSLRemoteObject, ObjComm.RemoteObjectConnListener>> objCommListeners = new HashMap<>();
    private final Map<JSL, Map<JSLRemoteObject, ObjStruct.RemoteObjectStructListener>> objStructListeners = new HashMap<>();
    private final Map<JSL, Map<JSLRemoteObject, ObjPerms.RemoteObjectPermsListener>> objPermsListeners = new HashMap<>();
    // components listeners 3rd level
    private final Map<JSL, Map<JSLRemoteObject, Map<JSLComponent, Object>>> objComponentListeners = new HashMap<>();
    // heartbeats
    private Timer heartbeatTimer;
    private final int heartbeatTimerDelaySeconds;


    // Constructors

    public JSLWebBridge(JSLParams jslParams,
                        int jslRemoveScheduledDelaySeconds,
                        int heartbeatTimerDelaySeconds) {
        this.jslParams = jslParams;
        this.jslRemoveScheduledDelaySeconds = jslRemoveScheduledDelaySeconds;
        this.heartbeatTimerDelaySeconds = heartbeatTimerDelaySeconds;
        startHeartBeatTimer();
    }

    public void destroyAll() {
        List<String> tmpSessionIds = new ArrayList<>(jslInstances.keySet());
        for (String sessionId : tmpSessionIds)
            destroyJSL(sessionId);
        stopHeartBeatTimer();
    }


    // JSL Instances mngm

    public JSL getJSL(String sessionId) throws JSLNotInitForSessionException {
        JSL jsl = jslInstances.get(sessionId);
        if (jsl == null)
            throw new JSLNotInitForSessionException(sessionId);
        return jsl;
    }

    public String getJSLFullId(String sessionId) throws JSLNotInitForSessionException {
        return getJSL(sessionId).getServiceInfo().getFullId();
    }

    public JSL initJSL(String sessionId, String clientId, String clientSecret, String clientCallback) throws JSLAlreadyInitForSessionException, JSLErrorOnInitException {
        if (jslInstances.get(sessionId) != null)
            throw new JSLAlreadyInitForSessionException(sessionId);

        JSL jsl = doInitAndStartupJSL(sessionId, jslParams, clientId, clientSecret, clientCallback);

        jslInstances.put(sessionId, jsl);
        jslEmitters.put(sessionId, new HashMap<>());

        registerJSLEvents(jsl, sessionId);

        log.info(String.format(LOG_CREATED_JSL, jsl.getServiceInfo().getFullId(), sessionId));
        return jsl;
    }

    private static JSL doInitAndStartupJSL(String sessionId, JSLParams jslParams, String clientId, String clientSecret, String clientCallback) throws JSLErrorOnInitException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JSLSettings_002.JCP_SSL, jslParams.useSSL);
        properties.put(JSLSettings_002.JCP_URL_APIS, jslParams.urlAPIs);
        properties.put(JSLSettings_002.JCP_URL_AUTH, jslParams.urlAuth);
        properties.put(JSLSettings_002.JCP_CLIENT_ID, clientId);
        properties.put(JSLSettings_002.JCP_CLIENT_SECRET, clientSecret);
        properties.put(JSLSettings_002.JCP_CLIENT_CALLBACK, clientCallback!=null ? clientCallback : jslParams.clientCallback);
        properties.put(JSLSettings_002.JSLSRV_ID, clientId);
        properties.put(JSLSettings_002.JSLSRV_NAME, clientId);
        properties.put(JSLSettings_002.JSLCOMM_LOCAL_ENABLED, LOC_COMM_ENABLED);

        try {
            JSL.Settings settings = FactoryJSL.loadSettings(properties, jslParams.jslVersion);
            JSL jsl = FactoryJSL.createJSL(settings, jslParams.jslVersion);
            jsl.startup();
            return jsl;

        } catch (JSL.FactoryException | StateException e) {
            throw new JSLErrorOnInitException(sessionId, e);
        }
    }

    public void destroyJSL(String sessionId) {
        JSL jsl;
        try {
            jsl = getJSL(sessionId);

        } catch (JSLNotInitForSessionException e) {
            JavaAssertions.makeWarning_Failed(e, String.format(ASSERTION_NO_JSL, "destroyJSL", sessionId));
            return;
        }

        Map<String, SseEmitter> emitters = new HashMap<>(jslEmitters.get(sessionId));
        if (emitters.size() > 0)
            for (SseEmitter emitter : emitters.values())
                destroySSEEmitter(sessionId, getClientFullAddress(emitter));

        deregisterJSLEvents(jsl);

        try {
            jsl.shutdown();

        } catch (StateException e) {
            log.warn(String.format(LOG_ERR_REMOVED_JSL, jsl.getServiceInfo().getFullId(), sessionId, e) + ", continue removing JSL Instance", e);
        }

        jslInstances.remove(sessionId);
        jslEmitters.remove(sessionId);
        //session.invalidate();
        log.info(String.format(LOG_REMOVED_JSL, jsl.getServiceInfo().getFullId(), sessionId));
    }


    // JSL Scheduled remove

    private void scheduleRemoveJSLInstance(String sessionId) {
        if (jslRemoveTimers.containsKey(sessionId))
            return;

        Timer timer = JavaTimers.initAndStart(new ScheduleJSLRemoveTimer(sessionId), TH_SCHEDULE_JSL_REMOVE, jslRemoveScheduledDelaySeconds);
        log.debug(String.format(LOG_SCHEDULE_JSL_REMOVE, sessionId));
        jslRemoveTimers.put(sessionId, timer);
    }

    private void abortScheduleRemoveJSLInstance(String sessionId) {
        Timer removedJSLTimer = jslRemoveTimers.remove(sessionId);
        JavaTimers.stopTimer(removedJSLTimer);
    }

    private class ScheduleJSLRemoveTimer implements Runnable {

        private final String sessionId;

        public ScheduleJSLRemoveTimer(String sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public void run() {
            abortScheduleRemoveJSLInstance(sessionId);

            if (jslEmitters.get(sessionId).size() > 0) {
                log.debug(String.format(LOG_SCHEDULE_JSL_REMOVE_ABORT, sessionId));
                return;
            }

            destroyJSL(sessionId);
        }

    }


    // SSE mgnm

    public SseEmitter getJSLEmitter(String sessionId) throws JSLNotInitForSessionException {
        String emitterId = getClientFullAddress();
        SseEmitter emitter = getJSLEmitter(sessionId, emitterId);
        if (emitter == null)
            emitter = createSSEEmitter(sessionId, emitterId);

        return emitter;
    }

    private SseEmitter getJSLEmitter(String sessionId, String emitterId) throws JSLNotInitForSessionException {
        Map<String, SseEmitter> emitters = jslEmitters.get(sessionId);
        if (emitters == null)
            throw new JSLNotInitForSessionException(sessionId);
        return emitters.get(emitterId);
    }

    private SseEmitter createSSEEmitter(String sessionId, String emitterId) throws JSLNotInitForSessionException {
        if (getJSLEmitter(sessionId, emitterId) != null) {
            JavaAssertions.makeWarning_Failed(String.format(ASSERTION_EXIST_EMITTER_CLIENT, "createSSEEmitter", sessionId, emitterId));
            return getJSLEmitter(sessionId, emitterId);
        }

        Map<String, SseEmitter> emitters = jslEmitters.get(sessionId);
        if (emitters == null)
            throw new JSLNotInitForSessionException(sessionId);

        SseEmitter emitter = new SseEmitter(-1L);
        emitters.put(emitterId, emitter);

        emittersCounters.put(emitter, 0);
        emittersIds.put(emitter, emitterId);

        log.info(String.format(LOG_CREATED_EMITTER, getJSLFullId(sessionId), sessionId, getClientFullAddress()));
        emit(sessionId, HEART_BEAT);
        return emitter;
    }

    private void destroySSEEmitter(String sessionId, String emitterId) {
        Map<String, SseEmitter> emitters = jslEmitters.get(sessionId);
        if (emitters == null) {
            JavaAssertions.makeWarning_Failed(String.format(ASSERTION_NO_EMITTERS, "destroySSEEmitter", sessionId));
            return;
        }

        SseEmitter emitter = null;
        try {
            emitter = getJSLEmitter(sessionId, emitterId);
        } catch (JSLNotInitForSessionException ignore) {
        }
        if (emitter == null) {
            JavaAssertions.makeWarning_Failed(String.format(ASSERTION_NO_EMITTER_CLIENT, "destroySSEEmitter", sessionId, emitterId));
            return;
        }

        emittersCounters.remove(emitter);
        emittersIds.remove(emitter);

        emitters.remove(emitterId);
        emitter.complete();

        try {
            log.info(String.format(LOG_REMOVED_EMITTER, getJSLFullId(sessionId), sessionId, emitterId));
        } catch (JSLNotInitForSessionException ignore) {
        }

        //if (emitters.size() == 0)
        //    scheduleRemoveJSLInstance(sessionId);
    }


    // JSL events registration

    private void registerJSLEvents(JSL jsl, String sessionId) {
        jsl.getObjsMngr().addListener(createObjsMngrListener(jsl, sessionId));
        jsl.getCommunication().getCloudAPIs().addConnectionListener(createCloudAPIsListener(jsl, sessionId));
        jsl.getCommunication().getCloudAPIs().addLoginListener(createCloudAPIsLoginListener(jsl, sessionId));
        jsl.getCommunication().getCloudConnection().addListener(createCloudConnectionListener(jsl, sessionId));

        objInfoListeners.put(jsl, new HashMap<>());
        objCommListeners.put(jsl, new HashMap<>());
        objStructListeners.put(jsl, new HashMap<>());
        objPermsListeners.put(jsl, new HashMap<>());

        objComponentListeners.put(jsl, new HashMap<>());
    }

    private void deregisterJSLEvents(JSL jsl) {
        jsl.getObjsMngr().removeListener(objsMngrListeners.remove(jsl));
        jsl.getCommunication().getCloudAPIs().removeConnectionListener(cloudAPIsListeners.remove(jsl));
        jsl.getCommunication().getCloudAPIs().removeLoginListener(cloudAPIsLoginListeners.remove(jsl));
        jsl.getCommunication().getCloudConnection().removeListener(cloudConnectionListeners.remove(jsl));

        for (JSLRemoteObject obj : jsl.getObjsMngr().getAllObjects()) {
            removeObjComponentListenerRecursively(jsl, obj, obj.getStruct().getStructure());
            objComponentListeners.get(jsl).remove(obj);

            objInfoListeners.get(jsl).remove(obj);
            objCommListeners.get(jsl).remove(obj);
            objStructListeners.get(jsl).remove(obj);
            objPermsListeners.get(jsl).remove(obj);
        }

        objInfoListeners.remove(jsl);
        objCommListeners.remove(jsl);
        objStructListeners.remove(jsl);
        objPermsListeners.remove(jsl);
        objComponentListeners.remove(jsl);
    }


    // JSL Listeners 1st level

    private JSLObjsMngr.ObjsMngrListener createObjsMngrListener(JSL jsl, String sessionId) {
        JSLObjsMngr.ObjsMngrListener l = new JSLObjsMngr.ObjsMngrListener() {
            @Override
            public void onObjAdded(JSLRemoteObject obj) {
                emit(sessionId, String.format(EVENT_OBJ_ADD, obj.getId()));

                obj.getInfo().addListener(createObjInfoListener(jsl, obj, sessionId));
                obj.getComm().addListener(createObjCommListener(jsl, obj, sessionId));
                obj.getStruct().addListener(createObjStructListener(jsl, obj, sessionId));
                obj.getPerms().addListener(createObjPermsListener(jsl, obj, sessionId));

                objComponentListeners.get(jsl).put(obj, new HashMap<>());
                addObjComponentListenerRecursively(jsl, obj, obj.getStruct().getStructure(), sessionId);

                log.info(String.format(LOG_JSL_EVENT, jsl.getServiceInfo().getFullId(), String.format("onObjAdded(%s)", obj.getId())));
            }

            @Override
            public void onObjRemoved(JSLRemoteObject obj) {
                emit(sessionId, String.format(EVENT_OBJ_REM, obj.getId()));

                obj.getInfo().removeListener(objInfoListeners.get(jsl).remove(obj));
                obj.getComm().removeListener(objCommListeners.get(jsl).remove(obj));
                obj.getStruct().removeListener(objStructListeners.get(jsl).remove(obj));
                obj.getPerms().removeListener(objPermsListeners.get(jsl).remove(obj));

                removeObjComponentListenerRecursively(jsl, obj, obj.getStruct().getStructure());
                objComponentListeners.get(jsl).remove(obj);

                log.info(String.format(LOG_JSL_EVENT, jsl.getServiceInfo().getFullId(), String.format("onObjRemoved(%s)", obj.getId())));
            }
        };
        objsMngrListeners.put(jsl, l);
        return l;
    }

    private JCPClient2.ConnectionListener createCloudAPIsListener(JSL jsl, String sessionId) {
        JCPClient2.ConnectionListener l = new JCPClient2.ConnectionListener() {
            @Override
            public void onConnected(JCPClient2 jcpClient) {
                emit(sessionId, String.format(EVENT_JCP_APIS_CONN, jcpClient.getAPIsUrl()));

                log.info(String.format(LOG_JSL_EVENT, jsl.getServiceInfo().getFullId(), String.format("JCP APIs::onConnected(%s)", jcpClient.getAPIsUrl())));
            }

            @Override
            public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
                emit(sessionId, String.format(EVENT_JCP_APIS_FAIL_GEN, jcpClient.getAPIsUrl(), t));

                log.warn(String.format(LOG_JSL_ERROR, jsl.getServiceInfo().getFullId(), String.format("JCP APIs::onConnectionFailed(%s) %s", jcpClient.getAPIsUrl(), t.getMessage())));
            }

            @Override
            public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
                emit(sessionId, String.format(EVENT_JCP_APIS_FAIL_AUTH, jcpClient.getAPIsUrl(), t));

                log.warn(String.format(LOG_JSL_ERROR, jsl.getServiceInfo().getFullId(), String.format("JCP APIs::onAuthenticationFailed(%s) %s", jcpClient.getAPIsUrl(), t.getMessage())));
            }

            @Override
            public void onDisconnected(JCPClient2 jcpClient) {
                emit(sessionId, String.format(EVENT_JCP_APIS_DISCONN, jcpClient.getAPIsUrl()));

                log.info(String.format(LOG_JSL_EVENT, jsl.getServiceInfo().getFullId(), String.format("JCP APIs::onDisconnected(%s)", jcpClient.getAPIsUrl())));
            }
        };
        cloudAPIsListeners.put(jsl, l);
        return l;
    }

    private JCPClient2.LoginListener createCloudAPIsLoginListener(JSL jsl, String sessionId) {
        JCPClient2.LoginListener l = new JCPClient2.LoginListener() {
            @Override
            public void onLogin(JCPClient2 jcpClient) {
                emit(sessionId, String.format(EVENT_JCP_APIS_LOGIN, jcpClient.getAPIsUrl(), jsl.getUserMngr().getUserId(), jsl.getUserMngr().getUsername()));

                log.info(String.format(LOG_JSL_EVENT, jsl.getServiceInfo().getFullId(), String.format("JCP APIs::onLogin(%s)", jcpClient.getAPIsUrl())));
            }

            @Override
            public void onLogout(JCPClient2 jcpClient) {
                emit(sessionId, String.format(EVENT_JCP_APIS_LOGOUT, jcpClient.getAPIsUrl()));

                log.info(String.format(LOG_JSL_EVENT, jsl.getServiceInfo().getFullId(), String.format("JCP APIs::onLogin(%s)", jcpClient.getAPIsUrl())));
            }
        };
        cloudAPIsLoginListeners.put(jsl, l);
        return l;
    }

    private PeerConnectionListener createCloudConnectionListener(JSL jsl, String sessionId) {
        PeerConnectionListener l = new PeerConnectionListener() {
            @Override
            public void onConnecting(Peer peer) {
                PeerInfoRemote remoteInfo = peer.getConnectionInfo().getRemoteInfo();
                emit(sessionId, String.format(EVENT_JCP_GWS_CONNECTING, remoteInfo.getProto(), remoteInfo.getHostname(), remoteInfo.getPort()));

                log.info(String.format(LOG_JSL_EVENT, jsl.getServiceInfo().getFullId(), String.format("JCP GWs::onConnecting(%s)", peer.getRemoteId())));
            }

            @Override
            public void onWaiting(Peer peer) {
                PeerInfoRemote remoteInfo = peer.getConnectionInfo().getRemoteInfo();
                emit(sessionId, String.format(EVENT_JCP_GWS_WAITING, remoteInfo.getProto(), remoteInfo.getHostname(), remoteInfo.getPort()));

                log.info(String.format(LOG_JSL_EVENT, jsl.getServiceInfo().getFullId(), String.format("JCP GWs::onWaiting(%s)", peer.getRemoteId())));
            }

            @Override
            public void onConnect(Peer peer) {
                PeerInfoRemote remoteInfo = peer.getConnectionInfo().getRemoteInfo();
                emit(sessionId, String.format(EVENT_JCP_GWS_CONNECTED, remoteInfo.getProto(), remoteInfo.getHostname(), remoteInfo.getPort()));

                log.info(String.format(LOG_JSL_EVENT, jsl.getServiceInfo().getFullId(), String.format("JCP GWs::onConnect(%s)", peer.getRemoteId())));
            }

            @Override
            public void onDisconnecting(Peer peer) {
                PeerInfoRemote remoteInfo = peer.getConnectionInfo().getRemoteInfo();
                emit(sessionId, String.format(EVENT_JCP_GWS_DISCONNECTING, remoteInfo.getProto(), remoteInfo.getHostname(), remoteInfo.getPort()));

                log.info(String.format(LOG_JSL_EVENT, jsl.getServiceInfo().getFullId(), String.format("JCP GWs::onDisconnecting(%s)", peer.getRemoteId())));
            }

            @Override
            public void onDisconnect(Peer peer) {
                PeerInfoRemote remoteInfo = peer.getConnectionInfo().getRemoteInfo();
                emit(sessionId, String.format(EVENT_JCP_GWS_DISCONNECTED, remoteInfo.getProto(), remoteInfo.getHostname(), remoteInfo.getPort()));

                log.info(String.format(LOG_JSL_EVENT, jsl.getServiceInfo().getFullId(), String.format("JCP GWs::onDisconnect(%s)", peer.getRemoteId())));
            }

            @Override
            public void onFail(Peer peer, String failMsg, Throwable t) {
                PeerInfoRemote remoteInfo = peer.getConnectionInfo().getRemoteInfo();
                emit(sessionId, String.format(EVENT_JCP_GWS_FAIL, remoteInfo.getProto(), remoteInfo.getHostname(), remoteInfo.getPort(), t));

                log.warn(String.format(LOG_JSL_ERROR, jsl.getServiceInfo().getFullId(), String.format("JCP GWs::onFail(%s) %s", peer.getRemoteId(), t.getMessage())));
            }
        };
        cloudConnectionListeners.put(jsl, l);
        return l;
    }


    // Objs Listeners 2nd level

    private ObjInfo.RemoteObjectInfoListener createObjInfoListener(JSL jsl, JSLRemoteObject obj, String sessionId) {
        ObjInfo.RemoteObjectInfoListener l = new ObjInfo.RemoteObjectInfoListener() {
            @Override
            public void onNameChanged(JSLRemoteObject obj, String newName, String oldName) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_INFO_NAME, obj.getId(), newName, oldName));
            }

            @Override
            public void onOwnerIdChanged(JSLRemoteObject obj, String newOwnerId, String oldOwnerId) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_INFO_OWNER, obj.getId(), newOwnerId, oldOwnerId));
            }

            @Override
            public void onJODVersionChanged(JSLRemoteObject obj, String newJODVersion, String oldJODVersion) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_INFO_JOD_VERSION, obj.getId(), newJODVersion, oldJODVersion));
            }

            @Override
            public void onModelChanged(JSLRemoteObject obj, String newModel, String oldModel) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_INFO_MODEL, obj.getId(), newModel, oldModel));
            }

            @Override
            public void onBrandChanged(JSLRemoteObject obj, String newBrand, String oldBrand) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_INFO_BRAND, obj.getId(), newBrand, oldBrand));
            }

            @Override
            public void onLongDescrChanged(JSLRemoteObject obj, String newLongDescr, String oldLongDescr) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_INFO_LONG_DESCR, obj.getId(), newLongDescr, oldLongDescr));
            }
        };
        objInfoListeners.get(jsl).put(obj, l);
        return l;
    }

    private ObjComm.RemoteObjectConnListener createObjCommListener(JSL jsl, JSLRemoteObject obj, String sessionId) {
        ObjComm.RemoteObjectConnListener l = new ObjComm.RemoteObjectConnListener() {
            @Override
            public void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            }

            @Override
            public void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            }

            @Override
            public void onCloudConnected(JSLRemoteObject obj) {
                emit(sessionId, String.format(EVENT_OBJ_CONN, obj.getId()));
            }

            @Override
            public void onCloudDisconnected(JSLRemoteObject obj) {
                emit(sessionId, String.format(EVENT_OBJ_DISCONN, obj.getId()));
            }
        };
        objCommListeners.get(jsl).put(obj, l);
        return l;
    }

    private ObjStruct.RemoteObjectStructListener createObjStructListener(JSL jsl, JSLRemoteObject obj, String sessionId) {
        ObjStruct.RemoteObjectStructListener l = new ObjStruct.RemoteObjectStructListener() {
            @Override
            public void onStructureChanged(JSLRemoteObject obj, JSLRoot newRoot) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_STRUCT, obj.getId()));

                removeObjComponentListenerRecursively(jsl, obj, newRoot);
                addObjComponentListenerRecursively(jsl, obj, newRoot, sessionId);
            }
        };
        objStructListeners.get(jsl).put(obj, l);
        return l;
    }

    private ObjPerms.RemoteObjectPermsListener createObjPermsListener(JSL jsl, JSLRemoteObject obj, String sessionId) {
        ObjPerms.RemoteObjectPermsListener l = new ObjPerms.RemoteObjectPermsListener() {
            @Override
            public void onPermissionsChanged(JSLRemoteObject obj, List<JOSPPerm> newPerms, List<JOSPPerm> oldPerms) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_PERMS, obj.getId()));
            }

            @Override
            public void onServicePermChanged(JSLRemoteObject obj, JOSPPerm.Connection connType, JOSPPerm.Type newPermType, JOSPPerm.Type oldPermType) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_PERM_SRV, obj.getId(), newPermType, oldPermType));
            }
        };
        objPermsListeners.get(jsl).put(obj, l);
        return l;
    }


    // Components Listeners 3rd level

    private void addObjComponentListenerRecursively(JSL jsl, JSLRemoteObject obj, JSLComponent component, String sessionId) {
        if (component instanceof JSLBooleanState)
            ((JSLBooleanState) component).addListener(createObjCompBooleanListener(jsl, obj, (JSLBooleanState) component, sessionId));

        else if (component instanceof JSLRangeState)
            ((JSLRangeState) component).addListener(createObjCompRangeListener(jsl, obj, (JSLRangeState) component, sessionId));

        else if (component instanceof JSLContainer)
            for (JSLComponent c : ((JSLContainer) component).getComponents())
                addObjComponentListenerRecursively(jsl, obj, c, sessionId);
    }

    private JSLBooleanState.BooleanStateListener createObjCompBooleanListener(JSL jsl, JSLRemoteObject obj, JSLBooleanState compBoolean, String sessionId) {
        JSLBooleanState.BooleanStateListener l = new JSLBooleanState.BooleanStateListener() {

            @Override
            public void onStateChanged(JSLBooleanState component, boolean newState, boolean oldState) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_COMP, obj.getId(), component.getPath().getString(), newState, oldState));
            }

        };
        objComponentListeners.get(jsl).get(obj).put(compBoolean, l);
        return l;
    }

    private JSLRangeState.RangeStateListener createObjCompRangeListener(JSL jsl, JSLRemoteObject obj, JSLRangeState compRange, String sessionId) {
        JSLRangeState.RangeStateListener l = new JSLRangeState.RangeStateListener() {

            @Override
            public void onStateChanged(JSLRangeState component, double newState, double oldState) {
                emit(sessionId, String.format(EVENT_OBJ_UPD_COMP, obj.getId(), component.getPath().getString(), newState, oldState));
            }

            @Override
            public void onMinReached(JSLRangeState component, double state, double min) {
            }

            @Override
            public void onMaxReached(JSLRangeState component, double state, double max) {
            }

        };
        objComponentListeners.get(jsl).get(obj).put(compRange, l);
        return l;
    }

    private void removeObjComponentListenerRecursively(JSL jsl, JSLRemoteObject obj, JSLComponent component) {
        if (component instanceof JSLBooleanState)
            ((JSLBooleanState) component).removeListener((JSLBooleanState.BooleanStateListener) objComponentListeners.get(jsl).get(obj).remove(component));

        else if (component instanceof JSLRangeState)
            ((JSLRangeState) component).removeListener((JSLRangeState.RangeStateListener) objComponentListeners.get(jsl).get(obj).remove(component));

        else if (component instanceof JSLContainer)
            for (JSLComponent c : ((JSLContainer) component).getComponents())
                removeObjComponentListenerRecursively(jsl, obj, c);
    }


    // Emitter send methods

    private void emit(String data) {
        for (String sessionId : jslInstances.keySet())
            emit(sessionId, data);
    }

    private void emit(String sessionId, String data) {
        if (jslEmitters.get(sessionId) == null) {
            JavaAssertions.makeWarning_Failed(String.format(ASSERTION_NO_EMITTERS, "emit", sessionId) + ", ??remove JSL Instance??");
            //destroyJSL(sessionId);
            return;
        }
        Collection<SseEmitter> emitters = jslEmitters.get(sessionId).values();

        //if (emitters.size() == 0) {
        //    scheduleRemoveJSLInstance(sessionId);
        //    return;
        //}

        List<SseEmitter> emittersTmp = new ArrayList<>(emitters);
        for (SseEmitter emitter : emittersTmp)
            doEmit(emitter, sessionId, data);
    }

    private void doEmit(SseEmitter emitter, String sessionId, String data) {
        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .data(data)
                .id(String.valueOf(increaseSSECounter(emitter)));

        String srvFullId = "N/A";
        try {
            srvFullId = getJSLFullId(sessionId);
        } catch (JSLNotInitForSessionException ignore) {
        }
        String emitterId = getClientFullAddress(emitter);

        try {
            emitter.send(event);
            log.trace(String.format(LOG_SEND_EVENT, srvFullId, sessionId, emitterId, data));

        } catch (Exception e) {
            if (e instanceof ClientAbortException)
                log.debug(String.format(LOG_SEND_EVENT_DISCONNECTED, srvFullId, sessionId, emitterId) + ", remove emitter");
            else
                log.warn(String.format(LOG_ERR_SEND_EVENT, srvFullId, sessionId, emitterId, data, e) + ", remove emitter", e);
            destroySSEEmitter(sessionId, emitterId);
        }
    }

    private int increaseSSECounter(SseEmitter emitter) {
        int newCount = emittersCounters.get(emitter) + 1;
        emittersCounters.put(emitter, newCount);
        return newCount;
    }


    // Session utils

    private String getClientAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null)
            ipAddress = request.getRemoteAddr();
        return ipAddress;
    }

    private int getClientPort() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getRemotePort();
    }

    private String getClientFullAddress() {
        return String.format("%s:%d", getClientAddress(), getClientPort());
    }

    private String getClientFullAddress(SseEmitter emitter) {
        return emittersIds.get(emitter);
    }


    // Heartbeat timer

    private void startHeartBeatTimer() {
        heartbeatTimer = JavaTimers.initAndStart(new HeartBeatTimer(), TH_EMITTERS_HEARTBEAT, heartbeatTimerDelaySeconds * 1000L, heartbeatTimerDelaySeconds * 1000L);
    }

    private void stopHeartBeatTimer() {
        JavaTimers.stopTimer(heartbeatTimer);
        heartbeatTimer = null;
    }

    private class HeartBeatTimer implements Runnable {

        @Override
        public void run() {
            emit(HEART_BEAT);

        }

    }

}
