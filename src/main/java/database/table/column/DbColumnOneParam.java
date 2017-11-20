package main.java.database.table.column;

import main.java.database.DbBase;
import main.java.database.table.DbColumn;

/**
 * Database column with one parameter
 * 
 * @author nicosim
 *
 */
public class DbColumnOneParam extends DbColumn {
	private int paramOne;

	public DbColumnOneParam(String name, String colType, String colDef, int p1) {
		super(name, colType, colDef);
		paramOne = p1;
	}

	@Override
	public String toSQL() {
		final StringBuffer sb = new StringBuffer();
		sb.append("\t" + DbBase.stringToSQL(this.getName()) + " " + this.getType() + " (" + this.paramOne + ")" + " "
				+ this.getNullableString());
		if (this.getDefault() != null) {
			sb.append(" DEFAULT \'" + this.getDefault() + "\'");
		}
		return sb.toString();
	}
}
