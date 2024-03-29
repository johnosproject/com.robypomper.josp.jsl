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

package com.robypomper.josp.jsl.objs;

import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.remote.DefaultObjComm;
import com.robypomper.josp.jsl.objs.remote.ObjPerms;
import com.robypomper.josp.jsl.objs.structure.AbsJSLState;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.protocol.JOSPPerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Implementation of the {@link JSLObjsMngr} interface.
 */
public class JSLObjsMngr_002 implements JSLObjsMngr {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JSLObjsMngr_002.class);
    private final JSLSettings_002 locSettings;
    private final JSLServiceInfo srvInfo;
    private final List<JSLRemoteObject> objs = new ArrayList<>();
    private JSLCommunication communication = null;
    private final List<ObjsMngrListener> listeners = new ArrayList<>();


    // Constructor

    /**
     * Default objects manager constructor.
     *
     * @param settings the JSL settings.
     * @param srvInfo  the service's info.
     */
    public JSLObjsMngr_002(JSLSettings_002 settings, JSLServiceInfo srvInfo, JSLUserMngr usrMngr) {
        this.locSettings = settings;
        this.srvInfo = srvInfo;
        usrMngr.addUserListener(userListener);

        log.info("Initialized JSLObjsMngr");

        AbsJSLState.loadAllStateClasses();
    }


    // Object's access

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLRemoteObject> getAllObjects() {
        return Collections.unmodifiableList(objs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLRemoteObject> getAllConnectedObjects() {
        List<JSLRemoteObject> connObjs = new ArrayList<>();
        for (JSLRemoteObject obj : objs)
            if (obj.getComm().isLocalConnected())
                connObjs.add(obj);

        return Collections.unmodifiableList(connObjs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLRemoteObject getById(String objId) {
        for (JSLRemoteObject obj : objs)
            if (objId.equalsIgnoreCase(obj.getId()))
                return obj;

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLRemoteObject getByConnection(JSLLocalClient client) {
        for (JSLRemoteObject obj : objs)
            if (((DefaultObjComm) obj.getComm()).getLocalClients().contains(client))
                return obj;

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLRemoteObject> getByModel(String model) {
        List<JSLRemoteObject> filteredObjs = new ArrayList<>();
        for (JSLRemoteObject obj : objs)
            if (obj.getInfo().getModel().compareToIgnoreCase(model)==0)
                filteredObjs.add(obj);

        return filteredObjs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JSLRemoteObject> searchObjects(JSLObjectSearchPattern pattern) {
        log.warn("Method searchObjects(...) not implemented, return empty objects list");
        return new ArrayList<>();
    }


    // Object's mngm

    private void resetAllObjects() {
        synchronized (objs) {
            List<JSLRemoteObject> tmpList = new ArrayList<>(objs);
            for (JSLRemoteObject obj : tmpList) {
                objs.remove(obj);
                emit_ObjRemoved(obj);
            }
        }
    }


    // Connections mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLRemoteObject addNewConnection(JSLLocalClient serverConnection, String locConnObjId) {
        assert serverConnection.getState().isConnected() : "Method addLocalClient() can be call only if localClient is connected.";

        log.debug(String.format("Register object '%s' new connection (%s:%d) from '%s' service", locConnObjId, serverConnection.getSocket().getInetAddress(), serverConnection.getSocket().getPort(), srvInfo.getSrvId()));
        JSLRemoteObject remObj;
        synchronized (objs) {
            remObj = getById(locConnObjId);
            if (remObj == null) {
                remObj = new DefaultJSLRemoteObject(srvInfo, locConnObjId, serverConnection, communication);
                objs.add(remObj);
                remObj.getPerms().addListener(objectPermsListener);
                emit_ObjAdded(remObj);

            } else {
                ((DefaultObjComm) remObj.getComm()).addLocalClient(serverConnection);
            }
        }

        return remObj;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeConnection(JSLLocalClient client) {
        log.warn("Method removeConnection(...) not implemented, return false");
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCommunication(JSLCommunication communication) {
        this.communication = communication;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCloudObject(String objId) {
        assert getById(objId) == null;
        log.info(String.format("Register new cloud object '%s' to '%s' service", objId, srvInfo.getSrvId()));
        JSLRemoteObject remObj = new DefaultJSLRemoteObject(srvInfo, objId, communication);
        objs.add(remObj);
        remObj.getPerms().addListener(objectPermsListener);
        emit_ObjAdded(remObj);
    }


    // Listeners connections

    public void addListener(ObjsMngrListener listener) {
        if (listeners.contains(listener))
            return;

        listeners.add(listener);
    }

    public void removeListener(ObjsMngrListener listener) {
        if (!listeners.contains(listener))
            return;

        listeners.remove(listener);
    }

    private void emit_ObjAdded(JSLRemoteObject obj) {
        for (ObjsMngrListener l : listeners)
            l.onObjAdded(obj);
    }

    private void emit_ObjRemoved(JSLRemoteObject obj) {
        for (ObjsMngrListener l : listeners)
            l.onObjRemoved(obj);
    }

    // Listeners object permission's changes

    private ObjPerms.RemoteObjectPermsListener objectPermsListener = new ObjPerms.RemoteObjectPermsListener() {

        @Override
        public void onPermissionsChanged(JSLRemoteObject obj, List<JOSPPerm> newPerms, List<JOSPPerm> oldPerms) {}

        @Override
        public void onServicePermChanged(JSLRemoteObject obj, JOSPPerm.Connection connType, JOSPPerm.Type newPermType, JOSPPerm.Type oldPermType) {
            if (obj.getPerms().getServicePerm(JOSPPerm.Connection.LocalAndCloud) == JOSPPerm.Type.None
                && obj.getPerms().getServicePerm(JOSPPerm.Connection.OnlyLocal) == JOSPPerm.Type.None) {
                obj.getPerms().removeListener(objectPermsListener);
                objs.remove(obj);
                emit_ObjRemoved(obj);
            }

        }

    };


    // User's login/out

    private final JSLUserMngr.UserListener userListener = new JSLUserMngr.UserListener() {

        @Override
        public void onLoginPreRestart(JSLUserMngr jslUserMngr) {
            resetAllObjects();
        }

        @Override
        public void onLogoutPreRestart(JSLUserMngr jslUserMngr) {
            resetAllObjects();
        }

        @Override
        public void onLogin(JSLUserMngr jslUserMngr) {

        }

        @Override
        public void onLogout(JSLUserMngr jslUserMngr) {

        }
    };

}
