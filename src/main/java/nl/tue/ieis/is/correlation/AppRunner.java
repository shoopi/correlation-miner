package main.java.nl.tue.ieis.is.correlation;

import javax.swing.JApplet;
import javax.swing.JFrame;

import main.java.nl.tue.ieis.is.correlation.milp.TraceCalculator;

public class AppRunner{
	
	private static double threshold = 1.0;
	private static boolean stdDeviation = false;
	private static boolean dataProducer = true;
	
	public static boolean logProducer = true;
	
	public static String username = "standalone_application";
	public static String filename = 
			"paper_log.xes";
			//"simple_log.xes";
			//"3XOR_50Case_Uniform.xes";
			//"53_4 loan-application_noLoop.xes";
			//"Exponential_Rand1to100_1000cases_ArrBig.xes";
	/*
	public static void main(String[] args){
		
		//ProjectConfig.changeProjectPath("SHAYA", "Standard");
		
		TraceCalculator.factor = 1; //40000;
		
		logManager lm = new logManager();
		
		lm.setDataProducer(dataProducer);
		//lm.setGreedyTimeMapping(isGreedyTimeMapping);
		lm.setPsThreshold(threshold);
		lm.setStdDeviationEnabled(stdDeviation);
		lm.setTraceLogProducer(logProducer);
		
		JApplet graph = lm.run(username, filename);
		JFrame frame =new JFrame();
	    frame.setSize(800, 800);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setVisible(true);
	    frame.add(graph);
	}
	*/

}
