/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2024 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.states.JSLState;
import com.robypomper.josp.states.StateException;

public class CmdsJSL {

    private final JSL jsl;

    public CmdsJSL(JSL jsl) {
        this.jsl = jsl;
    }

    /**
     * Print current JSL Service status.
     *
     * @return the JSL Service status.
     */
    @Command(description = "Print current JSL Object status.")
    public JSLState jslState() {
        return jsl.getState();
    }

    /**
     * Connect current JSL Service.
     *
     * @return success or error message.
     */
    @Command(description = "Print current JSL instance info.")
    public String jslInstanceInfo() {
        jsl.printInstanceInfo();

        return "OK";
    }

    /**
     * Connect current JSL Service.
     *
     * @return success or error message.
     */
    @Command(description = "Startup current JSL instance.")
    public String jslInstanceStartup() {
        try {
            jsl.startup();
        } catch (StateException e) {
            return "Error on startup JSL service because " + e.getMessage();
        }

        if (jsl.getState() != JSLState.RUN)
            return "JSL service NOT started.";

        return "JSL service started successfully.";
    }

    /**
     * Disconnect current JSL Service.
     *
     * @return success or error message.
     */
    @Command(description = "Shut down current JSL instance.")
    public String jslInstanceShutdown() {
        try {
            jsl.shutdown();
        } catch (StateException e) {
            return "Error on shut down JSL service because " + e.getMessage();
        }

        if (jsl.getState() != JSLState.STOP)
            return "JSL Service NOT shut down.";

        return "JSL Service shut down successfully.";
    }

    /**
     * Restart current JSL Service.
     *
     * @return success or error message.
     */
    @Command(description = "Restart current JSL instance.")
    public String jslInstanceRestart() {
        try {
            jsl.restart();

        } catch (StateException e) {
            return "Error on restart JSL service because " + e.getMessage();
        }

        if (jsl.getState() != JSLState.RUN)
            return "JSL Service NOT restarted.";

        return "JSL Service restarted successfully.";
    }

}
