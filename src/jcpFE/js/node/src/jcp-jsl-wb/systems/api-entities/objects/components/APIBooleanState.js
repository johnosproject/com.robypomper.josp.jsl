import APIComponent from './APIComponent'

export default class APIBooleanAction extends APIComponent {

    _listenersOnComponentUpdate = [];


    // Constructors

    constructor(jcpJSLWB, component) {
        super(jcpJSLWB,component);
    }


    // Getters

    getState() {
        return this._instance ? this._instance.state : "N/A";
    }
    
}