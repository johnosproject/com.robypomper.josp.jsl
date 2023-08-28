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

package com.robypomper.josp.jod.objinfo;


import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;

/**
 * Interface for Object's info system.
 * <p>
 * This system collect all object's info and provide them to other JOD's systems.
 * <p>
 * JODObjectInfo implementations can access to the JCP API and JOD settings file
 * to load and store values of Object's info (for example the Hardware ID must
 * be generated from the JCP APIs Object and stored on local settings file).
 */
public interface JODObjectInfo {

    // Object's systems

    /**
     * Set object's systems so ObjectInfo can access to systems info and provide
     * them as a unique point of reference.
     *
     * @param structure   the JOD structure system.
     * @param executor    the JOD executor system.
     * @param comm        the JOD communication system.
     * @param permissions the JOD permissions system.
     */
    void setSystems(JODStructure structure, JODExecutorMngr executor, JODCommunication comm, JODPermissions permissions);


    // Obj's info

    /**
     * The full service id is composed by service and user ids.
     *
     * @return an id composed by service and user id.
     */
    String getFullId();

    /**
     * The JOD version represent the object's agent version and define witch
     * JOSP protocol version can be used to communicate with this JOD instance.
     *
     * @return the current JOD implementation version.
     */
    String getJODVersion();

    /**
     * The Object's ID is the main id used to identifiy the Object in the JOSP
     * Eco-System. When it be reset, then must re-register the Object.
     *
     * @return the object's ID.
     */
    String getObjId();

    /**
     * Human readable object's name.
     *
     * @return the object's name
     */
    String getObjName();

    /**
     * Set the human readable object's name.
     *
     * @param newName the new obj's name to set
     */
    void setObjName(String newName);


    // Users's info

    /**
     * The object owner's User ID.
     *
     * @return owner's User ID.
     */
    String getOwnerId();

    /**
     * Set object's owner.
     *
     * @param ownerId the user's id.
     */
    void setOwnerId(String ownerId);

    /**
     * Set object's owner to unset.
     */
    void resetOwnerId();


    // Structure's info

    /**
     * The object's structure local file.
     *
     * @return object's structure file's path.
     */
    String getStructurePath();

    /**
     * The object's structure definition in a String object.
     *
     * @return object's structure definition.
     */
    String readStructureStr();

    /**
     * Object's structure string for JSL.
     *
     * @return the object's structure.
     */
    String getStructForJSL() throws JODStructure.ParsingException;

    /**
     * The object's brand.
     *
     * @return object's brand.
     */
    String getBrand();

    /**
     * The object's model.
     *
     * @return object's model.
     */
    String getModel();

    /**
     * The object's description.
     *
     * @return object's description.
     */
    String getLongDescr();


    // Mngm methods

    /**
     * Start periodically checks on settings file and JCP APIs Object for object's
     * info changes.
     */
    void startAutoRefresh();

    /**
     * Stop periodically checks on settings file and JCP APIs Object for object's
     * info changes.
     */
    void stopAutoRefresh();

    void syncObjInfo();

}
