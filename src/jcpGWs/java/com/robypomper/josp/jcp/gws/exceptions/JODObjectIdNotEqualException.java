/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.gws.exceptions;

public class JODObjectIdNotEqualException extends Throwable {

    // Class constants

    private static final String MSG = "Object's id '%s' on message not equal to current Object instance's id '%s'";

    // Internal vars

    private final String msgObjId;
    private final String currentObjId;


    // Constructors

    public JODObjectIdNotEqualException(String msgObjId, String currentObjId) {
        super(String.format(MSG, msgObjId, currentObjId));
        this.msgObjId = msgObjId;
        this.currentObjId = currentObjId;
    }


    // Getters

    public String getMsgObjId() {
        return msgObjId;
    }

    public String getCurrentObjId() {
        return currentObjId;
    }

}
