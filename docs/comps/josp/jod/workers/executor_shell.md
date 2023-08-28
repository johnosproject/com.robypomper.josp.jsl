# JOSP JOD Worker Executor Shell

When an [JOD Action](../specs/pillars.md#actions) must be executed, this executor executes
the ```cmd``` using the [JavaExecProcess::execCmdConcat(String cmd)](/src/jospCommons/java/com/robypomper/java/JavaExecProcess.java)
method.

Before execute the command, the ```cmd``` string is updated and all his placeholder
are replaced with current [Pillar](../specs/workers/placeholders.md#pillar), [State](../specs/workers/placeholders.md#state)
and [Action](../specs/workers/placeholders.md#action) properties.<br/>
After that execute the command.

---

## Firmware Configs

### ```cmd```

String containing the shell command to execute via [JavaExecProcess::execCmdConcat(String cmd)](/src/jospCommons/java/com/robypomper/java/JavaExecProcess.java)
method with 5 seconds as timeout. **It's mandatory.**

The ```cmd``` string can contain any available command on running machine and
his all params.<br/>
It also support piped command (```|```) for example ```echo "hello world" | sed 's/world/john/g'```.
The ```cmd``` string can concatenate multiple commands with ```&&```.

The concatenated commands are executed independently, so before executing each
cmd, this method wait for previous cmd termination.<br/>
Each cmd must be terminate before {@link #DEF_TIMEOUT}.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization and with [State Placeholder](../specs/workers/placeholders.md#state)
and [Action Placeholder](../specs/workers/placeholders.md#state) on executing action.

### ```redirect```

File path where write ```cmd``` output. Default value ''.

If this firmware Configs is set, it used as file path for write command output
as logs file.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization and with [State Placeholder](../specs/workers/placeholders.md#state)
and [Action Placeholder](../specs/workers/placeholders.md#state) on executing action.

---

## Examples

```json title="struct.jod: BoolenAction/Shell @ JOD PC Mac"
"Volume Mute (Mac)" : {
    "type": "BooleanAction",
    "puller" : "shell://cmd=osascript -e 'output muted of (get volume settings)';freq=1",
    "executor" : "shell://cmd=osascript -e 'set volume output muted %A_VAL_BOOL%'"
}
```

```json title="struct.jod: RangeAction/Shell @ JOD PC Mac"
"Volume" : {
    "type": "RangeAction",
    "puller" : "shell://cmd=osascript -e 'output volume of (get volume settings)';freq=1",
    "executor" : "shell://cmd=osascript -e 'set volume output volume %A_VAL%'",
    "min": "0",
    "max": "100",
    "step": "5"
},
```
