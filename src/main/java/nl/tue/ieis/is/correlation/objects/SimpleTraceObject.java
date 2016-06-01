package main.java.nl.tue.ieis.is.correlation.objects;

import java.util.List;

public class SimpleTraceObject {
	
	private List<SimpleEventObject> events;

	public List<SimpleEventObject> getEvents() {
		return events;
	}

	public void setEvents(List<SimpleEventObject> events) {
		this.events = events;
	}

	public SimpleTraceObject(List<SimpleEventObject> events) {
		super();
		this.events = events;
	}
	
}