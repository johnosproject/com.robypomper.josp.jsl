# JOSP Service Library -Specs: Communication Local

Also known as Direct Communication, the Local Communication is the connection
used by a JSL Service to communicate with JOSP Objects when both are connected on
the same local network.

Technically, the JOSP Object acts as a server and the JSL Service as a client.
Once started, the JOSP Object publishes himself on the local network using the
mDNS protocol. The JSL Service, in turn, searches for JOSP Objects on the local
network and connects to them.<br/>
Once the connection is established, the JOSP Object and the JSL Service can
exchange messages and data using the [JOSP Protocol](../josp_comps/josp_commons_josp_protocol.md).

The Local Communication on JSL Service is managed by the
[JSLLocalClientsMngr](../../src/main/java/com/robypomper/josp/jsl/comm/JSLLocalClientsMngr.java)
class. It uses the mDNS protocol to discover local JOSP Objects, then try to connect
to them using first an encrypted client, and then, if failed, a plain client.
Once the connection is established, the JSLLocalClientsMngr class pass it to the
[JSLObjMngr](../../src/main/java/com/robypomper/josp/jsl/objs/JSLObjsMngr.java)
so a new representation of a JOSP Object can be created and managed by the JSL
Service.

In order to support the [Security Levels](../josp_comps/josp_commons_josp_communication_securitylevels.md) feature,
the JSLLocalClientsMngr can use different clients: with and without SSL.
If SSL is used, then the client can share certificates with the JOSP Object
()the server). That enables the "Share" Security levels. Moreover, always if
SSL is enabled and supported by the JOSP Object, the certificate can
contain the full [JOD Service's ID](service_id.md). That enables the "Instance"
Security levels. More info and examples are available on the
[Security Levels](../josp_comps/josp_commons_josp_communication_securitylevels.md) page.

The incoming messages are processed by the `JODLocalServer::processFromObjectMsg(JSLLocalClient, String, JOSPPerm.Connection)}`
method that forward the message to the `JODCommunication#processFromObjectMsg(String, JOSPPerm.Connection)`
method.<br/>

On the other side the outgoing messages are sent using the
`ObjBase::sendToObject(String msg)` and the 
`HistoryBase::sendToObjectLocally(JOSPPerm.Type minReqPerm, String msg)`
methods.


## Local Connection process

A new connection via the Local Communication starts when a JSL Service
discovers a JOSP Object on the local network. The JSL Service can discover
the JOSP Object using the [Discovery System]() based on the mDNS protocol.
Then, the [JSLLocalClientsMngr](../../src/main/java/com/robypomper/josp/jsl/comm/JSLLocalClientsMngr.java)
class catch discovery events and try to connect to the discovered JOSP Object.

**1. Publish JOSP Object's service on the local network**<br/>
On Local Communication's startup, the JODCommunication
startups a local server and publishes the corresponding service on the local
network using the mDNS protocol. The service is published using the `_josp2._tcp.`
type and the `{OBJ_NAME}-{RND_NUMBER}` string as name.<br/>

**2. Discover and connect to JOSP Object on the local network**<br/>
Once the JOSP Object's service is published, the JSL Service can discover it
and connect to it.<br/>
Depending on the JOSP Object and Service configurations, different connections
types can be established, check the [Security Levels](../josp_comps/josp_commons_josp_communication_securitylevels.md)
page for more information.<br/>
Because of Security Levels support, the JSL Service can attempt to connect to
the JOSP Object using different connection configurations. In case of failure,
the JODLocalServer can receive invalid incoming connections (reachability test,
wrong encryption settings, wrong SSL certificates, etc...).

**3. Handle the connection**<br/>
When a connection to JOSP Object is established, the JODLocalServer creates a
new `JODLocalClientInfo`
object to manage the connection and associate it to a remote JSL Service.<br/>
Then the JODLocalServer checks if the related JSL Service is already connected
using another connection. If so, it discharges the new connection and keep the
old one. Otherwise, it adds the new connection to the list of connected services
and sends the [object's presentation message](../josp_comps/josp_commons_josp_protocol.md) to
the JSL Service.<br/>

When the [JSLLocalClientsMngr](../../src/main/java/com/robypomper/josp/jsl/comm/JSLLocalClientsMngr.java)
is started, it will start the Discovery System to discover JOSP Object's services.<br/>
When a service is discovered, it will try to connect to it using a local client.
If the connection is established, it will wait for the object's id (only if
required). Then it will pass the connection to the [JSLObjsMngr](../../src/main/java/com/robypomper/josp/jsl/objs/JSLObjsMngr.java).

This manager handles also the clients disconnections. On a client disconnection,
it will try to re-connect the remote object using a backup client (if
available). If and only if the remote object is definitely disconnected (locally),
then it will emit the *LocalClientListener#onLocalDisconnected(JSLRemoteObject, JSLLocalClient)*
event.

The connection process can be split in 4 phases:

*JSLClientsMngr::processDiscovered(DiscoveryService discSrv)*
1. Discovered JOD Object's service: when a new JOSP Object has been discovered
*JSLClientsMngr::processClientConnected(JSLLocalClient client)*
2. Connection established: when the client negotiated and established the connection
3. Connection Ready: when get the JOSP Object's ID
4. Remote Object Ready: when the connection has been associated to a {@link JSLRemoteObject}

NB: If the connection fails at any phase, the client is disconnected and the
connection is discharged.

Once the [JSLLocalClientsMngr](../../src/main/java/com/robypomper/josp/jsl/comm/JSLLocalClientsMngr.java)
receive a new discovered service, depending on his configurations, it tries to
connect first using an encrypted client, and then, if failed, a plain client.

During the encrypted connection attempt, if it fails and the share certificate
feature is enabled, the JSLLocalClientsMngr can try to exchange the certificates
with the JOSP Object (the server). The certificate sharing can fail if the JOSP
Object does not enable the sharing feature.

## JSL Local Communication configs

The JSL Service's local communication configuration is defined in the
`jsl.yml` [configuration file](jsl_yml.md). The properties that define
the local communication are:

* `jsl.comm.local.enabled` ("true"): Set 'false' to disable the [JOD Local Server](communication_local.md) and make object not reachable on local network ([Direct Communication](communication_local.md)).
* `jsl.comm.local.onlyLocalhost` ("false"): If 'true' the JSL Service will connect only to localhost JOSP Objects for local communication.
* `jsl.comm.local.discovery` ("Auto"): Discovery system implementation, you can choose between different mDNS/Bonjour implementations.
* `jsl.comm.local.onlySSL` ("false"): If 'true' the JSL Service will use only SSL clients for local communication.
* `jsl.comm.local.onlyNoSSL` ("false"): If 'true' the JSL Service will use only NoSSL clients for local communication.
* `jsl.comm.local.sslSharingEnabled` ("true"): If 'true' the local client tries to share his certificate with the JOSP Object's server.
* `jsl.comm.local.ks.path` ("./configs/local_ks.jks"): Path for the service's local keystore.
* `jsl.comm.local.ks.pass` ("123456"): Password for the service's local keystore.
* `jsl.comm.local.ks.alias` (""""): Alias of the certificate stored into the service's local keystore. By default, it's an empty string that means `$FULL_SRV_ID-LocalCert`.

The available [Discovery System implementations](../josp_comps/josp_commons_discovery.md)
are:

* `avahi`: Avahi cmdline tool
* `dnssd`: DNS-SD cmdline tool
* `jmdns`: JmDNS Java library
* `jmmdns`: JmmDNS Java library
