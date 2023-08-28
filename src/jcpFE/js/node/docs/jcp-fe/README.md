# JCP Front End for Javascript

The ```jcp-fe``` Javascript module provide the HTML components (based on the
[React](https://reactjs.org/) library) to display ```jcp-jsl-wb```'s API Entities and the Single Web Page components for
the JCP Front End service.

It's composed by following packages:

* [JCPFE](JCPFE.md): JCP Front End instance and class (inherits [JCPJSLWB](../jcp-jsl-wb/JCPJSLWB.md))
* Components
    * [components/Commons](components/Commons.md): Commons definitions, utils and conventions
    * [components/api-entities](components/api-entities.md): JCP JSL Web Bridge's API Entities components
    * [components/app](components/app.md): JCP Front End service's main app components
    * [components/base](components/base.md): component implementation base classes
* [Routers](routers/Routers.md): [ReactRouter](https://reactrouter.com) components for JCP Front End's Single Web Page
  routing