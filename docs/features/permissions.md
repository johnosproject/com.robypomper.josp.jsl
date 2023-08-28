# Permissions

The JOSP EcoSystem has his own access control system.<br/>
This **access control system is based on JOSP Permission and each JOSP Object has
his own permission tables**. Each permission is a tuple that define which access
level is granted to a JOSP Service/User pair for accessing to the object.

Every time a JOSP Object interact with a JOSP Service, **the object checks
service/user's access level**. Depending on interaction type (status update, action
execution or object configuration) and the access level granted (STATE, ACTION,
OWNER) the object can proceed or avoid the interaction.

Each time a JOSP Service connect to a JOSP Object, **the service presents itself
and his current user**. So, objects always know which service/user would access
to the object and looking for them on object's permissions list.<br/>
If no user is currently logged into JOSP Service, then it presents itself to
objects as a service with anonymous user. When a JOSP Object can't find a valid
permission to the service/user pair, it assigns the NONE access level to that pair.

Because, the JOSP EcoSystem allow different type of communication between objects
and services (mainly [direct](communication.md#direct-communication) or [via cloud](communication.md#cloud-communication)),
**a JOSP Permission can be set to be used only on direct or also for cloud communications**.
Each permission contains the ```connType``` field, that determinate if the permission
must be used when service is connected directly to the object; or also when service
is connected via cloud. This field allows setup specific behaviours like grant
users ACTION permission when they are near the objects (on the same local network)
and grant only the STATUS permission when user are connected from remote location.

With the aim to make this access system more flexible to user needs, we added some
**'jolly' value to define JOSP Permissions**.<br/>
Permissions can define the service and user to be applied to with the two fields
```srvId``` and ```usrId```. Those fields can accept, as you can guess, respectively
service and user ids but, they can accept also other special values:

* ```srvId="#All"``` the permission is applied to all services
* ```usrId="#All"``` the permission is applied to any user
* ```usrId="#Owner"``` the permission is applied to object's owner

Use those values instead service and user ids, allow you to define 'jolly' permission
that can be applied to multiple service/users pairs. For example, you can grant
the access to an object defining a permission with a specific user's id and the
```#All``` value as service id. This permission will grant the access to the JOSP
Object at specified user independently of the JOSP Service used.<br/>
At the same time, you can define a permission with a specific service's id and
the ```#All``` value as user id. In this case anybody can access to the JOSP Object
as long as he is using specified JOSP Service.

## State

This access level on an object allow service/users get object's structure and all
his statues' values.

## Action

This access level on an object allow service/users get object's structure, all
his statues' values and send action requests to the object.

## Owner

This access level on an object allow service/users get object's structure, all
his statues' values, send action requests to the object and  configure the object.

## None

This access level on an object don't grant any permission for current
service/users pair.
