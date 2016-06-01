package main.java.nl.tue.ieis.is.correlation.data;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import main.java.nl.tue.ieis.is.correlation.helper.EventTimesComparer;
import main.java.nl.tue.ieis.is.correlation.objects.SimpleEventObject;
import main.java.nl.tue.ieis.is.correlation.objects.SimpleTraceObject;
import main.java.nl.tue.ieis.is.correlation.xes_simple.Log;
import main.java.nl.tue.ieis.is.correlation.xes_simple.Log.Trace;
import main.java.nl.tue.ieis.is.correlation.xes_simple.Log.Trace.Event;
import main.java.nl.tue.ieis.is.correlation.config.*;

public class DataProducrer {
	public static void createDirtyLog(String username, String filename) {
		try {
			File file = new File(ProjectConfig.xesFilePath + username + "/" + filename);
			JAXBContext jaxbContext = JAXBContext.newInstance(Log.class);
			
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Log oldLog = (Log) jaxbUnmarshaller.unmarshal(file);
			System.out.println("Number of Traces in the original log: " + oldLog.getTrace().size());
			
			Log newLog = new Log();
			Trace newTrace = new Trace();
			for(Trace trace : oldLog.getTrace()) {
				List<Event> eventList = trace.getEvent();
				newTrace.getEvent().addAll(eventList);
			}
			Collections.sort(newTrace.getEvent(), new EventTimesComparer());
			
			newLog.getTrace().add(newTrace);
			File file2 = new File(ProjectConfig.xesFilePath + username + "/" + ProjectConfig.fileNameWithNoID(filename));
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(newLog, file2);
			System.out.println("A new log without case identifiers has been created -- Filename: " + ProjectConfig.fileNameWithNoID(filename) + 
					" \n ================================= \n\n");
		  } catch (JAXBException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		  }
	}
	
	public static void produceGeneratedTraceLog(String username, String filename, List<SimpleTraceObject> traces) {
		try {
			File file = new File(ProjectConfig.xesFilePath + "/" + username + "/" + filename);
			JAXBContext jaxbContext = JAXBContext.newInstance(Log.class);
			
			Log newLog = new Log();
			int traceCounter = 0;
			for(SimpleTraceObject traceObject : traces) {
				traceCounter++;
				Trace t = new Trace();
				
				Log.Trace.String st1 = new Log.Trace.String();
				st1.setKey("concept:name");
				st1.setValue(traceCounter + "");
				
				Log.Trace.String st2 = new Log.Trace.String();
				st2.setKey("creator");
				st2.setValue("CorrelationMiner");
				
				t.getString().add(st1);
				t.getString().add(st2);
				
				for(SimpleEventObject eventObject : traceObject.getEvents()) {
					Event e = new Event();
					
					Log.Trace.Event.String str1 = new Log.Trace.Event.String();
					str1.setKey("concept:name");
					str1.setValue(eventObject.getActivity());
					
					Log.Trace.Event.String str2 = new Log.Trace.Event.String();
					str2.setKey("lifecycle:transition");
					str2.setValue("complete");
					
					Log.Trace.Event.String str3 = new Log.Trace.Event.String();
					str3.setKey("org:resource");
					str3.setValue("NOT_SET");
					
					Log.Trace.Event.String str4 = new Log.Trace.Event.String();
					str4.setKey("time:timestamp");
					str4.setValue(eventObject.getTimestamp().toString());
					
					Log.Trace.Event.String str5 = new Log.Trace.Event.String();
					str5.setKey("Activity");
					str5.setValue(eventObject.getActivity());
					
					e.getString().add(str1);
					e.getString().add(str2);
					e.getString().add(str3);
					e.getString().add(str4);
					e.getString().add(str5);
					
					t.getEvent().add(e);
				}
				newLog.getTrace().add(t);
			}
			
			File file2 = new File(ProjectConfig.xesFilePath + "/" + username + "/" + ProjectConfig.fileNameWithTraceGenerated(filename));
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(newLog, file2);
			System.out.println("A new log with case identifiers and traces has been created -- Filename: " + ProjectConfig.fileNameWithTraceGenerated(filename) + 
					" \n ================================= \n\n");
		  } catch (JAXBException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		  }
	}

}
