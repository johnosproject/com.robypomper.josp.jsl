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

package com.robypomper.josp.jod.executor;

import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODBooleanState;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeState;
import com.robypomper.josp.protocol.JOSPProtocol;

import java.util.Map;


/**
 * Substitution utility class.
 * <p>
 * This class helps substitute JOD Object, component, worker, stats and/or
 * action related attributes with specific placeholders. Those placeholders
 * are defined within this class and are unique.
 * <p>
 * To use this class to substitute the placeholders contained in a string,
 * please initialize this class giving the original string. Then call (also as
 * chain) the substitution methods ({@link #substituteObject(JODObjectInfo)},
 * {@link #substituteComponent(JODComponent)}, {@link #substituteWorker(JODWorker)}...).
 * Finally, get the resulting string with the {@link #toString()} method.
 *
 * <h2>Updates since 2.2.3</h2>
 *
 * <h3>Component</h3>
 * <table border=1>
 *     <tr><th>PlaceHolder</th><th>Until 2.2.2</th><th>Since 2.2.3</th></tr>
 *     <tr><td>COMP_NAME</td><td><code>%C_NAME%</code></td><td><code>%COMP_NAME%</code></td></tr>
 *     <tr><td>COMP_TYPE</td><td><code>%C_TYPE%</code></td><td><code>%COMP_TYPE%</code></td></tr>
 *     <tr><td>COMP_PATH</td><td><code>%C_PATH%</code></td><td><code>%COMP_PATH%</code></td></tr>
 *     <tr><td>PARENT_NAME -&gt; COMP_PARENT_NAME</td><td><code>%PARENT_NAME%</code></td><td><code>%COMP_PARENT_NAME%</code></td></tr>
 *     <tr><td>PARENT_PATH -&gt; COMP_PARENT_PATH</td><td><code>%PARENT_PATH%</code></td><td><code>%COMP_PARENT_PATH%</code></td></tr>
 * </table>
 *
 * <h3>Status</h3>
 * <table border=1>
 *     <tr><th>PlaceHolder</th><th>Until 2.2.2</th><th>Since 2.2.3</th></tr>
 *     <tr><td>STATE_VAL</td><td><code>%VAL%</code></td><td><code>%S_VAL%</code></td></tr>
 *     <tr><td>STATE_VAL_BOOL</td><td><code>%VAL_BIN%</code></td><td><code>%S_VAL_BOOL%</code></td></tr>
 *     <tr><td>STATE_VAL_BIN</td><td><code>%VAL_BIN%</code></td><td><code>%S_VAL_BIN%</code></td></tr>
 *     <tr><td>STATE_VAL_COMMA</td><td><code>%VAL_COMMA%</code></td><td><code>%S_VAL_COMMA%</code></td></tr>
 *     <tr><td>STATE_VAL_POINT</td><td><code>%VAL_POINT%</code></td><td><code>%S_VAL_POINT%</code></td></tr>
 *     <tr><td>STATE_VAL_INT</td><td><code>%VAL_INT%</code></td><td><code>%S_VAL_INT%</code></td></tr>
 * </table>
 *
 * <h3>ActionCmd</h3>
 * <table border=1>
 *     <tr><th>PlaceHolder</th><th>Until 2.2.2</th><th>Since 2.2.3</th></tr>
 *     <tr><td>ACTION_VAL</td><td><code>%VAL%</code></td><td><code>%A_VAL%</code></td></tr>
 *     <tr><td>ACTION_VAL_OLD</td><td><code>%VAL_OLD%</code></td><td><code>%A_VAL_OLD%</code></td></tr>
 *     <tr><td>ACTION_SRV_ID</td><td><code>%SRV_ID%</code></td><td><code>%A_SRV_ID%</code></td></tr>
 *     <tr><td>ACTION_USR_ID</td><td><code>%USR_ID%</code></td><td><code>%A_USR_ID%</code></td></tr>
 *     <tr><td>ACTION_VAL_BOOL</td><td><code>%VAL_BOOL%</code></td><td><code>%A_VAL_BOOL%</code></td></tr>
 *     <tr><td>ACTION_VAL_OLD_BOOL</td><td><code>%VAL_OLD_BOOL%</code></td><td><code>%A_VAL_OLD_BOOL%</code></td></tr>
 *     <tr><td>ACTION_VAL_BIN</td><td><code>%VAL_BIN%</code></td><td><code>%A_VAL_BIN%</code></td></tr>
 *     <tr><td>ACTION_VAL_OLD_BIN</td><td><code>%VAL_OLD_BIN%</code></td><td><code>%A_VAL_OLD_BIN%</code></td></tr>
 *     <tr><td>ACTION_VAL_COMMA</td><td><code>%VAL_COMMA%</code></td><td><code>%A_VAL_COMMA%</code></td></tr>
 *     <tr><td>ACTION_VAL_OLD_COMMA</td><td><code>%VAL_OLD_COMMA%</code></td><td><code>%A_VAL_OLD_COMMA%</code></td></tr>
 *     <tr><td>ACTION_VAL_POINT</td><td><code>%VAL_POINT%</code></td><td><code>%A_VAL_POINT%</code></td></tr>
 *     <tr><td>ACTION_VAL_OLD_POINT</td><td><code>%VAL_OLD_POINT%</code></td><td><code>%A_VAL_OLD_POINT%</code></td></tr>
 *     <tr><td>ACTION_VAL_INT</td><td><code>%VAL_INT%</code></td><td><code>%A_VAL_INT%</code></td></tr>
 *     <tr><td>ACTION_VAL_OLD_INT</td><td><code>%VAL_OLD_INT%</code></td><td><code>%A_VAL_OLD_INT%</code></td></tr>
 * </table>
 */
