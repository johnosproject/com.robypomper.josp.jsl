import React from "react";
import Container from '@material-ui/core/Container';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import FormControl from '@material-ui/core/FormControl';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import InputLabel from '@material-ui/core/InputLabel';
import TextField from '@material-ui/core/TextField';
import Select from '@material-ui/core/Select';
import Checkbox from '@material-ui/core/Checkbox';
import Autocomplete from '@material-ui/lab/Autocomplete';
import MenuItem from '@material-ui/core/MenuItem';
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import DialogActions from '@material-ui/core/DialogActions';
import Grid from '@material-ui/core/Grid';

import ReactObjectsBase from "../base/ReactObjectsBase";
import { ObjectListItemCard } from './Object';
import { AddIcon, LoginIcon } from '../Commons';


export class ObjectsListPage extends React.Component {

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
        this.state = {
            showDialog: false
        }
    }

    _handleOpenAddDialog() {
        this.setState({ showDialog: true });
    }

    _handleCloseAddDialog() {
        this.setState({ showDialog: false });
    }

    render() {
        return (
            <div style={this.props.style}>
                <Container style={{display: 'flex', justifyContent: 'space-between'}}>
                    <div>
                        <Typography variant="subtitle1" component="p" style={{display: 'flex'}}>
                            
                        </Typography>
                        <Typography variant="h4" component="h2">
                            Object list
                        </Typography>
                        <Typography variant="subtitle2" component="p">
                            Here you can find all available objects in your John Eco-System
                        </Typography>
                    </div>
                    {this.jcpFE.getUser().isAuthenticated()
                        ? <Button color="primary" startIcon={<AddIcon />} style={{marginLeft: '16px'}}
                                onClick={() => this._handleOpenAddDialog()}>
                            Add Object
                        </Button>
                        : <Button color="primary" startIcon={<LoginIcon />} style={{marginLeft: '16px'}}
                                component='a' href={this.jcpFE.getJSLWBUrls().userLogin} >
                            Login to add objects
                        </Button> }
                    <ObjectsAddDialog jcpfe={this.jcpFE} onClose={() => this._handleCloseAddDialog()} open={this.state.showDialog} />
                </Container>

                <ObjectsListFiltered {...this.props} />
            </div>
        );
    }

}

export class ObjectsAddDialog extends React.Component {

