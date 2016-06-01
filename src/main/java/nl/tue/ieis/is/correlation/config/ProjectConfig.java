package main.java.nl.tue.ieis.is.correlation.config;

public class ProjectConfig {
	
	public static String projectPath = "C:/Users/GET/Dropbox/SHARE-Workspace/CorrelationMiner_Web/";
	public static String desktopPath = "C:/Users/GET/Desktop/";
	public static String xesFilePath = ProjectConfig.projectPath + "src/main/resource/";

	/*
	public static void changeProjectPath(String machine_user, String eclipseWorkbenchFolder) {
		projectPath = "C:/Users/" + machine_user + "/Dropbox/" + eclipseWorkbenchFolder + "/CorrelationMiner_Web/";
		desktopPath  = "C:/Users/" + machine_user+ "/Desktop/";
		xesFilePath = ProjectConfig.projectPath + "src/main/resource/";
	}
	*/
	
	public static String fileNameWithNoID(String filename) {
		return filename.substring(0, filename.length()-4) + "_noID.xes";
	}
	
	public static String fileNameWithTraceGenerated(String filename) {
		return filename.substring(0, filename.length()-4) + "_[GENERATED_LOG].xes";
	}
}
