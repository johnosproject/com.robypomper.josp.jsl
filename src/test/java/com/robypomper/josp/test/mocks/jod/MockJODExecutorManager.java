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

package com.robypomper.josp.test.mocks.jod;

import com.robypomper.josp.jod.executor.JODExecutor;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.executor.JODListener;
import com.robypomper.josp.jod.executor.JODPuller;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.executor.JODComponentExecutor;
import com.robypomper.josp.jod.structure.executor.JODComponentListener;
import com.robypomper.josp.jod.structure.executor.JODComponentPuller;

import java.util.Collection;

public class MockJODExecutorManager implements JODExecutorMngr {

    @Override
    public JODPuller initPuller(JODComponentPuller component) {
        return null;
    }

    @Override
    public JODListener initListener(JODComponentListener component) {
        return null;
    }

    @Override
    public JODExecutor initExecutor(JODComponentExecutor component) {
        return null;
    }

    @Override
    public Collection<JODPuller> getPullers() {
        return null;
    }

    @Override
    public Collection<JODListener> getListeners() {
        return null;
    }

    @Override
    public Collection<JODExecutor> getExecutors() {
        return null;
    }

    @Override
    public void activateAll() {
    }

    @Override
    public void deactivateAll() {
    }

    @Override
    public void startPuller(JODComponent component) {
    }

    @Override
    public void stopPuller(JODComponent component) {
    }

    @Override
    public void startAllPullers() {
    }

    @Override
    public void stopAllPullers() {
    }

    @Override
    public void connectListener(JODComponent component) {
    }

    @Override
    public void disconnectListener(JODComponent component) {
    }

    @Override
    public void connectAllListeners() {
    }

    @Override
    public void disconnectAllListeners() {
    }

    @Override
    public void enableExecutor(JODComponent component) {
    }

    @Override
    public void disableExecutor(JODComponent component) {
    }

    @Override
    public void enableAllExecutors() {
    }

    @Override
    public void disableAllExecutors() {
    }

}
