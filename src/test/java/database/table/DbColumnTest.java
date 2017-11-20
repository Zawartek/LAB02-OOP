package test.java.database.table;

import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import main.java.database.DatabaseConnection;
import main.java.database.table.DbColumn;
import main.java.database.table.DbColumnFactory;
import main.java.database.table.column.*;

public class DbColumnTest  extends TestCase {
	public DbColumnTest(String testName) {
		super(testName);
	}

	/** * @return the suite of tests being tested */
	public static Test suite() {
		return new TestSuite(DbColumnTest.class);
	}
	
	public void testFactory() {
		DbColumn column = null;
		DatabaseConnection dbCon = new DatabaseConnection("jdbc:mysql://localhost/sakila", "com.mysql.jdbc.Driver", "root", "root");
		ResultSet colResultSet;
		String tableName = "film_actor", columnName="film_id", columnType="SMALLINT UNSIGNED", toSQL=null;
		toSQL = "\t`film_id` SMALLINT UNSIGNED NOT NULL";
		try {
			dbCon.initialiseConnection();
			colResultSet = dbCon.getMetaData().getColumns(null, null, tableName, columnName);
			if (colResultSet.next()) {
				column = DbColumnFactory.createColumn(colResultSet);
				assertEquals(column.getClass().getName(), DbColumnNoParam.class.getName());

				assertEquals(column.getName(), columnName);
				assertEquals(column.getType(), columnType);
				assertNull(column.getDefault());
				assertFalse(column.isNullable());
				assertEquals(column.toSQL(),toSQL);
			}
			else {
				fail("Column not found");
			}
			dbCon.closeConnection();
		} catch(SQLException se) {
			se.printStackTrace();
			fail("SQL exception");
		} catch(Exception e) {
			fail("Connection with the database fail");
		}
	}
}
