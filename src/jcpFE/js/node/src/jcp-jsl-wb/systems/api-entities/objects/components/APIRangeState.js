import APIComponent from './APIComponent'

export default class APIRangeAction extends APIComponent {

    // Constructors

    constructor(jcpJSLWB, component) {
        super(jcpJSLWB,component);
    }


    // Getters

    getState() {
        return this._instance ? this._instance.state : "N/A";
    }

    getStep() {
        return this._instance ? this._instance.step : 1;
    }

    getMin() {
        return this._instance ? this._instance.min : 0;
    }

    getMax() {
        return this._instance ? this._instance.max : 100;
    }


    // // Actions

    // setValue(value,onSuccess,onError) {
    //     const url = this._jcpJSLWB.getJSLWBUrl() + this._pathSetValue + value;
    //     this.execGetAction(url,onSuccess,onError);
    // }

}