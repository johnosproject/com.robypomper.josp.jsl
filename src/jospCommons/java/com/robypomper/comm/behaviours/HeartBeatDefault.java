/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.comm.behaviours;

import com.robypomper.comm.exception.PeerException;
import com.robypomper.comm.exception.PeerNotConnectedException;
import com.robypomper.comm.peer.Peer;
import com.robypomper.comm.peer.PeerAbs;
import com.robypomper.comm.peer.PeerConnectionListener;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaThreads;

import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class HeartBeatDefault extends HeartBeatConfigsDefault implements HeartBeatImpl {

    // Class constants

    public static final String TH_HB_WAITER_NAME = "HB_WAITER";


    // Internal vars

    // peer configs
    private final PeerAbs peer;
    // heartbeat's latch
    private CountDownLatch hbResponseLatch = null;
    private boolean isTimeoutExpired = false;


    // Constructors

    public HeartBeatDefault(PeerAbs peer) {
        this(peer, TIMEOUT_MS);
    }

    public HeartBeatDefault(PeerAbs peer, int timeoutMs) {
        this(peer, timeoutMs, TIMEOUT_HB_MS);
    }

    public HeartBeatDefault(PeerAbs peer, int timeoutMs, int timeoutHBMs) {
        this(peer, timeoutMs, timeoutHBMs, ENABLE_HB_RES);
    }

    public HeartBeatDefault(PeerAbs peer, int timeoutMs, int timeoutHBMs, boolean enableHBRes) {
        super(timeoutMs, timeoutHBMs, enableHBRes);
        this.peer = peer;
        this.peer.addListener(peerConnectionListener);
    }


    // Sender

    @Override
    public void sendHeartBeatReq() throws PeerException {
        if (hbResponseLatch != null) {
            JavaAssertions.makeWarning_Failed("Can't send HB message when already waiting for HB response.");
            return;
        }

        hbResponseLatch = new CountDownLatch(1);
        try {
            peer.sendData(MSG_HEARTBEAT_REQ.getBytes(peer.getDataEncodingConfigs().getCharset()));
            emitOnSend(peer, this);

        } catch (PeerNotConnectedException e) {
            hbResponseLatch = null;
            throw e;
        }

        JavaThreads.initAndStart(new HBWaiter(), TH_HB_WAITER_NAME, peer.getLocalId());
    }

    @Override
    public boolean isWaiting() {
        return hbResponseLatch != null;
    }

    @Override
    public boolean isTimeoutExpired() {
        return isTimeoutExpired;
    }

    private class HBWaiter implements Runnable {

        @Override
        public void run() {
            boolean hbResponse = false;
            isTimeoutExpired = false;
            try {
                hbResponse = hbResponseLatch.await(getHBTimeout(), TimeUnit.MILLISECONDS);

            } catch (InterruptedException ignore) {
            }
            hbResponseLatch = null;

            if (hbResponse)
                emitOnSuccess(peer, HeartBeatDefault.this);
            else {
                isTimeoutExpired = true;
                emitOnFail(peer, HeartBeatDefault.this);
            }
        }

    }


    // Processor

    @Override
    public boolean processHeartBeatMsg(byte[] data) throws PeerException {
        return doProcessHeartBeatResponse(data) || doProcessHeartBeatRequest(data);
    }

    private boolean doProcessHeartBeatResponse(byte[] data) {
        if (isWaiting() && Arrays.equals(MSG_HEARTBEAT_RES.getBytes(peer.getDataEncodingConfigs().getCharset()), data)) {
            hbResponseLatch.countDown();
            return true;
        }

        return false;
    }

    private boolean doProcessHeartBeatRequest(byte[] data) throws PeerException {
        if (isHBResponseEnabled() && Arrays.equals(MSG_HEARTBEAT_REQ.getBytes(peer.getDataEncodingConfigs().getCharset()), data)) {
            peer.sendData(MSG_HEARTBEAT_RES);
            return true;
        }

        return false;
    }


    // Peer listener

    @SuppressWarnings("FieldCanBeLocal")
    private final PeerConnectionListener peerConnectionListener = new PeerConnectionListener() {

        @Override
        public void onConnecting(Peer peer) {
        }

        @Override
        public void onWaiting(Peer peer) {
        }

        @Override
        public void onConnect(Peer peer) {
            isTimeoutExpired = false;
            try {
                peer.getSocket().setSoTimeout(getTimeout());

            } catch (SocketException e) {
                JavaAssertions.makeWarning_Failed(e, "Method Socket.setSoTimeout() should not throw SocketException.");
            }
        }

        @Override
        public void onDisconnecting(Peer peer) {
        }

        @Override
        public void onDisconnect(Peer peer) {
        }

        @Override
        public void onFail(Peer peer, String failMsg, Throwable exception) {
        }

    };

}
