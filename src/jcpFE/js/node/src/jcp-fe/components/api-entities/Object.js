import React from "react";
import { Link } from "react-router-dom";
import Grid from '@material-ui/core/Grid';
import { DataGrid } from '@material-ui/data-grid';
import Container from '@material-ui/core/Container';
import Card from '@material-ui/core/Card';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import Divider from '@material-ui/core/Divider';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import Tooltip from '@material-ui/core/Tooltip';
import IconButton from '@material-ui/core/IconButton';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import TextField from '@material-ui/core/TextField';
import InputLabel from '@material-ui/core/InputLabel';
import Input from '@material-ui/core/Input';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import Autocomplete from '@material-ui/lab/Autocomplete';
import { withStyles, withTheme } from '@material-ui/core/styles';
import withWidth, { isWidthUp, isWidthDown } from '@material-ui/core/withWidth';
import {Line} from 'react-chartjs-2';

import { getDayString, getHourString, getDateTimeCode } from '../../../jcp-commons/DateUtils';
import { ValueField, StructureIcon, EventsIcon, AccessControlIcon, ConfigsIcon, DownloadIcon, OnlineIcon, OfflineIcon, DuplicateIcon, DeleteIcon, EditIcon, MenuIcon, MoreIcon } from '../Commons';
import { ObjectContainerDetails } from './Components';
import ReactObjectBase from '../base/ReactObjectBase';
import ReactObjectComponentBase from '../base/ReactObjectComponentBase';
import ReactObjectPageBase from '../base/ReactObjectPageBase';
import { getIsSection_Object } from '../../routers/ObjectRouter';


// Generic

export class ObjectListItemCard extends ReactObjectBase {

    constructor(props) {
        super(props);
        this.format = props.format ? props.format : "square";
        const object = this.getObject();
        if (object)
            this.state = {
                ...this.state,
                id: object.getId(),
                name: object.getName(),
                isConnected: object.isConnected(),
                model: object.getModel(),
                permission: object.getPermission(),
            }
    }

    onAdded(jcpJSLWB, object) {
        //const object = this.getObject();
        if (object)
            this.setState({
                id: object.getId(),
                name: object.getName(),
                isConnected: object.isConnected(),
                model: object.getModel(),
            });
    }

    onConnected(jcpJSLWB, gwConnection, objId) {
        this.setState({isConnected: true});
    }

    onDisconnected(jcpJSLWB, gwConnection, objId) {
        this.setState({isConnected: false});
    }

    onInfoUpd(jcpJSLWB, objects, objId, key, value, old) {
        if (key === "Name")
            this.setState({name: value});
        else if (key === "Model")
            this.setState({model: value});

    }

    onPermSrvUpd(objId,value,old) {
        this.setState({permission: value});
    }

    render() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();
            console.log("Check if can access " + this.isObjectAllowed() + " " + this.getObject().getPermission());
        if (!this.isObjectAllowed())
            return this.renderObjectNotAllowed();

        if (this.format === "square")
            return (
                <Card>
                    <CardContent>
                        <Typography variant="subtitle1" component="p">{this.state.id}</Typography>
                        <Typography variant="h6" component="h2" style={{display: 'flex', alignItems: 'center'}} >
                            {this.state.isConnected ? <OnlineIcon style={{margin: '0 10px'}} /> : <OfflineIcon style={{margin: '0 10px'}} /> }
                            {this.state.name}
                        </Typography>
                        <Typography variant="subtitle2" component="p">{this.state.model}</Typography>

                    </CardContent>
                    <CardActions>
                        <ObjectMenu objId={this.state.objId} permission={this.state.permission} objUrl={this.props.objUrl} />
                    </CardActions>
                </Card>
            );

            if (this.format === "horizontal")
                return (
                    <Card style={{width: '100%'}}>
                        <CardContent>
                            <div style={{display: 'flex', justifyContent: 'space-between'}}>
                                <div>
                                    <Typography variant="subtitle1" component="p" noWrap>{this.state.id}</Typography>
                                    <Typography variant="h6" component="h2" style={{display: 'flex', alignItems: 'center'}} >
                                        {this.state.isConnected ? <OnlineIcon style={{margin: '0 10px'}} /> : <OfflineIcon style={{margin: '0 10px'}} /> }
                                        <Link to={this.props.objUrl}>{this.state.name}</Link>
                                    </Typography>
                                    <Typography variant="subtitle2" component="p" noWrap>{this.state.model}</Typography>
                                </div>
                                <ObjectMenu objId={this.state.objId} permission={this.state.permission} objUrl={this.props.objUrl} />
                            </div>
                        </CardContent>
                    </Card>
                );
    }

}

export class ObjectHeaderRaw extends ReactObjectBase {

    constructor(props) {
        super(props);
        const object = this.jcpFE.getObjects().getById(this.objId);
        if (object)
            this.state = {
                ...this.state,
                id: object.getId(),
                name: object.getName(),
                isConnected: object.isConnected(),
                model: object.getModel(),
                permission: object.getPermission(),
            }
    }


    // Object's events

    onAdded(jcpJSLWB, object) {
        //const object = this.getObject();
        this.setState({
            id: object.getId(),
            name: object.getName(),
            isConnected: object.isConnected(),
            model: object.getModel(),
        });
    }

    onConnected(jcpJSLWB, object) {
        this.setState({isConnected: true});
    }

    onDisconnected(jcpJSLWB, object) {
        this.setState({isConnected: false});
    }

    onInfoUpd(jcpJSLWB, object, key, value, old) {
        if (key === "Name")
            this.setState({name: value});
        else if (key === "Model")
            this.setState({model: value});
    }

    onPermSrvUpd(objId,value,old) {
        this.setState({permission: value});
    }


    // Body

    render() {
        const classes = this.props.classes;

        if (!this.isObjectInit())
            return (
                <Container style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        flexWrap: isWidthUp(this.props.width) ? 'wrap' : 'inherit'
                    }}>
                    <div>
                        <Typography variant="subtitle1" component="p" noWrap style={{display: 'flex'}} >
                            <Tooltip title="Offline">
                                <OfflineIcon style={{margin: '0 10px'}} />
                            </Tooltip>
                            {this.objId}
                        </Typography>
                        <Typography variant="h4" component="h2" noWrap >
                            Object Not Found
                        </Typography>
                    </div>
                </Container>
            );

        if (!this.isObjectAllowed())
            return (
                <Container style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        flexWrap: isWidthUp(this.props.width) ? 'wrap' : 'inherit'
                    }}>
                    <div>
                        <Typography variant="subtitle1" component="p" noWrap style={{display: 'flex'}} >
                            <Tooltip title="Offline">
                                <OfflineIcon style={{margin: '0 10px'}} />
                            </Tooltip>
                            {this.objId}
                        </Typography>
                        <Typography variant="h4" component="h2" noWrap >
                            User can't access to Object
                        </Typography>
                    </div>
                </Container>
            );

        return (
            <Container style={{
                display: 'flex',
                justifyContent: 'space-between',
                flexWrap: isWidthUp('xs',this.props.width) ? 'wrap' : 'inherit'
            }}>
                <div style={{
                    width: isWidthDown('xs',this.props.width) ? '100%'
                            : isWidthDown('sm',this.props.width) ? 'calc(100% - 220px)'
                            : 'calc(100% - 330px)'
                }}>
                    <Typography variant="subtitle1" component="p" noWrap style={{display: 'flex'}}>
                        {this.state.isConnected
                            ?
                                <Tooltip title="Online">
                                    <OnlineIcon style={{margin: '0 10px'}} />
                                </Tooltip>
                            : 
                                <Tooltip title="Offline">
                                    <OfflineIcon style={{margin: '0 10px'}} />
                                </Tooltip>
                        }
                        {this.objId}
                    </Typography>
                    <div style={{display: 'flex'}}>
                        <Typography variant="h4" component="h2" noWrap >
                            {this.state.name}
                        </Typography>
                        {this.state.permission==="CoOwner"
                            ? <ObjectNameEdit_Dialog jcpfe={this.jcpFE} objId={this.state.id} />
                            : null }
                    </div>
                    <Typography variant="subtitle2" component="p" noWrap >{this.state.model}</Typography>
                </div>
                <ObjectActions permission={this.state.permission} objUrl={this.props.objUrl} {...this.props} />
            </Container>
        );
    }

}
export const ObjectHeader = withWidth()(withTheme(ObjectHeaderRaw))

export class ObjectNameEdit_Dialog extends ReactObjectBase {

    constructor(props) {
        super(props);
        const object = this.jcpFE.getObjects().getById(this.objId);
        if (object)
            this.state = {
                ...this.state,
                open: false,
                name: object.getName(),
                state: undefined
            }
    }


    // Object's events

    onAdded(jcpJSLWB, object) {
        //const object = this.getObject();
        this.setState({name: object.getName()});
    }


    handleEdit() {
        this.setState({open: true});
    }

    handleChange(event) {
        this.setState({name: event.target.value});
    }

    handleClose() {
        const object = this.jcpFE.getObjects().getById(this.objId);
        this.setState({
            open: false,
            name: object ? object.getName() : "",
            state: undefined
        });
    }

