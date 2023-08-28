import Caller from '../../../jcp-commons/Caller'
import Log from '../../../jcp-commons/Logging'
import { JCPJSLWBClass } from '../../JCPJSLWB';

export default class GWConnection {

    // Internal vars

    _jcpJSLWB = null;
    _isConnected = false;
    _onConnection = null;
    _onDisconnection = null;
    _onError = null;
    _gwSSEListener = null;
    _listenersOnConnected = [];


    // Constructors

    constructor(jcpJSLWB, onConnection, onDisconnection, onError) {
        this._jcpJSLWB = jcpJSLWB;
        this._onConnection = onConnection;
        this._onDisconnection = onDisconnection;
        this._onError = onError;
        this._gwSSEListener = new GWSSEListener(this);
    }


    // Connected

    /**
     * @returns true if the gateway connection on current JSL Session is connected.
     */
    isConnected() {
        return this._isConnected;
    }

    /**
     * Start Gateways connection checks.
     * 
     * If SSEUpdater is enabled, then this method register to his events.
     */
    start() {
        this._requestCheck();

        if (this._jcpJSLWB.isSSEEnabled())
            this._jcpJSLWB.getSSEUpdater().addOnGWEvents(this._gwSSEListener);
        // else
        //     ToDo schedule interval on _requestCheck()
    }

    /**
     * Stop Gateways connection checks.
     * 
     * If SSEUpdater is enabled, then this method deregister to his events.
     */
    stop() {
        if (this._jcpJSLWB.isSSEEnabled())
            this._jcpJSLWB.getSSEUpdater().removeOnGWEvents(this._gwSSEListener);
        // else
        //     ToDo stop scheudled interval on _requestCheck()
    }

    // GW Connection
    
    /**
     * Send a JSL Session status request and execute correspondent param.
     * 
     * This method update also the JSL Session init state. When this method
     * receive the response from the status call, it update the inist status.
     * Also when an error occurs, this method set the init status to false.
     */
    _requestCheck() {
        const url = this._jcpJSLWB.getJSLWBUrls().service
        Caller.apiGET(this, url,

             function processResponse(thiz,responseText) {
                const isConnected = JSON.parse(responseText).isCloudConnected;
                thiz._debugGW("GW Connection isConnected: " + isConnected);
                if (isConnected)
                    thiz._emitOnConnected(thiz._jcpJSLWB, thiz);
                else
                    thiz._emitOnDisconnected(thiz._jcpJSLWB, thiz);
             },

            function processError(thiz) {
                thiz._onError(thiz._jcpJSLWB, thiz);
            }
         );
    }


    // Events GW Connection methods

    addOnConnected(listener) {
        this._listenersOnConnected.push(listener);
    }

    removeOnConnected(listener) {
        const removeIndex = this._listenersOnConnected.findIndex(l => {
            return listener === l;
        });

        if (removeIndex !== -1) {
            this._listenersOnConnected.splice(removeIndex, 1);
        }
    }

    /**
     * Emited on GW Connection.
     * 
     * @param {JCPJSLWBClass} jcpJSLWB 
     * @param {GWConnection} thiz 
     */
    _emitOnConnected(jcpJSLWB, thiz) {
        if (this._isConnected == true)
            return;
        this._isConnected = true;
        this._onConnection(jcpJSLWB, thiz);
        if (this._listenersOnConnected.length > 0) {
            this._listenersOnConnected.forEach(l => l.onConnected(jcpJSLWB, thiz));
        }
    }


    /**
     * Emited on GW Disconnection.
     * 
     * @param {JCPJSLWBClass} jcpJSLWB 
     * @param {GWConnection} thiz 
     */
    _emitOnDisconnected(jcpJSLWB, thiz) {
        if (this._isConnected == false)
            return;
        this._isConnected = false;
        this._onDisconnection(jcpJSLWB, thiz);
        if (this._listenersOnConnected.length > 0) {
            this._listenersOnConnected.forEach(l => l.onDisconnected(jcpJSLWB, thiz));
        }
    }


    // Log

    _debugGW(msg) {
        Log.debug("JCPJSLWB/GW", msg);
    }

    _logGW(msg) {
        Log.log("JCPJSLWB/GW", msg);
    }

    _warnGW(msg) {
        Log.warn("JCPJSLWB/GW", msg);
    }

    _errGW(msg) {
        Log.error("JCPJSLWB/GW", msg);
    }

};


class GWSSEListener {

    _owner = null

    constructor(gwConnection) {
        this._owner = gwConnection;
    }

    onConnecting(event) {}
    
    onWaiting(event) {}
    
    onConnected(event) {
        this._owner._emitOnConnected(this._owner._jcpJSLWB, this._owner);
    }
    
    onDisconnecting(event) {}
    
    onDisconnected(event) {
        this._owner._emitOnDisconnected(this._owner._jcpJSLWB, this._owner)
    }
    
    onError(event) {
        this._owner._onError(this._owner._jcpJSLWB, this._owner);
    }

}
