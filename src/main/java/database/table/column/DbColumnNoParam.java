package main.java.database.table.column;

import main.java.database.DbBase;
import main.java.database.table.DbColumn;

/**
 * Database column with no parameter
 * @author nicosim
 *
 */
public class DbColumnNoParam extends DbColumn{

	public DbColumnNoParam(String name, String colType, String colDef) {
		super(name, colType, colDef);
	}

	@Override
	public String toSQL() {
        final StringBuffer sb = new StringBuffer();
        sb.append("\t" + DbBase.stringToSQL(this.getName()) + " " + this.getType() 
        	+ " " + this.getNullableString());
        if (this.getDefault()!=null) {
        	sb.append(" DEFAULT " + this.getDefault());
        }
		return sb.toString();
	}
	
}
