# com.robypomper.josp.cloud.spring/buildSrc

This is ```buildSrc``` project for [com.robypomper.josp](../README.md)
project. This buildSrc project add support for Docker and Spring based
micro-services with a set of static method that extends default Gradle plugins. 

This buildSrc project add support to build, run and publish micro-service based on:
* [Docker](docs/DockerSupport.md)
* [Spring](docs/SpringBootSupport.md)


## Usage

**Setup main project**<br>
In main project's ```build.gradle``` files is required to apply following plugins:

```groovy
plugins {
    // Docker service management
    id 'com.avast.gradle.docker-compose' //version '0.10.9'

    // Spring Boot management
    id 'org.springframework.boot' //version '2.2.4.RELEASE'                     version not required because specified in buildSrc/build.gradle
    id 'io.spring.dependency-management' //version '1.0.9.RELEASE'              version not required because specified in buildSrc/build.gradle
    id 'java'
}
```

**NB!:** plugin's versions are not defined because they are alredy defined in 
buildSrc's ```buildSrc/build.gradle``` file.

**Add Docker micro-services**<br>
Docker micro-services can be add as described by the 
[com.avast.gradle:gradle-docker-compose-plugin](https://github.com/avast/gradle-docker-compose-plugin)
plugin. Simply add a new ```com.avast.gradle.dockercompose.ComposeSettings```
to the ```dockerCompose```. Then link created micro-service with Docker Network
tasks.

```groovy
dockerCompose {
    dockerTest {
        useComposeFiles = ['src/dockerTest/docker/docker-compose-test.yml']
    }
}

// Add docker's network dependency
DockerUtils.setDockerNetworkDependency(project,dockerCompose.dockerTest,"rp-test")
```

For details on how to create Docker based micro-service check out the
[Docker Support](docs/DockerSupport.md#Docker-microservice-configuration) page.

**Add Spring Boot micro-services**<br>
In order to add a Spring Boot based microservice to the project, it's required
define a sourceSet and his dependencies. Then with the support of 
```SpringBuildUtils.makeSpringBootFromSourceSet(Project,SourceSet)``` method
Spring Boot's tasks are created. See [Spring Boot Support](docs/SpringBootSupport.md#Spring-Boot-microservice-tasks)
for complete list of created tasks.

```groovy
sourceSets {
	// Service's sourceSets
	apis {}             // APIs service         listening on http://localhost:7081/apis
	rest1 {}            // REST front-end1      listening on http://localhost:7082/rest1
}


// Declare boot{SourceSet}Jar and boot{SourceSet}Run tasks for service's source sets
SpringBuildUtils.makeSpringBootFromSourceSet(project,sourceSets.apis)
SpringBuildUtils.makeSpringBootFromSourceSet(project,sourceSets.rest1)


dependencies {

	// apis sourceSet's dependencies
    apisImplementation 'org.springframework.boot:spring-boot-starter-web'
    apisImplementation ...
    
    // rest1 sourceSet's dependencies
    rest1Implementation 'org.springframework.boot:spring-boot-starter-web'
    rest1Implementation ...
}
```

Different Spring Boot micro-services, can be executed also in a single instance.
That allow to deploy multiple Spring Boot based services using a single virtual
machine. Check out [Spring Boot Single Instance](docs/SpringBootSingleInstance.md)
for more details.

For details on how to create Spring Boot based micro-service check out the
[Spring Boot Support](docs/SpringBootSupport.md#Spring-Boot-microservice-configuration) page.
