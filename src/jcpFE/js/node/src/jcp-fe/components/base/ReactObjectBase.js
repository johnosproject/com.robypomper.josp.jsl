import React from "react";
import Container from '@material-ui/core/Container';
import Typography from '@material-ui/core/Typography';

export default class ReactObjectBase extends React.Component {
    
    jcpFE = null;
    objId = null;
    object = null;
    added = false;

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
        this.objId = props.objId;

        if (!this.isObjectInit())
            console.warn("Can't use ReactObjectBase component before initialize JCP JSL Web Bridge's 'object' system'");
        
        this._onObject = new ReactObjectBase_OnObject(this);
        this._onObjects = new ReactObjectBase_OnObjects(this);
        this.state = {
            isInit: this.isObjectInit(),
            isConnected: this.isObjectConnected()
        }
    }

    componentDidMount() {
        if (this.isObjectInit())
            this.getObject().addOnObject(this._onObject);
        else
            this.jcpFE.getObjects().addOnObjects(this._onObjects);
    }

    componentWillUnmount() {
        if (this.isObjectInit())
            this.getObject().remOnObject(this._onObject);
        this.jcpFE.getObjects().remOnObjects(this._onObjects);
    }

    getObject() {
        if (!this.isObjectInit())
            return null;

        if (this.object==null)
            this.object = this.jcpFE.getObjects().getById(this.objId);
        
        return this.object;
    }

    isObjectInit() {
        return this.object!=null || this.jcpFE.getObjects().getById(this.objId) != undefined;
    }

    isObjectConnected() {
        const object = this.getObject();
        if (object==null)
            return false;
        return object.isConnected();
    }

    isObjectAllowed() {
        const object = this.getObject();
        if (object==null)
            return false;
        
        return object.getPermission() != 'None';
    }

    renderObjectNotInit() {
        return (
            <Container>
                <Typography variant="body2" component="p">
                    Object '{this.objId}' not found.
                </Typography>
            </Container>
        );
    }

    renderObjectNotAllowed() {
        return (
            <Container>
                <Typography variant="body2" component="p">
                    Current user can't access to Object '{this.objId}'.<br/>
                    Please require object's access to his owner '{this.getObject().getOwner()}'
                </Typography>
            </Container>
        );
    }

}

class ReactObjectBase_OnObject {
        
    _owner = null;

    constructor(reactBase) {
        this._owner = reactBase;
    }

    onConnected(jcpJSLWB, object) {
        if (this._owner.onConnected)
            this._owner.onConnected(jcpJSLWB, object);
        this._owner.setState({isConnected: object.isConnected()});
    }

    onDisconnected(jcpJSLWB, object) {
        if (this._owner.onDisconnected)
            this._owner.onDisconnected(jcpJSLWB, object);
        this._owner.setState({isConnected: object.isConnected()});
    }

    onStructUpd(jcpJSLWB, object) {
        if (this._owner.onStructUpd)
            this._owner.onStructUpd(jcpJSLWB, object);
    }

    onPermsUpd(jcpJSLWB, object) {
        if (this._owner.onPermsUpd)
            this._owner.onPermsUpd(jcpJSLWB, object);
    }

    onPermSrvUpd(jcpJSLWB, object, value, old) {
        if (this._owner.onPermSrvUpd)
            this._owner.onPermSrvUpd(jcpJSLWB, object, value, old);
    }

    onInfoUpd(jcpJSLWB, object, key, value, old) {
        if (this._owner.onInfoUpd)
            this._owner.onInfoUpd(jcpJSLWB, object, key, value, old);
    }

}

class ReactObjectBase_OnObjects {
        
    _owner = null;

    constructor(reactBase) {
        this._owner = reactBase;
    }

    onAdd(jcpJSLWB, objects, objId) {
        const object = objects.getById(objId);
        if (this._owner.onAdded && !this._owner.added) {
            object.addOnObject(this._owner._onObject);
            this._owner.added = true;
            this._owner.onAdded(jcpJSLWB, object);
        }
        this._owner.setState({isInit: object.isObjectInit()});
    }

    onRem(jcpJSLWB, objects, objId) {
        const object = objects.getById(objId);
        if (this._owner.onRemoved && this._owner.added) {
            object.remOnObject(this._owner._onObject);
            this._owner.added = false;
            this._owner.onRemoved(jcpJSLWB, object);
        }
        this._owner.setState({isInit: object.isObjectInit()});
    }

    onConnected(jcpJSLWB, objects, objId) {}

    onDisconnected(jcpJSLWB, objects, objId) {}

}
