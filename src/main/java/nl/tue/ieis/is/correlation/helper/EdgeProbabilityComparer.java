package main.java.nl.tue.ieis.is.correlation.helper;

import java.util.Comparator;

import main.java.nl.tue.ieis.is.correlation.objects.PossibleEdge;

public class EdgeProbabilityComparer implements Comparator<PossibleEdge> {
	@Override
	public int compare(PossibleEdge e1, PossibleEdge e2) {
		double val1 = e1.getProbability();
		double val2 = e2.getProbability();
		return Double.compare(val1, val2);
	}
}
