## JCP Front End service App

The HTML components that provide the basic structure of the JCP Front End service web interface.

* [App](../../../src/jcp-fe/components/app/App.js)
  * JCPFEBackdrop: layer displayed when the ```jcp-jsl-wb``` module is not ready
  * JCPFEAppBar: application's bar that contains the web page's title, the user menu and the drawer switcher.
  * JCPFEAppBarSpacer: empty div with same height of JCPFEAppBar.
  * JCPFEUserMenu: simple menu that display links to login/logout, registration or user's page (if user was
    authenticated)
  * JCPFEDrawer: left drawer menu displaying links to main sections
  * JCPFEDrawerSpacer: empty div with same height of JCPFEDrawer.
  * JCPFEFooter: simple copyright message
  * [SnackBar](./app/Snackbar.md): function component to show user feedbacks on toast component
