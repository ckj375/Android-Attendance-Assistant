package com.ckj.worktime;

import java.util.List;

import com.umeng.analytics.MobclickAgent;
import com.yr.worktime.model.TimeInfo;
import com.yr.worktime.model.WorkState;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ckj.worktime.R;
import com.yr.worktime.util.ComFunction;

public class DataAnalysisActivity extends Activity {

    private List<TimeInfo> timeInfos;
    private Context mContext;
    private View backView;
    private ImageView settings;
    private TextView workDaysTV;
    private TextView workDaysDetailTV;
    private TextView restAlldayDaysTV;
    private TextView averageBeginTimeTV;
    private TextView averageBeginTipTV;
    private TextView averageEndTimeTV;
    private TextView averageEndTipTV;
    private TextView totalWorkTimeTV;
    private TextView totalWorkTimeTipTV;
    private TextView totalWorkOverTimeTV;
    private TextView totalWorkOverTimeTipTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analysis);

        timeInfos = (List<TimeInfo>) getIntent()
                .getSerializableExtra("timeInfos");

        mContext = this;

        backView = findViewById(R.id.back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        settings = (ImageView) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, SettingsActivity.class);
                mContext.startActivity(intent);
            }
        });

        workDaysTV = (TextView) findViewById(R.id.work_days_tv);
        workDaysDetailTV = (TextView) findViewById(R.id.work_days_detail_tv);
        restAlldayDaysTV = (TextView) findViewById(R.id.restAlldayTV);
        averageBeginTimeTV = (TextView) findViewById(R.id.averageWorkBeginTime);
        averageBeginTipTV = (TextView) findViewById(R.id.average_begin_time_tip);
        averageEndTimeTV = (TextView) findViewById(R.id.averageWorkEndTime);
        averageEndTipTV = (TextView) findViewById(R.id.average_end_time_tip);
        totalWorkTimeTV = (TextView) findViewById(R.id.total_work_time_tv);
        totalWorkTimeTipTV = (TextView) findViewById(R.id.total_work_time_tip_tv);
        totalWorkOverTimeTV = (TextView) findViewById(R.id.total_work_over_time);
        totalWorkOverTimeTipTV = (TextView) findViewById(R.id.total_work_over_time_tip);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();

        MobclickAgent.onResume(mContext);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
    }

    private void updateView() {
        int normalDays = 0;
        int validDays = 0;// 有效天数，上下班打卡均不为空
        int weekJobDays = 0;
        int restAlldayDays = 0;
        int totalBeginMin = 0;
        int totalEndMin = 0;
        int totalWorkTime1 = 0;
        int totalWorkTime2 = 0;
        int totalWorkOverTime = 0;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String defaultWorkOverTime = preferences.getString(SettingsActivity.DEFAULT_WORK_OVER_TIME, ComFunction.myTime(18,
                0));

        int defaultWorkOverHour = Integer.parseInt(defaultWorkOverTime.substring(0,
                2));
        int defaultWorkOverMin = Integer.parseInt(defaultWorkOverTime.substring(3));

        for (TimeInfo timeInfo : timeInfos) {
            if (WorkState.NORMAL.equals(timeInfo.getWorkState())) {
                normalDays++;
                totalWorkTime1 += timeInfo.getWorkTime(); // 工作日工作时长
            } else if (WorkState.WEEKJOB.equals(timeInfo.getWorkState())) {
                weekJobDays++;
                totalWorkTime2 += timeInfo.getWorkTime(); // 节假日工作时长
            } else if (WorkState.RESTALLDAY.equals(timeInfo.getWorkState())) {
                restAlldayDays++;
                continue;
            }

            // 获取累计加班时间
            String beginTime = timeInfo.getTimeTime1();
            String endTime = timeInfo.getTimeTime2();
            if (!beginTime.equals("--:--") && !beginTime.equals("") && !endTime.equals("--:--") && !endTime.equals("")) {
                validDays++;
                int beginHour = Integer.parseInt(beginTime.substring(0, 2));
                int beginMin = Integer.parseInt(beginTime.substring(3));
                int endHour = Integer.parseInt(endTime.substring(0, 2));
                int endMin = Integer.parseInt(endTime.substring(3));
                totalBeginMin += beginHour * 60 + beginMin;
                totalEndMin += endHour * 60 + endMin;

                if (WorkState.NORMAL.equals(timeInfo.getWorkState())) {
                    int workOverTime = (endHour * 60 + endMin) - (defaultWorkOverHour * 60 + defaultWorkOverMin);
                    if (workOverTime > 0) {
                        totalWorkOverTime += workOverTime;
                    }
                } else if (WorkState.WEEKJOB.equals(timeInfo.getWorkState())) {
                    int workOverTime = (endHour * 60 + endMin) - (beginHour * 60 + beginMin);
                    totalWorkOverTime += workOverTime;
                }
            }
        }

        workDaysTV.setText(String.format(getString(R.string.total_work_days), normalDays + weekJobDays));
        workDaysDetailTV.setText(String.format(getString(R.string.total_work_days_detail), normalDays, weekJobDays));
        restAlldayDaysTV.setText(String.format(getString(R.string.rest_all_day), restAlldayDays));
        int intAverageBeginTime = totalBeginMin / validDays;
        averageBeginTimeTV.setText(String.format(getString(R.string.average_begin_time), ComFunction.myTime(
                intAverageBeginTime / 60, intAverageBeginTime % 60)));
        averageBeginTipTV.setText(String.format(getString(R.string.average_time_tip), validDays));
        int intAverageEndTime = totalEndMin / validDays;
        averageEndTimeTV.setText(String.format(getString(R.string.average_end_time), ComFunction.myTime(
                intAverageEndTime / 60, intAverageEndTime % 60)));
        averageEndTipTV.setText(String.format(getString(R.string.average_time_tip), validDays));
        totalWorkTimeTV.setText(String.format(getString(R.string.total_work_time), (totalWorkTime1 + totalWorkTime2) / 60, (totalWorkTime1 + totalWorkTime2) % 60));
        totalWorkTimeTipTV.setText(String.format(getString(R.string.total_work_time_tip), totalWorkTime1 / 60, totalWorkTime1 % 60, totalWorkTime2 / 60, totalWorkTime2 % 60));
        totalWorkOverTimeTV.setText(String.format(getString(R.string.total_work_over_time), totalWorkOverTime / 60,
                totalWorkOverTime % 60));
        totalWorkOverTimeTipTV.setText(String.format(getString(R.string.total_work_over_time_tip), defaultWorkOverTime));
    }
}
