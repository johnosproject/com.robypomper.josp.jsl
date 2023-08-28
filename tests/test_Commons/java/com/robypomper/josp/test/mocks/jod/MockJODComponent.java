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

import com.robypomper.josp.jod.structure.*;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.util.Collections;
import java.util.List;

public class MockJODComponent implements JODComponent {

    public static final String DEF_COMP_NAME = "TEST Component 001";
    public static final String DEF_COMP_DESCR = "Mockup for JODComponent class.";
    public static final String DEF_COMP_TYPE = StructureDefinitions.TYPE_BOOL_STATE;
    public static final JODComponentPath DEF_COMP_PATH = null; // new MockJODComponentPath();
    public static final JODContainer DEF_COMP_PARENT = null; // new MockJODContainer(String.format("Parent of '%s'", MockJODComponent.DEF_COMP_NAME));
    public static final List<JOSPStatusHistory> DEF_COMP_HISTORY_LIST = Collections.emptyList();

    private final String compName;
    private final String compDescr;
    private final String compType;
    private final JODComponentPath compPath;
    private final JODContainer compParent;
    private final List<JOSPStatusHistory> compHistoryList;

    public MockJODComponent() {
        this(DEF_COMP_NAME);
    }

    public MockJODComponent(String compName) {
        this(compName, DEF_COMP_DESCR, DEF_COMP_TYPE, DEF_COMP_PATH, DEF_COMP_PARENT, DEF_COMP_HISTORY_LIST);
    }

    public MockJODComponent(String compName, String compDescr, String compType, JODComponentPath compPath, JODContainer compParent, List<JOSPStatusHistory> compHistoryList) {
        this.compName = compName;
        this.compDescr = compDescr;
        this.compType = compType;
        this.compPath = compPath;
        this.compParent = compParent;
        this.compHistoryList = compHistoryList;
    }

    @Override
    public String getName() {
        return compName;
    }

    @Override
    public String getDescr() {
        return compDescr;
    }

    @Override
    public JODComponentPath getPath() {
        return compPath;
    }

    @Override
    public JODContainer getParent() {
        return compParent;
    }

    @Override
    public String getType() {
        return compType;
    }

    @Override
    public List<JOSPStatusHistory> getHistoryStatus(HistoryLimits limits) {
        return compHistoryList;
    }

}
