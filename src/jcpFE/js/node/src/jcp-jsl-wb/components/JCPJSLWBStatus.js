import React from 'react';

export default class JCPJSLWBStatus extends React.Component {

    constructor(props) {
        super(props)
        this.jcpJSLWB = props.jcpjslwb;

        this.state = {
            lastUpdate: new Date()
        };

        this._onStateChangedListener = new OnStateChangedListener(this);
    }

    componentDidMount() {
        this.jcpJSLWB.addOnStateChanged(this._onStateChangedListener);
    }

    componentWillUnmount() {
        this.jcpJSLWB.removeOnStateChanged(this._onStateChangedListener);
    }

    render() {
        const jcpJSLWB = this.jcpJSLWB;
        return (
            <div style={this.props.style}>
                <h1>JCP JSL Web bridge state: {jcpJSLWB.getState()}</h1>
                <h2>Url:</h2>
                <table>
                    <tbody>
                        <tr><td>isUrlInit</td><td>{jcpJSLWB.isUrlInit() ? "true" : "false"}</td></tr>
                        <tr><td>getJSLWBUrl</td><td>{jcpJSLWB.getJSLWBUrl()}</td></tr>
                        <tr><td>getJSLWBUrls</td><td>{JSON.stringify(jcpJSLWB.getJSLWBUrls())}</td></tr>
                    </tbody>
                </table><br />
                <h2>JSLSession:</h2>
                <table>
                    <tbody>
                        <tr><td>isJSLInit</td><td>{jcpJSLWB.isJSLInit() ? "true" : "false"}</td></tr>
                    </tbody>
                </table><br />
                <h2>SSEUpdater:</h2>
                <table>
                    <tbody>
                        <tr><td>isSSEEnabled</td><td>{jcpJSLWB.isSSEEnabled() ? "true" : "false"}</td></tr>
                        <tr><td>isSSEConnected</td><td>{jcpJSLWB.isSSEConnected() ? "true" : "false"}</td></tr>
                    </tbody>
                </table><br />
                <h2>GWConnection:</h2>
                <table>
                    <tbody>
                        <tr><td>isGWConnected</td><td>{jcpJSLWB.isGWConnected() ? "true" : "false"}</td></tr>
                        <tr><td>GWConnectionStatus</td><td>{jcpJSLWB.getGWConnection() ? <GWConnectionStatus gwConnection={jcpJSLWB.getGWConnection()} /> : null}</td></tr>
                    </tbody>
                </table><br />
                <h2>User:</h2>
                <table>
                    <tbody>
                        <tr><td>isUserInit</td><td>{jcpJSLWB.isUserInit() ? "true" : "false"}</td></tr>
                        <tr><td>UserStatus</td><td>{jcpJSLWB.isUserInit() ? <UserStatus user={jcpJSLWB.getUser()} /> : null}</td></tr>
                    </tbody>
                </table><br />
                <h2>Service:</h2>
                <table>
                    <tbody>
                        <tr><td>isServiceInit</td><td>{jcpJSLWB.isServiceInit() ? "true" : "false"}</td></tr>
                        <tr><td>UserStatus</td><td>{jcpJSLWB.isServiceInit() ? <ServiceStatus service={jcpJSLWB.getService()} /> : null}</td></tr>
                    </tbody>
                </table><br />
                <h2>Object:</h2>
                <table>
                    <tbody>
                        <tr><td>isObjectsInit</td><td>{jcpJSLWB.isObjectsInit() ? "true" : "false"}</td></tr>
                        <tr><td>UserStatus</td><td>{jcpJSLWB.isObjectsInit() ? <ObjectsStatus objects={jcpJSLWB.getObjects()} /> : null}</td></tr>
                    </tbody>
                </table><br />
                <h2>Manager:</h2>
                <table>
                    <tbody>
                        <tr><td>isManagerInit</td><td>{jcpJSLWB.isManagerInit() ? "true" : "false"}</td></tr>
                        <tr><td>UserStatus</td><td>{jcpJSLWB.isManagerInit() ? <ManagerStatus manager={jcpJSLWB.getManager()} /> : null}</td></tr>
                    </tbody>
                </table><br />
            </div>);
    }

}

class OnStateChangedListener {
        
    _owner = null;

    constructor(jcpJSLWBStatus) {
        this._owner = jcpJSLWBStatus;
    }

    onStateChanged(state) {
        console.log("#######" + state);
        this._owner.setState({lastUpdate: new Date()});
    }

}

export class GWConnectionStatus extends React.Component {

    constructor(props) {
        super(props)
        if (props.gwConnection==null)
            throw "Can't use GWConnectionStatus component before initialize JCP JSL Web Bridge's 'gwConnection' system'";
        this.gwConnection = props.gwConnection;

        this.state = {
            lastUpdate: new Date()
        };

        this._onGWConnected = new OnGWConnectedListener(this);
    }

    componentDidMount() {
        this.gwConnection.addOnConnected(this._onGWConnected);
    }

    componentWillUnmount() {
        this.gwConnection.removeOnConnected(this._onGWConnected);
    }

    render() {
        const gwConnection = this.gwConnection;
        return (
            <div>
                <table>
                    <tbody>
                        <tr><td>isConnected</td><td>{gwConnection.isConnected() ? "true" : "false"}</td></tr>
                    </tbody>
                </table><br />
            </div>);
    }

}

class OnGWConnectedListener {
        
    _owner = null;

    constructor(jcpJSLWBStatus) {
        this._owner = jcpJSLWBStatus;
    }

