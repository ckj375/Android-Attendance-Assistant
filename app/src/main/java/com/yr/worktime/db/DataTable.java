package com.yr.worktime.db;

public class DataTable {

	private static final String DB_NAME = "WORKTIME_DB";
	
	private static final String TABLE_NAME_TIME = "TIME";
	private static final String TABLE_TIME_ID = "TIME_ID";
	private static final String TABLE_TIME_DATE = "TIME_DATE";
	private static final String TABLE_TIME_TIME1 = "TIME_TIME1";
	private static final String TABLE_TIME_TIME2 = "TIME_TIME2";
	private static final String TABLE_TIME_FLAG = "TIME_FLAG";
	private static final String TABLE_TIME_DEF_TIMEMS = "DEF_TIMEMS";
	private static final String TABLE_TIME_DEF_TIMEME = "DEF_TIMEME";
	private static final String TABLE_TIME_DEF_TIMEAS = "DEF_TIMEAS";
	private static final String TABLE_TIME_DEF_TIMEAE = "DEF_TIMEAE";
	private static final String TABLE_TIME_DEF_TIMENS = "DEF_TIMENS";
	private static final String TABLE_TIME_DEF_TIMENE = "DEF_TIMENE";
	private static final String TABLE_TIME_DEF_JUMPTIME = "DEF_JUMPTIME";
	private static final String TABLE_TIME_DEF_TIMEOFFEST = "DEF_TIMEOFFEST";
	

	private static final String SQL_CREATE_TABLE_TIME = "create table " + TABLE_NAME_TIME
								+"(" + TABLE_TIME_ID + " integer primary key autoincrement,"
								+ TABLE_TIME_DATE + " varchar(10)," 
								+ TABLE_TIME_TIME1 + " varchar(5)," 
								+ TABLE_TIME_TIME2 + " varchar(5),"
								+ TABLE_TIME_FLAG + " varchar(1),"
								+ TABLE_TIME_DEF_TIMEMS + " varchar(5),"
								+ TABLE_TIME_DEF_TIMEME + " varchar(5),"
								+ TABLE_TIME_DEF_TIMEAS + " varchar(5),"
								+ TABLE_TIME_DEF_TIMEAE + " varchar(5),"
								+ TABLE_TIME_DEF_TIMENS + " varchar(5),"
								+ TABLE_TIME_DEF_TIMENE + " varchar(5),"
								+ TABLE_TIME_DEF_JUMPTIME + " varchar(5),"
								+ TABLE_TIME_DEF_TIMEOFFEST + " varchar(5))";

	private static final String SQL_TABLE_TEMP = "ALTER TABLE " + TABLE_NAME_TIME + " RENAME TO __TEMP__TABLE";

	private static final String SQL_INTO_OLD_DATA = "INSERT INTO " + TABLE_NAME_TIME 
												+ "(" + TABLE_TIME_DATE 
												+ "," + TABLE_TIME_TIME1 
												+ "," + TABLE_TIME_TIME2 
												+ "," + TABLE_TIME_FLAG
												+ ")"
												+ " SELECT " + TABLE_TIME_DATE 
												+ "," + TABLE_TIME_TIME1 
												+ "," + TABLE_TIME_TIME2  
												+ "," + TABLE_TIME_FLAG 
												+ " FROM __TEMP__TABLE";

	private static final String SQL_DROP_TABLE_TEMP = "DROP TABLE __TEMP__TABLE";
	
	public static String getDbName() {
		return DB_NAME;
	}

	public static String getSqlTableTemp() {
		return SQL_TABLE_TEMP;
	}

	public static String getSqlIntoOldData() {
		return SQL_INTO_OLD_DATA;
	}

	public static String getSqlDropTableTemp() {
		return SQL_DROP_TABLE_TEMP;
	}

	public static String getTableNameTime() {
		return TABLE_NAME_TIME;
	}

	public static String getTableTimeId() {
		return TABLE_TIME_ID;
	}

	public static String getTableTimeDate() {
		return TABLE_TIME_DATE;
	}

	public static String getTableTimeTime1() {
		return TABLE_TIME_TIME1;
	}

	public static String getTableTimeTime2() {
		return TABLE_TIME_TIME2;
	}

	public static String getSqlCreateTableTime() {
		return SQL_CREATE_TABLE_TIME;
	}

	public static String getTableTimeFlag() {
		return TABLE_TIME_FLAG;
	}
	public static String getTableTimeDefTimems() {
		return TABLE_TIME_DEF_TIMEMS;
	}

	public static String getTableTimeDefTimeme() {
		return TABLE_TIME_DEF_TIMEME;
	}

	public static String getTableTimeDefTimeas() {
		return TABLE_TIME_DEF_TIMEAS;
	}

	public static String getTableTimeDefTimeae() {
		return TABLE_TIME_DEF_TIMEAE;
	}

	public static String getTableTimeDefTimens() {
		return TABLE_TIME_DEF_TIMENS;
	}

	public static String getTableTimeDefTimene() {
		return TABLE_TIME_DEF_TIMENE;
	}

	public static String getTableTimeDefJumptime() {
		return TABLE_TIME_DEF_JUMPTIME;
	}

	public static String getTableTimeDefTimeoffest() {
		return TABLE_TIME_DEF_TIMEOFFEST;
	}
	
}
