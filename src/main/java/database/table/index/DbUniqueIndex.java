package main.java.database.table.index;

import main.java.database.DbBase;
import main.java.database.table.DbIndex;

/**
 * Database Unique index
 * 
 * @author nicosim
 *
 */
public class DbUniqueIndex extends DbIndex {

	public DbUniqueIndex(String indexName, String colName) {
		super(indexName, colName);
	}

	public String toSQL() {
		final StringBuffer sb = new StringBuffer();
		if (getName() != null && !getName().equals("")) {
			sb.append("CONSTRAINT " + DbBase.stringToSQL(getName()) + " ");
		}
		sb.append("UNIQUE KEY " + DbBase.stringToSQL(this.getName()) + "(");
		for (String colName : getColumnNames()) {
			sb.append(DbBase.stringToSQL(colName) + ", ");
		}
		sb.deleteCharAt(sb.toString().lastIndexOf(','));
		sb.append(")");
		return sb.toString();
	}
}
