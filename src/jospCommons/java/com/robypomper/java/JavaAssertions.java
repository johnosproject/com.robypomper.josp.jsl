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


/**
 * Utils class to manage and print assertion errors.
 * <p>
 * Assertion errors are intended as error that should not occurs on production
 * execution, but can happen on development or testing process.
 * <p>
 * All errors printed by this class are printed with detailed error info and
 * enclosed in START/END lines to better identify error printed lines.
 * <p>
 * Such as classic <code>assert</code> statement, this assertions can be
 * enabled/disabled with the <code>-enableassertions</code> switch as JVM
 * param.
 */
public class JavaAssertions {

    // Internal vars

    private static final boolean isAssertionDisabled = checkAssertionDisabled();

    private static boolean checkAssertionDisabled() {
        try {
            assert false;
            return true;

        } catch (AssertionError ignore) {
            return false;
        }
    }


    // Assertion utils

    /**
     * @return true if assertions are disabled.
     */
    public static boolean isAssertionDisabled() {
        return isAssertionDisabled;
    }


    // Failed assertions

    /**
     * Make and print assertion error.
     *
     * @param e         the exception related with the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeAssertion_Failed(Throwable e, T returnVal) {
        makeAssertion_Failed(e);
        return returnVal;
    }

    /**
     * Make and print assertion error.
     *
     * @param msg       a message to describe the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeAssertion_Failed(String msg, T returnVal) {
        makeAssertion_Failed(msg);
        return returnVal;
    }

    /**
     * Make and print assertion error.
     *
     * @param e         the exception related with the assertion error.
     * @param msg       a message to describe the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeAssertion_Failed(Throwable e, String msg, T returnVal) {
        makeAssertion_Failed(e, msg);
        return returnVal;
    }

    /**
     * Make and print assertion error.
     *
     * @param e the exception related with the assertion error.
     */
    public static void makeAssertion_Failed(Throwable e) {
        makeAssertion_Failed(e, null);
    }

    /**
     * Make and print assertion error.
     *
     * @param msg a message to describe the assertion error.
     */
    public static void makeAssertion_Failed(String msg) {
        makeAssertion_Failed((Throwable) null, msg);
    }

    /**
     * Make and print assertion error.
     *
     * @param e   the exception related with the assertion error.
     * @param msg a message to describe the assertion error.
     */
    public static void makeAssertion_Failed(Throwable e, String msg) {
        makeAssertion(false, e, msg);
    }


    // Assertion makers

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param e         the exception related with the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeAssertion(boolean condition, Throwable e, T returnVal) {
        makeAssertion(condition, e);
        return returnVal;
    }

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param msg       a message to describe the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeAssertion(boolean condition, String msg, T returnVal) {
        makeAssertion(condition, msg);
        return returnVal;
    }

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param e         the exception related with the assertion error.
     * @param msg       a message to describe the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeAssertion(boolean condition, Throwable e, String msg, T returnVal) {
        makeAssertion(condition, e, msg);
        return returnVal;
    }

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param e         the exception related with the assertion error.
     */
    public static void makeAssertion(boolean condition, Throwable e) {
        makeAssertion(condition, e, null);
    }

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param msg       a message to describe the assertion error.
     */
    public static void makeAssertion(boolean condition, String msg) {
        makeAssertion(condition, (Throwable) null, msg);
    }

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param e         the exception related with the assertion error.
     * @param msg       a message to describe the assertion error.
     */
    public static void makeAssertion(boolean condition, Throwable e, String msg) {
        if (condition || isAssertionDisabled()) return;

        print(e, msg, true);
    }


    // Failed Warning assertions

    /**
     * Make and print assertion error.
     *
     * @param e         the exception related with the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeWarning_Failed(Throwable e, T returnVal) {
        makeWarning_Failed(e);
        return returnVal;
    }

    /**
     * Make and print assertion error.
     *
     * @param msg       a message to describe the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeWarning_Failed(String msg, T returnVal) {
        makeWarning_Failed(msg);
        return returnVal;
    }

    /**
     * Make and print assertion error.
     *
     * @param e         the exception related with the assertion error.
     * @param msg       a message to describe the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeWarning_Failed(Throwable e, String msg, T returnVal) {
        makeWarning_Failed(e, msg);
        return returnVal;
    }

    /**
     * Make and print assertion error.
     *
     * @param e the exception related with the assertion error.
     */
    public static void makeWarning_Failed(Throwable e) {
        makeWarning_Failed(e, null);
    }

    /**
     * Make and print assertion error.
     *
     * @param msg a message to describe the assertion error.
     */
    public static void makeWarning_Failed(String msg) {
        makeWarning_Failed((Throwable) null, msg);
    }

    /**
     * Make and print assertion error.
     *
     * @param e   the exception related with the assertion error.
     * @param msg a message to describe the assertion error.
     */
    public static void makeWarning_Failed(Throwable e, String msg) {
        makeWarning(false, e, msg);
    }


    // Assertion Warning makers

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param e         the exception related with the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeWarning(boolean condition, Throwable e, T returnVal) {
        makeWarning(condition, e);
        return returnVal;
    }

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param msg       a message to describe the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeWarning(boolean condition, String msg, T returnVal) {
        makeWarning(condition, msg);
        return returnVal;
    }

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param e         the exception related with the assertion error.
     * @param msg       a message to describe the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeWarning(boolean condition, Throwable e, String msg, T returnVal) {
        makeWarning(condition, e, msg);
        return returnVal;
    }

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param e         the exception related with the assertion error.
     */
    public static void makeWarning(boolean condition, Throwable e) {
        makeWarning(condition, e, null);
    }

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param msg       a message to describe the assertion error.
     */
    public static void makeWarning(boolean condition, String msg) {
        makeWarning(condition, (Throwable) null, msg);
    }

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param e         the exception related with the assertion error.
     * @param msg       a message to describe the assertion error.
     */
    public static void makeWarning(boolean condition, Throwable e, String msg) {
        if (condition || isAssertionDisabled()) return;

        print(e, msg + " Skipp error!", false);
    }


    // Printers

    private static void print(Throwable extraThrowable, String msg, boolean blocking) {
        String level = blocking ? "ERROR" : "WARNING";

        String s = "";
        s += String.format("ASSERTION %s (start)\n", level);
        if (msg != null)
            s += String.format("- Message:  %s\n", msg);
        s += String.format("- Date:     %s\n", new Date());
        s += String.format("- Thread:   %s\n", Thread.currentThread().getName());
        if (extraThrowable != null) {
            s += "- ExtraEx: ";
            s += JavaThreads.stackTraceToString(extraThrowable)
                    .replace("\n", "\n            ")
                    .replace("Caused by:", "Origin at:")
                    .trim() + "\n";
        }
        s += String.format("ASSERTION %s (end)", level);
        System.err.println(s);
        System.err.flush();

        if (blocking)
            throw new AssertionError(msg, extraThrowable);
    }

    public static void main(String[] s) {
        System.out.println("Start...");
        System.out.println("Assertion disabled = " + isAssertionDisabled());

        //assert false : "Assertion normale";

        print(null,"message",true);

        print(new IllegalArgumentException("xxx"),"message",false);
        System.out.println("End");
    }

}
