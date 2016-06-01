package main.java.nl.tue.ieis.is.correlation.objects;

import org.joda.time.DateTime;

public class SimpleEventObject {
	private String activity;
	private DateTime timestamp;
	
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public DateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(DateTime timestamp) {
		this.timestamp = timestamp;
	}
	public SimpleEventObject(String activity, DateTime timestamp) {
		super();
		this.activity = activity;
		this.timestamp = timestamp;
	}
	
	public String toString() {
		return activity + " [" + timestamp.toString() + "]";
	}
	
	public boolean equals(Object o) {
		return ((o instanceof SimpleEventObject) && 
				((((SimpleEventObject) o).getActivity()).equals(this.getActivity())) && 
				((((SimpleEventObject) o).getTimestamp()).equals(this.getTimestamp())));
	}
	
	public int hashCode() {
        return activity.hashCode() ^ timestamp.hashCode();
    }

}
