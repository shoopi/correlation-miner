package main.java.nl.tue.ieis.is.correlation.helper;

import java.util.Comparator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import main.java.nl.tue.ieis.is.correlation.schema.xes_simple.Log.Trace.Event;

public class EventTimesComparer implements Comparator<Event> {
	@Override
	public int compare(Event e1, Event e2) {
		//2021-11-03T00:00:00.000+01:00
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'.'SSSZ");
		DateTime dt1 = fmt.parseDateTime(e1.getDate().getValue());
		DateTime dt2 = fmt.parseDateTime(e2.getDate().getValue());
		//System.out.println(dt1.toString() + "   -   " + dt2.toString() + "   -   result: " + dt1.compareTo(dt2));
		//return dt1.isBefore(dt2)?-1: dt1.isAfter(dt2)?1: 0;
		return dt1.compareTo(dt2);
	}
}
