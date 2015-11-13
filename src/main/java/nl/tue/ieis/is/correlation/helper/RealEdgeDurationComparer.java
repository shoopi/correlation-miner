package main.java.nl.tue.ieis.is.correlation.helper;

import java.util.Comparator;

import main.java.nl.tue.ieis.is.correlation.objects.RealEdge;

public class RealEdgeDurationComparer implements Comparator<RealEdge> {
	@Override
	public int compare(RealEdge e1, RealEdge e2) {
		double val1 = e1.getDuration().getMillis();
		double val2 = e2.getDuration().getMillis();
		return Double.compare(val1, val2);
	}
}
