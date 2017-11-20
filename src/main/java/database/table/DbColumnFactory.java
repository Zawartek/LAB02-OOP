package main.java.database.table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.log4j.Logger;

import main.java.database.table.column.*;

public class DbColumnFactory {
	protected static Logger log = Logger.getLogger(DbColumnFactory.class);

	/**
	 * Create an object DbColumn based on the data in the resultset
	 * 
	 * @param column
	 * @return column
	 * @throws SQLException
	 */
	public static DbColumn createColumn(ResultSet column) throws SQLException {
		log.debug("Start method createColumn");
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
		log.debug("column.COLUMN_NAME : " + columnName);
		log.debug("column.TYPE_NAME : " + columnType);
		log.debug("column.COLUMN_DEF : " + columnDefault);
		log.debug("column.DATA_TYPE : " + sqlType);
		log.debug("column.COLUMN_SIZE : " + columnSize);
		log.debug("column.DECIMAL_DIGITS : " + decimalDigit);
		log.debug("column.IS_NULLABLE : " + nullable);

		dbCol = createColumn(columnName, columnType, columnDefault, sqlType, columnSize, decimalDigit, isNullable);
		log.debug("End method createColumn");
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
		log.debug("Start method createColumn");
		log.debug("column name : " + colName);
		log.debug("column type : " + colType);
		log.debug("sqlType : " + sqlType);
		log.debug("column default :" + colDef);
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
			log.debug("Create Column with no parameter");
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
			log.debug("Create Column with one parameter");
			log.debug("column size : " + colSize);
			dbCol = new DbColumnOneParam(colName, colType, colDef, colSize);
			break;
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.NUMERIC:
		case Types.REAL:
			log.debug("Create Column with two parameters");
			log.debug("column size : " + colSize);
			log.debug("decimal digits : " + decimalDigit);
			dbCol = new DbColumnTwoParam(colName, colType, colDef, colSize, decimalDigit);
			break;
		default:
			/*
			 * ARRAY BOOLEAN CLOB NCLOB DATALINK DISTINCT JAVA_OBJECT REF ROWID
			 * SQLXML STRUCT
			 */
			break;
		}
		log.debug("End method createColumn");
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