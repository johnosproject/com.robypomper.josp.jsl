import ReactDOM from 'react-dom';

const MAIN_ELEMENT = 'app';


// // JCP JSL Web Bridge initialization
//
// import JCPJSLWB from './JCPJSLWBInstance';
// import JCPJSLWBStatus from './jcp-jsl-wb/components/JCPJSLWBStatus';
//
// ReactDOM.render(<JCPJSLWBStatus jcpjslwb={JCPJSLWB} />, document.getElementById(MAIN_ELEMENT));


// JCP FE initialization

import JCPFE from './JCPFEInstance';
import App from './jcp-fe/components/app/App';

//ReactDOM.render(<JCPJSLWBStatus jcpjslwb={JCPFE} />, document.getElementById(MAIN_ELEMENT));
ReactDOM.render(<App jcpfe={JCPFE} />, document.getElementById(MAIN_ELEMENT));



// Initialize WebPack Hot Module Replacement

if (module.hot) {
    module.hot.accept();
}