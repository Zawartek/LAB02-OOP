package main.java.database.table;

import java.util.LinkedList;
import java.util.List;

import main.java.database.DbBase;

/**
 * Database Index DbIndex can be a FULLTEXT index
 * 
 * @author nicosim
 *
 */
public class DbIndex {
	private String indexName;
	private List<String> columnNames;
	private boolean fulltext = false;

	public DbIndex(String indexName, String columnName) {
		this.indexName = indexName;
		columnNames = new LinkedList<String>();
		addIndexColumn(columnName);
	}

	/**
	 * Return the index name
	 * 
	 * @return index name
	 */
	public String getName() {
		return indexName;
	}

	/**
	 * Return the column names list of the index
	 * 
	 * @return column names
	 */
	public List<String> getColumnNames() {
		return columnNames;
	}

	/**
	 * Add a new Column to the column names list
	 * 
	 * @param col
	 */
	public void addIndexColumn(String col) {
		columnNames.add(col);
	}

	/**
	 * Set the index to the fulltext mode (need if one of the column is BLOB or
	 * TEXT)
	 */
	public void setFullText() {
		fulltext = true;
	}

	/**
	 * Get the fulltext mode of an index
	 * 
	 * @return
	 */
	public boolean isFullText() {
		return fulltext;
	}

	/**
	 * Return the index in SQL format
	 * 
	 * @return
	 */
	public String toSQL() {
		final StringBuffer sb = new StringBuffer();
		if (fulltext) {
			sb.append("FULLTEXT ");
		}
		sb.append("KEY " + DbBase.stringToSQL(this.getName()) + "(");
		for (String colName : getColumnNames()) {
			sb.append(DbBase.stringToSQL(colName) + ", ");
		}
		sb.deleteCharAt(sb.toString().lastIndexOf(','));
		sb.append(")");
		return sb.toString();
	}
}
