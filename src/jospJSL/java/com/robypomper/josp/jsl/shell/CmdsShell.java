/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import asg.cliche.Shell;
import com.robypomper.java.JavaVersionUtils;
import com.robypomper.josp.jsl.JSL_002;

public class CmdsShell {

    private final Shell shell;

    public CmdsShell(Shell shell) {
        this.shell = shell;
    }

    @Command(description = "Print current Java versions.")
    public String printJavaVersions() {
        return JavaVersionUtils.buildJavaVersionStr("ohn Service Library", JSL_002.VERSION);
    }

}
