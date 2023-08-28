/*******************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
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

package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import asg.cliche.Shell;
import com.robypomper.java.JavaVersionUtils;
import com.robypomper.josp.jod.JOD_002;

public class CmdsShell {

    private final Shell shell;

    public CmdsShell(Shell shell) {
        this.shell = shell;
    }

    @Command(description = "Print current Java versions.")
    public String printJavaVersions() {
        return JavaVersionUtils.buildJavaVersionStr("John Object Daemon", JOD_002.VERSION);
    }

}
