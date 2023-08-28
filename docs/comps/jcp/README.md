# JOSP John Cloud Project

<table><tr>
<td>
<img src="JCP_Logo_250.png" width="200">
</td>
<td>
The John Cloud Platform is a collection of microservices that compose the cloud
component for a JOSP EcoSystem. The JOSP Project provide two public instances of
the JCP component: Public JCP and Stage JCP.
</td>
</tr></table>

NB!: IoT Solutions based on JOSP EcoSystem commonly use the Public JCP instance
as shared cloud.

---

**Component Info:** <br/>
Current version: 2.2.2 <br/>
Development version: 2.3.0-DEV <br/>
Docs: [JCP @ JOSP Docs](README.md) <br/>
Repo: [com.robypomper.josp @ Bitbucket](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/) <br/>
Downloads: [com.robypomper.josp > Downloads @ Bitbucket](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/downloads/)

---

## Sub-pages

* [Public JCP](public_jcp.md)
* [Tasks Groups](tasks_groups.md)
* [Cloud Versions](cloud_versions.md)

## John Cloud Platform

The JCP platform main goal is to allow [JOSP Objects](/docs/features/objects_integration.md)
and [JOSP Services](/docs/features/service_integration.md) communicate also when
they are not on the seme network. Both can connect to the JCP and communicate
together, overcoming obstacles such as firewalls and complex networks.

_Only on particular circumstances you must run the JCP microservices by your self
instead using the Public JCP.<br/>
Hosting your own JCP it's only required for example when your security policy
can't allow move data outside perimeters.

Until extremely necessary, we don't recommend using your own JCP instance._

JOSP Objects and Services authenticate their self to the [JCP APIs](core/apis/README.md)
using the [JCP Auth](docker/auth/README.md) service. Then they receive the address
of the available [JCP Gateways](core/gws/README.md) to connect with. Once
JOSP Objects and JOSP Services are connected to the JCP GWs, they can start communicate.
In other words, JCP GWs act as a router and forward JOSP messages from objects to
service and vice versa.

* [<span style='opacity: 100%'>JCP All</span>](core/all/README.md): the monolithic JCP Service that include all jcp services in one
  * [<span style='opacity: 100%'>JCP APIs</span>](core/apis/README.md): the service for objects, services and jcp micro-services coordination
  * [<span style='opacity: 100%'>JCP Gateways</span>](core/gws/README.md): to forward messages from objects 2 services and vice versa
  * [<span style='opacity: 100%'>JCP JSL Web Bridge</span>](core/jslwb/README.md): to allow JSL services as web client implementation
  * [<span style='opacity: 100%'>JCP Front End</span>](core/fe/README.md): to display JCP objects and info in a web portal
* [<span style='opacity: 100%'>JCP DBMS</span>](docker/dbms/README.md): service to store the AAA and the JCP databases
* [<span style='opacity: 100%'>JCP Auth</span>](docker/auth/README.md): service to register and manage objects, services and users
* [<span style='opacity: 60%'>JCP Commons</span>](libs/commons/README.md)
* [<span style='opacity: 60%'>JCP Service</span>](libs/service/README.md)
* [<span style='opacity: 60%'>JCP DB</span>](libs/db/README.md)
    * [<span style='opacity: 60%'>JCP DB APIs</span>](libs/db/apis/README.md)
    * [<span style='opacity: 60%'>JCP DB Gateways</span>](libs/db/gws/README.md)
    * [<span style='opacity: 60%'>JCP DB Frontend</span>](libs/db/fe/README.md)

