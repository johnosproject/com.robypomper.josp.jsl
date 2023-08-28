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


/**
 * Generic exception to throw when a not implemented method is called.
 */
public class JavaNotImplementedException extends RuntimeException {

    // Class constants

    private static final String MSG = "Method '%s::%s' not implemented.";


    // Internal vars

    private final StackTraceElement notImplementedMethod;


    // Constructor

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public JavaNotImplementedException() {
        super(String.format(MSG, (Object[]) getCallerClassAndMethod()));
        notImplementedMethod = getCallerStackElement();
    }


    // Getters

    /**
     * @return the {@link StackTraceElement} corresponding to not implemented
     * method that throw this exception.
     */
    public StackTraceElement getNotImplementedMethod() {
        return notImplementedMethod;
    }


    // Utils methods

    private static StackTraceElement getCallerStackElement() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        int callerIdx = 0;
        while (stack[callerIdx].getMethodName().equalsIgnoreCase("getStackTrace")
                || stack[callerIdx].getMethodName().equalsIgnoreCase("getCallerStackElement")
                || stack[callerIdx].getMethodName().equalsIgnoreCase("getCallerClassAndMethod")
                || stack[callerIdx].getMethodName().equalsIgnoreCase("<init>"))
            callerIdx++;

        return stack[callerIdx];
    }

    private static String[] getCallerClassAndMethod() {
        StackTraceElement stackElement = getCallerStackElement();

        String[] ret = new String[2];
        ret[0] = stackElement.getClassName();
        ret[1] = stackElement.getMethodName();
        return ret;
    }

}
