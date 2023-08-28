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

package com.robypomper.build.spring;

import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaApplication;
import org.springframework.boot.gradle.dsl.SpringBootExtension;
import org.springframework.boot.loader.tools.MainClassFinder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Provide public class
 * [org.springframework.boot.gradle.plugin.MainClassConvention](https://github.com/gradle/db-fork-spring-boot/blob/master/spring-boot-project/spring-boot-tools/spring-boot-gradle-plugin/src/main/java/org/springframework/boot/gradle/plugin/MainClassConvention.java)
 */
final class MainClassConvention2 implements Callable<Object> {

    private static final String SPRING_BOOT_APPLICATION_CLASS_NAME = "org.springframework.boot.autoconfigure.SpringBootApplication";

    private final Project project;

    private final Supplier<FileCollection> classpathSupplier;

    MainClassConvention2(Project project, Supplier<FileCollection> classpathSupplier) {
        this.project = project;
        this.classpathSupplier = classpathSupplier;
    }

    @Override
    public Object call() throws Exception {
        SpringBootExtension springBootExtension = this.project.getExtensions().findByType(SpringBootExtension.class);
        if (springBootExtension != null && springBootExtension.getMainClassName() != null) {
            return springBootExtension.getMainClassName();
        }
        JavaApplication javaApplication = this.project.getConvention().findByType(JavaApplication.class);
        if (javaApplication != null && javaApplication.getMainClassName() != null) {
            return javaApplication.getMainClassName();
        }
        return resolveMainClass();
    }

    private String resolveMainClass() {
        return this.classpathSupplier.get().filter(File::isDirectory).getFiles().stream().map(this::findMainClass)
                .filter(Objects::nonNull).findFirst().orElseThrow(() -> new InvalidUserDataException(
                        "Main class name has not been configured and it could not be resolved"));
    }

    private String findMainClass(File file) {
        try {
            return MainClassFinder.findSingleMainClass(file, SPRING_BOOT_APPLICATION_CLASS_NAME);
        } catch (IOException ex) {
            return null;
        }
    }

}