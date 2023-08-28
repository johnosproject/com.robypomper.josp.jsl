# JOSP JOD Worker Executor File

When an [JOD Action](../specs/pillars.md#actions) must be executed, this
executor writes Action Execution Formatted Content to ```path``` file.

Before write to the file, the ```format``` string is updated and all his placeholder
are replaced with current [Pillar](../specs/workers/placeholders.md#pillar), [State](../specs/workers/placeholders.md#state)
and [Action](../specs/workers/placeholders.md#action) properties.<br/>
After that write Action Execution Formatted Content to the file.

---

## Firmware Configs

### ```path```

File path where write Action Execution Formatted Content. **It's mandatory.**

The Action Execution Formatted Content is the result of replacing [State's](../specs/workers/placeholders.md#state)
and [Action's Placeholder](../specs/workers/placeholders.md#action) to the ```format```
Firmware Config.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization.

### ```format```

String format used to generate the content for the file. Default value '%A_VAL%'.

This string can contain [Pillar's](../specs/workers/placeholders.md#pillar), [State's](../specs/workers/placeholders.md#state)
and [Action's Placeholder](../specs/workers/placeholders.md#action) that will be replaced
before writing the Action Execution Formatted Content to the ```path``` file.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization and with [State Placeholder](../specs/workers/placeholders.md#state)
and [Action Placeholder](../specs/workers/placeholders.md#state) on executing action.

---

## Examples

```json title="struct.jod: BoolenAction/File"
"State On/Off" : {
    "type": "BooleanState",
    "listener" : "file://path=status/stateOnOff.txt",
    "executor" : "file://path=status/stateOnOff.txt"
}
```

```json title="struct.jod: RangeAction/File @ JOD Struct default file"
"Action 0-50" : {
    "type": "RangeAction",
    "listener" : "file://path=status/%COMP_NAME%_State.txt",
    "executor" : "file://path=status/%COMP_NAME%_State.txt",
    "min": "0",
    "max": "50",
    "step": "5"
}
```
