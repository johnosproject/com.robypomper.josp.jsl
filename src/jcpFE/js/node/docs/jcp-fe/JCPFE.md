# JCPFE and JCPFEClass

JCPFE class extends [JCPJSLWB](../jcp-jsl-wb/JCPJSLWB.md) class and add Stats and SnackBar instances to the global JCPFE
object.

The JCPFE instance provided initialize the super JCPJSLWB object using current ```window.location.hostname``` as domain
for JSL Web Bridge url. Then it adds the default ```https://``` protocol and ```9003``` port.

## Stats object

The Stats object contain all objects activities and stats collected from the web page.

ToDo: improve [Stats](../../src/jcp-fe/JCPFE.js) doc (add usage and reference)

## SnackBar

The SnackBar is the way to display a generic message to the user from the web page. It simply shows a toast message in
the bottom-left edge of the web page and use different colors depending on the level of the message to display.

For more info on SnackBar usage see [this page](components/app/Snackbar.md).