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

package com.robypomper.josp.jod.structure;


/**
 * Base root component representation.
 * <p>
 * The root component is a special container that add specific properties related
 * to the object they belong to.
 */
public interface JODRoot extends JODContainer {

    // Root's properties

    /**
     * Object's model is defined by object maker.
     *
     * @return the object's model.
     */
    String getModel();

    /**
     * Object's maker name.
     *
     * @return the object's brand.
     */
    String getBrand();

    /**
     * Object's 2nd description.
     * <p>
     * This description help end-user to understand what the object is and what
     * it can do for them.
     *
     * @return the object's long description.
     */
    String getDescr_long();

}
