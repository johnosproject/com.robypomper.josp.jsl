# JSL - Specs: Encryption

[README](../../README.md) | [SPECS](../specs.md) | [GUIDES](../guides.md) | [CHANGELOG](../../CHANGELOG.md) | [TODOs](../../TODOs.md) | [LICENCE](../../LICENCE.md)

With the intention of **providing the highest level of security**, both the
JCP Gateway S2O Client and the JSL Local Client use communication channels
based on TCP and encrypted with SSL. For Cloud communication, the SSL
encryption use a pre-shared certificate on the JCP GWs server side; and a
self-generated certificate as JOSP Object's identity.
Each JOSP Service, register his self-generated certificate to the JCP when requires
the JCP Gateways' access info to JCP APIs. On other hand, the Direct communication
requires a pre-connection step where JOSP Object and Service share their own
certificates. After that, they can open a direct encrypted communication.

Also, the Public JCP exposes only HTTPs endpoints and encrypt all communication
with his own SSL certificate.
