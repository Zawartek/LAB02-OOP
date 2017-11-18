package main.java.database.table.index;

import main.java.database.DbBase;
import main.java.database.table.DbIndex;

/**
 * Database primary Key Index
 * @author nicosim
 *
 */
public class DbPrimaryKey  extends DbIndex{

	public DbPrimaryKey(String indexName, String colName) {
		super(indexName, colName);
	}
	
	@Override
	public String toSQL() {
        final StringBuffer sb = new StringBuffer();
        if (getName() != null && !getName().equals("")) {
        	sb.append("CONSTRAINT " + DbBase.stringToSQL(getName()) + " ");
        }
        sb.append("PRIMARY KEY (");
        for (String colName : getColumnNames()) {
        	sb.append(DbBase.stringToSQL(colName) + ", ");
        }
        sb.deleteCharAt(sb.toString().lastIndexOf(','));
        sb.append(")");
		return sb.toString();
	}
}
