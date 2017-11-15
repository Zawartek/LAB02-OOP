package main.java.database.table;

public abstract class DbColumn {
	protected static final String STRING_QUOTE="\'";
	private String columnName;
	private String columnType;
	private String columnDefault;
	private boolean isNullable;
	
	
	public DbColumn(String name, String type, String colDef) {
		this.columnName = name;
		this.columnType = type;
		this.columnDefault = colDef;
	}
	
	public String getName() {
		return columnName;
	}
	
	public String getType() {
		return columnType;
	}
	
	public String getDefault() {
		return columnDefault;
	}

	public abstract String toSQL();
	
	protected String getNullableString() {
		String s = "";
		if (isNullable) {
			s = "NULL";
		}
		else {
			s = "NOT NULL";
		}
		return s;
	}
}
