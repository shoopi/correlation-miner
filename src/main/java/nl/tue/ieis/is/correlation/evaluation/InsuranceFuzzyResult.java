package main.java.nl.tue.ieis.is.correlation.evaluation;

import java.util.ArrayList;
import java.util.List;

import main.java.nl.tue.ieis.is.correlation.graph.*;

public class InsuranceFuzzyResult {
	private List<Node> nodes;
	private List<Edge> edges;

	public List<Edge> getEdges() {
		return edges;
	}
	
	
	
	public List<Node> getNodes() {
		return nodes;
	}



	public InsuranceFuzzyResult() {
		super();
		Node submittedApplication = new Node("Submitted Application", 11647);
		Node partlySubmitedApplication = new Node("Partly Submitted Application", 11647);
		Node preAcceptedApplication = new Node("Pre-accepted Application", 5927);
		Node declineApplication = new Node("Declined Application", 7440);
		Node returnedOffer = new Node("Returned Offer", 2259);
		Node cancelledApplication = new Node("Cancelled Application", 2389);
		Node acceptedOffer = new Node("Accepted Offer",1535);
		Node approvedApplication = new Node("Approved Application", 1536);
		Node selectedOffer = new Node("Selected Offer", 3575);
		Node createOffer = new Node("Created Offer", 3575);
		Node sentOffer = new Node("Sent Offer", 3575);
		Node acceptedApplication = new Node("Accepted Application", 3673);
		Node end = new Node("End", 11647);
		
		nodes = new ArrayList<Node>();
		nodes.add(submittedApplication);
		nodes.add(partlySubmitedApplication);
		nodes.add(preAcceptedApplication);
		nodes.add(declineApplication);
		nodes.add(returnedOffer);
		nodes.add(cancelledApplication);
		nodes.add(acceptedOffer);
		nodes.add(approvedApplication);
		nodes.add(selectedOffer);
		nodes.add(createOffer);
		nodes.add(sentOffer);
		nodes.add(acceptedApplication);
		nodes.add(end);

		
		Edge e1 = new Edge(submittedApplication, partlySubmitedApplication, 11647, 0);
		Edge e2 = new Edge(partlySubmitedApplication, preAcceptedApplication, 5927, 0);
		Edge e3 = new Edge(partlySubmitedApplication, declineApplication, 5719, 0);
		Edge e4 = new Edge(partlySubmitedApplication, cancelledApplication, 1, 0);
		Edge e5 = new Edge(preAcceptedApplication, declineApplication, 1085, 0);
		Edge e6 = new Edge(preAcceptedApplication, acceptedApplication, 3673, 0);
		Edge e7 = new Edge(preAcceptedApplication, cancelledApplication, 1100, 0);
		Edge e8 = new Edge(preAcceptedApplication, end, 69, 0);
		Edge e9 = new Edge(acceptedApplication, selectedOffer, 3575, 0);
		Edge e10 = new Edge(acceptedApplication, declineApplication, 29, 0);
		Edge e11 = new Edge(acceptedApplication, cancelledApplication, 66, 0);
		Edge e12 = new Edge(acceptedApplication, end, 3, 0);
		Edge e13 = new Edge(selectedOffer, createOffer, 3575, 0);
		Edge e14 = new Edge(createOffer, sentOffer, 3575, 0);
		Edge e15 = new Edge(sentOffer, returnedOffer, 2259, 0);
		Edge e16 = new Edge(sentOffer, declineApplication, 33, 0);
		Edge e17 = new Edge(sentOffer, cancelledApplication, 1132, 0);
		Edge e18 = new Edge(sentOffer, end, 151, 0);
		Edge e19 = new Edge(returnedOffer, declineApplication, 574, 0);
		Edge e20 = new Edge(returnedOffer, acceptedOffer, 1535, 0);
		Edge e21 = new Edge(returnedOffer, approvedApplication, 1, 0);
		Edge e22 = new Edge(returnedOffer, cancelledApplication, 90, 0);
		Edge e23 = new Edge(acceptedOffer, approvedApplication, 1535, 0);
		Edge e24 = new Edge(declineApplication, end, 7440, 0);
		Edge e25 = new Edge(approvedApplication, end, 1536, 0);
		Edge e26 = new Edge(cancelledApplication, end, 2389, 0);
		Edge e27 = new Edge(returnedOffer, end, 59, 0);
		
		edges = new ArrayList<Edge>();
		edges.add(e1);
		edges.add(e2);
		edges.add(e3);
		edges.add(e4);
		edges.add(e5);
		edges.add(e6);
		edges.add(e7);
		edges.add(e8);
		edges.add(e9);
		edges.add(e10);
		edges.add(e11);
		edges.add(e12);
		edges.add(e13);
		edges.add(e14);
		edges.add(e15);
		edges.add(e16);
		edges.add(e17);
		edges.add(e18);
		edges.add(e19);
		edges.add(e20);
		edges.add(e21);
		edges.add(e22);
		edges.add(e23);
		edges.add(e24);
		edges.add(e25);
		edges.add(e26);
		edges.add(e27);
	}
}
