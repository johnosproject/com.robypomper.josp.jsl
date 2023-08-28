import React from "react";

export default class ReactServiceBase extends React.Component {
    
    jcpFE = null;

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
        this.objId = props.objId;

        if (this.jcpFE.getService()==null)
            throw "Can't use ReactServiceBase component before initialize JCP JSL Web Bridge's 'service' system'";
        
        this.service = this.jcpFE.getService();
    }

    getService() {
        return this.service;
    }

}
