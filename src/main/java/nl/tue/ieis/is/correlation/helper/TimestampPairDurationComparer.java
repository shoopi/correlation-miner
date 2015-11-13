package main.java.nl.tue.ieis.is.correlation.helper;

import java.util.Comparator;

import main.java.nl.tue.ieis.is.correlation.objects.TimestampPairVariance;


public class TimestampPairDurationComparer implements Comparator<TimestampPairVariance> {
	@Override
	public int compare(TimestampPairVariance e1, TimestampPairVariance e2) {
		double val1 = e1.getDuration().getMillis();
		double val2 = e2.getDuration().getMillis();
		return Double.compare(val1, val2);
	}

}
