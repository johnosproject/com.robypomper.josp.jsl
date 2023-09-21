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

package com.robypomper.josp.jsl.objs.structure;


import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import org.slf4j.Logger;


/**
 * Action component representation.
 * <p>
 * Action component receive commands from the 3rd party service implementation
 * and send them to corresponding JOD object.
 */
public interface JSLAction extends JSLState {

    // Action cmd flow (struct)

    static void execAction(JSLActionParams params, JSLAction comp, Logger log) throws JSLRemoteObject.MissingPermission, JSLRemoteObject.ObjectNotConnected {
        log.debug(String.format("Sending component '%s' state to object '%s'", comp.getName(), comp.getRemoteObject().getId()));
        comp.getRemoteObject().sendObjectCmdMsg(comp, params);
        log.debug(String.format("Component '%s' send state to object '%s'", comp.getName(), comp.getRemoteObject().getId()));
    }
}
