# JOSP JOD

<table><tr>
<td>
<img src="JOSP_JOD_Logo_250.png" width="200">
</td>
<td>
The <b>John Object Daemon is software that expose a JOSP Object to the JOSP EcoSystem</b>. <br/>
It can be installed on computers, embedded devices, servers, etc...
</td>
</tr></table>

**NB!**: normally it's distributed within a [JOD Distribution](/docs/comps/jod_distributions.md)
designed by [Makers](/docs/actors/makers.md).

---

**Component Info:** <br/>
Current version: 2.2.2 <br/>
Development version: 2.3.0-DEV <br/>
Docs: [JCP @ JOSP Docs](README.md) <br/>
Repo: [com.robypomper.josp @ Bitbucket](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/) <br/>
Downloads: [com.robypomper.josp > Downloads @ Bitbucket](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/downloads/)

---

## Sub-pages

* [Tasks groups](tasks_groups.md)
* [Requirements](requirements.md)
* [Usage](usage.md)
* [Communication](comm.md)

**Specs** <br/>
* [Object ID](specs/object_id.md)
* [Configs](specs/configs.md)
* [Permissions](specs/permissions.md)
* [Structure](specs/structure.md)
* [Pillars](specs/pillars.md)
* [Workers](specs/workers.md)
* [Shell](specs/shell.md)
* [Files](specs/files.md)

**Build-in workers** <br/>
* Listeners
  * [File](workers/listener_file.md)
* Pullers
  * [Shell](workers/puller_shell.md)
  * [UnixShell](workers/puller_unixshell.md) (deprecated)
  * [HTTP](workers/puller_http.md)
* Executors
  * [File](workers/executor_file.md)
  * [Shell](workers/executor_shell.md)
  * [UnixShell](workers/executor_unixshell.md) (deprecated)
  * [HTTP](workers/executor_http.md)


# JOSP Object Daemon

The JOD Agent read object's structure from ```configs/struct.jod``` file and
expose that structure to the [JOSP EcoSystem](/docs/features/ecosystem.md) and
then to [JOSP Services](/docs/features/service_integration.md) as a [JOSP Object](/docs/features/objects_integration.md).
Only JOSP Services with the right [permission](/docs/features/permissions.md)
can access to the exposed JOSP Object. Depending on their permissions, they can:

* if permission=[STATE](/docs/features/permissions.md#state):
  read object's structure and contained [Pillar's states](specs/pillars.md#states)
* if permission=[ACTION](/docs/features/permissions.md#action):
  require [Pillar's actions](specs/pillars.md#actions) executions
* if permission=[OWNER](/docs/features/permissions.md#owner):
  edit object's name, owner or permissions
* if permission=[NONE](/docs/features/permissions.md#none):
  list object but can't get object's info or structure

In the JOSP EcoSystem **each JOSP Object is identified by his ID**. The
[Object's ID](specs/object_id.md), if not yet set, is generated automatically at
JOD Agent startup. This, normally happens on object's first startup.
The only other circumstance when the object's ID changes, is when the object's
change his owner. That happen on object's owner registration or when current
owner 'sell' the object to another user.

At startup, the JOD Agent loads the [object's structure](specs/structure.md) and
initialize all required object's [Pillars](specs/pillars.md) and relative
[Workers](specs/workers.md). Then checks if configured [JCP](/docs/comps/jcp/README.md)
is reachable, if it's available, the object opens a connection with returned
[JCP Gateways](/docs/comps/jcp/core/gws/README.md).<br/>
Finally, it startup a local server for direct communication.

It's possible to **customize the exposed object** changing the [JOD Agent behaviour](specs/configs.md)
with his main configs file  ```configs/jod.yml``` or editing the ```configs/struct.jod```
file to alter his [structure](specs/structure.md).
Here a list of all JOD Agent's [files](specs/files.md).

JOD Agent was developed to **run on many type of devices (computers, embedded,
server...) and keep running 24/7**.<br/>
Because of that, normally it's executed as a [background process](usage.md#daemon),
but occasionally it can be executed also as a [foreground command](usage.md#command)
and managed by the [JOD Shell](specs/shell.md). Mostly for configurations test purposes.

Here a simple guide on **JOD Agent's [usage](usage.md) and troubleshooting**.
