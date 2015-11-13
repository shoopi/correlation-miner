package main.java.nl.tue.ieis.is.correlation.helper;

import java.util.Comparator;

import main.java.nl.tue.ieis.is.correlation.objects.PossibleEdge;

public class EdgeHeuristicStandardDeviationComparer implements Comparator<PossibleEdge> {
	@Override
	public int compare(PossibleEdge e1, PossibleEdge e2) {
		return Double.compare(e1.getStdDevDiff(), e2.getStdDevDiff());
	}
}
