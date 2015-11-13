package main.java.nl.tue.ieis.is.correlation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JApplet;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import main.java.nl.tue.ieis.is.correlation.data.DataProducrer;
import main.java.nl.tue.ieis.is.correlation.duration.DurationManager;
import main.java.nl.tue.ieis.is.correlation.graph.Edge;
import main.java.nl.tue.ieis.is.correlation.graph.GraphUtil;
import main.java.nl.tue.ieis.is.correlation.graph.Node;
import main.java.nl.tue.ieis.is.correlation.helper.EdgeDurationComparer;
import main.java.nl.tue.ieis.is.correlation.helper.EdgeHeuristicComparer;
import main.java.nl.tue.ieis.is.correlation.helper.EdgeHeuristicStandardDeviationComparer;
import main.java.nl.tue.ieis.is.correlation.helper.EventTimesComparer;
import main.java.nl.tue.ieis.is.correlation.helper.FollowProbabilityComparer;
import main.java.nl.tue.ieis.is.correlation.objects.ActivityTimeArray;
import main.java.nl.tue.ieis.is.correlation.objects.EventObject;
import main.java.nl.tue.ieis.is.correlation.objects.PossibleEdge;
import main.java.nl.tue.ieis.is.correlation.objects.WeaklyFollowProb;
import main.java.nl.tue.ieis.is.correlation.schema.xes_simple.Log;
import main.java.nl.tue.ieis.is.correlation.schema.xes_simple.Log.Trace;
import main.java.nl.tue.ieis.is.correlation.schema.xes_simple.Log.Trace.Event;
import main.java.nl.tue.ieis.is.correlation.learner.AssessLog;
import main.java.nl.tue.ieis.is.correlation.milp.*;
import main.java.nl.tue.ieis.is.correlation.config.ProjectConfig;

import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.rits.cloning.Cloner;


public class logManager {
	private String startActivity;
	private String endActivity;
	
	private List<Node> nodes = new ArrayList<Node>();
	
	private boolean isStdDeviationEnabled = false;
	private double psThreshold = 0.8;
	private boolean isDataProducer = true;
	private boolean isGreedyTimeMapping = true;
	private BufferedImage correlationImage;
	
	private double recall_greedy = 0.0, recall_equal = 0.0, precision_greedy = 0.0, precision_equal = 0.0;	
			
	
	public String getStartActivity() {
		return startActivity;
	}

	public void setStartActivity(String startActivity) {
		this.startActivity = startActivity;
	}

	public String getEndActivity() {
		return endActivity;
	}

