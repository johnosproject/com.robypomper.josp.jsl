# JOSP Development

## What it builds?

Current source code allows build many artifacts and publish them as JOSP components. Because their nature, JOSP components can be represented by different kind of software (library, command line executable, user interface...) and can be implemented using different languages and frameworks (java, spring, node.js, dockers..). Each artifact must configure depending on his kind, language and frameworks. To do that, this project use the utils from the [/buildSrc](dev/buildsrc.md)
dir. This utils allow to configure each artifact with a small set of lines, but also homogenize artifact's configs along all project.

**Artifacts can group by category** depending on their kind, implementation language and required frameworks:

* [Java Library](dev/artifacts/java.md): a java software library; like jospCommons or jospJSL
* [Java Executable](dev/artifacts/java.md): a java software executable via command line or a daemon; like jospJOD or jospJSL (Shell version)
* [Spring Boot](dev/artifacts/spring.md): a java backend service based on spring framework; like jcpAPIs, jcpGateways...
* [Node.js](dev/artifacts/node.md): a node.js frontend app implemented with javascript (or derivatives); like jcpFEStatic
* [Docker](dev/artifacts/docker.md): a docker container that provide a single/multiple services; like jcpDB or jcpAuth

Check out artifact's category details for more details on their configuration and tasks_groups provided for artifact management (build, tests, publication..).

Click here for the [JOSP Components list](comps/INDEX.md).

---

## How build it?

**The JOSP source code is based on [Gradle](https://docs.gradle.org/) as build system and [Git](https://git-scm.com/) as VCS**.

That means JOSP developers can get and work with the source project just following those basic steps:

1. Clone the git repository
1. Execute Gradle's tasks (build, run, test, publish...)
1. Commit changes (if any) to the git repository

More info on Git and Gradle usage within the JOSP project at following pages:

* [JOSP Source Code Lifecycle](dev/lifecycle.md): steps to start new development, publish a release...
* [JOSP's Git conventions](dev/git.md): get source code (clone,branch), configure git flow
* [JOSP's Gradle organization](dev/gradle.md):  ```gradle/*``` files list and description, applied plugins, extra configs
* [JOSP's Gradle customizations](dev/gradle_josp_configs.md): customize JOSP build's configurations
* [JOSP's Gradle buildSrc](dev/buildsrc.md): ```buildSrc``` dir organization and docs
* [JOSP's Tests](dev/tests.md): run tests and analyze their results

----

## Other developer's info

### JOSP internal dependencies

JOSP internal dependencies refers to intra JOSP components dependencies.

This Gradle project resolve JOSP internal dependencies like external dependencies. That means, each time a Gradle task is executed, it looks for dependencies on all available maven repositories (central, stage and local). Also, when multiple components are builds, Gradle never use just compiled JOSP components, but always look for them in maven repositories.

Because of that, when you create a new version (that include also when the version's classifier changes) the next gradle task execution will fail. To prevent that, set the ```jospDependenciesVersion``` config with a value compatible with published JOSP components (at least on local repository).

```groovy file:gradle/josp_project.gradle
ext {
    ...

    // If jospMode=DEV, you can use CURRENT version
    ext.set('OVERRIDE_jospDependenciesVersion',ext.get('jospVersions.CURRENT'))
    // If jospMode=RELASE | STAGE, you can use PREV version
    ext.set('OVERRIDE_jospDependenciesVersion',ext.get('jospVersions.PREV'))
    
    // Else you can use custom values
    ext.set('OVERRIDE_jospDependenciesVersion','2.2.0')
    ext.set('OVERRIDE_jospDependenciesVersion',ext.get('jospVersions.220'))
    
    ...
}
```

When the ```jospDependenciesVersion``` config was set on desired value, publish all JOSP components locally with ```all_PublishToLocal``` gradle's task. After local publication finished successfully, you can re-comment the ```jospDependenciesVersion```
config.

### Public repository

Artifact's remote publications allow publishing JOSP components to remote repository and make them public available. Because of that artifact's remote publications are available only if ```enablePrivate``` config is ```true```.

All artifacts will be published with the base group ```com.robypomper.josp```. Java JOSP components are published on the maven repository [sonatype.org](https://oss.sonatype.org/)
where the ```com.robypomper``` was registered with
[OSSRH-45810](https://issues.sonatype.org/browse/OSSRH-45810?page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel&focusedCommentId=595848#comment-595848)
issue request. That means, specific Nexus/Sonatype's user and password are required to publish JOSP components, those credentials must be stored in the
```$USER_HOME/.gradle/gradle.properties``` file.

**Nexus/Sonatype's urls:**

* [Nexus/Sonatype portal](https://oss.sonatype.org/)
* [Staged artifacts](https://oss.sonatype.org/#nexus-search;quick~com.robypomper.josp)

**Nexus/Sonatype's guides:**

* [Getting started](https://central.sonatype.org/publish/publish-guide/)
* [GPG Keys](https://central.sonatype.org/publish/requirements/gpg/)
* [Gradle configs](https://central.sonatype.org/publish/publish-gradle/) (old gradle plugin 'maven')
* [Confirm release](https://central.sonatype.org/publish/release/)
