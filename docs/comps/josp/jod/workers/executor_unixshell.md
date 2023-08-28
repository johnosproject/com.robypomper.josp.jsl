# JOSP JOD Worker Executor UnixShell

Deprecated class for [Executor Shell](executor_shell) class.

This class is maintained as retro-compatibility with JOSP 2.2.0 version.

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
