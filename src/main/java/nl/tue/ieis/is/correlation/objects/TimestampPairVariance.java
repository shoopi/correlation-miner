package main.java.nl.tue.ieis.is.correlation.objects;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class TimestampPairVariance {
	
	private DateTime first;
	private DateTime second;
	private Duration difference;
	public DateTime getFirst() {
		return first;
	}
	public void setFirst(DateTime first) {
		this.first = first;
	}
	public DateTime getSecond() {
		return second;
	}
	public void setSecond(DateTime second) {
		this.second = second;
	}
	public Duration getDuration() {
		return difference;
	}
	public TimestampPairVariance(DateTime first, DateTime second) {
		super();
		this.first = first;
		this.second = second;
		this.difference = new Duration(first, second);
	}
	
	

}
