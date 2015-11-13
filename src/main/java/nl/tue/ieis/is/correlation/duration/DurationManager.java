package main.java.nl.tue.ieis.is.correlation.duration;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import main.java.nl.tue.ieis.is.correlation.objects.ActivityTimeArray;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public class DurationManager {
	
	public static boolean equalSize = true;
	
	public static double getAverageStdDeviationTimeDiff(ActivityTimeArray a, ActivityTimeArray b) {
		List<DateTime> listA = a.getTimestamps();
		List<DateTime> listB = b.getTimestamps();
		if(listA.size() == listB.size()) {
			return getStandardDeviation(listA, listB);
		} else if(listA.size() > listB.size()) {
			List<DateTime> acceptedTimeA = new ArrayList<DateTime>();
			DateTime last_tB = listB.get(listB.size()-1);
			for(DateTime tA : listA) {
				if (tA.isBefore(last_tB) || 
						tA.getMillis() == last_tB.getMillis()) {
					acceptedTimeA.add(tA);
				}
			}
			if(acceptedTimeA.size() == listB.size()) {
				return getStandardDeviation(acceptedTimeA, listB);
			} else if(acceptedTimeA.size() > listB.size()){
				List<DateTime> finalSelctionA = a_gt_b(acceptedTimeA, listB);
				return getStandardDeviation(finalSelctionA, listB);
			} else {
				List<DateTime> cloneB = new ArrayList<DateTime>(listB);
				List<DateTime> finalSelctionB = a_lt_b(acceptedTimeA, cloneB);
				return getStandardDeviation(acceptedTimeA, finalSelctionB);
			}
		} else { 
			//Size A < Size B
			List<DateTime> acceptedTimeB = new ArrayList<DateTime>();
			DateTime first_tA = listA.get(0);
			for(DateTime tB: listB) {
				if (tB.isAfter(first_tA) || 
						tB.getMillis() == first_tA.getMillis()) {
					acceptedTimeB.add(tB);
				}
			}
			if(listA.size() == acceptedTimeB.size()) {
				return getStandardDeviation(listA, acceptedTimeB);
			} else if(listA.size() < acceptedTimeB.size()) {
				List<DateTime> finalSelctionB = a_lt_b(listA, acceptedTimeB);
				return getStandardDeviation(listA, finalSelctionB);
			} else {
				List<DateTime> cloneA = new ArrayList<DateTime>(listA);
				List<DateTime> finalSelectionA = a_gt_b(cloneA, acceptedTimeB);
				return getStandardDeviation(finalSelectionA, acceptedTimeB);
			}
		}	
	}
	
	public static Duration getAverageTimeDiff(ActivityTimeArray a, ActivityTimeArray b) {
		List<DateTime> listA = a.getTimestamps();
		List<DateTime> listB = b.getTimestamps();
		if(listA.size() == listB.size()) {
			return getAverageTimeDiffForEqualSize(listA, listB);
		} else if(listA.size() > listB.size()) {
			List<DateTime> acceptedTimeA = new ArrayList<DateTime>();
			DateTime last_tB = listB.get(listB.size()-1);
			for(DateTime tA : listA) {
				if (tA.isBefore(last_tB) || 
						tA.getMillis() == last_tB.getMillis()) {
					acceptedTimeA.add(tA);
				}
			}
			if(acceptedTimeA.size() < 1) {return new Duration(-910000);}
			if(acceptedTimeA.size() == listB.size()) {
				return getAverageTimeDiffForEqualSize(acceptedTimeA, listB);
			} else if(acceptedTimeA.size() > listB.size()){
				List<DateTime> finalSelctionA = a_gt_b(acceptedTimeA, listB);
				return getAverageTimeDiffForEqualSize(finalSelctionA, listB);
			} else {
				List<DateTime> cloneB = new ArrayList<DateTime>(listB);
				List<DateTime> finalSelctionB = a_lt_b(acceptedTimeA, cloneB);
				return getAverageTimeDiffForEqualSize(acceptedTimeA, finalSelctionB);
			}
		} else { 
			//Size A < Size B
			List<DateTime> acceptedTimeB = new ArrayList<DateTime>();
			DateTime first_tA = listA.get(0);
			for(DateTime tB: listB) {
				if (tB.isAfter(first_tA) || 
						tB.getMillis() == first_tA.getMillis()) {
					acceptedTimeB.add(tB);
				}
			}
			if(acceptedTimeB.size() < 1) {return new Duration(-920000);}
			if(listA.size() == acceptedTimeB.size()) {
				return getAverageTimeDiffForEqualSize(listA, acceptedTimeB);
			} else if(listA.size() < acceptedTimeB.size()) {
				List<DateTime> finalSelctionB = a_lt_b(listA, acceptedTimeB);
				return getAverageTimeDiffForEqualSize(listA, finalSelctionB);
			} else {
				List<DateTime> cloneA = new ArrayList<DateTime>(listA);
				List<DateTime> finalSelectionA = a_gt_b(cloneA, acceptedTimeB);
				return getAverageTimeDiffForEqualSize(finalSelectionA, acceptedTimeB);
			}
		}	
	}
	
	private static List<DateTime> a_lt_b(List<DateTime> listA, List<DateTime> acceptedTimeB) {
		List<DateTime> finalSelctionB = new ArrayList<DateTime>();				
		Iterator<DateTime> itrB = acceptedTimeB.iterator();
		for(int i = 0; i < listA.size() ; i++) {
			DateTime tA = listA.get(i);
			DateTime next_tA = new DateTime();
			try{
				next_tA = listA.get(i+1);
			} catch(IndexOutOfBoundsException e) {next_tA = new DateTime();}
			List<DateTime> tempB = new ArrayList<DateTime>();
			itrB = acceptedTimeB.iterator();
			
			while (itrB.hasNext()) {
			   DateTime tB = itrB.next();
			   if((tA.isBefore(tB) && tB.isBefore(next_tA)) || 
					   tA.isBefore(tB) && tB.getMillis() == next_tA.getMillis() ||
					   tA.getMillis() == tB.getMillis() && tB.isBefore(next_tA) ||
					   tA.getMillis() == tB.getMillis() && tB.getMillis() == next_tA.getMillis()) {
				   tempB.add(tB);
				   itrB.remove();
			   }
			}
			if(tempB.size() > 0) {
				finalSelctionB.add(getAverageDateTime(tempB));
			} else {
				int whileCounter = 0;
				if(equalSize) whileCounter = 1;
				while(tempB.size() == 0) {
					try { 
						next_tA = listA.get(i+2);
					} catch(IndexOutOfBoundsException e) {next_tA = new DateTime();}
					itrB = acceptedTimeB.iterator();
					while (itrB.hasNext()) {
						if(!equalSize) whileCounter = 1;
						DateTime tB = itrB.next();
						if((tA.isBefore(tB) && tB.isBefore(next_tA)) || 
								   tA.isBefore(tB) && tB.getMillis() == next_tA.getMillis() ||
								   tA.getMillis() == tB.getMillis() && tB.isBefore(next_tA) ||
								   tA.getMillis() == tB.getMillis() && tB.getMillis() == next_tA.getMillis()) {
							tempB.add(tB);
							itrB.remove();
						}
					}
					if(tempB.size() > 0) {
						for(int c = 0; c < whileCounter; c++) {
							finalSelctionB.add(getAverageDateTime(tempB));
						}
						finalSelctionB.add(getAverageDateTime(tempB));
					} 
					
					else if ( i == listA.size()) {
						if(equalSize) {
							int remaining = listA.size() - finalSelctionB.size();
							for(int j = 0 ; j < remaining; j++) 
								finalSelctionB.add(finalSelctionB.get(finalSelctionB.size()-1));
						}
					}
					
					whileCounter++;
					i++;
					if(i > listA.size() ) {
						break;
					}
					//System.out.println("loop");
				}
			}
		}
		return finalSelctionB;
	}

	private static List<DateTime> a_gt_b(List<DateTime> acceptedTimeA, List<DateTime> listB) {
		List<DateTime> finalSelctionA = new ArrayList<DateTime>();				
		Iterator<DateTime> itrA = acceptedTimeA.iterator();
		for(int i = 0; i < listB.size(); i++) {
			DateTime tB = listB.get(i);
			List<DateTime> tempA = new ArrayList<DateTime>();
			itrA = acceptedTimeA.iterator();
			while (itrA.hasNext()) {
			   DateTime tA = itrA.next();
			   if(tA.isBefore(tB) ||
					   tA.getMillis() == tB.getMillis()) {
				   tempA.add(tA);
				   itrA.remove();
			   }
			}
			if(tempA.size() > 0) {
				finalSelctionA.add(getAverageDateTime(tempA));
			} else {
				if(finalSelctionA.size() > 0)
					finalSelctionA.add(finalSelctionA.get(finalSelctionA.size()-1));
				else {
					finalSelctionA.add(tB);
				}
			}
		}
		return finalSelctionA;
	}
	
	private static DateTime getAverageDateTime(List<DateTime> listX) {
		BigInteger total = BigInteger.ZERO;
		for(DateTime t: listX){
			total = total.add(BigInteger.valueOf(t.getMillis()));
		}
		BigInteger averageMillis = total.divide(BigInteger.valueOf(listX.size()));
		return new DateTime(averageMillis.longValue());
	}
	
	public static Duration getAverageTimeDiffForEqualSize(List<DateTime> a, List<DateTime> b) {
		DateTime averageA = getAverageDateTime(a);
		DateTime averageB = getAverageDateTime(b);
		return new Duration(averageA,averageB);
		/*
		if(averageA.isBefore(averageB) || 
				averageA.getMillis() == averageB.getMillis()){
			return new Duration(averageA,averageB);
		}
		else {
			//System.err.println("The order is not correct.");
			return new Duration(-654000);
		}
		*/
	}
	
	private static double getStandardDeviation(List<DateTime> averageA, List<DateTime> averageB) {

		double[] timeArrayA = new double[averageA.size()];
		for(int i = 0; i < averageA.size(); i++) {
			timeArrayA[i] = averageA.get(i).getMillis();
		}		    
		
		double[] timeArrayB = new double[averageB.size()];
		for(int i = 0; i < averageB.size(); i++) {
			timeArrayB[i] = averageB.get(i).getMillis();
		}	
		
		DescriptiveStatistics statisticsA = new DescriptiveStatistics(timeArrayA);
		DescriptiveStatistics statisticsB = new DescriptiveStatistics(timeArrayB);
		
		double stdA = statisticsA.getStandardDeviation();
		double stdB = statisticsB.getStandardDeviation();
		
		return stdA - stdB;
	}
	
}