	public void setEndActivity(String endActivity) {
		this.endActivity = endActivity;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public boolean isStdDeviationEnabled() {
		return isStdDeviationEnabled;
	}

	public void setStdDeviationEnabled(boolean isStdDeviationEnabled) {
		this.isStdDeviationEnabled = isStdDeviationEnabled;
	}

	public double getPsThreshold() {
		return psThreshold;
	}

	public void setPsThreshold(double psThreshold) {
		this.psThreshold = psThreshold;
	}

	public boolean isDataProducer() {
		return isDataProducer;
	}

	public void setDataProducer(boolean isDataProducer) {
		this.isDataProducer = isDataProducer;
	}

	public boolean isGreedyTimeMapping() {
		return isGreedyTimeMapping;
	}

	public void setGreedyTimeMapping(boolean isGreedyTimeMapping) {
		this.isGreedyTimeMapping = isGreedyTimeMapping;
	}
	
	public BufferedImage getCorrelationImage() {
		return correlationImage;
	}

	public void setCorrelationImage(BufferedImage correlationImage) {
		this.correlationImage = correlationImage;
	}

	public JApplet run(String username, String filename) {
		
		if(isDataProducer)
			DataProducrer.createDirtyLog(username, filename); 
		
		DurationManager.equalSize = true;
		System.out.println("===***=== Time mapping is set to **EQUAL_SIZE** method. ===***=== ");
		List<ActivityTimeArray> activityTimeList = loadLogFile(ProjectConfig.xesFilePath + "/" + username + "/" + ProjectConfig.fileNameWithNoID(filename));
		List<WeaklyFollowProb> followProababilities = calculateHappeningProbability(activityTimeList);
		List<WeaklyFollowProb> filteredFollowProb = filterProbabilityThreshold(followProababilities);
		List<PossibleEdge> sortedDurationList = getTimeDiffSortedList(activityTimeList, filteredFollowProb, false);
		
		Cloner cloner = new Cloner();

		Map<PossibleEdge, Integer> milpEdgeList = runMILP(sortedDurationList);

		JApplet applet = null;
		if(!isDataProducer) {
			GraphUtil graphUtil = new GraphUtil(nodes, milpEdgeList);
			applet = graphUtil.draw("MILP");		
			correlationImage = graphUtil.generatePicture(ProjectConfig.xesFilePath + "/" + username + "/" + ProjectConfig.fileNameWithNoID(filename) + ".png" );		
		} else {
			Map<PossibleEdge, Integer> clone1 = cloner.deepClone(milpEdgeList);
			compareWholePossibleEdge(sortedDurationList, ProjectConfig.xesFilePath + "/" + username + "/" + filename);
			double equalSizeExperiment = compare(milpEdgeList, ProjectConfig.xesFilePath + "/" + username + "/" + filename, DurationManager.equalSize);
			
			DurationManager.equalSize = false;
			System.out.println("===***=== Time mapping is set to **GREEDY** method. ===***=== ");
	
			List<PossibleEdge> sortedDurationList2 = getTimeDiffSortedList(activityTimeList, filteredFollowProb, false);
			Map<PossibleEdge, Integer> milpEdgeList2 = runMILP(sortedDurationList2);
			Map<PossibleEdge, Integer> clone2 = cloner.deepClone(milpEdgeList2);
			compareWholePossibleEdge(sortedDurationList2,ProjectConfig.xesFilePath + "/" + username + "/" + filename);
			double greedyExperiment = compare(milpEdgeList2, ProjectConfig.xesFilePath + "/" + username + "/" + filename, DurationManager.equalSize);
			
			double roundedRecall = 0.0;
			double roundedPrecision = 0.0;
			
			if(equalSizeExperiment >= greedyExperiment ) {
				System.out.println("**[EQUAL_SIZE]** has been accepted as the final model.");
				GraphUtil graphUtil = new GraphUtil(nodes, clone1);
				applet = graphUtil.draw("MILP");		
				correlationImage = graphUtil.generatePicture(ProjectConfig.xesFilePath + "/" + username + "/" + ProjectConfig.fileNameWithNoID(filename) + ".png" );
				roundedRecall = (double) Math.round(recall_equal * 100) / 100;
				roundedPrecision = (double) Math.round(precision_equal * 100) / 100;
			} else {
				System.out.println("**[GREEDY]** has been accepted as the final model.");
				GraphUtil graphUtil = new GraphUtil(nodes, clone2);
				applet = graphUtil.draw("MILP");	
				correlationImage = graphUtil.generatePicture(ProjectConfig.xesFilePath + "/" + username + "/" + ProjectConfig.fileNameWithNoID(filename) + ".png" );
				roundedRecall = (double) Math.round(recall_greedy * 100) / 100;
				roundedPrecision = (double) Math.round(precision_greedy * 100) / 100;
			}
			System.out.println("\n ==================== RESULT ====================");
			System.out.println("||.............................................||");
			System.out.println("||.........FINAL RECALL: " + String.format("%.2f", roundedRecall) + "..................||");
			System.out.println("||.............................................||");
			System.out.println("||........FINAL PRECISION: " + String.format("%.2f", roundedPrecision) + "................||");
			System.out.println("||.............................................||");
			System.out.println("=================================================");
		} 
		return applet;
	}

	private List<ActivityTimeArray> loadLogFile(String fileLocation) {
		File file = new File(fileLocation);
		List<ActivityTimeArray> timeArrayList = new ArrayList<ActivityTimeArray>();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Log.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Log log = (Log) jaxbUnmarshaller.unmarshal(file);
			System.out.println("# of events in the log: " + log.getTrace().get(0).getEvent().size());
			
			Trace trace = log.getTrace().get(0);
			List<Event> eventList = trace.getEvent();
			Collections.sort(eventList, new EventTimesComparer());
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
				}
				DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'.'SSSZ");
				eventObject.setTimestamp(fmt.parseDateTime(event.getDate().getValue()));
				events.add(eventObject);
			}
			
			startActivity = events.get(0).getActivity();
			endActivity = events.get(events.size()-1).getActivity();
			
			Set<EventObject> activitySet = new HashSet<EventObject>(events);			

