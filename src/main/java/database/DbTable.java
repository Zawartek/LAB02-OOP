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

	private static List<String> fordiddenIndexes = new LinkedList<String>(){
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
		log.debug("Start method loadTable");
		loadColumns(dbMetaData, tableName);
		loadIndexes(dbMetaData, tableName);
		log.debug("End method loadTable");
	}
	
	public void loadColumns(DatabaseMetaData dbMetaData, String tableName) throws SQLException {
		DbColumn column = null;
		ResultSet result = null;
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
	}
	
	public void loadIndexes(DatabaseMetaData dbMetaData, String tableName) throws SQLException {
		loadPrimaryKeys(dbMetaData, tableName);
		loadForeignKeys(dbMetaData, tableName);
		loadIndex(dbMetaData, tableName);
	}
	
	private void loadPrimaryKeys(DatabaseMetaData dbMetaData, String tableName) throws SQLException {
		DbIndex index = null;
		String primaryKeyName = null, lastPrimaryKeyName = null;
		ResultSet primaryKeys = dbMetaData.getPrimaryKeys(null, null, tableName);
		while (primaryKeys.next()) {
			primaryKeyName = primaryKeys.getString("PK_NAME");
			if(fordiddenIndexes.contains(primaryKeyName)) {
				primaryKeyName = "";
			}
			if (lastPrimaryKeyName ==null || !lastPrimaryKeyName.equals(primaryKeyName)) {
				index = DbIndexFactory.create(primaryKeyName, "PRIMARY", primaryKeys);
				indexes.add(index);
			}
			else {
				DbIndexFactory.addIndexColumn(index, "PRIMARY", primaryKeys);
			}
			lastPrimaryKeyName = primaryKeyName;
		}
	}

	private void loadForeignKeys(DatabaseMetaData dbMetaData, String tableName) throws SQLException {
		DbIndex index = null;
		String foreignName = null, lastForeignName = null;
		ResultSet foreignKeys = dbMetaData.getImportedKeys(null, null, tableName);
		while (foreignKeys.next()) {
			foreignName = foreignKeys.getString("FK_NAME");
			if (lastForeignName ==null || !lastForeignName.equals(foreignName)) {
				index = DbIndexFactory.create(foreignName, "FOREIGN", foreignKeys);
				foreignIndexes.add(index);
			}
			else {
				DbIndexFactory.addIndexColumn(index, "FOREIGN", foreignKeys);
			}
			lastForeignName = foreignName;
		}
	}
	
	private void loadIndex(DatabaseMetaData dbMetaData, String tableName) throws SQLException {
		DbIndex index = null;
		String indexName = null, lastIndexName = null;
		ResultSet indexesInfo = dbMetaData.getIndexInfo(null, null, tableName, false, false);
		while (indexesInfo.next()) {
			indexName = indexesInfo.getString("INDEX_NAME");
			if (!fordiddenIndexes.contains(indexName)) {
				if (indexName !=null && !indexName.equals(lastIndexName)) {
					index = DbIndexFactory.create(indexName, "INDEX", indexesInfo);
					indexes.add(index);
				}
				else {
					DbIndexFactory.addIndexColumn(index, "INDEX", indexesInfo);
				}
				lastIndexName = indexName;
			}
		}
		checkIndexes();
	}
	
	private void checkIndexes() {
		for (DbIndex index : indexes) {
			if (DbIndex.class.getName().equals(index.getClass().getName())) {
				for (DbColumn column : columns) {
					if (index.getColumnNames().contains(column.getName())
							&& (column.getType().equals("BLOB")
									|| column.getType().equals("TEXT"))
							)
					{
						index.setFullText();
					}
				}
			}
		}
	}
	
	public String toSQL() {
        final StringBuffer sb = new StringBuffer();
        sb.append(columnsToSQL());
        sb.append(indexesToSQL());
        sb.append(foreignIndexesToSQL());
        return sb.toString();
	}
	
    public String columnsToSQL()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE " + this.tableName + " (\n");
        for (DbColumn column : columns)
        {
       		sb.append(column.toSQL());
       		sb.append(",\n");
        }
        if (columns.size()>0) {
        	sb.deleteCharAt(sb.toString().lastIndexOf(','));
        }
        sb.append(");\n\n");
        return sb.toString();
    }

	public String indexesToSQL() {
        final StringBuffer sb = new StringBuffer();
        sb.append("ALTER TABLE " + this.tableName + " \n");
        for (DbIndex index : indexes)
        {
        	sb.append("\tADD ");
       		sb.append(index.toSQL());
       		sb.append(",\n");
        }
        if (indexes.size()>0) {
	        sb.deleteCharAt(sb.toString().lastIndexOf(','));
	        sb.deleteCharAt(sb.toString().lastIndexOf('\n'));
        }
        sb.append(";\n\n");
        return sb.toString();
	}

	public String foreignIndexesToSQL() {
        final StringBuffer sb = new StringBuffer();
        sb.append("ALTER TABLE " + this.tableName + " \n");
        for (DbIndex index : foreignIndexes)
        {
        	sb.append("\tADD ");
       		sb.append(index.toSQL());
       		sb.append(",\n");
        }
        if (foreignIndexes.size()>0) {
	        sb.deleteCharAt(sb.toString().lastIndexOf(','));
	        sb.deleteCharAt(sb.toString().lastIndexOf('\n'));
        }
        sb.append(";\n\n");
        return sb.toString();
	}
}
