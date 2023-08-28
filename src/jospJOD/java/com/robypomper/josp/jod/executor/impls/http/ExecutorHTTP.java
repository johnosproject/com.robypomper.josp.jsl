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


import com.robypomper.josp.clients.HTTPClient;
import com.robypomper.josp.jod.executor.AbsJODExecutor;
import com.robypomper.josp.jod.executor.Substitutions;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_JOD;

import java.net.MalformedURLException;
import java.util.Map;


/**
 * JOD Executor for HTTP.
 */
public class ExecutorHTTP extends AbsJODExecutor implements JODBooleanAction.JOSPBoolean.Executor, JODRangeAction.JOSPRange.Executor {

    // Class constants

    private static final String PROP_REQ_BODY = "requestBody";
    private static final String PROP_EXPECTED_SUCCESS = "requestSuccess";


    // Internal vars

    // Configs
    private final String requestBody;
    private final String requestSuccess;
    // Utils
    private final HTTPInternal http;
    private final FormatterInternal formatter;
    private final EvaluatorInternal evaluator;


    // Constructor

    /**
     * Default ExecutorHTTP constructor.
     *
     * @param name       name of the executor.
     * @param proto      proto of the executor.
     * @param configsStr configs string, can be an empty string.
     */
    public ExecutorHTTP(String name, String proto, String configsStr, JODComponent component) throws ParsingPropertyException, MissingPropertyException {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorHTTP for component '%s' init with config string '%s://%s'", getName(), proto, configsStr));

        http = new HTTPInternal(this, name, proto, configsStr, component);
        formatter = new FormatterInternal(this, name, proto, configsStr, component);
        evaluator = new EvaluatorInternal(this, name, proto, configsStr, component);

        Map<String, String> configs = splitConfigsStrings(configsStr);
        requestBody = parseConfigString(configs, PROP_REQ_BODY, "");
        requestSuccess = parseConfigString(configs, PROP_EXPECTED_SUCCESS, "");
    }


    // Mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exec(JOSPProtocol.ActionCmd commandAction, JODBooleanAction.JOSPBoolean cmdAction) {
        System.out.printf("\n\nReceived action command from %s::%s (srv::usr) for %s::%s (obj::component)%n", commandAction.getServiceId(), commandAction.getUserId(), commandAction.getObjectId(), commandAction.getComponentPath());
        System.out.printf("\tnewState %b%n", cmdAction.newState);
        System.out.printf("\toldState %b%n", cmdAction.oldState);

        return exec(commandAction);
    }

    @Override
    public boolean exec(JOSPProtocol.ActionCmd commandAction, JODRangeAction.JOSPRange cmdAction) {
        System.out.printf("\n\nReceived action command from %s::%s (srv::usr) for %s::%s (obj::component)%n", commandAction.getServiceId(), commandAction.getUserId(), commandAction.getObjectId(), commandAction.getComponentPath());
        System.out.printf("\tnewState %f%n", cmdAction.newState);
        System.out.printf("\toldState %f%n", cmdAction.oldState);

        return exec(commandAction);
    }

    private boolean exec(JOSPProtocol.ActionCmd commandAction) {
        String response;
        try {
            String requestUrl = http.getActionRequest(commandAction);
            String requestBodyStr = new Substitutions(requestBody)
                    //.substituteObject(jod.getObjectInfo())
                    //.substituteObjectConfigs(jod.getObjectInfo())
                    .substituteComponent(getComponent())
                    .substituteState((JODState)getComponent())
                    .substituteAction(commandAction)
                    .toString();
            response = http.execRequest(requestUrl, requestBodyStr);

        } catch (HTTPClient.RequestException | MalformedURLException | HTTPClient.ResponseException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorHTTP '%s' of proto '%s' error on pulling url '%s'", getName(), getProto(), http.getLastUrl()));
            return true;
        }

        String result;
        try {
            result = formatter.parse(response);

        } catch (FormatterInternal.ParsingException | FormatterInternal.PathNotFoundException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorHTTP '%s' of proto '%s' error on parsing response '%s'", getName(), getProto(), response), e);
            return true;
        }

        try {
            result = evaluator.evaluate(result,commandAction);
        } catch (EvaluatorInternal.EvaluationException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorHTTP '%s' of proto '%s' error on evaluating result '%s'", getName(), getProto(), result), e);
            return true;
        }

        ((JODState) getComponent()).forceCheckState();

        // returned value indicate if this method can exec the request,
        // not the request exit status
        //if (!requestSuccess.isEmpty())
        //    return result.equalsIgnoreCase(requestSuccess);

        return true;
    }

}
