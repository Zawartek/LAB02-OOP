package main.java.database;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class DbBase {
	protected static Logger log = Logger.getLogger(DbBase.class);
	protected static final String STRING_QUOTE="`";

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
	
	/**
	 * Load the database
	 * @param dbMetaData
	 * @throws SQLException
	 */
	public void loadDB(DatabaseMetaData dbMetaData) throws SQLException {
		loadDB(dbMetaData, null);
	}
    
	/**
	 * Load the database tables corresponding a type in tableTypes
	 * @param dbMetaData
	 * @param tableTypes
	 * @throws SQLException
	 */
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
	
	/**
	 * Return the database in SQL format
	 * @return
	 */
    public String toSQL()
    {
		log.debug("DbBase : Start method toSQL");
        final StringBuffer sb = new StringBuffer();
        sb.append("-- Database " + this.dbName + "\n");
        sb.append("CREATE DATABASE " + this.dbName + ";\n\n");
        sb.append("-- Tables\n");
        for (DbTable table : tables)
        {
            sb.append("-- Table " + table.getName() + "\n");
            sb.append(table.columnsToSQL());
        }
        sb.append("-- Indexes\n");
        for (DbTable table : tables)
        {
            sb.append("-- Table " + table.getName() + "\n");
            sb.append(table.indexesToSQL());
        }
        sb.append("-- Foreign Indexes\n");
        for (DbTable table : tables)
        {
            sb.append("-- Table " + table.getName() + "\n");
            sb.append(table.foreignIndexesToSQL());
        }
		log.debug("DbBase : End method toSQL");
        return sb.toString();
    }
    
    /**
     * Return the string between two sql quote
     * @param s
     * @return
     */
    public static String stringToSQL(String s) {
    	String newString = "";
    	newString = STRING_QUOTE + s + STRING_QUOTE;
    	return newString;
    }
    
    /**
     * Display the string return by the toSql method
     */
    public void printToSql() {
    	System.out.println(this.toSQL());
    }
    
    /**
     * Create the .sql file with the string return by toSql method
     */
    public void exportToSql() {
    	try {
			FileWriter file = new FileWriter("script.sql");
			file.write(this.toSQL());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
