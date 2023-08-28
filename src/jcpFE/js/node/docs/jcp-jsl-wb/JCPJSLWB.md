# JCPJSLWB and JCPJSLWBClass

The [JCPJSLWBClass](../../src/jcp-jsl-wb/JCPJSLWB.js) instance is the entry point of this module. It act as single entry
point for all data cached locally and provide methods to interact with them.

It can be included as self-initialized instance directly in HTML components (like React component in the below example).
Or it can be extended using the
```JCPJSLWBClass``` like the ```JCPFEClass``` from [jcp-fe](../jcp-fe/README.md)
module.

## JCPJSLWB instance usage

The JCPJSLWB instance must be initialized once for each page and shared among all Javascript objects that require to
access to the JCP JSL Web Bridge API.

Here an example of ```JCPJSLWBClass``` initialization that use the same hostname of original HTTP request (normally
the [index.html](../../public/index.html)
page) as JCP JSL Web Bridge url. It can be also initialized without params or keep them null. Then
the ```JCPJSLWBClass``` constructor will query the JCP Front End API for the JCP JSL Web Bridge url. In development
environment this param can be customized to set a different JCP JSL Web Bridge service than the local one.

```javascript
import { JCPJSLWBClass } from './jcp-jsl-wb/JCPJSLWB'

const JCPJSLWB = new JCPJSLWBClass('https://' + window.location.hostname + ':9003');
export default JCPJSLWB;
```

Once initialized the JCPJSLWB instance can be passed to HTML components like a common Javascript object.

```javascript
import JCPJSLWB from './JCPJSLWBInstance';
import JCPJSLWBStatus from './jcp-jsl-wb/components/JCPJSLWBStatus';

ReactDOM.render(<JCPJSLWBStatus jcpjslwb={JCPJSLWB} />, document.getElementById(MAIN_ELEMENT));
```

Then it can be used within HTML component's code.

**Here a list of usage examples from ```jcp-fe``` module.**

* JCPJSLWB Init state and events:<br>
  display and hide JCPFEBackdrop in [App](../../src/jcp-fe/components/app/App.js) component
* List, search and interact with Objects:<br>
  the [Objects](../../src/jcp-fe/components/api-entities/Objects.js) component list objects
  and [Object](../../src/jcp-fe/components/api-entities/Object.js)
  component interact with them.
* Get User or Service data:<br>
  the [Service](../../src/jcp-fe/components/api-entities/Service.js) and
  [User](../../src/jcp-fe/components/api-entities/User.js) components shown Service and User info.
* Get Manager data (for future implementation)

## JCPJSLWBClass extension

The JCPJSLWB instance can be customized extending his base class ```JCPJSLWBClass```
and initialized with custom params like in the following example from
[JCPFE.js](../../src/jcp-fe/JCPFE.js) and [JCPFEInstance.js](../../src/JCPFEInstance.js)
files.

```javascript
import { JCPJSLWBClass } from '../jcp-jsl-wb/JCPJSLWB'

export class JCPFEClass extends JCPJSLWBClass {

    _urlFE = null;
    
    constructor(urlFE = null, urlJSLWB = null) {
        super(urlJSLWB);
        this._urlFE = urlFE ? urlFE : location.origin;
    }

}
```

## JCPJSLWB Logs

### Initialization

