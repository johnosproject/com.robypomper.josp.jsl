import * as React from "react";
import { Link } from 'react-router-dom';
import Container from '@material-ui/core/Container';
import Grid from '@material-ui/core/Grid';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Collapse from '@material-ui/core/Collapse';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import Tooltip from '@material-ui/core/Tooltip';
import Slider from '@material-ui/core/Slider';
import Switch from '@material-ui/core/Switch';
import { withTheme } from '@material-ui/core/styles';
import withWidth, { isWidthUp, isWidthDown } from '@material-ui/core/withWidth';

import { ValueField, InfoIcon, HistoryIcon, MoreIcon, LessIcon } from '../Commons';
import ReactObjectComponentBase from '../base/ReactObjectComponentBase';


function getStateStr(state) {
    if (typeof state === "boolean")
        return state ? "true" : "false";
    if (typeof state === "number")
        return (Math.round(state * 100) / 100).toFixed(2);
    return state;

}

function renderComponent(jcpFE,objUrl,component) {
    const defaultExpanded = false;
    if (component.getType() === "Container")
        return <ObjectContainerDetails key={component.getComponentPath()} jcpfe={jcpFE} objId={component.getObjectId()} objUrl={objUrl} component={component} expanded={defaultExpanded} />;
    if (component.getType() === "BooleanState")
        return <ObjectBooleanStateDetails key={component.getComponentPath()} jcpfe={jcpFE} objId={component.getObjectId()} objUrl={objUrl} component={component} expanded={defaultExpanded} />;
    if (component.getType() === "RangeState")
        return <ObjectRangeStateDetails key={component.getComponentPath()} jcpfe={jcpFE} objId={component.getObjectId()} objUrl={objUrl} component={component} expanded={defaultExpanded} />;
    if (component.getType() === "BooleanAction")
        return <ObjectBooleanActionDetails key={component.getComponentPath()} jcpfe={jcpFE} objId={component.getObjectId()} objUrl={objUrl} component={component} expanded={defaultExpanded} />;
    if (component.getType() === "RangeAction")
        return <ObjectRangeActionDetails key={component.getComponentPath()} jcpfe={jcpFE} objId={component.getObjectId()} objUrl={objUrl} component={component} expanded={defaultExpanded} />;

    return <p id={component.getComponentPath()} key={component.getComponentPath()}>Unknow component {component.name}</p>;
}


// Compact

export class ObjectBooleanStateRaw extends ReactObjectComponentBase {

    constructor(props){
        super(props);
        this.state = {
            ...this.state,
            status: this.getComponent().getState()
        }
    }

    onComponentInit() {
        this.setState({status: this.getComponent().getState()});
    }
    
    onStatusUpdate(jcpJSLWB, component, status, oldStatus) {
        this.setState({status: this.getComponent().getState()});
    }
    
    render() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();
        if (!this.isObjectAllowed())
            return this.renderObjectNotAllowed();
        if (!this.isComponentInit())
            return this.renderComponentNotInit();

        const object = this.getObject();
        const component = this.getComponent();

        return (
            <div id={component.getComponentPath()} style={{minWidth: '100px', marginRight: '20px', textAlign: 'center'}}>
                <Switch
                    disabled
                    checked={this.state.status}
                    color="primary"
                    inputProps={{ 'aria-label': 'primary checkbox' }} />
            </div>);
    }

}
export const ObjectBooleanState = withWidth()(withTheme(ObjectBooleanStateRaw))

export class ObjectRangeStateRaw extends ReactObjectComponentBase {

    constructor(props){
        super(props);
        this.state = {
            ...this.state,
            status: this.getComponent().getState()
        }
    }

    onComponentInit() {
        this.setState({status: this.getComponent().getState()});
    }
    
    onStatusUpdate(jcpJSLWB, component, status, oldStatus) {
        this.setState({status: this.getComponent().getState()});
    }
    
    render() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();
        if (!this.isObjectAllowed())
            return this.renderObjectNotAllowed();
        if (!this.isComponentInit())
            return this.renderComponentNotInit();

        const object = this.getObject();
        const component = this.getComponent();

        return (
            <Slider
                id={component.getComponentPath()}
                disabled
                defaultValue={this.state.status}
                min={component.getMin()}
                max={component.getMax()}
                step={component.getStep()}
                style={{minWidth: '100px', marginRight: '20px'}}
            />);
    }
    
}
export const ObjectRangeState = withWidth()(withTheme(ObjectRangeStateRaw))

export class ObjectBooleanActionRaw extends ObjectBooleanStateRaw {

    constructor(props){
        super(props);
        this.state = {
            ...this.state,
            srvPerm: this.getObject().getPermission()
        }
    }

    onPermSrvUpd(jcpJSLWB, object, value, old, component) {
        console.log("Changed service permission on object " + value + "(old: " + old + ")");
        this.setState({srvPerm: value});
    }

