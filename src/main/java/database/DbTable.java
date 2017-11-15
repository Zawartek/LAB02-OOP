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

	private String tableName;
	private List<DbColumn> columns;
	private List<DbIndex> indexes;
	
	public DbTable(String tableName) {
		columns = new LinkedList<DbColumn>();
		this.tableName = tableName;
	}
	
	public String getName() {
		return tableName;
	}

    public String toSQL()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE " + this.tableName + " (\n");
        for (DbColumn column : columns)
        {
       		sb.append(column.toSQL());
       		sb.append(",\n");
        }
        sb.deleteCharAt(sb.toString().lastIndexOf(','));
        sb.append(");\n\n");
        return sb.toString();
    }
    
    public enum TableType {
    	TABLE, 
    	VIEW, 
    	SYSTEM_TABLE, 
    	GLOBAL_TEMPORARY, 
    	LOCAL_TEMPORARY, 
    	ALIAS, 
    	SYNONYM
    }

	public void loadTable(DatabaseMetaData dbMetaData, String tableName) throws SQLException {
		ResultSet result = null;
		DbColumn column = null;
		log.debug("Start method loadTable");
		result = dbMetaData.getColumns(null, null, tableName, null);
		if (!result.next()) {
			log.debug("No column in the table");
		}
		else {
			log.debug("Column(s) found in the database");
			do {
				column = DbColumnFactory.createColumn(result);
				columns.add(column);
			} while (result.next());
		}
		result.close();
		log.debug("End method loadTable");
	}

	public String indexesToSQL() {
		
		return null;
	}
}
