package main.java.nl.tue.ieis.is.correlation.helper;

import java.util.Comparator;

import main.java.nl.tue.ieis.is.correlation.objects.PossibleEdge;

public class EdgeHeuristicComparer implements Comparator<PossibleEdge> {
	@Override
	public int compare(PossibleEdge e1, PossibleEdge e2) {
		double val1 =  (double)((double)e1.getDuration().getMillis() / (double)e1.getProbability());
		double val2 =  (double)((double)e2.getDuration().getMillis() / (double)e2.getProbability());
		return Double.compare(val1, val2);
	}
}
