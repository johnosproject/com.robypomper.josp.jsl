# JOSP JOD Pillar Range

The range pillar can represent a value in a range. Range states can represent a
number (with floating point)

Range state value can be any number like integer or floating.
State Range's value is stored by JOD instance as ```double``` variable (a
double-precision 64-bit IEEE 754 floating point) and encoded to JOSP messages
with the [JavaFormatter::doubleToStr()](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) method.

An example for a **Pillar Range State** can be a temperature sensor that provide
values between ```-20``` and ```100``` (°C). This pillar sends a status update
message to JOSP Services every time sensor detect a different temperature.

Meanwhile, the example for a **Pillar Range Action** can be a dimmerable lamp
with a ```Bringness``` and ```Darkness``` actions. JOSP Service can send action
request to this pillar to set specific value, increase/decrease current value,
set min/max current value.

You can use this Pillar to expose hundreds of different features representing
them as Range states and actions like temperatures, speaker volumes, resource
usage...

**Range State can be configured to use any kind of Workers as [listener](../workers.md#listeners)
or [puller](../workers.md#pullers).** Because all Listeners and Pullers use the
AbsJODWorker::convertAndSetStatus(String) method to parse and update current
pillar status (independently to the pillar type).

On the other side, the **Range Actions can be configured to use as executor only
Workers that implements the ```JODRangeAction.JOSPRange.Executor``` interface**:
* [Shell](../../workers/executor_shell.md): on action request received, execute configured bash or powershell command
* [File](../../workers/executor_file.md): on action request received, write configured value to a file
* [Http](../../workers/executor_http.md): on action request received, query configured url

---

## Fields

The Boolean Pillar can be configured with following fields:

| Field Name  | Description                                                                         |
|-------------|-------------------------------------------------------------------------------------|
| ```Name```  | The name of the component. This filed is always set as JSON element's name.         |
| ```Descr``` | The description of the component.                                                   |
| ```Type```  | The component type. It must be ```BooleanState```.                                  |
| ```Min```   | The minimum value reachable.                                                        |
| ```Max```   | The maximum value reachable.                                                        |
| ```Step```  | The step value is used to increase/decrease current value by correspective actions. |

## Examples

```json title="struct.jod: RangeState/File @ JOD PC Windows"
"CPU" : {
    "type": "RangeState",
    "listener" : "file://path=status/cpu.txt",
    "min": "0",
    "max": "100"
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

```json title="struct.jod: RangeState/Http @ JOD Meteo Web"
"Temperature" : {
    "type": "RangeState",
    "puller" : "http://requestUrl='https://api.openweathermap.org/data/2.5/weather?q=${JOD_MWO_LOCATION}&units=metric&appid=03317c1f2de6827424efd170890ffd3c';formatType=JSON;formatPath='$.main.temp';formatPathType=JSONPATH;freq=600",
    "min": "-50",
    "max": "100"
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

```json title="struct.jod: RangeAction/Http @ JOD Philips Hue"
"Brightness": {
    "type": "RangeAction",
    "puller": "http://requestUrl='https://philips-hue-tres.local/api/Xex9YLRxERFf0TliilWFj3LkmjtCd2iGLmQSktYY/lights/1';formatType=JSON;formatPath='$.state.bri';formatPathType=JSONPATH;requestIgnoreSSLHosts=true;",
    "executor": "http://requestUrl='http://philips-hue-tres.local/api/Xex9YLRxERFf0TliilWFj3LkmjtCd2iGLmQSktYY/lights/1/state';requestVerb=PUT;formatType=JSON;formatPath='$.[0].success';formatPathType=JSONPATH;requestIgnoreSSLHosts=true;requestBody='{\"bri\":%A_VAL_INT%}'",
    "min": "0",
    "max": "254",
    "step": "25"
}
```