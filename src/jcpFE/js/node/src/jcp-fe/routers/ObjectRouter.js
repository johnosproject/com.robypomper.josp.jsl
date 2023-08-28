import React from "react";
import { Switch, Route } from "react-router-dom";

import { ObjectHeader, ObjectEventsPage, ComponentHistoryPage, ObjectPermissionsPage, ObjectStatsPage, ObjectStructPage } from '../components/api-entities/Object'


export class ObjectRouter extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const jcpFE = this.props.jcpfe;
        const objId = this.props.match.params.objId;
        const objUrl = this.props.match.url;

        return (
            <div style={this.props.style}>
                <ObjectHeader jcpfe={jcpFE} objId={objId} objUrl={objUrl} />
                <Switch>
                    <Route path={objUrl + "/events"} render={(props) => ( <ObjectEventsPage {...props} jcpfe={jcpFE} objId={objId} /> )} />
                    <Route path={objUrl + "/status/:compPath"} render={(props) => ( <ComponentHistoryPage {...props} jcpfe={jcpFE} objId={objId} compPath={props.match.params.compPath} /> )} />
                    <Route path={objUrl + "/permissions"} render={(props) => ( <ObjectPermissionsPage {...props} jcpfe={jcpFE} objId={objId} /> )} />
                    <Route path={objUrl + "/stats"} render={(props) => ( <ObjectStatsPage {...props} jcpfe={jcpFE} objId={objId} /> )} />
                    <Route path={objUrl + "/"} render={(props) => ( <ObjectStructPage {...props} jcpfe={jcpFE} objId={objId} objUrl={objUrl} /> )} />
                </Switch>
            </div>
        );
    }

}

export function getIsSection_Object() {
    const currentPath = location.pathname;
    const isEvents = currentPath.indexOf("events") > 0;
    const isPermissions = currentPath.indexOf("permissions") > 0;
    const isStats = currentPath.indexOf("stats") > 0;
    const isHistory = currentPath.indexOf("status") > 0;
    const isStruct = !isEvents && !isPermissions && !isStats && !isHistory;

    return {
        isStruct: isStruct,
        isEvents: isEvents,
        isPermissions: isPermissions,
        isStats: isStats,
        isHistory: isHistory,
    }
}
