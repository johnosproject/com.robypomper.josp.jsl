# JOSP JSL Tasks Groups

## Runner tasks

### Standard

The JOSP JSL's running tasks execute the JSL library with the interactive shell.
This JSL implementation is called JSL Shell and can be executed with following
tasks:

```shell
./gradlew javaJSLRun
```

this task start a JSL library from a persistent working dir. This means that,
each JSL execution preserve the latest execution state (p.e. user login). After
the first git commit, this task's working dir is added to ```.gitignore```
so any modification will not commit to the repository.

### Vanilla

```shell
./gradlew javaJSLVanillaRun
```

at every execution, the task's working dir (and then his configs and data)
are cleaned. So with this task is always possible execute a JSL service with
default configs.

### Alternative

Standard and Vanilla runners starts a JSL instance with both cloud and local
communications enable. There are other runner tasks that execute JSL instances
with different communication settings:

| Gradle Tasks                                                   | Configs used                                                  | Description                                          |
|----------------------------------------------------------------|---------------------------------------------------------------|------------------------------------------------------|
| ```javaJSLOnlyLocalRun```<br>```javaJSLVanillaOnlyLocalRun```  | [jsl_only-local.yml](/src/jospJSL/configs/jsl_only-local.yml) | Start a JSL Shell with Cloud Comm disabled           |
| ```javaJSLOnlyCloudRun```<br>```javaJSLVanillaOnlyCloudRun```  | [jsl_only-cloud.yml](/src/jospJSL/configs/jsl_only-cloud.yml) | Start a JSL Shell with Local Comm disabled           |
| ```javaJSLNoCommRun```<br>```javaJSLVanillaNoCommRun```        | [jsl_no-comm.yml](/src/jospJSL/configs/jsl_no-comm.yml)       | Start a JSL Shell with Cloud and Local Comm disabled |

### User and cloud Connected

```shell
./gradlew javaJSL{UC}Run
```

like the ```javaJSLVanillaRun``` task but with pre-set configs:

| UC  | User        | Cloud auto-connect  |
|-----|-------------|---------------------|
| un  | Unset       | Yes                 |
| uf  | Unset       | No                  |
| so  | Set (Pinco) | Yes                 |
| sf  | Set (Pinco) | No                  |

### Discovery system

```shell
./gradlew javaJSL{Disc}Run
```

like the ```javaJSL{UC}Run``` tasks, but use different configs that sets local
discovery sub-systems. This tasks can be used to test different ZeroConf
implementations compatibilities and/or JSL services tolerance to test
Discovery/Network errors.

| Disc    | Discovery sub-system                                      |
|---------|-----------------------------------------------------------|
| Avahi   | Use the avahi damon installed on hosting Operating System |
| JmDNS   | Use the JmDNS implementation of the ZeroConfig protocol   |
| JmmDNS  | Use the JmDNS implementation of the ZeroConfig protocol   |

---
For most used tasks ```javaJSLRun``` and ```javaJSLVanillaRun``` task modifiers
are available adding strings ```OnlyCloud```, ```OnlyLocal``` and ```NoComm```
to the tasks name ```javaJSL{Mod}Run``` and ```javaJSLVanilla{Mod}Run```. Each
modifier start a JSL Shell instance with configurations that disable the cloud
communication, or disable the local communication or disable both communications.

## Cleaner tasks

```shell
./gradlew javaJSL_Clean
```

delete working dir of ```javaJSLRun``` task.
**NB:** not yet implemented ####.

## Publish tasks

The JOSP JOD publication package and publish following files:

| File                         | Content                                                                    |
|------------------------------|----------------------------------------------------------------------------|
| ```jsl-{VERSION}-java.jar``` | Jar file to include to JSL services                                        |
| ```jsl-{VERSION}-src.jar```  | Src package containing all sources used to compile the JOSP JSL library    |
| ```jsl-{VERSION}-doc.jar```  | Docs archive contains all Java docs from JSL's source code                 |
| ```jsl-{VERSION}-deps.jar``` | Deps jar archive provide all dependencies required by the JOSP JSL library |

```shell
./gradlew jospJSL_PublishToLocal
```

generate the publication artifacts and publish them to local maven repo.

```shell
./gradlew jospJSL_PublishToSonatype
```

generate the publication artifacts and publish them to public Sonatype repo.
