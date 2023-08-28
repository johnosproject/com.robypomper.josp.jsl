import APIRangeState from './APIRangeState'

export default class APIRangeAction extends APIRangeState {

    // Constructors

    constructor(jcpJSLWB, component) {
        super(jcpJSLWB,component);
    }


    // Actions

    setValue(value,onSuccess,onError) {
        const url = this._jcpJSLWB.getJSLWBUrl() + this._instance.pathSetValue + value;
        this.execGetAction(url,onSuccess,onError);
    }

    setTrue(onSuccess,onError) {
        const url = this._jcpJSLWB.getJSLWBUrl() + this._instance._pathTrue;
        this.execGetAction(url,onSuccess,onError);
    }

    setFalse(onSuccess,onError) {
        const url = this._jcpJSLWB.getJSLWBUrl() + this._instance._pathFalse;
        this.execGetAction(url,onSuccess,onError);
    }

}