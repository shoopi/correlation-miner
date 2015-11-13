package main.java.nl.tue.ieis.is.correlation.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.nl.tue.ieis.is.correlation.utility.User;

public class UserDBConfig {
	static Connection conn = DBConfig.getConnection();
	
	public boolean addNewUserToDB(String email, String password, String firstname, String lastname, String institute) {
		try {
			String sql = "INSERT INTO USER(EMAIL,PASSWORD,FIRSTNAME,LASTNAME,INSTITUTE) VALUES (?,?,?,?,?)"; 
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, email);
			ps.setString(2, password);
			ps.setString(3, firstname);
			ps.setString(4, lastname);
			ps.setString(5, institute);
			ps.executeUpdate();
			ps.close();
			return true;
		} catch (Exception e) {
			System.err.println(e.getMessage() + " in creating a new user.");
			e.printStackTrace();
			return false;
		}
	}
	
	public User loadUser(String email, String password) {
		String sql = "SELECT * FROM USER WHERE EMAIL = ? AND PASSWORD = ?";
		User user = null;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, email);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				user = new User(email, password, rs.getString("FIRSTNAME"), rs.getString("LASTNAME"), rs.getString("INSTITUTE"));
			}
			ps.close();
			
		} catch (SQLException e) {
			System.err.println(e.getMessage() + " in loading a routelog status.");
			e.printStackTrace();
		}
		return user;
	}
	
	public boolean removeUser(String email) {
		try {
			String sql = "DELETE FROM USER WHERE EMAIL = ?"; 
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, email);
			ps.executeUpdate();
			ps.close();
			return true;
		} catch (SQLException e) {
			System.err.println(e.getMessage() + " in removing a user.");
			e.printStackTrace();
			return false;
		}
	}
}
