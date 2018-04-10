package com.ckj.worktime;

import java.util.Calendar;

import com.umeng.analytics.MobclickAgent;
import com.yr.worktime.model.TimeInfo;
import com.yr.worktime.model.WorkState;

import com.ckj.worktime.view.ClockButton;
import com.yr.worktime.R;
import com.yr.worktime.util.ComFunction;
import com.yr.worktime.db.DataTable;
import com.yr.worktime.db.DatabaseHandle;
import com.yr.worktime.db.MyDatabaseHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends Activity {

    private Context mContext;
    private Resources res;
    private String selectedDate;
    private String selectedTime;
    private String history;

    private ImageView mStatisticsView;
    private TextView dateTV;
    private TextView timeTV;
    private RadioGroup workTypeGroup;
    private View clockTypeLayout;
    private RadioGroup clockTypeGroup;
    private TextView historyTV;
    private ClockButton clockButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        res = getResources();

        Calendar calendar = Calendar.getInstance();
        selectedDate = ComFunction.myDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
        selectedTime = ComFunction.myTime(calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));

        mStatisticsView = (ImageView) findViewById(R.id.imageview_statistics);
        mStatisticsView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, StatisticsActivity.class);
                mContext.startActivity(intent);
            }
        });

        dateTV = (TextView) findViewById(R.id.date_textview);
        dateTV.setOnClickListener(new SetDayOnClickListener());
        timeTV = (TextView) findViewById(R.id.time_textview);
        timeTV.setOnClickListener(new SetTimeOnClickListener());
        dateTV.setText(selectedDate);
        timeTV.setText(selectedTime);
        workTypeGroup = (RadioGroup) findViewById(R.id.work_type_group);
        workTypeGroup.setOnCheckedChangeListener(new WorkTypeChangeListener());
        clockTypeLayout = findViewById(R.id.menu3);
        clockTypeGroup = (RadioGroup) findViewById(R.id.clock_type_group);
        historyTV = (TextView) findViewById(R.id.history_textview);

        checkAndSetFile();

        clockButton = (ClockButton) findViewById(R.id.clock_button);
        clockButton.setOnClickListener(new ClockListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void updateView() {
        history = getSelectedDateHistory();
        historyTV.setText(history);
    }

    class ClockListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            saveDatas();
        }
    }

    /**
     * 日期选择监听器
     */
    class SetDayOnClickListener implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            Calendar calendar = Calendar.getInstance();
            String strDate = dateTV.getText().toString().trim();
            calendar.set(Integer.parseInt(strDate.substring(0, 4)), -1
                            + Integer.parseInt(strDate.substring(5, 7)),
                    Integer.parseInt(strDate.substring(8)));
            new DatePickerDialog(mContext,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            monthOfYear = monthOfYear + 1;

                            selectedDate = ComFunction.myDate(year, monthOfYear,
                                    dayOfMonth);
                            dateTV.setText(selectedDate);
                            history = getSelectedDateHistory();
                            historyTV.setText(history);
                        }
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    /**
     * 时间选择监听器
     */
    class SetTimeOnClickListener implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            Calendar calendar = Calendar.getInstance();
            String strTime = timeTV.getText().toString().trim();
            int hourOfDay = Integer.parseInt(strTime.substring(0, 2));
            int minute = Integer.parseInt(strTime.substring(3));
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);

            new TimePickerDialog(mContext,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            selectedTime = ComFunction.myTime(hourOfDay, minute);
                            timeTV.setText(selectedTime);
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar
                    .get(Calendar.MINUTE), true).show();
        }
    }

    /**
     * 出勤类型按钮监听
     */
    class WorkTypeChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.work_restAllday) {
                timeTV.setVisibility(View.GONE);
                clockTypeLayout.setVisibility(View.GONE);
            } else {
                timeTV.setVisibility(View.VISIBLE);
                clockTypeLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 保存打卡信息至数据库
     */
    private void saveDatas() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String strDate = dateTV.getText().toString().trim();
        String strTime = timeTV.getText().toString().trim();

        String timeId = DatabaseHandle
                .getColumnByDate(db, DataTable.getTableTimeId(), strDate);
        String beginTime = DatabaseHandle
                .getColumnByDate(db, DataTable.getTableTimeTime1(), strDate);
        String endTime = DatabaseHandle
                .getColumnByDate(db, DataTable.getTableTimeTime2(), strDate);
        String workType = DatabaseHandle
                .getColumnByDate(db, DataTable.getTableTimeFlag(), strDate);

        String workState;
        switch (workTypeGroup.getCheckedRadioButtonId()) {
            case R.id.work_weekJob:
                workState = WorkState.WEEKJOB;
                break;
            case R.id.work_restAllday:
                workState = WorkState.RESTALLDAY;
                break;
            case R.id.work_normal:
            default:
                workState = WorkState.NORMAL;
                break;
        }

        String clockType = "";
        switch (clockTypeGroup.getCheckedRadioButtonId()) {
            case R.id.clock_type1:
                clockType = "1";
                break;
            case R.id.clock_type2:
                clockType = "2";
                break;
        }

        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setTimeDate(strDate);
        timeInfo.setWorkState(workState);

        SharedPreferences preferences = getSharedPreferences(WorkState.FILENM,
                MODE_PRIVATE);
        timeInfo.setDefaultTimeMS(preferences.getString(WorkState.DEF_TIMEMS,
                getString(R.string.defaultTimeMS)));
        timeInfo.setDefaultTimeME(preferences.getString(WorkState.DEF_TIMEME,
                getString(R.string.defaultTimeME)));
        timeInfo.setDefaultTimeAS(preferences.getString(WorkState.DEF_TIMEAS,
                getString(R.string.defaultTimeAS)));
        timeInfo.setDefaultTimeAE(preferences.getString(WorkState.DEF_TIMEAE,
                getString(R.string.defaultTimeAE)));
        timeInfo.setDefaultTimeNS(preferences.getString(WorkState.DEF_TIMENS,
                getString(R.string.defaultTimeNS)));
        timeInfo.setDefaultTimeNE(preferences.getString(WorkState.DEF_TIMENE,
                getString(R.string.defaultTimeNE)));
        timeInfo.setDefaultJumpTime(preferences.getString(
                WorkState.DEF_JUMPTIME, getString(R.string.defaultJumpTime)));
        timeInfo.setDefaultTimeOffest(preferences
                .getString(WorkState.DEF_TIMEOFFEST,
                        getString(R.string.defaultTimeOffest)));

        if (workState.equals(WorkState.RESTALLDAY)) {
            timeInfo.setTimeTime1("--:--");
            timeInfo.setTimeTime2("--:--");
            if (timeId.equals("")) {
                DatabaseHandle.insertNewData(db, timeInfo);
            } else {
                DatabaseHandle.updateData(db, timeInfo);
            }
        } else {
            if (timeId.equals("")) {
                if (clockType.equals("1")) {
                    timeInfo.setTimeTime1(strTime);
                    timeInfo.setTimeTime2("--:--");
                } else {
                    timeInfo.setTimeTime1("--:--");
                    timeInfo.setTimeTime2(strTime);
                }
                DatabaseHandle.insertNewData(db, timeInfo);
            } else {
                if (workType.equals(WorkState.RESTALLDAY)) {
                    if (clockType.equals("1")) {
                        timeInfo.setTimeTime1(strTime);
                        timeInfo.setTimeTime2("--:--");
                    } else {
                        timeInfo.setTimeTime1("--:--");
                        timeInfo.setTimeTime2(strTime);
                    }
                } else {
                    if (clockType.equals("1")) {
                        if (endTime.equals("--:--")) {
                            timeInfo.setTimeTime1(strTime);
                            timeInfo.setTimeTime2(endTime);
                            DatabaseHandle.updateData(db, timeInfo);
                        } else {
                            if (compareTime(strTime, endTime)) {
                                timeInfo.setTimeTime1(strTime);
                                timeInfo.setTimeTime2(endTime);
                                DatabaseHandle.updateData(db, timeInfo);
                            } else {
                                showDialog(res.getString(R.string.save_tip1));
                            }
                        }
                    } else {
                        if (beginTime.equals("--:--")) {
                            timeInfo.setTimeTime1(beginTime);
                            timeInfo.setTimeTime2(strTime);
                            DatabaseHandle.updateData(db, timeInfo);
                        } else {
                            if (compareTime(beginTime, strTime)) {
                                timeInfo.setTimeTime1(beginTime);
                                timeInfo.setTimeTime2(strTime);
                                DatabaseHandle.updateData(db, timeInfo);
                            } else {
                                showDialog(res.getString(R.string.save_tip2));
                            }
                        }
                    }
                }
            }
        }

        db.close();
        dbHelper.close();

        historyTV.post(new Runnable() {
            @Override
            public void run() {
                history = getSelectedDateHistory();
                historyTV.setText(history);
            }
        });
    }

    /**
     * 获取当前选中日期的打卡记录
     */
    private String getSelectedDateHistory() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String strDate = dateTV.getText().toString().trim();
        String beginTime = DatabaseHandle
                .getColumnByDate(db, DataTable.getTableTimeTime1(), strDate);
        String endTime = DatabaseHandle
                .getColumnByDate(db, DataTable.getTableTimeTime2(), strDate);
        String workType = DatabaseHandle
                .getColumnByDate(db, DataTable.getTableTimeFlag(), strDate);

        db.close();
        dbHelper.close();

        String history;
        if (workType.equals("4")) {
            history = strDate + res.getString(R.string.history_tip1);
        } else if (beginTime.equals("") && endTime.equals("")) {
            history = strDate + res.getString(R.string.history_tip2);
        } else {
            history = strDate + res.getString(R.string.history_tip3) + beginTime + "," + res.getString(R.string.history_tip4) + endTime;
        }

        return history;
    }

    private void checkAndSetFile() {
        boolean setFlag = false;

        SharedPreferences preferences = getSharedPreferences(WorkState.FILENM,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (null == preferences.getString(WorkState.DEF_TIMEMS, null)) {
            editor.putString(WorkState.DEF_TIMEMS,
                    getString(R.string.defaultTimeMS));
            setFlag = true;
        }

        if (null == preferences.getString(WorkState.DEF_TIMEME, null)) {
            editor.putString(WorkState.DEF_TIMEME,
                    getString(R.string.defaultTimeME));
            setFlag = true;
        }

        if (null == preferences.getString(WorkState.DEF_TIMEAS, null)) {
            editor.putString(WorkState.DEF_TIMEAS,
                    getString(R.string.defaultTimeAS));
            setFlag = true;
        }

        if (null == preferences.getString(WorkState.DEF_TIMEAE, null)) {
            editor.putString(WorkState.DEF_TIMEAE,
                    getString(R.string.defaultTimeAE));
            setFlag = true;
        }

        if (null == preferences.getString(WorkState.DEF_TIMENS, null)) {
            editor.putString(WorkState.DEF_TIMENS,
                    getString(R.string.defaultTimeNS));
            setFlag = true;
        }

        if (null == preferences.getString(WorkState.DEF_TIMENE, null)) {
            editor.putString(WorkState.DEF_TIMENE,
                    getString(R.string.defaultTimeNE));
            setFlag = true;
        }

        if (null == preferences.getString(WorkState.DEF_JUMPTIME, null)) {
            editor.putString(WorkState.DEF_JUMPTIME,
                    getString(R.string.defaultJumpTime));
            setFlag = true;
        }

        if (null == preferences.getString(WorkState.DEF_TIMEOFFEST, null)) {
            editor.putString(WorkState.DEF_TIMEOFFEST,
                    getString(R.string.defaultTimeOffest));
            setFlag = true;
        }

        if (setFlag) {

            editor.commit();
        }
    }

    private boolean compareTime(String beginTime, String endTime) {
        int beginHour = Integer.parseInt(beginTime.substring(0, 2));
        int beginMin = Integer.parseInt(beginTime.substring(3));

        int endHour = Integer.parseInt(endTime.substring(0, 2));
        int endMin = Integer.parseInt(endTime.substring(3));

        if (beginHour > endHour) {
            return false;
        } else if (beginHour < endHour) {
            return true;
        } else if (beginMin < endMin) {
            return true;
        } else {
            return false;
        }
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message).setPositiveButton(res.getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create().show();
    }

}
