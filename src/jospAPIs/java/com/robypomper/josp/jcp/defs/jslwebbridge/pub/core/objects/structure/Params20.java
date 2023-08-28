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

package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.structure;

import java.util.List;


/**
 * JCP JSL Web Bridge - Objects / Structure 2.0
 */
public class Params20 {

    public static class JOSPComponentHtml {

        public String name;
        public String description;
        public String objId;
        public String parentPath;
        public String componentPath;
        public String type;
        public String pathSelf;
        public String pathHistory;

    }

    public static class JOSPContainerHtml extends JOSPComponentHtml {

        public List<JOSPComponentHtml> subComps;

    }

    public static class JOSPStructHtml extends JOSPContainerHtml {

        public String brand;
        public String model;
        public String descrLong;

    }

    public static class JOSPBooleanStateHtml extends JOSPComponentHtml {

        public boolean state;
        public String pathState;

    }

    public static class JOSPBooleanActionHtml extends JOSPBooleanStateHtml {

        public String pathSwitch;
        public String pathTrue;
        public String pathFalse;

    }

    public static class JOSPRangeStateHtml extends JOSPComponentHtml {

        public double state;
        public String pathState;
        public double max;
        public double min;
        public double step;

    }

    public static class JOSPRangeActionHtml extends JOSPRangeStateHtml {

        public String pathSetValue;
        public String pathInc;
        public String pathDec;
        public String pathMax;
        public String pathMin;
        public String pathSet1_2;
        public String pathSet1_3;
        public String pathSet2_3;

    }

}
