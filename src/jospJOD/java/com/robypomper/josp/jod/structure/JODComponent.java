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


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.util.List;

/**
 * Base component representation.
 * <p>
 * This interface collect all components commons properties such as name,
 * description, etc...
 */
public interface JODComponent {

    // Commons properties

    /**
     * @return the name of the component.
     */
    String getName();

    /**
     * @return the description of the component.
     */
    String getDescr();

    /**
     * @return the path of the component (it will be unique path).
     */
    @JsonIgnore
    JODComponentPath getPath();

    /**
     * @return the parent of the component.
     */
    @JsonIgnore
    JODContainer getParent();

    /**
     * @return the component type.
     */
    String getType();


    // Status History

    /**
     * @return the component status history.
     */
    List<JOSPStatusHistory> getHistoryStatus(HistoryLimits limits);

}
