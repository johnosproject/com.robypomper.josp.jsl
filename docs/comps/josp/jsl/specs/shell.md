# JOSP JSL Shell

The JOD Shell is the interactive command line provided with the JOD Agent.

It's initialized only when the JOD Agent is executed as foreground command and
allow user to manage internal JOD instance.

JSL Shell provide different commands. Here are listed all available commands.

* [JSL](#jsl): service's info and states
* [JCP](#jcp): JOSP Cloud connection management
* [User](#user): current user info and login/out management
* [Communication](#communication): direct and cloud communications management
* [Objects list & info](#objects-list--info): list available objects and print their info
* [Object's states](#objects-states): print object's states and their history
* [Object's actions](#objects-actions): send action request to objects
* [Object's events](#objects-actions): print object's events
* [Listeners](#listeners): register default listeners implementation for test purpose

A full list of available JSL Shell commands can be printed with the ```?list``` command.
Type the ```exit``` command, to exit from the JSL Shell and shutdown the JSL instance.

---

## JSL

### ```jsl-instance-info```

Print all JSL instance's info. This log is also printed on JSL instance
initialization.

*NB:* This command print instance's info using the Logging system, so the output
will print to the console depending on the Logging system configuration.


Example output:

```
JSL Srv
 -- IDs
        JSL Srv
            ID                = test-client-srv
            name              = Test Client Srv
        User
            ID                = 00000-00000-00000
            name              = Anonymous
 -- Ver.s
    JSL Srv state             = RUN
    JSL Srv version           = 2.2.2-DEV-202112131759
    JOSP JOD supported        = [2.0.0, 2.0.1]
    JOSP protocol supported   = [2.0]
    JCP APIs supported        = [2.0]
 -- Comm.s
        JCP APIs
            State             = CONNECTED_ANONYMOUS
            Url               = https://api.johnosproject.org/
            HostName          = 15.161.26.117
            IsConnected       = true
            IsAuth            = false
            LastConn          = Mon Dec 13 18:59:18 CET 2021
            LastDiscon        = null
        Cloud Comm.
            State (Client)    = CONNECTED
            HostName          = ec2-15-161-133-224.eu-south-1.compute.amazonaws.com
            IPAddr            = 15.161.133.224
            Port            = 9103
            IsConnected       = true
            LastConn          = Mon Dec 13 18:59:21 CET 2021
            LastDiscon        = null
            LastDisconReason  = NOT_DISCONNECTED
        Local Comm.
            State (ClientMngr)= RUN_WAITING
            IsRunning         = true
            ClientsCount      = 4
            ClientsConn       = 9
            ClientsDisconn    = 5
            LastStart         = Mon Dec 13 18:59:18 CET 2021
            LastStop          = null
            LastConn          = Mon Dec 13 19:18:43 CET 2021
            LastDisconn       = Mon Dec 13 19:18:43 CET 2021
 -- Objs Mngr
        Count                 = 16
        List                  = Mac_User, Satsuma_01, Meteo Bolzano, Meteo Trento, Kumquat_22, Star_fruit_10, N/A, Watermelon_35, Yuzu_56, Peach_66, Ugli_fruit_25, Pineberry_62, Blackberry_96, Jujube_42, Durian_69, Plantain_32, 
OK
```

###	```info-srv```

Print JOSP service's info like his id and name.
It prints also if current user is authenticated or not.

Example output:

```
SERVICE'S INFO 
  SrvId . . . . . test-client-srv
  SrvName . . . . Test Client Srv

  is user auth  . false
```

### ```jsl-state```

Print current JSL instance state. ```RUN``` if it's running, ```STOP``` if not.

Example output:

```
RUN
```

### ```jsl-instance-startup```

Start JSL instance.

You can check the JSL instance state using the [```jsl-state``` command](#jsl-state).

### ```jsl-instance-shutdown```

Stop JSL instance.<br/>
The internal instance can be stopped and then re-started with the [```jsl-instance-startup``` command](#jsl-instance-startup).

You can check the JSL instance state using the [```jsl-state``` command](#jsl-state).

### ```jsl-instance-restart```

Restart the JSL instance.

You can check the internal JSL instance state using the [```jsl-state``` command](#jsl-state).

### ```print-java-versions```

Print working environment info like java path and version, environment vars, jsl version...

Example output:

```
John Service Library versions:   2.2.2-DEV-202112131759
+-----------------
| Current dir:     /Users/robertopompermaier/Workspaces/josp.com/com.robypomper.josp/envs/runnables/jsl/JSL
+-----------------
| Java Version:    1.8(1.8.0_262)
| Java VM Version: 1.8
| Java Runtime:    OpenJDK Runtime Environment
| Java Home:       /Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home/jre
| Java Home (ENV): /Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home
| Java Vendor:     AdoptOpenJDK
| Java Libs Path:  /Users/user/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.
| Java ClassPath:  /Users/robertopompermaier/Workspaces/josp.com/com.robypomper.josp/build/classes/java/jospJSL:/Users/robertopompermaier/Workspaces/josp.com/com.robypomper.josp/build/resources/jospJSL:/Users/robertopompermaier/.m2/repository/com/robypomper/josp/jospCommons/2.2.2-DEV/jospCommons-2.2.2-DEV.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/commons-cli/commons-cli/1.4/c51c00206bb913cd8612b24abd9fa98ae89719b1/commons-cli-1.4.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/com.googlecode.clichemaven/cliche/110413/6b857d46798400f1f19f5fe7c4f6de4c5d16ede0/cliche-110413.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.apache.logging.log4j/log4j-slf4j-impl/2.13.1/79e92fe5b6c30cc4c8a52893378d4d130e298c65/log4j-slf4j-impl-2.13.1.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.apache.logging.log4j/log4j-core/2.13.1/533f6ae0bb0ce091493f2eeab0c1df4327e46ef1/log4j-core-2.13.1.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.apache.logging.log4j/log4j-api/2.13.1/cc670f92dc77bbf4540904c3fa211b997cba00d8/log4j-api-2.13.1.jar:/Users/robertopompermaier/.m2/repository/com/robypomper/josp/jospAPIs/2.2.2-DEV/jospAPIs-2.2.2-DEV.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.yaml/snakeyaml/1.25/8b6e01ef661d8378ae6dd7b511a7f2a33fae1421/snakeyaml-1.25.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/com.github.scribejava/scribejava-apis/6.9.0/a374c7a36533e58e53b42b584a8b3751ab1e13c4/scribejava-apis-6.9.0.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/com.github.scribejava/scribejava-core/6.9.0/ed761f450d8382f75787e8fee9ae52e7ec768747/scribejava-core-6.9.0.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.jmdns/jmdns/3.5.5/a156f2da3a29df61e2cab6f4d47c22f42114e2d/jmdns-3.5.5.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api/1.7.30/b5a4b6d16ab13e34a88fae84c35cd5d68cac922c/slf4j-api-1.7.30.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-databind/2.10.5.1/7ff756c3af1fe95cb3cddba9158fc3289ca06387/jackson-databind-2.10.5.1.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-annotations/2.10.5/33298de8da86f92f8ccd61ced214d3b16f8c531e/jackson-annotations-2.10.5.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-core/2.10.5/db2ba27938de7f2d478a97d6abcdaa17cbbd3cea/jackson-core-2.10.5.jar
+-----------------
| OS Name:         Mac OS X
| OS Version:      10.16
| Current user:    user
| Dflt language:   it
| Dflt encoding:   UTF-8
| File encoding:   UTF-8
| Env Vars:        PATH: /Users/user/Workspaces/java jdks/google-cloud-sdk/bin:/opt/local/bin:/opt/local/sbin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin
                   HOME: /Users/user
                   ...other env vars...
+-----------------
| Working dir:     /Users/robertopompermaier/Workspaces/josp.com/com.robypomper.josp/envs/runnables/jsl/JSL
+-----------------
```

---

## JCP

### ```jcp-client-status```

Print JCP connection state. ```JCP Client is connect (user not logged).``` if
current JSL instance is connected to the JCP but no user authenticated,
```JCP Client is NOT connect.``` if not.

### ```jcp-client-connect```

Connect JSL instance to the JCP.<br/>
At JSL Library startup, the JCP connection is initialized only if the property
```jcp.connect``` from [JSL Library configs](configs) is set to 'true'.

You can check the JCP connection state using the [```jcp-client-status``` command](#jcp-client-status).

### ```jcp-client-disconnect```

Disconnect JSL instance from JCP.<br/>
The internal instance can be disconnected and the re-connected with the [```jcp-client-connect``` command](#jcp-client-connect).

You can check the JCP connection state using the [```jcp-client-status``` command](#jcp-client-status).

---

## User

### ```jcp-user-login```

...


Example output:

```

```

### ```jcp-user-logout```

...


Example output:

```

```

---

## Communication

### ```comm-local-status```

...


Example output:

```

```

### ```comm-local-start```

...


Example output:

```

```

### ```comm-local-stop```

...


Example output:

```

```

### ```comm-print-all-local-connections```

...


Example output:

```

```

### ```comm-cloud-status```

...


Example output:

```

```

### ```comm-cloud-connect```

...


Example output:

```

```

### ```comm-cloud-disconnect```

...


Example output:

```

```

---

## Objects list & info

### ```objs-print-all```

...


Example output:

```

```

### ```objs-print-all-connected```

...


Example output:

```

```

### ```obj-print-object-info```

...p1


Example output:

```

```

### ```obj-set-object-name```

...p1, p2


Example output:

```

```

### ```obj-set-object-owner```

...p1, p2


Example output:

```

```

### ```obj-print-object-struct```

...p1


Example output:

```

```

### ```obj-print-object-connections```

...p1


Example output:

```

```

### ```obj-print-object-permissions```

...p1


Example output:

```

```

### ```obj-add-perm```

...p1, p2, p3, p4, p5


Example output:

```

```

### ```obj-upd-perm```

...p1, p2, p3, p4, p5, p6


Example output:

```

```

### ```obj-rem-perm```

...p1, p2


Example output:

```

```

---

## Object's states

### ```obj-status```

...p1, p2


Example output:

```

```

### ```obj-status-history```

...p1, p2


Example output:

```

```

### ```obj-status-history-latest```

...p1, p2


Example output:

```

```

### ```obj-status-history-ancient```

...p1, p2


Example output:

```

```

### ```obj-status-history-from-ID```

...p1, p2, p3


Example output:

```

```

### ```obj-status-history-to-ID```

...p1, p2, p3


Example output:

```

```

### ```obj-status-history-between-ID```

...p1, p2, p3, p4


Example output:

```

```

### ```obj-status-history-last-hour```

...p1, p2


Example output:

```

```

### ```obj-status-history-past-hour```

...p1, p2


Example output:

```

```

### ```obj-status-history-page```

...p1, p2, p3, p4


Example output:

```

```

### ```obj-status-history-error```

...p1, p2


Example output:

```

```

---

## Object's actions

### ```obj-action-boolean-switch```

...p1, p2


Example output:

```

```

### ```obj-action-boolean-true```

...p1, p2


Example output:

```

```

### ```obj-action-boolean-false```

...p1, p2


Example output:

```

```

### ```obj-action-range```

...p1, p2, p3


Example output:

```

```

### ```obj-action-range-increase```

...p1, p2


Example output:

```

```

### ```obj-action-range-decrease```

...p1, p2


Example output:

```

```

### ```obj-action-range-max```

...p1, p2


Example output:

```

```

### ```obj-action-range-min```

...p1, p2


Example output:

```

```

---

## Object's events

### ```obj-print-object-events```

...p1


Example output:

```

```

### ```obj-print-object-events-latest```

...p1


Example output:

```

```

### ```obj-print-object-events-ancient```

...p1


Example output:

```

```

### ```obj-print-object-events-from-ID```

...p1, p2


Example output:

```

```

### ```obj-print-object-events-to-ID```

...p1, p2


Example output:

```

```

### ```obj-print-object-events-between-ID```

...p1, p2, p3


Example output:

```

```

### ```obj-print-object-events-last-hour```

...p1


Example output:

```

```

### ```obj-print-object-events-past-hour```

...p1


Example output:

```

```

### ```obj-print-object-events-page```

...p1, p2, p3


Example output:

```

```

### ```obj-print-object-events-error```

...p1


Example output:

```

```

---

## Listeners

### ```objs-mngr-add-listeners```

...


Example output:

```

```

### ```objs-comm-add-listeners```

...


Example output:

```

```

### ```obj-add-listeners```

...(p1)


Example output:

```

```

### ```obj-component-add-listeners```

...p1, p2


Example output:

```

```

---
