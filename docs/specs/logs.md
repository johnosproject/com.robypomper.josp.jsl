# JOSP Service Library -Specs: Logs

This library is based on the Simple Logging Facade 4 Java [SLF4J](https://www.slf4j.org/)
and, it uses (for the JSL's Shell) the [Apache Log4J](https://logging.apache.org/log4j/2.x/)
logging system. That means, when you include the JSL into your own software, you
can configure and print log messages just including a SLF4J implementation,
like the `org.apache.logging.log4j:log4j-slf4j2-impl` and set up relative configs
file (`$JSL_DIR/configs/log4j2.xml` for org.apache.logging.log4j).

Here how to include the `log4j-slf4j2-impl` as Gradle dependency into your
`build.gradle` file:

```groovy
dependencies {
    // Add log4j-slf4j2-impl as Gradle dependency
    runtime "org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0"
}
```

**Run the JSL's Shell:**
When you run the JSL Shell, you can configure the logging messages using
the `$JOD_DIR/configs/log4j2.xml` file.

By default, JSL Shell logs are stored in the ```logs/``` dir.
File logs are configured as a [RollingFile](https://logging.apache.org/log4j/2.x/manual/appenders.html#RollingFileAppender) file.
That means, each time the log file reaches the size of 5MB, a new log file is
created and the old one is compressed and renamed.


## Default `log4j2.xml`

Default `log4j2.xml` files are available in to the `/arc/main/configs/log4j2`
directory. Actually, two default `log4j2.xml` files are available, used for
development and release phases:

* [`log4j2_dev.xml`](/src/main/configs/log4j2/log4j2_dev.xml)
* [`log4j2_release.xml`](/src/main/configs/log4j2/log4j2_release.xml)

Both files are configured to print log messages to the console and to the file.
Also for both files, the log messages are grouped in 3 packages: artifact, josp
libraries and all the others.<br/>
These files differ in the log levels:

| Phase   | Package  | Console | File  |
|---------|----------|---------|-------|
| Develop | Artifact | DEBUG   | ALL   |
| Develop | JOSP     | DEBUG   | ALL   |
| Develop | Others   | WARN    | INFO  |
| Release | Artifact | INFO    | DEBUG |
| Release | JOSP     | FATAL   | INFO  |
| Release | Others   | FATAL   | WARN  |


## Customize `log4j2.xml`

The JSL's log messages are grouped in packages, so you can configure the
log messages for each package separately. The JSL's log messages include
also the JOSP Commons library's log messages. Here the full list of the JOD Agent's
loggers:

**From JSL:**
* `com.robypomper.jsl.srvinfo`
* `com.robypomper.jsl.user`
* `com.robypomper.jsl.comm`
* `com.robypomper.jsl.objs`
* `com.robypomper.jsl.objs.remote`
* `com.robypomper.jsl.objs.structure`
* `com.robypomper.jsl.objs.history`
* `com.robypomper.jsl.shell`
* `com.robypomper.jsl.admin`

**From JOSP Commons:**
* `com.robypomper.comm`
* `com.robypomper.discovery`
* `com.robypomper.josp`
* `com.robypomper.java`
* `com.robypomper.settings`
* `javax.jmdns`

Here an example for the `log4j2.xml` config file:

```xml
<Configuration>
    <Loggers>
        
        <Logger name="com.robypomper.josp.jsl" additivity="false">
            <AppenderRef ref="consoleInfo_fileDebug"/>
        </Logger>
        
        <Logger name="com.robypomper.josp.jsl.user" additivity="false">
            <AppenderRef ref="consoleDebug_fileDebug"/>
        </Logger>

        <Logger name="com.robypomper" additivity="false">
            <AppenderRef ref="consoleNone_fileWarning"/>
        </Logger>
        
        <Logger name="javax.jmdns" additivity="false">
            <AppenderRef ref="consoleNone_fileWarning"/>
        </Logger>
        
    </Loggers>
</Configuration>
```
