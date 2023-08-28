# com.robypomper.josp.cloud.spring/buildSrc<br>Multiple Spring Boot micro-services in single instance

This buildSrc project allow to run Spring Boot micro-services as stand alone instance.
But somtimes is required to execute multiple micr-services in a single instance,
for example when is required to run many micro-services on a single host or
virtual machine. Or even, just to aggregate similar micro-services like different
APIs sets.

Single instance services must be declares as SourceSet, like normal Spring Boot
micro-services. But dependencies are different: single instances depends only
by other Spring boot micro-services including their outputs and ```implementation```
configuration.

```groovy
dependencies {
    // mainRest source set (single instance) depends to rest1 source set (micro-service)
    mainRestImplementation sourceSets.rest1.output
    mainRestImplementation configurations.rest1Implementation
}
```

Again, like normal Spring boot micro-services, also single instances have to
setup running tasks calling the 
```SpringBuildUtils.makeSpringBootFromSourceSet(Project,SourceSet)``` method.


## Running example

Test micro-services: [springTest](../../src/springTest/docs/README.md),
[springTest2](../../src/springTest2/docs/README.md) and
[springTestSingleInstance](../../src/springTestSingleInstance/docs/README.md)
micro-services can be used to test the single instance method.

The springTest and springTest2 micro-services publish respectively:
* [localhost:5080/springTest](http://localhost:5080/springTest)
* [localhost:5081/springTest2](http://localhost:5081/springTest2)

But when joined in the springTestSingleInstance, it publish both endpoints:
* [localhost:5082/springTest](http://localhost:5082/springTest)
* [localhost:5082/springTest2](http://localhost:5082/springTest2)

Try it running:
```shell
./gradlew bootSpringTestRun
./gradlew bootSpringTest2Run
./gradlew bootSpringTestSingleInstanceRun
```


## build.gradle example

Here the ```build.gradle``` file for a complete single instance example.

```groovy
sourceSets {
    // Single instances sourceSets
	main {}             // Single instance      listening on http://localhost:7080/apis
                                                          // http://localhost:7080/rest1
                                                          // http://localhost:7080/rest2
	mainRest {}         // Single instance		listening on http://localhost:7180/rest1
                                                          // http://localhost:7180/rest2

	// Service's sourceSets
	apis {}             // APIs service         listening on http://localhost:7081/apis
	rest1 {}            // REST front-end1      listening on http://localhost:7082/rest1
	rest2 {}            // REST front-end2      listening on http://localhost:7083/rest2
}


// Declare boot{SourceSet}Jar and boot{SourceSet}Run tasks for single instance's source sets
// SpringBuildUtils.makeSpringBootFromSourceSet(project,sourceSets.main)    // already set by spring's gradle plugin
SpringBuildUtils.makeSpringBootFromSourceSet(project,sourceSets.mainRest)

// Declare boot{SourceSet}Jar and boot{SourceSet}Run tasks for service's source sets
SpringBuildUtils.makeSpringBootFromSourceSet(project,sourceSets.apis)
SpringBuildUtils.makeSpringBootFromSourceSet(project,sourceSets.rest1)
SpringBuildUtils.makeSpringBootFromSourceSet(project,sourceSets.rest2)


dependencies {
	// main sourceSet include all service's sourceSets
	implementation sourceSets.apis.output
	implementation configurations.apisImplementation
	implementation sourceSets.rest1.output
	implementation configurations.rest1Implementation
	implementation sourceSets.rest2.output
	implementation configurations.rest2Implementation

	// mainRest sourceSet include all service's sourceSets
	mainRestImplementation sourceSets.rest1.output
	mainRestImplementation configurations.rest1Implementation
	mainRestImplementation sourceSets.rest2.output
	mainRestImplementation configurations.rest2Implementation

	// apis sourceSet's dependencies
    apisImplementation 'org.springframework.boot:spring-boot-starter-web'
    apisImplementation ...
    
    // rest1 sourceSet's dependencies
    rest1Implementation 'org.springframework.boot:spring-boot-starter-web'
    rest1Implementation ...
    
    // rest2 sourceSet's dependencies
    rest2Implementation 'org.springframework.boot:spring-boot-starter-web'
    rest2Implementation ...
}
```

