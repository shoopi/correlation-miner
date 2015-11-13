package main.java.nl.tue.ieis.is.correlation.utility;

import main.java.nl.tue.ieis.is.correlation.config.UserDBConfig;

public class UserFunction {
	
	UserDBConfig userDB = new UserDBConfig();
	
	public boolean registerUser(User user) throws Exception {
		String password = encryptPassword(user.getPassword());
		boolean registered = userDB.addNewUserToDB(user.getEmail(), password, user.getFirstName(), user.getLastName(), user.getInstitute());
		if(registered) {
			System.out.println("User " + user.getEmail() + " has been successfully registered");
			return true;
		} else {
			System.err.println("Error in registering " + user.getEmail() + " as a new user.");
			return false;
		}
	}
	
	public boolean deleteUser(String email) throws Exception {
		boolean deleted = userDB.removeUser(email);
		if(deleted) {
			System.out.println("User " + email + " has been successfully removed");
			return true;
		} else {
			System.err.println("Error in deleting " + email + " as a user.");
			return false;
		}
	}
	
	public User login(String email, String password) throws Exception {
		String paswd = encryptPassword(password);
		User user = userDB.loadUser(email, paswd);
		return user;
	}
	
	
	private String encryptPassword(String input) throws Exception {
        PasswordService ps = new PasswordService();
        return ps.encrypt(input);
	}
}
