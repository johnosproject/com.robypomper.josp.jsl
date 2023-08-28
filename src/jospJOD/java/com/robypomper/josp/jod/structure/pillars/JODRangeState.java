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
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.history.JODHistory;
import com.robypomper.josp.jod.structure.AbsJODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.jod.structure.StructureDefinitions;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class JODRangeState extends AbsJODState {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final double min;
    private final double max;
    private final double step;
    private double state = 0;


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
    public JODRangeState(JODStructure structure, JODExecutorMngr execMngr, JODHistory history, String name, String descr, String listener, String puller, Double min, Double max, Double step) throws JODStructure.ComponentInitException {
        super(structure, execMngr, history, name, descr, listener, puller);
        this.min = min != null ? min : 0;
        this.max = max != null ? max : 100;
        this.step = step != null ? step : 10;
    }


    // Status's properties

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return StructureDefinitions.TYPE_RANGE_STATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return Double.toString(state);
    }

    public double getStateRange() {
        return state;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }


    // Status's methods

    public void setUpdate(double newState) {
        if (state == newState)
            return;

        double oldState = state;
        state = newState;
        try {
            propagateState(new JOSPRange(newState, oldState));
        } catch (JODStructure.CommunicationSetException e) {
            log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error on propagating state of component '%s' to JOD Communication because %s", getName(), e.getMessage()), e);
        }
    }


    // Range StateUpdate implementation

    private static class JOSPRange implements JODStateUpdate {

        private final double newState;
        private final double oldState;

        public JOSPRange(double newState, double oldState) {
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
            String newVal = String.format(KEY_VALUE_FORMAT, "new", JavaFormatter.doubleToStr(newState));
            String oldVal = String.format(KEY_VALUE_FORMAT, "old", JavaFormatter.doubleToStr(oldState));
            return newVal + ITEMS_SEP + oldVal;
        }

    }

}
