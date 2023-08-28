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
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Convenient Listener class used by {@link JODListener} implementations that
 * provide server basic class.
 */
public abstract class AbsJODListenerLoop extends AbsJODListener {

    // Class constants

    public static final String TH_LISTENER_NAME_FORMAT = "_LIST_%s";


    // Internal vars

    protected static final Logger log = LogManager.getLogger();
    private Thread thread;
    private boolean mustStop = false;


    // Constructor

    /**
     * {@inheritDoc}
     */
    public AbsJODListenerLoop(String name, String proto, JODComponent component) {
        super(name, proto, component);
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return thread != null;
    }

    /**
     * This method can be used in JOD Listeners implementations when check the
     * server's infinite loop.
     *
     * @return <code>true</code> if and only if the listener's server must shutdown.
     */
    protected boolean mustShoutingDown() {
        return mustStop;
    }

    /**
     * Return server's infinte loop function.
     * <p>
     * Implementation of this function must execute the main loop of the listener
     * and when received updates then send the update to the corresponding JOD
     * Component via the <code>update()</code> method.
     * <p>
     * This runnable will be executed on listener start and stopped (interrupted)
     * on listener stop. On stop, the server's thread receive an interrupt and
     * the method {@link #mustShoutingDown()} return <code>true</code>.
     */
    @SuppressWarnings("JavadocReference")
    protected abstract void getServerLoop();

    /**
     * Wait time in ms for thread terminating.
     * <p>
     * The value can be get from poller's settings or hardcoded by poller
     * implementation.
     *
     * @return the waiting time in ms.
     */
    protected long getJoinTime() {
        return DEF_JOIN_TIME;
    }


    // Mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void listen() {
        log.info(Mrk_JOD.JOD_EXEC_SUB, String.format("Start '%s' listener", getName()));
        if (isEnabled()) return;

        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Starting listener server");
        mustStop = false;
        //noinspection Convert2Lambda
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                log.debug(Mrk_JOD.JOD_EXEC_SUB, String.format("Thread listener server '%s' started", Thread.currentThread().getName()));
                while (!mustShoutingDown()) {
                    try {
                        getServerLoop();
                    } catch (Throwable t) {
                        log.warn(Mrk_JOD.JOD_EXEC_SUB, String.format("Thread listener server '%s' thrown exception: %s", Thread.currentThread().getName(), t.getMessage()), t);
                    }
                }
                log.trace(Mrk_JOD.JOD_EXEC_SUB, String.format("Thread listener server '%s' terminated", Thread.currentThread().getName()));
            }
        });
        thread.setName(String.format(TH_LISTENER_NAME_FORMAT, getName()));
        log.debug(Mrk_JOD.JOD_EXEC_SUB, String.format("Starting thread listener server '%s'", thread.getName()));
        thread.start();

        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Listener server started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void halt() {
        log.info(Mrk_JOD.JOD_EXEC_SUB, String.format("Stop '%s' listener server", getName()));
        if (!isEnabled()) return;

        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Stopping listener server");
        log.debug(Mrk_JOD.JOD_EXEC_SUB, String.format("Terminating thread listener server '%s'", thread.getName()));
        mustStop = true;
        thread.interrupt();
        try {
            thread.join(getJoinTime());

        } catch (InterruptedException e) {
            if (thread.isAlive())
                log.warn(Mrk_JOD.JOD_EXEC_SUB, String.format("Thread server loop '%s' not terminated", thread.getName()));
        }

        if (!thread.isAlive())
            log.debug(Mrk_JOD.JOD_EXEC_SUB, String.format("Thread listener server '%s' stopped", thread.getName()));

        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Listener server stopped");
    }

}
