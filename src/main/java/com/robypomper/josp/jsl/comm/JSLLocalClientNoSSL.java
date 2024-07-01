package com.robypomper.josp.jsl.comm;

import com.robypomper.comm.client.ClientAbsTCP;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPSecurityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class JSLLocalClientNoSSL extends ClientAbsTCP implements JSLLocalClient {
    // Internal vars

    private final JSLLocalClientsMngr clientsMngr;
    private JSLRemoteObject remoteObject = null;


    public JSLLocalClientNoSSL(JSLLocalClientsMngr clientsMngr, String srvFullId,
                               InetAddress remoteAddress, int port, String remoteObjId, PeerConnectionListener localClientListener) {
        super(srvFullId, remoteObjId, remoteAddress, port, JOSPProtocol.JOSP_PROTO_NAME);

        this.clientsMngr = clientsMngr;

        addListener(localClientListener);
    }


    // Message methods

    @Override
    protected boolean processData(byte[] data) {
        return false;
    }

    @Override
    protected boolean processData(String data) {
        return clientsMngr.processFromObjectMsg(this, data, JOSPPerm.Connection.OnlyLocal);
    }

    // JSL Local Client methods

    @Override
    public JSLRemoteObject getRemoteObject() {
        return remoteObject;
    }

    // JSL Local Client methods

    /**
     * When created, add corresponding JSLRemoteObject to current local client.
     *
     * @param remoteObject the JSLRemoteObject instance that use current local client
     *                     to communicate with object.
     */
    @Override
    public void setRemoteObject(JSLRemoteObject remoteObject) {
        if (this.remoteObject != null)
            throw new IllegalArgumentException("Can't set JSLRemoteObject twice for JSLLocalClient.");
        this.remoteObject = remoteObject;
    }


    // Others

    @Override
    public JOSPSecurityLevel getSecurityLevel() {
        return JOSPSecurityLevel.NoSSL;
    }

}
