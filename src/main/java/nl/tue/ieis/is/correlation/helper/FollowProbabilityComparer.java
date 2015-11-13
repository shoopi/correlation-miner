package main.java.nl.tue.ieis.is.correlation.helper;

import java.util.Comparator;

import main.java.nl.tue.ieis.is.correlation.objects.WeaklyFollowProb;

public class FollowProbabilityComparer implements Comparator<WeaklyFollowProb> {
	@Override
	public int compare(WeaklyFollowProb e1, WeaklyFollowProb e2) {
		return (-1) * Double.compare(e1.getProb(), e2.getProb());
	}
}
