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

import java.util.Map;


/**
 * Basic interface for JOD executor manager worker representations.
 */
public interface JODWorker {

    // Getters

    /**
     * Current JODWorker's name.
     *
     * @return the JODWorker's name.
     */
    String getName();

    /**
     * Current JODWorker's protocol.
     *
     * @return the JODWorker's protocol.
     */
    String getProto();

    /**
     * Current JODWorker's component.
     *
     * @return the JODWorker's component.
     */
    JODComponent getComponent();

    /**
     * Current JODWorker's configs map.
     * <p>
     * This map contains config's placeholder as keys and config's value as key.
     *
     * @return the JODWorker's configs.
     */
    Map<String, String> getConfigs();

    /**
     * @return <code>true</code> if current worker is enabled, <code>false</code>
     * otherwise.
     */
    boolean isEnabled();


    // Exceptions

    /**
     * Exceptions for {@link JODWorker} object creation.
     */
    class FactoryException extends Throwable {
        public FactoryException(String msg) {
            super(msg);
        }

        public FactoryException(String msg, Exception e) {
            super(msg, e);
        }
    }

    /**
     * Exceptions for missing mandatory property or with wrong value from the
     * configs string during the {@link JODWorker} object initialization.
     */
    class MalformedConfigsException extends Throwable {
        private static final String MSG_MALFORMED_CONFIG = "Config string '%s' bad formatted.";

        public MalformedConfigsException(String fullConfig, Exception e) {
            super(String.format(MSG_MALFORMED_CONFIG, fullConfig), e);
        }
    }

    /**
     * Exceptions for missing mandatory property or with wrong value from the
     * configs string during the {@link JODWorker} object initialization.
     */
    class MissingPropertyException extends Throwable {
        private static final String MSG_MISSING_PROP = "Mandatory property '%s' for '%s://%s' %s is missing.";

        public MissingPropertyException(String property, String proto, String workerName, String workerType) {
            super(String.format(MSG_MISSING_PROP, property, proto, workerName, workerType));
        }

        public MissingPropertyException(String property, String proto, String workerName, String workerType, Exception e) {
            super(String.format(MSG_MISSING_PROP, property, proto, workerName, workerType), e);
        }
    }

    /**
     * Exceptions for missing mandatory property or with wrong value from the
     * configs string during the {@link JODWorker} object initialization.
     */
    class ParsingPropertyException extends Throwable {
        private static final String MSG_WRONG_VAL = "Property '%s' for '%s://%s' value '%s' not valid ('%s').";

        public ParsingPropertyException(String property, String proto, String workerName, String workerType, String value) {
            super(String.format(MSG_WRONG_VAL, property, proto, workerName, workerType, value));
        }

        public ParsingPropertyException(String property, String proto, String workerName, String workerType, String value, Exception e) {
            super(String.format(MSG_WRONG_VAL, property, proto, workerName, workerType, value), e);
        }
    }

}
