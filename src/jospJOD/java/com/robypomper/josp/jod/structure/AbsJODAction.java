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

import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.protocol.JOSPActionCommandParams;

import java.util.HashMap;
import java.util.Map;


public class AbsJODAction {

    // Actions classes

    private static final Map<String, Class<? extends JOSPActionCommandParams>> actionClasses = new HashMap<>();

    public static void loadAllActionClasses() {
        registerActionClass(JODBooleanAction.JOSPBoolean.class.getSimpleName(), JODBooleanAction.JOSPBoolean.class);
        registerActionClass(JODRangeAction.JOSPRange.class.getSimpleName(), JODRangeAction.JOSPRange.class);
    }

    public static void registerActionClass(String typeName, Class<? extends JOSPActionCommandParams> cl) {
        actionClasses.put(typeName, cl);
    }

    public static Map<String, Class<? extends JOSPActionCommandParams>> getActionClasses() {
        return actionClasses;
    }

}
