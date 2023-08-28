
export default class Updater {

    evtSource = null;

    isActive() {
        return this.evtSource!=null;
    }

    startUpdater(url) {
        if (this.evtSource!=null)
            throw 'Event Source already started';

        this.evtSource = new EventSource(url, { withCredentials: true });
        const thiz = this;
        this.evtSource.onmessage = function(event) { thiz.onMessage(thiz,event) };
        this.evtSource.onopen = function(event) { thiz.onOpen(thiz,event) };
        this.evtSource.onerror = function(event) { thiz.onError(thiz,event) };
    }

    stopUpdater() {
        if (this.evtSource==null)
            throw 'Event Source already stopped';
        
            this.evtSource.close();
            this.evtSource = null;
    }

}
