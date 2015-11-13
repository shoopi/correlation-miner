package main.java.nl.tue.ieis.is.correlation.controller;


import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import main.java.nl.tue.ieis.is.correlation.utility.*;

public class UserController extends SelectorComposer<Component> {
	
	private static final long serialVersionUID = 8670883952725538263L;
	private static UserFunction uf = new UserFunction();

	@Wire	private Textbox username, password;
	@Wire	private Textbox regUsername, regPassword, regFirstname, regLastename, regInstitute;
	@Wire	private Label loginMsgLabel, regMsgLabel;
	@Wire	private Window loginWin, regWin;
	@Wire	private Button regBtn, loginwBtn;
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}
	
	@Listen("onOK = #loginWin")
	public void loginWindowOk() {
		login();
	}
	
	@Listen("onClick = #loginBtn")
	public void loginButtonClick() {
		login();
	}
	
	@Listen("onOK = #regWin")
	public void regWindowOk() {
		register();
	}
	
	@Listen("onClick = #regBtn")
	public void regButtonClick() {
		register();
	}
	
	private void login() {
		String inputUsername = username.getValue();
		String inputPassword = password.getValue();
		try{
			if(uf.login(inputUsername, inputPassword) != null) {
				User user = uf.login(inputUsername, inputPassword);
				(Sessions.getCurrent()).setAttribute("user", user);
				loginWin.setVisible(false);
				System.out.println("user " + inputUsername + " has logged in to the system.");
				Executions.sendRedirect("");
			} else loginMsgLabel.setValue("Please check your email and/or password and try again.");
		} catch(Exception e) {
			loginMsgLabel.setValue("Authentication Failed! Please try again.");
			e.printStackTrace();
		}
	}
	
	private void register() {
		String email 		= 	regUsername.getValue();
		String password 	= 	regPassword.getValue();
		String firstname 	= 	regFirstname.getValue();
		String lastname 	= 	regLastename.getValue();
		String institute 	=	regInstitute.getValue();
		
		User user = new User(email, password, firstname, lastname, institute);
        try{
        	boolean done = uf.registerUser(user);
			if(done) {
				System.out.println("User " + email + " has been registered.");
				regWin.setVisible(false);
				
				try {
					//TODO verification email.
					//MailUtil.sendMail(user);
					Clients.showNotification("<p>We have received your request! </p> "
							+ "<p>Please check your mailbox in order to confirm your email address. </p>");
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("An error has occurred during the sending of the confirmation email");
					uf.deleteUser(email);
					Clients.showNotification("<p>We cannot process your request now! </p> ");
				}
			} else {
				regWin.setVisible(false);
				Clients.showNotification("<p>We cannot process your request now! </p> "
						+ "<p>Maybe you have already registered. </p>");
			}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
	}
}