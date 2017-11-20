package main.java.database.table;

/**
 * Database column
 * 
 * @author nicosim
 *
 */
public abstract class DbColumn {
	private String columnName;
	private String columnType;
	private String columnDefault;
	private boolean isNullable;

	public DbColumn(String name, String type, String colDef) {
		this.columnName = name;
		this.columnType = type;
		this.columnDefault = colDef;
	}

	/**
	 * Get column name
	 * 
	 * @return column name
	 */
	public String getName() {
		return columnName;
	}

	/**
	 * Get column type
	 * 
	 * @return column type
	 */
	public String getType() {
		return columnType;
	}

	/**
	 * Get the default value
	 * 
	 * @return column default value
	 */
	public String getDefault() {
		return columnDefault;
	}

	/**
	 * Get the isNullable value
	 * 
	 * @return
	 */
	public boolean isNullable() {
		return isNullable;
	}

	/**
	 * Return the column in SQL format
	 * 
	 * @return column string
	 */
	public abstract String toSQL();

	/**
	 * Return the "NULL" if the column is nullable else return "NOT NULL"
	 * 
	 * @return
	 */
	protected String getNullableString() {
		String s = "";
		if (isNullable) {
			s = "NULL";
		} else {
			s = "NOT NULL";
		}
		return s;
	}
}
