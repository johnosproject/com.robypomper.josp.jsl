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

package com.robypomper.build.java;

import com.robypomper.build.commons.Naming;
import org.gradle.api.Project;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.jvm.tasks.Jar;

import java.io.File;
import java.util.Collections;


/**
 * Add support pure java applications.
 * <p>
 * This class register 2 tasks: jar and run tasks.
 * <p>
 * First one, the jar, task assemble in a single jar all source set java sources.
 * Second one, the run task, allow to execute the main class specified as a java
 * application.
 */
public class JavaRunnableUtils {

    /**
     * Create java application build and run tasks for given source set.
     *
     * @param project
     * @param ss
     * @param mainClass
     */
    static public void makeJavaFromSourceSet(Project project, SourceSet ss, String mainClass) {
        JavaRunnableUtils.configureJavaJarTask(project, ss, "");
        JavaRunnableUtils.configureJavaRunTask(project, ss, mainClass, "", project.getProjectDir());
    }

    /**
     * Create java application build and run tasks for given source set.
     *
     * @param project
     * @param ss
     * @param mainClass
     * @param taskBaseName
     */
    static public void makeJavaFromSourceSet(Project project, SourceSet ss, String mainClass, String taskBaseName) {
        JavaRunnableUtils.configureJavaJarTask(project, ss, taskBaseName);
        JavaRunnableUtils.configureJavaRunTask(project, ss, mainClass, taskBaseName, project.getProjectDir());
    }

    /**
     * Create java application build and run tasks for given source set.
     *
     * @param project
     * @param ss
     * @param mainClass
     * @param taskBaseName
     * @param workingDir
     */
    static public void makeJavaFromSourceSet(Project project, SourceSet ss, String mainClass, String taskBaseName, File workingDir) {
        JavaRunnableUtils.configureJavaJarTask(project, ss, taskBaseName);
        JavaRunnableUtils.configureJavaRunTask(project, ss, mainClass, taskBaseName, workingDir);
    }

    static private Jar configureJavaJarTask(Project project, SourceSet ss, String taskName) {
        if (taskName.isEmpty())
            taskName = ss.getName();
        else if (taskName.startsWith("_"))
            taskName = String.format("%s%s", ss.getName(), taskName);

        String jarTaskName = String.format("java%sJar", Naming.capitalize(taskName));
        Jar javaJar = project.getTasks().create(jarTaskName, Jar.class);
        javaJar.setDescription(String.format("Assembles an executable jar archive containing the %s classes and their dependencies.", ss.getName()));
        javaJar.setGroup(BasePlugin.BUILD_GROUP);
        javaJar.from(ss.getOutput());
        return javaJar;
    }

    static private JavaExec configureJavaRunTask(Project project, SourceSet ss, String mainClass, String taskName, File workingDir) {
        if (taskName.isEmpty())
            taskName = ss.getName();
        else if (taskName.startsWith("_"))
            taskName = String.format("%s%s", ss.getName(), taskName);

        String runTaskName = String.format("java%sRun", Naming.capitalize(taskName));
        String buildTaskName = String.format("java%sJar", Naming.capitalize(taskName));
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        JavaExec run = project.getTasks().create(runTaskName, JavaExec.class);
        run.setDescription(String.format("Runs this project %s sources as a Java application.", ss.getName()));
        run.setGroup(ApplicationPlugin.APPLICATION_GROUP);
        run.classpath(ss.getRuntimeClasspath());
        run.setMain(mainClass);
        run.setWorkingDir(workingDir);
        run.setJvmArgs(Collections.singletonList("-ea"));
        run.doFirst(task -> {
            if (!workingDir.exists())
                workingDir.mkdirs();
        });

        return run;
    }

}
