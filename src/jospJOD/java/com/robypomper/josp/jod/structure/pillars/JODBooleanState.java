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

import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.history.JODHistory;
import com.robypomper.josp.jod.structure.AbsJODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.jod.structure.StructureDefinitions;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class JODBooleanState extends AbsJODState {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private boolean state = false;


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
    public JODBooleanState(JODStructure structure, JODExecutorMngr execMngr, JODHistory history, String name, String descr, String listener, String puller) throws JODStructure.ComponentInitException {
        super(structure, execMngr, history, name, descr, listener, puller);
    }


    // Status's properties

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return StructureDefinitions.TYPE_BOOL_STATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return Boolean.toString(state);
    }

    public boolean getStateBoolean() {
        return state;
    }


    // Status's methods

    public void setUpdate(boolean newState) {
        if (state == newState)
            return;

        boolean oldState = state;
        state = newState;
        try {
            propagateState(new JOSPBoolean(newState, oldState));
        } catch (JODStructure.CommunicationSetException e) {
            log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error on propagating state of component '%s' to JOD Communication because %s", getName(), e.getMessage()), e);
        }
    }


    // Boolean StateUpdate implementation

    private static class JOSPBoolean implements JODStateUpdate {

        private final boolean newState;
        private final boolean oldState;

        public JOSPBoolean(boolean newState, boolean oldState) {
            this.newState = newState;
            this.oldState = oldState;
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            // No '\n', no ';'
            String newVal = String.format(KEY_VALUE_FORMAT, "new", Boolean.toString(newState));
            String oldVal = String.format(KEY_VALUE_FORMAT, "old", Boolean.toString(oldState));
            return newVal + ITEMS_SEP + oldVal;
        }

    }

}
