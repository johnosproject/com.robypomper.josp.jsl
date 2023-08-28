# JOSP JOD Worker Puller UnixShell

Deprecated class for [Puller Shell](puller_shell) class.

This class is maintained as retro-compatibility with JOSP 2.2.0 version.

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