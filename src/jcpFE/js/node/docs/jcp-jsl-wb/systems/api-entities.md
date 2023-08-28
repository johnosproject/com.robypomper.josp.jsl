# JCP JSL Web Bridge API Entities

The API Entities are Javascript objects that allow to cache remote API entities and provide methods to interact with
them asynchronously. Moreover the API Entities can register to SSEUpdater events to keep their cached data always
up-to-date.

All API Entities classes inherit from the base class
[APIEntity](../../../src/jcp-jsl-wb/systems/api-entities/APIEntity.js) that provide initialization and fetching utils
for his subclasses.

## API Entities list

* [APIUser](../../../src/jcp-jsl-wb/systems/api-entities/APIUser.js)
* [APIService](../../../src/jcp-jsl-wb/systems/api-entities/APIService.js)
* [APIObjects](../../../src/jcp-jsl-wb/systems/api-entities/APIObjects.js)
* object/
    * [APIObject](../../../src/jcp-jsl-wb/systems/api-entities/objects/APIObject.js)
    * components/
        * [APIComponent](../../../src/jcp-jsl-wb/systems/api-entities/objects/components/APIComponent.js)
        * [APIContainer](../../../src/jcp-jsl-wb/systems/api-entities/objects/components/APIContainer.js) -> Component
        * [APIBooleanState](../../../src/jcp-jsl-wb/systems/api-entities/objects/components/APIBooleanState.js) ->
          Component
        * [APIRangeState](../../../src/jcp-jsl-wb/systems/api-entities/objects/components/APIRangeState.js) -> Component
        * [APIBooleanAction](../../../src/jcp-jsl-wb/systems/api-entities/objects/components/APIBooleanAction.js) ->
          BooleanState
        * [APIRangeAction](../../../src/jcp-jsl-wb/systems/api-entities/objects/components/APIRangeAction.js) ->
          RangeState
    * [APIStructure](../../../src/jcp-jsl-wb/systems/api-entities/objects/APIStructure.js) -> component/Container
    * [APIPermission](../../../src/jcp-jsl-wb/systems/api-entities/objects/APIPermission.js)
* [APIManager](../../../src/jcp-jsl-wb/systems/api-entities/APIManager.js)

Where not specified the API Entity class inherits from ```APIEntity``` class.