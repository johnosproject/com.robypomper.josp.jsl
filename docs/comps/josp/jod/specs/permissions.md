# JOSP JOD Permissions

In the JOSP EcoSystem, permissions are handled by objects. **Each object know
which JOSP Service and user pair can access to himself.**

On JOSP Object and JOSP Service 'presentation', the object assign a permission
level to the connected service. Then the object will check this permission level
each time he needs to send a status update or when receives a request from the
connected service.

**Permission level are an ordered list** and each level grant to service/user the
same permission as the lower levels.<br/>
Here the permission levels list ordered from lower to higher:

* [NONE](/docs/features/permissions.md#none): list object but can't get object's info or structure
* [STATUS](/docs/features/permissions.md#state): read object's structure and contained [Pillar's states](pillars.md#states)
* [ACTION](/docs/features/permissions.md#action): require [Pillar's actions](pillars.md#actions) executions
* [OWNER](/docs/features/permissions.md#owner): edit object's name, owner or permissions

**JOD Agent assigns a permission level to JOSP Services** using an internal
permission table. This table includes a **service id, a user id and a level permission**.
If there is no row corresponding to the connecting JOSP Service, then this service
receive NONE as permission level.<br/>
On permissions update, all permission levels assigned to JOSP Services are re-assigned.

With the aim to speed up the permissions' configuration, the JOD Agent allow use
**special placeholder as wildcards** instead real service or user id. You can use
those special placeholder to assign a permission level to more than one service/user
pair. For example to grant a desired permission level to a specific user (set
user's id) regardless of the service he is using (set ```#All```).

Placeholder for the user's id:
* ```#Owner```: correspond if the JOSP Service's user is the same as [object's owner](#objects-owner)
* ```#All```: correspond for any JOSP Service's user id

Placeholder for the user's id:
* ```#All```: correspond for any JOSP Service id

To provide a more flexible access control management, **permission can specify
also a connection type**: ```OnlyLocal/LocalAndCloud```. If 'OnlyLocal' is set,
the permission can be applied to a service/user pair only if the JOSP Service is
connected via Direct Communication.

When objects and services interact via JCP ([Cloud Communication](/docs/features/communication.md#cloud-communication)),
the [JCP Gateway](/docs/comps/jcp/core/gws/README.md) caches permissions
table for each connected object, then use those tables to route all messages.

**Permission examples:**

* ```SrvId: #All, UsrId: x-y-z, Type: CoOwner, Conn: LocalAndCloud```<br/>
  Grant ```CoOwner``` permission level to ```all``` services used by specific ```x-y-z``` user.
* ```SrvId: #All, UsrId: #All, Type: Action, Conn: OnlyLocal```<br/>
  Grant ```Action``` permission level to ```all``` services used by specific ```any``` user if communicate via Direct Communication.
* ```SrvId: z-k-j, UsrId: #All, Type: Status, Conn: LocalAndCloud```<br/>
  Grant ```Status``` permission level to ```any``` user that use the ```z-k-j``` service.

---

## Edit object's permissions

JOD Agent allow **different ways to add, update or remove object's permission**.

The most easy way is to use the [JCP FrontEnd](/docs/comps/jcp/core/fe/README.md)
at [www.johnosproject.org/frontend](https://www.johnosproject.org/frontend).
After you logged in, you can go to the object's Access Control page and update
his permissions table.

Like the JCP Front End, also many other JOSP Services allow you to edit object's
permissions. As long as the service/user pair have the CoOwner permission level
on that object.

Another way to edit object's permission, used normally on JOSP Object's development,
is via the [JOD Shell](shell)'s 'permission' commands.

---

## Object's Owner

A permission in a JOSP Object can refer to a user, using the ```#Owner``` placeholder.
This placeholder is replaced with the value contained in the ```jod.permissions.owner```
property of the [JOD Agent configs](configs).

Because when a user change object's owner, the object update his id, it also
invalidates all permissions. That trigger a permissions' generation. This is part
of the re-fresh of the JOSP Object when transferred from old owner to a new one.

---

## Generation

On JOD Agent startup, if no permission files is present or if it's empty, a new
permissions are generated and stored in the file.<br/>
New permission are also generated on object's owner change.

When permission are generated, the JOD Agent try to generate them via JOSP Core
/ Permissions get method from [JCP APIs](/docs/comps/jcp/core/apis/README.md).
This method accepts the 'strategy' param that is read from the ```jod.permissions.generation_strategy```
property from [JOD Agent configs](configs).

If the JCP is not available, then the JOD Agent generate following permissions locally:

* ```SrvId: #All, UsrId: #Owner, Type: CoOwner, Conn: LocalAndCloud```
* ```SrvId: #All, UsrId: #All, Type: CoOwner, Conn: OnlyLocal```

The permissions file by default is placed in the main JOD Agent dir with
```configs/perms.jod``` name. But it can be changed using the ```jod.permissions.path```
property from [JOD Agent configs](configs).

---

## JCP Sync

The JCP keep a backup of the permissions' table for each JOSP object to use it when
object is offline.

Because object's permission can be updated when object was not connected to JCP,
it requires a sync system between object and cloud.<br/>
JOSP Services can edit object's permissions also via [Direct Communication](/docs/features/communication.md#direct-communication).
So when an object connects again to JCP, it must synchronize the cloud permissions table.
