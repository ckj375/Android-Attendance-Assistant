package com.yr.worktime.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class MyDatabaseHelper extends SQLiteOpenHelper{

	private static final int VERSION = 3;

	public MyDatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	public MyDatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}
	
	public MyDatabaseHelper(Context context, String name) {
		this(context, name, VERSION);
	}
	
	public MyDatabaseHelper(Context context) {
		this(context, DataTable.getDbName());
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("create a Database");
		db.execSQL(DataTable.getSqlCreateTableTime());
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("update a Database");	

		//System.out.println(DataTable.getSqlTableTemp());	
		db.execSQL(DataTable.getSqlTableTemp());

		//System.out.println(DataTable.getSqlCreateTableTime());
		db.execSQL(DataTable.getSqlCreateTableTime());

		//String sql = "INSERT INTO TIME(TIME_DATE,TIME_TIME1,TIME_TIME2) SELECT TIME_DATE,TIME_TIME1,TIME_TIME2 FROM __TEMP__TABLE";//"SELECT TIME_DATE,TIME_TIME1,TIME_TIME2 INTO TIME FROM __TEMP__TABLE";
		//System.out.println(DataTable.getSqlIntoOldData());
		db.execSQL(DataTable.getSqlIntoOldData());

		//System.out.println(DataTable.getSqlDropTableTemp());
		db.execSQL(DataTable.getSqlDropTableTemp());
		String sql = "UPDATE TIME SET DEF_TIMEMS='09:00',DEF_TIMEME='12:00',DEF_TIMEAS='13:00',DEF_TIMEAE='18:00',DEF_TIMENS='18:30',DEF_TIMENE='23:59',DEF_JUMPTIME='30',DEF_TIMEOFFEST='5'"
			+" WHERE (TIME_DATE >= '2013/03/01' AND TIME_DATE <= '2013/03/31')";
		db.execSQL(sql);
		
		sql = "UPDATE TIME SET DEF_TIMEMS='09:00',DEF_TIMEME='12:00',DEF_TIMEAS='13:00',DEF_TIMEAE='18:00',DEF_TIMENS='18:30',DEF_TIMENE='23:59',DEF_JUMPTIME='30',DEF_TIMEOFFEST='0'"
			+" WHERE (TIME_DATE >= '2013/04/01' AND TIME_DATE <= '2013/04/30')";
		db.execSQL(sql);
	}

}
