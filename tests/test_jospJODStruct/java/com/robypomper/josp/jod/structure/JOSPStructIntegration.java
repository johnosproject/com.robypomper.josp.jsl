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

package com.robypomper.josp.jod.structure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.history.JODHistory;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.JSLRoot_Jackson;
import com.robypomper.josp.test.mocks.jod.MockJODExecutorManager;
import com.robypomper.josp.test.mocks.jod.MockJODHistory;
import com.robypomper.josp.test.mocks.jod.MockJODStructure;
import com.robypomper.josp.test.mocks.jsl.MockJSLRemoteObject;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class JOSPStructIntegration {

    @Test
    public void integrationTest() throws IOException {
        // File containing jod structure in json
        String resourceName = "struct.jod";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());

        System.out.println("\nDESERIALIZE STRUCT.JOD");
        // Support objects
        JODStructure structure = new MockJODStructure();
        JODExecutorMngr executorMngr = new MockJODExecutorManager();
        JODHistory history = new MockJODHistory();

        JODRoot_Jackson jodRoot;
        // From: JODRoot JODStructure_002::loadStructure(String)
        try {
            ObjectMapper objMapper = new ObjectMapper();
            InjectableValues.Std injectVars = new InjectableValues.Std();
            injectVars.addValue(JODStructure.class, structure);
            injectVars.addValue(JODExecutorMngr.class, executorMngr);
            injectVars.addValue(JODHistory.class, history);
            objMapper.setInjectableValues(injectVars);

            jodRoot = objMapper.readerFor(JODRoot_Jackson.class).readValue(file);

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        System.out.println("JOD struct");
        printJODRoot(jodRoot);
        System.out.println("JOD paths");
        printJODRootPaths(jodRoot);


        System.out.println("\nSERIALIZE STRUCT.JSL");
        String jodStructureStr;
        // From: String JODStructure_002::getStructForJSL()
        try {
            ObjectMapper mapper = new ObjectMapper();
            jodStructureStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jodRoot);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw e;
        }

        System.out.println(jodStructureStr);

        System.out.println("\nDESERIALIZE STRUCT.JSL");
        // Support objects
        JSLRemoteObject remoteObject = new MockJSLRemoteObject();

        ObjectMapper mapper = new ObjectMapper();
        InjectableValues.Std injectVars = new InjectableValues.Std();
        injectVars.addValue(JSLRemoteObject.class, remoteObject);
        mapper.setInjectableValues(injectVars);
        JSLRoot jslRoot = mapper.readValue(jodStructureStr, JSLRoot_Jackson.class);

        System.out.println("JSL struct");
        printJSLRoot(jslRoot);
        System.out.println("JSL paths");
        printJSLRootPaths(jslRoot);

    }


    private void printJODRoot(JODRoot_Jackson root) {
        printJODCompRecursively(root, 0);
    }

    private void printJODCompRecursively(JODComponent comp, int indent) {
        String indentStr = new String(new char[indent]).replace('\0', ' ');
        System.out.printf("%s- %s%n", indentStr, comp.getName());

        if (comp instanceof JODContainer)
            for (JODComponent subComp : ((JODContainer) comp).getComponents())
                printJODCompRecursively(subComp, indent + 2);
    }

    private void printJODRootPaths(JODRoot root) {
        printJODCompRecursivelyPaths(root);
    }

    private void printJODCompRecursivelyPaths(JODComponent comp) {
        System.out.printf("- %s%n", comp.getPath().getString());

        if (comp instanceof JODContainer)
            for (JODComponent subComp : ((JODContainer) comp).getComponents())
                printJODCompRecursivelyPaths(subComp);
    }

    private void printJSLRoot(JSLRoot root) {
        printJSLCompRecursively(root, 0);
    }

    private void printJSLCompRecursively(JSLComponent comp, int indent) {
        String indentStr = new String(new char[indent]).replace('\0', ' ');
        System.out.printf("%s- %s%n", indentStr, comp.getName());

        if (comp instanceof JSLContainer)
            for (JSLComponent subComp : ((JSLContainer) comp).getComponents())
                printJSLCompRecursively(subComp, indent + 2);
    }

    private void printJSLRootPaths(JSLRoot root) {
        printJSLCompRecursivelyPaths(root);
    }

    private void printJSLCompRecursivelyPaths(JSLComponent comp) {
        System.out.printf("- %s%n", comp.getPath().getString());

        if (comp instanceof JSLContainer)
            for (JSLComponent subComp : ((JSLContainer) comp).getComponents())
                printJSLCompRecursivelyPaths(subComp);
    }

}
