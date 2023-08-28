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

package com.robypomper.josp.jod.executor.impls.http;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.DefaultHTTPClient;
import com.robypomper.josp.clients.HTTPClient;
import com.robypomper.josp.jod.executor.AbsJODWorker;
import com.robypomper.josp.jod.executor.JODWorker;
import com.robypomper.josp.jod.executor.Substitutions;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.protocol.JOSPProtocol;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class HTTPInternal {

    // Class constants

    //@formatter:off
    private static final String PROP_REQ_URL                    = "requestUrl";
    private static final String PROP_REQ_VERB                   = "requestVerb";
    private static final String PROP_REQ_TIMEOUT                = "requestTimeOut";
    private static final String PROP_REQ_IGNORE_SSL_HOSTS       = "requestIgnoreSSLHosts";
    private static final String PROP_AVAILABILITY_RETRY_SECONDS = "availabilityRetrySeconds";
    //private static final String PROP_FREQ = "freq";                 // in seconds
    //public static final int UNIX_SHELL_POLLING_TIME = 30000;        // in milliseconds
    //@formatter:onn

    // Internal vars
    private final AbsJODWorker worker;
    private final String name;
    private final String proto;
    private final String configsStr;
    private final JODComponent component;
    private final String requestUrl;
    private final Verb requestVerb;
    private final int requestTimeOutSeconds;
    private final int availabilityRetrySeconds;
    private final boolean ignoreSSLHostnames;
    private HTTPClient client = null;
    private String lastUrl = null;


    // Constructor

    public HTTPInternal(AbsJODWorker worker, String name, String proto, String configsStr, JODComponent component) throws JODWorker.ParsingPropertyException, JODWorker.MissingPropertyException {
        this.worker = worker;
        this.name = name;
        this.proto = proto;
        this.configsStr = configsStr;
        this.component = component;

        // Parse configs
        Map<String, String> configs = AbsJODWorker.splitConfigsStrings(configsStr);
        requestUrl = worker.parseConfigString(configs, PROP_REQ_URL);
        requestVerb = Verb.valueOf(worker.parseConfigString(configs, PROP_REQ_VERB, Verb.GET.name()));
        availabilityRetrySeconds = worker.parseConfigInt(configs, PROP_REQ_TIMEOUT, "30");
        ignoreSSLHostnames = worker.parseConfigBoolean(configs, PROP_REQ_IGNORE_SSL_HOSTS, "false");
        requestTimeOutSeconds = worker.parseConfigInt(configs, PROP_AVAILABILITY_RETRY_SECONDS, "10");

        try {
            URL url = new URL(requestUrl);
            String httpBaseUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() != -1 ? ":" + url.getPort() : "");
            client = new DefaultHTTPClient(httpBaseUrl, "httpServerName", requestTimeOutSeconds, availabilityRetrySeconds, ignoreSSLHostnames);
        } catch (MalformedURLException ignore) {
        }
    }

    public String getStateRequest() {
        return new Substitutions(requestUrl)
                //.substituteObject(jod.getObjectInfo())
                //.substituteObjectConfigs(jod.getObjectInfo())
                .substituteComponent(component)
                .substituteState((JODState) component)
                .toString();
    }

    public String getActionRequest(JOSPProtocol.ActionCmd commandAction) {
        return new Substitutions(requestUrl)
                //.substituteObject(jod.getObjectInfo())
                //.substituteObjectConfigs(jod.getObjectInfo())
                .substituteComponent(component)
                .substituteState((JODState) component)
                .substituteAction(commandAction)
                .toString();
    }

    public String execRequest(String strUrl) throws HTTPClient.ResponseException, HTTPClient.RequestException, MalformedURLException {
        lastUrl = strUrl;
        URL url = new URL(strUrl);
        HTTPClient tempClient = client;
        if (tempClient == null) {
            String httpBaseUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() != -1 ? ":" + url.getPort() : "");
            tempClient = new DefaultHTTPClient(httpBaseUrl, "HTTP Server", requestTimeOutSeconds, availabilityRetrySeconds, ignoreSSLHostnames);
        }
        String path = url.getFile();

        return tempClient.execReq(requestVerb, path, String.class);
    }

    public String execRequest(String strUrl, String reqBody) throws HTTPClient.ResponseException, HTTPClient.RequestException, MalformedURLException {
        if (reqBody.isEmpty() || requestVerb == Verb.GET)
            throw new HTTPClient.RequestException("Can't send body on GET request.");

        lastUrl = strUrl;
        URL url = new URL(strUrl);
        HTTPClient tempClient = client;
        if (tempClient == null) {
            String httpBaseUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() != -1 ? ":" + url.getPort() : "");
            tempClient = new DefaultHTTPClient(httpBaseUrl, "HTTP Server", requestTimeOutSeconds, availabilityRetrySeconds, ignoreSSLHostnames);
        }
        String path = url.getFile();

        return tempClient.execReq(requestVerb, path, String.class, reqBody);
    }

    public String getLastUrl() {
        return lastUrl;
    }

}
