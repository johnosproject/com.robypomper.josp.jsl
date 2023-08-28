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
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODBooleanState;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeState;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Default implementation of {@link JODContainer} interface.
 * <p>
 * In addition to the method defined by the interface, this class manage the
 * reference to the JOD Executor Mngr system. This reference is used by container
 * components to initialize state and action components. Because the state
 * components require this reference to propagate the status updates to the
 * JOD Communication system.
 * <p>
 * Moreover, this implementation provide a set of method to create components.
 * This methods helps {@link JODContainer} and {@link JODRoot} sub classes to
 * implement custom initialization procedures (p.e.: for different source formats
 * like json, xml...; actually the json implementation is provided by the
 * {@link JODRoot_Jackson} class).
 */
public class AbsJODContainer extends AbsJODComponent
        implements JODContainer {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private Map<String, JODComponent> components = null;
    private JODExecutorMngr executorMngr = null;


    // Constructor

    /**
     * Protected constructor used by {@link JODRoot} component and others
     * {@link JODContainer} sub classes.
     * <p>
     * When this constructor is used, the sub class must call also the
     * {@link #setComponents(Map)} method.
     *
     * @param structure the JOD Structure system.
     * @param execMngr  the JOD Executor Mngr system.
     * @param name      the name of the component.
     * @param descr     the description of the component.
     */
    protected AbsJODContainer(JODStructure structure, JODExecutorMngr execMngr, JODHistory history, String name, String descr) {
        super(structure, history, name, descr);
        this.executorMngr = execMngr;
    }


    /**
     * Default constructor that initialize the container with his properties and
     * the children components list.
     *
     * @param structure the JOD Structure system.
     * @param name      the name of the component.
     * @param descr     the description of the component.
     */
    private AbsJODContainer(JODStructure structure, JODHistory history, String name, String descr) {
        super(structure, history, name, descr);
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return StructureDefinitions.TYPE_CONTAINER;
    }


    // Sub components

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<JODComponent> getComponents() {
        return components != null ? components.values() : Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODComponent getComponentByName(String name) {
        return components != null ? components.get(name) : null;
    }


    // Implementation methods

    /**
     * Set sub components list.
     *
     * <b>This method can be called only one time.</b> During their initialization,
     * {@link JODContainer} and his sub classes, call this method. Then for each
     * sub component in the list, the {@link AbsJODComponent#setParent(JODContainer)}
     * method is called.
     * <p>
     * Protected method used by {@link AbsJODContainer} sub classes in conjunction
     * with the protected {@link AbsJODContainer#AbsJODContainer(JODStructure, JODExecutorMngr, JODHistory, String, String)}
     * constructor.
     */
    protected void setComponents(Map<String, JODComponent> subComps) throws JODStructure.ComponentInitException {
        if (this.components != null)
            throw new JODStructure.ComponentInitException(String.format("Component '%s', can't set twice 'contains' property.", getName()));

        this.components = subComps != null ? subComps : new HashMap<>();

        for (JODComponent comp : components.values()) {
            assert comp instanceof AbsJODComponent;
            ((AbsJODComponent) comp).setParent(this);
        }
    }

    /**
     * @return the JOD Executor Mngr system.
     */
    protected JODExecutorMngr getExecutorMngr() {
        return executorMngr;
    }


    // Component initialization

    /**
     * Create a component of <code>compType</code> type and the other properties.
     *
     * @param compType     the type of the component to create.
     * @param compName     the name of the component to create.
     * @param compSettings the key-value pairs of the component properties.
     * @return the created component.
     */
    protected JODComponent createComponent(String parentCompName, String compName, String compType, Map<String, Object> compSettings) throws JODStructure.ParsingException {
        if (compType == null || compType.isEmpty())
            throw new JODStructure.ParsingException(String.format("Missing 'compType' for '%s' component.", compName));

        if (StructureDefinitions.TYPE_JOD_CONTAINER.compareToIgnoreCase(compType) == 0)
            return createContainer(parentCompName, compName, compSettings);

        if (StructureDefinitions.TYPE_BOOL_STATE.compareToIgnoreCase(compType) == 0
                || StructureDefinitions.TYPE_BOOL_ACTION.compareToIgnoreCase(compType) == 0)
            return createBool(parentCompName, compName, compType, compSettings);

        if (StructureDefinitions.TYPE_RANGE_STATE.compareToIgnoreCase(compType) == 0
                || StructureDefinitions.TYPE_RANGE_ACTION.compareToIgnoreCase(compType) == 0)
            return createRange(parentCompName, compName, compType, compSettings);

        throw new JODStructure.ParsingUnknownTypeException(parentCompName, compType, compName);
    }

    /**
     * Create a state component.
     *
     * @param compName     the name of the component to create.
     * @param compSettings the key-value pairs of the component properties.
     * @return the created state component.
     */
    protected JODComponent createBool(String parentCompName, String compName, String compType, Map<String, Object> compSettings) throws JODStructure.ParsingException {
        String descr = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_DESCR);
        String listener = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_LISTNER);
        String puller = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_PULLER);

        try {
            if (StructureDefinitions.TYPE_BOOL_STATE.compareToIgnoreCase(compType) == 0)
                return new JODBooleanState(getStructure(), getExecutorMngr(), getHistory(), compName, descr, listener, puller);

            if (StructureDefinitions.TYPE_BOOL_ACTION.compareToIgnoreCase(compType) == 0) {
                String executor = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_EXECUTOR);
                return new JODBooleanAction(getStructure(), getExecutorMngr(), getHistory(), compName, descr, listener, puller, executor);
            }

        } catch (JODStructure.ComponentInitException e) {
            log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error creating state component '%s' for parent container '%s' because %s", compName, parentCompName, e.getMessage()), e);
            throw new JODStructure.InstantiationParsedDataException(compType, compName, listener, puller, e);
        }

        throw new JODStructure.ParsingUnknownTypeException(parentCompName, compName, compType);
    }

    /**
     * Create an action component.
     *
     * @param compName     the name of the component to create.
     * @param compSettings the key-value pairs of the component properties.
     * @return the created state component.
     */
    protected JODComponent createRange(String parentCompName, String compName, String compType, Map<String, Object> compSettings) throws JODStructure.InstantiationParsedDataException, JODStructure.ParsingUnknownTypeException {
        String descr = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_DESCR);
        String listener = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_LISTNER);
        String puller = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_PULLER);

        try {

            if (StructureDefinitions.TYPE_RANGE_STATE.compareToIgnoreCase(compType) == 0) {
                Double min, max, step;
                try {
                    min = Double.parseDouble((String) compSettings.get(StructureDefinitions.PROP_COMPONENT_RANGE_MIN));
                } catch (Throwable e) {
                    if (!(e instanceof NullPointerException))
                        log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error parsing param 'min' of range state component '%s' for parent container '%s' because %s, default value will used", compName, parentCompName, e.getMessage()), e);
                    min = null;
                }
                try {
                    max = Double.parseDouble((String) compSettings.get(StructureDefinitions.PROP_COMPONENT_RANGE_MAX));
                } catch (Throwable e) {
                    if (!(e instanceof NullPointerException))
                        log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error parsing param 'max' of range state component '%s' for parent container '%s' because %s, default value will used", compName, parentCompName, e.getMessage()), e);
                    max = null;
                }
                try {
                    step = Double.parseDouble((String) compSettings.get(StructureDefinitions.PROP_COMPONENT_RANGE_STEP));
                } catch (Throwable e) {
                    if (!(e instanceof NullPointerException))
                        log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error parsing param 'step' of range state component '%s' for parent container '%s' because %s, default value will used", compName, parentCompName, e.getMessage()), e);
                    step = null;
                }
                return new JODRangeState(getStructure(), getExecutorMngr(), getHistory(), compName, descr, listener, puller, min, max, step);
            }

            if (StructureDefinitions.TYPE_RANGE_ACTION.compareToIgnoreCase(compType) == 0) {
                String executor = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_EXECUTOR);
                Double min, max, step;
                try {
                    min = Double.parseDouble((String) compSettings.get(StructureDefinitions.PROP_COMPONENT_RANGE_MIN));
                } catch (Throwable e) {
                    if (!(e instanceof NullPointerException))
                        log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error parsing param 'min' of range state component '%s' for parent container '%s' because %s, default value will used", compName, parentCompName, e.getMessage()), e);
                    min = null;
                }
                try {
                    max = Double.parseDouble((String) compSettings.get(StructureDefinitions.PROP_COMPONENT_RANGE_MAX));
                } catch (Throwable e) {
                    if (!(e instanceof NullPointerException))
                        log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error parsing param 'max' of range state component '%s' for parent container '%s' because %s, default value will used", compName, parentCompName, e.getMessage()), e);
                    max = null;
                }
                try {
                    step = Double.parseDouble((String) compSettings.get(StructureDefinitions.PROP_COMPONENT_RANGE_STEP));
                } catch (Throwable e) {
                    if (!(e instanceof NullPointerException))
                        log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error parsing param 'step' of range state component '%s' for parent container '%s' because %s, default value will used", compName, parentCompName, e.getMessage()), e);
                    step = null;
                }
                return new JODRangeAction(getStructure(), getExecutorMngr(), getHistory(), compName, descr, listener, puller, executor, min, max, step);
            }

        } catch (JODStructure.ComponentInitException e) {
            log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error creating state component '%s' for parent container '%s' because %s", compName, parentCompName, e.getMessage()), e);
            throw new JODStructure.InstantiationParsedDataException(compType, compName, listener, puller, e);
        }

        throw new JODStructure.ParsingUnknownTypeException(parentCompName, compName, compType);
    }

    /**
     * Create a container component.
     *
     * @param compName     the name of the component to create.
     * @param compSettings the key-value pairs of the component properties.
     * @return the created state component.
     */
    protected JODContainer createContainer(String parentCompName, String compName, Map<String, Object> compSettings) throws JODStructure.ParsingException {
        String descr = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_DESCR);

        log.debug(Mrk_JOD.JOD_STRU_SUB, String.format("Creating container component '%s' for parent container '%s'", compName, parentCompName));


        AbsJODContainer cont = new AbsJODContainer(getStructure(), getHistory(), compName, descr);

        log.trace(Mrk_JOD.JOD_STRU_SUB, String.format("Create and set container '%s''s sub components", compName));
        @SuppressWarnings("unchecked")
        Map<String, Object> contains = (Map<String, Object>) compSettings.get("contains");
        Map<String, JODComponent> subComps = createFromContains(compName, contains);
        try {
            cont.setComponents(subComps);
        } catch (JODStructure.ComponentInitException ignore) {
            // container's components not set during container initialization
        }

        log.debug(Mrk_JOD.JOD_STRU_SUB, String.format("Container component '%s' created for parent container '%s'", compName, parentCompName));
        return cont;
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
     * @param subComps the map containing the pairs name/comp's properties.
     * @return a map containing the pairs name/component.
     */
    protected Map<String, JODComponent> createFromContains(String compName, Map<String, Object> subComps) throws JODStructure.ParsingException {
        log.trace(Mrk_JOD.JOD_STRU_SUB, String.format("Creating sub components '%s' for container '%s'", subComps.keySet(), compName));

        Map<String, JODComponent> components = new HashMap<>();
        for (Map.Entry<String, Object> compJson : subComps.entrySet()) {
            String subCompName = compJson.getKey();
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> compSettings = (Map<String, Object>) compJson.getValue();
                String subCompType = (String) compSettings.get(StructureDefinitions.PROP_COMPONENT_TYPE);
                JODComponent compInstance = createComponent(compName, subCompName, subCompType, compSettings);
                components.put(subCompName, compInstance);

            } catch (Exception e) {
                throw new JODStructure.ParsingException(String.format("---malformed component %s---", subCompName), e);
            }
        }

        return components;
    }

}
