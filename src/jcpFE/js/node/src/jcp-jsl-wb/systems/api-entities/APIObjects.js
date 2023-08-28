import APIEntity from './APIEntity';
import APIObject from './objects/APIObject';
import Log from '../../../jcp-commons/Logging'

export default class Objects extends APIEntity {

    // Internal vars

    _objectsSSEListener = null;
    _listenersOnObjects = [];
    _listenersOnObjectsMap = new Map();
    _instances = [];
    _retryAfterFetch_Struct = new Map();
    _retryAfterFetch_Perms = new Map();


    // Constructors

    constructor(jcpJSLWB, onInit, onError) {
        super(jcpJSLWB,jcpJSLWB.getJSLWBUrls().objects,onInit,onError);
        this._objectsSSEListener = new ObjectsSSEListener(this);

        if (this._jcpJSLWB.isSSEEnabled()) {
            this._jcpJSLWB.getSSEUpdater().addOnObjsEvents(this._objectsSSEListener);
        }
        // else
        //     ToDo schedule interval on _fetch()
    }

    
    // Initialization

    setInstance(fetchedInstance) {
        fetchedInstance.forEach( obj => this._handleListedObject(obj));
    }
    

    // Getters

    getById(objId) {
        const idx = this._instances.findIndex((obj) => obj.getId() === objId);
        if (idx==-1)
            return null;
            
        return this._instances[idx];
    }

    getList() {
        return this._instances ? this._instances.filter(x => x.getOwner() != '00000-00000-00000') : [];
    }
    

    _handleListedObject(object) {
        this._debugObjects("Added '" + object.id + "' object, fetch not required");
        const newObject = new APIObject(this._jcpJSLWB, object, null, null);
        this._addObj(newObject,false);
    }

    _handleAddedObject(objId) {
        this._debugObjects("Added '" + objId + "' object, fetching...");
        const object = {
            id: objId,
            pathSingle: this._jcpJSLWB.getJSLWBUrls().object(objId)
        }
        const thiz = this;
        const newObject = new APIObject(this._jcpJSLWB, object, 
            function onSuccess(jcpJSLWB, object) {
                thiz._debugObjects("Object '" + objId + "' fetched successfully");
                thiz._addObj(newObject,false);
            },
            function onError(jcpJSLWB, object, error) {
                thiz._errObjects("Error on object '" + objId + "': " + error);
            });
        
    }

    _addObj(obj, preventEvent = false) {
        this._instances.push(obj);
        if (!preventEvent) {
            this._logObjects("Object '" + obj.getId() + "' added and fetched successfully.");
            this._emitOnAdd(this._jcpJSLWB,this,obj.getId());
        }
    }

    _removeObj(objId, preventEvent = false) {
        const removeIndex = this._instances.findIndex(o => {
            return objId === o.getId();
        });

        if (removeIndex !== -1) {
            this._instances.splice(removeIndex, 1);
        }

        if (!preventEvent) {
            this._debugObjects("Object '" + objId + "' removed successfully.");
            this._emitOnRem(this._jcpJSLWB,this,objId);
        }
    }


    // Events Objects methods

    addOnObjects(listener) {
        this._listenersOnObjects.push(listener);
    }

    remOnObjects(listener) {
        const removeIndex = this._listenersOnObjects.findIndex(l => {
            return listener === l;
        });

        if (removeIndex !== -1) {
            this._listenersOnObjects.splice(removeIndex, 1);
        }
    }

    _emitOnAdd(jcpJSLWB, thiz, objId) {
        if (this._listenersOnObjects.length > 0) {
            this._listenersOnObjects.forEach(l => {l.onAdd(jcpJSLWB, thiz, objId)});
        }
    }

    _emitOnRem(jcpJSLWB, thiz, objId) {
        if (this._listenersOnObjects.length > 0) {
            this._listenersOnObjects.forEach(l => l.onRem(jcpJSLWB, thiz, objId));
        }
    }

    _emitOnConnected(jcpJSLWB, thiz, objId) {
        if (this._listenersOnObjects.length > 0) {
            this._listenersOnObjects.forEach(l => l.onConnected(jcpJSLWB, thiz, objId));
        }
    }

    _emitOnDisconnected(jcpJSLWB, thiz, objId) {
        if (this._listenersOnObjects.length > 0) {
            this._listenersOnObjects.forEach(l => l.onDisconnected(jcpJSLWB, thiz, objId));
        }
    }


    // Log

    _debugObjects(msg) {
        Log.debug("JCPJSLWB/Objects", msg);
    }

    _logObjects(msg) {
        Log.log("JCPJSLWB/Objects", msg);
    }

    _warnObjects(msg) {
        Log.warn("JCPJSLWB/Objects", msg);
    }

    _errObjects(msg) {
        Log.error("JCPJSLWB/Objects", msg);
    }

};


class ObjectsSSEListener {

    _owner = null

    constructor(objects) {
        this._owner = objects;
    }
        
    onAdd(objId) {
        this._owner._handleAddedObject(objId);
    }
    
    onRem(objId) {
        this._owner._removeObj(objId);
    }

    onConnected(objId) {}
    
    onDisconnected(objId) {}
    
}
