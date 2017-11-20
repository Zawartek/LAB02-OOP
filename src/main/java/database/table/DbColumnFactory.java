package main.java.database.table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import main.java.database.table.column.*;

public class DbColumnFactory {

	/**
	 * Create an object DbColumn based on the data in the resultset
	 * 
	 * @param column
	 * @return column
	 * @throws SQLException
	 */
	public static DbColumn createColumn(ResultSet column) throws SQLException {
		DbColumn dbCol = null;
		String columnName, columnType, nullable, columnDefault;
		int columnSize, decimalDigit, sqlType;
		boolean isNullable = true;

		columnName = column.getString("COLUMN_NAME");
		columnType = column.getString("TYPE_NAME");
		columnDefault = column.getString("COLUMN_DEF");
		sqlType = column.getInt("DATA_TYPE");
		columnSize = column.getInt("COLUMN_SIZE");
		decimalDigit = column.getInt("DECIMAL_DIGITS");
		nullable = column.getString("IS_NULLABLE");
		isNullable = isNullable(nullable);

		dbCol = createColumn(columnName, columnType, columnDefault, sqlType, columnSize, decimalDigit, isNullable);
		return dbCol;
	}

	/**
	 * Return a DbColumn object based on given parameters
	 * 
	 * @param colName
	 * @param colType
	 * @param colDef
	 * @param sqlType
	 * @param colSize
	 * @param decimalDigit
	 * @param isNull
	 * @return column
	 */
	private static DbColumn createColumn(String colName, String colType, String colDef, int sqlType, int colSize,
			int decimalDigit, boolean isNull) {
		DbColumn dbCol = null;
		switch (sqlType) {
		case Types.SMALLINT:
		case Types.BIT:
		case Types.TINYINT:
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			dbCol = new DbColumnNoParam(colName, colType, colDef);
			break;
		case Types.CHAR:
		case Types.NCHAR:
		case Types.VARCHAR:
		case Types.NVARCHAR:
		case Types.BLOB:
		case Types.BINARY:
		case Types.VARBINARY:

		case Types.LONGNVARCHAR:
		case Types.LONGVARBINARY:
		case Types.LONGVARCHAR:
			if (colType.equals("ENUM") || colType.equals("SET")) {
				colType = "VARCHAR";
			}
			dbCol = new DbColumnOneParam(colName, colType, colDef, colSize);
			break;
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.NUMERIC:
		case Types.REAL:
			dbCol = new DbColumnTwoParam(colName, colType, colDef, colSize, decimalDigit);
			break;
		default:
			/*
			 * ARRAY BOOLEAN CLOB NCLOB DATALINK DISTINCT JAVA_OBJECT REF ROWID
			 * SQLXML STRUCT
			 */
			break;
		}
		return dbCol;
	}

	/**
	 * Convert the string got from the sql parameter isNullable to a boolean
	 * 
	 * @param nullable
	 * @return
	 */
	private static boolean isNullable(String nullable) {
		boolean isNullable = false;
		switch (nullable) {
		case "YES":
			isNullable = true;
			break;
		case "NO":
			isNullable = false;
			break;
		default:
			break;
		}
		return isNullable;
	}
}