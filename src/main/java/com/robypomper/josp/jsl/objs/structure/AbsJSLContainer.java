/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2024 Roberto Pompermaier
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
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


/**
 * Default implementation of {@link JSLContainer} interface.
 * <p>
 * Moreover, this implementation provide a set of method to create components.
 * This methods helps {@link JSLContainer} and {@link JSLRoot} sub classes to
 * implement custom initialization procedures (p.e.: for different source formats
 * like json, xml...; actually the json implementation is provided by the
 * {@link JSLRoot_Jackson} class).
 */
public class AbsJSLContainer extends AbsJSLComponent
        implements JSLContainer {

    // Internal vars

    private Collection<JSLComponent> components = null;


    // Constructor

    /**
     * Protected constructor used by {@link JSLRoot} component and others
     * {@link JSLContainer} sub classes.
     * <p>
     * When this constructor is used, the sub class must call also the
     * {@link #setComponents(Collection)} method.
     *
     * @param remoteObject the {@link JSLRemoteObject} representing JOD object.
     * @param name         the name of the component.
     * @param descr        the description of the component.
     */
    protected AbsJSLContainer(JSLRemoteObject remoteObject, String name, String descr, String type) {
        super(remoteObject, name, descr, type);
    }


    /**
     * Default constructor that initialize the container with his properties and
     * the children components list.
     *
     * @param remoteObject the {@link JSLRemoteObject} representing JOD object.
     * @param name         the name of the component.
     * @param descr        the description of the component.
     * @param subComps     the list of container's sub components.
     */
    public AbsJSLContainer(JSLRemoteObject remoteObject, String name, String descr, String type, Collection<JSLComponent> subComps) {
        super(remoteObject, name, descr, type);

        try {
            setComponents(subComps);
        } catch (JSLRemoteObject.ComponentInitException e) {
            assert false;   // This is a implementation error
        }
    }


    // Sub components

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<JSLComponent> getComponents() {
        return components;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLComponent getComponentByName(String name) {
        for (JSLComponent comp : components)
            if (name.equals(comp.getName()))
                return comp;
        return null;
    }


    // Implementation methods

    /**
     * Set sub components list.
     *
     * <b>This method can be called only one time.</b> During their initialization,
     * {@link JSLContainer} and his sub classes, call this method. Then for each
     * sub component in the list, the {@link AbsJSLComponent#setParent(JSLContainer)}
     * method is called.
     * <p>
     * Protected method used by {@link AbsJSLContainer} sub classes in conjunction
     * with the protected {@link AbsJSLContainer#AbsJSLContainer(JSLRemoteObject, String, String, String)}
     * constructor.
     */
    protected void setComponents(Collection<JSLComponent> subComps) throws JSLRemoteObject.ComponentInitException {
        if (this.components != null)
            throw new JSLRemoteObject.ComponentInitException(getRemoteObject(), String.format("Component '%s', can't set twice 'contains' property.", getName()));

        this.components = subComps;

        for (JSLComponent comp : components) {
            assert comp instanceof AbsJSLComponent;
            ((AbsJSLComponent) comp).setParent(this);
        }
    }


    // Component initialization

    /**
     * Create a component of <code>compType</code> type and the other properties.
     *
     * @param compSettings the key-value pairs of the component properties, this
     *                     map must contain also the component's name and his type.
     * @return the created component.
     */
    protected JSLComponent createComponent(Map<String, Object> compSettings) throws JSLRemoteObject.ParsingException {
        String compName = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_NAME);
        String compType = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_TYPE);

        if (StructureDefinitions.TYPE_CONTAINER.compareToIgnoreCase(compType) == 0)
            return createContainer(compName, compType, compSettings);

        if (StructureDefinitions.TYPE_BOOL_STATE.compareToIgnoreCase(compType) == 0
                || StructureDefinitions.TYPE_BOOL_ACTION.compareToIgnoreCase(compType) == 0)
            return createBool(compName, compType, compSettings);

        if (StructureDefinitions.TYPE_RANGE_STATE.compareToIgnoreCase(compType) == 0
                || StructureDefinitions.TYPE_RANGE_ACTION.compareToIgnoreCase(compType) == 0)
            return createRange(compName, compType, compSettings);

        throw new JSLRemoteObject.ParsingUnknownTypeException(getRemoteObject(), compType, compName);
    }

    /**
     * Create a state component.
     *
     * @param compName     the name of the component to create.
     * @param compSettings the key-value pairs of the component properties.
     * @return the created state component.
     */
    protected JSLComponent createBool(String compName, String compType, Map<String, Object> compSettings) throws JSLRemoteObject.ParsingUnknownTypeException {
        String descr = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_DESCR);
        String stateStr = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_STATE);

        if (StructureDefinitions.TYPE_BOOL_STATE.compareToIgnoreCase(compType) == 0) {
            boolean state = Boolean.parseBoolean(stateStr);
            return new JSLBooleanState(getRemoteObject(), compName, descr, compType, state);
        }

        if (StructureDefinitions.TYPE_BOOL_ACTION.compareToIgnoreCase(compType) == 0) {
            boolean state = Boolean.parseBoolean(stateStr);
            return new JSLBooleanAction(getRemoteObject(), compName, descr, compType, state);
        }

        throw new JSLRemoteObject.ParsingUnknownTypeException(getRemoteObject(), compType, compName);
    }

    /**
     * Create an action component.
     *
     * @param compName     the name of the component to create.
     * @param compSettings the key-value pairs of the component properties.
     * @return the created state component.
     */
    protected JSLComponent createRange(String compName, String compType, Map<String, Object> compSettings) throws JSLRemoteObject.ParsingUnknownTypeException {
        String descr = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_DESCR);
        String stateStr = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_STATE);

        if (StructureDefinitions.TYPE_RANGE_STATE.compareToIgnoreCase(compType) == 0) {
            double min = (Double) compSettings.get(StructureDefinitions.PROP_COMPONENT_RANGE_MIN);
            double max = (Double) compSettings.get(StructureDefinitions.PROP_COMPONENT_RANGE_MAX);
            double step = (Double) compSettings.get(StructureDefinitions.PROP_COMPONENT_RANGE_STEP);
            double state = Double.parseDouble(stateStr);
            return new JSLRangeState(getRemoteObject(), compName, descr, compType, min, max, step, state);
        }

        if (StructureDefinitions.TYPE_RANGE_ACTION.compareToIgnoreCase(compType) == 0) {
            double min = (Double) compSettings.get(StructureDefinitions.PROP_COMPONENT_RANGE_MIN);
            double max = (Double) compSettings.get(StructureDefinitions.PROP_COMPONENT_RANGE_MAX);
            double step = (Double) compSettings.get(StructureDefinitions.PROP_COMPONENT_RANGE_STEP);
            double state = Double.parseDouble(stateStr);
            return new JSLRangeAction(getRemoteObject(), compName, descr, compType, min, max, step, state);
        }

        throw new JSLRemoteObject.ParsingUnknownTypeException(getRemoteObject(), compType, compName);
    }

    /**
     * Create a container component.
     *
     * @param compName     the name of the component to create.
     * @param compSettings the key-value pairs of the component properties.
     * @return the created state component.
     */
    protected JSLContainer createContainer(String compName, String compType, Map<String, Object> compSettings) throws JSLRemoteObject.ParsingException {
        String descr = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_DESCR);

        @SuppressWarnings("unchecked")
        Collection<Object> contains = (Collection<Object>) compSettings.get("components");
        Collection<JSLComponent> subComps = createFromContains(contains);

        return new AbsJSLContainer(getRemoteObject(), compName, descr, compType, subComps);
    }

    /**
     * Create all components contained in the <code>subComps</code> map.
     * <p>
     * The given map contains the pairs name/<code>compProps</code> for each
     * component to create. In the <code>compProps</code> there must be present
     * the {@value StructureDefinitions#PROP_COMPONENT_TYPE} property, used to
     * define witch component type to create.
     * <p>
     * The <code>compProps</code> must be a {@link Map} of {@link String}-{@link Object}s.
     *
     * @param comps the map containing the pairs name/comp's properties.
     * @return a map containing the pairs name/component.
     */
    protected Collection<JSLComponent> createFromContains(Collection<Object> comps) throws JSLRemoteObject.ParsingException {
        Collection<JSLComponent> components = new ArrayList<>();
        for (Object compSettingsObj : comps) {
            try {
                //noinspection unchecked
                Map<String, Object> compSettings = (Map<String, Object>) compSettingsObj;
                JSLComponent compInstance = createComponent(compSettings);
                components.add(compInstance);

            } catch (Exception e) {
                throw new JSLRemoteObject.ParsingException(getRemoteObject(), "---malformed component---", e);
            }
        }
        return components;
    }

}
