# JOSP JOD Pillars

Pillars are the **base config element for each feature exposed by a JOSP Object
to the JOSP Services**. They expose, in a standard way, to JOSP Services a complex
firmware calls. That means the JOSP Services can understand and interact with
object's feature independently to the object's firmware.

Pillars can interact with object's firmware via Workers. Workers are the components
that act as a bridge between the JOD Agent and his firmware.<br/>
Different Workers implementations are provided within the JOD Agent or extra can
be added. Provided Workers implementation allow interacting most common firmwares
types and external systems.<br/>
Check out the [Worker](workers.md) chapter for more info and how to choose the
right workers for your object.

On JOSP side, Pillars expose all details and descriptions required to JOSP Services
to interact with it. On the other side, they configure the object's firmware to
interact with hardware, other software, make web request or anything else you require.

Because of that, Pillars configs can be split in 2 groups:

* description pillar's configs: configs that describe the pillar to JOSP Services, depending on pillar type different properties become available.
* firmware pillar's configs: firmware [workers] configs, includes the ```listener```, ```puller``` and ```executor``` properties.

For detailed Pillar's configuration please check the Pillar Type documentation
and the [state](#states)/[action](#actions) specific configs.

* [Container](pillars/container.md):<br/>
  special pillar that can contains other pillars
* [Boolean](pillars/boolean.md):<br/>
  expose status like Open/Closed, Empty/Full, Enable/Disabled;<br/>
  or actions like On/Off, Fill/Empty, Mute/Unmute...
* [Range](pillars/range.md):<br/>
  expose status like Temperature, Absorbed power;<br/>
  or actions Control volume, Light dimming...
* Pillar #3:<br/>
  Not yet available
* Pillar #4:<br/>
  Not yet available

---

## States

In a JOSP Objects, each exposed feature is defined as a State or an Action.
**States can wait for firmware's value updates and update JOSP Services** on
value changes.

On JOSP Services side, state pillars expose info on how to handle it. For example
the State Range pillar allow to define the min and max value, those values can be
used by a JOSP Service to render the status value in a slider with the correct
range.

Depending on firmware details, state's workers can run as background listeners
or executed periodically to pull the state's value. To configure the pillar to
use a puller worker use the ```puller``` property, otherwise use the ```listener```
prop:

* ```listener```: define firmware calls for listener's states and actions
* ```puller```: define firmware calls for puller's states and actions

Those properties accept a string formatted with following pattern:

```
{FirmwareProto}://{FirmwareConfigs}
```

Where the ```FirmwareProto``` must be one of the **FirmwareProtocols registered
in the [JOD Agent configs](configs.md)** file with the properties
```jod.executor_mngr.pullers|listeners```.<br/>

---

## Actions

Action inherits from States, so they can send status update, but they can also
**receive commands requests from JOSP Services and translate those command to
firmware calls**.<br/>

Pillars that represent an Action must include the ```executor``` property in
their definition:

* ```executor```: define firmware calls used by actions when receive a command request

This property accept a list of worker configs string. Checkout the format and
examples on [Workers](workers.md) page.
