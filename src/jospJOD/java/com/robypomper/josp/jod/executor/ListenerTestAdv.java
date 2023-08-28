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

package com.robypomper.josp.jod.executor;

import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.pillars.JODBooleanState;
import com.robypomper.josp.jod.structure.pillars.JODRangeState;
import com.robypomper.log.Mrk_JOD;

import java.util.Map;


/**
 * JOD Listener advanced test.
 * <p>
 * This test show how to initialise current Listener using properties from
 * <code>configStr</code> constructor's param. This string is set by the user and
 * contain all properties required to the Listener. If some property is missing,
 * a {@link com.robypomper.josp.jod.executor.JODWorker.MissingPropertyException}
 * will throw from the Listener constructor (and catch from the FactoryJODListener).
 * <p>
 * Each {@link #frequency} interactions of the server's infinite loop
 * (from {@link #getServerLoop()} method) call the
 * {@link JODState} sub class's <code>setUpdate(...)</code> method.
 * <p>
 * The sleeping time between each loop interaction can be set via
 * {@value #PROP_SLEEP_TIME} configs string (default = 1000ms).
 * <p>
 * Print log messages, from dedicated thread, on server startup and shutdown.
 */
public class ListenerTestAdv extends AbsJODListenerLoop {

    // Class constants

    private static final String PROP_FREQUENCY = "frequency";
    private static final String PROP_SLEEP_TIME = "sleep";


    // Internal vars

    private int frequency = 1;
    private int sleepTime = 1000;


    // Constructor

    /**
     * Default ListenerTest constructor.
     *
     * @param name       name of the listener.
     * @param proto      proto of the listener.
     * @param configsStr configs string, parse {@value #PROP_FREQUENCY}(int) and
     *                   {@value #PROP_SLEEP_TIME}(int)properties.
     */
    public ListenerTestAdv(String name, String proto, String configsStr, JODComponent component) throws ParsingPropertyException {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerTestAdv for component '%s' init with config string '%s://%s'", getName(), proto, configsStr));

        Map<String, String> configs = splitConfigsStrings(configsStr);
        frequency = parseConfigInt(configs, PROP_FREQUENCY, Integer.toString(frequency));
        sleepTime = parseConfigInt(configs, PROP_SLEEP_TIME, Integer.toString(sleepTime));
    }


    // Mngm

    /**
     * Server Loop method: print a log messages and start infinite loop where
     * each {@link #frequency} (default: 10) interactions call the
     * {@link JODState} sub class's <code>setUpdate(...)</code> method.
     * <p>
     * Each loop interaction start a {@link Thread#sleep(long)} for {@link #sleepTime}
     * millisecond (default: 10x1000).
     */
    @Override
    protected void getServerLoop() {
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerTestAdv for component '%s' with frequency='%d' and sleepTime='%d'ms", getName(), frequency, sleepTime));

        int count = 0;
        while (!mustShoutingDown()) {
            count++;
            if (count % frequency == 0) {
                log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerTestAdv for component '%s' of proto '%s' listened", getName(), getProto()));

                // For each JODState supported
                if (getComponent() instanceof JODBooleanState)
                    ((JODBooleanState) getComponent()).setUpdate(true);
                else if (getComponent() instanceof JODRangeState)
                    ((JODRangeState) getComponent()).setUpdate(5);

            }
            try {
                //noinspection BusyWait
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                break;
            }
        }

        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerTestAdv for component '%s' terminated", getName()));
    }

}
