package main.java.nl.tue.ieis.is.correlation.graph;

import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import main.java.nl.tue.ieis.is.correlation.objects.PossibleRelation;
import main.java.nl.tue.ieis.is.correlation.objects.SimpleEventObject;
import main.java.nl.tue.ieis.is.correlation.objects.SimpleTraceObject;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.alg.AllDirectedPaths;

public class TraceGraph {
	
	private SimpleDirectedGraph<SimpleEventObject, PossibleRelation> graph;
	
	private Set<SimpleEventObject> nodeList;
	private List<PossibleRelation> edgeList;
	private String start;
	private String end;
	
	public TraceGraph(List<PossibleRelation> edgeList, String start, String end)
			throws HeadlessException {
		super();
		graph = new SimpleDirectedWeightedGraph<SimpleEventObject, PossibleRelation>(PossibleRelation.class);
		this.edgeList = edgeList;
		addNodesToGraph();
		addPossibleEdgesToGraph();
		this.start = start;
		this.end = end;
	}
	
	private void addNodesToGraph() {
		List<SimpleEventObject> allEvents = new ArrayList<SimpleEventObject>();
		for(PossibleRelation pr : edgeList) {
			allEvents.add(pr.getSource());
			allEvents.add(pr.getDest());
		}
		nodeList = new LinkedHashSet<SimpleEventObject>(allEvents);
		for(SimpleEventObject n : nodeList) {
			graph.addVertex(n);
		}
	}
	
	private void addPossibleEdgesToGraph() {
		for(PossibleRelation pr : edgeList) {
			graph.addEdge(pr.getSource(), pr.getDest(), pr); 
		}
	}
	
	public List<SimpleTraceObject> createTraces() {
		AllDirectedPaths<SimpleEventObject, PossibleRelation> pathConstructor = new AllDirectedPaths<SimpleEventObject, PossibleRelation>(graph);
		Set<SimpleEventObject> startNodes = new HashSet<SimpleEventObject>();
		Set<SimpleEventObject> endNodes = new HashSet<SimpleEventObject>();
		for(SimpleEventObject event : graph.vertexSet()) {
			if(event.getActivity().contentEquals(start))
				startNodes.add(event);
			else if (event.getActivity().contentEquals(end))
				endNodes.add(event);
		}
		List<GraphPath<SimpleEventObject, PossibleRelation>> pathList = pathConstructor.getAllPaths(startNodes, endNodes, true, null);
		List<SimpleTraceObject> traces = new ArrayList<SimpleTraceObject>();
		for(GraphPath<SimpleEventObject, PossibleRelation> p : pathList) {
			if(p.getStartVertex().getActivity().contentEquals(start) && p.getEndVertex().getActivity().contentEquals(end)) {
				LinkedHashSet<SimpleEventObject> events = new LinkedHashSet<SimpleEventObject>();
				for(PossibleRelation pr : p.getEdgeList()) {
					events.add(pr.getSource());
					events.add(pr.getDest());
				}
				traces.add(new SimpleTraceObject(new ArrayList<SimpleEventObject> (events)));
			}
		}
		return traces;
	}

}
