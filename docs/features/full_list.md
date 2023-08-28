# Features List

The present document describes John O.S. features from Manufacturers, Developers
and Users perspectives. Any IoT Platform must provide the most complete support
to IoT Solutions development.

IoT Solutions will enable hundreds of new business models. To allow Manufacturers
and Developers offer new services they need a platform that provide:

* Integrated EcoSystem
* Development Tools
* Freedom of Choice
* Flexible Business Models
* Easy End Users Adoptions

## Main actors benefits

### End users

* **Plug&Play**

  JOSP Objects are ready to use out of the box. No configuration or online
  registration needed.

* **Service Independence**

  JOSP Objects can interact with any JOSP Service, giving the freedom to chose
  the original default service by the manufacturer and those independently
  developed.

* **JOSP UniversalApp**

  The generic service (as Mobile and Web app) that allow users to interact with
  Objects. The Web version provide tools to manage objects and theirs permissions;
  the Mobile version make objects interactions easy and fast to eaverbody.

* **Remote access**

  Access to Object from any access to the Web.

* **System agnostic product selection**

  A JOSP Object can interact with any other system or brand smart object, leaving
  full freedom of choice.

* **JOSP services permission management**

  JOSP Object owners can select what service can interact with the object, and
  what level of information it can access, enabling a multi-layer privacy and
  security flexible management.

* **Sharing JOSP Objects**

  Access to JOSP Objects can be shared with other users. Access levels, rules and
  alerts can be easily adjusted.

* **JOSP Objects activity log**

  Users can access and manage activity log of objects or macro groups of objects
  (service, activity, time, geolocalization, user, stats, consumes...)

* **User notification system**

  John O.S. platform provides a notification system managing data from the object,
  the service, and the platform itself.

---

### Manufacturer

* **No coding**
  
  No need to write a single line of code to create a JOSP object.

* **XMLRemote Object Definition**

  XMLRemote files allow defining any type of object (by its commands and infos).

  No profiles to adopt means flexibility in building product/feature focused solutions

* **Meta-Objects**

  Flexible models of JOSP Objects, enabling manufacturers to build complex objects
  and developers to identify capabilities/objects and build services.

* **Hierarchical Definitions**

  JOSP Object’s definition can be layered and use more Meta-Objects contemporary

* **Mutable Objects**

  JOSP Objects can change definition as its features change. New capabilities
  immediately available to services for immediate use.

* **I18n Objects**

  Within JOSP Objects definition information as scale, currency or text available
  at user’s level. JOHN O.S. platforms supports translation, conversion, for full
  localization

* **XMLRemote Builder**

  A drag and drop intuitive and simple tool to create JOSP Objects in minutes.

* **Maximum Hardware compatibility and minimum requirements**

  Any prototyping board or computer is supported like any industrial processor.

  Minimum HW needed to enable communication between objects and services.

* **Gateway as Objects**

  Smart industrial and home systems are based on getaways (hubs, web servers,
  etc..) that are easily integrated in the JOSP ecosystem as JOSP Objects thru
  a simple XMLRemote file.

* **Software as Objects**
  
  Not only physical objects and gateways can be integrated into the ecosystem.

  Any software can be integrated with the platform, i.e. the cloud service running
  a generic object, made available to JOSP Services as any other JOSP Object.

* **OTA Updates**

  Over The Air updates allow managing object’s definition, modifying or adding
  functionalities, as well as managing the JOSP Demon installed on the object.

  Manufacturers can plan or push updates and maintenance.

* **Logs and usage statistics**

  Manufacturers can request use statistics of the product installed base.

* **Analysis and aggregation of data**

  Data analysis and aggregation can be drilled by a wide range of variables

* **Remote maintenance (see OTA)**

  High-Level Customer Care with full control of remote objects.

---

### Developers

* **Any Software**

  A JOSP Service is any software integrating JOSP Service Library, and interacting 
  with a JOSP Object. JOSP Service Library is available for major programming
  languages, making simple to build any kind of services such as: Mobile Apps,
  PC exe, Web Services…

* **Compatibility with all connected objects**

  Developing with the JSL, develop software compatible with any existing
  JOSP Object. The JOSP Cloud bridges between JOSP Services and objects connected
  to the web through different platforms and technologies.

* **Object Search System brand agnostic**

  The system is designed to support object selection by feature, capability and 
  meta-model, regardless the brand or how it was developed.

* **Reduced number of API**

  No more than two hours are needed on average to learn the object research and
  the object interaction system. Ready to create any kind of interaction between 
  services and objects.

* **Search JOSP Objects (see Object Search System)**

  Identify Objects is made easy by the ObjSS. With his filter it’s possibile find
  objects by their name, model, brand, meta-object, info and commands; in addition
  to Position, Owner, Last used...

* **Virtual objects**

  JOHN O.S. platform allows creating Virtual JOSP Objects, running on PC and
  Mobile Apps, reproducing any available Meta-Object.

* **Full support for guest environment**

  **JSL support all OS guest capabilities, such as the User Interface stack
  (when available) providing buttons, sliders and all kind of stuff to
  graphically represent objects and interact with.

* **JOSP Cloud Service 

  JOSP Service Library includes the full access to all JOSP Cloud Service

* **Cloud independent**

  JOSP Services can interact with JOSP Objects independently from JOSP Cloud


* **Universal login**

  Only a Single Login is needed to access all personal JOSP Objects. Access
  management is run internally by JOSP Cloud.

* **Repository distribution**

  JOSP Service Library is available via repository both in development phase
  (Maven, Ivy, Rubygem..) and in execution phase (Apt, Yum, Alien…). No library
  update is needed.

---

## Generic features

### System

* **System interactions**

  John O.S. Project enables interaction between systems, being a system any object
  or service/software.

* **Communication abstraction**

  John  manages the interaction between systems, embracing the communication
  hardware, data exchange protocol, and error management.

* **Free**

  Creating JOSP Objects and JOSP Services is FoC.

  Access to value added services at cloud level require a fee.

* **Open Source**

  John O.S. Project code is open source, enabling any manufacturer or developer
  to adjust and improve the software. Several open source sw were used to build
  John O.S., securing constand update and quality.

* **Object&Service interoperability**

  Any object can interact with any service via the due authorization.

* **Simplicity**

  Compared to the majority of IoT platforms, John O.S. Project provides simple
  architecture based on three elements: Objects, Services, and Cloud (Cloud is
  optional). No software development skills are needed to create smart objects.
  Services creation is based on most common programming languages and tools.