package main.java.nl.tue.ieis.is.correlation.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import main.java.nl.tue.ieis.is.correlation.utility.*;
import main.java.nl.tue.ieis.is.correlation.config.ProjectConfig;

import org.apache.commons.io.IOUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

import main.java.nl.tue.ieis.is.correlation.*;

public class MainController extends SelectorComposer<Component> {

	private static final long serialVersionUID = -9000079319525018613L;
	@Wire	private 	Button 		uploadIDBtn, uploadNoIDBtn, downloadLogBtn, downloadSampleLogBtn, runSampleLogBtn;
	@Wire	private 	Image 		processImage, correlationImgage;
	@Wire	private 	Vlayout		cmdLogLayout;
	@Wire	private		Slider 		psThreshold;
	@Wire	private 	Textbox 	sourceActivityTxtBox, sinkActivityTxtBox;
	@Wire 	private 	Radiogroup	timeRadio, sampleLogRadio;
	//@Wire	private		Applet 		processApplet;
			
	
	private String username;
	private String filename;
	private String selectedLogForDownload;
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		User user = (User) (Sessions.getCurrent()).getAttribute("user");
		if(user == null) { 
			uploadIDBtn.setDisabled(true); 
			uploadNoIDBtn.setDisabled(true);
		} else {
			username = user.getEmail();
			uploadIDBtn.setDisabled(false);
			uploadNoIDBtn.setDisabled(false); 
		}
		psThreshold.setCurpos(100);
	}
	
	@Listen("onUpload = #uploadIDBtn")
	public void uploadWithID(UploadEvent event) {
		org.zkoss.util.media.Media media = event.getMedia();
		downloadLogBtn.setDisabled(true);
		try {
			String name = media.getName();
			if(!name.substring(name.length()-4, name.length()).contentEquals(".xes")) {
				Messagebox.show("Please upload a file with .xes extension." , "Error", Messagebox.OK, Messagebox.ERROR);
			} else {
				filename = name;
				File output = getOutputFile(username, filename);
				convertInputStreamToFile(username, filename, media.getStreamData(), false);
				try {
					logManager lm = new logManager();
					lm.setDataProducer(true);
					//lm.setGreedyTimeMapping(isGreedyTimeMapping);
					double threshold = (double) ((double)psThreshold.getCurpos() / 100.00);
					lm.setPsThreshold(threshold);
					lm.setStdDeviationEnabled(Boolean.valueOf(timeRadio.getSelectedItem().getValue().toString()));
					lm.run(username, filename);
					correlationImgage.setContent(lm.getCorrelationImage());
					sourceActivityTxtBox.setValue(lm.getStartActivity());
					sinkActivityTxtBox.setValue(lm.getEndActivity());
					downloadLogBtn.setDisabled(false);
					
				} catch (Exception e) {
					Messagebox.show("An Error has occurred! " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
					e.printStackTrace();
				}
				showCMD(output);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Listen("onUpload = #uploadNoIDBtn")
	public void uploadWithoutID(UploadEvent event) {
		org.zkoss.util.media.Media media = event.getMedia();
		downloadLogBtn.setDisabled(true);
		try {
			String name = media.getName();
			if(!name.substring(name.length()-4, name.length()).contentEquals(".xes")) {
				Messagebox.show("Please upload a file with .xes extension." , "Error", Messagebox.OK, Messagebox.ERROR);
			} else {
				File output = getOutputFile(username, name);
				convertInputStreamToFile(username, name, media.getStreamData(), true);
				try {
					logManager lm = new logManager();
					lm.setDataProducer(false);
					//lm.setGreedyTimeMapping(isGreedyTimeMapping);
					double threshold = (double) ((double)psThreshold.getCurpos() / 100.00);
					lm.setPsThreshold(threshold);
					lm.setStdDeviationEnabled(Boolean.valueOf(timeRadio.getSelectedItem().getValue().toString()));
					lm.run(username, name);
					correlationImgage.setContent(lm.getCorrelationImage());
					sourceActivityTxtBox.setValue(lm.getStartActivity());
					sinkActivityTxtBox.setValue(lm.getEndActivity());					
				} catch (Exception e) {
					Messagebox.show("An Error has occurred! " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
					e.printStackTrace();
				}
				showCMD(output);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Listen("onClick=#downloadLogBtn")
	public void downloadNoIDLog() {
		try {
			File file = new File(ProjectConfig.xesFilePath + "/" + username + "/" + ProjectConfig.fileNameWithNoID(filename));
			Filedownload.save(file, "application/xml");
		} catch (FileNotFoundException e) {
			Messagebox.show("An Error has occurred! " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
			e.printStackTrace();
		}
	}
	
	
	private void convertInputStreamToFile(String username, String filename, InputStream is, boolean noID) {
		OutputStream outputStream;
		try {
			File dir = new File(ProjectConfig.xesFilePath + username);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if(noID)	
				filename = ProjectConfig.fileNameWithNoID(filename);
			
			File file = new File(dir, filename);
			if (!file.createNewFile()){}
			outputStream = new FileOutputStream(file);
			IOUtils.copy(is, outputStream);
			outputStream.close();
		} catch (IOException e) {
			Messagebox.show("An Error has occurred! " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
			e.printStackTrace();
		}
	}
	
	private File getOutputFile(String username, String filename) {
		File dir = new File(ProjectConfig.xesFilePath + username);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, ProjectConfig.fileNameWithNoID(filename) + "_output.txt");
		try {
			file.createNewFile();
			PrintStream out = new PrintStream(new FileOutputStream(file));
			System.setOut(out);
			return file;
		} catch (IOException e) {
			Messagebox.show("An Error has occurred! " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
			e.printStackTrace();
			return null;
		}
	}
	
	private void showCMD(File output) {
		cmdLogLayout.getChildren().clear();
		try(BufferedReader br = new BufferedReader(new FileReader(output))) {
		        String line = br.readLine();
		        while (line != null) {
		        	Label lbl = new Label(line);
		        	lbl.setSclass("cmdText");
		        	cmdLogLayout.appendChild(lbl);
		            line = br.readLine();
		        }
		    } catch (Exception e) {
		    	Messagebox.show("An Error has occurred! " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
				e.printStackTrace();
			} 
	}
	
	@Listen("onScroll = #psThreshold")
	public void changePSSlider() {
		showNotify("P/S threshold is set to " + psThreshold.getCurpos() + "%. ", psThreshold);
	}
	
	@Listen("onCheck = #timeRadio")
	public void changeTimeRadio() {
		showNotify("Time mapping is set to " +  timeRadio.getSelectedItem().getLabel(), timeRadio);
	}
	
	@Listen("onCheck = #sampleLogRadio")
	public void changeSampleLog() {
		downloadSampleLogBtn.setDisabled(false);
		runSampleLogBtn.setDisabled(false);
		
		int selected = Integer.parseInt(sampleLogRadio.getSelectedItem().getValue().toString());
		switch (selected) {
		case 1:
			selectedLogForDownload = ProjectConfig.xesFilePath + "/testLog/3XOR_diffExpo_1res_2000cases.xes";
			break;
		case 2:
			selectedLogForDownload = ProjectConfig.xesFilePath + "/testLog/3XOR_diffUniform_1res_2000cases.xes";
			break;
		case 3:
			selectedLogForDownload = ProjectConfig.xesFilePath + "/testLog/4 loan-application_noLoop.xes";
			break;
		case 4:
			selectedLogForDownload = ProjectConfig.xesFilePath + "/testLog/Exponential_Rand1to100_1000cases_ArrBig.xes";
			break;
		case 5:
			selectedLogForDownload = ProjectConfig.xesFilePath + "/testLog/Exponential_Rand1to100_1000cases_ArrSame.xes";
			break;
		case 6:
			selectedLogForDownload = ProjectConfig.xesFilePath + "/testLog/paper_log.xes";
			break;
		case 7:
			selectedLogForDownload = ProjectConfig.xesFilePath + "/testLog/Uniform_Rand1to100_1000cases_ArrBig.xes";
			break;
		case 8:
			selectedLogForDownload = ProjectConfig.xesFilePath + "/testLog/Uniform_Rand1to100_1000cases_ArrSame.xes";
			break;
		}
	}
	
	@Listen("onClick=#downloadSampleLogBtn")
	public void downloadSampleLog() {
		try {
			if(selectedLogForDownload != null && selectedLogForDownload.length() > 0) {
				File file = new File(selectedLogForDownload);
				Filedownload.save(file, "application/xml");
			}
		} catch (FileNotFoundException e) {
			Messagebox.show("An Error has occurred! " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
			e.printStackTrace();
		}
	}
	
	@Listen("onClick=#runSampleLogBtn")
	public void runMinerOnSampleLog() {
		try {
			if(selectedLogForDownload != null && selectedLogForDownload.length() > 0) {
				String username = "testLog";
				String name = selectedLogForDownload.replace(ProjectConfig.xesFilePath + "/testLog/", "");
				File output = getOutputFile(username, name);
				logManager lm = new logManager();
				lm.setDataProducer(false);
				//lm.setGreedyTimeMapping(isGreedyTimeMapping);
				double threshold = (double) ((double)psThreshold.getCurpos() / 100.00);
				lm.setPsThreshold(threshold);
				lm.setStdDeviationEnabled(Boolean.valueOf(timeRadio.getSelectedItem().getValue().toString()));
				lm.run(username, name);
				correlationImgage.setContent(lm.getCorrelationImage());
				sourceActivityTxtBox.setValue(lm.getStartActivity());
				sinkActivityTxtBox.setValue(lm.getEndActivity());	
				showCMD(output);
			} 
		} catch (Exception e) {
			Messagebox.show("An Error has occurred! " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
			e.printStackTrace();
		}
	}
	
	private void showNotify(String msg, Component ref) {
        Clients.showNotification(msg, "info", ref, "end_center", 1500);
 }
}
