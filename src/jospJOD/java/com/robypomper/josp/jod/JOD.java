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

package com.robypomper.josp.jod;

import com.robypomper.java.JavaAssertions;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.events.JODEvents;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.history.JODHistory;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.states.JODState;
import com.robypomper.josp.states.StateException;

import java.io.File;
import java.util.Map;


/**
 * Main interface for JOD object.
 * <p>
 * This interface define methods to initialize and manage a JOD object. Moreover,
 * it define also JOD support classes, struct and exceptions.
 */
@SuppressWarnings("unused")
public interface JOD {

    // New instance method

    /**
     * Static method to generate JOD object from <code>settings</code> configs.
     *
     * <b>This method (from {@link JOD} interface) return null object</b>,
     * because JOD sub-classes must re-implement the same method and
     * return sub-class instance.
     *
     * @param settings JOD.Settings containing JOD settings. Sub-classes can
     *                 extend JOD.Settings and and used extended class as
     *                 <code>instance</code> param.
     * @return null pointer.
     */
    static JOD instance(Settings settings) {
        return JavaAssertions.makeAssertion_Failed("Each sub-class must override this method", null);
    }


    // JOD implementation version

    /**
     * @return the JOSP JOD agent's implementation version.
     */
    String version();


    /**
     * @return the list of supported JOSP Protocol versions.
     */
    String[] versionsJOSPProtocol();


    /**
     * @return the list of supported JCP APIs versions.
     */
    String[] versionsJCPAPIs();


    // JOD mngm

    /**
     * @return the JOD daemon's state.
     */
    JODState getState();

    /**
     * Start current JOD object and all his systems.
     * <p>
     * Update the JOD object's status {@link #getState()}.
     */
    void startup() throws StateException;

    /**
     * Stop current JOD object and all his systems.
     * <p>
     * Update the JOD object's status {@link #getState()}.
     */
    void shutdown() throws StateException;

    /**
     * Stop then restart current JOD object and all his systems.
     * <p>
     * Update the JOD object's status {@link #getState()}.
     */
    boolean restart() throws StateException;

    /**
     * Pretty formatted JOD instance's info on current logger.
     */
    void printInstanceInfo();


    // JOD Systems

    JCPAPIsClientObj getJCPClient();

    JODObjectInfo getObjectInfo();

    JODStructure getObjectStructure();

    JODCommunication getCommunication();

    JODExecutorMngr getExecutor();

    JODPermissions getPermission();

    JODEvents getEvents();

    JODHistory getHistory();


    // Support struct and classes

    /**
     * JOD's settings interface.
     * <p>
     * JOD.Settings implementations can be used by JOD implementations to
     * customize settings for specific JOD implementations.
     */
    interface Settings {

        // New instance method

        /**
         * Static method to generate JOD.Settings object from <code>file</code>
         * configs.
         *
         * <b>This method (from {@link JOD.Settings} interface) return null object</b>,
         * because JOD.Settings sub-classes must re-implement the same method and
         * return sub-class instance.
         *
         * @param file file to be parsed as configs file.
         * @return null pointer.
         */
        static Settings instance(File file) {
            return null;
        }

        /**
         * Static method to generate JOD.Settings object from <code>file</code>
         * configs.
         *
         * <b>This method (from {@link JOD.Settings} interface) return null object</b>,
         * because JOD.Settings sub-classes must re-implement the same method and
         * return sub-class instance.
         *
         * @param properties map containing the properties to set as JOD configurations.
         * @return null pointer.
         */
        static Settings instance(Map<String, Object> properties) {
            return null;
        }

    }


    // Exceptions

    /**
     * Exceptions for {@link JOD} and {@link JOD.Settings} object creation.
     */
    class FactoryException extends Throwable {
        public FactoryException(String msg) {
            super(msg);
        }

        public FactoryException(String msg, Exception e) {
            super(msg, e);
        }
    }

}
