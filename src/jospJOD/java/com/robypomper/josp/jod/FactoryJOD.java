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

package com.robypomper.josp.jod;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * JOD Object factory.
 * <p>
 * This class allow create JOD objects specifying their settings and JOD version.
 * <p>
 * JOD and JOD.Settings versions must match.
 * <p>
 * String versions are resolved with {@link JOD.Settings} implementation class
 * or a {@link AbsJOD} sub-class by {@link #getJODSettingsClass(String)} and
 * {@link #getJODSettingsClass(String)}.
 */
public class FactoryJOD {

    // Version constants

    public static final String JOD_VER_002 = "0.0.2";
    private static final Class<? extends AbsJOD> JOD_VER_002_CLASS = JOD_002.class;
    private static final Class<? extends JOD.Settings> JOD_VER_002_CONFIG_CLASS = JODSettings_002.class;
    public static final String JOD_VER_2_0_0 = "2.0.0";
    private static final Class<? extends AbsJOD> JOD_VER_2_0_0_CLASS = JOD_002.class;
    private static final Class<? extends JOD.Settings> JOD_VER_2_0_0_CONFIG_CLASS = JODSettings_002.class;

    public static final String JOD_VER_LATEST = JOD_VER_2_0_0;


    // New instance method name

    private static final String NEW_INSTANCE_METHOD = "instance";


    // Factory methods

    /**
     * Create new JOD Settings object from given <code>fileName</code>.
     *
     * @param fileName file path of JOD config file.
     * @return JOD Settings object.
     */
    public static JOD.Settings loadSettings(String fileName) throws JOD.FactoryException {
        return loadSettings(fileName, "");
    }

    /**
     * Create new JOD Settings object from given <code>fileName</code> and required
     * <code>jodVer</code>.
     * <p>
     * If <code>jodVer</code> is not empty, then his value will be updated on
     * JOD.Settings loaded object. Otherwise {@link #JOD_VER_LATEST}
     * ({@value #JOD_VER_LATEST}) version will be used.
     *
     * @param fileName file path of JOD config file.
     * @param jodVer   version corresponding to JOD.Settings implementation required.
     * @return JOD Settings object.
     */
    public static JOD.Settings loadSettings(String fileName, String jodVer) throws JOD.FactoryException {
        File file = Paths.get(fileName).toFile();
        if (!file.exists())
            throw new JOD.FactoryException(String.format("JOD config file '%s' not found.", file.getAbsolutePath()));
        if (jodVer.isEmpty()) jodVer = JOD_VER_LATEST;

        Class<? extends JOD.Settings> jodSettingsClass = getJODSettingsClass(jodVer);

        try {
            Method method = jodSettingsClass.getMethod(NEW_INSTANCE_METHOD, file.getClass());
            Object instance = method.invoke(null, file);
            if (instance == null)
                throw new JOD.FactoryException(String.format("JOD.Settings init method '%s::%s(%s)' return null object.", jodSettingsClass.getName(), NEW_INSTANCE_METHOD, file.getClass().getSimpleName()));
            if (jodSettingsClass.isInstance(instance))
                return jodSettingsClass.cast(instance);
            if (instance instanceof JOD.Settings)
                throw new JOD.FactoryException(String.format("JOD.Settings init method '%s::%s(%s)' return object of wrong sub-type '%s'.", jodSettingsClass.getName(), NEW_INSTANCE_METHOD, file.getClass().getSimpleName(), instance.getClass().getSimpleName()));
            else
                throw new JOD.FactoryException(String.format("JOD.Settings init method '%s::%s(%s)' return wrong object of type '%s'.", jodSettingsClass.getName(), NEW_INSTANCE_METHOD, file.getClass().getSimpleName(), instance.getClass().getSimpleName()));

        } catch (NoSuchMethodException e) {
            throw new JOD.FactoryException(String.format("JOD.Settings init method '%s::%s(%s)' not found.", jodSettingsClass.getName(), NEW_INSTANCE_METHOD, file.getClass().getSimpleName()), e);
        } catch (IllegalAccessException e) {
            throw new JOD.FactoryException(String.format("Can't access to JOD.Settings init method '%s::%s(%s)'.", jodSettingsClass.getName(), NEW_INSTANCE_METHOD, file.getClass().getSimpleName()), e);
        } catch (InvocationTargetException e) {
            throw new JOD.FactoryException(String.format("Error.Settings occurred during '%s::%s(%s)' JOD.Settings init method execution.", jodSettingsClass.getName(), NEW_INSTANCE_METHOD, file.getClass().getSimpleName()), e);
        }
    }

    /**
     * Create new JOD Settings object from given <code>properties</code>.
     *
     * @param properties map containing the properties to set as JOD configurations.
     * @return JOD Settings object.
     */
    public static JOD.Settings loadSettings(Map<String, Object> properties) throws JOD.FactoryException {
        return loadSettings(properties, "");
    }

    /**
     * Create new JOD Settings object from given <code>fileName</code> and required
     * <code>jodVer</code>.
     * <p>
     * If <code>jodVer</code> is not empty, then his value will be updated on
     * JOD.Settings loaded object. Otherwise {@link #JOD_VER_LATEST}
     * ({@value #JOD_VER_LATEST}) version will be used.
     *
     * @param properties map containing the properties to set as JOD configurations.
     * @param jodVer     version corresponding to JOD.Settings implementation required.
     * @return JOD Settings object.
     */
    public static JOD.Settings loadSettings(Map<String, Object> properties, String jodVer) throws JOD.FactoryException {
        if (properties == null)
            properties = new HashMap<>();
        if (jodVer.isEmpty()) jodVer = JOD_VER_LATEST;

        Class<? extends JOD.Settings> jodSettingsClass = getJODSettingsClass(jodVer);

        try {
            Method method = jodSettingsClass.getMethod(NEW_INSTANCE_METHOD, Map.class);
            Object instance = method.invoke(null, properties);
            if (instance == null)
                throw new JOD.FactoryException(String.format("JOD.Settings init method '%s::%s(%s)' return null object.", jodSettingsClass.getName(), NEW_INSTANCE_METHOD, properties.getClass().getSimpleName()));
            if (jodSettingsClass.isInstance(instance))
                return jodSettingsClass.cast(instance);
            if (instance instanceof JOD.Settings)
                throw new JOD.FactoryException(String.format("JOD.Settings init method '%s::%s(%s)' return object of wrong sub-type '%s'.", jodSettingsClass.getName(), NEW_INSTANCE_METHOD, properties.getClass().getSimpleName(), instance.getClass().getSimpleName()));
            else
                throw new JOD.FactoryException(String.format("JOD.Settings init method '%s::%s(%s)' return wrong object of type '%s'.", jodSettingsClass.getName(), NEW_INSTANCE_METHOD, properties.getClass().getSimpleName(), instance.getClass().getSimpleName()));

        } catch (NoSuchMethodException e) {
            throw new JOD.FactoryException(String.format("JOD.Settings init method '%s::%s(%s)' not found.", jodSettingsClass.getName(), NEW_INSTANCE_METHOD, properties.getClass().getSimpleName()), e);
        } catch (IllegalAccessException e) {
            throw new JOD.FactoryException(String.format("Can't access to JOD.Settings init method '%s::%s(%s)'.", jodSettingsClass.getName(), NEW_INSTANCE_METHOD, properties.getClass().getSimpleName()), e);
        } catch (InvocationTargetException e) {
            throw new JOD.FactoryException(String.format("Error.Settings occurred during '%s::%s(%s)' JOD.Settings init method execution.", jodSettingsClass.getName(), NEW_INSTANCE_METHOD, properties.getClass().getSimpleName()), e);
        }
    }

    /**
     * Create new JOD Object with given <code>settings</code>.
     *
     * @param settings JOD Settings object.
     * @return JOD Object.
     */
    public static JOD createJOD(JOD.Settings settings) throws JOD.FactoryException {
        return createJOD(settings, JOD_VER_LATEST);
    }

    /**
     * Create new JOD Object with given <code>settings</code> and required
     * <code>jodVer</code>.
     *
     * @param settings JOD Settings object.
     * @param jodVer   version corresponding to JOD implementation required.
     * @return JOD Object.
     */
    public static JOD createJOD(JOD.Settings settings, String jodVer) throws JOD.FactoryException {
        if (settings == null) throw new JOD.FactoryException("JOD init method require Settings param");
        if (jodVer.isEmpty()) jodVer = JOD_VER_LATEST;

        Class<? extends AbsJOD> jodClass = getJODClass(jodVer);

        try {
            Method method = jodClass.getMethod(NEW_INSTANCE_METHOD, settings.getClass());
            Object instance = method.invoke(null, settings);
            if (instance == null)
                throw new JOD.FactoryException(String.format("JOD init method '%s::%s(%s)' return null object.", jodClass.getName(), NEW_INSTANCE_METHOD, settings.getClass().getSimpleName()));
            if (instance instanceof JOD)
                return (JOD) instance;
            throw new JOD.FactoryException(String.format("JOD init method '%s::%s(%s)' return wrong object of type '%s'.", jodClass.getName(), NEW_INSTANCE_METHOD, settings.getClass().getSimpleName(), instance.getClass().getSimpleName()));

        } catch (NoSuchMethodException e) {
            throw new JOD.FactoryException(String.format("JOD init method '%s::%s(%s)' not found.", jodClass.getName(), NEW_INSTANCE_METHOD, settings.getClass().getSimpleName()), e);
        } catch (IllegalAccessException e) {
            throw new JOD.FactoryException(String.format("Can't access to JOD init method '%s::%s(%s)'.", jodClass.getName(), NEW_INSTANCE_METHOD, settings.getClass().getSimpleName()), e);
        } catch (InvocationTargetException e) {
            throw new JOD.FactoryException(String.format("Error occurred during '%s::%s(%s)' JOD init method execution.", jodClass.getName(), NEW_INSTANCE_METHOD, settings.getClass().getSimpleName()), e);
        }
    }


    // Implementation class finders

    /**
     * <ul>
     *     <li>
     *         {@value JOD_VER_2_0_0} => {@link JODSettings_002}
     *     </li>
     * </ul>
     *
     * @param jodVer version corresponding to JOD.Settings implementation required.
     * @return JOD.Settings class corresponding to given <code>jodVer</code> version.
     */
    private static Class<? extends JOD.Settings> getJODSettingsClass(String jodVer) throws JOD.FactoryException {
        if (JOD_VER_002.compareToIgnoreCase(jodVer) == 0) return JOD_VER_002_CONFIG_CLASS;
        if (JOD_VER_2_0_0.compareToIgnoreCase(jodVer) == 0) return JOD_VER_2_0_0_CONFIG_CLASS;

        throw new JOD.FactoryException(String.format("JOD.Settings '%s' version not found.", jodVer));
    }

    /**
     * <ul>
     *     <li>
     *         {@value JOD_VER_2_0_0} => {@link JOD_002}
     *     </li>
     * </ul>
     *
     * @param jodVer version corresponding to JOD implementation required.
     * @return JOD class corresponding to given <code>jodVer</code> version.
     */
    private static Class<? extends AbsJOD> getJODClass(String jodVer) throws JOD.FactoryException {
        if (JOD_VER_002.compareToIgnoreCase(jodVer) == 0) return JOD_VER_002_CLASS;
        if (JOD_VER_2_0_0.compareToIgnoreCase(jodVer) == 0) return JOD_VER_2_0_0_CLASS;

        throw new JOD.FactoryException(String.format("JOD.Settings '%s' version not found.", jodVer));
    }
}
