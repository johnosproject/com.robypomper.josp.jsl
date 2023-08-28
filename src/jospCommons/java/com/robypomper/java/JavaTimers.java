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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Class for timers utils.
 */
public class JavaTimers {

    // Class constants

    private static final boolean IS_DAEMON = false;
    private static final int PERIOD = 0;


    // Init and start Timer and Task

    /**
     * Initialize and start a new Timer with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method and
     * initialize a TimerTask with {@link #initTask(Runnable, String)} method.
     * <p>
     * Timer thread and timer task thread can be differentiate because first
     * one terminate with <code>Timer</code> string and the other one with
     * <code>Task</code> string.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param name     creating timer and timer task threads generic name.
     * @param delayMs  delay in milliseconds before task is to be executed.
     * @return the started Timer.
     */
    public static Timer initAndStart(Runnable runnable, String name, long delayMs) {
        return initAndStart(runnable, IS_DAEMON, name, delayMs);
    }

    /**
     * Initialize and start a new Timer with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method and
     * initialize a TimerTask with {@link #initTask(Runnable, String)} method.
     * <p>
     * Timer thread and timer task thread can be differentiate because first
     * one terminate with <code>Timer</code> string and the other one with
     * <code>Task</code> string.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param isDaemon true if the associated thread should run as a daemon
     * @param name     creating timer and timer task threads generic name.
     * @param delayMs  delay in milliseconds before task is to be executed.
     * @return the started Timer.
     */
    public static Timer initAndStart(Runnable runnable, boolean isDaemon, String name, long delayMs) {
        return initAndStart(runnable, isDaemon, name, delayMs, PERIOD);
    }

    /**
     * Initialize and start a new Timer with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method and
     * initialize a TimerTask with {@link #initTask(Runnable, String)} method.
     * <p>
     * Timer thread and timer task thread can be differentiate because first
     * one terminate with <code>Timer</code> string and the other one with
     * <code>Task</code> string.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param name     creating timer and timer task threads generic name.
     * @param delayMs  delay in milliseconds before task is to be executed.
     * @param periodMs time in milliseconds between successive task executions.
     * @return the started Timer.
     */
    public static Timer initAndStart(Runnable runnable, String name, long delayMs, long periodMs) {
        return initAndStart(runnable, IS_DAEMON, name, delayMs, periodMs);
    }

    /**
     * Initialize and start a new Timer with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method and
     * initialize a TimerTask with {@link #initTask(Runnable, String)} method.
     * <p>
     * Timer thread and timer task thread can be differentiate because first
     * one terminate with <code>Timer</code> string and the other one with
     * <code>Task</code> string.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param isDaemon true if the associated thread should run as a daemon
     * @param name     creating timer and timer task threads generic name.
     * @param delayMs  delay in milliseconds before task is to be executed.
     * @param periodMs time in milliseconds between successive task executions.
     * @return the started Timer.
     */
    public static Timer initAndStart(Runnable runnable, boolean isDaemon, String name, long delayMs, long periodMs) {
        return initAndStart(runnable, isDaemon, name, null, delayMs, periodMs);
    }

    /**
     * Initialize and start a new Timer with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method and
     * initialize a TimerTask with {@link #initTask(Runnable, String)} method.
     * <p>
     * Timer thread and timer task thread can be differentiate because first
     * one terminate with <code>Timer</code> string and the other one with
     * <code>Task</code> string.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param isDaemon true if the associated thread should run as a daemon
     * @param name     creating timer and timer task threads generic name.
     * @param instance creating timer and timer task threads specific instance
     *                 identifier.
     * @param delayMs  delay in milliseconds before task is to be executed.
     * @return the started Timer.
     */
    public static Timer initAndStart(Runnable runnable, boolean isDaemon, String name, String instance, long delayMs) {
        return initAndStart(runnable, isDaemon, name, instance, delayMs, PERIOD);
    }

    /**
     * Initialize and start a new Timer with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method and
     * initialize a TimerTask with {@link #initTask(Runnable, String)} method.
     * <p>
     * Timer thread and timer task thread can be differentiate because first
     * one terminate with <code>Timer</code> string and the other one with
     * <code>Task</code> string.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param isDaemon true if the associated thread should run as a daemon
     * @param name     creating timer and timer task threads generic name.
     * @param instance creating timer and timer task threads specific instance
     *                 identifier.
     * @param delayMs  delay in milliseconds before task is to be executed.
     * @param periodMs time in milliseconds between successive task executions.
     * @return the started Timer.
     */
    public static Timer initAndStart(Runnable runnable, boolean isDaemon, String name, String instance, long delayMs, long periodMs) {
        Timer timer = initTimer(isDaemon, name, instance);
        TimerTask task = initTask(runnable, name, instance);
        return startTimer(timer, task, delayMs, periodMs);
    }

