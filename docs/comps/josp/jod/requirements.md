# JOSP JOD Requirements

The JOD Agent is provided as a Java application, so **the only requirements is
to have the JVM installed** on the system where you would run the JOD Agent.

To set up your target device to run the JOD Agent, please follow instructions
relative to your target's operating system.

**Linux:** <br/>
Java Runtime Environment (preferred java version 8)<br/>
<code>sudo apt install default-jre</code><br/>
Alternatively you can check this links:
<ul>
<li><a href="https://ubuntu.com/tutorials/install-jre#1-overview">Install Java on Ubuntu</a></li>
<li><a href="https://phoenixnap.com/kb/install-java-on-centos">Install Java on CentOS</a></li>
<li><a href="https://www.java.com/it/download/help/linux_x64_install.html">Install Java 8 on Linux x64 (java.com)</a></li>
</ul>

**MacOS:** <br/>
Java Runtime Environment (preferred java version 8)<br/>
Download and install it from <a href="https://www.java.com/download/ie_manual.jsp">java.com</a><br/>
Alternatively you can check this links:
<ul>
<li><a href="https://mkyong.com/java/how-to-install-java-on-mac-osx/">Install Java via Homebrew</a></li>
<li><a href="https://adoptopenjdk.net/archive.html?variant=openjdk8&jvmVariant=hotspot">Install Java via AdoptOpenJDK</a></li>
<li><a href="https://java.com/en/download/apple.jsp">Install Java 8 on Mac OS (java.com)</a></li>
</ul>

**Windows:** <br/>
Java Runtime Environment (preferred java version 8)<br/>
Download and install it from <a href="https://www.java.com/download/ie_manual.jsp">java.com</a><br/>
<ul>
<li>
Enable powershell scripts execution<br/>
<code>Set-ExecutionPolicy -ExecutionPolicy remotesigned -Scope CurrentUser</code>
(More info at <a href="https:/go.microsoft.com/fwlink/?LinkID=135170">microsoft</a>)
</li>
</ul>


More requirements can be added by JOD Agent configurations such as a valid internet
connection if the [JCP](/docs/comps/jcp/README.md) connection was enabled (it is
by default) or the permission to use specific port for [JOD Local Server](comm.md#direct-communication)
(many systems require the admin permission to opn a port <1024).

Extra requirements can be added also by object's structure definition and [Worker](specs/workers.md)
configs. For example if you use the [File Listener](workers/listener_file.md)
worker on a protected file.

**All those extra requirements don't halt the JOD Agent startup.** At least they
print a warning message to the logs.
