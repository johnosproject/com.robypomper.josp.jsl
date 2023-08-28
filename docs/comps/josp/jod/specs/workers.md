# JOSP JOD Workers

Workers are the components that act as **a bridge between the JOD Agent's Pillars
and his firmware**.<br/>
They translate Pillar's Actions requests to firmware calls, and checks for firmware
updates to send status update messages to JOSP Services.

Because JOSP Objects can represent almost any object, the **JOD Agent must be
able to interact with different type of firmware**.<br/>
The JOD Agent provide some worker implementations that allow you handle common
firmware integrations like shell command, HTTP requests... For a full list of
available workers' implementation see next chapters.

To use a worker in a state or an action, requires 2 configuration steps.
First add the Worker's implementation to the [JOD Agent configs](configs.md).
Second configure a pillar to use that worker implementation.

**Pillar's configuration provide 3 special properties to configure worker instances**:

* ```listener```: define firmware calls for listener's states and actions
* ```puller```: define firmware calls for puller's states and actions
* ```executor```: define firmware calls used by actions when receive a command request

Each pillar's config must include at least one of the two ```listener``` or
```puller``` properties. Only Action pillars must set also ```executor``` property.<br/>
Those properties accept a string formatted with following pattern:

```
{FirmwareProto}://{FirmwareConfigs}
```

Where the ```FirmwareProto``` must be one of the FirmwareProtocols registered in
the [JOD Agent configs](configs.md) file with the ```jod.executor_mngr.pullers|listeners|executors```
properties.<br/>
The ```FirmwareConfigs``` is a string containing a list of ```key=value```
separated by ';'. Because each worker requires different configs, check worker's
documentation for Firmware Configs.

Any Firmware Configs can contain placeholder that are replaced with pillar's properties.
**You can set up dynamic configuration using those placeholders.**<br/>
For example you can set a Firmware Config for a file path using the pillar's name.<br/>
Each worker's documentation specify when each Firmware Configs is updated.

More info about Firmware Configs Placeholders on [JOD Workers Placeholder](workers/placeholders.md) page.

**Example:**

In the following example we defined a 'Temperature' state, that pull every
```freq``` ('600') seconds the ```requestUrl```() url and retrieve the temperature
value. The ```puller``` property is set to use the 'http' firmware protocol and
pass him the configs string (everything after the '://' separator). In this case
the 'http' firmware require the ```requestUrl``` and ```freq``` params among
others. More details on firmware protocols params on the next section.

```json title="struct.json: RangeState puller 'http' example"
    // ...
    "Temperature" : {
        "type": "RangeState",
        "puller" : "http://requestUrl='https://api.openweathermap.org/data/2.5/weather?q=rome&units=metric&appid=03317c1f2de6827424efd170890ffd3c';formatType=JSON;formatPath='$.main.temp';formatPathType=JSONPATH;freq=600",
        "min": "-50",
        "max": "100"
    },
    // ...
```


## Pullers
  * [Shell](../workers/puller_shell.md): On pulling,execute bash or powershell commands and use their output as state's value
  * [Http](../workers/puller_http.md): On pulling, query configured url and parse the response as state's value

## Listeners
  * [File](../workers/listener_file.md): On startup, start a watchdog service that listen for configured file changes; when the file is updated use his content as state's value

## Executors
  * [Shell](../workers/executor_shell.md): on action request received, execute configured bash or powershell command
  * [File](../workers/executor_file.md): on action request received, write configured value to a file
  * [Http](../workers/executor_http.md): on action request received, query configured url
