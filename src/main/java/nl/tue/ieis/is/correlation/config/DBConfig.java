package main.java.nl.tue.ieis.is.correlation.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConfig {
	
private static final String dbLocation = ProjectConfig.projectPath + "/resources/cor.db";
	
	public static Connection getConnection()
	  {
	    Connection conn = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      conn = DriverManager.getConnection("jdbc:sqlite:" + dbLocation);
	      System.out.println("Connected to SQLite Database");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    return conn;
	  }
}
