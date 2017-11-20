package main.java.database.table;

import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.database.table.index.DbForeignKey;
import main.java.database.table.index.DbPrimaryKey;
import main.java.database.table.index.DbUniqueIndex;

public class DbIndexFactory {
	/**
	 * Create a DbIndex base on the Index Type and the ResultSet
	 * @param indexName
	 * @param type
	 * @param result
	 * @return index
	 * @throws SQLException
	 */
	public static DbIndex create(String indexName, String type, ResultSet result) throws SQLException {
		DbIndex index = null;
		switch (type) {
		case "FOREIGN" :
			index = createForeignKey(indexName, result);
			break;
		case "PRIMARY" :
			index = createPrimaryKey(indexName, result);
			break;
		case "INDEX" :
			index = createIndex(indexName, result);
			break;
		default :
			break;
		}
		return index;
	}
	
	private static DbIndex createPrimaryKey(String indexName, ResultSet result) throws SQLException {
		DbIndex index = null;
		String columnName = null;
		columnName = result.getString("COLUMN_NAME");
		index = new DbPrimaryKey(indexName, columnName);
		return index;
	}
	
	/**
	 * Create a foreign key
	 * @param indexName
	 * @param result
	 * @return index
	 * @throws SQLException
	 */
	private static DbIndex createForeignKey(String indexName, ResultSet result) throws SQLException {
		DbIndex index = null;
		String fk_column_name, pk_table_name, pk_column_name;
		fk_column_name = result.getString("FKCOLUMN_NAME");
		pk_table_name = result.getString("PKTABLE_NAME");
		pk_column_name = result.getString("PKCOLUMN_NAME");
		index = new DbForeignKey(indexName, fk_column_name, pk_table_name, pk_column_name);
		return index;
	}
	
	/**
	 * Create an index (UNIQUE KEY/FULLTEXT KEY/KEY)
	 * @param indexName
	 * @param colName
	 * @param nonUnique
	 * @return index
	 * @throws SQLException
	 */
	private static DbIndex createIndex(String indexName, ResultSet result) throws SQLException {
		DbIndex index = null;
		boolean nonUnique;
		String columnName;
		columnName = result.getString("COLUMN_NAME");
		nonUnique = result.getBoolean("NON_UNIQUE");
		if (nonUnique) {
			index = new DbIndex(indexName, columnName);
		}
		else {
			index = new DbUniqueIndex(indexName, columnName);
		}
		return index;
	}

	/**
	 * Add a column to the index
	 * @param index
	 * @param type
	 * @param result
	 * @throws SQLException
	 */
	public static void addIndexColumn(DbIndex index, String type, ResultSet result) throws SQLException {
		String colName, pkColName;
		switch (type) {
		case "FOREIGN" :
			colName = result.getString("FKCOLUMN_NAME");
			pkColName = result.getString("PKCOLUMN_NAME");
			index.addIndexColumn(colName);
			((DbForeignKey) index).addPrimaryIndexColumn(pkColName);
			break;
		case "PRIMARY" :
		case "INDEX" :
			colName = result.getString("COLUMN_NAME");
			index.addIndexColumn(colName);
			break;
		default :
			break;
		}
	}
}
