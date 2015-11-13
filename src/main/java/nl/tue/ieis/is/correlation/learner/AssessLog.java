package main.java.nl.tue.ieis.is.correlation.learner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import main.java.nl.tue.ieis.is.correlation.evaluation.ApplicationFuzzyResult;
import main.java.nl.tue.ieis.is.correlation.graph.GraphUtil;
import main.java.nl.tue.ieis.is.correlation.graph.Edge;
import main.java.nl.tue.ieis.is.correlation.graph.Node;
import main.java.nl.tue.ieis.is.correlation.helper.RealEdgeDurationComparer;
import main.java.nl.tue.ieis.is.correlation.objects.EventObject;
import main.java.nl.tue.ieis.is.correlation.objects.PossibleEdge;
import main.java.nl.tue.ieis.is.correlation.objects.RealEdge;
import main.java.nl.tue.ieis.is.correlation.objects.TraceObject;
import main.java.nl.tue.ieis.is.correlation.schema.xes_simple.Log;
import main.java.nl.tue.ieis.is.correlation.schema.xes_simple.Log.Trace;
import main.java.nl.tue.ieis.is.correlation.schema.xes_simple.Log.Trace.Event;
import main.java.nl.tue.ieis.is.correlation.milp.*;

import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class AssessLog {
	private static String startActivity;
	private static String endActivity;
	private static PrintWriter writer;
	private static Set<RealEdge> edgeSet;
	private static List<Node> nodes;
	
	public static void main(String[] args) throws JAXBException, FileNotFoundException, UnsupportedEncodingException {
		String fileLocation = 	
				 //"resources/4 loan-application_noLoop2.xes";
				 "resources/insurance.xes";
		 List<RealEdge> edges = loadLogFile(fileLocation);
		 //ApplicationFuzzyResult fuzzy = new ApplicationFuzzyResult();
		 //checkCompleteness(edgeSet, fuzzy);
		 Map<RealEdge, Integer> occuranceMap = findEdgeOccurance(edges, edgeSet);
		 List<PossibleEdge> edgeList = calculateEdgeDuration(edges, edgeSet, occuranceMap);
		 drawMILPGraph(nodes, edgeList);
	 }
	 
	private List<PossibleEdge> possibleEdges;
	public List<PossibleEdge> getPossibleEdges() { return possibleEdges; }

	public AssessLog(String fileLocation) {
		 List<RealEdge> edges;
		try {
			edges = loadLogFile(fileLocation);
			 Map<RealEdge, Integer> occuranceMap = findEdgeOccurance(edges, edgeSet);
			 this.possibleEdges = calculateEdgeDuration(edges, edgeSet, occuranceMap);
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	 
	 
	 
	 
	 private static List<RealEdge> loadLogFile(String fileLocation) throws FileNotFoundException, UnsupportedEncodingException {
		 List<RealEdge> edges = new ArrayList<RealEdge>();
		 try {
			 File file = new File(fileLocation);
			 writer = new PrintWriter("logging.txt", "UTF-8");
			 
			JAXBContext jaxbContext = JAXBContext.newInstance(Log.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Log log = (Log) jaxbUnmarshaller.unmarshal(file);
			
			//Set<EventObject> activitySet = new HashSet<EventObject>();
			edgeSet = new HashSet<RealEdge>();
			List<EventObject> wholeLog = new ArrayList<EventObject>();
			List<TraceObject> traces = new ArrayList<TraceObject>();
			for(Trace trace : log.getTrace()) {
				List<Event> eventList = trace.getEvent();
				List<EventObject> events = new ArrayList<EventObject>();
				for(Event event : eventList) {
					EventObject eventObject = new EventObject();
					for(main.java.nl.tue.ieis.is.correlation.schema.xes_simple.Log.Trace.Event.String string : event.getString()) {
						if(string.getKey().contentEquals("concept:name")) {
							eventObject.setActivity(string.getValue());
						} else if (string.getKey().contentEquals("org:resource")) {
							eventObject.setUser(string.getValue());
						} else if (string.getKey().contentEquals("lifecycle:transition")) {
							eventObject.setStatus(string.getValue());
						} else if (string.getKey().contentEquals("result")) {
							eventObject.setResult(string.getValue());
						}
						DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'.'SSSZ");
						eventObject.setTimestamp(fmt.parseDateTime(event.getDate().getValue()));
					}
					events.add(eventObject);
					wholeLog.add(eventObject);
				}
				traces.add(new TraceObject(events));
			}
			
			startActivity = traces.get(0).getEvents().get(0).getActivity();
			endActivity = traces.get(traces.size()-1).getEvents().get(traces.get(traces.size()-1).getEvents().size()-1).getActivity();
			
			nodes = new ArrayList<Node>();
			Set<EventObject> activitySet = new HashSet<EventObject>(wholeLog);			
			for(EventObject event : activitySet) {
				int occ = Collections.frequency(wholeLog, event);
				nodes.add(new Node(event.getActivity(), occ));
			}
			/*
			System.out.println("# Traces: " + traces.size() );//+ " == " + " # Activities: " + activitySet.size());
			System.out.println("Start Activity: " + startActivity + " == End Activity: " + endActivity);
			writer.println("Start Activity: " + startActivity + " == End Activity: " + endActivity);

			System.out.println("====Find all Edges======");
			writer.println("====Find all Edges======");
			*/
			
			for(TraceObject trace : traces) {
				for(int i = 0 ; i < trace.getEvents().size() - 1 ; i++) {
					edges.add(new RealEdge(trace.getEvents().get(i).getActivity(), trace.getEvents().get(i+1).getActivity(), 
							new Duration(trace.getEvents().get(i).getTimestamp(),trace.getEvents().get(i+1).getTimestamp())));
					//System.out.println(i + ". " + edges.get(i).getActivity1() + " --> " + edges.get(i).getActivity2() + "(" + edges.get(i).getDuration() + ")");
					//writer.println(i + ". " + edges.get(i).getActivity1() + " --> " + edges.get(i).getActivity2() + "(" + edges.get(i).getDuration() + ")");
					edgeSet.add(new RealEdge(trace.getEvents().get(i).getActivity(), trace.getEvents().get(i+1).getActivity(), 
							new Duration(trace.getEvents().get(i).getTimestamp(),trace.getEvents().get(i+1).getTimestamp())));
				}
				//System.out.println("\n");
				//writer.println("\n");
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			writer.println(e.getMessage());
			e.printStackTrace();
		}
		return edges;
 	}
	
	private static void drawMILPGraph(List<Node> nodes, List<PossibleEdge> possibleEdges) {
		Map<String, Integer> incoming = new HashMap<String, Integer>();
		Map<String, Integer> outgoing = new HashMap<String, Integer>();
		for(Node n : nodes) {
			if(n.getActivityName().contentEquals(startActivity)){
				incoming.put(n.getActivityName(), 0);
				outgoing.put(n.getActivityName(), n.getOccurance());
			} else if (n.getActivityName().contentEquals(endActivity)) {
				incoming.put(n.getActivityName(), n.getOccurance());
				outgoing.put(n.getActivityName(), 0);
			} else {
				incoming.put(n.getActivityName(), n.getOccurance());
				outgoing.put(n.getActivityName(), n.getOccurance());
			}
		}
		
		MILPCalculator milp = new MILPCalculator(possibleEdges, incoming, outgoing);
		milp.setAllEdge(true);
		Map<PossibleEdge, Integer> result = milp.calculate();
		GraphUtil graph = new GraphUtil(nodes, result);
		graph.draw("Fuzzy Result");
	}


	private static List<PossibleEdge> calculateEdgeDuration(List<RealEdge> edges, Set<RealEdge> edgeSet, Map<RealEdge, Integer> occurance) {
		//System.out.println("\n\n====Accumulating Edges======");
		//writer.println("\n\n====Accumulating Edges======");
		List<RealEdge> finalRealEdge = new ArrayList<RealEdge>();
		for(RealEdge e : edgeSet) {
			List<RealEdge> temp = new ArrayList<RealEdge>();
			for(RealEdge e2 : edges) {
				if(e.getActivity1().contentEquals(e2.getActivity1()) && e.getActivity2().contentEquals(e2.getActivity2())) {
					temp.add(e2);
				}
			}
			long tempMillis = 0;
			for(RealEdge t : temp) {
				tempMillis = tempMillis + t.getDuration().getMillis();
			}
			finalRealEdge.add(new RealEdge(temp.get(0).getActivity1(), temp.get(0).getActivity2(), new Duration(tempMillis/temp.size())));
		}
		//System.out.println("\n====Aggregate Edges======");
		//writer.println("\n====Aggregate Edges======");
		int counter = 1;
		Collections.sort(finalRealEdge, new RealEdgeDurationComparer());
		List<PossibleEdge> possibleEdges = new ArrayList<PossibleEdge>();
		for(RealEdge e : finalRealEdge) {
			//System.out.println(counter + ". "+ e.getActivity1() + " --> " + e.getActivity2() + " === dur(" +e.getDuration() + ") === occ(" + occurance.get(e) + ")");
			//writer.println(counter + ". " + e.getActivity1() + " --> " + e.getActivity2() + " === (" +e.getDuration() + ") === occ(" + occurance.get(e) + ")");
			possibleEdges.add(new PossibleEdge(e.getActivity1(), e.getActivity2(), e.getDuration(), 0.5));
			counter++;
		}
		return possibleEdges;
	}


	private static void checkCompleteness(Set<RealEdge> edgeSet, ApplicationFuzzyResult fuzzy) {
		System.err.println("size fuzzy edge: " + fuzzy.getEdges().size() + " == size edge set: " + edgeSet.size());
		for(Edge e : fuzzy.getEdges()) {
			boolean find = false;
			for(RealEdge e2 : edgeSet) {
				if(e.getSource().getActivityName().contentEquals(e2.getActivity1()) && e.getDest().getActivityName().contentEquals(e2.getActivity2())) {
					find = true;
					break;
				}
			}
			if(!find) {
				System.err.println("[NOT FOUND] " + e.getSource() + " --> " + e.getDest());
			}
		}
		
		for(RealEdge e2 : edgeSet) {
			boolean find = false;
			for(Edge e : fuzzy.getEdges()) {
				if(e.getSource().getActivityName().contentEquals(e2.getActivity1()) && e.getDest().getActivityName().contentEquals(e2.getActivity2())) {
					find = true;
					break;
				}
			}
			if(!find) {
				System.err.println("[NOT FOUND] " + e2.getActivity1() + " --> " + e2.getActivity2());
			}
		}
	}

	private static Map<RealEdge, Integer> findEdgeOccurance(List<RealEdge> edges, Set<RealEdge> edgeSet) {
		Map<RealEdge, Integer> realEdgeOccurance = new HashMap<RealEdge, Integer>();
		for(RealEdge e1 : edgeSet) {
			int occurance = 0;
			for(RealEdge e2 : edges) {
				if(e1.equals(e2)) {
					occurance++;
				}
			}
			realEdgeOccurance.put(e1, occurance);
		}
		return realEdgeOccurance;
	}
}
