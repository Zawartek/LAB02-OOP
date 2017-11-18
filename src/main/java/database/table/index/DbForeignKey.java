package main.java.database.table.index;

import java.util.LinkedList;
import java.util.List;

import main.java.database.DbBase;
import main.java.database.table.DbIndex;

/**
 * Database foreign key index
 * @author nicosim
 *
 */
public class DbForeignKey extends DbIndex{
	private String pkTableName;
	private List<String> pkColumnNames;
	

	public DbForeignKey(String name, String fk_col, String pk_tab, String pk_col) {
		super(name, fk_col);
		pkColumnNames = new LinkedList<String>();
		pkColumnNames.add(pk_col);
		pkTableName = pk_tab;
	}
	
	/**
	 * Get the referenced columns 
	 * @return
	 */
	public List<String> getPrimaryColumnNames() {
		return pkColumnNames;
	}
	
	/** 
	 * Add a new column to the referenced columns
	 * @param col
	 */
	public void addPrimaryIndexColumn(String col) {
		pkColumnNames.add(col);
	}
	
	@Override
	public String toSQL() {
        final StringBuffer sb = new StringBuffer();
        if (getName() != null && !getName().equals("")) {
        	sb.append("CONSTRAINT " + DbBase.stringToSQL(getName()) + " ");
        }
        sb.append("FOREIGN KEY "  + "(");
        for (String colName : getColumnNames()) {
        	sb.append(DbBase.stringToSQL(colName) + ", ");
        }
        sb.deleteCharAt(sb.toString().lastIndexOf(','));
        sb.append(")");
        sb.append(" REFERENCES " + DbBase.stringToSQL(pkTableName) + "(");
        
        for (String colName : getPrimaryColumnNames()) {
        	sb.append(DbBase.stringToSQL(colName) + ", ");
        }
        sb.deleteCharAt(sb.toString().lastIndexOf(','));
        
        sb.append(")");
		return sb.toString();
	}

}
