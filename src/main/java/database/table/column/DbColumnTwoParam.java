package main.java.database.table.column;

import main.java.database.DbBase;
import main.java.database.table.DbColumn;

/**
 * Database Column with two parameters
 * 
 * @author nicosim
 *
 */
public class DbColumnTwoParam extends DbColumn {
	private int paramOne;
	private int paramTwo;

	public DbColumnTwoParam(String name, String type, String colDef, int colSize, int decimalDigit) {
		super(name, type, colDef);
		paramOne = colSize;
		paramTwo = decimalDigit;
	}

	@Override
	public String toSQL() {
		final StringBuffer sb = new StringBuffer();
		sb.append("\t" + DbBase.stringToSQL(this.getName()) + " " + this.getType() + " (" + this.paramOne + ","
				+ this.paramTwo + ")" + " " + this.getNullableString());
		if (this.getDefault() != null) {
			sb.append(" DEFAULT " + this.getDefault());
		}
		return sb.toString();
	}
}