    INITIAL = "initial";
    FOUND = "found";
    NOT_FOUND = "not_found";
    REGISTERING = "registering";
    REGISTERING_ERR = "registering_err";
    REGISTERED = "registered";

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
        this.state = {
            showDialog: false,
            dialogState: this.INITIAL,
            objId: "",
        }
        this.onObjectsListener = new ObjectsAddDialog_OnObjects(this);
    }


    // Events Objects listeners

    _onObjectAdded(object) {
        this.setState({
            dialogState: this.REGISTERED,
            objId_NEW: object.getId()
        });
    }

    _onObjectRemoved(object) {
        this.setState({registeringState: "Object '" + object.getId() + "' removed."});
    }


    // UI's Events

    _onClose(event) {
        this.props.onClose(event);
        this.setState({
            showDialog: false,
            dialogState: this.INITIAL,
            objId: "",
            objName: "",
        });
        this.jcpFE.getObjects().remOnObjects(this.onObjectsListener);
    }
    
    _onSearch(event) {
        const object = this.jcpFE.getObjects().getById(this.state.objId);
        if (object)
            this.setState({
                dialogState: this.FOUND,
                objName: object.getName()
            });
        else
            this.setState({dialogState: this.NOT_FOUND});
    }

    _onRegister(event) {
        const object = this.jcpFE.getObjects().getById(this.state.objId);
        if (!object)
            throw "Illegal state of ObjectsAddDialog component: object '" + this.state.objId + "' not found";

            
        const userId = this.jcpFE.getUser().getId();
        const thiz = this;
        this.jcpFE.getObjects().addOnObjects(this.onObjectsListener);
        object.setOwner(userId,
            function onSuccess(object,response) {
                thiz.setState({dialogState: thiz.REGISTERING});
                //thiz.jcpFE.getObjects().remOnObjects(thiz.onObjectsListener);
            },
            function onError(object, xhttp, error) {
                thiz.setState({dialogState: thiz.REGISTERING_ERR, error: error});
                //thiz.jcpFE.getObjects().remOnObjects(thiz.onObjectsListener);
            });
    }

    _handleObjIdChange(event) {
        this.setState({objId: event.target.value});
    }


    // Body

    render() {
        return (
            <Dialog {...this.props} onClose={(event) => this._onClose()}>
                {this.state.dialogState === this.INITIAL
                || this.state.dialogState === this.NOT_FOUND
                    ? this._renderSearch()
                    : null}

                {this.state.dialogState === this.FOUND
                    ? this._renderFound()
                    : null}

                {this.state.dialogState === this.REGISTERING
                    ? this._renderRegistering()
                    : null}

                {this.state.dialogState === this.REGISTERING_ERR
                    ? this._renderRegisteringError()
                    : null}

                {this.state.dialogState === this.REGISTERED
                    ? this._renderRegistered()
                    : null}

            </Dialog>
        );
    }

    _renderSearch() {
        return (
            <React.Fragment>
                <DialogTitle>
                    Search JOSP Object
                </DialogTitle>,
                <DialogContent>
                    <p>Type the JOSP Object Id.</p>
                    <p>The JOSP Object Id can be found on phisical object's label as serial number or printed in the 'JOSP Object Daemon' startup logs.</p>

                    <TextField label="Object Id" variant="outlined" placeholder="XXXXX-YYYYY-ZZZZZ" style={{width: '40%', marginLeft: '30%'}}
                                    defaultValue={this.state.objId} onChange={(event) => this._handleObjIdChange(event)}
                                    error={this.state.dialogState === this.NOT_FOUND} helperText={this.state.dialogState === this.NOT_FOUND ? "Object Id not found" : "" } />
                                
                    
                </DialogContent>,
                <DialogActions>
                    <Button onClick={(event) => this._onSearch(event)} variant="contained" color="primary">
                        Search
                    </Button>
                </DialogActions>
            </React.Fragment>
        );
    }

    _renderFound() {
        return (
            <React.Fragment>
                <DialogTitle>
                    Register JOSP Object
                </DialogTitle>,
                <DialogContent>
                    <p>Continuing will set the current user as the owner of the <b>{this.state.objName}</b> object.</p>
                    <p> 
                        This will temporarily disconnect the object and change its Id but not its name.<br />
                        It will then be available in your objects list.
                    </p>
                </DialogContent>,
                <DialogActions>
                    <Button onClick={(event) => this._onClose(event)}>
                        Cancel
                    </Button>
                    <Button onClick={(event) => this._onRegister(event)} variant="contained" color="primary">
                        Accept
                    </Button>
                </DialogActions>
            </React.Fragment>
        );
    }

    _renderRegistering() {
        return (
            <React.Fragment>
                <DialogTitle>
                    Register JOSP Object
                </DialogTitle>,
                <DialogContent>
                    <p>Waiting for object <b>{this.state.objName}</b> registration</p>
                    <p> 
                        This process temporarily disconnects the object and waits for the addition of a new object <b>{this.state.objName}</b>.<br />
                        If the process takes more than a minute, close this window and refresh the object list.
                    </p>
                    {this.state.registeringState
                        ?   <p>{this.state.registeringState}</p>
                        : null }
                </DialogContent>,
                <DialogActions>
                    <Button onClick={(event) => this._onClose(event)}>
                        Don't wait
                    </Button>
                    <Button onClick={(event) => this._onClose(event)} disabled variant="contained" color="primary" style={{width:'150px'}}>
                        Ok
                    </Button>
                </DialogActions>
            </React.Fragment>
        );
    }

    _renderRegisteringError() {
        return (
            <React.Fragment>
                <DialogTitle>
                    ERROR Registering JOSP Object
                </DialogTitle>,
                <DialogContent>
                    <p>Error during object <b>{this.state.objName}</b> registration</p>
                    <p style={{color: 'red'}}> 
                        {this.state.error}
                    </p>
                </DialogContent>,
                <DialogActions>
                    <Button onClick={(event) => this._onClose(event)}>
                        Cancel
                    </Button>
                </DialogActions>
            </React.Fragment>
        );
    }

    _renderRegistered() {
        return (
            <React.Fragment>
                <DialogTitle>
                    JOSP Object registered
                </DialogTitle>,
                <DialogContent>
                    <p>The current user has been set as the owner of the object <b>{this.state.objName}</b> with new Id <b>{this.state.objId_NEW}</b>.</p>
                </DialogContent>,
                <DialogActions>
                    <Button onClick={(event) => this._onClose(event)} variant="contained" color="primary" style={{width:'150px'}}>
                        Ok
                    </Button>
                </DialogActions>
            </React.Fragment>
        );
    }
    
}
class ObjectsAddDialog_OnObjects {

