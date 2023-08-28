import React from "react";
import { Link } from "react-router-dom";
import Grid from '@material-ui/core/Grid';
import { DataGrid } from '@material-ui/data-grid';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';
import Tooltip from '@material-ui/core/Tooltip';

import { getTimeString } from '../../../jcp-commons/DateUtils';

const ObjectJavascript = globalThis.Object;

// Generic

export class Dashboard extends React.Component {

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
    }

    render() {
        return (
            <Grid container spacing={3} style={this.props.style}>
                <Grid item xs={12} sm={6} md={3}>
                    <DashboardObjectsCountCard jcpfe={this.jcpFE} />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <DashboardLastObjectActivityNamesCard jcpfe={this.jcpFE} />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <DashboardLastStatusUpdateNamesCard jcpfe={this.jcpFE} />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    {!this.jcpFE.getUser().isAuthenticated()
                        ? <DashboardJCPFEDescriptionCard jcpfe={this.jcpFE} />
                        : <DashboardObjectHowToAddCard jcpfe={this.jcpFE} />
                    }
                </Grid>
                <Grid item xs={12} sm={6}>
                    <DashboardStatusActionListCard jcpfe={this.jcpFE} />
                </Grid>
                <Grid item xs={12} sm={6}>
                    <DashboardLastObjectsAddedListCard jcpfe={this.jcpFE} />
                </Grid>
                <Grid item xs={12}>
                    <DashboardObjectsActivitiesCard jcpfe={this.jcpFE} />
                </Grid>
            </Grid>
        );
    }
}


// Components Base classes

export class ReactDashboardBaseCard extends React.Component {

    jcpFE = null;

    constructor(props, title, unsetHeight) {
        super(props);
        this.jcpFE = props.jcpfe;
        this.title = title;
        this.unsetHeight = unsetHeight;

        this._objectsActivities = new ReactDashboardBaseCard_OnObjectsActivities(this);
    }

    componentDidMount() {
        this.jcpFE.getStats().addOnObjectsActivities(this._objectsActivities);
    }

    componentWillUnmount() {
        this.jcpFE.getStats().remOnObjectsActivities(this._objectsActivities);
    }

    render() {
        return (
            <Paper elevation={3} style={{height: this.unsetHeight ? 'unset' : '170px', marginTop: '0px', padding: '20px', display: 'flex', flexDirection: 'column', justifyContent: 'flex-start'}}>
                <Tooltip title={this.title}>
                    <Typography gutterBottom noWrap variant="button" style={{color: '#0000008a'}}>
                        {this.title}
                    </Typography>
                </Tooltip>
                {this.renderContent()}
            </Paper>
        );
    }

}

class ReactDashboardBaseCard_OnObjectsActivities {

    _owner = null;

    constructor(reactBase) {
        this._owner = reactBase;
    }

    onObjectsActivitiesUpdate(objectActivitiesList) {
        if (this._owner.onObjectsActivitiesUpdate)
            this._owner.onObjectsActivitiesUpdate(objectActivitiesList);
    }

}


// Card Components

export class DashboardObjectsCountCard extends ReactDashboardBaseCard {

    constructor(props) {
        super(props,"Objects count");

        this.state = {
            count: this.jcpFE.getStats().getObjectsCount(),
            maxCount: this.jcpFE.getStats().getMaxObjectsCount()
        }
    }

    // Objects activities Events

    onObjectsActivitiesUpdate(objectActivitiesList) {
        if (objectActivitiesList[0].type === "OBJ_ADD"
            || objectActivitiesList[0].type === "OBJ_REM")
        this.setState({
            count: this.jcpFE.getStats().getObjectsCount(),
            maxCount: this.jcpFE.getStats().getMaxObjectsCount()
        });
    }


    // Body

    renderContent() {
        return (
            <React.Fragment>
                <Typography gutterBottom style={{paddingLeft: '20px', fontWeight: 'bold', fontSize: '2em'}}>
                    {this.state.count}
                </Typography>
                <Tooltip title={this.state.maxCount + "max object count" } >
                    <Typography >
                        {this.state.maxCount} max objects count<br />
                        Go to the <Link to="/objects">objects list</Link>
                    </Typography>
                </Tooltip>
            </React.Fragment>
        );
    }

}

export class DashboardLastObjectActivityNamesCard extends ReactDashboardBaseCard {

    jcpFE = null;

    constructor(props) {
        super(props,"Latest object activity");
        this.jcpFE = props.jcpfe;

        const lastActivity = this.jcpFE.getStats().getObjectsActivities()[0];
        var objId;
        var obj;
        if (lastActivity) {
            objId = lastActivity.objId;
            obj = this.jcpFE.getObjects().getById(objId);
        }
        this.state = {
            objId: objId ? objId : null,
            objName: obj ? obj.getName() : null
        }
    }

    // Objects activities Events

