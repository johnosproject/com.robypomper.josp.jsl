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

import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.actions.Paths20;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;

public class JOSPRangeActionHtml extends JOSPRangeStateHtml {

    public final String pathSetValue;
    public final String pathInc;
    public final String pathDec;
    public final String pathMax;
    public final String pathMin;
    public final String pathSet1_2;
    public final String pathSet1_3;
    public final String pathSet2_3;

    public JOSPRangeActionHtml(JSLRangeAction action) {
        super(action);
        this.pathSetValue = Paths20.FULL_PATH_RANGE_SET(objId, componentPath);
        this.pathInc = Paths20.FULL_PATH_RANGE_INC(objId, componentPath);
        this.pathDec = Paths20.FULL_PATH_RANGE_DEC(objId, componentPath);
        this.pathMax = Paths20.FULL_PATH_RANGE_MAX(objId, componentPath);
        this.pathMin = Paths20.FULL_PATH_RANGE_MIN(objId, componentPath);
        this.pathSet1_2 = Paths20.FULL_PATH_RANGE_1_2(objId, componentPath);
        this.pathSet1_3 = Paths20.FULL_PATH_RANGE_1_3(objId, componentPath);
        this.pathSet2_3 = Paths20.FULL_PATH_RANGE_2_3(objId, componentPath);
    }

}
