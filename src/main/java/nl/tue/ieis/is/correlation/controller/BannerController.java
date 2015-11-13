package main.java.nl.tue.ieis.is.correlation.controller;


import main.java.nl.tue.ieis.is.correlation.utility.User;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;


public class BannerController extends SelectorComposer<Component> {

	private static final long serialVersionUID = -6360927128036523270L;
	
	@Wire	private Window loginWin;
	@Wire	private Button initLoginBtn;
	@Wire	private Button initRegBtn;
	@Wire	private Textbox username;
	@Wire	private Textbox password;
	@Wire	private Label loginMsgLabel; 
	@Wire	private Div userInfo;
	@Wire	private Div bannerWin;
	@Wire 	private Label userLabel;
	@Wire	private Window regWin;
	@Wire	private Button logoutBtn;
		
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		setUserBanner();
	}
	
	@Listen("onClick = #initLoginBtn")
	public void showLoginWin() {
		try{
			bannerWin.appendChild(loginWin);
		} catch (Exception e) { System.out.println(e.getMessage());}
		if (!loginWin.isVisible())
			loginWin.setVisible(true);
		loginWin.doHighlighted();
	}

	@Listen("onClick = #logoutBtn")
	public void logout() {
		User user = (User)(Sessions.getCurrent()).getAttribute("user");
		(Sessions.getCurrent()).setAttribute("user", null);
		(Sessions.getCurrent()).setAttribute("selected", null);
		System.out.println("User " + user.getFirstName() + " has been logged out.");
		Executions.sendRedirect("");
	}
	
	@Listen("onClick = #initRegBtn")
	public void showRegWin() {
		try{
			bannerWin.appendChild(regWin);
		} catch (Exception e) { System.out.println(e.getMessage());}
		if (!regWin.isVisible())
			regWin.setVisible(true);
		regWin.doHighlighted();
	}
	
	@Listen("onOK = #loginWin")
	public void onOkLoginWindow(){
		setUserBanner();
	}
	
	private void setUserBanner() {
		User user = (User)(Sessions.getCurrent()).getAttribute("user");
		if(user != null) {
			userInfo.setVisible(true);
			initLoginBtn.setVisible(false);
			initRegBtn.setVisible(false);
			userLabel.setValue(user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
			logoutBtn.setVisible(true);
		} else {			
			userInfo.setVisible(false);
			initLoginBtn.setVisible(true);
			initRegBtn.setVisible(true);
			userLabel.setValue("No User");
			logoutBtn.setVisible(false);
		}
	}
}
