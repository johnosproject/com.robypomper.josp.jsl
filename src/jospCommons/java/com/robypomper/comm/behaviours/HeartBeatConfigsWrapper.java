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

import com.robypomper.comm.client.ClientWrapper;

public class HeartBeatConfigsWrapper extends HeartBeatConfigsDefault {

    // Internal vars

    // wrapper
    private final ClientWrapper clientWrapper;


    // Constructors

    public HeartBeatConfigsWrapper(ClientWrapper clientWrapper) {
        this(clientWrapper, TIMEOUT_MS);
    }

    public HeartBeatConfigsWrapper(ClientWrapper clientWrapper, int timeoutMs) {
        this(clientWrapper, timeoutMs, TIMEOUT_HB_MS);
    }

    public HeartBeatConfigsWrapper(ClientWrapper clientWrapper, int timeoutMs, int timeoutHBMs) {
        this(clientWrapper, timeoutMs, timeoutHBMs, HeartBeatConfigs.ENABLE_HB_RES);
    }

    public HeartBeatConfigsWrapper(ClientWrapper clientWrapper, int timeoutMs, int timeoutHBMs, boolean enableHBRes) {
        this.clientWrapper = clientWrapper;

        setTimeout(timeoutMs);
        setHBTimeout(timeoutHBMs);
        enableHBResponse(enableHBRes);
    }


    // Getter/setters

    @Override
    public void setTimeout(int ms) {
        if (clientWrapper != null)
            if (clientWrapper.getWrapper() != null)
                clientWrapper.getWrapper().getHeartBeatConfigs().setTimeout(ms);

        super.setTimeout(ms);
    }

    @Override
    public void setHBTimeout(int ms) {
        if (clientWrapper != null)
            if (clientWrapper.getWrapper() != null)
                clientWrapper.getWrapper().getHeartBeatConfigs().setHBTimeout(ms);

        super.setHBTimeout(ms);
    }

    @Override
    public void enableHBResponse(boolean enabled) {
        if (clientWrapper != null)
            if (clientWrapper.getWrapper() != null)
                clientWrapper.getWrapper().getHeartBeatConfigs().enableHBResponse(enabled);

        super.enableHBResponse(enabled);
    }

}