    onObjectsActivitiesUpdate(objectActivitiesList) {
        if (objectActivitiesList[0].objId != "NONE") {
            const object = this.jcpFE.getObjects().getById(objectActivitiesList[0].objId);
            this.setState({
                objId: objectActivitiesList[0].objId,
                objName: object ? object.getName() : "N/A"
            });
        }
    }


    // Body

    renderContent() {
        return (
            <React.Fragment>
                <Tooltip title={this.state.objName ? this.state.objName : "None"}>
                    <Typography gutterBottom style={{paddingLeft: '20px', fontWeight: 'bold', fontSize: '2em'}} noWrap >
                        {this.state.objName ? 
                            this.state.objName
                            : "None"}
                    </Typography>
                </Tooltip>
                {this.state.objId
                    ? <Typography noWrap >
                        <Link to={getObjectUrl(this.state.objId)}>{this.state.objId}</Link>
                    </Typography>
                    : null }
            </React.Fragment>
        );
    }

}

export class DashboardLastStatusUpdateNamesCard extends ReactDashboardBaseCard {

    jcpFE = null;

    constructor(props) {
        super(props,"Latest status update");
        this.jcpFE = props.jcpfe;

        const lastActivity = this.jcpFE.getStats().getObjectsActivities();
        const lastStatusActivity = lastActivity.find(x => x.type === 'OBJ_CMP');
        if (lastStatusActivity) {
            const { objId, compPath, compValue } = extractFromStatusUpdDescription(lastStatusActivity.description);
            this.state = {
                objStatusObjId: objId,
                objStatusPath: compPath,
                objStatusValue: compValue,
            }
        } else
            this.state = {
                objStatusObjId: null,
                objStatusPath: null,
                objStatusValue: null,
            }
    }

    // Objects activities Events

    onObjectsActivitiesUpdate(objectActivitiesList) {

        // activity.description = "Updated 'DXFYY-UHREH-KLRDN' object 'Volume' component's state = '81.0'"
        if (objectActivitiesList[0].objId != "NONE")
            if (objectActivitiesList[0].type === "OBJ_CMP") {
                const {objId, compPath, compValue} = extractFromStatusUpdDescription(objectActivitiesList[0].description);
                this.setState({
                    objStatusObjId: objectActivitiesList[0].objId,
                    objStatusPath: compPath,
                    objStatusValue: compValue
                });
            }
    }


    // Body

    renderContent() {
        return (
            <React.Fragment>
                <Typography gutterBottom style={{paddingLeft: '20px', fontWeight: 'bold', fontSize: '2em'}} noWrap >
                    {this.state.objStatusValue ? this.state.objStatusValue : "--"}
                </Typography>
                {this.state.objStatusObjId
                    ? <React.Fragment>
                        <Typography noWrap>
                            <Link to={getObjectUrl(this.state.objStatusObjId)}>{this.state.objStatusObjId}</Link>
                        </Typography>
                        <Typography noWrap>
                            {"> "} <Link to={getComponentUrl(this.state.objStatusObjId,this.state.objStatusPath)}>{this.state.objStatusPath}</Link>
                        </Typography>
                    </React.Fragment>
                    : null }
            </React.Fragment>
        );
    }

}

export class DashboardJCPFEDescriptionCard extends ReactDashboardBaseCard {

    constructor(props) {
        super(props,"JCP Front End");
    }


    // Body

    renderContent() {
        return (
            <React.Fragment>
                <Typography style={{height: '150px', overflow: 'auto'}}>
                    The <b>John Cloud Platform Front End</b> is
                    the <a href={this.jcpFE.getJSLWBUrls().userLogin}>signed-in</a>'s users portal to manage and interact with their IoT Eco-Systems.
                </Typography>
            </React.Fragment>
        );
    }

}

export class DashboardObjectHowToAddCard extends ReactDashboardBaseCard {

    constructor(props) {
        super(props,"Add objects");
    }


    // Body

    renderContent() {
        return (
            <React.Fragment>
                <Typography style={{height: '150px', overflow: 'auto'}}>
                    Add object's to your John Eco-System is easy!
                    Follow the right How-To from John OS
                    Platform <Link to="https://git-repo.com/docs/add_object.md">documentation</Link>
                    .
                </Typography>
            </React.Fragment>
        );
    }

}

export class DashboardStatusActionListCard extends ReactDashboardBaseCard {

    jcpFE = null;

    constructor(props) {
        super(props,"Latest object's status updates",true);
        this.jcpFE = props.jcpfe;

        this.state = {
            objectsActivities: this.jcpFE.getStats().getObjectsActivities()
        }
    }

    // Objects activities Events

    onObjectsActivitiesUpdate(objectActivitiesList) {
        this.setState({objectsActivities: objectActivitiesList});
    }


    // Body

