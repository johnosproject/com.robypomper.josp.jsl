class Caller {

    _send(xhttp) {
        xhttp.withCredentials = true;
        xhttp.send();
    }

    apiGETSync(url) {
        var xhttp = new XMLHttpRequest();
        try {
            xhttp.open("GET", url, false);
        } catch (e) {
            console.error("Error on opening GET (sync) request on url '" + url + "'");
            throw e;
        }

        try {
            this._send(xhttp);
        } catch (e) {
            console.error("Error on send GET (sync) request on url '" + url + "'");
            throw e;
        }
        return xhttp;
    }

    apiGET(thiz,url,processResponse,processError) {
        var xhttp = new XMLHttpRequest();
        var errorProcessed = false;
        xhttp.onreadystatechange = function() {
            if (xhttp.readyState != 4)
                return;
            if (xhttp.status == 200)
                processResponse(thiz,xhttp.responseText);
            else
                if (!errorProcessed) {
                    processError(thiz,xhttp);
                    errorProcessed = true;
                }
        };
        xhttp.onerror = function() {
            if (!errorProcessed) {
                processError(thiz,xhttp);
                errorProcessed = true;
            }
        };
        try {
            xhttp.open("GET", url, true);
        } catch (e) {
            console.error("Error on opening GET request on url '" + url + "'");
            processError(thiz,xhttp,e);
        }
            

        try {
            this._send(xhttp);
        } catch (e) {
            console.error("Error on send GET request on url '" + url + "'");
            processError(thiz,xhttp,e);
        }
    }

    apiPOST(thiz,url,processResponse,processError,params=null) {
        var xhttp = new XMLHttpRequest();
        var errorProcessed = false;
        xhttp.onreadystatechange = function() {
            if (xhttp.readyState != 4)
                return;
            if (xhttp.status == 200)
                processResponse(thiz,xhttp.responseText);
            else
                if (!errorProcessed) {
                    processError(thiz,xhttp);
                    errorProcessed = true;
                }

        };
        xhttp.onerror = function() {
            if (!errorProcessed) {
                processError(thiz,xhttp);
                errorProcessed = true;
            }
        };

        try {
            xhttp.open("POST", url, true);
        } catch (e) {
            console.error("Error on opening POST request on url '" + url + "'");
            processError(thiz,xhttp,e);
        }
        xhttp.withCredentials = true;

        var body = "";
        if (params!=null) {
            xhttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
            body += params;
        }

        try {
            xhttp.send(body);
        } catch (e) {
            console.error("Error on send POST request on url '" + url + "'");
            processError(thiz,xhttp, e);
        }
    }
}

export default new Caller();