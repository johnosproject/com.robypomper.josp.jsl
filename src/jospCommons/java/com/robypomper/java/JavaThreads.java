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

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Class for threads utils.
 */
public class JavaThreads {

    // Init and start Thread

    /**
     * Initialize and start a new Thread with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link #prepareThreadName(String, String)} method.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param name     creating thread generic name.
     * @return the started thread.
     */
    public static Thread initAndStart(Runnable runnable, String name) {
        return initAndStart(runnable, name, null);
    }

    /**
     * Initialize and start a new Thread with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link #prepareThreadName(String, String)} method.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param name     creating thread generic name.
     * @param instance creating thread specific instance identifier.
     * @return the started thread.
     */
    public static Thread initAndStart(Runnable runnable, String name, String instance) {
        Thread t = init(runnable, name, instance);
        return start(t);
    }


    // Init Thread

    /**
     * Initialize a new Thread with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link #prepareThreadName(String, String)} method.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param name     creating thread generic name.
     * @return the created thread.
     */
    public static Thread init(Runnable runnable, String name) {
        return init(runnable, name, null);
    }

    /**
     * Initialize a new Thread with given {@link Runnable} param.
     * <p>
     * This method set also the thread name using
     * {@link #prepareThreadName(String, String)} method.
     *
     * @param runnable the {@link Runnable} implementation.
     * @param name     creating thread generic name.
     * @param instance creating thread specific instance identifier.
     * @return the created thread.
     */
    public static Thread init(Runnable runnable, String name, String instance) {
        Thread t = new Thread(runnable);
        String thName = prepareThreadName(name, instance);
        t.setName(thName);
        return t;
    }


    // Start Thread

    /**
     * Start given thread and return the same thread.
     *
     * @param thread the thread to start.
     * @return the started thread.
     */
    public static Thread start(Thread thread) {
        thread.start();
        return thread;
    }

    static private int threadsCount = 0;

    /**
     * Generate a thread's name.
     * <p>
     * The generated name, contains current thread name plus creating thread
     * info such as specific thread name and instance identifier. That allow to
     * identify each thread by his creator thread, by thread type or by his
     * (optional) instance.
     * <p>
     * The instance identifier should refer to instance object that the thread
     * work for.
     *
     * @param name     creating thread generic name.
     * @param instance creating thread specific instance identifier.
     * @return a new thread's name.
     */
    public static String prepareThreadName(String name, String instance) {
        assert name != null && !name.isEmpty() : "Thread name must be set.";

        String currentThreadName = Thread.currentThread().getName();
        currentThreadName = currentThreadName.substring(currentThreadName.indexOf('.')+1);
        String newThreadName = name + (instance != null && !instance.isEmpty() ? "(" + instance + ")" : "");
        return String.format("%d. %s < %s", threadsCount++, newThreadName, currentThreadName);
    }


    // Stop Thread

    /**
     * Send interrupt signal to given thread.
     *
     * @param thread the thread to stop.
     */
    public static void stop(Thread thread) {
        thread.interrupt();
    }

    /**
     * Stop given thread and wait for his join to current thread.
     *
     * @param thread    the thread to stop.
     * @param timeoutMs thread's join timeout in ms.
     * @return true if given thread joined successfully, false otherwise.
     */
    public static boolean stopAndJoin(Thread thread, long timeoutMs) {
        stop(thread);
        try {
            thread.join(timeoutMs);
        } catch (InterruptedException ignore) {
        }

        return !thread.isAlive();
    }

    // Sleep

    /**
     * Calls the {@link Thread#sleep(long)} method catching
     * {@link InterruptedException} exceptions.
     *
     * @param millis the length of time to sleep in milliseconds
     * @return false if an {@link InterruptedException} was throw, true
     * otherwise.
     */
    public static boolean softSleep(long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }


    // Stack overflow check

    /**
     * Check if current thread is in a stack overflow condition.
     * <p>
     * This method count how many times last called method was called in
     * current stack trace. If it was called more than one time, then this
     * method return true, otherwise it return false.
     * <p>
     * This method get the latest class::method called from current thread
     * stacktrace (skipping {@link Thread#getStackTrace()} and
     * {@link #isInStackOverflow} methods).
     *
     * @return false if checked method was called less than
     * <code>callsLimit</code> times, true otherwise.
     */
    public static boolean isInStackOverflow() {
        return isInStackOverflow(null, null, -1);
    }

    /**
     * Check if current thread is in a stack overflow condition.
     * <p>
     * This method count how many times last called method was called in
     * current stack trace. If it was called more than
     * <code>callsLimit</code> time, then this method return true, otherwise it
     * return false.
     * <p>
     * This method get the latest class::method called from current thread
     * stacktrace (skipping {@link Thread#getStackTrace()} and
     * {@link #isInStackOverflow} methods).
     *
     * @param callsLimit number of allowed calls of checked method.
     * @return false if checked method was called less than
     * <code>callsLimit</code> times, true otherwise.
     */
    public static boolean isInStackOverflow(int callsLimit) {
        return isInStackOverflow(null, null, callsLimit);
    }

    /**
     * Check if current thread is in a stack overflow condition.
     * <p>
     * This method count how many times the <code>clazz::method</code> method
     * was called in current stack trace. If it was called more than
     * <code>callsLimit</code> time, then this method return true, otherwise it
     * return false.
     * <p>
     * If <code>clazz</code> or <code>method</code> are null, this method get
     * the latest class::method called from current thread stacktrace (skipping
     * {@link Thread#getStackTrace()} and {@link #isInStackOverflow} methods).
     *
     * @param clazz      the class of the method to check.
     * @param method     the method to check.
     * @param callsLimit number of allowed calls of checked method.
     * @return false if checked method was called less than
     * <code>callsLimit</code> times, true otherwise.
     */
    public static boolean isInStackOverflow(String clazz, String method, int callsLimit) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        int callerIdx = 0;
        while (stack[callerIdx].getMethodName().equalsIgnoreCase("getStackTrace")
                || stack[callerIdx].getMethodName().equalsIgnoreCase("isInStackOverflow"))
            callerIdx++;

        if (clazz == null)
            clazz = stack[callerIdx].getClassName();
        if (method == null)
            method = stack[callerIdx].getMethodName();
        if (callsLimit < 1)
            callsLimit = 1;

        int callsCount = 0;
        for (StackTraceElement el : stack) {
            if (el.getClassName().equalsIgnoreCase(clazz)
                    && el.getMethodName().equalsIgnoreCase(method)) {
                callsCount++;
                if (callsCount > callsLimit)
                    return true;
            }
        }

        return false;
    }


    // Stacktrace

    /**
     * Convert the <code>t</code> exception stack trace to a String.
     *
     * @param t the excpetion containing the stack trace to convert.
     * @return a formatted String containing the stack trace.
     */
    public static String stackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);

        return sw.toString();
    }

    /**
     * Convert current stack trace to a String.
     *
     * @return a formatted String containing the stack trace.
     */
    public static String currentStackTraceToString() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder s = new StringBuilder();
        s.append(stackTrace[3]);
        for (int i = 4; i < stackTrace.length; i++)
            s.append("\n\tat ").append(stackTrace[i]);
        return s.toString();
    }

}
