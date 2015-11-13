package main.java.nl.tue.ieis.is.correlation.objects;

public class WeaklyFollowProb {

	private String activity1;
	private String activity2;
	private double prob;
	
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

	public double getProb() {
		return prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}
	
	public WeaklyFollowProb(String activity1, String activity2, double prob) {
		super();
		this.activity1 = activity1;
		this.activity2 = activity2;
		this.prob = prob;
	}
	
	public WeaklyFollowProb(String activity1, String activity2) {
		super();
		this.activity1 = activity1;
		this.activity2 = activity2;
	}
	
	@Override
	public String toString(){
		return "P(" + activity1 + " --> " + activity2 + ") = " + prob;
	}
	
	@Override
	public boolean equals(Object o) {
		return ((o instanceof WeaklyFollowProb) && 
				((((WeaklyFollowProb) o).getActivity1()).equals(this.getActivity1())) && 
				((((WeaklyFollowProb) o).getActivity2()).equals(this.getActivity2())));
	}
	
	@Override
	public int hashCode() {
        return activity1.hashCode() ^ activity2.hashCode();
    }
}