    handleChange(event,value) {
        const state = value;
        const component = this.props.component;

        const thiz = this;
        if (state)
            component.setTrue(
                function onSuccess(component,response) {
                    if (thiz.jcpFE.isSnackBar())
                        thiz.jcpFE.getSnackBar().showMessage("success",
                                            "Action send successfully",
                                            null,
                                            "SetTrue action send successfully to '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.",
                                            null,2000);
                    console.log("Action setTrue to '" + component.componentPath + "' component on '" + component.objId + "' object.");
                },
                function onError(component, xhttp, error) {
                    if (thiz.jcpFE.isSnackBar())
                        thiz.jcpFE.getSnackBar().showMessage("error",
                                            "Error on send action",
                                            "Error on send setTrue action to '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.",
                                            error);
                    console.warn("Error on send setTrue action to '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.\n" + error);
                }
            )
        else
            component.setFalse(
                function onSuccess(component,response) {
                    if (thiz.jcpFE.isSnackBar())
                        thiz.jcpFE.getSnackBar().showMessage("success",
                                            "Action send successfully",
                                            null,
                                            "SetFalse action send successfully to '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.",
                                            null,2000);
                    console.log("Action setFalse to '" + component.componentPath + "' component on '" + component.objId + "' object.");
                },
                function onError(component, xhttp, error) {
                    if (thiz.jcpFE.isSnackBar())
                        thiz.jcpFE.getSnackBar().showMessage("error",
                                            "Error on send action",
                                            "Error on send setFalse action to '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.",
                                            error);
                    console.warn("Error on send setFalse action to '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.\n" + error);
                }
            )
    }
    
    render() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();
        if (!this.isObjectAllowed())
            return this.renderObjectNotAllowed();
        if (!this.isComponentInit())
            return this.renderComponentNotInit();

        // const object = this.getObject();
        const component = this.getComponent();
        // const haveActionPermission = this.state.srvPerm==="CoOwner" || this.state.srvPerm==="Actions";
        return (
            <div id={component.getComponentPath()} style={{
                minWidth: '100px',
                marginRight: isWidthUp('sm',this.props.width) ? this.props.theme.spacing(2) : '0px',
                textAlign: 'center'
                }}>
                <Switch
                    disabled={this.state.srvPerm!="CoOwner" && this.state.srvPerm!="Actions"}
                    checked={this.state.status}
                    color="primary"
                    onChange={(event,newValue) => this.handleChange(event,newValue)} />
            </div>);
    }
    
}
export const ObjectBooleanAction = withWidth()(withTheme(ObjectBooleanActionRaw))

export class ObjectRangeActionRaw extends ObjectRangeStateRaw {

    constructor(props){
        super(props);
        this.state = {
            ...this.state,
            srvPerm: this.getObject().getPermission()
        }
    }

    onPermSrvUpd(jcpJSLWB, object, value, old, component) {
        console.log("Changed service permission on object " + value + "(old: " + old + ")");
        this.setState({srvPerm: value});
    }

    handleChange(event,value) {
        const component = this.props.component;

        const thiz = this;
        component.setValue(value,
            function onSuccess(component,response) {
                if (thiz.jcpFE.isSnackBar())
                    thiz.jcpFE.getSnackBar().showMessage("success",
                                        "Action send successfully",
                                        null,
                                        "SetValue(" + value + ") action send successfully to '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.",
                                        null,2000);
                console.log("Action setValue(" + value + ") to '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.");
            },
            function onError(component, xhttp, error) {
                if (thiz.jcpFE.isSnackBar())
                    thiz.jcpFE.getSnackBar().showMessage("error",
                                        "Error on send action",
                                        "Error on send setValue(" + value + ") action to '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.",
                                        error);
                console.warn("Error on send setValue action to '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.\n" + error);
            }
        )
    }
    
    render() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();
        if (!this.isObjectAllowed())
            return this.renderObjectNotAllowed();
        if (!this.isComponentInit())
            return this.renderComponentNotInit();

        // const object = this.getObject();
        const component = this.getComponent();
        // const haveActionPermission = this.state.srvPerm==="CoOwner" || this.state.srvPerm==="Actions";
        return (
            <div id={component.getComponentPath()} style={{
                minWidth: '100px',
                marginRight: isWidthUp('sm',this.props.width) ? this.props.theme.spacing(2) : '0px',
                textAlign: 'center'
                }}>
                <Slider
                    disabled={this.state.srvPerm!="CoOwner" && this.state.srvPerm!="Actions"}
                    value={this.state.status}
                    min={component.getMin()}
                    max={component.getMax()}
                    step={component.getStep()}
                    style={{
                        minWidth: '100px',
                        marginRight: isWidthUp('sm',this.props.width) ? this.props.theme.spacing(2) : '0px'
                        }}
                    onChange={(event,newValue) => this.handleChange(event,newValue)}
                />
            </div>);
    }
    
}
export const ObjectRangeAction = withWidth()(withTheme(ObjectRangeActionRaw))

