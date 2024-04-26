# JSL TODOs

[README](README.md) | [SPECS](docs/specs.md) | [GUIDES](docs/guides.md) | [CHANGELOG](CHANGELOG.md) | [TODOs](TODOs.md) | [LICENCE](LICENCE.md)

* Communication
  * Add local comm methods to JSLCommunication, like the JODCommunication class
  * Update the JCP O2S GWs to support manually generated certificates
  * Add configurations to load the accepted servers' certificates for the JSLLocalClientsMngr
  * Move backup connections from DefaultObjComm to JSLLocalClientsMngr
  * JSLLocalClientsMngr must give also discSrv.intf to the JSLLocalClientSSLShare constructor
  * Replace discSrv.name with remoteObjId into JSLLocalClientsMngr::processDiscovered()
  * Fix missing backup clients in JSLLocalClientsMngr::processOnDisconnected()
  * Analyze the error and print adeguate logging message in JSLLocalClientsMngr::processOnFail()
* Remote Object
  * Check DefaultJSLComponentPath constructor behaviour with not Unique path (focus on ...>* paths)
* User
  * Implements user's specific settings (local/cloud)
  * Implements user's related to current service settings (local/cloud)
* Others
  * Convert publicXYListeners into maps, to allow registrations specific for object's id
* Docs
  * Document all Java classes and public fields
  * Update the "JOSP Service Library - Specs: API" page: short description list and describe JSL public API
  * Update the "JOSP Service Library - Specs: API Admin" page: short description list and describe JSL public API for Admin
  * Update the "JOSP Service Library - Specs: jsl.yml" page: short description and effect for each property
  * Write the "JOSP Service Library - Specs: Service ID" page: describe the relationships between the full, service, user and instance ids
  * Write the "JOSP Service Library - Specs: Communication Cloud" page: describe how cloud communication works and how to configure it.
  * Write the "JOSP Service Library - Specs: Authentication" page: describe how the authentication works and how to configure it.
  * Write the "JOSP Service Library - Specs: CmdLine" page: describe JSLShell's command line args
  * Write the "JOSP Service Library - Specs: Shell" page: list and describe JSLShell's commands
  * Write the "JOSP Service Library - Guide: Objects Mngm" page
  * Write the "JOSP Service Library - Guide: Object access" page
  * Create and write the "JOSP Service Library - Specs: Service Info fields: " page: list all available fields and describe their values Name, InstanceId, FullId, Connected/Known Objects -> Move to ObjsMngr; [User](#user), isUserLogged, UserId, Username -> Move to user
  * Create and write the "JOSP Service Library - Specs: User: " page: describe isUserAuthenticated, isAdmin, isMaker, isDeveloper, UserId, Username
  * Create and write the "JOSP Service Library - Specs: User Login/out: " page: describe how to login/out using the JCPAPIsClientSrv
  * Create and write the "JOSP Service Library - Specs: Objects Manager: " page: describe how looking for objects or listen for them
  * Create and write the "JOSP Service Library - Specs: Remote Object Info: " page: describe Remote Object's info, permissions and communication
  * Create and write the "JOSP Service Library - Specs: Remote Object Structure: " page: describe Remote Object's structure, states and actions
  * Create and write the "JOSP Service Library - Specs: Remote Object History: " page: describe Remote Object's events and states' histories
  * Create and write the "JOSP Service Library - Guide: JSL Service: " page
  * Create and write the "JOSP Service Library - Guide: User Management: " page
  * Create and write the "JOSP Service Library - Guide: Communication: " page
  * Create and write the "JOSP Service Library - Guide: JOSP Discovery: " page
  * Create and write the "JOSP Service Library - Guide: Remote Object States: " page
  * Create and write the "JOSP Service Library - Guide: Remote Object Actions: " page
  * Create and write the "JOSP Service Library - Guide: Remote Object History: " page
  * Create and write the "JOSP Service Library - Guide: Remote Object Configs and Permissions/: " page
  * Move the "JOSP Service Library - Specs: Service ID" page to the JOSP Protocol repository
