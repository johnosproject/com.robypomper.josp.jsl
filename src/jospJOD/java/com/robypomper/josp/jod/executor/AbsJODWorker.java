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
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.pillars.JODBooleanState;
import com.robypomper.josp.jod.structure.pillars.JODRangeState;

import java.util.HashMap;
import java.util.Map;


/**
 * Default basic implementation class for JOD executor's worker representations.
 */
public abstract class AbsJODWorker implements JODWorker {

    // Class constants

    public static final String CONFIG_STR_SEP = "://";


    // Internal vars

    private final String proto;
    private final String name;
    private final JODComponent component;
    private final Map<String, String> configs;


    // Constructor

    /**
     * Default constructor.
     *
     * @param name  the JODWorker's name.
     * @param proto the JODWorker's protocol.
     */
    public AbsJODWorker(String name, String proto, JODComponent component) {
        this.proto = proto;
        this.name = name;
        this.component = component;
        this.configs = new HashMap<>();
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProto() {
        return proto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODComponent getComponent() {
        return component;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getConfigs() {
        return configs;
    }

    /**
     * Values (value and defaultValue) must not contain others placeholders.
     */
    protected void setConfigs(String placeHolder, String value, String defaultValue) {
        if (value == null || value.isEmpty())
            configs.put(placeHolder, defaultValue);
        else
            configs.put(placeHolder, value);
    }


    // Status convert & set methods

    protected boolean convertAndSetStatus(String newStatus) {
        if (getComponent() instanceof JODBooleanState) {
            ((JODBooleanState) getComponent()).setUpdate(JavaFormatter.strToBoolean(newStatus.toUpperCase()));
            return true;
        }

        if (getComponent() instanceof JODRangeState) {
            Double val = JavaFormatter.strToDouble(newStatus);
            if (val != null)
                ((JODRangeState) getComponent()).setUpdate(val);
            return true;
        }

        return false;
    }


    // Full config string mngm

    /**
     * Return the protocol part of the worker fullConfigs string.
     *
     * @param fullConfigs the worker full configs string.
     * @return the protocol defined in given full configs string.
     */
    public static String extractProto(String fullConfigs) throws MalformedConfigsException {
        try {
            return fullConfigs.substring(0, fullConfigs.indexOf(CONFIG_STR_SEP)).trim();
        } catch (Exception e) {
            throw new MalformedConfigsException(fullConfigs, e);
        }
    }

    /**
     * Return the configs/name part of the worker fullConfigs string.
     *
     * @param fullConfigs the worker full configs string.
     * @return the configs/name defined in given full configs string.
     */
    public static String extractConfigsStr(String fullConfigs) throws MalformedConfigsException {
        try {
            return fullConfigs.substring(fullConfigs.indexOf(CONFIG_STR_SEP) + 3).trim();
        } catch (Exception e) {
            throw new MalformedConfigsException(fullConfigs, e);
        }
    }

    /**
     * Compose the two part of the full configs string.
     *
     * @param proto  the string used as protocol part of the full configs string.
     * @param config the string used as congis/name part of the full configs string.
     * @return the composed full configs string.
     */
    public static String mergeConfigsStr(String proto, String config) {
        return proto + CONFIG_STR_SEP + config;
    }

    public static Map<String, String> splitConfigsStrings(String config) {
        Map<String, String> configMap = new HashMap<>();

        for (String keyAndProp : config.split(";")) {

            // Boolean props
            if (keyAndProp.indexOf('=') < 0) {
                configMap.put(keyAndProp, "true");
                continue;
            }

            // Key = Value properties
            String key = keyAndProp.substring(0, keyAndProp.indexOf('='));
            String value = keyAndProp.substring(keyAndProp.indexOf('=') + 1);
            configMap.put(key, value);
        }

        return configMap;
    }

    public String parseConfigString(Map<String, String> configMap, String key) throws MissingPropertyException {
        String value = configMap.get(key);
        if (value == null)
            throw new MissingPropertyException(key, getProto(), getName(), "Listener");

        return parseConfigString(configMap, key, value);
    }

    public String parseConfigString(Map<String, String> configMap, String key, String defValue) {
        String strValue = configMap.get(key);
        if (strValue == null)
            strValue = defValue;

        strValue = parseConfigSubstitution(strValue);
        if ((strValue.startsWith("'") && strValue.endsWith("'"))
                || (strValue.startsWith("\"") && strValue.endsWith("\""))) {
            //if ((strValue.charAt(0)=='\'' && strValue.charAt(strValue.length()-1)=='\'')
            // || (strValue.charAt(0)=='"' && strValue.charAt(strValue.length()-1)=='"')) {
            strValue = strValue.substring(1, strValue.length() - 1);
        }

        return strValue;
    }

    public boolean parseConfigBoolean(Map<String, String> configMap, String key) throws MissingPropertyException, ParsingPropertyException {
        String value = configMap.get(key);
        if (value == null)
            throw new MissingPropertyException(key, getProto(), getName(), "Listener");

        return parseConfigBoolean(configMap, key, value);
    }

    public boolean parseConfigBoolean(Map<String, String> configMap, String key, String defValue) throws ParsingPropertyException {
        String strValue = configMap.get(key);
        if (strValue == null)
            strValue = defValue;

        strValue = parseConfigSubstitution(strValue);

        try {
            return Boolean.parseBoolean(strValue);
        } catch (Exception e) {
            throw new ParsingPropertyException(key, getProto(), getName(), "Listener", strValue, e);
        }
    }

    public int parseConfigInt(Map<String, String> configMap, String key) throws MissingPropertyException, ParsingPropertyException {
        String value = configMap.get(key);
        if (value == null)
            throw new MissingPropertyException(key, getProto(), getName(), "Listener");

        return parseConfigInt(configMap, key, value);
    }

    public int parseConfigInt(Map<String, String> configMap, String key, String defValue) throws ParsingPropertyException {
        String strValue = configMap.get(key);
        if (strValue == null)
            strValue = defValue;

        strValue = parseConfigSubstitution(strValue);

        try {
            return Integer.parseInt(strValue);
        } catch (Exception e) {
            throw new ParsingPropertyException(key, getProto(), getName(), "Listener", strValue, e);
        }
    }

    public Double parseConfigDouble(Map<String, String> configMap, String key) throws MissingPropertyException, ParsingPropertyException {
        String value = configMap.get(key);
        if (value == null)
            throw new MissingPropertyException(key, getProto(), getName(), "Listener");

        return parseConfigDouble(configMap, key, value);
    }

    public Double parseConfigDouble(Map<String, String> configMap, String key, String defValue) throws ParsingPropertyException {
        String strValue = configMap.get(key);
        if (strValue == null)
            strValue = defValue;

        strValue = parseConfigSubstitution(strValue);

        try {
            return Double.parseDouble(strValue);
        } catch (Exception e) {
            throw new ParsingPropertyException(key, getProto(), getName(), "Listener", strValue, e);
        }
    }

    protected String parseConfigSubstitution(String strValue) {
        return new Substitutions(strValue)
                //.substituteObject(jod.getObjectInfo())
                //.substituteObjectConfigs(jod.getObjectInfo())
                .substituteComponent(component)
                .toString();
    }

}