			for(EventObject event : activitySet) {
				int occ = Collections.frequency(events, event);
				List<DateTime> timestamps = new ArrayList<DateTime>();
				for(EventObject e1 : events) {
					if(e1.getActivity().contentEquals(event.getActivity())) {
						timestamps.add(e1.getTimestamp());
					}	
				}
				timeArrayList.add(new ActivityTimeArray(event.getActivity(), timestamps));
				nodes.add(new Node(event.getActivity(), occ));

			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return timeArrayList;
	}
	
	private  List<WeaklyFollowProb> calculateHappeningProbability(List<ActivityTimeArray> timeArrayList) {
		List<WeaklyFollowProb> follows = new ArrayList<WeaklyFollowProb>();
		for(ActivityTimeArray a : timeArrayList) {
			for(ActivityTimeArray b : timeArrayList) {
				if(!b.getActivity().contentEquals(a.getActivity())) {
						int total = 0;
						int sum = 0;
						for(DateTime dt1 : a.getTimestamps()) {
							for(DateTime dt2 : b.getTimestamps()) {
								if(dt1.isBefore(dt2)) {
									sum ++;
								}
								total ++;
							}
						}
						double portion = (double)sum/(double)total;	
						follows.add(new WeaklyFollowProb(a.getActivity(), b.getActivity(), portion));
				}
			}
		}
		return follows;
	}
	
	private List<WeaklyFollowProb> filterProbabilityThreshold(List<WeaklyFollowProb> follows) {
		Collections.sort(follows, new FollowProbabilityComparer());

		List<WeaklyFollowProb> removedFollows = new ArrayList<WeaklyFollowProb>();
		System.out.println("**** Threshold is manually set to " + psThreshold + " of all values." );
		int removedIndex = (int)(follows.size() * psThreshold);
		for(int i = removedIndex; i <follows.size(); i++){
			removedFollows.add(follows.get(i));
		}
		System.out.println("Before P/S threshold we had " + follows.size() + " elements." );
		follows.removeAll(removedFollows);
		System.out.println("After P/S threshold we have " + follows.size() + " elements. [Removed: " + removedFollows.size() + "]");
		return follows;
	}

