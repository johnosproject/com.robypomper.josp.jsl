# JOSP JOD files

## Configs

All configs file(s), by default, are contained in the ```configs/``` folder:

* jod.yml -> [JOD Agent Configs](configs)
* perms.jod -> [Permissions](permissions)
* struct.jod -> [Structure](structure)

---

## Cache

JOD Agent sync events and status histories to the JCP.

To allow objects register events and status histories also when are not connected
to the JCP, the JOD Agent use files contained in the ```cache/``` dir.

* event.jst: object's events cache state
* event.jbe: object's events data
* history.jst: statuses histories data
* history.jbe: statuses histories data

---

## Logs

JOD Agent logs are stored in the ```logs/``` dir.
File logs are configured as a [RollingFile](https://logging.apache.org/log4j/2.x/manual/appenders.html#RollingFileAppender) file.
That means, each time the log file reaches the size of 5MB, a new log file is
created and the old one is compressed and renamed.

---

## Libs

To work properly the JOD Agent requires all his dependencies (Java libraries)
available on running machine.<br/>
By default, libraries are stored in the ```libs/``` folder of the JOD Agent dir.
It can be customized using different Java ```-classpath``` param on executing the
JOD Agent's startup command.
