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

import com.robypomper.java.JavaDate;
import com.robypomper.josp.clients.HTTPClient;
import com.robypomper.josp.jod.executor.AbsJODPuller;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.log.Mrk_JOD;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * JOD Puller test.
 */
public class PullerHTTP extends AbsJODPuller {
    class CachedResponse {
        Date date;
        String response;
    }
    // Class constants

    private static final String PROP_FREQ_SEC = "freq";                 // in seconds
    private static final String PROP_CACHE_TIMEOUT = "cache_timeout";  // in seconds
    public static final int DEF_CACHE_TIMEOUT = 30000;

    // Internal vars

    private final HTTPInternal http;
    private final FormatterInternal formatter;
    private final EvaluatorInternal evaluator;
    private final int freq_ms;
    private final int cache_timeout_ms;
    private String lastResponse = "";
    private String lastResult = "";
    private static Map<String, CachedResponse> cache = new HashMap();


    // Constructor

    /**
     * Default PullerHTTP constructor.
     *
     * @param name       name of the puller.
     * @param proto      proto of the puller.
     * @param configsStr configs string, can be an empty string.
     */
    public PullerHTTP(String name, String proto, String configsStr, JODComponent component) throws ParsingPropertyException, MissingPropertyException {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTML for component '%s' init with config string '%s://%s'.", getName(), proto, configsStr));

        http = new HTTPInternal(this, name, proto, configsStr, component);
        formatter = new FormatterInternal(this, name, proto, configsStr, component);
        evaluator = new EvaluatorInternal(this, name, proto, configsStr, component);
        Map<String, String> configs = splitConfigsStrings(configsStr);
        freq_ms = parseConfigInt(configs, PROP_FREQ_SEC, Integer.toString(AbsJODPuller.DEF_POLLING_TIME / 1000)) * 1000;
        cache_timeout_ms = parseConfigInt(configs, PROP_CACHE_TIMEOUT, Integer.toString(DEF_CACHE_TIMEOUT / 1000)) * 1000;
    }

    protected long getPollingTime() {
        return freq_ms;
    }


    // Mngm

    /**
     * Pull method: print a log message and call the {@link JODState} sub
     * class's <code>setUpdate(...)</code> method.
     */
    @Override
    public void pull() {
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTML '%s' of proto '%s' pulling", getName(), getProto()));

        String requestUrl = http.getStateRequest();

        String response = null;
        synchronized (cache) {
            CachedResponse cachedRes = cache.get(requestUrl);
            if (cachedRes != null) {
                Date lastAcceptableDate = new Date(JavaDate.getNowDate().getTime() - cache_timeout_ms);
                if (cachedRes.date.after(lastAcceptableDate)) {
                    log.debug(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' use cached response", getName(), getProto()));
                    response = cachedRes.response;
                }
            }

            if (response == null) {
                try {
                    response = http.execRequest(requestUrl);

                } catch (HTTPClient.RequestException | MalformedURLException | HTTPClient.ResponseException e) {
                    log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' error on exec request '%s' because '%s'", getName(), getProto(), requestUrl, e.getMessage()), e);
                    return;
                }

                CachedResponse toCacheRes = new CachedResponse();
                toCacheRes.date = JavaDate.getNowDate();
                toCacheRes.response = response;
                cache.put(requestUrl, toCacheRes);

                if (lastResponse.compareTo(response) == 0) {
                    log.debug(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' get same response as last attempt, skip it", getName(), getProto()));
                    return;
                }
                lastResponse = response;
            }
        }

        String result;
        try {
            result = formatter.parse(response);
        } catch (FormatterInternal.ParsingException | FormatterInternal.PathNotFoundException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' error on parsing request '%s''s response because '%s'", getName(), getProto(), requestUrl, e.getMessage()), e);
            log.debug(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' error on parsing response '%s'", getName(), getProto(), response));
            return;
        }

        log.debug(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' read state '%s'", getName(), getProto(), result));

        if (lastResult.compareTo(result) == 0){
            log.debug(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' get same result as last attempt, skip it", getName(), getProto()));
            return;
        }
        lastResult = result;

        String resultEvaluated;
        try {
            resultEvaluated = evaluator.evaluate(result);
        } catch (EvaluatorInternal.EvaluationException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' error on evaluating request '%s''s result because '%s'", getName(), getProto(), requestUrl, e.getMessage()), e);
            log.debug(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' error on evaluating result '%s'", getName(), getProto(), result));
            return;
        }

        log.debug(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTML '%s' of proto '%s' pulling url '%s' and get '%s' value from '%s' result", getName(), getProto(), http.getLastUrl(), resultEvaluated, result));

        if (!convertAndSetStatus(resultEvaluated))
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' can't update his component because not supported (%s)", getName(), getComponent().getClass().getSimpleName()));
        //// For each JODState supported
        //if (getComponent() instanceof JODBooleanState)
        //    ((JODBooleanState) getComponent()).setUpdate(Boolean.parseBoolean(resultEvaluated));
        //else if (getComponent() instanceof JODRangeState)
        //    ((JODRangeState) getComponent()).setUpdate(Double.parseDouble(resultEvaluated));
    }

}
