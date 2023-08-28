import React from "react";
import { Link } from "react-router-dom";
import Container from '@material-ui/core/Container';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import ListItemText from '@material-ui/core/ListItemText';
import withWidth, { isWidthUp, isWidthDown } from '@material-ui/core/withWidth';

import { getDateTimeString } from '../../../jcp-commons/DateUtils';
import { JCPList, TableProperties, MoreIcon, RefreshIcon } from '../Commons';
import { ReactFetching, ReactFetchingCard, ReactFetchingSubCard } from '../base/ReactFetching';


// Generic JCP

export class JCPHeaderRaw extends React.Component {

    constructor(props) {
        super(props);
        this.jcpUrl = props.jcpUrl;
        this.state = {
            value: 0
        }
    }

    // setValue(value) {
    //     this.setState({value: value});
    // }

    render() {
        const currentPath = location.pathname;
        const isAPIs = currentPath.indexOf("jcp/apis") > 0;
        const isGWs = currentPath.indexOf("jcp/gateways") > 0;
        const isJSLWB = currentPath.indexOf("jcp/jslwebbridge") > 0;
        const isFE = currentPath.indexOf("jcp/frontend") > 0;
        const isStatus = currentPath.indexOf("status") > 0;
        const isExec = currentPath.indexOf("executable") > 0;
        const isBI = currentPath.indexOf("buildinfo") > 0;

        const serviceName = isAPIs ? "APIs"
                            : isGWs ? "Gateways"
                            : isJSLWB ? "JSL Web Bridge"
                            : isFE ? "Front End"
                            : undefined;
        const sectionName = isStatus ? "Status"
                            : isExec ? "Executable"
                            : isBI ? "Build Info"
                            : isGWs ? "List"
                            : undefined;
        const sectionDescription = isStatus ? "JCP service status stats"
                                   : isExec ? "JCP service executable info and stats"
                                   : isBI ? "JCP service build info"
                                   : isGWs ? "JCP Gateways server list"
                                   : undefined;

        return (
            <div>
                <Container style={{display: 'flex', justifyContent: 'space-between'}}>
                    <div>
                        <Typography variant="subtitle1" component="p" style={{display: 'flex'}}>
                            
                        </Typography>
                        <Typography variant="h4" component="h2">
                            {serviceName && sectionName
                                ? "JCP " + serviceName + "'s " + sectionName
                                : "JCP Home"
                            }
                        </Typography>
                        <Typography variant="subtitle2" component="p">
                            {sectionDescription ? sectionDescription : "JOSP Cloud Platform Manager"}
                        </Typography>
                    </div>
                </Container>
                <div style={{
                    display: 'flex', 
                    justifyContent: 'space-around', 
                    margin: '30px 0',
                    flexDirection: isWidthUp('sm',this.props.width) ? 'row' : 'column',
                    alignItems: 'center'
                    }}>
                    <JCPServiceMenu service="apis" jcpUrl={this.jcpUrl} selected={isAPIs}/>
                    <Button variant="contained"
                        color={isGWs
                                ? "primary"
                                : "default"
                            }
                        component={Link}
                        to={this.props.jcpUrl + "/gateways"}>
                        gateways
                    </Button>
                    <JCPServiceMenu service="jslwebbridge" jcpUrl={this.jcpUrl} selected={isJSLWB} />
                    <JCPServiceMenu service="frontend" jcpUrl={this.jcpUrl} selected={isFE} />
                </div>
            </div>
        );
    }

}
export const JCPHeader = withWidth()(JCPHeaderRaw)

export class JCPServiceMenu extends React.Component {

    constructor(props) {
        super(props);
        this.service = props.service;
        this.jcpUrl = props.jcpUrl;
        this.state = {
            anchorEl: null
        }
    }

    _handleClick(event) {
        this.setState({anchorEl: event.currentTarget});
    }

    _handleClose() {
        this.setState({anchorEl: null});
    }

    render() {
        return (
            <div>
                <Button variant="contained"
                    color={this.props.selected
                            ? "primary"
                            : "default"
                        }
                    onClick={(event) => this._handleClick(event)}
                    endIcon={<MoreIcon />}>
                    {this.service}
                </Button>
                    
                <Menu
                    anchorEl={this.state.anchorEl}
                    keepMounted
                    open={Boolean(this.state.anchorEl)}
                    onClose={() => this._handleClose()}
                    getContentAnchorEl={null}
                    anchorOrigin={{
                        vertical: 'bottom',
                        horizontal: 'center',
                    }}
                    transformOrigin={{
                        vertical: 'top',
                        horizontal: 'center',
                    }}
                >
                    <MenuItem component={Link} to={this.props.jcpUrl + "/" + this.service + "/status"} onClick={() => this._handleClose()} >
                        <ListItemText primary="Status" />
                    </MenuItem>
                    <MenuItem component={Link} to={this.props.jcpUrl + "/" + this.service + "/executable"} onClick={() => this._handleClose()} >
                        <ListItemText primary="Executable" />
                    </MenuItem>
                    <MenuItem component={Link} to={this.props.jcpUrl + "/" + this.service + "/buildinfo"} onClick={() => this._handleClose()} >
                        <ListItemText primary="Build Info" />
                    </MenuItem>
                </Menu>
            </div>
        );
    }

}


