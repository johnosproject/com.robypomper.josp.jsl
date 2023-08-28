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

package com.robypomper.josp.jod.executor.factories;

import com.robypomper.josp.jod.executor.AbsJODWorker;
import com.robypomper.josp.jod.executor.JODWorker;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.executor.JODComponentWorker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Basic class for JODWorker factory classes.
 * <p>
 * This class allow to register worker protocols/implementations that can be used
 * to initialize a worker with {@link #create(JODComponentWorker)} method.
 */
public abstract class AbsFactoryJODWorker<T extends AbsJODWorker> {

    // Internal vars

    private final Map<String, Class<? extends T>> implClasses = new HashMap<>();


    // Constructor

    /**
     * Protected constructor because only subclass can be initialized.
     */
    protected AbsFactoryJODWorker() {

    }

    /**
     * Convenient method that must be override from sub classes.
     *
     * @return new instance of implementation sub class.
     */
    public static AbsFactoryJODWorker<? extends AbsJODWorker> instance() {
        return null;
    }


    // Getters

    /**
     * @return String containing the factory type ("Puller"/"Listener"/"Executor").
     */
    public abstract String getType();

    /**
     * @return the list of registered protocols/implementations.
     */
    public Map<String, Class<? extends T>> getProtocols() {
        return Collections.unmodifiableMap(implClasses);
    }


    // Implementations registration

    /**
     * Register the protocol <code>proto</code> with <code>className</code>
     * implementation.
     *
     * @param proto     worker implementation protocol to register.
     * @param className worker implementation class name.
     */
    public void register(String proto, String className) throws T.FactoryException {
        try {
            Class<?> classRaw = getClass().getClassLoader().loadClass(className);
            if (AbsJODWorker.class.isAssignableFrom(classRaw))
                register(proto, (Class<? extends T>) classRaw);
            else
                throw new T.FactoryException(String.format("Can't register '%s' JOD " + getType() + " protocol because wrong type of given class '%s' .", proto, className));
        } catch (ClassNotFoundException e) {
            throw new T.FactoryException(String.format("Can't register '%s' JOD " + getType() + " protocol because class '%s' not found.", proto, className));
        }
    }

    /**
     * Register the protocol <code>proto</code> with <code>className</code>
     * implementation.
     *
     * @param proto worker implementation protocol to register.
     * @param clazz worker implementation class.
     */
    public void register(String proto, Class<? extends T> clazz) throws T.FactoryException {
        if (implClasses.containsKey(proto))
            throw new T.FactoryException(String.format("Can't register '%s' JOD " + getType() + " implementation because '%s' was already set for the same protocol '%s'.", clazz.getName(), implClasses.get(proto).getName(), proto));

        implClasses.put(proto, clazz);
    }


    // Factory method

    /**
     * Create new worker depending on <code>componentWorker</code> content.
     *
     * @param componentWorker settings to use for worker creation.
     */
    public T create(JODComponentWorker componentWorker) throws T.FactoryException {
        String name = componentWorker.getName();
        String proto = componentWorker.getProto();
        String configStr = componentWorker.getConfigsStr();

        if (!implClasses.containsKey(proto))
            throw new T.FactoryException(String.format("Can't init JOD " + getType() + " because no implementation found for '%s' protocol", proto));

        try {
            Constructor<? extends T> c = implClasses.get(proto).getConstructor(String.class, String.class, String.class, JODComponent.class);
            T t = c.newInstance(name, proto, configStr, componentWorker.getComponent());
            return t;

        } catch (NoSuchMethodException e) {
            throw new T.FactoryException("Error on init JOD " + getType() + ", required constructor not found.", e);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new T.FactoryException(String.format("Error on init JOD " + getType() + ", constructor thrown exception: '%s'.", e.getMessage()), e);
        } catch (InvocationTargetException e) {
            Throwable targetEx = e.getTargetException();
            if (targetEx instanceof JODWorker.MissingPropertyException)
                throw new T.FactoryException(targetEx.getMessage());

            throw new T.FactoryException(String.format("Error on init JOD " + getType() + ", constructor thrown exception: '%s'.", targetEx.getMessage()));
        }
    }

}
