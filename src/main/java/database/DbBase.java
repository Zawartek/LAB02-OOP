package main.java.database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class DbBase {
	protected static Logger log = Logger.getLogger(DbBase.class);
    private String dbName = null;
    private List<DbTable> tables;
	

	/**
	 * Construction of the class
	 * Create the jdbc URL base on the parameters
	 * @param database
	 * @param user
	 * @param password
	 */
	public DbBase(String dbName) {
		this.dbName = dbName;
		tables = new LinkedList<DbTable>();
	}
	
	public void loadDB(DatabaseMetaData dbMetaData) throws SQLException {
		loadDB(dbMetaData, null);
	}
    
	public void loadDB(DatabaseMetaData dbMetaData, String[] tableTypes) throws SQLException {
		ResultSet result = null;
		DbTable table = null;
		log.debug("Start method loadDB");
		result = dbMetaData.getTables(null, null, "%", null);
		if (!result.next()) {
			log.debug("No table in the database");
		}
		else {
			log.debug("Table(s) found in the database");
			do {
				table = DbTableFactory.createTable(result);
				if (table != null) {
					tables.add(table);
					table.loadTable(dbMetaData, table.getName());
				}
			} while (result.next());
		}
		result.close();
		log.debug("End method loadDB");
	}
	
    public String toSQL()
    {
		log.debug("DbBase : Start method toSQL");
        final StringBuffer sb = new StringBuffer();
        sb.append("CREATE DATABASE " + this.dbName + ";\n\n");
        for (DbTable table : tables)
        {
            sb.append(table.toSQL());
        }

        for (DbTable table : tables)
        {
            sb.append(table.indexesToSQL());
        }
		log.debug("DbBase : End method toSQL");
        return sb.toString();
    }
}
