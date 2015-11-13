package main.java.nl.tue.ieis.is.correlation.graph;

import java.io.Serializable;

public class Node implements Serializable {

	private static final long serialVersionUID = 753960307838257847L;
	
	private String activityName;
	private int occurance;

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public int getOccurance() {
		return occurance;
	}

	public void setOccurance(int occurance) {
		this.occurance = occurance;
	}

	public Node(String activityName, int occurance) {
		super();
		this.activityName = activityName;
		this.occurance = occurance;
	}
	
	@Override
	public String toString() {
		return  activityName + " (" + occurance + ")";
		
	}
	
	public boolean equals(Object o) {
		return ((o instanceof Node) 
				&& ((((Node) o).activityName).equals(this.activityName)) 
				&& (((Node) o).getOccurance()) == this.occurance);
	}
	
	public int hashCode() {
        return activityName.hashCode() ^ occurance;
    }

}
