# JOSP JSL usage

The JSL Library is a software library that provide the ```JSL instance```, an
object that handle a JOSP Service session.

_Detailed instruction on how to include the JSL library and instantiate a JSL
object, can be found to the JSL implementation pages:

* [Java](langs/java.md#include)
* [HTTP APIs](langs/http.md#include)
* [JavaScript](langs/javascript.md#include)_

## JSL Instance

The main object provided by the JSL Library is the ```JSL Instance```. That instance
allows the developer to register a JOSP Service and manage his connections,
discover JOSP Objects, handle user's login/logout, etc...

Basically the JSL instance provide the access to all JOSP Services sub systems:

* [Objects Manager](apis/objs_mngr.md): list JOSP Objects and return [RemoteObject](apis/remote_object.md) instances to interact with
* [Communication](apis/comm.md): manage JOSP Service connection to the JOSP Objects and the JCP
* [User Manager](apis/user_mngr.md): handle current user profile
* [Service Info](apis/service_info.md): return current JOSP Service info
* [Admin](apis/admin.md): access to JCP Admin features (reserved for Management JOSP Service)

## JSL Remote Object

Each object discovered by a ```JSL instance``` is represented as a ```JSL Remote Object```.
Through that representation, developers can get JOSP Object's info and structure.

    § JSL Structure
    § JSL Component
    § JSL State
    § JSL Action

    All JOSP Object's info and representation are available to the JSL instance only
    the JSL instance's user 
    Once the right object / component was identify,  
    Object's structure is composed by Object's pillars, that are also represented
    to the developer as instances

## JSL Communication

...

    § JCP APIs client
    § cloud comm
    § direct comm

## JSL User Manager

...

    § login and logout current user
    § store auth token and auto-login
    § service-user and client credential flow

## JSL Service Info

...
    
    § info about current JOSP service

## JSL Admin Manager

...
 
    § mange and control John Cloud Platform
    § only for user with role=mng