    constructor(owner) {
        this._owner = owner;
    }

    onAdd(jcpJSLWB, objects, objId) {
        const object = objects.getById(objId);
        if (this._owner.state.objName === object.getName())
            this._owner._onObjectAdded(object);
    }

    onRemoved(jcpJSLWB, objects, objId) {
        const object = objects.getById(objId);
        if (this._owner.state.objName === object.getName())
            this._owner._onObjectRemoved(object);
    }

    onConnected(jcpJSLWB, objects, objId) {}

    onDisconnected(jcpJSLWB, objects, objId) {}

}

export class ObjectsList extends ReactObjectsBase {

    constructor(props) {
        super(props);

        this.state = {
            objects: this.jcpFE.getObjects().getList(),
            count: this.jcpFE.getObjects().getList().length,
        }
    }


    // Events Objects listeners

    onObjectAdded(jcpJSLWB, objects, objId) {
        this.setState({
            count: objects.getList().length,
            objects: objects.getList(),
        });
    }

    onObjectRemoved(jcpJSLWB, objects, objId) {
        this.setState({
            count: objects.getList().length,
            objects: objects.getList(),
        });
    }

    onObjectConnected(jcpJSLWB, objects, objId) {
        this.setState({
            objects: objects.getList(),
        });
    }

    onObjectDisconnected(jcpJSLWB, objects, objId) {
        this.setState({
            objects: objects.getList(),
        });
    }

    
    // Body

    render() {
        return (
            <div>
                <p>Objects list ({this.state.count})</p>
                <List>
                    {this.state.objects.map( obj =>
                        <ListItem key={obj.getId()}>
                            <ObjectListItemCard
                                jcpfe={this.jcpFE}
                                objId={obj.getId()}
                                objUrl={this.props.match.url + "/" + obj.getId()}
                                format="horizontal" />
                        </ListItem>
                    )}
                </List>
            </div>
        );
    }

}

export class ObjectsListFiltered extends ReactObjectsBase {

    constructor(props) {
        super(props);

        this.state = {
            objects: this.jcpFE.getObjects().getList(),
            count: this.jcpFE.getObjects().getList().length,
            showAll: false,
            showMy: true,
            showShared: true,
            showDisconnected: false,
            objectSearchKey: '',
            objectSearchSelected: null,
            filterModel: '',
            orderBy: '',
        }
    }


    // Events Objects listeners

    onObjectAdded(jcpJSLWB, objects, objId) {
        this.setState({
            count: objects.getList().length,
            objects: objects.getList(),
        });
    }

    onObjectRemoved(jcpJSLWB, objects, objId) {
        this.setState({
            count: objects.getList().length,
            objects: objects.getList(),
        });
    }

    onObjectConnected(jcpJSLWB, objects, objId) {
        this.setState({
            objects: objects.getList(),
        });
    }

    onObjectDisconnected(jcpJSLWB, objects, objId) {
        this.setState({
            objects: objects.getList(),
        });
    }


    // UI's Events

    _handleChangeShowAll() {
        this.setState({
            showAll: true,
            showMy: true,
            showShared: true,
            showDisconnected: true,
            objectSearchKey: '',
            objectSearchSelected: null,
            filterModel: '',
            orderBy: '',
        });
    }

    _handleChangeShowMy() {
        this.setState({
            showAll: !this.state.showMy && this.state.showShared && this.state.showDisconnected
                    && this.state.objectSearchKey!='' && this.state.filterModel!='' && this.state.orderBy!='',
            showMy: !this.state.showMy
        });
    }

    _handleChangeShowShared() {
        this.setState({
            showAll: this.state.showMy && !this.state.showShared && this.state.showDisconnected
                    && this.state.objectSearchKey!='' && this.state.filterModel!='' && this.state.orderBy!='',
            showShared: !this.state.showShared
        });
    }

