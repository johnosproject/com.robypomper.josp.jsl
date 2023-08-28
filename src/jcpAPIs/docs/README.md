# jcpApis

This project provide the **JCP API's service**.

The JCP API's (aka jcpAPIs) are the main API for the JOSP Cloud Platform and
support Objects, Services and Users from JOSP Eco-System.


## Status

Now it implements a Spring Boot application responding on port 9001 and
expose 2 groups of endpoints:

Methods:
* [https://localhost:9001/apis/str](): test String as return type
* [https://localhost:9001/apis/int](): test int as return type

DBEntity:
* [https://localhost:9001/apis/db](): full list of usernames and ids
* [https://localhost:9001/apis/db/{id}](): username (as Entity) of specified id
* [https://localhost:9001/apis/db/{id}/username](): username (as String) of specified id
* [https://localhost:9001/apis/db/add?username=pippo](): add new username with string 'pippo'


## Development steps

### Spring Boot service
Spring Boot services require only a @SpringBootApplication class, then it scan
other classes looking for Spring's beans: configurators, services, controllers, ecc...
For more info checkout the official [Spring Boot](https://spring.io/projects/spring-boot)
website.

As basic example, the [APIMethodsController](com.robypomper.josp.jcp.apis.APIMethodsController)
class implements 2 sample endpoints ```/apis/str``` and ```/apis/int```. That
endpoints will be substituted shortly with jcpAPIs methods.


### JPA Database access
jcpAPIs service use [Spring Boot Data JPA](https://spring.io/projects/spring-data-jpa)
model, and the underling layer [Hibernate](https://hibernate.org/) +
[Project Lombok](https://projectlombok.org/), to access to database data.

Here it's implemented parting required classes in 3 groups:
* entities:<br>
  Classes that describe the entities contained in the database. This classes with
  the support of Hibernate library can create/update the tables on the connected
  database.
* mnmg:<br>
  Spring based classes that bridge entities classes to spring components like:
  controllers, services, etc... For each entity there are 2 mngm classes: a
  repository and a service.
* clients:<br>
  All classes that need to access to the data entities.  

Database configurations can be enabled including ```db``` profile to main
```application.yml``` file.


### Enable HTTPs
All request to the jcpAPIs must be via HTTPS. That enable SSL encryption to all
messages send from clients to server and vice-versa.

HTTPS configurations can be enabled including ```ssl``` profile to main
```application.yml``` file.
