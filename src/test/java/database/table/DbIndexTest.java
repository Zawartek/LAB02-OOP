package test.java.database.table;

import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import main.java.database.DatabaseConnection;
import main.java.database.table.DbIndex;
import main.java.database.table.DbIndexFactory;
import main.java.database.table.index.*;

public class DbIndexTest  extends TestCase {
	public DbIndexTest(String testName) {
		super(testName);
	}
	
	/** * @return the suite of tests being tested */
	public static Test suite() {
		return new TestSuite(DbIndexTest.class);
	}
	

	public void testPrimaryKey() {
		DbIndex index = null;
		DatabaseConnection dbCon = new DatabaseConnection("jdbc:mysql://localhost/sakila", "com.mysql.jdbc.Driver", "root", "root");
		ResultSet indexResultSet;
		String tableName = "film_actor", indexName, indexType, indexColumns, toSQL=null;
		boolean isCreated = false;
		indexName = indexType = "PRIMARY";
		indexColumns = "[actor_id, film_id]";
		toSQL = "CONSTRAINT `PRIMARY` PRIMARY KEY (`actor_id`, `film_id` )";
		try {
			dbCon.initialiseConnection();
			indexResultSet = dbCon.getMetaData().getPrimaryKeys(null, null, tableName);
			if (indexResultSet.next()) {
				do {
					if (!isCreated) {
						index = DbIndexFactory.create(indexName, indexType, indexResultSet);
						isCreated = true;
					}
					else {
						DbIndexFactory.addIndexColumn(index, indexType, indexResultSet);
					}
				} while(indexResultSet.next());
				assertEquals(index.getClass().getName(), DbPrimaryKey.class.getName());
				assertEquals(index.getName(),indexName);
				assertEquals(index.getColumnNames().toString(), indexColumns);
				assertFalse(index.isFullText());
				assertEquals(index.toSQL(),toSQL);
			}
			else {
				fail("Index not found");
			}
			dbCon.closeConnection();
		} catch(SQLException se) {
			se.printStackTrace();
			fail("SQL exception");
		} catch(Exception e) {
			e.printStackTrace();
			fail("Connection with the database fail");
		}
	}

	public void testForeignKey() {
		DbIndex index = null;
		DatabaseConnection dbCon = new DatabaseConnection("jdbc:mysql://localhost/sakila", "com.mysql.jdbc.Driver", "root", "root");
		ResultSet indexResultSet;
		String tableName = "film_actor", indexName, indexType, indexColumns, toSQL=null;
		boolean isCreated = false;
		indexName = "idx_fk_film_id";
		indexType = "FOREIGN";
		indexColumns = "[actor_id, film_id]";
		toSQL = "CONSTRAINT `idx_fk_film_id` FOREIGN KEY (`actor_id`, `film_id` ) "
				+ "REFERENCES `actor`(`actor_id`, `film_id` )";
		try {
			dbCon.initialiseConnection();
			indexResultSet = dbCon.getMetaData().getImportedKeys(null, null, tableName);
			if (indexResultSet.next()) {
				do {
					if (!isCreated) {
						index = DbIndexFactory.create(indexName, indexType, indexResultSet);
						isCreated = true;
					}
					else {
						DbIndexFactory.addIndexColumn(index, indexType, indexResultSet);
					}
				} while(indexResultSet.next());
				assertEquals(index.getClass().getName(), DbForeignKey.class.getName());
				assertEquals(index.getName(),indexName);
				assertEquals(index.getColumnNames().toString(), indexColumns);
				assertFalse(index.isFullText());
				assertEquals(index.toSQL(),toSQL);
			}
			else {
				fail("Index not found");
			}
			dbCon.closeConnection();
		} catch(SQLException se) {
			se.printStackTrace();
			fail("SQL exception");
		} catch(Exception e) {
			e.printStackTrace();
			fail("Connection with the database fail");
		}
	}
	

	public void testIndex() {
		DbIndex index = null;
		DatabaseConnection dbCon = new DatabaseConnection("jdbc:mysql://localhost/sakila", "com.mysql.jdbc.Driver", "root", "root");
		ResultSet indexResultSet;
		String tableName = "actor", indexName, indexType, indexColumns, toSQL=null;
		boolean isCreated = false;
		indexName = "idx_actor_last_name";
		indexType = "INDEX";
		indexColumns = "[last_name]";
		toSQL = "KEY `idx_actor_last_name`(`last_name` )";
		try {
			dbCon.initialiseConnection();
			indexResultSet = dbCon.getMetaData().getIndexInfo(null, null, tableName, isCreated, isCreated);
			// On ignore l'index unique de la clé primaire
			indexResultSet.next();
			if (indexResultSet.next()) {
				do {
					if (!isCreated) {
						index = DbIndexFactory.create(indexName, indexType, indexResultSet);
						isCreated = true;
					}
					else {
						DbIndexFactory.addIndexColumn(index, indexType, indexResultSet);
					}
				} while(indexResultSet.next());
				assertEquals(index.getClass().getName(), DbIndex.class.getName());
				assertEquals(index.getName(),indexName);
				assertEquals(index.getColumnNames().toString(), indexColumns);
				assertFalse(index.isFullText());
				assertEquals(index.toSQL(),toSQL);
				index.setFullText();
				assertTrue(index.isFullText());
				toSQL  = "FULLTEXT " + toSQL;
				assertEquals(index.toSQL(),toSQL);
			}
			else {
				fail("Index not found");
			}
			dbCon.closeConnection();
		} catch(SQLException se) {
			se.printStackTrace();
			fail("SQL exception");
		} catch(Exception e) {
			e.printStackTrace();
			fail("Connection with the database fail");
		}
	}
}
