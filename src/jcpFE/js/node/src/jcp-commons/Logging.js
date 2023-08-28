import { getTimeDateString } from './DateUtils'

function print(level, section, msg) {
    const datetime = getTimeDateString(new Date());
    section = section.padEnd(20,' ');
    const msgFormatted = "[ " + datetime + " @ " + section + " ]  " + level + ": " + msg;
    
    if (level === "DEB")
        console.debug(msgFormatted);
    else if (level === "INF")
        console.log(msgFormatted);
    else if (level === "WAR")
        console.warn(msgFormatted);
    else if (level === "ERR")
        console.error(msgFormatted);
}

const Log = { 
    
    debug(section, msg) {
        print("DEB",section,msg);
    },

    log(section, msg) {
        print("INF",section,msg);
    },

    warn(section, msg) {
        print("WAR",section,msg);
    },

    error(section, msg) {
        print("ERR",section,msg);
    }

}

export default Log;
