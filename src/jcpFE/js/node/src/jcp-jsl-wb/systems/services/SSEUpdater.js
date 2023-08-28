import Updater from '../../../jcp-commons/Updater'
import Log from '../../../jcp-commons/Logging'

/**
 * SSEUpdater class provide a realtime updates from JCP JSL Web Bridge api and
 * local JCP JSL Web bridge systems (GWConnection, User, Objects...).
 * 
 * The start() method connect a ServerSendEvent object to JCP JSL Web Bridge,
 * then received message are converted in events. This events are listend
 * only from JCPJSLWB's services.
 * 
 * No external (to jcp-jsl-wb package) can access to SSEUpdater events (excepts
 * for jcp-fe package).
 */
export default class SSEUpdater extends Updater {

    // Internal vars

    _jcpJSLWB = null;
    _isConnected = false;
    _onConnection = null;
    _onDisconnection = null;
    _onError = null;
    _listenersOnConnected = [];
    _listenersAPIs = [];
    _listenersGWs = [];
    _listenersObjs = [];
    _listenersObj = [];


    // Constructors

    constructor(jcpJSLWB, onConnection, onDisconnection, onError) {
        super();
        this._jcpJSLWB = jcpJSLWB;
        this._onConnection = onConnection;
        this._onDisconnection = onDisconnection;
        this._onError = onError;
    }


    // Connected

    isConnected() {
        return this._isConnected;
    }

    _emitOnConnected_System(jcpJSLWB, thiz) {
        if (this._isConnected == true)
            return;
        this._isConnected = true;
        this._onConnection(jcpJSLWB, thiz);
    }

    _emitOnDisconnected_System(jcpJSLWB, thiz) {
        if (this._isConnected == false)
            return;
        this._isConnected = false;
        this._onDisconnection(jcpJSLWB, thiz);
    }

    start() {
        const url = this._jcpJSLWB.getJSLWBUrls().sseUpdater;
        super.startUpdater(url);
    }

    stop(thiz = this) {
        thiz.evtSource.close()
        thiz.evtSource = null;
        thiz._emitOnDisconnected_System(thiz._jcpJSLWB,thiz);
        thiz._emitOnDisconnected();
    }


    // Internal SSE event listeners

    onMessage(thiz,event) {
        var data = event.data;
        if (data.startsWith("\"")) data = data.substring(1)
        if (data.endsWith("\"")) data = data.substring(0,data.length-1);
        data = data.replaceAll("\\","");

        if (data.startsWith("HB")) {
            thiz._debugSSE("Received 'HB'");
            // N/A
            return;

        }

        var event = JSON.parse(data);
        var processed = false;
        if (event.what.startsWith("JCP_APIS")) {
            processed = thiz._processJCPAPIs(event);
        } else if (event.what.startsWith("JCP_GWS")) {
            processed = thiz._processJCPGWs(event);
        } else if (event.what.startsWith("OBJ_UPD")) {
            processed = thiz._processObjUpdate(event);
        } else if (event.what.startsWith("OBJ")) {
            processed = thiz._processObj(event);
        }

        //} else if (data.startsWith("csrf:")) {
        //    var token = data.substring("csrf:".length,data.indexOf(";"));
        //    var header = data.substring(data.lastIndexOf("header:")+"header:".length,data.length);
        //    setCsrf(token,header);
        //
        //} else if (data.startsWith("cookie:")) {
        //    var value = data.substring("cookie:".length,data.indexOf(";"));
        //    setCookie(value);

        if (!processed)
            thiz._warnSSE("UNKNOW DATA (" + data + ")");
    }

    onOpen(thiz,event) {
        this._logSSE("Connected");
        this._emitOnConnected_System(thiz._jcpJSLWB,thiz);
        this._emitOnConnected();
    }

    onError(thiz,event) {
        thiz._logSSE("Disconnected");
        thiz.stop(thiz);
    }


    // Message processing methods

