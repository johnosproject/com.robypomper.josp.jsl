# How to register a JOSP Object to your EcoSystem

When the JOD Agent is running, it starts a JOSP Object.
That means other JOSP Services can see it and interact with it.

When the JOD Agent is executed for the first time, it exposes an **anonymous
JOSP Object**, an object without an owner (1). Anonymous objects, normally, are
not listed in object's list.

All [Johnny Services](/docs/comps/jsl_services.md#johnny-services) and many
more [JOSP Services](/docs/comps/jsl_services.md) allow you **register
anonymous objects** as your objects. Using those JOSP Services you can set
yourself as object's owner (2). Then the JOSP Object changes his owner, and it
changes also his id (3).

![JOSP Object registration process](josp_object_registration.png)

_Watch the [JOD PC Linux - Installation - 1.0](https://www.youtube.com/watch?v=9wPoEweUVJI)
video on YouTube._

If the JOSP Object can reach the John Cloud Platform, you can also use the
JCP Front End service to register a JOSP Object into your EcoSystem.<br/>
Otherwise, use one of the other [Johnny Services](/docs/comps/jsl_services.md#johnny-services)
to register objects locally.

_The object's registration process will ask you for the object's id.<br/>
Depending on JOSP Object nature, you can find it in different places.
If you run manually a JOD Distribution, you can find the object's id in the
agent's logs (or in the configs/jod.yml file). Otherwise, for JOSP Native Objects,
you can find the object id printed in his label (near the object's serial number)._

---

1. ### Open a JOSP Service

You can choose your favourite Johnny Service or another JOSP Service that support
object's registration.<br/>
If not, [login to the service](../6_user_account/3_login_and_out.md) with your JOSP
credentials.

2. ### Add new object

When on the service, go to the 'Object's list' and click on 'Add object' button.<br/>
Then appears a dialog where you can type/copy the object's id to register.
Past it and click on the 'Next' button.

3. ### Wait for object's registration

Now the JOSP Service send a 'set owner' message to the object.
Then the object, elaborate the request and restart his communication system.
Once the objects restarted the communication system, it can be available on all
your JOSP services.

4. ### Open registered object

If the registration process ends successfully, you can se the registered object
in the object's list of your JOSP Service.
Click on the object's name to see his details and structure.

---

⏩ Now you can [show object's structure](5_show_object_structure.md) and interact with it.