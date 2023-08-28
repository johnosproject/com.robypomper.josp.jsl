import Caller from '../jcp-commons/Caller'
import Log from '../jcp-commons/Logging'

import JSLSession from './systems/services/JSLSession'
import SSEUpdater from './systems/services/SSEUpdater'
import GWConnection from './systems/services/GWConnection'
import APIUser from './systems/api-entities/APIUser'
import APIService from './systems/api-entities/APIService'
import APIObjects from './systems/api-entities/APIObjects'
import APIManager from './systems/api-entities/APIManager'


export class JCPJSLWBClass {
    
    StateEnum = Object.freeze({
        NO_URL:"NO_URL",
        NO_JSL:"NO_JSL",
        INIT:"INITIALIZING",
        READY:"READY"
    });
    API_VERSION = '2.0';
    API_FE_ENTRY_POINT = '/apis/pub/frontend/entrypoint/2.0/entrypoint';
        API_FE_INIT_SESSION = '/../apis/pub/frontend/entrypoint/2.0/jslwbsession';
        API_JSLWB_INIT_STATUS = '/apis/pub/jslwebbridge/core/init/2.0/status';
    API_JSLWB_INIT_SSE = '/apis/pub/jslwebbridge/core/init/2.0/sse';
    API_JSLWB_INIT_SESSION = '/apis/pub/jslwebbridge/core/init/2.0/jsl';
    API_JSLWB_USER = '/apis/pub/jslwebbridge/core/user/2.0/';
    API_JSLWB_USER_LOGIN = '/apis/pub/jslwebbridge/core/user/2.0/login/';
    API_JSLWB_USER_LOGOUT = '/apis/pub/jslwebbridge/core/user/2.0/logout/';
    API_JSLWB_USER_REGISTRATION = '/apis/pub/jslwebbridge/core/user/2.0/registration/';
    API_JSLWB_SERVICE = '/apis/pub/jslwebbridge/core/service/2.0/';
    API_JSLWB_OBJECTS = '/apis/pub/jslwebbridge/core/objects/2.0/';
    API_JSLWB_OBJECT = '/apis/pub/jslwebbridge/core/objects/2.0/{obj_id}';
    API_JSLWB_COMPONENT = '/apis/pub/jslwebbridge/core/objects/2.0/{obj_id}/history/{comp_path}';
    
    API_ADMIN_APIS_STATUS = '/apis/pub/jslwebbridge/admin/apis/status/2.0/';
    API_ADMIN_GATEWAYS_STATUS_LIST = '/apis/pub/jslwebbridge/admin/gateways/status/2.0/all';
    API_ADMIN_GATEWAYS_STATUS = '/apis/pub/jslwebbridge/admin/gateways/status/2.0/{gwServerId}/';
    API_ADMIN_JSLWEBBRDIGE_STATUS = '/apis/pub/jslwebbridge/admin/jslwebbridge/status/2.0/';
    API_ADMIN_FRONTEND_STATUS = '/apis/pub/jslwebbridge/admin/frontend/status/2.0/';
    API_ADMIN_EXECUTABLE = '/apis/pub/jslwebbridge/admin/{service}/exec/2.0/';
    API_ADMIN_EXECUTABLE_GW = '/apis/pub/jslwebbridge/admin/{service}/exec/2.0/{gwServerId}/';
    API_ADMIN_BUILDINFO = '/apis/pub/jslwebbridge/admin/{service}/buildinfo/2.0/';
    API_ADMIN_BUILDINFO_GW = '/apis/pub/jslwebbridge/admin/{service}/buildinfo/2.0/{gwServerId}/';

    _urlJSLWB = null;
    _jslSession = null;
    _sseUpdater = null;
    _gwConnection = null;
    _user = null;
    _service = null;
    _objects = null;
    _manager = null;

    _isSSEEnabled = true;
    _logStart_sseUpdater = false;
    _logStart_gwConnection = false;
    _logStart_user = false;
    _logStart_service = false;
    _logStart_service = false;
    _logStart_manager = false;
    _listenersOnStateChanged = [];
    _lastStateEmitted = null;


    // Constructors

