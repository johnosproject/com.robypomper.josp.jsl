# JOSP Source Code Documentation index

This repository contains the source code, the documentation and scripts to build,
test and distribute the JOSP Project's artifacts and tools.

This repository basically is a [Gradle](https://gradle.org) project that handle
all source sets and relative tasks for all JOSP Project's components. Most of the
sources are [Java](https://www.java.com) based, but there is also some
[Node.js](https://nodejs.org/en/) subproject, like into the `JCP Front End` component.

## Main JOSP Repository's Docs
* [README](../README.md): proj. description, getting started (with source code), collaborate, versions, licenses
* [CHANGELOG](../CHANGELOG.md): changes list group by version and based on git merges
* [LICENSES](../LICENSES): JOSP components' licenses
* [VERSIONS](../VERSIONS): JOSP components and runtime requirements versions

## JOSP Development's Docs
* [INDEX](INDEX.md)
* [DEVELOPMENT](development.md)
  * [JOSP Source Code Lifecycle](dev/lifecycle.md): steps to start new development, publish a release...
  * [JOSP's Git conventions](dev/git.md): get source code (clone,branch), configure git flow
  * [JOSP's Gradle organization](dev/gradle.md):  ```gradle/*``` files list and description, applied plugins, extra configs
  * [JOSP's Gradle customizations](dev/gradle_josp_configs.md): customize JOSP build's configurations
  * [JOSP's Gradle buildSrc](dev/buildsrc.md): ```buildSrc``` dir organization and docs
  * [JOSP's Tests](dev/tests.md): run tests and analyze their results
* [COMPONENTS LIST](comps/INDEX.md)
* COMPONENTS TYPES
  * [Java Library](dev/artifacts/java.md): a java software library; like jospCommons or jospJSL
  * [Java Executable](dev/artifacts/java.md): a java software executable via command line or a daemon; like jospJOD or jospJSL (Shell version)
  * [Spring Boot](dev/artifacts/spring.md): a java backend service based on spring framework; like jcpAPIs, jcpGateways...
  * [Node.js](dev/artifacts/node.md): a node.js frontend app implemented with javascript (or derivatives); like jcpFEStatic
  * [Docker](dev/artifacts/docker.md): a docker container that provide a single/multiple services; like jcpDB or jcpAuth

## JOSP Project's Docs
features
manuals
media
resources