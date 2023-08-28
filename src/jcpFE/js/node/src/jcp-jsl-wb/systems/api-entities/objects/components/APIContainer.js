import APIComponent from './APIComponent'

export default class APIContainer extends APIComponent {

    // Constructors

    constructor(jcpJSLWB, component) {
        super(jcpJSLWB,component);
    }


    // Getters

    getSubComponents() {
        return this._instance ? this._instance.subComps : [];
    }


    // // Actions

    // switch(onSuccess,onError) {
    //     const url = this._jcpJSLWB.getJSLWBUrl() + this._pathSwitch;
    //     this.execGetAction(url,onSuccess,onError);
    // }

    toJSON() {
        var json = new Object();
        Object.assign(json,this._instance);
        json.subComps.forEach(function(subComp, index, subComps) {
            subComps[index] = subComp.toJSON();
        });
        return json;
    }

}