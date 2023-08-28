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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.jod.comm.JODCommunication;

import java.util.Date;


/**
 * Interface for Object's structure system.
 *
 * Implementation of this interface must provide methods to access and manage
 * the object's structure.
 *
 * This system is based on data parsing, then when activated with {@link #startAutoRefresh()}
 * this class periodically checks for data updates. Then, on updates, it notifies
 * connected services for the new object structure.
 */
public interface JODStructure {

    // JOD Component's interaction methods (from communication)

    /**
     * @return the object's Root component.
     */
    JODRoot getRoot();

    /**
     * @param pathStr required component path string.
     * @return component corresponding to given path, or null if not found.
     */
    JODComponent getComponent(String pathStr);

    /**
     * @param path required component path.
     * @return component corresponding to given path, or null if not found.
     */
    JODComponent getComponent(JODComponentPath path);

    /**
     * Transform current structure in a String that can be parsed from JSL.
     *
     * @return current structure string representation.
     */
    String getStructForJSL() throws ParsingException;

    /**
     * @return date and time of the last structure update.
     */
    Date getLastStructureUpdate();

    /**
     * Return the JOD Communication system to state component to dispatch state
     * updates.
     *
     * @return the JOD Communication system.
     */
    JODCommunication getCommunication() throws CommunicationSetException;

    /**
     * Set the {@link JODCommunication} reference to the JODStructure object.
     * <p>
     * This cross-system reference is required by the State Update Flow.
     *
     * @param comm the {@link JODCommunication} reference.
     */
    void setCommunication(JODCommunication comm) throws CommunicationSetException;


    // Mngm methods

    /**
     * Start periodically checks on object's structure data file for new updates.
     */
    void startAutoRefresh();

    /**
     * Stop periodically checks on object's structure data file for new updates.
     */
    void stopAutoRefresh();

    void syncObjStruct();


    // Exceptions

    /**
     * Exceptions for {@link #setCommunication(JODCommunication)} called twice.
     */
    class CommunicationSetException extends Throwable {
        private static final String MSG = "Communication already set for current Structure.";

        public CommunicationSetException() {
            super(MSG);
        }
    }

    /**
     * Exceptions for structure parsing, file load... errors.
     */
    class ParsingException extends Throwable {
        private static final String MSG = "(@line: %d; col: %d)";

        public ParsingException(String msg) {
            super(msg);
        }

        public ParsingException(String msg, Throwable e) {
            super(msg, e);
        }

        public ParsingException(String msg, JsonProcessingException e) {
            super(msg + String.format(MSG, e.getLocation() != null ? e.getLocation().getLineNr() : -1, e.getLocation() != null ? e.getLocation().getColumnNr() : -1), e);
        }
    }

    /**
     * Exception thrown when the structure initialization try to generate an
     * unknown component type.
     */
    class ParsingUnknownTypeException extends ParsingException {
        private static final String MSG = "Unknown type '%s' for '%s' JOD Component of parent component %s";

        public ParsingUnknownTypeException(String parentCompName, String compType, String compName) {
            super(String.format(MSG, compType, compName, parentCompName));
        }
    }

    /**
     * Exception thrown when the structure initialization get an error on parsing
     * structure string.
     */
    class InstantiationParsedDataException extends ParsingException {
        private static final String MSG = "Can't initialize '%s' JOD Component of type '%s' because error on %s configs strings because %s.";
        private static final String JOIN_2 = "l'%s' or p'%s'";
        private static final String JOIN_3 = "l'%s', p'%s' or e'%s'";

        public InstantiationParsedDataException(String compType, String compName, String listener, String puller, String reason) {
            super(String.format(MSG, compName, compType, String.format(JOIN_2, listener, puller), reason));
        }

        public InstantiationParsedDataException(String compType, String compName, String listener, String puller, Throwable e) {
            super(String.format(MSG, compName, compType, String.format(JOIN_2, listener, puller), e.getMessage()), e);
        }

        public InstantiationParsedDataException(String compType, String compName, String listener, String puller, String executor, Throwable e) {
            super(String.format(MSG, compName, compType, String.format(JOIN_3, listener, puller, executor), e.getMessage()), e);
        }
    }


    // Exceptions

    /**
     * Exceptions for all component initialization errors.
     * <p>
     * This class is reserved for properties or hierarchy implementation errors
     * normally catched with a <code>assert false;</code> code block.
     */
    class ComponentInitException extends Throwable {
        public ComponentInitException(String msg) {
            super(msg);
        }

        public ComponentInitException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
