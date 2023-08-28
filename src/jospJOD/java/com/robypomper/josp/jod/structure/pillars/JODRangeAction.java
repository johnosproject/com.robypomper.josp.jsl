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

package com.robypomper.josp.jod.structure.pillars;

import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jod.executor.AbsJODWorker;
import com.robypomper.josp.jod.executor.JODExecutor;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.executor.JODWorker;
import com.robypomper.josp.jod.history.JODHistory;
import com.robypomper.josp.jod.structure.JODAction;
import com.robypomper.josp.jod.structure.JODActionParams;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.jod.structure.StructureDefinitions;
import com.robypomper.josp.jod.structure.executor.JODComponentExecutor;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JODRangeAction extends JODRangeState implements JODAction {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODExecutor exec;


    // Constructor

    /**
     * Default constructor that initialize the action component with his
     * properties and executor.
     *
     * <b>NB:</b> only once of <code>listener</code> and <code>puller</code>
     * params can be set, the other one must be null.
     * <p>
     *  @param structure the JOD Structure system.
     * @param execMngr  the JOD Executor Mngr system.
     * @param history
     * @param name      the name of the component.
     * @param descr     the description of the component.
     * @param listener  the listener full configs string.
     * @param puller    the puller full configs string.
     * @param executor  the executor full configs string.
     */
    public JODRangeAction(JODStructure structure, JODExecutorMngr execMngr, JODHistory history, String name, String descr, String listener, String puller, String executor, Double min, Double max, Double step) throws JODStructure.ComponentInitException {
        super(structure, execMngr, history, name, descr, listener, puller, min, max, step);

        try {
            if (executor != null) {
                log.trace(Mrk_JOD.JOD_STRU_SUB, String.format("Setting action component '%s' executor '%s'", getName(), listener));
                JODComponentExecutor compWorker = new JODComponentExecutor(this, name, AbsJODWorker.extractProto(executor), AbsJODWorker.extractConfigsStr(executor));
                exec = execMngr.initExecutor(compWorker);

            } else {
                log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error on setting action component '%s' executor because no executor given", getName()));
                throw new JODStructure.ComponentInitException(String.format("Error on setting action component '%s' executor because not set", getName()));
            }

        } catch (JODWorker.FactoryException | JODWorker.MalformedConfigsException e) {
            log.warn(String.format("Error on setting action component '%s' executor because %s", getName(), e.getMessage()), e);
            throw new JODStructure.ComponentInitException(String.format("Error on setting action component '%s' executor", getName()), e);
        }
    }


    // Action's properties

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return StructureDefinitions.TYPE_RANGE_ACTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExecutor() {
        return AbsJODWorker.mergeConfigsStr(exec.getProto(), exec.getName());
    }


    // Action's methods

    @Override
    public boolean execAction(JOSPProtocol.ActionCmd commandAction) {
        log.debug(Mrk_JOD.JOD_STRU_SUB, String.format("Executing component '%s' action", getName()));
        if (commandAction.getCommand() instanceof JOSPRange) {
            JOSPRange cmdAction = (JOSPRange) commandAction.getCommand();
            if (exec instanceof JOSPRange.Executor)
                if (!((JOSPRange.Executor) exec).exec(commandAction, cmdAction)) {
                    log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error on executing component '%s' action", getName()));
                    return false;
                }
        } else {
            log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error on executing component '%s' action because command type '%s' not supported", getName(), commandAction.getCommand().getType()));
            return false;
        }
        log.debug(Mrk_JOD.JOD_STRU_SUB, String.format("Component '%s' executed action", getName()));
        return true;
    }


    // Range StateUpdate implementation

    public static class JOSPRange implements JODActionParams {

        public final double newState;
        public final double oldState;

        public JOSPRange(String updData) {
            String[] lines = updData.split(ITEMS_SEP);

            Double newVal = JavaFormatter.strToDouble(lines[0].substring(lines[0].indexOf(KEY_VALUE_SEP) + 1));
            newState = newVal != null ? newVal : 0;
            Double oldVal = JavaFormatter.strToDouble(lines[1].substring(lines[1].indexOf(KEY_VALUE_SEP) + 1));
            oldState = oldVal != null ? oldVal : 0;
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            throw new RuntimeException("JSL JOSPIntTest::encode() method must be NOT called");
        }

        public interface Executor {

            boolean exec(JOSPProtocol.ActionCmd commandAction, JOSPRange cmdAction);

        }

    }

}
