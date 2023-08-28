import APIBooleanState from './APIBooleanState'

export default class APIBooleanAction extends APIBooleanState {

    // Constructors

    constructor(jcpJSLWB, component) {
        super(jcpJSLWB,component);
    }


    // Actions

    switch(onSuccess,onError) {
        const url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathSwitch;
        this.execGetAction(url,onSuccess,onError);
    }

    setTrue(onSuccess,onError) {
        const url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathTrue;
        this.execGetAction(url,onSuccess,onError);
    }

    setFalse(onSuccess,onError) {
        const url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathFalse;
        this.execGetAction(url,onSuccess,onError);
    }

}