```
[ 15:15:33 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 1 Init URLs
[ 15:15:33 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 1.1 Set JSL WB url (via params)
[ 15:15:33 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 2 Init JSL
[ 15:15:33 04/05/2021 @ JCPJSLWB/JSL         ]  DEB: JSL Session status response: {"sessionId":"A1B096D6C4955F48246B348310B37B51","isJSLInit":false}
[ 15:15:33 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 2.2 Init JSL Session (via FE)
[ 15:15:33 04/05/2021 @ JCPJSLWB/JSL         ]  DEB: JSL Session initialized succesffully
[ 15:15:33 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 2.3 Init JSL Session (via JSL WB)
[ 15:15:34 04/05/2021 @ JCPJSLWB/JSL         ]  DEB: JSL Session DIRECT initialized succesffully
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 2.3 update JSL Session
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 3 Init JSL Systems
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 3.1 Init SSE Updater
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 3.2 Init GW Connection
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 3.3 Init User
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 3.4 Init Service
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 3.5 Init Objects
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 3.6 Init Manager
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 3 Start JSL Systems
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 3.3 User initialized
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 3.4 Service initialized
[ 15:15:34 04/05/2021 @ JCPJSLWB/SSE         ]  INF: Connected
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 3.1 SSE Updater connected
[ 15:15:34 04/05/2021 @ JCPJSLWB/SSE         ]  DEB: Received 'HB'
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 3.5 Objects initialized
[ 15:15:34 04/05/2021 @ JCPJSLWB/GW          ]  DEB: GW Connection isConnected: true
[ 15:15:34 04/05/2021 @ JCPJSLWB/StartUp     ]  INF: 3.2 GW Connection connected
```

### Object Added

```
[ 15:15:35 04/05/2021 @ JCPJSLWB/SSE         ]  INF: Received 'OBJ_ADD' on QGASL-CIZKX-QGUBY
[ 15:15:35 04/05/2021 @ JCPJSLWB/Objects     ]  DEB: Added 'QGASL-CIZKX-QGUBY' object, fetching...
[ 15:15:35 04/05/2021 @ JCPJSLWB/SSE         ]  DEB: Received 'OBJ_UPD_INFO_NAME' on QGASL-CIZKX-QGUBY value = Blood_orange_13
[ 15:15:35 04/05/2021 @ JCPJSLWB/SSE         ]  DEB: Received 'OBJ_UPD_INFO_OWNER' on QGASL-CIZKX-QGUBY value = 00000-00000-00000
[ 15:15:35 04/05/2021 @ JCPJSLWB/SSE         ]  DEB: Received 'OBJ_UPD_INFO_JOD_VERSION' on QGASL-CIZKX-QGUBY value = 2.0.0
[ 15:15:35 04/05/2021 @ JCPJSLWB/SSE         ]  DEB: Received 'OBJ_UPD_INFO_MODEL' on QGASL-CIZKX-QGUBY value = Development JOSP Object
[ 15:15:35 04/05/2021 @ JCPJSLWB/SSE         ]  DEB: Received 'OBJ_UPD_INFO_BRAND' on QGASL-CIZKX-QGUBY value = John OS
[ 15:15:35 04/05/2021 @ JCPJSLWB/SSE         ]  DEB: Received 'OBJ_UPD_INFO_LONG_DESCR' on QGASL-CIZKX-QGUBY value = A Linux computer ...
[ 15:15:35 04/05/2021 @ JCPJSLWB/SSE         ]  INF: Received 'OBJ_UPD_STRUCT' on QGASL-CIZKX-QGUBY
[ 15:15:35 04/05/2021 @ JCPJSLWB/SSE         ]  INF: Received 'OBJ_UPD_PERMS' on QGASL-CIZKX-QGUBY
[ 15:15:35 04/05/2021 @ JCPJSLWB/SSE         ]  INF: Received 'OBJ_UPD_PERM_SRV' on QGASL-CIZKX-QGUBY value = CoOwner
[ 15:15:35 04/05/2021 @ JCPJSLWB/SSE         ]  INF: Received 'OBJ_DISCONN' on QGASL-CIZKX-QGUBY
[ 15:15:35 04/05/2021 @ JCPJSLWB/Objects     ]  DEB: Object 'QGASL-CIZKX-QGUBY' fetched successfully
[ 15:15:35 04/05/2021 @ JCPJSLWB/Objects     ]  INF: Object 'QGASL-CIZKX-QGUBY' added and fetched successfully.
```

Can be throw some warning message
like ```Can't fetch object's permissions because object 'XXXXX-YYYYY-ZZZZZ' not yet initialized. [IGNORED]```.
