import React from "react";

export default class ReactUserBase extends React.Component {
    
    jcpFE = null;

    constructor(props) {
        super(props);
        this.jcpFE = props.jcpfe;
        this.objId = props.objId;

        if (this.jcpFE.getUser()==null)
            throw "Can't use ReactUserBase component before initialize JCP JSL Web Bridge's 'user' system'";
        
        this.user = this.jcpFE.getUser();
    }

    getUser() {
        return this.user;
    }

}
