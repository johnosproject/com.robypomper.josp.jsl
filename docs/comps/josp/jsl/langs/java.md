# JOSP JSL Java

## Requirements

## Include

## Initialization

        ### JSL Settings initialization

        To initialize a ```JSL.Settings``` instance
        ```java title="Snipet from java/com/robypomper/josp/jsl/JSLShell.java"
        try {
            JSL.Settings settings = FactoryJSL.loadSettings(configsFile, jslVer);
            shell.createJSL(settings, jslVer);
        } catch (Exception | JSL.FactoryException e) {
            shell.fatal(e, EXIT_ERROR_CONFIG);
            return;
        }
        ```
        
        ```java title="Snipet from java/com/robypomper/josp/jcp/jslwebbridge/webbridge/JSLWebBridge.java"
            Map<String, Object> properties = new HashMap<>();
            properties.put(JSLSettings_002.JCP_SSL              , true);
            properties.put(JSLSettings_002.JCP_CLIENT_ID        , clientId);
            properties.put(JSLSettings_002.JCP_CLIENT_SECRET    , clientSecret);
            properties.put(JSLSettings_002.JCP_CLIENT_CALLBACK  , clientCallback);
            properties.put(JSLSettings_002.JSLSRV_ID            , clientId);
            properties.put(JSLSettings_002.JSLSRV_NAME          , clientId);
            properties.put(JSLSettings_002.JSLCOMM_LOCAL_ENABLED, true);
            properties.put(JSLSettings_002.JSLCOMM_CLOUD_ENABLED, true);
            
            try {
                JSL.Settings settings = FactoryJSL.loadSettings(properties, jslParams.jslVersion);
                JSL jsl = FactoryJSL.createJSL(settings, jslParams.jslVersion);
                jsl.startup();
                return jsl;
            
            } catch (JSL.FactoryException | StateException e) {
                throw new JSLErrorOnInitException(sessionId, e);
            }
        }
        ```
        
        *By default the JSL Shell loads the ```jsl.yml``` file from the current directory.*
        
        The JSL.Settings can be applied to a JSL instance only on his initialization.
        **Any further update will be ignored.**








```
    From /Home.md/Start withServices/Java IoT Services
    #### Java IoT Services

    The Java JSL implementation can be used in any Java, Groovy or Kotlin software. It's published on Maven Central as a public `jar` artifactory. You can download it and copy to /libs directory, or you can include it in your build system like Gradle or Maven. Once it's included in your project, you can initialize its instance, then access all JSL methods: to [list objects, get info or execute their commands]. More info about JSL's instance and his JSL methods can be found at [JSL Reference](3_references/jsl/) page.

    Here the steps required to **include and use Java JSL in your projects**:
    1. [Include JSL library in your source project](2_guides/developers.md#include-jsl-library)
    1. [Initialize the JSL Instance](2_guides/developers.md#initialize-jsl-instance)


    1. [List or filter objects, listen for states updates and send action request to objects](2_guides/developers.md#list-and-interact-with-objects)
    1. [Manage users](2_guides/developers.md#manage-users)
    1. [Manage current service](2_guides/developers.md#manage-current-service)

    1. [List](2_guides/developers.md#list-objects) or [filter available objects](2_guides/developers.md#filter-objects)
    1. [Get object's state value](2_guides/developers.md#get-state-value)
    or [register a listener for updates](2_guides/developers.md#listen-for-state-updates)
    1. [Send an action request to object](2_guides/developers.md#send-action-to-object)
    1. [Authenticate](2_guides/developers.md#user-authentication)
    or [De-Authenticate](2_guides/developers.md#user-de-authentication) user


    * [Users](#users)
    * [Get current user info](#get-current-user-info)
    * [Get other user info](#get-other-user-info)
    * [Authenticate user](#authenticate-user)
    * [De-authenticate user](#de-authenticate-user)

    Once the Java JSL is included in your project, you must initialize its instance before use it. After that you can access all JSL methods: to list objects, get info or execute their commands. More info about JSL's instance and his JSL methods can be found at [JSL Reference](3_references/jsl/) page.

        ToDo: add java jsl initialization and objects filter source example
```