    _handleChangeShowDisconnected() {
        this.setState({
            showAll: this.state.showMy && this.state.showShared && !this.state.showDisconnected
                    && this.state.objectSearchKey!='' && this.state.filterModel!='' && this.state.orderBy!='',
            showDisconnected: !this.state.showDisconnected
        });
    }

    _handleChangeSearchObject(event,value) {
        // value = {type, id, label}
        const objId = value ? value.id : '';
        console.log(value);
        this.setState({
            showAll: this.state.showMy && !this.state.showShared && this.state.showDisconnected
                    && objId!='' && this.state.filterModel!='' && this.state.orderBy!='',
            objectSearchKey: objId,
            objectSearchSelected: value
        })
    }

    _handleChangeFilterModel(event) {
        // event.target.value = model
        const modelKey = event.target.value === "all" ? '' : event.target.value;
        console.log(modelKey);
        this.setState({
            showAll: this.state.showMy && !this.state.showShared && this.state.showDisconnected
                    && this.state.objectSearchKey!='' && modelKey!='' && this.state.orderBy!='',
            filterModel: modelKey
        })
    }

    _handleChangeOrderBy(event,value) {
        // event.target.value = 10 | 20 | 30
        const orderByValue = event.target.value;
        console.log(orderByValue);
        this.setState({
            showAll: this.state.showMy && !this.state.showShared && this.state.showDisconnected
                    && this.state.objectSearchKey!='' && this.state.filterModel!='' && orderByValue!='',
            orderBy: orderByValue
        })
    }
    

    // Body

    _mustShow(object) {
        const usrId = this.jcpFE.getUser().getId();

        // ObjectSearchKey override also Show's flags (showMy, showShared...)
        if (this.state.objectSearchKey) {
            if (object.getId() === this.state.objectSearchKey)
                return true;
            else
                return false;
        }

        if (((this.state.showMy && object.getOwner() === usrId)
         || (this.state.showShared && object.getOwner() != usrId))
         && (this.state.showDisconnected || object.isConnected()))
            
            return true;

        return false;
    }

    _isFiltered(object) {
        const model = decodeURIComponent(this.state.filterModel);

        if (model === "" || model === object.getModel())            
            return true;

        return false;
    }

