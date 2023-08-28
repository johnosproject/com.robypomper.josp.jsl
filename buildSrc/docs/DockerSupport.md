# com.robypomper.josp.cloud.spring/buildSrc<br>Docker Support

Docker ([docker-compose](https://docs.docker.com/compose/reference/overview/))
support is based on
[com.avast.gradle:gradle-docker-compose-plugin](https://github.com/avast/gradle-docker-compose-plugin):
plugin.

New Docker micro-services can be added using the ```dockerCompose``` section
in the ```build.gradle``` file. Moreover, the support also helps to manage
docker-compose networks setting docker's tasks dependencies.


## Docker microservice tasks

From [docker-compose-plugin](https://github.com/avast/gradle-docker-compose-plugin)
documentation:

From ```docker``` group:
* ```{ComposeName}Down``` task stops the application and removes the containers, only if 'stopContainers' is set to 'true' (default value).
* ```{ComposeName}DownForced``` task stops the application and removes the containers.
* ```{ComposeName}Pull``` task pulls and optionally builds the images required by the application. This is useful, for example, with a CI platform that caches docker images to decrease build times.
* ```{ComposeName}Build``` task builds the services of the application.
* ```{ComposeName}Push``` task pushes images for services to their respective registry/repository.
* ```{ComposeName}Logs``` task stores logs from all containers to files in containerLogToDir directory.

Where ```{ComposeName}``` is the name used to define the ComposeSettings object
in ```dockerCompose``` section.

From ```docker maintenance``` group:
* ```test{NetworkName}Network``` task test if the docker network exist 
* ```create{NetworkName}Network``` task create the docker network if not exist
* ```remove{NetworkName}Network``` task remove the docker network

Where ```{NetworkName}``` is the name used to define the docker network used by
the docker micro-service.


## Docker microservice configuration

Docker based micro-services are composed at least from a docker-compose file.
The docker-compose file must refer directly an image or a micro-service custom
```Dockerfile``` file.

Then a new micro-service can be declared in main project's ```build.gradle``` file
with following lines:
 
```groovy
dockerCompose {
    dbms {
        useComposeFiles = ['src/{MS_NAME}/docker/docker-compose{[-VARIANT]}.yml']
    }
}
```

To add Gradle tasks for Spring Boot application build and run, remember to call
the ```DockerNetworkUtils.setDockerNetworkDependency(Project,ComposeSetting,String)``` method.

## Docker Test Example

A working example of Docker based micro-service can be found at dockerTest
[README.md](../../src/dockerTest/docs/README.md).

To run the example exec the command:
```shell
./gradlew dockerTestComposeUp
```

## Docker Support Classes

Following classes provide public support for Docker micro-services:
* ```com.robypomper.build.docker.DockerUtils``` provide static method to
  setup dependencies to docker's network tasks
