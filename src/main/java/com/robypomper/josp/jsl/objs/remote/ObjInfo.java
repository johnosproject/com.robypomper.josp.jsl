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

package com.robypomper.josp.jsl.objs.remote;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.history.HistoryObjEvents;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;

import java.util.List;

public interface ObjInfo {

    // Getters / Setters

    /**
     * @return the object's id.
     */
    String getId();

    /**
     * @return the object's name.
     */
    String getName();

    /**
     * Send a request to set the object's name.
     * <p>
     * Send SetObjectName request to represented object.
     */
    void setName(String newName) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

    /**
     * @return the object's owner Id.
     */
    String getOwnerId();

    /**
     * Send a request to set the object's owner id.
     * <p>
     * Send SetObjectOwnerId request to represented object.
     */
    void setOwnerId(String newOwnerId) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

    /**
     * @return the object's JOD version.
     */
    String getJODVersion();

    /**
     * @return the object's model.
     */
    String getModel();

    /**
     * @return the object's brand.
     */
    String getBrand();

    /**
     * @return the object's long description.
     */
    String getLongDescr();


    // Listeners

    void addListener(RemoteObjectInfoListener listener);

    void removeListener(RemoteObjectInfoListener listener);

    interface RemoteObjectInfoListener {

        void onNameChanged(JSLRemoteObject obj, String newName, String oldName);

        void onOwnerIdChanged(JSLRemoteObject obj, String newOwnerId, String oldOwnerId);

        void onJODVersionChanged(JSLRemoteObject obj, String newJODVersion, String oldJODVersion);

        void onModelChanged(JSLRemoteObject obj, String newModel, String oldModel);

        void onBrandChanged(JSLRemoteObject obj, String newBrand, String oldBrand);

        void onLongDescrChanged(JSLRemoteObject obj, String newLongDescr, String oldLongDescr);

    }


    // Events History

    List<JOSPEvent> getEventsHistory(HistoryLimits limits, int timeoutSeconds) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

    void getEventsHistory(HistoryLimits limits, HistoryObjEvents.EventsListener listener) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

}
