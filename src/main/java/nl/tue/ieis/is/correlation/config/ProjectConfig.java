package main.java.nl.tue.ieis.is.correlation.config;

public class ProjectConfig {
	
	public static String projectPath = "C:/Users/GET/Dropbox/SHARE-Workspace/CorrelationMiner_Web";
	
	public static String xesFilePath = ProjectConfig.projectPath + "/src/main/resource/";
	
	public static String fileNameWithNoID(String filename) {
		return filename.substring(0, filename.length()-4) + "_noID.xes";
	}
}
