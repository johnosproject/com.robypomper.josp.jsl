import APIEntity from '../APIEntity'
import APIStructure from './APIStructure';
import APIPermission from './APIPermission'

const ObjectJavascript = globalThis.Object;

export default class APIObject extends APIEntity {

    // Internal vars

    _instance = null;
        // id = obj.id;
        // name = obj.name;
        // model = obj.model;
        // jodVersion = obj.jodVersion;
        // owner = obj.owner;
        // permission = obj.permission;
        // isCloudConnected = obj.isCloudConnected;
        // isConnected = obj.isConnected;
        // isLocalConnected = obj.isLocalConnected;
        // _pathEvents = obj.pathEvents;
        // _pathPerms = obj.pathPerms;
        // _pathPermsAdd = obj.pathPermsAdd;
        // _pathSetName = obj.pathSetName;
        // _pathSetOwner = obj.pathSetOwner;
        // _pathSingle = obj.pathSingle;
        // _pathStruct = obj.pathStruct;
    _listenersOnObject = [];
    _fetchStructOnInit = false;
    _fetchPermsOnInit = false;


    // Constructors

    constructor(jcpJSLWB, obj, onInit = null, onError = null) {
        super(jcpJSLWB,obj.pathSingle,onInit,onError);
        this._gwsSSEListener = new GWsSSEListener(this);
        this._objectsSSEListener = new ObjectsSSEListener(this);
        this._objectSSEListener = new ObjectSSEListener(this);
        this._instance = obj;
        if (!onInit && !onError)
            this._isInit = true;

        if (this._jcpJSLWB.isSSEEnabled()) {
            this._jcpJSLWB.getSSEUpdater().addOnGWEvents(this._gwsSSEListener);
            this._jcpJSLWB.getSSEUpdater().addOnObjsEvents(this._objectsSSEListener);
            this._jcpJSLWB.getSSEUpdater().addOnObjEvents(this._objectSSEListener);
        }
        // else
        //     ToDo schedule interval on _fetch()
    }

    
    // Initialization

    setInstance(fetchedInstance) {
        this._instance = fetchedInstance;
        if (this._fetchStructOnInit) this.fetchStruct();
        if (this._fetchPermsOnInit) this.fetchPerms();
    }


    // Getters

    getId() {
        return this._instance ? this._instance.id : "Unknown";
    }

    getName() {
        return this._instance ? this._instance.name : "Unknown";
    }

    getModel() {
        return this._instance ? this._instance.model : "Unknown";
    }

    getJODVersion() {
        return this._instance ? this._instance.jodVersion : "Unknown";
    }

    getOwner() {
        return this._instance ? this._instance.owner : "Unknown";
    }

    getPermission() {
        return this._instance ? this._instance.permission : "Unknown";
    }

    isConnected() {
        if (!this._jcpJSLWB.isGWConnected())
            return false;
        return this._instance ? this._instance.isConnected : "Unknown";
    }

    isCloudConnected() {
        return this._instance ? this._instance.isCloudConnected : "Unknown";
    }

    isLocalConnected() {
        return this._instance ? this._instance.isLocalConnected : "Unknown";
    }

    isStructInit() {
        return this.getStruct()!=null;
    }

    getStruct() {
        return this._instance ? this._instance.struct : null;
    }

    fetchStruct(retryOnInit = false) {
        if (!this._isInit)
            if (!retryOnInit)
                throw "Can't fetch object's struct because object '" + this.getId() + "' not yet initialized.";
            else {
                this._fetchStructOnInit = true;
                return;
            }

        const url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathStruct;
        this.execGetAction(url,

            function onSuccess(thiz,struct) {
                thiz._instance.struct = new APIStructure(thiz._jcpJSLWB,thiz,struct);
                
                thiz._emitOnObjStructUpd(thiz._jcpJSLWB,thiz);
            },

            function onError(thiz) {
                thiz._onError(thiz._jcpJSLWB, thiz, "can't fetch object's structure");
            }
            
        );
    }

