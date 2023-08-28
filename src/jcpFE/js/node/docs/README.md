# JCP Front End (static)

This [Node.js](https://nodejs.org) project provide the client side of JCP Front End project.

This project can be executed as node.js server or builded and included in the super project JCP Front End that provide
the corresponding JCP service. The node.js server is used during the JCP Front End development for local testing
purposes.

To run the node.js server run following commands:

```shell
// from JCP Front End Static dir as direct npm command
$ cd {JCP_FE_STATIC_PROJECT}
$ npm install          // only on first run, required to download all node's dependencies
$ npm start

// Or from JOSP Project main dir as gradle task:
$ cd {JCP_FE_PROJECT}
$ ./gradlew nodeJcpFERun
```

To build and run the JCP Front End service run following commands:

```shell
$ cd {JCP_FE_PROJECT}
$ ./gradlew jcpFE_Start
```

## Project structure

This project is compose by 3 Javascript modules and node.js configurations.

* [jcp-commons](jcp-commons/README.md): definitions, utils and conventions for Javascript
* [jcp-jsl-wb](jcp-jsl-wb/README.md): JCP JSL Web bridge API's layer for Javascript. It includes local Services and
  APIEntities to interact with JSL Web Bridge's API and sync local data representations.
* [jcp-fe](jcp-fe/README.md): based on ```jcp-jsl-wb``` module and provide React components for JSL Web Bridge's API's
  entities like Objects, User...

The ```jcp-jsl-wb``` and ```jcp-fe``` modules acts as MVC framework to manage and represent the JOSP entities in a
Javascript project. The first module provides the Model and the Controller components of the framework. The data Model
is represented by API Entities classes. Each one represent a JOSP Entity such as Object, User, Service, Permission and
so on... ```jcp-jsl-wb```'s services act as Controller component because they are delegate to keep data Model up-to-date
synching it with the JCP JSL Web Bridge APIs (using SSE or API).<br>
To the other side the ```jcp-fe``` module provides the View component of the MVC framework with his React components.
This module use the
[React](https://reactjs.org/) library to render HTML components into a HTML page.

## Project entry point

Both servers (node.js and JCP service) provided by this project, when required, return
the [index.html](../public/index.html) file that contain a basic structure for a SinglePage Web App. Then
the [index.js](../src/index.js) load and render the [App](../src/jcp-fe/components/App/App.js) component
from ```jcp-fe``` module.