    onConnected(jcpJSLWB, gwConnection) {
        this._owner.setState({lastUpdate: new Date()});
    }

    onDisconnected(jcpJSLWB, gwConnection) {
        this._owner.setState({lastUpdate: new Date()});
    }

}

export class UserStatus extends React.Component {

    constructor(props) {
        super(props)
        if (props.user==null)
            throw "Can't use UserStatus component before initialize JCP JSL Web Bridge's 'user' system'";
        this.user = props.user;

        this.state = {
            lastUpdate: new Date()
        };

        this._onUser = new OnUserListener(this);
    }

    componentDidMount() {
        this.user.addOnUser(this._onUser);
    }

    componentWillUnmount() {
        this.user.removeOnUser(this._onUser);
    }

    render() {
        const user = this.user;
        return (
            <div>
                <table>
                    <tbody>
                        <tr><td>Id</td><td>{user.getId()}</td></tr>
                        <tr><td>Name</td><td>{user.getName()}</td></tr>
                        <tr><td>isAuthenticated</td><td>{user.isAuthenticated() ? "true" : "false"}</td></tr>
                        <tr><td>isAdmin</td><td>{user.isAdmin() ? "true" : "false"}</td></tr>
                        <tr><td>isDeveloper</td><td>{user.isDeveloper() ? "true" : "false"}</td></tr>
                        <tr><td>isMaker</td><td>{user.isMaker() ? "true" : "false"}</td></tr>
                    </tbody>
                </table><br />
            </div>);
    }

}

class OnUserListener {
        
    _owner = null;

    constructor(jcpJSLWBStatus) {
        this._owner = jcpJSLWBStatus;
    }

    onLogin(jcpJSLWB, gwConnection) {
        this._owner.setState({lastUpdate: new Date()});
    }

    onLogout(jcpJSLWB, gwConnection) {
        this._owner.setState({lastUpdate: new Date()});
    }

}

export class ServiceStatus extends React.Component {

    constructor(props) {
        super(props)
        if (props.service==null)
            throw "Can't use ServiceStatus component before initialize JCP JSL Web Bridge's 'service' system'";
        this.service = props.service;

        this.state = {
            lastUpdate: new Date()
        };

        this._onService = new OnServiceListener(this);
    }

    componentDidMount() {
        this.service.addOnService(this._onService);
    }

    componentWillUnmount() {
        this.service.addOnService(this._onService);
    }

    render() {
        const service = this.service;
        return (
            <div>
                <table>
                    <tbody>
                        <tr><td>Name</td><td>{service.getName()}</td></tr>
                        <tr><td>Srv Id</td><td>{service.getSrvId()}</td></tr>
                        <tr><td>Usr Id</td><td>{service.getUsrId()}</td></tr>
                        <tr><td>Inst Id</td><td>{service.getInstId()}</td></tr>
                        <tr><td>Session Id</td><td>{service.getSessionId()}</td></tr>
                        <tr><td>JSL Version</td><td>{service.getJSLVersion()}</td></tr>
                        <tr><td>isJCPConnected</td><td>{service.isJCPConnected() ? "true" : "false"}</td></tr>
                        <tr><td>isCloudConnected</td><td>{service.isCloudConnected() ? "true" : "false"}</td></tr>
                        <tr><td>isLocalRunning</td><td>{service.isLocalRunning() ? "true" : "false"}</td></tr>
                    </tbody>
                </table><br />
            </div>);
    }

}

class OnServiceListener {
        
    _owner = null;

    constructor(jcpJSLWBStatus) {
        this._owner = jcpJSLWBStatus;
    }

    onLogin(jcpJSLWB, gwConnection) {
        this._owner.setState({lastUpdate: new Date()});
    }

    onLogout(jcpJSLWB, gwConnection) {
        this._owner.setState({lastUpdate: new Date()});
    }

}

export class ObjectsStatus extends React.Component {

    constructor(props) {
        super(props)
        if (props.objects==null)
            throw "Can't use ObjectsStatus component before initialize JCP JSL Web Bridge's 'objects' system'";
        this.objects = props.objects;

        this.state = {
            lastUpdate: new Date()
        };

        this._onObjects = new OnObjectsListener(this);
    }

    componentDidMount() {
        this.objects.addOnObjects(this._onObjects);
    }

    componentWillUnmount() {
        this.objects.addOnObjects(this._onObjects);
    }

    render() {
        const objects = this.objects;
        return (
            <div>
                <table>
                    <tbody>
                        <tr><td>Count</td><td>{objects.getList().length}</td></tr>
                        {objects.getList().map( obj =>
                            <tr key={obj.id}><td></td><td>{obj.id} / {obj.name} / {obj.isConnected ? "Online" : "Offline"}</td></tr>
                        )}
                    </tbody>
                </table><br />
            </div>);
    }

}

class OnObjectsListener {
        
    _owner = null;

    constructor(jcpJSLWBStatus) {
        this._owner = jcpJSLWBStatus;
    }

    onAdd(jcpJSLWB, gwConnection, objId) {
        this._owner.setState({lastUpdate: new Date()});
    }

    onRem(jcpJSLWB, gwConnection, objId) {
        this._owner.setState({lastUpdate: new Date()});
    }

    onConnected(jcpJSLWB, gwConnection, objId) {
        this._owner.setState({lastUpdate: new Date()});
    }

    onDisconnected(jcpJSLWB, gwConnection, objId) {
        this._owner.setState({lastUpdate: new Date()});
    }

}

export class ManagerStatus extends React.Component {

    constructor(props) {
        super(props)
    }

    render() {
        return <div></div>;
    }
    
}


