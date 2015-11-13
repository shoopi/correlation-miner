package main.java.nl.tue.ieis.is.correlation.objects;


import org.joda.time.Duration;

public class PossibleEdge {

	private String activity1;
	private String activity2;
	private Duration duration;
	private double stdDevDiff;
	private double probability;

	
	public String getActivity1() {
		return activity1;
	}
	public PossibleEdge(String activity1, String activity2, double stdDevDiff,
			double probability) {
		super();
		this.activity1 = activity1;
		this.activity2 = activity2;
		this.stdDevDiff = stdDevDiff;
		this.probability = probability;
	}
	public double getStdDevDiff() {
		return stdDevDiff;
	}
	public void setStdDevDiff(double stdDevDiff) {
		this.stdDevDiff = stdDevDiff;
	}
	/**
	 * @param activity1 the activity1 to set
	 */
	public void setActivity1(String activity1) {
		this.activity1 = activity1;
	}
	/**
	 * @return the activity2
	 */
	public String getActivity2() {
		return activity2;
	}
	/**
	 * @param activity2 the activity2 to set
	 */
	public void setActivity2(String activity2) {
		this.activity2 = activity2;
	}
	/**
	 * @return the duration
	 */
	public Duration getDuration() {
		return duration;
	}
	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	/**
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}
	/**
	 * @param probability the probability to set
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}
	public PossibleEdge(String activity1, String activity2,
			Duration duration, double probability) {
		super();
		this.activity1 = activity1;
		this.activity2 = activity2;
		this.duration = duration;
		this.probability = probability;
	}
	
	@Override
	public String toString() {
		String edgeProperties = "Edge (" + activity1 + " --> " + activity2 + ")";
		double val = 0;
		if(duration != null) {
			edgeProperties = edgeProperties.concat(" 		** Duration: " + this.duration);
			val = (double)((double)duration.getMillis() / (double)probability);
		} else {
			edgeProperties = edgeProperties.concat(" 		** Std. Deviation: " + this.stdDevDiff);
			val = (double)(stdDevDiff / (double)probability);
		}
		edgeProperties = edgeProperties.concat(" 		** P/S Fraction: " + this.probability);
		edgeProperties = edgeProperties.concat(" 		** Edge-Ratio: " + val);
		return edgeProperties;

	}
	
	public boolean equals(Object o) {
		return ((o instanceof PossibleEdge) 
				&& ((((PossibleEdge) o).activity1).contentEquals(this.activity1)) 
				&& (((PossibleEdge) o).activity2).contentEquals(this.activity2));
	}
	
	public int hashCode() {
        return activity1.hashCode() ^ activity2.hashCode();
    }
}
