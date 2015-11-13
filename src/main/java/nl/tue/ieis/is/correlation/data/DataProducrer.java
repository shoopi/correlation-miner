package main.java.nl.tue.ieis.is.correlation.data;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import main.java.nl.tue.ieis.is.correlation.helper.EventTimesComparer;
import main.java.nl.tue.ieis.is.correlation.schema.xes_simple.Log;
import main.java.nl.tue.ieis.is.correlation.schema.xes_simple.Log.Trace;
import main.java.nl.tue.ieis.is.correlation.schema.xes_simple.Log.Trace.Event;
import main.java.nl.tue.ieis.is.correlation.config.*;

public class DataProducrer {
	public static void createDirtyLog(String username, String filename) {
		try {
			File file = new File(ProjectConfig.xesFilePath + "/" + username + "/" + filename);
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
			File file2 = new File(ProjectConfig.xesFilePath + "/" + username + "/" + ProjectConfig.fileNameWithNoID(filename));
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

}