public class Substitutions {

    /**
     * Default value returned on null element substitution.
     */
    public static final String DEF_NA = "N/A";

    //@formatter:off
    // Object substitution placeholders (see AbsJODWorker#objectSubstitution())
    /** JOD Version from {@link JODObjectInfo}, see {@link JODObjectInfo#getJODVersion()}. */
    public static final String OBJ_JOD_VER  = "%OBJ_JOD_VER%";
    /** Object's ID from {@link JODObjectInfo}, see {@link JODObjectInfo#getObjId()}  */
    public static final String OBJ_ID       = "%OBJ_ID%";
    /** Object's name from {@link JODObjectInfo}, see {@link JODObjectInfo#getObjName()}  */
    public static final String OBJ_NAME     = "%OBJ_NAME%";
    /** Object owner's ID from {@link JODObjectInfo}, see {@link JODObjectInfo#getOwnerId()}  */
    public static final String OBJ_OWNER    = "%OBJ_OWNER%";
    /** Object's model from {@link JODObjectInfo}, see {@link JODObjectInfo#getModel()}  */
    public static final String OBJ_MODEL    = "%OBJ_MODEL%";
    /** Object's brand from {@link JODObjectInfo}, see {@link JODObjectInfo#getBrand()}. */
    public static final String OBJ_BRAND    = "%OBJ_BRAND%";

    // Component substitution placeholders (see AbsJODWorker#componentSubstitution())
    /** Component's name from {@link JODComponent}, see {@link JODComponent#getName()}. */
    public static final String COMP_NAME        = "%COMP_NAME%";
    /** Component's type from {@link JODComponent}, see {@link JODComponent#getType()}. */
    public static final String COMP_TYPE        = "%COMP_TYPE%";
    /** Component's path from {@link JODComponent}, see {@link JODComponent#getPath()}. */
    public static final String COMP_PATH        = "%COMP_PATH%";
    /** Component parent's name from {@link JODComponent}, see {@link JODComponent#getParent()} and {@link JODComponent#getName()}. */
    public static final String COMP_PARENT_NAME = "%COMP_PARENT_NAME%";
    /** Component parent's name from {@link JODComponent}, see {@link JODComponent#getParent()} and {@link JODComponent#getPath()}. */
    public static final String COMP_PARENT_PATH = "%COMP_PARENT_PATH%";

