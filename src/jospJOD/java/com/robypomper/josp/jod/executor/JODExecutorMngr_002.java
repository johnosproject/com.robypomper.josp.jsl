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

package com.robypomper.josp.jod.executor;

import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.executor.factories.AbsFactoryJODWorker;
import com.robypomper.josp.jod.executor.factories.FactoryJODExecutor;
import com.robypomper.josp.jod.executor.factories.FactoryJODListener;
import com.robypomper.josp.jod.executor.factories.FactoryJODPuller;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.executor.JODComponentExecutor;
import com.robypomper.josp.jod.structure.executor.JODComponentListener;
import com.robypomper.josp.jod.structure.executor.JODComponentPuller;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the JOD executor manager (shortly ExM) implementation.
 * <p>
 * It manage JOD Pullers, Listeners and Executors (this group is also called
 * JOD Workers for executor manager). JOD workers support {@link JODComponent}s
 * from the JOD structure system interfacing with firmware and external systems.
 *
 * <h1>Usage from JOD Structure</h1>
 * JOD Components can use the methods provided by ExM to create new JOD workers:
 * <ul>
 *     <li>{@link #initPuller(JODComponentPuller)}</li>
 *     <li>{@link #initListener(JODComponentListener)}</li>
 *     <li>{@link #initExecutor(JODComponentExecutor)}</li>
 * </ul>
 * This method return a JOD Worker object that can be used, by JOD Component to
 * manage the JOD execution manager's worker.
 * <p>
 * JOD Pullers and Listeners helps {@link com.robypomper.josp.jod.structure.JODState}
 * components to monitoring a resource (hardware or software). When a puller
 * detect / a listener receive a status update, they can call the
 * <code>AbsJODWorker#sendUpdate()</code> method.
 * <p>
 * On the other side, the JOD Executor allow {@link com.robypomper.josp.jod.structure.JODAction}
 * execute actions on managed resource (hardware or software). The JODAction can
 * call the {@link JODExecutor#exec()} method of the returned JODExecutor object
 * from the {@link #initExecutor(JODComponentExecutor)} method.
 * <p>
 * ...
 *
 * <h1>Executor manager's Workers implementations</h1>
 * JOD Workers are instances of {@link JODPuller}, {@link JODListener} and
 * {@link JODExecutor} interfaces and can be implemented extending corresponding
 * classes {@link com.robypomper.josp.jod.executor.AbsJODPuller},
 * {@link com.robypomper.josp.jod.executor.AbsJODListener} (or
 * {@link com.robypomper.josp.jod.executor.AbsJODListenerLoop}) and
 * {@link com.robypomper.josp.jod.executor.AbsJODExecutor}.
 * <p>
 * During ExM startup, it register all implementations contained in
 * {@value JODSettings_002#JODPULLER_IMPLS}, {@value JODSettings_002#JODLISTENER_IMPLS}
 * and {@value JODSettings_002#JODEXECUTOR_IMPLS} JOD properties.<br>
 * This properties contains a list of protocol/implementations pairs
 * (<code>{Proto1}://{Class1};{Proto2}://{Class2};{ProtoN}://{ClassN}</code>).
 * During registration each implementation class is associated to a specific, unique
 * protocol.
 * <p>
 * The implementation's protocols will be use from worker's factories
 * ({@link FactoryJODPuller}, {@link FactoryJODListener} and {@link FactoryJODExecutor})
 * to identify right worker's implementation to instantiate. Factories classes
 * will pass, to the implementation constructors, also the configs string
 * containing all properties set from the {@link JODComponent} configuration and
 * required to initialize the specific implementation.
 */
public class JODExecutorMngr_002 implements JODExecutorMngr {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODSettings_002 settings;
    private final JODObjectInfo objInfo;
    private final Map<JODComponent, JODPuller> pullers = new HashMap<>();
    private final Map<JODComponent, JODListener> listeners = new HashMap<>();
    private final Map<JODComponent, JODExecutor> executors = new HashMap<>();


    // Constructor

    /**
     * Create new executor manager.
     * <p>
     * This constructor load Pullers, Listeners and Executor implementations and
     * associate them to a specific, unique protocol name. Protocl/implementation
     * pairs are read from <code>settings</code>.
     *
     * @param settings the JOD settings.
     * @param objInfo  the object's info.
     */
    public JODExecutorMngr_002(JODSettings_002 settings, JODObjectInfo objInfo) {
        this.settings = settings;
        this.objInfo = objInfo;

        loadPullerImpls();
        loadListenerImpls();
        loadExecutorImpls();

        int protocolsCount = FactoryJODListener.instance().getProtocols().size();
        protocolsCount += FactoryJODPuller.instance().getProtocols().size();
        protocolsCount += FactoryJODExecutor.instance().getProtocols().size();
        log.info(Mrk_JOD.JOD_EXEC, String.format("Initialized JODExecutorMngr instance with '%d' protocols", protocolsCount));
        for (Map.Entry<String, Class<? extends AbsJODPuller>> proto : FactoryJODPuller.instance().getProtocols().entrySet())
            log.debug(Mrk_JOD.JOD_EXEC, String.format("                                     Puller   '%s'\t (%s)", proto.getKey(), proto.getValue().getName()));
        for (Map.Entry<String, Class<? extends AbsJODListener>> proto : FactoryJODListener.instance().getProtocols().entrySet())
            log.debug(Mrk_JOD.JOD_EXEC, String.format("                                     Listener '%s'\t (%s)", proto.getKey(), proto.getValue().getName()));
        for (Map.Entry<String, Class<? extends AbsJODExecutor>> proto : FactoryJODExecutor.instance().getProtocols().entrySet())
            log.debug(Mrk_JOD.JOD_EXEC, String.format("                                     Executor '%s'\t (%s)", proto.getKey(), proto.getValue().getName()));
    }


    // Protocols/Implementations loaders

    /**
     * Load JOD Pullers implementations from
     * {@link JODSettings_002#getJODExecutorImpls()}
     * JOD's property.
     */
    private void loadPullerImpls() {
        loadWorkerImpls(settings.getJODPullerImpls(), FactoryJODPuller.instance());
    }

    /**
     * Load JOD Listeners implementations from
     * {@link JODSettings_002#getJODExecutorImpls()}
     * JOD's property.
     */
    private void loadListenerImpls() {
        loadWorkerImpls(settings.getJODListenerImpls(), FactoryJODListener.instance());
    }

    /**
     * Load JOD Executors implementations from
     * {@link JODSettings_002#getJODExecutorImpls()}
     * JOD's property.
     */
    private void loadExecutorImpls() {
        loadWorkerImpls(settings.getJODExecutorImpls(), FactoryJODExecutor.instance());
    }

    /**
     * Split and parse <code>implsString</code> String containing the list of
     * protocol/implementation pairs.
     * <p>
     * For each extracted pair, register it to the given <code>factory</code>
     * object.
     * <p>
     * This method print log warning messages on registration errors and when no
     * implementations are available.
     *
     * @param implsString String containing the proto/implementation pairs.
     * @param factory     the factory where register the proto/implementations pairs.
     */
    private void loadWorkerImpls(String implsString, AbsFactoryJODWorker<? extends AbsJODWorker> factory) {
        String workName = factory.getType();
        log.debug(Mrk_JOD.JOD_EXEC, String.format("Loading %s workers", workName));
        log.trace(Mrk_JOD.JOD_EXEC, String.format("Loading %ss from '%s' config strings ", workName, implsString));

        if (implsString.isEmpty()) {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("No %s worker config strings", workName));
            return;
        }

        String[] implClasses = implsString.split(";|\\s");
        for (String iClass : implClasses) {
            if (iClass.isEmpty()) continue;

            if (!iClass.contains(AbsJODWorker.CONFIG_STR_SEP)) {
                log.warn(Mrk_JOD.JOD_EXEC, String.format("Error loading %s worker because wrong config string '%s', expected format '{proto}://{impl}'", workName, iClass));
                continue;
            }

            String proto;
            String iClassName;
            try {
                proto = AbsJODWorker.extractProto(iClass);
                iClassName = AbsJODWorker.extractConfigsStr(iClass);

            } catch (JODWorker.MalformedConfigsException e) {
                log.warn(Mrk_JOD.JOD_EXEC, String.format("Error instantiating %s worker protocol '%s' because %s", workName, iClass, e.getMessage()), e);
                continue;
            }

            try {
                log.trace(Mrk_JOD.JOD_EXEC, String.format("Instantiating %s worker protocol '%s' with class '%s'", workName, proto, iClassName));
                factory.register(proto, iClassName);

            } catch (JODWorker.FactoryException e) {
                log.warn(Mrk_JOD.JOD_EXEC, String.format("Error instantiating %s worker protocol '%s' with class '%s' because %s", workName, proto, iClassName, e.getMessage()), e);
            }
        }

        log.debug(Mrk_JOD.JOD_EXEC, String.format("%s workers (%d) loaded", workName, factory.getProtocols().size()));
    }


    // JOD Component's interaction methods (from structure)

    /**
     * {@inheritDoc}
     */
    @Override
    public JODPuller initPuller(JODComponentPuller component) throws JODPuller.FactoryException {
        Class<? extends AbsJODPuller> pullerClass = FactoryJODPuller.instance().getProtocols().get(component.getProto());
        if (pullerClass == null)
            throw new JODListener.FactoryException(String.format("Can't init puller because '%s' protocol not registered", component.getProto()));

        log.info(Mrk_JOD.JOD_EXEC, String.format("Load '%s' component's puller with protocol and configs '%s://%s' and class '%s'", component.getName(), component.getProto(), component.getConfigsStr(), pullerClass.getName()));
        JODPuller puller = FactoryJODPuller.instance().create(component);
        pullers.put(component.getComponent(), puller);
        return puller;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODListener initListener(JODComponentListener component) throws JODListener.FactoryException {
        Class<? extends AbsJODListener> listenerClass = FactoryJODListener.instance().getProtocols().get(component.getProto());
        if (listenerClass == null)
            throw new JODListener.FactoryException(String.format("Can't init listener because '%s' protocol not registered", component.getProto()));

        log.info(Mrk_JOD.JOD_EXEC, String.format("Load '%s' component's listener with protocol and configs '%s://%s' and class '%s'", component.getName(), component.getProto(), component.getConfigsStr(), listenerClass.getName()));
        JODListener listener = FactoryJODListener.instance().create(component);
        listeners.put(component.getComponent(), listener);
        return listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODExecutor initExecutor(JODComponentExecutor component) throws JODExecutor.FactoryException {
        Class<? extends AbsJODExecutor> executorClass = FactoryJODExecutor.instance().getProtocols().get(component.getProto());
        if (executorClass == null)
            throw new JODListener.FactoryException(String.format("Can't init executor because '%s' protocol not registered", component.getProto()));

        log.info(Mrk_JOD.JOD_EXEC, String.format("Load '%s' component's executor with protocol and configs '%s://%s' and class '%s'", component.getName(), component.getProto(), component.getConfigsStr(), executorClass.getName()));
        JODExecutor executor = FactoryJODExecutor.instance().create(component);
        executors.put(component.getComponent(), executor);
        return executor;
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<JODPuller> getPullers() {
        return pullers.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<JODListener> getListeners() {
        return listeners.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<JODExecutor> getExecutors() {
        return executors.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateAll() {
        startAllPullers();
        connectAllListeners();
        enableAllExecutors();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateAll() {
        stopAllPullers();
        disconnectAllListeners();
        disableAllExecutors();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAllPullers() {
        log.info(Mrk_JOD.JOD_EXEC, String.format("Start all object '%s' pullers", objInfo.getObjId()));
        for (JODPuller p : getPullers()) {
            p.startTimer();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAllPullers() {
        log.info(Mrk_JOD.JOD_EXEC, String.format("Stop all object '%s' pullers", objInfo.getObjId()));
        for (JODPuller p : getPullers())
            p.stopTimer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectAllListeners() {
        log.info(Mrk_JOD.JOD_EXEC, String.format("Start all object '%s' listeners", objInfo.getObjId()));
        for (JODListener l : getListeners())
            l.listen();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectAllListeners() {
        log.info(Mrk_JOD.JOD_EXEC, String.format("Stop all object '%s' listeners", objInfo.getObjId()));
        for (JODListener l : getListeners())
            l.halt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableAllExecutors() {
        log.info(Mrk_JOD.JOD_EXEC, String.format("Enable all object '%s' executors", objInfo.getObjId()));
        for (JODExecutor e : getExecutors())
            e.enable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disableAllExecutors() {
        log.info(Mrk_JOD.JOD_EXEC, String.format("Disable all object '%s' executors", objInfo.getObjId()));
        for (JODExecutor e : getExecutors())
            e.disable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startPuller(JODComponent comp) {
        JODPuller p = pullers.get(comp);

        if (p != null) {
            log.info(Mrk_JOD.JOD_EXEC, String.format("Start puller '%s' of component '%s'", p.getName(), comp.getName()));
            p.startTimer();
        } else {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("Error on disable puller of '%s' component because no puller found", comp.getName()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopPuller(JODComponent comp) {
        JODPuller p = pullers.get(comp);

        if (p != null) {
            log.info(Mrk_JOD.JOD_EXEC, String.format("Stop puller '%s' of component '%s'", p.getName(), comp.getName()));
            p.stopTimer();
        } else {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("Error on disable puller of '%s' component because no puller found", comp.getName()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectListener(JODComponent comp) {
        JODListener l = listeners.get(comp);

        if (l != null) {
            log.info(Mrk_JOD.JOD_EXEC, String.format("Start listener servers '%s' of component '%s'", l.getName(), comp.getName()));
            l.listen();
        } else {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("Error on disable listener server of '%s' component because no listener found", comp.getName()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectListener(JODComponent comp) {
        JODListener l = listeners.get(comp);
        if (l != null) {
            log.info(Mrk_JOD.JOD_EXEC, String.format("Stop listener servers '%s' of component '%s'", l.getName(), comp.getName()));
            l.halt();
        } else {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("Error on disable listener server of '%s' component because no listener found", comp.getName()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableExecutor(JODComponent comp) {
        JODExecutor e = executors.get(comp);
        if (e != null) {
            log.info(Mrk_JOD.JOD_EXEC, String.format("Enable executor '%s' of component '%s'", e.getName(), comp.getName()));
            e.disable();
        } else {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("Error on disable executor of '%s' component because no executor found", comp.getName()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disableExecutor(JODComponent comp) {
        JODExecutor e = executors.get(comp);

        if (e != null) {
            log.info(Mrk_JOD.JOD_EXEC, String.format("Disable executor '%s' of component '%s'", e.getName(), comp.getName()));
            e.disable();
        } else {
            log.warn(Mrk_JOD.JOD_EXEC, String.format("Error on disable executor of '%s' component because no executor found", comp.getName()));
        }
    }

}