    _processJCPAPIs(event) {
        if (event.what == "JCP_APIS_CONN") {
            this._logSSE("Received 'JCP_APIS_CONN' on " + event.url);
            // event.url
            this._emitOnAPIsConnected(event);
            return true;

        } else if (event.what == "JCP_APIS_DISCONN") {
            this._logSSE("Received 'JCP_APIS_DISCONN' on " + event.url);
            // event.url
            this._emitOnAPIsDisconnected(event);
            return true;

        } else if (event.what == "JCP_APIS_FAIL_GEN") {
            this._warnSSE("Received 'JCP_APIS_FAIL_GEN' on " + event.url + "(" + event.error + ")");
            // event.url
            // event.error
            this._emitOnAPIsError(event);
            return true;

        } else if (event.what == "JCP_APIS_FAIL_AUTH") {
            this._warnSSE("Received 'JCP_APIS_FAIL_AUTH' on " + event.url);
            // event.url
            // event.error
            this._emitOnAPIsError(event);
            return true;

        } else if (event.what == "JCP_APIS_LOGIN") {
            this._logSSE("Received 'JCP_APIS_LOGIN' for user " + event.username);
            // event.url
            // event.userid
            // event.username
            this._emitOnAPIsLogin(event);
            return true;

        } else if (event.what == "JCP_APIS_LOGOUT") {
            this._logSSE("Received 'JCP_APIS_LOGOUT'");
            // event.url
            this._emitOnAPIsLogout(event);
            return true;

        }

        return false;
    }

    _processJCPGWs(event) {
        if (event.what == "JCP_GWS_CONNECTING") {
            this._debugSSE("Received 'JCP_GWS_CONNECTING' on " + event.proto + "://" + event.host + ":" + event.port);
            // event.proto
            // event.host
            // event.port
            this._emitOnGWConnecting(event);
            return true;

        } else if (event.what == "JCP_GWS_WAITING") {
            this._logSSE("Received 'JCP_GWS_WAITING' on " + event.proto + "://" + event.host + ":" + event.port);
            // event.proto
            // event.host
            // event.port
            this._emitOnGWWaiting(event);
            return true;

        } else if (event.what == "JCP_GWS_CONNECTED") {
            this._logSSE("Received 'JCP_GWS_CONNECTED' on " + event.proto + "://" + event.host + ":" + event.port);
            // event.proto
            // event.host
            // event.port
            this._emitOnGWConnected(event);
            return true;

        } else if (event.what == "JCP_GWS_DISCONNECTING") {
            this._debugSSE("Received 'JCP_GWS_DISCONNECTING' on " + event.proto + "://" + event.host + ":" + event.port);
            // event.proto
            // event.host
            // event.port
            this._emitOnGWDisconnecting(event);
            return true;

        } else if (event.what == "JCP_GWS_DISCONNECTED") {
            this._logSSE("Received 'JCP_GWS_DISCONNECTED' on " + event.proto + "://" + event.host + ":" + event.port);
            // event.proto
            // event.host
            // event.port
            this._emitOnGWDisconnected(event);
            return true;

        } else if (event.what == "JCP_GWS_FAIL") {
            this._warnSSE("Received 'JCP_GWS_FAIL' on " + event.proto + "://" + event.host + ":" + event.port + " [" + event.error + "]");
            // event.proto
            // event.host
            // event.port
            // event.error
            this._emitOnGWError(event);
            return true;
        }

        return false;
    }

    _processObj(event) {
        if (event.what == "OBJ_ADD") {
            this._logSSE("Received 'OBJ_ADD' on " + event.objId);
            // event.objId
            this._emitOnObjsAdd(event.objId);
            return true;

        } else if (event.what == "OBJ_REM") {
            this._logSSE("Received 'OBJ_REM' on " + event.objId);
            // event.objId
            this._emitOnObjsRem(event.objId);
            return true;

        } else if (event.what == "OBJ_CONN") {
            this._logSSE("Received 'OBJ_CONN' on " + event.objId);
            // event.objId
            this._emitOnObjsConnected(event.objId);
            return true;

        } else if (event.what == "OBJ_DISCONN") {
            this._logSSE("Received 'OBJ_DISCONN' on " + event.objId);
            // event.objId
            this._emitOnObjsDisconnected(event.objId);
            return true;
        }

        return false;
    }

