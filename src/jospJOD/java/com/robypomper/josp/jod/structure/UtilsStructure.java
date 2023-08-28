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

import java.util.ArrayList;
import java.util.Collection;


/**
 * Utils class for Structure management.
 * <p>
 * Actually it implements only components printing methods.
 */
public class UtilsStructure {

    /**
     * Return a single-line string component description.
     *
     * @param comp component to be printed.
     * @return single-line, formatted, string containing component's main properties.
     */
    public static String compToPrettyPrint(JODComponent comp) {
        return compToPrettyPrint(comp, false);
    }

    /**
     * Return a single-line string component description and optionally, returned
     * string can contain also <code>comp</code>'s sub components.
     *
     * @param comp        component to be printed.
     * @param recursively true if it must return a
     * @return single-line (one for each sub component), formatted, string
     * containing component's main properties.
     */
    public static String compToPrettyPrint(JODComponent comp, boolean recursively) {
        String out = "";
        if (recursively) {
            out = " Component             (Type)                      | Path\n";
            out += "---------------------------------------------------+------------------------------------------------\n";
        }

        return out + compToPrettyPrint_RECURSIVE(comp, recursively, "");
    }

    /**
     * Private method used for sub component recursive printing.
     */
    private static String compToPrettyPrint_RECURSIVE(JODComponent comp, boolean recursively, String indent) {
        String compStr = String.format("%s+ %-20s (%s)", indent, comp.getName(), comp.getClass().getSimpleName());

        String pathStr = comp.getPath().getString();

        //String out = String.format("%-50s | %s", compStr, workersStr);
        StringBuilder out = new StringBuilder(String.format("%-50s | %s", compStr, pathStr));
        if (recursively && comp instanceof JODContainer) {
            JODContainer container = (JODContainer) comp;
            for (JODComponent subComp : container.getComponents()) {
                out.append("\n").append(compToPrettyPrint_RECURSIVE(subComp, true, indent + "| "));
            }
        }
        return out.toString();
    }

    /**
     * Return a multi-line string component description.
     *
     * @param comp component to be printed.
     * @return single-line, formatted, string containing all component properties.
     */
    public static String compToFullPrint(JODComponent comp) {
        String out = "--- Component START ---\n";
        out += String.format("# %s\n", comp.getName());
        if (!comp.getDescr().isEmpty())
            out += String.format("%s\n", comp.getDescr());

        String strLayout = "- %-1s %s\n";
        out += String.format(strLayout, "Path #:", comp.getPath().getString());


        if (comp instanceof JODRoot) {
            out += String.format(strLayout, "Model:", ((JODRoot) comp).getModel());
            out += String.format(strLayout, "Brand:", ((JODRoot) comp).getBrand());
            out += String.format(strLayout, "Descr:", ((JODRoot) comp).getDescr_long());
        }

        if (comp instanceof JODContainer) {
            out += String.format(strLayout, "SubComps #:", ((JODContainer) comp).getComponents().size());
            out += String.format(strLayout, "SubComps:", compsToStrings(((JODContainer) comp).getComponents()));
        }

        if (comp instanceof JODState) {
            out += String.format(strLayout, "Worker:", ((JODState) comp).getWorker());
        }

        if (comp instanceof JODAction) {
            out += String.format(strLayout, "Executor:", ((JODAction) comp).getExecutor());
        }
        out += "--- --------------- ---";

        return out;
    }

    /**
     * Transform given collection to a list of component names.
     *
     * @param components the components list.
     * @return the component names list.
     */
    public static Collection<String> compsToStrings(Collection<JODComponent> components) {
        Collection<String> strings = new ArrayList<>();
        for (JODComponent comp : components)
            strings.add(comp.getName());
        return strings;
    }

}