    handleSave(name) {
        const thiz = this;
        this.getObject().setName(this.state.name,
            function onSuccess(object,response) {
                thiz.setState({state: "success"});
            },
            function onError(object, xhttp, error) {
                thiz.setState({state: "error"});
            });
    }

    render() {
        return (
            <React.Fragment>
                <IconButton variant="contained" color="primary" size="medium" onClick={(event) => this.handleEdit(event)}>
                    <EditIcon />
                </IconButton>

                <Dialog onClose={() => this.handleClose()} open={this.state.open}>
                    <DialogTitle>Set object's name</DialogTitle>
                    <DialogContent>
                        {this.state.state === "success"
                            ? <Typography>Object's name updated successfully</Typography>
                            : this.state.state === "error"
                                ? <Typography>Error on update object's name</Typography>
                                : <TextField value={this.state.name} onChange={(event) => this.handleChange(event)} />
                        }
                        
                    </DialogContent>
                    
                    {this.state.state === "success" || this.state.state === "error"
                        ?
                            <DialogActions>
                                <Button onClick={() => this.handleClose()}>Ok</Button>
                            </DialogActions>
                        : 
                            <DialogActions>
                                <Button onClick={() => this.handleClose()}>Cancel</Button>
                                <Button variant="contained" color="primary" onClick={() => this.handleSave()}>Save</Button>
                            </DialogActions>
                    }
                </Dialog>
            </React.Fragment>
        );
    }

}

export class ObjectOwnerEdit_Dialog extends ReactObjectBase {

    defaultUsersList = [{label: "Me", value: "_ME"}, {label: "Anonymous", value: "00000-00000-00000"}];

    constructor(props) {
        super(props);
        const object = this.jcpFE.getObjects().getById(this.objId);
        this.state = {
            ...this.state,
            open: false,
            owner: object ? {
                label: object.getOwner(),
                value: object.getOwner()
            } : undefined,
            state: undefined
        }
    }


    // Object's events

    onAdded(jcpJSLWB, object) {
        //const object = this.getObject();
        this.setState({owner: {
            label: object.getOwner(),
            value: object.getOwner()
        }});
    }


    handleEdit() {
        this.setState({open: true});
    }

    handleChange(event) {
        this.setState({owner: event.target.value});
    }

    handleClose() {
        const object = this.jcpFE.getObjects().getById(this.objId);
        this.setState({
            open: false,
            owner: object ? object.getOwner() : "",
            state: undefined
        });
    }

    handleSave() {
        const thiz = this;
        if (this.state.owner.value === "_ME")
            this.state.owner.value = this.jcpFE.getUser().getId();
        this.getObject().setOwner(this.state.owner.value,
            function onSuccess(object,response) {
                thiz.setState({state: "success"});
            },
            function onError(object, xhttp, error) {
                thiz.setState({state: "error"});
            });
    }

    setOwner(owner) {
        this.setState({owner: owner});
    }

    handleOwnerChange(event,newValue) {
        // newValue is a valid user id?

        if (typeof newValue === 'string' && newValue !== "") {
            newValue = {
                label: newValue,
                value: newValue
            }
        }

        this.setOwner(newValue);
    };

    render() {
        return (
            <React.Fragment>
                <Button variant="contained" color="primary" startIcon={<EditIcon />} onClick={(event) => this.handleEdit(event)} style={{display: 'none'}}>
                    Change owner
                </Button>
                {/* <Button variant="contained" color="primary" startIcon={<EditIcon />} onClick={(event) => this.handleEdit(event)}>
                    Reset owner
                </Button> */}

                <Dialog onClose={() => this.handleClose()} open={this.state.open}>
                    <DialogTitle>Set object's owner</DialogTitle>
                    <DialogContent>
                        {this.state.state === "success"
                            ? <Typography>Object's owner updated successfully</Typography>
                            : this.state.state === "error"
                                ? <Typography>Error on update object's owner</Typography>
                                :   <React.Fragment>
                                        <Typography gutterBottom>
                                            By continuing with this procedure, the current object will be disabled and a new object will be created.
                                            The new object in addition to the owner set, will have a new id. The name of the object remains the same.
                                        </Typography>
                                        <Typography gutterBottom>
                                            WARNING: The created object will be accessible and visible in the object list
                                            <b>if and only if</b> the current user / service has at least <code>"Status"</code> access permission.
                                        </Typography>
                                        <Autocomplete
                                            id="sma_userId"
                                            value={this.state.owner}
                                            onChange={(event, newValue) => this.handleOwnerChange(event, newValue)}
                                            freeSolo
                                            options={this.defaultUsersList}
                                            getOptionLabel={(option) => {
                                                if (typeof option === 'string')
                                                    return option;
                                                
                                                return option.label
                                            }}
                                            renderInput={(params) => (
                                            <TextField
                                                {...params}
                                                label="New owner"
                                                InputProps={{ ...params.InputProps, type: 'user' }}
                                            />
                                            )}
                                        />
                                    </React.Fragment>
                        }
                        
                    </DialogContent>
                    
                    {this.state.state === "success" || this.state.state === "error"
                        ?
                            <DialogActions>
                                <Button onClick={() => this.handleClose()}>Ok</Button>
                            </DialogActions>
                        : 
                            <DialogActions>
                                <Button onClick={() => this.handleClose()}>Cancel</Button>
                                <Button variant="contained" color="primary" onClick={() => this.handleSave()}>Save</Button>
                            </DialogActions>
                    }
                </Dialog>
            </React.Fragment>
        );
    }

}

export class ObjectActionsRaw extends React.Component {

    constructor(props) {
        super(props);
        this.jcpUrl = props.jcpUrl;
        this.state = {
            anchorEl: null
        }
    }

    handleClick(event) {
        this.setState({anchorEl: event.currentTarget});
    }

    handleClose() {
        this.setState({anchorEl: null});
    }

    handleDownload() {
        const currentPath = location.pathname;
        const isEvents = currentPath.indexOf("events") > 0;
        const isPermissions = currentPath.indexOf("permissions") > 0;
        const isStats = currentPath.indexOf("stats") > 0;
        const isHistory = currentPath.indexOf("status") > 0;
        const isStruct = !isEvents && !isPermissions && !isStats && !isHistory;

        var fileName = "";
        var dataToDownload = "";
        const currentData = getDisplayedData();
        if (isStruct) {
            fileName = "obj_" + currentData.getId() + "_struct_" + getDateTimeCode() + ".json";
            console.log("Download object '" + currentData.getId() + "' structure to '" + fileName + "'");
            dataToDownload = JSON.stringify(currentData.toJSON(), null, 4);
        }
        else if (isStats) {
            fileName = "obj_" + currentData.id + "_stats_" + getDateTimeCode() + ".json";
            console.log("Download object '" + currentData.id + "' stats to '" + fileName + "'");
            dataToDownload = JSON.stringify(currentData, null, 4);
        }
        else if (isPermissions) {
            fileName = "obj_" + currentData.id + "_permissions_" + getDateTimeCode() + ".json";
            console.log("Download object '" + currentData.id + "' permissions to '" + fileName + "'");
            var json = new Object();
            json.perms = Array.from(currentData.perms);
            json.perms.forEach(function(perm, index, perms) {
                perms[index] = perm.toJSON();
            });
            dataToDownload = JSON.stringify(json, null, 4);
        }
        else if (isEvents) {
            fileName = "obj_" + currentData.id + "_events_" + getDateTimeCode() + ".json";
            console.log("Download object '" + currentData.id + "' events to '" + fileName + "'");
            var json = new Object();
            json.events = Array.from(currentData.events);
            // json.events.forEach(function(event, index, events) {
            //     events[index] = event.toJSON();
            // });
            dataToDownload = JSON.stringify(json, null, 4);
        }
        else if (isHistory) {
            fileName = "obj_" + currentData.id + "_" + currentData.compName + "_history_" + getDateTimeCode() + ".json";
            console.log("Download object '" + currentData.id + "'/'" + currentData.compName + "' str to '" + fileName + "'");
            var json = new Object();
            json.history = Array.from(currentData.history);
            // json.events.forEach(function(event, index, events) {
            //     events[index] = event.toJSON();
            // });
            dataToDownload = JSON.stringify(json, null, 4);
        }

        var download = function(filename, text) {
            var element = document.createElement('a');
            element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
            element.setAttribute('download', filename);
            
            element.style.display = 'none';
            document.body.appendChild(element);
            
            element.click();
            
            document.body.removeChild(element);
        }

        download(fileName,dataToDownload);
        this.handleClose();
    }

