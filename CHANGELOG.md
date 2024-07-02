# JOSP Service Library - Changelog

[README](README.md) | [SPECS](docs/specs.md) | [GUIDES](docs/guides.md) | [CHANGELOG](CHANGELOG.md) | [TODOs](TODOs.md) | [LICENCE](LICENCE.md)


## Version 2.2.4

* Improved the JOSP Service Library documentation
* Updated to JOSP Commons 2.2.4
* Added integration tests
* Removed log4j-core dependency and all related Markers
* Replaced slf4j-api dependency with log4j-slf4j2-impl
* Updated GradleBuildInfo to version 2
* Communication
  * Added Security Levels to Local Communication
  * Updated SSL certificate provisioning
  * Added JSL settings for SSL certificate provisioning
  * Various fixes to Local clients
* Objects
  * Added filter by Model into ObjsMngr
* Others
  * Added JSLStateListener to JSL interface
  * Added the JSLListener class as support class for JSL's events registration


## Isolate JSL 2.2.3

* Removed all NOT JSL files
* Moved required files to jospJSL sourceSet
* Removed all NOT JSL Gradle configs
* Cleaned Gradle configs and tasks
* Moved jospJSL sourceSet to main sourceSet
* Moved all tests sourceSet to test sourceSet
* Updated all dependencies to latest versions
* Changed default jospDependenciesVersion behaviour
* Updated the Gradle Wrapper to version 8.3
* Removed buildSrc dependency
* Imported JSL docs from old repository
* Created TODOs.md
* Updated README.md, CHANGELOG.md and LICENCE.md to updated JOD repository