// Details

class ObjectComponentDetailsRaw extends ReactObjectComponentBase {

    constructor(props){
        super(props);
        this.objUrl = props.objUrl;
        this.state = {
            ...this.state,
            component: props.component,
            expanded: []
        };
        if (props.component && (props.component.getComponentPath() || (props.component.getName()==="root" && props.component.getComponentPath() === ""))) {
            this.key = props.component.getComponentPath().replace(/ /g,"_");
            this.state.expanded[this.key] = props.expanded;
        }
    }
    
    onStatusUpdate(jcpJSLWB, component, status, oldStatus) {
        this.setState({component: component});
    }

    _handleExpandClick = (id) => {
        const expanded = this.state.expanded;
        expanded[id] = expanded.hasOwnProperty(id) ? !expanded[id] : true
        this.setState({ expanded });
    };

    renderCommonsFields(component) {
        return (
            <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                    <ValueField
                        title="Component path" 
                        helperText=""
                        value={component.getComponentPath() !== "" ? component.getComponentPath() : "(root)" } />
                </Grid>
                <Grid item xs={12} sm={6}>
                    <ValueField
                        title="Type" 
                        helperText=""
                        value={component.getType()} />
                </Grid>
                <Grid item xs={12} sm={12}>
                    <ValueField
                        title="Component description" 
                        helperText=""
                        value={component.getDescription()} />
                </Grid>
            </Grid>
        );
    }

    renderStateRangeFields(component) {
        return (
            <Grid container spacing={3}>
                <Grid item xs={12} sm={6} md={4}>
                    <ValueField
                        title="Min value" 
                        helperText="Min allowed value"
                        value={component.getMin()} />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                    <ValueField
                        title="Step size" 
                        helperText="Inc/dec quantity"
                        value={component.getStep()} />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                    <ValueField
                        title="Max value" 
                        helperText="Max allowed value"
                        value={component.getMax()} />
                </Grid>
            </Grid>
        );
    }

    renderStateField(component) {
        return (
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <ValueField
                        title="State" 
                        helperText=""
                        value={component.getState()} />
                </Grid>
            </Grid>
        );
    }

    renderComponent(component,compMain,compInfo) {
        const expanded = this.state.expanded;
        var stateStr = component.getType() !== "Container" ? getStateStr(component.getState()) : "";

        const pageWidth = window.innerWidth;
        var padding;
        var width;
        if (pageWidth>=1920)
          padding = '32px';
        else if (pageWidth>=1024)
          padding = '16px';
        else
          padding = '0px';
        return (
            <Card id={component.getComponentPath()!=""?component.getComponentPath():"(root)"} key={this.key} style={{
                    marginLeft: '0px',
                    marginRight: '0px',
                    marginTop: this.props.theme.spacing(2),
                    marginBottom: this.props.theme.spacing(2),
                }}>
                <CardContent>
                    <div style={{
                        display: 'flex',
                        flexDirection: 'row',
                        flexWrap: isWidthUp('sm',this.props.width) ? 'nowrap' : 'wrap',
                        justifyContent: 'space-between',
                        alignItems: 'flex-end'}}>
                        
                        <Typography variant="h6" component="h4" style={{
                            flexGrow: component.getType() === "Container" ? 'unset' : '1'
                            }}>{component.getName() === "root" ? "Root Component" : component.getName()}</Typography>

                        {component.getType() !== "Container" ?
                            <React.Fragment>
                                <Tooltip title={stateStr}>
                                    <div style={{
                                        marginRight: isWidthUp('sm',this.props.width) ? this.props.theme.spacing(2) : '0px',
                                        width: isWidthUp('sm',this.props.width) ? 'unset' : '100%'
                                        }}>{compMain}</div>
                                </Tooltip>
                                <IconButton component={Link} size="small" to={this.objUrl + "/status/" + encodeURIComponent(component.getComponentPath())}
                                    style={{
                                        marginRight: isWidthUp('sm',this.props.width) ? this.props.theme.spacing(2) : '0px',
                                        width: isWidthUp('sm',this.props.width) ? 'unset' : '50%'
                                        }}>
                                    <HistoryIcon />
                                </IconButton>
                            </React.Fragment>
                            : null
                        }
                        <IconButton size="small" onClick={() => this._handleExpandClick(this.key)}
                            style={{
                                marginRight: isWidthUp('sm',this.props.width) ? this.props.theme.spacing(2) : '0px',
                                width: isWidthUp('sm',this.props.width) || component.getType() === "Container" ? 'unset' : '50%'
                                }}>
                                {component.getType() === "Container"
                                    ? expanded[this.key] ? <LessIcon /> : <MoreIcon />
                                    : <InfoIcon />
                                }
                            </IconButton>
                        
                    </div>

                    <Collapse id="comp_collapsable" in={expanded[this.key]} timeout="auto" unmountOnExit>
                        <Container id="comp_collapsable_container" style={{paddingLeft: padding, paddingRight: padding}}>{compInfo}</Container>
                    </Collapse>

                </CardContent>
            </Card>);

    }
    
}
export const ObjectComponentDetails = withWidth()(withTheme(ObjectComponentDetailsRaw))


