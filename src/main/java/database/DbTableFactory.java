package main.java.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import main.java.database.DbTable.TableType;

public class DbTableFactory {

	public static DbTable createTable(ResultSet column) throws SQLException {
		DbTable dbTab = null;
		String tableName = null, tableType = null;
		TableType tabType = null;

		tableName = column.getString("TABLE_NAME");
		tableType = column.getString("TABLE_TYPE");
		tableType = tableType.replaceAll(" ", "_");
		tabType = TableType.valueOf(tableType);
		dbTab = createTable(tableName, tabType);
		return dbTab;
	}

	private static DbTable createTable(String tableName, TableType tableType) {
		DbTable dbTab = null;
		switch (tableType) {
		case TABLE :
			dbTab = new DbTable(tableName);
			break;
		default :
			break;
		}
		return dbTab;
	}
}