    getComponent(compPath) {
        if (!this._isInit)
            throw "Can't browse object's structure because object '" + this.getId() + "' not yet initialized.";
        if (!this.isStructInit())
            throw "Can't browse object's structure because object '" + this.getId() + "' not yet fetched structure's data.";

        return this._searchComponent(compPath,this.getStruct());
    }

    _searchComponent(compPath,component) {
        if (component.getComponentPath() === compPath)
            return component;
        
        if (component.getType() === "Container")
            for (let subComp of component.getSubComponents()) {
                const s = this._searchComponent(compPath,subComp);
                if (s!=null) return s;
            }

        return null;
    }

    getPerms() {
        return this._instance ? this._instance.perms : null;
    }

    fetchPerms(retryOnInit = false) {
        if (!this._isInit)
            if (!retryOnInit)
                throw "Can't fetch object's permissions because object '" + this.getId() + "' not yet initialized."
            else {
                this._fetchPermsOnInit = true;
                return;
            }

        const url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathPerms;
        this.execGetAction(url,

            function onSuccess(thiz,perms) {
                perms.forEach(function(perm, idx, perms) {
                    perms[idx] = new APIPermission(thiz._jcpJSLWB, perm);
                });
                thiz._instance.perms = perms;
                
                thiz._emitOnObjPermsUpd(thiz._jcpJSLWB,thiz);
            },

            function onError(thiz) {
                thiz._onError(thiz._jcpJSLWB, thiz, "can't fetch object's permissions");
            }
            
        );
    }

    fetchEvents(onSuccess,onError) {
        if (!this._isInit)
            throw "Can't fetch object's events because object '" + this.getId() + "' not yet initialized.";

        const onSuccessParam = onSuccess;
        const onErrorParam = onError;
        const url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathEvents;
        this.execGetAction(url,

            function onSuccess(thiz,events) {
                onSuccessParam(thiz,events);
            },

            function onError(thiz, xhttp, error) {
                onErrorParam(thiz, xhttp, error);
            }
            
        );
    }

    toJSON() {
        var json = new ObjectJavascript();
        ObjectJavascript.assign(json,this._instance);
        json.struct = json.struct.toJSON();
        return json;
    }


    // Internal setters

    _setIsConnected(isConnected) {
        if (this._instance.isConnected == isConnected)
            return;
        
        this._instance.isConnected = isConnected;
        this._instance.isCloudConnected = isConnected;
        if (this._instance.isConnected)
            this._emitOnConnected(this._jcpJSLWB,this);
        else
            this._emitOnDisconnected(this._jcpJSLWB,this);
    }
    
    _setObjPermSrv(objId,perm,oldPerm) {
        // if (obj.isFetching)
        //     { console.log("Can't update object's service permission because it's fetching " + objId); return;}
        this._instance.permission = perm;
        this._emitOnObjPermSrvUpd(this._jcpJSLWB,this,perm,oldPerm);
    }

    _setObjInfo(objId,key,value,old) {
        // Values of keys from SSEUpdater::_processObjUpdate()
        // if (obj.isFetching)
        //     { console.log("Can't update " + key + " because it's fetching " + objId); return;}
        if (key === "Name")
            this._instance.name = value;
        else if (key === "Owner")
            this._instance.owner = value;
        else if (key === "JODVersion")
            this._instance.jodVersion = value;
        else if (key === "Model")
            this._instance.model = value;
        else if (key === "Brand") {
            if (this._instance.struct)
                this._instance.struct.brand = value;
            else
                this._instance.struct = { brand: value};
        } else if (key === "LongDescr") {
            if (this._instance.struct)
                this._instance.struct.longDescr = value;
            else
                this._instance.struct = { longDescr: value};
        }

        this._emitOnObjInfoUpd(this._jcpJSLWB,this,key,value,old);
    }


    // Actions

    setName(name, onSuccess, onError) {
        var url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathSetName;
        url += '?new_name=' + encodeURIComponent(name);
        this.execPostAction(url,onSuccess,onError);
    }

    setOwner(owner, onSuccess, onError) {
        var url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathSetOwner;
        url += '?new_owner=' + encodeURIComponent(owner);
        this.execPostAction(url,onSuccess,onError);
    }

