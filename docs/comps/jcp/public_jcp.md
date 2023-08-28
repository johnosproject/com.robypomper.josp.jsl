# Public JCP

For an [End User](/docs/actors/end_users.md) is pretty easy create a
customized IoT solutions. He can choose between multiple JOSP Services and
interact with many other JOSP Objects.

But what happen if the end user require the JOSP cloud component (the JCP
instance) to run his own solutions?<br/>
**He can use the [Public John Cloud Platform](https://www.johnosproject.org/frontend)**.
A public JCP instance where anyone can connect all his objects and services.<br/>
It's simple as create a new user account and register your objects. Then, the end
users can log in to JOSP Service using the previously created account, so they can
interact with their JOSP objects .

Also, when EndUser would share their JOSP Objects with other users (family/friends/
colleagues), the Public JCP simplify this process. From JOSP Object's owner account,
add the right permission to the object to allow other user access it.

By default, all JOSP Objects (as JOD Distributions) and all JOSP Services connect
to the Public John Cloud Platform. So user are facilitated to create even more
complex and customized IoT solution and reuse JOSP components among them.

Moreover, as JCP instance it provide a simple **object's management service**
based on the JCP Front End microservice.<br/>
With this web dashboard, users can:
* register new objects
* list their own or shared objects and interact with them
* get object's events and status's histories
* manage objet's permission and share with other users

With the aim **to help Makers and Developers test their object's and service in
a real working environment**, we provide also a second JCP instance: the
[Stage John Cloud Platform](https://www-stage.johnosproject.org/frontend).
This instance can be used to test development objects and services, Makers and
Developers can use test credentials to connect their objects/services to the
Stage JCP and test them in a full working environment.

## Production environment

| JCP Url                                                                             | Description                                                          |
|-------------------------------------------------------------------------------------|----------------------------------------------------------------------|
| [auth.johnosproject.org](https://auth.johnosproject.org/auth/admin/master/console/) | Url for [JCP Auth](docker/auth/README.md) micro-service              |
| [apis.johnosproject.org](https://apis.johnosproject.org/swagger-ui.html)            | Url for [JCP APIs](core/apis/README.md) micro-service                |
| [jslwb.johnosproject.org](https://jslwb.johnosproject.org/swagger-ui.html)          | Url for [JCP JSL Web Bridge](core/jslwb/README.md) micro-service     |
| [www.johnosproject.org/frontend](https://www.johnosproject.org/frontend)            | Url for [JCP Front End](core/fe/README.md) micro-service             |
| [www.johnosproject.org/docs](https://www.johnosproject.org/docs)                    | Url for JOSP Docs @ [JCP Front End](core/fe/README.md) micro-service |

**Credentials**

To publish your JOSP Object or JOSP Service to the Public JCP, please send an
email at [tech@johnosproject.com](mailto:tech@johnosproject.com) to request
personalized client id and secret for your objects or services.

## Stage environment

| JCP Url                                                                                         | Description                                                          |
|-------------------------------------------------------------------------------------------------|----------------------------------------------------------------------|
| [auth-stage.johnosproject.org](https://auth-stage.johnosproject.org/auth/admin/master/console/) | Url for [JCP Auth](docker/auth/README.md) micro-service              |
| [apis-stage.johnosproject.org](https://apis-stage.johnosproject.org/swagger-ui.html)            | Url for [JCP APIs](core/apis/README.md) micro-service                |
| [jslwb-stage.johnosproject.org](https://jslwb-stage.johnosproject.org/swagger-ui.html)          | Url for [JCP JSL Web Bridge](core/jslwb/README.md) micro-service     |
| [www-stage.johnosproject.org/frontend](https://www-stage.johnosproject.org/frontend)            | Url for [JCP Front End](core/fe/README.md) micro-service             |
| [www-stage.johnosproject.org/docs](https://www-stage.johnosproject.org/docs)                    | Url for JOSP Docs @ [JCP Front End](core/fe/README.md) micro-service |

**Credentials**

| Client Id             | Client Secret                              |
|-----------------------|--------------------------------------------|
| ```test-client-obj``` | ```2d1f9b96-70d3-443b-b21b-08a401ddc16c``` |
| ```test-client-srv``` | ```88bc8baf-c924-42b9-b691-b2d7c5cec696``` |
| ```jcp-swagger```     | ```cf319f62-5275-4092-b346-65448535748d``` |
