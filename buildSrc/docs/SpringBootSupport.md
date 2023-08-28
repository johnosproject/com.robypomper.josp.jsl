# com.robypomper.josp.cloud.spring/buildSrc<br>Spring Boot Support

[Spring Boot](https://spring.io/projects/spring-boot) support is based on
[org.springframework.boot:spring-boot-gradle-plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/)
plugin.

New Spring Boot micro-services can be added simply declaring a new sourceSet with
his dependencies and then create execution tasks with the support of 
```SpringBuildUtils.makeSpringBootFromSourceSet(Project,SourceSet)``` method.


## Spring Boot microservice tasks

From ```build``` group:
* ```boot{SourceSet}Jar``` task build the Spring Boot application's jar file

From ```application``` group:
* ```boot{SourceSet}Run``` task run the Spring Boot application locally


## Spring Boot microservice configuration

A Spring Boot application must be composed by his ```src/{MS_NAME}/resources/application.yml```
configs file and a ```@SpringBootApplication``` class in the ```src/{MS_NAME}/java/```
dir.

Then a new micro-service can be declared in main project's ```build.gradle``` file
with following lines:
```groovy
// Source Sets declaration
sourceSets {
    // Micro service's source sets
    springTest {}                   // {SRV_NAME}           on localhost:{MAPPED_PORT}
    springTest2 {}                  // {SRV_NAME}           on {CONTAINER_IP}:{EXPOSED_PORT}

    // Single instance source sets
    springTestSingleInstance {}     // Single instance      listening on localhost:{MAPPED_PORT}
                                                                    //   {CONTAINER_IP}:{EXPOSED_PORT}
}
```

The application can depend by other libraries or source set simply add dependencies
to the associated source set.
```groovy
dependencies {

    // Spring libreries
    {MS_NAME}Implementation 'org.springframework.boot:spring-boot'
    {MS_NAME}Implementation 'org.springframework.boot:spring-boot-autoconfigure'
    
    // Other micro services based on Spring Boot
    {MS_NAME}Implementation sourceSets.{OTHER_MS_NAME}.output
    {MS_NAME}Implementation configurations.{OTHER_MS_NAME}Implementation

}
```

To work properly a Spring Boot application must include ```com.springframework:spring-context```,
```com.springframework:spring-core``` and ```com.springframework:spring-boot-autoconfigure```
dependencies.

To add Gradle tasks for Spring Boot application build and run, remember to call
the ```SpringBuildUtils.makeSpringBootFromSourceSet(Project,SourceSet)``` method.


## Spring Boot Test Example

A working example of Spring Boot based micro-service can be found at dockerTest
[README.md](../../src/springTest/docs/README.md).

To run the example exec the command:
```shell
./gradlew botSpringTestRun
```

## Spring Boot Support Classes

Following classes provide public support for Docker micro-services:
* ```com.robypomper.build.spring.SpringBuildUtils``` provide static method to
  create Spring Boot application related tasks
* ```com.robypomper.build.spring.MainClassConvention2``` provide a public
  accessible MainClassConvention implementation
