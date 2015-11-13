package main.java.nl.tue.ieis.is.correlation.schema.xes_simple;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public ObjectFactory() {
    }

    public Log createLog() {
        return new Log();
    }

    public Log.Trace createLogTrace() {
        return new Log.Trace();
    }

    public Log.Trace.Event createLogTraceEvent() {
        return new Log.Trace.Event();
    }

    public Log.Trace.Date createLogTraceDate() {
        return new Log.Trace.Date();
    }

    public Log.Trace.String createLogTraceString() {
        return new Log.Trace.String();
    }

    public Log.Trace.Event.String createLogTraceEventString() {
        return new Log.Trace.Event.String();
    }

    public Log.Trace.Event.Date createLogTraceEventDate() {
        return new Log.Trace.Event.Date();
    }
}