// APIs Status

export class JCPAPIsStatusPage extends ReactFetching {

    constructor(props) {
        super(props,props.jcpfe.getJSLWBAdminUrls().apisStatus);
        this.jcpFE = props.jcpfe;
        this.jslWBUrl = props.jcpfe.getJSLWBUrl();
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;
        
        return (
            <Container>
                <JCPAPIsStatus_ObjectsCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlObjects} />
                <JCPAPIsStatus_ServicesCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlServices} />
                <JCPAPIsStatus_UsersCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlUsers} />
                <JCPAPIsStatus_GatewaysCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlGateways} />
            </Container>
        );
    }

}

export class JCPAPIsStatus_ObjectsCard extends ReactFetchingCard {

    constructor(props) {
        super(props,props.url,"Registered objects");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <React.Fragment>
                <div style={{padding:'1em'}}>
                    {this._getTableProperties(data)}
                </div>
                <JCPList jcpfe={this.jcpFE} title="Object details" data={data.objectsList} component={JCPAPIsStatus_ObjectsDetails} />
            </React.Fragment>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Objects count', value: data.count});
        properties.push({name: 'Online', value: data.onlineCount});
        properties.push({name: 'Offline', value: data.offlineCount});
        properties.push({name: 'Active', value: data.activeCount});
        properties.push({name: 'Inactive', value: data.inactiveCount});
        properties.push({name: 'Owners', value: data.ownersCount});
        return (
            <TableProperties properties={properties} disableClipboardCopy nameWidth="80%" valueWidth="20%" style={{width: '200px'}} />
        );
    }

}

export class JCPAPIsStatus_ObjectsDetails extends ReactFetching {

    constructor(props) {
        super(props, props.url);
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        const properties = [];
        properties.push({name: 'ID', value: data.id});
        properties.push({name: 'Name', value: data.name});
        properties.push({name: 'Owner', value: data.owner});
        properties.push({name: 'IsOnline', value: data.online ? "True" : "False"});
        properties.push({name: 'IsActive', value: data.active ? "True" : "False"});
        properties.push({name: 'JOD Version', value: data.version});
        properties.push({name: 'Created At', value: getDateTimeString(data.createdAt)});
        properties.push({name: 'Updated At', value: getDateTimeString(data.updatedAt)});
        return (
            <TableProperties properties={properties} style={{width: '500px'}} />
        )
    }
    
}

export class JCPAPIsStatus_ServicesCard extends ReactFetchingCard {

    constructor(props) {
        super(props,props.url,"Registered services");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <React.Fragment>
                <div style={{padding:'1em'}}>
                    {this._getTableProperties(data)}
                </div>
                <JCPList jcpfe={this.jcpFE} title="Service details" data={data.servicesList} component={JCPAPIsStatus_ServicesDetails} />
            </React.Fragment>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Services count', value: data.count});
        properties.push({name: 'Online', value: data.onlineCount});
        properties.push({name: 'Offline', value: data.offlineCount});
        properties.push({name: 'Instances count', value: data.instancesCount});
        properties.push({name: 'Instances Online', value: data.instancesOnlineCount});
        properties.push({name: 'Instances Offline', value: data.instancesOfflineCount});
        return (
            <TableProperties properties={properties} disableClipboardCopy nameWidth="80%" valueWidth="20%" style={{width: '200px'}} />
        );
    }

}

export class JCPAPIsStatus_ServicesDetails extends ReactFetching {

    constructor(props) {
        super(props, props.url);
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        const properties = [];
        properties.push({name: 'ID', value: data.id});
        // ToDo add missing service details fields
        return (
            <TableProperties properties={properties} style={{width: '500px'}} />
        )
    }
    
}

export class JCPAPIsStatus_UsersCard extends ReactFetchingCard {

