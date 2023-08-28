import Log from './jcp-commons/Logging'
import { JCPFEClass } from './jcp-fe/JCPFE'

Log.log("JCPFE","".padEnd(80,"#"));
Log.log("JCPFE","".padEnd(80,"#"));
Log.log("JCPFE","    Init JCPFE");
Log.log("JCPFE","".padEnd(80,"#"));

var thisUrl = window.location.origin + "/frontend";
const JCPFE = new JCPFEClass(thisUrl,window.location.port == 3000 ? 'https://' + window.location.hostname + ':9003' : '');
//const JCPFE = new JCPFEClass('https://www.johnosproject.org','https://jslwb.johnosproject.org');
export default JCPFE;


// On SpringBoot success
// On LocalWebDev fails because can't serve JSL Web Bridge url
//export default JCPFE = new JCPFEClass('');

// On SpringBoot (local) success
// On SpringBoot (cloud) fails because can't reach local address
// On LocalWebDev fails because CORS error
//export default JCPFE = new JCPFEClass('https://' + window.location.hostname + ':9004');

// On SpringBoot success
// On LocalWebDev success (because don't request JSL Web Bridge url)
//export default JCPFE = new JCPFEClass('','https://' + window.location.hostname + ':9003');

// On SpringBoot (local) success
// On SpringBoot (cloud) fails because can't reach local address
// On LocalWebDev fails because CORS error
//export default JCPFE = new JCPFEClass('https://' + window.location.hostname + ':9004','https://' + window.location.hostname + ':9003');