    render() {
        const currentPath = location.pathname;
        const isEvents = currentPath.indexOf("events") > 0;
        const isPermissions = currentPath.indexOf("permissions") > 0;
        const isStats = currentPath.indexOf("stats") > 0;
        const isHistory = currentPath.indexOf("status") > 0;
        const isStruct = !isEvents && !isPermissions && !isStats && !isHistory;
        
        return (
            <div style={{
                display: 'flex',
                flexWrap: 'nowrap',
                justifyContent: 'flex-end',
                width: isWidthDown('xs',this.props.width) ? '100%' : 'auto'
            }}>
                <Button color="primary" startIcon={<StructureIcon />} style={{marginLeft: '16px'}} disabled={isStruct}
                        component={Link} to={this.props.objUrl}>
                    <span style={{
                            display: isWidthDown('sm',this.props.width) ? 'none' : 'inherit'
                        }}>
                        Structure
                    </span>
                </Button>
                {this.props.permission === "CoOwner" ?
                    <Button color="primary" startIcon={<EventsIcon />} style={{marginLeft: '16px'}} disabled={isEvents}
                        component={Link} to={this.props.objUrl + "/events"}>
                        <span style={{
                                display: isWidthDown('sm',this.props.width) ? 'none' : 'inherit'
                            }}>
                            Events
                        </span>
                    </Button>
                    : null }


                <IconButton variant="contained" color="primary" size="medium" onClick={(event) => this.handleClick(event)}>
                    <MenuIcon />
                </IconButton>
                <Menu
                    anchorEl={this.state.anchorEl}
                    keepMounted
                    open={Boolean(this.state.anchorEl)}
                    onClose={() => this.handleClose()}
                    getContentAnchorEl={null}
                    anchorOrigin={{
                      vertical: 'bottom',
                      horizontal: 'right',
                    }}
                    transformOrigin={{
                      vertical: 'top',
                      horizontal: 'right',
                    }}
                >
                    <MenuItem component={Link} to={this.props.objUrl} onClick={() => this.handleClose()} selected={isStruct} style={{display: 'none'}} >
                        <ListItemIcon>
                            <StructureIcon fontSize="small" />
                        </ListItemIcon>
                        <ListItemText primary="Structure" />
                    </MenuItem>
                    {this.props.permission === "CoOwner" ?
                        <MenuItem component={Link} to={this.props.objUrl + "/events"} onClick={() => this.handleClose()} selected={isEvents} style={{display: 'none'}} >
                            <ListItemIcon>
                                <EventsIcon fontSize="small" />
                            </ListItemIcon>
                            <ListItemText primary="Events" />
                        </MenuItem>
                        : null }
                    {this.props.permission === "CoOwner" ?
                        <MenuItem component={Link} to={this.props.objUrl + "/permissions"} onClick={() => this.handleClose()} selected={isPermissions}>
                            <ListItemIcon>
                                <AccessControlIcon fontSize="small" />
                            </ListItemIcon>
                            <ListItemText primary="Access Control" />
                        </MenuItem>
                        : null }
                    <MenuItem component={Link} to={this.props.objUrl + "/stats"} onClick={() => this.handleClose()} selected={isStats}>
                            <ListItemIcon>
                                <ConfigsIcon fontSize="small" />
                            </ListItemIcon>
                        <ListItemText primary="Configs" />
                    </MenuItem>
                    <Divider />
                    <MenuItem onClick={() => this.handleDownload()} >
                        <ListItemIcon>
                            <DownloadIcon fontSize="small" />
                        </ListItemIcon>
                        <ListItemText primary="Download data" />
                        <a style={{display: "none"}}
                            download={isStruct ? "object_struct.json"
                                    : isEvents ? "object_events.json"
                                    : isPermissions ? "object_permissions.json"
                                    : isStats ? "object_stats.json"
                                    : isHistory ? "object_history.json"
                                    : "object.json" }
                            href={this.state.fileDownloadUrl}
                            ref={e=>this.dofileDownload = e}
                            >download it</a>
                    </MenuItem>
                </Menu>
            </div>
        );
    }

}
export const ObjectActions = withWidth()(ObjectActionsRaw)

export class ObjectMenu extends React.Component {

    constructor(props) {
        super(props);
        this.jcpUrl = props.jcpUrl;
        this.state = {
            anchorEl: null
        }
    }

    handleClick(event) {
        this.setState({anchorEl: event.currentTarget});
    }

    handleClose() {
        this.setState({anchorEl: null});
    }

    handleDownload() {
        const currentPath = location.pathname;
        const isEvents = currentPath.indexOf("events") > 0;
        const isPermissions = currentPath.indexOf("permissions") > 0;
        const isStats = currentPath.indexOf("stats") > 0;
        const isHistory = currentPath.indexOf("status") > 0;
        const isStruct = !isEvents && !isPermissions && !isStats && !isHistory;

        var fileName = "";
        var dataToDownload = "";
        const currentData = getDisplayedData();
        if (isStruct) {
            fileName = "obj_" + currentData.getId() + "_struct_" + getDateTimeCode() + ".json";
            console.log("Download object '" + currentData.getId() + "' structure to '" + fileName + "'");
            dataToDownload = JSON.stringify(currentData.toJSON(), null, 4);
        }
        else if (isStats) {
            fileName = "obj_" + currentData.id + "_stats_" + getDateTimeCode() + ".json";
            console.log("Download object '" + currentData.id + "' stats to '" + fileName + "'");
            dataToDownload = JSON.stringify(currentData, null, 4);
        }
        else if (isPermissions) {
            fileName = "obj_" + currentData.id + "_permissions_" + getDateTimeCode() + ".json";
            console.log("Download object '" + currentData.id + "' permissions to '" + fileName + "'");
            var json = new Object();
            json.perms = Array.from(currentData.perms);
            json.perms.forEach(function(perm, index, perms) {
                perms[index] = perm.toJSON();
            });
            dataToDownload = JSON.stringify(json, null, 4);
        }
        else if (isEvents) {
            fileName = "obj_" + currentData.id + "_events_" + getDateTimeCode() + ".json";
            console.log("Download object '" + currentData.id + "' events to '" + fileName + "'");
            var json = new Object();
            json.events = Array.from(currentData.events);
            // json.events.forEach(function(event, index, events) {
            //     events[index] = event.toJSON();
            // });
            dataToDownload = JSON.stringify(json, null, 4);
        }
        else if (isHistory) {
            fileName = "obj_" + currentData.id + "_" + currentData.compName + "_history_" + getDateTimeCode() + ".json";
            console.log("Download object '" + currentData.id + "'/'" + currentData.compName + "' str to '" + fileName + "'");
            var json = new Object();
            json.history = Array.from(currentData.history);
            // json.events.forEach(function(event, index, events) {
            //     events[index] = event.toJSON();
            // });
            dataToDownload = JSON.stringify(json, null, 4);
        }

        var download = function(filename, text) {
            var element = document.createElement('a');
            element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
            element.setAttribute('download', filename);
            
            element.style.display = 'none';
            document.body.appendChild(element);
            
            element.click();
            
            document.body.removeChild(element);
        }

        download(fileName,dataToDownload);
        this.handleClose();
    }

    render() {
        // const currentPath = location.pathname;
        // const isEvents = currentPath.indexOf("events") > 0;
        // const isPermissions = currentPath.indexOf("permissions") > 0;
        // const isStats = currentPath.indexOf("stats") > 0;
        // const isHistory = currentPath.indexOf("status") > 0;
        // const isStruct = !isEvents && !isPermissions && !isStats && !isHistory;

        const {isStruct, isEvents, isPermissions, isStats, isHistory } = getIsSection_Object()
        
        return (
            <div>
                <IconButton variant="contained" color="primary" size="medium" onClick={(event) => this.handleClick(event)}>
                    <MenuIcon />
                </IconButton>
                <Menu
                    anchorEl={this.state.anchorEl}
                    keepMounted
                    open={Boolean(this.state.anchorEl)}
                    onClose={() => this.handleClose()}
                    getContentAnchorEl={null}
                    anchorOrigin={{
                      vertical: 'bottom',
                      horizontal: 'right',
                    }}
                    transformOrigin={{
                      vertical: 'top',
                      horizontal: 'right',
                    }}
                >
                    <MenuItem component={Link} to={this.props.objUrl} onClick={() => this.handleClose()} selected={isStruct}>
                        <ListItemIcon>
                            <StructureIcon fontSize="small" />
                        </ListItemIcon>
                        <ListItemText primary="Structure" />
                    </MenuItem>
                    {this.props.permission === "CoOwner" ?
                        <MenuItem component={Link} to={this.props.objUrl + "/events"} onClick={() => this.handleClose()}  selected={isEvents}>
                            <ListItemIcon>
                                <EventsIcon fontSize="small" />
                            </ListItemIcon>
                            <ListItemText primary="Events" />
                        </MenuItem>
                        : null }
                    {this.props.permission === "CoOwner" ?
                        <MenuItem component={Link} to={this.props.objUrl + "/permissions"} onClick={() => this.handleClose()}  selected={isPermissions}>
                            <ListItemIcon>
                                <AccessControlIcon fontSize="small" />
                            </ListItemIcon>
                            <ListItemText primary="Access Control" />
                        </MenuItem>
                        : null }
                    <MenuItem component={Link} to={this.props.objUrl + "/stats"} onClick={() => this.handleClose()}  selected={isStats}>
                            <ListItemIcon>
                                <ConfigsIcon fontSize="small" />
                            </ListItemIcon>
                        <ListItemText primary="Configs" />
                    </MenuItem>
                    <Divider />
                    <MenuItem onClick={() => this.handleDownload()} >
                        <ListItemIcon>
                            <DownloadIcon fontSize="small" />
                        </ListItemIcon>
                        <ListItemText primary="Download data" />
                        <a style={{display: "none"}}
                            download={isStruct ? "object_struct.json"
                                    : isEvents ? "object_events.json"
                                    : isPermissions ? "object_permissions.json"
                                    : isStats ? "object_stats.json"
                                    : isHistory ? "object_history.json"
                                    : "object.json" }
                            href={this.state.fileDownloadUrl}
                            ref={e=>this.dofileDownload = e}
                            >download it</a>
                    </MenuItem>
                </Menu>
            </div>
        );
    }

}


