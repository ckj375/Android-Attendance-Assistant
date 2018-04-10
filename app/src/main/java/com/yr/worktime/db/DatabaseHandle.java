package com.yr.worktime.db;

import java.util.ArrayList;
import java.util.List;

import com.yr.worktime.model.TimeInfo;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHandle extends Activity {

    public static Cursor getAllResult(SQLiteDatabase db) {
        String sql = "SELECT * FROM "
                + DataTable.getTableNameTime()
                + " ORDER BY "
                + DataTable.getTableTimeDate();

        return db.rawQuery(sql, null);
    }

    public static TimeInfo getKeepResultByDate(SQLiteDatabase db, String strDate) {
        TimeInfo timeInfo = new TimeInfo();
        String sql = "SELECT * FROM "
                + DataTable.getTableNameTime()
                + " WHERE "
                + DataTable.getTableTimeDate()
                + "=?";

        Cursor cursor = db.rawQuery(sql, new String[]{strDate});

        while (cursor.moveToNext()) {
            timeInfo = getDbTimeInfo(cursor);
        }
        cursor.close();

        return timeInfo;
    }

    public static void insertNewData(SQLiteDatabase db, TimeInfo timeInfo) {
        String insert_sql = "INSERT INTO "
                + DataTable.getTableNameTime()
                + "(" + DataTable.getTableTimeDate()
                + ","
                + DataTable.getTableTimeTime1()
                + ","
                + DataTable.getTableTimeTime2()
                + ","
                + DataTable.getTableTimeFlag()
                + ","
                + DataTable.getTableTimeDefTimems()
                + ","
                + DataTable.getTableTimeDefTimeme()
                + ","
                + DataTable.getTableTimeDefTimeas()
                + ","
                + DataTable.getTableTimeDefTimeae()
                + ","
                + DataTable.getTableTimeDefTimens()
                + ","
                + DataTable.getTableTimeDefTimene()
                + ","
                + DataTable.getTableTimeDefJumptime()
                + ","
                + DataTable.getTableTimeDefTimeoffest()
                + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

        db.execSQL(insert_sql, new String[]{timeInfo.getTimeDate()
                , timeInfo.getTimeTime1()
                , timeInfo.getTimeTime2()
                , timeInfo.getWorkState()

                , timeInfo.getDefaultTimeMS()
                , timeInfo.getDefaultTimeME()
                , timeInfo.getDefaultTimeAS()
                , timeInfo.getDefaultTimeAE()
                , timeInfo.getDefaultTimeNS()
                , timeInfo.getDefaultTimeNE()
                , timeInfo.getDefaultJumpTime()
                , timeInfo.getDefaultTimeOffest()});

    }

    public static void updateData(SQLiteDatabase db, TimeInfo timeInfo) {
        String update_sql = "UPDATE "
                + DataTable.getTableNameTime()
                + " SET "
                + DataTable.getTableTimeTime1()
                + "=?"
                + ","
                + DataTable.getTableTimeTime2()
                + "=?"
                + ","
                + DataTable.getTableTimeFlag()
                + "=?"
                + " WHERE "
                + DataTable.getTableTimeDate()
                + "=?";

        db.execSQL(update_sql, new String[]{timeInfo.getTimeTime1()
                , timeInfo.getTimeTime2()
                , timeInfo.getWorkState()
                , timeInfo.getTimeDate()});
    }

    public static String getColumnByDate(SQLiteDatabase db, String column, String strDate) {
        String value = "";
        String sql = "SELECT "
                + column
                + " FROM "
                + DataTable.getTableNameTime()
                + " WHERE "
                + DataTable.getTableTimeDate()
                + "=?";

        Cursor cursor = db.rawQuery(sql, new String[]{strDate});

        while (cursor.moveToNext()) {
            value = cursor.getString(cursor.getColumnIndex(column));
            break;
        }
        cursor.close();

        return value;
    }

    public static String getTimeIdByDate(SQLiteDatabase db, String strDate) {
        String timeId = "";
        String sql = "SELECT "
                + DataTable.getTableTimeId()
                + " FROM "
                + DataTable.getTableNameTime()
                + " WHERE "
                + DataTable.getTableTimeDate()
                + "=?";

        Cursor cursor = db.rawQuery(sql, new String[]{strDate});

        while (cursor.moveToNext()) {
            timeId = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeId()));
            break;
        }
        cursor.close();

        return timeId;
    }

    public static String getBeginTimeByDate(SQLiteDatabase db, String strDate) {
        String keepTimeBegin = "";
        String sql = "SELECT "
                + DataTable.getTableTimeTime1()
                + " FROM "
                + DataTable.getTableNameTime()
                + " WHERE "
                + DataTable.getTableTimeDate()
                + "=?";

        Cursor cursor = db.rawQuery(sql, new String[]{strDate});

        while (cursor.moveToNext()) {
            keepTimeBegin = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeTime1()));
            break;
        }
        cursor.close();

        return keepTimeBegin;
    }

    public static String getEndTimeByDate(SQLiteDatabase db, String strDate) {
        String endTime = "";
        String sql = "SELECT "
                + DataTable.getTableTimeTime2()
                + " FROM "
                + DataTable.getTableNameTime()
                + " WHERE "
                + DataTable.getTableTimeDate()
                + "=?";

        Cursor cursor = db.rawQuery(sql, new String[]{strDate});

        while (cursor.moveToNext()) {
            endTime = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeTime2()));
            break;
        }
        cursor.close();

        return endTime;
    }

    public static List<TimeInfo> getKeepResults(SQLiteDatabase db) {

        List<TimeInfo> timeInfos = new ArrayList<>();
        String sql = "SELECT * FROM "
                + DataTable.getTableNameTime()
                + " ORDER BY "
                + DataTable.getTableTimeDate();
        //+ " ASC" ;

        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            timeInfos.add(getDbTimeInfo(cursor));
        }
        cursor.close();
        return timeInfos;
    }

    public static void deleteDataById(SQLiteDatabase db, String timeId) {
        String delete_sql = "DELETE FROM "
                + DataTable.getTableNameTime()
                + " WHERE "
                + DataTable.getTableTimeId()
                + "=?";

        db.execSQL(delete_sql, new String[]{timeId});
    }

    public static List<TimeInfo> getKeepResultsByDate(SQLiteDatabase db,
                                                      String strdateBegin, String strdateEnd) {

        List<TimeInfo> timeInfos = new ArrayList<>();
        String sql = "SELECT * FROM "
                + DataTable.getTableNameTime()
                + " WHERE "
                + DataTable.getTableTimeDate()
                + " >=?"
                + " AND "
                + DataTable.getTableTimeDate()
                + " <=?"
                + " ORDER BY "
                + DataTable.getTableTimeDate();
        //+ " ASC" ;

        Cursor cursor = db.rawQuery(sql, new String[]{strdateBegin, strdateEnd});

        while (cursor.moveToNext()) {
            timeInfos.add(getDbTimeInfo(cursor));
        }
        cursor.close();
        return timeInfos;
    }

    private static TimeInfo getDbTimeInfo(Cursor cursor) {
        TimeInfo timeInfo = new TimeInfo();

        String timeId = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeId()));
        String timeDate = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeDate()));
        String timeTime1 = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeTime1()));
        String timeTime2 = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeTime2()));
        String workState = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeFlag()));
        String defaultTimeMS = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeDefTimems()));
        String defaultTimeME = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeDefTimeme()));
        String defaultTimeAS = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeDefTimeas()));
        String defaultTimeAE = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeDefTimeae()));
        String defaultTimeNS = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeDefTimens()));
        String defaultTimeNE = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeDefTimene()));
        String defaultJumpTime = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeDefJumptime()));
        String defaultTimeOffest = cursor.getString(cursor.getColumnIndex(DataTable.getTableTimeDefTimeoffest()));

        timeInfo.setTimeId(timeId);
        timeInfo.setTimeDate(timeDate);
        timeInfo.setTimeTime1(timeTime1);
        timeInfo.setTimeTime2(timeTime2);
        timeInfo.setWorkState(workState);
        timeInfo.setDefaultTimeMS(defaultTimeMS);
        timeInfo.setDefaultTimeME(defaultTimeME);
        timeInfo.setDefaultTimeAS(defaultTimeAS);
        timeInfo.setDefaultTimeAE(defaultTimeAE);
        timeInfo.setDefaultTimeNS(defaultTimeNS);
        timeInfo.setDefaultTimeNE(defaultTimeNE);
        timeInfo.setDefaultJumpTime(defaultJumpTime);
        timeInfo.setDefaultTimeOffest(defaultTimeOffest);

        return timeInfo;
    }

}
