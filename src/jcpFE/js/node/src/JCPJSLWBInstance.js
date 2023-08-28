import Log from './jcp-commons/Logging'
import { JCPJSLWBClass } from './jcp-jsl-wb/JCPJSLWB'

Log.log("JCPJSLWB","".padEnd(80,"#"));
Log.log("JCPJSLWB","".padEnd(80,"#"));
Log.log("JCPJSLWB","    Init JCPJSLWB");
Log.log("JCPJSLWB","".padEnd(80,"#"));

const JCPJSLWB = new JCPJSLWBClass('https://' + window.location.hostname + ':9003');
export default JCPJSLWB;