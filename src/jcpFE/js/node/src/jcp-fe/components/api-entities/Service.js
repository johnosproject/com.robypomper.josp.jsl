import React from "react";
import Grid from '@material-ui/core/Grid';
import Container from '@material-ui/core/Container';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';

import { ValueField, RefreshIcon } from '../Commons';
import ReactServiceBase from '../base/ReactServiceBase';

export class ServicePage extends React.Component {

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
    }


    // Body

    render() {
        return (
            <div style={this.props.style}>
                <ServiceHeader jcpfe={this.jcpFE} />
                <Container>
                    <Card style={{margin:'20px 0'}}>
                        <CardContent>
                            <ServiceInfoGrid jcpfe={this.jcpFE} />
                        </CardContent>
                    </Card>
                </Container>
            </div>
        );
    }
}

export class ServiceHeader extends ReactServiceBase {

    constructor(props) {
        super(props);
    }


    // Body

    render() {
        const service = this.getService();

        return (
            <Container style={{display: 'flex', justifyContent: 'space-between'}}>
                <div>
                    <Typography variant="subtitle1" component="p" style={{display: 'flex'}}>
                        
                    </Typography>
                    <div style={{display: 'flex'}}>
                        <Typography variant="h4" component="h2">
                            Service {service.getName()}
                        </Typography>
                    </div>
                    <Typography variant="subtitle2" component="p">Current JSL Service info</Typography>
                </div>
            </Container>
        );
    }

}

export class ServiceInfoGrid extends ReactServiceBase {

    constructor(props) {
        super(props);
    }


    // Body

    render() {
        const service = this.getService();

        return (
            <Container style={{margin: '10px 0px'}}>

                <div style={{display: 'flex', justifyContent: 'space-between'}}>
                    <Typography variant="h5" component="h3"></Typography>
                </div>

                <Grid container spacing={3}>
                    <Grid item xs={12} sm={12} md={12}>
                        <Typography variant="h6" component="h4">Info</Typography>
                    </Grid>
                    <Grid item xs={12} sm={12} md={12}>
                        <ValueField
                            title="Full Service Id" 
                            helperText="Unique service's instance identifier"
                            value={service.getSrvId() + "/" + service.getUsrId() + "/" + service.getInstId()} />
                    </Grid>
                    <Grid item xs={12} sm={6} md={4}>
                        <ValueField
                            title="Service Id" 
                            helperText="Service Id"
                            value={service.getSrvId()} />
                    </Grid>
                    <Grid item xs={12} sm={6} md={4}>
                        <ValueField
                            title="User Id" 
                            helperText="User Id"
                            value={service.getUsrId()} />
                    </Grid>
                    <Grid item xs={12} sm={6} md={4}>
                        <ValueField
                            title="Instance Id" 
                            helperText="Random instance number"
                            value={service.getInstId()} />
                    </Grid>

                    <Grid item xs={12} sm={12} md={12}>
                        <ValueField
                            title="Session Id" 
                            helperText="Web client's session Id"
                            value={service.getSessionId()} />
                    </Grid>
                    <Grid item xs={12} sm={6} md={4}>
                        <ValueField
                            title="JSL Version" 
                            helperText="JSL instance's version"
                            value={service.getJSLVersion()} />
                    </Grid>
                </Grid>
            </Container>
        );
    }

}