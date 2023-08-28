# Pillars

Pillars are the object's features representations exposed by JOSP Objects to the
JOSP EcoSystem.

When an object is configured as an JOSP Object, all his features are translated
in to pillars. Depending on his configuration, a pillar can represent any kind of
feature exposed by the object.

First, define if the object's feature **expose a value or a command executable**
on the object itself. Depending on that, the pillar describing this feature must
be respectively a [State](#state) or an [Action](#action) pillar.

Then, identify **which kind of value or which commands expose the features**.
In the JOSP EcoSystem there are 4 types of pillars (with JOSP 2.2.0 only 2 are
available) to describe different values and commands exposed by objects.<br/>
For examples all binary values (On/Off, Empty/Full, Active/Inactive...) can be
represented with a [Boolean](#boolean) [State](#state) pillar. On other hands all
commands to regulate an actuator (oven temperature, speakers volume, valve flow)
can be represented with a [Range](#range) [Action](#action) pillar, and so on...

## State

States pillars are configured to read object's values (with firmware calls) and
send [update messages](protocol.md#status-update).

Depending on firmware, states pillars can listen for firmware updates or can
periodically poll the firmware for updated value. Only when the value changes,
this pillar sends the update message to all connected JOSP Services.

This message is send to all JOSP Services/Users with at least the [STATE](permissions.md#state)
permission on current object.

## Action

Actions inherit all features from States pillars.
So also action pillars send update messages, when configured object's value changes.

Moreover, actions pillars expose commands that can be executed by JOSP Services.
JOSP Services can execute those commands sending an [action request](protocol.md#action-request)
to the JOSP Object. 

When a pillar action receive an action request, depending on his configuration,
it translates the action request in firmware calls. Finally, the firmware send
the command to the object's actuators.

Only JOSP Services/Users with at least the [ACTION](permissions.md#action) permission
can send action requests to current object.

## Boolean

Boolean pillars can be used to **represent any binary value** like On/Off, Empty/Full,
Active/Inactive...<br/>
On the other side it can be used also to **represent any pair of opposing commands**
like SwitchOn/SwitchOff, ToEmpty/ToFill, Start/Stop...

A Boolean state emits a status update message each time his value switches.<br/>
A Boolean action exposes setTrue and setFalse opposing commands, but also the
switchValue command that, as you can guess, switch current pillar's value.

## Range

Range pillars are used to **represent values within a range** like environment
temperature in °C, vehicle speed in Km/h, % of humidity in the ground... Range
pillars are also used to **represent commands to regulate remotely a value within
a range** like the oven temperature, a valve flow, the speaker volume...

Range pillars introduce some specific properties in the pillar configuration
like ```min```, ```max``` and ```step```. Those properties allow pillar to determinate
the range of the value handled.

A Range state emits a status update each time his value change.<br/>
A Range action exposes different commands to remotely control the value like
setValue, setMin, setMax, increase and decrease.

This pillar type is really flexible.<br/>
It can represent also cumulative values, like how many times a light was switched
on/off. Those pillars can set ```0``` as the minimum value and a high number as
the maximum value reachable.<br/>
Another example where use this pillar, is when object allow to select a program
(like the washing machine). Each program can be associated to a value from 0 to N.
Then the pillar should be configured with ```0``` as the minimum value, ```N```
as the maximum value and ```1``` as the step. So services, using exposed commands,
can easily select the Nth program, switch to the next/previous one, or even select
the first/last program.

## Pillar 3

Not yet available.

## Pillar 4

Not yet available.


