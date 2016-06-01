package main.java.nl.tue.ieis.is.correlation.objects;

import java.util.UUID;

public class VarUsage {
	
	private SimpleEventObject event;
	private UUID index; //to make sure that each event will be used only onece
	public VarUsage(SimpleEventObject event, UUID index) {
		super();
		this.event = event;
		this.index = index;
	}
	public SimpleEventObject getEvent() {
		return event;
	}
	public void setEvent(SimpleEventObject event) {
		this.event = event;
	}
	public UUID getIndex() {
		return index;
	}
	public void setIndex(UUID index) {
		this.index = index;
	}
	
	public boolean equals(Object o) {
		return ((o instanceof SimpleEventObject) && 
				((((VarUsage) o).getEvent()).equals(this.getEvent())) && 
				((((VarUsage) o).getIndex()) == this.getIndex()));
	}
	
	public int hashCode() {
        return event.hashCode() ^ index.hashCode();
    }
	
	public String toString() {
		return event.getActivity() + "_"  + event.getTimestamp().toString() + "_" + index.toString();
	}
}
