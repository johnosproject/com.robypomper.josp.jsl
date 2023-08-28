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

import com.robypomper.build.commons.ManifestUtils;
import com.robypomper.build.commons.Naming;
import groovy.util.Node;
import groovy.xml.QName;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.publish.Publication;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.*;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.jvm.tasks.Jar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Add support pure java applications.
 * <p>
 * This class register 2 tasks: jar and run tasks.
 * <p>
 * First one, the jar, task assemble in a single jar all source set java sources.
 * Second one, the run task, allow to execute the main class specified as a java
 * application.
 */
@SuppressWarnings("unused")
public class JavaPublicationUtils {

    public static class Descriptor {
        public SourceSet sourceSet;
        public Configuration runtimeConfig;
        public String groupId;
        public String name;
        public String artifact;
        public String version;
        public String description;
        public boolean executable;

        public boolean artifactDeps;
        //public boolean artifactFat;
        public boolean artifactDoc;
        public boolean artifactSrc;

        public String url;
        public String git;
        public String gitUrl;

        public String licence;
        public String licenceUrl;

        public Descriptor(SourceSet sourceSet, Configuration runtimeConfig, String groupId, String name, String artifact, String version, String description) {
            this.sourceSet = sourceSet;
            this.runtimeConfig = runtimeConfig;
            this.groupId = groupId;
            this.name = name;
            this.artifact = artifact;
            this.version = version;
            this.description = description;
            this.executable = false;
            this.artifactDeps = false;
            //this.artifactFat = false;
            this.artifactDoc = false;
            this.artifactSrc = false;
        }

        public Descriptor(Project p, SourceSet sourceSet, String groupId, String name, String artifact, String version, String description) {
            this(sourceSet, p.getConfigurations().getByName(sourceSet.getImplementationConfigurationName()), groupId, name, artifact, version, description);
        }

        public Descriptor(Project p, SourceSet sourceSet, String name, String artifact, String version, String description) {
            this(sourceSet, p.getConfigurations().getByName(sourceSet.getImplementationConfigurationName()), p.getGroup().toString(), name, artifact, version, description);
        }

        //

        public Descriptor setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }


        // Default values

        public Descriptor setUrlsFromProject(Project p) {
            Object extObj = p.getExtensions().findByName("ext");
            if (extObj == null)
                return this;
            assert extObj instanceof ExtraPropertiesExtension;
            ExtraPropertiesExtension ext = (ExtraPropertiesExtension) extObj;

            try {
                Object pObj = ext.get("projectUrl");
                if (pObj != null)
                    this.url = (String) pObj;
            } catch (ExtraPropertiesExtension.UnknownPropertyException ignore) {
                p.getLogger().warn(String.format("Can't set 'url' from project.ext on '%s' [class %s]", this.name, this.getClass().getName()));
            }
            try {
                Object pObj = ext.get("projectGit");
                if (pObj != null)
                    this.git = (String) pObj;
            } catch (ExtraPropertiesExtension.UnknownPropertyException ignore) {
                p.getLogger().warn(String.format("Can't set 'git' from project.ext on '%s' [class %s]", this.name, this.getClass().getName()));
            }
            try {
                Object pObj = ext.get("projectGitUrl");
                if (pObj != null)
                    this.gitUrl = (String) pObj;
            } catch (ExtraPropertiesExtension.UnknownPropertyException ignore) {
                p.getLogger().warn(String.format("Can't set 'gitUrl' from project.ext on '%s' [class %s]", this.name, this.getClass().getName()));
            }
            return this;
        }

        public Descriptor setLicenceGPLv3() {
            this.licence = "GPLv3";
            this.licenceUrl = "https://www.gnu.org/licenses/gpl-3.0.html";
            return this;
        }

        public Descriptor setLicenceAGPLv3() {
            this.licence = "AGPLv3";
            this.licenceUrl = "https://www.gnu.org/licenses/agpl-3.0.html";
            return this;
        }

        public Descriptor setExecutable() {
            this.executable = true;
            return this;
        }

        public Descriptor setArtifactOpens() {
            this.artifactDoc = true;
            this.artifactSrc = true;
            return this;
        }

