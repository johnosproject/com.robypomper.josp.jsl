# JOSP JOD Workers Placeholders

With Worker Configs Placeholder, **Makers can setups dynamic configs for their
[Pillar's](../pillars) firmware**.

For example, using the worker's placeholders, you can set up a File Listener worker
that use the Pillar's name (```%COMP_NAME%```) in the monitored file path.
Or even you can configure an HTTP Puller to use the pillar's path (```%COMP_PATH```).

Worker **Placeholders are substituted to Firmware Configs on worker initialization**.
When worker parses the Firmware Configs String, from the pillar's definition in
the [JOD Agent's structure](../structure).
This string is defined by Maker in the object's configuration.<br/>
Firmware Configs are parsed using the [AbsJODWorker::parseConfig{TYPE}(Map,String)](/src/jospJOD/java/com/robypomper/josp/jod/executor/AbsJODWorker.java)
methods. Those methods substitute string config's value placeholders with
[Pillar](#pillar) properties, then convert resulting value to required ```{TYPE}```. <br/>
So any Firmware Configs is updated with Component properties.

**Placeholders can be replaced also when the worker must do his work** (listen
for a state, pull a state or execute an action).<br/>
Depending on worker implementation, it can update some Firmware Config with
[State](#state) or  [Action](#action) (only on action execution) properties. <br/>
Check the worker's documentation for placeholder replacement info.

**NB!**: At the JOD Version 2.2.3 this class was updated and also some placeholders.
Please see the JavaDoc page related to the [Substitutions](https://www.javadoc.io/static/com.robypomper.josp/jospJOD/2.2.3/com/robypomper/josp/jod/executor/Substitutions.html)
class for more info.

## Pillar

Those placeholders are replaced with properties from the Pillar that use current
Worker.

Those placeholders are always replaced in almost all Firmware Configs on worker
initialization.

| Placeholder              | Replaced with                                        |
|--------------------------|------------------------------------------------------|
| ```%COMP_NAME%```        | Pillar name                                          |
| ```%COMP_TYPE%```        | Pillar type ('BooleanState', 'RangeAction'...)       |
| ```%COMP_PATH%```        | Pillar full path                                     |
| ```%COMP_PARENT_NAME%``` | Pillar's parent name, 'N/A' for root component.      |
| ```%COMP_PARENT_PATH%``` | Pillar's parent full path, 'N/A' for root component. |

## State

States Placeholders allow using Pillar's value in puller and executors workers.

A puller can use current value to query the updated one.
Or an executor should now current value before update the new one.

Those placeholders are replaced only in certainly Firmware Configs during worker
job execution (pulling or execute).<br/>
Check the worker's Firmware Configs details for placeholder replacement info.

Only for **Pillar of Boolean** type:

| Placeholder        | Replaced with                                                                                                                                                                          |
|--------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ```%S_VAL%```      | Pillar value that correspond to 'TRUE' or 'FALSE' strings, see the [JavaFormatter.booleanToString(boolean)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.    |
| ```%S_VAL_BOOL%``` | Pillar value that correspond to 'TRUE' or 'FALSE' strings, see the [JavaFormatter.booleanToString(boolean)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.    |
| ```%S_VAL_BIN%```  | Pillar value that correspond to '1' (true) or '0' (false), see the [JavaFormatter.booleanToStringBin(boolean)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function. |

Only for **Pillar of Range** type:

| Placeholder         | Replaced with                                                                                                                                                                      |
|---------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ```%S_VAL%```       | Pillar value that correspond to a valid number rendered with the [JavaFormatter.doubleToStr(double)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.       |
| ```%S_VAL_COMMA%``` | Pillar value that correspond to a valid number rendered with the [JavaFormatter.doubleToStr_Point(double)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function. |
| ```%S_VAL_POINT%``` | Pillar value that correspond to a valid number rendered with the [JavaFormatter.doubleToStr_Comma(double)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function. |

## Action

Action Placeholders allow using Action Request params in executors workers.

Action Request params include info such as the (new) value to set and the (old)
current Pillar's state.
Moreover, you can find other info about Action Request like the JOSP Service's id
that send the request and the User's id that was using the service.

Those placeholders are replaced only in certainly Firmware Configs during executor
job execution (execute).<br/>
Check the worker's Firmware Configs details for placeholder replacement info.

For **all Actions Requests**:

| Placeholder      | Replaced with                                         |
|------------------|-------------------------------------------------------|
| ```%A_SRV_ID%``` | JOSP Service's id that required the action execution. |
| ```%A_USR_ID%``` | JOSP User's id that required the action execution.    |

Only for **Actions Requests on Pillar of Boolean Action** type:

| Placeholder            | Replaced with                                                                                                                                                                                                                                  |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ```%A_VAL%```          | Value to set, requested by Action Execution. This value can be one of the two 'TRUE' or 'FALSE' strings, see the [JavaFormatter.booleanToString(boolean)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.              |
| ```%A_VAL_BOOL%```     | Value to set, requested by Action Execution. This value can be one of the two 'TRUE' or 'FALSE' strings, see the [JavaFormatter.booleanToString(boolean)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.              |
| ```%A_VAL_BIN%```      | Value to set, requested by Action Execution. This value can be one of the two '1' (true) or '0' (false) strings, see the [JavaFormatter.booleanToStringBin(boolean)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.   |
| ```%A_VAL_OLD%```      | Current pillar value, before Action Execution. This value can be one of the two 'TRUE' or 'FALSE' strings, see the [JavaFormatter.booleanToString(boolean)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.            |
| ```%A_VAL_OLD_BOOL%``` | Current pillar value, before Action Execution. This value can be one of the two 'TRUE' or 'FALSE' strings, see the [JavaFormatter.booleanToString(boolean)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.            |
| ```%A_VAL_OLD_BIN%```  | Current pillar value, before Action Execution. This value can be one of the two '1' (true) or '0' (false) strings, see the [JavaFormatter.booleanToStringBin(boolean)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function. |

For **Actions Requests on Pillar of Range Action** type:

| Placeholder             | Replaced with                                                                                                                                                                                                           |
|-------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ```%A_VAL%```           | Value to set, requested by Action Execution. This value can be a valid number rendered with the [JavaFormatter.doubleToStr(double)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.             |
| ```%A_VAL_POINT%```     | Value to set, requested by Action Execution. This value can be a valid number rendered with the [JavaFormatter.doubleToStr(double)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.             |
| ```%A_VAL_COMMA%```     | Value to set, requested by Action Execution. This value can be a valid number rendered with the [JavaFormatter.doubleToStr(double)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.             |
| ```%A_VAL_INT%```       | Value to set, requested by Action Execution. This value can be a valid number rendered with the [JavaFormatter.doubleToStr_Truncated(double)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.   |
| ```%A_VAL_OLD%```       | Current pillar value, before Action Execution. This value can be a valid number rendered with the [JavaFormatter.doubleToStr(double)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.           |
| ```%A_VAL_OLD_POINT%``` | Current pillar value, before Action Execution. This value can be a valid number rendered with the [JavaFormatter.doubleToStr(double)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.           |
| ```%A_VAL_OLD_COMMA%``` | Current pillar value, before  Action Execution. This value can be a valid number rendered with the [JavaFormatter.doubleToStr(double)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function.          |
| ```%A_VAL_OLD_INT%```   | Current pillar value, before Action Execution. This value can be a valid number rendered with the [JavaFormatter.doubleToStr_Truncated(double)](/src/jospCommons/java/com/robypomper/java/JavaFormatter.java) function. |
