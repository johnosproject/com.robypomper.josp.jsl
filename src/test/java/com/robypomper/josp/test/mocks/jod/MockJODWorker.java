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

import com.robypomper.josp.jod.executor.JODWorker;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODComponentPath;
import com.robypomper.josp.jod.structure.JODContainer;
import com.robypomper.josp.jod.structure.StructureDefinitions;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MockJODWorker implements JODWorker {

    public static final String DEF_WORKER_NAME = "TEST Worker 001";
    public static final String DEF_WORKER_PROTO = "protocol";
    public static final JODComponent DEF_WORKER_COMP = new MockJODComponent("Component of the Mockup class for JODWorker class.");
    public static final Map<String, String> DEF_WORKER_CONFIGS = Collections.emptyMap();
    public static final boolean DEF_WORKER_IS_ENABLED = false;

    private final String workerName;
    private final String workerProto;
    private final JODComponent workerComponent;
    private final Map<String, String> workerConfigs;
    private final boolean workerIsEnabled;

    public MockJODWorker() {
        this(DEF_WORKER_NAME);
    }

    public MockJODWorker(String compName) {
        this(compName, DEF_WORKER_PROTO, DEF_WORKER_COMP, DEF_WORKER_CONFIGS, DEF_WORKER_IS_ENABLED);
    }

    public MockJODWorker(String workerName, String workerProto, JODComponent workerComponent, Map<String, String> workerConfigs, boolean workerIsEnabled) {
        this.workerName = workerName;
        this.workerProto = workerProto;
        this.workerComponent = workerComponent;
        this.workerConfigs = workerConfigs;
        this.workerIsEnabled = workerIsEnabled;
    }

    @Override
    public String getName() {
        return workerName;
    }

    @Override
    public String getProto() {
        return workerProto;
    }

    @Override
    public JODComponent getComponent() {
        return workerComponent;
    }

    @Override
    public Map<String, String> getConfigs() {
        return workerConfigs;
    }

    @Override
    public boolean isEnabled() {
        return workerIsEnabled;
    }

}