        public Descriptor setArtifactDistr() {
            this.artifactDeps = true;
            //this.artifactFat = true;
            return this;
        }

    }

    // Class constants

    public static final String CLASSIFIER_DOC = "javadoc";
    public static final String CLASSIFIER_SRC = "sources";
    public static final String CLASSIFIER_DEP = "deps";
    public static final String CLASSIFIER_FAT = "fat";
    public static final boolean JAVADOC_FAIL_ON_ERROR = false;


    // Publication tasks

    public static Publication generatePublication(Project project, Descriptor pubDescr) {
        Jar jarTask = configureJarTask(project, pubDescr);
        Jar depsTask = pubDescr.artifactDeps ? configureDepsJarTask(project, pubDescr) : null;
        Jar fatTask = null; /*pubDescr.artifactFat ? configureFatJarTask(project, pubDescr) : null;*/
        Javadoc docTask = pubDescr.artifactDoc ? configureJavaDocTask(project, pubDescr) : null;
        Jar docJarTask = pubDescr.artifactDoc ? configureJavaDocJarTask(project, pubDescr, docTask) : null;
        Jar srcJarTask = pubDescr.artifactSrc ? configureJavaSrcJarTask(project, pubDescr) : null;

        return addPublication(project, pubDescr, jarTask, depsTask, fatTask, docJarTask, srcJarTask);
    }

    public static String getJarTaskName(String name) {
        return String.format("%sJar", Naming.minuscule(name));
    }

    private static Jar configureJarTask(Project project, Descriptor pubDescr) {
        String jarTaskName = getJarTaskName(pubDescr.artifact);
        Jar javaJar = project.getTasks().create(jarTaskName, Jar.class);
        javaJar.setDescription(String.format("Assembles an jar archive containing the %s classes.", pubDescr.sourceSet.getName()));
        javaJar.setGroup(BasePlugin.BUILD_GROUP);
        doFirstArchiveBaseName(javaJar, pubDescr.artifact);
        javaJar.from(pubDescr.sourceSet.getOutput());
        javaJar.getManifest().attributes(ManifestUtils.prepareManifestAttributes(pubDescr));
        return javaJar;
    }

    public static String getDepsJarTaskName(String name) {
        return String.format("%sDepsJar", Naming.minuscule(name));
    }

    private static Jar configureDepsJarTask(Project project, Descriptor pubDescr) {
        String jarTaskName = getDepsJarTaskName(pubDescr.artifact);
        Jar javaJar = project.getTasks().create(jarTaskName, Jar.class);
        javaJar.setDescription(String.format("Assembles an jar archive containing all %s dependencies.", pubDescr.sourceSet.getName()));
        javaJar.setGroup(BasePlugin.BUILD_GROUP);
        javaJar.setClassifier(CLASSIFIER_DEP);
        doFirstArchiveBaseName(javaJar, pubDescr.artifact);
        List<File> fs = new ArrayList<>();
        for (File f : pubDescr.sourceSet.getRuntimeClasspath())
            if (f.exists() && !f.isDirectory())
                fs.add(f);
        javaJar.from(fs);
        javaJar.getManifest().attributes(ManifestUtils.prepareManifestAttributes(pubDescr, ManifestUtils.EXT_DEPS));
        return javaJar;
    }

//    public static String getFatJarTaskName(String name) {
//        return String.format("%sFatJar", Naming.minuscule(name));
//    }

