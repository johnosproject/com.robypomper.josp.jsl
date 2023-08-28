import Caller from '../../../jcp-commons/Caller'

export default class APIEntity {

    // Internal vars

    _jcpJSLWB = null;
    _isInit = false;
    _onInit = null;
    _onError = null;
    _url = null;


    // Constructors

    constructor(jcpJSLWB, url, onInit = null, onError = null) {
        this._jcpJSLWB = jcpJSLWB;
        this._onInit = onInit;
        this._onError = onError;
        this._url = url;

        if (onInit && onError)
            this._fetch();
    }


    // Initialization (first fetch)

    isInit() {
        return this._isInit;
    }

    _fetch() {
        const thiz = this;
        Caller.apiGET(thiz, this._url,
            
            function processResponse(thiz,responseText) {
                var response;
                try {
                    response = JSON.parse(responseText);

                } catch (e) {
                    thiz._onError(thiz._jcpJSLWB, thiz, "can't parse fetch response (" + e + ")");
                    return;
                }
                // try {
                    if (thiz._isInit == true)
                        return;
                    thiz._isInit = true;

                    thiz.setInstance(response);
                    thiz._onInit(thiz._jcpJSLWB, thiz);

                // } catch (e) {
                //     thiz._onError(thiz._jcpJSLWB, thiz, "can't store fetched response (" + e + ")");
                // }
            },

            function processError(thiz) {
                thiz._onError(thiz._jcpJSLWB, thiz, "can't fetch entity's data ()");
            }

        );
    }


    // API executors 

    execGetAction(url,onSuccess,onError) {
        const thiz = this;
        Caller.apiGET(thiz, url,
            
            function processResponse(thiz,responseText) {
                var responseParsed;
                try {
                    responseParsed = JSON.parse(responseText)
                } catch (error) {
                    responseParsed = responseText;
                }
                onSuccess(thiz,responseParsed);
            },

            function processError(thiz, xhttp, error) {
                var message;
                if (error)
                    message = "can't exec GET request because: " + error + "\n\ton url: " + url;
                else {
                    const response = JSON.parse(xhttp.response);
                    message = "can't exec GET request because:\n\t[" + response.status + "/" + response.error + "] " + response.message + "\n\ton url: " + url;
                }
                onError(thiz, xhttp, message);
            }

        );
    }

    execPostAction(url,onSuccess,onError,params) {
        const thiz = this;
        Caller.apiPOST(thiz, url,
            
            function processResponse(thiz,responseText) {
                onSuccess(thiz,JSON.parse(responseText));
            },

            function processError(thiz, xhttp, error) {
                var message;
                if (error)
                    message = "can't exec POST request because: " + error + "\n\ton url: " + url;
                else {
                    const response = JSON.parse(xhttp.response);
                    message = "can't exec POST request because:\n\t[" + response.status + "/" + response.error + "] " + response.message + "\n\ton url: " + url;
                }
                onError(thiz, xhttp, message);
            },

            params
        );
    }

}