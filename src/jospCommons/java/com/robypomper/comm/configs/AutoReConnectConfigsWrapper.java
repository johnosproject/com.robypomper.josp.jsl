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

package com.robypomper.comm.configs;

import com.robypomper.comm.client.ClientWrapper;

public class AutoReConnectConfigsWrapper implements AutoReConnectConfigs {

    // Internal vars

    // wrapper
    private final ClientWrapper clientWrapper;
    // auto re-connect configs
    private boolean enable;
    private int delay;


    // Constructors

    public AutoReConnectConfigsWrapper(ClientWrapper clientWrapper) {
        this(clientWrapper, ENABLE, DELAY);
    }

    public AutoReConnectConfigsWrapper(ClientWrapper clientWrapper, boolean enable, int delayMs) {
        this.clientWrapper = clientWrapper;

        enable(enable);
        setDelay(delayMs);
    }


    // Getter/setters

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void enable(boolean enable) {
        if (clientWrapper.getWrapper() != null)
            clientWrapper.getWrapper().getAutoReConnectConfigs().enable(enable);

        this.enable = enable;
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public void setDelay(int ms) {
        if (clientWrapper.getWrapper() != null)
            clientWrapper.getWrapper().getAutoReConnectConfigs().setDelay(ms);

        this.delay = ms;
    }

}