    constructor(props) {
        super(props,props.url,"Registered users");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <React.Fragment>
                <div style={{padding:'1em'}}>
                    {this._getTableProperties(data)}
                </div>
                <JCPList jcpfe={this.jcpFE} title="User details" data={data.usersList} component={JCPAPIsStatus_UsersDetails} />
            </React.Fragment>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Users count', value: data.count});
        return (
            <TableProperties properties={properties} disableClipboardCopy nameWidth="80%" valueWidth="20%" style={{width: '200px'}} />
        );
    }

}

export class JCPAPIsStatus_UsersDetails extends ReactFetching {

    constructor(props) {
        super(props, props.url);
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        const properties = [];
        properties.push({name: 'ID', value: data.id});
        properties.push({name: 'Name', value: data.name});
        properties.push({name: 'First Name', value: data.first_name});
        properties.push({name: 'Second Name', value: data.second_name});
        properties.push({name: 'eMail', value: data.email});
        properties.push({name: 'Created At', value: getDateTimeString(data.createdAt)});
        properties.push({name: 'Updated At', value: getDateTimeString(data.updatedAt)});
        return (
            <TableProperties properties={properties} style={{width: '500px'}} />
        )
    }
    
}

export class JCPAPIsStatus_GatewaysCard extends ReactFetchingCard {

    constructor(props) {
        super(props,props.url,"Registered gateways");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <React.Fragment>
                <div style={{padding:'1em'}}>
                    {this._getTableProperties(data)}
                </div>
                <JCPList jcpfe={this.jcpFE} title="Gateway details" data={data.gatewaysList} component={JCPAPIsStatus_GatewaysDetails} />
            </React.Fragment>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Gateways count', value: data.count});
        properties.push({name: 'Gateways removed', value: data.removed});
        properties.push({name: 'Gateways total', value: data.total});
        return (
            <TableProperties properties={properties} disableClipboardCopy nameWidth="80%" valueWidth="20%" style={{width: '200px'}} />
        );
    }

}

export class JCPAPIsStatus_GatewaysDetails extends ReactFetching {

    constructor(props) {
        super(props, props.url);
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        const properties = [];
        properties.push({name: 'ID', value: data.id});
        properties.push({name: 'Type', value: data.type});
        properties.push({name: 'GW Version', value: data.version});
        properties.push({name: 'isConnected', value: data.connected ? "True" : "False"});
        properties.push({name: 'Reconnection attempts', value: data.reconnectionAttempts});
        properties.push({name: 'Current clients', value: data.currentClients});
        properties.push({name: 'Max clients', value: data.maxClients});
        properties.push({name: 'APU url', value: data.apiUrl});
        properties.push({name: 'GW url', value: data.gwUrl});
        properties.push({name: 'Created At', value: getDateTimeString(data.createdAt)});
        properties.push({name: 'Updated At', value: getDateTimeString(data.updatedAt)});

        return (
            <TableProperties properties={properties} style={{width: '500px'}} />
        )
    }
    
}


// Gateways Status

export class JCPGatewaysPage extends ReactFetching {

    constructor(props) {
        super(props, props.jcpfe.getJSLWBAdminUrls().gatewaysStatusList);
        this.jcpFE = props.jcpfe;
        this.jslWBUrl = props.jcpfe.getJSLWBUrl();
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;
            
        return (
            <Container>
                <Card style={{margin:'20px 0'}}>
                    <CardContent>
                        <Typography variant="h5" component="h3">Gateways list</Typography>
                        {data.serverList.map (gwServer =>
                            <div key={gwServer.id} style={{padding:'1em'}}>
                                <Typography variant="h6" component="h6">{gwServer.name}</Typography>
                                <ul>
                                    <li>
                                        <Link to={this.props.jcpUrl + "/gateways/" + gwServer.id + "/status"}>
                                            Gateways Server Status
                                        </Link>
                                    </li>
                                    <li>
                                        <Link to={this.props.jcpUrl + "/gateways/" + gwServer.id + "/executable"}>
                                            Gateways Server Executable
                                        </Link>
                                    </li>
                                    <li>
                                        <Link to={this.props.jcpUrl + "/gateways/" + gwServer.id + "/buildinfo"}>
                                            Gateways Server Build Info
                                        </Link>
                                    </li>
                                </ul>
                            </div>
                        )}
                    </CardContent>
                </Card>
            </Container>
        );
    }

}

export class JCPGatewaysStatusPage extends ReactFetching {

    constructor(props) {
        super(props,props.jcpfe.getJSLWBAdminUrls().gatewaysStatus(props.gwServerId));
        this.jcpFE = props.jcpfe;
        this.jslWBUrl = props.jcpfe.getJSLWBUrl();
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;
        
        return (
            <Container>
                <JCPGatewaysStatusPage_GatewayCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlGateways} />
                <JCPGatewaysStatusPage_BrokerCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlBroker} />
            </Container>
        );
    }

}

