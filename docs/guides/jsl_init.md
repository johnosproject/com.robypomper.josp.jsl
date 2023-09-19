# JSL - Guides: Init JSL

[README](../../README.md) | [SPECS](../specs.md) | [GUIDES](../guides.md) | [CHANGELOG](../../CHANGELOG.md) | [TODOs](../../TODOs.md) | [LICENCE](../../LICENCE.md)

A JSL Instance must be initialized using a `JSL.Settings` object. This object can
be initialized parsing a file (like during the JSL Shell initialization) or
starting from a property `Map`.
Once you loaded the JSL settings, then you can initialize the instance:

```shell
JSL.Settings settings = FactoryJSL.loadSettings("jsl.yml", "");
jsl = FactoryJSL.createJSL(settings, jslVer);
```

For more `JSL` and `JSL.Settings` constructors check out the
`com.robypomper.josp.jsl.FactoryJSL` class.
