# JOSP Protocol

The JOSP protocol is the language used by JOSP Objects and JOSP Services to
communicate with each other.<br/>
This protocol is used in both communication types (direct and cloud) supported
by a JOSP EcoSystem.

With this protocol JOSP Objects can send their info and structure to JOSP Services.
They can also inform services when an objet's state change his value.<br/>
On the other side, JOSP Services use this protocol to send request to object for
commands execution or update object's configs.

The JOSP protocol, as a result of having applied the **Security-by-design**
concept during the JOSP EcoSystem design, provides fields related to the system
security. Each message from a JOSP Object contains his id, like each message from
a JOSP Service contains service and user ids. That plus the use of **encrypted
communication channels** with SSL certificates, prevents services change their
identity after the connection has been established.

---

## Object Presentation

Objects presentation is a set of JOSP **messages used by objects to present
themselves to other JOSP Services and to the JCP Gateway**.

When a JOSP Object opens a new connection (to a JOSP Service or to the JCP Gateway),
it sends following messages:

* ```OBJ_INF_MSG``` a message that contains object's info like id, name, model, description...
* ```OBJ_STRUCT_MSG``` a message that contains object's structure, this structure is an altered version of the structure defined by Makers.
* ```OBJ_PERMS_MSG``` a message that contains object's permissions list.

Those messages are always send to the JCP Gateway, meanwhile objects send those
messages only to services with at least the STATE access level. The ```OBJ_PERMS_MSG```
message is sent only to JOSP Services with the OWNER access level.

Only when the remote peer of the new connection is a JOSP Services, the object
calculate from his permission list the service/user access level. Then the object
sent the ```SRV_PERMS_MSG``` message that inform the service of what level of
access they have been granted.

---

## Status Update

OBJ > SRV

Every time a JOSP Object must inform JOSP Services for a state update, it sent a
```UPD_MSG``` message to the JCP Gateway and all connected JOSP Services with at
least the STATE access level on current object. 

The ```UPD_MSG``` contains following additional fields:

* compPath: the pillar's path to identify which pillar in the object's structure update his value
* cmdType: the pillar's type
* payload: the rest of the message depends on pillar type and the value updated

Update state messages are sent only to JOSP Services that have at least the STATUS
access level on current object.

---

## Action Request

SRV > OBJ

JOSP Services can send request to object to execute command exposed as Actions.<br/>

The ```CMD_MSG``` request can be used to request the execution of any Action Pillar type.

* fullSrvId: the full service's id contains the service id, the current service's user id and the service instance number
* objId: target object's id
* compPath: target pillar's path
* cmdType: the pillar's type
* payload: the rest of the message depends on pillar type and the action to execute

When a JOSP Object receive an action request message, it checks the service
permission (on current object) and, execute the action only if the service/user
pair have at least the ACTION access level.

---

## Object Setters

SRV > OBJ

JOSP Services can also configure some object's configs: object's name and owner.

To update those configs, services send one of the following messages to the object
to config:

* ```OBJ_SETNAME_MSG``` a message to update object's name
* ```OBJ_SETOWNERID_MSG``` a message to update object's owner

* fullSrvId: the full service's id contains the service id, the current service's user id and the service instance number
* objId: target object's id
* objName: the new name for the target object (only for ```OBJ_SETNAME_MSG```)
* ownerId: the user id of the new owner for the target object (only for ```OBJ_SETOWNERID_MSG```)

_Each time a JOSP Object update his owner, the object disconnect from all JOSP
Service (and JCP Gateway), regenerate his object's id and then restart local and
cloud connection._

To edit JOSP Object's configs, service/user pair must have at least OWNER access
level on that object.

---

## Object Permissions Editing

SRV > OBJ

JOSP Services can edit object's permissions remotely using one of the following
messages:

* ```OBJ_ADDPERM_MSG``` a message to add a new permission to the object
  * fullSrvId: the full service's id contains the service id, the current service's user id and the service instance number
  * objId: target object's id
  * srvId: the new permission's service id
  * usrId: the new permission's user id
  * permType: the new permission's access level
  * connType: the new permission's connection type
* ```OBJ_UPDPERM_MSG``` a message to update an existing permission to the object
  * fullSrvId: the full service's id contains the service id, the current service's user id and the service instance number
  * objId: target object's id
  * permId: target object's id
  * srvId: the updated permission's service id
  * usrId: the updated permission's user id
  * permType: the updated permission's access level
  * connType: the updated permission's connection type
* ```OBJ_REMPERM_MSG``` a message to remove an existing permission to the object
  * fullSrvId: the full service's id contains the service id, the current service's user id and the service instance number
  * objId: target object's id
  * permId: target object's id

To edit JOSP Object's permissions, service/user pair must have at least OWNER
access level on that object.

---

## Object History Requests

SRV > OBJ

JOSP Services can also access to object's history.

JOSP Objects store all statuses values and all internal events, so services can
request both via following messages.

* ```H_EVENTS_MSG```
  * fullSrvId: the full service's id contains the service id, the current service's user id and the service instance number
  * objId: target object's id
  * reqId: request's id, used to pair requests and responses
  * limits: HistoryLimits objects to define request's time or quantity limits
  * evType: request's filter on events type
* ```H_STATUS_MSG```
  * fullSrvId: the full service's id contains the service id, the current service's user id and the service instance number
  * objId: target object's id
  * compPath: required pillar's path
  * reqId: request's id, used to pair requests and responses
  * limits: HistoryLimits objects to define request's time or quantity limits

---

## JCP Gateway messages

JCP GW > SRV

When a JOSP Service connect to the JCP Gateway, it sent to the connected services
all available objects (also objects actually not connected).

The Gateway informs connected services that an object is not connected with the
```OBJ_DISCONNECT_MSG``` message. This message contains only the object's id
(```objId```) that is not connected to the Gateway.
