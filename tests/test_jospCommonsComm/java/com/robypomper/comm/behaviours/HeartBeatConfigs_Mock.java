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

public class HeartBeatConfigs_Mock implements HeartBeatConfigs {
    @Override
    public int getTimeout() {
        return 0;
    }

    @Override
    public void setTimeout(int ms) {
    }

    @Override
    public int getHBTimeout() {
        return 0;
    }

    @Override
    public void setHBTimeout(int ms) {
    }

    @Override
    public boolean isHBResponseEnabled() {
        return false;
    }

    @Override
    public void enableHBResponse(boolean enabled) {
    }

    @Override
    public void addListener(HeartBeatListener listener) {
    }

    @Override
    public void removeListener(HeartBeatListener listener) {
    }
}
