/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2024 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.robypomper.josp.jsl.srvinfo;


import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.user.JSLUserMngr;

/**
 * Interface for JSL Service's info system.
 * <p>
 * This system collect all service's info and provide them to other JSL's systems.
 * <p>
 * JSLServiceInfo implementations can access to the JCP API and JSL settings file
 * to load and store values of Service's info.
 */
public interface JSLServiceInfo {

    // Service's systems

    /**
     * Set service's systems (UserMngr and ObjsMngr) so ServiceInfo can access
     * to systems info and provide them as a unique point of reference.
     *
     * @param user the JSL user mngm system.
     * @param objs the JSL objects mngm system.
     */
    void setSystems(JSLUserMngr user, JSLObjsMngr objs);

    /**
     * Set service's systems (Communication) so ServiceInfo can access to systems
     * info and provide them as a unique point of reference.
     *
     * @param comm the JSL communication system.
     */
    void setCommunication(JSLCommunication comm);

    // Srv's info

    /**
     * The Service's ID is the main id used to identify the Service in the JOSP
     * Eco-System.
     *
     * @return the service's ID.
     */
    String getSrvId();

    /**
     * Human readable service's name.
     *
     * @return the service's name
     */
    String getSrvName();


    // Users's info

    /**
     * @return <code>true</code> if current JSL instance has logged current user.
     */
    boolean isUserLogged();

    /**
     * The logged user ID.
     *
     * @return logged user ID, if user was not logged then it return
     * <code>null</code>.
     */
    String getUserId();

    /**
     * The logged user ID.
     *
     * @return logged user ID, if user was not logged then it return
     * <code>null</code>.
     */
    String getUsername();


    // Instance and fullId

    /**
     * The current instance ID.
     *
     * @return current instance ID.
     */
    String getInstanceId();

    /**
     * The full service id is composed by service and user ids.
     *
     * @return an id composed by service and user id.
     */
    String getFullId();


    // Objects's info

    /**
     * @return the number of connected objects.
     */
    int getConnectedObjectsCount();

    /**
     * @return the number of known objects.
     */
    int getKnownObjectsCount();


    // Mngm methods

    /**
     * Start periodically checks on settings file and JCP APIs Service for service's
     * info changes.
     */
    void startAutoRefresh();

    /**
     * Stop periodically checks on settings file and JCP APIs Service for service's
     * info changes.
     */
    void stopAutoRefresh();


    // Exceptions

    /**
     * Exceptions thrown on accessing JSL systems from JSLServiceInfo when are
     * not yet set.
     * <p>
     * This exception is thrown on implementation errors.
     */
    class SystemNotSetException extends RuntimeException {
        private static final String MSG = "System %s not yet set";

        public SystemNotSetException(String systemName) {
            super(String.format(MSG, systemName));
        }
    }
}
