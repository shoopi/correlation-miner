package main.java.nl.tue.ieis.is.correlation.schema.xes_simple;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "trace"
})
@XmlRootElement(name = "log")
public class Log {
	@XmlAttribute(name = "xes.version", 	required = false)
	protected Float xesVersion;
	@XmlAttribute(name = "xes.creator", 	required = false)
	protected String xesCreator;
	@XmlAttribute(name = "xes.features", 	required = false)
	protected String xesFeatures;
	@XmlAttribute(name = "openxes.version", required = false)
	protected String opemXESVersion;
	
    @XmlElement(required = true)
    protected List<Log.Trace> trace;

    public List<Log.Trace> getTrace() {
        if (trace == null) {
            trace = new ArrayList<Log.Trace>();
        }
        return this.trace;
    }
    
    public Float getXesVersion() {
        return xesVersion;
    }
    
    public void setXesVersion(Float value) {
        this.xesVersion = value;
    }
    
    public String getXesCreator() {
        return xesCreator;
    }
    
    public void setXesCreator(String value) {
        this.xesCreator = value;
    }
    
    public String getXesFeatures() {
		return xesFeatures;
	}

	public void setXesFeatures(String xesFeatures) {
		this.xesFeatures = xesFeatures;
	}

	public String getOpemXESVersion() {
		return opemXESVersion;
	}

	public void setOpemXESVersion(String opemXESVersion) {
		this.opemXESVersion = opemXESVersion;
	}

	public void setTrace(List<Log.Trace> trace) {
		this.trace = trace;
	}




	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "date",
        "string",
        "event"
    })
    public static class Trace {

        @XmlElement(required = true)
        protected Log.Trace.Date date;
        @XmlElement(required = true)
        protected List<Log.Trace.String> string;
        @XmlElement(required = true)
        protected List<Log.Trace.Event> event;

        public Log.Trace.Date getDate() {
            return date;
        }

 
        public void setDate(Log.Trace.Date value) {
            this.date = value;
        }

   
        public List<Log.Trace.String> getString() {
            if (string == null) {
                string = new ArrayList<Log.Trace.String>();
            }
            return this.string;
        }

     
        public List<Log.Trace.Event> getEvent() {
            if (event == null) {
                event = new ArrayList<Log.Trace.Event>();
            }
            return this.event;
        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Date {

            @XmlAttribute(name = "key")
            protected java.lang.String key;
            @XmlAttribute(name = "value")
            protected java.lang.String value;

  
            public java.lang.String getKey() {
                return key;
            }

            public void setKey(java.lang.String value) {
                this.key = value;
            }

    
            public java.lang.String getValue() {
                return value;
            }

            
            public void setValue(java.lang.String value) {
                this.value = value;
            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "string",
            "date"
        })
        public static class Event {

            @XmlElement(required = true)
            protected List<Log.Trace.Event.String> string;
            @XmlElement(required = true)
            protected Log.Trace.Event.Date date;

           
            public List<Log.Trace.Event.String> getString() {
                if (string == null) {
                    string = new ArrayList<Log.Trace.Event.String>();
                }
                return this.string;
            }

            public Log.Trace.Event.Date getDate() {
                return date;
            }

            
            public void setDate(Log.Trace.Event.Date value) {
                this.date = value;
            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Date {

                @XmlAttribute(name = "key")
                protected java.lang.String key;
                @XmlAttribute(name = "value")
                protected java.lang.String value;

                public java.lang.String getKey() {
                    return key;
                }

              
                public void setKey(java.lang.String value) {
                    this.key = value;
                }

                public java.lang.String getValue() {
                    return value;
                }

                public void setValue(java.lang.String value) {
                    this.value = value;
                }

            }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class String {

                @XmlAttribute(name = "key")
                protected java.lang.String key;
                @XmlAttribute(name = "value")
                protected java.lang.String value;

              
                public java.lang.String getKey() {
                    return key;
                }

              
                public void setKey(java.lang.String value) {
                    this.key = value;
                }

               
                public java.lang.String getValue() {
                    return value;
                }

             
                public void setValue(java.lang.String value) {
                    this.value = value;
                }

            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class String {

            @XmlAttribute(name = "key")
            protected java.lang.String key;
            @XmlAttribute(name = "value")
            protected java.lang.String value;

            public java.lang.String getKey() {
                return key;
            }

            public void setKey(java.lang.String value) {
                this.key = value;
            }

            public java.lang.String getValue() {
                return value;
            }

         
            public void setValue(java.lang.String value) {
                this.value = value;
            }

        }

    }

}
