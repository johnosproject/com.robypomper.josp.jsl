# JOSP JOD Worker Executor Http

When an [action](../specs/pillars.md#actions) must be executed, this
executor performs an HTTP Request defined via [HTTP Request](#firmware-configs---http-request)
configs.

Before execute the request, the ```requestUrl``` and the ```requestBody``` strings
are updated and all their placeholder are replaced with current [Pillar](../specs/workers/placeholders.md#pillar),
[State](../specs/workers/placeholders.md#state) and [Action](../specs/workers/placeholders.md#action)
properties.<br/>
After that execute the HTTP Request with updated url and body.

---

## Firmware Configs - Executor

### ```requestBody```

String format used to generate the HTTP Request body. Default value ''.

This string can contain [Pillar's](../specs/workers/placeholders.md#pillar), [State's](../specs/workers/placeholders.md#state)
and [Action's Placeholder](../specs/workers/placeholders.md#action) that will be replaced
before executing the HTTP Request.

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization and with [State Placeholder](../specs/workers/placeholders.md#state)
and [Action Placeholder](../specs/workers/placeholders.md#state) on executing action.

---

## Firmware Configs - Http request

HTTP Requests are performed using the [DefaultHTTPClient](/src/jospCommons/java/com/robypomper/josp/clients/DefaultHTTPClient.java)
from the [JOSP Commons](/docs/comps/josp/commons/README.md)
library.

Following Firmware Configs allow you to customize the request that must be performed
on worker execution.<br/>
Those configs are defined and used by [HTTPInternal](/src/jospJOD/java/com/robypomper/josp/jod/executor/impls/http/HTTPInternal.java)
class.

### ```requestUrl```

The url used for the HTTP Request. **It's mandatory.**

This string is used to format the final url for the HTTP Request.<br/>
The ```requestUrl``` string is updated during worker initialization and then each
time the executor execute an action.

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

*This property is not used in the Executor HTTP worker execution.*

This property is updated with [Pillar's Placeholder](../specs/workers/placeholders.md#pillar)
on worker initialization.

---

## Examples

```json title="struct.jod: BoolenAction/Http @ JOD Philips Hue"
"Switch": {
    "type": "BooleanAction",
    "puller": "http://requestUrl='https://philips-hue-tres.local/api/Xex9YLRxERFf0TliilWFj3LkmjtCd2iGLmQSktYY/lights/1';requestIgnoreSSLHosts=true;",
    "executor": "http://requestUrl='http://philips-hue-tres.local/api/Xex9YLRxERFf0TliilWFj3LkmjtCd2iGLmQSktYY/lights/1/state';requestVerb=PUT;requestIgnoreSSLHosts=true;requestBody='{\"on\":%A_VAL%}'"
}
```

```json title="struct.jod: RangeAction/Http @ JOD Philips Hue"
"Brightness": {
    "type": "RangeAction",
    "puller": "http://requestUrl='https://philips-hue-tres.local/api/Xex9YLRxERFf0TliilWFj3LkmjtCd2iGLmQSktYY/lights/1';requestIgnoreSSLHosts=true;",
    "executor": "http://requestUrl='http://philips-hue-tres.local/api/Xex9YLRxERFf0TliilWFj3LkmjtCd2iGLmQSktYY/lights/1/state';requestVerb=PUT;requestIgnoreSSLHosts=true;requestBody='{\"bri\":%A_VAL_INT%}'",
    "min": "0",
    "max": "254",
    "step": "25"
}
```
