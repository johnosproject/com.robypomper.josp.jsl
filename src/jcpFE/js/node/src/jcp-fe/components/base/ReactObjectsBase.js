import React from "react";
import Container from '@material-ui/core/Container';
import Typography from '@material-ui/core/Typography';

export default class ReactObjectsBase extends React.Component {
    
    jcpFE = null;

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;

        if (this.jcpFE.getObjects()==null)
            throw "Can't use ReactObjectsBase component before initialize JCP JSL Web Bridge's 'objects' system'";
        
        this._onObjects = new ReactObjectsBase_OnObjects(this);
    }

    componentDidMount() {
        this.jcpFE.getObjects().addOnObjects(this._onObjects);
    }

    componentWillUnmount() {
        this.jcpFE.getObjects().remOnObjects(this._onObjects);
    }

    renderObjectsNotInit() {
        return (
            <Container>
                <Typography variant="body2" component="p">
                    Objects not yet initialized. Waiting...
                </Typography>
            </Container>
        );
    }

}

class ReactObjectsBase_OnObjects {
        
    _owner = null;

    constructor(jcpJSLWBStatus) {
        this._owner = jcpJSLWBStatus;
    }

    onAdd(jcpJSLWB, objects, objId) {
        if (this._owner.onObjectAdded)
            this._owner.onObjectAdded(jcpJSLWB, objects, objId);
    }

    onRem(jcpJSLWB, objects, objId) {
        if (this._owner.onObjectRemoved)
            this._owner.onObjectRemoved(jcpJSLWB, objects, objId);
    }

    onConnected(jcpJSLWB, objects, objId) {
        if (this._owner.onObjectConnected)
            this._owner.onObjectConnected(jcpJSLWB, objects, objId);
    }

    onDisconnected(jcpJSLWB, objects, objId) {
        if (this._owner.onObjectDisconnected)
            this._owner.onObjectDisconnected(jcpJSLWB, objects, objId);
    }

}
