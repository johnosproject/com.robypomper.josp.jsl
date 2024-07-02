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

package com.robypomper.josp.jsl.objs.structure;

import com.robypomper.josp.protocol.JOSPProtocol;

/**
 * State component representation.
 * <p>
 * State component receive status changes from correlated object and emit
 * corresponding events.
 */
public interface JSLState extends JSLComponent {

    // Status upd flow (struct)

    /**
     * Called by {@link com.robypomper.josp.jsl.objs.JSLRemoteObject} that own
     * current state, this method process the update and then trigger the update
     * event to all registered listeners.
     *
     * @param statusUpd the status to process.
     */
    boolean updateStatus(JOSPProtocol.StatusUpd statusUpd);

}