class ObjectContainerDetailsRaw extends ObjectComponentDetailsRaw {

    constructor(props){
        super(props);
    }

    render() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();
        if (!this.isObjectAllowed())
            return this.renderObjectNotAllowed();
        if (!this.isComponentInit())
            return this.renderComponentNotInit();

        const object = this.getObject();
        const component = this.getComponent();
        const compMain = null;
        const compInfo = (
            <div id="sub_comps">
                {/* this.renderCommonsFields(component) */}
                {component.getSubComponents().map( subComp =>
                    renderComponent(this.jcpFE, this.objUrl, subComp)
                )}
            </div>);

        return this.renderComponent(component,compMain,compInfo);
    }
    
}
export const ObjectContainerDetails = withWidth()(withTheme(ObjectContainerDetailsRaw))

class ObjectBooleanStateDetailsRaw extends ObjectComponentDetailsRaw {

    constructor(props){
        super(props);
    }
    
    render() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();
        if (!this.isObjectAllowed())
            return this.renderObjectNotAllowed();
        if (!this.isComponentInit())
            return this.renderComponentNotInit();

        const object = this.getObject();
        const component = this.getComponent();
        const compMain = <ObjectBooleanState jcpfe={this.jcpFE} objId={component.getObjectId()} component={component} />;
        const compInfo = (
            <React.Fragment>
                {this.renderStateField(component)}
                {this.renderCommonsFields(component)}
            </React.Fragment>);

        return this.renderComponent(component,compMain,compInfo);
    }

}
export const ObjectBooleanStateDetails = withWidth()(withTheme(ObjectBooleanStateDetailsRaw))

class ObjectRangeStateDetailsRaw extends ObjectComponentDetailsRaw {

    constructor(props){
        super(props);
    }
    
    render() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();
        if (!this.isObjectAllowed())
            return this.renderObjectNotAllowed();
        if (!this.isComponentInit())
            return this.renderComponentNotInit();

        const object = this.getObject();
        const component = this.getComponent();
        const compMain = <ObjectRangeState jcpfe={this.jcpFE} objId={component.getObjectId()} component={component} />;
        const compInfo = (
            <React.Fragment>
                {this.renderStateField(component)}
                {this.renderCommonsFields(component)}
                {this.renderStateRangeFields(component)}
            </React.Fragment>);

        return this.renderComponent(component,compMain,compInfo);
    }
    
}
export const ObjectRangeStateDetails = withWidth()(withTheme(ObjectRangeStateDetailsRaw))

class ObjectBooleanActionDetailsRaw extends ObjectComponentDetailsRaw {

    constructor(props){
        super(props);
    }
    
    render() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();
        if (!this.isObjectAllowed())
            return this.renderObjectNotAllowed();
        if (!this.isComponentInit())
            return this.renderComponentNotInit();

        const object = this.getObject();
        const component = this.getComponent();
        const compMain = <ObjectBooleanAction jcpfe={this.jcpFE} objId={component.getObjectId()} component={component} />;
        const compInfo = (
            <React.Fragment>
                {this.renderStateField(component)}
                {this.renderCommonsFields(component)}
            </React.Fragment>);

        return this.renderComponent(component,compMain,compInfo);
    }
    
}
export const ObjectBooleanActionDetails = withWidth()(withTheme(ObjectBooleanActionDetailsRaw))

class ObjectRangeActionDetailsRaw extends ObjectComponentDetailsRaw {

    constructor(props){
        super(props);
    }
    
    render() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();
        if (!this.isObjectAllowed())
            return this.renderObjectNotAllowed();
        if (!this.isComponentInit())
            return this.renderComponentNotInit();

        const object = this.getObject();
        const component = this.getComponent();
        const compMain = <ObjectRangeAction jcpfe={this.jcpFE} objId={component.getObjectId()} component={component} />;
        const compInfo = (
            <React.Fragment>
                {this.renderStateField(component)}
                {this.renderCommonsFields(component)}
                {this.renderStateRangeFields(component)}
            </React.Fragment>);

        return this.renderComponent(component,compMain,compInfo);
    }
    
}
export const ObjectRangeActionDetails = withWidth()(withTheme(ObjectRangeActionDetailsRaw))