    constructor(urlJSLWB = null) {
        this._initUrls(urlJSLWB);
    }


    // JCP JSL WB state management

    getState() {
        if (!this.isUrlInit())
            return this.StateEnum.NO_URL;

        if (!this.isJSLInit())
            return this.StateEnum.NO_JSL;

        if ((this.isSSEEnabled() && !this.isSSEConnected())
            || !this.isUserInit()
            || !this.isObjectsInit())
            return this.StateEnum.INIT;

        return this.StateEnum.READY;
    }

    addOnStateChanged(listener) {
        this._listenersOnStateChanged.push(listener);
    }

    removeOnStateChanged(listener) {
        const removeIndex = this._listenersOnStateChanged.findIndex(l => {
            return listener === l;
        });

        if (removeIndex !== -1) {
            this._listenersOnStateChanged.splice(removeIndex, 1);
        }
    }

    _tryEmitOnStateChanged() {
        const state = this.getState();
        if (this._lastStateEmitted==state)
            return;

        this._lastStateEmitted = state;
        if (this._listenersOnStateChanged.length > 0) {
            this._listenersOnStateChanged.forEach(l => l.onStateChanged(state));
        }
    }


    // 1 Init URLs                                                              _initUrls()
    // 1.1      Set JSL WB url (via params)             --->_initJSL()
    // 1.1      Get JSL WB url (to FE)                  ---(onResponse)
    // ---(onResponse)
    // 1.1      Set JSL WB url (via FE call)            --->_initJSL()
    // (on error)---> _errorOnInitUrls(msg)
    // 1.error  {message}
    _initUrls(urlJSLWB) {
        this._logStartup("1","Init URLs");

        if (urlJSLWB) {
            this._logStartup("1.1","Set JSL WB url (via params)","--->_initJSL()");
            this._urlJSLWB = urlJSLWB;
            this._tryEmitOnStateChanged();
            this._initJSL();
            return;
        }
        
        this._logStartup("1.1","Get JSL WB url (to FE)","---(onResponse)");
        Caller.apiGET(this, this.API_FE_ENTRY_POINT,
                function processResponse(thiz,responseText) {
                    try {
                        thiz._logStartup("1.1","Set JSL WB url (via FE call)","--->_initJSL()");
                        thiz._urlJSLWB = responseText;
                        thiz._tryEmitOnStateChanged();
                        thiz._initJSL();

                    } catch (err) {
                        console.error(err);
                        thiz._errorOnInitUrls("Invalid response on JSL WB url request");
                    }
                },
                function processError(thiz) {
                    thiz._errorOnInitUrls("Error on JSL WB url request");
                }
            );
        
    }

    _errorOnInitUrls(msg) {
        this._errStartup("1.error",msg);
    }

