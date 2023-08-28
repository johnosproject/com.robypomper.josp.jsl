import APIEntity from '../../APIEntity'

export default class Component extends APIEntity {

    // Internal vars

    _instance = null;
    _listenersOnComponent = [];


    // Constructors

    constructor(jcpJSLWB, component) {
        super(jcpJSLWB,component.pathSingle,null,null);
        this._objectSSEListener = new ObjectSSEListener(this);
        this._instance = component;
        this._isInit = true;

        if (this._jcpJSLWB.isSSEEnabled()) {
            this._jcpJSLWB.getSSEUpdater().addOnObjEvents(this._objectSSEListener);
        }
        // else
        //     ToDo schedule interval on _fetch()
    }


    // Getters

    getName() {
        return this._instance ? this._instance.name : "N/A";
    }

    getDescription() {
        return this._instance ? this._instance.description : "N/A";
    }

    getObjectId() {
        return this._instance ? this._instance.objId : "N/A";
    }

    getType() {
        return this._instance ? this._instance.type : "N/A";
    }

    getComponentPath() {
        return this._instance ? this._instance.componentPath : "N/A";
    }

    toJSON() {
        var json = new Object();
        Object.assign(json,this._instance);
        return json;
    }


    // Internal setters
    
    _setStatus(status,oldStatus) {
        if (typeof this._instance.state === 'boolean')
            this._instance.state = status === "true";
        else if (typeof this._instance.state === 'number')
            this._instance.state = Number(status);
        else
            this._instance.state = status;
        this._emitOnStatusUpdate(this._jcpJSLWB,this,status,oldStatus);
    }


    // Events Component methods

    addOnComponent(listener) {
        this._listenersOnComponent.push(listener);
    }

    remOnComponent(listener) {
        const removeIndex = this._listenersOnComponent.findIndex(l => {
            return listener === l;
        });

        if (removeIndex !== -1) {
            this._listenersOnComponent.splice(removeIndex, 1);
        }
    }

    _emitOnStatusUpdate(jcpJSLWB, thiz, status, oldStatus) {
        if (this._listenersOnComponent.length > 0) {
            this._listenersOnComponent.forEach(l => {l.onStatusUpdate(jcpJSLWB, thiz, status, oldStatus) });
        }
    }

    fetchHistory(onSuccess,onError) {
        if (!this._isInit)
            throw "Can't fetch component's history because component '" + this.getObjectId() + "/" + this.getComponentPath() + "' not yet initialized.";

        if (this.getType() == "Container")
        throw "Can't fetch component's history because component '" + this.getObjectId() + "/" + this.getComponentPath() + "' is a 'container'.";

        const onSuccessParam = onSuccess;
        const onErrorParam = onError;
        const url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathHistory;
        this.execGetAction(url,

            function onSuccess(thiz,events) {
                onSuccessParam(thiz,events);
            },

            function onError(thiz) {
                onErrorParam(thiz);
            }
            
        );
    }

}

class ObjectSSEListener {

    _owner = null

    constructor(object) {
        this._owner = object;
    }
        
    onStructUpd(objId) {}
        
    onPermsUpd(objId) {}
        
    onPermSrvUpd(objId, value, old) {
    }
    
    // Component updates fowarded by ObjectComponentSSEListener
    onCompUpd(objId,compPath,value,old) {
        if (this._owner.getObjectId() === objId
        && this._owner.getComponentPath() === compPath)
            this._owner._setStatus(value,old);
    }
        
    onInfoUpd(objId,key,value,old) {}

}