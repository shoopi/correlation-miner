package main.java.nl.tue.ieis.is.correlation.objects;

import java.util.List;

public class TraceObject {
	private List<EventObject> events;

	public List<EventObject> getEvents() {
		return events;
	}

	public void setEvents(List<EventObject> events) {
		this.events = events;
	}

	public TraceObject(List<EventObject> events) {
		super();
		this.events = events;
	}
	
	
}
