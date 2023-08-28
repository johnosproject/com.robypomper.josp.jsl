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

import com.robypomper.josp.jod.executor.AbsJODWorker;
import com.robypomper.josp.jod.executor.Substitutions;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.protocol.JOSPProtocol;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

public class EvaluatorInternal {

    // Class constants

    //@formatter:off
    private static final String SCRIPT_VAR_HTTP_RESULT  = "httpResult";

    private static final String PROP_EVAL_SCRIPT                = "eval";
    private static final String PROP_EVAL_SCRIPT_DEF            = "{" + SCRIPT_VAR_HTTP_RESULT + "}";
    //@formatter:onn

    // Internal vars
    private final AbsJODWorker worker;
    private final String name;
    private final String proto;
    private final String configsStr;
    private final JODComponent component;
    private final String evalScript;
    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");


    // Constructor

    public EvaluatorInternal(AbsJODWorker worker, String name, String proto, String configsStr, JODComponent component) {
        this.worker = worker;
        this.name = name;
        this.proto = proto;
        this.configsStr = configsStr;
        this.component = component;

        // Parse configs
        Map<String, String> configs = AbsJODWorker.splitConfigsStrings(configsStr);
        evalScript = worker.parseConfigString(configs, PROP_EVAL_SCRIPT, PROP_EVAL_SCRIPT_DEF);
    }

    public String evaluate(String result) throws EvaluationException {
        return evaluate(result,null);
    }

    public String evaluate(String result, JOSPProtocol.ActionCmd commandAction) throws EvaluationException {
        if (evalScript.isEmpty())
            return result;

        String evalResult;
        synchronized (engine) {
            Substitutions evalScriptSubstitution = new Substitutions(evalScript)
                    //.substituteObject(jod.getObjectInfo())
                    //.substituteObjectConfigs(jod.getObjectInfo())
                    .substituteComponent(component)
                    .substituteState((JODState) component);
            if (commandAction!=null)
                    evalScriptSubstitution.substituteAction(commandAction);
            String evalScriptUpdated = evalScriptSubstitution.toString();
            engine.put(SCRIPT_VAR_HTTP_RESULT, result);
            try {
                evalResult = engine.eval(evalScriptUpdated).toString();

            } catch (ScriptException e) {
                throw new EvaluationException(e);
            }

            engine.put(SCRIPT_VAR_HTTP_RESULT, null);
        }
        return evalResult;
    }


    // Exceptions

    public static class EvaluationException extends Throwable {

        public EvaluationException(Throwable cause) {
            super(cause);
        }

    }

}
