# Makers

A JOSP Object, at least, should be configured by **someone that define object's
info and structure**.
In the JOSP Project this is the Maker.

* [Makers' manual](../manuals/makers/INDEX.md)

Any JOSP Object require a basic configuration before starting.
This configuration includes the object's structure, a file that define all features
exposed by the object.
Whoever configures this file is considered **the Maker of that JOSP Object**.
Once the maker has finished configuring the object, he can start distributing it.
There are two mainly way to distribute JOSP Objects:

* as physical objects:<br/>
  Connected objects that run the JOD agent.
  When plugged in, those objects start up the JOD agent to expose their feature to the JOSP EcoSystem
* as JOD Distributions:<br/>
  JOD distributions are compressed archives that contain all files required to install and run a pre-configured JOD Agent.
  Commonly are used for systems integrations.
  JOD distributions can be installed on a PC (or better on a local server) that can communicate with the system to integrate (gateway, hub, proprietary protocols...).

_\* the JOD agent is the software that expose the JOSP Object to the EcoSystem._

All JOD Distribution are configured to use specific firmware's tools (the object's
structure define also the list of the firmware calls). Because of that, **each
JOD Distribution come with his specific requirements**.<br/>
Sometimes those requirements are only software like libraries, tools or special
configs. Other times, distribution's requirements are also about hardware. For
example a distribution can require a specific gateway reachable on the local network.

Also, the JOD agent installed on **physical objects, can come from a JOD Distribution**.
In this case the Maker design and make the object (physically, connecting sensors
and actuators). Then he prepares a JOD Distribution to install and run on the
physical object.<br/>
This distribution will be configured to expose to the EcoSystem, certain object's
features, echo of them connected to the specific hardware mounted on the object.

Often, for JOSP Objects provided as physical objects, the **object's maker
correspond with the product's manufacturer**.

Any maker, whether amateur or professional, shares those needs with other Makers:

* Make connected object easily and fast<br/>
  In recent years, new tools reduced the time for prototyping electronic circuits.
  Configuring the software layer should also be a matter of little time, easy to learn and as self-configuring as possible.<br/>
  Fast prototyping has become crucial for today's business plans to reduce the time-to-market for a connected product.
* Be free to choose hardware components according to their needs<br/>
  Each objects expose his own features and each object must withstand the conditions of its working environment.
  Because of that, each objects requires different hardware components.
  Makers must be free to design their objects using any hardware component available.
* Not be bound by limits of the tools used<br/>
  In an IoT solution, often, are involved dozens of different service and tools.
  Each of them with their own role and limitations.<br/>
  It must never happen that, once the project has begun, we realize that some tool does not allow us to do what we hoped for.
* Keep component costs and energy consumption as low as possible<br/>
  A well-designed object keeps production costs low and when active it reduces its energy consumption to a minimum.
* Being able to forget about the IT infrastructure<br/>
  Makers are not often experienced computer technicians.
  Indeed, they usually have little more than basic IT knowledge.<br/>
  At the same time, when the maker is an enterprise that sell connected products, often they don't have the skills or willingness to manage internally an IT infrastructure for their IoT solutions.
  So, normally they opt for a managed IoT infrastructure.
* Provide at least one out-of-the-box IoT solution<br/>
  A connected object without an IoT service is almost useless.
  Makers, and manufacturers, together with the object, also provides at least an IoT Service to interact with their products.<br/>
  Depending on Makers intentions, this IoT Service can be a mobile app for connected object monitor and control, a voice assistant command, a web dashboard with objects stats and reports...
