package com.robypomper.josp.jsl.comm;

import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.peer.Peer;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.JOSPSecurityLevel;

public interface JSLLocalClient extends Peer {

    void connect() throws PeerConnectionException;

    void setRemoteObject(JSLRemoteObject remote);

    /**
     * The security level used by the current local server.
     *
     * @return the represented client's security level.
     */
    JOSPSecurityLevel getSecurityLevel();

}
