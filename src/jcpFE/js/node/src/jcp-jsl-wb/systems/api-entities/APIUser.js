import APIEntity from './APIEntity';

export default class User extends APIEntity {

    // Internal vars

    _userSSEListener = null;
    _listenersOnUser = [];
    _instance = null;


    // Constructors

    constructor(jcpJSLWB, onInit, onError) {
        super(jcpJSLWB,jcpJSLWB.getJSLWBUrls().user,onInit,onError);
        this._userSSEListener = new UserSSEListener(this);

        if (this._jcpJSLWB.isSSEEnabled())
            this._jcpJSLWB.getSSEUpdater().addOnAPIsEvents(this._userSSEListener);
        // else
        //     ToDo schedule interval on _fetch()
    }


    // Initialization

    setInstance(fetchedInstance) {
        this._instance = fetchedInstance;
    }


    // Getters

    getId() {
        return this._instance ? this._instance.id : "N/A";
    }

    getName() {
        return this._instance ? this._instance.name : "N/A";
    }

    isAuthenticated() {
        return this._instance ? this._instance.isAuthenticated : false;
    }

    isAdmin() {
        return this._instance ? this._instance.isAdmin : false;
    }

    isMaker() {
        return this._instance ? this._instance.isMaker : false;
    }

    isDeveloper() {
        return this._instance ? this._instance.isDeveloper : false;
    }


    // Events User methods

    addOnUser(listener) {
        this._listenersOnUser.push(listener);
    }

    removeOnUser(listener) {
        const removeIndex = this._listenersOnUser.findIndex(l => {
            return listener === l;
        });

        if (removeIndex !== -1) {
            this._listenersOnUser.splice(removeIndex, 1);
        }
    }

    _emitOnLogin(jcpJSLWB, thiz) {
        if (this._listenersOnUser.length > 0) {
            this._listenersOnUser.forEach(l => l.onLogin(jcpJSLWB, thiz));
        }
    }

    _emitOnLogout(jcpJSLWB, thiz) {
        if (this._listenersOnUser.length > 0) {
            this._listenersOnUser.forEach(l => l.onLogout(jcpJSLWB, thiz));
        }
    }

};

class UserSSEListener {

    _owner = null

    constructor(gwConnection) {
        this._owner = gwConnection;
    }

    onConnected(event) {}
    
    onDisconnected(event) {}
    
    onError(event) {}
    
    onLogin(event) {
        this._owner._emitOnLogin(this._owner._jcpJSLWB,this._owner);
    }
    
    onLogout(event) {
        this._owner._emitOnLogout(this._owner._jcpJSLWB,this._owner);
    }
    
}
