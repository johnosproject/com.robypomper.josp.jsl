import Caller from '../../../jcp-commons/Caller'
import Log from '../../../jcp-commons/Logging'

export default class JSLSession {

    // Class constants

    _jcpFE = 'jcp-fe';                                          // Fallback client id for development environment
    _jcpFESecret = 'f379a100-46bb-4fc8-a660-947a98a3afab';      // Fallback client id for development environment


    // Internal vars

    _jcpJSLWB = null;
    _isInit = false;
    _idJSLWBSession = null;


    // Constructors

    constructor(jcpJSLWB) {
        this._jcpJSLWB = jcpJSLWB;
    }


    // Initialized

    /**
     * @returns true if last check()'s response contained isInit value true. 
     */
    isInit() {
        return this._isInit;
    }

    /**
     * @returns the JSL Session's id.
     */
    getId() {
        return this._idJSLWBSession;
    }
    
    /**
     * Send a JSL Session status request and execute correspondent param.
     * 
     * This method update also the JSL Session init state. When this method
     * receive the response from the status call, it update the inist status.
     * Also when an error occurs, this method set the init status to false.
     */
    check(onInit, onNotInit, onError) {
        const thiz = this;
        Caller.apiGET(thiz, thiz._jcpJSLWB.getJSLWBUrl() + thiz._jcpJSLWB.API_JSLWB_INIT_STATUS,
            
            function processResponse(thiz,responseText) {
                const response = JSON.parse(responseText);
                thiz._idJSLWBSession = response.sessionId;
                thiz._isInit = response.isJSLInit;
                thiz._debugJSL("JSL Session status response: " + responseText);
                
                if (thiz._isInit)
                    onInit(thiz._jcpJSLWB, thiz);
                else
                    onNotInit(thiz._jcpJSLWB, thiz);
            },

            function processError(thiz) {
                thiz._isInit = false;
                onError(thiz._jcpJSLWB, thiz);
            }

        );
    }
    
    /**
     * Init JSL Session calling JCP Front End api.
     * 
     * Initialize JSL Session via the JCP FE api allow to initialize a JSL Session 
     * with right JSL client's id and secret.
     */
    init(onSuccess, onError) {
        if (this._isInit)
            throw 'JSL Session already init';

        const thiz = this;
        Caller.apiGET(thiz, thiz._jcpJSLWB.getFEUrl() + thiz._jcpJSLWB.API_FE_INIT_SESSION + '?session_id=' + this._idJSLWBSession,

            function processresponse(thiz,responseText) {
                thiz._isInit = responseText === 'true';
                thiz._debugJSL("JSL Session initialized succesffully");

                if (thiz._isInit)
                    onSuccess(thiz._jcpJSLWB, thiz);
                else
                    onError(thiz._jcpJSLWB, thiz);
            },
            
            function processError(thiz) {
                onError(thiz._jcpJSLWB, thiz);
            }

        );
    }
    
    /**
     * Init JSL Session calling JCP JSL Web Bridge api directly.
     * 
     * JSL Web Bridge require the client's id and secret to initialize a new JSL
     * session. So this method can be used only on development environment,
     * because it use class constants set to development JCP FE client.
     */
    initDirect(onSuccess, onError) {
        if (this._isInit)
            throw 'JSL Session already init';
            
        const thiz = this;
        Caller.apiGET(thiz, thiz._jcpJSLWB.getJSLWBUrl() + thiz._jcpJSLWB.API_JSLWB_INIT_SESSION + '?client_id=' + thiz._jcpFE + '&client_secret=' + thiz._jcpFESecret,

            function processresponse(thiz,responseText) {
                thiz._debugJSL("JSL Session DIRECT initialized succesffully");

                // responseText <= full service id (srvId/usrId/instId)
                thiz._isInit = true;
                onSuccess(thiz._jcpJSLWB, thiz);
            },
        
            function processError(thiz) {
                onError(thiz._jcpJSLWB, thiz);
            }

        );
    }


    // Log

    _debugJSL(msg) {
        Log.debug("JCPJSLWB/JSL", msg);
    }

    _logJSL(msg) {
        Log.log("JCPJSLWB/JSL", msg);
    }

    _warnJSL(msg) {
        Log.warn("JCPJSLWB/JSL", msg);
    }

    _errJSL(msg) {
        Log.error("JCPJSLWB/JSL", msg);
    }

}
