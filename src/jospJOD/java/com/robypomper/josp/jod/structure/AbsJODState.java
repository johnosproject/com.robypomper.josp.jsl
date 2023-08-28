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

package com.robypomper.josp.jod.structure;

import com.robypomper.josp.jod.events.Events;
import com.robypomper.josp.jod.executor.AbsJODWorker;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.executor.JODPuller;
import com.robypomper.josp.jod.executor.JODWorker;
import com.robypomper.josp.jod.history.JODHistory;
import com.robypomper.josp.jod.structure.executor.JODComponentListener;
import com.robypomper.josp.jod.structure.executor.JODComponentPuller;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Default implementation of {@link JODState} interface.
 * <p>
 * In addition to the method defined by the interface, this class manage the
 * instance of the JODWorker delegated to monitoring the status's resource (FW	&amp;	Apps).
 * The instance Worker can be both type: a {@link com.robypomper.josp.jod.executor.JODListener}
 * or a {@link com.robypomper.josp.jod.executor.JODPuller}, depending on params
 * given to the constructor.
 */
public abstract class AbsJODState extends AbsJODComponent
        implements JODState {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODWorker stateWorker;


    // Constructor

    /**
     * Default constructor that initialize the status component with his
     * properties and worker.
     *
     * <b>NB:</b> only once of <code>listener</code> and <code>puller</code>
     * params can be set, the other one must be null.
     *
     * @param structure the JOD Structure system.
     * @param execMngr  the JOD Executor Mngr system.
     * @param name      the name of the component.
     * @param descr     the description of the component.
     * @param listener  the listener full configs string.
     * @param puller    the puller full configs string.
     */
    protected AbsJODState(JODStructure structure, JODExecutorMngr execMngr, JODHistory history, String name, String descr, String listener, String puller) throws JODStructure.ComponentInitException {
        super(structure, history, name, descr);

        if (listener == null && puller == null) {
            log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error on setting state component '%s' listener or puller because no listener nor puller given", getName()));
            throw new JODStructure.ComponentInitException(String.format("Can't initialize '%s' component state, one of listener or puller params must set.", getName()));
        }

        JODWorker stateWorkerTmp = null;
        if (listener != null) {
            if (listener.equalsIgnoreCase("NONE")) {
                log.info(Mrk_JOD.JOD_STRU_SUB, String.format("State component '%s' configured without a listener worker", getName()));

            } else {
                try {
                    log.trace(Mrk_JOD.JOD_STRU_SUB, String.format("Setting state component '%s' listener '%s'", getName(), listener));
                    JODComponentListener compWorker = new JODComponentListener(this, name, AbsJODWorker.extractProto(listener), AbsJODWorker.extractConfigsStr(listener));
                    stateWorkerTmp = execMngr.initListener(compWorker);

                } catch (JODWorker.FactoryException | JODWorker.MalformedConfigsException e) {
                    log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error on setting state component '%s' listener because %s", getName(), e.getMessage()), e);
                    throw new JODStructure.ComponentInitException(String.format("Error on setting state component '%s' listener", getName()), e);
                }
            }
        }

        if (puller != null) {
            if (!puller.equalsIgnoreCase("NONE")) {
                log.info(Mrk_JOD.JOD_STRU_SUB, String.format("State component '%s' configured without a puller worker", getName()));

            } else {
                try {
                    log.trace(Mrk_JOD.JOD_STRU_SUB, String.format("Setting state component '%s' puller '%s'", getName(), puller));
                    JODComponentPuller compWorker = new JODComponentPuller(this, name, AbsJODWorker.extractProto(puller), AbsJODWorker.extractConfigsStr(puller));
                    stateWorkerTmp = execMngr.initPuller(compWorker);

                } catch (JODWorker.FactoryException | JODWorker.MalformedConfigsException e) {
                    log.warn(String.format("Error on setting state component '%s' puller because %s", getName(), e.getMessage()), e);
                    throw new JODStructure.ComponentInitException(String.format("Error on setting state component '%s' puller", getName()), e);
                }
            }
        }

        stateWorker = stateWorkerTmp;
    }


    // Status's properties

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWorker() {
        return AbsJODWorker.mergeConfigsStr(stateWorker.getProto(), stateWorker.getName());
    }


    // Status upd flow (struct)

    /**
     * {@inheritDoc}
     * <p>
     * Force state's pulling when worker is a {@link JODPuller} instance.
     */
    @Override
    public void forceCheckState() {
        //if (stateWorker instanceof JODListener)
        //((JODListener)stateWorker).listen();
        if (stateWorker instanceof JODPuller)
            ((JODPuller) stateWorker).pull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propagateState(JODStateUpdate update) throws JODStructure.CommunicationSetException {
        log.debug(Mrk_JOD.JOD_STRU_SUB, String.format("Propagating component '%s' state", getName()));
        Events.registerStatusUpd(this, update);

        getHistory().register(this, update);
        getStructure().getCommunication().sendObjectUpdMsg(this, update);

        log.debug(Mrk_JOD.JOD_STRU_SUB, String.format("Component '%s' propagated state '%s'", getName(), update.encode()));
    }

}