export class JCPGatewaysStatusPage_GatewayCard extends ReactFetchingCard {

    constructor(props) {
        super(props,props.url,"Registered gateways");
        this.jcpFE = props.jcpfe;
        this.jslWBUrl = props.jcpfe.getJSLWBUrl();
    }

    renderContent(data) {
        return (
            <React.Fragment>
                <div style={{padding:'1em'}}>
                    {this._getTableProperties(data)}    
                    {data.gwList.map( gw => 
                        <div key={gw.id} style={{marginTop: '20px'}}>
                            <JCPGatewaysStatusPage_GatewaySubCard jcpfe={this.jcpFE} url={this.jslWBUrl + gw.url} name={gw.name} />
                        </div>
                    )}
                    
                </div>
                
            </React.Fragment>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Gateways count', value: data.gwList.length});
        return (
            <TableProperties properties={properties} disableClipboardCopy nameWidth="80%" valueWidth="20%" style={{width: '200px'}} />
        );
    }

}

export class JCPGatewaysStatusPage_GatewaySubCard extends ReactFetchingSubCard {

    constructor(props) {
        super(props,props.url,"Gateway " + props.name);
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <div style={{padding:'1em'}}>
                {this._getTableProperties(data)}
                <Typography variant="h6" component="h4">Gateway's Clients</Typography>
                <JCPList jcpfe={this.jcpFE} title="Gateway's client" data={data.clientsList} component={JCPGatewaysStatusPage_GatewayDetails} />
            </div>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Id', value: data.id});
        properties.push({name: 'Status', value: data.status});
        properties.push({name: 'Type', value: data.type});
        properties.push({name: 'API Port', value: data.apisPort});
        properties.push({name: 'GW Port', value: data.gwPort});
        properties.push({name: 'Internal Address', value: data.internalAddress});
        properties.push({name: 'Public Address', value: data.publicAddress});
        properties.push({name: 'Clients count', value: data.clientsCount});
        properties.push({name: 'Max Clients', value: data.maxClientsCount});
        return (
            <TableProperties properties={properties} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

}

export class JCPGatewaysStatusPage_GatewayDetails extends ReactFetching {

    constructor(props) {
        super(props, props.url);
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        const properties = [];

        properties.push({name: 'ID', value: data.id});
        properties.push({name: 'isConnected', value: data.isConnected ? "True" : "False"});
        properties.push({name: 'Local Id/Address', value: data.local});
        properties.push({name: 'Remote Id/Address', value: data.remote});
        properties.push({name: 'Last Connection', value: getDateTimeString(data.lastConnection)});
        properties.push({name: 'Last Heartbeat', value: getDateTimeString(data.lastHeartBeat)});
        properties.push({name: 'Data Rx', value: data.bytesRx});
        properties.push({name: 'Data Tx', value: data.bytesTx});
        properties.push({name: 'Last Data Rx', value: getDateTimeString(data.lastDataRx)});
        properties.push({name: 'Last Data Tx', value: getDateTimeString(data.lastDataTx)});

        return (
            <TableProperties properties={properties} style={{width: '500px'}} />
        )
    }
    
}

export class JCPGatewaysStatusPage_BrokerCard extends ReactFetchingCard {

    constructor(props) {
        super(props,props.url,"Broker");
        this.jcpFE = props.jcpfe;
        this.jslWBUrl = props.jcpfe.getJSLWBUrl();
    }

    renderContent(data) {
        console.log(data.objsList);
        return (
            <React.Fragment>
                <div style={{padding:'1em'}}>
                    {this._getTableProperties(data)}
                </div>
                <Typography variant="h6" component="h4">Broker's registered Objects</Typography>
                <JCPList jcpfe={this.jcpFE} title="Registered object on Broker" data={data.objsList} component={JCPGatewaysStatusPage_BrokerObjectDetails} />
                <Typography variant="h6" component="h4">Broker's registered Service</Typography>
                <JCPList jcpfe={this.jcpFE} title="Registered service on Broker" data={data.srvsList} component={JCPGatewaysStatusPage_BrokerServiceDetails} />
                <Typography variant="h6" component="h4">Broker's registered Objects DB</Typography>
                <JCPList jcpfe={this.jcpFE} title="Registered object DB on Broker" data={data.objsDBList} component={JCPGatewaysStatusPage_BrokerObjectDBDetails} />
                
            </React.Fragment>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Objects count', value: data.objsList.length});
        properties.push({name: 'Services count', value: data.srvsList.length});
        properties.push({name: 'Objects DB count', value: data.objsDBList.length});
        return (
            <TableProperties properties={properties} disableClipboardCopy nameWidth="80%" valueWidth="20%" style={{width: '200px'}} />
        );
    }

}

export class JCPGatewaysStatusPage_BrokerObjectDetails extends ReactFetching {

    constructor(props) {
        super(props, props.url);
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        const properties = [];
        properties.push({name: 'ID', value: data.id});
        properties.push({name: 'Name', value: data.name});
        properties.push({name: 'Owner', value: data.owner});

        return (
            <TableProperties properties={properties} style={{width: '500px'}} />
        )
    }
    
}

export class JCPGatewaysStatusPage_BrokerServiceDetails extends ReactFetching {

    constructor(props) {
        super(props, props.url);
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        const properties = [];
        properties.push({name: 'ID', value: data.id});
        properties.push({name: 'Name', value: data.name});
        properties.push({name: 'User', value: data.user});

        return (
            <TableProperties properties={properties} style={{width: '500px'}} />
        )
    }
    
}

export class JCPGatewaysStatusPage_BrokerObjectDBDetails extends ReactFetching {

    constructor(props) {
        super(props, props.url);
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        const properties = [];
        properties.push({name: 'ID', value: data.id});
        properties.push({name: 'Name', value: data.name});
        properties.push({name: 'Owner', value: data.owner});

        return (
            <TableProperties properties={properties} style={{width: '500px'}} />
        )
    }
    
}


// JSL Web Bridge Status

export class JCPJSLWebBridgeStatusPage extends ReactFetching {

    constructor(props) {
        super(props,props.jcpfe.getJSLWBAdminUrls().jslWebBridgeStatus);
        this.jcpFE = props.jcpfe;
        this.jslWBUrl = props.jcpfe.getJSLWBUrl();
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        return (
            <Container>
                <JCPJSLWebBridgeStatus_Sessions jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlSession} />
            </Container>
        );
    }

}

export class JCPJSLWebBridgeStatus_Sessions extends ReactFetchingCard {

    constructor(props) {
        super(props,props.url,"Web Sessions");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <React.Fragment>
                <div style={{padding:'1em'}}>
                    {this._getTableProperties(data)}
                </div>
                <JCPList jcpfe={this.jcpFE} title="Gateway details" data={data.sessionsList} component={JCPJSLWebBridgeStatus_SessionsDetails} />
            </React.Fragment>
        )
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Sessions count', value: data.sessionsList.length});
        return (
            <TableProperties properties={properties} disableClipboardCopy={true} style={{width: '300px'}} />
        );
    }

}

export class JCPJSLWebBridgeStatus_SessionsDetails extends ReactFetching {

