# JOSP JOD Communication system

**To connect  a [JOSP Object](/docs/features/objects_integration.md) to a [JOSP EcoSystem](/docs/features/ecosystem.md),
the JOD Agent use a complex communication system.**<br/>
This system is delegated to handle communication with JOSP Services (both
[Direct](/docs/features/communication.md#direct-communication) and [Cloud](/docs/features/communication.md#cloud-communication)),
manage JCP Authentication, but also to encrypt all tx/rx data.

Because a **JOSP EcoSystem is composed by different 'pieces', a JOD Agent use
multiple communication channels** with different protocols to talks with all those 'pieces':

* to JCP APIs (via HTTP) to register and handle the JOSP object to the JCP
* to JCP Gateways (via JOSP) to communicate with JOSP Services via Cloud Communication
* from JOSP Services (via JOSP) to communicate with JOSP Services via Direct Communication

[JCP APIs](/docs/comps/jcp/core/apis/README.md) are a set of HTTP Methods
exposed by the JCP platform and are used by JOD Agent to handle the JOSP Object
on the JCP. That include: the generation of a unique object's id, the supply of
the JCP GWs access info, the storage of JOSP Object's events and status histories,
etc...<br/>
Moreover JCP APIs requires [JOD Agent authentication](#jcp-authentication) via
the JCP Auth service (an OAuth2 authorization server).

To communicate with JOSP Services, the JOD Agent support both JOSP Communications
types: [Direct](/docs/features/communication.md#direct-communication) and [Cloud](/docs/features/communication.md#cloud-communication).

## Cloud communication

For **Cloud Communication**, the JOD Agent open a connection to the [JCP Gateways](/docs/comps/jcp/core/gws/README.md).
So all JOSP Services (with at lest [STATUS permission](specs/permissions.md) on the
JOSP Object represented by the JOD Agent) can see exposed JOSP Object and interact
with it via Cloud communication.<br/>
The JOD Agent use internal [JOD Gateway O2S Client](#cloud-communication),
that support [JOSP protocol](/docs/features/protocol.md), to connect with the
JCP GW.
You can disable Cloud Communication setting the ```jod.comm.cloud.enabled```
property from [JOD AGent Configs](specs/configs.md) to ```false``` (it's ```true```
by default). So the jod gateway client is not initialized at JOD Agent startup.

## Direct communication

On the other side, for **Direct Communication** the JOD Agent startup a local
server and publish it as mDNS/Bonjour service on local network. That allow all
JOSP Services on the same local network to discover and connect to the JOSP Object
exposed by the JOD Agent.<br/>
[Local JOD Server](#direct-communication) support the [JOSP protocol](/docs/features/protocol.md)
and is executed at JOD Agent startup.<br/>
You can disable Direct Communication setting the ```jod.comm.local.enabled```
property from [JOD AGent Configs](specs/configs.md) to ```false``` (it's ```true```
by default). So the local server is not executed at JOD Agent startup.

## Communication encryption

With the intention of **providing the highest level of security**, both the
[JOD Gateway O2S Client](#cloud-communication) and the [JOD Local Server](#direct-communication)
use communication channels based on TCP and encrypted with SSL. For Cloud
communication, the SSL encryption use a pre-shared certificate on the JCP GWs
server side; and a self-generated certificate as JOSP Object's identity.
Each JOSP Object, register his self-generated certificate to the JCP when requires
the JCP Gateways' access info to JCP APIs. On other hand, the Direct communication
requires a pre-connection step where JOSP Object and Service share their own
certificates. After that, they can open a direct encrypted communication.

Also, the [Public JCP](/docs/comps/jcp/public_jcp.md), with the aim to
improve your security, expose only HTTPs endpoints and encrypt all communication
with his own SSL certificate.

## JCP Authentication

...
