# JOSP Service Library -Guides: Init JSL

First of all' you need to include the JSL library into your project. You can do
this adding the JSL library as a Gradle dependency into your `build.gradle` file:

```groovy
dependencies {
    // Add JSL as Gradle dependency
    implementation "com.robypomper.josp:jospJSL:$VERSION"
}
```

Then, a new JSL Instance must be initialized using a `JSL.Settings` object that
contains instances configs. This object can be initialized parsing a file (like
done during the JSL Shell initialization) or starting from a property `Map`.
Once you loaded the JSL settings, then you can initialize the instance:

```shell
JSL.Settings settings = FactoryJSL.loadSettings("jsl.yml", "");
jsl = FactoryJSL.createJSL(settings, "");
```

For more `JSL` and `JSL.Settings` constructors check out the
`com.robypomper.josp.jsl.FactoryJSL` class.
