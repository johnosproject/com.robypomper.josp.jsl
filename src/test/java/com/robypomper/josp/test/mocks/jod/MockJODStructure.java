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

import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODComponentPath;
import com.robypomper.josp.jod.structure.JODRoot;
import com.robypomper.josp.jod.structure.JODStructure;

import java.util.Date;

public class MockJODStructure implements JODStructure {

    @Override
    public JODRoot getRoot() {
        return null;
    }

    @Override
    public JODComponent getComponent(String pathStr) {
        return null;
    }

    @Override
    public JODComponent getComponent(JODComponentPath path) {
        return null;
    }

    @Override
    public JODCommunication getCommunication() {
        return null;
    }

    @Override
    public void setCommunication(JODCommunication comm) {
    }

    @Override
    public void startAutoRefresh() {
    }

    @Override
    public void stopAutoRefresh() {
    }

    @Override
    public void syncObjStruct() {
    }

    @Override
    public String getStructForJSL() {
        return null;
    }

    @Override
    public Date getLastStructureUpdate() {
        return null;
    }
}
