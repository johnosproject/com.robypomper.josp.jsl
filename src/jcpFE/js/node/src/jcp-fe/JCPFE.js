import { JCPJSLWBClass } from '../jcp-jsl-wb/JCPJSLWB'
import Log from '../jcp-commons/Logging'

export class JCPFEClass extends JCPJSLWBClass {

    _urlFE = null;
    _snackBar = null;
    _stats = null;
    
    constructor(urlFE = null, urlJSLWB = null) {
        super(urlJSLWB);
        this._urlFE = urlFE ? urlFE : location.origin;
        this._stats = new Stats(this);
    }


    // Url FE

    getFEUrl() {
        return this._urlFE;
    }


    // App's Snackbar

    isSnackBar() {
        return this._snackBar!=null;
    }

    registerSnackBar(snackBar) {
        this._snackBar = snackBar;
    }

    getSnackBar() {
        if (!this._snackBar)
            throw "SnackBar not set. Please add a Snackbar tag to DOM three (like 'JCPFESnackBarThemed') and register it with 'JCPFEClass.registerSnackBar(sb)'.";
        return this._snackBar;
    }


    // Stats

    getStats() {
        return this._stats;
    }
    


    // Logs

    _debug(msg) {
        Log.debug("JCPFE", msg);
    }

    _log(msg) {
        Log.log("JCPFE", msg);
    }

    _warn(msg) {
        Log.warn("JCPFE", msg);
    }

    _err(msg) {
        Log.error("JCPFE", msg);
    }

    _debugStats(msg) {
        Log.debug("JCPFE/Stats", msg);
    }

    _logStats(msg) {
        Log.log("JCPFE/Stats", msg);
    }

    _warnStats(msg) {
        Log.warn("JCPFE/Stats", msg);
    }

    _errStats(msg) {
        Log.error("JCPFE/Stats", msg);
    }
}


class Stats {

    jcpFE = null;

    constructor(JCPFEInstance) {
        this.jcpFE = JCPFEInstance;

        this.onJCPJSLWBStateChanged = new Stats_OnJCPJSLWBStateChanged(this);
        this.onObjects = new Stats_OnObjects(this);
        this.onObject = new Stats_OnObject(this);
        this.onComponent = new Stats_OnComponent(this);

        this.jcpFE.addOnStateChanged(this.onJCPJSLWBStateChanged);
        this.jcpFE._logStats("Stats init and registered to JCPJSLWB");
    }


    registerOnObjects() {
        this.jcpFE._debugStats("Register onObjects (" + this.jcpFE.getObjects().getList().length + " objects to register)");
        this.jcpFE.getObjects().addOnObjects(this.onObjects);

        for (const obj of this.jcpFE.getObjects().getList())
        this.registerOnObject(obj.getId());

        // Processing
        this._maxObjectCount = this.jcpFE.getObjects().getList().length;
    }

    deregisterOnObjects() {
        const count = this.jcpFE.getObjects() ? this.jcpFE.getObjects().getList().length : "?";
        this.jcpFE._debugStats("Deregister onObjects (" + count + " objects to deregister)");
        if (this.jcpFE.getObjects()) {
            this.jcpFE.getObjects().remOnObjects(this.onObjects);

            for (const obj of this.jcpFE.getObjects().getList())
                this.deregisterOnObject(obj.getId());
        }
    }

    registerOnObject(objId) {
        this.jcpFE._debugStats("Register onObject (" + objId + " )");
        const object = this.jcpFE.getObjects().getById(objId);
        object.addOnObject(this.onObject);
        if (object.getStruct()==null)
            object.fetchStruct(true);
        else
            this.registerOnComponent(object.getStruct());
    }

    deregisterOnObject(objId) {
        this.jcpFE._debugStats("Deregister onObject (" + objId + " )");
        const object = this.jcpFE.getObjects().getById(objId);
        if (object) {
            object.remOnObject(this.onObject);
            this.deregisterOnComponent(object.getStruct());
        }
    }

    updateOnComponent(object) {
        object.getStruct()
        this.deregisterOnComponent(object.getStruct());
        this.registerOnComponent(object.getStruct());
    }

    registerOnComponent(component) {
        this.jcpFE._debugStats("Register onComponent (" + component.getObjectId() + " > " + (component.getComponentPath() === "" ? "Root" : component.getComponentPath()) + ")");
        component.addOnComponent(this.onComponent);
        if (component.getType() === "Container")
            for (const comp of component.getSubComponents())
                this.registerOnComponent(comp);
    }

    deregisterOnComponent(component) {
        if (!component) {
            this.jcpFE._warnStats("Deregister onComponent (warning component==undefined)");
            return;
        }

        this.jcpFE._debugStats("Deregister onComponent (" + component.getObjectId() + " > " + (component.getComponentPath() === "" ? "Root" : component.getComponentPath()) + ")");
        component.remOnComponent(this.onComponent);
        if (component.getType() === "Container")
            for (const comp of component.getSubComponents())
                this.deregisterOnComponent(comp);
    }


    // Objects count

    _maxObjectCount = 0;

    getObjectsCount() {
        return this.jcpFE.getObjects().getList().length;
    }

    getMaxObjectsCount() {
        return this._maxObjectCount;
    }


    // Objects Activities

    _objectActivitiesLastId = 0;
    _objectActivitiesList = [{
        id: this._objectActivitiesLastId++,
        time: new Date().getTime(),
        objId: "N/A",
        objName: "N/A",
        objOwner: "N/A",
        type: "JCPFE_INIT",
        description: "JCP FE Client initialized"
    }];
    _listenersOnObjectActivities = [];

