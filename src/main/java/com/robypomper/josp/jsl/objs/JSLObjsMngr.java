/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
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

package com.robypomper.josp.jsl.objs;

import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLGwS2OClient;
import com.robypomper.josp.jsl.comm.JSLLocalClient;

import java.util.List;


/**
 * Interface for JSL Objects management system.
 * <p>
 * This system manage and provide all available JOD objects as
 * {@link JSLRemoteObject} instances.
 * <p>
 * Available JOD objects list depends on user/service permissions. A service can
 * access to an object if and only if that objects grant the permission to the
 * pair user/service.
 * <p>
 * Each {@link JSLRemoteObject} is initialized from {@link JSLLocalClient} instance
 * provided by the {@link JSLCommunication} system. An Object can be associated
 * to multiple {@link JSLLocalClient} and to the {@link JSLGwS2OClient}
 * at the same time.<br>
 * For every message tx between services and objects is send preferring local
 * connections and, only if no local connection is available is choose the
 * cloud connection.
 */
public interface JSLObjsMngr {

    // Object's access

    /**
     * @return an array containing all known objects.
     */
    List<JSLRemoteObject> getAllObjects();

    /**
     * @return an array containing all connected objects.
     */
    List<JSLRemoteObject> getAllConnectedObjects();

    /**
     * @param objId id of object required.
     * @return the object corresponding to given id, null if not found.
     */
    JSLRemoteObject getById(String objId);

    /**
     * @param client required object's local connection.
     * @return the object associated to given local connection, null if not found.
     */
    JSLRemoteObject getByConnection(JSLLocalClient client);

    /**
     * @param model required object's model.
     * @return the object associated to given local connection, null if not found.
     */
    List<JSLRemoteObject> getByModel(String model);

    /**
     * @param pattern object's search pattern.
     * @return an array containing all object's corresponding to given search
     * pattern.
     */
    List<JSLRemoteObject> searchObjects(JSLObjectSearchPattern pattern);


    // Connections mngm

    /**
     * Associate given connection to corresponding {@link JSLRemoteObject}.
     * <p>
     * This method is call by {@link JSLCommunication} system when it opens a
     * local connection with a JOD Object.
     * <p>
     * If there is no {@link JSLRemoteObject} yet, it will be created.
     *
     * @param serverConnection the open local connection to JOD object.
     * @return created or connected object.
     */
    JSLRemoteObject addNewConnection(JSLLocalClient serverConnection);

    /**
     * Remove given connection to corresponding {@link JSLRemoteObject}.
     * <p>
     * This method is call by {@link JSLCommunication} system when it close (or
     * detect that is closed) a local connection with a JOD Object.
     * <p>
     * If there is no {@link JSLRemoteObject} yet, it will be created anyway and
     * set offline.
     *
     * @param localConnection the closed local connection to JOD object.
     */
    boolean removeConnection(JSLLocalClient localConnection);

    void setCommunication(JSLCommunication communication);

    void addCloudObject(String objId);


    // Listeners

    void addListener(ObjsMngrListener listener);

    void removeListener(ObjsMngrListener listener);

    interface ObjsMngrListener {

        void onObjAdded(JSLRemoteObject obj);

        void onObjRemoved(JSLRemoteObject obj);

    }
}
