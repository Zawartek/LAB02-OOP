package test.java.database;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import main.java.database.DbBase;

public class DbBaseTest extends TestCase {
	
	public DbBaseTest(String testName) {
		super(testName);
	}
	
	/** * @return the suite of tests being tested */
	public static Test suite() {
		return new TestSuite(DbBaseTest.class);
	}

	/** * Rigourous Test */
	public void testDb() {
		String dbName = "sakila", dbToSql = null, dbToSqlTest = null;
		DbBase db = new DbBase(dbName);
		assertEquals(db.getName(), dbName);
		dbToSql = db.toSQL();
		dbToSqlTest = "-- Database sakila\n"
				+ "CREATE DATABASE sakila;\n\n";
		assertEquals(dbToSql, dbToSqlTest);
	}
	
	public void testDbStringToSQL() {
		String dbName = "sakila", dbToSql = null, dbToSqlTest = null;
		dbToSqlTest = "`sakila`";
		dbToSql = DbBase.stringToSQL(dbName);
		assertEquals(dbToSql, dbToSqlTest);
	}
}