//    private static Jar configureFatJarTask(Project project, Descriptor pubDescr) {
//        String jarTaskName = getFatJarTaskName(pubDescr.artifact);
//        Jar javaJar = project.getTasks().create(jarTaskName, Jar.class);
//        javaJar.setDescription(String.format("Assembles an jar archive containing the %s classes and all his dependencies.", pubDescr.sourceSet.getName()));
//        javaJar.setGroup(BasePlugin.BUILD_GROUP);
//        javaJar.setClassifier(CLASSIFIER_FAT);
//        doFirstArchiveBaseName(javaJar, pubDescr.artifact);
//        for (File f : pubDescr.sourceSet.getRuntimeClasspath())
//            if (f.exists() && !f.isDirectory())
//                javaJar.from(f);
//        javaJar.from(pubDescr.sourceSet.getOutput());
//        javaJar.rename("(.+).jar","libs/$1.jar");
//        Map<String,String> manifest = ManifestUtils.prepareManifestAttributes(pubDescr);
//        StringBuilder classPath = new StringBuilder();
//        for (File f : javaJar.getSource().getFiles())
//            if (f.getName().endsWith(".jar"))
//                classPath.append(String.format("libs/%s ", f.getName()));
//        manifest.put("Class-Path",classPath.toString());
//        //manifest.put("Class-Path","libs/");
//        System.out.println(classPath.toString());
//        javaJar.getManifest().attributes(manifest);
//        return javaJar;
//    }

    public static String getJavaDocTaskName(String name) {
        return String.format("%sDocs", Naming.minuscule(name));
    }

    private static Javadoc configureJavaDocTask(Project project, Descriptor pubDescr) {
        String docTaskName = getJavaDocTaskName(pubDescr.artifact);
        Javadoc javaDoc = project.getTasks().create(docTaskName, Javadoc.class);
        javaDoc.setDescription(String.format("Generate %s source set's Java Docs.", pubDescr.sourceSet.getName()));
        javaDoc.setGroup(JavaBasePlugin.DOCUMENTATION_GROUP);
        javaDoc.setTitle(String.format("%s JavaDocs", Naming.capitalize(pubDescr.name)));
        javaDoc.source(pubDescr.sourceSet.getAllJava().filter(new Spec<File>() {
            @Override
            public boolean isSatisfiedBy(File file) {       // This prevent javadoc generation for 2 classes that import from sun.security.x509 packages
                return !file.getName().equalsIgnoreCase("UtilsJKS.java")
                        && !file.getName().equalsIgnoreCase("UtilsSSL.java");
            }
        }));
        FileCollection classPath = javaDoc.getClasspath();
        classPath = classPath.plus(pubDescr.sourceSet.getRuntimeClasspath());
        classPath = classPath.plus(project.files(pubDescr.sourceSet.getCompileClasspath().getFiles()));
        classPath = classPath.plus(pubDescr.sourceSet.getAnnotationProcessorPath());
        javaDoc.setClasspath(classPath);
        javaDoc.setFailOnError(JAVADOC_FAIL_ON_ERROR);
        File outputDir = new File(javaDoc.getDestinationDir(), pubDescr.artifact);
        javaDoc.setDestinationDir(outputDir);
        return javaDoc;
    }

    public static String getJavaDocJarTaskName(String name) {
        return String.format("%sDocsJar", Naming.minuscule(name));
    }

    private static Jar configureJavaDocJarTask(Project project, Descriptor pubDescr, Javadoc javaDoc) {
        String docJarTaskName = getJavaDocJarTaskName(pubDescr.artifact);
        Jar javaDocJar = project.getTasks().create(docJarTaskName, Jar.class);
        javaDocJar.setDescription(String.format("Assembles an jar archive containing the %s Java Docs.", pubDescr.sourceSet.getName()));
        javaDocJar.setGroup(BasePlugin.BUILD_GROUP);
        javaDocJar.setClassifier(CLASSIFIER_DOC);
        doFirstArchiveBaseName(javaDocJar, pubDescr.artifact);
        javaDocJar.from(javaDoc.getOutputs());
        javaDocJar.getManifest().attributes(ManifestUtils.prepareManifestAttributes(pubDescr, ManifestUtils.EXT_DOCS));
        return javaDocJar;
    }

    public static String getJavaSrcJarTaskName(String name) {
        return String.format("%sSrcJar", Naming.minuscule(name));
    }

    private static Jar configureJavaSrcJarTask(Project project, Descriptor pubDescr) {
        String srcJarTaskName = getJavaSrcJarTaskName(pubDescr.artifact);
        Jar javaSrcJar = project.getTasks().create(srcJarTaskName, Jar.class);
        javaSrcJar.setDescription(String.format("Assembles an jar archive containing the %s source code.", pubDescr.sourceSet.getName()));
        javaSrcJar.setGroup(BasePlugin.BUILD_GROUP);
        javaSrcJar.setClassifier(CLASSIFIER_SRC);
        doFirstArchiveBaseName(javaSrcJar, pubDescr.artifact);
        javaSrcJar.from(pubDescr.sourceSet.getAllSource());
        javaSrcJar.getManifest().attributes(ManifestUtils.prepareManifestAttributes(pubDescr, ManifestUtils.EXT_SOURCES));
        return javaSrcJar;
    }

    private static Publication addPublication(Project project,
                                              Descriptor pubDescr,
                                              Jar jarTask,
                                              Jar depsTask,
                                              Jar fatTask,
                                              Jar docJarTask,
                                              Jar srcJarTask) {
        Object ext = project.getExtensions().findByName("publishing");
        assert ext instanceof PublishingExtension;
        PublicationContainer publicationsContainer = ((PublishingExtension) ext).getPublications();
        return configurePublication(project, pubDescr.runtimeConfig, publicationsContainer, pubDescr, jarTask, depsTask, fatTask, docJarTask, srcJarTask);
    }

    private static Publication configurePublication(Project project,
                                                    Configuration compRuntimeConfig,
                                                    PublicationContainer publications,
                                                    Descriptor pubDescr,
                                                    Jar jarTask,
                                                    Jar depsTask,
                                                    Jar fatTask,
                                                    Jar docJarTask,
                                                    Jar srcJarTask) {
        MavenPublication p = publications.create(pubDescr.artifact, MavenPublication.class);
        p.setGroupId(project.getGroup().toString());
        p.setArtifactId(pubDescr.artifact);
        p.setVersion(pubDescr.version);

        p.artifact(jarTask);
        if (depsTask != null) p.artifact(depsTask);
        if (fatTask != null) p.artifact(fatTask);
        if (docJarTask != null) p.artifact(docJarTask);
        if (srcJarTask != null) p.artifact(srcJarTask);

        JavaPublicationUtils.initPom(p.getPom(), pubDescr.name, pubDescr.description, pubDescr.url, pubDescr.git, pubDescr.gitUrl, pubDescr.licence, pubDescr.licenceUrl);
        JavaPublicationUtils.injectDependenciesToPom(p.getPom(), compRuntimeConfig, true);

        return p;
    }

    private static void doFirstArchiveBaseName(final Jar jarTask, String baseName) {
        jarTask.doFirst(new Action<Task>() {
            @Override
            public void execute(Task task) {
                jarTask.setBaseName(baseName);
            }
        });
    }


    // Maven POM Management

    static public void initPom(org.gradle.api.publish.maven.MavenPom pom, String name, String description,
                               String url, String urlGit, String urlGitRepo) {
        initPom(pom, name, description, url, urlGit, urlGitRepo, null, null);
    }

    static public void initPom(org.gradle.api.publish.maven.MavenPom pom, String name, String description,
                               String url, String urlGit, String urlGitRepo,
                               String licence, String urlLicence) {

        pom.getName().set(name);
        pom.getDescription().set(description);
        pom.getUrl().set(url);

        pom.licenses(new Action<MavenPomLicenseSpec>() {
            @Override

            public void execute(MavenPomLicenseSpec mavenPomLicenseSpec) {
                mavenPomLicenseSpec.license(new Action<MavenPomLicense>() {
                    @Override
                    public void execute(MavenPomLicense mavenPomLicense) {
                        if (licence != null) mavenPomLicense.getName().set(licence);
                        if (urlLicence != null) mavenPomLicense.getUrl().set(urlLicence);
                    }
                });
            }
        });

        pom.developers(new Action<MavenPomDeveloperSpec>() {
            @Override
            public void execute(MavenPomDeveloperSpec mavenPomDeveloperSpec) {
                mavenPomDeveloperSpec.developer(new Action<MavenPomDeveloper>() {
                    @Override
                    public void execute(MavenPomDeveloper mavenPomDeveloper) {
                        mavenPomDeveloper.getId().set("robypomper");
                        mavenPomDeveloper.getName().set("Roberto Pompermaier");
                        mavenPomDeveloper.getEmail().set("robypomper@johnosproject.com");
                    }
                });
            }
        });


        pom.scm(mavenPomScm -> {
            mavenPomScm.getConnection().set(String.format("scm:git:https://%s", urlGit));
            mavenPomScm.getDeveloperConnection().set(String.format("scm:git:ssh://%s", urlGit));
            mavenPomScm.getUrl().set(urlGitRepo);
        });

    }

    static public void injectDependenciesToPom(MavenPom pom, Configuration fromConfig, boolean preClean) {
        pom.withXml(xmlProvider -> {
            Node dependenciesNode = null;
            try {
                dependenciesNode = (Node) xmlProvider.asNode().getAt(new QName("dependencies")).get(0);
            } catch (IndexOutOfBoundsException ignore) {
            }
            if (dependenciesNode == null) dependenciesNode = xmlProvider.asNode().appendNode(new QName("dependencies"));
            final Node dependenciesNodeFinal = dependenciesNode;

            if (preClean)
                dependenciesNodeFinal.setValue("");

            fromConfig.getAllDependencies().forEach(it -> {
                if (it instanceof ExternalModuleDependency) {
                    Node dependencyNode = dependenciesNodeFinal.appendNode("dependency");
                    dependencyNode.appendNode("groupId", it.getGroup());
                    dependencyNode.appendNode("artifactId", it.getName());
                    dependencyNode.appendNode("version", it.getVersion());
                }
            });
        });
    }

    static public void clearAllDependenciesToPom(MavenPom pom) {
        pom.withXml(xmlProvider -> {
            Node dependenciesMngrNode = (Node) xmlProvider.asNode().getAt(new QName("dependencyManagement")).get(0);
            if (dependenciesMngrNode == null)
                dependenciesMngrNode = xmlProvider.asNode().appendNode(new QName("dependencyManagement"));

            Node dependenciesNode = (Node) dependenciesMngrNode.getAt(new QName("dependencies")).get(0);
            if (dependenciesNode == null) dependenciesNode = dependenciesMngrNode.appendNode(new QName("dependencies"));
            final Node dependenciesNodeFinal = dependenciesNode;

            dependenciesNodeFinal.setValue("");
        });
    }

}