    constructor(props) {
        super(props, props.url);
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        const properties = [];
        properties.push({name: 'ID', value: data.id});
        properties.push({name: 'Max Inactive Interval', value: data.maxInactiveInterval});
        properties.push({name: 'Created At', value: getDateTimeString(data.createdAt)});
        properties.push({name: 'Last Access At', value: getDateTimeString(data.lastAccessedAt)});

        return (
            <TableProperties properties={properties} style={{width: '500px'}} />
        )
    }
    
}


// Front End Status

export class JCPFrontEndStatusPage extends ReactFetching {

    constructor(props) {
        super(props,props.jcpfe.getJSLWBAdminUrls().frontendStatus);
        this.jcpFE = props.jcpfe;
        this.jslWBUrl = props.jcpfe.getJSLWBUrl();
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();

        return (
            <Container>
                <Card style={{margin:'20px 0'}}>
                    <CardContent>
                        <p>No status from JCP Front End service</p>
                    </CardContent>
                </Card>
            </Container>
        );
    }

}


// Execution Status

export class JCPExecutablePage extends ReactFetching {

    constructor(props) {
        super(props,
            props.service==="gateways"
                ? props.jcpfe.getJSLWBAdminUrls().statusExecutableGateways(props.service,props.gwServerId)
                : props.jcpfe.getJSLWBAdminUrls().statusExecutable(props.service));
        this.jcpFE = props.jcpfe;
        this.jslWBUrl = props.jcpfe.getJSLWBUrl();
        this.service = props.service;
    }

    render() {
        if (this.service!=this.props.service) {
            this.service=this.props.service;
            this.urlToFetch = this.props.jcpfe.getJSLWBAdminUrls().statusExecutable(this.props.service);
            this.fetchData();
        }
        
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;
        
        return (
            <Container>
                <JCPExecutable_OnlineCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlOnline} />
                <JCPExecutable_JavaCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlJava} />
                <JCPExecutable_OSCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlOS} />
                <JCPExecutable_CPUCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlCPU} />
                <JCPExecutable_DisksCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlDisks} />
                <JCPExecutable_NetworksCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlNetworks} />
            </Container>
        );
    }
    
}

