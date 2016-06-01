package main.java.nl.tue.ieis.is.correlation.objects;

import org.joda.time.Duration;

public class VarBinaryRel {
	
	private VarUsage sourceVariable;
	private VarUsage destVaribale;
	private Duration averageDuration;
	private long standardDeviation;
	private int edgeOccurrance;
	private long minDuration;
	private long maxDuration;
	
	
	
	public long getMinDuration() {
		return minDuration;
	}
	public void setMinDuration(long minDuration) {
		this.minDuration = minDuration;
	}
	public long getMaxDuration() {
		return maxDuration;
	}
	public void setMaxDuration(long maxDuration) {
		this.maxDuration = maxDuration;
	}
	public int getEdgeOccurrance() {
		return edgeOccurrance;
	}
	public void setEdgeOccurrance(int edgeOccurrance) {
		this.edgeOccurrance = edgeOccurrance;
	}
	public long getStandardDeviation() {
		return standardDeviation;
	}
	public void setStandardDeviation(long standardDeviation) {
		this.standardDeviation = standardDeviation;
	}
	public VarUsage getDestVaribale() {
		return destVaribale;
	}
	public void setDestVaribale(VarUsage destVaribale) {
		this.destVaribale = destVaribale;
	}
	public VarUsage getSourceVariable() {
		return sourceVariable;
	}
	public void setSourceVariable(VarUsage sourceVariable) {
		this.sourceVariable = sourceVariable;
	}
	public VarBinaryRel(VarUsage sourceVariable, VarUsage destVaribale) {
		super();
		this.sourceVariable = sourceVariable;
		this.destVaribale = destVaribale;
	}
	
	
	public Duration getAverageDuration() {
		return averageDuration;
	}
	public void setAverageDuration(Duration averageDuration) {
		this.averageDuration = averageDuration;
	}
	@Override
	public String toString() {
		return sourceVariable.toString() + " ==> " + destVaribale.toString();
	}
	
}
