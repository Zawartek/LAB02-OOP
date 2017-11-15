package main.java.database.table.column;

import main.java.database.table.DbColumn;

public class DbColumnNoParam extends DbColumn{

	public DbColumnNoParam(String name, String colType, String colDef) {
		super(name, colType, colDef);
	}

	@Override
	public String toSQL() {
        final StringBuffer sb = new StringBuffer();
        sb.append("\t" + STRING_QUOTE + this.getName() + STRING_QUOTE + " " + this.getType() 
        	+ " " + this.getNullableString());
        if (this.getDefault()!=null) {
        	sb.append(" DEFAULT " + this.getDefault());
        }
		return sb.toString();
	}
	
}