	private List<PossibleEdge> getTimeDiffSortedList(List<ActivityTimeArray> activityTimeList, List<WeaklyFollowProb> follows, boolean onlyDuration) {
		List<PossibleEdge> possibleEdges = new ArrayList<PossibleEdge>();
		System.out.println("\n=================== Duration Matrix ===================");
		for(WeaklyFollowProb f : follows) {
			ActivityTimeArray a = getTimeList(activityTimeList, f.getActivity1());
			ActivityTimeArray b = getTimeList(activityTimeList, f.getActivity2());
			
			/***** Remove for Standard Deviation *****/
			if(!isStdDeviationEnabled) {
				Duration d = DurationManager.getAverageTimeDiff(a,b);
				if(d.getMillis() >= 0) {
					possibleEdges.add(new PossibleEdge(f.getActivity1(), f.getActivity2(), d, f.getProb()));
					//System.out.println("[ADDED] " + f.toString());
				} else {
					//System.err.println("[REMOVED] " + f.toString() + " == duration: " + d);
				}
			} else {
				Double d = DurationManager.getAverageStdDeviationTimeDiff(a,b);
				possibleEdges.add(new PossibleEdge(f.getActivity1(), f.getActivity2(), d, f.getProb()));
			}
		}
		System.out.println();
		if(onlyDuration)
			Collections.sort(possibleEdges, new EdgeDurationComparer());
		else {
			if(!isStdDeviationEnabled)
				Collections.sort(possibleEdges, new EdgeHeuristicComparer());
			else Collections.sort(possibleEdges, new EdgeHeuristicStandardDeviationComparer());
		}

		System.out.println("\n FINAL SELECTED EDGE LIST:");
		for(PossibleEdge pe : possibleEdges) {
			System.out.println(pe);
		}
		System.out.println("======================================\n");
		return possibleEdges;
	}
	
	
	private  Map<PossibleEdge, Integer> runMILP(List<PossibleEdge> possibleEdges) { 
		System.out.println("\n=================== Starting ILP ===================");
		Map<String, Integer> outRemaining1 = new HashMap<String, Integer>();
		Map<String, Integer> inRemaining1 = new HashMap<String, Integer>();
		initRemainingMaps(outRemaining1, inRemaining1);
		
		MILPCalculator milp = new MILPCalculator(possibleEdges, inRemaining1, outRemaining1);
		milp.setAllEdge(false);
		Map<PossibleEdge, Integer> milpEdgeList = milp.calculate();
		GraphUtil graphUtil = new GraphUtil(nodes, milpEdgeList);		
		System.out.println("\n*******************===Finding Cycles====*******************");
		System.out.println("# of edges in the graph: " + graphUtil.getGraph().edgeSet().size());
		JohnsonSimpleCycles<Node, Edge> johnson = new JohnsonSimpleCycles<Node, Edge>(graphUtil.getGraph());
		List<List<Node>> cycleList = johnson.findSimpleCycles();
		
		while(cycleList.size() > 0) {
			List<PossibleEdge> finalCycleRemoval = new ArrayList<PossibleEdge>();
			for(List<Node> cycle : cycleList) {
				Map<PossibleEdge, Double> cycleMap = new HashMap<PossibleEdge, Double>();
				for(int i = cycle.size() - 1; i >= 0 ; i--) {
					try{
						for(PossibleEdge pe : milpEdgeList.keySet()) {
							if(pe.getActivity1().contentEquals(cycle.get(i).getActivityName()) && 
									pe.getActivity2().contentEquals(cycle.get(i-1).getActivityName())) {
									double ratio = 0.0;
									if(pe.getDuration() != null)
										ratio = (double)(double)(pe.getDuration().getMillis() / (double)pe.getProbability());
									else
										ratio = (double)(pe.getStdDevDiff() / (double)pe.getProbability());
								System.out.println(pe.getActivity1() + " --> " + pe.getActivity2() + " (" + ratio + ")");
								cycleMap.put(pe, ratio);
								break;
							}
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						for(PossibleEdge pe : milpEdgeList.keySet()) {
							if(pe.getActivity1().contentEquals(cycle.get(0).getActivityName()) && pe.getActivity2().contentEquals(cycle.get(cycle.size()-1).getActivityName())) {
								double ratio = 0.0;
								if(pe.getDuration() != null)
									ratio = (double)(double)(pe.getDuration().getMillis() / (double)pe.getProbability());
								else
									ratio = (double)(pe.getStdDevDiff() / (double)pe.getProbability());
							System.out.println(pe.getActivity1() + " --> " + pe.getActivity2() + " (" + ratio + ")");
								System.out.println(pe.getActivity1() + " --> " + pe.getActivity2() + " (" + ratio + ")");
								cycleMap.put(pe, ratio);
								break;
							}
						}
					}
				}
				
				Double max = -10000000000000000.0;
				PossibleEdge toBeRemovedPossibleEdge = null;
				for(Map.Entry<PossibleEdge, Double> entry : cycleMap.entrySet()) {
					if(entry.getValue() > max) {
						max = entry.getValue();
						toBeRemovedPossibleEdge = entry.getKey();
					}
				}
				finalCycleRemoval.add(toBeRemovedPossibleEdge);
				System.out.println();
			}
			
			System.out.println("======================================");
			System.out.println("# of loop: " + cycleList.size() + " = # of edges to be removed: " + finalCycleRemoval.size());
			
			for(PossibleEdge p : finalCycleRemoval) 
				System.out.println("[REMOVED] " + p.getActivity1() + " --> " + p.getActivity2());
			
			System.out.println("# of possible edges [BEFORE] loop removal: " + milp.getPossibleEdges().size());
			if(milp.getPossibleEdges().removeAll(finalCycleRemoval)) {
				System.out.println("# of possible edges [AFTER] loop removal: " + milp.getPossibleEdges().size());
				System.out.println();
				milpEdgeList.clear();
				milpEdgeList = milp.calculate();
				if(milpEdgeList.size() == 0) break;
				graphUtil = new GraphUtil(nodes, milpEdgeList);
				johnson = new JohnsonSimpleCycles<Node, Edge>(graphUtil.getGraph());
				cycleList = johnson.findSimpleCycles();
			}
		}
		return milpEdgeList;
	}

	
	private  ActivityTimeArray getTimeList(List<ActivityTimeArray> timeArrayList, String activityName) {
		for(ActivityTimeArray a : timeArrayList) {
			if(a.getActivity().contentEquals(activityName))
				return a;
		}
		return null;
	}
	
	private  void initRemainingMaps(Map<String, Integer> outRemaining, Map<String, Integer> inRemaining) {
		for (Node n : nodes) {
			inRemaining.put(n.getActivityName(), n.getOccurance());
			outRemaining.put(n.getActivityName(), n.getOccurance());
			if(n.getActivityName().contentEquals(startActivity))
				inRemaining.put(startActivity, 0);
			if(n.getActivityName().contentEquals(endActivity))
				outRemaining.put(endActivity, 0);
		}
	}
	
	private  void compareWholePossibleEdge(List<PossibleEdge> allPossibleEdge, String fileLocation) {
		System.out.println("\n === Compare all ''possible edges'' with the original log === ");
		AssessLog log = new AssessLog(fileLocation);
		List<PossibleEdge> comparision = new ArrayList<PossibleEdge>();
		for(PossibleEdge e1: log.getPossibleEdges()){
			for(PossibleEdge pe : allPossibleEdge) {
				if(e1.equals(pe)) {
					comparision.add(e1);
				}
			}
		}
		List<PossibleEdge> finalRemainingEdges = log.getPossibleEdges();
		finalRemainingEdges.removeAll(comparision);
		System.out.println("Remaining size: " + finalRemainingEdges.size());
		for(PossibleEdge e : finalRemainingEdges) {
			System.out.println("[" + e.getActivity1() + " --> " + e.getActivity2() + "] is not part of possible edges.");
		}
	}
	
	private double compare(Map<PossibleEdge, Integer> modelEdges, String realFile, boolean equalSize) {
		AssessLog log = new AssessLog(realFile);
		List<PossibleEdge> comparision = new ArrayList<PossibleEdge>();
		List<PossibleEdge> finalRemainingEdges = log.getPossibleEdges();
		
		System.out.println("\n === Edges that are in the original log but not in the correlation miner model [FN]) ===");
		for(PossibleEdge e1: log.getPossibleEdges()) {
			for(Map.Entry<PossibleEdge, Integer> e2 : modelEdges.entrySet()) {
				if(e1.equals(e2.getKey())) {
					comparision.add(e1);
				}
			}
		}
		
		finalRemainingEdges = log.getPossibleEdges();
		finalRemainingEdges.removeAll(comparision);
		System.out.println("[TP]: " + comparision.size());
		System.out.println("[FN]: " + finalRemainingEdges.size());
		double recall = (double)((double)comparision.size() / (double)(comparision.size() + finalRemainingEdges.size()));
		for(PossibleEdge e : finalRemainingEdges) {
			System.out.println("[" + e.getActivity1() + " --> " + e.getActivity2() + "] is not part of selected edges.");
		}

		System.out.println("\n === Edges that are not in the original log but they are in the correlation miner model [FP]) ===");
		log = new AssessLog(realFile);
		Set<PossibleEdge> comparision2 = new HashSet<PossibleEdge>();		
		for(Map.Entry<PossibleEdge, Integer> e2 : modelEdges.entrySet()) {
			for(PossibleEdge e1: log.getPossibleEdges()) {
				if(e1.equals(e2.getKey())) {
					comparision2.add(e2.getKey());
				}
			}
		}
		Set<PossibleEdge> finalRemainingEdges2 = new HashSet<PossibleEdge>();
		finalRemainingEdges2 = modelEdges.keySet();
		finalRemainingEdges2.removeAll(comparision2);
		System.out.println("[FP]: " + finalRemainingEdges2.size());
		double precision = (double)((double)comparision.size() / (double)(comparision.size() + finalRemainingEdges2.size()));
		for(PossibleEdge e : finalRemainingEdges2) {
			System.out.println("[" + e.getActivity1() + " --> " + e.getActivity2()+ "] is not part of the original log.");
		}
		
		if(equalSize) {
			recall_equal = recall;
			precision_equal = precision;
			System.out.println("[EQUAL_SIZE] RECALL: " + recall);
			System.out.println("[EQUAL_SIZE] PRECISION: " + precision);
		} else {
			recall_greedy = recall;
			precision_greedy = precision;		System.out.println("RECALL: " + recall);
			System.out.println("[GREEDY] RECALL: " + recall);
			System.out.println("[GREEDY] PRECISION: " + precision);
		}
		System.out.println("********************************************************************************");
		
		double result = 0.0;
		if(recall > 0 && precision > 0)
			result = recall * precision;
		
		return result;
	}
}