    createPermission(srvId, usrId, permType, connType, onSuccess, onError) {
        var url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathPermsAdd;
        url += '?srv_id=' + encodeURIComponent(typeof srvId === "object" ? srvId.value : srvId);
        url += '&usr_id=' + encodeURIComponent(typeof usrId === "object" ? usrId.value : usrId);
        url += '&type=' + encodeURIComponent(permType);
        url += '&conn=' + encodeURIComponent(connType);
        this.execPostAction(url,onSuccess,onError);
    }


    // Events Object methods

    addOnObject(listener) {
        this._listenersOnObject.push(listener);
    }

    remOnObject(listener) {
        const removeIndex = this._listenersOnObject.findIndex(l => {
            return listener === l;
        });

        if (removeIndex !== -1) {
            this._listenersOnObject.splice(removeIndex, 1);
        }
    }

    _emitOnConnected(jcpJSLWB, thiz) {
        if (this._listenersOnObject.length > 0) {
            this._listenersOnObject.forEach(l => {l.onConnected(jcpJSLWB, thiz) });
        }
    }

    _emitOnDisconnected(jcpJSLWB, thiz) {
        if (this._listenersOnObject.length > 0) {
            this._listenersOnObject.forEach(l => {l.onDisconnected(jcpJSLWB, thiz) });
        }
    }

    _emitOnObjStructUpd(jcpJSLWB, thiz) {
        if (this._listenersOnObject.length > 0) {
            this._listenersOnObject.forEach(l => {l.onStructUpd(jcpJSLWB, thiz) });
        }
    }

    _emitOnObjPermsUpd(jcpJSLWB, thiz) {
        if (this._listenersOnObject.length > 0) {
            this._listenersOnObject.forEach(l => {l.onPermsUpd(jcpJSLWB, thiz) });
        }
    }

    _emitOnObjPermSrvUpd(jcpJSLWB, thiz, value, old) {
        if (this._listenersOnObject.length > 0) {
            this._listenersOnObject.forEach(l => {l.onPermSrvUpd(jcpJSLWB, thiz, value, old) });
        }
    }

    _emitOnObjInfoUpd(jcpJSLWB, thiz, key, value, old) {
        if (this._listenersOnObject.length > 0) {
            this._listenersOnObject.forEach(l => {l.onInfoUpd(jcpJSLWB, thiz, key, value, old) });
        }
    }

}


class GWsSSEListener {

    _owner = null

    constructor(object) {
        this._owner = object;
    }

    onConnecting(event) {}

    onWaiting(event) {}

    onConnected(event) {
        this._owner._setIsConnected(true);
    }

    onDisconnecting(event) {
        this._owner._setIsConnected(false);
    }

    onDisconnected(event) {
        this._owner._setIsConnected(false);
    }

    onError(event) {}
    
    onRem(objId) {}
    
}

class ObjectsSSEListener {

    _owner = null

    constructor(object) {
        this._owner = object;
    }
        
    onAdd(objId) {}
    
    onRem(objId) {}

    onConnected(objId) {
        if (this._owner.getId() === objId)
            this._owner._setIsConnected(true);
    }
    
    onDisconnected(objId) {
        if (this._owner.getId() === objId)
            this._owner._setIsConnected(false);
    }
    
}

class ObjectSSEListener {

    _owner = null

    constructor(object) {
        this._owner = object;
    }
        
    onStructUpd(objId) {
        if (this._owner.getId() === objId)
            try {
                this._owner.fetchStruct();
            } catch (e) {
                console.warn(e + " [IGNORED]");
            }
    }
        
    onPermsUpd(objId) {
        if (this._owner.getId() === objId)
            try {
                this._owner.fetchPerms();
            } catch (e) {
                console.warn(e + " [IGNORED]");
            }
    }
        
    onPermSrvUpd(objId, value, old) {
        if (this._owner.getId() === objId)
            this._owner._setObjPermSrv(objId,value,old);
    }
    
    // Component updates fowarded by ObjectComponentSSEListener
    onCompUpd(objId,compPath,value,old) {
        //this._owner...
    }
        
    onInfoUpd(objId,key,value,old) {
        if (this._owner.getId() === objId)
            this._owner._setObjInfo(objId,key,value,old);
    }
    
}