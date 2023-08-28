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

package com.robypomper.build.node;

import com.github.gradle.node.npm.task.NpmTask;
import com.robypomper.build.commons.Naming;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;

import java.io.File;


/**
 * Add support to Node projects.
 * <p>
 * This class register 4 tasks to install modules (one task for dev modules and
 * another for project modules), build and run given Node.js project.
 */
public class NodeBuildUtils {

    public static abstract class NpmTaskExt extends NpmTask {

        @InputFiles
        public abstract ConfigurableFileCollection getConfigFiles();

        @OutputDirectory
        public abstract DirectoryProperty getOutputDir();

    }

    /**
     * Create Node.js project install, build and run tasks for given source set.
     *
     * @param project
     * @param sourceSetName
     */
    static public void makeNodeFromSourceSet(Project project, String sourceSetName, File sourceSetDir) {
        NpmTask npmInstallDev = NodeBuildUtils.configureNpmDevInstallTask(project, sourceSetName, sourceSetDir);
        NpmTask npmInstall = NodeBuildUtils.configureNpmInstallTask(project, sourceSetName, sourceSetDir, npmInstallDev);
        NodeBuildUtils.configureNpmBuildTask(project, sourceSetName, sourceSetDir, npmInstall);
        NodeBuildUtils.configureNodeRunTask(project, sourceSetName, sourceSetDir, npmInstall);
    }

    // node{SourceSetName}InstallDev
    static private NpmTask configureNpmDevInstallTask(Project project, String sourceSetName, File sourceSetDir) {
        String taskName = String.format("node%sInstallDev", Naming.capitalize(sourceSetName));
        NpmTaskExt npmInstallDev = project.getTasks().create(taskName, NpmTaskExt.class);

        npmInstallDev.getConfigFiles().setFrom(project.files(sourceSetDir + "/package.json"));
        npmInstallDev.getOutputDir().set(project.file(sourceSetDir + "/node_modules"));

        npmInstallDev.setDescription(String.format("Install Node.js DEV modules for '%s' project.", sourceSetName));
        npmInstallDev.setGroup(BasePlugin.BUILD_GROUP);
        npmInstallDev.getWorkingDir().set(sourceSetDir);
        String[] args = {"install",
                "--save-dev",
                //"@babel/cli",
                //"@babel/preset-env",
                //"@babel/preset-react",
                //"babel-loader",
                //"webpack",
                //"webpack-cli"
        };
        npmInstallDev.getArgs().addAll(args);
        return npmInstallDev;
    }

    // node{SourceSetName}Install
    static private NpmTask configureNpmInstallTask(Project project, String sourceSetName, File sourceSetDir, Task npmInstallDev) {
        String taskName = String.format("node%sInstall", Naming.capitalize(sourceSetName));
        NpmTaskExt npmInstall = project.getTasks().create(taskName, NpmTaskExt.class);

        npmInstall.getConfigFiles().setFrom(project.files(sourceSetDir + "/package.json"));
        npmInstall.getOutputDir().set(project.file(sourceSetDir + "/node_modules"));

        npmInstall.setDescription(String.format("Install Node.js modules for '%s' project.", sourceSetName));
        npmInstall.setGroup(BasePlugin.BUILD_GROUP);
        npmInstall.getWorkingDir().set(sourceSetDir);
        String[] args = {"install"};
        npmInstall.getArgs().addAll(args);

        npmInstall.dependsOn(npmInstallDev);
        return npmInstall;
    }

    // node{SourceSetName}Build       ex: processJcpFEResourcesNpmBuild
    static private NpmTask configureNpmBuildTask(Project project, String sourceSetName, File sourceSetDir, Task npmInstall) {
        String taskName = String.format("node%sBuild", Naming.capitalize(sourceSetName));
        NpmTaskExt npmBuild = project.getTasks().create(taskName, NpmTaskExt.class);

        npmBuild.getConfigFiles().setFrom(project.fileTree(sourceSetDir).exclude("/build").exclude("/node_modules").exclude("/.*"));
        npmBuild.getOutputDir().set(project.file(sourceSetDir + "/build"));

        npmBuild.setDescription(String.format("Build Node.js '%s' project.", sourceSetName));
        npmBuild.setGroup(BasePlugin.BUILD_GROUP);
        npmBuild.getWorkingDir().set(sourceSetDir);
        String[] cmds = {"run", "build"};
        npmBuild.getNpmCommand().addAll(cmds);


        npmBuild.dependsOn(npmInstall);
        return npmBuild;
    }

    // node{SourceSetName}Run
    static private NpmTask configureNodeRunTask(Project project, String sourceSetName, File sourceSetDir, Task npmInstall) {
        String taskName = String.format("node%sRun", Naming.capitalize(sourceSetName));
        NpmTask npmRun = project.getTasks().create(taskName, NpmTask.class);

        npmRun.setDescription(String.format("Run Node.js '%s' project.", sourceSetName));
        npmRun.setGroup("NPM Run");
        npmRun.getWorkingDir().set(sourceSetDir);
        // npm start

        String[] cmds = {"start"};
        npmRun.getNpmCommand().addAll(cmds);

        npmRun.dependsOn(npmInstall);
        return npmRun;
    }

}