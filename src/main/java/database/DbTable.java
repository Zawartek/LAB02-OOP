package main.java.database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import main.java.database.table.*;

public class DbTable {
	protected static Logger log = Logger.getLogger(DbTable.class);

	private static List<String> fordiddenIndexes = new LinkedList<String>() {
		{
			add("PRIMARY");
			add("FOREIGN");
			add("INDEX");
		}
	};

	private String tableName;
	private List<DbColumn> columns;
	private List<DbIndex> indexes;
	private List<DbIndex> foreignIndexes;

	public DbTable(String tableName) {
		columns = new LinkedList<DbColumn>();
		indexes = new LinkedList<DbIndex>();
		foreignIndexes = new LinkedList<DbIndex>();
		this.tableName = tableName;
	}

	public String getName() {
		return tableName;
	}

	/**
	 * List all table types that exist in MySql
	 */
	public enum TableType {
		TABLE, VIEW, SYSTEM_TABLE, GLOBAL_TEMPORARY, LOCAL_TEMPORARY, ALIAS, SYNONYM
	}

	/**
	 * Call loadColumns and loadIndexes to get all informations on a given table
	 * 
	 * @param dbMetaData
	 * @throws SQLException
	 */
	public void loadTable(DatabaseMetaData dbMetaData) throws SQLException {
		log.debug("Start method loadTable");
		loadColumns(dbMetaData);
		loadIndexes(dbMetaData);
		log.debug("End method loadTable");
	}

	/**
	 * Get informations about columns of a given table such as their name, type,
	 * size, ...
	 * 
	 * @param dbMetaData
	 * @throws SQLException
	 */
	public void loadColumns(DatabaseMetaData dbMetaData) throws SQLException {
		DbColumn column = null;
		ResultSet result = null;
		result = dbMetaData.getColumns(null, null, tableName, null);
		if (!result.next()) {
			log.debug("No column in the table");
		} else {
			log.debug("Column(s) found in the database");
			do {
				column = DbColumnFactory.createColumn(result);
				columns.add(column);
			} while (result.next());
		}
		result.close();
	}

	/**
	 * Call some methods that get indexes informations on a given table
	 * 
	 * @param dbMetaData
	 * @throws SQLException
	 */
	public void loadIndexes(DatabaseMetaData dbMetaData) throws SQLException {
		loadPrimaryKeys(dbMetaData);
		loadForeignKeys(dbMetaData);
		loadIndex(dbMetaData);
	}

	/**
	 * Get informations about primary keys of a given table
	 * 
	 * @param dbMetaData
	 * @throws SQLException
	 */
	private void loadPrimaryKeys(DatabaseMetaData dbMetaData) throws SQLException {
		DbIndex index = null;
		String primaryKeyName = null, lastPrimaryKeyName = null;
		ResultSet primaryKeys = dbMetaData.getPrimaryKeys(null, null, tableName);
		while (primaryKeys.next()) {
			primaryKeyName = primaryKeys.getString("PK_NAME");
			if (fordiddenIndexes.contains(primaryKeyName)) {
				primaryKeyName = "";
			}
			if (lastPrimaryKeyName == null || !lastPrimaryKeyName.equals(primaryKeyName)) {
				index = DbIndexFactory.create(primaryKeyName, "PRIMARY", primaryKeys);
				indexes.add(index);
			} else {
				DbIndexFactory.addIndexColumn(index, "PRIMARY", primaryKeys);
			}
			lastPrimaryKeyName = primaryKeyName;
		}
	}

	/**
	 * Get informations about foreign keys of a given table
	 * 
	 * @param dbMetaData
	 * @throws SQLException
	 */
	private void loadForeignKeys(DatabaseMetaData dbMetaData) throws SQLException {
		DbIndex index = null;
		String foreignName = null, lastForeignName = null;
		ResultSet foreignKeys = dbMetaData.getImportedKeys(null, null, tableName);
		while (foreignKeys.next()) {
			foreignName = foreignKeys.getString("FK_NAME");
			if (lastForeignName == null || !lastForeignName.equals(foreignName)) {
				index = DbIndexFactory.create(foreignName, "FOREIGN", foreignKeys);
				foreignIndexes.add(index);
			} else {
				DbIndexFactory.addIndexColumn(index, "FOREIGN", foreignKeys);
			}
			lastForeignName = foreignName;
		}
	}

