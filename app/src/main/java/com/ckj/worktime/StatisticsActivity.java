package com.ckj.worktime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.umeng.analytics.MobclickAgent;
import com.yr.worktime.model.TimeInfo;
import com.yr.worktime.model.WorkState;

import com.yr.worktime.R;
import com.yr.worktime.util.ComFunction;
import com.yr.worktime.db.DatabaseHandle;
import com.yr.worktime.db.MyDatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StatisticsActivity extends Activity {

    private Context mContext;
    private int intWorkOverTime = 0;
    private int restTime = 0;
    private TimeInfosAdapter adapter;

    private View backView;
    private List<TimeInfo> timeInfos = new ArrayList();
    private TextView dateBegin;
    private TextView dateEnd;
    private Button userSearch;
    private LinearLayout layoutNoData;
    private View layoutHasData;
    private View dataAnalysisView;
    private ListView listView;
    private TextView detailTV;
    private Button detailBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        mContext = this;

        backView = findViewById(R.id.back);
        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Calendar calendar = Calendar.getInstance();

        dateBegin = (TextView) findViewById(R.id.dateBegin);
        dateBegin.setOnClickListener(new SetDayOnClickListener());
        dateBegin.setText(ComFunction.myDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1));
        dateEnd = (TextView) findViewById(R.id.dateEnd);
        dateEnd.setOnClickListener(new SetDayOnClickListener());
        dateEnd.setText(ComFunction.myDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));

        layoutNoData = (LinearLayout) findViewById(R.id.layoutNoData);
        layoutHasData = findViewById(R.id.layoutHasData);
        dataAnalysisView = findViewById(R.id.data_analysis_view);

        userSearch = (Button) findViewById(R.id.userSearch);
        userSearch.setOnClickListener(new SearchOnClickListener());

        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemLongClickListener(new deleteListener());

        detailTV = (TextView) findViewById(R.id.statistics_from_to);
        detailBtn = (Button) findViewById(R.id.dataAnalysis);
        detailBtn.setOnClickListener(new AnalysisOnClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();

        MobclickAgent.onResume(mContext);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
    }

    class AnalysisOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("timeInfos", (Serializable) timeInfos);
            intent.setClass(mContext, DataAnalysisActivity.class);
            mContext.startActivity(intent);
        }
    }

    class SearchOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            updateListView();
        }

    }

    class SetDayOnClickListener implements OnClickListener {

        @Override
        public void onClick(View arg0) {

            final int viewid = arg0.getId();

            Calendar calendar = Calendar.getInstance();

            String strDate = "";
            switch (viewid) {
                case R.id.dateBegin:
                    strDate = dateBegin.getText().toString().trim();
                    break;
                case R.id.dateEnd:
                    strDate = dateEnd.getText().toString().trim();
                    break;
            }

            calendar.set(Integer.parseInt(strDate.substring(0, 4))
                    , -1 + Integer.parseInt(strDate.substring(5, 7))
                    , Integer.parseInt(strDate.substring(8)));
            new DatePickerDialog(StatisticsActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            String day = ComFunction.myDate(year, monthOfYear + 1, dayOfMonth);

                            switch (viewid) {
                                case R.id.dateBegin:
                                    dateBegin.setText(day);
                                    break;
                                case R.id.dateEnd:
                                    dateEnd.setText(day);
                                    break;
                            }
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        }
    }

    private void updateListView() {
        String strdateBegin = dateBegin.getText().toString().trim();
        String strdateEnd = dateEnd.getText().toString().trim();
        detailTV.setText(String.format(getResources().getString(R.string.statistics_from_to), strdateBegin, strdateEnd));
        detailTV.setText(String.format(getResources().getString(R.string.statistics_from_to), strdateBegin, strdateEnd));

        int days = ComFunction.getGapCount(strdateBegin, strdateEnd);
        if (days < 0) {
            Toast.makeText(StatisticsActivity.this, getString(R.string.msg_5), Toast.LENGTH_SHORT).show();
            return;
        } else if (days > 30) {
            Toast.makeText(StatisticsActivity.this, getString(R.string.msg_6), Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(StatisticsActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        timeInfos.clear();
        timeInfos = DatabaseHandle.getKeepResultsByDate(db, strdateBegin, strdateEnd);
        if (timeInfos.isEmpty()) {
            layoutNoData.setVisibility(View.VISIBLE);
            layoutHasData.setVisibility(View.GONE);
            dataAnalysisView.setVisibility(View.GONE);
            return;
        }

        layoutNoData.setVisibility(View.GONE);
        layoutHasData.setVisibility(View.VISIBLE);
        dataAnalysisView.setVisibility(View.VISIBLE);

        int time_No = 0;
        int totalWorkTime = 0;
        for (TimeInfo timeInfo : timeInfos) {
            HashMap<String, String> map = new HashMap<>();
            map.put("search_no", String.valueOf(++time_No));
            map.put("search_date", timeInfo.getTimeDate());
            map.put("search_time1", timeInfo.getTimeTime1());
            map.put("search_time2", timeInfo.getTimeTime2());
            map.put("search_type", timeInfo.getWorkState());

            String beginTime = timeInfo.getTimeTime1();
            String endTime = timeInfo.getTimeTime2();
            String workState = timeInfo.getWorkState();
            // 老版本默认时间为""，新版本默认为"--:--";老版本全天请假默认时间均为"00:00",现改为"--:--"
            if (beginTime.equals("--:--") || beginTime.equals("") || endTime.equals("--:--") || endTime.equals("") || WorkState.RESTALLDAY.equals(workState)) {
                map.put("work_time", "--:--");
            } else {
                int time = getWorkTimeByDay(timeInfo);
                timeInfo.setWorkTime(time);
                timeInfo.setRestTime(restTime);
                if (WorkState.WEEKJOB.equals(timeInfo.getWorkState())) {
                    intWorkOverTime = time;
                }
                timeInfo.setWorkOverTime(intWorkOverTime);
                map.put("work_time", ComFunction.myTime(time / 60, time % 60));
                totalWorkTime += time;
            }
            list.add(map);
        }

        adapter = new TimeInfosAdapter(mContext, list);
        listView.setAdapter(adapter);
        db.close();
        dbHelper.close();
    }

    /**
     * 获取每天上班总时长（单位：min）
     */
    private int getWorkTimeByDay(TimeInfo timeInfo) {
        int time;
        String workTimeBegin = timeInfo.getTimeTime1();
        int hourWorkTimeBegin = Integer.parseInt(workTimeBegin.substring(0, 2));
        int minWorkTimeBegin = Integer.parseInt(workTimeBegin.substring(3));

        String workTimeEnd = timeInfo.getTimeTime2();
        int hourWorkTimeEnd = Integer.parseInt(workTimeEnd.substring(0, 2));
        int minWorkTimeEnd = Integer.parseInt(workTimeEnd.substring(3));

        time = (hourWorkTimeEnd * 60 + minWorkTimeEnd) - (hourWorkTimeBegin * 60 + minWorkTimeBegin);

        return time;
    }

    class deleteListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final TimeInfo timeInfo = timeInfos.get(position);

            new AlertDialog.Builder(mContext).setTitle(
                    getString(R.string.delete)).setPositiveButton(getString(R.string.OK),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyDatabaseHelper dbHelper = new MyDatabaseHelper(mContext);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            DatabaseHandle.deleteDataById(db, timeInfo.getTimeId());
                            db.close();
                            dbHelper.close();

                            updateListView();
                        }
                    }).setNegativeButton(getString(R.string.Cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
            return false;
        }
    }

}