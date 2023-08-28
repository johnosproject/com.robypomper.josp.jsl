import APIEntity from './APIEntity';

export default class Service extends APIEntity {

    // Internal vars

    _serviceSSEListener = null;
    _listenersOnService = [];
    _instance = null;


    // Constructors

    constructor(jcpJSLWB, onInit, onError) {
        super(jcpJSLWB,jcpJSLWB.getJSLWBUrls().service,onInit,onError);
        this._serviceSSEListener = new ServiceSSEListener(this);

        if (this._jcpJSLWB.isSSEEnabled())
            this._jcpJSLWB.getSSEUpdater().addOnAPIsEvents(this._serviceSSEListener);
        // else
        //     ToDo schedule interval on _fetch()
    }

    
    // Initialization

    setInstance(fetchedInstance) {
        this._instance = fetchedInstance;
    }
    

    // Getters

    getName() {
        return this._instance ? this._instance.name : "N/A";
    }

    getSrvId() {
        return this._instance ? this._instance.srvId : "N/A";
    }

    getUsrId() {
        return this._instance ? this._instance.usrId : "N/A";
    }

    getInstId() {
        return this._instance ? this._instance.instId : "N/A";
    }

    getSessionId() {
        return this._instance ? this._instance.sessionId : "N/A";
    }

    getJSLVersion() {
        return this._instance ? this._instance.jslVersion : "N/A";
    }

    isJCPConnected() {
        return this._instance ? this._instance.isJCPConnected : false;
    }

    isCloudConnected() {
        return this._instance ? this._instance.isCloudConnected : false;
    }

    isLocalRunning() {
        return this._instance ? this._instance.isLocalRunning : false;
    }


    // Events Service methods

    addOnService(listener) {
        this._listenersOnService.push(listener);
    }

    removeOnService(listener) {
        const removeIndex = this._listenersOnService.findIndex(l => {
            return listener === l;
        });

        if (removeIndex !== -1) {
            this._listenersOnService.splice(removeIndex, 1);
        }
    }

    _emitOnXY(jcpJSLWB, thiz) {
        if (this._listenersOnService.length > 0) {
            this._listenersOnService.forEach(l => l.onXY(jcpJSLWB, thiz));
        }
    }

};



class ServiceSSEListener {

    _owner = null

    constructor(gwConnection) {
        this._owner = gwConnection;
    }

                    onConnected(event) {}
                    
                    onDisconnected(event) {}
                    
                    onError(event) {}
                    
                    onLogin(event) {}
                    
                    onLogout(event) {}
    
}