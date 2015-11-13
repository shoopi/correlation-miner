package main.java.nl.tue.ieis.is.correlation.helper;

import java.util.Comparator;

import main.java.nl.tue.ieis.is.correlation.objects.PossibleEdge;

public class EdgeDurationComparer implements Comparator<PossibleEdge> {
	@Override
	public int compare(PossibleEdge e1, PossibleEdge e2) {
		double val1 = e1.getDuration().getMillis();
		double val2 = e2.getDuration().getMillis();
		return Double.compare(val1, val2);
	}
}
