package main.java.nl.tue.ieis.is.correlation.objects;

import java.util.List;
import java.util.Map;

import main.java.nl.tue.ieis.is.correlation.graph.Node;

import org.joda.time.DateTime;

public class PossibleEvents {
	private Node sourceNode;
	private List<DateTime> sourceTimestamps;
	private Map<Node, List<DateTime>> allPossibleEvents;
	public Node getSourceNode() {
		return sourceNode;
	}
	public void setSourceNode(Node sourceNode) {
		this.sourceNode = sourceNode;
	}
	public List<DateTime> getSourceTimestamps() {
		return sourceTimestamps;
	}
	public void setSourceTimestamps(List<DateTime> sourceTimestamps) {
		this.sourceTimestamps = sourceTimestamps;
	}
	public Map<Node, List<DateTime>> getAllPossibleEvents() {
		return allPossibleEvents;
	}
	public void setAllPossibleEvents(Map<Node, List<DateTime>> allPossibleEvents) {
		this.allPossibleEvents = allPossibleEvents;
	}
	public PossibleEvents(Node sourceNode, List<DateTime> sourceTimestamps,
			Map<Node, List<DateTime>> allPossibleEvents) {
		super();
		this.sourceNode = sourceNode;
		this.sourceTimestamps = sourceTimestamps;
		this.allPossibleEvents = allPossibleEvents;
	}
}
