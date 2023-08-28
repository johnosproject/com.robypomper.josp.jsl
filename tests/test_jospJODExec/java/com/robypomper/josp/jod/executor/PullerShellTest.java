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

import com.robypomper.java.JavaFiles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PullerShellTest {

    @Test
    public void pullerTest() throws InterruptedException, JODWorker.MissingPropertyException, JODWorker.ParsingPropertyException {
        String echoParam = "new state value " + new Date();
        String configs = String.format("cmd=echo '%s'", echoParam);
        CountDownLatch latch = new CountDownLatch(1);


        System.out.println("\nCREATE AND START PULLER FOR UNIX SHELL");
        PullerShell l = execCommand(configs, latch);

        System.out.println("\nWAIT UNIX SHELL EXECUTION");
        Assertions.assertTrue(latch.await(10, TimeUnit.SECONDS));

        System.out.println("\nSTOP PULLER FOR UNIX SHELL");
        l.stopTimer();
    }

    @Test
    public void pullerTestViaFile() throws InterruptedException, IOException, JODWorker.MissingPropertyException, JODWorker.ParsingPropertyException {
        String filePath = "pullerShellTest.txt";
        String configs = "cmd=cat " + filePath;
        CountDownLatch latch = new CountDownLatch(1);

        System.out.println("\nCREATE AND START PULLER FOR UNIX SHELL");
        PullerShell l = execCommand(configs, latch);

        System.out.println("\nUPDATE FILE");
        JavaFiles.writeString(filePath, "new state value " + new Date());
        Assertions.assertTrue(latch.await(10, TimeUnit.SECONDS));

        System.out.println("\nSTOP PULLER FOR UNIX SHELL");
        l.stopTimer();

        Paths.get(filePath).toFile().delete();
    }


    public PullerShell execCommand(String configs, CountDownLatch latch) throws JODWorker.MissingPropertyException, JODWorker.ParsingPropertyException {
        String name = "pullerTest";
        String proto = "shell";

        PullerShell l = new PullerShell(name, proto, configs, null) {
            @Override
            protected boolean convertAndSetStatus(String newStatus) {
                System.out.println("Status received: " + newStatus);
                latch.countDown();
                return true;
            }
        };
        l.startTimer();

        return l;
    }

}