// Shared Data

var _displayedDate = null;

function getDisplayedData() {
    return _displayedDate;
}

function setDisplayedData(displayedDate) {
    _displayedDate = displayedDate;
}


// Struct

export class ObjectStructPage extends ReactObjectPageBase {

    constructor(props) {
        super(props, "Structure");
        const object = this.getObject();
        this.state = {
            ...this.state,
            struct: object ? object.getStruct() : undefined,
            isFetching: object ? object.getStruct()==null : true,
        }
        setDisplayedData(object);
    }

    componentDidMount() {
        super.componentDidMount();
        if ((!this.state.struct)
            && this.isObjectInit()) {
            this.setState({isFetching: true});
            this.getObject().fetchStruct(true);
        }
    }


    // Object's events

    onAdded(jcpJSLWB, object) {
        //const object = this.getObject();
        if ((!this.state.struct)
            && this.isObjectInit()) {
            this.setState({isFetching: true});
            this.getObject().fetchStruct(true);
        } else {
            this.setState({
                struct: object.getStruct(),
                isFetching: false,
            });
            setDisplayedData(object);
        }
    }
    
    onStructUpd(jcpJSLWB, object) {
        //const object = this.getObject();
        this.setState({
            struct: object.getStruct(),
            isFetching: false,
        });
    }

    renderPage(object) {
        return (
            <React.Fragment>
                <ObjectInfoGrid jcpfe={this.jcpFE} objId={this.objId} />
                <ObjectContainerDetails jcpfe={this.jcpFE} objId={object.getId()} objUrl={this.objUrl} component={this.state.struct} expanded={true} />
            </React.Fragment>
        );
    }

}

export class ObjectInfoGrid extends ReactObjectBase {

    constructor(props) {
        super(props);
        const object = this.jcpFE.getObjects().getById(this.objId);
        if (object)
            this.state = {
                ...this.state,
                owner: object.getOwner(),
                permission: object.getPermission(),
                isConnected: object.isConnected(),
            }
    }


    // Object's events

    onAdded(jcpJSLWB, objects, objId) {
        const object = this.getObject();
        this.setState({
            owner: object.getOwner(),
            permission: object.getPermission(),
            isConnected: object.isConnected(),
        });
    }

    onConnected(jcpJSLWB, gwConnection, objId) {
        this.setState({
            isConnected: true,
        });
    }

    onDisconnected(jcpJSLWB, gwConnection, objId) {
        this.setState({
            isConnected: false,
        });
    }

    onInfoUpd(jcpJSLWB, objects, objId, key, value, old) {
        if (key === "Owner")
            this.setState({owner: value});
    }

    onPermSrvUpd(jcpJSLWB, objId, value, old) {
        this.setState({
            permission: value,
        });
    }

    render() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();
        if (!this.isObjectAllowed())
            return this.renderObjectNotAllowed();

        return (
            <Container style={{margin: '10px 0px'}}>
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <Typography variant="h6" component="h4">Info</Typography>
                    </Grid>
                    <Grid item xs={12} sm={6} md={4}>
                        <ValueField
                            title="Object owner" 
                            helperText="Owner of the object"
                            value={this.state.owner} />
                    </Grid>
                    <Grid item xs={12} sm={6} md={4}>
                        <ValueField
                            title="Service permission" 
                            helperText="Current service/user permission on object"
                            value={this.state.permission} />
                    </Grid>
                    <Grid item xs={12} sm={12} md={4}>
                        <ValueField
                            title="Is Connected" 
                            helperText="If current object is connected to current service"
                            value={this.state.isConnected} />
                    </Grid>
                </Grid>
            </Container>
        );
    }

}


// Events

import Paper from '@material-ui/core/Paper';
import Accordion from '@material-ui/core/Accordion';
import AccordionSummary from '@material-ui/core/AccordionSummary';
import AccordionDetails from '@material-ui/core/AccordionDetails';
import Collapse from '@material-ui/core/Collapse';
import Timeline from '@material-ui/lab/Timeline';
import TimelineItem from '@material-ui/lab/TimelineItem';
import TimelineSeparator from '@material-ui/lab/TimelineSeparator';
import TimelineConnector from '@material-ui/lab/TimelineConnector';
import TimelineContent from '@material-ui/lab/TimelineContent';
import TimelineOppositeContent from '@material-ui/lab/TimelineOppositeContent';
import TimelineDot from '@material-ui/lab/TimelineDot';
import {Doughnut} from 'react-chartjs-2';
import 'chartjs-adapter-moment';

// ToDo: assign an icon for each event type
import PowerSettingsNewIcon from '@material-ui/icons/PowerSettingsNew';
import PlayCircleFilledIcon from '@material-ui/icons/PlayCircleFilled';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';

export class ObjectEventsPageRaw extends ReactObjectPageBase {

    eventTypeIndexes = [
        'JOD_START',            // 'JOD_START'
        'JOD_STOP',             // 'JOD_STOP'
        'JOD_COMM_',            // 'JOD_COMM_JCP_CONN', 'JOD_COMM_JCP_DISC',
                                // 'JOD_COMM_LOC_START', 'JOD_COMM_LOC_STOP', 'JOD_COMM_LOC_CONN', 'JOD_COMM_LOC_DISC',
                                // 'JOD_COMM_CLOUD_CONN', 'JOD_COMM_CLOUD_DISC'
        'JOD_INFO_',            // 'JOD_INFO_UPD'
        'JOD_STRUCT_',          // 'JOD_STRUCT_LOAD'
        'JOD_STATUS_UPD',       // 'JOD_STATUS_UPD'
        'JOD_ACTION_',          // 'JOD_ACTION_REQ', 'JOD_ACTION_EXEC'
        'JOD_PERMS_',           // 'JOD_PERMS_LOAD', 'JOD_PERMS_ADD', 'JOD_PERMS_REM'
    ]

    eventTypeLabels = new Map([
        [this.eventTypeIndexes[0], 'StartUp'],
        [this.eventTypeIndexes[1], 'Shutdown'],
        [this.eventTypeIndexes[2], 'Communication'],
        [this.eventTypeIndexes[3], 'Info'],
        [this.eventTypeIndexes[4], 'Struct'],
        [this.eventTypeIndexes[5], 'Updates'],
        [this.eventTypeIndexes[6], 'Actions'],
        [this.eventTypeIndexes[7], 'Permissions'],
      ]);

    eventTypeColors = new Map([
        [this.eventTypeIndexes[0], '#FF9800'],      // Arancio
        [this.eventTypeIndexes[1], '#FF9800'],      // Arancio
        [this.eventTypeIndexes[2], '#FFEB3B'],      // Giallo
        [this.eventTypeIndexes[3], '#00BCD4'],      // Azzurro chiaro
        [this.eventTypeIndexes[4], '#2196F3'],      // Azzurro scuro
        [this.eventTypeIndexes[5], '#CDDC39'],      // Verde Chiaro
        [this.eventTypeIndexes[6], '#4CAF50'],      // Verde
        [this.eventTypeIndexes[7], '#9C27B0'],      // Viola
      ]);

    constructor(props) {
        super(props,"Events");
        this.state = {
            ...this.state,
            events: [],
            isFetching: true,
            expanded: []
        }
        setDisplayedData({id: this.props.objId, events: this.state.events});
    }

    componentDidMount() {
        super.componentDidMount();
        if (this.isObjectInit())
            this.fetchEvents();
    }

    // Object's events

    onAdded(jcpJSLWB, object) {
        if ((this.state.events.length == 0)
            && this.isObjectInit()) {
            this.fetchEvents();
        }
    }

    fetchEvents() {
        const thiz = this;
        this.getObject().fetchEvents(
            function onSuccess(object,events) {
                thiz.setState({
                    events: events,
                    isFetching: false,
                });
                setDisplayedData({id: thiz.props.objId, events: thiz.state.events});
                if (thiz.jcpFE.isSnackBar())
                    thiz.jcpFE.getSnackBar().showMessage("info",
                                        "Data fetched successfully",
                                        null,
                                        "Data events fetched successfully for '" + object.getId() + "' object.",
                                        null,1000);
            },
            function onError(object, xhttp, error) {
                thiz.setState({
                    events: [],
                    isFetching: false,
                });
                if (thiz.jcpFE.isSnackBar())
                    thiz.jcpFE.getSnackBar().showMessage("warning",
                                        "Error on fetch data",
                                        "Error on fetch events data for '" + object.getId() + "' object.",
                                        error);
            },
        );
    }

    _handleExpandClick = (id) => {
        const expanded = this.state.expanded;
        expanded[id] = expanded.hasOwnProperty(id) ? !expanded[id] : true
        this.setState({ expanded });
    };

