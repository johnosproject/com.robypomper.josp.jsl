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

package com.robypomper.josp.jsl;

import com.robypomper.java.JavaAssertions;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.jsl.admin.JSLAdmin;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.states.JSLState;
import com.robypomper.josp.states.StateException;

import java.io.File;
import java.util.Map;


/**
 * Main interface for JSL library.
 * <p>
 * This interface define methods to initialize and manage a JSL library. Moreover,
 * it define also JSL support classes, struct and exceptions.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface JSL {

    // New instance method

    /**
     * Static method to generate JSL library from <code>settings</code> configs.
     *
     * <b>This method (from {@link JSL} interface) return null object</b>,
     * because JSL sub-classes must re-implement the same method and
     * return sub-class instance.
     *
     * @param settings JSL.Settings containing JSL settings. Sub-classes can
     *                 extend JSL.Settings and used extended class as
     *                 <code>instance</code> param.
     * @return none, it throw a JavaNotImplementedException().
     */
    static JSL instance(Settings settings) {
        return JavaAssertions.makeAssertion_Failed("Each sub-class must override this method", null);
    }


    // JOD implementation version

    /**
     * @return the JSL library implementation version.
     */
    String version();

    /**
     * @return the array of supported JOSP JOD (direct) versions.
     */
    String[] versionsJOSPObject();

    /**
     * @return the array of supported JOSP Protocol versions.
     */
    String[] versionsJOSPProtocol();

    /**
     * @return the array of supported JCP APIs versions.
     */
    String[] versionsJCPAPIs();


    // JSL mngm

    /**
     * @return the JSL library's state.
     */
    JSLState getState();

    /**
     * Starts JSL library and his systems, then connect current JSL library
     * instance to the Gw S2O and to all available local JOD objects.
     */
    void startup() throws StateException;

    /**
     * Disconnect current JSL library instance from the JCP cloud and from all
     * local JOD objects, then stops JSL and all his systems.
     */
    void shutdown() throws StateException;

    /**
     * Disconnect and connect again current JSL library instance from the
     * JCP cloud and all local JOD objects.
     * <p>
     * This method also stop and start JSL instance and all his systems.
     *
     * @return <code>true</code> if the JSL library result connected.
     */
    boolean restart() throws StateException;

    /**
     * Pretty formatted JSL instance's info on current logger.
     */
    void printInstanceInfo();


    // JSL Systems

    JCPAPIsClientSrv getJCPClient();

    JSLServiceInfo getServiceInfo();

    JSLUserMngr getUserMngr();

    JSLAdmin getAdmin();

    JSLObjsMngr getObjsMngr();

    JSLCommunication getCommunication();


    // Listeners

    /**
     * Add given listener to current JSLState's events.
     *
     * @param listener the listener to add.
     */
    void addListener(JSLStateListener listener);

    /**
     * Remove given listener from current JSLState's events.
     *
     * @param listener the listener to remove.
     */
    void removeListener(JSLStateListener listener);

    interface JSLStateListener {

        void onJSLStateChanged(JSLState newState, JSLState oldState);

    }


    // Settings class

    /**
     * JSL's settings interface.
     * <p>
     * JSL.Settings implementations can be used by JSL implementations to
     * customize settings for specific JSL implementations.
     */
    interface Settings {

        // New instance method

        /**
         * Static method to generate JSL.Settings object from <code>file</code>
         * configs.
         *
         * <b>This method (from {@link JSL.Settings} interface) return null object</b>,
         * because JSL.Settings sub-classes must re-implement the same method and
         * return sub-class instance.
         *
         * @param file file to be parsed as configs file.
         * @return null pointer.
         */
        static Settings instance(File file) {
            return null;
        }

        /**
         * Static method to generate JSL.Settings object from <code>file</code>
         * configs.
         *
         * <b>This method (from {@link JSL.Settings} interface) return null object</b>,
         * because JSL.Settings sub-classes must re-implement the same method and
         * return sub-class instance.
         *
         * @param properties map containing the properties to set as JSL configurations.
         * @return null pointer.
         */
        static Settings instance(Map<String, Object> properties) {
            return null;
        }

    }


    // Exceptions

    /**
     * Exceptions for {@link JSL} and {@link JSL.Settings} object creation.
     */
    class FactoryException extends Throwable {
        public FactoryException(String msg) {
            super(msg);
        }

        public FactoryException(String msg, Exception e) {
            super(msg, e);
        }
    }

}
