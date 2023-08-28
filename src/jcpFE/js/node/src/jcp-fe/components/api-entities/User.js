import React from "react";
import Grid from '@material-ui/core/Grid';
import Container from '@material-ui/core/Container';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Typography from '@material-ui/core/Typography';

import { ValueField } from '../Commons';
import ReactUserBase from '../base/ReactUserBase';

export class UserPage extends React.Component {

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
    }

    render() {
        return (
            <div style={this.props.style}>
                <UserHeader jcpfe={this.jcpFE} />
                <Container>
                    <Card style={{margin:'20px 0'}}>
                        <CardContent>
                            <UserInfoGrid jcpfe={this.jcpFE} />
                        </CardContent>
                    </Card>
                </Container>
            </div>
        );
    }
}

export class UserHeader extends ReactUserBase {

    constructor(props) {
        super(props);
    }


    // Body

    render() {
        const user = this.getUser();

        return (
            <Container style={{display: 'flex', justifyContent: 'space-between'}}>
                <div>
                    <Typography variant="subtitle1" component="p" style={{display: 'flex'}}>
                        
                    </Typography>
                    <div style={{display: 'flex'}}>
                        <Typography variant="h4" component="h2">
                            User {user.getName()}
                        </Typography>
                    </div>
                    <Typography variant="subtitle2" component="p">Current JSL User info</Typography>
                </div>
            </Container>
        );
    }

}

export class UserInfoGrid extends ReactUserBase {

    constructor(props) {
        super(props);
    }


    // Body

    render() {
        const user = this.getUser();
        const splitCount = user.isAdmin() ? 3 : 4;

        return (
            <Container style={{margin: '10px 0px'}}>

                <div style={{display: 'flex', justifyContent: 'space-between'}}>
                    <Typography variant="h5" component="h3"></Typography>
                </div>

                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <Typography variant="h6" component="h4">Info</Typography>
                    </Grid>
                    <Grid item xs={12} sm={6}>
                        <ValueField
                            title="Id" 
                            helperText="Unique user's identifier"
                            value={user.getId()} />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                        <ValueField
                            title="Name" 
                            helperText="User nikname"
                            value={user.getName()} />
                    </Grid>
                    <Grid item xs={12} sm={6} md={splitCount}>
                        <ValueField
                            title="isAuthenticated" 
                            helperText="Is current user authenticated"
                            value={user.isAuthenticated()} />
                    </Grid>
                    {user.isAdmin()
                        ? <Grid item xs={12} sm={6} md={splitCount}>
                            <ValueField
                                title="isAdmin" 
                                helperText="Is current user Admin"
                                value={user.isAdmin()} />
                        </Grid>
                        : null }
                    <Grid item xs={12} sm={6} md={splitCount}>
                        <ValueField
                            title="isMaker" 
                            helperText="Is current user a Maker"
                            value={user.isMaker()} />
                    </Grid>
                    <Grid item xs={12} sm={6} md={splitCount}>
                        <ValueField
                            title="isDeveloper" 
                            helperText="Is current user a Developer"
                            value={user.isDeveloper()} />
                    </Grid>
                </Grid>
            </Container>
        );
    }

}