    // Action substitution placeholders (see AbsJODWorker#stateSubstitution())
    /** State's value from {@link JODState}, see {@link JODState#getState()}. */
    public static final String STATE_VAL            = "%S_VAL%";
    /** State's value from {@link JODState} as "TRUE" or "FALSE", see {@link JODBooleanState#getState()} and {@link JavaFormatter#booleanToString(boolean)}. */
    public static final String STATE_VAL_BOOL       = "%S_VAL_BOOL%";
    /** State's value from {@link JODState} as "1" or "0", see {@link JODBooleanState#getState()} and {@link JavaFormatter#booleanToStringBin(boolean)}. */
    public static final String STATE_VAL_BIN        = "%S_VAL_BIN%";
    /** State's value from {@link JODState} as comma separated String, see {@link JODRangeState#getState()} and {@link JavaFormatter#doubleToStr_Comma(double)}. */
    public static final String STATE_VAL_COMMA      = "%S_VAL_COMMA%";
    /** State's value from {@link JODState} as point separated String, see {@link JODRangeState#getState()} and {@link JavaFormatter#doubleToStr_Point(double)}. */
    public static final String STATE_VAL_POINT      = "%S_VAL_POINT%";
    /** State's value from {@link JODState} as integer rounded String, see {@link JODRangeState#getState()} and {@link JavaFormatter#doubleToStr_Truncated(double)} . */
    public static final String STATE_VAL_INT        = "%S_VAL_INT%";

    // Action substitution placeholders
    /** Action's new state from {@link JOSPProtocol.ActionCmd}. */
    public static final String ACTION_VAL               = "%A_VAL%";
    /** Action's old state from {@link JOSPProtocol.ActionCmd}. */
    public static final String ACTION_VAL_OLD           = "%A_VAL_OLD%";
    /** Action service's ID from {@link JOSPProtocol.ActionCmd}, see {@link JOSPProtocol.ActionCmd#getServiceId()}. */
    public static final String ACTION_SRV_ID            = "%A_SRV_ID%";
    /** Action user's ID from {@link JOSPProtocol.ActionCmd}, see {@link JOSPProtocol.ActionCmd#getUserId()}. */
    public static final String ACTION_USR_ID            = "%A_USR_ID%";
    /** Action's new state from {@link JOSPProtocol.ActionCmd}, see {@link JODBooleanAction.JOSPBoolean#newState} and {@link JavaFormatter#booleanToString(boolean)}. */
    public static final String ACTION_VAL_BOOL          = "%A_VAL_BOOL%";
    /** Action's old state from {@link JOSPProtocol.ActionCmd}, see {@link JODBooleanAction.JOSPBoolean#oldState} and {@link JavaFormatter#booleanToString(boolean)}. */
    public static final String ACTION_VAL_OLD_BOOL      = "%A_VAL_OLD_BOOL%";
    /** Action's new state from {@link JOSPProtocol.ActionCmd}, see {@link JODBooleanAction.JOSPBoolean#newState} and {@link JavaFormatter#booleanToStringBin(boolean)}. */
    public static final String ACTION_VAL_BIN           = "%A_VAL_BIN%";
    /** Action's old state from {@link JOSPProtocol.ActionCmd}, see {@link JODBooleanAction.JOSPBoolean#oldState} and {@link JavaFormatter#booleanToStringBin(boolean)}. */
    public static final String ACTION_VAL_OLD_BIN       = "%A_VAL_OLD_BIN%";
    /** Action's old state from {@link JOSPProtocol.ActionCmd}, see {@link JODRangeAction.JOSPRange#newState} and {@link JavaFormatter#doubleToStr_Comma(double)}. */
    public static final String ACTION_VAL_COMMA             = "%A_VAL_COMMA%";
    /** Action's old state from {@link JOSPProtocol.ActionCmd}, see {@link JODRangeAction.JOSPRange#oldState} and {@link JavaFormatter#doubleToStr_Comma(double)}. */
    public static final String ACTION_VAL_OLD_COMMA         = "%A_VAL_OLD_COMMA%";
    /** Action's old state from {@link JOSPProtocol.ActionCmd}, see {@link JODRangeAction.JOSPRange#newState} and {@link JavaFormatter#doubleToStr_Point(double)}. */
    public static final String ACTION_VAL_POINT             = "%A_VAL_POINT%";
    /** Action's old state from {@link JOSPProtocol.ActionCmd}, see {@link JODRangeAction.JOSPRange#oldState} and {@link JavaFormatter#doubleToStr_Point(double)}. */
    public static final String ACTION_VAL_OLD_POINT         = "%A_VAL_OLD_POINT%";
    /** Action's old state from {@link JOSPProtocol.ActionCmd}, see {@link JODRangeAction.JOSPRange#newState} and {@link JavaFormatter#doubleToStr_Truncated(double)}. */
    public static final String ACTION_VAL_INT               = "%A_VAL_INT%";
    /** Action's old state from {@link JOSPProtocol.ActionCmd}, see {@link JODRangeAction.JOSPRange#oldState} and {@link JavaFormatter#doubleToStr_Truncated(double)}. */
    public static final String ACTION_VAL_OLD_INT           = "%A_VAL_OLD_INT%";
    //@formatter:on


