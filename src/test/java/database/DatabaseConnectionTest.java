package test.java.database;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import main.java.database.DatabaseConnection;

public class DatabaseConnectionTest extends TestCase {

	public DatabaseConnectionTest(String testName) {
		super(testName);
	}

	/** * @return the suite of tests being tested */
	public static Test suite() {
		return new TestSuite(DatabaseConnectionTest.class);
	}

	public void testDatabaseConnection() {
		DatabaseConnection dbCon = new DatabaseConnection("jdbc:mysql://localhost/sakila", "com.mysql.jdbc.Driver", "root", "root");
		assertNotNull(dbCon);

		try {
			dbCon.initialiseConnection();
			assertNotNull(dbCon.getConnection());
			assertNotNull(dbCon.getStatement());
			assertNotNull(dbCon.getMetaData());

			dbCon.closeConnection();
			assertNull(dbCon.getConnection());
			assertNull(dbCon.getStatement());
			assertNull(dbCon.getMetaData());
		} catch (Exception e) {
			fail("Connection to the database fail");
		}
	}
}
