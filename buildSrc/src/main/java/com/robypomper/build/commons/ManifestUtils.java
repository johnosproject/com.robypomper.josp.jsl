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

package com.robypomper.build.commons;

import com.robypomper.build.java.JavaPublicationUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ManifestUtils {

    public static final String EXT_DOCS = "docs";
    public static final String EXT_SOURCES = "sources";
    public static final String EXT_DEPS = "dependencies";

    public static Map<String, String> prepareManifestAttributes(String name, String groupId, String artifact, String version) {
        return prepareManifestAttributes(name, groupId, artifact, version, "", false);
    }

    public static Map<String, String> prepareManifestAttributes(String name, String groupId, String artifact, String version, boolean executable) {
        return prepareManifestAttributes(name, groupId, artifact, version, "", executable);
    }

    public static Map<String, String> prepareManifestAttributes(String name, String groupId, String artifact, String version, String nameExtension) {
        return prepareManifestAttributes(name, groupId, artifact, version, nameExtension, false);
    }

    public static Map<String, String> prepareManifestAttributes(String name, String groupId, String artifact, String version, String nameExtension, boolean executable) {
        Map<String, String> res = new HashMap<>();
        res.put("Implementation-Title", name + (nameExtension.isEmpty() ? "" : " - " + Naming.capitalize(nameExtension)));
        res.put("Implementation-Version", version);
        res.put("Implementation-Variant", (nameExtension.isEmpty() ? "default" : " - " + Naming.capitalize(nameExtension)));
        if (executable)
            res.put("Main-Class", (!groupId.isEmpty() ? groupId + "." : "") + Naming.capitalize(artifact));
        //res.put("Class-Path"                : sourceSets.jcpFE_Extended.runtimeClasspath.collect { "libs/" + it.getName() }.join(" "));
        res.put("Built-By", System.getProperty("user.name"));
        res.put("Build-Timestamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
        //res.put("Build-Revision"            : versioning.info.commit);
        res.put("Created-By", String.format("Gradle %s", System.getProperty("gradle.gradleVersion}")));
        res.put("Build-Jdk", String.format("%s (%s %s)", System.getProperty("java.version"), System.getProperty("java.vendor"), System.getProperty("java.vm.version")));
        res.put("Build-OS", String.format("%s %s %s", System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version")));
        return res;
    }

    public static Map<String, String> prepareManifestAttributes(JavaPublicationUtils.Descriptor pubDesc) {
        return prepareManifestAttributes(pubDesc, "");
    }

    public static Map<String, String> prepareManifestAttributes(JavaPublicationUtils.Descriptor pubDesc, String nameExtension) {
        //@formatter:off
        Map<String, String> res = new HashMap<>();
        res.put("Implementation-Title"      , pubDesc.name + (nameExtension.isEmpty() ? "" : " - " + Naming.capitalize(nameExtension)));
        res.put("Implementation-Version"    , pubDesc.version);
        res.put("Implementation-Variant"    , (nameExtension.isEmpty() ? "default" : " - " + Naming.capitalize(nameExtension)));
        if (pubDesc.executable)
            res.put("Main-Class"            , pubDesc.groupId + "." + Naming.capitalize(pubDesc.artifact));
        res.put("Built-By"                  , System.getProperty("user.name"));
        res.put("Build-Timestamp"           , new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
        res.put("Created-By"                , String.format("Gradle %s", System.getProperty("gradle.gradleVersion}")));
        res.put("Build-Jdk"                 , String.format("%s (%s %s)", System.getProperty("java.version"), System.getProperty("java.vendor"), System.getProperty("java.vm.version")));
        res.put("Build-OS"                  , String.format("%s %s %s", System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version")));
        StringBuilder classPath = new StringBuilder();
        for (File f : pubDesc.sourceSet.getRuntimeClasspath().getFiles())
            if (f.getName().endsWith(".jar"))
                classPath.append(String.format("libs/%s ", f.getName()));
        res.put("Class-Path"                , classPath.toString());
        //res.put("Build-Revision"            : versioning.info.commit);
        //@formatter:on
        return res;
    }

}
