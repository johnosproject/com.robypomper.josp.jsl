# JOSP JOD Worker Listener File

This listener monitors ```path``` file and read his content on any file change,
then update the [JOD State](../specs/pillars.md#states).

When it detects a file change, it read the file content and pass it as new Pillar's
state, independently to the Pillar's type.

---

## Firmware Configs

### ```path```

File path to monitoring. **It's mandatory.**

During Listener File initialization, it checks if ```path```'s parent directory
exist. If not then the worker create it.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization.

---

## Examples

```json title="struct.jod: BoolenState/File"
"State On/Off" : {
    "type": "BooleanState",
    "listener" : "file://path=status/stateOnOff.txt"
}
```

```json title="struct.jod: RangeState/File @ JOD PC Windows"
"CPU" : {
    "type": "RangeState",
    "listener" : "file://path=status/cpu.txt",
    "min": "0",
    "max": "100"
}
```
