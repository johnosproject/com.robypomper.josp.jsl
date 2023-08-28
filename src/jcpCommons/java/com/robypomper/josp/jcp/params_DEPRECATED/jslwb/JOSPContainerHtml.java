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

import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JOSPContainerHtml extends JOSPComponentHtml {

    public final List<JOSPComponentHtml> subComps;

    public JOSPContainerHtml(JSLContainer container, boolean recursive) {
        super(container);
        subComps = recursive ? convert(container.getComponents()) : null;
    }

    public static List<JOSPComponentHtml> convert(Collection<JSLComponent> subComps) {
        List<JOSPComponentHtml> subCompsHtml = new ArrayList<>();
        for (JSLComponent sc : subComps)
            subCompsHtml.add(generateJOSPComponentHtml(sc));
        return subCompsHtml;
    }

    public static JOSPComponentHtml generateJOSPComponentHtml(JSLComponent comp) {
        if (comp instanceof JSLContainer)
            return new JOSPContainerHtml((JSLContainer) comp, true);

        // Actions
        if (comp instanceof JSLBooleanAction)
            return new JOSPBooleanActionHtml((JSLBooleanAction) comp);
        if (comp instanceof JSLRangeAction)
            return new JOSPRangeActionHtml((JSLRangeAction) comp);

        // States
        if (comp instanceof JSLBooleanState)
            return new JOSPBooleanStateHtml((JSLBooleanState) comp);
        if (comp instanceof JSLRangeState)
            return new JOSPRangeStateHtml((JSLRangeState) comp);

        return new JOSPComponentHtml(comp);
    }

}
