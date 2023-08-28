# JOSP JOD Pillar Boolean

The boolean pillar can represent any state or action that can be included 2
values/actions.

The first example for a **Pillar Boolean State** can be a switch with his two
values: ```On``` or ```Off```. This pillar sends a status update message to
JOSP Services every time the pillar's status change his value.

Meanwhile, the example for a **Pillar Boolean Action** can be a controllable lamp
with a ```Switch On``` and ```Switch Off``` actions. JOSP Service can send action
request to this pillar to switch on/off the light.

You can use this Pillar to expose hundreds of different features representing
them as Boolean states and actions like:

* [State] On/Off a ventilator, a washing machine
* [Action] Switch On/Switch Off a light, an electric cooker
* [State] Enabled/Disabled
* [Action] Enable/Disable
* [State] Operational/Stopped an industrial machinery
* [Action] Start/Stop a washing machine program, an engine of a vehicle
* [State] Opened/Closed a Door, Gate, Window, Box
* [Action] Open/Close a motorized Door, Gate, Window, Box
* [State] Full/Empty a bath tub, a bin
* [Action] Fill/Empty a bath tube, a tank
* [State] High/Low
* [Action] Raise/Lower a shutter, a working desk
* [State] Free/Busy a chair, a room
* [Action] Free/Occupy
* [State] Online/Offline
* [Action] Connect/Disconnect

Or even features more customized like: Alarm/Ok, Problem/Ok...

**Boolean State can be configured to use any kind of Workers as [listener](../workers.md#listeners)
or [puller](../workers.md#pullers).** Because all Listeners and Pullers use the
AbsJODWorker::convertAndSetStatus(String) method to parse and update current
pillar status (independently to the pillar type).

On the other side, the **Boolean Actions can be configured to use as
[executor](../workers.md#executors) only Workers that implements the
```JODBooleanAction.JOSPBoolean.Executor``` interface**:
* [Shell](../../workers/executor_shell.md): on action request received, execute configured bash or powershell command
* [File](../../workers/executor_file.md): on action request received, write configured value to a file
* [Http](../../workers/executor_http.md): on action request received, query configured url

---

### Fields

The Range Pillar can be configured with following fields:

| Field Name  | Description                                                                 |
|-------------|-----------------------------------------------------------------------------|
| ```Name```  | The name of the component. This filed is always set as JSON element's name. |
| ```Descr``` | The description of the component.                                           |
| ```Type```  | The component type. It must be ```BooleanState```.                          |

---

## Examples:

```json title="struct.jod: BoolenState/File"
"State On/Off" : {
    "type": "BooleanState",
    "listener" : "file://path=status/stateOnOff.txt"
}
```

```json title="struct.jod: BoolenState/Shell"
"State On/Off" : {
    "type": "BooleanState",
    "puller" : "shell://cmd=osascript -e 'output muted of (get volume settings)';freq=1"
}
```

```json title="struct.jod: BoolenState/Http @ JOD Philips Hue"
"Online": {
    "type": "BooleanState",
    "puller": "http://requestUrl='https://philips-hue-tres.local/api/Xex9YLRxERFf0TliilWFj3LkmjtCd2iGLmQSktYY/lights/1';formatType=JSON;formatPath='$.state.reachable';formatPathType=JSONPATH;requestIgnoreSSLHosts=true;"
}
```



```json title="struct.jod: BooleanAction/File"
"State On/Off" : {
    "type": "BooleanState",
    "listener" : "file://path=status/stateOnOff.txt",
    "executor" : "file://path=status/stateOnOff.txt"
}
```

```json title="struct.jod: BoolenAction/Shell @ JOD PC Mac"
"Volume Mute (Mac)" : {
    "type": "BooleanAction",
    "puller" : "shell://cmd=osascript -e 'output muted of (get volume settings)';freq=1",
    "executor" : "shell://cmd=osascript -e 'set volume output muted %A_VAL_BOOL%'"
}
```

```json title="struct.jod: BooleanAction/Http @ JOD Philips Hue"
"Switch": {
    "type": "BooleanAction",
    "puller": "http://requestUrl='https://philips-hue-tres.local/api/Xex9YLRxERFf0TliilWFj3LkmjtCd2iGLmQSktYY/lights/1';formatType=JSON;formatPath='$.state.on';formatPathType=JSONPATH;requestIgnoreSSLHosts=true;",
    "executor": "http://requestUrl='http://philips-hue-tres.local/api/Xex9YLRxERFf0TliilWFj3LkmjtCd2iGLmQSktYY/lights/1/state';requestVerb=PUT;formatType=JSON;formatPath='$.[0].success';formatPathType=JSONPATH;requestIgnoreSSLHosts=true;requestBody='{\"on\":%A_VAL%}'"
}
```













"State On/Off" : {
    "type": "BooleanState",
    "listener" : "tstLAdv://sleep=1000;frequency=60;"
}

"Online": {
  "type": "BooleanState",
  "puller": "http://requestUrl='https://philips-hue-tres.local/api/Xex9YLRxERFf0TliilWFj3LkmjtCd2iGLmQSktYY/lights/1';formatType=JSON;formatPath='$.state.reachable';formatPathType=JSONPATH;requestIgnoreSSLHosts=true;"
},



"Volume Mute" : {
    "type": "BooleanAction",
    "puller" : "shell://cmd=scripts/hw/volume_mute.sh;freq=1",
    "executor" : "shell://cmd=scripts/hw/volume.sh %A_VAL_BOOL%"
},

"Volume Mute" : {
    "type": "BooleanAction",
    "puller" : "shell://cmd=osascript -e 'output muted of (get volume settings)';freq=1",
    "executor" : "shell://cmd=osascript -e 'set volume output muted %A_VAL_BOOL%'"
},