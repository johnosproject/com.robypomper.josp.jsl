# JCP Front End's SnackBar

This is a functional component that allow print user feedback in a toast component.

'Functional components' means that a component witch can be reached from other components and used calling his methods.
The SnackBar functional component can be retrieved from ```jcp-fe```'s components via the ```jcpfe``` property.

```javascript
if (thiz.jcpFE.isSnackBar())
    thiz.jcpFE.getSnackBar();
```

Always check if the SnackBar was set with ```isSnackBar()``` method, because the SnackBar must register him self to
the ```JCPFE``` instance. Provided SnackBar
[JCPFESnackBar](../../../../src/jcp-fe/components/app/App.js) register him self to the ```JCPFE``` instance on his
constructor.

## Usage

To help user understand feedback messages, must be set conventions when
```jcp-fe``` components call SnackBar. Depending on the event to display components must choose one of the following
snippets:

* When component send an action command and receive a success response:
    ```javascript
    if (thiz.jcpFE.isSnackBar())
        thiz.jcpFE.getSnackBar().showMessage("success",
                            "Action send successfully",
                            null,
                            "SetTrue action send successfully to '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.",
                            null,2000);
    ```
* When component send an action command and receive a fail response:
    ```javascript
    if (thiz.jcpFE.isSnackBar())
        thiz.jcpFE.getSnackBar().showMessage("error",
                            "Error on send action",
                            "Error on send setTrue action to '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.",
                            error);
    ```
* When component fetch object's data and receive a success response:
    ```javascript
    if (thiz.jcpFE.isSnackBar())
        thiz.jcpFE.getSnackBar().showMessage("info",
                            "Data fetched successfully",
                            null,
                            "Data __WITCH_DATA__ fetched successfully for '" + object.getId() + "' object.",
                            null,1000);
    ```
* When component fetch object's data command and receive a fail response:
    ```javascript
    if (thiz.jcpFE.isSnackBar())
        thiz.jcpFE.getSnackBar().showMessage("warning",
                            "Error on fetch data",
                            "Error on fetch __WITCH_DATA__ data on '" + object.getId() + "' object.",
                            error);
    ```
* When component fetch object's component data and receive a success response:
    ```javascript
    if (thiz.jcpFE.isSnackBar())
        thiz.jcpFE.getSnackBar().showMessage("info",
                            "Data fetched successfully",
                            null,
                            "Data __WITCH_DATA__ fetched successfully for '" + component.getComponentPath() + "' component on '" + object.getId() + "' object.",
                            null,1000);
    ```
* When component fetch object's component data command and receive a fail response:
    ```javascript
    if (thiz.jcpFE.isSnackBar())
        thiz.jcpFE.getSnackBar().showMessage("warning",
                            "Error on fetch data",
                            "Error on fetch __WITCH_DATA__ data for '" + component.getComponentPath() + "' component on '" + component.getObjectId() + "' object.",
                            error);
    ```