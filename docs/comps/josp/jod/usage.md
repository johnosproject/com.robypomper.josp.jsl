# JOSP JOD Usage

The JOD Agent is provided as Java application, so it must be run as a ```java``` command.

_The JOD Agent is not designed to be run directly.

It's designed to be run as part of the JOD Distribution (See the
[JOD Distribution TEMPLATE](/docs/comps/tools.md)), so use
[JOD Dist Cmds to run corresponding JOD Agent](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.template/src/master/docs/dists/start.md)._

Fastest way **to get a JOD Agent working dir is to download latest JOD Agent Dir**
from [com.robypomper.josp > Downloads @ Bitbucket](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/downloads/) repository.<br/>
This compressed dir contains not only the JOD Agent, but also his dependencies
and default config files to run it in minutes.

Another way, is to prepare your JOD Agent Dir manually.
It requires downloading JAR files from the Maven Repository and edit your configs
files.<br/>
You can download following two files [jospJOD-2.2.2.jar](https://repo1.maven.org/maven2/com/robypomper/josp/jospJOD/2.2.2/jospJOD-2.2.2.jar)
and [jospJOD-2.2.2-deps.jar](https://repo1.maven.org/maven2/com/robypomper/josp/jospJOD/2.2.2/jospJOD-2.2.2-deps.jar)
in an empty directory.
Then extract the content of ```jospJOD-2.2.2-deps.jar``` file in the ```/libs```
sub-dir, it's a zipped file, so you can use your favourite tool to extract it.<br/>
Now you must create JOD Agent's configs files like the ```configs/jod.yml``` for
generic [JOD Agent Configs](specs/configs) and the ```configs/struct.jod``` file
[object's structure](specs/structure).
[Other files](specs/files) like the ```log4j2.xml``` for logging configuration
are optionals.

Once your prepared your JOD Agent's dir, **open a terminal session and cd to
prepared dir**.

Now you can decide to run the JOD Agent as a [background process](#daemon) or as
a [foreground command](#command).

## Daemon

The JOD Agent is designed to be run as a background process. Mostly started from
the operating system's init system. <br/>
You can run it manually with following command, or you can include the same command
in the init system's scripts files:

```
$ cd {JOD_AGENT_DIR}
$ java -cp jospJOD-2.2.2.jar com.robypomper.josp.jod.JODDaemon >logs/console.log 2>&1 &
```

**This command return immediately, but it continues running in background.** <br/>
By default, all logs are printed on the ```logs/jospJOD.log``` file (in the Java
command we use the ```>logs/console.log 2>&1``` redirect to avoid printing console
logs on current console). <br/>
You can alter this behaviour adding a custom ```log4j2.xml``` file and include it
in the java command with the param ```-Dlog4j.configurationFile=log4j2.xml```.

Logs can be read with following command (remove the ```-f``` param to quit after
prints last log):

```
$ tail -f logs/jospJOD.log
  < (Ctrl+C to exit)
```

## Command

When executed **as a foreground command, the JOD Agent, after his startup, show
the [JOD Shell](specs/shell)**.

You can use shell's command to interact with current JOD Agent and test your configs. <br/>
Type ```exit``` to the JOD Shell to shut down the JOD Agent and exit from the shell.

```
$ java -cp jospJOD-2.2.2.jar com.robypomper.josp.jod.JODShell
```

By default, all logs are displayed on console (on the JOD Shell's prompt) and on
the ```logs/jospJOD.log``` file. <br/>
You can alter this behaviour adding a custom ```log4j2.xml``` file and include
it in the java command with the param ```-Dlog4j.configurationFile=log4j2.xml```.
