package com.robypomper.josp.jsl;

import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.comm.peer.PeerDataListener;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.comm.JSLLocalClientsMngr;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.remote.ObjInfo;
import com.robypomper.josp.jsl.objs.remote.ObjPerms;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;
import com.robypomper.josp.jsl.objs.structure.DefaultJSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.states.JSLState;

import java.util.*;

/**
 * Commodity class to register most of the JSL listeners, even without a JSL
 * instance.
 * <p>
 * This class collect all listeners and when a JSL instance becomes available,
 * it starts acting as a bridge between the linkd JSL instance and the registered
 * listeners.
 */
@SuppressWarnings("unused")
public class JSLListeners {

    private JSL jsl = null;

    public void setJSL(JSL jsl) {
        if (jsl == this.jsl) return;

        if (jsl != null && this.jsl == null) {
            this.jsl = jsl;
            registerInternalListeners();
            return;
        }

        if (jsl == null) { // && this.jsl != null) {
            deregisterInternalListeners();
            this.jsl = null;
            return;
        }

        { // if (jsl != null && this.jsl != null) {
            deregisterInternalListeners();
            this.jsl = jsl;
            registerInternalListeners();
            return;
        }
    }


    // Public registrations

    private final List<JSL.JSLStateListener> publicJSLStateListeners = new ArrayList<>();
    private final List<JCPClient2.ConnectionListener> publicJCPClientConnectionListeners = new ArrayList<>();
    private final List<JCPClient2.LoginListener> publicJCPClientLoginListeners = new ArrayList<>();
    private final List<JSLLocalClientsMngr.CommLocalStateListener> publicCommLocalStateListeners = new ArrayList<>();
    private final List<JSLLocalClientsMngr.LocalClientListener> publicCommLocalClientListeners = new ArrayList<>();
    private final List<PeerConnectionListener> publicCommLocalClient_ConnectionListeners = new ArrayList<>();
    private final List<PeerDataListener> publicCommLocalClient_DataListeners = new ArrayList<>();
    private final List<PeerConnectionListener> publicCommCloudConnectionListeners = new ArrayList<>();
    private final List<PeerDataListener> publicCommCloudDataListeners = new ArrayList<>();
    private final List<JSLObjsMngr.ObjsMngrListener> publicObjsMngrListeners = new ArrayList<>();
    private final List<ObjInfo.RemoteObjectInfoListener> publicObjsMngr_InfoListeners = new ArrayList<>();
    private final List<ObjComm.RemoteObjectConnListener> publicObjsMngr_CommListeners = new ArrayList<>();
    private final List<ObjStruct.RemoteObjectStructListener> publicObjsMngr_StructListeners = new ArrayList<>();
    private final List<ObjPerms.RemoteObjectPermsListener> publicObjsMngr_PermsListeners = new ArrayList<>();
    private final List<JSLUserMngr.UserListener> publicUserMngrListener = new ArrayList<>();

    public void addJSLStateListener(JSL.JSLStateListener listener) {
        if (publicJSLStateListeners.contains(listener)) return;

        publicJSLStateListeners.add(listener);
    }

    public void removeJSLStateListener(JSL.JSLStateListener listener) {
        if (!publicJSLStateListeners.contains(listener)) return;

        publicJSLStateListeners.remove(listener);
    }

    private void emitJSLStateListeners(JSLState newState, JSLState oldState) {
        for (JSL.JSLStateListener l : publicJSLStateListeners)
            l.onJSLStateChanged(newState, oldState);
    }

    public void addJCPClientConnectionListeners(JCPClient2.ConnectionListener listener) {
        if (publicJCPClientConnectionListeners.contains(listener)) return;

        publicJCPClientConnectionListeners.add(listener);
    }

    public void removeJCPClientConnectionListeners(JCPClient2.ConnectionListener listener) {
        if (!publicJCPClientConnectionListeners.contains(listener)) return;

        publicJCPClientConnectionListeners.remove(listener);
    }

    private void emitOnJCPClientConnectionConnected(JCPClient2 jcpClient) {
        for (JCPClient2.ConnectionListener l : publicJCPClientConnectionListeners)
            l.onConnected(jcpClient);
    }

    private void emitOnJCPClientConnectionConnectionFailed(JCPClient2 jcpClient, Throwable t) {
        for (JCPClient2.ConnectionListener l : publicJCPClientConnectionListeners)
            l.onConnectionFailed(jcpClient, t);
    }

    private void emitOnJCPClientConnectionAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
        for (JCPClient2.ConnectionListener l : publicJCPClientConnectionListeners)
            l.onAuthenticationFailed(jcpClient, t);
    }

    private void emitOnJCPClientConnectionDisconnected(JCPClient2 jcpClient) {
        for (JCPClient2.ConnectionListener l : publicJCPClientConnectionListeners)
            l.onDisconnected(jcpClient);
    }

    public void addJCPClientLoginListeners(JCPClient2.LoginListener listener) {
        if (publicJCPClientLoginListeners.contains(listener)) return;

        publicJCPClientLoginListeners.add(listener);
    }

    public void removeJCPClientLoginListeners(JCPClient2.LoginListener listener) {
        if (!publicJCPClientLoginListeners.contains(listener)) return;

        publicJCPClientLoginListeners.remove(listener);
    }

    public void emitOnJCPClientLoginLogin(JCPClient2 jcpClient) {
        for (JCPClient2.LoginListener l : publicJCPClientLoginListeners)
            l.onLogin(jcpClient);
    }

    public void emitOnJCPClientLoginLogout(JCPClient2 jcpClient) {
        for (JCPClient2.LoginListener l : publicJCPClientLoginListeners)
            l.onLogout(jcpClient);
    }

    public void addCommLocalStateListeners(JSLLocalClientsMngr.CommLocalStateListener listener) {
        if (publicCommLocalStateListeners.contains(listener)) return;

        publicCommLocalStateListeners.add(listener);
    }

    public void removeCommLocalStateListeners(JSLLocalClientsMngr.CommLocalStateListener listener) {
        if (!publicCommLocalStateListeners.contains(listener)) return;

        publicCommLocalStateListeners.remove(listener);
    }

    private void emitOnCommLocalStateStarted() {
        for (JSLLocalClientsMngr.CommLocalStateListener l : publicCommLocalStateListeners)
            l.onStarted();
    }

    private void emitOnCommLocalStateStopped() {
        for (JSLLocalClientsMngr.CommLocalStateListener l : publicCommLocalStateListeners)
            l.onStopped();
    }

    public void addCommLocalClientListeners(JSLLocalClientsMngr.LocalClientListener listener) {
        if (publicCommLocalClientListeners.contains(listener)) return;

        publicCommLocalClientListeners.add(listener);
    }

    public void removeCommLocalClientListeners(JSLLocalClientsMngr.LocalClientListener listener) {
        if (!publicCommLocalClientListeners.contains(listener)) return;

        publicCommLocalClientListeners.remove(listener);
    }

    private void emitOnCommLocalClientConnected(JSLRemoteObject remObj, JSLLocalClient client) {
        for (JSLLocalClientsMngr.LocalClientListener l : publicCommLocalClientListeners)
            l.onLocalConnected(remObj, client);
    }

    private void emitOnCommLocalClientConnectionError(JSLLocalClient client, Throwable t) {
        for (JSLLocalClientsMngr.LocalClientListener l : publicCommLocalClientListeners)
            l.onLocalConnectionError(client, t);
    }

    private void emitOnCommLocalClientDisconnected(JSLRemoteObject remObj, JSLLocalClient client) {
        for (JSLLocalClientsMngr.LocalClientListener l : publicCommLocalClientListeners)
            l.onLocalDisconnected(remObj, client);
    }

    public void addCommLocalClient_ConnectionListeners(PeerConnectionListener listener) {
        if (publicCommLocalClient_ConnectionListeners.contains(listener))
            return;

        publicCommLocalClient_ConnectionListeners.add(listener);
    }

    public void removeCommLocalClient_ConnectionListeners(PeerConnectionListener listener) {
        if (!publicCommLocalClient_ConnectionListeners.contains(listener))
            return;

        publicCommLocalClient_ConnectionListeners.remove(listener);
    }

    private void emitOnCommLocalClient_ConnectionConnecting(Peer peer) {
        for (PeerConnectionListener l : publicCommLocalClient_ConnectionListeners)
            l.onConnecting(peer);
    }

    private void emitOnCommLocalClient_ConnectionWaiting(Peer peer) {
        for (PeerConnectionListener l : publicCommLocalClient_ConnectionListeners)
            l.onWaiting(peer);
    }

    private void emitOnCommLocalClient_ConnectionConnect(Peer peer) {
        for (PeerConnectionListener l : publicCommLocalClient_ConnectionListeners)
            l.onConnect(peer);
    }

    private void emitOnCommLocalClient_ConnectionDisconnecting(Peer peer) {
        for (PeerConnectionListener l : publicCommLocalClient_ConnectionListeners)
            l.onDisconnecting(peer);
    }

    private void emitOnCommLocalClient_ConnectionDisconnect(Peer peer) {
        for (PeerConnectionListener l : publicCommLocalClient_ConnectionListeners)
            l.onDisconnect(peer);
    }

    private void emitOnCommLocalClient_ConnectionFail(Peer peer, String failMsg, Throwable t) {
        for (PeerConnectionListener l : publicCommLocalClient_ConnectionListeners)
            l.onFail(peer, failMsg, t);
    }

    public void addCommLocalClient_DataListeners(PeerDataListener listener) {
        if (publicCommLocalClient_DataListeners.contains(listener)) return;

        publicCommLocalClient_DataListeners.add(listener);
    }

    public void removeCommLocalClient_DataListeners(PeerDataListener listener) {
        if (!publicCommLocalClient_DataListeners.contains(listener)) return;

        publicCommLocalClient_DataListeners.remove(listener);
    }

    private void emitOnCommLocalClient_DataRx(Peer peer, byte[] data) {
        for (PeerDataListener l : publicCommLocalClient_DataListeners)
            l.onDataRx(peer, data);
    }

    private void emitOnCommLocalClient_DataTx(Peer peer, byte[] data) {
        for (PeerDataListener l : publicCommLocalClient_DataListeners)
            l.onDataTx(peer, data);
    }

    public void addCommCloudConnectionListeners(PeerConnectionListener listener) {
        if (publicCommCloudConnectionListeners.contains(listener)) return;

        publicCommCloudConnectionListeners.add(listener);
    }

    public void removeCommCloudConnectionListeners(PeerConnectionListener listener) {
        if (!publicCommCloudConnectionListeners.contains(listener)) return;

        publicCommCloudConnectionListeners.remove(listener);
    }

    private void emitOnCommCloudConnectionConnecting(Peer peer) {
        for (PeerConnectionListener l : publicCommCloudConnectionListeners)
            l.onConnecting(peer);
    }

    private void emitOnCommCloudConnectionWaiting(Peer peer) {
        for (PeerConnectionListener l : publicCommCloudConnectionListeners)
            l.onWaiting(peer);
    }

    private void emitOnCommCloudConnectionConnect(Peer peer) {
        for (PeerConnectionListener l : publicCommCloudConnectionListeners)
            l.onConnect(peer);
    }

    private void emitOnCommCloudConnectionDisconnecting(Peer peer) {
        for (PeerConnectionListener l : publicCommCloudConnectionListeners)
            l.onDisconnecting(peer);
    }

    private void emitOnCommCloudConnectionDisconnect(Peer peer) {
        for (PeerConnectionListener l : publicCommCloudConnectionListeners)
            l.onDisconnect(peer);
    }

    private void emitOnCommCloudConnectionFail(Peer peer, String failMsg, Throwable t) {
        for (PeerConnectionListener l : publicCommCloudConnectionListeners)
            l.onFail(peer, failMsg, t);
    }

    public void addCommCloudDataListeners(PeerDataListener listener) {
        if (publicCommCloudDataListeners.contains(listener)) return;

        publicCommCloudDataListeners.add(listener);
    }

    public void removeCommCloudDataListeners(PeerDataListener listener) {
        if (!publicCommCloudDataListeners.contains(listener)) return;

        publicCommCloudDataListeners.remove(listener);
    }

    private void emitOnCommCloudDataRx(Peer peer, byte[] data) {
        for (PeerDataListener l : publicCommCloudDataListeners)
            l.onDataRx(peer, data);
    }

    private void emitOnCommCloudDataTx(Peer peer, byte[] data) {
        for (PeerDataListener l : publicCommCloudDataListeners)
            l.onDataTx(peer, data);
    }

    public void addObjsMngrListeners(JSLObjsMngr.ObjsMngrListener listener) {
        if (publicObjsMngrListeners.contains(listener)) return;

        publicObjsMngrListeners.add(listener);
    }

    public void removeObjsMngrListeners(JSLObjsMngr.ObjsMngrListener listener) {
        if (!publicObjsMngrListeners.contains(listener)) return;

        publicObjsMngrListeners.remove(listener);
    }

    private void emitObjsMngrObjAdded(JSLRemoteObject obj) {
        for (JSLObjsMngr.ObjsMngrListener l : publicObjsMngrListeners)
            l.onObjAdded(obj);
    }

    private void emitObjsMngrObjRemoved(JSLRemoteObject obj) {
        for (JSLObjsMngr.ObjsMngrListener l : publicObjsMngrListeners)
            l.onObjRemoved(obj);
    }

    public void addObjsMngr_InfoListeners(ObjInfo.RemoteObjectInfoListener listener) {
        if (publicObjsMngr_InfoListeners.contains(listener)) return;

        publicObjsMngr_InfoListeners.add(listener);
    }

    public void removeObjsMngr_InfoListeners(ObjInfo.RemoteObjectInfoListener listener) {
        if (!publicObjsMngr_InfoListeners.contains(listener)) return;

        publicObjsMngr_InfoListeners.remove(listener);
    }

    private void emitObjsMngr_InfoNameChanged(JSLRemoteObject obj, String newName, String oldName) {
        for (ObjInfo.RemoteObjectInfoListener l : publicObjsMngr_InfoListeners)
            l.onNameChanged(obj, newName, oldName);
    }

    private void emitObjsMngr_InfoOwnerIdChanged(JSLRemoteObject obj, String newOwnerId, String oldOwnerId) {
        for (ObjInfo.RemoteObjectInfoListener l : publicObjsMngr_InfoListeners)
            l.onOwnerIdChanged(obj, newOwnerId, oldOwnerId);
    }

    private void emitObjsMngr_InfoJODVersionChanged(JSLRemoteObject obj, String newJODVersion, String oldJODVersion) {
        for (ObjInfo.RemoteObjectInfoListener l : publicObjsMngr_InfoListeners)
            l.onJODVersionChanged(obj, newJODVersion, oldJODVersion);
    }

    private void emitObjsMngr_InfoModelChanged(JSLRemoteObject obj, String newModel, String oldModel) {
        for (ObjInfo.RemoteObjectInfoListener l : publicObjsMngr_InfoListeners)
            l.onModelChanged(obj, newModel, oldModel);
    }

    private void emitObjsMngr_InfoBrandChanged(JSLRemoteObject obj, String newBrand, String oldBrand) {
        for (ObjInfo.RemoteObjectInfoListener l : publicObjsMngr_InfoListeners)
            l.onBrandChanged(obj, newBrand, oldBrand);
    }

    private void emitObjsMngr_InfoLongDescrChanged(JSLRemoteObject obj, String newDescription, String oldDescription) {
        for (ObjInfo.RemoteObjectInfoListener l : publicObjsMngr_InfoListeners)
            l.onLongDescrChanged(obj, newDescription, oldDescription);
    }

    public void addObjsMngr_CommListeners(ObjComm.RemoteObjectConnListener listener) {
        if (publicObjsMngr_CommListeners.contains(listener)) return;

        publicObjsMngr_CommListeners.add(listener);
    }

    public void removeObjsMngr_CommListeners(ObjComm.RemoteObjectConnListener listener) {
        if (!publicObjsMngr_CommListeners.contains(listener)) return;

        publicObjsMngr_CommListeners.remove(listener);
    }

    private void emitObjsMngr_CommLocalConnected(JSLRemoteObject obj, JSLLocalClient client) {
        for (ObjComm.RemoteObjectConnListener l : publicObjsMngr_CommListeners)
            l.onLocalConnected(obj, client);
    }

    private void emitObjsMngr_CommLocalDisconnected(JSLRemoteObject obj, JSLLocalClient client) {
        for (ObjComm.RemoteObjectConnListener l : publicObjsMngr_CommListeners)
            l.onLocalDisconnected(obj, client);
    }

    private void emitObjsMngr_CommCloudConnected(JSLRemoteObject obj) {
        for (ObjComm.RemoteObjectConnListener l : publicObjsMngr_CommListeners)
            l.onCloudConnected(obj);
    }

    private void emitObjsMngr_CommCloudDisconnected(JSLRemoteObject obj) {
        for (ObjComm.RemoteObjectConnListener l : publicObjsMngr_CommListeners)
            l.onCloudDisconnected(obj);
    }

    public void addObjsMngr_StructListeners(ObjStruct.RemoteObjectStructListener listener) {
        if (publicObjsMngr_StructListeners.contains(listener)) return;

        publicObjsMngr_StructListeners.add(listener);
    }

    public void removeObjsMngr_StructListeners(ObjStruct.RemoteObjectStructListener listener) {
        if (!publicObjsMngr_StructListeners.contains(listener)) return;

        publicObjsMngr_StructListeners.remove(listener);
    }

    private void emitObjsMngr_StructStructureChanged(JSLRemoteObject obj, JSLRoot newRoot) {
        for (ObjStruct.RemoteObjectStructListener l : publicObjsMngr_StructListeners)
            l.onStructureChanged(obj, newRoot);
    }

    public void addObjsMngr_PermsListeners(ObjPerms.RemoteObjectPermsListener listener) {
        if (publicObjsMngr_PermsListeners.contains(listener)) return;

        publicObjsMngr_PermsListeners.add(listener);
    }

    public void removeObjsMngr_PermsListeners(ObjPerms.RemoteObjectPermsListener listener) {
        if (!publicObjsMngr_PermsListeners.contains(listener)) return;

        publicObjsMngr_PermsListeners.remove(listener);
    }

    private void emitObjsMngr_PermsPermissionsChanged(JSLRemoteObject obj, List<JOSPPerm> newPerms, List<JOSPPerm> oldPerms) {
        for (ObjPerms.RemoteObjectPermsListener l : publicObjsMngr_PermsListeners)
            l.onPermissionsChanged(obj, newPerms, oldPerms);
    }

    private void emitObjsMngr_PermsServicePermChanged(JSLRemoteObject obj, JOSPPerm.Connection connType, JOSPPerm.Type newPermType, JOSPPerm.Type oldPermType) {
        for (ObjPerms.RemoteObjectPermsListener l : publicObjsMngr_PermsListeners)
            l.onServicePermChanged(obj, connType, newPermType, oldPermType);
    }

    public void addUserMngrListener(JSLUserMngr.UserListener listener) {
        if (publicUserMngrListener.contains(listener)) return;

        publicUserMngrListener.add(listener);
    }

    public void removeUserMngrListener(JSLUserMngr.UserListener listener) {
        if (!publicUserMngrListener.contains(listener)) return;

        publicUserMngrListener.remove(listener);
    }

    private void emitUserMngrLoginPreRestart(JSLUserMngr jslUserMngr) {
        for (JSLUserMngr.UserListener l : publicUserMngrListener)
            l.onLoginPreRestart(jslUserMngr);
    }

    private void emitUserMngrLogoutPreRestart(JSLUserMngr jslUserMngr) {
        for (JSLUserMngr.UserListener l : publicUserMngrListener)
            l.onLogoutPreRestart(jslUserMngr);
    }

    private void emitUserMngrLogin(JSLUserMngr jslUserMngr) {
        for (JSLUserMngr.UserListener l : publicUserMngrListener)
            l.onLogin(jslUserMngr);
    }

    private void emitUserMngrLogout(JSLUserMngr jslUserMngr) {
        for (JSLUserMngr.UserListener l : publicUserMngrListener)
            l.onLogout(jslUserMngr);
    }


    // Filtered registrations

    private final Map<String, List<JSLObjsMngr.ObjsMngrListener>> publicObjsMngrListeners_byID = new HashMap<>();
    private final Map<String, List<JSLObjsMngr.ObjsMngrListener>> publicObjsMngrListeners_byModel = new HashMap<>();
    private final Map<String, List<JSLObjsMngr.ObjsMngrListener>> publicObjsMngrListeners_byBrand = new HashMap<>();
    private final Map<String, List<JSLObjsMngr.ObjsMngrListener>> publicObjsMngrListeners_byCompPath = new HashMap<>();


    public void addObjsMngrListenersByID(String id, JSLObjsMngr.ObjsMngrListener listener) {
        if (!publicObjsMngrListeners_byID.containsKey(id))
            publicObjsMngrListeners_byID.put(id, new ArrayList<>());

        if (publicObjsMngrListeners_byID.get(id).contains(listener)) return;

        publicObjsMngrListeners_byID.get(id).add(listener);
    }

    public void removeObjsMngrListenersByID(String id, JSLObjsMngr.ObjsMngrListener listener) {
        if (!publicObjsMngrListeners_byID.containsKey(id)) return;

        if (!publicObjsMngrListeners_byID.get(id).contains(listener)) return;

        publicObjsMngrListeners_byID.get(id).remove(listener);
    }

    private void emitOnObjAddedByID(JSLRemoteObject obj) {
        String id = obj.getId();
        if (!id.isEmpty()) {
            List<JSLObjsMngr.ObjsMngrListener> listeners = publicObjsMngrListeners_byID.get(id);
            if (listeners != null)
                for (JSLObjsMngr.ObjsMngrListener l : listeners)
                    l.onObjAdded(obj);
        }
    }

    private void emitOnObjRemovedByID(JSLRemoteObject obj) {
        String id = obj.getId();
        if (!id.isEmpty()) {
            List<JSLObjsMngr.ObjsMngrListener> listeners = publicObjsMngrListeners_byID.get(id);
            if (listeners != null)
                for (JSLObjsMngr.ObjsMngrListener l : listeners)
                    l.onObjRemoved(obj);
        }
    }

    public void addObjsMngrListenersByModel(String model, JSLObjsMngr.ObjsMngrListener listener) {
        if (!publicObjsMngrListeners_byModel.containsKey(model))
            publicObjsMngrListeners_byModel.put(model, new ArrayList<>());

        if (publicObjsMngrListeners_byModel.get(model).contains(listener))
            return;

        publicObjsMngrListeners_byModel.get(model).add(listener);
    }

    public void removeObjsMngrListenersByModel(String model, JSLObjsMngr.ObjsMngrListener listener) {
        if (!publicObjsMngrListeners_byModel.containsKey(model)) return;

        if (!publicObjsMngrListeners_byModel.get(model).contains(listener))
            return;

        publicObjsMngrListeners_byModel.get(model).remove(listener);
    }

    private void emitOnObjAddedByModel(JSLRemoteObject obj) {
        String model = obj.getInfo().getModel();
        if (!model.isEmpty()) {
            List<JSLObjsMngr.ObjsMngrListener> listeners = publicObjsMngrListeners_byModel.get(model);
            if (listeners != null)
                for (JSLObjsMngr.ObjsMngrListener l : listeners)
                    l.onObjAdded(obj);
        }
    }

    private void emitOnObjRemovedByModel(JSLRemoteObject obj) {
        String model = obj.getInfo().getModel();
        if (!model.isEmpty()) {
            List<JSLObjsMngr.ObjsMngrListener> listeners = publicObjsMngrListeners_byModel.get(model);
            if (listeners != null)
                for (JSLObjsMngr.ObjsMngrListener l : listeners)
                    l.onObjRemoved(obj);
        }
    }

    public void addObjsMngrListenersByBrand(String brand, JSLObjsMngr.ObjsMngrListener listener) {
        if (!publicObjsMngrListeners_byBrand.containsKey(brand))
            publicObjsMngrListeners_byBrand.put(brand, new ArrayList<>());

        if (publicObjsMngrListeners_byBrand.get(brand).contains(listener))
            return;

        publicObjsMngrListeners_byBrand.get(brand).add(listener);
    }

    public void removeObjsMngrListenersByBrand(String brand, JSLObjsMngr.ObjsMngrListener listener) {
        if (!publicObjsMngrListeners_byBrand.containsKey(brand)) return;

        if (!publicObjsMngrListeners_byBrand.get(brand).contains(listener))
            return;

        publicObjsMngrListeners_byBrand.get(brand).remove(listener);
    }

    private void emitOnObjAddedByBrand(JSLRemoteObject obj) {
        String brand = obj.getInfo().getBrand();
        if (!brand.isEmpty()) {
            List<JSLObjsMngr.ObjsMngrListener> listeners = publicObjsMngrListeners_byBrand.get(brand);
            if (listeners != null)
                for (JSLObjsMngr.ObjsMngrListener l : listeners)
                    l.onObjAdded(obj);
        }
    }

    private void emitOnObjRemovedByBrand(JSLRemoteObject obj) {
        String brand = obj.getInfo().getBrand();
        if (!brand.isEmpty()) {
            List<JSLObjsMngr.ObjsMngrListener> listeners = publicObjsMngrListeners_byBrand.get(brand);
            if (listeners != null)
                for (JSLObjsMngr.ObjsMngrListener l : listeners)
                    l.onObjRemoved(obj);
        }
    }

    public void addObjsMngrListenersByCompPath(String compPath, JSLObjsMngr.ObjsMngrListener listener) {
        if (!publicObjsMngrListeners_byCompPath.containsKey(compPath))
            publicObjsMngrListeners_byCompPath.put(compPath, new ArrayList<>());

        if (publicObjsMngrListeners_byCompPath.get(compPath).contains(listener))
            return;

        publicObjsMngrListeners_byCompPath.get(compPath).add(listener);
    }

    public void removeObjsMngrListenersByCompPath(String compPath, JSLObjsMngr.ObjsMngrListener listener) {
        if (!publicObjsMngrListeners_byCompPath.containsKey(compPath)) return;

        if (!publicObjsMngrListeners_byCompPath.get(compPath).contains(listener))
            return;

        publicObjsMngrListeners_byCompPath.get(compPath).remove(listener);
    }

    private void emitOnObjAddedByCompPath(JSLRemoteObject obj) {
        Set<String> compPaths = publicObjsMngrListeners_byCompPath.keySet();
        JSLRoot root = obj.getStruct().getStructure();
        if (root != null) {
            for (String compPath : compPaths) {
                JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
                JSLComponent comp = DefaultJSLComponentPath.searchComponent(root, componentPath);
                if (comp != null) {
                    List<JSLObjsMngr.ObjsMngrListener> listeners = publicObjsMngrListeners_byCompPath.get(compPath);
                    if (listeners != null)
                        for (JSLObjsMngr.ObjsMngrListener l : listeners)
                            l.onObjAdded(obj);
                }
            }
        }
    }

    private void emitOnObjRemovedByCompPath(JSLRemoteObject obj) {
        Set<String> compPaths = publicObjsMngrListeners_byCompPath.keySet();
        JSLRoot root = obj.getStruct().getStructure();
        if (root != null) {
            for (String compPath : compPaths) {
                JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
                JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), componentPath);
                if (comp != null) {
                    List<JSLObjsMngr.ObjsMngrListener> listeners = publicObjsMngrListeners_byCompPath.get(compPath);
                    if (listeners != null)
                        for (JSLObjsMngr.ObjsMngrListener l : listeners)
                            l.onObjRemoved(obj);
                }
            }
        }
    }


    // Internal registrations

    private void registerInternalListeners() {
        jsl.addListener(internalJSLStateListener);
        jsl.getJCPClient().addConnectionListener(internalJCPClientConnectionListener);
        jsl.getJCPClient().addLoginListener(internalJCPClientLoginListener);
        jsl.getCommunication().getLocalConnections().addListener(internalCommLocalStateListener);
        jsl.getCommunication().getLocalConnections().addListener(internalCommLocalClientListener);
        // sub-listeners for each client
        jsl.getCommunication().getCloudConnection().addListener(internalCommCloudConnectionListener);
        jsl.getCommunication().getCloudConnection().addListener(internalCommCloudDataListener);
        jsl.getObjsMngr().addListener(internalObjsMngrListener);
        // sub-listeners for each object
        jsl.getUserMngr().addUserListener(internalUserMngrListener);
    }

    private void deregisterInternalListeners() {
        jsl.removeListener(internalJSLStateListener);
        jsl.getJCPClient().removeConnectionListener(internalJCPClientConnectionListener);
        jsl.getJCPClient().removeLoginListener(internalJCPClientLoginListener);
        jsl.getCommunication().getLocalConnections().removeListener(internalCommLocalStateListener);
        jsl.getCommunication().getLocalConnections().removeListener(internalCommLocalClientListener);
        // sub-listeners for each client
        jsl.getCommunication().getCloudConnection().removeListener(internalCommCloudConnectionListener);
        jsl.getCommunication().getCloudConnection().removeListener(internalCommCloudDataListener);
        jsl.getObjsMngr().removeListener(internalObjsMngrListener);
        // sub-listeners for each object
        jsl.getUserMngr().removeUserListener(internalUserMngrListener);
    }

    private void registerInternalListeners_LocalClient(JSLLocalClient jslLocCli) {
        jslLocCli.addListener(internalCommLocalClient_ConnectionListener);
        jslLocCli.addListener(internalCommLocalClient_DataListener);
    }

    private void deregisterInternalListeners_LocalClient(JSLLocalClient jslLocCli) {
        jslLocCli.removeListener(internalCommLocalClient_ConnectionListener);
        jslLocCli.removeListener(internalCommLocalClient_DataListener);
    }

    private void registerInternalListeners_Object(JSLRemoteObject obj) {
        obj.getInfo().addListener(internalObjsMngr_InfoListener);
        obj.getComm().addListener(internalObjsMngr_CommListener);
        obj.getStruct().addListener(internalObjsMngr_StructListener);
        obj.getPerms().addListener(internalObjsMngr_PermsListener);
    }

    private void deregisterInternalListeners_Object(JSLRemoteObject obj) {
        obj.getInfo().removeListener(internalObjsMngr_InfoListener);
        obj.getComm().removeListener(internalObjsMngr_CommListener);
        obj.getStruct().removeListener(internalObjsMngr_StructListener);
        obj.getPerms().removeListener(internalObjsMngr_PermsListener);
    }


    // Internal Listeners

    private final JSL.JSLStateListener internalJSLStateListener = new JSL.JSLStateListener() {
        @Override
        public void onJSLStateChanged(JSLState newState, JSLState oldState) {
            // public listeners
            emitJSLStateListeners(newState, oldState);
        }
    };
    private final JCPClient2.ConnectionListener internalJCPClientConnectionListener = new JCPClient2.ConnectionListener() {
        @Override
        public void onConnected(JCPClient2 jcpClient) {
            // public listeners
            emitOnJCPClientConnectionConnected(jcpClient);
        }

        @Override
        public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
            // public listeners
            emitOnJCPClientConnectionConnectionFailed(jcpClient, t);
        }

        @Override
        public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
            // public listeners
            emitOnJCPClientConnectionAuthenticationFailed(jcpClient, t);
        }

        @Override
        public void onDisconnected(JCPClient2 jcpClient) {
            // public listeners
            emitOnJCPClientConnectionDisconnected(jcpClient);
        }
    };
    private final JCPClient2.LoginListener internalJCPClientLoginListener = new JCPClient2.LoginListener() {
        @Override
        public void onLogin(JCPClient2 jcpClient) {
            // public listeners
            emitOnJCPClientLoginLogin(jcpClient);
        }

        @Override
        public void onLogout(JCPClient2 jcpClient) {
            // public listeners
            emitOnJCPClientLoginLogout(jcpClient);
        }
    };
    private final JSLLocalClientsMngr.CommLocalStateListener internalCommLocalStateListener = new JSLLocalClientsMngr.CommLocalStateListener() {
        @Override
        public void onStarted() {
            // public listeners
            emitOnCommLocalStateStarted();
        }

        @Override
        public void onStopped() {
            // public listeners
            emitOnCommLocalStateStopped();
        }
    };
    private final JSLLocalClientsMngr.LocalClientListener internalCommLocalClientListener = new JSLLocalClientsMngr.LocalClientListener() {
        @Override
        public void onLocalConnected(JSLRemoteObject remObj, JSLLocalClient jslLocCli) {
            // internal listeners
            registerInternalListeners_LocalClient(jslLocCli);

            // public listeners
            emitOnCommLocalClientConnected(remObj, jslLocCli);
        }

        @Override
        public void onLocalConnectionError(JSLLocalClient jslLocCli, Throwable throwable) {
            // public listeners
            emitOnCommLocalClientConnectionError(jslLocCli, throwable);
        }

        @Override
        public void onLocalDisconnected(JSLRemoteObject remObj, JSLLocalClient jslLocCli) {
            // internal listeners
            deregisterInternalListeners_LocalClient(jslLocCli);

            // public listeners
            emitOnCommLocalClientDisconnected(remObj, jslLocCli);
        }
    };
    private final PeerConnectionListener internalCommLocalClient_ConnectionListener = new PeerConnectionListener() {
        @Override
        public void onConnecting(Peer peer) {
            // public listeners
            emitOnCommLocalClient_ConnectionConnecting(peer);
        }

        @Override
        public void onWaiting(Peer peer) {
            // public listeners
            emitOnCommLocalClient_ConnectionWaiting(peer);
        }

        @Override
        public void onConnect(Peer peer) {
            // public listeners
            emitOnCommLocalClient_ConnectionConnect(peer);
        }

        @Override
        public void onDisconnecting(Peer peer) {
            // public listeners
            emitOnCommLocalClient_ConnectionDisconnecting(peer);
        }

        @Override
        public void onDisconnect(Peer peer) {
            // public listeners
            emitOnCommLocalClient_ConnectionDisconnect(peer);
        }

        @Override
        public void onFail(Peer peer, String failMsg, Throwable exception) {
            // public listeners
            emitOnCommLocalClient_ConnectionFail(peer, failMsg, exception);
        }
    };
    private final PeerDataListener internalCommLocalClient_DataListener = new PeerDataListener() {
        @Override
        public void onDataRx(Peer peer, byte[] data) {
            // public listeners
            emitOnCommLocalClient_DataRx(peer, data);
        }

        @Override
        public void onDataTx(Peer peer, byte[] data) {
            // public listeners
            emitOnCommLocalClient_DataTx(peer, data);
        }
    };
    private final PeerConnectionListener internalCommCloudConnectionListener = new PeerConnectionListener() {
        @Override
        public void onConnecting(Peer peer) {
            // public listeners
            emitOnCommCloudConnectionConnecting(peer);
        }

        @Override
        public void onWaiting(Peer peer) {
            // public listeners
            emitOnCommCloudConnectionWaiting(peer);
        }

        @Override
        public void onConnect(Peer peer) {
            // public listeners
            emitOnCommCloudConnectionConnect(peer);
        }

        @Override
        public void onDisconnecting(Peer peer) {
            // public listeners
            emitOnCommCloudConnectionDisconnecting(peer);
        }

        @Override
        public void onDisconnect(Peer peer) {
            // public listeners
            emitOnCommCloudConnectionDisconnect(peer);
        }

        @Override
        public void onFail(Peer peer, String failMsg, Throwable exception) {
            // public listeners
            emitOnCommCloudConnectionFail(peer, failMsg, exception);
        }
    };
    private final PeerDataListener internalCommCloudDataListener = new PeerDataListener() {
        @Override
        public void onDataRx(Peer peer, byte[] data) {
            // public listeners
            emitOnCommCloudDataRx(peer, data);
        }

        @Override
        public void onDataTx(Peer peer, byte[] data) {
            // public listeners
            emitOnCommCloudDataTx(peer, data);
        }
    };
    private final JSLObjsMngr.ObjsMngrListener internalObjsMngrListener = new JSLObjsMngr.ObjsMngrListener() {
        @Override
        public void onObjAdded(JSLRemoteObject obj) {
            // internal listeners
            registerInternalListeners_Object(obj);

            // public listeners
            emitObjsMngrObjAdded(obj);

            // filtered listeners
            emitOnObjAddedByID(obj);
            /* Following filter is USELESS because the JSLObjsMngr.ObjsMngrListener.onObjAdded
             * event is thrown before get the presentations messages, that means when it's triggered
             * the jsl don't know yet his name, model, structure, permissions... */
            emitOnObjAddedByModel(obj);
            emitOnObjAddedByBrand(obj);
            emitOnObjAddedByCompPath(obj);
        }

        @Override
        public void onObjRemoved(JSLRemoteObject obj) {
            // internal listeners
            deregisterInternalListeners_Object(obj);

            // public listeners
            emitObjsMngrObjRemoved(obj);

            // filtered listeners
            emitOnObjRemovedByID(obj);
            emitOnObjRemovedByModel(obj);
            emitOnObjRemovedByBrand(obj);
            emitOnObjRemovedByCompPath(obj);
        }
    };
    private final ObjInfo.RemoteObjectInfoListener internalObjsMngr_InfoListener = new ObjInfo.RemoteObjectInfoListener() {
        @Override
        public void onNameChanged(JSLRemoteObject obj, String newName, String oldName) {
            // public listeners
            emitObjsMngr_InfoNameChanged(obj, newName, oldName);
        }

        @Override
        public void onOwnerIdChanged(JSLRemoteObject obj, String newOwnerId, String oldOwnerId) {
            // public listeners
            emitObjsMngr_InfoOwnerIdChanged(obj, newOwnerId, oldOwnerId);
        }

        @Override
        public void onJODVersionChanged(JSLRemoteObject obj, String newJODVersion, String oldJODVersion) {
            // public listeners
            emitObjsMngr_InfoJODVersionChanged(obj, newJODVersion, oldJODVersion);
        }

        @Override
        public void onModelChanged(JSLRemoteObject obj, String newModel, String oldModel) {
            // public listeners
            emitObjsMngr_InfoModelChanged(obj, newModel, oldModel);

            // filtered listeners
            /* Following filter is REQUIRED because the JSLObjsMngr.ObjsMngrListener.onObjAdded
             * event is thrown before get the presentations messages, that means when it's triggered
             * the jsl don't know yet his name, model, structure, permissions... */
            if (oldModel == null || newModel.compareTo(oldModel) != 0) {
                emitOnObjRemovedByModel(obj);
                emitOnObjAddedByModel(obj);
            }
        }

        @Override
        public void onBrandChanged(JSLRemoteObject obj, String newBrand, String oldBrand) {
            // public listeners
            emitObjsMngr_InfoBrandChanged(obj, newBrand, oldBrand);

            // filtered listeners
            /* Following filter is REQUIRED because the JSLObjsMngr.ObjsMngrListener.onObjAdded
             * event is thrown before get the presentations messages, that means when it's triggered
             * the jsl don't know yet his name, model, structure, permissions... */
            if (oldBrand == null || newBrand.compareTo(oldBrand) != 0) {
                emitOnObjRemovedByBrand(obj);
                emitOnObjAddedByBrand(obj);
            }
        }

        @Override
        public void onLongDescrChanged(JSLRemoteObject obj, String newLongDescr, String oldLongDescr) {
            // public listeners
            emitObjsMngr_InfoLongDescrChanged(obj, newLongDescr, oldLongDescr);
        }
    };
    private final ObjComm.RemoteObjectConnListener internalObjsMngr_CommListener = new ObjComm.RemoteObjectConnListener() {
        @Override
        public void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            // public listeners
            emitObjsMngr_CommLocalConnected(obj, localClient);
        }

        @Override
        public void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            // public listeners
            emitObjsMngr_CommLocalDisconnected(obj, localClient);
        }

        @Override
        public void onCloudConnected(JSLRemoteObject obj) {
            // public listeners
            emitObjsMngr_CommCloudConnected(obj);
        }

        @Override
        public void onCloudDisconnected(JSLRemoteObject obj) {
            // public listeners
            emitObjsMngr_CommCloudDisconnected(obj);
        }
    };
    private final ObjStruct.RemoteObjectStructListener internalObjsMngr_StructListener = new ObjStruct.RemoteObjectStructListener() {
        @Override
        public void onStructureChanged(JSLRemoteObject obj, JSLRoot newRoot) {
            emitObjsMngr_StructStructureChanged(obj, newRoot);
            // Following event is emitted every time the structure changes
            // even if the structure already contained the component.
            //
            // Actually it's not a problem because structures can't be updated
            // otherwise it require a cache system to store the old structure
            // and compare it with the new one to check if the component was added
            // moreover
            emitOnObjAddedByCompPath(obj);
        }
    };
    private final ObjPerms.RemoteObjectPermsListener internalObjsMngr_PermsListener = new ObjPerms.RemoteObjectPermsListener() {
        @Override
        public void onPermissionsChanged(JSLRemoteObject obj, List<JOSPPerm> newPerms, List<JOSPPerm> oldPerms) {
            // public listeners
            emitObjsMngr_PermsPermissionsChanged(obj, newPerms, oldPerms);
        }

        @Override
        public void onServicePermChanged(JSLRemoteObject obj, JOSPPerm.Connection connType, JOSPPerm.Type newPermType, JOSPPerm.Type oldPermType) {
            // public listeners
            emitObjsMngr_PermsServicePermChanged(obj, connType, newPermType, oldPermType);
        }
    };
    private final JSLUserMngr.UserListener internalUserMngrListener = new JSLUserMngr.UserListener() {
        @Override
        public void onLoginPreRestart(JSLUserMngr jslUserMngr) {
            // public listeners
            emitUserMngrLoginPreRestart(jslUserMngr);
        }

        @Override
        public void onLogoutPreRestart(JSLUserMngr jslUserMngr) {
            // public listeners
            emitUserMngrLogoutPreRestart(jslUserMngr);
        }

        @Override
        public void onLogin(JSLUserMngr jslUserMngr) {
            // public listeners
            emitUserMngrLogin(jslUserMngr);
        }

        @Override
        public void onLogout(JSLUserMngr jslUserMngr) {
            // public listeners
            emitUserMngrLogout(jslUserMngr);
        }
    };

}
