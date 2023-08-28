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

package com.robypomper.josp.jsl.objs.remote;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.history.HistoryCompStatus;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.util.List;

public interface ObjStruct {

    // Getters

    /**
     * @return true if current service received the JOSP Structure message and
     * initialized the Remote object's structure.
     */
    boolean isInit();

    /**
     * @return the object's structure.
     */
    JSLRoot getStructure();

    /**
     * @return the object's component corresponding to given path.
     */
    JSLComponent getComponent(String compPath);

    /**
     * @return the object's component corresponding to given path.
     */
    JSLComponent getComponent(JSLComponentPath compPath);


    // Listeners

    void addListener(RemoteObjectStructListener listener);

    void removeListener(RemoteObjectStructListener listener);

    interface RemoteObjectStructListener {

        void onStructureChanged(JSLRemoteObject obj, JSLRoot newRoot);

    }


    // Components History

    List<JOSPStatusHistory> getComponentHistory(JSLComponent component, HistoryLimits limits, int timeoutSeconds) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

    void getComponentHistory(JSLComponent component, HistoryLimits limits, HistoryCompStatus.StatusHistoryListener listener) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

}
