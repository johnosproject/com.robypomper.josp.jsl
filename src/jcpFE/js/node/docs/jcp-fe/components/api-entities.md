# JCP JSL Web Bridge's API Entities components

* [Service](../../../src/jcp-fe/components/api-entities/Service.js)
  * ServicePage
  * ServiceHeader -> ReactServiceBase
  * ServiceInfoGrid -> ReactServiceBase
* [User](../../../src/jcp-fe/components/api-entities/User.js)
  * UserPage
  * UserHeader -> ReactUserBase
  * UserInfoGrid -> ReactUserBase
* [Objects](../../../src/jcp-fe/components/api-entities/Objects.js)
  * ObjectsListPage
  * ObjectsAddDialog
  * ObjectsList -> ReactObjectBase
  * ObjectsListFiltered -> ReactObjectBase
* [Object](../../../src/jcp-fe/components/api-entities/Object.js)
  * ObjectListItemCard -> ReactObjectBase
  * ObjectHeader -> ReactObjectBase
  * ObjectNameEdit_Dialog -> ReactObjectBase
  * ObjectOwnerEdit_Dialog -> ReactObjectBase
  * ObjectActions
  * ObjectMenu
  * ObjectStructPage -> ReactObjectPageBase
    * ObjectInfoGrid -> ReactObjectBase
  * ObjectEventsPage -> ReactObjectPageBase
  * ComponentHistoryPage -> ReactObjectComponentBase
  * ObjectPermissionsPage -> ReactObjectPageBase
    * ShareMenuAction
  * ObjectStatsPage -> ReactObjectPageBase
* [Components](../../../src/jcp-fe/components/api-entities/Components.js)
  * ObjectBooleanState -> ReactObjectComponentBase
  * ObjectRangeState -> ReactObjectComponentBase
  * ObjectBooleanAction -> ObjectBooleanState
  * ObjectRangeAction -> ObjectRangeState
  * ObjectComponentDetails -> ReactObjectComponentBase
  * ObjectContainerDetails -> ObjectComponentDetails
  * ObjectBooleanStateDetails -> ObjectComponentDetails
  * ObjectRangeStateDetails -> ObjectComponentDetails
  * ObjectBooleanActionDetails -> ObjectComponentDetails
  * ObjectRangeActionDetails -> ObjectComponentDetails
* [JCP](../../../src/jcp-fe/components/api-entities/JCP.js)
  * JCPHeader
  * JCPServiceMenu
  * JCPAPIsStatusPage -> ReactFetching
    * JCPAPIsStatus_ObjectsCard -> ReactFetchingCard
      * JCPAPIsStatus_ObjectsDetails -> ReactFetching
    * JCPAPIsStatus_ServicesCard -> ReactFetchingCard
      * JCPAPIsStatus_ServicesDetails -> ReactFetching
    * JCPAPIsStatus_UsersCard -> ReactFetchingCard
      * JCPAPIsStatus_UsersDetails -> ReactFetching
    * JCPAPIsStatus_GatewaysCard -> ReactFetchingCard
      * JCPAPIsStatus_GatewaysDetails -> ReactFetching
  * JCPGatewaysPage -> ReactFetching
  * JCPGatewaysStatusPage -> ReactFetching
    * JCPGatewaysStatusPage_GatewayCard -> ReactFetchingCard
      * JCPGatewaysStatusPage_GatewaySubCard -> ReactFetchingSubCard
        * JCPGatewaysStatusPage_GatewayDetails -> ReactFetching
    * JCPGatewaysStatusPage_BrokerCard -> ReactFetchingCard
      * JCPGatewaysStatusPage_BrokerObjectDetails -> ReactFetching
      * JCPGatewaysStatusPage_BrokerServiceDetails -> ReactFetching
      * JCPGatewaysStatusPage_BrokerObjectDBDetails -> ReactFetching
  * JCPJSLWebBridgeStatusPage -> ReactFetching
    * JCPJSLWebBridgeStatus_Sessions -> ReactFetchingCard
      * JCPJSLWebBridgeStatus_SessionsDetails -> ReactFetching
  * JCPFrontEndStatusPage -> ReactFetching
  * JCPExecutablePage -> ReactFetching
    * JCPExecutable_OnlineCard -> ReactFetchingCard
    * JCPExecutable_JavaCard -> ReactFetchingCard
      * JCPExecutable_JavaVMSubCard -> ReactFetchingSubCard
      * JCPExecutable_JavaRuntimeSubCard -> ReactFetchingSubCard
      * JCPExecutable_JavaTimesSubCard -> ReactFetchingSubCard
      * JCPExecutable_JavaClassesSubCard -> ReactFetchingSubCard
      * JCPExecutable_JavaMemorySubCard -> ReactFetchingSubCard
      * JCPExecutable_JavaThreadsSubCard -> ReactFetchingSubCard
      * JCPExecutable_JavaThreadsDetails -> ReactFetching
    * JCPExecutable_OSCard -> ReactFetchingCard
    * JCPExecutable_CPUCard -> ReactFetchingCard
    * JCPExecutable_DisksCard -> ReactFetchingCard
      * JCPExecutable_DisksDetails -> ReactFetching
    * JCPExecutable_NetworksCard -> ReactFetchingCard
      * JCPExecutable_NetworksDetails -> ReactFetching
  * JCPBuildInfoPage -> ReactFetching
