# JCP Tasks Groups

<div style="color: red"><b>Sorry:</b> this page has not yet been written.</div>
Ask for more information at [tech@johnosproject.com](mailto:tech@johnosproject.com)<br/>
or come back in a few days.

## JCP Docker

group: josp runners platform (dockers)
list:   jospCloudDockers_Start
        jospCloudDockers_Stop
        jospCloudDockers_Restart
        jospCloudDockers_Clean
        jospCloudDockers_Refresh

----

## JCP Soft

group: josp runners platform (soft)
list:   jospCloudSoft_Start
        jospCloudSoft_Stop
        jospCloudSoft_Restart
        jospCloudSoft_Clean
        jospCloudSoft_Refresh

----

## JCP Services

group: josp runners jcp (dockers)
list:   dbms_Up
        auth_Up
        dbms_Down
        auth_Down

group: josp runners jcp (soft)
list:   jcp{JCP_SERVICE}_Start
        jcp{JCP_SERVICE}_StartAsync
        jcp{JCP_SERVICE}_StatusAsync
        jcp{JCP_SERVICE}_StopAsync
    JCP_SERVICE: All, APIs, GWs, JSLWebBridge, FE
