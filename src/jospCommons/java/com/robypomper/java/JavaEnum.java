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

import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.Logger;


/**
 * Enum synchronization helper class.
 * <p>
 * This class provide the sub classes to make Enum synchronizable.
 * By default Enum instances can't be used as synchronizable objects and
 * updated in synchronized code block. Check follow example:
 *
 * <pre>
 * enum Enum {
 *   A, B
 * }
 *
 * Enum x = Enum.A;
 *
 * void method() {
 *   synchronized (x) {
 *     // sync on Enum.A instance
 *     // next synchronized block will sync on Enum.B instance
 *     x = Enum.B;
 *   }
 * }
 * </pre>
 * <p>
 * With the help from {@link Synchronizable} class can be rewrite as:
 *
 * <pre>
 * enum Enum {
 *   A, B
 * }
 * final Synchronizable&lt;Enum&gt; x = new Synchronizable&lt;&gt;(Enum.A);
 *
 * void method() {
 *   synchronized (x) {
 *     x.set(Enum.B);
 *   }
 * }
 * </pre>
 * <p>
 * The {@link SynchronizableState} class add logging automation to
 * {@link Synchronizable} class. Each time the value is update it
 * print a log message.
 */
public class JavaEnum {

    // Synchronizable Enum classes

    /**
     * Class that make Enum synchronizable.
     *
     * @param <E> the Enum type to make synchronizable.
     */
    public static class Synchronizable<E> {

        // Internal vars

        private E val;


        // Constructor

        /**
         * Default constructor that set initial internal Enum instance.
         *
         * @param val initial value to set.
         */
        public Synchronizable(E val) {
            this.val = val;
        }


        // Value access

        /**
         * Update internal Enum instance.
         *
         * @param val the Enum value to set.
         */
        public void set(E val) {
            this.val = val;
        }

        /**
         * @return the internal Enum instance.
         */
        public E get() {
            return val;
        }


        // Value comparison

        /**
         * Shortcut for Enum comparison.
         *
         * @param obj the Enum instance to compare with.
         * @return true if internal Enum and <code>obj</code> are equals.
         */
        public boolean enumEquals(E obj) {
            return this.val == obj;
        }

        /**
         * Shortcut for Enum comparison.
         *
         * @param obj the Enum instance to compare with.
         * @return true if internal Enum and <code>obj</code> are NOT equals.
         */
        public boolean enumNotEquals(E obj) {
            return !enumEquals(obj);
        }

    }

    /**
     * Synchronizable enum and logging class.
     * <p>
     * When internal Enum is updated, this class print a log message including
     * the class::method that called the {@link #set(Object)} method.
     *
     * @param <E> the Enum type to make synchronizable.
     */
    public static class SynchronizableState<E> extends Synchronizable<E> {

        // Internal vars

        private Logger log;
        private String stateName;


        // Constructor

        /**
         * Default constructor that set initial internal Enum instance.
         * <p>
         * Set SynchronizableState's son class name as state name.
         *
         * @param val initial value to set.
         */
        public SynchronizableState(E val) {
            this(val, Thread.currentThread().getStackTrace()[2].getClassName().substring(Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf('.') + 1), null);
        }

        /**
         * Constructor to set internal Enum instance and state name.
         *
         * @param val       initial value to set.
         * @param stateName state name to set.
         */
        public SynchronizableState(E val, String stateName) {
            this(val, stateName, null);
        }

        /**
         * Constructor to set internal Enum instance and state name.
         *
         * @param val initial value to set.
         * @param log the logger instance to use to log updates.
         */
        public SynchronizableState(E val, Logger log) {
            this(val, Thread.currentThread().getStackTrace()[2].getClassName().substring(Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf('.') + 1), log);
        }

        /**
         * Constructor to set internal Enum instance and state name.
         *
         * @param val       initial value to set.
         * @param stateName state name to set.
         * @param log       the logger instance to use to log updates.
         */
        public SynchronizableState(E val, String stateName, Logger log) {
            super(val);
            this.log = log;
            this.stateName = stateName;
        }


        // Value access

        /**
         * Update internal Enum instance.
         *
         * @param val the Enum value to set.
         */
        public void set(E val) {
            set(val, log, stateName);
        }

        /**
         * Update internal Enum instance.
         *
         * @param val the Enum value to set.
         * @param log the logger object to print the update message.
         */
        public void set(E val, Logger log) {
            set(val, log, stateName);
        }

        /**
         * Update internal Enum instance.
         *
         * @param val       the Enum value to set.
         * @param log       the logger object to print the update message.
         * @param stateName the state's name to use in the update message.
         */
        public void set(E val, Logger log, String stateName) {
            E oldVal = this.get();
            super.set(val);

            if (log == null)
                return;

            StackTraceElement caller = null;
            for (StackTraceElement e : Thread.currentThread().getStackTrace())
                if (!e.getClassName().equals(Thread.class.getName())
                        && e.getClassName().equals(Thread.class.getName())) {
                    caller = e;
                    break;
                }
            if (caller == null)
                log.debug(Mrk_Commons.STATE, String.format("%s new state = %s (from = %s)", stateName, val, oldVal));
            else
                log.debug(Mrk_Commons.STATE, String.format("%s new state = %s by %s::%s at line %d (from = %s)", stateName, val, caller.getClassName(), caller.getMethodName(), caller.getLineNumber(), oldVal));
        }


        // Setters

        /**
         * Set internal logger.
         *
         * @param log the logger object to use for print update messages.
         */
        public void setLog(Logger log) {
            this.log = log;
        }

        /**
         * Set internal state's name.
         *
         * @param stateName the state's name to use in update messages.
         */
        public void setStateName(String stateName) {
            this.stateName = stateName;
        }

    }

}
