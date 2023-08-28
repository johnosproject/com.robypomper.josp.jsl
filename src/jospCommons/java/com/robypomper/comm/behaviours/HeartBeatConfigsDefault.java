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

import com.robypomper.comm.peer.Peer;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaListeners;

import java.util.ArrayList;
import java.util.List;

public class HeartBeatConfigsDefault implements HeartBeatConfigs {

    // Internal vars

    // heartbeat's configs
    private int timeoutMs;
    private int timeoutHBMs;
    private boolean enableHBRes;
    // heartbeat's listeners
    private final List<HeartBeatListener> listeners = new ArrayList<>();


    // Constructors

    public HeartBeatConfigsDefault() {
        this(TIMEOUT_MS);
    }

    public HeartBeatConfigsDefault(int timeoutMs) {
        this(timeoutMs, TIMEOUT_HB_MS);
    }

    public HeartBeatConfigsDefault(int timeoutMs, int timeoutHBMs) {
        this(timeoutMs, timeoutHBMs, ENABLE_HB_RES);
    }

    public HeartBeatConfigsDefault(int timeoutMs, int timeoutHBMs, boolean enableHBRes) {
        int minDiff = timeoutMs / 10;
        JavaAssertions.makeWarning(timeoutHBMs >= timeoutMs - minDiff, String.format("HeartBeat timeout (%d) can't be greater than network timeout (%d), reset heartbeat timeout to '%d'", timeoutHBMs, timeoutMs, timeoutMs - minDiff));
        if (timeoutHBMs > timeoutMs - minDiff)
            timeoutHBMs = timeoutMs - minDiff;
        setTimeout(timeoutMs);
        setHBTimeout(timeoutHBMs);
        enableHBResponse(enableHBRes);
    }


    // Getter/setters

    @Override
    public int getTimeout() {
        return timeoutMs;
    }

    @Override
    public void setTimeout(int ms) {
        timeoutMs = ms;
    }

    @Override
    public int getHBTimeout() {
        return timeoutHBMs;
    }

    @Override
    public void setHBTimeout(int ms) {
        timeoutHBMs = ms;
    }

    @Override
    public boolean isHBResponseEnabled() {
        return enableHBRes;
    }

    @Override
    public void enableHBResponse(boolean enabled) {
        enableHBRes = enabled;
    }


    // Listeners

    @Override
    public void addListener(HeartBeatListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HeartBeatListener listener) {
        listeners.remove(listener);
    }

    protected void emitOnSend(Peer peer, HeartBeatConfigs hb) {
        // Log already printed by PeerAbs::emitOnDataTx
        //log.trace(String.format("Peer '%s' send HB request", peer.getLocalId()));

        JavaListeners.emitter(this, listeners, "onSend", new JavaListeners.ListenerMapper<HeartBeatListener>() {
            @Override
            public void map(HeartBeatListener l) {
                l.onSend(peer, hb);
            }
        });
    }

    protected void emitOnSuccess(Peer peer, HeartBeatConfigs hb) {
        // Log already printed by PeerAbs::emitOnDataRx
        //log.trace(String.format("Peer '%s' received HB response", peer.getLocalId()));

        JavaListeners.emitter(this, listeners, "onSuccess", new JavaListeners.ListenerMapper<HeartBeatListener>() {
            @Override
            public void map(HeartBeatListener l) {
                l.onSuccess(peer, hb);
            }
        });
    }

    protected void emitOnFail(Peer peer, HeartBeatConfigs hb) {
        // Log already printed by PeerAbs::emitOnFail
        //log.warn(String.format("Peer '%s' failed HB request", peer.getLocalId()));

        JavaListeners.emitter(this, listeners, "onFail", new JavaListeners.ListenerMapper<HeartBeatListener>() {
            @Override
            public void map(HeartBeatListener l) {
                l.onFail(peer, hb);
            }
        });
    }

}
