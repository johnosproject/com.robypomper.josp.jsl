# The IoT EcoSystem for Domestic users

A Personal IoT EcoSystem for domestic usage can contain all the connected objects
in a house.

With a Personal IoT EcoSystem, Users can configure their own Smart Home solutions
adding connected objects and JOSP Services that automate domestic task, monitor
home consumptions, check user safety and security... 

The JOSP EcoSystem fits perfectly domestic users such as those described below.

---

* ### Same object, multiple manufacturers

Today there are a hundred of connected bulbs, lamps and led strips.
Each one using a different technology and a different mobile app to control them.
Users can register all of those smart lights as JOSP Objects and **use all of
them from a single mobile app** (or any other JOSP Service), regardless the
technology used by each single smart light.

⏩ Check out the [What JOSP Object can represent](../2_new_objects/1_what_object_represent.md)
  page for an overview of JOSP Objects<br/>
⏩ or go directly to the [official JOD Distributions list](/docs/comps/jod_distributions.md)
  for a list of available JOSP Objects as downloadable JOD Distribution.

* ### One EcoSystem for any object's type

Depending on the service the user would configure in his Domestic EcoSystem, he
can integrate many object's types. Then **each JOSP Service can access to all
object's types that interests him**.<br/>
For example a JOSP Service can act as Building Energy Manager. This service can
monitor home consumptions reading data from counters for household supplies
(energy, water, gas). Then display those data as daily report to the user.
A more advanced version of this service can also analyze consumption data with
an AI/ML algorithm and predict users needs by acting on other JOSP Objects such
as boilers, photovoltaic panels, accumulators...

⏩ Discover JOSP Services on the [official JOSP Services list](/docs/comps/jsl_services.md)<br/>
⏩ and [enable JOSP support on desired services](../2_new_objects/1_what_object_represent.md)
 
* ### Which service, can interact with which object

When user is the owner of the JOSP Object, he can update the object's permissions.

Each JOSP Object check his permission table before any interaction with a JOSP
Service. A permission can grant a specific access level ([NONE](/docs/features/permissions.md#none),
[STATE](/docs/features/permissions.md#state), [ACTION](/docs/features/permissions.md#action),
[OWNER](/docs/features/permissions.md#owner)) to a specific JOSP Service/User
pair. That means a **JOSP Service (and his current logged user) can interact with
a JOSP Object if and only if they have the right access level required for the
interaction**. For example a JOSP Service (and his user) must have at lest the
STATE access level to receive status updates from an object. Instead, to update
the object's permission table, the service/user need the OWNER access level.
You can find more details on required access levels on
[JOSP Permission](/docs/features/permissions.md) page.

⏩ Check out [How to set JOSP Object permission](../5_manage_services/2_set_permissions.md)
   for specific JOSP Service.

* ### Share JOSP Objects with your family and friends

When a user setups a SmartHome devices, he would **grant object's access also to
his family**; and sometimes, for some objects, he would also grant access to
**his friends**. They can do it adding right permissions on desired JOSP objects.

For example users can decide to share all smart lights and home appliances with
his family members and allow them to **fully access to connected objects from
anywhere**. To do that, user must add following permission to each object to
share for each family member:
```
SrvId: #ALL, UsrId: {FAMILY_MEMBER_ID}, AccessLevel: OWNER, ConnType: CloudAndLocal
```

Another example can be when a user would share a JOSP Object with his friend,
but only when his friend is physically near to the object. Like when you would
grant TV control access to your friend that would show his latest photos or videos.
Then to allow other users access to your JOSP Object when they are connected to
the same local network (p.e. to the same WiFi), user must add this permission to
the JOSP Object:
```
SrvId: #ALL, UsrId: {FRIEND_ID or #ALL}, AccessLevel: ACTIONS, ConnType: OnlyLocal
```
With this permission, user's friend can access to the shared JOSP Object only
when he is physically near to the object (connected on the same WiFi) and execute
actions but can't change object's configurations. Be careful, this permission
allows ```FRIEND_ID``` to access to the shared JOSP Object with any JOSP Service.
To limit your friend to use specific JOSP Services, please change the ```#ALL```
value with the allowed JOSP Service's id.

⏩ Check out [How to set share JOSP Object](../3_manage_objects/3_share_object_with_users.md)
  with other JOSP Users.

---
