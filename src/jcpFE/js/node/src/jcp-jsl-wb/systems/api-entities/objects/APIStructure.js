import APIBooleanState from './components/APIBooleanState'
import APIRangeState from './components/APIRangeState'
import APIBooleanAction from './components/APIBooleanAction'
import APIRangeAction from './components/APIRangeAction'
import APIContainer from './components/APIContainer';

function enrichObjectStructure(jcpJSLWB, component) {
    if (component.type=="BooleanState")
        return new APIBooleanState(jcpJSLWB, component);
    
    if (component.type=="RangeState")
        return new APIRangeState(jcpJSLWB, component);

    if (component.type=="BooleanAction")
        return new APIBooleanAction(jcpJSLWB, component);
    
    if (component.type=="RangeAction")
        return new APIRangeAction(jcpJSLWB, component);

    if (component.type=="Container") {
        if (component.componentPath==="") {
            component.subComps.forEach(function(component, idx, objPerms) {
                objPerms[idx] = enrichObjectStructure(jcpJSLWB, component);
            });
        } else {
            component = new APIContainer(jcpJSLWB, component);
            component.getSubComponents().forEach(function(component, idx, objPerms) {
                objPerms[idx] = enrichObjectStructure(jcpJSLWB, component);
            });
        }
    }
        
    return component;
}

export default class APIStructure extends APIContainer {

    _object = null;

    constructor(jcpJSLWB,object,struct) {
        super(jcpJSLWB,enrichObjectStructure(jcpJSLWB, struct));
        this._object = object;
    }

}