    render() {
        const usrId = this.jcpFE.getUser().getId();
        var countMy = 0;
        var countShared = 0;
        var countDisc = 0;
        var countAll = this.state.objects.length;
        this.state.objects.map(x => { if (x.getOwner() === usrId) countMy++; if (x.getOwner() != usrId) countShared++; if (!x.isConnected()) countDisc++; } );
        var filteredObjects = this.state.objects;

        // 1st objects filtering (no anonymous objects and show's flags filters)
        var filteredObjects = filteredObjects.filter(x => this._mustShow(x) );

        // Create ObjectSearchKey list
        const idAndNames = [];
        filteredObjects.map( object => {
            idAndNames.push({type: "NAME" , id: object.getId(), label: object.getName()});
        });
        filteredObjects.map( object => {
            idAndNames.push({type: "ID"   , id: object.getId(), label: object.getId()});
        });
        // Create filterModel list
        const models = [];
        filteredObjects.map( object => {
            const currentKey = encodeURIComponent(object.getModel());
            if (models.find(x => x.key === currentKey))
                return;
            models.push({name: object.getModel(), key: currentKey});
        });

        // 2nd objects filtering (filterModel...)
        filteredObjects = filteredObjects.filter(x => this._isFiltered(x) );

        filteredObjects.sort((a,b) => (a.getName() > b.getName()) ? 1 : ((b.getName() > a.getName()) ? -1 : 0))

        const styleFilterItems = {
            paddingTop: '0px',
            paddingBottom: '0px'
        };
        
        return (
            <Container>
                <Card style={{margin:'20px 0'}}>
                    <CardContent>

                        <Typography variant="h5" component="h3">Objects</Typography>

                        <Grid container spacing={3}
                            container
                            justify="flex-end"
                            alignItems="flex-end"
                            style={{margin: '0px', width: '100%', marginBottom: '16px'}}
                            >
                            <Grid item xs={12} style={styleFilterItems}>
                                <FormControlLabel
                                    control={
                                        <Checkbox
                                            checked={this.state.showAll}
                                            onChange={() => this._handleChangeShowAll()}
                                            color="primary"
                                        />
                                    }
                                    label={<span style={{whiteSpace: 'nowrap'}}>({countAll}) Show All</span> }
                                />
                            </Grid>
                            <Grid item xs={12} sm={6} md={4} style={styleFilterItems}>
                                <FormControlLabel
                                    control={
                                        <Checkbox
                                            checked={this.state.showMy}
                                            onChange={() => this._handleChangeShowMy()}
                                            color="primary"
                                        />
                                    }
                                    label={<span style={{whiteSpace: 'nowrap'}}>({countMy}) Show MY Objects</span> }
                                />
                            </Grid>
                            <Grid item xs={12} sm={6} md={4} style={styleFilterItems}>
                                <FormControlLabel
                                    control={
                                        <Checkbox
                                            checked={this.state.showShared}
                                            onChange={() => this._handleChangeShowShared()}
                                            color="primary"
                                        />
                                    }
                                    label={<span style={{whiteSpace: 'nowrap'}}>({countShared}) Show Objects shared</span> }
                                />
                            </Grid>
                            <Grid item xs={12} sm={12} md={4} style={styleFilterItems}>
                                <FormControlLabel
                                    control={
                                        <Checkbox
                                            checked={this.state.showDisconnected}
                                            onChange={() => this._handleChangeShowDisconnected()}
                                            color="primary"
                                        />
                                    }
                                    label={<span style={{whiteSpace: 'nowrap'}}>({countDisc}) Show Disconnected objects</span> }
                                />
                            </Grid>
                            <Grid item xs={12} sm={6} style={styleFilterItems}>
                                <FormControl style={{ width: '100%', marginRight: '16px' }}>
                                    <Autocomplete
                                        id="combo-box-demo"
                                        value={this.state.objectSearchSelected}
                                        options={idAndNames}
                                        getOptionLabel={(option) => option.label}
                                        renderInput={(params) => <TextField {...params} label="Search objects by Name or Id" placeholder="XXXXX-YYYYY-ZZZZZ" InputLabelProps={{shrink: true}} />}
                                        onChange={(event,value) => this._handleChangeSearchObject(event,value)}
                                        
                                        />
                                </FormControl>
                            </Grid>
                            <Grid item xs={12} sm={6} style={styleFilterItems}>
                                <FormControl style={{ width: '100%', marginRight: '16px' }} >
                                    <InputLabel htmlFor="filter-by-model" shrink>Filter by Model</InputLabel>
                                    <Select
                                        value={this.state.filterModel}
                                        onChange={(event) => this._handleChangeFilterModel(event)}
                                        inputProps={{
                                            name: 'filterByModel',
                                            id: 'filter-by-model'
                                        }} 
                                        style={{ width: 'auto' }}
                                        displayEmpty
                                        >
                                        <MenuItem value=""><Typography color="textSecondary"><em>All</em></Typography></MenuItem>
                                        {models.map(model =>
                                            <MenuItem key={model.key} value={model.key}><Typography>{model.name}</Typography></MenuItem>
                                        )}
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={12} sm={12} style={styleFilterItems}>
                                <FormControl style={{ width: '100%', marginRight: '16px', display: 'none' }}>
                                    <InputLabel htmlFor="order-by" shrink>Order by</InputLabel>
                                    <Select
                                        value={this.state.orderBy}
                                        onChange={(event,value) => this._handleChangeOrderBy(event,value)}
                                        inputProps={{
                                            name: 'orderBy',
                                            id: 'order-by',
                                        }}
                                        style={{ width: 'auto' }}
                                        >
                                        <MenuItem value={10}><Typography>Name</Typography></MenuItem>
                                        <MenuItem value={20}><Typography>Last activity</Typography></MenuItem>
                                        <MenuItem value={30}><Typography>Last discovered</Typography></MenuItem>
                                    </Select>
                                </FormControl>
                            </Grid>
                        </Grid>

                        <List>
                            {filteredObjects.map( obj =>
                                <ListItem key={obj.getId()}>
                                    <ObjectListItemCard
                                        jcpfe={this.jcpFE}
                                        objId={obj.getId()}
                                        objUrl={this.props.match.url + "/" + obj.getId()}
                                        format="horizontal" />
                                </ListItem>
                            )}
                        </List>

                    </CardContent>
                </Card>
            </Container>

        );
    }

}
