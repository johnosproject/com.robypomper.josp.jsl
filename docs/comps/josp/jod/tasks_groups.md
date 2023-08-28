# JOSP JOD Tasks Groups

## Runner tasks

### Standard

To run the JOSP JOD agent this project provide different tasks for different
purposes. With Gradle's task, the JOD agent is always executed as interactive shell:

```shell
./gradlew javaJODRun
```

### Vanilla

this task start a JOD agent from a persistent working dir. This means that,
after the first execution (when the object initialize his ids, name, etc...),
all other execution keep the same configs. In other words when executed with
this task, the JOD agent always represent the same object. After the first git
commit, this task's working dir is added to ```.gitignore```
so any modification will not commit to the repository.

```shell
./gradlew javaJODVanillaRun
```

at every execution, the task's working dir (and then his configs and data)
are cleaned. So with this task is always possible execute a new JOD object with
default configs.

### Alternative

Standard and Vanilla runners starts a JOD instance with both cloud and local
communications enable. There are other runner tasks that execute JOD instances
with different communication settings:

| Gradle Tasks                                                  | Configs used                                                  | Description                                          |
|---------------------------------------------------------------|---------------------------------------------------------------|------------------------------------------------------|
| ```javaJODOnlyLocalRun```<br>```javaJODVanillaOnlyLocalRun``` | [jod_only-local.yml](/src/jospJOD/configs/jod_only-local.yml) | Start a JOD agent with Cloud Comm disabled           |
| ```javaJODOnlyCloudRun```<br>```javaJODVanillaOnlyCloudRun``` | [jod_only-cloud.yml](/src/jospJOD/configs/jod_only-cloud.yml) | Start a JOD agent with Local Comm disabled           |
| ```javaJODNoCommRun```<br>```javaJODVanillaNoCommRun```       | [jod_no-comm.yml](/src/jospJOD/configs/jod_no-comm.yml)       | Start a JOD agent with Cloud and Local Comm disabled |

### Object's ID, Owner and cloud Connected

```shell
./gradlew javaJOD{IOC}Run
```

like the ```javaJODVanillaRun``` task but with pre-set configs:

| IOC | Obj's IDs   | Obj's Owner | Cloud auto-connect |
|-----|-------------|-------------|--------------------|
| uun | Unset       | Unset       | Yes                |
| uuf | Unset       | Unset       | No                 |
| usn | Unset       | Set (Pinco) | Yes                |
| usf | Unset       | Set (Pinco) | No                 |
| cun | Set (Cloud) | Unset       | Yes                |
| cuf | Set (Cloud) | Unset       | No                 |
| csn | Set (Cloud) | Set (Pinco) | Yes                |
| csf | Set (Cloud) | Set (Pinco) | No                 |
| lun | Set (Local) | Unset       | Yes                |
| luf | Set (Local) | Unset       | No                 |
| lsn | Set (Local) | Set (Pinco) | Yes                |
| lsf | Set (Local) | Set (Pinco) | No                 |

### Discovery system

```shell
./gradlew javaJOD{Disc}Run
```

like the ```javaJOD{UC}Run``` tasks, but use different configs that sets local
discovery sub-systems. This tasks can be used to test different ZeroConf
implementations compatibilities and/or JOD object tolerance to test Discovery/Network errors.

| Disc   | Discovery sub-system                                      |
|--------|-----------------------------------------------------------|
| Avahi  | Use the avahi damon installed on hosting Operating System |
| JmDNS  | Use the JmDNS implementation of the ZeroConfig protocol   |
| JmmDNS | Use the JmDNS implementation of the ZeroConfig protocol   |

For most used tasks ```javaJODRun``` and ```javaJODVanillaRun``` task modifiers
are available adding strings ```OnlyCloud```, ```OnlyLocal``` and ```NoComm```
to the tasks name ```javaJOD{Mod}Run``` and ```javaJODVanilla{Mod}Run```. Each
modifier start a JOD agent instance with configurations that disable the cloud
communication, or disable the local communication or disable both communications.

----

## Cleaner tasks

```shell
./gradlew javaJOD_Clean
```

delete working dir of ```javaJODRun``` task.

----

## Publish tasks

The JOSP JOD publication package and publish following files:

| File                         | Content                                                                  |
|------------------------------|--------------------------------------------------------------------------|
| ```jod-{VERSION}-java.jar``` | Jar file used to run the JOD Agent as a interactive shell or a daemon    |
| ```jod-{VERSION}-src.jar```  | Src package containing all sources used to compile the JOSP JOD agent    |
| ```jod-{VERSION}-doc.jar```  | Docs archive contains all Java docs from JOD's source code               |
| ```jod-{VERSION}-deps.jar``` | Deps jar archive provide all dependencies required by the JOSP JOD agent |

```shell
./gradlew jospJOD_PublishToLocal
```

generate the publication artifacts and publish them to local maven repo.

```shell
./gradlew jospJOD_PublishToSonatype
```

generate the publication artifacts and publish them to public Sonatype repo.