    renderPage() {
        return (
            <React.Fragment>
                { /** EventFilter's Tag */}
                {this.state ?
                    this._objEventsList()
                    : null }
            </React.Fragment>
        );
    }

    _objEventsList() {
        const events = this.state.events;
        const expanded = this.state.expanded;

        // styles
        const styleTextTruncate = {whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis'};
        const styleTimelineOpposite = {width: '100px', maxWidth: '100px'};

        // sort
        function compareEmittedAt_Inverted(a, b) {
            if (a.emittedAt < b.emittedAt) { return 1; }
            if (a.emittedAt > b.emittedAt) { return -1; }
            return 0;
        }
        events.sort(compareEmittedAt_Inverted);

        // group
        function groupBy(list, keyGetter) {
            const arr = [];

            list.forEach((item) => {
                const key = keyGetter(item);
                const group = arr.find(function(group, index) {
                    if(group.key.getTime() === key.getTime())
                        return true;
                });
                if (!group) {
                    arr.push({key: key, events: [item]});
                } else {
                    group.events.push(item);
                }
            });

            return arr;
        }
        const groupByHours = groupBy(events, evnt => new Date(Math.trunc(evnt.emittedAt / 1000 / 60 / 60 ) * 1000 * 60 * 60 ));
        const groupByDays = groupBy(groupByHours, evnt => new Date(Math.trunc(evnt.key.getTime() / 1000 / 60 / 60 / 24 ) * 1000 * 60 * 60 * 24 ));
        for (const day of groupByDays) {
            for (const hour of day.events) {
                var evnts = new Map();
                var evntsNoError = new Map();
                var errors = new Map(evnts);
                this.eventTypeIndexes.forEach( x => {
                    evnts.set(x,0);
                    errors.set(x,0);
                });
                var errorsCount = 0;
                
                for (const event of hour.events) {
                    evnts.forEach((value,key) => {
                        if (event.type.startsWith(key)) {
                            evnts.set(key,value+1);
                            if (event.errorPayload == "null")
                                evntsNoError.set(key,value+1);
                            else {
                                errors.set(key,value+1);
                                errorsCount++;
                            }
                        }
                    });
                }
                hour.stats = {
                    eventsTypeCount: evnts,
                    eventsNoErrorTypeCount: evntsNoError,
                    errorsTypeCount: errors,

                    eventsCount: hour.events.length,
                    errorsCount: errorsCount,

                    startupEventsCount: evnts.get('JOD_START') + errors.get('JOD_START')
                                      + evnts.get('JOD_STOP') + errors.get('JOD_STOP'),
                    startupErrorsCount: errors.get('JOD_START')
                                      + errors.get('JOD_STOP'),

                    communicationEventsCount: evnts.get('JOD_COMM_') + errors.get('JOD_COMM_'),
                    communicationErrorsCount: errors.get('JOD_COMM_'),

                    objectEventsCount: evnts.get('JOD_INFO_') + errors.get('JOD_INFO_')
                                     + evnts.get('JOD_STRUCT_') + errors.get('JOD_STRUCT_'),
                    objectErrorsCount: errors.get('JOD_INFO_')
                                     + errors.get('JOD_STRUCT_'),

                    statusEventsCount: evnts.get('JOD_STATUS_UPD') + errors.get('JOD_STATUS_UPD'),
                    statusErrorsCount: errors.get('JOD_STATUS_UPD'),

                    actionsEventsCount: evnts.get('JOD_ACTION_') + errors.get('JOD_ACTION_'),
                    actionsErrorsCount: errors.get('JOD_ACTION_'),

                    permissionsEventsCount: evnts.get('JOD_PERMS_') + errors.get('JOD_PERMS_'),
                    permissionsErrorsCount: errors.get('JOD_PERMS_'),
                }
            }
        }
        
        // render
        const getColor = type => {
            for (var [key, value] of this.eventTypeColors)
                if (type.startsWith(key))
                    return value;
        }
        const getEventTypeIcon = type => {
            const color = getColor(type);
            const style = { color: color, fontSize: 30, margin: '10px' };
            const icon = <PlayCircleFilledIcon style={style}/>;
            return (
                <Tooltip title={type}>
                    {icon}
                </Tooltip>
            );
        }
        this.eventInternalCounter = 0;
        const getEventPaper = (evnt) => (
            <Paper key={evnt.id + "-" + this.eventInternalCounter++} elevation={3}
            style={{
                margin: '20px', 
                marginTop: '0px', 
                padding: '20px', 
                }}>
                <div style={{display: 'flex', flexDirection: 'row', flexWrap: 'nowrap', alignItems: 'center', margin: '0px 0px'}}>
                    {getEventTypeIcon(evnt.type)}
                    <div style={{display: 'flex', flexDirection: 'column', flexWrap: 'nowrap', width: '100%'}}>
                        <div style={{display: 'flex', flexDirection: 'row'}}>
                            <Typography><b>{evnt.type}</b> <Typography color="textSecondary" display="inline" component="span">has</Typography> {evnt.phase}</Typography>
                        </div>

                        <Typography color="textSecondary" style={{fontFamily: 'monospace'}}>
                            Id:  {evnt.id}<br/>
                            Date: {new Date(evnt.emittedAt).toLocaleString()}
                        </Typography>

                        {evnt.payload!=null ? <Tooltip title={evnt.payload}><Typography style={{...styleTextTruncate, maxWidth: '300px', fontFamily: 'monospace'}}>Payload: {evnt.payload}</Typography></Tooltip> : null}
                        {evnt.errorPayload!=null && evnt.errorPayload!=='null' ? <Tooltip title={evnt.errorPayload}><Typography color="error" style={{...styleTextTruncate, maxWidth: '300px', fontFamily: 'monospace'}}>Error: {evnt.errorPayload}</Typography></Tooltip> : ""}
                    </div>
                </div>
            </Paper>
        );

        // Accordion + Timeline
        return (
            <div>

                {groupByDays.map(dayGroup => { return (
                    <Accordion key={dayGroup.key.toString()} >
                        <AccordionSummary
                            expandIcon={<ExpandMoreIcon />}
                            >
                            <Typography variant="h6" component="h4">
                                {getDayString(dayGroup.key)}
                            </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                            <Timeline style={{margin: '0px', padding: '1em', width: '100%', overflow: 'scroll'}}>

                                {dayGroup.events.map(hourGroup => { return (
                                    <React.Fragment>

                                        <TimelineItem>
                                            <TimelineOppositeContent style={{flex: 'unset'}} />
                                            <TimelineSeparator>
                                                <TimelineDot color="primary"style={{margin: '12px 0px'}} />
                                                <TimelineConnector />
                                            </TimelineSeparator>

                                            <TimelineContent>
                                                <Typography display="block" gutterBottom component="h5" variant="h6" style={{fontSize: '1.15rem'}}>
                                                    {this._getEventRange(hourGroup.key)}
                                                </Typography>

                                                <div style={{
                                                    display: 'flex',
                                                    flexWrap: isWidthUp('sm',this.props.width) ? 'nowrap' : 'wrap',
                                                    justifyContent: 'flex-start'
                                                    }}>

                                                    <Paper elevation={3} style={{
                                                        margin: '20px', 
                                                        marginTop: '0px', 
                                                        padding: '20px', 
                                                        width: isWidthUp('sm',this.props.width) ? '30%' : 'unset',
                                                        display: 'flex', 
                                                        flexDirection: 'column', 
                                                        justifyContent: 'space-between'
                                                        }}>
                                                        <Typography gutterBottom variant="button" style={{color: '#0000008a'}}>
                                                            Total events
                                                        </Typography>
                                                        <Typography gutterBottom style={{paddingLeft: '20px', fontWeight: 'bold', fontSize: '2em'}}>
                                                            {hourGroup.stats.eventsCount}
                                                        </Typography>
                                                        <Typography >
                                                            of which <span style={hourGroup.stats.errorsCount>0 ? {color: 'red'} : {}}>
                                                                    {hourGroup.stats.errorsCount} errors
                                                                </span>
                                                        </Typography>
                                                    </Paper>

                                                    <Paper elevation={3} style={{
                                                        margin: '20px', 
                                                        marginTop: '0px', 
                                                        padding: '20px', 
                                                        width: isWidthUp('sm',this.props.width) ? '30%' : 'unset',
                                                        display: 'flex', 
                                                        flexDirection: 'column', 
                                                        justifyContent: 'space-between'
                                                        }}>
                                                        <Typography gutterBottom variant="button" style={{color: '#0000008a'}}>
                                                            Upds / Cmds
                                                        </Typography>
                                                        <Typography gutterBottom style={{paddingLeft: '20px', fontWeight: 'bold', fontSize: '2em'}}>
                                                            {hourGroup.stats.statusEventsCount} / {hourGroup.stats.actionsEventsCount}
                                                        </Typography>
                                                        <Typography >
                                                            of which  <span style={hourGroup.stats.statusErrorsCount + hourGroup.stats.actionsErrorsCount>0 ? {color: 'red'} : {}}>
                                                                    {hourGroup.stats.statusErrorsCount + hourGroup.stats.actionsErrorsCount} errors
                                                                </span>
                                                        </Typography>
                                                    </Paper>

                                                    <Paper elevation={3} style={{
                                                        margin: '20px', 
                                                        marginTop: '0px', 
                                                        padding: '20px', 
                                                        width: isWidthUp('sm',this.props.width) ? '30%' : 'unset',
                                                        display: 'flex', 
                                                        flexDirection: 'column', 
                                                        justifyContent: 'space-between'
                                                        }}>
                                                        <Typography gutterBottom variant="button" style={{color: '#0000008a'}}>
                                                            Events types
                                                        </Typography>
                                                        <div style={{width: '100%', height: '50%', margin: 'auto'}}>
                                                            {this.getDoughnut(hourGroup.stats.eventsTypeCount,false)}
                                                        </div>
                                                    </Paper>
                                                </div>


                                                <Button color="primary" onClick={() => this._handleExpandClick(hourGroup.key)} >
                                                    <Typography color="textSecondary" style={{fontFamily: 'monospace'}} display="block" gutterBottom >
                                                        {this.state.expanded[hourGroup.key] ? "Hide events list..." : "Show events list..."}
                                                    </Typography>
                                                </Button>
                                                <Collapse in={expanded[hourGroup.key]} timeout="auto" unmountOnExit>
                                                    {hourGroup.events.map(evnt => getEventPaper(evnt))}
                                                </Collapse>

                                            </TimelineContent>

                                        </TimelineItem>
                                    </React.Fragment>
                                )})}

                            </Timeline>
                        </AccordionDetails>
                    </Accordion>
                )})}
            </div>
        );
    }

    getDoughnut(typesCount, isError) {
        const data = {
            labels: Array.from(this.eventTypeLabels.values()),
            datasets: [{
                data: Array.from(typesCount.values()),
                backgroundColor: Array.from(this.eventTypeColors.values()),
                hoverOffset: 4,
                circumference: 360
              }]
          }
          const options = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: isError,
                    position: 'right'
                }
            }
          }
        return (
            <Doughnut data={data} options={options} />
        );
    }

    _getEventRange(keyDate) {
        const startDate = keyDate;
        const endDate = new Date(keyDate);
        endDate.setHours(endDate.getHours()+1);
        return getHourString(startDate) + " > " + getHourString(endDate);
    }

}
export const ObjectEventsPage = withWidth()(ObjectEventsPageRaw)


