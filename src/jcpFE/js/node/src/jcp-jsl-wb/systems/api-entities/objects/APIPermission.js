import APIEntity from '../APIEntity'

export default class Permission extends APIEntity {

    // Internal vars

    _instance = null;

    // Constructors

    constructor(jcpJSLWB, perm) {
        super(jcpJSLWB,null,null,null);     // No fetch required
        this._instance = perm;
        this._isInit = true;
    }


    // Getters

    getInstance() {
        return this._instance;
    }

    getObjId() {
        return this._instance ? this._instance.objId : "N/A";
    }

    getId() {
        return this._instance ? this._instance.id : "N/A";
    }

    getSrvId() {
        return this._instance ? this._instance.srvId : "N/A";
    }

    getUsrId() {
        return this._instance ? this._instance.usrId : "N/A";
    }

    getConnectionType() {
        return this._instance ? this._instance.connection : "N/A";
    }

    getPermissionType() {
        return this._instance ? this._instance.type : "N/A";
    }

    getLastUpdate() {
        return this._instance ? this._instance.lastUpdate : "N/A";
    }

    toJSON() {
        var json = new Object();
        Object.assign(json,this._instance);
        return json;
    }


    // Actions

    duplicate(onSuccess,onError) {
        const url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathDup;
        this.execGetAction(url,onSuccess,onError);
    }

    delete(onSuccess,onError) {
        const url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathDel;
        this.execGetAction(url,onSuccess,onError);
    }

    update(onSuccess,onError) {
        console.log("Sono ancora qui " + this._pathUpd);
        // ...
        const url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathUpd;
        //this.execGetAction(url,onSuccess,onError);
    }

}