    // 2 Init JSL                                                                               _initJSL()
    // 2.1      Check & update JSL Session              ?--->_startJSLSystems()
    // 2.2      Init JSL Session (via FE)               ---(onResponse)
    // ---(onResponse)
    // 2.2      Update JSL Session                      --->_startJSLSystems()
    // ---(onResponse.error)
    // 2.3      Init JSL Session (via JSL WB)           ---(onResponse)
    // ---(onResponse)
    // 2.3      Update JSL Session                      --->_startJSLSystems()
    // (on error)---> _errorOnInitJSL(msg)
    // 2.error  {message}
    _initJSL() {
        if (!this.isUrlInit())
            throw "JCPFE can\'t init because urls are not initialized";

        this._logStartup("2","Init JSL");
        this._jslSession = new JSLSession(this);
        
        this._jslSession.check(

            function onInit(jcpJSLWB, jslSession) {
                jcpJSLWB._logStartup("2.1","Check & update JSL Session","?--->_startJSLSystems()");
                jcpJSLWB._startJSLSystems();
                jcpJSLWB._tryEmitOnStateChanged();
            },
            
            function onNotInit(jcpJSLWB, jslSession) {
                jcpJSLWB._logStartup("2.2","Init JSL Session (via FE)","---(onResponse)");
                jcpJSLWB._jslSession.init(
            
                    function onSuccess(jcpJSLWB, jslSession) {
                        jcpJSLWB._logStartup("2.2","Update JSL Session","--->_startJSLSystems()");
                        jcpJSLWB._startJSLSystems();
                        jcpJSLWB._tryEmitOnStateChanged();
                    },
            
                    function onError(jcpJSLWB, jslSession) {
                        jcpJSLWB._logStartup("2.3","Init JSL Session (via JSL WB)","---(onResponse)");
                        jcpJSLWB._jslSession.initDirect(
            
                            function onSuccess(jcpJSLWB, jslSession) {
                                jcpJSLWB._logStartup("2.3","update JSL Session","--->_startJSLSystems()");
                                jcpJSLWB._startJSLSystems();
                                jcpJSLWB._tryEmitOnStateChanged();
                            },
            
                            function onError(jcpJSLWB, jslSession) {
                                setTimeout(jcpJSLWB._initJSL, 5000);
                                jcpJSLWB._errorOnInitJSL('Direct JSL Web Bridge session\'s initialization failed, retry in 5 seconds');
                                jcpJSLWB._tryEmitOnStateChanged();
                            }
            
                        )
                    }
            
                )
            },
            
            function onError(jcpJSLWB, jslSession) {
                jcpJSLWB._tryEmitOnStateChanged();
                jcpJSLWB._errorOnInitJSL('JSL Web Bridge session\'s status failed, retry in within 5 seconds');
                setTimeout(jcpJSLWB._refreshJSLSession, 5000, jcpJSLWB);
            }
        )
    }

    _refreshJSLSession(thiz = this) {
        const sessionId = thiz._jslSession ? thiz._jslSession.getId() : "None";
        thiz._log("Check & update JSL Session (" + sessionId + ")");
        thiz._jslSession.check(
            function onInit(jcpJSLWB, jslSession) {
                jcpJSLWB._log("JSL Session is up (" + sessionId + ")");
                jcpJSLWB._startJSLSystems();
                jcpJSLWB._tryEmitOnStateChanged();
            },
            
            function onNotInit(jcpJSLWB, jslSession) {
                jcpJSLWB._log("JSL Session is down (" + sessionId + ")");
                jcpJSLWB._initJSL();
                jcpJSLWB._tryEmitOnStateChanged();
            },
            
            function onError(jcpJSLWB, jslSession) {
                jcpJSLWB._tryEmitOnStateChanged();
                jcpJSLWB._errorOnInitJSL("JSL Web Bridge session  (" + sessionId + ") status failed, retry in 30 seconds");
                setTimeout(jcpJSLWB._refreshJSLSession, 30000, jcpJSLWB);
            }

        );
    }

    _errorOnInitJSL(msg) {
        this._errStartup("2.error",msg);
    }

