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

package com.robypomper.josp.jcp.jslwebbridge.services;

import com.robypomper.josp.jcp.jslwebbridge.exceptions.JSLAlreadyInitForSessionException;
import com.robypomper.josp.jcp.jslwebbridge.exceptions.JSLErrorOnInitException;
import com.robypomper.josp.jcp.jslwebbridge.exceptions.JSLNotInitForSessionException;
import com.robypomper.josp.jcp.jslwebbridge.webbridge.JSLParams;
import com.robypomper.josp.jcp.jslwebbridge.webbridge.JSLWebBridge;
import com.robypomper.josp.jsl.JSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JSLWebBridgeService {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLWebBridgeService.class);
    private final JSLWebBridge webBridge;
    private static final Map<String, HttpSession> sessions = new HashMap<>();


    // Constructors

    @Autowired
    protected JSLWebBridgeService(JSLParams jslParams,
                                  @Value("${jcp.jsl.remove.delay:900}") int jslRemoveScheduledDelaySeconds,
                                  @Value("${jcp.jsl.heartbeat.delay:60}") int heartbeatTimerDelaySeconds) {
        webBridge = new JSLWebBridge(jslParams, jslRemoveScheduledDelaySeconds, heartbeatTimerDelaySeconds);
    }


    @PreDestroy
    public void destroy() {
        webBridge.destroyAll();
        log.trace("JSL webBridge service destroyed");
    }


    // Getters

    public JSL getJSL(String sessionId) throws JSLNotInitForSessionException {
        return webBridge.getJSL(sessionId);
    }

    public SseEmitter getJSLEmitter(String sessionId) throws JSLNotInitForSessionException {
        return webBridge.getJSLEmitter(sessionId);
    }


    // Creators

    public JSL initJSL(String sessionId, String clientId, String clientSecret, String clientCallback) throws JSLAlreadyInitForSessionException, JSLErrorOnInitException {
        return webBridge.initJSL(sessionId, clientId, clientSecret, clientCallback);
    }


    // Sessions

    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> sessionListenerWithMetrics() {
        ServletListenerRegistrationBean<HttpSessionListener> listenerRegBean = new ServletListenerRegistrationBean<>();

        listenerRegBean.setListener(new HttpSessionListener() {
            public void sessionCreated(HttpSessionEvent hse) {
                sessions.put(hse.getSession().getId(), hse.getSession());
            }

            public void sessionDestroyed(HttpSessionEvent hse) {
                String sessionId = hse.getSession().getId();
                log.info(String.format("Terminated session '%s'", sessionId));
                webBridge.destroyJSL(sessionId);
                sessions.remove(hse.getSession().getId());
            }

        });

        return listenerRegBean;
    }

    public List<HttpSession> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }

    public HttpSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

}
