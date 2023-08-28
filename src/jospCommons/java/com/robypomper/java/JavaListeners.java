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

package com.robypomper.java;

import java.util.ArrayList;
import java.util.List;

/**
 * Utils class for listener implementations.
 */
public class JavaListeners {

    // Emitters

    /**
     * Helper class to mapping method to a generic type.
     *
     * @param <T> the generic type to map method with.
     */
    public interface ListenerMapper<T> {
        void map(T t);
    }

    /**
     * Helper method to emit events.
     * <p>
     * This method create a new listeners list to prevent
     * {@link java.util.ConcurrentModificationException} in case of a listener
     * add/remove him self or another listener to the list.
     * <p>
     * Thie method, also catch exception thrown by listener and print an
     * {@link JavaAssertions} error.
     *
     * @param instance  object that emit the event.
     * @param listeners list of listener waiting for emitted event.
     * @param eventName string containing event name (aka the method name to be
     *                  executed)
     * @param mapper    mapper object to allow execute listeners event's method.
     * @param <T>       type of managed listeners.
     */
    public static <T> void emitter(Object instance, List<T> listeners, String eventName, ListenerMapper<T> mapper) {
        List<T> list = new ArrayList<>(listeners);
        for (T l : list)
            try {
                mapper.map(l);

            } catch (Throwable e) {
                if (e instanceof AssertionError)
                    return;

                String listenerType = l != null ? l.getClass().getSimpleName() : "N/A";
                JavaAssertions.makeAssertion_Failed(e, String.format("Catch exception executing event %s.%s() of '%s' instance for '%s' listener.", listenerType, eventName, instance, l));
            }
    }

}
