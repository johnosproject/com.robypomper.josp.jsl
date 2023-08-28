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

package com.robypomper.josp.jcp.defs.base.internal.status.buildinfo;


import java.util.HashMap;

/**
 * JCP All - Status / Build Info 2.0
 */
public class Params20 {

    // Build Info methods

    public static class BuildInfo extends com.robypomper.BuildInfo {

        public static BuildInfo clone(com.robypomper.BuildInfo other) {
            BuildInfo bi = new BuildInfo();
            bi.project = other.project;
            bi.sourceSet = other.sourceSet;
            bi.version = other.version;
            bi.versionBuild = other.versionBuild;
            bi.buildTime = other.buildTime;
            bi.javaVersion = other.javaVersion;
            bi.javaHome = other.javaHome;
            bi.gradleVersion = other.gradleVersion;
            bi.gitCommit = other.gitCommit;
            bi.gitCommitShort = other.gitCommitShort;
            bi.gitBranch = other.gitBranch;
            bi.user = other.user;
            bi.osName = other.osName;
            bi.osVersion = other.osVersion;
            bi.osArch = other.osArch;
            if (other.extra!=null)
                bi.extra = new HashMap<>(other.extra);
            return bi;
        }
    }

}