    /**
     * Initialize and start a new Timer with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method and
     * initialize a TimerTask with {@link #initTask(Runnable, String)} method.
     * <p>
     * Timer thread and timer task thread can be differentiate because first
     * one terminate with <code>Timer</code> string and the other one with
     * <code>Task</code> string.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param name     creating timer and timer task threads generic name.
     * @param date     first time at which task is to be executed.
     * @return the started Timer.
     */
    public static Timer initAndStart(Runnable runnable, String name, Date date) {
        return initAndStart(runnable, IS_DAEMON, name, date);
    }

    /**
     * Initialize and start a new Timer with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method and
     * initialize a TimerTask with {@link #initTask(Runnable, String)} method.
     * <p>
     * Timer thread and timer task thread can be differentiate because first
     * one terminate with <code>Timer</code> string and the other one with
     * <code>Task</code> string.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param isDaemon true if the associated thread should run as a daemon
     * @param name     creating timer and timer task threads generic name.
     * @param date     first time at which task is to be executed.
     * @return the started Timer.
     */
    public static Timer initAndStart(Runnable runnable, boolean isDaemon, String name, Date date) {
        return initAndStart(runnable, isDaemon, name, date, PERIOD);
    }

    /**
     * Initialize and start a new Timer with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method and
     * initialize a TimerTask with {@link #initTask(Runnable, String)} method.
     * <p>
     * Timer thread and timer task thread can be differentiate because first
     * one terminate with <code>Timer</code> string and the other one with
     * <code>Task</code> string.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param name     creating timer and timer task threads generic name.
     * @param date     first time at which task is to be executed.
     * @param periodMs time in milliseconds between successive task executions.
     * @return the started Timer.
     */
    public static Timer initAndStart(Runnable runnable, String name, Date date, long periodMs) {
        return initAndStart(runnable, IS_DAEMON, name, date, periodMs);
    }

    /**
     * Initialize and start a new Timer with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method and
     * initialize a TimerTask with {@link #initTask(Runnable, String)} method.
     * <p>
     * Timer thread and timer task thread can be differentiate because first
     * one terminate with <code>Timer</code> string and the other one with
     * <code>Task</code> string.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param isDaemon true if the associated thread should run as a daemon
     * @param name     creating timer and timer task threads generic name.
     * @param date     first time at which task is to be executed.
     * @param periodMs time in milliseconds between successive task executions.
     * @return the started Timer.
     */
    public static Timer initAndStart(Runnable runnable, boolean isDaemon, String name, Date date, long periodMs) {
        return initAndStart(runnable, isDaemon, name, null, date, periodMs);
    }

    /**
     * Initialize and start a new Timer with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method and
     * initialize a TimerTask with {@link #initTask(Runnable, String)} method.
     * <p>
     * Timer thread and timer task thread can be differentiate because first
     * one terminate with <code>Timer</code> string and the other one with
     * <code>Task</code> string.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param isDaemon true if the associated thread should run as a daemon
     * @param name     creating timer and timer task threads generic name.
     * @param instance creating timer and timer task threads specific instance
     *                 identifier.
     * @param date     first time at which task is to be executed.
     * @return the started Timer.
     */
    public static Timer initAndStart(Runnable runnable, boolean isDaemon, String name, String instance, Date date) {
        return initAndStart(runnable, isDaemon, name, instance, date, PERIOD);
    }

    /**
     * Initialize and start a new Timer with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method and
     * initialize a TimerTask with {@link #initTask(Runnable, String)} method.
     * <p>
     * Timer thread and timer task thread can be differentiate because first
     * one terminate with <code>Timer</code> string and the other one with
     * <code>Task</code> string.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param isDaemon true if the associated thread should run as a daemon
     * @param name     creating timer and timer task threads generic name.
     * @param instance creating timer and timer task threads specific instance
     *                 identifier.
     * @param date     first time at which task is to be executed.
     * @param periodMs time in milliseconds between successive task executions.
     * @return the started Timer.
     */
    public static Timer initAndStart(Runnable runnable, boolean isDaemon, String name, String instance, Date date, long periodMs) {
        Timer timer = initTimer(isDaemon, name, instance);
        TimerTask task = initTask(runnable, name, instance);
        return startTimer(timer, task, date, periodMs);
    }


