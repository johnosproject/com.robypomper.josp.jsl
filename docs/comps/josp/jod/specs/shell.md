# JOSP JOD Shell

The JOD Shell is the interactive command line provided with the JOD Agent.

It's initialized only when the JOD Agent is executed as foreground command and
allow user to manage internal JOD instance.

JOD Shell provide different commands. Here are listed all available commands
grouped by JOD Sub System they interact with.

* [JOD](#jod): object's info and states
* [JCP](#jcp): JOSP Cloud connection management
* [Communication](#communication): direct and cloud communications management
* [Permissions](#permissions): list and edit object's permissions
* [Structure](#structure): object's structure queries
* [Workers](#workers): workers testing commands

A full list of available JOD Shell commands can be printed with the ```?list```
command. Type the ```exit``` command, to exit from the JOD Shell and to shut down
the JOD Agent.

---

## JOD

### ```info```

Print current object's info like object's id, name, model, brand... and the owner id.

Example output:

```
OBJECT'S INFO
  ObjId . . . . . AAQNQ-AENAM-XCQUM
  ObjName . . . . Gooseberry_44
  Model . . . . . JOD Test Object 2.0
  Brand . . . . . John OS
  Descr . . . . . Object to use in design testing
USER'S INFO
  OwnerId . . . . f89b2e21-d3cb-41d2-8349-ef2d5e247f37
```

### ```info-obj```

Print current object's info like object's id, name, model, brand...

Example output:

```
OBJECT'S INFO
  ObjId . . . . . AAQNQ-AENAM-XCQUM
  ObjName . . . . Gooseberry_44
  Model . . . . . JOD Test Object 2.0
  Brand . . . . . John OS
  Descr . . . . . Object to use in design testing
```

### ```info-set-object-name (new_name)```

Update current object's name.

You can set any valid string as object's name, also including space and special chars.
```
JOD> info-set-object-name "Entrance Lamp 1"
```

### ```info-user```

Print current object owner's info like owner id.

Example output:

```
USER'S INFO
  OwnerId . . . . f89b2e21-d3cb-41d2-8349-ef2d5e247f37
```

### ```jod-state```

Print current JOD state. ```RUN``` if it's running, ```STOP``` if not.

### ```jod-start```

Start internal JOD instance.<br/>
The internal instance is started by default on each JOD Agent startup.

You can check the internal JOD instance state using the [```jod-state``` command](#jod-state).

### ```jod-stop```

Stop internal JOD instance.<br/>
The internal instance can be stopped and then re-started with the [```jod-start``` command](#jod-start).

You can check the internal JOD instance state using the [```jod-state``` command](#jod-state).

### ```print-java-versions```

Print working environment info like java path and version, environment vars, jod version...

Example output:

```
John Object Daemon versions:   2.2.2-DEV-202110111459
+-----------------
| Current dir:     /Users/robertopompermaier/Workspaces/josp.com/com.robypomper.josp/envs/runnables/jod/JOD
+-----------------
| Java Version:    1.8(1.8.0_262)
| Java VM Version: 1.8
| Java Runtime:    OpenJDK Runtime Environment
| Java Home:       /Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home/jre
| Java Home (ENV): /Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home
| Java Vendor:     AdoptOpenJDK
| Java Libs Path:  /Users/user/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.
| Java ClassPath:  /Users/user/Workspaces/josp.com/com.robypomper.josp/build/classes/java/jospJOD:/Users/robertopompermaier/Workspaces/josp.com/com.robypomper.josp/build/resources/jospJOD:/Users/robertopompermaier/.m2/repository/com/robypomper/josp/jospCommons/2.2.2-DEV/jospCommons-2.2.2-DEV.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/commons-cli/commons-cli/1.4/c51c00206bb913cd8612b24abd9fa98ae89719b1/commons-cli-1.4.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/com.googlecode.clichemaven/cliche/110413/6b857d46798400f1f19f5fe7c4f6de4c5d16ede0/cliche-110413.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/net.sourceforge.htmlcleaner/htmlcleaner/2.24/3e193655071befbf2a1ed5efb7d2d05deb302325/htmlcleaner-2.24.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/com.jayway.jsonpath/json-path/2.4.0/765a4401ceb2dc8d40553c2075eb80a8fa35c2ae/json-path-2.4.0.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.apache.logging.log4j/log4j-slf4j-impl/2.13.1/79e92fe5b6c30cc4c8a52893378d4d130e298c65/log4j-slf4j-impl-2.13.1.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.apache.logging.log4j/log4j-core/2.13.1/533f6ae0bb0ce091493f2eeab0c1df4327e46ef1/log4j-core-2.13.1.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.apache.logging.log4j/log4j-api/2.13.1/cc670f92dc77bbf4540904c3fa211b997cba00d8/log4j-api-2.13.1.jar:/Users/robertopompermaier/.m2/repository/com/robypomper/josp/jospAPIs/2.2.2-DEV/jospAPIs-2.2.2-DEV.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.yaml/snakeyaml/1.25/8b6e01ef661d8378ae6dd7b511a7f2a33fae1421/snakeyaml-1.25.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/com.github.scribejava/scribejava-apis/6.9.0/a374c7a36533e58e53b42b584a8b3751ab1e13c4/scribejava-apis-6.9.0.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/com.github.scribejava/scribejava-core/6.9.0/ed761f450d8382f75787e8fee9ae52e7ec768747/scribejava-core-6.9.0.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.jmdns/jmdns/3.5.5/a156f2da3a29df61e2cab6f4d47c22f42114e2d/jmdns-3.5.5.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.jdom/jdom2/2.0.6/6f14738ec2e9dd0011e343717fa624a10f8aab64/jdom2-2.0.6.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/net.minidev/json-smart/2.3/7396407491352ce4fa30de92efb158adb76b5b/json-smart-2.3.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api/1.7.30/b5a4b6d16ab13e34a88fae84c35cd5d68cac922c/slf4j-api-1.7.30.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-databind/2.10.5.1/7ff756c3af1fe95cb3cddba9158fc3289ca06387/jackson-databind-2.10.5.1.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-annotations/2.10.5/33298de8da86f92f8ccd61ced214d3b16f8c531e/jackson-annotations-2.10.5.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/net.minidev/accessors-smart/1.2/c592b500269bfde36096641b01238a8350f8aa31/accessors-smart-1.2.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-core/2.10.5/db2ba27938de7f2d478a97d6abcdaa17cbbd3cea/jackson-core-2.10.5.jar:/Users/robertopompermaier/.gradle/caches/modules-2/files-2.1/org.ow2.asm/asm/5.0.4/da08b8cce7bbf903602a25a3a163ae252435795/asm-5.0.4.jar
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
| Working dir:     /Users/robertopompermaier/Workspaces/josp.com/com.robypomper.josp/envs/runnables/jod/JOD
+-----------------
```

---

## JCP

### ```jcp-client-status```

Print JCP connection state. ```JCP Client is connect.``` if current JOD instance
is connected to the JCP, ```JCP Client is NOT connect.``` if not.

### ```jcp-client-connect```

Connect internal JOD instance to the JCP.<br/>
At JOD Agent startup, the JCP connection is initialized only if the property
```jcp.connect``` from [JOD Agent configs](configs) is set to 'true'.

You can check the JCP connection state using the [```jcp-client-status``` command](#jcp-client-status).

### ```jcp-client-disconnect```

Disconnect internal JOD instance from JCP.<br/>
The internal instance can be disconnected and the re-connected with the [```jcp-client-connect``` command](#jcp-client-connect).

You can check the JCP connection state using the [```jcp-client-status``` command](#jcp-client-status).

---

## Communication

### ```comm-local-status```

Print the status of the JOD Local Server. Prints 'Local communication discovery
system is true' if server is running, '' otherwise.

### ```comm-local-start```

Start internal JOD Local Server.<br/>
At JOD Agent startup, the local server is started only if the property
```jod.comm.local.enabled``` from [JOD Agent configs](configs) is set to 'true'.

You can check the JOD Local Server state using the [```comm-local-status``` command](#comm-local-status).

### ```comm-local-stop```

Disconnect all Direct JOSP Services clients, and stop internal JOD Local Server.<br/>
The local server can be stopped and the re-started with the [```comm-local-start``` command](#comm-local-start).

You can check the JOD Local Server state using the [```comm-local-status``` command](#comm-local-status).

### ```comm-print-all-local-connections```

Print a list of all Direct JOSP Service clients are connected to JOD Local Server.

Example output:

```
LOCAL CONNECTIONS LIST
- localhost/127.0.0.1:54284      (srv: test-client-srv; usr: f89b2e21-d3cb-41d2-8349-ef2d5e247f37; status: connected; local: /127.0.0.1:1234)
```

### ```comm-cloud-status```

Print the status of the JOD Local Server. Prints 'Local communication discovery
system is true' if server is running, '' otherwise.

### ```comm-cloud-connect```

Connect JOD Gateway O2S Client to JCP if available.<br/>
At JOD Agent startup, the gateway client is initialized only if the property
```jod.comm.cloud.enabled``` from [JOD Agent configs](configs) is set to 'true'.

You can check the JOD Gateway O2S Client state using the [```comm-cloud-status``` command](#comm-cloud-status).

### ```comm-cloud-disconnect```

Disconnect the JOD Gateway O2S Client and then all Cloud JOSP Services clients.<br/>
The gateway client can be disconnected and the re-connected with the [```comm-cloud-connect``` command](#comm-cloud-connect).

You can check the JOD Gateway O2S Client state using the [```comm-cloud-status``` command](#comm-cloud-status).


---

## Permissions

### ```permissions-list```

Print all object's permissions.

Example output:

```
OBJECT'S PERMISSIONS LIST
  +--------------------+----------------------+----------------------+---------------------------+-------------------------+-------------------------+
  | ObjId              | SrvId                | UsrId                | Perm. and Connection Type | Updated At              | PermId                  |
  +--------------------+----------------------+----------------------+---------------------------+-------------------------+-------------------------+
  | AAQNQ-AENAM-XCQUM  | #All                 | #Owner               | CoOwner   , LocalAndCloud | 20211011-165956970      | NRJOPHLNLYMFHMEPFWQR    |
  | AAQNQ-AENAM-XCQUM  | #All                 | #All                 | CoOwner   , OnlyLocal     | 20211011-165956970      | PKRWBGVQRJOERQDRQHWI    |
  | AAQNQ-AENAM-XCQUM  | #All                 | #All                 | Actions   , OnlyLocal     | 20211011-171213402      | NPSIUIIFNOOLDQJSWGEW    |
  | AAQNQ-AENAM-XCQUM  | #All                 | #All                 | Status    , LocalAndCloud | 20211011-171216574      | XVIUCTUFWQLNUCGUHCXG    |
  +--------------------+----------------------+----------------------+---------------------------+-------------------------+-------------------------+
```

### ```permission-add (srvId, usrId, type, connection)```

Add a new permission build with given values.

* srvId: service's id or '#All' wildcard
* usrId: user's id to or '#All' or '#Owner' wildcards
* type: permission level granted ('None', 'Status', 'Actions' or 'CoOwner')
* connection: ```OnlyLocal``` to apply permission only to direct communication,
* ```LocalAndCloud``` to apply permission also to cloud communications.

Following example adds an Actions' permission for any user that use the 'x-y-z' JOSP service:
```
JOD> permission-add "x-y-z" "#All" Actions LocalAndCloud
```

### ```permission-update (permId, srvId, usrId, type, connection)```

Update given permission with given values.

* permId: the reference to the permission to update, you can get it with the [```permissions-list``` command](#permissions-list).
* srvId: service's id or '#All' wildcard
* usrId: user's id to or '#All' or '#Owner' wildcards
* type: permission level granted ('None', 'Status', 'Actions' or 'CoOwner')
* connection: ```OnlyLocal``` to apply permission only to direct communication,
* ```LocalAndCloud``` to apply permission also to cloud communications.

### ```permission-remove (permId)```

Delete given permission from object.

* permId: the reference to the permission to remove, you can get it with the [```permissions-list``` command](#permissions-list).

### ```permission-owner-set (usrId)```

Set given user's id as object's owner id.

### ```permission-owner-reset```

Set object's owner id equals '00000-00000-00000', the default value.

---

## Structure

### ```obj-tree```

Prints current object's structure.

Example output:

```
 Component             (Type)                      | Path
---------------------------------------------------+------------------------------------------------
+ root                 (JODRoot_Jackson)           |
| + Group that contains status (AbsJODContainer)   | Group that contains status
| | + State 0-50           (JODRangeState)         | Group that contains status>State 0-50
| | + State On/Off         (JODBooleanState)       | Group that contains status>State On/Off
| + Light example        (AbsJODContainer)         | Light example
| | + Consumption          (JODRangeState)         | Light example>Consumption
| | + Switch               (JODBooleanAction)      | Light example>Switch
...
```

### ```obj-component (compPath)```

Prints 'compPath''s Pillar info like his full path and workers instances.

Example output for ```obj-component "Light example > Switch"```:

```
--- Component START ---
# Switch
- Path #: Light example>Switch
- Worker: file://Switch
- Executor: shell://Switch
--- --------------- ---
```

### ```obj-component```

Prints 'root' Pillar info like his full path and workers instances.

Example output:

```
--- Component START ---
# root
Object to use in design testing
- Path #:
- Model: JOD Test Object 2.0
- Brand: John OS
- Descr: This structure provide an object with basic pillars and structured examples.
- SubComps #: 4
- SubComps: [Group that contains status, Light example, Color Light example, Group that contains actions]
--- --------------- ---
```

### ```obj-component (compPath)```

**Deprecated** Executes a null action on 'compPath' Pillar.

---

## Workers

### TODO

| abbrev | name                    | params                         |
|--------|-------------------------|--------------------------------|
| ea     | exec-activate           | ()                             |
| ed     | exec-deactivate         | ()                             |
| elpp   | exec-ls-puller-protos   | ()                             |
| ellp   | exec-ls-listener-protos | ()                             |
| elep   | exec-ls-executor-protos | ()                             |
| elp    | exec-ls-pullers         | ()                             |
| ell    | exec-ls-listeners       | ()                             |
| ele    | exec-ls-executors       | ()                             |
| eap    | exec-add-puller         | (name, name, proto, configStr) |
| erp    | exec-rm-puller          | (name)                         |
| eep    | exec-enable-puller      | (name)                         |
| edp    | exec-disable-puller     | (name)                         |
| erl    | exec-rm-listener        | (name)                         |
| eal    | exec-add-listener       | (type, name, proto, configStr) |
| eel    | exec-enable-listener    | (name)                         |
| edl    | exec-disable-listener   | (name)                         |
| eae    | exec-add-executor       | (type, name, proto, configStr) |
| ere    | exec-rm-executor        | (name)                         |
| eee    | exec-enable-executor    | (name)                         |
| ede    | exec-disable-executor   | (name)                         |
| eae    | exec-action-executor    | (name)                         |