    _processObjUpdate(event) {
        if (event.what == "OBJ_UPD_STRUCT") {
            this._logSSE("Received 'OBJ_UPD_STRUCT' on " + event.objId);
            // event.objId
            this._emitOnObjStructUpd(event.objId);
            return true;

        } else if (event.what == "OBJ_UPD_PERMS") {
            this._logSSE("Received 'OBJ_UPD_PERMS' on " + event.objId);
            // event.objId
            this._emitOnObjPermsUpd(event.objId);
            return true;

        } else if (event.what == "OBJ_UPD_PERM_SRV") {
            this._logSSE("Received 'OBJ_UPD_PERM_SRV' on " + event.objId + " value = " + event.new);
            // event.objId
            // event.new
            // event.old
            this._emitOnObjPermSrvUpd(event.objId,event.new,event.old);
            return true;

        } else if (event.what == "OBJ_UPD_COMP") {
            this._debugSSE("Received 'OBJ_UPD_COMP' on " + event.objId + " for " + event.compPath + " value = " + event.new);
            // event.objId
            // event.compPath
            // event.new
            // event.old
            this._emitOnObjCompUpd(event.objId,event.compPath,event.new,event.old);
            return true;

        } else if (event.what == "OBJ_UPD_INFO_NAME") {
            this._debugSSE("Received 'OBJ_UPD_INFO_NAME' on " + event.objId + " value = " + event.new);
            // event.objId
            // event.new
            // event.old
            this._emitOnObjInfoUpd(event.objId,"Name",event.new,event.old);
            return true;

        } else if (event.what == "OBJ_UPD_INFO_OWNER") {
            this._debugSSE("Received 'OBJ_UPD_INFO_OWNER' on " + event.objId + " value = " + event.new);
            // event.objId
            // event.new
            // event.old
            this._emitOnObjInfoUpd(event.objId,"Owner",event.new,event.old);
            return true;

        } else if (event.what == "OBJ_UPD_INFO_JOD_VERSION") {
            this._debugSSE("Received 'OBJ_UPD_INFO_JOD_VERSION' on " + event.objId + " value = " + event.new);
            // event.objId
            // event.new
            // event.old
            this._emitOnObjInfoUpd(event.objId,"JODVersion",event.new,event.old);
            return true;

        } else if (event.what == "OBJ_UPD_INFO_MODEL") {
            this._debugSSE("Received 'OBJ_UPD_INFO_MODEL' on " + event.objId + " value = " + event.new);
            // event.objId
            // event.new
            // event.old
            this._emitOnObjInfoUpd(event.objId,"Model",event.new,event.old);
            return true;

        } else if (event.what == "OBJ_UPD_INFO_BRAND") {
            this._debugSSE("Received 'OBJ_UPD_INFO_BRAND' on " + event.objId + " value = " + event.new);
            // event.objId
            // event.new
            // event.old
            this._emitOnObjInfoUpd(event.objId,"Brand",event.new,event.old);
            return true;

        } else if (event.what == "OBJ_UPD_INFO_LONG_DESCR") {
            this._debugSSE("Received 'OBJ_UPD_INFO_LONG_DESCR' on " + event.objId + " value = " + event.new);
            // event.objId
            // event.new
            // event.old
            this._emitOnObjInfoUpd(event.objId,"LongDescr",event.new,event.old);
            return true;
        }

        return false;
    }


    // Events Connected methods

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

    _emitOnConnected() {
        if (this._listenersOnConnected.length > 0) {
            this._listenersOnConnected.forEach(l => l.onConnected());
        }
    }

    _emitOnDisconnected() {
        if (this._listenersOnConnected.length > 0) {
            this._listenersOnConnected.forEach(l => l.onDisconnected());
        }
    }


    // Events APIs methods

    addOnAPIsEvents(listener) {
        this._listenersAPIs.push(listener);
    }

    removeOnAPIsEvents(listener) {
        const removeIndex = this._listenersAPIs.findIndex(l => {
            return listener === l;
        });

        if (removeIndex !== -1) {
            this._listenersAPIs.splice(removeIndex, 1);
        }
    }

    _emitOnAPIsConnected(event) {
        if (this._listenersAPIs.length > 0) {
            this._listenersAPIs.forEach(l => l.onConnected(event));
        }
    }

    _emitOnAPIsDisconnected(event) {
        if (this._listenersAPIs.length > 0) {
            this._listenersAPIs.forEach(l => l.onDisconnected(event));
        }
    }

    _emitOnAPIsError(event) {
        if (this._listenersAPIs.length > 0) {
            this._listenersAPIs.forEach(l => l.onError(event));
        }
    }

    _emitOnAPIsLogin(event) {
        if (this._listenersAPIs.length > 0) {
            this._listenersAPIs.forEach(l => l.onLogin(event));
        }
    }