    getObjectsActivities() {
        return this._objectActivitiesList;
    }

    addOnObjectsActivities(listener) {
        this._listenersOnObjectActivities.push(listener);
    }

    remOnObjectsActivities(listener) {
        const removeIndex = this._listenersOnObjectActivities.findIndex(l => {
            return listener === l;
        });

        if (removeIndex !== -1) {
            this._listenersOnObjectActivities.splice(removeIndex, 1);
        }
    }

    _emitOnObjectActivitiesUpdate() {
        const lastActivity = this._objectActivitiesList[0];
        this.jcpFE._logStats("Update on ([" + lastActivity.type + " - " + lastActivity.objId + "] : " + lastActivity.description + ")");
        if (this._listenersOnObjectActivities.length > 0) {
            this._listenersOnObjectActivities.forEach(l => {l.onObjectsActivitiesUpdate(Array.from(this._objectActivitiesList)) });
        }
    }

    addObjectsActivity(newActivity) {
        this._objectActivitiesList = Object.assign([], this._objectActivitiesList);
        this._objectActivitiesList.unshift(newActivity)
        this._emitOnObjectActivitiesUpdate();
    }

    addObjectsActivityFromObjId(objId = null, actType, actDescription) {
        var actObjId = "";
        var actObjName = "";
        var actObjOwner = "";

        if (objId == null) {
          actObjId = "N/A";
          actObjName = "N/A";
          actObjOwner = "N/A";

        } else {
          const object = this.jcpFE.getObjects().getById(objId);
          if (object == null) {
            actObjId = objId;
            actObjName = "NotFound";
            actObjOwner = "NotFound";
          } else if (object.getOwner()=="00000-00000-00000") {
            actObjId = objId;
            actObjName = object.getName();
            actObjOwner = "Anonymous";
          } else {
            actObjId = objId;
            actObjName = object.getName();
            actObjOwner = object.getOwner();
          }
        }

        if (actObjOwner === "Anonymous")
          return;

        const newActivity = {
            id: this._objectActivitiesLastId++,
            time: new Date().getTime(),
            objId: actObjId,
            objName: actObjName,
            objOwner: actObjOwner,
            type: actType,
            description: actDescription,
        };
        this.addObjectsActivity(newActivity);
    }

}

class Stats_OnJCPJSLWBStateChanged {

    _owner = null;

    constructor(owner) {
        this._owner = owner;
    }

    onStateChanged(state) {
        // Events chain
        if (state === this._owner.jcpFE.StateEnum.READY)
            this._owner.registerOnObjects();
        else
            this._owner.deregisterOnObjects();
    }
    
}

class Stats_OnObjects {

    _owner = null;

    constructor(owner) {
        this._owner = owner;
    }

    onAdd(jcpJSLWB, objects, objId) {
        // Events chain
        this._owner.registerOnObject(objId);

        // Processing
        this._owner.addObjectsActivityFromObjId(objId,"OBJ_ADD","Added '" + objId + "' object");
        if (this._owner._maxObjectCount < this._owner.getObjectsCount())
            this._owner._maxObjectCount = this._owner.getObjectsCount();
    }

    onRem(jcpJSLWB, objects, objId) {
        // Events chain
        this._owner.deregisterOnObject(objId);

        // Processing
        this._owner.addObjectsActivityFromObjId(objId,"OBJ_REM","Removed '" + objId + "' object");
    }

    onConnected(jcpJSLWB, objects, objId) {
        // Processing
        this._owner.addObjectsActivityFromObjId(objId,"OBJ_CON","Connected '" + objId + "' object");
    }

    onDisconnected(jcpJSLWB, objects, objId) {
        // Processing
        this._owner.addObjectsActivityFromObjId(objId,"OBJ_DIS","Disconnected '" + objId + "' object");
    }
    
}

class Stats_OnObject {

    _owner = null;

    constructor(owner) {
        this._owner = owner;
    }

    onConnected(jcpJSLWB, object) {}

    onDisconnected(jcpJSLWB, object) {}

    onStructUpd(jcpJSLWB, object) {
        // Events chain
        this._owner.updateOnComponent(object);

        // Processing
        this._owner.addObjectsActivityFromObjId(object.getId(),"OBJ_STR","Updated '" + object.getId() + "' object structure");
    }

    onPermsUpd(jcpJSLWB, object) {
        // Processing
        this._owner.addObjectsActivityFromObjId(object.getId(),"OBJ_PRS","Updated '" + object.getId() + "' object permissions");
    }

    onPermSrvUpd(jcpJSLWB, object, value, old) {
        // Processing
        this._owner.addObjectsActivityFromObjId(object.getId(),"OBJ_SPR","Updated '" + object.getId() + "' object / service's permission = '" + value + "'");
    }

    onInfoUpd(jcpJSLWB, object, key, value, old) {
        // Processing
        this._owner.addObjectsActivityFromObjId(object.getId(),"OBJ_INF","Updated '" + object.getId() + "'  'object info's '" + key + "' = '" + value + "'");
    }
    
}

class Stats_OnComponent {

    _owner = null;

    constructor(owner) {
        this._owner = owner;
    }

    onStatusUpdate(jcpJSLWB, component, status, oldStatus) {
        // Processing
        this._owner.addObjectsActivityFromObjId(component.getObjectId(),"OBJ_CMP","Updated '" + component.getObjectId() + "' object '" + component.getComponentPath() + "' component's state = '" + status + "'");
    }
    
}