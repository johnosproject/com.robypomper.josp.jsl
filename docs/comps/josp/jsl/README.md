# JOSP JSL

<table><tr>
<td>
<img src="JOSP_JSL_Logo_250.png" width="200">
</td>
<td>
The <b>John Service Library is a collection of libraries to interact with
<a src="/docs/features/object_integration.md">JOSP Object</a> via the <a src="/docs/features/ecosystem.md)">JOSP EcoSystem</a></b>.
Those libraries can be included in 3rd party software, And each software that
include this library become a <a src="/docs/features/service_integration.md">JOSP Services</a>.
</td>
</tr></table>

**NB!**: depending on your programming language, the JSL library can be available
to corresponding package manager.

---

**Component Info:** <br/>
Current version: 2.2.2 <br/>
Development version: 2.3.0-DEV <br/>
Docs: [JCP @ JOSP Docs](README.md) <br/>
Repo: [com.robypomper.josp @ Bitbucket](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/) <br/>
Downloads: [com.robypomper.josp > Downloads @ Bitbucket](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/downloads/)

---

## Sub-pages

* [Tasks groups](tasks_groups.md)
* [Requirements](requirements.md)
* [Usage](usage.md)
* [Communication](comm.md)

**Specs** <br/>
* [Configs](specs/configs.md)
* [Shell](specs/shell.md)
* [Shell JCP Admin](specs/shell_admin.md)

**APIs** <br/>
* [JSL](apis/jsl.md)
* [Objects Manager](apis/objs_mngr.md)
  * [Remote Object](apis/remote_object.md)
    * [Pillars](apis/pillars.md)
* [Communication](apis/comm.md)
* [User Manager](apis/user_mngr.md)
* [Service Info](apis/service_info.md)
* [JCP Admin](apis/admin.md)

**Implementations** <br/>
* [Java](langs/java.md)
* [HTTP](langs/http.md)
* [JavaScript](langs/javascript.md)


# JOSP Service Library

The JSL library provides the ability to interact with the JOSP EcoSystem. For example to list available objects and interact with them.
This library provide also methods to check objects and cloud communication state, but also to handle user login/logout. So, everything you need to interact with a JOSP EcoSystem from you own software.

At this moment, this library is provided in following languages:
* [Java](langs/java.md) (stable)
* [Http](langs/http.md) (stable)
* [JavaScript](langs/javascript.md) (under dev)

Checkout the language's specifications for detailed requirements and installation instructions.

Once initialize, the JSL library provide you an instance of the [JSL](apis/jsl.md) object. From this object, you can access to different sub-systems:
* [Objects Manager](apis/objs_mngr.md): list JOSP Objects and return [RemoteObject](apis/remote_object.md) instances to interact with
* [Communication](apis/comm.md): manage JOSP Service connection to the JOSP Objects and the JCP
* [User Manager](apis/user_mngr.md): handle current user profile
* [Service Info](apis/service_info.md): return current JOSP Service info
* [Admin](apis/admin.md): access to JCP Admin features (reserved for Management JOSP Service)

The JSL library provide also the [JSL Shell](specs/shell.md). An interactive shell used to test environments and objects via command line interface.
