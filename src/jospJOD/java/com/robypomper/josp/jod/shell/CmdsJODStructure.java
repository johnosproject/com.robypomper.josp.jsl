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

package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import com.robypomper.josp.jod.structure.*;

public class CmdsJODStructure {

    private final JODStructure structure;

    public CmdsJODStructure(JODStructure structure) {
        this.structure = structure;
    }


    @Command(description = "Print object's Structure tree")
    public String objTree() {
        return UtilsStructure.compToPrettyPrint(structure.getRoot(), true);
    }

    @Command(description = "Print object's Structure Root Component")
    public String objComponent() {
        return UtilsStructure.compToFullPrint(structure.getRoot());
    }

    @Command(description = "Print object's Structure component specified as JOD Component Path")
    public String objComponent(String compPath) {
        JODComponentPath path = new DefaultJODComponentPath(compPath);
        if (!path.isUnique())
            return "Please specify an unique JOD Component Path";

        JODComponent comp = structure.getComponent(path);
        if (comp == null)
            return String.format("No component found for '%s' path.", compPath);

        return UtilsStructure.compToFullPrint(comp);
    }

    @Command(description = "Print object's Structure Root Component")
    public String objExec(String compPath) {
        JODComponentPath path = new DefaultJODComponentPath(compPath);
        if (!path.isUnique())
            return "Please specify an unique JOD Component Path";

        JODComponent comp = structure.getComponent(path);
        if (comp == null)
            return String.format("No component found for '%s' path.", compPath);

        if (!(comp instanceof JODAction))
            return String.format("Component '%s' found, but not JODAction component.", comp.getName());

        if (((JODAction) comp).execAction(null))
            return String.format("Action executed successfully on component '%s'", comp.getName());
        return String.format("Error occurred executing action on component '%s'", comp.getName());
    }

}