    _emitOnAPIsLogout(event) {
        if (this._listenersAPIs.length > 0) {
            this._listenersAPIs.forEach(l => l.onLogout(event));
        }
    }


    // Events GW methods

    addOnGWEvents(listener) {
        this._listenersGWs.push(listener);
    }

    removeOnGWEvents(listener) {
        const removeIndex = this._listenersGWs.findIndex(l => {
            return listener === l;
        });

        if (removeIndex !== -1) {
            this._listenersGWs.splice(removeIndex, 1);
        }
    }

    _emitOnGWConnecting(event) {
        if (this._listenersGWs.length > 0) {
            this._listenersGWs.forEach(l => l.onConnecting(event));
        }
    }

    _emitOnGWWaiting(event) {
        if (this._listenersGWs.length > 0) {
            this._listenersGWs.forEach(l => l.onWaiting(event));
        }
    }

    _emitOnGWConnected(event) {
        if (this._listenersGWs.length > 0) {
            this._listenersGWs.forEach(l => l.onConnected(event));
        }
    }

    _emitOnGWDisconnecting(event) {
        if (this._listenersGWs.length > 0) {
            this._listenersGWs.forEach(l => l.onDisconnecting(event));
        }
    }

    _emitOnGWDisconnected(event) {
        if (this._listenersGWs.length > 0) {
            this._listenersGWs.forEach(l => l.onDisconnected(event));
        }
    }

    _emitOnGWError(event) {
        if (this._listenersGWs.length > 0) {
            this._listenersGWs.forEach(l => l.onError(event));
        }
    }


    // Events Objects methods

    addOnObjsEvents(listener) {
        this._listenersObjs.push(listener);
    }

    removeOnObjsEvents(listener) {
        const removeIndex = this._listenersObjs.findIndex(l => {
            return listener === l;
        });

        if (removeIndex !== -1) {
            this._listenersObjs.splice(removeIndex, 1);
        }
    }

    _emitOnObjsAdd(objId) {
        if (this._listenersObjs.length > 0) {
            this._listenersObjs.forEach(l => l.onAdd(objId));
        }
    }

    _emitOnObjsRem(objId) {
        if (this._listenersObjs.length > 0) {
            this._listenersObjs.forEach(l => l.onRem(objId));
        }
    }

    _emitOnObjsConnected(objId) {
        if (this._listenersObjs.length > 0) {
            this._listenersObjs.forEach(l => l.onConnected(objId));
        }
    }

    _emitOnObjsDisconnected(objId) {
        if (this._listenersObjs.length > 0) {
            this._listenersObjs.forEach(l => l.onDisconnected(objId));
        }
    }


    // Events Object methods

    addOnObjEvents(listener) {
        this._listenersObj.push(listener);
    }

    removeOnObjEvents(listener) {
        const removeIndex = this._listenersObj.findIndex(l => {
            return listener === l;
        });

        if (removeIndex !== -1) {
            this._listenersObj.splice(removeIndex, 1);
        }
    }

    _emitOnObjStructUpd(objId) {
        if (this._listenersObj.length > 0) {
            this._listenersObj.forEach(l => l.onStructUpd(objId));
        }
    }

    _emitOnObjPermsUpd(objId) {
        if (this._listenersObj.length > 0) {
            this._listenersObj.forEach(l => l.onPermsUpd(objId));
        }
    }

    _emitOnObjPermSrvUpd(objId,value,old) {
        if (this._listenersObj.length > 0) {
            this._listenersObj.forEach(l => l.onPermSrvUpd(objId,value,old));
        }
    }

    _emitOnObjCompUpd(objId,compPath,value,old) {
        if (this._listenersObj.length > 0) {
            this._listenersObj.forEach(l => l.onCompUpd(objId,compPath,value,old));
        }
    }

    _emitOnObjInfoUpd(objId,key,value,old) {
        if (this._listenersObj.length > 0) {
            this._listenersObj.forEach(l => l.onInfoUpd(objId,key,value,old));
        }
    }

    
    // Log

    _debugSSE(msg) {
        Log.debug("JCPJSLWB/SSE", msg);
    }

    _logSSE(msg) {
        Log.log("JCPJSLWB/SSE", msg);
    }

    _warnSSE(msg) {
        Log.warn("JCPJSLWB/SSE", msg);
    }

    _errJSL(msg) {
        Log.error("JCPJSLWB/SSE", msg);
    }

}
