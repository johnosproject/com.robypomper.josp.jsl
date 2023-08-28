import React from "react";
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Container from '@material-ui/core/Container';
import Typography from '@material-ui/core/Typography';
import Tooltip from '@material-ui/core/Tooltip';
import Button from '@material-ui/core/Button';

import Caller from '../../../jcp-commons/Caller'
import { InfoIcon, RefreshIcon } from '../Commons'


export class ReactFetching extends React.Component {

    constructor(props,urlToFetch) {
        super(props);
        this.urlToFetch = urlToFetch;
        this.state = {
            isFetching: false,
            data: null
        }
    }

    componentDidMount() {
        this.fetchData();
    }

    isFetching() {
        return this.state.isFetching;
    }

    isFetched() {
        return this.state.data!=null;
    }

    fetchData() {
        if (!this.urlToFetch) {
            this.setState({
                isFetching: false,
                data: null,
                error: "Fetch url not set"
            })
            return;
        }

        this.setState({ isFetching: true });
        const thiz = this;
        Caller.apiGET(thiz, this.urlToFetch,
            
            function processResponse(thiz,responseText) {
                try {
                    const data = JSON.parse(responseText);
                    thiz.setState({
                        isFetching: false,
                        data: data
                    })
                } catch (e) {
                    thiz.setState({
                        isFetching: false,
                        data: null,
                        error: e
                    })
                }
            },

            function processError(thiz, xhttp, error) {
                var message;
                if (error)
                    message = "can't exec GET request because: " + error + "\n\ton url: " + thiz.urlToFetch;
                else {
                    const response = JSON.parse(xhttp.response);
                    message = "can't exec GET request because:\n\t[" + response.status + "/" + response.error + "] " + response.message + "\n\ton url: " + thiz.urlToFetch;
                }

                thiz.setState({
                    isFetching: false,
                    data: null,
                    error: message
                })
            }

        );
    }


    // Body

    render() {
        if (this.isStillFetching())
            return this.renderDataFetching();
        if (this.isNotFetching())
        return this.renderDataNotFetched();

        return (
            <Container>
                <Typography variant="body2" component="p">
                    {JSON.stringify(this.state.data)}
                </Typography>
            </Container>
        );
    }

    isStillFetching() {
        return !this.isFetched() && this.isFetching();
    }

    isNotFetching() {
        return !this.isFetched() && !this.isFetching();
    }

    renderDataFetching() {
        return (
            <Container>
                <Typography variant="body2" component="p">
                    Loading...
                </Typography>
            </Container>
        );
    }

    renderDataNotFetched() {
        return (
            <Container style={{display: 'flex', alignItems: 'center'}}>
                <Typography variant="body2" component="p" color="textSecondary" display="inline">
                    Error loading data
                </Typography>
                <Tooltip title={this.state.error}>
                    <InfoIcon color="error" style={{marginLeft: '10px'}} />
                </Tooltip>
            </Container>
        );
    }

}

export class ReactFetchingCard extends ReactFetching {

    constructor(props,urlToFetch,title) {
        super(props,urlToFetch);
        this.title = title;
    }

    render() {
        if (this.urlToFetch!=this.props.url) {
            this.urlToFetch=this.props.url;
            this.fetchData();
        }

        return (
            <Card style={{margin:'20px 0'}}>
                <CardContent>

                    <div style={{display: 'flex', justifyContent: 'space-between'}}>
                        <Typography variant="h5" component="h3">{this.title}</Typography>
                        
                        <Button color="primary" startIcon={<RefreshIcon />} style={{marginLeft: '16px'}}
                                onClick={() => this.fetchData()}>
                            Refresh
                        </Button>
                    </div>
                    {this.isStillFetching()
                        ? this.renderDataFetching()
                        : this.isNotFetching()
                            ? this.renderDataNotFetched()
                            : this.renderContent(this.state.data)
                    }
                </CardContent>
            </Card>    
        );
    }

}

export class ReactFetchingSubCard extends ReactFetching {

    constructor(props,urlToFetch,title) {
        super(props,urlToFetch);
        this.title = title;
    }

    render() {
        if (this.urlToFetch!=this.props.url) {
            this.urlToFetch=this.props.url;
            this.fetchData();
        }
        
        // {this.isStillFetching()
        //     ? this.renderDataFetching()
        //     : this.isNotFetching()
        //         ? this.renderDataNotFetched()
        //         : this.renderContent()
        // }
        // const title = <Typography id="java-runtime" variant="h6" component="h4">{this.title}</Typography>;
        // if (this.isStillFetching())
        //     return [title, this.renderDataFetching()];
        // if (this.isNotFetching())
        //     return [title, this.renderDataNotFetched()];

        return (
            <React.Fragment>
                {this.title}
                {this.isStillFetching()
                    ? this.renderDataFetching()
                    : this.isNotFetching()
                        ? this.renderDataNotFetched()
                        : this.renderContent(this.state.data)
                }
            </React.Fragment>
        );
    }

}