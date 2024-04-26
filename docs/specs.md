# JOSP Service Library - Specifications

[README](../README.md) | [SPECS](specs.md) | [GUIDES](guides.md) | [CHANGELOG](../CHANGELOG.md) | [TODOs](../TODOs.md) | [LICENCE](../LICENCE.md)

* [JSL API](specs/api.md)
* [JSL API Admin](specs/api_admin.md)

## Service Info
* [jsl.yml](specs/jsl_yml.md)
* [Service ID](specs/service_id.md)
* Service Info fields: Name, InstanceId, FullId, Connected/Known Objects -> Move to ObjsMngr; [User](#user), isUserLogged, UserId, Username -> Move to user
* [JSL Info](specs/jsl-info.md)

## User
* Current User and roles
* Login/out using the JCPAPIsClientSrv

## Communication
* [JOSP Commons :: JOSP Protocol](josp_comps/josp_commons_josp_protocol.md)
* [Direct and Cloud Communication](specs/communication.md)
  * [Local Communication](specs/communication_local.md)
  * [Cloud Communication](specs/communication_cloud.md)
* [JOSP Commons :: Discovery System](josp_comps/josp_commons_discovery.md)
* [JOSP Commons :: Security Levels](josp_comps/josp_commons_josp_communication_securitylevels.md)
* [Authentication](specs/auth.md)

## Remote Objects
* [Objects ID and their structures](specs/objects.md)
* Objects manager
* Remote Object's info, permissions and communication
* Remote Object's structure, states and actions
* Remote Object's events and states' histories

## JSL Shell
* [CmdLine and Args](specs/cmdline.md)
* [JOD Shell](specs/shell.md)

## Logging
* [Logs](specs/logs.md)