    _startJSLSystems() {
        this._logStartup("3","Init JSL Systems");

        this._logStartup("3.1","Init SSE Updater");
        this._sseUpdater = new SSEUpdater(this,
            function onConnection(jcpJSLWB, sseUpdater) {
                if (!jcpJSLWB._logStart_sseUpdater) {
                    jcpJSLWB._logStartup("3.1","SSE Updater connected");
                    jcpJSLWB._logStart_sseUpdater = true;
                } else {
                    jcpJSLWB._log("SSE Updater connected");
                }
                jcpJSLWB._tryEmitOnStateChanged();
            },
            function onDisconnection(jcpJSLWB, sseUpdater) {
                jcpJSLWB._log("SSE Updater disconnected");
                jcpJSLWB._tryEmitOnStateChanged();
                jcpJSLWB._refreshJSLSession();
            },
            function onError(jcpJSLWB, sseUpdater) {
                jcpJSLWB._err("SSE Updater error");
                jcpJSLWB._tryEmitOnStateChanged();
                jcpJSLWB._refreshJSLSession();   // SSE disconnection can be caused by JCP JSL WB disconnection
            }
        );
        this._logStartup("3.2","Init GW Connection");
        this._gwConnection = new GWConnection(this,
            function onConnection(jcpJSLWB, gwConnection) {
                if (!jcpJSLWB._logStart_gwConnection) {
                    jcpJSLWB._logStartup("3.2","GW Connection connected");
                    jcpJSLWB._logStart_gwConnection = true;
                } else {
                    jcpJSLWB._log("GW Connection connected");
                }
                jcpJSLWB._tryEmitOnStateChanged();
            },
            function onDisconnection(jcpJSLWB, gwConnection) {
                jcpJSLWB._log("GW Connection disconnected");
                jcpJSLWB._tryEmitOnStateChanged();
            },
            function onError(jcpJSLWB, gwConnection) {
                jcpJSLWB._err("GW Connection error");
                jcpJSLWB._tryEmitOnStateChanged();
            }
        );
        this._logStartup("3.3","Init User");
        this._user = new APIUser(this,
            function onInit(jcpJSLWB, user) {
                if (!jcpJSLWB._logStart_user) {
                    jcpJSLWB._logStartup("3.3","User initialized");
                    jcpJSLWB._logStart_user = true;
                } else {
                    jcpJSLWB._log("User initialized");
                }
                jcpJSLWB._tryEmitOnStateChanged();
            },
            function onError(jcpJSLWB, user, error) {
                jcpJSLWB._err("Error on user: " + error);
                jcpJSLWB._tryEmitOnStateChanged();
            }
        );
        this._logStartup("3.4","Init Service");
        this._service = new APIService(this,
            function onInit(jcpJSLWB, service) {
                if (!jcpJSLWB._logStart_service) {
                    jcpJSLWB._logStartup("3.4","Service initialized");
                    jcpJSLWB._logStart_service = true;
                } else {
                    jcpJSLWB._log("Service initialized");
                }
                jcpJSLWB._tryEmitOnStateChanged();
            },
            function onError(jcpJSLWB, service, error) {
                jcpJSLWB._err("Error on service: " + error);
                jcpJSLWB._tryEmitOnStateChanged();
            }
        );
        this._logStartup("3.5","Init Objects");
        this._objects = new APIObjects(this,
            function onInit(jcpJSLWB, objects) {
                if (!jcpJSLWB._logStart_objects) {
                    jcpJSLWB._logStartup("3.5","Objects initialized");
                    jcpJSLWB._logStart_objects = true;
                } else {
                    jcpJSLWB._log("Objects initialized");
                }
                jcpJSLWB._tryEmitOnStateChanged();
            },
            function onError(jcpJSLWB, objects, error) {
                jcpJSLWB._err("Error on objects: " + error);
                jcpJSLWB._tryEmitOnStateChanged();
            }
        );
        this._logStartup("3.6","Init Manager");
        this._manager = new APIManager(this,
            function onInit(jcpJSLWB, manager) {
                if (!jcpJSLWB._logStart_manager) {
                    jcpJSLWB._logStartup("3.6","Manager initialized");
                    jcpJSLWB._logStart_manager = true;
                } else {
                    jcpJSLWB._log("Manager initialized");
                }
                jcpJSLWB._tryEmitOnStateChanged();
            },
            function onError(jcpJSLWB, manager) {
                jcpJSLWB._err("Manager error");
                jcpJSLWB._tryEmitOnStateChanged();
            }
        );

        this._logStartup("3","Start JSL Systems");
        this._gwConnection.start();

        this._sseUpdater.start();
    }


    // Url JSL WB

    isUrlInit() {
        return this._urlJSLWB != null;
    }

    getJSLWBUrl() {
        return this._urlJSLWB;
    }

    getJSLWBUrls() {
        return {
            sseUpdater: this.getJSLWBUrl() + this.API_JSLWB_INIT_SSE,
            user: this.getJSLWBUrl() + this.API_JSLWB_USER,
            userLogin: this.getJSLWBUrl() + this.API_JSLWB_USER_LOGIN + '?auto_redirect=true&redirect_uri=' + location.href,
            userLogout: this.getJSLWBUrl() + this.API_JSLWB_USER_LOGOUT + '?auto_redirect=true&redirect_uri=' + location.href,
            userRegistration: this.getJSLWBUrl() + this.API_JSLWB_USER_REGISTRATION + '?auto_redirect=true&redirect_uri=' + location.href,
            service: this.getJSLWBUrl() + this.API_JSLWB_SERVICE,
            objects: this.getJSLWBUrl() + this.API_JSLWB_OBJECTS,
            object: (objId) => this.getJSLWBUrl() + this.API_JSLWB_OBJECT.replace("{obj_id}",objId),
            component: (objId,compPath) => this.getJSLWBUrl() + this.API_JSLWB_COMPONENT.replace("{obj_id}",objId).replace("{comp_path}",encodeURIComponent(compPath)),
            manager: this.getJSLWBUrl() + this.API_JSLWB_MANAGER,
        };
    }

