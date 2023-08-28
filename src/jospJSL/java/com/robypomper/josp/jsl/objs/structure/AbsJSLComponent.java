/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.robypomper.josp.jsl.objs.structure;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;

import java.util.Collection;


/**
 * Default implementation of {@link JSLComponent} interface.
 * <p>
 * In addition to the method defined by the interface, this class manage the
 * reference to the {@link JSLRemoteObject} instance. This reference is used by
 * container components to initialize other components. Also the action components
 * use this reference to propagate the action commands to the JSL Communication
 * system.
 */
public class AbsJSLComponent implements JSLComponent {

    // Internal vars

    private final JSLRemoteObject remoteObject;
    private JSLContainer parent = null;
    private final String name;
    private final String descr;
    private String type;


    // Constructor

    /**
     * Default constructor that initialize component's commons properties.
     * <p>
     * Moreover it store the reference to the {@link JSLRemoteObject}.
     *
     * @param remoteObject the {@link JSLRemoteObject} representing JOD object.
     * @param name         the name of the component.
     * @param descr        the description of the component.
     * @param type         the type of the component.
     */
    public AbsJSLComponent(JSLRemoteObject remoteObject, String name, String descr, String type) {
        this.remoteObject = remoteObject;
        this.name = name;
        this.descr = descr != null ? descr : "";
        this.type = type;
    }


    // Commons properties

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescr() {
        return descr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLContainer getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLComponentPath getPath() {
        if (parent == null)
            return new DefaultJSLComponentPath(StructureDefinitions.PATH_STR_ROOT);
        return parent.getPath().add(getName());
    }


    // Implementation methods

    /**
     * Method called by {@link AbsJSLContainer#setComponents(Collection)} to set
     * his self as parent of the current component.
     *
     * <b>This method can be called only one time.</b> During their initialization,
     * {@link JSLContainer} and his sub classes, call the setComponents(Map)
     * method and then this method.
     *
     * @param parent the container parent of current component.
     */
    public void setParent(JSLContainer parent) throws JSLRemoteObject.ComponentInitException {
        if (this.parent != null)
            throw new JSLRemoteObject.ComponentInitException(remoteObject, String.format("Component '%s', can't set twice 'parent' property", getName()));

        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLRemoteObject getRemoteObject() {
        return remoteObject;
    }

}
