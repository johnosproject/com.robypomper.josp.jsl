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

package com.robypomper.josp.jod.executor;

import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jod.structure.AbsJODState;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.protocol.JOSPMsgParams;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.test.mocks.jod.MockActionCmd;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class ExecutorShellTest {

    @Test
    public void executorTest() throws InterruptedException, JODWorker.MissingPropertyException {
        String name = "executorTest";
        String proto = "shell";
        String echoParam = String.format("'read %s value at %s'", Substitutions.ACTION_VAL, new Date());
        String configs = String.format("cmd=echo %s", echoParam);

        System.out.println("\nCREATE AND START LISTENER FOR FILES");
        JODState state = null;
        try {
            state = new AbsJODState(null,null,null,"","","NONE",null) {
                @Override
                public String getState() {
                    return "N/A";
                }

                @Override
                public String getType() {
                    return "JODStateMock";
                }
            };
        } catch (JODStructure.ComponentInitException e) {
            e.printStackTrace();
        }
        ExecutorShell e = new ExecutorShell(name, proto, configs, state);
        JOSPProtocol.ActionCmd commandAction = new MockActionCmd();

        System.out.println("\nEXECUTE RANGE ACTION");
        String updStr = formatUpdStr(true, false);
        JODBooleanAction.JOSPBoolean cmdActionBoolean = new JODBooleanAction.JOSPBoolean(updStr);
        e.exec(commandAction, cmdActionBoolean);
        Thread.sleep(1000);
        //String readFile = JavaFiles.readString(Paths.get(filePath));
        //Assertions.assertTrue(JavaFormatter.strToBoolean(readFile));

        System.out.println("\nEXECUTE RANGE ACTION");
        updStr = formatUpdStr(5.33, 0.0);
        JODRangeAction.JOSPRange cmdActionRange = new JODRangeAction.JOSPRange(updStr);
        e.exec(commandAction, cmdActionRange);
        Thread.sleep(1000);
        //String readFile2 = JavaFiles.readString(Paths.get(filePath));
        //Assertions.assertEquals(new Double(5.33), JavaFormatter.strToDouble(readFile2));
    }

    public static String formatUpdStr(boolean newState, boolean oldState) {
        String updStr = String.format(JOSPMsgParams.KEY_VALUE_FORMAT, "new", newState);
        updStr += JOSPMsgParams.ITEMS_SEP;
        updStr += String.format(JOSPMsgParams.KEY_VALUE_FORMAT, "old", oldState);
        return updStr;
    }

    public static String formatUpdStr(double newState, double oldState) {
        String updStr = String.format(JOSPMsgParams.KEY_VALUE_FORMAT, "new", JavaFormatter.doubleToStr(newState));
        updStr += JOSPMsgParams.ITEMS_SEP;
        updStr += String.format(JOSPMsgParams.KEY_VALUE_FORMAT, "old", JavaFormatter.doubleToStr(oldState));
        return updStr;
    }

}