    getJSLWBAdminUrls() {
        return {
            apisStatus: this.getJSLWBUrl() + this.API_ADMIN_APIS_STATUS,
            gatewaysStatusList: this.getJSLWBUrl() + this.API_ADMIN_GATEWAYS_STATUS_LIST,
            gatewaysStatus: (gwServerId) => this.getJSLWBUrl() + this.API_ADMIN_GATEWAYS_STATUS.replace("{gwServerId}",gwServerId),
            jslWebBridgeStatus: this.getJSLWBUrl() + this.API_ADMIN_JSLWEBBRDIGE_STATUS,
            frontendStatus: this.getJSLWBUrl() + this.API_ADMIN_FRONTEND_STATUS,
            statusExecutable: (service) => this.getJSLWBUrl() + this.API_ADMIN_EXECUTABLE.replace("{service}",service),
            statusExecutableGateways: (service, gwServerId) => this.getJSLWBUrl() + this.API_ADMIN_EXECUTABLE_GW.replace("{service}",service).replace("{gwServerId}",gwServerId),
            statusBuildInfo: (service) => this.getJSLWBUrl() + this.API_ADMIN_BUILDINFO.replace("{service}",service),
            statusBuildInfoGateways: (service, gwServerId) => this.getJSLWBUrl() + this.API_ADMIN_BUILDINFO_GW.replace("{service}",service).replace("{gwServerId}",gwServerId),
        };
    }

    
    // JSL Session

    isJSLInit() {
        return this._jslSession != null && this._jslSession.isInit();
    }


    // SSE Updater

    isSSEEnabled() {
        return this._isSSEEnabled;
    }

    isSSEConnected() {
        return this._isSSEEnabled && (this._sseUpdater != null && this._sseUpdater.isConnected());
    }

    getSSEUpdater() {
        return this._sseUpdater;
    }


    // GW Connection

    isGWConnected() {
        return (this._gwConnection != null && this._gwConnection.isConnected());
    }

    getGWConnection() {
        return this._gwConnection;
    }


    // User

    isUserInit() {
        return this._user != null && this._user.isInit();
    }

    getUser() {
        return this.isUserInit() ? this._user : null;
    }


    // Service

    isServiceInit() {
        return this._service != null && this._service.isInit();
    }

    getService() {
        return this.isServiceInit() ? this._service : null;
    }


    // Objects

    isObjectsInit() {
        return this._objects != null && this._objects.isInit();
    }

    getObjects() {
        return this.isObjectsInit() ? this._objects : null;
    }


    // Manager

    isManagerInit() {
        return this._manager != null && this._manager.isInit();
    }

    getManager() {
        return this.isManagerInit() ? this._manager : null;
    }


    // Logs

    _debug(msg) {
        Log.debug("JCPJSLWB", msg);
    }

    _log(msg) {
        Log.log("JCPJSLWB", msg);
    }

    _warn(msg) {
        Log.warn("JCPJSLWB", msg);
    }

    _err(msg) {
        Log.error("JCPJSLWB", msg);
    }

    _debugStartup(code, msg) {
        Log.debug("JCPJSLWB/Startup", code + " " +msg);
    }

    _logStartup(code, msg) {
        Log.log("JCPJSLWB/StartUp", code + " " + msg);
    }

    _warnStartup(code, msg) {
        Log.warn("JCPJSLWB/StartUp", code + " " + msg);
    }

    _errStartup(code, msg) {
        Log.error("JCPJSLWB/StartUp", code + " " + msg);
    }

}
