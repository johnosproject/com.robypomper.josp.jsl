/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
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

package com.robypomper.settings;

import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Default settings implementation class.
 * <p>
 * This class provide a common way to read settings from a <code>yaml</code>
 * file or from a <code>Map(String,String)</code>object.
 * <p>
 * This class provide to his sub classes the methods to parse/get different
 * types of settings ({@link String}, {@link Integer}, {@link Boolean} and
 * {@link File}).
 * <p>
 * When settings are read from a <code>yaml</code> file, it's possible also
 * set and store settings to read file with {@link #store(String, String, boolean)}
 * method.
 */
public class DefaultSettings {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final File file;
    private final Map<String, Object> properties;
    private boolean errorAlreadyPrinted = false;


    // Constructor

    /**
     * Load settings from given file.
     * <p>
     * If the file not exist, then a {@link FileNotFoundException} is thrown.
     *
     * @param yalmFile the file containing the settings.
     */
    public DefaultSettings(File yalmFile) throws FileNotFoundException {
        this.file = yalmFile;

        InputStream inputStream = new FileInputStream(file);
        Map<String, Object> tmpProp = new Yaml().load(inputStream);
        if (tmpProp == null) tmpProp = new HashMap<>();
        properties = tmpProp;
    }

    /**
     * Load settings from given map.
     *
     * @param map the map containing the settings.
     */
    public DefaultSettings(Map<String, Object> map) {
        this.file = null;
        this.properties = map;
    }


    // Store

    /**
     * Set given pair properties and value.
     * <p>
     * If <code>syncFile</code> is <code>true</code> then it try to store the
     * pair also on the file used to load current settings. If current settings
     * are not loaded from file then it print a log warning message. That
     * message will printed only once.
     *
     * @param property the property name to store.
     * @param value    the property value to store.
     * @param syncFile if <code>true</code> then this method try to store the
     *                 pair on the file, otherwise not.
     */
    protected void store(String property, String value, boolean syncFile) {
        if (value == null)
            properties.remove(property);
        else
            properties.put(property, value);

        if (!syncFile)
            return;

        if (file == null) {
            if (!errorAlreadyPrinted) {
                log.error(Mrk_JOD.JOD_MAIN, "Can't store configs on file, because settings are not loaded from file.");
                errorAlreadyPrinted = true;
            }
            return;
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
        } catch (IOException ignore) {}

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        new Yaml(options).dump(properties, writer);
    }


    // Getters

    /**
     * Get the property {@link String} value or the default value.
     *
     * @param property  required property name.
     * @param defString default property value.
     * @return the property value, or the default value if can't find the property.
     */
    protected String getString(String property, String defString) {
        if (properties.get(property) != null) {
            Object propObj = properties.get(property);
            if (propObj instanceof String)
                return (String) propObj;
        }

        return defString;
    }

    /**
     * Get the property {@link Integer} value or the default value.
     * <p>
     * This method accept properties value in {@link Integer} and {@link String}
     * formats.
     *
     * @param property  required property name.
     * @param defString default property value.
     * @return the property value, or the default value if can't find the property.
     */
    protected int getInt(String property, String defString) {
        if (properties.get(property) != null) {
            Object propObj = properties.get(property);
            if (propObj instanceof Integer)
                return (int) propObj;
            if (propObj instanceof String)
                return Integer.parseInt((String) propObj);
        }

        return Integer.parseInt(defString);
    }

    /**
     * Get the property {@link Boolean} value or the default value.
     * <p>
     * This method accept properties value in {@link Boolean} and {@link String}
     * formats.
     *
     * @param property  required property name.
     * @param defString default property value.
     * @return the property value, or the default value if can't find the property.
     */
    protected boolean getBoolean(String property, String defString) {
        if (properties.get(property) != null) {
            Object propObj = properties.get(property);
            if (propObj instanceof Boolean)
                return (boolean) propObj;
            if (propObj instanceof String)
                return Boolean.parseBoolean((String) propObj);
        }

        return Boolean.parseBoolean(defString);
    }

    /**
     * Get the property {@link File} value or the default value.
     * <p>
     * This method accept properties value in {@link String} formats as file paths.
     *
     * @param property  required property name.
     * @param defString default property value.
     * @return the property value, or the default value if can't find the property.
     */
    protected File getFile(String property, String defString) {
        String fileName = getString(property, defString);
        return new File(fileName);
    }

}
