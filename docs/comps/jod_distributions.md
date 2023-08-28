# JOD Distributions

## John Object Daemon Distributions

The [JOD Distribution TEMPLATE](/docs/comps/tools.md) is
the main tool to create a new JOD Distribution.<br/>
That's necessary only if you can't find a distribution that fits your needs on
the bellow lists.

More distribution will be available soon, come back and check again.<br/>
To publish your own distribution on this list, please contact us at
[tech@johnosproject.com](mailto:tech@johnosproject.com).


## Real physical objects

* [JOD PC Linux](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.pc.linux/src/master/): Represents a Linux computer as a JOSP Object.
* [JOD PC macOS](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.pc.mac/src/master/): Represents a macOS computer as a JOSP Object.
* [JOD PC Windows](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.pc.windows/src/master/): Represents a Windows computer as a JOSP Object.
* JOD SmartPowerStrip: A connected power strip that represent a set of On/Off and Dimmerable controls as a JOSP Object.
* JOD Aladino Kit: A windows, doors and gates smart device that represent a window, door or gate as a JOSP Object.

---

## Protocols / Gateways / Etc

* [JOD Philips Hue](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.philips_hue/src/master/): Represents a local Philips Hue Hub and all connected lights and switches as a single JOSP Object.
* [JOD MBus](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.mbus/src/master/): Represents a single or multiple MBus Slave devices as a JOSP Object, slaves must be connected with a MBus Master reachable via serial communication.
* JOD MQTT: The base distribution for MQTT based objects. It represents MQTT's topics in a user-configured object's structure as a different JOSP Objects. End user can customize this distribution to integrate their own objects connected via MQTT. Then those objects can be shared again as a JOD Distributions.
* JOD Matter: ...
* JOD ZigBee: ...
* JOD OPC-UA: ...

---

## Web and Cloud

* JOD Web Object: The base distribution for Web based objects. It represents a set of urls in a user-configured object's structure as a different JOSP Objects. End user can customize this distribution and create their own JOSP objects interacting with web pages, APIs or other cloud based services. Then those objects can be shared again as a JOD Distributions.
* [JOD Meteo Web](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.meteo_web/src/master/): Represents a meteo station that query his values to <a href="https://openweathermap.org/">OpenWhetherMap</a>'s APIs as a JOSP Object.
* [JOD Alto Adige OpenDataHub](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.aa_odh/src/master/): Represents <a href="https://opendatahub.bz.it/">AltoAdige OpenData Hub</a> Dataset as JOSP Objects.
