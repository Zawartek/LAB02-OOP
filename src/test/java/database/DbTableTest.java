package test.java.database;

import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import main.java.database.DatabaseConnection;
import main.java.database.DbTable;

public class DbTableTest extends TestCase {
	public DbTableTest(String testName) {
		super(testName);
	}

	/** * @return the suite of tests being tested */
	public static Test suite() {
		return new TestSuite(DbTableTest.class);
	}

	public void testTable() {
		String tableName = "tableName", tableToSql;
		DbTable table = new DbTable(tableName);
		assertEquals(table.getName(), tableName);

		tableToSql = "CREATE TABLE tableName (\n" + ");\n\n";
		assertEquals(table.toSQL(), tableToSql);
	}

	public void testTableWithColumn() {
		String tableName = "film_actor", tableToSql;
		DatabaseConnection dbCon = new DatabaseConnection("jdbc:mysql://localhost/sakila", "com.mysql.jdbc.Driver",
				"root", "root");
		DbTable table = new DbTable(tableName);
		tableToSql = "CREATE TABLE film_actor (\n" + "\t`actor_id` SMALLINT UNSIGNED NOT NULL,\n"
				+ "\t`film_id` SMALLINT UNSIGNED NOT NULL,\n"
				+ "\t`last_update` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP\n" + ");\n\n"
				+ "ALTER TABLE film_actor \n" + "\tADD PRIMARY KEY (`actor_id`, `film_id` ),\n"
				+ "\tADD KEY `idx_fk_film_id`(`film_id` );\n\n" + "ALTER TABLE film_actor \n"
				+ "\tADD CONSTRAINT `fk_film_actor_actor` "
				+ "FOREIGN KEY (`actor_id` ) REFERENCES `actor`(`actor_id` ),\n"
				+ "\tADD CONSTRAINT `fk_film_actor_film` "
				+ "FOREIGN KEY (`film_id` ) REFERENCES `film`(`film_id` );\n\n";
		try {
			dbCon.initialiseConnection();
			table.loadTable(dbCon.getMetaData());
			assertEquals(table.toSQL(), tableToSql);
			dbCon.closeConnection();
		} catch (SQLException se) {
			fail("Load Table fail");
		} catch (Exception e) {
			fail("Connection with the database fail");
		}
	}
}
