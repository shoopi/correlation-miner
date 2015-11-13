package main.java.nl.tue.ieis.is.correlation.objects;

import java.util.List;

import org.joda.time.DateTime;

public class ActivityTimeArray {

	private String activity;
	private String status;
	private List<DateTime> timestamps;
	
	public String getActivity() {
		return activity;
	}
	
	public void setActivity(String activity) {
		this.activity = activity;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public List<DateTime> getTimestamps() {
		return timestamps;
	}

	public void setTimestamps(List<DateTime> timestamps) {
		this.timestamps = timestamps;
	}
	
	public ActivityTimeArray(String activity, String status,
			List<DateTime> timestamps) {
		super();
		this.activity = activity;
		this.status = status;
		this.timestamps = timestamps;
	}
	
	public ActivityTimeArray(String activity,
			List<DateTime> timestamps) {
		super();
		this.activity = activity;
		this.timestamps = timestamps;
	}
}