    // Constructor

    private String str;

    /**
     * Create a new Substitution instance.
     *
     * @param str the string containing the placeholders to substitute.
     */
    public Substitutions(String str) {
        this.str = str;
    }


    // Substitution methods

    /**
     * Replace all {@link JODObjectInfo} placeholders.
     *
     * @param objInfo the instance used to get values to replace placeholders.
     * @return current Substitution instance.
     */
    public Substitutions substituteObject(JODObjectInfo objInfo) {
        this.str = doSubstituteObject(str, objInfo);
        return this;
    }

    /**
     * Replace all {@link JODComponent} placeholders.
     *
     * @param component the instance used to get values to replace placeholders.
     * @return current Substitution instance.
     */
    public Substitutions substituteComponent(JODComponent component) {
        this.str = doSubstituteComponent(str, component);
        return this;
    }

    /**
     * Replace all {@link JODWorker} placeholders.
     *
     * @param worker the instance used to get values to replace placeholders.
     * @return current Substitution instance.
     */
    public Substitutions substituteWorker(JODWorker worker) {
        this.str = doSubstituteWorker(str, worker);
        return this;
    }

    /**
     * Replace all {@link JODState} placeholders.
     *
     * @param componentState the instance used to get values to replace placeholders.
     * @return current Substitution instance.
     */
    public Substitutions substituteState(JODState componentState) {
        this.str = doSubstituteState(str, componentState);
        return this;
    }

    /**
     * Replace all {@link JOSPProtocol.ActionCmd} placeholders.
     *
     * @param commandAction the instance used to get values to replace placeholders.
     * @return current Substitution instance.
     */
    public Substitutions substituteAction(JOSPProtocol.ActionCmd commandAction) {
        this.str = doSubstituteAction(str, commandAction);
        return this;
    }


    // Static substitution methods

    private static String doSubstituteObject(String str, JODObjectInfo objInfo) {
        if (objInfo == null || str == null || str.isEmpty()) return str;

        return str.replaceAll(Substitutions.OBJ_ID, objInfo.getObjId()).replaceAll(Substitutions.OBJ_NAME, objInfo.getObjName()).replaceAll(Substitutions.OBJ_OWNER, objInfo.getOwnerId()).replaceAll(Substitutions.OBJ_JOD_VER, objInfo.getJODVersion()).replaceAll(Substitutions.OBJ_MODEL, objInfo.getModel()).replaceAll(Substitutions.OBJ_BRAND, objInfo.getBrand());
    }

    private static String doSubstituteComponent(String str, JODComponent component) {
        if (component == null || str == null || str.isEmpty()) return str;

        return str.replaceAll(Substitutions.COMP_NAME, component.getName()).replaceAll(Substitutions.COMP_PATH, component.getPath() != null ? component.getPath().getString() : DEF_NA).replaceAll(Substitutions.COMP_TYPE, component.getType()).replaceAll(Substitutions.COMP_PARENT_NAME, component.getParent() != null ? component.getParent().getName() : DEF_NA).replaceAll(Substitutions.COMP_PARENT_PATH, component.getParent() != null && component.getParent().getPath() != null ? component.getParent().getPath().getString() : DEF_NA);
    }

