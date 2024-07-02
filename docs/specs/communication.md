# JOSP Service Library -Specs: Communication

Because a **JOSP EcoSystem is composed by different 'pieces', a JSL Service use
multiple communication channels** with different protocols to talks with all those 'pieces':

* to JCP APIs (via HTTP) to register the JSL Service and login his user to the John Cloud Platform
* to JCP Gateways (via JOSP) to communicate with remote JOSP Object via Cloud Communication
* to JOSP Object (via JOSP) to communicate with local JOSP Object via Direct Communication

The JCP APIs client is initialized directly from the [JSL](../../src/main/java/com/robypomper/josp/jsl/JSL.java)
class as the `JCPAPIsClientSrv` object; and then shared to all other subcomponents.<br/>
At the same time, the JSL class, initializes the [JSLCommunication](../../src/main/java/com/robypomper/josp/jsl/comm/JSLCommunication.java)
that is responsible for the JOSP communications and the relative message routing.
To communicate with JOSP Objects, the JSL Service support both JOSP Communications
types:

* [Direct/Local](communication_local.md) via [JSLLocalClientsMngr](../../src/main/java/com/robypomper/josp/jsl/comm/JSLLocalClientsMngr.java)
* and [Cloud/Remote](communication_cloud.md) via [JSLGwS2OClient](../../src/main/java/com/robypomper/josp/jsl/comm/JSLGwS2OClient.java)

To keep the communication secure the JSL Service use encrypted communication with
the JCP APIs and the JCP Gateways. For the Local Communication, the JSL Service
supports the [Security Levels](../josp_comps/josp_commons_josp_communication_securitylevels.md) feature that
allows the JSL Service to communicate with JOSP Objects using different security
levels based on the SSL/TLS connection.


## Message routing

The JSL Service uses the [JSLCommunication](../../src/main/java/com/robypomper/josp/jsl/comm/JSLCommunication.java)
class to handle the message routing.

Based on the [JOSP Protocol](../josp_comps/josp_commons_josp_protocol.md), those are the
available flows:
- Local JOSP Object to Current JSL Service
- JCP GWs to Current JSL Service
- Current JSL Service to Specific Local JOSP Object
- Current JSL Service to JCP GWs

This class is responsible for the incoming messages routing to the correct
[JSL's Object representation](objects.md).<br/>
Any incoming message contains the JOSP Object's ID, so the message is routed to
the correct JSL's Object representation that will process it.

The incoming messages are processed by the following methods chain:
- *JSLLocalClient(JSLLocalClient client, String msg, JOSPPerm.Connection connType)*
  - *JSLLocalClientsMngr::processFromObjectMsg(JSLLocalClient client, String msg, JOSPPerm.Connection connType)*
    - *JSLCommunication::processFromObjectMsg(String msg, JOSPPerm.Connection connType)*
      - *JSLRemoteObject::processFromObjectMsg()*

Actually, the JSLCommunication do NOT handle the outgoing messages, that is done
by the JSL's Object itself or by the [HistoryBase](../../src/main/java/com/robypomper/josp/jsl/objs/history/HistoryBase.java)
class. Those classes directly use the *Peer::sendData(String data)* method to
send the messages to the JOSP Objects.
      
- *ObjBase::sendToObject(String msg)*
- *HistoryBase::sendToObjectLocally(JOSPPerm.Type minReqPerm, String msg)*
- *HistoryBase::sendToObjectCloudly(JOSPPerm.Type minReqPerm, String msg)*


## Permissions checking

For the JOSP EcoSystem, the component that checks the permissions is the JOSP Object.

Even if not mandatory, the JSL Service can also check the permissions for the
outgoing messages. That allows avoiding sending messages to JOSP Objects that
cannot handle them. This check is done by the
*ObjBase::sendToObject(String msg)* and the
*HistoryBase::sendToObjectLocally(JOSPPerm.Type minReqPerm, String msg)* methods,
the same methods used to send messages to the JOSP Objects.

The JSL Service do not check the permissions for the incoming messages, that is
done by the JOSP Object itself.


## John Cloud Platform APIs

JCP APIs are a set of HTTP Methods
exposed by the JCP platform and are used by JSL Service to handle the JSL Service
on the JCP. That include: login/logout the user, the supply of
the JCP GWs access info...

The JCP APIs service requires the JSL Service authenticate itself via "OAuth2 -
Authorization Code Flow" and the JCP APIs service will return a JWT token that
will be used to authenticate the JSL Service in the subsequent requests. This
token is stored in the `JCPAPIsClientSrv' object, so it can provide all methods
used to send the requests to the JCP APIs.


## JSL Communication configs

The JSL Service's communication configuration is defined in the
`jsl.yml` [configuration file](jsl_yml.md). The properties that define
the communication are:

* `jsl.comm.local.enabled` ("true"): If 'true' the server for Local Communication will be enabled, otherwise it will not be started.
* `jsl.comm.local.discovery` ("Auto"): Discovery implementation for the Local Communication server.
* See the [JSL Local Communication configs](communication_local.md#jsl-local-communication-configs)
* `jsl.comm.cloud.enabled` ("true"): If 'true' the server for Local Communication will be enabled, otherwise it will not be started.
* See the [JSL Cloud Communication configs](communication_cloud.md#jsl-cloud-communication-configs)

The available [Discovery System implementations](../josp_comps/josp_commons_discovery.md)
are:

* `avahi`: Avahi cmdline tool
* `dnssd`: DNS-SD cmdline tool
* `jmdns`: JmDNS Java library
* `jmmdns`: JmmDNS Java library