export class JCPExecutable_OnlineCard extends ReactFetchingCard {

    constructor(props) {
        super(props,props.url,"Online");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <div style={{padding:'1em'}}>
                {this._getTableProperties(data)}
            </div>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Service\'s date time', value: getDateTimeString(data)});
        return (
            <TableProperties properties={properties} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

}

export class JCPExecutable_JavaCard extends ReactFetchingCard {

    constructor(props) {
        super(props,props.url,"Java");
        this.jcpFE = props.jcpfe;
        this.jslWBUrl = props.jcpfe.getJSLWBUrl();
    }

    renderContent(data) {
        return (
            <div style={{padding:'1em'}}>
                <div style={{marginTop: '20px'}}>
                    <JCPExecutable_JavaVMSubCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlVM} />
                </div>
                <div style={{marginTop: '20px'}}>
                    <JCPExecutable_JavaRuntimeSubCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlRuntime} />
                </div>
                <div style={{marginTop: '20px'}}>
                    <JCPExecutable_JavaTimesSubCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlTimes} />
                </div>
                <div style={{marginTop: '20px'}}>
                    <JCPExecutable_JavaClassesSubCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlClasses} />
                </div>
                <div style={{marginTop: '20px'}}>
                    <JCPExecutable_JavaMemorySubCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlMemory} />
                </div>
                <div style={{marginTop: '20px'}}>
                    <JCPExecutable_JavaThreadsSubCard jcpfe={this.jcpFE} url={this.jslWBUrl + data.urlThreads} />
                </div>
            </div>
        );
    }

}

export class JCPExecutable_JavaVMSubCard extends ReactFetchingSubCard {

