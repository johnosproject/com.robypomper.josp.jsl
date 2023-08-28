# How to run a JOD Distribution

The JOD Agent can be distributed as part of a JOD Distribution.
The distribution contains also all firmware configs and scripts required to
represent the JOSP Object correctly.

Once you copied the JOD Distribution files in the running machine, you can
execute it using the [JOD TMPL Distribution scripts](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.template/src/master/docs/references#jod-template-distribution-scripts).
The running machine depends on [JOD Object nature](1_what_object_represent.md).<br/>
The JOD Distribution can be also installed as a service/daemon on running machine's 
operating system. So, the JOD Agent will be executed on each machine's boot.

---

1. ### Check JOD Distribution requirements

Each JOD Distribution can require specific software and hardware configs.
Before install a JOD Distribution, please check is requirements from his
documentation and from generic [JOD Distribution requirements page](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.template/src/master/docs/how_to_use_a_jod_distribution/requirements).

2. ### Download and extract the JOD Distribution's files

You can download many distributions directly at official [JOD Distributions list](/docs/comps/jod_distributions.md).
Depending on object's nature and where the JOD Agent must run, you can [download the JOD Distribution](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.template/src/master/docs/how_to_use_a_jod_distribution/get_a_jod_distribution)
on target machine.

3. ### Optional distribution configs

Read at JOD Distribution's docs for [optional configs](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.template/src/master/docs/how_to_use_a_jod_distribution/configure_a_jod_distribution).
Depending on object represented the distribution can require different
configuration like the gateway address, or other properties specified in the
distribution documentation.

4. ### Start the JOD Distribution

When everything is ready, you can start the JOD Agent.<br/>
The JOD Distribution comes with a set of commands (bash or powershell scripts)
that help you manage the JOD Agent (start/stop and query his status).

[Start the JOD Distribution](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.template/src/master/docs/how_to_use_a_jod_distribution/run_a_jod_distribution)
with the start command.

**NB!:** you can run only one JOD Agent instance at time for each folder.

5. ### (Optionally) Install the JOD Distribution as service/daemon

Commonly, JOD Distributions are installed on local servers or remote devices.
To configure those devices to start the JOD Agent each time they boot, you
must install it as a service/daemon on device's operating system.

[Install the JOD Distribution](https://bitbucket.org/johnosproject_shared/com.robypomper.josp.jod.template/src/master/docs/how_to_use_a_jod_distribution/install_a_jod_distribution_as_a_service)
as service/daemon with the start install.


---

⏩ Once the JOD Agent is started, you can [register represented JOSP Object](4_register_object.md) into your JOSP EcoSystem.
