/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.params_DEPRECATED.jslwb;

import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.states.Paths20;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;

public class JOSPBooleanStateHtml extends JOSPComponentHtml {

    public final boolean state;
    public final String pathState;

    public JOSPBooleanStateHtml(JSLBooleanState state) {
        super(state);
        this.state = state.getState();
        this.pathState = Paths20.FULL_PATH_BOOL(objId, componentPath);
    }

}