// History

export class ComponentHistoryPage extends ReactObjectComponentBase {

    constructor(props) {
        super(props);
        this.state = {
            ...this.state,
            history: [],
            isFetching: true
        }
        setDisplayedData({id: this.props.objId, compName: this.getComponent() ? this.getComponent().getComponentPath() : "", history: this.state.history});
    }

    componentDidMount() {
        super.componentDidMount();
        if (this.isObjectInit()
          && this.getObject().isStructInit())
            this.fetchHistory();
    }

    // Object's events

    onAdded(jcpJSLWB, object) {
        if ((this.state.history.length == 0)
            && this.isObjectInit()
            && this.getObject().isStructInit())
            this.fetchHistory();
    }

    onComponentInit() {
        if ((this.state.history.length == 0)
            && this.isObjectInit()
            && this.getObject().isStructInit())
            this.fetchHistory();
    }

    fetchHistory() {
        const thiz = this;
        this.getComponent().fetchHistory(
            function onSuccess(component,history) {
                thiz.setState({
                    history: history,
                    isFetching: false,
                });
                setDisplayedData({id: thiz.props.objId, compName: thiz.getComponent() ? thiz.getComponent().getComponentPath() : "", history: thiz.state.history});
                if (thiz.jcpFE.isSnackBar())
                    thiz.jcpFE.getSnackBar().showMessage("info",
                                        "Data fetched successfully",
                                        null,
                                        "Data history fetched successfully for '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.",
                                        null,1000);
            },
            function onError(component, xhttp, error) {
                thiz.setState({
                    history: [],
                    isFetching: false,
                });
                if (thiz.jcpFE.isSnackBar())
                    thiz.jcpFE.getSnackBar().showMessage("warning",
                                        "Error on fetch data",
                                        "Error on fetch history data for '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.",
                                        error);
            },
        );
    }

    renderComponentHistoryFetching() {
        return (
            <Container>
                <Typography variant="body2" component="p">
                    Loading Component {this.compPath} history...
                </Typography>
            </Container>
        );
    }

    _renderCard(content) {
      return (
        <Container>
            <Card style={{margin:'20px 0'}}>
                <CardContent>
                    {content}
                </CardContent>
            </Card>
        </Container>
      );
    }

    render() {
        if (this.state.isFetching)
            return this._renderCard(this.renderComponentHistoryFetching());
        if (!this.isObjectInit())
            return this._renderCard(this.renderObjectNotInit());
        if (!this.isObjectAllowed())
            return this._renderCard(this.renderObjectNotAllowed());
        if (!this.isComponentInit())
            return this._renderCard(this.renderComponentNotInit());

        const type = this.getComponent().getType();
        const minDate = this.state.history[0] ? new Date(this.state.history[0].updatedAt) : new Date();
        const maxDate = this.state.history[0] ? new Date(this.state.history[this.state.history.length-1].updatedAt) : new Date();

        var dataStatus = [];
        for (var i = 0; i < this.state.history.length; i++) {
            var status = this._extractStatusFromPayload(this.state.history[i].payload,this.state.history[i].compType);
            if (status==='true') status=1;
            if (status==='false') status=0;
            dataStatus[i] = { x: new Date(this.state.history[i].updatedAt), y: status };
            //dataStatus[i] = status;
        }
        const chartData = {
            datasets: [{
                label: 'Statuses',
                data: dataStatus,
                backgroundColor: [ 'rgba(92,158,194,0.2)' ],
                borderColor:     [ 'rgba(92,158,194,1)' ],
                borderWidth:     1,
                stepped:         (type==="BooleanState" || type==="BooleanAction"),
                lineTension:     0.1
            }]
        };
        const chartOptions = {
            scales: {
                x: {
                    type: 'time',
                    offset: true,
                    distribution: 'linear',
                    min: minDate,
                    max: maxDate
                },
                y: {
                    //offset: true,
                    min: (type==="BooleanState" || type==="BooleanAction") ? -0.5 : undefined,
                    max: (type==="BooleanState" || type==="BooleanAction") ? 1.5 : undefined
                }
            }
        };

        return (
            <Container>
                <Card style={{margin:'20px 0'}}>
                    <CardContent>
                        {this.state.isFetching
                            ? <Typography variant="h5" component="h3">Loading...</Typography>
                            : (
                                <div>
                                    <Typography variant="h5" component="h3">Component '{this.props.compPath}' History</Typography>
                                    { /** HistoryFilter's Tag */}
                                    <Line
                                        data={chartData}
                                        options={chartOptions}
                                        />
                                </div>
                            )
                        }
                    </CardContent>
                </Card>
            </Container>
        );
    }

    _extractStatusFromPayload(payload,compType) {
        if (compType === "BooleanState"
          || compType === "BooleanAction"
          || compType === "RangeState"
          || compType === "RangeAction") {
          return payload.substring("new:".length,payload.indexOf(','));
    
        } else if (compType === "Container") {
            return "error: container has no status";
        }
    }

}


// Permissions

export class ObjectPermissionsPage extends ReactObjectPageBase {

    constructor(props) {
        super(props,"Permissions");
        const object = this.getObject();
        this.state = {
            ...this.state,
            id: object ? object.getId() : undefined,
            perms: object ? object.getPerms() : undefined,
            isFetching: object ? object.getPerms()==null : true,
            permission: object ? object.getPermission() : undefined
        }
        setDisplayedData({id: this.state.id, perms: this.state.perms});
    }

    componentDidMount() {
        super.componentDidMount();
        if ((!this.state.perms)
            && this.isObjectInit()) {
            this.setState({isFetching: true});
            this.getObject().fetchPerms(true);
        }
    }

    onAdded(jcpJSLWB, object) {
        //const object = this.getObject();

        if ((!this.state.perms)                     // ...is it real required? see ObjectEvents onAdded()
            && this.isObjectInit()) {
            this.setState({isFetching: true});
            this.getObject().fetchPerms(true);
        } else {
            this.setState({
                id: object.getId(),
                perms: object.getPerms(),
                isFetching: false,
                permission: object.getPermission(),
            });
            setDisplayedData({id: this.state.id, perms: this.state.perms});
        }
    }
    
    onPermsUpd(jcpJSLWB, object) {
        //const object = this.getObject();
        this.setState({
            id: object.getId(),
            perms: object.getPerms(),
            isFetching: false,
            permission: object.getPermission(),
        });
        setDisplayedData({id: this.state.id, perms: this.state.perms});
    }

    handleUpdate(perm) {
                    const thiz = this;
                    perm.duplicate(
                        function onSuccess(perm,response) {
                            this.fetchPermissions();
                            if (thiz.jcpFE.isSnackBar())
                                thiz.jcpFE.getSnackBar().showMessage("success",
                                                    "Permissions updated successfully",
                                                    "Permission '" + perm.getId() + "' updated successfully on '" + perm.getObjId() + "' object.");
                            console.log("Permission '" + perm.id + "' duplicated for '" + perm.objId + "' object.");
                        },
                        function onError(perm, xhttp, error) {
                            if (thiz.jcpFE.isSnackBar())
                                thiz.jcpFE.getSnackBar().showMessage("error",
                                                    "Error on update object's permissions",
                                                    "Error on update permission '" + perm.getId() + "' on '" + perm.getObjId() + "' object.",
                                                    error);
                            const response = JSON.parse(xhttp.responseText);
                            console.warn("Error on duplicate permission '" + perm.id + "' for '" + perm.objId + "' object.\n" + response.error + ": " + response.message);
                        }
                    )
    }

