package main.java.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import main.java.database.DbTable.TableType;

public class DbTableFactory {
	protected static Logger log = Logger.getLogger(DbTableFactory.class);

	/**
	 * Create a DbTable based on the resultSet
	 * 
	 * @param column
	 * @return
	 * @throws SQLException
	 */
	public static DbTable createTable(ResultSet column) throws SQLException {
		log.debug("Start method createTable");
		DbTable dbTab = null;
		String tableName = null, tableType = null;
		TableType tabType = null;

		tableName = column.getString("TABLE_NAME");
		tableType = column.getString("TABLE_TYPE");
		log.debug("column.TABLE_NAME : " + tableName);
		log.debug("column.TABLE_TYPE : " + tableType);
		tableType = tableType.replaceAll(" ", "_");
		tabType = TableType.valueOf(tableType);
		dbTab = createTable(tableName, tabType);
		log.debug("End method createTable");
		return dbTab;
	}

	/**
	 * Create a DbTable based on the table type
	 * 
	 * @param tableName
	 * @param tableType
	 * @return
	 */
	private static DbTable createTable(String tableName, TableType tableType) {
		log.debug("Start method createTable");
		log.debug("Table name : " + tableName);
		log.debug("Table type : " + tableType);
		DbTable dbTab = null;
		switch (tableType) {
		case TABLE:
			dbTab = new DbTable(tableName);
			break;
		default:
			break;
		}
		log.debug("End method createTable");
		return dbTab;
	}
}