    private static String doSubstituteWorker(String str, JODWorker worker) {
        if (worker == null || str == null || str.isEmpty()) return str;

        for (Map.Entry<String, String> conf : worker.getConfigs().entrySet())
            str = str.replace(conf.getKey(), conf.getValue());

        return str;
    }

    private static String doSubstituteState(String str, JODState componentState) {
        if (componentState == null || str == null || str.isEmpty()) return str;

        if (componentState instanceof JODBooleanState) {
            boolean state = ((JODBooleanState) componentState).getStateBoolean();
            return str.replaceAll(Substitutions.STATE_VAL, JavaFormatter.booleanToString(state)).replaceAll(Substitutions.STATE_VAL_BOOL, JavaFormatter.booleanToString(state)).replaceAll(Substitutions.STATE_VAL_BIN, JavaFormatter.booleanToStringBin(state));
        }

        if (componentState instanceof JODRangeState) {
            double state = ((JODRangeState) componentState).getStateRange();
            return str.replaceAll(Substitutions.STATE_VAL, JavaFormatter.doubleToStr(state)).replaceAll(Substitutions.STATE_VAL_POINT, JavaFormatter.doubleToStr_Point(state)).replaceAll(Substitutions.STATE_VAL_COMMA, JavaFormatter.doubleToStr_Comma(state)).replaceAll(Substitutions.STATE_VAL_INT, JavaFormatter.doubleToStr_Truncated(state));
        }

        return str;
    }

    private static String doSubstituteAction(String str, JOSPProtocol.ActionCmd commandAction) {
        if (commandAction == null || str == null || str.isEmpty()) return str;

        str = str.replaceAll(Substitutions.ACTION_SRV_ID, commandAction.getServiceId()).replaceAll(Substitutions.ACTION_USR_ID, commandAction.getUserId());

        if (commandAction.getCommand() instanceof JODBooleanAction.JOSPBoolean) {
            JODBooleanAction.JOSPBoolean cmd = (JODBooleanAction.JOSPBoolean) commandAction.getCommand();
            return str.replaceAll(Substitutions.ACTION_VAL, JavaFormatter.booleanToString(cmd.newState)).replaceAll(Substitutions.ACTION_VAL_BOOL, JavaFormatter.booleanToString(cmd.newState)).replaceAll(Substitutions.ACTION_VAL_BIN, JavaFormatter.booleanToStringBin(cmd.newState)).replaceAll(Substitutions.ACTION_VAL_OLD, JavaFormatter.booleanToString(cmd.oldState)).replaceAll(Substitutions.ACTION_VAL_OLD_BOOL, JavaFormatter.booleanToString(cmd.oldState)).replaceAll(Substitutions.ACTION_VAL_OLD_BIN, JavaFormatter.booleanToStringBin(cmd.oldState));
        }

        if (commandAction.getCommand() instanceof JODRangeAction.JOSPRange) {
            JODRangeAction.JOSPRange cmd = (JODRangeAction.JOSPRange) commandAction.getCommand();
            return str.replaceAll(Substitutions.ACTION_VAL, JavaFormatter.doubleToStr(cmd.newState)).replaceAll(Substitutions.ACTION_VAL_POINT, JavaFormatter.doubleToStr_Point(cmd.newState)).replaceAll(Substitutions.ACTION_VAL_COMMA, JavaFormatter.doubleToStr_Comma(cmd.newState)).replaceAll(Substitutions.ACTION_VAL_INT, JavaFormatter.doubleToStr_Truncated(cmd.newState)).replaceAll(Substitutions.ACTION_VAL_OLD, JavaFormatter.doubleToStr(cmd.oldState)).replaceAll(Substitutions.ACTION_VAL_OLD_POINT, JavaFormatter.doubleToStr_Point(cmd.oldState)).replaceAll(Substitutions.ACTION_VAL_OLD_COMMA, JavaFormatter.doubleToStr_Comma(cmd.oldState)).replaceAll(Substitutions.ACTION_VAL_OLD_INT, JavaFormatter.doubleToStr_Truncated(cmd.oldState));
        }

        return str;
    }


    // toString()

    /**
     * @return current instance string, with replaced placeholder if any
     * substitution method was called.
     */
    @Override
    public String toString() {
        return str;
    }

}
