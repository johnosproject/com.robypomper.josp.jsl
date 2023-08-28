# JOSP JOD Worker Puller Shell

Each ```freq``` seconds, this puller executes the ```cmd``` using the
[JavaExecProcess::execCmd(String cmd)](/src/jospCommons/java/com/robypomper/java/JavaExecProcess.java)
method , then update the [JOD State](../specs/pillars.md#states).

Before execute the command, the ```cmd``` string is updated and all his placeholder
are replaced with current [Pillar](../specs/workers/placeholders.md#pillar) and
[State](../specs/workers/placeholders.md#state) properties.<br/>
Once executed the shell command, it takes his output and pass it as new Pillar's
state, independently to the Pillar's type.

---

## Firmware Configs

### ```cmd```

String containing the shell command to execute via [JavaExecProcess::execCmd(String cmd)](/src/jospCommons/java/com/robypomper/java/JavaExecProcess.java)
method. **It's mandatory.**

The ```cmd``` string can contain any available command on running machine and
his all params.<br/>
It also support piped command (```|```) for example ```echo "hello world" | sed 's/world/john/g'```.

Following redirects ar not supported:
* ```cmd >> out.txt```
* ```cmd > out.txt```
* ```cmd &> out.txt```
* ```cmd > out.txt 2> err.txt```

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization and with [State Placeholder](../specs/workers/placeholders.md#state)
on pulling state.

### ```freq```

Pulling frequency in seconds. By default '5'.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization.

---

## Examples

```json title="struct.jod: BoolenState/Shell"
"State On/Off" : {
    "type": "BooleanState",
    "puller" : "shell://cmd=osascript -e 'output muted of (get volume settings)';freq=1"
}
```

```json title="struct.jod: RangeState/Shell @ JOD PC Linux"
"CPU MPStat" : {
    "type": "RangeState",
    "puller" : "shell://cmd=scripts/hw/cpu_mpstat.sh;freq=30",
    "min": "0",
    "max": "100"
}
```