	/**
	 * Get informations about indexes of a given table
	 * 
	 * @param dbMetaData
	 * @throws SQLException
	 */
	private void loadIndex(DatabaseMetaData dbMetaData) throws SQLException {
		DbIndex index = null;
		String indexName = null, lastIndexName = null;
		ResultSet indexesInfo = dbMetaData.getIndexInfo(null, null, tableName, false, false);
		while (indexesInfo.next()) {
			indexName = indexesInfo.getString("INDEX_NAME");
			if (!fordiddenIndexes.contains(indexName)) {
				if (indexName != null && !indexName.equals(lastIndexName)) {
					index = DbIndexFactory.create(indexName, "INDEX", indexesInfo);
					indexes.add(index);
				} else {
					DbIndexFactory.addIndexColumn(index, "INDEX", indexesInfo);
				}
				lastIndexName = indexName;
			}
		}
		checkIndexes();
	}

	/**
	 * Manage particular case of indexes on BLOP or TEXT field
	 */
	private void checkIndexes() {
		for (DbIndex index : indexes) {
			if (DbIndex.class.getName().equals(index.getClass().getName())) {
				for (DbColumn column : columns) {
					if (index.getColumnNames().contains(column.getName())
							&& (column.getType().equals("BLOB") || column.getType().equals("TEXT"))) {
						index.setFullText();
					}
				}
			}
		}
	}

	/**
	 * Create the sql script by calling methods that create table, indexes
	 * (primary key, index) and foreign key (call in last to avoid reference
	 * error)
	 * 
	 * @return
	 */
	public String toSQL() {
		final StringBuffer sb = new StringBuffer();
		sb.append(columnsToSQL());
		sb.append(indexesToSQL());
		sb.append(foreignIndexesToSQL());
		return sb.toString();
	}

	/**
	 * Return the sql script that create tables
	 * 
	 * @return
	 */
	public String columnsToSQL() {
		final StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE " + this.tableName + " (\n");
		for (DbColumn column : columns) {
			sb.append(column.toSQL());
			sb.append(",\n");
		}
		if (columns.size() > 0) {
			sb.deleteCharAt(sb.toString().lastIndexOf(','));
		}
		sb.append(");\n\n");
		return sb.toString();
	}

	/**
	 * Return the sql script that add indexes on tables
	 * 
	 * @return
	 */
	protected String indexesToSQL() {
		final StringBuffer sb = new StringBuffer();
		if (indexes.size() > 0) {
			sb.append("ALTER TABLE " + this.tableName + " \n");
			for (DbIndex index : indexes) {
				sb.append("\tADD ");
				sb.append(index.toSQL());
				sb.append(",\n");
			}
			if (indexes.size() > 0) {
				sb.deleteCharAt(sb.toString().lastIndexOf(','));
				sb.deleteCharAt(sb.toString().lastIndexOf('\n'));
			}
			sb.append(";\n\n");
		}
		return sb.toString();
	}

	/**
	 * Return the sql script that add the foreign keys on tables
	 * 
	 * @return
	 */
	protected String foreignIndexesToSQL() {
		final StringBuffer sb = new StringBuffer();
		if (foreignIndexes.size() > 0) {
			sb.append("ALTER TABLE " + this.tableName + " \n");
			for (DbIndex index : foreignIndexes) {
				sb.append("\tADD ");
				sb.append(index.toSQL());
				sb.append(",\n");
			}
			if (foreignIndexes.size() > 0) {
				sb.deleteCharAt(sb.toString().lastIndexOf(','));
				sb.deleteCharAt(sb.toString().lastIndexOf('\n'));
			}
			sb.append(";\n\n");
		}
		return sb.toString();
	}
}