    // Init Timer

    /**
     * Initialize a Timer with given params.
     * <p>
     * This method set also the timer's thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method.
     *
     * @param name creating timer associated thread generic name.
     * @return the initialized Timer.
     */
    public static Timer initTimer(String name) {
        return initTimer(IS_DAEMON, name);
    }

    /**
     * Initialize a Timer with given params.
     * <p>
     * This method set also the timer's thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method.
     *
     * @param isDaemon true if the associated thread should run as a daemon.
     * @param name     creating timer associated thread generic name.
     * @return the initialized Timer.
     */
    public static Timer initTimer(boolean isDaemon, String name) {
        return initTimer(isDaemon, name, null);
    }

    /**
     * Initialize a Timer with given params.
     * <p>
     * This method set also the timer's thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method.
     *
     * @param isDaemon true if the associated thread should run as a daemon.
     * @param name     creating timer associated thread generic name.
     * @param instance creating timer associated thread specific instance
     *                 identifier.
     * @return the initialized Timer.
     */
    public static Timer initTimer(boolean isDaemon, String name, String instance) {
        String timerName = JavaThreads.prepareThreadName(name + "Timer", instance);
        return new Timer(timerName, isDaemon);
    }


    // Init Task

    /**
     * Initialize a TimerTask with given {@link Runnable} instance.
     * <p>
     * This method set also the timer's thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param name     creating timer task associated thread generic name.
     * @return the initialized TimerTask.
     */
    public static TimerTask initTask(Runnable runnable, String name) {
        return initTask(runnable, name, null);
    }

    /**
     * Initialize a TimerTask with given {@link Runnable} instance.
     * <p>
     * This method set also the timer's thread name using
     * {@link JavaThreads#prepareThreadName(String, String)} method.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param name     creating timer task associated thread generic name.
     * @param instance creating timer task associated thread specific instance identifier.
     * @return the initialized TimerTask.
     */
    public static TimerTask initTask(Runnable runnable, String name, String instance) {
        String thName = JavaThreads.prepareThreadName(name + "_TIMER", instance);
        return new TimerTask() {

            @Override
            public void run() {
                Thread.currentThread().setName(thName);

                runnable.run();
            }

        };
    }


    // Start Timer

    /**
     * Start given <code>timer</code>.
     *
     * @param timer the timer to start.
     * @param task  task to be scheduled.
     * @param date  first time at which task is to be executed.
     * @return the timer started.
     */
    public static Timer startTimer(Timer timer, TimerTask task, Date date) {
        return startTimer(timer, task, date, 0);
    }

    /**
     * Start given <code>timer</code>.
     *
     * @param timer    the timer to start.
     * @param task     task to be scheduled.
     * @param date     first time at which task is to be executed.
     * @param periodMs time in milliseconds between successive task executions.
     * @return the timer started.
     */
    public static Timer startTimer(Timer timer, TimerTask task, Date date, long periodMs) {
        if (periodMs <= 0)
            timer.schedule(task, date);
        else
            timer.schedule(task, date, periodMs);
        return timer;
    }

    /**
     * Start given <code>timer</code>.
     *
     * @param timer   the timer to start.
     * @param task    task to be scheduled.
     * @param delayMs delay in milliseconds before task is to be executed.
     * @return the timer started.
     */
    public static Timer startTimer(Timer timer, TimerTask task, long delayMs) {
        return startTimer(timer, task, delayMs, 0);
    }

    /**
     * Start given <code>timer</code>.
     *
     * @param timer    the timer to start.
     * @param task     task to be scheduled.
     * @param delayMs  delay in milliseconds before task is to be executed.
     * @param periodMs time in milliseconds between successive task executions.
     * @return the timer started.
     */
    public static Timer startTimer(Timer timer, TimerTask task, long delayMs, long periodMs) {
        if (periodMs <= 0)
            timer.schedule(task, delayMs);
        else
            timer.schedule(task, delayMs, periodMs);
        return timer;
    }


    // Stop timer

    /**
     * Cancel next execution of given timer.
     * <p>
     * This methods doesn't terminate timer's running execution if any.
     *
     * @param timer the timer to stop.
     */
    public static void stopTimer(Timer timer) {
        timer.cancel();
    }

}
