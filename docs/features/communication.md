# Communication

A JOSP EcoSystem, with the aim to always provide the best connection, can handle
different type of communication between JOSP Objects and JOSP Services.

By default, each JOSP Objects create a local server to allow local JOSP Services
to **connect directly to the object**. At the same time, both JOSP Objects and
Services, on their startup, open a connection to the JCP Gateway. With this
connection objects and services can **communicate together via Cloud**.

When an internet connection is not available, or even when there is no network
infrastructure, **objects can start working as an Access Point**. Then other
devices like smartphones or notebook (with JOSP Services installed) can connect
to the Access Point. Now installed services can reach the JOSP Object that emit
the Access Point like in a direct communication because they are effectively on
the same network.

When both communications are available between an object and a service, the JOSP
Objects send statuses updates via both communication channels (to decrease the
probability of packet loss). On the other side, the JOSP Service send his requests
depending on permission level. Service use the communication channel with the
highest access level on object. If both communication have the same access level,
then the direct communication is preferred.

**All communication are encrypted with SSL certificates.**

---

## Direct communication

Each JOSP Object, by default, at his startup initialize a local server and publish
it to the local network via [mDNS](https://en.wikipedia.org/wiki/Multicast_DNS)/[Bonjour](https://developer.apple.com/bonjour/)
protocol.<br/>
On the other side, each JOSP Service, by default, at his initialization start a
mDNS/Bonjour listener for JOSP Objects local servers.

When a **JOSP Service discover a JOSP Object** on a local network, open a new
connection. For each new local connection objects and service share their public
certificate, if they haven't already. After that, they can open an encrypted
communication channel.

**Immediately, the object send his presentations messages** (object's info and
structure) as long as the service/user pair has the adequate access level. The
service also send his presentation (service and user ids) to the object on opening
the connection. Then the object respond to the service presentation with a message
containing the permission level granted to that service/user pair.

---

## Cloud communication

Both JOSP Objects and JOSP Services, by default, at their startup/initialization
open a connection to the JCP Gateway. This connection is encrypted using the JCP'
SSL certificate (already know from the JOSP object because included in the JOD
library) and a self-signed certificate from the object or from the service.

Objects and services during their startup also generate their own self-signed
SSL Certificate. Before connect to the JCP Gateway they must ask for gateway
address and port to the JCP APIs. This API method accept as param, the public key
of the self-signed certificate from the connecting object/service. So the API can
register the object/service's certificate in the JCP. Then, when the object/service
connect to the gateway, it already knows his public SSL certificate.

In a JCP Gateway connection, this additional step (via the JCP API) not only
permits encrypt the communication but also allows to check the objects and service(/user)
identities across cloud communications.
