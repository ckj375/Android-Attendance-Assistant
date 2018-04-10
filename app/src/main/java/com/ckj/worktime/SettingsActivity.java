package com.ckj.worktime;

import java.util.Calendar;

import com.umeng.analytics.MobclickAgent;
import com.yr.worktime.R;
import com.yr.worktime.util.ComFunction;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    public static final String DEFAULT_WORK_OVER_TIME = "default_work_over_time";
    private Context mContext;
    private String defaultWorkOverTime;
    private SharedPreferences preferences;

    private View backView;
    private TextView settingTimeTipView;
    private TextView timeTV;
    private Button modify;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mContext = this;

        backView = findViewById(R.id.back);
        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        defaultWorkOverTime = preferences.getString(DEFAULT_WORK_OVER_TIME, ComFunction.myTime(18,
                0));

        settingTimeTipView = (TextView) findViewById(R.id.setting_time_tip);
        settingTimeTipView.setText(String.format(getResources().getString(R.string.setting_time_tip), defaultWorkOverTime));

        timeTV = (TextView) findViewById(R.id.time_textview);
        timeTV.setText(defaultWorkOverTime);
        timeTV.setOnClickListener(new SetTimeOnClickListener());

        modify = (Button) findViewById(R.id.modify);
        modify.setOnClickListener(new ModifyOnClickListener());
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
                            defaultWorkOverTime = ComFunction.myTime(hourOfDay, minute);
                            timeTV.setText(defaultWorkOverTime);
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar
                    .get(Calendar.MINUTE), true).show();
        }
    }

    class ModifyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            save();
            Toast.makeText(SettingsActivity.this, getResources().getString(R.string.success), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void save() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DEFAULT_WORK_OVER_TIME, defaultWorkOverTime);
        editor.commit();

        settingTimeTipView.post(new Runnable() {
            @Override
            public void run() {
                settingTimeTipView.setText(String.format(getResources().getString(R.string.setting_time_tip), defaultWorkOverTime));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(mContext);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
    }
}
