import React from "react";
import TextField from '@material-ui/core/TextField';
import IconButton from '@material-ui/core/IconButton';
import Tooltip from '@material-ui/core/Tooltip';
import Button from '@material-ui/core/Button';
import { DataGrid } from '@material-ui/data-grid';
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import DialogActions from '@material-ui/core/DialogActions';


// Icons

export {default as UserIcon} from '@material-ui/icons/Person';
export {default as ServiceIcon} from '@material-ui/icons/Settings';
export {default as ObjectIcon} from '@material-ui/icons/EmojiObjects';
export {default as CloudIcon} from '@material-ui/icons/Cloud';
export {default as HomeIcon} from '@material-ui/icons/Home';
export {default as AboutIcon} from '@material-ui/icons/Info';

export {default as StructureIcon} from '@material-ui/icons/AccountTree';
export {default as EventsIcon} from '@material-ui/icons/EventNote';
export {default as AccessControlIcon} from '@material-ui/icons/LockOpen';
export {default as ConfigsIcon} from '@material-ui/icons/Settings';
export {default as DownloadIcon} from '@material-ui/icons/GetApp';

export {default as LogoutIcon} from '@material-ui/icons/ExitToApp';
export {default as LoginIcon} from '@material-ui/icons/VpnKey';

export {default as OnlineIcon} from '@material-ui/icons/Link';
export {default as OfflineIcon} from '@material-ui/icons/LinkOff';
export {default as InfoIcon} from '@material-ui/icons/InfoOutlined';
export {default as HistoryIcon} from '@material-ui/icons/UpdateOutlined';
export {default as RefreshIcon} from '@material-ui/icons/Cached';
export {default as MoreIcon} from '@material-ui/icons/ExpandMore';
export {default as LessIcon} from '@material-ui/icons/ExpandLess';
export {default as AddIcon} from '@material-ui/icons/Add';
export {default as DuplicateIcon} from '@material-ui/icons/FileCopy';
export {default as DeleteIcon} from '@material-ui/icons/Delete';
export {default as EditIcon} from '@material-ui/icons/Edit';
export {default as MenuIcon} from '@material-ui/icons/MoreVert';
export {default as ClipboardIcon} from '@material-ui/icons/Assignment';

import InfoIcon from '@material-ui/icons/InfoOutlined';
import ClipboardIcon from '@material-ui/icons/Assignment';


// Fields

export class ValueField extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <TextField fullWidth
                label={this.props.title}
                value={this.props.value==undefined ?  "N/A" : this.props.value }
                helperText={this.props.helperText}
                InputProps={{
                  readOnly: true
                }} />
        );
    }
}


// Table properties

export class TableProperties extends React.Component {

    constructor(props) {
        super(props);
        this.showClipboardCopy = !props.disableClipboardCopy;
        this.nameWidth = props.nameWidth ? props.nameWidth : "50%";
        this.valueWidth = props.valueWidth ? props.valueWidth : "50%";
    }

    handleCopy(value) {
        var activeElement = document.activeElement;
        var tempInput = document.createElement("input");
        tempInput.value = value;
        document.body.appendChild(tempInput);
        tempInput.select();
        document.execCommand("copy");
        document.body.removeChild(tempInput);
        activeElement.focus();
      }

    render() {
        const borderStyle = {
            borderBottom: '1px dashed #00000040'
        }
        const nameStyle = {
            width: this.nameWidth,
            padding: '10px 0'
        }
        const valueStyle = {
            width: this.valueWidth,
            maxWidth: '0px',
            padding: '10px 0px 10px 10px',
            whiteSpace: 'nowrap',
            overflow: 'hidden',
            textOverflow: 'ellipsis'
        }
        const btnStyle = {
            width: '50px',
            padding: '10px 0'
        }

        return (
            <table style={this.props.style}>
                <tbody>
                    {this.props.properties.map((prop, i) =>
                        <tr key={prop.name}>
                            <td style={i!=this.props.properties.length-1 ? {...nameStyle, ...borderStyle} : nameStyle} >
                                <b>{prop.name}</b>
                            </td>
                            <td style={i!=this.props.properties.length-1 ? {...valueStyle, ...borderStyle} : valueStyle} >
                                <Tooltip title={prop.value}>
                                    <span>{prop.value}</span>
                                </Tooltip>
                            </td>
                            {this.showClipboardCopy ?
                                /* <td style={i!=this.props.properties.length-1 ? {...btnStyle, ...borderStyle} : btnStyle} > */
                                <td style={btnStyle} >
                                    <IconButton variant="contained" size="small" onClick={() => this.handleCopy(prop.value)}>
                                        <Tooltip title="Copy to clipboard"><ClipboardIcon /></Tooltip>
                                    </IconButton>
                                </td>
                                : null}
                        </tr>
                    )}
                </tbody>
            </table>
        );
    }
}


// JCP List

export class JCPList extends React.Component {

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
        this.component = props.component;
        
        this.columns = [
            { field: 'id', headerName: 'Id', width: 200 },
            { field: 'name', headerName: 'Name', width: 200 },
            { field: 'details', headerName: 'Details', width: 80, sortable: false,
            renderCell: (params) => (
                <IconButton variant="contained" color="primary" size="small" onClick={() => this.showDialog(params.row.url)}>
                    <Tooltip title="Show details"><InfoIcon /></Tooltip>
                </IconButton>
            )},
        ];
        this.state = {
            dialogOpen: false,
            url: null,
        }
    }

    showDialog(url) {
        this.setState({
            dialogOpen: true,
            url: url
        });
    }

    closeDialog() {
        this.setState({
            dialogOpen: false,
            url: null
        });
    }

    render() {
        return (
            <div style={{height: '400px', margin: '0px', padding: '1em'}} >
                <DataGrid
                    rows={this.props.data}
                    columns={this.columns}
                    pageSize={5}
                    disableColumnMenu
                    disableClickEventBubbling />
                <JCPListDialog jcpfe={this.jcpFE} url={this.state.url} title={this.props.title} component={this.component} open={this.state.dialogOpen} onClose={() => this.closeDialog()} />
            </div>
        );
    }
    
}

export class JCPListDialog extends React.Component {

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
        this.component = props.component;
    }

    render() {
        return (
            <Dialog onClose={() => this.props.onClose()} open={this.props.open}>
                <DialogTitle>
                    {this.props.title}
                </DialogTitle>
                <DialogContent>
                    <this.component url={this.jcpFE.getJSLWBUrl() + this.props.url} />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => this.props.onClose()}>
                        Close
                    </Button>
                </DialogActions>
            </Dialog>
        );
    }
    
}