    constructor(props) {
        super(props,props.url,"Java VM");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <div style={{padding:'1em'}}>
                {this._getTableProperties(data)}
            </div>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Name', value: data.vmName});
        properties.push({name: 'Version', value: data.vmVersion});
        properties.push({name: 'Specification Name', value: data.specName});
        properties.push({name: 'Specification Vendor', value: data.specVendor});
        properties.push({name: 'Specification Version', value: data.specVersion});
        properties.push({name: 'Specification Managment Version', value: data.specMngmVersion});
        return (
            <TableProperties properties={properties} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

}

export class JCPExecutable_JavaRuntimeSubCard extends ReactFetchingSubCard {

    constructor(props) {
        super(props,props.url,"Java Runtime");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <div style={{padding:'1em'}}>
                {this._getTableProperties(data)}
                Missing ENV Vars
            </div>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Path Class', value: data.runtimePathClass.length < 80 ? data.runtimePathClass : data.runtimePathClass.substring(0,80)});
        properties.push({name: 'Path Library', value: data.runtimePathLibrary < 80 ? data.runtimePathLibrary : data.runtimePathLibrary.substring(0,80)});
        properties.push({name: 'Path Boot Class', value: data.runtimePathBootClass < 80 ? data.runtimePathBootClass : data.runtimePathBootClass.substring(0,80)});
        return (
            <TableProperties properties={properties} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

}

export class JCPExecutable_JavaTimesSubCard extends ReactFetchingSubCard {

    constructor(props) {
        super(props,props.url,"Java Times");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <div style={{padding:'1em'}}>
                {this._getTableProperties(data)}
            </div>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Running time', value: data.timeRunning/1000 + "(seconds)"});
        properties.push({name: 'Running since', value: getDateTimeString(new Date(data.timeStart))});
        return (
            <TableProperties properties={properties} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

}

export class JCPExecutable_JavaClassesSubCard extends ReactFetchingSubCard {

    constructor(props) {
        super(props,props.url,"Java Classes");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <div style={{padding:'1em'}}>
                {this._getTableProperties(data)}
                Missing ENV Vars
            </div>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Loaded', value: data.classesLoaded});
        properties.push({name: 'Unloaded', value: data.classesUnloaded});
        properties.push({name: 'Total', value: data.classesLoadedTotal});
        return (
            <TableProperties properties={properties} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

}

export class JCPExecutable_JavaMemorySubCard extends ReactFetchingSubCard {

    constructor(props) {
        super(props,props.url,"Java Memory");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <div style={{padding:'1em'}}>
                {this._getTableProperties(data)}
            </div>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Init', value: data.memoryInit + "(MB)"});
        properties.push({name: 'Used', value: data.memoryUsed + "(MB)"});
        properties.push({name: 'Max', value: data.memoryMax + "(MB)"});
        properties.push({name: 'Committed', value: data.memoryCommitted + "(MB)"});
        properties.push({name: 'Heap Init', value: data.memoryHeapInit + "(MB)"});
        properties.push({name: 'Heap Used', value: data.memoryHeapUsed + "(MB)"});
        properties.push({name: 'Heap Max', value: data.memoryHeapMax + "(MB)"});
        properties.push({name: 'Heap Free', value: data.memoryHeapFree + "(MB)"});
        properties.push({name: 'Heap Committed', value: data.memoryHeapCommitted + "(MB)"});
        return (
            <TableProperties properties={properties} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

}

export class JCPExecutable_JavaThreadsSubCard extends ReactFetchingSubCard {

    constructor(props) {
        super(props,props.url,"Java Threads");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <div style={{padding:'1em'}}>
                {this._getTableProperties(data)}
                <JCPList jcpfe={this.jcpFE} title="Thread details" data={data.threadsList} component={JCPExecutable_JavaThreadsDetails} />
            </div>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Threads count', value: data.threadsList.length});
        return (
            <TableProperties properties={properties} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

}

export class JCPExecutable_JavaThreadsDetails extends ReactFetching {

    constructor(props) {
        super(props, props.url);
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        const properties = [];
        properties.push({name: 'ID', value: data.id});
        // properties.push({name: 'Name', value: data.name});
        // properties.push({name: 'Owner', value: data.owner});
        // properties.push({name: 'IsOnline', value: data.online ? "True" : "False"});
        // properties.push({name: 'IsActive', value: data.active ? "True" : "False"});
        // properties.push({name: 'JOD Version', value: data.version});
        // properties.push({name: 'Created At', value: getDateTimeString(data.createdAt)});
        // properties.push({name: 'Updated At', value: getDateTimeString(data.updatedAt)});
        return (
            <TableProperties properties={properties} style={{width: '500px'}} />
        )
    }
    
}

export class JCPExecutable_OSCard extends ReactFetchingCard {

    constructor(props) {
        super(props,props.url,"OS");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <div style={{padding:'1em'}}>
                {this._getTableProperties(data)}
            </div>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'OS Name', value: data.name});
        properties.push({name: 'OS Version', value: data.version});
        properties.push({name: 'Architecture', value: data.arch});
        return (
            <TableProperties properties={properties} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

}

export class JCPExecutable_CPUCard extends ReactFetchingCard {

    constructor(props) {
        super(props,props.url,"CPU");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <div style={{padding:'1em'}}>
                {this._getTableProperties(data)}
            </div>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'CPUs count', value: data.count});
        properties.push({name: 'Avg Load', value: data.loadAvg});
        return (
            <TableProperties properties={properties} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

}

export class JCPExecutable_DisksCard extends ReactFetchingCard {

    constructor(props) {
        super(props,props.url,"Disks");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <React.Fragment>
                <div style={{padding:'1em'}}>
                    {this._getTableProperties(data)}
                </div>
                <JCPList jcpfe={this.jcpFE} title="Disk details" data={data.disksList} component={JCPExecutable_DisksDetails} />
            </React.Fragment>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Disks count', value: data.disksList.length});
        return (
            <TableProperties properties={properties} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

}

export class JCPExecutable_DisksDetails extends ReactFetching {

    constructor(props) {
        super(props, props.url);
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        const properties = [];
        properties.push({name: 'ID', value: data.id});
        // properties.push({name: 'Name', value: data.name});
        // properties.push({name: 'Owner', value: data.owner});
        // properties.push({name: 'IsOnline', value: data.online ? "True" : "False"});
        // properties.push({name: 'IsActive', value: data.active ? "True" : "False"});
        // properties.push({name: 'JOD Version', value: data.version});
        // properties.push({name: 'Created At', value: getDateTimeString(data.createdAt)});
        // properties.push({name: 'Updated At', value: getDateTimeString(data.updatedAt)});
        return (
            <TableProperties properties={properties} style={{width: '500px'}} />
        )
    }
    
}

export class JCPExecutable_NetworksCard extends ReactFetchingCard {

    constructor(props) {
        super(props,props.url,"Networks");
        this.jcpFE = props.jcpfe;
    }

    renderContent(data) {
        return (
            <React.Fragment>
                <div style={{padding:'1em'}}>
                    {this._getTableProperties(data)}
                </div>
                <JCPList jcpfe={this.jcpFE} title="Network details" data={data.networksList} component={JCPExecutable_NetworksDetails} />
            </React.Fragment>
        );
    }

    _getTableProperties(data) {
        const properties = [];
        properties.push({name: 'Address Localhost', value: data.addrLocalhost});
        properties.push({name: 'Address Loopback', value: data.addrLoopback});
        properties.push({name: 'Networks count', value: data.networksList.length});
        return (
            <TableProperties properties={properties} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

}

export class JCPExecutable_NetworksDetails extends ReactFetching {

    constructor(props) {
        super(props, props.url);
    }

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        const properties = [];
        properties.push({name: 'ID', value: data.id});
        // properties.push({name: 'Name', value: data.name});
        // properties.push({name: 'Owner', value: data.owner});
        // properties.push({name: 'IsOnline', value: data.online ? "True" : "False"});
        // properties.push({name: 'IsActive', value: data.active ? "True" : "False"});
        // properties.push({name: 'JOD Version', value: data.version});
        // properties.push({name: 'Created At', value: getDateTimeString(data.createdAt)});
        // properties.push({name: 'Updated At', value: getDateTimeString(data.updatedAt)});
        return (
            <TableProperties properties={properties} style={{width: '500px'}} />
        )
    }
    
}


// Build Info Status

export class JCPBuildInfoPage extends ReactFetching {

    constructor(props) {
        super(props,
            props.service==="gateways"
                ? props.jcpfe.getJSLWBAdminUrls().statusBuildInfoGateways(props.service,props.gwServerId)
                : props.jcpfe.getJSLWBAdminUrls().statusBuildInfo(props.service));
        this.jcpFE = props.jcpfe;
        this.jslWBUrl = props.jcpfe.getJSLWBUrl();
        this.service = props.service;
    }

    render() {
        if (this.service!=this.props.service) {
            this.service=this.props.service;
            this.urlToFetch = this.props.jcpfe.getJSLWBAdminUrls().statusBuildInfo(this.props.service);
            this.fetchData();
        }

        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
            return this.renderDataNotFetched();
        const data = this.state.data;

        return (
            <Container>
                <Card style={{margin:'20px 0'}}>
                    <CardContent>
                        <Typography id="build" variant="h5" component="h3">Build constants</Typography>
                        <div style={{padding:'1em'}}>
                            {this._getTablePropertiesBuild(data)}
                        </div>
                    </CardContent>
                </Card>
                <Card style={{margin:'20px 0'}}>
                    <CardContent>
                        <Typography id="build" variant="h5" component="h3">Source constants</Typography>
                        <div style={{padding:'1em'}}>
                            {this._getTablePropertiesSource(data)}
                        </div>
                    </CardContent>
                </Card>
                <Card style={{margin:'20px 0'}}>
                    <CardContent>
                        <Typography id="build" variant="h5" component="h3">Build's Environment constants</Typography>
                        <div style={{padding:'1em'}}>
                            {this._getTablePropertiesEnv(data)}
                        </div>
                    </CardContent>
                </Card>
            </Container>
        )
    }

    _getTablePropertiesBuild(data) {
        const propertiesBuild = [];
        propertiesBuild.push({name: 'Version', value: data.version});
        propertiesBuild.push({name: 'Version Build', value: data.versionBuild});
        propertiesBuild.push({name: 'Builded at', value: getDateTimeString(new Date(data.buildTime))});
        return (
            <TableProperties properties={propertiesBuild} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

    _getTablePropertiesSource(data) {
        const propertiesSources = [];
        propertiesSources.push({name: 'Project name', value: data.project});
        propertiesSources.push({name: 'Source set', value: data.sourceSet});
        propertiesSources.push({name: 'Git Branch', value: data.gitBranch});
        propertiesSources.push({name: 'Git Commit', value: data.gitCommitShort});
        propertiesSources.push({name: 'Git Commit (long)', value: data.gitCommit});
        return (
            <TableProperties properties={propertiesSources} style={{width: '100%', maxWidth: '600px'}} />
        );
    }

    _getTablePropertiesEnv(data) {
        const propertiesEnv = [];
        propertiesEnv.push({name: 'Java Version', value: data.javaVersion});
        propertiesEnv.push({name: 'Java Home', value: data.javaHome});
        propertiesEnv.push({name: 'Gradle Version', value: data.gradleVersion});
        propertiesEnv.push({name: 'Architecture', value: data.osArch});
        propertiesEnv.push({name: 'OS Name', value: data.osName});
        propertiesEnv.push({name: 'OS Version', value: data.osVersion});
        propertiesEnv.push({name: 'User', value: data.user});
        return (
            <TableProperties properties={propertiesEnv} style={{width: '100%', maxWidth: '600px'}} />
        );
    }
    
}
