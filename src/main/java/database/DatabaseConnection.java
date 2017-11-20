package main.java.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseConnection {
	private String jdbcURL;
	public String jdbcDriver;
	private String user;
	private String password;
	// Database Connection
	private Connection connection;
	private Statement statement;
	private DatabaseMetaData dbMeta;

	public DatabaseConnection(String url, String driver, String user, String password) {
		jdbcURL = url;
		jdbcDriver =  driver;
		this.user = user;
		this.password = password;
	}

	/**
	 * Return the database connection
	 * @return connection
	 */
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * Return the connection statement
	 * @return statement
	 */
	public Statement getStatement() {
		return statement;
	}
	
	/**
	 * Return the database metadata
	 * @return
	 */
	public DatabaseMetaData getMetaData() {
		return dbMeta;
	}

	/**
	 * Init the Connection to the database
	 * @throws Exception
	 */
	private void initialiseConnection() throws Exception{
		Class.forName(jdbcDriver);
		connection = DriverManager.getConnection(jdbcURL, user, password);
		statement = connection.createStatement();
		dbMeta = connection.getMetaData();
	}

	/**
	 * Release all the database objects (resultSet, statement, connection)
	 */
	public void closeConnection() {
		try {
			if (statement != null) {
				statement.close();
				statement = null;
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
			//e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String dbName = "sakila";
		String[] tabTypes = {"TABLE"};
		DatabaseConnection dbCon = new DatabaseConnection("jdbc:mysql://localhost/"+dbName, "com.mysql.jdbc.Driver", "root", "root");
		DbBase db = new DbBase(dbName);
		try {
			dbCon.initialiseConnection();
			db.loadDB(dbCon.getMetaData(),tabTypes);
			db.printToSql();
			db.exportToSql();
			dbCon.closeConnection();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
