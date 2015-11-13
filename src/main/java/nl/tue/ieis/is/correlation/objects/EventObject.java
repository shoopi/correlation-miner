package main.java.nl.tue.ieis.is.correlation.objects;

import org.joda.time.DateTime;

public class EventObject {
	
	private String user;
	private String status;
	private String activity;
	private DateTime timestamp;
	private String result;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	public EventObject(String user, String status, String activity,
			DateTime timestamp, String result) {
		super();
		this.user = user;
		this.status = status;
		this.activity = activity;
		this.timestamp = timestamp;
		this.result = result;
	}

	public EventObject() {}
	
	public boolean equals(Object o) {
		return ((o instanceof EventObject) && 
				((((EventObject) o).getActivity()).equals(this.getActivity())) && 
				((((EventObject) o).getStatus()).equals(this.getStatus())));
	}
	
	public int hashCode() {
        return activity.hashCode() ^ status.hashCode();
    }

	
}
