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
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.List;
import java.util.Map;

public interface ObjPerms {

    // Getters / Setters

    /**
     * @return object's permissions.
     */
    List<JOSPPerm> getPerms();

    /**
     * @return service's permissions related to current object.
     */
    Map<JOSPPerm.Connection, JOSPPerm.Type> getPermTypes();

    /**
     * @return the service's permission to access to object via given connection
     * type.
     */
    JOSPPerm.Type getServicePerm(JOSPPerm.Connection connType);

    void addPerm(String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

    void updPerm(String permId, String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

    void remPerm(String permId) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;


    // Listeners

    void addListener(RemoteObjectPermsListener listener);

    void removeListener(RemoteObjectPermsListener listener);

    interface RemoteObjectPermsListener {

        void onPermissionsChanged(JSLRemoteObject obj, List<JOSPPerm> newPerms, List<JOSPPerm> oldPerms);

        void onServicePermChanged(JSLRemoteObject obj, JOSPPerm.Connection connType, JOSPPerm.Type newPermType, JOSPPerm.Type oldPermType);

    }

}
