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

package com.robypomper.josp.states;

import java.util.Arrays;

public class StateException extends Throwable {

    private static final String MSG_1 = "Method '%s' can be executed only on '%s' state(s), but actual state is '%s'";
    private static final String MSG_2 = "Wrong state: %s";
    private static final String MSG_3 = "Wrong state: %s; required '%s' state(s), actual state '%s'";

    public <E> StateException(String method, E expected, E actual) {
        super(String.format(MSG_1, method, expected, actual));
    }

    public <E> StateException(String method, E[] expected, E actual) {
        super(String.format(MSG_1, method, Arrays.toString(expected), actual));
    }

    public <E> StateException(String message) {
        super(String.format(MSG_2, message));
    }

    public <E> StateException(E expected, E actual, String message) {
        super(String.format(MSG_3, message, expected, actual));
    }

    public <E> StateException(E[] expected, E actual, String message) {
        super(String.format(MSG_3, message, Arrays.toString(expected), actual));
    }

}
