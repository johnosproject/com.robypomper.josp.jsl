import React from "react";
import { Switch, Route } from "react-router-dom";

import JCPJSLWBStatus from '../../jcp-jsl-wb/components/JCPJSLWBStatus';
import { JCPFEDrawer, JCPFEDrawerSpacer } from '../components/app/App';
import { Dashboard } from '../components/app/Dashboard';
import { UserPage } from '../components/api-entities/User';
import { ServicePage } from '../components/api-entities/Service';
import { ObjectsListPage } from '../components/api-entities/Objects';
import { ObjectRouter } from './ObjectRouter';
import { JCPRouter } from './JCPRouter';


export class AppRouter extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const jcpFE = this.props.jcpfe;
        const pageWidth = window.innerWidth;
        var width;
        if (pageWidth>=1920)
          width = 'calc(100% - 56px)';
        else if (pageWidth>=1024)
          width = 'calc(100% - 56px)';
        else
          width = '100%';
        
        return (
                <Switch id="appRouter">
                    <Route path="/objects/:objId" render={(props) => (  
                        <React.Fragment>
                            <ObjectRouter {...props} jcpfe={jcpFE} objId={props.match.params.objId} style={{float: 'left', width: width}} />
                        </React.Fragment> 
                    )} />
                    <Route path="/objects" render={(props) => (  
                        <React.Fragment>
                            <ObjectsListPage {...props} jcpfe={jcpFE} style={{float: 'left', width: width}} />
                        </React.Fragment> 
                    )} />
                    <Route path="/user" render={(props) => (  
                        <React.Fragment>
                            <UserPage {...props} jcpfe={jcpFE} style={{float: 'left', width: width}} />
                        </React.Fragment> 
                    )} />
                    <Route path="/service" render={(props) => (  
                        <React.Fragment>
                            <ServicePage {...props} jcpfe={jcpFE} style={{float: 'left', width: width}} />
                        </React.Fragment> 
                    )} />
                    <Route path="/jcp" render={(props) => (  
                        <React.Fragment>
                            <JCPRouter {...props} jcpfe={jcpFE} style={{float: 'left', width: width}} />
                        </React.Fragment> 
                    )} />
                    <Route path="/stats" render={(props) => ( 
                        <React.Fragment>
                            <JCPJSLWBStatus {...props} jcpjslwb={jcpFE} style={{float: 'left', width: width}} />
                        </React.Fragment> 
                    )} />
                    <Route path="/" render={(props) => ( 
                        <React.Fragment>
                            <Dashboard {...props} jcpfe={jcpFE} style={{float: 'left', width: width}} />
                        </React.Fragment> 
                    )} />
                </Switch>
        );
    }

}

export function getIsSection_App() {
    const currentPath = location.pathname;
    const isObjects = currentPath.indexOf("objects") > 0;
    const isService = currentPath.indexOf("service") > 0;
    const isJCP = currentPath.indexOf("jcp") > 0;
    const isStats = currentPath.indexOf("stats") > 0;
    const isHome = !isObjects && !isService && !isJCP && !isStats;

    return {
        isHome: isHome,
        isObjects: isObjects,
        isService: isService,
        isJCP: isJCP,
        isStats: isStats,
    }
}
