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

import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.history.JODHistory;


/**
 * Basic root component implementation.
 * <p>
 * This class implement the {@link JODRoot} interface and is used as a base class
 * for root parser classes. That means the {@link AbsJODRoot} sub classes are
 * used to initialize object's structures. This technique allow to initialize
 * an object structure from a stored structure definition (like text file).
 * moreover different sub classes can support different sources and formats (json,
 * xml...).
 * <p>
 * The double constructor and the setters methods give more flexibility on root
 * initialization to his sub classes.
 */
public class AbsJODRoot extends AbsJODContainer
        implements JODRoot {

    // Internal vars

    private String model;
    private String brand;
    private String descr_long;


    // Constructor

    /**
     * Protected constructor, that delegate the sub classes to set the root
     * component properties.
     *
     * @param structure the JOD Structure system.
     * @param execMngr  the JOD Executor Mngr system.
     */
    protected AbsJODRoot(JODStructure structure, JODExecutorMngr execMngr, JODHistory history) {
        super(structure, execMngr, history, StructureDefinitions.ROOT_NAME, StructureDefinitions.ROOT_DESCR);
    }

    /**
     * Default constructor that set all root component properties.
     *
     * @param structure the JOD Structure system.
     * @param execMngr  the JOD Executor Mngr system.
     * @param model     the object's model.
     * @param brand     the object's brand.
     * @param descrLong the object's long description.
     */
    public AbsJODRoot(JODStructure structure, JODExecutorMngr execMngr, JODHistory history, String model, String brand, String descrLong) {
        this(structure, execMngr, history);
        setModel(model);
        setBrand(brand);
        setDescr_long(descrLong);
    }


    // Root's properties

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModel() {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBrand() {
        return brand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescr_long() {
        return descr_long;
    }


    // Implementation methods

    /**
     * Set object's model.
     *
     * @param model the object's model.
     */
    protected void setModel(String model) {
        this.model = model;
    }

    /**
     * Set object's brand.
     *
     * @param brand the object's brand.
     */
    protected void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Set object's long description.
     *
     * @param descrLong the object's long description.
     */
    protected void setDescr_long(String descrLong) {
        this.descr_long = descrLong;
    }

}
