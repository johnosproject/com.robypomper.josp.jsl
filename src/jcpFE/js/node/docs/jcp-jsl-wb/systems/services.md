# JCP JSL Web Bridge Services

To be fully operative, the ```jcp-jsl-wb``` module depends on different factors:

* Basic urls: base url for JCP JSL Web Bridge service
* [JSLSession](../../../src/jcp-jsl-wb/systems/services/JSLSession.js): JSL session dedicated to current client on JCP
  JSL Web Bridge service
* [GWConnection](../../../src/jcp-jsl-wb/systems/services/GWConnection.js): the connection state between the JSL session
  on the JSL Web Bridge service and the corresponding JCP Gateway
* [SSEUpdater](../../../src/jcp-jsl-wb/systems/services/SSEUpdater.js): updates channels from JSL session on JCP Web
  Bridge service to current client

This package provides all Javascript services to check, to init and to keep updated the [API Entities](api-entities.md).