    handleDuplicate(perm) {
        const thiz = this;
        perm.duplicate(
            function onSuccess(perm,response) {
                if (thiz.jcpFE.isSnackBar())
                    thiz.jcpFE.getSnackBar().showMessage("success",
                                        "Permissions updated successfully",
                                        "Permission '" + perm.getId() + "' duplicated successfully on '" + perm.getObjId() + "' object.");
                console.log("Permission '" + perm.getId() + "' duplicated for '" + perm.getObjId() + "' object.");
            },
            function onError(perm, xhttp, error) {
                if (thiz.jcpFE.isSnackBar())
                    thiz.jcpFE.getSnackBar().showMessage("error",
                                        "Error on update object's permissions",
                                        "Error on duplicate permission '" + perm.getId() + "' on '" + perm.getObjId() + "' object.",
                                        error);
                console.warn("Error on duplicate permission '" + perm.getId() + "' for '" + perm.getObjId() + "' object.\n" + error);
            }
        )
    }

    handleDelete(perm) {
        const thiz = this;
        perm.delete(
            function onSuccess(perm,response) {
                if (thiz.jcpFE.isSnackBar())
                    thiz.jcpFE.getSnackBar().showMessage("success",
                                        "Permissions updated successfully",
                                        "Permission '" + perm.getId() + "' deleted successfully on '" + perm.getObjId() + "' object.");
                console.log("Permission '" + perm.getId() + "' deleted from '" + perm.getObjId() + "' object.");
            },
            function onError(perm, xhttp, error) {
                if (thiz.jcpFE.isSnackBar())
                    thiz.jcpFE.getSnackBar().showMessage("error",
                                        "Error on update object's permissions",
                                        "Error on delete permission '" + perm.getId() + "' on '" + perm.getObjId() + "' object.",
                                        error);
                console.error("Error on delete permission '" + perm.getId() + "' for '" + perm.getObjId() + "' object.\n" + error);
            }
        )
    }

    handleMenuClick(event) {
        this.setState({menuActions: event.currentTarget});
    };

    handleMenuClose() {
        this.setState({menuActions: null});
    };

    renderPage() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();

        const object = this.getObject();
        if (this.state.perms) {
            var perms = Array.from(this.state.perms);
            perms = perms.map((x) => {
                var x1 = new Object;
                Object.assign(x1,x.getInstance());
                x1.Perm = x;
                return x1;
            });
        }
        else
            perms = [];
            
        const columns = [
            { field: 'actions', headerName: 'Actions', width: 80, sortable: false,
            renderCell: (params) => (
                <strong>
                    <IconButton variant="contained" color="primary" size="small" onClick={() => this.handleDuplicate(params.row.Perm)}>
                        <Tooltip title="Duplicate"><DuplicateIcon /></Tooltip>
                    </IconButton>
                    <IconButton variant="contained" color="primary" size="small" onClick={() => this.handleDelete(params.row.Perm)}>
                        <Tooltip title="Delete"><DeleteIcon /></Tooltip>
                    </IconButton>
                </strong>
            )},
            { field: 'connection', headerName: 'Connection', width: 140 },
            { field: 'type', headerName: 'Access Type', width: 140 },
            { field: 'srvId', headerName: 'Service', width: 200 },
            { field: 'usrId', headerName: 'User', width: 200 },
            { field: 'lastUpdateDate', headerName: 'Laste Update', width: 200, type: 'dateTime',
            valueGetter: (params) => (new Date(params.row.lastUpdate))},
            { field: 'id', headerName: 'ID', width: 230 },
            
        ];

        return (
            <div>
                <Button variant="contained" color="primary" startIcon={<MoreIcon />} onClick={(event) => this.handleMenuClick(event)}>
                    Share with
                </Button>
                <Menu
                    anchorEl={this.state.menuActions}
                    keepMounted
                    open={Boolean(this.state.menuActions)}
                    onClose={() => this.handleMenuClose()}
                    getContentAnchorEl={null}
                    anchorOrigin={{
                        vertical: 'bottom',
                        horizontal: 'left',
                    }}
                    transformOrigin={{
                        vertical: 'top',
                        horizontal: 'left',
                    }}
                    >
                    <ShareMenuAction jcpfe={this.jcpFE} object={object} handleClose={() => this.handleMenuClose()} />
                    <ShareMenuAction jcpfe={this.jcpFE} object={object} handleClose={() => this.handleMenuClose()} 
                        menuText="Make object public locally" connType="OnlyLocal" permType="Actions" srvId="#All" usrId="#All" />
                    <ShareMenuAction jcpfe={this.jcpFE} object={object} handleClose={() => this.handleMenuClose()} 
                        menuText="Make object public globally" connType="LocalAndCloud" permType="Status" srvId="#All" usrId="#All" />
                </Menu>
                {this.state.permission==="CoOwner"
                    ? <span style={{float: 'right'}}>
                        <ObjectOwnerEdit_Dialog jcpfe={this.jcpFE} objId={this.state.id} />
                    </span>
                    : null }
                <div style={{height: '400px', margin: '0px', padding: '1em'}} >
                    {perms
                        ?
                            <DataGrid
                                rows={perms}
                                columns={columns}
                                pageSize={5}
                                disableColumnMenu
                                disableClickEventBubbling />
                        :
                            <Typography>No permissions</Typography>
                    }
                </div>
            </div>
        );
    }

}

class ShareMenuAction extends React.Component {

