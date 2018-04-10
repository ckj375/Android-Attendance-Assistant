package com.ckj.worktime;

import android.app.Application;

import com.umeng.commonsdk.UMConfigure;

/**
 * Created by kaijianchen on 2018/4/9.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, "5ac9d960f43e4853860001f4", "baiduyun", UMConfigure.DEVICE_TYPE_PHONE,
                null);
    }
}
