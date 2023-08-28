import ReactObjectBase from './ReactObjectBase';
import Container from '@material-ui/core/Container';
import Typography from '@material-ui/core/Typography';


export default class ReactObjectComponentBase extends ReactObjectBase {

    component = null;

    constructor(props){
        super(props);
        if (!props.component && !props.compPath)
            throw "Can't use ObjectComponent component with undefined 'component' or 'compPath' properties'";
        this._onObject = new ReactObjectBase_OnObject(this);
        this._onComponent = new ReactObjectComponentBase_OnComponent(this);
        
        this.component = props.component;
        if (this.component) {
            this.getObject().addOnObject(this._onObject);
            this.component.addOnComponent(this._onComponent);
        }
        this.compPath = props.compPath;
    }

    componentDidMount() {
        super.componentDidMount();
        this._tryUpdateComponent();
    }

    componentWillUnmount() {
        if (this.isComponentInit()) {
            this.getObject().remOnObject(this._onObject);
            this.getComponent().remOnComponent(this._onComponent);
        }
    }


    // Object's events

    onAdded(jcpJSLWB, object) {
        this._tryUpdateComponent();
    }
    
    onStructUpd(jcpJSLWB, object) {
        this._tryUpdateComponent();
    }

    _tryUpdateComponent() {
        if (this.component)
            return;
        
        if (!this.isObjectInit())
            return;

        if (!this.getObject().isStructInit()) {
            this.getObject().fetchStruct(true);
            return;
        }

        this.component = this.getObject().getComponent(this.compPath);
        this.getObject().addOnObject(this._onObject);
        this.component.addOnComponent(this._onComponent);
        if (this.onComponentInit)
            this.onComponentInit();
    }

    getComponent() {
        return this.component;
    }

    isComponentInit() {
        return this.getComponent() != null;
    }

    renderComponentNotInit() {
        return (
            <Container>
                <Typography variant="body2" component="p">
                    Component {this.compPath} not found on '{this.objId}' object.
                </Typography>
            </Container>
        );
    }

}

class ReactObjectComponentBase_OnComponent {
        
    _owner = null;

    constructor(reactBase) {
        this._owner = reactBase;
    }

    onStatusUpdate(jcpJSLWB, component, status, oldStatus) {
        if (this._owner.onStatusUpdate)
            this._owner.onStatusUpdate(jcpJSLWB, component, status, oldStatus);
    }

}

class ReactObjectBase_OnObject {
        
    _owner = null;

    constructor(reactBase) {
        this._owner = reactBase;
    }

    onConnected(jcpJSLWB, object) {
        if (this._owner.onConnected)
            this._owner.onConnected(jcpJSLWB, object, this._owner.getComponent());
    }

    onDisconnected(jcpJSLWB, object) {
        if (this._owner.onDisconnected)
            this._owner.onDisconnected(jcpJSLWB, object, this._owner.getComponent());
    }

    onStructUpd(jcpJSLWB, object) {
        if (this._owner.onStructUpd)
            this._owner.onStructUpd(jcpJSLWB, object, this._owner.getComponent());
    }

    onPermsUpd(jcpJSLWB, object) {
        if (this._owner.onPermsUpd)
            this._owner.onPermsUpd(jcpJSLWB, object, this._owner.getComponent());
    }

    onPermSrvUpd(jcpJSLWB, object, value, old) {
        if (this._owner.onPermSrvUpd)
            this._owner.onPermSrvUpd(jcpJSLWB, object, value, old, this._owner.getComponent());
    }

    onInfoUpd(jcpJSLWB, object, key, value, old) {
        if (this._owner.onInfoUpd)
            this._owner.onInfoUpd(jcpJSLWB, object, key, value, old, this._owner.getComponent());
    }

}