    renderContent() {
        var activities = this.state.objectsActivities;
            
        const columns = [
            { field: 'objId', headerName: 'Status updates', width: 650,
            renderCell: (params) => {
                    const objLink = <Link to={getObjectUrl(params.row.objId)}>{params.row.objId}</Link>;
                    const compLink = <Link to={getComponentUrl(params.row.objId,params.row.compPath)}>{params.row.compPath}</Link>
                    return (
                        <Typography>
                            Updated {objLink} object {compLink} component's state = '{params.row.compValue}'
                        </Typography>
                    );
                }
            }
        ];

        const activitiesStatus = activities.filter(activity => activity.type === "OBJ_CMP").map(activity => {
            const { objId, compPath, compValue } = extractFromStatusUpdDescription(activity.description);
            activity.compPath = compPath;
            activity.compValue = compValue;
            return activity;
        });

        return (
            <div style={{height: '400px', margin: '0px', padding: '1em'}} >
                <DataGrid
                    rows={activitiesStatus}
                    columns={columns}
                    pageSize={4}
                    disableColumnMenu
                    disableClickEventBubbling />
            </div>
        );
    }

}

export class DashboardLastObjectsAddedListCard  extends ReactDashboardBaseCard {

    jcpFE = null;

    constructor(props) {
        super(props,"Latest object's status updates",true);
        this.jcpFE = props.jcpfe;

        this.state = {
            objectsActivities: this.jcpFE.getStats().getObjectsActivities()
        }
    }

    // Objects activities Events

    onObjectsActivitiesUpdate(objectActivitiesList) {
        this.setState({objectsActivities: objectActivitiesList});
    }


    // Body

    renderContent() {
        var activities = this.state.objectsActivities;
            
        const columns = [
            { field: 'objId', headerName: 'Objects added list', width: 400,
            renderCell: (params) => {
                    const objLink = <Link to={getObjectUrl(params.row.objId)}>{params.row.objId}</Link>;
                    const datetime = new Date(params.row.time);
                    return (
                        <Typography>
                            Added {objLink} object at {getTimeString(datetime)}
                        </Typography>
                    );
                }
            }
        ];

        const activitiesStatus = activities.filter(activity => activity.type === "OBJ_ADD");

        return (
            <div style={{height: '400px', margin: '0px', padding: '1em'}} >
                <DataGrid
                    rows={activitiesStatus}
                    columns={columns}
                    pageSize={4}
                    disableColumnMenu
                    disableClickEventBubbling />
            </div>
        );
    }

}

export class DashboardObjectsActivitiesCard extends ReactDashboardBaseCard {

    jcpFE = null;

    constructor(props) {
        super(props,"Latest object's activities",true);
        this.jcpFE = props.jcpfe;

        this.state = {
            objectsActivities: this.jcpFE.getStats().getObjectsActivities()
        }
    }

    // Objects activities Events

    onObjectsActivitiesUpdate(objectActivitiesList) {
        this.setState({objectsActivities: objectActivitiesList});
    }


    // Body

    renderContent() {
        var activities = this.state.objectsActivities;
            
        const columns = [
            { field: 'time', headerName: 'Time', width: 160, type: 'dateTime',
                valueGetter: (params) => (new Date(params.row.time))},
            { field: 'objId', headerName: 'Object', flex: 0.3,
                renderCell: (params) => <Typography>{params.row.objId != "N/A" ? <Link to={getObjectUrl(params.row.objId)}>{params.row.objId}</Link> : "-"}</Typography>},
            { field: 'objName', headerName: 'Name', flex: 0.3,
                renderCell: (params) => <Typography>{params.row.objName != "N/A" ? params.row.objName : "-"}</Typography> },
            { field: 'type', headerName: 'Type', width: 140 },
            { field: 'description', headerName: 'Activities', flex: 1 },
            { field: 'id', headerName: 'ID', width: 60 },
        ];

        return (
            <div style={{height: '400px', margin: '0px', padding: '1em'}} >
                <DataGrid
                    rows={activities}
                    rowHeight={25} 
                    columns={columns}
                    pageSize={10}
                    disableColumnMenu
                    disableClickEventBubbling />
            </div>
        );
    }

}


// Utils

function getObjectUrl(objId) {
    //return this.jcpFE.getJSLWBUrls().object(objId);
    return "/objects/" + objId;
}

function getComponentUrl(objId,compPath) {
    //return this.jcpFE.getJSLWBUrls().component(objId,compPath);
    return "/objects/" + objId + "/status/" + encodeURIComponent(compPath);
}

function extractFromStatusUpdDescription(descr) {
    const objId = descr.substr(9,descr.substr(9).indexOf("'"));
    descr = descr.substr(9 + descr.substr(9).indexOf("'")+1);

    const compPath = descr.substr(9,descr.substr(9).indexOf("'"));
    descr = descr.substr(9 + descr.substr(9).indexOf("'")+1);

    const compValue = descr.substr(22,descr.substr(22).indexOf("'"));
    descr = descr.substr(22 + descr.substr(22).indexOf("'"));

    return {objId: objId, compPath: compPath, compValue: compValue};
}


