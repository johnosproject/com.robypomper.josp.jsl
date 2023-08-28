# Object integration

A connected object allow network services access his features. Into the JOSP
Project a connected object is represented by the JOD Agent and his features can
be used by any JOSP Service.

A JOSP Objects can be configured to represent any kind of object, regardless the
tecnology used to connect it. It can be also configured to represent custom objects
such as Native JOSP Objects. Purchasable products or maker's projects that come
with a pre-installed JOD Agent.

## Usage from a Service

A JOSP Object, from [JOSP Services](service_integration.md) perspective is a set
of structured features represented by [JOSP Pillars](pillars.md). This structure, plus a set
of info like id, name, model, etc..., allows services identify objects and
differentiate them by their capabilities.<br/>
Object's features exposed to the [JOSP EcoSystem](ecosystem) allow services to
interact with the object. Depending on the feature, services can query a status
value or request to execute an action to connected objects.

It's really important how an object expose his features to the JOSP EcoSystem.
If it contains common features, it can be found and used by many JOSP Service;
but if the object result to generic it is discarded in preference to other more
specific objects.

**JOSP Objects info and structure are defined to the [Maker](/docs/actors/makers.md).**
Maker is who configure the JOD Agent, the software that expose JOSP Objects to
the EcoSystem.<br/>
Typically Maker is the manufacturer of the product or, the maker that designed
the connected object. Other times, JOSP Objects are distributed as [JOD Distributions](/docs/comps/jod_distributions.md),
downloadable archives that contain all files to run a JOSP Object. Those JOD
Distributions can be even customized by end users to adapt them to their hardware setup.<br/>
Starting point to create a new JOD Distribution is the [JOD Dist TMPL](/docs/comps/tools.md)
project that provide a directory to use as template for customizable JOD Distributions.

**[End Users](/docs/actors/end_users.md) run a JOSP Object** each time they plug in the
electrical outlet of a Native JOSP Object, or each time they start up a JOD Distribution.
Once executed, a JOSP Object continues to run 24/7 and can be used in any
[JOSP Solution](iot_solutions.md).

---

## What is a JOSP Object?

A JOSP Objects is the representation of a connected object in the JOSP EcoSystem.<br/>
The communication between the JOSP EcoSystem and the represented objects is
delegated to the JOD Agent. This is a software that can be executed on embedded
devices, on personal computers or on servers, depending on the object to represent.
This agent manages object's communications, permissions and translate the JOSP Protocol
messages from/to JOSP Services and represented object.

### Exported features

### Local or cloud objects

---

## Multi-level integration

### Native JOSP Objects

### Hub JOSP Objects

### Web JOSP Objects

---

## Where find JOSP Objects?

## JOD Agent and JOD Distributions

### JOSP Object's stack


