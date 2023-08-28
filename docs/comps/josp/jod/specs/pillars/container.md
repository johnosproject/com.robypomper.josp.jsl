# JOSP JOD Pillar Container

Container pillar can contain only other Pillars. This pillar allows Makers to
create hierarchical object's structures.

A container require just 3 properties: his name, his type (```JODContainer```)
and the list of contained pillars.<br/>
```JODContainers```'s name is defined by the element name, so in the following
example you define a container called 'MainLamp'.

---

## Examples:

```json title="struct.json: container example
    // ...
    "MainLamp" : {
        "type": "JODContainer",
        "contains": {
            // Pillars list or other container
        }
    },
    // ...
```