    defaultServicesList = [{label: "#All (All services)", value: "#All"}];
    defaultUsersList = [{label: "#All (All users)", value: "#All"}, {label: "#Owners (All owners)", value: "#Owners"}];

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
        this.object = props.object;
        this.menuText = props.menuText ? props.menuText : "Share with a friend";
        this.apiUrl = this.props.apiUrl;
        this.state = {
            open: false,
            connType: props.connType ? props.connType : "",
            permType: props.permType ? props.permType : "",
            srvId: props.srvId ? props.srvId : "",
            usrId: props.usrId ? props.usrId : "",
        }
    }

    setOpen(open) {
        this.setState({
            open: open,
            connType: this.props.connType ? this.props.connType : "",
            permType: this.props.permType ? this.props.permType : "",
            srvId: this.props.srvId ? this.props.srvId : "",
            usrId: this.props.usrId ? this.props.usrId : "",
        });
    }

    setConnType(connType) {
        this.setState({connType: connType});
    }

    setPermType(permType) {
        this.setState({permType: permType});
    }

    setSrvId(srvId) {
        this.setState({srvId: srvId});
    }

    setUsrId(usrId) {
        this.setState({usrId: usrId});
    }

    handleClickOpen() {
        this.props.handleClose();
        this.setOpen(true);
    };

    handleCancel() {
        this.setConnType("");
        this.setPermType("");
        this.setSrvId("");
        this.setUsrId("");
        this.setOpen(false);
    };

    handleSubmit(){
        if (this.state.connType === "") return this.onError("'Connection Type' can't be empty");
        if (this.state.permType === "") return this.onError("'Permission Type' can't be empty");
        if (this.state.srvId === "") return this.onError("'Service ID' can't be empty");
        if (this.state.usrId === "") return this.onError("'User ID' can't be empty");
        
        this.object.createPermission(
            this.state.srvId, this.state.usrId, this.state.permType, this.state.connType,
            this.onSuccess, this.onError);

        this.setConnType("");
        this.setPermType("");
        this.setSrvId("");
        this.setUsrId("");
        this.setOpen(false);
    };

    onError(jcpJSLWB,APIObject,error) {
        console.log("Error '" + error + "' on create permission");
    }

    onSuccess(APIObject,success) {}

    handleConnTypeChange(event) {
        this.setConnType(event.target.value);
        this.tryEnableCreateButton();
    };

    handlePermTypeChange(event) {
        this.setPermType(event.target.value);
        this.tryEnableCreateButton();
    };

    handleSrvIdChange(event, newValue) {
        // newValue is a valid service id?

        if (typeof newValue === 'string' && newValue !== "") {
            newValue = {
                label: newValue,
                value: newValue
            }
        }

        this.setSrvId(newValue);
        this.tryEnableCreateButton();
    };

    handleUsrIdChange(event,newValue) {
        // newValue is a valid user id?

        if (typeof newValue === 'string' && newValue !== "") {
            newValue = {
                label: newValue,
                value: newValue
            }
        }

        this.setUsrId(newValue);
        this.tryEnableCreateButton();
    };

    tryEnableCreateButton() {
    }

    render() {
        const isFilled = this.state.connType && this.state.connType !== "" &&
                        this.state.permType && this.state.permType !== "" &&
                        this.state.srvId && this.state.srvId !== "" &&
                        this.state.usrId && this.state.usrId !== "";

        return (
            <React.Fragment>
                <MenuItem onClick={() => this.handleClickOpen()}>{this.menuText}</MenuItem>
                <Dialog
                    open={this.state.open} 
                    onClose={() => this.handleCancel()}
                    scroll="paper"
                    fullWidth={false}
                    maxWidth="md"
                    disableBackdropClick
                    disableEscapeKeyDown >
                    <DialogTitle>Share object '{this.props.object.name}' with a friend</DialogTitle>
                    <DialogContent>
                        <form>
                            <table>
                                <tbody>
                                    <tr>
                                        <td style={{padding: '20px'}}>
                                            <FormControl>
                                                <InputLabel id="sma_connectionType_label">Connection Type</InputLabel>
                                                <Select
                                                    labelId="sma_connectionType_label"
                                                    id="sma_connectionType"
                                                    value={this.state.connType}
                                                    onChange={(event) => this.handleConnTypeChange(event)}
                                                    input={<Input />}
                                                    style={{width: "200px"}}
                                                >
                                                    <MenuItem value={"OnlyLocal"}>Only Local</MenuItem>
                                                    <MenuItem value={"LocalAndCloud"}>Local & Cloud</MenuItem>
                                                </Select>
                                            </FormControl>
                                        </td>
                                        <td style={{padding: '20px'}}>
                                            <DialogContentText>
                                                Select 'Local & Cloud' to apply this permission when requests are
                                                coming for both cloud and local services. Set it to 'Only Local'
                                                to apply this permission only on local services connections.
                                            </DialogContentText>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style={{padding: '20px'}}>
                                            <FormControl>
                                                <InputLabel id="sma_accessType_label">Access Type</InputLabel>
                                                <Select
                                                    labelId="sma_accessType_label"
                                                    id="sma_accessType"
                                                    value={this.state.permType}
                                                    onChange={(event) => this.handlePermTypeChange(event)}
                                                    input={<Input />}
                                                    style={{width: "200px"}}
                                                >
                                                    <MenuItem value={"None"}><em>None</em></MenuItem>
                                                    <MenuItem value={"Status"}>State</MenuItem>
                                                    <MenuItem value={"Actions"}>Action</MenuItem>
                                                    <MenuItem value={"CoOwner"}>CoOwner</MenuItem>
                                                </Select>
                                            </FormControl>
                                        </td>
                                        <td style={{padding: '20px'}}>
                                            <DialogContentText>
                                                'State' allow user/service to get object's structure and receive status updates<br />
                                                'Action' same as 'State' plus send actions requests<br />
                                                'CoOwner' same as 'Action' plus set object's name/owner and manage his permissions<br />
                                                'None' block user/service to access on this object<br />
                                            </DialogContentText>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style={{padding: '20px'}}>
                                            <InputLabel id="sma_serviceId_label">Service</InputLabel>
                                            <FormControl>
                                                <Autocomplete
                                                    id="sma_serviceId"
                                                    value={this.state.srvId}
                                                    onChange={(event, newValue) => this.handleSrvIdChange(event, newValue)}
                                                    freeSolo
                                                    options={this.defaultServicesList}
                                                    getOptionLabel={(option) => {
                                                        if (typeof option === 'string')
                                                            return option;
                                                        
                                                        return option.label
                                                    }}
                                                    renderInput={(params) => (
                                                    <TextField
                                                        {...params}
                                                        InputProps={{ ...params.InputProps, type: 'service' }}
                                                    />
                                                    )}
                                                    style={{width: "200px"}}
                                                />
                                            </FormControl>
                                        </td>
                                        <td style={{padding: '20px'}}>
                                            <DialogContentText>
                                                Set the service's id to associate with this permission.<br />
                                                Or set '#All' to apply this permission on all services requests.
                                            </DialogContentText>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style={{padding: '20px'}}>
                                            <InputLabel id="sma_userId_label">User</InputLabel>
                                            <FormControl>
                                                <Autocomplete
                                                    id="sma_userId"
                                                    value={this.state.usrId}
                                                    onChange={(event, newValue) => this.handleUsrIdChange(event, newValue)}
                                                    freeSolo
                                                    options={this.defaultUsersList}
                                                    getOptionLabel={(option) => {
                                                        if (typeof option === 'string')
                                                            return option;
                                                        
                                                        return option.label
                                                    }}
                                                    renderInput={(params) => (
                                                    <TextField
                                                        {...params}
                                                        InputProps={{ ...params.InputProps, type: 'user' }}
                                                    />
                                                    )}
                                                    style={{width: "200px"}}
                                                />
                                            </FormControl>
                                        </td>
                                        <td style={{padding: '20px'}}>
                                            <DialogContentText>
                                                Set the user's id to associate with this permission.<br />
                                                Set '#All' to apply this permission on all users requests.
                                                Or set '#Owner' to apply this permission on object's owners requests.
                                            </DialogContentText>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </form>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => this.handleCancel()}>
                            Cancel
                        </Button>
                        <Button onClick={() => this.handleSubmit()} color="primary" disabled={!isFilled}>
                            Create
                        </Button>
                    </DialogActions>
                </Dialog>
            </React.Fragment>
        );
    }
}


// Stats

export class ObjectStatsPage extends ReactObjectBase {

    constructor(props) {
        super(props);
        const object = this.jcpFE.getObjects().getById(this.objId);
        if (object) {
            this.state = {
                ...this.state,
                id: object.getId(),
                name: object.getName(),
                isConnected: object.isConnected(),
                model: object.getModel(),
    
                owner: object.getOwner(),
                permission: object.getPermission(),
                jodVersion: object.getJODVersion(),
    
                isCloudConnected: object.isCloudConnected(),
                isLocalConnected: object.isLocalConnected(),
            }
            setDisplayedData(this.state);
        }
    }


    // Object's events

    onAdded(jcpJSLWB, object) {
        //const object = this.getObject();
        this.setState({
            id: object.getId(),
            name: object.getName(),
            isConnected: object.isConnected(),
            model: object.getModel(),

            owner: object.getOwner(),
            permission: object.getPermission(),
            jodVersion: object.getJODVersion(),

            isCloudConnected: object.isCloudConnected(),
            isLocalConnected: object.isLocalConnected(),
        });
        setDisplayedData(this.state);
    }

    onConnected(jcpJSLWB, object) {
        this.setState({
            isConnected: true,
            isCloudConnected: true,
        });
        setDisplayedData(this.state);
    }

    onDisconnected(jcpJSLWB, object) {
        this.setState({
            isConnected: false,
            isCloudConnected: false,
        });
        setDisplayedData(this.state);
    }

    onInfoUpd(jcpJSLWB, object, key, value, old) {
        if (key === "Name")
            this.setState({name: value});
        else if (key === "Model")
            this.setState({model: value});
        setDisplayedData(this.state);
    }

    render() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();
        if (!this.isObjectAllowed())
            return this.renderObjectNotAllowed();

        return (
            <Container>
                <Card style={{margin:'20px 0'}}>
                    <CardContent>
                        <Grid container spacing={3}>
                            <Grid item xs={12} sm={12}>
                                <Typography variant="h5" component="h3">Info</Typography>
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <ValueField
                                    title="Object id"
                                    helperText="Unique id for object"
                                    value={this.state.id} />
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <ValueField
                                    title="Object model" 
                                    helperText="Current object's model"
                                    value={this.state.model} />
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <ValueField
                                    title="Object name"
                                    helperText="Readable object's name"
                                    value={this.state.name} />
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <ValueField
                                    title="JOD Version" 
                                    helperText="JOD version running on object"
                                    value={this.state.jodVersion} />
                            </Grid>
                        </Grid>
                    </CardContent>
                </Card>
                <Card style={{margin:'20px 0'}}>
                    <CardContent>
                        <Grid container spacing={3}>
                            <Grid item xs={12} sm={12}>
                                <Typography variant="h5" component="h3">Access control</Typography>
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <ValueField
                                    title="Object owner" 
                                    helperText="Owner of the object"
                                    value={this.state.owner} />
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <ValueField
                                    title="Service permission" 
                                    helperText="Current service/user permission on object"
                                    value={this.state.permission} />
                            </Grid>
                        </Grid>
                    </CardContent>
                </Card>
                <Card style={{margin:'20px 0'}}>
                    <CardContent>
                        <Grid container spacing={3}>
                            <Grid item xs={12} sm={12} md={12}>
                                <Typography variant="h5" component="h3">Connection</Typography>
                            </Grid>
                            <Grid item xs={12} sm={6} md={4}>
                                <ValueField
                                    title="Is Connected" 
                                    helperText="If current object is connected to current service"
                                    value={this.state.isConnected} />
                            </Grid>
                            <Grid item xs={12} sm={6} md={4}>
                                <ValueField
                                    title="Is Cloud Connected" 
                                    helperText="If current object is connected to current service"
                                    value={this.state.isCloudConnected} />
                            </Grid>
                            <Grid item xs={12} sm={12} md={4}>
                                <ValueField
                                    title="Is Local Connected" 
                                    helperText="If current object is connected to current service"
                                    value={this.state.isLocalConnected} />
                            </Grid>
                        </Grid>
                    </CardContent>
                </Card>
            </Container>
        );
    }

}
