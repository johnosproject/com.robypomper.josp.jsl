# JOSP JOD Worker Puller Http

Each ```freq``` seconds, this puller performs the HTTP Request defined via
[HTTP Request](#firmware-configs---http-request) configs , then update the
[JOD State](../specs/pillars.md#states).

Before execute the request, the ```requestUrl``` string is updated and all his
placeholder are replaced with current [Pillar](../specs/workers/placeholders.md#pillar) and
[State](../specs/workers/placeholders.md#state) properties. Once executed the HTTP Request,
his response body is [formatted](#firmware-configs---formatter) and [evaluated](#firmware-configs---evaluator).
Finally, the evaluated HTTP Response's body is passed as new Pillar's state,
independently to the Pillar's type.

---

## Firmware Configs - Puller

### ```freq```

Pulling frequency in seconds. By default '5'.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization.


### ```cache_timeout```

Cache timeout in seconds. By default '30'.

Once an url is required by any instance of Puller Http worker, his response is
retrained in a cache memory for ```cache_timeout``` seconds.<br/>
For all time an url response is cached, all other request (from any HTTP Puller
instance) to the same url receive cached response.<br/>
That prevent HTTP server overload, especially when multiple HTTP Puller workers
get their value from the same web page or API method.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization.

---

## Firmware Configs - HTTP Request

HTTP Requests are performed using the [DefaultHTTPClient](/src/jospCommons/java/com/robypomper/josp/clients/DefaultHTTPClient.java)
from the [JOSP Commons](/docs/comps/josp/commons/README.md)
library.

Following Firmware Configs allow you to customize the request that must be performed
on worker execution.<br/>
Those configs are defined and used by [HTTPInternal](/src/jospJOD/java/com/robypomper/josp/jod/executor/impls/http/HTTPInternal.java) class.

### ```requestUrl```

The url used for the HTTP Request. **It's mandatory.**

This string is used to format the final url for the HTTP Request.<br/>
The ```requestUrl``` string is updated during worker initialization and then each
time the puller pulls for pillar state.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization and with [State Placeholder](../specs/workers/placeholders.md#state)
on pulling state.

### ```requestVerb```

The HTTP method to use for the HTTP Request. Default 'GET'.

This string can be one of the following values:

* 'GET'
* 'POST'
* 'PUT'
* 'DELETE'
* 'HEAD'
* 'OPTIONS'
* 'TRACE'
* 'PATCH'

Those values are coming from the [Scribe Java library](https://github.com/scribejava/scribejava).

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization.

### ```requestTimeOut```

The HTTP Request's timeout in seconds. Default '30'.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization.

### ```requestIgnoreSSLHosts```

Set this string to 'True' to ignore "SSL: Invalid Hostname" error. Default 'false'.

If the HTTP Server that receive the HTTP Request is set to use the SSL encryption
but his certificate or hostname are not valid.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization.

### ```availabilityRetrySeconds```

When the HTTP Server is not reachable, this property define how many seconds wait
before retry contacting the server. Default '10'.

*This property is not used in the Puller HTTP worker execution.*

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization.

---

## Firmware Configs - Formatter

Once an HTTP Request receive a response, the response's body is parsed and
formatted depending on those Firmware Configs.

That means, the HTTP Responses bodies are parsed using the ```formatType``` format.
Then can be extracted part of the response, using the ```formatPathType``` and
```formatPath``` configs. Finally, only the extracted string is used as Pillar's
state.

To avoid any response alteration, you can use the ```TEXT``` value in the ```formatType```
config. That will thread the HTTP Response Body as raw text and, all contents from
the response body are used as Pillar's state.

Those configs are defined and used by [FormatterInternal](/src/jospJOD/java/com/robypomper/josp/jod/executor/impls/http/FormatterInternal.java) class.

### ```formatType```

HTTP Response's body format. Default 'HTML'.

Please set this FirmwareConfigs according to the expected response type.<br/>
You can choose one of the following values:

* ```TEXT```: raw text response, this format does not alter the response body.
* ```HTML```: for HTML responses that can be queried with XPath expression or with a TagName (Not Yet Implemented).
* ```XML```: for XML responses (Not Yet Implemented).
* ```JSON```: for JSON responses that can be queried with JSONPath expression.
* ```YML```: for YML responses (Not Yet Implemented).

Those values are coming from the [FormatterInternal::FormatType](/src/jospJOD/java/com/robypomper/josp/jod/executor/impls/http/FormatterInternal.java) enum.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization.

### ```formatPath```

Path used to query the HTTP Response, this value depends on ```formatPathType```
Firmware Config. Default '/'.

Please set this FirmwareConfigs according to the expected response type.<br/>
Depending on the ```formatPathType``` value you can use different path syntax:

* ```XPATH```: an expression format to identify nodes in XML documents ([W3School tutorial](https://www.w3schools.com/xml/xpath_intro.asp), [XPather: Online XPath Tester](http://xpather.com/))
* ```TAG_NAME```: just write the tag name, then his content will be used as formatted response
* ```JSONPATH```: an expression format to identify nodes in JSON documents ([Jayway JsonPath](https://github.com/json-path/JsonPath), [JsonPath.com: Online JsonPath Tester](https://jsonpath.com/))

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization.

### ```formatPathType```

HTTP Response's body format type. Default ''.

Please set this FirmwareConfigs according to the expected response type.<br/>
You can choose one of the following values:

* ```XPATH```: to query HTTP Responses with HTML and XML (Not Yet Implemented) bodies.
* ```TAG_NAME```: to query HTTP Responses with HTML and XML bodies (Not Yet Implemented).
* ```JSONPATH```: to query HTTP Responses with JSON bodies.

Those values are coming from the [FormatterInternal](/src/jospJOD/java/com/robypomper/josp/jod/executor/impls/http/FormatterInternal.java) class.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization.

---

## Firmware Configs - Evaluator

If you are not yet satisfy from the result after the HTTP Response body format,
you can continue customizing it within a configurable JavaScript script.

For example, if after response format, it still contains unwanted chars; or also
if you configured the worker for handle a raw response  (```formatType=TEXT```).
Then you need to edit the obtained response before passing it as new Pillar's
state value.

To edit obtained result, after the response's body format, you must use the
```eval``` Firmware Config.<br/>
Default ```eval``` value, or an empty string, prevents any response alteration.

Those configs are defined and used by [EvaluatorInternal](/src/jospJOD/java/com/robypomper/josp/jod/executor/impls/http/EvaluatorInternal.java) class.

### ```eval```

JavaScript code to evaluate HTTP Response body after formatting it. Default
'{httpResult}'.

This config accepts any JavaScript script and response the evaluation function
returns his output.

Custom JavaScript can use the ```httpResult``` string that contains the HTTP Response
body after formatting.<br/>
After alter this string, the script must print the desired result.

Script are executed as [Java ScriptEngine](https://docs.oracle.com/javase/8/docs/api/javax/script/ScriptEngine.html).<br/>
More details on how to write JavaScript for the Java ScriptEngine at [Oracle: Java Scripting Programmer's Guide](https://docs.oracle.com/javase/7/docs/technotes/guides/scripting/programmer_guide/).

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization and with [State Placeholder](../specs/workers/placeholders.md#state)
on pulling state.

---

## Examples

```json title="struct.jod: BoolenState/Http @ JOD Philips Hue"
"Online": {
    "type": "BooleanState",
    "puller": "http://requestUrl='https://philips-hue-tres.local/api/Xex9YLRxERFf0TliilWFj3LkmjtCd2iGLmQSktYY/lights/1';formatType=JSON;formatPath='$.state.reachable';formatPathType=JSONPATH;requestIgnoreSSLHosts=true;"
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