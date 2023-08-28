## Base component classes

Following classes helps develop API Entity specific components. A React component that must display an Object, then it
inherit from the ReactObjectBase class. This classes register their listeners to corresponding API Entity events and
call sub-class's methods (if implemented).

* [ReactFetching](../../../src/jcp-fe/components/base/ReactFetching.js): base component for fetching dependent component
* [ReactObjectsBase](../../../src/jcp-fe/components/base/ReactObjectsBase.js): base component for list of Objects entity
* [ReactObjectBase](../../../src/jcp-fe/components/base/ReactObjectBase.js): base component for Object entity
* [ReactObjectPageBase](../../../src/jcp-fe/components/base/ReactObjectPageBase.js): base component for Object entity as
  main entity in the page
* [ReactObjectComponentBase](../../../src/jcp-fe/components/base/ReactObjectComponentBase.js): base component for
  Objects Component entity
* [ReactServiceBase](../../../src/jcp-fe/components/base/ReactServiceBase.js): base component for Service entity
* [ReactUserBase](../../../src/jcp-fe/components/base/ReactUserBase.js): base component for User entity
