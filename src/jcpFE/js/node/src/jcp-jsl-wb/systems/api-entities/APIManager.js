import APIEntity from "./APIEntity";

export default class APIManager extends APIEntity {

    // Internal vars

    //...


    // Constructors

    constructor(jcpJSLWB, onInit, onError) {
        super(jcpJSLWB,jcpJSLWB.getJSLWBUrls().manager,null,null);      // fetch disabled (not yet implemented)

        // ToDo: implement fetch and event registration
        //caller.get api this._url

        //if (this._jcpJSLWB.isSSEEnabled())
        //    this._jcpJSLWB.registerManagerListener();
    }

};