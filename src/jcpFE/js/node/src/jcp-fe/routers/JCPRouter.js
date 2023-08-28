import React from "react";
import { Switch, Route } from "react-router-dom";

import { JCPHeader, JCPAPIsStatusPage, JCPGatewaysPage, JCPGatewaysStatusPage, JCPJSLWebBridgeStatusPage, JCPFrontEndStatusPage, JCPExecutablePage, JCPBuildInfoPage } from '../components/api-entities/JCP'


export class JCPRouter extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const jcpFE = this.props.jcpfe;
        const objId = this.props.match.params.objId;
        const jcpUrl = this.props.match.url;

        return (
            <div style={this.props.style}>
                <JCPHeader {...this.props} jcpfe={jcpFE} jcpUrl={jcpUrl} />
                <Switch>
                    <Route path={jcpUrl + "/apis/status"} render={(props) => ( <JCPAPIsStatusPage {...props} jcpfe={jcpFE} /> )} />
                    <Route path={jcpUrl + "/apis/executable"} render={(props) => ( <JCPExecutablePage {...props} jcpfe={jcpFE} service="apis" /> )} />
                    <Route path={jcpUrl + "/apis/buildinfo"} render={(props) => ( <JCPBuildInfoPage {...props} jcpfe={jcpFE} service="apis" /> )} />
                    
                    <Route path={jcpUrl + "/gateways/:gwServerId/status"} render={(props) => ( <JCPGatewaysStatusPage {...props} jcpfe={jcpFE} gwServerId={props.match.params.gwServerId} /> )} />
                    <Route path={jcpUrl + "/gateways/:gwServerId/executable"} render={(props) => ( <JCPExecutablePage {...props} jcpfe={jcpFE} service="gateways" gwServerId={props.match.params.gwServerId}  /> )} />
                    <Route path={jcpUrl + "/gateways/:gwServerId/buildinfo"} render={(props) => ( <JCPBuildInfoPage {...props} jcpfe={jcpFE} service="gateways" gwServerId={props.match.params.gwServerId} /> )} />
                    <Route path={jcpUrl + "/gateways"} render={(props) => ( <JCPGatewaysPage {...props} jcpfe={jcpFE} jcpUrl={jcpUrl} /> )} />
                    
                    <Route path={jcpUrl + "/jslwebbridge/status"} render={(props) => ( <JCPJSLWebBridgeStatusPage {...props} jcpfe={jcpFE} /> )} />
                    <Route path={jcpUrl + "/jslwebbridge/executable"} render={(props) => ( <JCPExecutablePage {...props} jcpfe={jcpFE} service="jslwebbridge" /> )} />
                    <Route path={jcpUrl + "/jslwebbridge/buildinfo"} render={(props) => ( <JCPBuildInfoPage {...props} jcpfe={jcpFE} service="jslwebbridge" /> )} />
                    
                    <Route path={jcpUrl + "/frontend/status"} render={(props) => ( <JCPFrontEndStatusPage {...props} jcpfe={jcpFE} /> )} />
                    <Route path={jcpUrl + "/frontend/executable"} render={(props) => ( <JCPExecutablePage {...props} jcpfe={jcpFE} service="frontend" /> )} />
                    <Route path={jcpUrl + "/frontend/buildinfo"} render={(props) => ( <JCPBuildInfoPage {...props} jcpfe={jcpFE} service="frontend" /> )} />
                </Switch>
            </div>
        );
    }

}