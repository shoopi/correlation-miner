package main.java.nl.tue.ieis.is.correlation.objects;

import org.joda.time.Duration;

public class RealEdge {

	private String activity1;
	private String activity2;
	private Duration duration;
	
	public String getActivity1() {
		return activity1;
	}
	public void setActivity1(String activity1) {
		this.activity1 = activity1;
	}
	public String getActivity2() {
		return activity2;
	}
	public void setActivity2(String activity2) {
		this.activity2 = activity2;
	}
	public Duration getDuration() {
		return duration;
	}
	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	public RealEdge(String activity1, String activity2, Duration duration) {
		super();
		this.activity1 = activity1;
		this.activity2 = activity2;
		this.duration = duration;
	}
	
	public boolean equals(Object o) {
		return ((o instanceof RealEdge) 
				&& ((((RealEdge) o).activity1).contentEquals(this.activity1)) 
				&& (((RealEdge) o).activity2).contentEquals(this.activity2));
	}
	
	public int hashCode() {
        return activity1.hashCode() ^ activity2.hashCode();
    }
	
}
